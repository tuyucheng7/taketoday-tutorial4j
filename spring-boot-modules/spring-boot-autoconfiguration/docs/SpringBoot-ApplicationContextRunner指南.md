## 1. 概述

众所周知，自动配置是Spring Boot的关键特性之一，但测试自动配置场景可能会很棘手。

**在本文中，我们介绍ApplicationContextRunner如何简化自动配置测试**。

## 2. 测试自动配置场景

**ApplicationContextRunner是一个工具类，它运行ApplicationContext并提供AssertJ风格的断言**。它最好**用作测试类中的一个字段**，用于共享配置，之后我们会在每个测试中进行自定义：

```java
private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();
```

让我们通过测试几个案例来演示它的用法。

### 2.1 测试类条件

在本节中，我们将**测试一些使用@ConditionalOnClass和@ConditionalOnMissingClass注解的自动配置类**：

```java
class ConditionalOnClassIntegrationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Configuration
    @ConditionalOnClass(ConditionalOnClassIntegrationTest.class)
    protected static class ConditionalOnClassConfiguration {
        @Bean
        public String created() {
            return "This is created when ConditionalOnClassIntegrationTest is present on the classpath";
        }
    }

    @Configuration
    @ConditionalOnMissingClass("cn.tuyucheng.taketoday.autoconfiguration.ConditionalOnClassIntegrationTest")
    protected static class ConditionalOnMissingClassConfiguration {
        @Bean
        public String missed() {
            return "This is missed when ConditionalOnClassIntegrationTest is present on the classpath";
        }
    }
}
```

我们想测试自动配置是否正确实例化或跳过给定预期条件的created和missed的bean。

ApplicationContextRunner为我们提供了withUserConfiguration方法，我们可以根据需要提供自动配置来为每个测试自定义ApplicationContext。

run方法将ContextConsumer作为参数，将断言应用于上下文。当测试退出时，ApplicationContext将自动关闭：

```java
class ConditionalOnClassIntegrationTest {

    @Test
    void whenDependentClassIsPresent_thenBeanCreated() {
        this.contextRunner.withUserConfiguration(ConditionalOnClassConfiguration.class)
              .run(context -> {
                  assertThat(context).hasBean("created");
                  assertThat(context.getBean("created")).isEqualTo("This is created when ConditionalOnClassIntegrationTest is present on the classpath");
              });
    }

    @Test
    void whenDependentClassIsPresent_thenBeanMissing() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingClassConfiguration.class)
              .run(context -> assertThat(context).doesNotHaveBean("missed"));
    }
}
```

通过前面的示例，我们看到了测试某个类存在于类路径中的场景的简单性。**但是，当类路径中不存在该类时，我们将如何测试相反的情况呢**？

这就是FilteredClassLoader发挥作用的地方，它用于在运行时过滤类路径上的指定类：

```java
class ConditionalOnClassIntegrationTest {

    @Test
    void whenDependentClassIsNotPresent_thenBeanMissing() {
        this.contextRunner.withUserConfiguration(ConditionalOnClassConfiguration.class)
              .withClassLoader(new FilteredClassLoader(ConditionalOnClassIntegrationTest.class))
              .run(context -> {
                  assertThat(context).doesNotHaveBean("created");
                  assertThat(context).doesNotHaveBean(ConditionalOnClassIntegrationTest.class);
              });
    }

    @Test
    void whenDependentClassIsNotPresent_thenBeanCreated() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingClassConfiguration.class)
              .withClassLoader(new FilteredClassLoader(ConditionalOnClassIntegrationTest.class))
              .run(context -> {
                  assertThat(context).hasBean("missed");
                  assertThat(context).getBean("missed").isEqualTo("This is missed when ConditionalOnClassIntegrationTest is present on the classpath");
                  assertThat(context).doesNotHaveBean(ConditionalOnClassIntegrationTest.class);
              });
    }
}
```

### 2.2 测试Bean条件

我们刚刚演示了测试@ConditionalOnClass和@ConditionalOnMissingClass注解，现在**让我们看看使用@ConditionalOnBean和@ConditionalOnMissingBean注解时的情况**。

首先，我们同样需要一些自动配置类：

```java
@Configuration
protected static class BasicConfiguration {
    @Bean
    public String created() {
        return "This is always created";
    }
}

@Configuration
@ConditionalOnBean(name = "created")
protected static class ConditionalOnBeanConfiguration {
    @Bean
    public String createOnBean() {
        return "This is created when bean (name=created) is present";
    }
}

@Configuration
@ConditionalOnMissingBean(name = "created")
protected static class ConditionalOnMissingBeanConfiguration {
    @Bean
    public String createOnMissingBean() {
        return "This is created when bean (name=created) is missing";
    }
}
```

