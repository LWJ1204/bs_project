package com.lwj.FinalServer.web.net.custom.config;


import cn.hutool.log.Log;
import com.lwj.FinalServer.common.Utils.DataDesensitizationUtil;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.web.net.custom.Retention.DataDesensitization;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.RecordStateVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Aspect
@Component
public class DataDesensitizationAspect {
    private static final Logger log= (Logger) LoggerFactory.getLogger(DataDesensitizationAspect.class);
    //定义切入点
    @Pointcut("@annotation(com.lwj.FinalServer.web.net.custom.Retention.DataDesensitization)")
    public void pointcut(){
        log.info("DataDesensitizationAspect pointcut initialized");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (!method.isAnnotationPresent(DataDesensitization.class)) {
            return getResult(point);
        }
        //获取传递的参数
        Object[]args=point.getArgs();
        Map<String,Object>data=convertArgToMap(args);
        //判断参数是否有去敏标志
        if(data.containsKey("Absolute")&& (boolean) data.get("Absolute")){
                Object result=getResult(point);
                return desensitizaResult(result);
        }
        return getResult(point);
    }

    private Object desensitizaResult(Object result) {
        try {
            //拦截的返回结果
            Result<PageBean<RecordStateVO>> res = (Result<PageBean<RecordStateVO>>) result;
            PageBean<RecordStateVO> data = res.getData();
            //签到记录列表
            List<RecordStateVO> list = data.getList();
            for (RecordStateVO rvo:list){
                //去敏处理
                rvo.setAstudentid(DataDesensitizationUtil.handleId(rvo.getStudentid()));
                rvo.setStudentName(null);
            }
            //更新返回结果
            data.setList(list);
            res.setData(data);
            log.info("更改后的数据为："+res);
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private Map<String, Object> convertArgToMap(Object[] args) {
        Map<String,Object>ans=new HashMap<>();
        if(args != null){
            for(Object obj:args){
                if(obj instanceof Map){
                    ans.putAll((Map<String,Object>) obj);
                }
            }
        }
        return ans;
    }

    private Object getResult(ProceedingJoinPoint point) throws Throwable {
        return point.proceed();
    }

}
