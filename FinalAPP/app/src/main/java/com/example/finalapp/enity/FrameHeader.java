package com.example.finalapp.enity;

public class FrameHeader{
    public long ts_sec;//捕获包的时间
    public long ts_usec;//捕获此包的秒数
    public long incl_len;//实际捕获并保存在文件中的数据包数据的字节数
    public long orig_len;//捕获时在网络上出现的长度

    @Override
    public String toString() {
        return "FrameHeader{" +
                "ts_sec=" + ts_sec +
                ", ts_usec=" + ts_usec +
                ", incl_len=" + incl_len +
                ", orig_len=" + orig_len +
                '}';
    }
}