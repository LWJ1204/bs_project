package com.lwj.FinalServer.web.net.custom.Retention;


import java.lang.annotation.*;

@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataDesensitization {
}
