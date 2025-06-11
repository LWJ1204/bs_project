package com.example.finalapp.Callback;

public interface TimeCallback {
    void onSuccess(String formattedTime);
    void onFailure(String errorMessage);
}