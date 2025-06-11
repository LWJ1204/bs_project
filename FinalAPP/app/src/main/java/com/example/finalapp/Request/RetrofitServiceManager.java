package com.example.finalapp.Request;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 10;//连接超时时间
    private static final int DEFAULT_WRITE_TIME = 30;//写超时时间
    private static final int DEFAULT_READ_TIME = 30;//读超时时间lwj20021204.z
    private final OkHttpClient okHttpClient;
//    49.232.26.127
    private static final String REQUEST_PATH = "http://192.168.173.240:8080/api/";
    private final Retrofit retrofit;
    private Context context;
    private static RetrofitServiceManager retrofitServiceManager;
    private RetrofitServiceManager(Context context) {
        this.context= context;
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new mInterceptor(context))
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)//设置读操作超时时间
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(REQUEST_PATH)//设置服务器路径
                .addConverterFactory(GsonConverterFactory.create())//添加转化库，默认是Gson
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加回调库，采用RxJava
                .build();

    }


    /*
     * 获取RetrofitServiceManager
     **/
    public static RetrofitServiceManager getInstance(Context context) {
        if (retrofitServiceManager==null){
            retrofitServiceManager=new RetrofitServiceManager(context);
        }
        return retrofitServiceManager;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
