## 1. 概述

在本快速教程中，我们将学习几种不同的方法来检查两个java.util.Date对象是否具有同一天。

在查看几个Java8 之前的替代方案之前，我们将首先考虑使用核心 Java(即Java8 功能)的解决方案。

最后，我们还将查看一些外部库——Apache Commons Lang、Joda-Time 和 Date4J。

## 2.核心Java

Date类代表一个特定的时刻，精度为毫秒。要确定两个Date对象是否包含同一天，我们需要检查两个对象的 Year-Month-Day 是否相同并丢弃时间方面。

### 2.1. 使用本地日期

[通过Java 8](https://www.baeldung.com/java-8-date-time-intro)新的Date-Time API ，我们可以使用LocalDate对象。这是一个表示没有时间的日期的不可变对象。

让我们看看如何使用此类检查两个Date对象是否在同一天：

```java
public static boolean isSameDay(Date date1, Date date2) {
    LocalDate localDate1 = date1.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
    LocalDate localDate2 = date2.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
    return localDate1.isEqual(localDate2);
}
```

在此示例中，我们使用默认时区将两个Date对象都转换为LocalDate。转换后，我们只需要使用isEqual方法检查LocalDate对象是否相等。

因此，使用这种方法，我们将能够确定两个Date对象是否包含同一天。

### 2.2. 使用即时

在上面的示例中，我们在将Date对象转换为LocalDate对象时使用Instant作为中间对象。Instant在Java8 中引入，代表一个特定的时间点。

我们可以直接将Instant对象截断为DAYS unit，这会将时间字段值设置为零，然后我们可以比较它们：

```java
public static boolean isSameDayUsingInstant(Date date1, Date date2) {
    Instant instant1 = date1.toInstant()
      .truncatedTo(ChronoUnit.DAYS);
    Instant instant2 = date2.toInstant()
      .truncatedTo(ChronoUnit.DAYS);
    return instant1.equals(instant2);
}
```

### 2.3. 使用简单日期格式

从Java的早期版本开始，我们就可以使用[SimpleDateFormat](https://www.baeldung.com/java-simple-date-format)类在Date和String对象表示之间进行转换。此类支持使用多种模式进行转换。在我们的例子中，我们将使用模式“yyyyMMdd”。

使用它，我们将格式化Date，将其转换为 String对象，然后使用标准equals方法比较它们：

```java
public static boolean isSameDay(Date date1, Date date2) {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
    return fmt.format(date1).equals(fmt.format(date2));
}
```

### 2.4. 使用日历

Calendar类提供了获取特定时刻不同日期时间单位值的方法。

首先，我们需要创建一个Calendar实例并使用每个提供的日期设置Calendar对象的时间。然后我们可以单独查询和比较年月日属性以确定Date对象是否具有同一天：

```java
public static boolean isSameDay(Date date1, Date date2) {
    Calendar calendar1 = Calendar.getInstance();
    calendar1.setTime(date1);
    Calendar calendar2 = Calendar.getInstance();
    calendar2.setTime(date2);
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
      && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
      && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
}
```

## 3. 外部图书馆

现在我们已经很好地理解了如何使用核心Java提供的新旧 API来比较Date对象，让我们来看看一些外部库。

### 3.1. Apache Commons Lang DateUtils

DateUtils类提供了许多有用的实用程序，可以更轻松地使用旧版Calendar和Date对象。

[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)工件可从Maven [Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")获得：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

然后我们可以简单地使用DateUtils中的isSameDay方法：

```java
DateUtils.isSameDay(date1, date2);
```

### 3.2. 乔达时代图书馆

核心 Java日期 和 时间 库 的替代方法 是[Joda-Time](https://www.baeldung.com/joda-time)。在处理 Date and Time 时，这个广泛使用的[库](http://www.joda.org/joda-time/)是一个很好的替代品。

该工件可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"joda-time" AND a%3A"joda-time")上找到：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.10</version>
</dependency>
```

在此库中，org.joda.time.LocalDate表示没有时间的日期。因此，我们可以从java.util.Date对象构造LocalDate对象，然后比较它们：

```java
public static boolean isSameDay(Date date1, Date date2) {
    org.joda.time.LocalDate localDate1 = new org.joda.time.LocalDate(date1);
    org.joda.time.LocalDate localDate2 = new org.joda.time.LocalDate(date2);
    return localDate1.equals(localDate2);
}
```

### 3.3. Date4J 库

[Date4j](http://www.date4j.net/)还提供了一个我们可以使用的简单明了的实现。

同样，它也可以从[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"com.darwinsys" AND a%3A"hirondelle-date4j")获得：

```xml
<dependency>
    <groupId>com.darwinsys</groupId>
    <artifactId>hirondelle-date4j</artifactId>
    <version>1.5.1</version>
</dependency>
```

使用这个库，我们需要从java.util.Date对象构造DateTime对象。然后我们可以简单地使用isSameDayAs方法：

```java
public static boolean isSameDay(Date date1, Date date2) {
    DateTime dateObject1 = DateTime.forInstant(date1.getTime(), TimeZone.getDefault());
    DateTime dateObject2 = DateTime.forInstant(date2.getTime(), TimeZone.getDefault());
    return dateObject1.isSameDayAs(dateObject2);
}
```

## 4。总结

在本快速教程中，我们探讨了检查两个java.util.Date对象是否包含同一天的几种方法。