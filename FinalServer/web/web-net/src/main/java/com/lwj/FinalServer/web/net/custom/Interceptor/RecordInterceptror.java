package com.lwj.FinalServer.web.net.custom.Interceptor;

import com.lwj.FinalServer.common.Utils.JwtUtil;
import com.lwj.FinalServer.common.Utils.RequestUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

@Component
public class RecordInterceptror implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }


        String token=request.getHeader("Access-token");
        String ip= RequestUtil.getClientIp(request);
        Claims claims =JwtUtil.parseToken(token);
        request.setAttribute("UserId",claims.get("Id"));
        request.setAttribute("role",claims.get("role"));
        request.setAttribute("username",claims.get("username"));
        String mip= (String) claims.get("IP");
        return mip.equals(ip);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
