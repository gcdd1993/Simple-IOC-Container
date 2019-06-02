package io.github.gcdd1993.ioc.bean;

import io.github.gcdd1993.ioc.util.Person;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author gaochen
 * @date 2019/6/2
 */
public class ApplicationContextTest {

    @Test
    public void refresh() {
        Set<String> basePackages = new HashSet<>(1);
        basePackages.add("io.github.gcdd1993.ioc");
        ApplicationContext ctx = new ApplicationContext(basePackages);
        ctx.refresh();

        Person person = ctx.getBean(Person.class);
        System.out.println(person);

        Object person1 = ctx.getBean("Person");
        System.out.println(person1);
    }
}