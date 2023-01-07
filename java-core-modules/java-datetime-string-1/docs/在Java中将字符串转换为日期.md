## 1. 概述

在本教程中，我们将探索几种将String对象转换为Date对象的方法。我们将从Java8 中引入的新 日期时间API java.time开始，然后再查看也用于表示日期的旧java.util.Date数据类型。

最后，我们将查看一些使用 Joda-Time 和 Apache Commons Lang DateUtils类进行转换的外部库。

## 延伸阅读：

## [将 java.util.Date 转换为字符串](https://www.baeldung.com/java-util-date-to-string)

了解在Java中将 Date 对象转换为 String 对象的几种方法。

[阅读更多](https://www.baeldung.com/java-util-date-to-string)→

## [检查字符串是否是Java中的有效日期](https://www.baeldung.com/java-string-valid-date)

查看在Java中检查字符串是否为有效日期的不同方法

[阅读更多](https://www.baeldung.com/java-string-valid-date)→

## [在字符串和时间戳之间转换](https://www.baeldung.com/java-string-to-timestamp)

在 LocalDateTime 和Java8 的帮助下了解如何在 String 和 Timestamp 之间进行转换。

[阅读更多](https://www.baeldung.com/java-string-to-timestamp)→

## 2. 将字符串转换为LocalDate或LocalDateTime

[LocalDate](https://www.baeldung.com/java-8-date-time-intro)和LocalDateTime是不可变的日期时间对象，分别表示日期、日期和时间。默认情况下，Java 日期采用 ISO-8601 格式，所以如果我们有任何表示这种格式的日期和时间的字符串，那么我们可以直接使用这些类的parse() API。

有兴趣担任Java开发人员实习生吗？查看[Jooble](https://jooble.org/jobs-java-developer-internship)！

### 2.1. 使用解析API

Date -Time API 提供了parse()方法来解析包含日期和时间信息的字符串。要将 String 对象转换为LocalDate 和 LocalDateTime 对象，String必须表示符合[ISO_LOCAL_DATE](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE)或[ISO_LOCAL_DATE_TIME](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE_TIME)的有效日期或时间。

否则， 将在运行时抛出DateTimeParseException 。

在我们的第一个示例中，让我们将String转换为java.time。本地日期：

```java
LocalDate date = LocalDate.parse("2018-05-05");
```

可以使用与上述类似的方法将String转换为java.time。本地日期时间：

```java
LocalDateTime dateTime = LocalDateTime.parse("2018-05-05T11:50:55");
```

重要的是要注意LocalDate和LocalDateTime对象都是时区不可知的。但是，当我们需要处理特定时区的日期和时间时，我们可以直接使用ZonedDateTime 解析方法来获取特定时区的日期时间：

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-05-05 10:15:30 Europe/Paris", formatter);
```

现在让我们看一下如何将字符串转换为自定义格式。

### 2.2. 将Parse API 与自定义格式化程序一起使用

将具有自定义日期格式的字符串转换为日期对象是Java中广泛使用的操作。

为此，我们将使用DateTimeFormatter类，它提供了许多预定义的格式化程序，并允许我们定义格式化程序。

让我们从使用DateTimeFormatter的预定义格式化程序之一的示例开始 ：

```java
String dateInString = "19590709";
LocalDate date = LocalDate.parse(dateInString, DateTimeFormatter.BASIC_ISO_DATE);
```

在下一个示例中，让我们创建一个应用格式“EEE, MMM d yyyy”的格式化程序。此格式指定三个字符作为星期的全称，一位数字表示月份中的日期，三个字符表示月份，四位数字表示年份。

此格式化程序可识别诸如“ Fri, 3 Jan 2003”或“Wed, 23 Mar 1994 ”之类的字符串：

```java
String dateInString = "Mon, 05 May 1980";
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.ENGLISH);
LocalDate dateTime = LocalDate.parse(dateInString, formatter);
```

### 2.3. 常见的日期和时间模式

让我们看看一些常见的日期和时间模式：

-   y-年份 (1996; 96)
-   M –一年中的月份(7 月；7 月；07)
-   d – 月中的第几天 (1-31)
-   E – 星期中的日期名称(星期五、星期日)
-   a –上午/下午标记(上午、下午)
-   H – 一天中的小时 (0-23)
-   h – 上午/下午的小时 (1-12)
-   m – 小时中的分钟 (0-60)
-   s – 分秒 (0-60)

有关我们可用于指定解析模式的符号的完整列表，请单击[此处](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns)。

如果我们需要将 java.time日期转换为旧的java.util.Date对象，请阅读[本文](https://www.baeldung.com/java-date-to-localdate-and-localdatetime)了解更多详细信息。

## 3. 将字符串转换为java.util.Date

在Java8 之前，Java 日期和时间机制由 java.util.Date、java.util.Calendar和java.util.TimeZone类的旧 API 提供，我们有时仍然需要使用它们。

让我们看看如何将 String 转换为java.util.Date对象：

```java
SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

String dateInString = "7-Jun-2013";
Date date = formatter.parse(dateInString);
```

在上面的示例中，我们首先需要通过传递描述日期和时间格式的模式来构造一个SimpleDateFormat对象。

接下来我们需要调用传递日期字符串的parse()方法。如果传递的String参数与模式的格式不同，则会抛出ParseException 。

### 3.1. 将时区信息添加到 java.util.Date

重要的是要注意java.util.Date没有时区的概念，它只代表自 Unix 纪元时间 - 1970-01-01T00:00:00Z 以来经过的秒数。

但是，当我们直接打印Date对象时，它总是会打印Java默认的系统时区。

在最后一个示例中，我们将了解如何在添加时区信息的同时格式化日期：

```java
SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));

String dateInString = "22-01-2015 10:15:55 AM"; 
Date date = formatter.parse(dateInString);
String formattedDateString = formatter.format(date);
```

我们也可以通过编程方式更改 JVM 时区，但不推荐这样做：

```java
TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
```

## 4. 外部图书馆

现在我们已经很好地理解了如何使用核心Java提供的新旧 API将String对象转换为Date对象，让我们来看看一些外部库。

### 4.1. 乔达时代图书馆

核心 Java日期和时间库的替代方法是[Joda-Time](http://www.joda.org/joda-time/)。尽管作者现在建议用户迁移到java.time (JSR-310)，但如果这不可能，那么Joda-Time 库提供了使用 Date 和 Time 的绝佳替代方案。这个库几乎提供了Java8 Date Time项目支持的所有功能。

该工件可以在 [Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"joda-time")上找到：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.10</version>
</dependency>
```

这是一个使用标准DateTime的简单示例：

```java
DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

String dateInString = "07/06/2013 10:11:59";
DateTime dateTime = DateTime.parse(dateInString, formatter);
```

我们还可以看一个显式设置时区的示例：

```java
DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

String dateInString = "07/06/2013 10:11:59";
DateTime dateTime = DateTime.parse(dateInString, formatter);
DateTime dateTimeWithZone = dateTime.withZone(DateTimeZone.forID("Asia/Kolkata"));
```

### 4.2. Apache Commons Lang – DateUtils

DateUtils类提供了许多有用的实用程序，可以更轻松地使用旧版Calendar和Date对象。

commons-lang3 工件可从 [Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"commons-lang3")获得：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

让我们使用日期模式数组将日期字符串 转换为 java.util.Date ：

```java
String dateInString = "07/06-2013";
Date date = DateUtils.parseDate(dateInString, 
  new String[] { "yyyy-MM-dd HH:mm:ss", "dd/MM-yyyy" });
```

## 5.总结

在本文中，我们展示了几种将字符串转换为不同类型的Date对象(带时间和不带时间)的方法，既可以使用纯Java也可以使用外部库。