package com.example.finalapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.finalapp.Callback.TimeCallback;
import com.example.finalapp.Request.HttpEngine;
import com.example.finalapp.Utils.GaussUtil;
import com.example.finalapp.Utils.GetDeviceUtil;
import com.example.finalapp.Utils.LocalHirbertUtil;
import com.example.finalapp.Utils.LocationUtil;
import com.example.finalapp.Utils.ProcessPcapUtil;
import com.example.finalapp.database.MsgdbHelper;
import com.example.finalapp.enity.FrameData;
import com.example.finalapp.enity.MsgInfo;
import com.example.finalapp.enity.Point;
import com.example.finalapp.enity.Result;

import android.Manifest;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.WatchEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observer;


public class LocationActivity extends AppCompatActivity implements View.OnClickListener {

    private String tag = "Location_tag";
    private boolean flag=true;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Button location_btn;
    private ImageView back_img;
    private TextView Positionx;
    private TextView Positiony;
    private TextView Code;
    private TextView resultText;
    private ExecutorService executorService;
    private LocalHirbertUtil localHirbertUtil;
    private HttpEngine httpEngine;
    private MsgInfo msgInfo;
    private Long crruenttime;
    //数据库
    private MsgdbHelper msgdbHelper;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        //获取信息
        try {
            Intent intent=getIntent();
            if(intent!=null){
                msgInfo= (MsgInfo) intent.getSerializableExtra("MsgInfo");
                Log.i(tag,"LocationActivity:"+msgInfo.toString());
            }
        }catch (Exception e){
            Log.i(tag,e.toString());
        }

        //连接数据库
        msgdbHelper=MsgdbHelper.getInstance(LocationActivity.this);
        msgdbHelper.openWriteLink();
        msgdbHelper.openReadLink();

        location_btn = findViewById(R.id.location_btn);
        back_img=findViewById(R.id.back_location);
        back_img.setOnClickListener(this);
        location_btn.setOnClickListener(this);

