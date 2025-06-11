package com.example.finalapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.finalapp.R;
import com.example.finalapp.Request.HttpEngine;
import com.example.finalapp.adapter.ClassRecordAdapter;
import com.example.finalapp.adapter.RecordAdapter;
import com.example.finalapp.database.MsgdbHelper;
import com.example.finalapp.enity.ClassInfo;
import com.example.finalapp.enity.MsgInfo;
import com.example.finalapp.enity.Result;


import org.json.JSONObject;

import java.nio.channels.Channel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.SimpleFormatter;

import rx.Observer;

public class ClassRecordFragment extends Fragment {
    private View rootview=null;

    private RecyclerView recyclerView;
    private ClassRecordAdapter recordAdapter;
    private final String tag="classrecordtag";
    private List<MsgInfo> datas;
    private MsgdbHelper msgdbHelper;
    private HttpEngine httpEngine;
    private ClassInfo classInfo;
    private Context context;
    public ClassRecordFragment(Context context, ClassInfo classInfo) {

        msgdbHelper=MsgdbHelper.getInstance(context);
        msgdbHelper.openReadLink();
        msgdbHelper.openWriteLink();
        // Required empty public constructor
        datas=new ArrayList<>();
        this.classInfo=classInfo;
        this.context=context;
        httpEngine=HttpEngine.getInstance(context);
        recordAdapter=new ClassRecordAdapter(context,classInfo,datas);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        this.recyclerView=rootview.findViewById(R.id.class_record);
        //配置适配器
        this.recyclerView.setAdapter(recordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        try{
            Map<String,Object>mp=new HashMap<>();
            SharedPreferences sharedPreferences=this.context.getSharedPreferences("permission",Context.MODE_PRIVATE);
            mp.put("StudentId",sharedPreferences.getString("user","default"));
            mp.put("CourseId",classInfo.getClass_id());
            httpEngine.getRecord(mp, new Observer<Result<List<Object>>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(tag,"出现错误："+e.toString());
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onNext(Result<List<Object>> listResult) {
                    List<Object> datas = listResult.getData();
                    Log.i(tag,"获取的数据："+datas.get(0).toString());
                    List<MsgInfo>list=new ArrayList<>();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                    for(Object data:datas){
                        Map<String,Object>mp= (Map<String, Object>) data;
                       Double recordidD= (Double) mp.get("recordid");
                        assert recordidD != null;
                        Long recordid=recordidD.longValue();
                       String settime= (String) mp.get("recordSetTime");
                       String courseid= (String) mp.get("courseId");
                       Double stated= (Double) mp.get("recordstate");
                        assert stated != null;
                        Integer state=stated.intValue();
                       String arrtime= (String) mp.get("recordarrtime");
                       String coursename= (String) mp.get("courseName");
                       MsgInfo msg=new MsgInfo();

                        try {
                            Long recordarrtime =null,recordsettime=null;
                            if(settime!=null)
                            {
                                recordsettime= Objects.requireNonNull(sdf.parse(settime)).getTime();
                            }
                            if(arrtime!=null){
                                recordarrtime=sdf.parse(arrtime).getTime();
                            }
                            msg.setRecordid(recordid);
                            msg.setCourseid(courseid);
                            msg.setRecordstate(state);
                            msg.setRecordarrtime(recordarrtime);
                            msg.setCoursename(coursename);
                            msg.setRecordsettime(recordsettime);
                            list.add(msg);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    msgdbHelper.updatelist(list);
                    list.sort(new Comparator<MsgInfo>() {
                        @Override
                        public int compare(MsgInfo t1, MsgInfo t2) {
                            return t1.getRecordsettime().compareTo(t2.getRecordsettime());
                        }
                    });
                    recordAdapter.setDate(list);
                    recordAdapter.notifyDataSetChanged();
                }
            });
        }catch (Exception e)
        {
            datas=msgdbHelper.querryall();
            Log.i(tag,"获取record出现错误："+e.toString());
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void changdate(List<MsgInfo>datas){
        this.datas=datas;
        recordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      if(rootview==null){
          rootview=inflater.inflate(R.layout.fragment_class_record, container, false);
      }
      return rootview;
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
}