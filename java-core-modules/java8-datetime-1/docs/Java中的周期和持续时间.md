## 1. 概述

在本快速教程中，我们将了解两个用于处理Java8 中引入的日期的新类：Period和Duration。

这两个类都可用于表示时间量或确定两个日期之间的差异。这两个类之间的主要区别在于Period使用基于日期的值，而Duration使用基于时间的值。

## 2.时期班

Period类使用单位年、月和日来表示一段时间。

我们可以使用between()方法获得一个Period对象作为两个日期之间的差异：

```java
LocalDate startDate = LocalDate.of(2015, 2, 20);
LocalDate endDate = LocalDate.of(2017, 1, 15);

Period period = Period.between(startDate, endDate);
```

然后，我们可以使用getYears()、getMonths()、getDays()方法确定期间的日期单位：

```java
LOG.info("Years:" + period.getYears() + 
  " months:" + period.getMonths() + 
  " days:"+period.getDays());
```

在这种情况下，isNegative()方法(如果任何单位为负数则返回true )可用于确定endDate是否高于startDate：

```java
assertFalse(period.isNegative());
```

如果isNegative()返回 false，则startDate早于endDate值。

另一种创建Period对象的方法是使用专用方法基于天数、月数、周数或年数：

```java
Period fromUnits = Period.of(3, 10, 10);
Period fromDays = Period.ofDays(50);
Period fromMonths = Period.ofMonths(5);
Period fromYears = Period.ofYears(10);
Period fromWeeks = Period.ofWeeks(40);

assertEquals(280, fromWeeks.getDays());
```

如果仅存在一个值，例如使用ofDays()方法，则其他单位的值为 0。

在ofWeeks()方法的情况下，参数值用于通过将其乘以 7 来设置天数。

我们还可以通过解析文本序列来创建一个Period对象，它的格式必须是“PnYnMnD”：

```java
Period fromCharYears = Period.parse("P2Y");
assertEquals(2, fromCharYears.getYears());

Period fromCharUnits = Period.parse("P2Y3M5D");
assertEquals(5, fromCharUnits.getDays());
```

可以使用plusX()和minusX()形式的方法增加或减少期间的值，其中 X 代表日期单位：

```java
assertEquals(56, period.plusDays(50).getDays());
assertEquals(9, period.minusMonths(2).getMonths());
```

## 3.持续时间类

Duration类表示以秒或纳秒为单位的时间间隔，最适合处理较短的时间，以防需要更高的精度。

我们可以使用between()方法确定两个时刻之间的差异作为Duration对象：

```java
Instant start = Instant.parse("2017-10-03T10:15:30.00Z");
Instant end = Instant.parse("2017-10-03T10:16:30.00Z");
        
Duration duration = Duration.between(start, end);
```

然后我们可以使用getSeconds()或getNanoseconds()方法来确定时间单位的值：

```java
assertEquals(60, duration.getSeconds());
```

或者，我们可以从两个 LocalDateTime 实例中获取一个 Duration 实例：

```java
LocalTime start = LocalTime.of(1, 20, 25, 1024);
LocalTime end = LocalTime.of(3, 22, 27, 1544);

Duration.between(start, end).getSeconds();
```

isNegative ()方法可用于验证结束时刻是否高于开始时刻：

```java
assertFalse(duration.isNegative());
```

我们还可以使用ofDays()、ofHours()、ofMillis()、ofMinutes()、ofNanos()、ofSeconds()方法获取基于多个时间单位的Duration对象：

```java
Duration fromDays = Duration.ofDays(1);
assertEquals(86400, fromDays.getSeconds());
       
Duration fromMinutes = Duration.ofMinutes(60);
```

要基于文本序列创建Duration对象，其格式必须为“PnDTnHnMn.nS”：

```java
Duration fromChar1 = Duration.parse("P1DT1H10M10.5S");
Duration fromChar2 = Duration.parse("PT10M");
```

可以使用toDays()、toHours()、toMillis()、toMinutes()将持续时间转换为其他时间单位：

```java
assertEquals(1, fromMinutes.toHours());
```

可以使用plusX()或minusX()形式的方法增加或减少持续时间值，其中 X 可以代表天、小时、毫秒、分钟、纳米或秒：

```java
assertEquals(120, duration.plusSeconds(60).getSeconds());     
assertEquals(30, duration.minusSeconds(30).getSeconds());
```

我们还可以使用plus()和minus()方法，其参数指定要添加或减去的TemporalUnit ：

```java
assertEquals(120, duration.plus(60, ChronoUnit.SECONDS).getSeconds());     
assertEquals(30, duration.minus(30, ChronoUnit.SECONDS).getSeconds());
```

## 4。总结

在本教程中，我们展示了如何使用Period和Duration类。