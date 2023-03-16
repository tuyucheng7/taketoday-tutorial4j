## 1. 概述

在这篇简短的文章中，我们将介绍Spring 5中的@EnabledIf和@DisabledIf注解。

简单地说，**如果满足指定条件，这些注解可以禁用/启用特定测试**。

我们将使用一个简单的测试类来演示这些注解是如何工作的：

```java
@SpringJUnitConfig(Spring5EnabledAnnotationIntegrationTest.Config.class)
class Spring5EnabledAnnotationIntegrationTest {

    @Configuration
    static class Config {
    }
}
```

## 2. @EnabledIf

让我们将这个简单的测试添加到我们的类中:

```java
@EnabledIf("true")
@Test
void givenEnabledIfLiteral_WhenTrue_ThenTestExecuted() {
    assertTrue(true);
}
```

如果我们运行这个测试，它会正常执行。

**但是，如果我们将@EnabledIf注解中提供的字符串替换为“false”，则不会执行**：

<img src="../assets/img.png">

请记住，如果你想静态禁用测试，则有一个专用的[@Disabled](http://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/Disabled.html)注解。

## 3. @EnabledIf使用属性占位符

使用@EnabledIf更实用的方法是使用属性占位符：

```java
@EnabledIf(expression = "${tests.enabled}", loadContext = true)
@Test
void givenEnabledIfExpression_WhenTrue_ThenTestExecuted() {
    assertTrue(true);
}
```

首先，我们需要确保loadContext参数设置为true以便加载Spring上下文。

默认情况下，此参数设置为false，这是为了避免不必要的上下文加载。

## 4. @EnabledIf使用SpEL表达式

最后，**我们可以将注解与Spring表达式语言(SpEL)表达式一起使用**。

例如，我们可以仅在JDK为17时启用测试：

```java
@EnabledIf("#{systemProperties['java.version'].startsWith('17')}")
@Test
void givenEnabledIfSpel_WhenTrue_ThenTestExecuted() {
    assertTrue(true);
}
```

## 5. @DisabledIf

**此注解与@EnabledIf的作用相反**。

例如，我们可以指定在Java的版本为8时禁用测试：

```java
@DisabledIf("#{systemProperties['java.version'].startsWith('8')}")
@Test
void givenDisabledIf_WhenTrue_ThenTestNotExecuted() {
    assertTrue(true);
}
```

## 6. 自定义注解

我们也可以将这些注解作用于自己定义的注解上，这样就可以**实现重用性**。

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@EnabledIf(
      expression = "#{systemProperties['java.version'].startsWith('1.8')}",
      reason = "Enabled on Java 8"
)
public @interface EnabledOnJava8 {
}
```

然后我们可以直接使用自定义的注解：

```java
@EnabledOnJava17
@Test
void givenEnabledOnJava8_WhenTrue_ThenTestExecuted() {
    assertTrue(true);
}
```

## 7. 总结

在这篇简短的文章中，我们通过几个示例介绍了在使用SpringExtension的JUnit 5测试中使用@EnabledIf和@DisabledIf注解。