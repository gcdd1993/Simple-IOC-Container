package io.github.gcdd1993.ioc.annotation;

import java.lang.annotation.*;

/**
 * TODO
 *
 * @author gaochen
 * @date 2019/6/2
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    String value();
}
