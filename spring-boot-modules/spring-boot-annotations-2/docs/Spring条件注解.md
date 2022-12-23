## 1. 概述

在本教程中，我们介绍@Conditional注解。它用于根据定义的条件指示给定组件是否有资格注册。

我们演示如何使用预定义的条件注解，将它们与不同的条件结合起来，以及创建我们的自定义、基于条件的注解。

## 2. 声明条件

最常见的用法是包含或排除整个配置类：

```java
@Configuration
@Conditional(IsDevEnvCondition.class)
public class DevEnvLoggingConfiguration {

}

public class IsDevEnvCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "dev".equals(System.getProperty("env"));
    }
}
```

或者包含或排除一个单一的bean：

```java
@Configuration
public class DevEnvLoggingConfiguration {

    @Bean
    @Conditional(IsDevEnvCondition.class)
    LoggingService loggingService() {
        return new LoggingService();
    }
}
```

通过这样做，我们可以将应用程序的行为基于给定的条件，例如环境类型或客户的特定需求。在上面的例子中，我们只为Dev环境初始化了额外的LoggingService。

另一种使组件具有条件的方法是将注解直接放置在组件类上：

```java
@Service
@Conditional(IsDevEnvCondition.class)
class LoggingService {
    // ...
}
```

我们可以将上面的例子应用于使用@Component、@Service、@Repository或@Controller注解声明的任何bean。

## 3. 预定义的条件注解

Spring附带有一组预定义的条件注解。首先，让我们看看如何根据配置属性值包含或排除组件：

```java
@Service
@ConditionalOnProperty(
        value = "logging.enabled",
        havingValue = "true",
        matchIfMissing = true
)
class LoggingService {
    // ...
}
```

第一个属性value告诉我们要检查的配置属性，第二个属性haveValue定义了这个条件所需的值，最后，matchIfMissing属性告诉Spring如果参数丢失，条件是否应该匹配。

同样，我们可以将条件基于表达式：

```java
@Service
@ConditionalOnExpression("${logging.enabled:true} and '${logging.level}'.equals('DEBUG')")
class LoggingService {
    // ...
}
```

此时，只有当logging.enabled配置属性设置为true并且logging.level设置为DEBUG时，Spring才会创建LoggingService bean。

我们可以应用的另一个条件是检查是否创建了给定的bean：

```java
@Service
@ConditionalOnBean(CustomLoggingConfiguration.class)
class LoggingService {
    // ...
}
```

或者给定的类是否存在于类路径中：

```java
@Service
@ConditionalOnClass(CustomLogger.class)
class LoggingService {
    // ...
}
```

我们可以通过应用@ConditionalOnMissingBean或@ConditionalOnMissingClass注解来实现相反的行为。

此外，我们可以将组件注册依赖于给定的Java版本：

```java
@Service
@ConditionalOnJava(JavaVersion.EIGHT)
class LoggingService {
    // ...
}
```

在上面的例子中，只有在运行环境是Java 8时才会创建LoggingService bean。

最后，我们可以使用@ConditionalOnWarDeployment注解仅在war包中启用bean：

```java
@Configuration
@ConditionalOnWarDeployment
class AdditionalWebConfiguration {
    // ...
}
```

请注意，对于具有嵌入式服务器的应用程序，此条件将返回false。

## 4. 自定义条件

Spring允许我们通过创建自定义条件来自定义@Conditional注解的行为。为此，我们只需要实现Condition接口：

```java
class Java8Condition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return JavaVersion.getJavaVersion().equals(JavaVersion.EIGHT);
    }
}
```

matches方法告诉Spring条件是否已经通过。它有两个参数，分别为我们提供有关bean将初始化的上下文和使用的@Conditional注解的元数据的信息。

在我们的示例中，我们只是检查Java版本是否为8。

然后，我们应该将Java8Condition作为属性放在@Conditional注解中：

```java
@Service
@Conditional(Java8Condition.class)
public class Java8DependedService {
    // ...
}
```

这样，只有当Java8Condition类中的条件匹配时，才会创建Java8DependentService bean。

## 5. 组合条件

对于更复杂的解决方案，我们可以使用OR或AND逻辑运算符对条件注解进行分组。

要应用OR运算符，我们需要创建一个继承AnyNestedCondition类的自定义条件。在它内部，我们需要为每个条件创建一个空的静态类，并使用适当的@Conditional实现对其进行标注。

例如，让我们创建一个需要Java 8或Java 9的条件：

```java
public class Java8OrJava9 extends AnyNestedCondition {

    Java8OrJava9() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @Conditional(Java8Condition.class)
    static class Java8 {
    }

    @Conditional(Java9Condition.class)
    static class Java9 {
    }
}
```

另一方面，AND运算符要简单得多，我们可以简单地对条件进行分组：

```java
@Service
@Conditional({IsWindowsCondition.class, Java8Condition.class})
@ConditionalOnJava(JavaVersion.EIGHT)
public class LoggingService {
    // ...
}
```

在上面的示例中，只有当IsWindowsCondition和Java8Condition都匹配时，才会创建LoggingService bean。

## 6. 总结

在本文中，我们学习了如何使用和自定义条件注解。