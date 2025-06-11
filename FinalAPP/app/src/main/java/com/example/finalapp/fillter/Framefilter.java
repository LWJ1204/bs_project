package com.example.finalapp.fillter;

import jwave.transforms.FastWaveletTransform;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static org.apache.commons.math3.linear.MatrixUtils.inverse;

import com.example.finalapp.enity.FrameData;


public class Framefilter {
    private List<FrameData>data;
    private double[][]mean;

    private int subnum;
    RealMatrix[] covarianceMatrix;

    public Framefilter(int subnum){
        this.subnum=subnum;
        mean=new double[2][this.subnum];
        covarianceMatrix=new RealMatrix[2];
        for (int i = 0; i < 2; i++) {
            covarianceMatrix[i]=new Array2DRowRealMatrix(this.subnum,this.subnum);
        }

    }
    public  void setData(List<FrameData>data){
        this.data=data;
        for (int i = 0; i < data.size(); i++) {
            data.get(i).changeShape();
        }
    }

    public void  getu(int choice){
        int len=data.size();
        for (int i = 0; i < len; i++) {
            FrameData f=data.get(i);
            f.changeShape();
            for (int j=0;j<f.amplitude.length;j++){
                if(choice==0) mean[j/this.subnum][j%this.subnum]+=f.amplitude[j]/len;
                else if (choice==1) {
                    mean[j/this.subnum][j%this.subnum]+=f.phase[j]/len;
                }
            }
        }

        //计算协方差举证
        Covariance covariance=new Covariance();
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < this.subnum; i++) {
                for (int j = 0; j < this.subnum; j++) {
                    covarianceMatrix[k].setEntry(i,j,covariance.covariance(getColumn(i,k,choice),getColumn(j,k,choice)));
                }
            }
        }


        // System.out.println(covarianceMatrix);

    }



    //choice 0---A  1--P
    public List<FrameData> filter(int choice) {
        getu(choice);
        //计算每个帧的马氏距离
        for (int i = 0; i < this.data.size(); i++) {
            double dis=0;
            RealMatrix fragementk;
            for (int j = 0; j < 2; j++) {
                if (choice==0)
                    fragementk= new Array2DRowRealMatrix(this.data.get(i).Amplitude[j]);
                else
                    fragementk=new Array2DRowRealMatrix(this.data.get(i).Phase[j]);
                RealMatrix Meank = new Array2DRowRealMatrix(this.mean[j]);
                RealMatrix inverseConvarianceMartix = inverse(covarianceMatrix[j]);
                RealMatrix disvector= fragementk.subtract(Meank).transpose();
                RealMatrix ans=disvector.multiply(inverseConvarianceMartix).multiply(disvector.transpose());
                dis+=ans.getData()[0][0];
            }
            this.data.get(i).mdis=dis/2;
            //System.out.println(this.data.get(i).mdis);
        }

        int len= (int) Math.floor(this.data.size()*0.8);
        this.data.sort(new Comparator<FrameData>() {
            @Override
            public int compare(FrameData o1, FrameData o2) {
                return Double.compare(o1.mdis, o2.mdis);
            }
        });

        List<FrameData>temp=this.data.subList(0,len);
        temp.sort(new Comparator<FrameData>() {
            @Override
            public int compare(FrameData o1, FrameData o2) {
                return Long.compare(o1.timestamp,o2.timestamp);
            }
        });
        return temp;
    }





    public double[] getColumn ( int column, int k,int choice){
        double[] ans = new double[this.data.size()];
        for (int i = 0; i < ans.length; i++) {
            if(choice==0)   ans[i] = this.data.get(i).Amplitude[k][column];
            else if(choice==1) ans[i] = this.data.get(i).Phase[k][column];
        }
        return ans;
    }


}
