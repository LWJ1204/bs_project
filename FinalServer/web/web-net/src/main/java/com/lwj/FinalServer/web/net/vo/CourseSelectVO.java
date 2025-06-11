package com.lwj.FinalServer.web.net.vo;

import com.lwj.FinalServer.model.entity.CourseSelect;
import lombok.Data;

@Data
public class CourseSelectVO extends CourseSelect {
    private String CourseName;
    private String CourseRoom;
    private String TeacherName;
    private String StudentName;
    private String Academy;

    public CourseSelectVO(String courseid, String studentid, Integer coursestate, String courseName, String courseRoom, String teacherName, String studentName, String academy) {
        super(courseid, studentid, coursestate);
        CourseName = courseName;
        CourseRoom = courseRoom;
        TeacherName = teacherName;
        StudentName = studentName;
        Academy = academy;
    }

    public CourseSelectVO(String courseName, String courseRoom, String teacherName, String studentName, String academy) {
        CourseName = courseName;
        CourseRoom = courseRoom;
        TeacherName = teacherName;
        StudentName = studentName;
        Academy = academy;
    }

    public CourseSelectVO(CourseSelect cs, String courseName, String courseRoom, String teacherName, String studentName, String academy) {
        super(cs);
        CourseName = courseName;
        CourseRoom = courseRoom;
        TeacherName = teacherName;
        StudentName = studentName;
        Academy = academy;
    }
}
