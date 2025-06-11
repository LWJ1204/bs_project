package com.lwj.FinalServer.web.net.custom.Component;

import com.lwj.FinalServer.common.Utils.DataDesensitizationUtil;
import com.lwj.FinalServer.model.Enum.DataDesensitizationTypeEnum;
import io.micrometer.common.util.StringUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class DataDesensitizationFormatter  implements Formatter<String> {
    private DataDesensitizationTypeEnum type;

    public void setTypeEnum(DataDesensitizationTypeEnum type){
        this.type=type;
    }

    @Override
    public String parse(String text, Locale locale) throws ParseException {
        if(StringUtils.isNotBlank(text)){
            switch (type){
                case ID :
                    text= DataDesensitizationUtil.handleId(text);
                    break;
                case PHONE:
                    text=DataDesensitizationUtil.handlePhone(text);
                    break;
                default:
            }
        }
        return text;
    }

    @Override
    public String print(String object, Locale locale) {
        return object;
    }
}
