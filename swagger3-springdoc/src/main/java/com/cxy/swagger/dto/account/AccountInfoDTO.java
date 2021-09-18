package com.cxy.swagger.dto.account;

import com.cxy.swagger.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 账户信息
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Schema(description = "账户信息请求参数实体")
@Setter
@Getter
@ToString(callSuper = true)
public class AccountInfoDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    /** 账号 */
    @Schema(description = "账号")
    @NotBlank(message = "账号不能为空")
    private String acctNo;
}
