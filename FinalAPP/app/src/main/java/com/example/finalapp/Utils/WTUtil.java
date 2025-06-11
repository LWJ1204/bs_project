package com.example.finalapp.Utils;

import java.util.Arrays;

import jwave.Transform;
import jwave.tools.MathToolKit;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.Wavelet;
import jwave.transforms.wavelets.daubechies.Daubechies4;

public class WTUtil {


    public double[]signal;
    public int level;
    public double alpha;
    public double beta;

    double[][]coef;
    Wavelet wavelet=new Daubechies4();
    Transform t=new Transform(new FastWaveletTransform(wavelet));
    public  int transformWavelength ;

    public void setData(double[] signal){
        this.signal=signal;
    }
    public WTUtil(int level,double beta,double alpha){
        this.level=level;
        this.beta=beta;
        this.alpha=alpha;
        transformWavelength=wavelet.getTransformWavelength();
    }
    public void trans() throws Exception {
        // System.out.println("trans");
        if (!MathToolKit.isBinary(this.signal.length)) {
            throw new Exception("FastWaveletTransform#forward - given array length is not 2^p | p € N ... = 1, 2, 4, 8, 16, 32, .. please use the Ancient Egyptian Decomposition for any other array length!");
        } else {
            int noOfLevels = this.calcExponent(this.signal.length);
            if (level >= 0 && level <= noOfLevels) {
                double[] arrHilb = Arrays.copyOf(this.signal, this.signal.length);//初始化分解系数
                int l = 0;
                int h = arrHilb.length;
                this.coef=new double[level][];
                for(int transformWavelength = this.transformWavelength; h >= transformWavelength && l < level; ++l) {
                    double[] arrTempPart =wavelet.forward(arrHilb,h);
                    System.arraycopy(arrTempPart, 0, arrHilb, 0, h);
                    this.coef[l]=new double[h];//前half 低频  后half 高频
                    System.arraycopy(arrHilb,0,this.coef[l],0,h);
                    h >>= 1;
                }
            } else {
                throw new Exception("FastWaveletTransform#forward - given level is out of range for given array");
            }
        }
    }
    public void denoise(){
        //System.out.println("denoise");
        double sigma=estimateNoiseStd(coef[0]);
        double  threshold=sigma*Math.sqrt(2*Math.log(this.signal.length));
        for (int i=0;i<level;i++){
            if(i!=0) threshold*=Math.sqrt(2)/2;
            for (int j=coef[i].length/2;j<coef[i].length;j++){
                double absc=Math.abs(coef[i][j]);
                if (absc>=threshold){
                    coef[i][j]=sign(coef[i][j])*(absc-threshold*getT(absc,threshold));
                }else{
                    coef[i][j]=0;
                }
            }
        }
    }
    public double[] reset(){
        int steps=calcExponent(coef[0].length);
        int h=this.transformWavelength;
        double []arrtime=new double[coef[0].length];
        for (int l=this.level;l<steps;l++){
            h<<=1;
        }
        int i=this.level-1;

        while(i>=0){

            double[]arrtemppart=wavelet.reverse(coef[i],h);

            System.arraycopy(arrtemppart,0,arrtime,0,h);
            for(int j=0;i-1>=0&&j<coef[i-1].length/2;j++){
                coef[i-1][j]=arrtemppart[j];
            }
            h<<=1;
            i--;
        }

        return arrtime;
    }
    private int calcExponent(int number) {
        if (!MathToolKit.isBinary(number)) {
            throw new RuntimeException("BasicTransform#calcExponent - given number is not binary: 2^p | p€N .. = 1, 2, 4, 8, 16, 32, .. ");
        } else {
            return MathToolKit.getExponent((double)number);
        }
    }

    private double sign(double v) {
        if(v>0) return 1;
        else if(v<0) return  -1;
        return  0;
    }

    double getT(double w,double e){
        return this.beta/(Math.pow(this.alpha,Math.abs(w-e))+this.beta-1);
    }

    // 使用MAD估计噪声标准差
    public double estimateNoiseStd(double[] detailCoefficients) {
        double[] absCoefficients = new double[detailCoefficients.length];
        for (int i = 0; i < detailCoefficients.length; i++) {
            absCoefficients[i] = Math.abs(detailCoefficients[i]);
        }
        double median = median(absCoefficients);
        return median / 0.6745;
    }

    // 计算中位数
    public double median(double[] values) {
        double[] sortedValues = values.clone();
        java.util.Arrays.sort(sortedValues);
        int middle = sortedValues.length / 2;
        if (sortedValues.length % 2 == 0) {
            return (sortedValues[middle - 1] + sortedValues[middle]) / 2.0;
        } else {
            return sortedValues[middle];
        }
    }

}
