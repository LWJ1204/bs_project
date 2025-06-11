package com.lwj.FinalServer.web.net.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.mUser;
import com.lwj.FinalServer.web.net.vo.CaptchaVo;
import com.lwj.FinalServer.web.net.vo.LoginVo;

import java.util.Map;

public interface PermissionService extends IService<mUser> {
    CaptchaVo getCaptcha();

    LoginVo login(Map data, String ip);

    String register(Integer role, String username, String password);
}
