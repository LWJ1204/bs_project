package com.lwj.FinalServer.web.net.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.Teacher;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.TeacherVO;

import java.util.List;

public interface TeacherService extends IService<Teacher> {
    public PageBean<TeacherVO> getTeacherPage(Integer pageNum, Integer PageSize, String keyword);
    public List<Teacher>querryByAcademy(String academy);
}
