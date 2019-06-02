package io.github.gcdd1993.ioc.bean;

/**
 * bean factory interface
 *
 * @author gaochen
 * @date 2019/6/2
 */
public interface BeanFactory {

    /**
     * 通过bean名称获取bean
     *
     * @param name bean名称
     * @return bean
     */
    Object getBean(String name);

    /**
     * 通过bean类型获取bean
     *
     * @param tClass bean类型
     * @param <T>    泛型T
     * @return bean
     */
    <T> T getBean(Class<T> tClass);

}
