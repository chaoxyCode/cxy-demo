package com.cxy.swagger.dto.user;

import com.cxy.swagger.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户信息
 *
 * @author chaoxy
 * @version 1.0
 * @date 2021-09-18
 */
@Schema(description = "用户信息请求参数实体")
@Setter
@Getter
@ToString(callSuper = true)
public class UserInfoDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /** 年龄 */
    @Schema(description = "年龄")
    @NotNull(message = "年龄不能为空")
    private Integer age;

    /** 邮箱 */
    @Schema(example = "zhangsan@126.com", description = "邮箱")
    @Email(message = "邮箱格式错误")
    private String email;
}
