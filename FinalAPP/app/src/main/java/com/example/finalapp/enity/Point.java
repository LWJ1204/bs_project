package com.example.finalapp.enity;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Point implements Serializable {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double getdis(Point p){
        double disx=x-p.x;
        double disy=y-p.y;
        return Math.sqrt(disx*disx+disy*disy);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
