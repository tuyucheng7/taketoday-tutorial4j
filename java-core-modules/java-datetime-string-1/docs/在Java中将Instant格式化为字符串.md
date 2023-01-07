## 1. 概述

在本教程中，我们将学习如何在Java中将instant 格式化为String。

首先，我们将从一些关于Java中的瞬间是什么的背景知识开始。然后我们将演示如何使用核心Java和第三方库(例如 Joda-Time)来回答我们的核心问题。

## 2. 使用 CoreJava格式化 Instant

根据[Java 文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)，瞬间是从 1970-01-01T00:00:00Z 的Java纪元开始测量的时间戳。

Java 8 包含一个名为Instant的方便类，用于表示时间轴上的特定瞬时点。通常，我们可以使用此类来记录应用程序中的事件时间戳。

现在我们知道了Java中的 instant 是什么，让我们看看如何将它转换为String对象。

### 2.1. 使用DateTimeFormatter类

一般来说，我们需要一个格式化程序来格式化Instant对象。对我们来说幸运的是，Java 8 引入了[DateTimeFormatter](https://www.baeldung.com/java-datetimeformatter)类来统一格式化日期和时间。

基本上，DateTimeFormatter提供了format()方法来完成这项工作。

简单地说，DateTimeFormatter需要一个时区来格式化一个 instant。没有它，它将无法将即时转换为人类可读的日期/时间字段。

例如，假设我们要使用dd.MM.yyyy格式显示我们的Instant实例：

```java
public class FormatInstantUnitTest {
    
    private static final String PATTERN_FORMAT = "dd.MM.yyyy";

    @Test
    public void givenInstant_whenUsingDateTimeFormatter_thenFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
            .withZone(ZoneId.systemDefault());

        Instant instant = Instant.parse("2022-02-15T18:35:24.00Z");
        String formattedInstant = formatter.format(instant);

        assertThat(formattedInstant).isEqualTo("15.02.2022");
    }
    ...
}
```

如上所示，我们可以使用withZone()方法来指定时区。

请记住，未能指定时区将导致[UnsupportedTemporalTypeException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/UnsupportedTemporalTypeException.html)：

```java
@Test(expected = UnsupportedTemporalTypeException.class)
public void givenInstant_whenNotSpecifyingTimeZone_thenThrowException() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT);

    Instant instant = Instant.now();
    formatter.format(instant);
}
```

### 2.2. 使用toString()方法

另一种解决方案是使用toString()方法来获取Instant对象的字符串表示形式。

让我们使用测试用例来举例说明toString()方法的使用：

```java
@Test
public void givenInstant_whenUsingToString_thenFormat() {
    Instant instant = Instant.ofEpochMilli(1641828224000L);
    String formattedInstant = instant.toString();

    assertThat(formattedInstant).isEqualTo("2022-01-10T15:23:44Z");
}
```

这种方法的局限性在于我们不能使用自定义的、人性化的格式来显示即时.

## 3. Joda-Time 图书馆

或者，我们可以使用[Joda-Time API](https://www.baeldung.com/joda-time)来实现相同的目标。这个库提供了一组随时可用的类和接口，用于在Java中操作日期和时间。

在这些类中，我们将找到[DateTimeFormat](https://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormatter.html)类。顾名思义，此类可用于格式化日期/时间数据或从字符串中解析日期/时间数据。

让我们来说明如何使用DateTimeFormatter 将瞬间转换为字符串：

```java
@Test
public void givenInstant_whenUsingJodaTime_thenFormat() {
    org.joda.time.Instant instant = new org.joda.time.Instant("2022-03-20T10:11:12");
        
    String formattedInstant = DateTimeFormat.forPattern(PATTERN_FORMAT)
        .print(instant);

    assertThat(formattedInstant).isEqualTo("20.03.2022");
}
```

正如我们所见，DateTimeFormatter提供forPattern()来指定格式化模式，并提供print()来格式化Instant对象。

## 4。总结

在本文中，我们深入介绍了如何在Java中将即时格式化为字符串。

我们探索了几种使用核心Java方法实现此目的的方法。然后我们解释了如何使用 Joda-Time 库完成同样的事情。