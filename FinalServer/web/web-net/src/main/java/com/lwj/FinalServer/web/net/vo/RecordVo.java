package com.lwj.FinalServer.web.net.vo;

import com.lwj.FinalServer.model.entity.mRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@EqualsAndHashCode(callSuper = true)
@Getter
@Data
public class RecordVo extends mRecord {
    private String coursename;
    private String classroom;
    private long recordarrtime;
    private int readFlag;
    private int recordstate;

    public RecordVo(Integer recordid, String courseid, Date recordsettime, Date recordendtime, String classroomid, String coursename, String classroom, int readFlag, int recordstate) {
        super(recordid, courseid, recordsettime, recordendtime, classroomid);
        this.coursename = coursename;
        this.classroom = classroom;
        this.readFlag = readFlag;
        this.recordstate = recordstate;
    }

    public RecordVo(String coursename, String classroom, int readFlag, int recordstate) {
        this.coursename = coursename;
        this.classroom = classroom;
        this.readFlag = readFlag;
        this.recordstate = recordstate;
    }

    public RecordVo(mRecord c, String coursename, String classroom, int readFlag, int recordstate) {
        super(c);
        this.coursename = coursename;
        this.classroom = classroom;
        this.readFlag = readFlag;
        this.recordstate = recordstate;
    }

}
