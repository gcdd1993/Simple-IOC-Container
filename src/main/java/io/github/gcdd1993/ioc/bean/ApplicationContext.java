package io.github.gcdd1993.ioc.bean;

import io.github.gcdd1993.ioc.annotation.Autowired;
import io.github.gcdd1993.ioc.annotation.Bean;
import io.github.gcdd1993.ioc.annotation.Value;
import io.github.gcdd1993.ioc.util.PackageScanner;
import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author gaochen
 * @date 2019/6/2
 */
@Data
public class ApplicationContext implements BeanFactory {

    private final Set<String> basePackages;

    /**
     * 按照beanName分组
     */
    private final Map<String, Object> beanByNameMap = new ConcurrentHashMap<>(256);

    /**
     * 按照beanClass分组
     */
    private final Map<Class<?>, Object> beanByClassMap = new ConcurrentHashMap<>(256);

    @Override
    public Object getBean(String name) {
        return beanByNameMap.get(name);
    }

    @Override
    public <T> T getBean(Class<T> tClass) {
        return tClass.cast(beanByClassMap.get(tClass));
    }

    /**
     * 默认构造器
     */
    public ApplicationContext() {
        this(new HashSet<>(Collections.singletonList(ApplicationContext.class.getPackage().getName())));
    }

    /**
     * 全参构造器
     *
     * @param basePackages 扫描的包名列表
     */
    public ApplicationContext(Set<String> basePackages) {
        this.basePackages = basePackages;
        refresh();
    }

    /**
     * 刷新容器
     */
    public void refresh() {
        for (String packageName : basePackages) {
            List<Class> beanClasses = PackageScanner.findClassesWithAnnotation(packageName, Bean.class);
            System.out.println("scan classes with Bean annotation : " + beanClasses.toString());

            for (Class beanClass : beanClasses) {
                try {
                    createBean(beanClass);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过bean名称创建bean
     *
     * @param beanName bean名称
     * @return bean
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Object createBean(String beanName) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class<?> aClass = Class.forName(beanName);
        return createBean(aClass);
    }

    /**
     * 通过bean类型创建bean
     *
     * @param beanClass bean类型
     * @return bean
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private Object createBean(Class<?> beanClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (beanByClassMap.get(beanClass) != null) {
            return beanByClassMap.get(beanClass);
        }
        Constructor constructor = beanClass.getDeclaredConstructor();
        Object object = constructor.newInstance();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired != null) {
                // 依赖注入
                String name = autowired.name();
                // 按照名称注入
                Object diObj;
                if (!name.isEmpty()) {
                    diObj = beanByNameMap.get(name) == null ?
                            createBean(name) :
                            beanByNameMap.get(name);
                } else {
                    // 按照类型注入
                    Class<?> aClass = field.getType();
                    diObj = beanByClassMap.get(aClass) == null ?
                            createBean(aClass) :
                            beanByClassMap.get(aClass);
                }
                // 注入
                field.setAccessible(true);
                field.set(object, diObj);
            } else {
                Value value = field.getAnnotation(Value.class);
                if (value != null) {
                    // 注入
                    field.setAccessible(true);
                    // 需要做一些类型转换，从String转为对应的类型
                    field.set(object, value.value());
                }
            }
        }

        Bean bean = beanClass.getAnnotation(Bean.class);

        beanByNameMap.put(bean.name().isEmpty() ?
                beanClass.getSimpleName() :
                bean.name(), object);

        beanByClassMap.put(beanClass, object);

        return object;
    }

}
