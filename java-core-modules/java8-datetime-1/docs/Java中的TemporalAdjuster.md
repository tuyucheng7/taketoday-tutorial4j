## 1. 概述

在本教程中，我们将快速了解TemporalAdjuster并在一些实际场景中使用它。

Java 8 引入了一个用于处理日期和时间的新库——java.time和TemporalAdjuster是其中的一部分。如果你想阅读有关java.time 的更多信息，请查看[这篇介绍性文章。](https://www.baeldung.com/java-8-date-time-intro)

简单地说，TemporalAdjuster是一种调整Temporal对象的策略。在开始使用TemporalAdjuster之前，让我们看一下Temporal接口本身。

## 2.时间

Temporal定义日期、时间或两者组合的表示，具体取决于我们将要使用的实现。

Temporal接口有许多实现，包括：

-   LocalDate – 表示没有时区的日期
-   LocalDateTime – 表示没有时区的日期和时间
-   HijrahDate——表示 Hijrah 日历系统中的日期
-   MinguoDate – 表示 Minguo 日历系统中的日期
-   ThaiBuddhistDate – 代表泰国佛历系统中的日期

## 3.时间调整器

这个新库中包含的接口之一是TemporalAdjuster。

TemporalAdjuster是一个功能接口，在TemporalAdjusters类中有许多预定义的实现。该接口有一个名为adjustInto()的抽象方法，可以通过将Temporal对象传递给它来在其任何实现中调用该方法。

TemporalAdjuster允许我们执行复杂的日期操作。例如，我们可以获得下一个星期日的日期、当月的最后一天或下一年的第一天。当然，我们可以使用旧的java.util.Calendar来做到这一点。

但是，新的 API 使用其预定义的实现抽象出底层逻辑。有关详细信息，请访问[Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalAdjuster.html)。

## 4. 预定义的TemporalAdjusters

TemporalAdjusters类有许多预定义的静态方法，这些方法返回一个TemporalAdjuster对象以许多不同的方式调整Temporal对象，无论它们是什么Temporal实现。

以下是这些方法的简短列表以及它们的快速定义：

-   dayOfWeekInMonth() – 一周中第几天的调整器。例如三月的第二个星期二的日期
-   firstDayOfMonth() – 当前月份第一天日期的调整器
-   firstDayOfNextMonth() – 下个月第一天日期的调整器
-   firstDayOfNextYear() – 下一年第一天日期的调整器
-   firstDayOfYear() – 当前年份第一天日期的调整器
-   lastDayOfMonth() – 当前月份最后一天日期的调整器
-   nextOrSame() – 下一次特定星期几或同一天的调整器，以防今天与所需的星期几匹配

正如我们所见，这些方法的名称几乎是不言自明的。有关更多TemporalAdjusters，请访问[Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalAdjusters.html)。

让我们从一个简单的示例开始——我们可以使用LocalDate.now()从系统时钟获取当前日期，而不是像示例中那样使用特定日期。

但是，对于本教程，我们将使用一个固定的日期，这样当预期结果发生变化时，测试就不会失败。让我们看看如何使用TemporalAdjusters类来获取 2017-07-08 之后星期日的日期：

```java
@Test
public void whenAdjust_thenNextSunday() {
    LocalDate localDate = LocalDate.of(2017, 07, 8);
    LocalDate nextSunday = localDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
    
    String expected = "2017-07-09";
    
    assertEquals(expected, nextSunday.toString());
}
```

以下是我们如何获得当月的最后一天：

```java
LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
```

## 5. 定义自定义TemporalAdjuster实现

我们还可以为TemporalAdjuster定义自定义实现。有两种不同的方法可以做到这一点。

### 5.1. 使用 Lambda 表达式

让我们看看如何使用Temporal.with()方法获取 2017-07-08 之后 14 天的日期：

```java
@Test
public void whenAdjust_thenFourteenDaysAfterDate() {
    LocalDate localDate = LocalDate.of(2017, 07, 8);
    TemporalAdjuster temporalAdjuster = t -> t.plus(Period.ofDays(14));
    LocalDate result = localDate.with(temporalAdjuster);
    
    String fourteenDaysAfterDate = "2017-07-22";
    
    assertEquals(fourteenDaysAfterDate, result.toString());
}
```

在此示例中，我们使用 lambda 表达式将temporalAdjuster对象设置为将 14 天添加到保存日期 (2017-07-08) 的localDate对象。

让我们看看如何通过使用 lambda 表达式定义我们自己的TemporalAdjuster实现来获取 2017 年 7 月 8 日之后的工作日日期。但是，这一次，通过使用ofDateAdjuster()静态工厂方法：

```java
static TemporalAdjuster NEXT_WORKING_DAY = TemporalAdjusters.ofDateAdjuster(date -> {
    DayOfWeek dayOfWeek = date.getDayOfWeek();
    int daysToAdd;
    if (dayOfWeek == DayOfWeek.FRIDAY)
        daysToAdd = 3;
    else if (dayOfWeek == DayOfWeek.SATURDAY)
        daysToAdd = 2;
    else
        daysToAdd = 1;
    return today.plusDays(daysToAdd);
});
```

测试我们的代码：

```java
@Test
public void whenAdjust_thenNextWorkingDay() {
    LocalDate localDate = LocalDate.of(2017, 07, 8);
    TemporalAdjuster temporalAdjuster = NEXT_WORKING_DAY;
    LocalDate result = localDate.with(temporalAdjuster);

    assertEquals("2017-07-10", date.toString());
}
```

### 5.2. 通过实现TemporalAdjuster接口

让我们看看我们如何通过实现TemporalAdjuster接口来编写一个自定义的TemporalAdjuster来获取 2017-07-08 之后的工作日：

```java
public class CustomTemporalAdjuster implements TemporalAdjuster {

    @Override
    public Temporal adjustInto(Temporal temporal) {
        DayOfWeek dayOfWeek 
          = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
        
        int daysToAdd;
        if (dayOfWeek == DayOfWeek.FRIDAY)
            daysToAdd = 3;
        else if (dayOfWeek == DayOfWeek.SATURDAY)
            daysToAdd = 2;
        else
            daysToAdd = 1;
        return temporal.plus(daysToAdd, ChronoUnit.DAYS);
    }
}
```

现在，让我们运行我们的测试：

```java
@Test
public void whenAdjustAndImplementInterface_thenNextWorkingDay() {
    LocalDate localDate = LocalDate.of(2017, 07, 8);
    CustomTemporalAdjuster temporalAdjuster = new CustomTemporalAdjuster();
    LocalDate nextWorkingDay = localDate.with(temporalAdjuster);
    
    assertEquals("2017-07-10", nextWorkingDay.toString());
}
```

## 六，总结

在本教程中，我们展示了TemporalAdjuster是什么、预定义的TemporalAdjuster、如何使用它们，以及我们如何以两种不同的方式实现我们的自定义TemporalAdjuster实现。