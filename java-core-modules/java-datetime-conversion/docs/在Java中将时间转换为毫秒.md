## 1. 概述

在本快速教程中，我们将说明在Java中将时间转换为 Unix-epoch 毫秒的多种方法。

更具体地说，我们将使用：

-   核心Java的java.util.Date和日历
-   Java 8 的日期和时间 API
-   Joda-Time 库

## 2.核心Java

### 2.1. 使用日期

首先，让我们定义一个包含毫秒随机值的millis属性：

```java
long millis = 1556175797428L; // April 25, 2019 7:03:17.428 UTC
```

我们将使用这个值来初始化我们的各种对象并验证我们的结果。

接下来，让我们从Date对象开始：

```java
Date date = // implementation details
```

现在，我们准备好通过简单地调用getTime()方法将日期转换为毫秒：

```java
Assert.assertEquals(millis, date.getTime());
```

### 2.2. 使用日历

同样，如果我们有一个Calendar对象，我们可以使用getTimeInMillis()方法：

```java
Calendar calendar = // implementation details
Assert.assertEquals(millis, calendar.getTimeInMillis());
```

## 3.Java8 日期时间 API

### 3.1. 使用即时

简单地说，[Instant](https://www.baeldung.com/current-date-time-and-timestamp-in-java-8)是Java纪元时间轴上的一个点。

我们可以从Instant获取当前时间(以毫秒为单位)：

```java
java.time.Instant instant = // implementation details
Assert.assertEquals(millis, instant.toEpochMilli());
```

结果，toEpochMilli()方法返回与我们之前定义的相同的毫秒数。

### 3.2. 使用LocalDateTime

同样，我们可以使用Java8 的[日期和时间 API](https://www.baeldung.com/java-8-date-time-intro)将LocalDateTime转换为毫秒：

```java
LocalDateTime localDateTime = // implementation details
ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
Assert.assertEquals(millis, zdt.toInstant().toEpochMilli());
```

首先，我们创建了一个当前日期的实例。之后，我们使用toEpochMilli()方法将ZonedDateTime转换为毫秒。

正如我们所知，LocalDateTime不包含有关时区的信息。换句话说，我们不能直接从LocalDateTime实例中获取毫秒数。

## 4.乔达时间

虽然Java8 添加了许多 Joda-Time 的功能，但如果我们使用的是Java7 或更早版本，我们可能希望使用此选项。

### 4.1. 使用即时

首先，我们可以使用getMillis()方法从[Joda-Time](https://www.baeldung.com/joda-time) Instant类实例中获取当前系统毫秒数：

```java
Instant jodaInstant = // implementation details
Assert.assertEquals(millis, jodaInstant.getMillis());
```

### 4.2. 使用日期时间

此外，如果我们有一个 Joda-Time DateTime实例：

```java
DateTime jodaDateTime = // implementation details
```

然后我们可以使用getMillis()方法检索毫秒数：

```java
Assert.assertEquals(millis, jodaDateTime.getMillis());
```

## 5.总结

总之，本文演示了如何在Java中将时间转换为毫秒。