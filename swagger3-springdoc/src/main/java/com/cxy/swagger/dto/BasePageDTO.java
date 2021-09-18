package com.cxy.swagger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 基础分页
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Schema(description = "分页请求参数实体父类")
@Setter
@Getter
@ToString(callSuper = true)
public class BasePageDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    /** 页数 */
    @Schema(description = "页数")
    @NotNull(message = "页数不能为空")
    @Min(value = 1L, message = "页数最小值为1")
    private Integer page;

    /** 每页数量 */
    @Schema(description = "每页数量")
    @NotNull(message = "每页数量不能为空")
    @Min(value = 1L, message = "每页数量最小值为1")
    private Integer size;
}
