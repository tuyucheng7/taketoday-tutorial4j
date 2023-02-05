## 1. 概述

在本快速教程中，我们将了解如何使用Java7、Java 8 和 Joda-Time 库设置日期的时区。

## 2. 使用Java8

Java 8 引入 [了一个新的日期时间 API](https://www.baeldung.com/migrating-to-java-8-date-time-api) ，用于处理日期和时间，它主要基于 Joda-Time 库。

Java Date Time API中的[Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)类模拟 UTC 时间轴上的单个瞬时点。这表示自 1970 UTC 的第一时刻以来的纳秒数。

首先，我们将从系统时钟和 [ZoneId](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/ZoneId.html)获取当前Instant 作为时区名称：

```java
Instant nowUtc = Instant.now();
ZoneId asiaSingapore = ZoneId.of("Asia/Singapore");
```

最后，ZoneId和Instant 可用于创建具有时区详细信息的日期时间对象。ZonedDateTime 类表示 ISO-8601 日历系统中带有时区的日期时间[：](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/ZonedDateTime.html)

```java
ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, asiaSingapore);
```

我们使用Java8 的ZonedDateTime来表示带有时区的日期时间。

## 3. 使用Java7

在Java7 中，设置时区有点棘手。[Date](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Date.html)类(表示特定的瞬间)不包含任何时区信息。

首先，让我们获取当前的 UTC 日期和一个 [TimeZone](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/TimeZone.html) 对象：

```java
Date nowUtc = new Date();
TimeZone asiaSingapore = TimeZone.getTimeZone(timeZone);
```

在Java7 中，我们需要使用 [Calendar](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Calendar.html)类来表示带有时区的日期。

最后，我们可以使用 asiaSingapore TimeZone创建一个nowUtc Calendar并设置时间：

```java
Calendar nowAsiaSingapore = Calendar.getInstance(asiaSingapore);
nowAsiaSingapore.setTime(nowUtc);
```

建议避免使用Java7 日期时间 API，而使用Java8 日期时间 API 或 Joda-Time 库。

## 4. 使用 Joda-Time

如果Java8 不是一个选项， 我们仍然可以从 Joda-Time 获得相同类型的结果， [Joda-Time](http://www.joda.org/joda-time/)是Java8 之前世界中日期时间操作的实际标准。

首先，我们需要将 [Joda-Time 依赖](https://search.maven.org/classic/#artifactdetails|joda-time|joda-time|2.10|jar)项添加 到pom.xml 中：

```xml
<dependency>
  <groupId>joda-time</groupId>
  <artifactId>joda-time</artifactId>
  <version>2.10</version>
</dependency>
```

为了表示时间轴上的确切点，我们可以使用org.joda.time包中的[Instant 。](http://joda-time.sourceforge.net/apidocs/org/joda/time/Instant.html) 在内部，该类保存一条数据，从 1970-01-01T00:00:00Z 的Java时代开始以毫秒为单位的瞬间：

```java
Instant nowUtc = Instant.now();
```

我们将使用[DateTimeZone](https://www.joda.org/joda-time/apidocs/org/joda/time/DateTimeZone.html) 来表示时区(对于指定的时区 ID)：

```java
DateTimeZone asiaSingapore = DateTimeZone.forID("Asia/Singapore");
```

现在nowUtc 时间将使用时区信息转换为[DateTime](https://www.joda.org/joda-time/apidocs/org/joda/time/DateTime.html) 对象：

```java
DateTime nowAsiaSingapore = nowUtc.toDateTime(asiaSingapore);
```

这就是如何使用 Joda-time API 来组合日期和时区信息。

## 5.总结

在本文中，我们了解了如何使用Java7、8 和 Joda-Time API 在Java中设置时区。要了解有关Java8 的日期时间支持的更多信息，请查看 [我们的Java8 日期时间介绍](https://www.baeldung.com/java-8-date-time-intro)。