package com.example.finalapp.Utils;

import com.example.finalapp.enity.CsiDataPoint;
import com.example.finalapp.enity.Frame;
import com.example.finalapp.enity.FrameData;
import com.example.finalapp.enity.myComplex;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

public class ProcessPcapUtil {
    private static final int K_TOF_UNPACK_SGN_MASK = 1 << 31;
    private int BW;//频段宽
    private int NPKTS_MAX=1000;//
    private RandomAccessFile filename;
    private int HOFFSET=16;//起始
    private int NFFT;//数据点
    private int N;
    private int Rxnum=0;
    private int Txnum=0;
    public List<Frame> frames=new ArrayList<>();
    private ReadPcapUtil readPcapUtil;
    public ProcessPcapUtil(){}

    public ProcessPcapUtil(int BW,RandomAccessFile filename) throws IOException {
        setBW(BW);
        setFilename(filename);
    }

    public void setBW(int BW){
        this.BW=BW;
        this.NFFT=(int)(this.BW*3.2);
    }

    public void setFilename(RandomAccessFile file) throws IOException {
        this.filename=file;
        this.readPcapUtil=new ReadPcapUtil();
        this.readPcapUtil.open(file);

        init();
        this.readPcapUtil.fromStart();
    }

    public void init(){
        int maxcore=0,maxspatial=0;
        try {
            Map<String,Integer> mp=new HashMap<>();
            this.frames= this.readPcapUtil.all();

            this.N=Math.min(this.frames.size(),this.NPKTS_MAX);
            for (Frame frame : this.frames) {
                maxcore=Math.max(maxcore,frame.payloadheader.core);
                maxspatial=Math.max(maxspatial,frame.payloadheader.spatial_stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            this.Rxnum=maxcore+1;
            this.Txnum=maxspatial+1;
        }

    }



    public Map<String,List<FrameData>> process(boolean normal,boolean rssiflag) throws IOException {
        Map<String,List<FrameData>> mp=new HashMap<>();
        if(FrameData.rx_num<this.Rxnum) FrameData.rx_num=this.Rxnum;
        if(FrameData.tx_num<this.Txnum) FrameData.tx_num=this.Txnum;
        int cpos=0;
        FrameData frameData=new FrameData();
        int t=1;
        Frame f=new Frame();
        //System.out.println("process 75:"+this.N);
        while (t<=this.N){
            CsiDataPoint tempcsi=new CsiDataPoint();
            f=this.readPcapUtil.next();
            if(f==null){
                System.out.println("no more Frames");
                break;
            }
            if(f.header.orig_len-(HOFFSET-1)*4!=4*this.NFFT){
                System.out.println("不符合长度");
                continue;
            }

            long []temppayload=f.payload;
            long []H= Arrays.copyOfRange(temppayload,this.HOFFSET-1,this.HOFFSET+this.NFFT-1);//一个数据包的负载数据
            int[][]data=unpack_float(H);

            //Frame data
            if(cpos==0||cpos>=this.Txnum*this.Rxnum) {
                if(cpos!=0)mp.computeIfAbsent(f.payloadheader.source_mac,k->new ArrayList<>()).add(frameData);
                frameData=new FrameData();
                frameData.Phase=new double[this.Rxnum*this.Txnum][data.length];
                frameData.Amplitude=new double[this.Rxnum*this.Txnum][data.length];
                cpos=0;
            }
            frameData.rssi=f.payloadheader.rssi;
            frameData.timestamp=f.header.ts_sec;


            tempcsi.datas=new myComplex[data.length];
            for (int i = 0; i < data.length; i++) {
                tempcsi.datas[i]=new myComplex(data[i][0],data[i][1]);
            }
            //根据rssi恢复csi
            if(rssiflag){
                double []arr=new double[data.length];
                double rssipwr=Math.pow(10,f.payloadheader.rssi/10);//转为mw
                double sum=0;
                for (int i = 0; i < arr.length; i++) {
                    arr[i]=tempcsi.datas[i].abs();
                    sum+=arr[i]*arr[i];
                }

                double norm_csi_mag=sum/this.NFFT;
                //System.out.println("110:"+norm+" "+norm_csi_mag);
                double scale=rssipwr/norm_csi_mag;//计算s
                for (int i = 0; i < tempcsi.datas.length; i++) {
                    tempcsi.datas[i].real*=Math.sqrt(scale);
                    tempcsi.datas[i].imag*=Math.sqrt(scale);
                }
            }

            tempcsi.datas=fftshift(tempcsi.datas);
            for (int i = 0; i < data.length; i++)
            {
                frameData.Amplitude[cpos][i] = tempcsi.datas[i].abs();
                //System.out.println("Pro_88:"+ans.Amplitude[(k-1)/(this.Txnum*this.Rxnum)][f.payloadheader.spatial_stream][f.payloadheader.core][i]);
                frameData.Phase[cpos][i]= tempcsi.datas[i].angle();
                //System.out.println("Pro_90:"+ans.Phase[(k-1)/(this.Txnum*this.Rxnum)][f.payloadheader.spatial_stream][f.payloadheader.core][i]);
            }
            if(normal){
                double maxMagnitude = getMax(frameData.Amplitude[cpos]);
                for (int j = 0; j < frameData.Amplitude[cpos].length; j++) {
                    //System.out.println("109:"+frameData.Amplitude[cpos][j]);
                    frameData.Amplitude[cpos][j] /= maxMagnitude;
                    //System.out.println(frameData.Amplitude[cpos][j]);
                }

            }
            cpos++;
            t+=1;
        }
        if(cpos==this.Rxnum*this.Txnum) mp.computeIfAbsent(f.payloadheader.source_mac,k->new ArrayList<>()).add(frameData);
        return mp;
    }

    private myComplex[] fftshift(myComplex[] datas) {
        myComplex[]temp=new myComplex[datas.length];
        for (int i = 0; i < datas.length; i++) {
            int idx=(i+ datas.length/2)%datas.length;
            temp[idx]=datas[i];
        }
        return temp;
    }

    public double getMax(double[] arr){
        if(arr.length==0){ return -1;}
        double maxans=Math.abs(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            maxans=Math.max(maxans,Math.abs(arr[i]));
        }
        return maxans;
    }
    public int[][]unpack_float(long[] payload){
        if(this.NFFT<payload.length){
            System.out.println("NexmonCSI:unpack_float:Hsize\",\n" + " \"Length of H must be at least nfft.\"");
        }
        int[] data=unpack_float_acphy(10,1,0,1,9,5,this.NFFT,payload);
        if (data.length%2!=0){
            System.out.println("ans.size 不能整除2");

        }
        int [][]ans=new int [data.length/2][2];
        for(int i=0;i<data.length/2;i++){
            ans[i][0]=data[i*2];
            ans[i][1]=data[i*2+1];
            // System.out.println(i+" "+ans[i][0]+" "+ans[i][1]);
        }

        return ans;
    }

    public int[]unpack_float_acphy(int nbits,int autoscale,int shft,
                                   int fmt,int nman,int nexp,int nfft,
                                   long[]payload)
    {
        //计算掩码
        int iq_mask=(1<<(nman-1))-1;//8_1  尾数掩码
        int e_mask=(1<<nexp)-1;//5_1 e掩码
        long sgnr_mask=(1L <<(nexp+2*nman-1));//2^22 符号掩码
        long sgni_mask=(sgnr_mask>>nman);


        int e_p=(1<<(nexp-1));//16 偏移量
        int e_zero=-nman;
        int pOut=0;
        int n_out=(nfft<<1);
        int e_shift=1;
        int maxbit=-e_p;
        int vi,vq,e;
        int []He=new int[nfft];
        myComplex[]ans=new myComplex[nfft];
        long x;
        int[]Hout=new int[n_out];

        for (int i = 0; i < nfft; i++) {
            //提取i，q分量
            vi= (int) ((payload[i]>>(nexp+nman))&(iq_mask));
            vq=(int)((payload[i]>>nexp)&iq_mask);
            //处理指数
            e= (int) (payload[i]&e_mask);
            if(e>=e_p){
                e-=(e_p<<1);
            }
            He[i]=e;
            x=(long)(vi)|(long)(vq);
            if((autoscale!=0)&&((vi|vq)!=0)){
                int m=0xffff0000,b=0xffff;
                int s=16;
                while(s>0) {
                    if ((x & m) != 0) {
                        e += s;
                        x >>= s;
                    }
                    s >>= 1;
                    m = (m >> s) & b;
                    b >>= s;
                }
                if(e>maxbit){
                    maxbit=e;
                }
            }
            // 符号位处理
            if ((payload[i] & sgnr_mask) != 0) vi |= this.K_TOF_UNPACK_SGN_MASK;
            if ((payload[i] & sgni_mask) != 0) vq |= this.K_TOF_UNPACK_SGN_MASK;
            Hout[i << 1] = vi;
            Hout[(i << 1) + 1] = vq;
        }
        shft=nbits-maxbit;

        for (int i = 0; i < n_out; i++) {
            int idx=i>>e_shift;
            e=He[idx]+shft;
            vi=Hout[i];
            int sgn=1;
            if((vi&this.K_TOF_UNPACK_SGN_MASK)!=0){
                sgn=-1;
                vi&=~this.K_TOF_UNPACK_SGN_MASK;
            }
            if(e<e_zero){
                vi=0;
            }
            else if(e<0){
                e=-e;
                vi=vi>>e;
            }
            else{
                vi=vi<<e;
            }
            Hout[pOut++]=sgn*vi;
        }

        return Hout;
    }

}

