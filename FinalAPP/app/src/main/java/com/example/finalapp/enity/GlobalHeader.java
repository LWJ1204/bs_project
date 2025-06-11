package com.example.finalapp.enity;

public class GlobalHeader{
    public long magicNumber;//检验字节顺序&标志位
    public int versionMajor;//主版本
    public int versionMinor;//次版本
    public int thiszone;//时区
    public long sigfigs;//时间抽准确性0
    public long snaplen;//快照长度
    public long network;//链路层类型

    @Override
    public String toString() {
        return "GlobalHeader{" +
                "magicNumber=" + magicNumber +
                ", versionMajor=" + versionMajor +
                ", versionMinor=" + versionMinor +
                ", thiszone=" + thiszone +
                ", sigfigs=" + sigfigs +
                ", snaplen=" + snaplen +
                ", network=" + network +
                '}';
    }
}
