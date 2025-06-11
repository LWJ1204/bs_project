package com.example.finalapp.enity;
import jwave.datatypes.natives.Complex;

public class myComplex extends Complex {
    public double real;
    public double imag;

    public myComplex() {
    }

    public myComplex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }
    //加
    public myComplex add(myComplex o){
        return new myComplex(this.real+o.real,this.imag+o.real);
    }
    //减
    public myComplex reduce(myComplex o){return new myComplex(this.real-o.real,this.imag-o.imag);}
    //乘法
    public myComplex multiply(myComplex o){return new myComplex(this.real*o.real-this.imag*o.imag,this.real*o.imag+this.imag*o.real); }
    //除法
    public myComplex divide(myComplex o){
        if(o==null) return this;
        double sumabs=o.abs()*o.abs();
        double real=(this.real*o.real+this.imag*o.imag)/sumabs;
        double imag=(this.imag*o.real-this.real*o.imag)/sumabs;
        return  new myComplex(real,imag);
    }

    public myComplex conjugate(){
        this.imag=-this.imag;
        return this;
    }
    //f幅度
    public double abs(){
        return (double) Math.sqrt(real*real+imag*imag);
    }
    //相位
    public double angle(){
        return Math.atan2(imag,real);
    }

    @Override
    public String toString() {
        return "Complex{" +
                "real=" + real +
                ", imag=" + imag +
                '}';
    }
}
