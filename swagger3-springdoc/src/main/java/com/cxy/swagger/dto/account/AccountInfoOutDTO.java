package com.cxy.swagger.dto.account;

import com.cxy.swagger.dto.BaseOutDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 账户信息
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Schema(description = "账户信息响应实体")
@Setter
@Getter
@ToString(callSuper = true)
public class AccountInfoOutDTO extends BaseOutDTO {

    private static final long serialVersionUID = 1L;

    /** 账号 */
    @Schema(description = "账号")
    private String acctNo;

    /** 户名 */
    @Schema(description = "户名")
    private String acctName;

    /** 余额 */
    @Schema(description = "余额")
    private BigDecimal balance;

    /** 可用余额 */
    @Schema(description = "可用余额")
    private BigDecimal availableBalance;

    /** 冻结余额 */
    @Schema(description = "冻结余额")
    private BigDecimal freezeBalance;
}
