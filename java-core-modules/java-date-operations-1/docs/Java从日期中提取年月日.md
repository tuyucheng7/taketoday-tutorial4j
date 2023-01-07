## 1. 概述

在这个简短的教程中，我们将学习如何在Java中从给定的Date 中提取年、月和日 。

我们将讨论如何使用遗留的java.util.Date类以及使用Java8 的新日期时间库提取这些值。

[在Java8 中，出于多种原因](http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html)引入了一个全新的日期和时间库 。除了其他优点外，新库还为操作提供了更好的 API，例如从给定的Date中提取Year、Month、Day等。

有关新日期时间库的更详细文章，请查看[此处](https://www.baeldung.com/java-8-date-time-intro)。

## 2. 使用LocalDate

新的java.time包包含许多可用于表示Date的类。

除了Date之外，每个类的不同之处在于它存储的附加信息。

基本的 LocalDate 只包含日期信息，而LocalDateTime包含日期和时间信息。

同样，更高级的类(例如OffsetDateTime和ZonedDateTime)分别包含有关UTC偏移量和时区的附加信息。

无论如何，所有这些类都支持直接方法来提取年、月和日信息。

让我们探索这些方法以从名为localDate的 LocalDate 实例中提取信息 。

### 2.1. 获取年份

要提取年份，LocalDate只需提供 一个 getYear 方法：

```java
localDate.getYear();
```

### 2.2. 获取月份

同样，要提取 月份， 我们将使用getMonthValue API：

```java
localDate.getMonthValue();
```

与Calendar不同， LocalDate中的月份从 1 开始索引；对于一月份，这将返回 1。

### 2.3. 获得一天

最后，为了提取Day，我们有getDayOfMonth方法：

```java
localDate.getDayOfMonth();
```

## 3. 使用java.util.Date

对于给定的java.util.Date，要提取Year、Month、Day 等各个字段，我们需要做的第一步是将其转换为Calendar实例：

```java
Date date = // the date instance
Calendar calendar = Calendar.getInstance();
calendar.setTime(date);
```

一旦我们有了一个Calendar 实例，我们就可以直接调用它的get方法，并提供我们想要提取的特定字段。

我们可以使用Calendar 中存在的常量来提取特定字段。

### 3.1. 获取年份

要提取 年份， 我们可以通过将Calendar.YEAR作为参数传递来调用get ：

```java
calendar.get(Calendar.YEAR);
```

### 3.2. 获取月份

同样，要提取月份， 我们可以通过将Calendar.MONTH 作为参数传递来调用get ：

```java
calendar.get(Calendar.MONTH);
```

请注意日历 中的月份是零索引的；对于一月份，此方法将返回 0。

### 3.3. 获得一天

最后，为了提取 日期，我们通过传递Calendar.DAY_OF_MONTH 作为参数来 调用get ：

```java
calendar.get(Calendar.DAY_OF_MONTH);
```

## 4。总结

在这篇简短的文章中，我们探讨了如何在Java中从Date中提取Year、Month和Day的整数值。

我们学习了如何使用旧的Date和Calendar类以及 Java8 的新日期时间库来提取这些值。