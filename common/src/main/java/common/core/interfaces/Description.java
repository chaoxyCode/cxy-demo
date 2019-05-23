package common.core.interfaces;

import java.lang.annotation.*;

/**
 * 描述接口类
 *
 * @auther chaoxy
 * @date 2019-05-13
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {

  String value() default "";
}
