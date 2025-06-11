package com.lwj.FinalServer.web.net.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.Classes;
import com.lwj.FinalServer.web.net.vo.ClassVO;
import com.lwj.FinalServer.web.net.vo.PageBean;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.Map;

public interface ClassesService extends IService<Classes> {
public PageBean<ClassVO> getClassesPage(Integer PageNum, Integer PageSize, String keyword);
}
