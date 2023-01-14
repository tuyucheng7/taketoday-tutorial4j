## 1. 概述

在本教程中，我们将重点介绍如何使用[Java 8 Date/Time API](https://www.baeldung.com/java-8-date-time-intro)比较日期。我们将深入探讨检查两个日期是否相等以及如何比较日期的不同方法。

## 2.比较日期

在Java中表示日期的基本方法是LocalDate。让我们考虑两个LocalDate对象实例，分别代表 2019 年 8 月 10 日和 2019 年 7 月 1 日：

```java
LocalDate firstDate = LocalDate.of(2019, 8, 10);
LocalDate secondDate = LocalDate.of(2019, 7, 1);
```

我们将使用 isAfter()、isBefore()和isEqual() 方法以及 equals()和compareTo()来比较两个LocalDate对象。

我们使用isAfter()方法来检查日期实例是否在其他指定日期之后。因此，下一个 JUnit 断言将通过：

```java
assertThat(firstDate.isAfter(secondDate), is(true));
```

类似地，方法isBefore()检查日期实例是否在另一个指定日期之前：

```java
assertThat(firstDate.isBefore(secondDate), is(false));
```

isEqual()方法检查一个日期是否表示本地时间轴上与另一个指定日期相同的点：

```java
assertThat(firstDate.isEqual(firstDate), is(true));
assertThat(firstDate.isEqual(secondDate), is(false));
```

### 2.1. 使用Comparable界面比较日期

equals()方法将给出与isEqual()相同的结果，但前提是传递的参数属于同一类型(在本例中为LocalDate)：

```java
assertThat(firstDate.equals(secondDate), is(false));
```

可以使用isEqual()方法来与不同类型的对象进行比较，例如JapaneseDate、ThaiBuddhistDate等。

我们可以使用Comparable接口定义的compareTo()方法来比较两个日期实例：

```java
assertThat(firstDate.compareTo(secondDate), is(1));
assertThat(secondDate.compareTo(firstDate), is(-1));
```

## 3. 比较包含时间成分的日期实例

本节将解释如何比较两个LocalDateTime实例。LocalDateTime实例包含日期和时间组件。

与LocalDate类似，我们将两个LocalDateTime实例与方法isAfter()、isBefore()和isEqual()进行比较。此外，equals()和compareTo()的使用方式与LocalDate的描述类似。

同样，我们可以使用相同的方法来比较两个ZonedDateTime实例。让我们对比一下同一天纽约当地时间 8:00 和柏林当地时间 14:00：

```java
ZonedDateTime timeInNewYork = 
  ZonedDateTime.of(2019, 8, 10, 8, 0, 0, 0, ZoneId.of("America/New_York"));
ZonedDateTime timeInBerlin = 
  ZonedDateTime.of(2019, 8, 10, 14, 0, 0, 0, ZoneId.of("Europe/Berlin"));

assertThat(timeInNewYork.isAfter(timeInBerlin), is(false));
assertThat(timeInNewYork.isBefore(timeInBerlin), is(false));
assertThat(timeInNewYork.isEqual(timeInBerlin), is(true));
```

尽管两个ZonedDateTime实例代表同一时刻，但它们并不代表相同的Java对象。它们内部有不同的LocalDateTime和ZoneId字段：

```java
assertThat(timeInNewYork.equals(timeInBerlin), is(false)); 
assertThat(timeInNewYork.compareTo(timeInBerlin), is(-1));
```

## 4. 额外比较

让我们创建一个简单的实用程序类来进行稍微复杂的比较。

首先，我们将检查LocalDateTime和LocalDate的实例是否在同一天：

```java
public static boolean isSameDay(LocalDateTime timestamp, 
  LocalDate localDateToCompare) {
    return timestamp.toLocalDate().isEqual(localDateToCompare);
}
```

其次，我们将检查LocalDateTime的两个实例是否在同一天：

```java
public static boolean isSameDay(LocalDateTime timestamp, 
  LocalDateTime timestampToCompare) {
    return timestamp.truncatedTo(DAYS)
      .isEqual(timestampToCompare.truncatedTo(DAYS));
}
```

truncatedTo(TemporalUnit)方法截断给定级别的日期， 在我们的示例中是一天。

第三，我们可以实现一个小时级别的比较：

```java
public static boolean isSameHour(LocalDateTime timestamp, 
  LocalDateTime timestampToCompare) {
    return timestamp.truncatedTo(HOURS)
      .isEqual(timestampToCompare.truncatedTo(HOURS));
}
```

最后，以类似的方式，我们可以检查两个ZonedDateTime实例是否在同一小时内发生：

```java
public static boolean isSameHour(ZonedDateTime zonedTimestamp, 
  ZonedDateTime zonedTimestampToCompare) {
    return zonedTimestamp.truncatedTo(HOURS)
      .isEqual(zonedTimestampToCompare.truncatedTo(HOURS));
}
```

我们可以看到两个ZonedDateTime对象实际上发生在同一小时内，即使它们的本地时间不同(分别为 8:30 和 14:00)：

```java
ZonedDateTime zonedTimestamp = 
  ZonedDateTime.of(2019, 8, 10, 8, 30, 0, 0, ZoneId.of("America/New_York"));
ZonedDateTime zonedTimestampToCompare = 
  ZonedDateTime.of(2019, 8, 10, 14, 0, 0, 0, ZoneId.of("Europe/Berlin"));

assertThat(DateTimeComparisonUtils.
  isSameHour(zonedTimestamp, zonedTimestampToCompare), is(true));
```

## 5. 旧JavaDate API 中的比较

在Java8 之前，我们必须使用java.util.Date和java.util.Calendar类来处理日期/时间信息。旧的JavaDate API 的设计有很多缺陷，例如复杂和不线程安全。java.util.Date实例表示“即时”而不是真实日期。

解决方案之一是使用[Joda Time](https://www.baeldung.com/joda-time)库。自Java8 发布以来，建议[迁移到Java8 Date/Time API](https://www.baeldung.com/migrating-to-java-8-date-time-api)。

与LocalDate和LocalDateTime类似，java.util.Date和java.util.Calendar对象都具有用于比较两个日期实例的after()、before()、compareTo()和equals()方法。将日期作为时间的瞬间进行比较，以毫秒为单位：

```java
Date firstDate = toDate(LocalDateTime.of(2019, 8, 10, 0, 00, 00));
Date secondDate = toDate(LocalDateTime.of(2019, 8, 15, 0, 00, 00));

assertThat(firstDate.after(secondDate), is(false));
assertThat(firstDate.before(secondDate), is(true));
assertThat(firstDate.compareTo(secondDate), is(-1));
assertThat(firstDate.equals(secondDate), is(false));
```

对于更复杂的比较，我们可以使用[Apache Commons Lang](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3)库中的DateUtils 。这个类包含许多处理Date和Calendar对象的方便方法：

```java
public static boolean isSameDay(Date date, Date dateToCompare) {
    return DateUtils.isSameDay(date, dateToCompare);
}

public static boolean isSameHour(Date date, Date dateToCompare) {
    return DateUtils.truncatedEquals(date, dateToCompare, Calendar.HOUR);
}
```

要比较来自不同 API 的日期对象，我们应该首先进行适当的转换，然后才应用比较。我们可以在我们的[Convert Date to LocalDate 或 LocalDateTime and Back](https://www.baeldung.com/java-date-to-localdate-and-localdatetime)教程中找到更多详细信息。

## 六，总结

在本文中，我们探讨了在Java中比较日期实例的不同方法。

Java 8 日期/时间类有丰富的 API 用于比较日期，有或没有时间和时区。我们还看到了如何在天、小时、分钟等粒度上比较日期。