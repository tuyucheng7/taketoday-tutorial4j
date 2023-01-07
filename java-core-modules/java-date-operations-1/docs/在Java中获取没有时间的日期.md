## 1. 概述

在这个简短的教程中，我们将展示如何在Java中获取没有时间的日期。

我们将展示如何在Java8 之前和之后执行此操作，因为在Java8 中发布新的时间 API 后情况有所不同。

## 2.Java 8之前

在Java8 之前，除非我们使用像 Joda-time 这样的第三方库，否则没有直接的方法来获取没有时间的日期。

这是因为Java 中的Date类是对特定时刻的表示，以毫秒为单位表示。因此，这使得无法忽略 Date 上的时间。

在接下来的部分中，我们将展示一些常见的解决方法来解决这个问题。

### 2.1. 使用日历

获取不带时间的Date的最常见方法之一是使用Calendar类将时间设置为零。通过这样做，我们得到一个干净的日期，时间设置在一天的开始。

让我们在代码中看到它：

```java
public static Date getDateWithoutTimeUsingCalendar() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime();
}
```

如果我们调用这个方法，我们会得到这样的日期：

```plaintext
Sat Jun 23 00:00:00 CEST 2018
```

如我们所见，它返回一个完整的日期，时间设置为零，但我们不能忽略时间。

此外，为确保返回的日期中没有设置时间，我们可以创建以下测试：

```java
@Test
public void whenGettingDateWithoutTimeUsingCalendar_thenReturnDateWithoutTime() {
    Date dateWithoutTime = DateWithoutTime.getDateWithoutTimeUsingCalendar();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateWithoutTime);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    calendar.setTimeInMillis(dateWithoutTime.getTime() + MILLISECONDS_PER_DAY - 1);
    assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));

    calendar.setTimeInMillis(dateWithoutTime.getTime() + MILLISECONDS_PER_DAY);
    assertNotEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
}
```

正如我们所见，当我们将一天中的毫秒数减去 1 加到日期上时，我们仍然得到同一天，但是当我们添加一整天时，我们得到的是第二天。

### 2.2. 使用格式化程序

另一种方法是将Date格式化为没有时间的String，然后将该String转换回Date。由于字符串的格式没有时间，因此转换后的日期会将时间设置为零。

让我们实现它看看它是如何工作的：

```java
public static Date getDateWithoutTimeUsingFormat() 
  throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(
      "dd/MM/yyyy");
    return formatter.parse(formatter.format(new Date()));
}
```

此实现返回与上一节中显示的方法相同的方法：

```plaintext
Sat Jun 23 00:00:00 CEST 2018
```

同样，我们可以像以前一样使用测试来确保返回的日期中没有时间。

## 3. 使用Java8

随着Java8 中新时间 API 的发布，有一种更简单的方法可以在没有时间的情况下获取日期。这个新 API 带来的新特性之一是有几个类可以处理带时间或不带时间的日期，甚至可以只处理时间。

在本文中，我们将重点关注LocalDate类，它代表了我们所需要的，一个没有时间的日期。

让我们看看这个例子：

```java
public static LocalDate getLocalDate() {
    return LocalDate.now();
}
```

此方法返回具有此日期表示的LocalDate对象：

```plaintext
2018-06-23
```

正如我们所见，它只返回一个日期，完全忽略了时间。

同样，让我们像以前一样对其进行测试，以确保此方法按预期工作：

```java
@Test
public void whenGettingLocalDate_thenReturnDateWithoutTime() {
    LocalDate localDate = DateWithoutTime.getLocalDate();

    long millisLocalDate = localDate
      .atStartOfDay()
      .toInstant(OffsetDateTime
        .now()
        .getOffset())
      .toEpochMilli();

    Calendar calendar = Calendar.getInstance();

    calendar.setTimeInMillis(
      millisLocalDate + MILLISECONDS_PER_DAY - 1);
    assertEquals(
      localDate.getDayOfMonth(), 
      calendar.get(Calendar.DAY_OF_MONTH));

    calendar.setTimeInMillis(millisLocalDate + MILLISECONDS_PER_DAY);
    assertNotEquals(
      localDate.getDayOfMonth(), 
      calendar.get(Calendar.DAY_OF_MONTH));
}
```

## 4。总结

在本文中，我们展示了如何在Java中获取没有时间的日期。我们首先展示了如何在Java8 之前以及如何使用Java8 来做到这一点。

正如我们在本文中实现的示例中看到的那样，使用Java 8 应该始终是首选，因为它具有处理没有时间的日期的特定表示。