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

/**
 * 
 * @TableName recordstatetable
 */
@TableName(value ="recordstatetable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordState extends BaseEntity{
    /**
     * 
     */
    @TableId(value = "RecordId")
    @Schema(description = "签到表Id")
    private Integer recordid;

    /**
     * 
     */
    @Schema(description = "签到学生")
    @TableField(value = "StudentId")
    private String studentid;

    /**
     * 
     */
    @Schema(description = "考勤状态")
    @TableField(value = "RecordState")
    private Integer recordstate;

    /**
     * 
     */
    @Schema(description = "签到时间")
    @TableField(value = "RecordArrTime")
    private Date recordarrtime;

    @Schema(description = "扰动编码")
    @TableField(value = "Code")
    private String code;

    public RecordState(RecordState r){
        this.recordarrtime=r.getRecordarrtime();
        this.recordstate=r.getRecordstate();
        this.recordid=r.getRecordid();
        this.studentid=r.getStudentid();
        this.code=null;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        RecordState other = (RecordState) that;
        return (this.getRecordid() == null ? other.getRecordid() == null : this.getRecordid().equals(other.getRecordid()))
            && (this.getStudentid() == null ? other.getStudentid() == null : this.getStudentid().equals(other.getStudentid()))
            && (this.getRecordstate() == null ? other.getRecordstate() == null : this.getRecordstate().equals(other.getRecordstate()))
            && (this.getRecordarrtime() == null ? other.getRecordarrtime() == null : this.getRecordarrtime().equals(other.getRecordarrtime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRecordid() == null) ? 0 : getRecordid().hashCode());
        result = prime * result + ((getStudentid() == null) ? 0 : getStudentid().hashCode());
        result = prime * result + ((getRecordstate() == null) ? 0 : getRecordstate().hashCode());
        result = prime * result + ((getRecordarrtime() == null) ? 0 : getRecordarrtime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", recordid=").append(recordid);
        sb.append(", studentid=").append(studentid);
        sb.append(", recordstate=").append(recordstate);
        sb.append(", recordarrtime=").append(recordarrtime);
        sb.append("]");
        return sb.toString();
    }
}