## 1. 概述

这篇快速文章描述了我们如何在Java8 中获取当前日期、当前时间和当前时间戳。

## 2. 当前日期

首先，让我们使用java.time.LocalDate获取当前系统日期：

```java
LocalDate localDate = LocalDate.now();
```

要获取任何其他时区的日期，我们可以使用LocalDate.now(ZoneId)：

```java
LocalDate localDate = LocalDate.now(ZoneId.of("GMT+02:30"));
```

我们还可以使用java.time.LocalDateTime来获取LocalDate 的实例：

```java
LocalDateTime localDateTime = LocalDateTime.now();
LocalDate localDate = localDateTime.toLocalDate();
```

## 3. 当前时间

使用java.time.LocalTime，让我们检索当前系统时间：

```java
LocalTime localTime = LocalTime.now();
```

要获取特定时区的当前时间，我们可以使用LocalTime.now(ZoneId)：

```java
LocalTime localTime = LocalTime.now(ZoneId.of("GMT+02:30"));
```

我们还可以使用java.time.LocalDateTime来获取 LocalTime 的实例：

```java
LocalDateTime localDateTime = LocalDateTime.now();
LocalTime localTime = localDateTime.toLocalTime();
```

## 4. 当前时间戳

使用java.time.Instant从Java纪元获取时间戳。根据[JavaDoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)，“纪元秒是从 1970-01-01T00:00:00Z 的标准Java纪元开始测量的，其中纪元之后的瞬间具有正值：

```java
Instant instant = Instant.now();
long timeStampMillis = instant.toEpochMilli();
```

我们可以获得纪元秒数：

```java
Instant instant = Instant.now();
long timeStampSeconds = instant.getEpochSecond();
```

## 5.总结

在本教程中，我们重点介绍了使用java.time.来获取当前日期、时间和时间戳。