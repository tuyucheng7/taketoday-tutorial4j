## 1. 概述

在本教程中，你将学习如何重构代码以利用Java8 中引入的新日期时间 API。

## 2. 新 API 概览

在Java中处理日期曾经很困难。JDK 提供的旧日期库仅包含三个类：java.util.Date、java.util.Calendar和java.util.Timezone。

这些只适用于最基本的任务。对于任何复杂的事物，开发人员都必须使用第三方库或编写大量自定义代码。

Java 8 引入了一个全新的日期时间 API ( java.util.time. )，它大致基于名为 JodaTime 的流行Java库。这个新的 API 极大地简化了日期和时间处理，并修复了旧日期库的许多缺点。

### 1.1. API清晰度

新 API 的第一个优势是清晰——API非常清晰、简洁且易于理解。它没有在旧库中发现的很多不一致之处，例如字段编号(在日历中，月份是从零开始的，但星期几是从一开始的)。

### 1.2. API 灵活性

另一个优势是灵活性——处理多种时间表示。旧的日期库只包含一个时间表示类——java.util.Date，尽管它的名字，实际上是一个时间戳。它只存储自 Unix 纪元以来经过的毫秒数。

新的 API 有许多不同的时间表示，每一种都适用于不同的用例：

-   Instant——代表一个时间点(timestamp)
-   LocalDate——代表一个日期(年、月、日)
-   LocalDateTime – 与LocalDate相同，但包括纳秒精度的时间
-   OffsetDateTime – 与LocalDateTime相同，但具有时区偏移
-   LocalTime – 具有纳秒精度且没有日期信息的时间
-   ZonedDateTime – 与OffsetDateTime相同，但包括时区 ID
-   OffsetLocalTime – 与LocalTime相同，但具有时区偏移
-   MonthDay – 月份和日期，没有年份或时间
-   YearMonth – 月份和年份，没有日期或时间
-   持续时间——以秒、分钟和小时表示的时间量。具有纳秒精度
-   期间——以天、月和年表示的时间量

### 1.3. 不变性和线程安全

另一个优点是Java8 Date Time API 中的所有时间表示都是不可变的，因此是线程安全的。

所有变异方法都返回一个新副本，而不是修改原始对象的状态。

java.util.Date等旧类不是线程安全的，可能会引入非常微妙的并发错误。

### 1.4. 方法链接

所有变异方法都可以链接在一起，允许在一行代码中实现复杂的转换。

```java
ZonedDateTime nextFriday = LocalDateTime.now()
  .plusHours(1)
  .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
  .atZone(ZoneId.of("PST"));

```

## 2.例子

下面的示例将演示如何使用新旧 API 执行常见任务。

获取当前时间

```java
// Old
Date now = new Date();

// New
ZonedDateTime now = ZonedDateTime.now();


```

代表具体时间

```java
// Old
Date birthDay = new GregorianCalendar(1990, Calendar.DECEMBER, 15).getTime();

// New
LocalDate birthDay = LocalDate.of(1990, Month.DECEMBER, 15);

```

提取特定字段

```java
// Old
int month = new GregorianCalendar().get(Calendar.MONTH);

// New
Month month = LocalDateTime.now().getMonth();

```

加减时间

```java
// Old
GregorianCalendar calendar = new GregorianCalendar();
calendar.add(Calendar.HOUR_OF_DAY, -5);
Date fiveHoursBefore = calendar.getTime();

// New
LocalDateTime fiveHoursBefore = LocalDateTime.now().minusHours(5);

```

更改特定字段

```java
// Old
GregorianCalendar calendar = new GregorianCalendar();
calendar.set(Calendar.MONTH, Calendar.JUNE);
Date inJune = calendar.getTime();

// New
LocalDateTime inJune = LocalDateTime.now().withMonth(Month.JUNE.getValue());

```

截断

截断会重置所有小于指定字段的时间字段。在下面的示例中，分钟和下面的所有内容都将设置为零

```java
// Old
Calendar now = Calendar.getInstance();
now.set(Calendar.MINUTE, 0);
now.set(Calendar.SECOND, 0);
now.set(Calendar.MILLISECOND, 0);
Date truncated = now.getTime();

// New
LocalTime truncated = LocalTime.now().truncatedTo(ChronoUnit.HOURS);

```

时区转换

```java
// Old
GregorianCalendar calendar = new GregorianCalendar();
calendar.setTimeZone(TimeZone.getTimeZone("CET"));
Date centralEastern = calendar.getTime();

// New
ZonedDateTime centralEastern = LocalDateTime.now().atZone(ZoneId.of("CET"));

```

获取两个时间点之间的时间跨度

```java
// Old
GregorianCalendar calendar = new GregorianCalendar();
Date now = new Date();
calendar.add(Calendar.HOUR, 1);
Date hourLater = calendar.getTime();
long elapsed = hourLater.getTime() - now.getTime();

// New
LocalDateTime now = LocalDateTime.now();
LocalDateTime hourLater = LocalDateTime.now().plusHours(1);
Duration span = Duration.between(now, hourLater);

```

时间格式化和解析

DateTimeFormatter 是旧的 SimpleDateFormat 的替代品，它是线程安全的并提供额外的功能。

```java
// Old
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
Date now = new Date();
String formattedDate = dateFormat.format(now);
Date parsedDate = dateFormat.parse(formattedDate);

// New
LocalDate now = LocalDate.now();
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
String formattedDate = now.format(formatter);
LocalDate parsedDate = LocalDate.parse(formattedDate, formatter);

```

一个月中的天数

```java
// Old
Calendar calendar = new GregorianCalendar(1990, Calendar.FEBRUARY, 20);
int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

// New
int daysInMonth = YearMonth.of(1990, 2).lengthOfMonth();
```

## 3. 与遗留代码交互

在许多情况下，用户可能需要确保与依赖旧日期库的第三方库的互操作性。

在Java8 中，旧的日期库类已通过将它们转换为来自新日期 API 的相应对象的方法进行了扩展。
新类提供类似的功能。

```java
Instant instantFromCalendar = GregorianCalendar.getInstance().toInstant();
ZonedDateTime zonedDateTimeFromCalendar = new GregorianCalendar().toZonedDateTime();
Date dateFromInstant = Date.from(Instant.now());
GregorianCalendar calendarFromZonedDateTime = GregorianCalendar.from(ZonedDateTime.now());
Instant instantFromDate = new Date().toInstant();
ZoneId zoneIdFromTimeZone = TimeZone.getTimeZone("PST").toZoneId();

```

## 4。总结

在本文中，我们探讨了Java8 中可用的新日期时间 API。我们了解了它与已弃用的 API 相比的优势，并使用多个示例指出了差异。

请注意，我们几乎不了解新日期时间 API 的功能。请务必通读官方文档以发现新 API 提供的全部工具。