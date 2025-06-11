package com.lwj.FinalServer.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

/**
 * 
 * @TableName recordtable
 */
@TableName(value ="recordtable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class mRecord extends BaseEntity{
    /**
     * 
     */
    @TableId(value = "RecordId")
    @Schema(description = "签到表")
    private Integer recordid;

    /**
     * 
     */
    @Schema(description = "课程号")
    @TableField(value = "CourseId")
    private String courseid;

    /**
     * 
     */
    @Schema(description = "开始时间")
    @TableField(value = "RecordSetTime")
    private Date recordsettime;

    /**
     * 
     */
    @Schema(description = "结束时间")
    @TableField(value = "RecordEndTime")
    private Date recordendtime;

    @Schema(description = "签到教室")
    @TableField(value = "ClassRoomId")
    private String classroomid;

    public mRecord(mRecord c){
        this.courseid=c.courseid;
        this.recordendtime=c.recordendtime;
        this.recordid=c.recordid;
        this.recordsettime=c.recordsettime;
        this.classroomid=c.getClassroomid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof mRecord mRecord)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getRecordid(), mRecord.getRecordid()) && Objects.equals(getCourseid(), mRecord.getCourseid()) && Objects.equals(getRecordsettime(), mRecord.getRecordsettime()) && Objects.equals(getRecordendtime(), mRecord.getRecordendtime()) && Objects.equals(getClassroomid(), mRecord.getClassroomid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRecordid(), getCourseid(), getRecordsettime(), getRecordendtime(), getClassroomid());
    }

    @Override
    public String toString() {
        return "mRecord{" +
                "recordid=" + recordid +
                ", courseid='" + courseid + '\'' +
                ", recordsettime=" + recordsettime +
                ", recordendtime=" + recordendtime +
                ", classroomid='" + classroomid + '\'' +
                '}';
    }
}