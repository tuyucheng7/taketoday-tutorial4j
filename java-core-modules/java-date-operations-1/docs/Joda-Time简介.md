## 1. 概述

Joda-Time 是Java8 发布之前使用最广泛的日期和时间处理库。它的目的是提供一个直观的 API 来处理日期和时间，同时解决JavaDate/Time API 中存在的设计问题。

随着Java8 版本的发布，JDK 核心引入了该库中实现的核心概念。新的日期和时间 API 位于java.time包 ( [JSR-310](https://jcp.org/en/jsr/detail?id=310) ) 中。可以在[本文](https://www.baeldung.com/java-8-date-time-intro)中找到这些功能的概述。

Java 8 发布后，作者认为项目基本完成，建议尽可能使用Java8 API。

## 2. 为什么要使用 Joda-Time？

在Java8 之前，日期/时间 API 存在多个设计问题。

问题之一是 Date 和SimpleDateFormatter 类不是线程安全的。为了解决这个问题，Joda-Time 使用不可变类来处理日期和时间。

Date类 不表示实际日期，而是指定一个时间点，精度为毫秒。Date中的年份从 1900 年开始，而大多数日期操作通常使用 Epoch 时间，从 1970 年 1 月 1 日开始。

此外，日期的日、月和年偏移量是违反直觉的。天从 0 开始，而月份从 1 开始。要访问其中任何一个，我们必须使用 Calendar类。Joda-Time 提供了一个简洁流畅的 API 来处理日期和时间。

Joda-Time 还提供对八种日历系统的支持，而Java仅提供两种：公历 - java.util.GregorianCalendar和日语 - java.util.JapaneseImperialCalendar。

##  3.设置

要包含 Joda-Time 库的功能，我们需要从[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"joda-time" AND a%3A"joda-time")添加以下依赖项：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.10</version>
</dependency>
```

##  4.图书馆概况

Joda-Time使用org.joda.time包中的类 对日期和时间的概念进行建模。

在这些类中，最常用的是：

-   LocalDate – 表示没有时间的日期
-   LocalTime – 表示没有时区的时间
-   LocalDateTime – 表示没有时区的日期和时间
-   Instant – 表示从 1970-01-01T00:00:00Z 的Java纪元开始的精确时间点(以毫秒为单位)
-   持续时间——表示两个时间点之间的持续时间(以毫秒为单位)
-   Period – 类似于Duration，但允许访问日期和时间对象的各个组成部分，如年、月、日等。
-   Interval——表示2个瞬间之间的时间间隔

其他重要功能是日期解析器和格式化程序。这些可以在 org.joda.time.format 包中找到。

日历系统和时区特定类可以在org.joda.time.chrono和org.joda.time.tz 包中找到。

让我们看一些示例，在这些示例中我们使用 Joda-Time 的关键功能来处理日期和时间。

## 5. 表示日期和时间

### 5.1. 当前日期和时间

当前日期，没有时间信息，可以通过使用LocalDate类 的now()方法获得：

```java
LocalDate currentDate = LocalDate.now();
```

当我们只需要当前时间而不需要日期信息时，我们可以使用 LocalTime类：

```java
LocalTime currentTime = LocalTime.now();
```

要在不考虑时区的情况下获得当前日期和时间的表示，我们可以使用LocalDateTime：

```java
LocalDateTime currentDateAndTime = LocalDateTime.now();
```

现在，使用currentDateAndTime，我们可以将其转换为其他类型的对象建模日期和时间。

我们可以使用toDateTime()方法获得一个DateTime对象(考虑了时区 ) 。当不需要时间时，我们可以使用 toLocalDate( )方法将其转换为LocalDate，而当我们只需要时间时，我们可以使用toLocalTime()来获取LocalTime对象：

```java
DateTime dateTime = currentDateAndTime.toDateTime();
LocalDate localDate = currentDateAndTime.toLocalDate();
LocalTime localTime = currentDateAndTime.toLocalTime();
```

以上所有方法都有一个重载方法，它接受一个DateTimeZone对象来帮助我们表示指定时区的日期或时间：

```java
LocalDate currentDate = LocalDate.now(DateTimeZone.forID("America/Chicago"));
```

此外，Joda-Time 还提供与Java日期和时间 API 的出色集成。构造函数接受一个java.util.Date对象，而且我们可以使用 toDate()方法返回一个java.util.Date对象：

```java
LocalDateTime currentDateTimeFromJavaDate = new LocalDateTime(new Date());
Date currentJavaDate = currentDateTimeFromJavaDate.toDate();
```

### 5.2. 自定义日期和时间

为了表示自定义日期和时间，Joda-Time 为我们提供了几个构造函数。我们可以指定以下对象：

-   瞬间_
-   Java日期对象
-   使用 ISO 格式的日期和时间的字符串表示形式
-   日期和时间的组成部分：年、月、日、时、分、秒、毫秒

```java
Date oneMinuteAgoDate = new Date(System.currentTimeMillis() - (60  1000));
Instant oneMinutesAgoInstant = new Instant(oneMinuteAgoDate);

DateTime customDateTimeFromInstant = new DateTime(oneMinutesAgoInstant);
DateTime customDateTimeFromJavaDate = new DateTime(oneMinuteAgoDate);
DateTime customDateTimeFromString = new DateTime("2018-05-05T10:11:12.123");
DateTime customDateTimeFromParts = new DateTime(2018, 5, 5, 10, 11, 12, 123);

```

我们可以定义自定义日期和时间的另一种方法是解析 ISO 格式的日期和时间的给定字符串表示形式：

```java
DateTime parsedDateTime = DateTime.parse("2018-05-05T10:11:12.123");
```

我们还可以通过定义自定义DateTimeFormatter来解析日期和时间的自定义表示：

```java
DateTimeFormatter dateTimeFormatter
  = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
DateTime parsedDateTimeUsingFormatter
  = DateTime.parse("05/05/2018 10:11:12", dateTimeFormatter);
```

## 6. 使用日期和时间

### 6.1. 使用即时

Instant表示从 1970-01-01T00:00:00Z 到给定时刻的毫秒数。例如，可以使用默认构造函数或方法now()获取当前时刻：

```java
Instant instant = new Instant();
Instant.now();
```

要为自定义时刻 创建Instant ，我们可以使用构造函数之一或使用方法ofEpochMilli()和ofEpochSecond()：

```java
Instant instantFromEpochMilli
  = Instant.ofEpochMilli(milliesFromEpochTime);
Instant instantFromEpocSeconds
  = Instant.ofEpochSecond(secondsFromEpochTime);
```

构造函数接受表示 ISO 格式日期和时间的字符串、Java日期或表示从 1970-01-01T00:00:00Z 开始的毫秒数的长值：

```java
Instant instantFromString
  = new Instant("2018-05-05T10:11:12");
Instant instantFromDate
  = new Instant(oneMinuteAgoDate);
Instant instantFromTimestamp
  = new Instant(System.currentTimeMillis() - (60  1000));
```

当日期和时间表示为 字符串时，我们可以选择使用我们想要的格式解析字符串：

```java
Instant parsedInstant
  = Instant.parse("05/05/2018 10:11:12", dateTimeFormatter);
```

现在我们知道Instant代表什么以及我们如何创建它，让我们看看如何使用它。

为了与Instant对象进行比较，我们可以使用compareTo()因为它实现了Comparable接口，但我们也可以使用ReadableInstant接口中提供的 Joda-Time API 方法， Instant也实现了该接口：

```java
assertTrue(instantNow.compareTo(oneMinuteAgoInstant) > 0);
assertTrue(instantNow.isAfter(oneMinuteAgoInstant));
assertTrue(oneMinuteAgoInstant.isBefore(instantNow));
assertTrue(oneMinuteAgoInstant.isBeforeNow());
assertFalse(oneMinuteAgoInstant.isEqual(instantNow));
```

另一个有用的功能是 Instant 可以转换为DateTime对象或事件JavaDate：

```java
DateTime dateTimeFromInstant = instant.toDateTime();
Date javaDateFromInstant = instant.toDate();
```

当我们需要访问日期和时间的部分内容时，例如年、小时等，我们可以使用get()方法并指定一个 DateTimeField：

```java
int year = instant.get(DateTimeFieldType.year());
int month = instant.get(DateTimeFieldType.monthOfYear());
int day = instant.get(DateTimeFieldType.dayOfMonth());
int hour = instant.get(DateTimeFieldType.hourOfDay());
```

现在我们介绍了Instant类，让我们看一些如何使用Duration、Period和Interval的例子。

### 6.2. 使用Duration、Period和Interval

Duration表示两个时间点之间的时间(以毫秒为单位)，或者在这种情况下它可以是两个Instants。当我们需要在不考虑时间顺序和时区的情况下向另一个Instant添加或从中减去特定时间时，我们将使用它：

```java
long currentTimestamp = System.currentTimeMillis();
long oneHourAgo = currentTimestamp - 24601000;
Duration duration = new Duration(oneHourAgo, currentTimestamp);
Instant.now().plus(duration);
```

此外，我们可以确定持续时间代表多少天、小时、分钟、秒或毫秒：

```java
long durationInDays = duration.getStandardDays();
long durationInHours = duration.getStandardHours();
long durationInMinutes = duration.getStandardMinutes();
long durationInSeconds = duration.getStandardSeconds();
long durationInMilli = duration.getMillis();
```

Period 和 Duration之间的主要区别在于 ，Period是根据其日期和时间组件(年、月、小时等)定义的，并不代表精确的毫秒数。使用Period时， 日期和时间计算将考虑时区和夏令时。

例如，将 1 个月的期间添加到 2 月 1 日将导致日期表示为 3 月 1 日。通过使用Period，图书馆将考虑闰年。

如果我们要使用Duration，结果将不正确，因为Duration表示不考虑时间顺序或时区的固定时间量：

```java
Period period = new Period().withMonths(1);
LocalDateTime datePlusPeriod = localDateTime.plus(period);
```

Interval，顾名思义，表示两个Instant对象表示的两个固定时间点之间的日期和时间间隔：

```java
Interval interval = new Interval(oneMinuteAgoInstant, instantNow);
```

当我们需要检查两个区间是否重叠或计算它们之间的差距时，该类很有用。overlap ()方法将返回重叠的Interval或当它们不重叠时返回null ：

```java
Instant startInterval1 = new Instant("2018-05-05T09:00:00.000");
Instant endInterval1 = new Instant("2018-05-05T11:00:00.000");
Interval interval1 = new Interval(startInterval1, endInterval1);
        
Instant startInterval2 = new Instant("2018-05-05T10:00:00.000");
Instant endInterval2 = new Instant("2018-05-05T11:00:00.000");
Interval interval2 = new Interval(startInterval2, endInterval2);

Interval overlappingInterval = interval1.overlap(interval2);
```

可以使用gap()方法计算区间之间的差异，当我们想知道一个区间的结束是否等于另一个区间的开始时，我们可以使用 abuts()方法：

```java
assertTrue(interval1.abuts(new Interval(
  new Instant("2018-05-05T11:00:00.000"),
  new Instant("2018-05-05T13:00:00.000"))));
```

### 6.3. 日期和时间操作

一些最常见的操作是加、减和转换日期和时间。该库为每个类LocalDate、LocalTime、LocalDateTime和DateTime提供特定方法。重要的是要注意这些类是不可变的，因此每个方法调用都会创建一个新的类型对象。

让我们为当前时刻获取LocalDateTime 并尝试更改它的值：

```java
LocalDateTime currentLocalDateTime = LocalDateTime.now();
```

要向 currentLocalDateTime添加额外的一天，我们使用 plusDays()方法：

```java
LocalDateTime nextDayDateTime = currentLocalDateTime.plusDays(1);
```

我们还可以使用plus()方法将Period或Duration添加到我们的currentLocalDateTime：

```java
Period oneMonth = new Period().withMonths(1);
LocalDateTime nextMonthDateTime = currentLocalDateTime.plus(oneMonth);
```

其他日期和时间组件的方法类似，例如， 用于添加额外年份的 plusYears ( )，用于添加更多秒数的 plusSeconds() 等。

要从 currentLocalDateTime中减去一天，我们可以使用minusDays()方法：

```java
LocalDateTime previousDayLocalDateTime
  = currentLocalDateTime.minusDays(1);
```

此外，在进行日期和时间的计算时，我们还可以设置日期或时间的各个部分。例如，可以使用withHourOfDay()方法将小时设置为 10。以前缀“with”开头的其他方法可用于设置该日期或时间的组件：

```java
LocalDateTime currentDateAtHour10 = currentLocalDateTime
  .withHourOfDay(0)
  .withMinuteOfHour(0)
  .withSecondOfMinute(0)
  .withMillisOfSecond(0);
```

另一个重要方面是我们可以将日期和时间类类型转换为另一种类型。为此，我们可以使用库提供的特定方法：

-   toDateTime() – 将LocalDateTime转换为DateTime对象
-   toLocalDate() – 将LocalDateTime转换为LocalDate对象
-   toLocalTime() – 将 LocalDateTime 转换为 LocalTime 对象
-   toDate() – 将LocalDateTime转换为JavaDate对象

## 7. 使用时区

Joda-Time 使我们可以轻松地处理不同的时区并在它们之间进行更改。我们有 DateTimeZone抽象类，用于表示有关时区的所有方面。

Joda-Time 使用的默认时区是从user.timezoneJava系统属性中选择的。库 API 允许我们为每个类或计算单独指定应使用的时区。例如，我们可以创建一个 LocalDateTime 对象

当我们知道我们将在整个应用程序中使用特定时区时，我们可以设置默认时区：

```java
DateTimeZone.setDefault(DateTimeZone.UTC);
```

从现在开始，所有的日期和时间操作，如果没有特别说明，都将以 UTC 时区表示。

要查看所有可用时区，我们可以使用方法getAvailableIDs()：

```java
DateTimeZone.getAvailableIDs()
```

当我们需要表示特定时区的日期或时间时，我们可以使用任何类LocalTime、LocalDate、LocalDateTime、DateTime并在构造函数中指定 DateTimeZone 对象：

```java
DateTime dateTimeInChicago
  = new DateTime(DateTimeZone.forID("America/Chicago"));
DateTime dateTimeInBucharest
  = new DateTime(DateTimeZone.forID("Europe/Bucharest"));
LocalDateTime localDateTimeInChicago
  = new LocalDateTime(DateTimeZone.forID("America/Chicago"));
```

此外，在这些类之间进行转换时，我们可以指定所需的时区。toDateTime()方法 接受 DateTimeZone对象，而 toDate()方法 接受 java.util.TimeZone 对象：

```java
DateTime convertedDateTime
  = localDateTimeInChicago.toDateTime(DateTimeZone.forID("Europe/Bucharest"));
Date convertedDate
  = localDateTimeInChicago.toDate(TimeZone.getTimeZone("Europe/Bucharest"));
```

## 八、总结

Joda-Time 是一个很棒的库，其主要目标是解决 JDK 中有关日期和时间操作的问题。它很快成为日期和时间处理的实际库，最近它的主要概念在Java8 中被引入。

值得注意的是，作者认为它“是一个基本完成的项目”，并建议迁移现有代码以使用Java8 实现。