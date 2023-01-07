## 1. 概述

在本快速教程中，我们将学习如何从[Unix timestamp](https://www.baeldung.com/linux/date-command)解析日期表示。[Unix 时间](https://en.wikipedia.org/wiki/Unix_time)是自 1970 年 1 月 1 日以来经过的秒数。但是，时间戳可以表示精确到纳秒级的时间。因此，我们将看到可用的工具并创建一个方法来将任何范围的时间戳转换为Java对象。

## 2. 旧方法(Java 8 之前)

在Java8 之前，我们最简单的选择是Date 和Calendar。Date类有一个构造函数，它直接接受以毫秒为单位的时间戳：

```java
public static Date dateFrom(long input) {
    return new Date(input);
}
```

对于Calendar，我们必须在getInstance( )之后调用setTimeInMillis( ) ：

```java
public static Calendar calendarFrom(long input) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(input);
    return calendar;
}
```

换句话说，我们必须知道我们的输入是以秒、纳秒还是介于两者之间的任何其他精度为单位的。然后，我们必须手动将时间戳转换为毫秒。

## 3.新方式(Java 8+)

Java 8 引入了Instant。此类具有从秒和毫秒创建实例的实用方法。此外，其中之一接受纳秒调整参数：

```java
Instant.ofEpochSecond(seconds, nanos);
```

但是我们仍然必须提前知道时间戳的精度。因此，例如，如果我们知道我们的时间戳以纳秒为单位，则需要进行一些计算：

```java
public static Instant fromNanos(long input) {
    long seconds = input / 1_000_000_000;
    long nanos = input % 1_000_000_000;

    return Instant.ofEpochSecond(seconds, nanos);
}
```

首先，我们将时间戳除以十亿得到秒数。然后，我们使用它的[余数](https://www.baeldung.com/modulo-java)来得到秒后的部分。

## 4.即时通用解决方案

为了避免额外的工作，让我们创建一个可以将任何输入转换为毫秒的方法，大多数类都可以解析它。首先，我们检查时间戳的范围。然后，我们执行计算以提取毫秒。此外，我们将使用科学记数法使我们的条件更具可读性。

另外，请记住时间戳是有[符号](https://www.baeldung.com/java-unsigned-arithmetic)的，所以我们必须检查正负范围(负时间戳意味着它们是从 1970 年开始倒数的)。

因此，让我们首先检查我们的输入是否以纳秒为单位：

```java
private static long millis(long timestamp) {
    if (millis >= 1E16 || millis <= -1E16) {
        return timestamp / 1_000_000;
    }

    // next range checks
}
```

首先，我们检查它是否在1E16范围内，即 1 后跟 16 个零。负值表示 1970 年之前的日期，因此我们也必须检查它们。然后，我们将我们的价值除以一百万得到毫秒。

同样，微秒在1E14范围内。这次，我们除以一千：

```java
if (timestamp >= 1E14 || timestamp <= -1E14) {
    return timestamp / 1_000;
}
```

当我们的值在 1E11 到 -3E10 范围内时，我们不需要改变任何东西。这意味着我们的输入已经以毫秒为单位：

```java
if (timestamp >= 1E11 || timestamp <= -3E10) {
    return timestamp;
}
```

最后，如果我们的输入不是这些范围中的任何一个，那么它必须以秒为单位，因此我们需要将其转换为毫秒：

```java
return timestamp  1_000;
```

### 4.1. Instant标准化输入

现在，让我们创建一个方法，使用Instant.ofEpochMilli()从输入以任意精度返回Instant：

```java
public static Instant fromTimestamp(long input) {
    return Instant.ofEpochMilli(millis(input));
}
```

请注意，每次我们对值进行除法或乘法时，精度都会丢失。

### 4.2. 使用LocalDateTime 的本地时间

Instant代表一个时刻。但是，没有[时区](https://www.baeldung.com/java-set-date-time-zone)，它不容易阅读，因为它取决于我们在世界上的位置。因此，让我们创建一个方法来生成本地时间表示。我们将使用 UTC 来避免在我们的测试中出现不同的结果：

```java
public static LocalDateTime localTimeUtc(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
}
```

现在，我们可以测试使用错误的精度如何在方法需要特定格式时导致完全不同的日期。首先，让我们传递一个以纳秒为单位的时间戳，我们已经知道正确的日期，但将其转换为微秒并使用我们之前创建的fromNanos()方法：

```java
@Test
void givenWrongPrecision_whenInstantFromNanos_thenUnexpectedTime() {
    long microseconds = 1660663532747420283l / 1000;
    Instant instant = fromNanos(microseconds);
    String expectedTime = "2022-08-16T15:25:32";

    LocalDateTime time = localTimeUtc(instant);
    assertThat(!time.toString().startsWith(expectedTime));
    assertEquals("1970-01-20T05:17:43.532747420", time.toString());
}
```

当我们使用我们在上一小节中创建的fromTimestamp()方法时，不会发生这个问题：

```java
@Test
void givenMicroseconds_whenInstantFromTimestamp_thenLocalTimeMatches() {
    long microseconds = 1660663532747420283l / 1000;

    Instant instant = fromTimestamp(microseconds);
    String expectedTime = "2022-08-16T15:25:32";

    LocalDateTime time = localTimeUtc(instant);
    assertThat(time.toString().startsWith(expectedTime));
}
```

## 5.总结

在本文中，我们学习了如何使用核心Java类转换时间戳。然后，我们看到了它们如何具有不同级别的精度以及这如何影响我们的结果。最后，我们创建了一种简单的方法来规范我们的输入并获得一致的结果。