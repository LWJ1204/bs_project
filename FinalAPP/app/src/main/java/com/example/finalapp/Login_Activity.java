package com.example.finalapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finalapp.Request.HttpEngine;
import com.example.finalapp.Utils.GetDeviceUtil;
import com.example.finalapp.Utils.WiFiUtils;
import com.example.finalapp.Utils.YzmCodeGen;
import com.example.finalapp.database.ClassdbHelper;
import com.example.finalapp.database.MsgdbHelper;
import com.example.finalapp.enity.ClassInfo;
import com.example.finalapp.enity.LoginVo;
import com.example.finalapp.enity.MsgInfo;
import com.example.finalapp.enity.Result;

import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import rx.Observer;


public class Login_Activity extends AppCompatActivity implements View.OnClickListener {


    private Button login_button,register_button;
    private ImageButton yzm_btn;
    private EditText user_input,password_input,yzm_input;
    private String tag="login_tag";
//    验证码
    private YzmCodeGen yzmgen;
    private Bitmap yzm;
    private String code;
    private HttpEngine httpEngine;
    private SharedPreferences sharedPreferences;
    private WiFiUtils wifiUtils;
    private MsgdbHelper msgdbhelper;
    private ClassdbHelper cdbhhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         initview();
         showyzm();
         Intent intent=getIntent();
         if(intent!=null){
            String username=intent.getStringExtra("UserName");
            user_input.setText(username);
         }

         msgdbhelper=MsgdbHelper.getInstance(this);
         msgdbhelper.openWriteLink();
         msgdbhelper.openReadLink();
         cdbhhelper=ClassdbHelper.getInstance(this);
         cdbhhelper.openReadLink();
         cdbhhelper.openWriteLink();
         
//         ConectWiFi();
    }

    private void initview() {
        login_button=findViewById(R.id.Login_Button);
        register_button=findViewById(R.id.Register_Button);
        yzm_btn =findViewById(R.id.l_yzm_btn);

        user_input=findViewById(R.id.Input_Name);
        password_input=findViewById(R.id.Input_Password);
        yzm_input=findViewById(R.id.Input_YZM);

        login_button.setOnClickListener(this);
        register_button.setOnClickListener(this);
        yzm_btn.setOnClickListener(this);
        yzmgen=YzmCodeGen.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(login_button)){
            Log.i(tag,"login is clicked");
            login_method();
        }else if(view.equals(register_button)){
            Log.i(tag,"register is clicked");
            register_method();
        }else if(view.equals(yzm_btn)){
            Log.i(tag,"yzm_btn is clicked");
           showyzm();
        }
    }

    private void register_method() {
        Intent intent=new Intent(this,Register_Activity.class);
        startActivity(intent);
    }

    private void login_method() {
        String user=user_input.getText().toString().trim();
        String pwd=password_input.getText().toString().trim();
        String yzmcode=yzm_input.getText().toString().trim();
        sharedPreferences=this.getSharedPreferences("permission",MODE_PRIVATE);
        if(code.equalsIgnoreCase(yzmcode)){
            Log.i(tag,"开始登录");
            Map<String, Object> map=new HashMap<>();
            map.put("username",user);
            map.put("password",pwd);
            map.put("role",3);
            map.put("fingerprint", GetDeviceUtil.getEncodedDeviceFingerprint(this));
            httpEngine=HttpEngine.getInstance(Login_Activity.this);
            httpEngine.Login_m(map, new Observer<Result<LoginVo>>() {

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Result<LoginVo> loginVoResult) {
                    LoginVo loginVo=loginVoResult.getData();
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    if(!user.equals(sharedPreferences.getString("user","default")))
                    {
                            msgdbhelper.deleteAll();
                            cdbhhelper.deleteAll();
                    }
                    editor.putString("user",user);
                    editor.putString("token",loginVo.getToken());
                    editor.apply();
                    Log.i(tag,"testshaare:"+sharedPreferences.getString("token","default"));
                    Log.i(tag,"get LoginVo:"+loginVo.toString());
                    if(loginVo.getState().equals("成功登录")){
                        Intent intent=new Intent(Login_Activity.this,ALL_Activity.class);
                        startActivity(intent);
                    }

                }
            });
        }else{
            Toast.makeText(this,"验证码输入错误",Toast.LENGTH_SHORT).show();
        }
    }

    private void showyzm(){
        //生成验证码图像
        yzm=yzmgen.CreateCode();
        yzm_btn.setImageBitmap(yzm);
        code=yzmgen.getCode();
    }
}