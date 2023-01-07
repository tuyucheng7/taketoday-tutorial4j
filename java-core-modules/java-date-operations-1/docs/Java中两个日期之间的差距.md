## 1. 概述

在这个快速教程中，我们将探讨在Java中计算两个日期之间的差异的多种可能性。

## 延伸阅读：

## [Java 中的增量日期](https://www.baeldung.com/java-increment-date)

为日期添加天数的各种核心和第 3 方方法的概述

[阅读更多](https://www.baeldung.com/java-increment-date)→

## [检查字符串是否是Java中的有效日期](https://www.baeldung.com/java-string-valid-date)

查看在Java中检查字符串是否为有效日期的不同方法

[阅读更多](https://www.baeldung.com/java-string-valid-date)→

## 2.核心Java

### 2.1. 使用 java.util.Date查找天数差异

让我们首先使用核心JavaAPI 进行计算并确定两个日期之间的天数：

```java
@Test
public void givenTwoDatesBeforeJava8_whenDifferentiating_thenWeGetSix()
  throws ParseException {
 
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
    Date firstDate = sdf.parse("06/24/2017");
    Date secondDate = sdf.parse("06/30/2017");

    long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

    assertEquals(6, diff);
}
```

 

### 2.2. 使用java.time.temporal.ChronoUnit找出差异

[Java 8 中的时间 API 使用TemporalUnit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalUnit.html) 接口表示日期时间单位，例如秒或天 。

每个单元都提供一个名为[between](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalUnit.html#between(java.time.temporal.Temporal,java.time.temporal.Temporal))的方法的实现，以根据该特定单元计算两个时间对象之间的时间量。

例如，为了计算两个LocalDateTime之间的秒数：

```java
@Test
public void givenTwoDateTimesInJava8_whenDifferentiatingInSeconds_thenWeGetTen() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tenSecondsLater = now.plusSeconds(10);

    long diff = ChronoUnit.SECONDS.between(now, tenSecondsLater);

    assertEquals(10, diff);
}
```

[ChronoUnit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/ChronoUnit.html)通过实现TemporalUnit接口提供一组具体的时间单位强烈建议静态导入ChronoUnit枚举值以获得更好的可读性：

```java
import static java.time.temporal.ChronoUnit.SECONDS;

// omitted
long diff = SECONDS.between(now, tenSecondsLater);
```

此外，我们可以将任何两个兼容的时间对象传递给 between 方法，甚至是 ZonedDateTime。

ZonedDateTime的 优点在于即使将它们设置为不同的时区，计算也会起作用：

```java
@Test
public void givenTwoZonedDateTimesInJava8_whenDifferentiating_thenWeGetSix() {
    LocalDateTime ldt = LocalDateTime.now();
    ZonedDateTime now = ldt.atZone(ZoneId.of("America/Montreal"));
    ZonedDateTime sixMinutesBehind = now
      .withZoneSameInstant(ZoneId.of("Asia/Singapore"))
      .minusMinutes(6);
    
    long diff = ChronoUnit.MINUTES.between(sixMinutesBehind, now);
    
    assertEquals(6, diff);
}

```

### 2.3. 使用时间#until()

任何[Temporal](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/Temporal.html)对象，例如LocalDate 或 ZonedDateTime，都提供了一个[until](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/Temporal.html#until(java.time.temporal.Temporal,java.time.temporal.TemporalUnit))方法来根据指定的单位计算另一个Temporal之前的时间量：

```java
@Test
public void givenTwoDateTimesInJava8_whenDifferentiatingInSecondsUsingUntil_thenWeGetTen() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tenSecondsLater = now.plusSeconds(10);

    long diff = now.until(tenSecondsLater, ChronoUnit.SECONDS);

    assertEquals(10, diff);
}
```

Temporal#until和TemporalUnit#between 是实现相同功能的两个不同 API。

### 2.4. 使用java.time.Duration 和 java.time.Period

在Java8 中，时间 API 引入了两个新类：[Duration](https://www.baeldung.com/java-8-date-time-intro#period)[和](https://www.baeldung.com/java-8-date-time-intro#period)[Period](https://www.baeldung.com/java-8-date-time-intro#period)。

如果我们想在基于时间(小时、分钟或秒)的时间量内计算两个日期时间之间的差异，我们可以使用Duration 类：

```java
@Test
public void givenTwoDateTimesInJava8_whenDifferentiating_thenWeGetSix() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime sixMinutesBehind = now.minusMinutes(6);

    Duration duration = Duration.between(now, sixMinutesBehind);
    long diff = Math.abs(duration.toMinutes());

    assertEquals(6, diff);
}
```

但是，如果我们尝试使用Period类来表示两个日期之间的差异，我们应该警惕陷阱。

一个例子将很快解释这个陷阱。

让我们使用Period类计算两个日期之间的天数：

```java
@Test
public void givenTwoDatesInJava8_whenUsingPeriodGetDays_thenWorks()  {
    LocalDate aDate = LocalDate.of(2020, 9, 11);
    LocalDate sixDaysBehind = aDate.minusDays(6);

    Period period = Period.between(aDate, sixDaysBehind);
    int diff = Math.abs(period.getDays());

    assertEquals(6, diff);
}
```

如果我们运行上面的测试，它就会通过。我们可能认为Period类可以方便地解决我们的问题。到目前为止，一切都很好。

如果这种方式适用于六天的差异，我们毫不怀疑它也适用于 60 天。

因此，让我们将上面测试中的6更改为60 ，看看会发生什么：

```java
@Test
public void givenTwoDatesInJava8_whenUsingPeriodGetDays_thenDoesNotWork() {
    LocalDate aDate = LocalDate.of(2020, 9, 11);
    LocalDate sixtyDaysBehind = aDate.minusDays(60);

    Period period = Period.between(aDate, sixtyDaysBehind);
    int diff = Math.abs(period.getDays());

    assertEquals(60, diff);
}
```

现在，如果我们再次运行测试，我们将看到：

```java
java.lang.AssertionError: 
Expected :60
Actual   :29
```

哎呀！为什么Period类将差异报告为29天？

这是因为 Period类以“x 年，y 个月和 z 天”的格式表示基于日期的时间量。 当我们调用它的getDays() 方法时，它只返回“z days”部分。

因此，上面测试中的period 对象的值为“0年1月29日”：

```java
@Test
public void givenTwoDatesInJava8_whenUsingPeriod_thenWeGet0Year1Month29Days() {
    LocalDate aDate = LocalDate.of(2020, 9, 11);
    LocalDate sixtyDaysBehind = aDate.minusDays(60);
    Period period = Period.between(aDate, sixtyDaysBehind);
    int years = Math.abs(period.getYears());
    int months = Math.abs(period.getMonths());
    int days = Math.abs(period.getDays());
    assertArrayEquals(new int[] { 0, 1, 29 }, new int[] { years, months, days });
}
```

如果我们想使用Java8 的时间 API 计算天数差异，ChronoUnit.DAYS.between() 方法是最直接的方法。

## 3. 外部图书馆

### 3.1. 乔达时间

我们也可以用JodaTime做一个相对简单的实现：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.9.9</version>
</dependency>
```

你可以从 Maven Central获取最新版本的[Joda-time 。](https://search.maven.org/classic/#search|ga|1|a%3A"joda-time")

LocalDate案例：

```java
@Test
public void givenTwoDatesInJodaTime_whenDifferentiating_thenWeGetSix() {
    org.joda.time.LocalDate now = org.joda.time.LocalDate.now();
    org.joda.time.LocalDate sixDaysBehind = now.minusDays(6);

    long diff = Math.abs(Days.daysBetween(now, sixDaysBehind).getDays());
    assertEquals(6, diff);
}

```

同样，对于LocalDateTime：

```java
@Test
public void givenTwoDateTimesInJodaTime_whenDifferentiating_thenWeGetSix() {
    org.joda.time.LocalDateTime now = org.joda.time.LocalDateTime.now();
    org.joda.time.LocalDateTime sixMinutesBehind = now.minusMinutes(6);

    long diff = Math.abs(Minutes.minutesBetween(now, sixMinutesBehind).getMinutes());
    assertEquals(6, diff);
}

```

### 3.2. 日期4J

Date4j还提供了一个简单明了的实现——请注意，在这种情况下，我们需要明确提供一个TimeZone。

让我们从 Maven 依赖项开始：

```xml
<dependency>
    <groupId>com.darwinsys</groupId>
    <artifactId>hirondelle-date4j</artifactId>
    <version>1.5.1</version>
</dependency>
```

这是使用标准DateTime的快速测试：

```java
@Test
public void givenTwoDatesInDate4j_whenDifferentiating_thenWeGetSix() {
    DateTime now = DateTime.now(TimeZone.getDefault());
    DateTime sixDaysBehind = now.minusDays(6);
 
    long diff = Math.abs(now.numDaysFrom(sixDaysBehind));

    assertEquals(6, diff);
}
```

## 4。总结

在本文中，我们说明了几种计算日期(有时间和没有时间)之间差异的方法，既可以使用普通Java也可以使用外部库。