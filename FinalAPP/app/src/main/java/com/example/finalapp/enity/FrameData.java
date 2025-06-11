package com.example.finalapp.enity;


public class FrameData {
    public static int rx_num;
    public static int tx_num;
    public double [][]Phase;
    public double [][]Amplitude;
    public double rssi;

    public double mdis;

    public double dis;
    public long timestamp;

    public FrameData() {
    }

    public FrameData(double[][] phase, double[][] amplitude, double rssi, long timestamp) {
        Phase = phase;
        Amplitude = amplitude;
        this.rssi = rssi;
        this.timestamp = timestamp;
    }

    public double[] phase;
    public double[] amplitude;
    public double[] alldata;
    public void changeShape(){
        if(amplitude!=null) return;
        phase=new double[Phase.length*Phase[0].length];
        System.arraycopy(Phase[0],0,phase,0,Phase[0].length);
        System.arraycopy(Phase[1],0,phase,Phase[0].length,Phase[1].length);
        amplitude=new double[Phase.length*Phase[0].length];
        System.arraycopy(Amplitude[0],0,amplitude,0,Amplitude[0].length);
        System.arraycopy(Amplitude[1],0,amplitude,Amplitude[0].length,Amplitude[1].length);
        alldata=new double[phase.length*2];
        System.arraycopy(amplitude,0,alldata,0,phase.length);
        System.arraycopy(phase,0,alldata,amplitude.length,phase.length);
    }

}
