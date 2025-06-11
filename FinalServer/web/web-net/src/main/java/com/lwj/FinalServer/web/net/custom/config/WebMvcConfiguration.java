package com.lwj.FinalServer.web.net.custom.config;

import com.lwj.FinalServer.web.net.custom.Interceptor.AuthenticationInterceptor;
import com.lwj.FinalServer.web.net.custom.Interceptor.LoginInterceptor;
import com.lwj.FinalServer.web.net.custom.Interceptor.RecordInterceptror;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private RecordInterceptror recordInterceptror;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(this.authenticationInterceptor)
               .addPathPatterns("/api/**")
               .excludePathPatterns("/api/permission/**")
               .excludePathPatterns("/api/record/**")
               .excludePathPatterns("hello");
       registry.addInterceptor(this.loginInterceptor)
               .addPathPatterns("/api/permission/login");
       registry.addInterceptor(this.recordInterceptror)
               .addPathPatterns("/api/record/**");
    }


}
