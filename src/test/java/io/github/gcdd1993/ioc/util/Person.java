package io.github.gcdd1993.ioc.util;

import io.github.gcdd1993.ioc.annotation.Autowired;
import io.github.gcdd1993.ioc.annotation.Bean;
import io.github.gcdd1993.ioc.annotation.Value;
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

    @Value("gaochen")
    private String name;

    @Value("27")
    private String age;
}
