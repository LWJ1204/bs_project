package com.lwj.FinalServer.web.net.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.model.entity.mUser;
import com.lwj.FinalServer.web.net.service.PermissionService;
import com.lwj.FinalServer.web.net.vo.CaptchaVo;
import com.lwj.FinalServer.web.net.vo.LoginVo;
import com.lwj.FinalServer.web.net.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name="登录注册管理")
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService service;
    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result<CaptchaVo>getCaptcha(){
        CaptchaVo captcha=service.getCaptcha();
        return Result.ok(captcha);
    }
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVo>login(@RequestBody Map data, HttpServletRequest request){
        String ip= (String) request.getAttribute("clientIp");
        LoginVo res=service.login(data,ip);
        return Result.ok(res);
    }
    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<String>register(@RequestBody Map data){
        Integer role= (Integer) data.get("role");
        String username= (String) data.get("username");
        String password= (String) data.get("password");
        String ans=service.register(role,username,password);
        return Result.ok(ans);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/changepassword")
    public Result<String>ChangePassword(@RequestBody Map data){
        Integer role= (Integer) data.get("role");
        String username= (String) data.get("username");
        String password= (String) data.get("password");
        QueryWrapper<mUser>qu=new QueryWrapper<>();
        qu.eq("Role",role).eq("UserName",username);
        mUser user=service.getOne(qu);
        password= DigestUtils.md5Hex(password);
        user.setPassword(password);
        boolean isok=service.update(user,qu);
        if(isok){
            return Result.ok("修改成功");
        }
        return Result.ok("修改失败");
    }
}
