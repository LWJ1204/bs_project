package com.lwj.FinalServer.web.net.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class LoginVo {
    @Setter
    @Getter
    String state;
    @Getter
    @Setter
    String token;
    @Getter
    @Setter
    List<Menu>menuList;
}
