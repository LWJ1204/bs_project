package com.example.finalapp.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finalapp.Callback.IClassFragmentCallback;
import com.example.finalapp.R;
import com.example.finalapp.enity.ClassInfo;


public class ClassMain extends Fragment {


    private TextView setclassroom;
    private TextView setclassteacher;
    private TextView setclassyear;
    private TextView setclasssemester;
    private TextView setclassvalue;
    private TextView setclassid;
    private TextView setclassfinish;
    private TextView setclasstime;

    private ClassInfo data;
    private View rootview;

    private IClassFragmentCallback mcallback;

    public ClassMain() {
        // Required empty public constructor
    }

    public void setIClassFragmentCallback(IClassFragmentCallback callback){
        mcallback=callback;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        initview();
        data=mcallback.getMsgFromActivity().get(0);
        initdate();
    }

    @SuppressLint("SetTextI18n")
    private void initdate() {
       setclassroom.setText(data.getClass_room());
       setclassteacher.setText(data.getClass_teacher());
       setclassyear.setText(data.getClass_year());
       setclasssemester.setText(data.getClass_semester()+"");
       setclassvalue.setText(data.getClass_value()+"");
       setclassid.setText(data.getClass_id());
       setclassfinish.setText(data.getClass_finish());
       setclasstime.setText(data.getClass_time());
    }

    private void initview() {
         setclassroom=rootview.findViewById(R.id.set_class_room);
         setclassteacher=rootview.findViewById(R.id.set_class_teacher);
         setclassyear=rootview.findViewById(R.id.set_class_year);
         setclasssemester=rootview.findViewById(R.id.set_class_semester);
         setclassvalue=rootview.findViewById(R.id.set_class_value);
         setclassid=rootview.findViewById(R.id.set_class_id);
         setclassfinish=rootview.findViewById(R.id.set_class_finish);
         setclasstime=rootview.findViewById(R.id.set_class_time);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       if(rootview==null){
            rootview=inflater.inflate(R.layout.fragment_class_main, container, false);
        }
       return rootview;
    }

}