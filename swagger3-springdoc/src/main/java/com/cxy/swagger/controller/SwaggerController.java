package com.cxy.swagger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * SwaggerController
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Tag(name = "api", description = "Swagger3 springdoc  openapi")
@RestController
@ResponseBody
@Slf4j
@RequestMapping("/api")
public class SwaggerController {

    @Operation(summary = "健康检查", description = "检查应用状态")
    @RequestMapping(
            value = "/ok",
            method = {RequestMethod.GET, RequestMethod.POST})
    public String ok() {
        log.info("健康检查");
        return "ok";
    }
}
