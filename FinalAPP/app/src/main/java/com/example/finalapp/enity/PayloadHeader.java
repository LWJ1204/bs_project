package com.example.finalapp.enity;

import java.util.HashMap;
import java.util.Map;

public class PayloadHeader{
    public static Map<String,String> CHIPS =new HashMap<>(){{
        put("0100", "4339");
        put("6500","43455c0");
        put("dca6", "43455c0");
        put( "0300","4358");
        put("adde","4358");
        put("34e8", "4366c0");
        put("6a00","4366c0");
    }};
    public String magic_bytes;
    public  double rssi;
    public double frame_control;
    public String source_mac;
    public int sequence_no;
    public int core;
    public int spatial_stream;
    public String channel_spec;
    public String chip;

    @Override
    public String toString() {
        return "PayloadHeader{" +
                "magic_bytes='" + magic_bytes + '\'' +
                ", rssi=" + rssi +
                ", frame_control=" + frame_control +
                ", source_mac='" + source_mac + '\'' +
                ", sequence_no=" + sequence_no +
                ", core=" + core +
                ", spatial_stream=" + spatial_stream +
                ", channel_spec='" + channel_spec + '\'' +
                ", chip='" + chip + '\'' +
                '}';
    }
}
