## 1. 概述

在本文中，我们将了解如何在Spring中查找使用自定义注解进行标注的所有bean。我们将根据我们使用的Spring版本介绍不同的方法。

## 2. 使用Spring Boot2.2或更高版本

从Spring Boot 2.2开始，我们可以使用getBeansWithAnnotation方法。

让我们创建一个示例。首先，我们将定义我们的自定义注解。
我们使用@Retention(RetentionPolicy.RUNTIME)对其进行标注，以确保程序在运行时可以访问该注解：

```java

@Retention(RetentionPolicy.RUNTIME)
public @interface MyCustomAnnotation {

}
```

现在，让我们定义一个使用我们的注解进行标注的第一个bean。我们还将使用@Component对其进行标注：

```java

@Component
@MyCustomAnnotation
public class MyComponent {

}
```

然后，让我们定义另一个用我们的注解标注的bean。但是，这一次我们将通过@Configuration类中的@Bean方法来创建它：

```java
public class MyService {

}

@Configuration
public class MyConfigurationBean {

  @Bean
  @MyCustomAnnotation
  MyService myService() {
    return new MyService();
  }
}
```

现在，让我们编写一个测试来检查getBeansWithAnnotation方法是否可以检测到我们的两个bean：

```java

@ExtendWith(SpringExtension.class)
public class FindBeansWithCustomAnnotationUnitTest {

  @Test
  void whenApplicationContextStarted_ThenShouldDetectAllAnnotatedBeans() {
    try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyComponent.class, MyConfigurationBean.class)) {
      Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MyCustomAnnotation.class);
      assertEquals(2, beans.size());
      assertTrue(beans.keySet().containsAll(List.of("myComponent", "myService")));
    }
  }
}
```

## 3. 使用较老的Spring版本

### 3.1 历史背景

在Spring Framework 5.2之前的版本中，getBeansWithAnnotation方法只能检测在类或接口级别注解的bean，
但无法检测在工厂方法级别注解的bean。

在Spring Boot 2.2中Spring Framework的依赖已经升级到了5.2，这就是为什么在旧版本的Spring中，我们刚刚编写的测试会失败：

+ 正确检测到MyComponent bean，因为注解处于类级别。
+ 不能检测到MyService bean，因为它是通过工厂方法创建的。

让我们看看如何绕过这种行为。

### 3.2 用@Qualifier修饰我们的自定义注解

有一个相当简单的解决方法：我们可以简单地用@Qualifier修饰我们的注解。

```java

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface MyCustomAnnotation {

}
```

现在，我们能够自动注入两个带注解的bean。让我们通过测试来检查一下：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MyConfigurationBean.class)
public class FindBeansWithCustomAnnotationUnitTest {
  @Autowired
  @MyCustomAnnotation
  private List<Object> annotatedBeans;

  @Test
  void whenAutowiring_ThenShouldDetectAllAnnotatedBeans() {
    assertEquals(2, annotatedBeans.size());
    List<String> classNames = annotatedBeans.stream()
        .map(Object::getClass)
        .map(Class::getName)
        .map(s -> s.substring(s.lastIndexOf(".") + 1)).toList();
    assertTrue(classNames.containsAll(List.of("MyComponent", "MyService")));
  }
}
```

这种解决方法是最简单的，但是，它可能不适合我们的需求，例如，如果我们不拥有注解。

我们还要注意，使用@Qualifier修饰我们的自定义注解会将其变成Spring限定符。

### 3.3 列出通过工厂方法创建的Bean

现在我们已经了解问题主要出现在通过工厂方法创建的bean上，让我们专注于如何仅列出这些。
我们将提供一个在所有情况下都有效的解决方案，而不会对我们的自定义注解进行任何更改。我们将使用反射来访问bean的注解。

鉴于我们可以访问Spring ApplicationContext，我们将遵循一系列步骤：

+ 访问BeanFactory。
+ 查找与每个bean关联的BeanDefinition。
+ 检查BeanDefinition的来源是否是AnnotatedTypeMetadata，这意味着我们将能够访问bean的注解。
+ 如果bean有注解，检查所需的注解是否在其中。

让我们创建自己的BeanUtils工具类并在方法中实现此逻辑：

```java
public class BeanUtils {

  public static List<String> getBeansWithAnnotation(GenericApplicationContext applicationContext, Class<?> annotationClass) {
    List<String> result = new ArrayList<>();
    ConfigurableListableBeanFactory factory = applicationContext.getBeanFactory();
    for (String name : factory.getBeanDefinitionNames()) {
      BeanDefinition bd = factory.getBeanDefinition(name);
      if (bd.getSource() instanceof AnnotatedTypeMetadata metadata)
        if (metadata.getAnnotationAttributes(annotationClass.getName()) != null)
          result.add(name);
    }
    return result;
  }
}
```

或者，我们也可以使用Streams来改进代码风格：

```java
public class BeanUtils {

  public static List<String> getBeansWithAnnotation(GenericApplicationContext applicationContext, Class<?> annotationClass) {
    ConfigurableListableBeanFactory factory = applicationContext.getBeanFactory();
    return Arrays.stream(factory.getBeanDefinitionNames())
        .filter(name -> isAnnotated(factory, name, annotationClass))
        .collect(Collectors.toList());
  }

  private static boolean isAnnotated(ConfigurableListableBeanFactory factory, String beanName, Class<?> annotationClass) {
    BeanDefinition beanDefinition = factory.getBeanDefinition(beanName);
    if (beanDefinition.getSource() instanceof AnnotatedTypeMetadata metadata)
      return metadata.getAnnotationAttributes(annotationClass.getName()) != null;
    return false;
  }
}
```

在这些方法中，我们使用了GenericApplicationContext，它是Spring ApplicationContext的实现，不采用特定的bean定义格式。

例如，要访问GenericApplicationContext，我们可以将其注入：

```java

@Component
public class AnnotatedBeansComponent {

  @Autowired
  GenericApplicationContext applicationContext;

  public List<String> getBeansWithAnnotation(Class<?> annotationClass) {
    return BeanUtils.getBeansWithAnnotation(applicationContext, annotationClass);
  }
}
```

## 4. 总结

在本文中，我们讨论了如何列出使用给定注解标注的bean。
我们看到，从Spring Boot 2.2开始，这是通过getBeansWithAnnotation方法完成的。

另一方面，我们演示了一些替代方法来克服此方法先前版本的限制：要么仅在自定义注解上面添加@Qualifier，
要么通过查找bean，使用反射检查它们是否具有注解。