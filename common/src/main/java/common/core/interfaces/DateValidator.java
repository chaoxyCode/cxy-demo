package common.core.interfaces;

import common.core.utils.DateValidatorImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义校验日期字符串注释
 *
 * @author chaoxy
 * @date 20190531
 */
@Constraint(validatedBy = {DateValidatorImpl.class})
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateValidator {
  /**
   * 校验信息 必须的属性 利用 {} 获取 属性值 参考官方的 message
   *
   * @see org.hibernate.validator 静态资源包里面的 message 编写方式
   * @return String
   */
  String message() default "日期格式不匹配{dateFormat}";

  /**
   * 校验格式 默认 yyyy-MM-dd HH:mm:ss
   *
   * @return String
   */
  String dateFormat() default "yyyy-MM-dd HH:mm:ss";

  /**
   * 分组校验 必须的属性
   *
   * @return 数组
   */
  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
