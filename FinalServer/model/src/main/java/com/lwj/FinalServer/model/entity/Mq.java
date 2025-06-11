package com.lwj.FinalServer.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@TableName(value ="mqtable")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mq extends BaseEntity{
    @Schema(description = "交换机名")
    @TableId(value = "CourseId")
    private String courseid;
    /**
     *
     */
    @Schema(description = "队列名")
    @TableField(value = "StudentId")
    private String studentid;

    @Schema(description = "交换机类型")
    @TableField(value = "ExchangeType")
    private Integer exchangetype;
    /**
     *
     */
    @Schema(description = "绑定类型")
    @TableField(value = "binding")
    private String binding;

    @Schema(description = "死信队列(0是1否)")
    @TableField(value = "delaytype")
    private Integer delaytype;

    @Schema(description = "删除标志（0删1存)")
    @TableField(value = "del_flag")
    private Integer del_flag;




}
