package io.github.gcdd1993.ioc.annotation;

import java.lang.annotation.*;

/**
 * 定义依赖注入
 *
 * @author gaochen
 * @date 2019/6/2
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    /**
     * @return 要注入的bean名称，如果为空，按照类型注入
     */
    String name() default "";
}
