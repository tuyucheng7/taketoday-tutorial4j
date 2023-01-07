## 1. 概述

在这个简短的教程中，我们将了解如何从Java日期中提取星期几作为数字和文本。

## 2.问题

业务逻辑通常需要星期几。为什么？一方面，工作日和周末的工作时间和服务水平不同。因此，很多系统都需要将日期作为数字来获取。但我们也可能需要将日期作为文本进行显示。

那么，我们如何从Java中的日期中提取星期几呢？

## 3. 使用java.util.Date 的解决方案

java.util.Date 自Java1.0 以来一直是Java日期类。以Java版本 7 或更低版本开始的代码可能使用此类。

### 3.1. 星期几作为数字

首先，我们 使用java.util.Calendar将日期提取为数字：

```java
public static int getDayNumberOld(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_WEEK);
}
```

结果数字的范围从 1(星期日)到 7(星期六)。Calendar为此定义常量：Calendar.SUNDAY – Calendar.SATURDAY。

### 3.2. 星期几作为文本

现在我们将日期提取为文本。我们传入Locale来确定语言：

```java
public static String getDayStringOld(Date date, Locale locale) {
    DateFormat formatter = new SimpleDateFormat("EEEE", locale);
    return formatter.format(date);
}
```

这将以你的语言返回全天，例如英语中的“Monday”或德语中的“Montag”。

## 4. 使用java.time.LocalDate 的解决方案

[Java 8 全面改进了日期和时间处理](https://www.baeldung.com/java-8-date-time-intro)，并为日期引入了java.time.LocalDate。因此，只运行在Java版本 8 或更高版本上的Java项目应该使用这个类！

### 4.1. 星期几作为数字

将日期提取为数字现在很简单：

```java
public static int getDayNumberNew(LocalDate date) {
    DayOfWeek day = date.getDayOfWeek();
    return day.getValue();
}
```

结果数字仍然在 1 到 7 之间。但是这次，星期一是 1，星期日是 7！星期几有自己的枚举—— DayOfWeek。正如预期的那样， 枚举值是 MONDAY – SUNDAY。

### 4.2. 星期几作为文本

现在我们再次将日期提取为文本。我们还传入了一个Locale：

```java
public static String getDayStringNew(LocalDate date, Locale locale) {
    DayOfWeek day = date.getDayOfWeek();
    return day.getDisplayName(TextStyle.FULL, locale);
}
```

与java.util.Date一样，这会以所选语言返回全天。

## 5.总结

在本文中，我们从Java日期中提取了星期几。我们看到了如何使用java.util.Date 和 java.time.LocalDate返回数字和文本。