然后，我们可以像上一节一样调用withUserConfiguration方法，并传递我们的自定义配置类来测试自动配置是否在不同条件下适当地实例化或跳过createOnBean或createOnMissingBean bean：

```java
class ConditionalOnBeanIntegrationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void whenDependentBeanIsPresent_thenConditionalBeanCreated() {
        this.contextRunner.withUserConfiguration(BasicConfiguration.class, ConditionalOnBeanConfiguration.class)
              .run((context) -> {
                  assertThat(context).hasBean("created");
                  assertThat(context).getBean("created")
                        .isEqualTo("This is always created");
                  assertThat(context).hasBean("createOnBean");
                  assertThat(context).getBean("createOnBean")
                        .isEqualTo("This is created when bean (name=created) is present");
              });
    }

    @Test
    void whenDependentBeanIsPresent_thenConditionalMissingBeanIgnored() {
        this.contextRunner.withUserConfiguration(BasicConfiguration.class, ConditionalOnMissingBeanConfiguration.class)
              .run((context) -> {
                  assertThat(context).hasBean("created");
                  assertThat(context).getBean("created")
                        .isEqualTo("This is always created");
                  assertThat(context).doesNotHaveBean("createOnMissingBean");
              });
    }

    @Test
    void whenDependentBeanIsNotPresent_thenConditionalMissingBeanCreated() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanConfiguration.class)
              .run((context) -> {
                  assertThat(context).hasBean("createOnMissingBean");
                  assertThat(context).getBean("createOnMissingBean")
                        .isEqualTo("This is created when bean (name=created) is missing");
                  assertThat(context).doesNotHaveBean("created");
              });
    }
}
```

### 2.3 测试属性条件

在本节中，我们**测试使用@ConditionalOnProperty注解的自动配置类**。

首先，我们需要一个用于此测试的属性：

```properties
#ConditionalOnPropertyTest.properties
cn.tuyucheng.taketoday.service=custom
```

之后，我们编写嵌套的自动配置类来基于前面的属性创建bean：

```java
@Configuration
@TestPropertySource("classpath:ConditionalOnPropertyTest.properties")
protected static class SimpleServiceConfiguration {

    @Bean
    @ConditionalOnProperty(name = "cn.tuyucheng.taketoday.service", havingValue = "default")
    @ConditionalOnMissingBean
    public DefaultService defaultService() {
        return new DefaultService();
    }

    @Bean
    @ConditionalOnProperty(name = "cn.tuyucheng.taketoday.service", havingValue = "custom")
    @ConditionalOnMissingBean
    public CustomService customService() {
        return new CustomService();
    }
}
```

现在，我们调用withPropertyValues方法来重写每个测试中的属性值：

```java
public class ConditionalOnPropertyIntegrationTest {

    @Test
    void whenGivenCustomPropertyValue_thenCustomServiceCreated() {
        this.contextRunner.withPropertyValues("cn.tuyucheng.taketoday.service=custom")
              .withUserConfiguration(SimpleServiceConfiguration.class)
              .run(context -> {
                  assertThat(context).hasBean("customService");
                  SimpleService simpleService = context.getBean(CustomService.class);
                  assertThat(simpleService.serve()).isEqualTo("Custom Service");
                  assertThat(context).doesNotHaveBean("defaultService");
              });
    }

    @Test
    void whenGivenDefaultPropertyValue_thenDefaultServiceCreated() {
        this.contextRunner.withPropertyValues("cn.tuyucheng.taketoday.service=default")
              .withUserConfiguration(SimpleServiceConfiguration.class)
              .run(context -> {
                  assertThat(context).hasBean("defaultService");
                  SimpleService simpleService = context.getBean(DefaultService.class);
                  assertThat(simpleService.serve()).isEqualTo("Default Service");
                  assertThat(context).doesNotHaveBean("customService");
              });
    }
}
```

## 3. 总结

总而言之，本文只是展示了如何使用ApplicationContextRunner来运行具有自定义和应用断言的ApplicationContext。我们在这里介绍了最常用的场景，而不是详细列出如何自定义ApplicationContext。

同时，请记住ApplicationContextRunner适用于非Web应用程序，因此还有WebApplicationContextRunner用于基于Servlet的Web应用程序，和ReactiveWebApplicationContextRunner用于响应式Web应用程序。