package common.core.interfaces;


import common.core.utils.IdCardValidatorImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义校验--身份证校验--注释
 *
 * @author chaoxy
 * @date 20190710
 */
@Constraint(validatedBy = {IdCardValidatorImpl.class})
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdCardValidator {
    /**
     * 校验信息 必须的属性 利用 {} 获取 属性值 参考官方的 message
     *
     * @see org.hibernate.validator 静态资源包里面的 message 编写方式
     * @return String
     */
    String message() default "身份证号不匹配身份证号规则";

    /**
     * 校验身份证长度（是否限制长度15或18位），为空不限制
     *
     * @return String
     */
    String cardLength() default "18";

    /**
     * 分组校验 必须的属性
     *
     * @return 数组
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
