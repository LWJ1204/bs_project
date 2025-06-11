package com.example.finalapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.example.finalapp.Callback.IClassFragmentCallback;
import com.example.finalapp.Fragment.ClassMain;
import com.example.finalapp.Fragment.ClassRecordFragment;
import com.example.finalapp.adapter.ClassPageFragmentAdapter;
import com.example.finalapp.enity.ClassInfo;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Class_Activity extends AppCompatActivity implements View.OnClickListener {
    //top

    private TextView classinfo_name;
    //组件
    private ViewPager mvp;
    private TabLayout mtab;
    //适配器
    private ClassPageFragmentAdapter mCPFAdapter;
    //数据
    private List<Fragment> flist;
    private List<String> titles;

    private final String tag="ClassInfo_tag";
    private ClassInfo data;

    //Fragment
    private ClassMain classMain;
    private ClassRecordFragment reFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        //获取传递数据
        data= (ClassInfo) getIntent().getSerializableExtra("ClassInfo");
        initview();
        initdata();

        //viewPager适配器
        mCPFAdapter=new ClassPageFragmentAdapter(getSupportFragmentManager(),flist,titles);
        mvp.setAdapter(mCPFAdapter);
        mtab.setupWithViewPager(mvp);
    }

    private void initdata() {

        flist=new ArrayList<>();
        titles=new ArrayList<>();
        titles.add("课程介绍");
        titles.add("签到记录");

        //创建fragment
        classMain=new ClassMain();
        reFragment=new ClassRecordFragment(Class_Activity.this,data);

        classMain.setIClassFragmentCallback(new IClassFragmentCallback() {
            @Override
            public List<ClassInfo> getMsgFromActivity() {
                List<ClassInfo>datas=new ArrayList<>();
                datas.add(data);
                return datas;
            }
        });



        flist.add(classMain);
        flist.add(reFragment);
        classinfo_name.setText(data.getClass_name());

    }

    private void initview() {
        //绑定组间
       // back_btn=findViewById(R.id.classinfo_back);
        classinfo_name=findViewById(R.id.classinfo_name);
        mtab=findViewById(R.id.classinfo_select);
        mvp=findViewById(R.id.class_vp);

        //绑定点击事件
        //back_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

    }
}