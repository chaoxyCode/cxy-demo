package common.core.interfaces;

import common.core.utils.EnumValidatorImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义校验-枚举数据-注释
 *
 * @author chaoxy
 * @date 20190531
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumValidatorImpl.class})
public @interface EnumValidator {
    /**
     * 校验信息 必须的属性 利用 {} 获取 属性值 参考官方的 message
     *
     * @see org.hibernate.validator 静态资源包里面的 message 编写方式
     * @return String
     */
    String message() default "不在约定数据范围内";

    /**
     * 分组校验 必须的属性
     *
     * @return 数组
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 校验枚举-枚举类
     *
     * @return clazz
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 校验枚举-枚举类方法
     *
     * @return String
     */
    String enumMethod() default "getEnumTypeByValue";
}
