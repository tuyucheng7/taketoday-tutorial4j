## 1. 概述

在本文中，我们将快速回顾一下JUnit的@Test注解。此注解为执行单元和回归测试提供了强大的工具。

## 2. maven配置

要使用最新版本的JUnit 5，我们需要添加以下Maven依赖项：

```xml

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

我们使用test范围是因为我们不希望Maven在最终构建中包含此依赖项。

由于surefire插件本身并不完全支持JUnit 5，我们还需要添加一个提供程序，它告诉Maven在哪里可以找到我们的测试：

```xml

<plugins>
    <plugin>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.0.0-M3</version>
        <dependencies>
            <dependency>
                <groupId>org.junit.platform</groupId>
                <artifactId>junit-platform-surefire-provider</artifactId>
                <version>1.3.2</version>
            </dependency>
        </dependencies>
    </plugin>
</plugins>
```

## 3. 测试中的方法

首先，让我们构建一个简单的方法，我们将在测试场景中使用它来演示@Test注解的功能：

```java
public class NumbersBean {

    public boolean isNumberEven(int number) {
        return number % 2 == 0;
    }
}
```

如果传递的参数是偶数，则此方法应返回true，否则返回false。现在，让我们看看它是否按预期的方式工作。

## 4. 测试

在我们的示例中，我们要特别检查两种情况：

+ 当给定偶数时，该方法应返回true。
+ 当给定奇数时，该方法应返回false。

这意味着实现代码将使用不同的参数调用我们的isNumberEven()方法，并检查结果是否符合我们的预期。

**为了使测试能够被识别，我们添加@Test注解。我们可以在一个类中编写很多测试方法，但最好只将相关的测试放在一起。
还要注意，一个测试方法不能是私有的，也不能返回一个值，否则它会被忽略**。

```java
class NumbersBeanUnitTests {
    private final NumbersBean bean = new NumbersBean();

    @Test
    void givenEvenNumber_whenCheckingIsNumberEven_thenTure() {
        boolean result = bean.isNumberEven(8);
        assertTrue(result);
    }

    @Test
    void givenOddNumber_whenCheckingIsNumberEven_thenFalse() {
        boolean result = bean.isNumberEven(1);
        assertFalse(result);
    }
}
```

如果我们现在运行Maven build，**surefire插件将遍历src/test/java下的类中的所有带有@Test注解的方法并执行它们**，
如果发生任何测试失败，则会导致构建失败。

如果你使用JUnit 4，请注意，在此版本中，注解不接收任何参数。要检查超时或抛出的异常，我们可以使用断言来代替：

```java
class NumbersBeanUnitTests {

    @Test
    void givenLowerThanTenNumber_whenCheckingIsNumberEven_thenResultUnderTenMillis() {
        assertTimeout(Duration.ofMillis(10), () -> bean.isNumberEven(3));
    }

    @Test
    void givenNull_whenCheckingIsNumberEven_thenNullPointerException() {
        assertThrows(NullPointerException.class, () -> bean.isNumberEven(null));
    }
}
```

## 5. 总结

在这个教程中，我们演示了如何使用@Test注解实现和运行一个简单的JUnit测试。