package com.example.finalapp.enity;

public class CsiDataPoint{
    public long ts_sec;//捕获包的时间
    public long ts_usec;//捕获此包的秒数

    public myComplex[] datas;

    public CsiDataPoint() {
    }

    public CsiDataPoint(long ts_sec, long ts_usec, myComplex[]datas) {
        this.ts_sec = ts_sec;
        this.ts_usec = ts_usec;
        this.datas = datas;
    }

    public long getTs_sec() {
        return ts_sec;
    }

    public void setTs_sec(long ts_sec) {
        this.ts_sec = ts_sec;
    }

    public long getTs_usec() {
        return ts_usec;
    }

    public void setTs_usec(long ts_usec) {
        this.ts_usec = ts_usec;
    }

    public myComplex[] getDatas() {
        return datas;
    }

    public void setDatas(myComplex[] datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "CsiDataPoint{" +
                "ts_sec=" + ts_sec +
                ", ts_usec=" + ts_usec +
                '}';
    }
}