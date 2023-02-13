## 1. 概述

在Spring 3.0之前，XML是定义和配置bean的唯一方法。Spring 3.0引入了[JavaConfig](https://docs.spring.io/spring-javaconfig/docs/1.0.0.M4/reference/html/)，允许我们使用Java类来配置bean。但是，XML配置文件至今仍在使用。

在本教程中，我们将讨论**如何将XML配置集成到Spring Boot中**。

## 2. @ImportResource注解

@ImportResource注解允许我们导入一个或多个包含bean定义的资源。

假设我们有一个beans.xml文件，其中包含bean的定义：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="cn.tuyucheng.taketoday.springbootxml.Pojo">
        <property name="field" value="sample-value"/>
    </bean>
</beans>
```

要在Spring Boot应用程序中使用它，我们可以**使用@ImportResource注解**，告诉它在哪里可以找到配置文件：

```java
@Configuration
@ImportResource("classpath:beans.xml")
public class SpringBootXmlApplication implements CommandLineRunner {

    @Autowired private Pojo pojo;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootXmlApplication.class, args);
    }
}
```

在这种情况下，Pojo实例将注入beans.xml中定义的bean。

## 3. 访问XML配置中的属性

假设我们要在XML中使用application.properties文件中声明的属性：

```properties
sample=string loaded from properties!
```

让我们更新beans.xml中的Pojo定义，以包含sample属性：

```xml
<bean class="cn.tuyucheng.taketoday.springbootxml.Pojo">
    <property name="field" value="${sample}"/>
</bean>
```

接下来，让我们验证该属性是否正确引入：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootXmlApplication.class)
class SpringBootXmlApplicationIntegrationTest {

    @Autowired
    private Pojo pojo;
    @Value("${sample}")
    private String sample;

    @Test
    void whenCallingGetter_thenPrintingProperty() {
        assertThat(pojo.getField())
              .isNotBlank()
              .isEqualTo(sample);
    }
}
```

不幸的是，此测试将失败，因为**默认情况下XML配置文件无法解析占位符**。但是，我们可以通过包含@EnableAutoConfiguration注解来解决这个问题：

```java
@Configuration
@EnableAutoConfiguration
@ImportResource("classpath:beans.xml")
public class SpringBootXmlApplication implements CommandLineRunner {
    // ...
}
```

此注解启用自动配置并尝试配置bean。

## 4. 推荐方法

我们仍然可以继续使用XML配置文件。但出于几个原因，我们也可以考虑将所有配置移至Java配置类。首先，**在Java中配置bean是类型安全的**，一旦出现任何Bean类型配置错误，我们可以在编译期就发现问题。此外，**XML配置会变得非常臃肿**，从而难以维护。

## 5. 总结

在本文中，我们了解了如何使用XML配置文件在Spring Boot应用程序中定义我们的bean。