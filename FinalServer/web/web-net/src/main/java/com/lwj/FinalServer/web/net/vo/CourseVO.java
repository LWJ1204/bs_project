package com.lwj.FinalServer.web.net.vo;

import com.lwj.FinalServer.model.entity.Course;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class CourseVO extends Course {
        public String teacherName;
        public Integer CourseSelectNum;
        public String CourseRoom;

    public CourseVO(String courseid, String coursename, String teacherid, String classroomid, Double coursevalue, String coursefinish, Integer coursenum, String coursetime, String courseyear, Integer coursesemester, String academy, String teacherName, Integer courseSelectNum,String courseroom) {
        super(courseid, coursename, teacherid, classroomid, coursevalue, coursefinish, coursenum, coursetime, courseyear, coursesemester, academy);
        this.teacherName = teacherName;
        CourseSelectNum = courseSelectNum;
        this.CourseRoom=courseroom;
    }

    public CourseVO(String teacherName, Integer courseSelectNum,String courseRoom) {
        this.teacherName = teacherName;
        CourseSelectNum = courseSelectNum;
        this.CourseRoom=courseRoom;
    }

    public CourseVO(Course c, String teacherName, Integer courseSelectNum,String courseRoom) {
        super(c);
        this.teacherName = teacherName;
        CourseSelectNum = courseSelectNum;
        this.CourseRoom=courseRoom;
    }
}
