package common.core.utils;

import common.core.interfaces.EnumValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 自定义校验-枚举类-实现类
 *
 * @author chaoxy
 * @date 20190531
 */
public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Object> {

    private static final Logger log = LoggerFactory.getLogger(EnumValidatorImpl.class);

    /** 枚举类 */
    private Class<? extends Enum<?>> enumClass;
    /** 枚举类方法 */
    private String enumMethod;

    @Override
    public void initialize(EnumValidator enumValidator) {
        this.enumClass = enumValidator.enumClass();
        this.enumMethod = enumValidator.enumMethod();
    }

    /**
     * 自定义校验 逻辑实现
     *
     * @param value 需要校验的值
     * @param context 校验容器
     * @return boolean
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (null == value || "".equals(value)) {
            return Boolean.TRUE;
        }
        if (null == enumClass || null == enumMethod) {
            return Boolean.TRUE;
        }
        Class<?> clazz = value.getClass();
        try {
            Method method = enumClass.getMethod(enumMethod, clazz);
            Class<?> returnType = method.getReturnType();
            if (!Modifier.isStatic(method.getModifiers())) {
                log.error("自定义枚举[{}]类型校验方法[{}]错误：不是静态方法", enumClass, enumMethod);
                return Boolean.TRUE;
            }
            if (!Enum.class.equals(returnType.getSuperclass())) {
                log.error("自定义枚举[{}]类型校验方法[{}]返回类型[{}]错误", enumClass, enumMethod, returnType);
                return Boolean.TRUE;
            }
            Enum result = (Enum) method.invoke(null, value);
            return null != result;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }
}
