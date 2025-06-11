package com.example.finalapp.enity;

import android.os.Parcelable;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MsgInfo implements Comparable<MsgInfo>, Serializable {
    private String courseid;
    private Long recordid;
    private String coursename;
    private String classroom;
    private Long recordsettime;
    private Long recordendtime;
    private Long recordarrtime;
    private Integer readFlag;//0未读 1已读
    private Integer recordstate;//0 已签到 1 未签 2 病假

    public MsgInfo() {
    }


    public MsgInfo(int readFlag, int recordstate, long recordendtime, long recordsettime, String classroom, String coursename, Long recordid, String courseid) {
        this.readFlag = readFlag;
        this.recordstate = recordstate;
        this.recordendtime = recordendtime;
        this.recordsettime = recordsettime;
        this.classroom = classroom;
        this.coursename = coursename;
        this.recordid = recordid;
        this.courseid = courseid;
    }

    public MsgInfo(String courseid,Long recordid, String coursename, String classroom, long recordsettime, long recordendtime, long recordarrtime, int readFlag, int recordstate) {
        this.courseid = courseid;
        this.recordid = recordid;
        this.coursename = coursename;
        this.classroom = classroom;
        this.recordsettime = recordsettime;
        this.recordendtime = recordendtime;
        this.recordarrtime = recordarrtime;
        this.readFlag = readFlag;
        this.recordstate = recordstate;
    }

    public String getCourseid() {
        return courseid;
    }

    @Override
    public String toString() {
        return "MsgInfo{" +
                "courseid='" + courseid + '\'' +
                ", recordid='" + recordid + '\'' +
                ", coursename='" + coursename + '\'' +
                ", classroom='" + classroom + '\'' +
                ", recordsettime=" + recordsettime +
                ", recordendtime=" + recordendtime +
                ", recordarrtime=" + recordarrtime +
                ", readFlag=" + readFlag +
                ", recordstate=" + recordstate +
                '}';
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public Long getRecordid() {
        return recordid;
    }

    public void setRecordid(Long recordid) {
        this.recordid = recordid;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Long getRecordsettime() {
        return recordsettime;
    }

    public void setRecordsettime(long recordsettime) {
        this.recordsettime = recordsettime;
    }

    public Long getRecordendtime() {
        return recordendtime;
    }

    public void setRecordendtime(long recordendtime) {
        this.recordendtime = recordendtime;
    }

    public Long getRecordarrtime() {
        return recordarrtime;
    }

    public void setRecordarrtime(Long recordarrtime) {
        this.recordarrtime = recordarrtime;
    }

    public int getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(int readFlag) {
        this.readFlag = readFlag;
    }

    public int getRecordstate() {
        return recordstate;
    }

    public void setRecordstate(int recordstate) {
        this.recordstate = recordstate;
    }
    public String formattedDate(Long time){
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateTime.format(formatter);
        return formattedDate;
    }
    @Override
    public int compareTo(MsgInfo msgInfo) {
        if (this.readFlag!= msgInfo.getReadFlag()){
            return Integer.compare(msgInfo.getReadFlag(),this.readFlag);
        }
        return Long.compare(this.recordsettime,msgInfo.getRecordsettime());
    }

}
