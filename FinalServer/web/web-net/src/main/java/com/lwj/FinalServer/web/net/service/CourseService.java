package com.lwj.FinalServer.web.net.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.Course;
import com.lwj.FinalServer.web.net.vo.CountData;
import com.lwj.FinalServer.web.net.vo.CourseVO;
import com.lwj.FinalServer.web.net.vo.PageBean;

import java.util.List;

public interface CourseService extends IService<Course> {
    public PageBean<CourseVO>getCoursePage(Integer pageNum, Integer pageSize, String keyword, String teacherId);

    List<CourseVO> getCourseList(Integer role, String id);

    CountData CountData(Integer role, String id);
}
