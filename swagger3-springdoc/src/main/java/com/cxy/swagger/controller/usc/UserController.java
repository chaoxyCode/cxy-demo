package com.cxy.swagger.controller.usc;

import com.cxy.swagger.dto.user.UserInfoDTO;
import com.cxy.swagger.dto.user.UserInfoOutDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用户相关服务
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Tag(name = "用户", description = "用户相关服务文档")
@RestController
@ResponseBody
@Slf4j
@RequestMapping("/api/usc")
public class UserController {

    @Operation(summary = "查询用户列表", description = "根据用户名模糊查询用户列表")
    @RequestMapping(
            value = "/list",
            method = {RequestMethod.GET, RequestMethod.POST})
    public List<UserInfoOutDTO> getUserList(
            @Parameter(description = "用户名") @RequestParam(value = "userName", required = false)
                    String userName) {
        List<UserInfoOutDTO> result = new ArrayList<>();
        UserInfoOutDTO info = new UserInfoOutDTO();
        info.setUserName("zhang san");
        info.setAge(18);
        info.setEmail("zhangsan@163.com");
        if (StringUtils.isNotBlank(userName)) {
            info.setUserName(userName);
        }
        result.add(info);
        return result;
    }

    @Operation(summary = "新增用户", description = "新增用户信息")
    @PostMapping("/add")
    public String addUser(@Parameter(required = true) @Valid @RequestBody UserInfoDTO request) {
        log.info("新增用户: {}", request);
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
