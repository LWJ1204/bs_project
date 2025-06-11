package com.lwj.FinalServer.web.net.custom.Interceptor;

import com.lwj.FinalServer.common.Exception.myException;
import com.lwj.FinalServer.common.Utils.JwtUtil;
import com.lwj.FinalServer.common.Utils.RequestUtil;
import com.lwj.FinalServer.common.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token=request.getHeader("Access-Token");
        String Method=request.getMethod();
        System.out.println("testMethod:"+Method);
        if("OPTIONS".equals(Method)) return true;
        System.out.println("testtoken: "+token);
        if(token==null){
            throw  new myException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        JwtUtil.parseToken(token);
        return true;
    }

}
