package com.lwj.FinalServer.web.net.vo;

import com.lwj.FinalServer.model.entity.Classes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ClassVO extends Classes {
    private String TeacherName;
    private String TeacherPhone;
    private String MonitorName;
    private String MonitorPhone;
    private String Academy;
    private String Major;


    public ClassVO(String teacherName, String teacherPhone, String monitorName, String monitorPhone, String academy, String major) {
        TeacherName = teacherName;
        TeacherPhone = teacherPhone;
        MonitorName = monitorName;
        MonitorPhone = monitorPhone;
        Academy = academy;
        Major = major;
    }

    public ClassVO(Classes t, String teacherName, String teacherPhone, String monitorName, String monitorPhone, String academy, String major) {
        super(t);
        TeacherName = teacherName;
        TeacherPhone = teacherPhone;
        MonitorName = monitorName;
        MonitorPhone = monitorPhone;
        Academy = academy;
        Major = major;
    }
}
