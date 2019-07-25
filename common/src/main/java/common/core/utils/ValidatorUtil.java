package common.core.utils;

import common.core.enums.SysErrorInfo;
import common.core.exception.BusException;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 公共校验工具类
 *
 * @author 800P_chaoxy01
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
     * @param strType 是否拼接属性字段true-拼接
     * @param obj 数据实体类
     * @param groups 分组校验
     * @return String
     */
    public static String commonValidate(boolean strType, Object obj, Class<?>... groups) {
        return commonValidation(strType, false, obj, null, groups);
    }

    /**
     * 单一属性校验
     *
     * @param strType 是否拼接属性字段true-拼接
     * @param obj 数据实体类
     * @param propertyName 单一属性名称
     * @param groups 分组校验
     * @return String
     */
    public static String commonValidateProperty(
            boolean strType, Object obj, String propertyName, Class<?>... groups) {
        if (StringUtils.isBlank(propertyName)) {
            return null;
        }
        return commonValidation(strType, true, obj, propertyName, groups);
    }

    /**
     * 公共校验
     *
     * @param isProperty 是否校验单一字段属性
     * @param obj 实体类
     * @param propertyName 单一字段属性
     * @param groups 分组校验
     * @return Set
     */
    private static Set<ConstraintViolation<Object>> commonValidation(
            boolean isProperty, Object obj, String propertyName, Class<?>... groups) {
        if (null == groups) {
            if (isProperty) {
                return validator.validateProperty(obj, propertyName);
            }
            return validator.validate(obj);
        }
        if (isProperty) {
            return validator.validateProperty(obj, propertyName, groups);
        }
        return validator.validate(obj, groups);
    }

    /**
     * 公共校验
     *
     * @param strType 是否拼接属性字段true-拼接
     * @param isProperty 是否为单一属性校验
     * @param obj 数据实体类
     * @param propertyName 单一属性名称
     * @param groups 分组校验
     * @return String
     */
    private static String commonValidation(
            boolean strType,
            boolean isProperty,
            Object obj,
            String propertyName,
            Class<?>... groups) {
        Set<ConstraintViolation<Object>> failures =
                commonValidation(isProperty, obj, propertyName, groups);
        if (null == failures || failures.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        failures.forEach(
                failure -> {
                    if (strType) {
                        sb.append(failure.getPropertyPath().toString());
                        sb.append(":");
                        sb.append(failure.getMessage());
                        sb.append(";");
                    } else {
                        sb.append(failure.getMessage());
                        sb.append(";");
                    }
                });
        return sb.toString();
    }

    /**
     * 功能数据验证处理
     *
     * @param strType 是否拼接属性字段true-拼接
     * @param obj 数据实体类
     * @param groups 分组校验
     * @throws BusException
     */
    public static void commonValidateException(boolean strType, Object obj, Class<?>... groups)
            throws BusException {
        String errorMsg = ValidatorUtil.commonValidate(strType, obj, groups);
        if (StringUtils.isNotBlank(errorMsg)) {
            throw new BusException(SysErrorInfo.ARGUMENT_NOT_VALID.getErrorCode(), errorMsg);
        }
    }

    /**
     * 单一属性校验
     *
     * @param strType 是否拼接属性字段true-拼接
     * @param obj 数据实体类
     * @param propertyName 属性名称，若多个则以逗号分隔
     * @param groups 分组校验
     * @throws BusException
     */
    public static void commonValidatePropertyException(
            boolean strType, Object obj, String propertyName, Class<?>... groups)
            throws BusException {
        if (StringUtils.isNotBlank(propertyName)) {
            StringBuilder sb = new StringBuilder();
            String spiltStr = ",";
            if (propertyName.contains(spiltStr)) {
                String[] properties = propertyName.split(spiltStr);
                for (String property : properties) {
                    String errorMsg = commonValidateProperty(strType, obj, property, groups);
                    if (StringUtils.isNotBlank(errorMsg)) {
                        sb.append(errorMsg);
                        sb.append(";");
                    }
                }
            } else {
                String errorMsg = commonValidateProperty(strType, obj, propertyName, groups);
                if (StringUtils.isNotBlank(errorMsg)) {
                    sb.append(errorMsg);
                    sb.append(";");
                }
            }
            String errorMsg = sb.toString();
            if (StringUtils.isNotBlank(errorMsg)) {
                throw new BusException(SysErrorInfo.ARGUMENT_NOT_VALID.getErrorCode(), errorMsg);
            }
        }
    }
}
