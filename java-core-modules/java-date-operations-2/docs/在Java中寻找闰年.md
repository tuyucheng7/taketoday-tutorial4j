## 1. 概述

 在本教程中，我们将演示在 Java中确定给定年份是否为[闰年的几种方法。](https://en.wikipedia.org/wiki/Leap_year)

闰年是可以被 4 和 400 整除而没有余数的年份。因此，可以被 100 整除但不能被 400 整除的年份不符合条件，即使它们可以被 4 整除。

## 2.使用 Pre-Java-8 Calendar API

从Java1.1 开始， [GregorianCalendar](https://www.baeldung.com/java-gregorian-calendar) 类允许我们检查年份是否为闰年：

```java
public boolean isLeapYear(int year);
```

正如我们所料，如果给定的年份是闰年 ，则此方法返回true ，对于非闰年则返回false 。

Years in BC (Before Christ) 需要作为负值传递并计算为 1 – year。例如，公元前 3 年表示为 -2，因为 1 – 3 = -2。

## 3.使用Java8+ 日期/时间 API

Java 8 引入了 java . 具有更好的[日期和时间 API的](https://www.baeldung.com/java-8-date-time-intro)时间包。

Java中的 [Year ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Year.html)类。time包有一个静态方法来检查给定的年份是否是闰年：

```java
public static boolean isLeap(long year);
```

它也有一个实例方法来做同样的事情：

```java
public boolean isLeap();
```

## 4.使用 Joda-Time API

[Joda-Time API](https://www.baeldung.com/joda-time)是用于日期和时间实用程序的Java项目中最常用的第三方库之一。从Java8 开始，该库处于可维护状态，如 [Joda-Time GitHub 源存储库](https://github.com/JodaOrg/joda-time#joda-time)中所述。

在 Joda-Time 中没有预定义的 API 方法来查找闰年。但是，我们可以使用它们的[LocalDate](https://www.joda.org/joda-time/apidocs/org/joda/time/LocalDate.html)和[Days](https://www.joda.org/joda-time/apidocs/org/joda/time/Days.html)类来检查闰年：

```java
LocalDate localDate = new LocalDate(2020, 1, 31);
int numberOfDays = Days.daysBetween(localDate, localDate.plusYears(1)).getDays();

boolean isLeapYear = (numberOfDays > 365) ? true : false;
```

## 5.总结

在本教程中，我们了解了闰年是什么、查找它的逻辑以及我们可以用来检查它的几个JavaAPI。