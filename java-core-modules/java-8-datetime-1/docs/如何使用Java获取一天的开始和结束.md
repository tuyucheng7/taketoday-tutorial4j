## 1. 概述

在这个简短的教程中，我们将学习如何在Java中开始和结束一天，使用针对不同场景的简单、直接的示例。

我们将使用 [Java 的 8 日期/时间 API](http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html) 来构建这些示例。

如果你想在继续之前阅读更多关于Java的 8 日期和时间库的信息，你可以从[这里](https://www.baeldung.com/java-8-date-time-intro)开始。

## 2. 来自LocalDate对象

首先，让我们看看如何获取作为[LocalDate](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/LocalDate.html)对象提供给我们的一天的开始或结束，例如：

```java
LocalDate localDate = LocalDate.parse("2018-06-23");
```

### 2.1. 在StartOfDay()

获取表示特定日期开始的 [LocalDateTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/LocalDateTime.html)的最简单方法是使用atStartOfDay()方法：

```java
LocalDateTime startOfDay = localDate.atStartOfDay();
```

此方法已重载，因此如果我们想从中获取[ZonedDateTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/ZonedDateTime.html)，我们可以通过指定[ZoneId](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/ZoneId.html)来实现：

```java
ZonedDateTime startOfDay = localDate.atStartOfDay(ZoneId.of("Europe/Paris"));
```

### 2.2. 的()

我们可以获得相同结果的另一种方法是使用 of()方法，提供LocalDate和[LocalTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/LocalTime.html)的静态字段之一：

```java
LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
```

LocalTime提供以下静态字段：MIDNIGHT (00:00)、 MIN (00:00)、NOON (12:00) 和MAX (23:59:59.999999999)。

因此，如果我们想得到一天的结束，我们会使用MAX值。

让我们尝试一下，但使用不同的方法。

### 2.3. 在时间()

这个方法是重载的，允许我们使用小时、分钟、秒甚至纳秒来指定所需的时间。

在这种情况下，无论如何，我们将使用LocalTime的MAX字段作为参数来获取给定日期的最后时刻：

```java
LocalDateTime startOfDay = localDate.atTime(LocalTime.MAX);
```

### 2.4. 在日期()

这个例子与前面的例子非常相似，但这次，我们将使用LocalTime对象的atDate()方法，将LocalDate作为参数传递：

```java
LocalDateTime endOfDate = LocalTime.MAX.atDate(localDate);
```

## 3. 来自LocalDateTime 对象

几乎不言而喻，我们可以从中获取LocalDate，然后使用第 2 节中的任何方法从中获取一天的结束或开始：

```java
LocalDateTime localDateTime = LocalDateTime
  .parse("2018-06-23T05:55:55");
LocalDateTime endOfDate = localDateTime
  .toLocalDate().atTime(LocalTime.MAX);
```

但在本节中，我们将分析另一种方法，直接从另一个给定的LocalDateTime对象获取时间段设置为一天开始或结束的对象。

### 3.1. 和()

所有实现[Temporal 接口](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/Temporal.html)的类都可以使用这个方法。

在这种情况下，我们将使用方法的签名，该方法采用[TemporalField](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalField.html) (特别是[ChronoField 枚举](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/ChronoField.html) 值之一)和一个长 参数作为字段的新值：

```java
LocalDateTime endOfDate = localDateTime.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());
```

## 4. 来自ZonedDateTime对象

如果给定一个ZonedDateTime，我们可以使用 with()方法，因为它也实现了Temporal 接口：

```java
ZonedDateTime startofDay = zonedDateTime.with(ChronoField.HOUR_OF_DAY, 0);
```

## 5.总结

总而言之，我们已经针对许多不同的案例场景分析了许多在Java中获取一天开始和结束的不同方法。

最后，我们了解了Java的 8 个日期和时间库类的见解，并熟悉了它的许多类和接口。