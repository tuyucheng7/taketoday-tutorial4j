## 1. 概述

在本教程中，我们将探索从Java中的 Date 对象中减去天数的各种方法。

我们将从使用Java8 引入的[Date Time API](https://www.baeldung.com/java-8-date-time-intro)开始。之后，我们将学习如何使用java.util包中的类来实现它，最后，我们将使用[Joda-Time](https://www.baeldung.com/joda-time)库的帮助。

## 2.java.time.LocalDateTime _

Java 8 中引入的 Date/Time API 是目前最可行的日期和时间计算选项。

让我们看看如何从Java8 的java.util.LocalDateTime对象中减去天数：

```java
@Test
public void givenLocalDate_whenSubtractingFiveDays_dateIsChangedCorrectly() {
    LocalDateTime localDateTime = LocalDateTime.of(2022, 4, 20, 0, 0);

    localDateTime = localDateTime.minusDays(5);

    assertEquals(15, localDateTime.getDayOfMonth());
    assertEquals(4, localDateTime.getMonthValue());
    assertEquals(2022, localDateTime.getYear());
}
```

## 3.java.util.日历

java.util中的Date和Calendar是用于日期操作的最常用的Java8 之前的实用程序类。

让我们使用java.util.Calendar从日期中减去五天：

```java
@Test
public void givenCalendarDate_whenSubtractingFiveDays_dateIsChangedCorrectly() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2022, Calendar.APRIL, 20);

    calendar.add(Calendar.DATE, -5);

    assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(Calendar.APRIL, calendar.get(Calendar.MONTH));
    assertEquals(2022, calendar.get(Calendar.YEAR));
}
```

我们在使用它们时应该小心，因为它们有一些设计缺陷并且不是线程安全的。

[我们可以在有关迁移到新的Java8 日期时间 API](https://baeldung.com/migrating-to-java-8-date-time-api)的文章中阅读有关与遗留代码的交互以及两个 API 之间的差异的更多信息。

## 4.乔达时间

我们可以使用 Joda-Time 作为Java最初的日期和时间处理解决方案的更好替代方案。该库提供更直观的 API、多个日历系统、线程安全和不可变对象。

为了使用[Joda-Time](https://search.maven.org/search?q=g:joda-time AND a:joda-time)，我们需要将它作为依赖项包含在pom.xml文件中：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.10.14</version>
</dependency>
```

让我们从 Joda-Time 的DateTime对象中减去五天：

```java
@Test
public void givenJodaDateTime_whenSubtractingFiveDays_dateIsChangedCorrectly() {
    DateTime dateTime = new DateTime(2022, 4, 20, 12, 0, 0);

    dateTime = dateTime.minusDays(5);

    assertEquals(15, dateTime.getDayOfMonth());
    assertEquals(4, dateTime.getMonthOfYear());
    assertEquals(2022, dateTime.getYear());
}

```

Joda-Time 是遗留代码的一个很好的解决方案。然而，该项目正式“完成”，库的作者建议迁移到Java8 的日期/时间 API。

## 5.总结

在这篇简短的文章中，我们探索了几种从日期对象中减去天数的方法。

我们已经使用java.time.LocalDateTime 和Java8 之前的解决方案实现了这一点：java.util.Calendar和 Joda-Time 库。