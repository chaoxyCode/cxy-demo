package com.cxy.swagger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 基础响应实体父类
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Schema(description = "响应实体父类")
@Setter
@Getter
@ToString(callSuper = true)
public class BaseOutDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 响应码 */
    @Schema(description = "响应码")
    private String code;

    /** 错误信息 */
    @Schema(description = "错误信息")
    private String msg;
}
