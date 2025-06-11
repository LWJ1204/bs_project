package com.lwj.FinalServer.web.net.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.mUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface mUserMapper extends BaseMapper<mUser> {
}
