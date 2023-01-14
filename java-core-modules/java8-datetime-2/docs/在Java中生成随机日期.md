## 1. 概述

在本教程中，我们将了解如何以有界和无界的方式生成随机日期和时间。

我们将研究如何使用遗留的[java.util.Date](https://www.baeldung.com/java-util-date-sql-date) API 以及Java 8中的[新日期时间库生成这些值。](https://www.baeldung.com/java-8-date-time-intro)

## 2.随机日期和时间

[与纪元时间](https://en.wikipedia.org/wiki/Unix_time)相比，日期和时间不过是 32 位整数，因此我们可以通过以下简单算法生成随机时间值：

1.  生成一个随机的 32 位数字，一个int
2.  将生成的随机值传递给适当的日期和时间构造函数或构建器

### 2.1. 有界瞬间

java.time.I nstant是Java 8 中新增的日期和时间之一。它们表示时间线上的瞬时点。

为了 在另外两个 Instant 之间生成一个随机Instant ，我们可以：

1.  在给定Instants的纪元秒之间生成一个随机数
2.  通过将该随机数传递给 [ofEpochSecond()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html#ofEpochSecond(long))方法来创建随机Instant 

```java
public static Instant between(Instant startInclusive, Instant endExclusive) {
    long startSeconds = startInclusive.getEpochSecond();
    long endSeconds = endExclusive.getEpochSecond();
    long random = ThreadLocalRandom
      .current()
      .nextLong(startSeconds, endSeconds);

    return Instant.ofEpochSecond(random);
}
```

为了在多线程环境中实现更高的吞吐量，我们使用 [ThreadLocalRandom](https://www.baeldung.com/java-thread-local-random)来生成随机数。

我们可以验证生成的Instant 总是大于或等于第一个 Instant 并且 小于第二个 Instant：

```java
Instant hundredYearsAgo = Instant.now().minus(Duration.ofDays(100  365));
Instant tenDaysAgo = Instant.now().minus(Duration.ofDays(10));
Instant random = RandomDateTimes.between(hundredYearsAgo, tenDaysAgo);
assertThat(random).isBetween(hundredYearsAgo, tenDaysAgo);
```

当然，请记住，测试随机性本质上是不确定的，通常不建议在实际应用中使用。

同样，也可以在另一个 Instant 之后或之前生成一个随机Instant ：

```java
public static Instant after(Instant startInclusive) {
    return between(startInclusive, Instant.MAX);
}

public static Instant before(Instant upperExclusive) {
    return between(Instant.MIN, upperExclusive);
}
```

### 2.2. 限时日期

java.util.Date 构造函数之一 采用纪元后的毫秒数。因此，我们可以使用相同的算法在其他两个日期之间生成一个随机 日期 ：

```java
public static Date between(Date startInclusive, Date endExclusive) {
    long startMillis = startInclusive.getTime();
    long endMillis = endExclusive.getTime();
    long randomMillisSinceEpoch = ThreadLocalRandom
      .current()
      .nextLong(startMillis, endMillis);

    return new Date(randomMillisSinceEpoch);
}
```

同样，我们应该能够验证此行为：

```java
long aDay = TimeUnit.DAYS.toMillis(1);
long now = new Date().getTime();
Date hundredYearsAgo = new Date(now - aDay  365  100);
Date tenDaysAgo = new Date(now - aDay  10);
Date random = LegacyRandomDateTimes.between(hundredYearsAgo, tenDaysAgo);
assertThat(random).isBetween(hundredYearsAgo, tenDaysAgo);
```

### 2.3. 无界瞬间

为了生成一个完全随机的Instant，我们可以简单地生成一个随机整数并将其传递给ofEpochSecond() 方法：

```java
public static Instant timestamp() {
    return Instant.ofEpochSecond(ThreadLocalRandom.current().nextInt());
}
```

使用 32 位秒，因为纪元时间会生成更合理的随机时间，因此我们在这里使用 nextInt() 方法。

此外，该值仍应介于Java可以处理的最小和最大Instant 可能值之间：

```java
Instant random = RandomDateTimes.timestamp();
assertThat(random).isBetween(Instant.MIN, Instant.MAX);
```

### 2.4. 无界日期

与有界示例类似，我们可以将随机值传递给 Date 的 构造函数以生成随机 Date：

```java
public static Date timestamp() {
    return new Date(ThreadLocalRandom.current().nextInt()  1000L);
}
```

由于 构造函数的时间单位是毫秒，我们通过将 32 位纪元秒乘以 1000 将其转换为毫秒。

当然，这个值仍然在可能的最小和最大 Date 值之间：

```java
Date MIN_DATE = new Date(Long.MIN_VALUE);
Date MAX_DATE = new Date(Long.MAX_VALUE);
Date random = LegacyRandomDateTimes.timestamp();
assertThat(random).isBetween(MIN_DATE, MAX_DATE);
```

## 3.随机日期

到目前为止，我们生成了包含日期和时间成分的随机时间。同样，我们可以使用纪元日的概念来生成仅包含日期成分的随机时间。

纪元日等于自 1970 年 1 月 1 日以来的天数。因此，为了生成随机日期，我们只需生成一个随机数并将该数字用作纪元日。

### 3.1. 有界的

我们需要一个仅包含日期组件的时间抽象，因此java.time.LocalDate 似乎是一个不错的选择：

```java
public static LocalDate between(LocalDate startInclusive, LocalDate endExclusive) {
    long startEpochDay = startInclusive.toEpochDay();
    long endEpochDay = endExclusive.toEpochDay();
    long randomDay = ThreadLocalRandom
      .current()
      .nextLong(startEpochDay, endEpochDay);

    return LocalDate.ofEpochDay(randomDay);
}
```

在这里，我们使用 [toEpochDay()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/chrono/ChronoLocalDate.html#toEpochDay()) 方法将每个 LocalDate 转换为其对应的纪元日。同样，我们可以验证这种做法是否正确：

```java
LocalDate start = LocalDate.of(1989, Month.OCTOBER, 14);
LocalDate end = LocalDate.now();
LocalDate random = RandomDates.between(start, end);
assertThat(random).isAfterOrEqualTo(start, end);
```

### 3.2. 无界

为了生成不考虑任何范围的随机日期，我们可以简单地生成一个随机纪元日：

```java
public static LocalDate date() {
    int hundredYears = 100  365;
    return LocalDate.ofEpochDay(ThreadLocalRandom
      .current().nextInt(-hundredYears, hundredYears));
}
```

我们的随机日期生成器从纪元前后 100 年中随机选择一天。同样，这背后的基本原理是生成合理的日期值：

```java
LocalDate randomDay = RandomDates.date();
assertThat(randomDay).isBetween(LocalDate.MIN, LocalDate.MAX);
```

## 4.随机时间

与我们对日期所做的类似，我们可以生成仅包含时间成分的随机时间。为了做到这一点，我们可以使用一天中的第二个概念。也就是说，一个随机时间等于一个随机数，表示自一天开始以来的秒数。

### 4.1. 有界的

java.time.LocalTime 类是一个时间抽象，只封装了时间组件： 

```java
public static LocalTime between(LocalTime startTime, LocalTime endTime) {
    int startSeconds = startTime.toSecondOfDay();
    int endSeconds = endTime.toSecondOfDay();
    int randomTime = ThreadLocalRandom
      .current()
      .nextInt(startSeconds, endSeconds);

    return LocalTime.ofSecondOfDay(randomTime);
}
```

为了在另外两个时间之间生成一个随机时间，我们可以：

1.  在给定时间的第二天之间生成一个随机数
2.  使用该随机数创建一个随机时间

我们可以很容易地验证这个随机时间生成算法的行为：

```java
LocalTime morning = LocalTime.of(8, 30);
LocalTime randomTime = RandomTimes.between(LocalTime.MIDNIGHT, morning);
assertThat(randomTime)
  .isBetween(LocalTime.MIDNIGHT, morning)
  .isBetween(LocalTime.MIN, LocalTime.MAX);
```

### 4.2. 无界

即使是无限时间值也应该在 00:00:00 到 23:59:59 范围内，所以我们可以通过委托简单地实现这个逻辑：

```java
public static LocalTime time() {
    return between(LocalTime.MIN, LocalTime.MAX);
}
```

## 5.总结

在本教程中，我们将随机日期和时间的定义简化为随机数。然后，我们看到这种减少如何帮助我们生成随机时间值，其行为类似于时间戳、日期或时间。