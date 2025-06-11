package com.example.finalapp.enity;

public class LoginVo {
    private String token;
    private String state;

    public LoginVo(String token, String state) {
        this.token = token;
        this.state = state;
    }

    public LoginVo() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "token='" + token + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
