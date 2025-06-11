package com.lwj.FinalServer.web.net.vo;

import com.lwj.FinalServer.model.Enum.DataDesensitizationTypeEnum;
import com.lwj.FinalServer.model.entity.RecordState;
import com.lwj.FinalServer.model.entity.mRecord;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class RecordStateVO extends RecordState {
    private String CourseName;
    private String CourseRoom;
    private String TeacherName;
    private String StudentName;
    private String studentid;
    private String CourseId;
    private Date RecordSetTime;
    private  String astudentid;


    public RecordStateVO(String courseName, String courseRoom, String teacherName, String studentName, String courseId, Date recordSetTime) {
        CourseName = courseName;
        CourseRoom = courseRoom;
        TeacherName = teacherName;
        StudentName = studentName;
        CourseId = courseId;
        RecordSetTime = recordSetTime;
    }

    public RecordStateVO(RecordState r, String courseName, String courseRoom, String teacherName, String studentName, String courseId, Date recordSetTime) {
        super(r);
        this.studentid=r.getStudentid();
        CourseName = courseName;
        CourseRoom = courseRoom;
        TeacherName = teacherName;
        StudentName = studentName;
        CourseId = courseId;
        RecordSetTime = recordSetTime;
        astudentid=studentid;
    }
}
