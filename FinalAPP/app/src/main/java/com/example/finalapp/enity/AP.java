package com.example.finalapp.enity;

public class AP {
    public String mac;
    public Point p;
    public AP(String mac, Point p) {
        this.mac = mac;
        this.p = p;
    }

    public AP() {
    }

    @Override
    public String toString() {
        return "AP{" +
                "mac='" + mac + '\'' +
                ", p=" + p +
                '}';
    }
}
