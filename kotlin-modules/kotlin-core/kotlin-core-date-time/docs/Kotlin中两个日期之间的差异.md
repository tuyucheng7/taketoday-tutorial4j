## 1. 概述

在本教程中，我们将学习如何在 Kotlin 中计算两个日期之间的差异。我们将看到如何根据年、月、日等来确定日期差异。

## 2. 在 Java SE 8 版本中使用日期时间包

Java SE 8 版本中引入的内置日期时间包java.time基于 ISO 日历系统作为默认日历。这个包提供了类和方法来表示和操作日期和时间值。

Date-Time API 中的核心类具有LocalDate、LocalDateTime、ZonedDateTime和OffsetDateTime等名称。

LocalDate是一个不可变的类，默认表示 ISO-8601 日历系统 ( yyyy-MM-dd ) 格式的日期。但是，此类不存储时间或时区信息。

该包有一个名为Period的类，它根据年、月和日对数量或时间量进行建模。

DateTimeFormatter类具有根据给定的日期时间模式[解析日期时间和格式化日期时间的方法。](https://www.baeldung.com/kotlin/string-to-date)

我们将使用DateTimeFormatter类将两个日期解析为LocalDate日期实例。然后我们将使用Period来找出它们之间的区别。

让我们看一下使用java.time包类查找日期差异的代码：

```kotlin
val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")

val from = LocalDate.parse(fromDate, dateFormatter)
val to = LocalDate.parse(toDate, dateFormatter)

val period = Period.between(from, to)
val years = period.years
val months = period.months
val days = period.days
```

在上面的示例代码中，fromDate和toDate是以MM/dd/yyyy格式表示为字符串的日期。

让我们看一些测试断言，看看02/01/2007和11/25/2022之间的日期差异的预期输出：

```scss
assertEquals(15, years)
assertEquals(9, months)
assertEquals(24, days)
```

## 3. 使用 Joda-Time 外部库

[引入Joda-Time 库](https://www.baeldung.com/joda-time)是为了解决 SE 8 发布之前 Java 日期时间的糟糕实现。它是 Java SE 8 之前的 Java 应用程序的 goto 日期时间库。事实上，我们将看到 Joda-Time 和 Java SE 8 日期时间类之间的相似之处。

要包含 Joda-Time 库的功能，我们需要从[Maven Central](https://search.maven.org/search?q=g:joda-time AND a:joda-time)添加以下依赖项：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.12.1</version>
</dependency>
```

org.joda.time.LocalDate是一个不可变的日期时间类，表示没有时区的日期。

org.joda.time.Period是根据一组持续时间字段指定的不可变时间段。此外，PeriodType类定义了支持的字段。因此，默认周期类型支持年、月、周、日、小时、分钟、秒和毫秒。

org.joda.time.format.DateTimeFormatter控制日期时间与字符串的打印和解析。

让我们看看如何使用 Joda-Time 库查找日期差异：

```kotlin
val dateFormatter: org.joda.time.format.DateTimeFormatter? =  DateTimeFormat.forPattern("MM/dd/yyyy")

val from = org.joda.time.LocalDate.parse(fromDate, dateFormatter)
val to = org.joda.time.LocalDate.parse(toDate, dateFormatter)

val period = org.joda.time.Period(from, to)

val years = period.years
val months = period.months
val days = period.weeks  7 + period.days
```

在上面的代码中，我们看到 Joda-Time 库中的Period类支持周。因此，为了计算月后剩余的天数，我们得到周数并将其转换为天数。然后，我们将其添加到实际天数。

## 4。总结

在本文中，我们学习了如何使用 Java Date-Time(Java SE 8 内置)和 Joda-Time(外部)库来计算两个给定日期之间的差异。此外，我们注意到两个库之间的相似之处。