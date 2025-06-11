package com.example.finalapp.Utils;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finalapp.enity.Frame;
import com.example.finalapp.enity.FrameHeader;
import com.example.finalapp.enity.GlobalHeader;
import com.example.finalapp.enity.PayloadHeader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ReadPcapUtil{
    public RandomAccessFile file;
    public GlobalHeader globalHeader;

    public ByteOrder myByteOrder=ByteOrder.BIG_ENDIAN;

    //读文件
    public void open(RandomAccessFile file) throws IOException {

        this.file=file;
        long magicNumber=readUInt32();
        //判断大端or小端
        if(magicNumber!=0xA1B2C3D4){
            myByteOrder=ByteOrder.LITTLE_ENDIAN;
        }
        file.seek(0);

        globalHeader=new GlobalHeader();
        globalHeader.magicNumber=readUInt32();
        globalHeader.versionMajor=readUInt16();
        globalHeader.versionMinor=readUInt16();
        globalHeader.thiszone=readInt32();
        globalHeader.sigfigs=readUInt32();
        globalHeader.snaplen=readUInt32();
        globalHeader.network=readUInt32();

        // System.out.println(globalHeader);
    }

    //frame
    public Frame next() throws IOException {
        if(file.getFilePointer()>=file.length()) return null;
        Frame frame=new Frame();
        frame.header=new FrameHeader();
        frame.header.ts_sec=readUInt32();
        frame.header.ts_usec=readUInt32();
        frame.header.incl_len=readUInt32();
        frame.header.orig_len=readUInt32();
        if(frame.header.incl_len==0) return null;

        boolean ismod4= frame.header.incl_len % 4 ==0;
        int payloadLen= (int) (ismod4==true?frame.header.incl_len/4:frame.header.incl_len);
        long []payload=new long[payloadLen];
        for (int i = 0; i < payload.length; i++) {
            if(ismod4) payload[i]=readUInt32();
            else payload[i]=readUInt8();
        }
        frame.payload=payload;
        byte[]byteArray=longArrToByteArr(payload,0,16);
        frame.payloadheader=readPayloadHeader(byteArray,42,64);
        // System.out.println(frame);
        return frame;
    }

    private byte[]longArrToByteArr(long[]arr,int sta,int end){
        int totalLength=(end-sta)*4;
        ByteBuffer buffer=ByteBuffer.allocate(totalLength);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i=sta;i<end;i++){
            buffer.putInt((int) arr[i]);
        }
        return buffer.array();
    }
    // 解析有效载荷头部
    private PayloadHeader readPayloadHeader(byte[]arr, int sta, int end){
        PayloadHeader payloadHeader=new PayloadHeader();
        payloadHeader.magic_bytes=arr[sta]+" "+arr[sta+1];
        boolean flag=false;
        for (int i = 0; i < 4; i++) {
            if(arr[i]!=0x11){
                flag=true;
                break;
            }
        }
        if(!flag){
            payloadHeader.rssi=-1;
            payloadHeader.frame_control=-1.0;
        }else{
            payloadHeader.rssi=(int)(arr[2+sta]);
            payloadHeader.frame_control=(int)(arr[3+sta]&0xff);
        }
        payloadHeader.source_mac=bytesToMac(arr,sta+4,sta+10);
        ByteBuffer buffer=ByteBuffer.wrap(arr,sta+10,2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        payloadHeader.sequence_no=buffer.getShort()&0xFFFF;


        byte[] coreSpatialBytes = new byte[2];
        System.arraycopy(arr, sta+12, coreSpatialBytes, 0, 2);
        int coreSpatialVal = ByteBuffer.wrap(coreSpatialBytes).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
        // 检查字节序
        if (coreSpatialVal > 63) {
            coreSpatialVal = ByteBuffer.wrap(coreSpatialBytes).order(ByteOrder.BIG_ENDIAN).getShort() & 0xFFFF;
        }

        // 提取二进制掩码
        String coreSpatialBits = String.format("%6s", Integer.toBinaryString(coreSpatialVal)).replace(' ', '0');

        payloadHeader.core= Integer.parseInt(coreSpatialBits.substring(3, 6), 2);
        // 提取核心和空间流编号
        payloadHeader.spatial_stream=Integer.parseInt(coreSpatialBits.substring(0,3),2);
        // 提取通道规范
        byte[] channelSpecBytes = new byte[2];
        System.arraycopy(arr, sta+14, channelSpecBytes, 0, 2);
        payloadHeader.channel_spec=bytesToHex(channelSpecBytes);

        // 提取芯片标识符
        byte[] chipIdentifierBytes = new byte[2];
        System.arraycopy(arr, sta+16, chipIdentifierBytes, 0, 2);
        String chipIdentifier = bytesToHex(chipIdentifierBytes);
        payloadHeader.chip=PayloadHeader.CHIPS.getOrDefault(chipIdentifier,"UnKNown");
        return payloadHeader;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString=new StringBuilder();
        for (byte b:bytes){
            hexString.append(String.format("%02x",b));
        }
        return hexString.toString();
    }

    private String bytesToMac(byte[]arr,int sta,int end){
        StringBuilder macBuilder = new StringBuilder();
        for (int i = sta; i < end; i++) {
            macBuilder.append(String.format("%02X", arr[i]));
            if (i !=end-1) {
                macBuilder.append(":");
            }
        }
        return macBuilder.toString();
    }

    public List<Frame> all() throws IOException {
        List<Frame> frames=new ArrayList<>();
        fromStart();
        Frame frame;
        while((frame=next())!=null){
            frames.add(frame);
        }
        return frames;
    }

    public void close() throws IOException {
        if(file!=null) file.close();
    }
    public void fromStart() throws IOException {
        file.seek(24);
    }
    //读取无符号4byte
    private long readUInt32() throws IOException {
        byte[] bytes = new byte[4];
        file.readFully(bytes); // 读取4字节
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(myByteOrder); // 按字节序解析
        return buffer.getInt() & 0xFFFFFFFFL; // 转换为无符号long
    }
    //读取无符号2byte
    private int readUInt16() throws IOException {
        byte[] bytes = new byte[2];
        file.readFully(bytes); // 读取2字节
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(myByteOrder);
        return buffer.getShort() & 0xFFFF; // 转换为无符号int
    }
    //读取int
    private int readInt32() throws IOException {
        byte[] bytes = new byte[4];
        file.readFully(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(myByteOrder);
        return buffer.getInt(); // 直接读取有符号int
    }

    private int readUInt8() throws IOException {
        byte[]bytes=new byte[1];
        file.readFully(bytes);
        ByteBuffer buffer=ByteBuffer.wrap(bytes).order(myByteOrder);
        return buffer.getShort()&0xFFFF;
    }
}
