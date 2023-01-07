## 1. 概述

在本教程中，我们了解Date 和OffsetDateTime之间的区别。我们还学习了如何从一种转换为另一种。

## 2. Date和OffsetDateTime 的区别

OffsetDateTime作为[java.util.Date](https://www.baeldung.com/java-util-date-sql-date)的现代替代品在 JDK 8 中引入。

OffsetDateTime是一个线程安全的类，它以纳秒的精度存储日期和时间。另一方面， Date不是线程安全的，它存储的时间精确到毫秒。

OffsetDateTime是一个基于值的类，这意味着我们[在比较引用时](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/doc-files/ValueBased.html)[需要使用equals](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/doc-files/ValueBased.html)而不是典型的 == 。

OffsetDateTime的toString方法的输出是 ISO-8601 格式，而Date的toString是自定义的非标准格式。

让我们调用两个类的toString来查看区别：

```plaintext
Date: Sat Oct 19 17:12:30 2019
OffsetDateTime: 2019-10-19T17:12:30.174Z
```

日期不能存储时区和相应的偏移量。Date对象唯一包含的是自 1970 年 1 月 1 日 00:00:00 UTC 以来的毫秒数，因此如果我们的时间不是 UTC，我们应该[将时区存储在辅助类](https://www.baeldung.com/java-set-date-time-zone)中。相反，OffsetDateTime在内部存储了[ZoneOffset](https://www.baeldung.com/java-zone-offset)。

## 3.将日期转换为OffsetDateTime

将Date转换为OffsetDateTime非常简单。如果我们的日期是 UTC，我们可以用一个表达式转换它：

```java
Date date = new Date();
OffsetDateTime offsetDateTime = date.toInstant()
  .atOffset(ZoneOffset.UTC);
```

如果原始Date不是 UTC，我们可以提供偏移量(存储在辅助对象中，因为如前所述，Date 类无法存储时区)。

假设我们的原始日期是 +3:30(德黑兰时间)：

```java
int hour = 3;
int minute = 30;
offsetDateTime = date.toInstant()
  .atOffset(ZoneOffset.ofHoursMinutes(hour, minute));
```

OffsetDateTime提供了[许多有用的方法](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/OffsetDateTime.html)，可以在以后使用。例如，我们可以简单地获取 DayOfWeek()、getDayOfMonth()和getDayOfYear()。使用isAfter和isBefore方法比较两个 OffsetDateTime 对象也很容易。

最重要的是，完全避免使用已弃用的Date类是一种很好的做法。

## 4。总结

在本教程中，我们了解了从Date转换为OffsetDateTime是多么简单。