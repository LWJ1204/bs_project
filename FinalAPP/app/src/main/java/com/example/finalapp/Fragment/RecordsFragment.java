package com.example.finalapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.finalapp.Callback.IMsgFragmentCallback;
import com.example.finalapp.LocationActivity;
import com.example.finalapp.R;
import com.example.finalapp.Request.HttpEngine;
import com.example.finalapp.Utils.LinearDecorationUtil;
import com.example.finalapp.adapter.RecordAdapter;
import com.example.finalapp.database.MsgdbHelper;
import com.example.finalapp.enity.MsgInfo;
import com.example.finalapp.enity.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observer;

/**
 */
public class RecordsFragment extends Fragment {
    private View rootview;
    private RecyclerView recyclerView;
    //record适配器
    private RecordAdapter recordAdapter;

    private final  String tag="record_tag";
    //接口
    private IMsgFragmentCallback mcallback;
    //数据
    private List<MsgInfo> datas;
    private MsgdbHelper msgdbHelper;
    //网络请求
    private HttpEngine httpEngine;
    private Long time;//当前时间

    private Comparator<MsgInfo>msgcomparetor;

    public RecordsFragment(Context context) {
        // Required empty public constructor
        datas=new ArrayList<>();
        httpEngine=HttpEngine.getInstance(context);
        recordAdapter=new RecordAdapter(context,datas);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        this.recyclerView=rootview.findViewById(R.id.record_recycleview);
       //连接数据库
        msgdbHelper=MsgdbHelper.getInstance(getContext());
        msgdbHelper.openReadLink();
        msgdbHelper.openWriteLink();

        //比较器
        msgcomparetor=new Comparator<MsgInfo>() {
            @Override
            public int compare(MsgInfo t1, MsgInfo t2) {
                int readFlagComparison = Integer.compare(t1.getReadFlag(), t2.getReadFlag());
                if (readFlagComparison != 0) {
                    return readFlagComparison;
                }

                // 如果读取状态相同，则按 settime 排序（升序）
                return t1.getRecordsettime().compareTo(t2.getRecordsettime());
            }
        };
        initdata();
        //适配器

        //设置适配器
        recyclerView.setAdapter(recordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        //添加分割线
        recyclerView.addItemDecoration(new LinearDecorationUtil(getContext(),LinearLayoutManager.VERTICAL,20, Color.GRAY));
        //设置点击事件
        recordAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(View view, MsgInfo data) throws InterruptedException {

                    if (data.getReadFlag() == 0) {
                        gettime(new TimeCallback() {
                            @Override
                            public void onSuccess(Long time) {
                                if (time == null) {
                                    Toast.makeText(getContext(), "获取时间失败", Toast.LENGTH_LONG).show();
                                } else {
                                    data.setReadFlag(1);
                                    mcallback.updataReadFlag(data);
                                    Collections.sort(datas,msgcomparetor);
                                    recordAdapter.notifyDataSetChanged();

                                    Log.i(tag, "当前时间为：" + time);
                                    if (data.getRecordsettime() < time && time < data.getRecordendtime()) {
                                        Intent intent = new Intent(getContext(), LocationActivity.class);
                                        intent.putExtra("MsgInfo", data);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getContext(), "时间已过期", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                Toast.makeText(getContext(), "获取时间失败", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "已完成", Toast.LENGTH_SHORT).show();
                    }
                }


        });
    }
    private interface TimeCallback {
        void onSuccess(Long time);
        void onFailure(Throwable e);
    }

    private void gettime(final TimeCallback callback) {
        httpEngine.getTime(new Observer<Result<Long>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag, "获取时间失败");
                callback.onFailure(e);
            }

            @Override
            public void onNext(Result<Long> longResult) {
                Long time = longResult.getData();
                callback.onSuccess(time);
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void setCallBack(IMsgFragmentCallback callback){
        this.mcallback=callback;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initdata(){
        datas=mcallback.getMsgFromActivity();
        Collections.sort(datas, msgcomparetor);
        recordAdapter.setDates(datas);
        Log.i(tag,"datas size"+datas.size());
        recordAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootview==null)
        {
            rootview=inflater.inflate(R.layout.fragment_records, container, false);
        }
        return rootview;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
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

    @SuppressLint("NotifyDataSetChanged")
    public void update(MsgInfo msgInfo){
        datas.add(msgInfo);
        Collections.sort(datas,msgcomparetor);
        recordAdapter.setDates(datas);
        recordAdapter.notifyDataSetChanged();
    }

}