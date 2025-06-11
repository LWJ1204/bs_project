package com.lwj.FinalServer.web.net.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class PageInfo {
    /*
    页数
    * */
    private Integer pageNum;
    /*
    每页大小
    * */
    private Integer pageSize;
    /*
    总共
    * */
    private Integer total;
}
