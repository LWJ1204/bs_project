package com.lwj.FinalServer.web.net.vo;

import com.lwj.FinalServer.model.entity.Teacher;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class TeacherVO extends Teacher {
    private String major;
    private String academy;

    public TeacherVO(String teacherid, String teachername, Integer teachersex, String teacherphone, Date teacherbirth, String amid, Integer teachertitle, String major, String academy) {
        super(teacherid, teachername, teachersex, teacherphone, teacherbirth, amid, teachertitle);
        this.major = major;
        this.academy = academy;
    }

    public TeacherVO() {
    }

    public TeacherVO(String major, String academy) {
        this.major = major;
        this.academy = academy;
    }

    public TeacherVO(Teacher teacher, String major, String academy) {
        super(teacher);
        this.major = major;
        this.academy = academy;
    }
}
