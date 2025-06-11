package com.lwj.FinalServer.web.net.vo;

import com.lwj.FinalServer.model.entity.Student;
import com.lwj.FinalServer.model.entity.Teacher;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Data
@Getter
@Setter
public class StudentVO extends Student {
    private String major;
    private String academy;

    private String className;

    private String Amid;
    private String courseid;
    private String coursename;
    public StudentVO(String studentid, String studentname, Integer studentsex, Date studentbirth, String studentphone, String classid, String major, String academy, String className, String amid,String courseid,String coursename) {
        super(studentid, studentname, studentsex, studentbirth, studentphone, classid);
        this.major = major;
        this.academy = academy;
        this.className = className;
        Amid = amid;
        this.courseid=courseid;
        this.coursename=coursename;
    }

    public StudentVO(String major, String academy, String className, String amid,String courseid,String coursename) {
        this.major = major;
        this.academy = academy;
        this.className = className;
        Amid = amid;
        this.courseid=courseid;
        this.coursename=coursename;
    }

    public StudentVO(Student student, String major, String academy, String className, String amid,String courseid,String coursename) {
        super(student);
        this.major = major;
        this.academy = academy;
        this.className = className;
        Amid = amid;
        this.courseid=courseid;
        this.coursename=coursename;
    }
}
