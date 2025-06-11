package com.lwj.FinalServer.web.net.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Schema(description = "图像验证码")
@Data
public class CaptchaVo {
    @Schema(description="验证码图片信息")
    private String image;

    @Schema(description="验证码key")
    private String key;
}
