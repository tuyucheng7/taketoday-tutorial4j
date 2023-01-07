## 1. 概述

在Java8 之前，java.util.Date是Java中最常用的表示日期时间值的类之一。

然后Java8 引入了 java.time.LocalDateTime和 java.time.ZonedDateTime。Java 8 还允许我们使用java.time.Instant在时间轴上表示特定时间。

在本教程中，我们将学习在Java中从给定的日期时间增加或减少n小时。我们将首先查看一些标准的Java日期时间相关类，然后我们将展示一些第三方选项。

要了解有关Java8 DateTime API 的更多信息，我们建议阅读[这篇文章](https://www.baeldung.com/java-8-date-time-intro)。

## 2. java.util.Date

如果我们使用的是Java7 或更低版本，我们可以使用 [java.util.Date](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Date.html)和[java.util.Calendar](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Calendar.html)类来进行大多数与日期时间相关的处理。

让我们看看如何将n小时添加到给定的Date对象：

```java
public Date addHoursToJavaUtilDate(Date date, int hours) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.HOUR_OF_DAY, hours);
    return calendar.getTime();
}
```

请注意，Calendar.HOUR_OF_DAY指的是 24 小时制。

上述方法返回一个新的Date对象，其值可以是(date + hours)或(date – hours)，具体取决于我们分别传递正数还是负数的小时数。

假设我们有一个Java8 应用程序，但我们仍然希望以我们的方式使用java.util.Date实例。

对于这种情况，我们可以选择采用以下替代方法：

1.  使用java.util.Date toInstant()方法将Date对象转换为java.time.Instant实例
2.  使用plus()方法将特定的Duration添加到java.time.Instant对象 
3.  通过将java.time.Instant对象传递给java.util.Date.from()方法来恢复我们的java.util.Date实例

让我们快速看一下这种方法：

```java
@Test
public void givenJavaUtilDate_whenUsingToInstant_thenAddHours() {
    Date actualDate = new GregorianCalendar(2018, Calendar.JUNE, 25, 5, 0)
      .getTime();
    Date expectedDate = new GregorianCalendar(2018, Calendar.JUNE, 25, 7, 0)
      .getTime();

    assertThat(Date.from(actualDate.toInstant().plus(Duration.ofHours(2))))
      .isEqualTo(expectedDate);
}
```

但是请注意，始终建议对Java8 或更高版本上的所有应用程序使用新的 DateTime API。

## 3.java.time.LocalDateTime /ZonedDateTime

在Java8 或更高版本中，向[java.time.LocalDateTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/LocalDateTime.html)或[java.time.ZonedDateTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/ZonedDateTime.html)实例添加小时数非常简单，并使用 plusHours()方法：

```java
@Test
public void givenLocalDateTime_whenUsingPlusHours_thenAddHours() {
    LocalDateTime actualDateTime = LocalDateTime
      .of(2018, Month.JUNE, 25, 5, 0);
    LocalDateTime expectedDateTime = LocalDateTime.
      of(2018, Month.JUNE, 25, 10, 0);

    assertThat(actualDateTime.plusHours(5)).isEqualTo(expectedDateTime);
}
```

如果我们想减去几个小时怎么办？

将小时的负值传递给plusHours()方法就可以了。但是，建议使用 minusHours()方法：

```java
@Test
public void givenLocalDateTime_whenUsingMinusHours_thenSubtractHours() {
    LocalDateTime actualDateTime = LocalDateTime
      .of(2018, Month.JUNE, 25, 5, 0);
    LocalDateTime expectedDateTime = LocalDateTime
      .of(2018, Month.JUNE, 25, 3, 0);
   
    assertThat(actualDateTime.minusHours(2)).isEqualTo(expectedDateTime);

}
```

java.time.ZonedDateTime 中的plusHours ()和minusHours()方法的工作方式完全相同。

## 4.java.time.Instant _

众所周知，Java8 DateTime API 中引入的[java.time.Instant ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)表示时间轴上的特定时刻。

要向Instant对象添加一些小时，我们可以将其plus()方法与 [java.time.temporal.TemporalAmount](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalAmount.html)一起使用：

```java
@Test
public void givenInstant_whenUsingAddHoursToInstant_thenAddHours() {
    Instant actualValue = Instant.parse("2018-06-25T05:12:35Z");
    Instant expectedValue = Instant.parse("2018-06-25T07:12:35Z");

    assertThat(actualValue.plus(2, ChronoUnit.HOURS))
      .isEqualTo(expectedValue);
}
```

同样，minus()方法可用于减去特定的TemporalAmount。

## 5.Apache Commons DateUtils

Apache Commons Lang 库中的 [DateUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/time/DateUtils.html#addHours-java.util.Date-int-)类公开了一个静态 addHours()方法：

```java
public static Date addHours(Date date, int amount)
```

该方法接受一个java.util.Date对象以及我们希望添加到它的数量，其值可以是正数或负数。

作为结果返回一个新的java.util.Date对象：

```java
@Test
public void givenJavaUtilDate_whenUsingApacheCommons_thenAddHours() {
    Date actualDate = new GregorianCalendar(2018, Calendar.JUNE, 25, 5, 0)
      .getTime();
    Date expectedDate = new GregorianCalendar(2018, Calendar.JUNE, 25, 7, 0)
      .getTime();

    assertThat(DateUtils.addHours(actualDate, 2)).isEqualTo(expectedDate);
}
```

最新版本的Apache Commons Lang可在[Maven Central 获得。](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")

## 6.乔达时间

[Joda Time](http://www.joda.org/joda-time/)是Java8 DateTime API 的替代品，并提供了自己的DateTime 实现。

它的大多数与DateTime相关的类都公开了 plusHours()和minusHours()方法，以帮助我们从DateTime对象中添加或减去给定的小时数。

让我们看一个例子：

```java
@Test
public void givenJodaDateTime_whenUsingPlusHoursToDateTime_thenAddHours() {
    DateTime actualDateTime = new DateTime(2018, 5, 25, 5, 0);
    DateTime expectedDateTime = new DateTime(2018, 5, 25, 7, 0);

    assertThat(actualDateTime.plusHours(2)).isEqualTo(expectedDateTime);
}
```

我们可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"joda-time" AND a%3A"joda-time")轻松查看Joda Time的最新可用版本。

## 七、总结

在本教程中，我们介绍了几种从标准Java日期时间值中添加或减去给定小时数的方法。