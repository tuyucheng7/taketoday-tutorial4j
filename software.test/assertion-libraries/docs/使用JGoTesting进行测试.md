## 1. 概述

**[JGoTesting](https://gitlab.com/tastapod/jgotesting)是一个JUnit兼容的测试框架，灵感来自Go的测试包**。

在本文中，我们介绍JGoTesting框架的关键特性并通过案例来展示它的功能。

## 2. Maven依赖

首先，我们将jgottesting依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>org.jgotesting</groupId>
    <artifactId>jgotesting</artifactId>
    <version>0.12</version>
</dependency>
```

## 3. 简介

JGoTesting允许我们编写与JUnit兼容的测试，**对于JGoTesting提供的每种断言方法，在JUnit中都有一个具有相同签名的方法**，因此实现这个库非常简单。

然而，**与JUnit不同的是，当一个断言失败时，JGoTesting不会停止测试的执行**。相反，失败被记录为一个事件，并且只有在所有断言都已执行时才呈现给我们。

## 4. JGoTesting实践

### 4.1 入门

为了编写我们的测试，我们首先静态导入JGoTesting的断言方法：

```java
import static org.jgotesting.Assert.; // same methods as JUnit
import static org.jgotesting.Check.; // aliases starting with "check"
import static org.jgotesting.Testing.;
```

该库需要一个用@Rule注解标记的强制性JGoTestRule实例，这表明类中的所有测试都将由JGoTesting管理。

让我们创建一个声明此类规则的类：

```java
public class JGoTestingUnitTest {
 
    @Rule
    public final JGoTestRule test = new JGoTestRule();
    
    //...
}
```

### 4.2 编写测试

JGoTesting提供了两组断言方法来编写我们的测试，第一组中的方法名称以assert开头，并且与JUnit兼容，其他方法的名称以check开头。这两组方法的行为相同，库提供了它们之间的一一对应关系。

以下是一个使用两种方法测试一个数字是否等于另一个数字的示例：

```java
@Test
public void whenComparingIntegers_thenEqual() {
    int anInt = 10;

    assertEquals(anInt, 10);
    checkEquals(anInt, 10);
}
```

API的其余部分是不言自明的，因此我们不再赘述。对于接下来的所有示例，我们只关注这两种方法的check版本。

### 4.3 失败事件和消息

当测试检查失败时，JGoTesting会记录失败，以便测试用例继续执行；**测试结束后，报告失败**。

下面是一个简单的例子：

```java
@Test
public void whenComparingStrings_thenMultipleFailingAssertions() {
    String aString = "The test string";
    String anotherString = "The test String";

    checkEquals("Strings are not equal!", aString, equalTo(anotherString));
    checkTrue("String is longer than one character", aString.length() == 1);
    checkTrue("A failing message", aString.length() == 2);
}
```

执行测试后，我们得到以下输出：

```shell
org.junit.ComparisonFailure: Strings are not equal!
  expected:<[the test s]tring> but was:<[The Test S]tring>
// ...
java.lang.AssertionError: String is longer than one character
// ...
java.lang.AssertionError: Strings are not the same
  expected the same:<the test string> was not:<The Test String>
```

除了在每个方法中传递失败消息外，我们还可以记录它们，以便它们仅在测试至少出现一次失败时出现。

例如以下的的测试方法：

```java
@Test
public void whenComparingNumbers_thenLoggedMessage() {
    log("There was something wrong when comparing numbers");

    int anInt = 10;
    int anotherInt = 10;

    checkEquals(anInt, 10);
    checkTrue("First number should be bigger", 10 > anotherInt);
    checkSame(anInt, anotherInt);
}
```

测试执行后，我们得到以下输出：

```plaintext
org.jgotesting.events.LogMessage: There was something wrong
  when comparing numbers
// ...
java.lang.AssertionError: First number should be bigger
```

请注意，除了可以将消息格式化为String.format()方法的logf()之外，我们还可以使用logIf()和logUnless()方法根据条件表达式记录消息。

### 4.4 中断测试

JGoTesting提供了几种在测试用例未能通过给定前提条件时终止测试用例的方法。

以下是一个由于所需文件不存在而提前结束的测试示例：

```java
@Test
public void givenFile_whenDoesnotExists_thenTerminated() throws Exception {
    File aFile = new File("a_dummy_file.txt");

    terminateIf(aFile.exists(), is(false));

    // this doesn't get executed
    checkEquals(aFile.getName(), "a_dummy_file.txt");
}
```

请注意，我们还可以使用terminate()和terminateUnless()方法来中断测试执行。

### 4.5 链式API

**JGoTestRule类还有一个流式的API，我们可以使用它将检查链接在一起**。

下面的例子使用JGoTestRule实例将对String对象的多个检查链接在一起：

```java
@Test
public void whenComparingStrings_thenMultipleAssertions() {
    String aString = "This is a string";
    String anotherString = "This Is a String";

    test.check(aString, equalToIgnoringCase(anotherString))
        .check(aString.length() == 16)
        .check(aString.startsWith("This"));
}
```

### 4.6 自定义检查

除了布尔表达式和Matcher实例之外，**JGoTestRule的方法还可以接收自定义Checker对象来进行检查**，这是一个可以使用lambda表达式实现的单一抽象方法接口。

下面是一个使用上述接口验证字符串是否匹配特定正则表达式的例子：

```java
@Test
public void givenChecker_whenComparingStrings_thenEqual() throws Exception {
    Checker<String> aChecker = s -> s.matches("d+");

    String aString = "1235";

    test.check(aString, aChecker);
}
```

## 5. 总结

在这个快速教程中，我们介绍了JGoTesting提供的编写测试的功能，演示了与JUnit兼容的断言方法及其对应的check方法。我们还演示了该库如何记录和报告失败事件，并且我们使用lambda表达式编写了一个自定义检查器。
