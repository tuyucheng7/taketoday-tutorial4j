## 1. 概述

[在遗留系统中，当新的日期和时间 API](https://www.baeldung.com/java-8-date-time-intro) 和强烈推荐的[Joda-Time 库](https://www.baeldung.com/joda-time)都不可用时，我们可能需要使用日期。

在这个简短的教程中，我们将研究几种方法来了解如何在Java8 之前的系统中获取当前日期。

## 2.系统时间

当我们只需要一个表示当前日期和时间的数值时，我们可以使用系统时间。要获取自格林威治标准时间 1970 年 1 月 1 日 00:00:00以来经过的毫秒数，我们可以使用返回long的currentTimeMillis方法：

```java
long elapsedMilliseconds = System.currentTimeMillis();
```

当我们想要更精确地[测量经过的时间时](https://www.baeldung.com/java-measure-elapsed-time)，我们可以使用nanoTime方法。这将返回从固定但任意时刻开始经过的纳秒值。

这个任意时间对于 JVM 内的所有调用都是相同的，因此返回的值仅对计算多次nanoTime调用之间经过的纳秒数的差异有用：

```java
long elapsedNanosecondsStart = System.nanoTime();
long elapsedNanoseconds = System.nanoTime() - elapsedNanosecondsStart;
```

## 3. java.util包

使用java.util包中的类，我们可以表示一个时刻，通常是自1970 年 1 月 1 日 00:00:00 GMT以来经过的毫秒数。

### 3.1. java.util.Date

我们可以使用Date对象表示特定的日期和时间。这包含毫秒精度和时区信息。

虽然有许多可用的构造函数，但创建表示本地时区当前日期的Date对象的最简单方法是使用基本构造函数：

```java
Date currentUtilDate = new Date();
```

现在让我们为特定的日期和时间创建一个Date对象。我们可以使用上述构造函数并简单地传递毫秒值。

或者，我们可以使用SimpleDateFormat 类[将String](https://www.baeldung.com/java-string-to-date)值转换为实际的Date对象：

```java
SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
Date customUtilDate = dateFormatter.parse("30-01-2020 10:11:12");
```

我们可以使用范围广泛的日期模式来满足我们的需要。

### 3.2. java.util.日历

Calendar对象可以做Date做的事情，而且它更适合日期算术计算，因为它也可以采用[Locale](https://www.baeldung.com/java-8-localization)。我们可以将Locale指定为地理、政治或文化区域。

要获取当前日期，不指定TimeZone或Locale，我们可以使用getInstance方法：

```java
Calendar currentUtilCalendar = Calendar.getInstance();
```

对于日历到日期的转换，我们可以简单地使用getTime方法：

```java
Date currentDate = Calendar.getInstance().getTime();
```

有趣的是， [GregorianCalendar](https://www.baeldung.com/java-gregorian-calendar) 类是世界上使用最广泛的日历的实现。

## 4. java.sql包

接下来，我们将研究表示等效 SQL 对象的java.util.Date类的三个扩展。

### 4.1. java.sql.Date

使用java.sql.Date对象，我们无法访问时区信息，并且精度在日级别被截断。为了表示今天，我们可以使用采用毫秒长表示的构造函数：

```java
Date currentSqlDate = new Date(System.currentTimeMillis());
```

和以前一样，对于特定日期，我们可以先使用SimpleDateFormat类转换为java.util.Date，然后使用getTime方法获取毫秒数。然后，我们可以将这个值传递给java.sql.Date构造函数。

当日期的字符串表示匹配yyyy-[m]m-[d]d模式时，我们可以简单地使用valueOf方法：

```java
Date customSqlDate = Date.valueOf("2020-01-30");
```

### 4.2. java.sql.时间

java.sql.Time对象提供对小时、分钟和秒信息的访问——同样，没有访问时区的权限。让我们使用毫秒表示获取当前时间：

```java
Time currentSqlTime = new Time(System.currentTimeMillis());
```

要使用valueOf方法指定时间，我们可以传入一个匹配hh:mm:ss模式的值：

```java
Time customSqlTime = Time.valueOf("10:11:12");
```

### 4.3. java.sql.时间戳

在最后一节中，我们将使用Timestamp类组合 SQL日期和时间信息。这使我们能够精确到纳秒级。

让我们通过再次将当前毫秒数的long值传递给构造函数来创建一个Timestamp对象：

```java
Timestamp currentSqlTimestamp = new Timestamp(System.currentTimeMillis());
```

最后，让我们使用具有所需yyyy-[m]m-[d]d hh:mm:ss[.f…]模式的valueOf方法创建一个新的自定义时间戳：

```java
Timestamp customSqlTimestamp = Timestamp.valueOf("2020-1-30 10:11:12.123456789");
```

## 5.总结

在这个简短的教程中，我们了解了如何在不使用Java8 或任何外部库的情况下获取当前日期和给定时刻的日期。