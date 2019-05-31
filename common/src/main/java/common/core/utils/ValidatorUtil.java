package common.core.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 公共校验工具类
 *
 * @author chaoxy
 * @date 20190531
 */
public class ValidatorUtil {

  private static Validator validator;

  static {
    // 快速结束模式 failFast（true）
    // ValidatorFactory factory =
    // Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  /**
   * 功能数据验证处理
   *
   * @param obj 数据实体类
   * @return Map<String, String>
   */
  public static Map<String, String> commonValidate(Object obj) {
    return commonValidation(false, obj, null);
  }

  /**
   * 单一属性校验
   *
   * @param obj 数据实体类
   * @param propertyName 单一属性名称
   * @return Map<String, String>
   */
  public static Map<String, String> commonValidateProperty(Object obj, String propertyName) {
    return commonValidation(true, obj, propertyName);
  }

  /**
   * 公共校验
   *
   * @param isProperty 是否为单一属性校验
   * @param obj 数据实体类
   * @param propertyName 单一属性名称
   * @return Map<String, String>
   */
  private static Map<String, String> commonValidation(
      boolean isProperty, Object obj, String propertyName) {
    Set<ConstraintViolation<Object>> failures;
    if (isProperty) {
      failures = validator.validateProperty(obj, propertyName);
    } else {
      failures = validator.validate(obj);
    }
    if (null == failures || failures.isEmpty()) {
      return null;
    }
    Map<String, String> map = new HashMap<>(16);
    failures.forEach(
        failure -> {
          map.put(failure.getPropertyPath().toString(), failure.getMessage());
        });
    return map;
  }
}
