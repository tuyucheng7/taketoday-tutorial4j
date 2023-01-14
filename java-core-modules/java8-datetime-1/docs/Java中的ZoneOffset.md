## 1. 概述

在我们的世界中，每个国家/地区都遵循特定的时区。这些时区对于方便有效地表达时间至关重要。但是，由于夏令时等变量的影响，时区有时可能不明确。

此外，在我们的代码中表示这些时区时，事情会变得混乱。Java 过去提供了多个类，例如Date、Time和DateTime来处理时区。

然而，新的Java版本已经提出了更有用和更具表现力的类，例如ZoneId和ZoneOffset，用于管理时区。

在本文中，我们将讨论ZoneId和ZoneOffset以及相关的DateTime 类。

我们还可以在上一篇文章中了解Java8 中引入的一组新[的](https://www.baeldung.com/java-8-date-time-intro)DateTime类。

## 2. ZoneId和ZoneOffset

随着[JSR-310](https://jcp.org/en/jsr/detail?id=310)的出现，添加了一些有用的 API 来管理日期、时间和时区。 作为此更新的一部分，还添加了ZoneId和ZoneOffset类。

### 2.1. 区域编号

如上所述，ZoneId是时区的表示，例如“ Europe/Paris ”。

ZoneId有 2 个实现。首先，与 GMT/UTC 相比具有固定的偏移量。其次，作为一个地理区域，它有一套规则来计算与 GMT/UTC 的偏移量。

让我们为德国柏林创建一个ZoneId ：

```java
ZoneId zone = ZoneId.of("Europe/Berlin");
```

### 2.2. 区域偏移

ZoneOffset扩展ZoneId并 定义 当前时区与 GMT/UTC 的固定偏移量，例如 +02:00。

这意味着这个数字代表固定的小时和分钟，代表当前时区和 GMT/UTC 之间的时间差：

```java
LocalDateTime now = LocalDateTime.now();
ZoneId zone = ZoneId.of("Europe/Berlin");
ZoneOffset zoneOffSet = zone.getRules().getOffset(now);
```

如果一个国家/地区有 2 个不同的偏移量——在夏季和冬季，同一区域将有 2 个不同的ZoneOffset 实现，因此需要指定LocalDateTime。

## 3.日期时间类

接下来让我们讨论一些实际利用 ZoneId和ZoneOffset的DateTime类。

### 3.1. 分区日期时间

ZonedDateTime是 ISO-8601 日历系统中带有时区的日期时间的不可变表示，例如2007-12-03T10:15:30+01:00 Europe/Paris。 ZonedDateTime 持有等同于三个独立对象的状态，一个LocalDateTime ，一个ZoneId和解析的ZoneOffset。 

此类存储所有日期和时间字段，精度为纳秒，以及时区和ZoneOffset，以处理不明确的本地日期时间。例如，ZonedDateTime可以存储值“欧洲/巴黎时区 2007 年 10 月 2 日 13:45.30.123456789 +02:00”。

让我们获取前一个区域的当前 ZonedDateTime：

```java
ZoneId zone = ZoneId.of("Europe/Berlin");
ZonedDateTime date = ZonedDateTime.now(zone);
```

ZonedDateTime 还 提供内置函数，将给定日期从一个时区转换为另一个时区：

```java
ZonedDateTime destDate = sourceDate.withZoneSameInstant(destZoneId);
```

### 3.2. 偏移日期时间

OffsetDateTime是日期时间的不可变表示，在 ISO-8601 日历系统中具有偏移量，例如2007-12-03T10:15:30+01:00。

此类存储所有日期和时间字段，精度为纳秒，以及与 GMT/UTC 的偏移量。例如，OffsetDateTime可以存储值“2007 年 10 月 2 日 13:45.30.123456789 +02:00”。

让我们获取与 GMT/UTC 相差 2 小时的当前 OffsetDateTime ：

```java
ZoneOffset zoneOffSet= ZoneOffset.of("+02:00");
OffsetDateTime date = OffsetDateTime.now(zoneOffSet);
```

### 3.3. 偏移时间

OffsetTime是一个不可变的日期时间对象，表示一个时间，在 ISO-8601 日历系统中通常被视为时-分-秒-偏移量，例如10:15:30+01:00。

此类存储所有时间字段，精度为纳秒，以及区域偏移量。例如， OffsetTime可以存储值“13:45.30.123456789+02:00”。

让我们以 2 小时的偏移量获取当前的OffsetTime ： 

```java
ZoneOffset zoneOffSet = ZoneOffset.of("+02:00");
OffsetTime time = OffsetTime.now(zoneOffSet);
```

## 4。总结

回到焦点，ZoneOffset是根据 GMT/UTC 与给定时间之间的差异来表示时区。这是一种表示时区的便捷方式，尽管还有其他可用的表示方式。

此外，ZoneId和ZoneOffset不仅独立使用，而且还被某些DateTimeJava类使用，例如ZonedDateTime、OffsetDateTime和OffsetTime。