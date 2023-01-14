## 1. 概述

随着Java8 的出现，在Java中创建日期被重新定义。此外，相对于java.util包中的旧 API  ，java.time包中的新[日期和时间 API](https://www.baeldung.com/migrating-to-java-8-date-time-api)可以更轻松地使用。在本教程中，我们将看到它如何产生巨大的不同。

java.time包中的[LocalDate](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/LocalDate.html)类帮助我们实现了这一点。LocalDate是一个不可变的、线程安全的类。此外，LocalDate只能包含日期值，不能包含时间分量。

现在让我们看看创建一个带有值的所有变体。

## 2.使用of()创建自定义LocalDate

让我们看一下创建表示 2020 年 1 月 8 日的LocalDate的几种方法。我们可以通过将值传递给工厂方法来创建一个：

```java
LocalDate date = LocalDate.of(2020, 1, 8);
```

也可以使用Month枚举指定月份：

```java
LocalDate date = LocalDate.of(2020, Month.JANUARY, 8)
```

我们也可以尝试使用纪元日来获取它：

```java
LocalDate date = LocalDate.ofEpochDay(18269);
```

最后，让我们创建一个具有年份和年份值的值：

```java
LocalDate date = LocalDate.ofYearDay(2020, 8);
```

## 3.通过解析字符串创建LocalDate

最后一个选项是通过解析字符串来创建日期。我们可以使用只有一个参数的parse方法来解析yyyy-mm-dd格式的日期：

```java
LocalDate date = LocalDate.parse("2020-01-08");
```

我们还可以指定一个不同的模式来使用[DateTimeFormatter](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html)类作为解析方法的第二个参数：

```java
LocalDate date = LocalDate.parse("8-Jan-2020", DateTimeFormatter.ofPattern("d-MMM-yyyy"));
```

## 4。总结

在本文中，我们看到了在 Java中创建具有值的LocalDate的所有变体。[Date & Time API 文章](https://www.baeldung.com/java-8-date-time-intro)可以 帮助我们了解更多。