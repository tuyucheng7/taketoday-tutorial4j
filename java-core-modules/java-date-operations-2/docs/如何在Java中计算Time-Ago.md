## 1. 概述

计算相对时间和两个时间点之间的持续时间是软件系统中的常见用例。例如，我们可能想向用户显示自从在社交媒体平台上发布新图片等事件以来已经过去了多长时间。这种“时间之前”文本的例子有“5 分钟之前”、“1 年前”等。

虽然语义和单词的选择完全取决于上下文，但总体思路是相同的。

在本教程中，我们将探讨在Java中计算时间前的几种解决方案。由于[Java 8 引入了新的 Date and Time API](https://www.baeldung.com/java-8-date-time-intro)，我们将分别讨论版本 7 和版本 8 的解决方案。

## 2.Java版本 7

Java 7中有几个与时间相关的类，而且由于Java 7 Date API的不足，还提供了几个第三方时间日期库。

首先，让我们使用纯Java7 来计算“time ago”。

### 2.1. 纯Java 7

我们定义了一个枚举，它包含不同的时间粒度并将它们转换为毫秒：

```java
public enum TimeGranularity {
    SECONDS {
        public long toMillis() {
            return TimeUnit.SECONDS.toMillis(1);
        }
    }, MINUTES {
        public long toMillis() {
            return TimeUnit.MINUTES.toMillis(1);
        }
    }, HOURS {
        public long toMillis() {
            return TimeUnit.HOURS.toMillis(1);
        }
    }, DAYS {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(1);
        }
    }, WEEKS {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(7);
        }
    }, MONTHS {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(30);
        }
    }, YEARS {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(365);
        }
    }, DECADES {
        public long toMillis() {
            return TimeUnit.DAYS.toMillis(365  10);
        }
    };

    public abstract long toMillis();
}
```

我们使用了java.util.concurrent.TimeUnit 枚举，这是一个强大的时间转换工具。使用TimeUnit枚举，我们为TimeGranularity枚举的每个值覆盖toMillis()抽象方法，以便它返回与每个值相等的毫秒数。例如，对于“decade”，它返回 3650 天的毫秒数。

作为定义TimeGranularity枚举的结果，我们可以定义两种方法。第一个接受一个java.util.Date对象和一个TimeGranularity实例，并返回一个“time ago”字符串：

```java
static String calculateTimeAgoByTimeGranularity(Date pastTime, TimeGranularity granularity) {
    long timeDifferenceInMillis = getCurrentTime() - pastTime.getTime();
    return timeDifferenceInMillis / granularity.toMillis() + " " + 
      granularity.name().toLowerCase() + " ago";
}
```

此方法将当前时间与给定时间的差异除以TimeGranularity值(以毫秒为单位)。因此，我们可以粗略地计算出在指定时间粒度内自给定时间以来经过的时间量。

我们使用getCurrentTime()方法来获取当前时间。为了测试，我们返回一个固定的时间点，避免从本地机器读取时间。实际上，此方法将使用System.currentTimeMillis()或LocalDateTime.now() 返回当前时间的实际值。

让我们测试一下方法：

```java
Assert.assertEquals("5 hours ago", 
  TimeAgoCalculator.calculateTimeAgoByTimeGranularity(
    new Date(getCurrentTime() - (5  60  60  1000)), TimeGranularity.HOURS));
```

此外，我们还可以编写一个方法来自动检测最大的合适时间粒度并返回更人性化的输出：

```java
static String calculateHumanFriendlyTimeAgo(Date pastTime) {
    long timeDifferenceInMillis = getCurrentTime() - pastTime.getTime();
    if (timeDifferenceInMillis / TimeGranularity.DECADES.toMillis() > 0) {
        return "several decades ago";
    } else if (timeDifferenceInMillis / TimeGranularity.YEARS.toMillis() > 0) {
        return "several years ago";
    } else if (timeDifferenceInMillis / TimeGranularity.MONTHS.toMillis() > 0) {
        return "several months ago";
    } else if (timeDifferenceInMillis / TimeGranularity.WEEKS.toMillis() > 0) {
        return "several weeks ago";
    } else if (timeDifferenceInMillis / TimeGranularity.DAYS.toMillis() > 0) {
        return "several days ago";
    } else if (timeDifferenceInMillis / TimeGranularity.HOURS.toMillis() > 0) {
        return "several hours ago";
    } else if (timeDifferenceInMillis / TimeGranularity.MINUTES.toMillis() > 0) {
        return "several minutes ago";
    } else {
        return "moments ago";
    }
}
```

现在，让我们看一个测试以查看示例用法：

```java
Assert.assertEquals("several hours ago", 
  TimeAgoCalculator.calculateHumanFriendlyTimeAgo(new Date(getCurrentTime() - (5  60  60  1000))));
```

根据上下文，我们可以使用不同的词，例如“很少”、“几个”、“很多”，甚至是确切的值。

### 2.2. 乔达时代图书馆

在Java8 发布之前，[Joda-Time](https://www.baeldung.com/joda-time)是Java中各种时间和日期相关操作的事实标准。我们可以使用 Joda-Time 库的三个类来计算“时间前”：

-   org.joda.time.Period获取org.joda.time.DateTime的两个对象并计算这两个时间点之间的差异
-   org.joda.time.format.PeriodFormatter定义打印Period对象的格式
-   org.joda.time.format.PeriodFormatuilder是一个用于创建自定义PeriodFormatter的构建器类

我们可以使用这三个类轻松获取现在和过去某个时间之间的准确时间：

```java
static String calculateExactTimeAgoWithJodaTime(Date pastTime) {
    Period period = new Period(new DateTime(pastTime.getTime()), new DateTime(getCurrentTime()));
    PeriodFormatter formatter = new PeriodFormatterBuilder().appendYears()
      .appendSuffix(" year ", " years ")
      .appendSeparator("and ")
      .appendMonths()
      .appendSuffix(" month ", " months ")
      .appendSeparator("and ")
      .appendWeeks()
      .appendSuffix(" week ", " weeks ")
      .appendSeparator("and ")
      .appendDays()
      .appendSuffix(" day ", " days ")
      .appendSeparator("and ")
      .appendHours()
      .appendSuffix(" hour ", " hours ")
      .appendSeparator("and ")
      .appendMinutes()
      .appendSuffix(" minute ", " minutes ")
      .appendSeparator("and ")
      .appendSeconds()
      .appendSuffix(" second", " seconds")
      .toFormatter();
    return formatter.print(period);
}
```

让我们看一个示例用法：

```java
Assert.assertEquals("5 hours and 1 minute and 1 second", 
  TimeAgoCalculator.calculateExactTimeAgoWithJodaTime(new Date(getCurrentTime() - (5  60  60  1000 + 1  60  1000 + 1  1000))));

```

也可以生成更人性化的输出：

```java
static String calculateHumanFriendlyTimeAgoWithJodaTime(Date pastTime) {
    Period period = new Period(new DateTime(pastTime.getTime()), new DateTime(getCurrentTime()));
    if (period.getYears() != 0) {
        return "several years ago";
    } else if (period.getMonths() != 0) {
        return "several months ago";
    } else if (period.getWeeks() != 0) {
        return "several weeks ago";
    } else if (period.getDays() != 0) {
        return "several days ago";
    } else if (period.getHours() != 0) {
        return "several hours ago";
    } else if (period.getMinutes() != 0) {
        return "several minutes ago";
    } else {
        return "moments ago";
    }
}
```

我们可以运行一个测试，看看这个方法返回了一个更人性化的“time ago”字符串：

```java
Assert.assertEquals("several hours ago", 
  TimeAgoCalculator.calculateHumanFriendlyTimeAgoWithJodaTime(new Date(getCurrentTime() - (5  60  60  1000))));

```

同样，我们可以根据用例使用不同的术语，例如“一个”、“几个”或“几个”。

### 2.3. 乔达时间时区

使用 Joda-Time 库在“time ago”的计算中添加时区非常简单：

```java
String calculateZonedTimeAgoWithJodaTime(Date pastTime, TimeZone zone) {
    DateTimeZone dateTimeZone = DateTimeZone.forID(zone.getID());
    Period period = new Period(new DateTime(pastTime.getTime(), dateTimeZone), new DateTime(getCurrentTimeByTimeZone(zone)));
    return PeriodFormat.getDefault().print(period);
}
```

getCurrentTimeByTimeZone()方法返回指定时区的当前时间值。对于测试，此方法返回一个固定时间点，但实际上，这应该使用Calendar.getInstance(zone).getTimeInMillis()或LocalDateTime.now(zone) 返回当前时间的实际值。

## 3.Java 8

[Java 8 引入了一个新的改进的 Date and Time API](https://www.baeldung.com/java-8-date-time-intro)，它采用了 Joda-Time 库的许多思想。我们可以使用原生的java.time.Duration和java.time.Period类来计算“时间前”：

```java
static String calculateTimeAgoWithPeriodAndDuration(LocalDateTime pastTime, ZoneId zone) {
    Period period = Period.between(pastTime.toLocalDate(), getCurrentTimeByTimeZone(zone).toLocalDate());
    Duration duration = Duration.between(pastTime, getCurrentTimeByTimeZone(zone));
    if (period.getYears() != 0) {
        return "several years ago";
    } else if (period.getMonths() != 0) {
        return "several months ago";
    } else if (period.getDays() != 0) {
        return "several days ago";
    } else if (duration.toHours() != 0) {
        return "several hours ago";
    } else if (duration.toMinutes() != 0) {
        return "several minutes ago";
    } else if (duration.getSeconds() != 0) {
        return "several seconds ago";
    } else {
        return "moments ago";
    }
}
```

上面的代码片段支持时区并且仅使用原生Java8 API。

## 4. PrettyTime 图书馆

PrettyTime 是一个功能强大的库，专门提供具有 i18n 支持的“time ago”功能。此外，它是高度可定制的、易于使用的，并且可以与Java版本 7 和 8 一起使用。

首先，让我们将它的[依赖](https://mvnrepository.com/artifact/org.ocpsoft.prettytime/prettytime/3.2.7.Final)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.ocpsoft.prettytime</groupId>
    <artifactId>prettytime</artifactId>
    <version>3.2.7.Final</version>
</dependency>
```

现在以人性化的格式获取“time ago”非常容易：

```java
String calculateTimeAgoWithPrettyTime(Date pastTime) {
    PrettyTime prettyTime = new PrettyTime();
    return prettyTime.format(pastTime);
}
```

## 5.Time4J 库

最后，Time4J 是另一个用于在Java中处理时间和日期数据的优秀库。它有一个PrettyTime类，可用于计算时间。

让我们添加它的[依赖](https://mvnrepository.com/artifact/net.time4j)项：

```xml
<dependency>
    <groupId>net.time4j</groupId>
    <artifactId>time4j-base</artifactId>
    <version>5.9</version>
</dependency>
<dependency>
    <groupId>net.time4j</groupId>
    <artifactId>time4j-sqlxml</artifactId>
    <version>5.8</version>
</dependency>
```

添加此依赖项后，计算时间就非常简单了：

```java
String calculateTimeAgoWithTime4J(Date pastTime, ZoneId zone, Locale locale) {
    return PrettyTime.of(locale).printRelative(pastTime.toInstant(), zone);
}
```

与 PrettyTime 库一样，Time4J 也支持开箱即用的 i18n。

## 六，总结

在本文中，我们讨论了在Java中计算时间前的不同方法。

纯Java和第三方库都有解决方案。由于Java 8引入了新的Date and Time API，因此8前后版本的纯java解决方案有所不同。