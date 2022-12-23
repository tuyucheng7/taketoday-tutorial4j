## 1. 概述

Spring Boot通过其自动配置功能使配置Spring变得更加容易。

在这个快速教程中，我们介绍org.springframework.boot.autoconfigure和org.springframework.boot.autoconfigure.condition包中的注解。

## 2. @SpringBootApplication

我们使用这个注解来**标记Spring Boot应用程序的主类**：

```java
@SpringBootApplication
class VehicleFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleFactoryApplication.class, args);
    }
}
```

@SpringBootApplication封装了@Configuration、@EnableAutoConfiguration和@ComponentScan注解及其默认属性。

## 3. @EnableAutoConfiguration

@EnableAutoConfiguration，顾名思义，启用自动配置。**这意味着Spring Boot在其类路径中查找自动配置bean并自动应用它们**。

请注意，我们必须将此注解与@Configuration一起使用：

```java
@Configuration
@EnableAutoConfiguration
class VehicleFactoryConfig {
}
```

## 4. 自动配置条件

通常，当我们编写自定义自动配置时，我们希望Spring有条件地使用它们。

我们可以将本节中所讲到的注解放在@Configuration类或@Bean方法上。

在接下来的部分中，我们将只介绍每个Condition背后的基本概念。如需了解更多信息，请访问[本文](../../spring-boot-autoconfiguration/docs/SpringBoot_CustomAutoConfiguration.md)。

### 4.1 @ConditionalOnClass和@ConditionalOnMissingClass

使用这些条件，**只有当注解参数中的类存在/不存在时**，Spring才会使用标记的自动配置bean：

```java
@Configuration
@ConditionalOnClass(DataSource.class)
class MySQLAutoconfiguration {
    // ...
}
```

### 4.2 @ConditionalOnBean和@ConditionalOnMissingBean

**当我们想根据特定的bean存在或不存在来定义条件时**，可以使用这些注解：

```java
class MySQLAutoconfiguration {

    @Bean
    @ConditionalOnBean(name = "dataSource")
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // ...
    }
}
```

### 4.3 @ConditionalOnProperty

使用此注解，**我们可以对属性的值设置条件**：

```java
class MySQLAutoconfiguration {

    @Bean
    @ConditionalOnProperty(name = "usemysql", havingValue = "local")
    DataSource dataSource() {
        // ...
    }
}
```

### 4.4 @ConditionalOnResource

我们可以让Spring仅在**存在特定资源时才使用定义**：

```java
class MySQLAutoconfiguration {

    @ConditionalOnResource(resources = "classpath:mysql.properties")
    Properties additionalProperties() {
        // ...
    }
}
```

### 4.5 @ConditionalOnWebApplication和@ConditionalOnNotWebApplication

使用这些注解，我们可以**根据当前应用程序是否为Web应用程序来创建条件**：

```java
@Configuration
public class WebApplicationSpecificConfiguration {

    @ConditionalOnWebApplication
    HealthCheckController healthCheckController() {
        return new HealthCheckController();
    }
}
```

### 4.6 @ConditionalExpression

我们可以在更复杂的情况下使用这个注解，**当SpEL表达式被计算为true时**，Spring将使用标记的定义：

```java
class MySQLAutoconfiguration {

    @Bean
    @ConditionalOnExpression("${usemysql} && ${mysqlserver == 'local'}")
    DataSource dataSource() {
        // ...
    }
}
```

### 4.7 @Conditional

对于更复杂的条件，我们可以创建一个计算自定义条件的类，告诉Spring使用这个自定义条件：

```java
class MySQLAutoconfiguration {

    @Conditional(HibernateCondition.class)
    Properties additionalProperties() {
        //...
    }
}
```

## 5. 总结

在本文中，我们概述了如何微调自动配置过程并为自定义自动配置bean提供条件。