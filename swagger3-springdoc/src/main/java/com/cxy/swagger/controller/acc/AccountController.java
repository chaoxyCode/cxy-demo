package com.cxy.swagger.controller.acc;

import com.cxy.swagger.dto.account.AccountInfoDTO;
import com.cxy.swagger.dto.account.AccountInfoOutDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户相关服务
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Tag(name = "账户", description = "账户相关服务接口")
@RestController
@ResponseBody
@Slf4j
@RequestMapping("/api/acc")
public class AccountController {

    @Operation(summary = "查询用户账户列表", description = "根据用户ID查询用户账户列表")
    @RequestMapping(
            value = "/list",
            method = {RequestMethod.GET, RequestMethod.POST})
    public List<AccountInfoOutDTO> getUserAccountList(
            @Parameter(description = "用户ID") @RequestParam(value = "userId", required = false)
                    String userId) {
        List<AccountInfoOutDTO> result = new ArrayList<>();
        AccountInfoOutDTO info = new AccountInfoOutDTO();
        info.setAcctName("zhang san");
        info.setBalance(new BigDecimal("1000.00"));
        info.setAvailableBalance(new BigDecimal("1000.00"));
        if (StringUtils.isNotBlank(userId)) {
            info.setAvailableBalance(new BigDecimal("900.00"));
            info.setFreezeBalance(new BigDecimal("100.00"));
        }
        result.add(info);
        return result;
    }

    @Operation(summary = "查询用户账户信息", description = "根据用户号、账号查询用户账户信息")
    @PostMapping("/getAccountInfo")
    public AccountInfoOutDTO getUserAccount(
            @Parameter(required = true) @Valid @RequestBody AccountInfoDTO request) {
        AccountInfoOutDTO info = new AccountInfoOutDTO();
        info.setAcctNo(request.getAcctNo());
        info.setAcctName("李四");
        info.setAvailableBalance(new BigDecimal("900.00"));
        info.setFreezeBalance(new BigDecimal("100.00"));
        return info;
    }
}
