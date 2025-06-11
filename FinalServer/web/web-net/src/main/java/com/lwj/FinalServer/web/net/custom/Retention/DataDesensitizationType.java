package com.lwj.FinalServer.web.net.custom.Retention;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataDesensitizationType {
    int type() default 1;
}
