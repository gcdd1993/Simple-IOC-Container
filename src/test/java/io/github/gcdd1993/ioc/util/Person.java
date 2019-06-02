package io.github.gcdd1993.ioc.util;

import io.github.gcdd1993.ioc.annotation.Autowired;
import io.github.gcdd1993.ioc.annotation.Bean;
import lombok.Data;

/**
 * TODO
 *
 * @author gaochen
 * @date 2019/6/2
 */
@Data
@Bean
public class Person {
    @Autowired
    private Address address;

    private String name;
    private int age;
}
