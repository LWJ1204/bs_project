package com.lwj.FinalServer.web.net.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.CourseSelect;
import com.lwj.FinalServer.web.net.vo.CourseSelectVO;
import com.lwj.FinalServer.web.net.vo.CourseVO;
import com.lwj.FinalServer.web.net.vo.PageBean;

import java.util.List;

public interface CourseSelectService extends IService<CourseSelect> {
    public PageBean<CourseSelectVO> getCourseSelectPage(Integer pageNum,Integer pageSize,String keyword);

    List<CourseVO> getCourseList(String stuid, Integer courseSemester, String courseYear);
}
