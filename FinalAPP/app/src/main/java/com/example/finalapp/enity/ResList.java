package com.example.finalapp.enity;

import java.util.List;

public class ResList<T> {
    List<T> datalist;

    public ResList() {
    }

    public ResList(List<T> datalist) {
        this.datalist = datalist;
    }

    public List<T> getDatalist() {
        return datalist;
    }

    public void setDatalist(List<T> datalist) {
        this.datalist = datalist;
    }
}
