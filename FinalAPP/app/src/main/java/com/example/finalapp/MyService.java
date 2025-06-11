package com.example.finalapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.finalapp.database.MsgdbHelper;
import com.example.finalapp.enity.MsgInfo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.tools.json.JSONUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class MyService extends Service {
    ConnectionFactory factory=new ConnectionFactory();
    private static final String HOST = "39.107.237.214";
    private static final int PORT = 5672;
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static final String VIRTUAL_HOST = "/";
    private static final String tag="Service";
    private Connection conn;
    private Channel channel;
    private String QueueName=null;
    private DeliverCallback deliverCallback;
    private ExecutorService executor;


    public MyService() throws IOException, TimeoutException {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.QueueName= intent.getStringExtra("queuename");
        initMQ();
        Log.i(tag,"注册成功");
        Log.i(tag,"queuename:"+this.QueueName);
        return START_NOT_STICKY;
    }
    private void initMQ() {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                factory.setHost(HOST);
                factory.setPort(PORT);
                factory.setUsername(USERNAME);
                factory.setPassword(PASSWORD);
                factory.setVirtualHost(VIRTUAL_HOST);

                conn = factory.newConnection();
                channel = conn.createChannel();

                if (channel == null) {
                    Log.e(tag, "Channel is null");
                    return;
                }

                deliverCallback =new DeliverCallback() {
                    @Override
                    public void handle(String consumerTag, Delivery message) throws IOException {
                        try
                        {
                            Log.i(tag, "监听到了");
                            String msg = new String(message.getBody(), "UTF-8");
                            JSONObject jobj=new JSONObject(msg);
                            MsgInfo msgInfo=new MsgInfo();

                            Log.i(tag,"接收到的数据为："+jobj.toString());;
                            msgInfo.setCourseid((String) jobj.get("courseid"));
                            msgInfo.setRecordid(jobj.optLong("recordid",0));
                            msgInfo.setClassroom(jobj.optString("classroom",""));
                            msgInfo.setReadFlag(0);
                            msgInfo.setCoursename(jobj.optString("coursename",""));
                            msgInfo.setRecordarrtime(null);
                            msgInfo.setRecordid(jobj.optLong("recordid",0));
                            msgInfo.setRecordendtime(jobj.optLong("recordendtime",0));
                            msgInfo.setRecordsettime(jobj.optLong("recordsettime",0));
                            msgInfo.setRecordstate(1);
                            Intent intent=new Intent("RecordCome");
                            intent.putExtra("msg",msgInfo);
                            //发送广播
                            sendBroadcast(intent);
                            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Log.i(tag,"处理消息时发生错误："+e.toString());
                            try {
                                channel.basicNack(message.getEnvelope().getDeliveryTag(), false, true);
                            }catch (IOException ie){
                                ie.printStackTrace();
                            }
                        }
                    }
                };

                channel.queueDeclare(QueueName, true, false, false, null);
                channel.basicConsume(QueueName, false, deliverCallback, consumerTag -> {});

                Log.i(tag, "绑定成功");
                Log.i(tag, "Connection: " + channel.getConnection());
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                Log.e(tag, "Exception occurred: " + e.getMessage());
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            if(channel!=null){
                channel.close();
            }
            if(conn!=null){
                conn.close();
            }
        }catch (Exception e){
            Log.e(tag,"error:"+e);
        }
        executor.shutdown();
    }


}