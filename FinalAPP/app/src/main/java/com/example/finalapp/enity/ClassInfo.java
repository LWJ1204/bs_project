package com.example.finalapp.enity;

import java.io.Serializable;

public class ClassInfo implements Serializable {
    private String class_name;//课程名称
    private String class_id;//课程号
    private String class_room;//教室
    private String class_teacher;//教师
    private double class_value;//学分
    private String class_finish;//结课形式
    private String class_time;//课程时间
    private String class_year;//开课学年
    private Integer class_semester;//开课学期
    private String academy;

    public ClassInfo() {
    }

    public ClassInfo(String class_name, String class_id, String class_room, String class_teacher, double class_value, String class_finish, String class_time, String class_year, Integer class_semester, String academy) {
        this.class_name = class_name;
        this.class_id = class_id;
        this.class_room = class_room;
        this.class_teacher = class_teacher;
        this.class_value = class_value;
        this.class_finish = class_finish;
        this.class_time = class_time;
        this.class_year = class_year;
        this.class_semester = class_semester;
        this.academy = academy;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getClass_room() {
        return class_room;
    }

    public void setClass_room(String class_room) {
        this.class_room = class_room;
    }

    public String getClass_teacher() {
        return class_teacher;
    }

    public void setClass_teacher(String class_teacher) {
        this.class_teacher = class_teacher;
    }

    public double getClass_value() {
        return class_value;
    }

    public void setClass_value(double class_value) {
        this.class_value = class_value;
    }

    public String getClass_finish() {
        return class_finish;
    }

    public void setClass_finish(String class_finish) {
        this.class_finish = class_finish;
    }

    public String getClass_time() {
        return class_time;
    }

    public void setClass_time(String class_time) {
        this.class_time = class_time;
    }

    public String getClass_year() {
        return class_year;
    }

    public void setClass_year(String class_year) {
        this.class_year = class_year;
    }

    public Integer getClass_semester() {
        return class_semester;
    }

    public void setClass_semester(Double class_semester) {
        this.class_semester = (int) Math.round(class_semester);
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "class_name='" + class_name + '\'' +
                ", class_id='" + class_id + '\'' +
                ", class_room='" + class_room + '\'' +
                ", class_teacher='" + class_teacher + '\'' +
                ", class_value=" + class_value +
                ", class_finish='" + class_finish + '\'' +
                ", class_time='" + class_time + '\'' +
                ", class_year='" + class_year + '\'' +
                ", class_semester=" + class_semester +
                ", academy='" + academy + '\'' +
                '}';
    }
}
