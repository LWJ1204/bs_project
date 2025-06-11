package com.lwj.FinalServer.web.net.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.lwj.FinalServer.model.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
* @author lwj
* @description 针对表【teachertable】的数据库操作Mapper
* @createDate 2025-04-18 00:14:38
* @Entity entity.domain.Teachertable
*/

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

}




