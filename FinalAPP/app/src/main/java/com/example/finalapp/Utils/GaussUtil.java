package com.example.finalapp.Utils;


import com.example.finalapp.enity.FrameData;

import java.util.ArrayList;
import java.util.List;

public class GaussUtil {
    private String tag="Gasusstag";
    private int ksize;//模版大小
    private double sigma;
    private List<FrameData> datas;
    private double[] gausstemplate;//模版

    public static GaussUtil gaussUtil=null;

    public static GaussUtil getInstance(int ksize,double sigma){
        if(gaussUtil==null){
            gaussUtil=new GaussUtil(ksize,sigma);
        }
        return  gaussUtil;
    }

    private GaussUtil(int ksize, double sigma){
        this.ksize = ksize;
        this.sigma=sigma;
        initGauss();
    }

    //初始化模版
    private void initGauss() {
        gausstemplate=new double[ksize];
        double sum=0;
        for (int i = 0; i < ksize; i++) {
            int x=i-(ksize-1)/2;
            gausstemplate[i]= Math.exp(-(x*x)/(2*sigma*sigma))/(sigma*Math.sqrt(2*Math.PI));
            sum+=gausstemplate[i];
        }
        for (int i = 0; i < ksize; i++) {
            gausstemplate[i]/=sum;
        }
    }

    public void setData(List<FrameData>datas){
        this.datas=datas;
    }

    //高斯滤波计算
    public List<FrameData> convolution(){
        List<FrameData>output=new ArrayList<>();
        int i=0;
        for (i = 0; i+ksize-1<datas.size(); i++){
            double tempRssi=0;
            for (int j = 0; j < ksize; j++) {
                tempRssi+=datas.get(i+j).rssi*gausstemplate[j]; //rssi进行滤波处理
            }
            output.add(new FrameData(datas.get(i+ksize/2).Phase,datas.get(i+ksize/2).Amplitude,tempRssi,datas.get(i+ksize/2).timestamp));
        }
        return output;
    }

}
