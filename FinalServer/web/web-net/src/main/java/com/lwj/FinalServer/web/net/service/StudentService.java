package com.lwj.FinalServer.web.net.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.Student;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.StudentVO;

public interface StudentService extends IService<Student> {
    public PageBean<StudentVO>getStudentPage(Integer pageNum, Integer PageSize, String keyword);

    PageBean<StudentVO> getMyStudentPage(Integer pageNum, Integer pageSize, String keyword,String teacherid);

    StudentVO getStu(String studentId);
}
