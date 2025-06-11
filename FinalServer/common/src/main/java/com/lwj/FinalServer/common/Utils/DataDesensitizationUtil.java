package com.lwj.FinalServer.common.Utils;

import org.apache.commons.lang3.StringUtils;

public class DataDesensitizationUtil {
    public static String handlePhone(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return StringUtils.left(value, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(value, 4), StringUtils.length(value)
                , "*"), "***"));
    }

    public static String handleName(String name) {
        if(StringUtils.isNotBlank(name)){
            String aname=StringUtils.left(name,1);
            return StringUtils.rightPad(name,StringUtils.length(aname));
        }
        return name;
    }

    public static String handleId(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return StringUtils.left(value, 1) + StringUtils.repeat("*", value.length() - 2) + StringUtils.right(value, 1);
    }

}
