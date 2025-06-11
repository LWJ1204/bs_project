package com.example.finalapp.Utils;


import android.content.Context;
import android.content.res.AssetManager;

import com.example.finalapp.enity.AP;
import com.example.finalapp.enity.FrameData;
import com.example.finalapp.enity.Point;
import com.example.finalapp.fillter.Framefilter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public class LocationUtil {

    private final int []dx={-2,-1,0,1,2};
    private final int []dy={-2,-1,0,1,2};
    double[][][] watchdata=new double[3][64][240];
    public AP[] mAp={
            new AP("8c:53:c3:c2:9c:eb",new Point(1.6,0)),
            new AP("c8:bf:4c:9b:a6:3e",new Point(0.8,12.8)),
            new AP("64:6e:97:23:8c:41",new Point(8,12.8))
    };
    Framefilter framefilter=new Framefilter(64);
    Point rssipoint=new Point();
    private double sigma=1;
    static int []m={-28,-27,-26,-25,-24,-23,-22,-21,-20,-19,-18,-17,-16,-15,-14,-13,-12,-11,-10,-9,-8,-7,-6,-5,-4,-3,-2,-1,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28};
    WTUtil wtUtil=new WTUtil(3,2,2);
    AssetManager assetManager;
    public LocationUtil(Context context, double sigma){
        this.sigma=sigma;
        assetManager=context.getAssets();
    }

    public Point getLocation(Map<String,List<FrameData>>mp, int ksize) throws Exception {

        Point p=new Point();
        RssiDisUtil rssiDisUtil=new RssiDisUtil(6,2);

        double []dis=new double[3];
        for (Map.Entry<String, List<FrameData>> entry : mp.entrySet()) {
            int index=getIndex(entry.getKey());
            rssiDisUtil.setDatas(entry.getValue());
            dis[index]=rssiDisUtil.getdis();
            framefilter.setData(entry.getValue());
            List<FrameData>filltered=framefilter.filter(0);

            for (int k = 0; k < filltered.size(); k++) {
                double[] temp = new double[240];
                for (int m = 0; m < 2; m++) {
                    wtUtil.setData(filltered.get(k).Amplitude[m]);
                    wtUtil.trans();
                    wtUtil.denoise();
                    double[] reset = wtUtil.reset();
                    System.arraycopy(reset, 0, temp, m == 0 ? 0 : 64 * m , 64);

                    double[] resetP = line1(unwrap(filltered.get(k).Phase[m]));
                    System.arraycopy(resetP, 0, temp, m == 0 ? 128 : 240 - 56, 56);

                }

                watchdata[index][k]=temp;

            }
            watchdata[index]=normal(watchdata[index]);
        }

        rssipoint=TriLocation(dis[0],dis[1],dis[2]);
        int tempx= (int) Math.ceil(rssipoint.x/0.8);
        int tempy= (int) Math.ceil(rssipoint.y/0.8);
        Map<Point,Double> mpdis=new HashMap<>() ;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                int nx=tempx+dx[i];
                int ny=tempy+dy[j];
                if(nx>=1&&nx<=12&&ny>=1&&ny<=12){
                    mpdis.put(new Point(nx,ny),getKnndis(nx,ny));
                }
            }
        }
        // 将Map转换为List并按相似性值降序排序
        List<Map.Entry<Point, Double>> sortedList = new ArrayList<>(mpdis.entrySet());
        sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        // 选择最相似的K个参考点
        double[] weights = new double[ksize];
        double[] xCoords = new double[ksize];
        double[] yCoords = new double[ksize];
        int pos = 0;
        int Fksize=Math.min(sortedList.size(),ksize);
        for (Map.Entry<Point, Double> entry : sortedList) {
            if (pos < Fksize) {
                xCoords[pos] = entry.getKey().x*0.8;
                yCoords[pos] = entry.getKey().y*0.8;
                weights[pos] = entry.getValue();
            }
            pos++;
        }

        // 计算加权平均位置
        double sumWeights = 0;
        double weightedX = 0;
        double weightedY = 0;

        for (int i = 0; i < Fksize; i++) {
            sumWeights += weights[i];
            weightedX += xCoords[i] * weights[i];
            weightedY += yCoords[i] * weights[i];
        }

        double finalX = weightedX / sumWeights;
        double finalY = weightedY / sumWeights;
        return new Point(finalX,finalY) ;
    }


    private Double getKnndis(int nx, int ny) throws IOException {
        // System.out.println("x:"+nx+" y: "+ny);
        String path="finger/"+"x"+nx+"y"+ny+"/";
        double adis=0;
        for (int i = 0; i < 3; i++) {
            String tempfile=path+i+".csv";
            double[][]data=readcsv(tempfile);
            double dis=getdis(watchdata[i],data);
            adis+=dis;
        }
        return adis/3.0;
    }

    private double getdis(double[][] watchdata, double[][] data) {
        int len1 = watchdata.length; // watchdatum的行数
        int len2 =320;// data.length;       // data的行数
        double ans = 0; // 用于存储最终结果

        // 遍历watchdatum中的每一行
        for (int i = 0; i < len1; i++) {
            double disab = Double.MAX_VALUE; // 初始化为最大值

            // 遍历data中的每一行
            for (int j = 0; j < len2; j++) {
                double tempdis = 0; // 用于存储当前行之间的相似度
                tempdis=gaussianKernel(watchdata[i],data[j],this.sigma);
                disab = Math.min(disab, tempdis); // 更新最小距离
            }

            ans = Math.max(ans, disab); // 更新最终结果
        }

        return ans; // 返回所有最小距离中的最大值
    }
    // 高斯核函数
    public double gaussianKernel(double[] x, double[] y, double sigma) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }
        double distance = 0;
        for (int i = 0; i < x.length; i++) {
            distance += Math.pow(x[i] - y[i], 2);
        }
        //计算测量值和参考点之间的欧式距离
        distance = Math.sqrt(distance);
        return Math.exp(-distance * distance / (2 * sigma * sigma));
    }

    private int getIndex(String key) {
        int index=-1;
        for (int i = 0; i < mAp.length; i++) {
            if(mAp[i].mac.equalsIgnoreCase(key)){
                index=i;
            }
        }
        return index;
    }

    public double[][] readcsv(String csvFilePath) throws IOException {
        double[][]figerdata=new double[64*30][240];
        InputStream inputStream=assetManager.open(csvFilePath);
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] line;
            int cnt=0;
            while ((line = reader.readNext()) != null) {
                if(cnt==0){
                    cnt++;
                    continue;
                }
                for(int j=1;j<=240;j++){
                    figerdata[cnt-1][j-1]=Double.parseDouble(line[j]);
                }
                cnt++;
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return figerdata;
    }


    public Point TriLocation(double dis0,double dis1,double dis2){
        if(dis0==0)dis0=0.001;
        if(dis1==0)dis1=0.001;
        if(dis2==0)dis2=0.001;
        double ap01=Math.sqrt ((mAp[0].p.x-mAp[1].p.x)*(mAp[0].p.x-mAp[1].p.x)+(mAp[0].p.y-mAp[1].p.y)*(mAp[0].p.y-mAp[1].p.y));
        double ap02= Math.sqrt((mAp[0].p.x-mAp[2].p.x)*(mAp[0].p.x-mAp[2].p.x)+(mAp[0].p.y-mAp[2].p.y)*(mAp[0].p.y-mAp[2].p.y));
        double ap12=Math.sqrt ((mAp[1].p.x-mAp[2].p.x)*(mAp[1].p.x-mAp[2].p.x)+(mAp[1].p.y-mAp[2].p.y)*(mAp[1].p.y-mAp[2].p.y));
        Point ans=null;
        while(ans==null)
        {
            //两两相交
            if (dis0 + dis1 >= ap01 && dis0 + dis2 >= ap02 && dis1 + dis2 >= ap12) {
                ans = getPoint(dis0, dis1, dis2, ap01, ap02, ap12);
            }
            //圆0与圆1,2相交，圆1,2相离
            if (dis0 + dis1 >= ap01 && dis0 + dis2 >= ap02 && dis1 + dis2 < ap12) {
                double k = ap12 / (dis1 + dis2);
                dis1*=k;
                dis2*=k;
            }
            //圆1与圆0,2相交，圆0,2相离
            if (dis0 + dis1 >= ap01 && dis0 + dis2 < ap02 && dis1 + dis2 >= ap12) {
                double k = ap02 / (dis0 + dis2);
                dis0*=k;
                dis2*=k;
            }
            //圆2与圆0,1相交，圆0,1相离
            if (dis0 + dis1 <= ap01 && dis0 + dis2 >= ap02 && dis1 + dis2 >= ap12) {
                double k = ap01 / (dis1 + dis0);
                dis0*=k;
                dis1*=k;
            }
            //一圆相离
            if (dis0 + dis1 >= ap01 && dis0 + dis2 < ap02 && dis1 + dis2 < ap12) {
                double k = Math.max(ap02 / (dis0 + dis2), ap12 / (dis1 + dis2));
                dis2*=k;
            }
            if (dis0 + dis1 < ap01 && dis0 + dis2 >= ap02 && dis1 + dis2 < ap12) {
                double k = Math.max(ap01 / (dis0 + dis1), ap12 / (dis1 + dis2));
                dis1*=k;
            }
            if (dis0 + dis1 < ap01 && dis0 + dis2 < ap02 && dis1 + dis2 >= ap12) {
                double k = Math.max(ap01 / (dis0 + dis1), ap02 / (dis0 + dis2));
                dis0*=k;
            }
            //三圆相离
            if (dis0 + dis1 < ap01 && dis0 + dis2 < ap02 && dis1 + dis2 < ap12) {
                double k = Math.max(ap01 / (dis0 + dis1), ap02 / (dis0 + dis2));
                k = Math.max(k, ap12 / (dis1 + dis2));
                dis0*=k;
                dis1*=k;
                dis2*=k;
            }
        }
        //System.out.println(ans);
        return ans;
    }

    public Point getPoint(double dis0,double dis1,double dis2,double ap01,double ap02,double ap12){
        Point p1=get2Point(mAp[0],dis0,mAp[1],dis1,mAp[2]);
        Point p2=get2Point(mAp[0],dis0,mAp[2],dis2,mAp[1]);
        Point p3=get2Point(mAp[1],dis1,mAp[2],dis2,mAp[0]);
        double x=0,y=0;
        int cnt=0;
        if(p3!=null){
            x+=p3.x;
            y+=p3.y;
            cnt++;
        }
        if(p2!=null){
            x+=p2.x;
            y+=p2.y;
            cnt++;
        }
        if(p1!=null){
            x+=p1.x;
            y+=p1.y;
            cnt++;
        }
        if(x<0) x=0;
        if(y<0) y=0;
        return new Point(x/cnt,y/cnt);
    }

    public Point get2Point(AP ap1,double r1,AP ap2 ,double r2,AP ap3){
        Point p1=new Point();
        Point p2=new Point();
        double D1 = -2 * ap1.p.x;
        double E1 = -2 * ap1.p.y;
        double F1 = ap1.p.x * ap1.p.x + ap1.p.y * ap1.p.y - r1 * r1;

        double D2 = -2 * ap2.p.x;
        double E2 = -2 * ap2.p.y;
        double F2 = ap2.p.x * ap2.p.x + ap2.p.y * ap2.p.y - r2 * r2;

        if (Math.abs(ap1.p.x - ap2.p.x) < Math.abs(ap1.p.y - ap2.p.y)) //1)圆心连线与y轴平行的情况, 2)以x,y差值大的方向来计算精度更高
        {
            double a = (D2 - D1) / (E1 - E2);
            double b = (F2 - F1) / (E1 - E2);

            double A = a * a + 1;
            double B = 2 * a * b + E1 * a + D1;
            double C = b * b + E1 * b + F1;

            double B4AC = B * B - 4 * A * C;
            if (B4AC < 0)
                return new Point(0,0);

            double tempSqrt = Math.sqrt(B4AC);
            double temp2A = 2 * A;

            double x1 = (-B + tempSqrt) / temp2A;
            double y1 = a * x1 + b;

            p1 = new Point(x1, y1);

            if (B4AC > 0) //2个交点
            {
                double x2 = (-B - tempSqrt) / temp2A;
                double y2 = a * x2 + b;
                p2 = new Point(x2, y2);
            }
            else //1个交点
            {
                p2 = p1;
            }
        }
        else
        {
            double a = (E2 - E1) / (D1 - D2);
            double b = (F2 - F1) / (D1 - D2);

            double A = a * a + 1;
            double B = 2 * a * b + D1 * a + E1;
            double C = b * b + D1 * b + F1;

            double B4AC = B * B - 4 * A * C;
            if (B4AC < 0)
                return null;

            double tempSqrt = Math.sqrt(B4AC);
            double temp2A = 2 * A;

            double y1 = (-B + tempSqrt) / temp2A;
            double x1 = a * y1 + b;

            p1 = new Point(x1, y1);

            if (B4AC > 0) //2个交点
            {
                double y2 = (-B - tempSqrt) / temp2A;
                double x2 = a * y2 + b;
                p2 = new Point(x2, y2);
            }
            else //1个交点
            {
                p2 = p1;
            }
        }
        double dis=p1.getdis(ap3.p);
        double dis2=p2.getdis(ap3.p);
        if(dis<dis2) return p1;
        else  return p2;
    }
    public static double[] unwrap(double[] data) {
        double []ans=new double[data.length];
        double pre=0;
        for (int i=0;i<data.length;i++){
            double dp=data[i]-pre;
            while(Math.abs(dp)>=Math.PI){
                if(dp>0) dp-=Math.PI*2;
                else dp+=Math.PI*2;
                if(Math.abs(dp)==Math.PI) break;
                //System.out.println(dp);
            }
            ans[i]=pre+dp;
            pre=ans[i];
        }
        return  ans;
    }

    public static  double[]line1(double[]data){
        SimpleRegression regression = new SimpleRegression();
        double []ans=new double[m.length];
        int []subindex=new int[data.length];
        for (int i=0;i<64;i++){
            subindex[i]=-32+i;
        }
        for (int i = 0; i < m.length; i++) {
            int index= Arrays.binarySearch(subindex,m[i]);
            regression.addData(m[i],data[index]);
        }
        double b=regression.getIntercept();
        double k=regression.getSlope();
        for (int i = 0; i < m.length; i++) {
            int index=Arrays.binarySearch(subindex,m[i]);
            ans[i]=data[index]-k*m[i]-b;
        }
        return  ans;
    }

    public  double[][]normal(double [][]datas){
        int len=datas.length;
        int num=datas[0].length;

        double []mind=new double[num];
        double []maxd=new double[num];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < num; j++) {
                if(i==0){
                    mind[j]=datas[i][j];
                    maxd[j]=datas[i][j];
                }else {
                    mind[j]=Math.min(mind[j],datas[i][j]);
                    maxd[j]=Math.max(maxd[j],datas[i][j]);
                }
            }
        }


        double [][]ans=new double[len][240];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < datas[i].length; j++) {
                if(maxd[j]!=mind[j])
                    ans[i][j]=(datas[i][j]-mind[j])/(maxd[j]-mind[j]);
                else
                    ans[i][j]=0;

            }
        }
        return  ans;
    }

}
