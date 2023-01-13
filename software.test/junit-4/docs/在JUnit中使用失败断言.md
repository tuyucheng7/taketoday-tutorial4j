## 1. 概述

在本教程中，我们介绍如何在常见的测试场景中使用JUnit失败断言。

## 2. 使用fail断言

失败断言未通过无条件抛出AssertionError的测试。

**在编写单元测试时，我们可以使用fail在所需的测试条件下显式创建失败**。下面我们介绍这可能会有所帮助的一些情况。

### 2.1 不完整的测试

当测试不完整或尚未实现时，我们可以使测试失败：

```java
@Test
public void incompleteTest() {
    fail("Not yet implemented");
}
```

### 2.2 预期异常

当我们认为会发生异常时，我们可以使测试失败：

```java
@Test
public void expectedException() {
    try {
        methodThrowsException();
        fail("Expected exception was not thrown");
    } catch (Exception e) {
        assertNotNull(e);
    }
}
```

### 2.3 意外异常

当预期不会抛出异常时测试失败是另一种选择：

```java
@Test
public void unexpectedException() {
    try {
        safeMethod();
        // more testing code
    } catch (Exception e) {
        fail("Unexpected exception was thrown");
    }
}
```

### 2.4 测试条件

当结果不满足某些所需条件时，我们可以调用fail() ：

```java
@Test
public void testingCondition() {
    int result = randomInteger();
    if(result > Integer.MAX_VALUE) {
        fail("Result cannot exceed integer max value");
    }
    // more testing code
}
```

### 2.5. 返回之前

最后，当代码没有按预期返回/中断时，我们可以使测试失败：

```java
@Test
public void returnBefore() {
    int value = randomInteger();
    for (int i = 0; i < 5; i++) {
        // returns when (value + i) is an even number
        if ((i + value) % 2 == 0) {
            return;
        }
    }
    fail("Should have returned before");
}
```

## 3. JUnit 5与JUnit 4

JUnit 4中的所有断言都是org.junit.Assert类的一部分。对于JUnit 5，这些已移至org.junit.jupiter.api.Assertions。

当我们在JUnit 5中调用fail并得到异常时，我们会收到一个AssertionFailedError而不是JUnit 4中的AssertionError。

除了fail()和fail(String message)，JUnit 5还包括一些其他有用的重载：

-   fail(Throwable cause)
-   fail(String message, Throwable cause)
-   fail(Supplier<String\> messageSupplier)

**此外，所有形式的fail在JUnit 5中都被声明为public static <V\> V fail()。泛型返回类型V允许将这些方法用作lambda表达式中的单语句**：

```java
Stream.of().map(entry -> fail("should not be called"));
```

## 4. 总结

在本文中，我们介绍了JUnit中失败断言的一些实际用例。[有关JUnit 4和JUnit 5]()中所有可用的断言，请参阅[JUnit断言]()。我们还强调了JUnit 4和JUnit 5之间的主要区别，以及fail方法的一些有用的增强功能。