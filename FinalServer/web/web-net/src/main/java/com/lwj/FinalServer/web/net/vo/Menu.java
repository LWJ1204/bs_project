package com.lwj.FinalServer.web.net.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class Menu {
    @Setter
    @Getter
    private String path;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String label;
    @Setter
    @Getter
    private String icon;
    @Setter
    @Getter
    private String url;
    @Setter
    @Getter
    private List<MenuItem> children;

}
