package com.example.finalapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalapp.Request.HttpEngine;
import com.example.finalapp.Utils.YzmCodeGen;
import com.example.finalapp.enity.Result;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;


public class Register_Activity extends AppCompatActivity implements View.OnClickListener {
    private String tag="register_tag";
    private ImageView yzm_r;
    private YzmCodeGen yzmgen;
    private Bitmap yzm;
    private String code;
    private Button registerbtn;
    private TextView username;
    private TextView password;
    private TextView apassword;
    private TextView Code;
    private HttpEngine httpEngine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initview();
        yzmgen=YzmCodeGen.getInstance();
        showyzm();
        httpEngine=HttpEngine.getInstance(this);
    }

    private void showyzm() {
        //生成验证码图像
        yzm=yzmgen.CreateCode();
        yzm_r.setImageBitmap(yzm);
        code=yzmgen.getCode();
    }

    private void initview(){
        yzm_r=findViewById(R.id.r_yzm_btn);
        yzm_r.setOnClickListener(this);
        registerbtn=findViewById(R.id.r_btn);
        registerbtn.setOnClickListener(this);
        username=findViewById(R.id.r_id_input);
        password=findViewById(R.id.r_pwd_input);
        apassword=findViewById(R.id.r_pwd_again_input);
        Code=findViewById(R.id.r_yzm_input);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.r_yzm_btn){
            showyzm();
        }
        else if(view.getId()==R.id.r_btn){
            Log.i(tag,"开始登录");
            String userinput= username.getText().toString().trim();
            String pwd= password.getText().toString().trim();
            String apwd= apassword.getText().toString().trim();
            String inputcode=Code.getText().toString().trim();
            if(!userinput.isEmpty()&&!pwd.isEmpty()&&!apwd.isEmpty()&&!inputcode.isEmpty()){
                if(pwd.equals(apwd)){
                    Map<String,Object> data=new HashMap<>();
                    data.put("role",3);
                    data.put("username",userinput);
                    data.put("password",pwd);
                    httpEngine.register(data, new Observer<Result<String>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(Register_Activity.this,"注册出错："+e.toString(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(Result<String> stringResult) {
                            String data=stringResult.getData();
                            Toast.makeText(Register_Activity.this,data,Toast.LENGTH_LONG).show();
                            if(data.equals("注册成功")){
                                Intent intent=new Intent(Register_Activity.this,Login_Activity.class);
                                intent.putExtra("UserName",userinput);
                                startActivity(intent);
                            }
                        }
                    });
                }else{
                    Toast.makeText(this,"两次密码输入不一致",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"请输入完整内容",Toast.LENGTH_LONG).show();
            }
        }
    }
}