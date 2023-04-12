## 1. 概述

**默认情况下，Spring在应用程序上下文的启动/引导时急切地创建所有单例bean**。这背后的原因很简单：立即避免和检测所有可能的错误，而不是在运行时。

但是，在某些情况下，我们需要在请求它时才创建bean，而不是在应用程序上下文启动时。

**在这个快速教程中，我们将讨论Spring中的@Lazy注解**。

## 2. 延迟初始化

@Lazy注解自Spring 3.0版本起就存在。有几种方法可以告诉IoC容器延迟初始化bean。

### 2.1 @Configuration类

**当我们将@Lazy注解放在@Configuration类上时，它表示所有带有@Bean注解的方法都应该延迟加载**。

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

如我们所见，所有bean仅在我们第一次请求它们时才会创建：

```shell
Region bean initialized
Country bean initialized
```

要仅将其应用于特定的bean，让我们从类上删除@Lazy。

然后我们将它添加到所需bean的配置上：

```java
@Lazy
@Bean
public Region getRegion() {
    return new Region();
}
```

### 2.2 与@Autowired使用

在继续之前，请查看这些文章以了解[@Autowired](https://www.baeldung.com/spring-autowire)和[@Component](https://www.baeldung.com/spring-bean-annotations)注解。

**为了初始化一个惰性bean，我们从另一个bean引用它**。

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

**请注意，@Lazy在这两个地方都是强制性的**。

在City类上使用@Component注解并使用@Autowired引用它：

```java
@Test
void givenLazyAnnotation_whenAutowire_thenLazyBean() {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class);
    ctx.refresh();
    Region region = ctx.getBean(Region.class);
    region.getCityInstance();
}
```

在这里，**City bean只有在我们调用getCityInstance()方法时才被初始化**。

## 3. 总结

在这个快速教程中，我们介绍了Spring中@Lazy注解的基本使用，以及几种配置和使用它的方法。