        Positionx=findViewById(R.id.Positionx_num);
        Positiony=findViewById(R.id.PostionY_num);
        Code=findViewById(R.id.Code_Show);
        resultText=findViewById(R.id.result_record);
        //初始化httpendgin
        httpEngine=HttpEngine.getInstance(LocationActivity.this);
        // 初始化 ExecutorService
        executorService = Executors.newSingleThreadExecutor();
        // 检查是否已经拥有读取权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        loadLocalHirbertUtil();
    }

    private void loadLocalHirbertUtil() {
        executorService.execute(() -> {
            try {
                // 在后台线程中加载 LocalHirbertUtil
                localHirbertUtil = new LocalHirbertUtil(this,16,2);
                localHirbertUtil.show();
                runOnUiThread(() -> {
                    // 初始化成功，可以使用 localHirbertUtil
                    Log.i(tag, "LocalHirbertUtil loaded successfully");
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // 初始化失败
                    Log.e(tag, "Failed to load LocalHirbertUtil", e);
                    Toast.makeText(LocationActivity.this, "Failed to load LocalHirbertUtil", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                // 权限被拒绝
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onClick(View view) {
        if (view.equals(location_btn)&&flag) {
            flag=false;
            Log.i(tag, "点击了");
            //mwifi.startScan();
            String []params={
                    "ndABEwAAAQCMU8PCnOsAAAAAAAAAAAAAAAAAAAAAAAAAAA==",//1  9ce9
                    "ndABEwAAAQDIv0ybpj4AAAAAAAAAAAAAAAAAAAAAAAAAAA==",//2 3769
                    "ndABEwAAAQBkbpcjjEEAAAAAAAAAAAAAAAAAAAAAAAAAAA==",//3 wdr

            };
//            WifiManager wifiManager = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            int netid = wifiInfo.getNetworkId();// 获取当前连接的SSID
//            wifiManager.disconnect();
            //exec_train(params);
//            wifiManager.setWifiEnabled(true);
//            wifiManager.enableNetwork(netid,true);
//            wifiManager.reconnect();
            try {
              //  Point p=Locate();
                Point p =new Point(3,4);
                Positionx.setText(String.format("%.2f", p.x));
                Positiony.setText(String.format("%.2f", p.y));
                int x= (int) Math.ceil(p.x/0.65);
                int y=(int)Math.ceil(p.y/0.65);
                String code=localHirbertUtil.applyPrivacy(new Point(x,y));
                Code.setText(code);

                Map<String, Object> data = new HashMap<>();
                getTimeMethod(new TimeCallback() {
                    @Override
                    public void onSuccess(String formattedTime) {
                        if (msgInfo != null) {
                            SharedPreferences sharedPreferences = getSharedPreferences("permission", Context.MODE_PRIVATE);
                            data.put("RecordId", msgInfo.getRecordid());
                            data.put("StudentId", sharedPreferences.getString("user", "default"));
                            data.put("ArrTime", formattedTime);
                            data.put("Code", code);
                            String  message=msgInfo.getRecordid()+"_+"+sharedPreferences.getString("user","default")+"_+"+formattedTime+"_+"+code;
                            data.put("message", GetDeviceUtil.sha256Hex(message));
                        }
                        httpEngine.SendRecord(data, new Observer<Result<String>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(tag,"发生了错误"+e);
                            }

                            @Override
                            public void onNext(Result<String> stringResult) {
                                Log.i(tag,"签到成功："+stringResult.toString());
                                String data=stringResult.getData();
                                Toast.makeText(LocationActivity.this,data,Toast.LENGTH_SHORT).show();
                                if(data.equals("签到成功")){
                                    msgInfo.setRecordstate(0);

                                    int isok=  msgdbHelper.update(msgInfo);

                                    resultText.setText("考勤成功");
                                    resultText.setTextColor(Color.GREEN);


                                }else{
                                    resultText.setText("签到失败");
                                    resultText.setTextColor(Color.RED);
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(LocationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            flag=true;
        }else if (view.equals(back_img)){
            Intent intent=new Intent(LocationActivity.this,ALL_Activity.class);
            intent.putExtra("flag",false);
            startActivity(intent);
        }

    }


    private void getTimeMethod(TimeCallback callback) {
        httpEngine.getTime(new Observer<Result<Long>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag, "获取时间失败");
                callback.onFailure("获取时间失败");
            }

            @Override
            public void onNext(Result<Long> longResult) {
                Long time = longResult.getData();
                if (time != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                    String formattedDate = sdf.format(new Date(time));
                    callback.onSuccess(formattedDate);
                } else {
                    callback.onFailure("获取时间失败");
                }
            }
        });
    }


    private Point Locate() throws Exception {

        Map<String, List<FrameData>> mp=new HashMap<>();
        ProcessPcapUtil processPcapUtil=new ProcessPcapUtil();
        LocationUtil locationUtil=new LocationUtil(this,3);
        processPcapUtil.setBW(20);
        for (int i = 0; i < 3; i++) {
            String  filepath = "/sdcard/AP"+i+".pcap";
            RandomAccessFile file=new RandomAccessFile(filepath,"r");
            processPcapUtil.setFilename(file);
            Map<String,List< FrameData >>tmp=processPcapUtil.process(false,true);
            for (Map.Entry<String, List<FrameData>> entry : tmp.entrySet()) {
                String key = entry.getKey();
                List<FrameData> value = entry.getValue();
                // 如果mp中已经存在该key，则合并结果
                if (mp.containsKey(key)) {
                    mp.get(key).addAll(value);
                } else {
                    mp.put(key, value);
                }
            }
        }
        Point p =locationUtil.getLocation(mp,7);
        return p;
    }

    private void exec_train(String[] params) {
        for (int i = 0; i < 3; i++) {
            @SuppressLint("SdCardPath") String filePath = "/sdcard/AP" + i + ".pcap";
            String[] commands = {
                    "ifconfig wlan0 down",
                    "ifconfig wlan0 up",
                    "nexutil -Iwlan0 -s500 -b -l34 -v" + params[i],//"ndABEwGIAgCMU8PCnOvIv0ybpj4AAAAAAAAAAAAAAAAAAA==",
                    "nexutil -Iwlan0 -m1",
                    "tcpdump -i wlan0 -v dst port 5500 -w " +filePath+ " -c 80",
                    "chown " + getApplicationInfo().uid + ":" + getApplicationInfo().uid + " " + filePath,
                    "chmod 777 " + filePath,
                    "echo " + i,
                    "ifconfig wlan0 down",
                    "ifconfig wlan0 up",
            };
            String out = execCommand(commands);
            Log.i(tag, out);
        }
    }

    private static String execCommand(String... commands) {
        StringBuilder output = new StringBuilder();
        Process process;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (String command : commands) {
                os.writeBytes(command + "\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString().trim();
    }

}

