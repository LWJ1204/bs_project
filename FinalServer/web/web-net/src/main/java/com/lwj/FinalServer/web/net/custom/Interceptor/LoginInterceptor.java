package com.lwj.FinalServer.web.net.custom.Interceptor;

import com.lwj.FinalServer.common.Utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 包装request，缓存请求体
        ContentCachingRequestWrapper cachingRequest = null;
        if (!(request instanceof ContentCachingRequestWrapper)) {
            cachingRequest = new ContentCachingRequestWrapper(request);
        } else {
            cachingRequest = (ContentCachingRequestWrapper) request;
        }

        // 读取请求体
        String body = new String(cachingRequest.getContentAsByteArray(), StandardCharsets.UTF_8);

        // 获取客户端IP
        String clientIp = RequestUtil.getClientIp(request);

        // 将IP放入request属性，方便后续controller使用
        request.setAttribute("clientIp", clientIp);

        // 这里打印测试
        log.info("请求体：" + body);
        log.info("客户端IP：" + clientIp);

        return true;
    }


}
