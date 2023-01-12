## 1. 概述

**默认情况下，Spring在应用程序上下文的启动/引导时急切地创建所有单例bean**。
这背后的原因很简单：在启动时避免并立即检测所有可能的错误，而不是在运行时。

然而，在某些情况下，我们需要在请求它时才创建bean，而不是在应用程序上下文启动时。

**在本文中，我们将介绍Spring中的@Lazy注解**。

## 2. 延迟初始化

@Lazy注解自Spring 3.0版本起就存在。有几种方法可以告诉IoC容器延迟初始化bean。

### 2.1 @Configuration类

**当我们在@Configuration类上加上@Lazy注解时，它表示所有带有@Bean注解的方法都应该被延迟加载**。

这等效于基于XML配置的default-lazy-init="true"属性。

让我们看看下面的AppConfig配置类：

```java

@Lazy
@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.lazy")
public class AppConfig {

    @Bean
    public Region getRegion() {
        return new Region();
    }

    @Bean
    public Country getCountry() {
        return new Country();
    }
}
```

现在让我们测试一下功能：

```java
class LazyAnnotationUnitTest {

    @Test
    void givenLazyAnnotation_whenConfigClass_thenLazyAll() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
        ctx.getBean(Region.class);
        ctx.getBean(Country.class);
    }
}
```

正如我们所见，所有bean仅在我们第一次请求它们时才会创建：

```
Region bean initialized
Country bean initialized
```

要将其仅应用于特定的bean，让我们从类上删除@Lazy。

然后我们将它添加到所需bean的配置上：

```java

@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.lazy")
public class AppConfig {

    @Lazy
    @Bean
    public Region getRegion() {
        return new Region();
    }

    @Bean
    public Country getCountry() {
        return new Country();
    }
}
```

### 2.2 与@Autowired使用

**为了初始化一个惰性bean，我们从另一个bean中引用它**。

我们要延迟加载的bean：

```java

@Lazy
@Component
public class City {

    public City() {
        System.out.println("City bean initialized");
    }
}
```

引用它的bean：

```java
public class Region {

    @Lazy
    @Autowired
    private City city;

    public Region() {
        System.out.println("Region bean initialized");
    }

    public City getCityInstance() {
        return city;
    }
}
```

**当在City类上使用@Component注解，并使用@Autowired引用它时：@Lazy在这两个类中都是强制性的**。

```java
class LazyAnnotationUnitTest {

    @Test
    void givenLazyAnnotation_whenAutowire_thenLazyBean() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
        Region region = ctx.getBean(Region.class);
        region.getCityInstance();
    }
}
```

在这里，**只有当我们调用getCityInstance()方法时才会初始化City bean**。

## 3. 总结

在这个教程中，我们介绍了Spring中@Lazy注解的基本使用，以及几种配置和使用它的方法。