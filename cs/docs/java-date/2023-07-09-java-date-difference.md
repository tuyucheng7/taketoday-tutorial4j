---
layout: post
title:  Java中两个日期之间的差距
category: java-date
copyright: java-date
excerpt: Java Date Time
---

## 1. 概述

在这个快速教程中，我们将探讨在Java中计算两个日期之间的差距的多种可能性。

## 2. 核心Java

### 2.1 使用java.util.Date获取天数差距

让我们首先使用核心Java API进行计算并确定两个日期之间的天数：

```java
@Test
public void givenTwoDatesBeforeJava8_whenDifferentiating_thenWeGetSix() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
    Date firstDate = sdf.parse("06/24/2017");
    Date secondDate = sdf.parse("06/30/2017");

    long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

    assertEquals(6, diff);
}
```

### 2.2 使用java.time.temporal.ChronoUnit获取差距

Java 8中的时间API使用[TemporalUnit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalUnit.html)接口表示日期时间单位，例如秒或天。

**每个单位都提供一个名为[between](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalUnit.html#between(java.time.temporal.Temporal,java.time.temporal.Temporal))的方法的实现，以根据该特定单位计算两个时间对象之间的时间量**。

例如，为了计算两个LocalDateTime之间的秒数：

```java
@Test
public void givenTwoDateTimesInJava8_whenDifferentiatingInSeconds_thenWeGetTen() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tenSecondsLater = now.plusSeconds(10);

    long diff = ChronoUnit.SECONDS.between(now, tenSecondsLater);

    assertEquals(10, diff);
}
```

[ChronoUnit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/ChronoUnit.html)通过实现TemporalUnit接口提供了一组具体的时间单位，**强烈建议静态导入ChronoUnit枚举值以获得更好的可读性**：

```java
import static java.time.temporal.ChronoUnit.SECONDS;

// omitted
long diff = SECONDS.between(now, tenSecondsLater);
```

此外，我们可以将任何两个兼容的时间对象传递给between方法，甚至是ZonedDateTime。

ZonedDateTime的优点在于即使将它们设置为不同的时区，计算也会起作用：

```java
@Test
public void givenTwoZonedDateTimesInJava8_whenDifferentiating_thenWeGetSix() {
    LocalDateTime ldt = LocalDateTime.now();
    ZonedDateTime now = ldt.atZone(ZoneId.of("America/Montreal"));
    ZonedDateTime sixMinutesBehind = now
        .withZoneSameInstant(ZoneId.of("Asia/Singapore"))
        .minusMinutes(6);
    
    long diff = ChronoUnit.MINUTES.between(sixMinutesBehind, now);
    
    assertEquals(6, diff);
}
```

### 2.3 使用Temporal#until()

**任何[Temporal](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/Temporal.html)对象(例如LocalDate或ZonedDateTime)都提供了一个[until](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/Temporal.html#until(java.time.temporal.Temporal,java.time.temporal.TemporalUnit))方法来根据指定的单位计算距离另一个Temporal的时间量**：

```java
@Test
public void givenTwoDateTimesInJava8_whenDifferentiatingInSecondsUsingUntil_thenWeGetTen() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tenSecondsLater = now.plusSeconds(10);

    long diff = now.until(tenSecondsLater, ChronoUnit.SECONDS);

    assertEquals(10, diff);
}
```

Temporal#until和TemporalUnit#between是实现相同功能的两个不同API。

### 2.4 使用java.time.Duration和java.time.Period

在Java 8中，时间API引入了两个新类：[Duration和Period](https://www.baeldung.com/java-8-date-time-intro#period)。

**如果我们想在基于时间(小时、分钟或秒)的时间量内计算两个日期时间之间的差距，我们可以使用Duration类**：

```java
@Test
public void givenTwoDateTimesInJava8_whenDifferentiating_thenWeGetSix() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime sixMinutesBehind = now.minusMinutes(6);

    Duration duration = Duration.between(now, sixMinutesBehind);
    long diff = Math.abs(duration.toMinutes());

    assertEquals(6, diff);
}
```

但是，**如果我们尝试使用Period类来表示两个日期之间的差距，我们应该警惕陷阱**。

一个例子可以快速解释这个陷阱。

让我们使用Period类计算两个日期之间的天数：

```java
@Test
public void givenTwoDatesInJava8_whenUsingPeriodGetDays_thenWorks()  {
    LocalDate aDate = LocalDate.of(2020, 9, 11);
    LocalDate sixDaysBehind = aDate.minusDays(6);

    Period period = Period.between(aDate, sixDaysBehind);
    int diff = Math.abs(period.getDays());

    assertEquals(6, diff);
}
```

如果我们运行上面的测试，它就会通过。我们可能认为Period类可以方便地解决我们的问题。到目前为止，一切都很好。

如果这种方式适用于6天的差距，我们毫不怀疑它也适用于60天。

因此，让我们将上面测试中的6更改为60，看看会发生什么：

```java
@Test
public void givenTwoDatesInJava8_whenUsingPeriodGetDays_thenDoesNotWork() {
    LocalDate aDate = LocalDate.of(2020, 9, 11);
    LocalDate sixtyDaysBehind = aDate.minusDays(60);

    Period period = Period.between(aDate, sixtyDaysBehind);
    int diff = Math.abs(period.getDays());

    assertEquals(60, diff);
}
```

现在，如果我们再次运行测试，我们将看到：

```text
java.lang.AssertionError: 
Expected :60
Actual   :29
```

哎呀！为什么Period类将差距报告为29天？

**这是因为Period类以“x年，y个月和z天”的格式表示基于日期的时间量。当我们调用它的getDays()方法时，它只返回“z days”部分**。

因此，上面测试中的period对象的值为“0 years, 1 month and 29 days”：

```java
@Test
public void givenTwoDatesInJava8_whenUsingPeriod_thenWeGet0Year1Month29Days() {
    LocalDate aDate = LocalDate.of(2020, 9, 11);
    LocalDate sixtyDaysBehind = aDate.minusDays(60);
    Period period = Period.between(aDate, sixtyDaysBehind);
    int years = Math.abs(period.getYears());
    int months = Math.abs(period.getMonths());
    int days = Math.abs(period.getDays());
    assertArrayEquals(new int[] { 0, 1, 29 }, new int[] { years, months, days });
}
```

**如果我们想使用Java 8的时间API计算天数差距，ChronoUnit.DAYS.between()方法是最直接的方法**。

## 3. 外部库

### 3.1 JodaTime

我们也可以用JodaTime做一个相对简单的实现：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.9.9</version>
</dependency>
```

你可以从Maven Central获取最新版本的[Joda-time](https://mvnrepository.com/artifact/joda-time/joda-time)。

LocalDate案例：

```java
@Test
public void givenTwoDatesInJodaTime_whenDifferentiating_thenWeGetSix() {
    org.joda.time.LocalDate now = org.joda.time.LocalDate.now();
    org.joda.time.LocalDate sixDaysBehind = now.minusDays(6);

    long diff = Math.abs(Days.daysBetween(now, sixDaysBehind).getDays());
    assertEquals(6, diff);
}

```

同样，对于LocalDateTime：

```java
@Test
public void givenTwoDateTimesInJodaTime_whenDifferentiating_thenWeGetSix() {
    org.joda.time.LocalDateTime now = org.joda.time.LocalDateTime.now();
    org.joda.time.LocalDateTime sixMinutesBehind = now.minusMinutes(6);

    long diff = Math.abs(Minutes.minutesBetween(now, sixMinutesBehind).getMinutes());
    assertEquals(6, diff);
}
```

### 3.2 Date4J

Date4j也提供了一个简单明了的实现-请注意，在这种情况下，我们需要明确提供一个TimeZone。

让我们首先添加Maven依赖项：

```xml
<dependency>
    <groupId>com.darwinsys</groupId>
    <artifactId>hirondelle-date4j</artifactId>
    <version>1.5.1</version>
</dependency>
```

这是使用标准DateTime的快速测试：

```java
@Test
public void givenTwoDatesInDate4j_whenDifferentiating_thenWeGetSix() {
    DateTime now = DateTime.now(TimeZone.getDefault());
    DateTime sixDaysBehind = now.minusDays(6);
 
    long diff = Math.abs(now.numDaysFrom(sixDaysBehind));

    assertEquals(6, diff);
}
```

## 4. 总结

在本文中，我们说明了几种计算日期(有时间和没有时间)之间的方法，既可以使用普通Java也可以使用外部库。

与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-date-operations-1)上获得。