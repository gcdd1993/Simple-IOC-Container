package io.github.gcdd1993.ioc.util;

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
public class Address {
    @Value("2222")
    private String longitude;

    @Value("1111")
    private String latitude;
}
