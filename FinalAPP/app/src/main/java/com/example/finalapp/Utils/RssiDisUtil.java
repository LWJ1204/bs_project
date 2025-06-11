package com.example.finalapp.Utils;

import android.util.Log;


import com.example.finalapp.enity.FrameData;
import com.example.finalapp.enity.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RssiDisUtil {
    public GaussUtil gaussUtil;
    public List<FrameData> Datasporcessed;
    private  List<Point>points=new ArrayList<>();
    private final double [][]inputsys={
            {-41.961797288933525,-37.153932429644506,-34.34606757035549,-22},//1m
            {-43,-38.0,-33.0,0.0},//2m
            {-45.5,-40,-34.0,0.0},//3m
            {-46,-41.0,-36.0,0.0},//4m
            {-50,-42,-40,0.0},//5m
            {-54,-44,-41,0.0},//6m
            {-56,-52,-42,0.0},//7m
            {-59,-52,-43.50,0.0},//8m
            {-59,-54,-44,0.0},//9m
            {-85,-56.0,-54,-52}
    };
    private final double[][]outputsys={
            {-0.8,1.2,1.5,2.9},//1
            {1.0,1.8,2,5.0},//2
            {2.0,3,3.5,0.0},//3
            {2.5,4.0,4.5,0.0},//4
            {5.0,5.5,6.5,0.0},//5
            {7,7.5,8,0.0},//6
            {7.5,8.5,9.5,0},//7
            {8.0,9,9.5,0},//8
            {8.5,9.5,10,0.0},//9
            {9.0,10.0,11.0,14.0},//10
    };
    private List<FrameData> datas;

    public double[]Blurarr;

    public RssiDisUtil(int ksize, double sigma){
        gaussUtil=GaussUtil.getInstance(ksize,sigma);
    }
    public double getdis(){
        double dis=0;
        double []arr=new double[Datasporcessed.size()];
        for (int i = 0; i < Datasporcessed.size(); i++)
        {
            Blur(Datasporcessed.get(i).rssi);
            getPoints();
            double tempdis=distance();
            arr[i]=tempdis;
            Datasporcessed.get(i).dis=tempdis;
        }


        for (int i = 0; i < Datasporcessed.size(); i++) {
            double tdis=Datasporcessed.get(i).dis;
            double p=arr[(int) (tdis)];
            dis+=tdis;
            // System.out.println(dis);
        }
        Arrays.sort(arr);
        int q1= (int) (arr.length*0.4);
        int median= (int) (arr.length*0.5);
        int q3= (int) (arr.length*0.6);

        double dis1=arr[q3]-arr[median];
        double dis0=arr[median]-arr[q1];
        if(dis1<dis0) dis=arr[q3]+arr[median];
        else dis=arr[q1]+arr[median];
        return  dis/2;
    }
    public  void getPoints(){
        points.clear();
        boolean flag=true;
        double tempdis=0;
        double tempx=0;
        for (int i = 0; i < Blurarr.length; i++) {
            if(Blurarr[i]==0) continue;
            if(flag) {
                points.add(new Point( outputsys[i][0],0));
                flag=false;
            }
            if((i-1>=0&&Blurarr[i]>Blurarr[i-1])||i==0){
                tempdis=outputsys[i][1]-outputsys[i][0];
                tempx=outputsys[i][0]+tempdis*Blurarr[i];
            }else{
                if(i-1==0){
                    tempdis=outputsys[i-1][3]-outputsys[i-1][2];
                    tempx=outputsys[i-1][3]-tempdis*Blurarr[i];
                }else{
                    tempdis=outputsys[i-1][2]-outputsys[i-1][1];
                    tempx=outputsys[i-1][2]-tempdis*Blurarr[i];
                }
            }
            points.add(new Point(tempx,Blurarr[i]));
            if((i+1< Blurarr.length&&Blurarr[i]>Blurarr[i+1])||i==Blurarr.length-1){
                if(i==0||i==Blurarr.length-1){
                    tempdis=outputsys[i][3]-outputsys[i][2];
                    tempx=outputsys[i][3]-tempdis*Blurarr[i];
                }else{
                    tempdis=outputsys[i][2]- outputsys[i][1];
                    tempx=outputsys[i][2]-tempdis*Blurarr[i];
                }
            }else{
                tempdis=outputsys[i+1][1]-outputsys[i+1][0];
                tempx=outputsys[i+1][0]+tempdis*Blurarr[i];
            }
            points.add(new Point(tempx,Blurarr[i]));
            if((i+1<Blurarr.length&&Blurarr[i+1]==0)||i==Blurarr.length-1){
                if(i==Blurarr.length-1) points.add(new Point(outputsys[i][3],0));
                else{
                    if(i==0) points.add(new Point(outputsys[i][3],0));
                    else points.add(new Point(outputsys[i][2],0));
                    flag=true;
                }
            }
        }
    }
    public void setDatas(List<FrameData>datas){
        this.datas=datas;
        gaussUtil.setData(this.datas);
        this.Datasporcessed=gaussUtil.convolution();
        this.Datasporcessed.sort(new Comparator<FrameData>() {
            @Override
            public int compare(FrameData o1, FrameData o2) {
                return Double.compare(o1.rssi,o2.rssi);
            }
        });

    }
    public void Blur(double rssi){
        Blurarr=new double[this.inputsys.length];
        for (int i = 0; i < inputsys.length; i++) {
            if(i==0||i==inputsys.length-1){
                Blurarr[i]=getBlur(rssi,i,2);
            }else{
                Blurarr[i]=getBlur(rssi,i,1);
            }
        }
    }
    private double getBlur(double x,int pos,int choose) {
        double ans = 0.0;
        double  a= inputsys[pos][0];
        double  b= inputsys[pos][1];
        double  c= inputsys[pos][2];
        double  d= inputsys[pos][3];
        switch (choose) {
            case 1:
                // 三角隶属函数
                if (x >= a && x < b) {
                    ans = (x - a) / (b - a);
                } else if (x == b) {
                    ans = 1.0;
                } else if (x > b && x <= c) {
                    ans = (c - x) / (c - b);
                } else {
                    ans = 0.0;
                }
                break;
            case 2:
                // 梯形隶属函数
                if (x >= a && x < b) {
                    ans = (x - a) / (b - a);
                } else if (x >= b && x < c) {
                    ans = 1.0;
                } else if (x >= c && x < d) {
                    ans = (d - x) / (d - c);
                } else {
                    ans = 0.0;
                }
                break;
        }
        return ans;

    }


    public double[] getdouble(){
        double[]arr=new double[this.Datasporcessed.size()];
        for (int i = 0; i < this.Datasporcessed.size(); i++) {
            arr[i]=this.Datasporcessed.get(i).dis;
        }
        return arr;
    }

    private double distance(){
        if(points.isEmpty()) return 1;
        double x1,y1,x2,y2,x3,y3;
        double sum_x=0,sum_s=0;
        //if(points.isEmpty()) return -1;
        x1=points.get(0).x;
        y1=points.get(0).y;
        x2=points.get(0).x;
        y2=points.get(0).y;
        int n=points.size();
        int k=2;
        for (int i=1;i<=n-2;i++){
            x3=points.get(k).x;
            y3=points.get(k).y;
            double s=((x2-x1)*(y3-y1)-(x3-x1)*(y2-y1))/2.0;
            sum_x+=(x1+x2+x3)*s;
            sum_s+=s;
            x2=x3;
            y2=y3;
            k++;
        }
        return  sum_x/sum_s/3.0;
    }

}
