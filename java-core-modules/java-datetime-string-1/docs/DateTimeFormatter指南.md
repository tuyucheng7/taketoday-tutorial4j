## 1. 概述

在本教程中，我们将回顾Java8 DateTimeFormatter类及其格式化模式。我们还将讨论此类的可能用例。

我们可以使用DateTimeFormatter 在具有预定义或用户定义模式的应用程序中统一格式化日期和时间。

## 2.具有预定义实例的 DateTimeFormatter

DateTimeFormatter带有多种[预定义的日期/时间格式](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE) ，这些格式遵循 ISO 和 RFC 标准。例如，我们可以使用 ISO_LOCAL_DATE 实例来解析诸如“2018-03-09”之类的日期：

```java
DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.of(2018, 3, 9));
```

要解析带有偏移量的日期，我们可以使用ISO_OFFSET_DATE来获得类似“2018-03-09-03:00”的输出：

```java
DateTimeFormatter.ISO_OFFSET_DATE.format(LocalDate.of(2018, 3, 9).atStartOfDay(ZoneId.of("UTC-3")));
```

DateTimeFormatter类的大多数预定义实例 都关注 ISO-8601 标准。 ISO-8601 是日期和时间格式的国际标准。

[但是，有一个不同的预定义实例可以解析 IETF发布](https://tools.ietf.org/html/rfc1123.html)的RFC-1123，Internet 主机要求：

```java
DateTimeFormatter.RFC_1123_DATE_TIME.format(LocalDate.of(2018, 3, 9).atStartOfDay(ZoneId.of("UTC-3")));
```

此代码段生成“ 2018 年 3 月 9 日星期五 00:00:00 -0300”。'

有时我们必须将收到的日期作为已知格式的字符串进行处理。为此，我们可以使用parse()方法：

```java
LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse("2018-03-09")).plusDays(3);
```

此代码片段的结果是 2018 年 3 月 12 日的 LocalDate表示形式。

## 3. DateTimeFormatter与 FormatStyle

有时我们可能希望以人类可读的方式打印日期。

在这种情况下，我们可以将java.time.format.FormatStyle enum (FULL, LONG, MEDIUM, SHORT) 值与我们的DateTimeFormatter一起使用：

```java
LocalDate anotherSummerDay = LocalDate.of(2016, 8, 23);
System.out.println(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(anotherSummerDay));
System.out.println(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(anotherSummerDay));
System.out.println(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(anotherSummerDay));
System.out.println(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(anotherSummerDay));
```

同一日期的这些不同格式样式的输出是：

```java
Tuesday, August 23, 2016
August 23, 2016
Aug 23, 2016
8/23/16
```

我们还可以为日期和时间使用预定义的格式样式。要将FormatStyle与时间一起使用，我们必须使用ZonedDateTime实例，否则将抛出DateTimeException ：

```java
LocalDate anotherSummerDay = LocalDate.of(2016, 8, 23);
LocalTime anotherTime = LocalTime.of(13, 12, 45);
ZonedDateTime zonedDateTime = ZonedDateTime.of(anotherSummerDay, anotherTime, ZoneId.of("Europe/Helsinki"));
System.out.println(
  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
  .format(zonedDateTime));
System.out.println(
  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
  .format(zonedDateTime));
System.out.println(
  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
  .format(zonedDateTime));
System.out.println(
  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
  .format(zonedDateTime));
```

注意，我们这次使用了DateTimeFormatter的ofLocalizedDateTime()方法。

我们得到的输出是：

```java
Tuesday, August 23, 2016 1:12:45 PM EEST
August 23, 2016 1:12:45 PM EEST
Aug 23, 2016 1:12:45 PM
8/23/16 1:12 PM
```

我们还可以使用FormatStyle来解析日期时间字符串，例如 将其转换为ZonedDateTime。

然后我们可以使用解析后的值来操作日期和时间变量：

```java
ZonedDateTime dateTime = ZonedDateTime.from(
  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
    .parse("Tuesday, August 23, 2016 1:12:45 PM EET"));
System.out.println(dateTime.plusHours(9));
```

这段代码的输出是“2016-08-23T22:12:45+03:00[Europe/Bucharest]”。请注意，时间已更改为“22:12:45”。

## 4.自定义格式的 DateTimeFormatter

预定义和内置的格式化程序和样式可以涵盖很多情况。然而，有时我们需要以不同的方式格式化日期和时间。这是自定义格式模式发挥作用的时候。

### 4.1. 日期的DateTimeFormatter

假设我们想要使用像 31.12.2018 这样的常规欧洲格式来呈现一个java.time.LocalDate对象。为此，我们可以调用工厂方法 DateTimeFormatter。ofPattern(“dd.MM.yyyy”)。

这将创建一个合适的DateTimeFormatter实例，我们可以使用它来格式化我们的日期：

```java
String europeanDatePattern = "dd.MM.yyyy";
DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(europeanDatePattern);
System.out.println(europeanDateFormatter.format(LocalDate.of(2016, 7, 31)));
```

此代码片段的输出将为“31.07.2016”。

我们可以使用许多不同的模式字母来创建适合我们需要的日期格式：

```java
  Symbol  Meaning                     Presentation      Examples
  ------  -------                     ------------      -------
   u       year                        year              2004; 04
   y       year-of-era                 year              2004; 04
   M/L     month-of-year               number/text       7; 07; Jul; July; J
   d       day-of-month                number            10
```

这是DateTimeFormatter类的[官方Java文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html)的摘录。

模式格式中的字母数量很重要。

如果我们对月份使用两个字母的模式，我们将得到一个两位数的月份表示。如果月份数小于 10，则用零填充。当我们不需要提到的用零填充时，我们可以使用单字母模式“M”，它将 January 显示为“1”。

如果我们碰巧使用四字母模式来表示月份，“MMMM”，那么我们将得到一个“完整形式”的表示。在我们的示例中，它将是“七月”。五个字母的模式“MMMMM”将使格式化程序使用“窄格式”。在我们的例子中，将使用“J”。

同样，自定义格式化模式也可用于解析包含日期的字符串：

```java
DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
System.out.println(LocalDate.from(europeanDateFormatter.parse("15.08.2014")).isLeapYear());
```

此代码片段检查日期“ 15.08.2014 ”是否是闰年之一，而事实并非如此。

### 4.2. 时间的DateTimeFormatter

还有一些模式字母可用于时间模式：

```java
  Symbol  Meaning                     Presentation      Examples
  ------  -------                     ------------      -------
   H       hour-of-day (0-23)          number            0
   m       minute-of-hour              number            30
   s       second-of-minute            number            55
   S       fraction-of-second          fraction          978
   n       nano-of-second              number            987654321
```

使用DateTimeFormatter格式化java.time.LocalTime实例非常简单。假设我们要显示用冒号分隔的时间(小时、分钟和秒)：

```java
String timeColonPattern = "HH:mm:ss";
DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern(timeColonPattern);
LocalTime colonTime = LocalTime.of(17, 35, 50);
System.out.println(timeColonFormatter.format(colonTime));
```

这将生成输出“ 17:35:50。”

如果我们想在输出中添加毫秒，我们应该在模式中添加“SSS”：

```java
String timeColonPattern = "HH:mm:ss SSS";
DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern(timeColonPattern);
LocalTime colonTime = LocalTime.of(17, 35, 50).plus(329, ChronoUnit.MILLIS);
System.out.println(timeColonFormatter.format(colonTime));
```

这给了我们输出“ 17:35:50 329。 ”

请注意，“HH”是一种生成 0-23 输出的小时模式。当我们想要显示 AM/PM 时，我们应该使用小写的“hh”表示小时，并添加一个“a”模式：

```java
String timeColonPattern = "hh:mm:ss a";
DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern(timeColonPattern);
LocalTime colonTime = LocalTime.of(17, 35, 50);
System.out.println(timeColonFormatter.format(colonTime));
```

生成的输出是“ 05:35:50 PM。”

我们可能想用我们的自定义格式化程序解析一个时间字符串，并检查它是否在中午之前：

```java
DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
System.out.println(LocalTime.from(timeFormatter.parse("12:25:30 AM")).isBefore(LocalTime.NOON));
```

最后一个片段的输出显示给定时间实际上是在中午之前。

### 4.3. 时区的DateTimeFormatter

通常我们希望看到某个特定日期时间变量的时区。如果我们使用基于纽约的日期时间(UTC -4)，我们可以使用“z”模式字母作为时区名称：

```java
String newYorkDateTimePattern = "dd.MM.yyyy HH:mm z";
DateTimeFormatter newYorkDateFormatter = DateTimeFormatter.ofPattern(newYorkDateTimePattern);
LocalDateTime summerDay = LocalDateTime.of(2016, 7, 31, 14, 15);
System.out.println(newYorkDateFormatter.format(ZonedDateTime.of(summerDay, ZoneId.of("UTC-4"))));
```

这将生成输出“31.07.2016 14:15 UTC-04:00”。

我们可以像之前那样解析带有时区的日期时间字符串：

```java
DateTimeFormatter zonedFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z");
System.out.println(ZonedDateTime.from(zonedFormatter.parse("31.07.2016 14:15 GMT+02:00")).getOffset().getTotalSeconds());
```

正如我们所期望的，此代码的输出是“7200”秒，即 2 小时。

我们必须确保向parse()方法提供正确的日期时间字符串。如果我们将不带时区的“31.07.2016 14:15”传递给最后一个代码片段中的zonedFormatter，我们将得到一个DateTimeParseException。

### 4.4. Instant 的DateTimeFormatter

DateTimeFormatter带有一个很棒的 ISO 即时格式化程序，称为ISO_INSTANT。顾名思义，此格式化程序提供了一种方便的方法来格式化或解析 UTC 中的瞬间。

根据[官方文档](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/time/format/DateTimeFormatter.html#ISO_INSTANT)，如果不指定时区，则无法将瞬间格式化为日期或时间。因此，尝试在LocalDateTime或LocalDate对象上使用ISO_INSTANT将导致异常：

```java
@Test(expected = UnsupportedTemporalTypeException.class)
public void shouldExpectAnExceptionIfInputIsLocalDateTime() {
    DateTimeFormatter.ISO_INSTANT.format(LocalDateTime.now());
}
```

但是，我们可以毫无问题地使用ISO_INSTANT来格式化ZonedDateTime实例：

```java
@Test
public void shouldPrintFormattedZonedDateTime() {
    ZonedDateTime zonedDateTime = ZonedDateTime.of(2021, 02, 15, 0, 0, 0, 0, ZoneId.of("Europe/Paris"));
    String formattedZonedDateTime = DateTimeFormatter.ISO_INSTANT.format(zonedDateTime);
    
    Assert.assertEquals("2021-02-14T23:00:00Z", formattedZonedDateTime);
}
```

如我们所见，我们创建了带有“Europe/Paris”时区的ZonedDateTime 。但是，格式化的结果是 UTC。

同样，在解析为ZonedDateTime时，我们需要指定时区：

```java
@Test
public void shouldParseZonedDateTime() {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
    ZonedDateTime zonedDateTime = ZonedDateTime.parse("2021-10-01T05:06:20Z", formatter);
    
    Assert.assertEquals("2021-10-01T05:06:20Z", DateTimeFormatter.ISO_INSTANT.format(zonedDateTime));
}
```

如果不这样做将导致[DateTimeParseException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeParseException.html)：

```java
@Test(expected = DateTimeParseException.class)
public void shouldExpectAnExceptionIfTimeZoneIsMissing() {
    ZonedDateTime zonedDateTime = ZonedDateTime.parse("2021-11-01T05:06:20Z", DateTimeFormatter.ISO_INSTANT);
}
```

还值得一提的是，解析需要至少指定秒字段。否则，将抛出DateTimeParseException 。

## 5.总结

在本文中，我们讨论了如何使用DateTimeFormatter 类来格式化日期和时间。我们还研究了在处理日期时间实例时经常出现的真实示例模式。

[我们可以在之前的教程](https://www.baeldung.com/java-8-date-time-intro)中找到更多关于Java8 的Date/Time API 的信息。