package com.example.finalapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.finalapp.Callback.IClassFragmentCallback;
import com.example.finalapp.Class_Activity;
import com.example.finalapp.R;
import com.example.finalapp.Request.HttpEngine;
import com.example.finalapp.Utils.LinearDecorationUtil;
import com.example.finalapp.adapter.ClassedAdapter;
import com.example.finalapp.database.ClassdbHelper;
import com.example.finalapp.enity.ClassInfo;
import com.example.finalapp.enity.ResList;
import com.example.finalapp.enity.Result;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rx.Observer;


public class ClassesFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    //组件
    private RecyclerView recyclerView;
    private Spinner select_y;
    private Spinner select_x;
    private ImageButton class_down;

    //适配器
    private ClassedAdapter classedAdapter;
    private ArrayAdapter<String>yearAdapter;
    private ArrayAdapter<String>xqAdapter;
    //数据
    private List<ClassInfo> datas;
    private int selected_year;
    private int  selected_xq;
    private String[] years;
    private String [] xqs;
    //接口
    private IClassFragmentCallback ClassesCallback;
    private View rootview;
    private String tag="classesFragment";
    //数据库
    private ClassdbHelper classdbHelper;

    public ClassesFragment() {
        // Required empty public constructor

    }
    public void setClassesCallback(IClassFragmentCallback callback){
        ClassesCallback=callback;
    }

    public IClassFragmentCallback getClassesCallback(){
        Log.i(tag,"设置接口");
        return this.ClassesCallback;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(tag, "onCreate: Fragment created");
    }

    //初始化数据
    private void initdata() {
        //获取数据
        selected_xq=0;
        selected_year=0;
        try {
            datas = ClassesCallback.getMsgFromActivity();
            Log.i(tag, "获取到的数据数量: " + datas.size());
        } catch (Exception e) {
            // 捕获异常，记录日志
            Log.e(tag, "获取数据失败: " + e.getMessage());
            // 提供默认数据或友好的提示
            datas = new ArrayList<>(); // 初始化为空列表
            Toast.makeText(getContext(), "数据加载失败，请稍后重试！", Toast.LENGTH_SHORT).show();
        }
        years=new String[]{
                "2020-2021",
                "2021-2022",
                "2022-2023",
                "2023-2024",
                "2024-2025",
                "2025-2026"
        };
        xqs=new String[]{"1","2"};
        Log.i(tag,datas.size()+" ");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(tag,"执行了");
        if(rootview==null) {
            rootview= inflater.inflate(R.layout.fragment_classes, container, false);
        }
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //数据库

        try{
            classdbHelper = ClassdbHelper.getInstance(getContext());
            classdbHelper.openWriteLink();
            classdbHelper.openReadLink();
        }catch (Exception e){
            Log.i(tag,"加载数据库有问题");
        }
        //绑定
        recyclerView=rootview.findViewById(R.id.classes_recycleview);
        select_y=rootview.findViewById(R.id.select_year);
        select_x=rootview.findViewById(R.id.select_xq);
        class_down=rootview.findViewById(R.id.class_search);

        class_down.setOnClickListener( this);
        initdata();
        //创建适配器
        Log.i(tag,"datas"+datas);
        classedAdapter=new ClassedAdapter(getContext(), datas);
        yearAdapter=new ArrayAdapter<>(getContext(),R.layout.item_common,years);
        xqAdapter=new ArrayAdapter<>(getContext(),R.layout.item_common,xqs);
        //设置适配器
        recyclerView.setAdapter(classedAdapter);
        select_y.setAdapter(yearAdapter);
        select_x.setAdapter(xqAdapter);
        //layoutmanger
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
//        //添加分割线
        recyclerView.addItemDecoration(new LinearDecorationUtil(getContext(),LinearLayoutManager.VERTICAL,20,Color.GRAY));
        //添加点击事件、监听
        select_y.setSelection(selected_year);
        select_x.setSelection(selected_xq);
        select_y.setPrompt("请选择学年");
        select_x.setPrompt("请选择学期");
        select_y.setOnItemSelectedListener(this);
        select_x.setOnItemSelectedListener(this);
        classedAdapter.setOnItemClickListener(new ClassedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ClassInfo data) {
                Log.i(tag,"clicked");
                Intent intent=new Intent(getContext(), Class_Activity.class);
                intent.putExtra("ClassInfo",data);
                startActivity(intent);
            }
        });
        super.onViewCreated(view, savedInstanceState);
        Log.i(tag, "onViewCreated: Fragment view created");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(tag, "onStart: Fragment started");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(tag, "onResume: Fragment resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(tag, "onPause: Fragment paused");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "onStop: Fragment stopped");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 移除所有装饰
        for (int i = 0; i < recyclerView.getItemDecorationCount(); i++) {
            recyclerView.removeItemDecorationAt(0);
        }
        Log.i(tag, "onDestroyView: Fragment view destroyed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag, "onDestroy: Fragment destroyed");
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.select_year) {
            selected_year =i;
        } else if (adapterView.getId() == R.id.select_xq) {
            selected_xq = i;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        if(view.equals(class_down)){
            SharedPreferences sharedPreferences= getContext().getSharedPreferences("permission", Context.MODE_PRIVATE);
            String user=sharedPreferences.getString("user","default");
            if(user.equals("default")){
                Toast.makeText(getContext(),"无法获取用户",Toast.LENGTH_LONG).show();
            }
            Map<String, Object>data=new HashMap<>();
            data.put("StudentId",user);
            data.put("CourseYear",select_y.getSelectedItem().toString());
            data.put("CourseSemester",select_x.getSelectedItem());
            HttpEngine httpEngine=HttpEngine.getInstance(getContext());
            httpEngine.QuerryCourse_m(data, new Observer<Result<List<Object>>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onNext(Result<List<Object>> resListResult) {
                    Log.i(tag,"请求成功");
                    List<Object>data=resListResult.getData();
                    if(data.isEmpty()){
                        Toast.makeText(getContext(),"没有查到相关数据",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        datas.clear();
                        for (int i = 0; i < data.size(); i++) {
                            Map<String,Object>obj= (Map<String, Object>) data.get(i);
                            ClassInfo classInfo=new ClassInfo();
                            classInfo.setClass_name((String) obj.get("coursename"));
                            classInfo.setClass_id((String) obj.get("courseid"));
                            classInfo.setClass_finish((String) obj.get("coursefinish"));
                            classInfo.setClass_teacher((String) obj.get("teacherName"));
                            classInfo.setClass_room((String) obj.get("courseRoom"));
                            classInfo.setClass_time((String) obj.get("coursetime"));
                            classInfo.setClass_value( (Double) obj.get("coursevalue"));
                            classInfo.setAcademy((String) obj.get("academy"));
                            classInfo.setClass_year((String) obj.get("courseyear"));
                            classInfo.setClass_semester(((Double) obj.get("coursesemester")));
                            datas.add(classInfo);
                        }

                        classedAdapter.notifyDataSetChanged();
                        classdbHelper.deleteAll();
                        classdbHelper.insertlist(datas);
                        Log.i(tag,"执行插入课程信息");
                    }
                }
            });
        }
    }
}