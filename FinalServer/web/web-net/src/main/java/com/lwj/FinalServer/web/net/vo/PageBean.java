package com.lwj.FinalServer.web.net.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class PageBean<T> {
    private Long count;
    private List<T>list;
}
