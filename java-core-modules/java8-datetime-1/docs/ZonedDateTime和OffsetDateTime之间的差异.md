## 1. 概述

ZonedDateTime和OffsetDateTime是[Java 8 DateTime API中非常流行的类](https://www.baeldung.com/java-8-date-time-intro)。此外，两者都在时间轴上存储一个瞬间，精度可达纳秒。而且，起初，在它们之间进行选择可能会让人感到困惑。

在本快速教程中，我们将了解 ZonedDateTime和OffsetDateTime之间的区别。

## 2.分区日期时间

ZonedDateTime是 ISO-8601 日历系统中带有时区的日期时间的不可变表示，例如2007-12-03T10:15:30+01:00 Europe/Paris。它持有等同于三个独立对象的状态：一个LocalDateTime、一个ZoneId和解析的[ZoneOffset](https://www.baeldung.com/java-zone-offset)。 

此处，ZoneId确定偏移量如何以及何时更改。因此，不能自由设置偏移量，因为区域控制哪些偏移量有效。

要获取特定区域的当前ZonedDateTime ，我们将使用：

```java
ZoneId zone = ZoneId.of("Europe/Berlin");
ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);
```

ZonedDateTime类还提供了将给定日期从一个时区转换为另一个时区的内置方法：

```java
ZonedDateTime destZonedDateTime = sourceZonedDateTime.withZoneSameInstant(destZoneId);
```

最后，它完全支持夏令时并处理夏令时调整。当我们想要在特定时区显示日期时间字段时，它通常会派上用场。

## 3.偏移日期时间

OffsetDateTime是日期时间的不可变表示，在 ISO-8601 日历系统中具有与 UTC/格林威治的偏移量，例如2007-12-03T10:15:30+01:00。换句话说，它存储 所有日期和时间字段，精确到纳秒，以及与 GMT/UTC 的偏移量。

让我们获取与 GMT/UTC 相差两小时的当前 OffsetDateTime ：

```java
ZoneOffset zoneOffSet= ZoneOffset.of("+02:00");
OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneOffSet);
```

## 4.主要区别

首先，直接比较两个具有完整时区信息的日期(没有转换)是没有意义的。因此，我们应该始终更喜欢将OffsetDateTime存储在数据库中，而不是 ZonedDateTime，因为具有本地时间偏移的日期始终表示相同的时刻。

此外，与 ZonedDateTime不同，在存储OffsetDateTime的列上添加索引 不会改变日期的含义。

让我们快速总结一下主要区别。

分区日期时间：

-   存储所有日期和时间字段，精度为纳秒，以及时区，时区偏移量用于处理不明确的本地日期时间
-   不能自由设置偏移量，因为区域控制有效偏移值
-   完全了解 DST 并处理夏令时调整
-   在用户特定时区中显示日期时间字段时派上用场

偏移日期时间：

-   存储所有日期和时间字段，精度为纳秒，以及与 GMT/UTC 的偏移量(无时区信息)
-   应该用于在数据库中存储日期或通过网络进行通信

## 5.总结

在本教程中，我们介绍了 ZonedDateTime和 OffsetDateTime之间的差异。