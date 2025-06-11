package com.example.finalapp.Request.Service;

import com.example.finalapp.enity.Result;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface PersonService {
    @POST("student/getstuinfo")
    Observable<Result<Object>> get(
            @Body Map<String,Object> data
    );
}
