package com.example.finalapp.Request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class mInterceptor implements okhttp3.Interceptor {

    private static final String tag= "request";
    private Context context;
    public mInterceptor(Context context){
        this.context=context;
    }
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Charset UTF8 = Charset.forName("UTF-8");
        SharedPreferences sharedPreferences=context.getSharedPreferences("permission",Context.MODE_PRIVATE);
        String token=sharedPreferences.getString("token","default");
        // 打印请求报文
        Request request = chain.request();
        RequestBody requestBody = request.body();
        String reqBody = null;
        if(requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            reqBody = buffer.readString(charset);
        }
        Log.d(tag, String.format("发送请求\nmethod：%s\nurl：%s\nheaders: %s\nbody：%s",
                request.method(), request.url(), request.headers(), reqBody));

        // 打印返回报文
        // 先执行请求，才能够获取报文
        Request newrequest=request.newBuilder()
                .addHeader("Access-Token",token)
                .build();
        Response response = chain.proceed(newrequest);
        ResponseBody responseBody = response.body();
        String respBody = null;
        if(responseBody != null) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }
            }
            respBody = buffer.clone().readString(charset);
        }
        Log.d(tag, String.format("收到响应\n%s %s\n请求url：%s\n请求body：%s\n响应body：%s",
                response.code(), response.message(), response.request().url(), reqBody, respBody));
        return response;

    }
}
