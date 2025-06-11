package com.example.finalapp.Request.Service;

import com.example.finalapp.enity.ClassInfo;
import com.example.finalapp.enity.LoginVo;
import com.example.finalapp.enity.ResList;
import com.example.finalapp.enity.Result;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface ClassService {
    @POST("course/querrycourse")
    Observable<Result<List<Object>>> querryMyCourse(
            @Body Map<String,Object> data
    );
}
