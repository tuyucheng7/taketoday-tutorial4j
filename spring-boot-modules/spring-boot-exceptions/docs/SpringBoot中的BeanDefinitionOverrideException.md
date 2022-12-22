## 1. 简介

Spring Boot 2.1升级让人们意外地发现了BeanDefinitionOverrideException，它可能会使开发人员感到困惑，并使他们想知道Spring中的bean覆盖行为发生了什么。

在本教程中，我们将解决这个问题，并了解如何最好地解决它。

## 2. Maven依赖

对于我们的示例Maven项目，我们需要添加[Spring Boot Starter](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter")依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.3.3.RELEASE</version>
</dependency>
```

## 3. Bean覆盖

Spring bean在ApplicationContext中由它们的名称标识。

因此，**当我们在ApplicationContext中定义一个与另一个bean同名的bean时，bean覆盖是一种默认行为**。它的工作原理是在名称冲突的情况下简单地替换以前的bean。

**从Spring 5.1开始，引入了[BeanDefinitionOverrideException](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/support/BeanDefinitionOverrideException.html)以允许开发人员自动抛出异常以防止任何意外的bean覆盖**。默认情况下，原始行为仍然可用，这允许覆盖bean。

## 4. Spring Boot 2.1的配置变更

[Spring Boot 2.1默认禁用bean覆盖](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.1-Release-Notes#bean-overriding)作为一种防御方法，主要目的是提前**注意到重复的bean名称，以防止意外覆盖bean**。

因此，如果我们的Spring Boot应用依赖于bean覆盖，那么在我们将Spring Boot版本升级到2.1及之后的版本后，很有可能会遇到BeanDefinitionOverrideException。

在接下来的部分中，我们重现一个会发生BeanDefinitionOverrideException的示例，然后我们讨论一些解决方案。

## 5. 识别冲突的Bean

让我们创建两个不同的Spring配置，每个配置都有一个testBean()方法，以产生BeanDefinitionOverrideException：

```java
@Configuration
public class TestConfiguration1 {

    class TestBean1 {
        private String name;

        // standard getters and setters
    }

    @Bean
    public TestBean1 testBean(){
        return new TestBean1();
    }
}
```

```java
@Configuration
public class TestConfiguration2 {

    class TestBean2 {
        private String name;

        // standard getters and setters
    }

    @Bean
    public TestBean2 testBean(){
        return new TestBean2();
    }
}
```

接下来，创建我们的Spring Boot测试类：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfiguration1.class, TestConfiguration2.class})
class SpringBootBeanDefinitionOverrideExceptionIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void whenBeanOverridingAllowed_thenTestBean2OverridesTestBean1() {
        Object testBean = applicationContext.getBean("testBean");

        assertThat(testBean.getClass()).isEqualTo(TestConfiguration2.TestBean2.class);
    }
}
```

运行测试会产生BeanDefinitionOverrideException，但是，该异常为我们提供了一些有用的信息：

```shell
Invalid bean definition with name 'testBean' defined in ... 
... cn.tuyucheng.taketoday.beandefinitionoverrideexception.TestConfiguration2 ...
Cannot register bean definition [ ... defined in ... 
... cn.tuyucheng.taketoday.beandefinitionoverrideexception.TestConfiguration2] for bean 'testBean' ...
There is already [ ... defined in ...
... cn.tuyucheng.taketoday.beandefinitionoverrideexception.TestConfiguration1] bound.
```

请注意，该异常揭示了两条重要信息。

第一个是冲突的bean名称testBean：

```java
Invalid bean definition with name 'testBean' ...
```

第二个向我们显示了受影响的配置的完整路径：

```java
... cn.tuyucheng.taketoday.beandefinitionoverrideexception.TestConfiguration2 ...
... cn.tuyucheng.taketoday.beandefinitionoverrideexception.TestConfiguration1 ...
```

结果我们可以看到两个不同的bean被识别为testBean，从而导致冲突。此外，bean包含在配置类TestConfiguration1和TestConfiguration2中。

## 6. 可能的解决方案

根据我们的配置，Spring Bean具有默认名称，除非我们明确设置它们。

因此，第一个可能的解决方案是重命名我们的bean，在Spring中有一些常见的方法来设置bean名称。

### 6.1 更改方法名称

默认情况下，**Spring将带注解的方法的名称作为bean名称**。

因此，如果我们在配置类中定义了beans，就像我们的示例一样，那么只需更改方法名称就可以防止BeanDefinitionOverrideException：

```java
@Bean
public TestBean1 testBean1() {
    return new TestBean1();
}
```

```java
@Bean
public TestBean2 testBean2() {
    return new TestBean2();
}
```

### 6.2 @Bean注解

Spring的@Bean注解是一种非常常见的定义bean的方式。

所以另一种选择是设置@Bean注解的name属性：

```java
@Bean("testBean1")
public TestBean1 testBean() {
    return new TestBean1();
}
```

```java
@Bean("testBean2")
public TestBean1 testBean() {
    return new TestBean2();
}
```

### 6.3 原型注解

另一种定义bean的方法是使用[构造型注解]()，启用Spring的@ComponentScan功能后，我们可以使用@Component注解在类级别定义bean名称：

```java
@Component("testBean1")
class TestBean1 {

    private String name;

    // standard getters and setters
}
```

```java
@Component("testBean2")
class TestBean2 {

    private String name;

    // standard getters and setters
}
```

### 6.4 来自第三方库的Bean

在某些情况下，**可能会遇到由来自第三方Spring支持的库的bean引起的名称冲突**。发生这种情况时，我们应该尝试确定哪个冲突bean属于我们的应用程序，以确定我们是否可以使用上述任何解决方案。但是，如果我们无法更改任何bean定义，那么配置Spring Boot以允许bean覆盖可能是一种解决方法。

要启用bean覆盖，我们需要在application.properties文件中将spring.main.allow-bean-definition-overriding属性设置为true：

```properties
spring.main.allow-bean-definition-overriding=true
```

通过这样做，我们告诉Spring Boot允许bean重写而无需对bean定义进行任何更改。

最后一点，我们应该意识到**很难猜测哪个bean具有优先级，因为bean创建顺序是由依赖关系决定的，而依赖关系主要在运行时受到影响**。因此，允许覆盖bean会产生意想不到的行为，除非我们足够了解bean的依赖层次结构。

## 7. 总结

在本文中，我们解释了BeanDefinitionOverrideException在Spring中的含义，为什么它突然出现，以及在Spring Boot 2.1升级后如何解决。