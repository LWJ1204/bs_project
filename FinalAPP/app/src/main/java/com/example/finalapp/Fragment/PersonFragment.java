package com.example.finalapp.Fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatRadioButton$InspectionCompanion;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.finalapp.R;
import com.example.finalapp.Request.HttpEngine;
import com.example.finalapp.enity.Result;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;


public class PersonFragment extends Fragment implements View.OnClickListener {

    private String tag="Person_tag";

    private View rootview;

    private static final int  Requset_Code_Selce_Imag=1;


    //
    private TextView person_name;
    private TextView person_phone;
    private TextView person_id;
    private TextView person_class;
    private TextView person_zy;
    private TextView person_yx;
    private ImageView person_image;
    private SharedPreferences sp1;
    private SharedPreferences sp2;

    private HttpEngine httpEngine;
    public PersonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        initview();
        httpEngine=HttpEngine.getInstance(getContext());
        sp1=getContext().getSharedPreferences("permission", Context.MODE_PRIVATE);
        sp2= getContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String stuid1=sp1.getString("user","defalut1");
        String stuid2= sp2.getString("user","defalut2");
        if(stuid1.equals(stuid2)){
            initdata();
        }else{
            Map<String,Object> data=new HashMap<>();
            data.put("role",3);
            data.put("StudentId",sp1.getString("user","default"));
            httpEngine.getStuInfo(data, new Observer<Result<Object>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.i(tag,"获取消息错误");
                    Toast.makeText(getContext(),"获取消息失败",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNext(Result<Object> jsonObjectResult) {
                    Object object=jsonObjectResult.getData();
                    Log.i(tag,object.toString());
                    Map<String,Object>obj= (Map<String, Object>) object;
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor= sp2.edit();
                    editor.putString("personname", (String) obj.get("studentname"));
                    editor.putString("personphone", (String) obj.get("studentphone"));
                    editor.putString("personid", (String) obj.get("studentid"));
                    editor.putString("personclass", (String) obj.get("className"));
                    editor.putString("personmajor", (String) obj.get("major"));
                    editor.putString("academy", (String) obj.get("academy"));
                    editor.apply();
                    initdata();
                }
            });
        }
    }

    private void initdata() {
        person_name.setText(sp2.getString("personname","default"));
        person_phone.setText(sp2.getString("personphone","default"));
        person_id.setText(sp2.getString("personid","default"));
        person_class.setText(sp2.getString("personclass","default"));
        person_zy.setText(sp2.getString("personmajor","default"));
        person_yx.setText(sp2.getString("academy","default"));

    }

    private void initview() {
        person_name=rootview.findViewById(R.id.person_name_b);
        person_phone=rootview.findViewById(R.id.person_phone_b);
        person_id=rootview.findViewById(R.id.person_id_b);
        person_class=rootview.findViewById(R.id.person_class_b);
        person_zy=rootview.findViewById(R.id.person_zy_b);
        person_yx=rootview.findViewById(R.id.person_yx_b);
        person_image=rootview.findViewById(R.id.person_photo_b);

        person_image.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        if(rootview==null){
            rootview=inflater.inflate(R.layout.fragment_person, container, false);
        }
        return rootview;
    }


    @Override
    public void onClick(View view) {
        if(view.equals(person_image)){
            Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,Requset_Code_Selce_Imag);

        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode==Requset_Code_Selce_Imag&&requestCode==RESULT_OK&&data!=null){
            Uri selectImageURi=data.getData();
            person_image.setImageURI(selectImageURi);
        }
    }
}