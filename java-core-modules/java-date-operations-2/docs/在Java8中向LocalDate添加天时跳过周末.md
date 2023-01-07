## 1. 概述

在本教程中，我们将简要介绍在Java8 中向LocalDate实例添加天数时跳过周末的算法。

我们还将通过算法在跳过周末的同时从LocalDate对象中减去天数。

## 2.增加天数

在此方法中，我们不断向LocalDate对象添加一天，直到添加了所需的天数。添加一天时，我们检查新LocalDate实例的日期是星期六还是星期日。

如果检查返回true，那么我们不会为到该点之前添加的天数增加计数器。但是，如果当天是工作日，那么我们会增加计数器。

这样，我们不断添加天数，直到计数器等于应该添加的天数：

```java
public static LocalDate addDaysSkippingWeekends(LocalDate date, int days) {
    LocalDate result = date;
    int addedDays = 0;
    while (addedDays < days) {
        result = result.plusDays(1);
        if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
            ++addedDays;
        }
    }
    return result;
}
```

在上面的代码中，我们使用LocalDate对象的plusDays()方法向结果对象添加天数。只有当一天是工作日时，我们才增加addedDays 变量。当变量addedDays等于days变量时，我们停止向结果LocalDate对象添加一天。

## 3.减去天数

同样，我们可以使用minusDays()方法从LocalDate对象中减去天数，直到减去所需的天数。

为实现这一点，我们将保留一个计数器，用于计算减去的天数，仅当结果日为工作日时才递增：

```java
public static LocalDate subtractDaysSkippingWeekends(LocalDate date, int days) {
    LocalDate result = date;
    int subtractedDays = 0;
    while (subtractedDays < days) {
        result = result.minusDays(1);
        if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
            ++subtractedDays;
        }
    }
    return result;
}
```

从上面的实现中，我们可以看到当结果 LocalDate对象是工作日时， subtractedDays 只会增加。使用 while 循环，我们减去天数，直到subtractedDays等于天数变量。

## 4。总结

在这篇简短的文章中，我们研究了在跳过周末的LocalDate对象中添加和减去天数的算法。此外，我们还研究了它们在Java中的实现。