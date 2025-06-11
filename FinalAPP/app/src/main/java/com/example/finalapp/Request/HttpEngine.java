package com.example.finalapp.Request;


import android.content.Context;
import android.util.Log;

import com.example.finalapp.Request.Service.ClassService;
import com.example.finalapp.Request.Service.LoginService;
import com.example.finalapp.Request.Service.PersonService;
import com.example.finalapp.Request.Service.RecordService;
import com.example.finalapp.enity.ClassInfo;
import com.example.finalapp.enity.LoginVo;
import com.example.finalapp.enity.MsgInfo;
import com.example.finalapp.enity.ResList;
import com.example.finalapp.enity.Result;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import pabeles.concurrency.ConcurrencyOps;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HttpEngine {

    private RetrofitServiceManager retrofitServiceManager;
    private static HttpEngine httpEngine=null;
    private final LoginService loginService;
    private final ClassService classService;
    private final RecordService recordService;
    private final PersonService personService;
    //初始化
    private HttpEngine (Context context){
        retrofitServiceManager=RetrofitServiceManager.getInstance(context);
        loginService=retrofitServiceManager.create(LoginService.class);
        classService=retrofitServiceManager.create(ClassService.class);
        recordService=retrofitServiceManager.create(RecordService.class);
        personService=retrofitServiceManager.create(PersonService.class);
    }
    public static HttpEngine getInstance(Context context){
        if(httpEngine==null){
            httpEngine=new HttpEngine(context);
        }
        return httpEngine;
    }



    public  void Login_m(Map<String,Object> data, Observer<Result<LoginVo>>observer){
        setSubscribe(loginService.login(data),observer);
    }

    public  void QuerryCourse_m(Map<String,Object>data, Observer<Result<List<Object>>>observer){
        setSubscribe(classService.querryMyCourse(data),observer);
    }

    public void SendRecord(Map<String,Object>data,Observer<Result<String>>observer){
        setSubscribe(recordService.send(data),observer);
    }

    public void getStuInfo(Map<String,Object>data, Observer<Result<Object>>observer){
        setSubscribe(personService.get(data),observer);
    }
    public void getRecord(Map<String,Object>data, Observer<Result<List<Object>>>observer){
        setSubscribe(recordService.get(data),observer);
    }
    public void getTime(Observer<Result<Long>> observer){
        setSubscribe(recordService.getTime(),observer);
    }

    public void register(Map<String,Object>data,Observer<Result<String>>observer){
        setSubscribe(loginService.register(data),observer);
    }

    private static  <T> void setSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(observer);
    }


}
