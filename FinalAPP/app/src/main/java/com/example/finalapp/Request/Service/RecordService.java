package com.example.finalapp.Request.Service;

import com.example.finalapp.enity.LoginVo;
import com.example.finalapp.enity.MsgInfo;
import com.example.finalapp.enity.Result;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface RecordService {
    @POST("record/stuarr")
    Observable<Result<String>> send(
            @Body Map<String,Object> data
    );

    @POST("record/getRecord")
    Observable<Result<List<Object>>>get(
        @Body Map<String,Object>data
    );

    @GET("record/getTime")
    Observable<Result<Long>>getTime();

}
