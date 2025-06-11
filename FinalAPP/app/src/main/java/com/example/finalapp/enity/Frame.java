package com.example.finalapp.enity;
import java.util.Arrays;

public class Frame{
    public FrameHeader header;
    public PayloadHeader payloadheader;

    public  long[] payload;

    @Override
    public String toString() {
        return "Frame{" +
                "header=" + header +
                ", payloadheader=" + payloadheader +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }
}