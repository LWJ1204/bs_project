package com.example.finalapp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalapp.Callback.IClassFragmentCallback;
import com.example.finalapp.Callback.IMsgFragmentCallback;
import com.example.finalapp.Fragment.ClassesFragment;
import com.example.finalapp.Fragment.PersonFragment;
import com.example.finalapp.Fragment.RecordsFragment;
import com.example.finalapp.database.ClassdbHelper;
import com.example.finalapp.database.MsgdbHelper;
import com.example.finalapp.enity.ClassInfo;
import com.example.finalapp.enity.MsgInfo;

import java.util.Collections;
import java.util.List;

public class ALL_Activity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton mclasses_btn;
    private ImageButton mmsgs_btn;
    private ImageButton mperson_btn;
    private TextView mytop;
    private final String tag="all_tag";
    //Fragment
    private ClassesFragment mClassesMain;
    private RecordsFragment mRecordsFragment;
    private PersonFragment mPersonFragment;
    //数据库
    private MsgdbHelper msgdbHelper;
    private ClassdbHelper mhelper;
    //test
    //监听
    private BroadcastReceiver mreceiver;
    private boolean flag=true;
    private IClassFragmentCallback mClassesCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        try
        {
            Intent intent=getIntent();
            if(intent!=null){
                flag=intent.getBooleanExtra("flag",true);
            }
            mhelper = ClassdbHelper.getInstance(ALL_Activity.this);
            mhelper.openReadLink();
            mhelper.openWriteLink();
            msgdbHelper = MsgdbHelper.getInstance(ALL_Activity.this);
            msgdbHelper.openWriteLink();
            msgdbHelper.openReadLink();

        }catch (Exception e){
            Log.i(tag,"数据库出现问题");
            throw  e;
        }



        SharedPreferences sharedPreferences=getSharedPreferences("permission", Context.MODE_PRIVATE);
        String queuename=sharedPreferences.getString("user","default");
        Intent intent=new Intent(this, MyService.class);
        intent.putExtra("queuename",queuename);
        startService(intent);

        initview();
        //注册receiver
        mreceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(tag,"接收到广播消息");
                if(intent!=null){
                    Log.i(tag,"接收到内容");
                    String action=intent.getAction();
                    if(action!=null&&action.equals("RecordCome")){
                        MsgInfo msgInfo= (MsgInfo) intent.getSerializableExtra("msg");
                        if(msgInfo!=null){
                            Log.i(tag,"成功接收到消息："+msgInfo.toString());
                            msgdbHelper.insert(msgInfo);
                            mRecordsFragment.update(msgInfo);
                        }

                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("RecordCome");
        registerReceiver(mreceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        initCallBack();
         if(flag) showClasses();
         else showmsgs();
        Log.i(tag,"ALL_ACtivity start");
    }

    private void initCallBack() {
        mClassesCallback=new IClassFragmentCallback() {
            @Override
            public List<ClassInfo> getMsgFromActivity() {
                return mhelper.queryall();
            }
        };
    }

    private void initview() {
        mclasses_btn=findViewById(R.id.classes_imagebtn);
        mmsgs_btn=findViewById(R.id.message_imagebtn);
        mperson_btn=findViewById(R.id.person_imagebtn);
        mytop=findViewById(R.id.MyTop);

        mclasses_btn.setOnClickListener(this);
        mmsgs_btn.setOnClickListener(this);
        mperson_btn.setOnClickListener(this);

        mRecordsFragment =new RecordsFragment(ALL_Activity.this);
        mClassesMain=new ClassesFragment();
        mPersonFragment=new PersonFragment();
        if(flag)mclasses_btn.setSelected(true);
        else mmsgs_btn.setSelected(true);
    }



    @Override
    public void onClick(View view) {
        setBtnState();
        if (view.equals(mclasses_btn)){
            mclasses_btn.setSelected(true);
            showClasses();
        }else if (view.equals(mmsgs_btn)){
            mmsgs_btn.setSelected(true);
            showmsgs();
        }else{
            mperson_btn.setSelected(true);
            showperson();
        }
    }

    private void showmsgs() {
        mytop.setText("我的消息");
        mRecordsFragment.setCallBack(new IMsgFragmentCallback() {
            @Override
            public List<MsgInfo> getMsgFromActivity() {
                List<MsgInfo> msgs=msgdbHelper.querryall();
                Log.i(tag,"msg 大小"+msgs.size());
                Collections.sort(msgs);
                return msgs;
            }

            @Override
            public void updataReadFlag(MsgInfo data) {
                msgdbHelper.updataReadFlag(data.getCourseid(),data.getRecordid(),data.getReadFlag());
                Log.i(tag,"更新了"+data.toString());
            }
        });
        replaceFragment(mRecordsFragment);
    }

    private void showperson(){
        mytop.setText("个人信息");
        replaceFragment(mPersonFragment);
    }

    private void showClasses(){
        if(mClassesMain.getClassesCallback()==null) mClassesMain.setClassesCallback(mClassesCallback);
        mytop.setText("我的课程");
        replaceFragment(mClassesMain);
    }
    private void setBtnState(){
        mclasses_btn.setSelected(false);
        mperson_btn.setSelected(false);
        mmsgs_btn.setSelected(false);
    }
    public void replaceFragment(Fragment fragment){
        Log.i(tag,"replace");
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.all_frame,fragment);
        transaction.commit();
    }
}