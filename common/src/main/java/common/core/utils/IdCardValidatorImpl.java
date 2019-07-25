package common.core.utils;

import common.core.interfaces.IdCardValidator;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义校验-身份证校验-实现类
 *
 * @author chaoxy
 * @date 20190710
 */
public class IdCardValidatorImpl implements ConstraintValidator<IdCardValidator, String> {

    /** 校验身份证长度（是否限制长度15或18位），为空不限制 */
    private String cardLength;
    /** 长度18位限制 */
    private static final String CARD_LIMIT = "18";

    @Override
    public void initialize(IdCardValidator constraintAnnotation) {
        this.cardLength = constraintAnnotation.cardLength();
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
        if (StringUtils.isBlank(value)) {
            return Boolean.TRUE;
        }
        if (CARD_LIMIT.equals(cardLength)) {
            int limit = Integer.parseInt(CARD_LIMIT);
            if (value.length() != limit) {
                return Boolean.FALSE;
            }
        }
        return IdCardUtil.isValidatedAllIdCard(value);
    }
}
