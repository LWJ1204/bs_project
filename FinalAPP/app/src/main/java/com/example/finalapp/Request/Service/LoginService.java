package com.example.finalapp.Request.Service;


import com.example.finalapp.enity.LoginVo;
import com.example.finalapp.enity.Result;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface LoginService {
    @POST("permission/login")
    Observable<Result<LoginVo>> login(
      @Body Map<String,Object> data
    );
    @POST("permission/register")
    Observable<Result<String>> register(
            @Body Map<String,Object> data
    );
}
