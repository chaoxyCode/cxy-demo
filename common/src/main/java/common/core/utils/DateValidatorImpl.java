package common.core.utils;

import common.core.interfaces.DateValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;

/**
 * 自定义校验-日期字符串-实现类
 *
 * @author chaoxy
 * @date 20190531
 */
public class DateValidatorImpl implements ConstraintValidator<DateValidator, String> {

  /** 日期格式 */
  private String dateFormat;

  @Override
  public void initialize(DateValidator constraintAnnotation) {
    this.dateFormat = constraintAnnotation.dateFormat();
  }

  /**
   * 自定义校验 逻辑实现
   *
   * @param value 需要校验的值
   * @param context 校验容器
   * @return boolean
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (null == value || "".equals(value)) {
      return Boolean.TRUE;
    }
    try {
      SimpleDateFormat df = new SimpleDateFormat(dateFormat);
      df.setLenient(false);
      return null != df.parse(value);
    } catch (Exception e) {
      return Boolean.FALSE;
    }
  }
}
