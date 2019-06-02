package io.github.gcdd1993.ioc.annotation;

import java.lang.annotation.*;

/**
 * 标记为bean
 *
 * @author gaochen
 * @date 2019/6/2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    String name() default "";
}
