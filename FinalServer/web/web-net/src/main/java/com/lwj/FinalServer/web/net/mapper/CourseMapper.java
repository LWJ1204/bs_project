package com.lwj.FinalServer.web.net.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lwj.FinalServer.model.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
* @author lwj
* @description 针对表【coursetable】的数据库操作Mapper
* @createDate 2025-04-18 00:14:38
* @Entity entity.domain.Coursetable
*/
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

}




