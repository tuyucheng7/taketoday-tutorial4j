## 1. 概述

Java 8 为日期和时间引入了新的 API，以解决旧的java.util.Date和java.util.Calendar的缺点。

在本教程中，让我们从现有日期和日历API 中的问题开始，并讨论新的Java8日期和时间API 如何解决这些问题。

我们还将了解新Java8 项目的一些核心类，它们是java.time包的一部分，例如LocalDate、LocalTime、LocalDateTime、ZonedDateTime、Period、Duration及其支持的 API。

## 延伸阅读：

## [在 Spring 中使用日期参数](https://www.baeldung.com/spring-date-parameters)

了解如何在 Spring MVC 中使用日期参数

[阅读更多](https://www.baeldung.com/spring-date-parameters)→

## [检查字符串是否是Java中的有效日期](https://www.baeldung.com/java-string-valid-date)

查看在Java中检查字符串是否为有效日期的不同方法

[阅读更多](https://www.baeldung.com/java-string-valid-date)→

## 2. 现有日期/时间API的问题

-   线程安全——Date和Calendar类不是线程安全的，这让开发人员不得不处理令人头疼的难以调试的并发问题，并编写额外的代码来处理线程安全。相反，Java 8 中引入的新日期和时间API 是不可变的和线程安全的，从而使开发人员不再为并发问题头疼。
-   API 设计和易于理解——Date和Calendar API 的设计很差，没有足够的方法来执行日常操作。新的日期/时间API 以 ISO 为中心，并遵循日期、时间、持续时间和周期的一致域模型。有各种各样的实用方法支持最常见的操作。
-   ZonedDate和Time——开发人员必须编写额外的逻辑来处理旧 API 的时区逻辑，而使用新 API 时区的处理可以通过Local和ZonedDate / Time API 完成。

## 3. 使用LocalDate、LocalTime和LocalDateTime

最常用的类是LocalDate、LocalTime和LocalDateTime。正如它们的名称所示，它们代表观察者上下文中的本地日期/时间。

当不需要在上下文中明确指定时区时，我们主要使用这些类。作为本节的一部分，我们将介绍最常用的 API。

### 3.1. 使用LocalDate

LocalDate表示不带时间的 ISO 格式 (yyyy-MM-dd) 的日期。我们可以用它来存储生日和发薪日等日期。

可以从系统时钟创建当前日期的实例：

```java
LocalDate localDate = LocalDate.now();
```

而我们可以通过of方法或者parse方法得到代表特定日月年的LocalDate。

例如，这些代码片段代表2015 年 2 月 20 日的LocalDate ：

```java
LocalDate.of(2015, 02, 20);

LocalDate.parse("2015-02-20");
```

LocalDate提供了多种实用方法来获取各种信息。让我们快速浏览一下其中的一些 API 方法。

以下代码片段获取当前本地日期并添加一天：

```java
LocalDate tomorrow = LocalDate.now().plusDays(1);
```

此示例获取当前日期并减去一个月。请注意它如何接受枚举作为时间单位：

```java
LocalDate previousMonthSameDay = LocalDate.now().minus(1, ChronoUnit.MONTHS);
```

在下面的两个代码示例中，我们解析日期“2016-06-12”并分别获取星期几和月份中的第几天。注意返回值——第一个是表示DayOfWeek的对象，而第二个是表示月份序数值的int ：

```java
DayOfWeek sunday = LocalDate.parse("2016-06-12").getDayOfWeek();

int twelve = LocalDate.parse("2016-06-12").getDayOfMonth();
```

我们可以测试一个日期是否出现在闰年，例如当前日期：

```java
boolean leapYear = LocalDate.now().isLeapYear();
```

此外，可以确定一个日期与另一个日期的关系是发生在另一个日期之前还是之后：

```java
boolean notBefore = LocalDate.parse("2016-06-12")
  .isBefore(LocalDate.parse("2016-06-11"));

boolean isAfter = LocalDate.parse("2016-06-12")
  .isAfter(LocalDate.parse("2016-06-11"));
```

最后，可以从给定日期获得日期边界。

在以下两个示例中，我们分别获取表示给定日期的一天开始时间(2016-06-12T00:00)的LocalDateTime和表示月份开始时间(2016-06-01)的LocalDate ：

```java
LocalDateTime beginningOfDay = LocalDate.parse("2016-06-12").atStartOfDay();
LocalDate firstDayOfMonth = LocalDate.parse("2016-06-12")
  .with(TemporalAdjusters.firstDayOfMonth());
```

现在让我们看看我们如何处理当地时间。

### 3.2. 使用本地时间

LocalTime表示没有日期的时间。

与LocalDate类似，我们可以从系统时钟或使用parse and of方法创建LocalTime的实例。

我们现在将快速浏览一些常用的 API。

可以从系统时钟创建当前LocalTime的实例：

```java
LocalTime now = LocalTime.now();
```

我们可以通过解析字符串表示来创建表示早上 6:30的LocalTime ：

```java
LocalTime sixThirty = LocalTime.parse("06:30");
```

的工厂方法也可用于创建LocalTime。此代码使用工厂方法创建表示早上 6:30 的LocalTime ：

```java
LocalTime sixThirty = LocalTime.of(6, 30);
```

让我们通过解析字符串并使用“plus”API 向其添加一个小时来创建LocalTime 。结果将是代表早上 7:30的LocalTime ：

```java
LocalTime sevenThirty = LocalTime.parse("06:30").plus(1, ChronoUnit.HOURS);
```

有多种 getter 方法可用于获取特定的时间单位，如小时、分钟和秒：

```java
int six = LocalTime.parse("06:30").getHour();
```

我们还可以检查特定时间是在另一个特定时间之前还是之后。此代码示例比较两个结果为真的LocalTime ：

```java
boolean isbefore = LocalTime.parse("06:30").isBefore(LocalTime.parse("07:30"));
```

最后，可以通过LocalTime类中的常量获取一天的最大、最小和中午时间。这在执行数据库查询以查找给定时间跨度内的记录时非常有用。

例如，下面的代码表示 23:59:59.99：

```java
LocalTime maxTime = LocalTime.MAX复制
```

现在让我们深入了解LocalDateTime。

### 3.3. 使用LocalDateTime

LocalDateTime用于表示日期和时间的组合。当我们需要日期和时间的组合时，这是最常用的类。

该类提供了多种 API。在这里，我们将看看一些最常用的。

LocalDateTime的实例可以从类似于LocalDate和LocalTime的系统时钟中获取：

```java
LocalDateTime.now();
```

下面的代码示例解释了如何使用工厂“of”和“parse”方法创建实例。结果将是一个代表 2015 年 2 月 20 日早上 6:30的LocalDateTime实例：

```java
LocalDateTime.of(2015, Month.FEBRUARY, 20, 06, 30);
LocalDateTime.parse("2015-02-20T06:30:00");
```

有一些实用程序 API 支持特定时间单位的加法和减法，例如日、月、年和分钟。

下面的代码演示了“加”和“减”方法。这些 API 的行为与LocalDate和LocalTime中的对应 API 完全相同：

```java
localDateTime.plusDays(1);
localDateTime.minusHours(2);
```

Getter 方法也可用于提取类似于日期和时间类的特定单位。给定上述LocalDateTime实例，此代码示例将返回二月：

```java
localDateTime.getMonth();
```

## 4. 使用ZonedDateTime API

Java 8 provides ZonedDateTime when we need to deal with time-zone-specific date and time. The ZoneId is an identifier used to represent different zones. There are about 40 different time zones, and the ZoneId represents them as follows.

Here, we create a Zone for Paris:

```java
ZoneId zoneId = ZoneId.of("Europe/Paris");

```

我们可以获得一组所有区域 ID：

```java
Set<String> allZoneIds = ZoneId.getAvailableZoneIds();
```

LocalDateTime可以转换为特定区域：

```java
ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
```

ZonedDateTime提供了解析方法来获取时区特定的日期时间：

```java
ZonedDateTime.parse("2015-05-03T10:15:30+01:00[Europe/Paris]");
```

另一种使用时区的方法是使用OffsetDateTime。OffsetDateTime是具有偏移量的日期时间的不可变表示。此类存储所有日期和时间字段，精确到纳秒，以及与 UTC/格林威治的偏移量。

可以使用ZoneOffset创建OffSetDateTime实例。在这里，我们创建一个代表 2015 年 2 月 20 日早上 6:30的LocalDateTime ：

```java
LocalDateTime localDateTime = LocalDateTime.of(2015, Month.FEBRUARY, 20, 06, 30);
```

然后我们通过为localDateTime实例创建ZoneOffset和设置来增加两个小时的时间：

```java
ZoneOffset offset = ZoneOffset.of("+02:00");

OffsetDateTime offSetByTwo = OffsetDateTime
  .of(localDateTime, offset);
```

我们现在的localDateTime为 2015-02-20 06:30 +02:00。

现在让我们继续讨论如何使用Period和Duration类修改日期和时间值。

## 5.使用期间和持续时间

Period类表示以年、月和日为单位的时间量，而Duration类表示以秒和纳秒为单位的时间量。

### 5.1. 使用期间

Period类广泛用于修改给定日期的值或获取两个日期之间的差异：

```java
LocalDate initialDate = LocalDate.parse("2007-05-10");
```

我们可以使用Period来操作Date：

```java
LocalDate finalDate = initialDate.plus(Period.ofDays(5));
```

Period类具有各种 getter 方法，例如getYears、getMonths和getDays以从Period对象获取值。

例如，当我们尝试获得天数差异时，这将返回 5的int值：

```java
int five = Period.between(initialDate, finalDate).getDays();
```

我们可以使用ChronoUnit.between获取特定单位(例如天、月或年)中两个日期之间的 Period：

```java
long five = ChronoUnit.DAYS.between(initialDate, finalDate);
```

此代码示例返回五天。

让我们继续看一下Duration类。

### 5.2. 使用持续时间

与Period类似， Duration类是用来处理Time的。

让我们创建一个6:30 am 的LocalTime，然后添加 30 秒的持续时间，使LocalTime为 6:30:30 am：

```java
LocalTime initialTime = LocalTime.of(6, 30, 0);

LocalTime finalTime = initialTime.plus(Duration.ofSeconds(30));
```

我们可以将两个瞬间之间的持续时间作为持续时间或特定单位。

首先，我们使用Duration类的between()方法找出finalTime和initialTime之间的时间差，并返回以秒为单位的差值：

```java
long thirty = Duration.between(initialTime, finalTime).getSeconds();
```

在第二个例子中，我们使用ChronoUnit类的between()方法来执行相同的操作：

```java
long thirty = ChronoUnit.SECONDS.between(initialTime, finalTime);
```

现在我们来看看如何将现有的Date和Calendar转换为新的Date / Time。

## 6.日期和日历的兼容性

Java 8 添加了toInstant()方法，它有助于将现有的Date和Calendar实例转换为新的 Date and Time API：

```java
LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
```

LocalDateTime可以从纪元秒构造。以下代码的结果将是表示 2016-06-13T11:34:50的LocalDateTime ：

```java
LocalDateTime.ofEpochSecond(1465817690, 0, ZoneOffset.UTC);
```

现在让我们继续日期和时间格式。

## 7.日期和时间格式

Java 8 提供了用于轻松格式化Date和Time的 API ：


```java
LocalDateTime localDateTime = LocalDateTime.of(2015, Month.JANUARY, 25, 6, 30);
```

此代码通过 ISO 日期格式来格式化本地日期，结果为 2015-01-25：

```java
String localDateString = localDateTime.format(DateTimeFormatter.ISO_DATE);
```

DateTimeFormatter提供各种标准格式化选项。

自定义模式也可以提供给格式方法，这里返回一个LocalDate作为 2015/01/25：

```java
localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
```

我们可以将格式化样式作为SHORT、LONG或MEDIUM作为格式化选项的一部分传递。

例如，这将给出表示LocalDateTime的输出，时间为 2015 年 1 月 25 日，06:30:00：

```java
localDateTime
  .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
  .withLocale(Locale.UK));
```

让我们看一下Java8 核心日期/时间API 可用的替代方案。

## 8. 向后移植和替代选项

### 8.1. 使用 ThreeTen 项目

对于正在从Java7 或Java6 迁移到Java8 并希望使用日期和时间 API 的组织，[ThreeTen](http://www.threeten.org/)项目提供了向后移植功能。

开发人员可以使用此项目中可用的类来实现与新的Java8日期和时间API相同的功能。一旦他们迁移到Java8，就可以切换包。

ThreeTen 项目的工件可以在[Maven 中央存储库](https://mvnrepository.com/artifact/org.threeten/threetenbp)中找到：

```xml
<dependency>
    <groupId>org.threeten</groupId>
    <artifactId>threetenbp</artifactId>
    <version>1.3.1</version>
</dependency>
```

### 8.2. 乔达时代图书馆

Java 8日期和时间库的另一个替代方案是[Joda-Time](http://www.joda.org/joda-time/)库。事实上，Java 8 Date / Time API 已经由 Joda-Time 库的作者 (Stephen Colebourne) 和 Oracle 共同领导。该库几乎提供了Java8日期/时间项目支持的所有功能。

通过在我们的项目中包含以下 pom 依赖项，可以在[Maven Central中找到该工件：](https://mvnrepository.com/artifact/joda-time/joda-time)

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.9.4</version>
</dependency>

```

## 9.总结

Java 8 提供了丰富的 API 集，API 设计一致，更易于开发。