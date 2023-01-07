## 1. 概述

从Java8 开始，我们有了一个新的日期 API：java.time。

然而，有时我们仍然需要在新旧 API 之间进行转换，并使用两者的日期表示。

## 延伸阅读：

## [迁移到新的Java8 日期时间 API](https://www.baeldung.com/migrating-to-java-8-date-time-api)

关于过渡到Java8 的新 DateTime API 的快速实用指南。

[阅读更多](https://www.baeldung.com/migrating-to-java-8-date-time-api)→

## [Java 8 日期/时间 API 简介](https://www.baeldung.com/java-8-date-time-intro)

在本文中，我们将了解用于日期和时间的新Java8 API，以及构建和操作日期和时间有多容易。

[阅读更多](https://www.baeldung.com/java-8-date-time-intro)→

## 2. 将java.util.Date转换为java.time.LocalDate

让我们从将旧日期表示转换为新日期表示开始。

在这里，我们可以利用新的toInstant()方法，该方法已添加到Java 8中的java.util.Date中。

当我们转换Instant 对象时，需要使用ZoneId ，因为Instant对象与时区无关 ——只是时间轴上的点。

Instant对象的atZone(ZoneId zone) API返回一个ZonedDateTime，因此我们只需要使用toLocalDate()方法从中提取LocalDate 。

首先，我们使用默认系统ZoneId：

```java
public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
    return dateToConvert.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
}
```

还有一个类似的解决方案，但创建Instant对象的方式不同——使用ofEpochMilli()方法：

```java
public LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
    return Instant.ofEpochMilli(dateToConvert.getTime())
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
}
```

在我们继续之前，让我们也快速浏览一下旧的java.sql.Date类以及如何将其转换为LocalDate。

从Java8 开始，我们可以在java.sql.Date上找到一个额外的toLocalDate()方法，这也为我们提供了一种将其转换为java.time.LocalDate的简单方法。

在这种情况下，我们不需要担心时区：

```java
public LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
    return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
}
```

非常相似，我们也可以将旧的Date对象转换为LocalDateTime对象。接下来让我们看一下。

## 3. 将java.util.Date转换为java.time.LocalDateTime

要获取LocalDateTime实例，我们可以类似地使用中介ZonedDateTime ，然后使用toLocalDateTime() API。

就像以前一样，我们可以使用两种可能的解决方案从java.util.Date获取Instant对象：

```java
public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
    return dateToConvert.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();
}

public LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
    return Instant.ofEpochMilli(dateToConvert.getTime())
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();
}
```

请注意，对于 1582 年 10 月 10 日之前的日期，需要将 Calendar 设置为公历并调用方法[setGregorianChange](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/GregorianCalendar.html#setGregorianChange(java.util.Date)) ()：

```java
GregorianCalendar calendar = new GregorianCalendar();
calendar.setGregorianChange(new Date(Long.MIN_VALUE));
Date dateToConvert = calendar.getTime();
```

从Java8 开始，我们还可以使用java.sql.Timestamp来获取LocalDateTime：

```java
ocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
    return new java.sql.Timestamp(
      dateToConvert.getTime()).toLocalDateTime();
}
```

## 4.将java.time.LocalDate转换为java.util.Date

现在我们已经很好地理解了如何从旧数据表示形式转换为新数据表示形式，让我们看看从另一个方向进行的转换。

我们将讨论将LocalDate转换为Date的两种可能方法。

首先，我们使用java.sql.Date对象中提供的一个新的valueOf(LocalDate date)方法，它以 LocalDate作为参数：

```java
public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
    return java.sql.Date.valueOf(dateToConvert);
}
```

正如我们所见，它既轻松又直观。它使用本地时区进行转换(所有操作都在后台完成，因此无需担心)。

在另一个Java8 示例中，我们使用Instant对象传递给java.util.Date对象的from(Instant instant) 方法 ：

```java
public Date convertToDateViaInstant(LocalDate dateToConvert) {
    return java.util.Date.from(dateToConvert.atStartOfDay()
      .atZone(ZoneId.systemDefault())
      .toInstant());
}
```

请注意，我们在这里使用了一个Instant对象，并且在进行此转换时我们还需要关心时区。

接下来，让我们使用非常相似的解决方案将LocalDateTime转换为Date对象。

## 5.将java.time.LocalDateTime转换为java.util.Date

从LocalDateTime获取java.util.Date的 最简单方法是使用java.sql.Timestamp的扩展——Java 8 可用：

```java
public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
    return java.sql.Timestamp.valueOf(dateToConvert);
}
```

但当然，另一种解决方案是使用Instant对象，我们从ZonedDateTime获得它：

```java
Date convertToDateViaInstant(LocalDateTime dateToConvert) {
    return java.util.Date
      .from(dateToConvert.atZone(ZoneId.systemDefault())
      .toInstant());
}
```

## 6.Java9 添加

在Java9 中，有一些新方法可以简化java.util.Date和java.time.LocalDate或java.time.LocalDateTime之间的转换。

LocalDate.ofInstant(Instant instant, ZoneId zone)和LocalDateTime.ofInstant(Instant instant, ZoneId zone)提供方便的快捷方式：

```java
public LocalDate convertToLocalDate(Date dateToConvert) {
    return LocalDate.ofInstant(
      dateToConvert.toInstant(), ZoneId.systemDefault());
}

public LocalDateTime convertToLocalDateTime(Date dateToConvert) {
    return LocalDateTime.ofInstant(
      dateToConvert.toInstant(), ZoneId.systemDefault());
}
```

## 七、总结

在本文中，我们介绍了将旧的java.util.Date转换为新的java.time.LocalDate和java.time.LocalDateTime的可能方法，以及相反的方法。