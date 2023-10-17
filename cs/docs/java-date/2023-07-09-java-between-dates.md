---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-date
copyright: java-date
excerpt: Java Date Time
---

## 1. 概述

Java 8中引入的新时间API可以在不使用外部库的情况下处理日期和时间。

在这个简短的教程中，我们将介绍如何在不同版本的Java中获取两个日期之间的所有日期。

## 2. 使用Java 7

在Java 7中，一种计算方法是使用Calendar实例。

首先，我们得到[没有时间](https://www.baeldung.com/java-date-without-time)的开始和结束日期。然后，我们将遍历这些并使用add方法和Calendar.Date字段在每次迭代中添加一天，直到它到达结束日期。

以下是演示它的代码-使用Calendar实例：

```java
public static List getDatesBetweenUsingJava7(Date startDate, Date endDate) {
    List datesInRange = new ArrayList<>();
    Calendar calendar = getCalendarWithoutTime(startDate);
    Calendar endCalendar = getCalendarWithoutTime(endDate);
    
    while (calendar.before(endCalendar)) {
        Date result = calendar.getTime();
        datesInRange.add(result);
        calendar.add(Calendar.DATE, 1);
    }

  return datesInRange;
}

private static Calendar getCalendarWithoutTime(Date date) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar;
}
```

## 3. 使用Java 8

在Java 8中，我们现在可以创建一个连续的无限日期流，并且只获取相关部分。**不幸的是，当谓词匹配时，没有办法终止无限流**-这就是为什么我们需要计算这两天之间的天数，然后简单地limit()流：

```java
public static List<LocalDate> getDatesBetweenUsingJava8(LocalDate startDate, LocalDate endDate) {
    long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate); 
    return IntStream.iterate(0, i -> i + 1)
        .limit(numOfDaysBetween)
        .mapToObj(i -> startDate.plusDays(i))
        .collect(Collectors.toList()); 
}
```

请注意，首先，我们如何使用between函数获取两个日期之间的天数差-与ChronoUnit枚举的DAYS常量相关联。

然后我们创建一个整数流，表示自开始日期以来的天数。在下一步中，我们使用plusDays() API将整数转换为LocalDate对象。

最后，我们将所有内容收集到一个列表实例中。

## 4. 使用Java 9

最后，Java 9带来了用于计算此值的专用方法：

```java
public static List<LocalDate> getDatesBetweenUsingJava9(LocalDate startDate, LocalDate endDate) {
    return startDate.datesUntil(endDate)
        .collect(Collectors.toList());
}
```

我们可以使用LocalDate类的专用datesUntil方法通过单个方法调用获取两个日期之间的日期。datesUntil返回顺序排列的日期流，从调用其方法的日期对象开始，到作为方法参数给出的日期。

## 5. 总结

在这篇简短的文章中，我们研究了如何使用不同版本的Java获取两个日期之间的所有日期。

我们讨论了Java 8版本中引入的时间API如何使对日期文本运行操作变得更容易，而在Java 9中，它可以通过调用datesUntil来完成。

与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-date-operations-1)上获得。