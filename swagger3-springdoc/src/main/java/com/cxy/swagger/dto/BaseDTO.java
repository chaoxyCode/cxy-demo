package com.cxy.swagger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 基础
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Schema(description = "请求参数实体父类")
@Setter
@Getter
@ToString(callSuper = true)
public class BaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Schema(description = "用户ID")
    private String userId;
}
