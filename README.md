# 前言

本文是为了学习`Spring IOC`容器的执行过程而写，不能完全代表`Spring IOC`容器，只是简单实现了容器的**依赖注入**和**控制反转**功能，无法用于生产，只能说对理解Spring容器能够起到一定的作用。

# 开始

## 创建项目

创建Gradle项目，并修改`build.gradle`

```groovy
plugins {
    id 'java'
    id "io.franzbecker.gradle-lombok" version "3.1.0"
}

group 'io.github.gcdd1993'
version '1.0-SNAPSHOT'

sourceCompatibility = 1.8

repositories {
    mavenCentral()
}

dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```

## 创建`BeanFactory`

`BeanFactory`是IOC中用于存放bean实例以及获取bean的核心接口，它的核心方法是`getBean`以及`getBean`的重载方法，这里简单实现两个`getBean`的方法。

```java
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
```

## 创建`ApplicationContext`上下文

`ApplicationContext`，即我们常说的应用上下文，实际就是Spring容器本身了。

我们创建`ApplicationContext`类，并实现`BeanFactory`接口。

```java
public class ApplicationContext implements BeanFactory {
}
```

### `getBean`方法

既然说是容器，那肯定要有地方装我们的bean实例吧，使用两个Map作为容器。

```java
/**
 * 按照beanName分组
 */
private final Map<String, Object> beanByNameMap = new ConcurrentHashMap<>(256);

/**
 * 按照beanClass分组
 */
private final Map<Class<?>, Object> beanByClassMap = new ConcurrentHashMap<>(256);
```

然后，我们可以先完成我们的`getBean`方法。

```java
@Override
public Object getBean(String name) {
    return beanByNameMap.get(name);
}

@Override
public <T> T getBean(Class<T> tClass) {
    return tClass.cast(beanByClassMap.get(tClass));
}
```

直接从Map中获取bean实例，是不是很简单？当然了，在真实的Spring容器中，是不会这么简单啦，不过我们这次是要化繁为简，理解IOC容器。

### 构造器

Spring提供了`@ComponentScan`来扫描包下的`Component`，我们为了简便，直接在构造器中指定要扫描的包。

```java
private final Set<String> basePackages;
/**
 * 默认构造器，默认扫描当前所在包
 */
public ApplicationContext() {
    this(new HashSet<>(Collections.singletonList(ApplicationContext.class.getPackage().getName())));
}

/**
 * 全参构造器
 * @param basePackages 扫描的包名列表
 */
public ApplicationContext(Set<String> basePackages) {
    this.basePackages = basePackages;
}
```

### `refresh`方法

refresh的过程基本按照以下流程来走

![](https://i.loli.net/2019/06/02/5cf3c4c4b465587417.png)

1. 扫描指定的包下所有带`@Bean`注解（Spring中是`@Component`注解）的类。

```java
List<Class> beanClasses = PackageScanner.findClassesWithAnnotation(packageName, Bean.class);
System.out.println("scan classes with Bean annotation : " + beanClasses.toString());

for (Class beanClass : beanClasses) {
    try {
        createBean(beanClass);
    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
        e.printStackTrace();
    }
}
```

2. 遍历类，获取类的构造器以及所有字段。

```java
Constructor constructor = beanClass.getDeclaredConstructor();
Object object = constructor.newInstance();
Field[] fields = beanClass.getDeclaredFields();
```

3. 判断字段是依赖注入的还是普通字段。

4. 如果是普通字段，通过字段类型初始化该字段，并尝试从`@Value`注解获取值塞给字段。

```java
Value value = field.getAnnotation(Value.class);
if (value != null) {
    // 注入
    field.setAccessible(true);
    // 需要做一些类型转换，从String转为对应的类型
    field.set(object, value.value());
}
```

5. 如果是依赖注入的字段，尝试从`beanByClassMap`中获取对应的实例，如果没有，就先要去实例化该字段对应的类型。

```java
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
}
```

## 测试我们的IOC容器

创建`Address`

```java
@Data
@Bean
public class Address {
    @Value("2222")
    private String longitude;

    @Value("1111")
    private String latitude;
}
```

创建`Person`并注入`Address`

```java
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
```

创建测试类`ApplicationContextTest`

```java
public class ApplicationContextTest {

    @Test
    public void refresh() {
        Set<String> basePackages = new HashSet<>(1);
        basePackages.add("io.github.gcdd1993.ioc");
        ApplicationContext ctx = new ApplicationContext(basePackages);
        ctx.refresh();

        Person person = ctx.getBean(Person.class);
        System.out.println(person);

        Object person1 = ctx.getBean("person");
        System.out.println(person1);
    }
}
```

控制台将会输出：

```bash
scan classes with Bean annotation : [class io.github.gcdd1993.ioc.util.Address, class io.github.gcdd1993.ioc.util.Person]
scan classes with Bean annotation : [class io.github.gcdd1993.ioc.util.Address, class io.github.gcdd1993.ioc.util.Person]
Person(address=Address(longitude=2222, latitude=1111), name=gaochen, age=27)
Person(address=Address(longitude=2222, latitude=1111), name=gaochen, age=27)
```

# 获取源码

完整源码可以在我的github仓库获取👉[Simple-IOC-Container](<https://github.com/gcdd1993/Simple-IOC-Container>)