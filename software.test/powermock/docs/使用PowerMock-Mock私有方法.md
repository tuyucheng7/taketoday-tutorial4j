## 1. 概述

单元测试的主要挑战之一是mock私有方法。

在本教程中，我们将学习如何通过使用JUnit和TestNG支持的[PowerMock](https://github.com/powermock/powermock)库来实现这一点。

**PowerMock与EasyMock和Mockito等mock框架集成，旨在为这些框架添加额外的功能-例如mock私有方法、final类和final方法等**。

它依赖于字节码操作和一个完全独立的类加载器来做到这一点。

## 2. Maven依赖

首先，让我们将PowerMock与Mockito和JUnit一起使用所需的依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-module-junit4</artifactId>
    <version>2.21.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-api-mockito2</artifactId>
    <version>2.0.7</version>
    <scope>test</scope>
</dependency>
```

可以在[此处](https://central.sonatype.com/artifact/org.powermock/powermock-module-junit4/2.0.9)和[此处](https://central.sonatype.com/artifact/org.powermock/powermock-api-mockito2/2.0.9)查看最新版本。

## 3. 示例

让我们从LuckyNumberGenerator的示例开始。此类有一个用于生成幸运数字的公共方法：

```java
class LuckyNumberGenerator {

    public int getLuckyNumber(String name) {
        saveIntoDatabase(name);

        if (name == null) {
            return getDefaultLuckyNumber();
        }

        return getComputedLuckyNumber(name.length());
    }

    private void saveIntoDatabase(String name) {
        // Save the name into the database
    }

    private int getDefaultLuckyNumber() {
        return 100;
    }

    private int getComputedLuckyNumber(int length) {
        return length < 5 ? 5 : 10000;
    }
}
```

## 4. Mock私有方法

为了对该方法进行详尽的单元测试，我们需要mock其中调用的私有方法。

### 4.1 没有参数但有返回值的方法

作为一个简单的例子，让我们mock一个没有参数的私有方法的行为，并强制它返回所需的值：

```java
LuckyNumberGenerator mock = spy(new LuckyNumberGenerator());

when(mock, "getDefaultLuckyNumber").thenReturn(300);
```

在这种情况下，我们mock私有方法getDefaultLuckyNumber并使其返回值300。

### 4.2 带参数和返回值的方法

接下来，让我们mock一个带有参数的私有方法的行为，并强制它返回所需的值：

```java
LuckyNumberGenerator mock = spy(new LuckyNumberGenerator());

doReturn(1).when(mock, "getComputedLuckyNumber", ArgumentMatchers.anyInt());
```

在这种情况下，我们mock私有方法getComputedLuckyNumber并使其返回1。

请注意，我们并不关心getComputedLuckyNumber方法的输入参数。因此这里使用ArgumentMatchers.anyInt()作为通配符。

### 4.3 验证方法的调用

最后我们使用PowerMock来验证私有方法的调用：

```java
LuckyNumberGenerator mock = spy(new LuckyNumberGenerator());
int result = mock.getLuckyNumber("Tyranosorous");

verifyPrivate(mock).invoke("saveIntoDatabase", ArgumentMatchers.anyString());
```

## 5. 注意点

最后，虽然可以使用PowerMock测试私有方法，但**我们在使用这种技术时必须格外小心**。

鉴于我们测试的目的是验证类的行为，我们应该避免在单元测试期间更改类的内部行为。

**mock技术应该应用于类的外部依赖，而不是类本身**。

如果对私有方法的mock对于测试我们的类来说是必不可少的，那么这通常表明设计不好。

## 6. 总结

在这篇简短的文章中，我们展示了如何使用PowerMock扩展Mockito的功能，以mock和验证被测试类中的私有方法。