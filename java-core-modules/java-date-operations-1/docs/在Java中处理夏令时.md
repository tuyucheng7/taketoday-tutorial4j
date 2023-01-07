## 1. 概述

[夏令时 ( Daylight Saving Time](https://en.wikipedia.org/wiki/Daylight_saving_time) ) 或 DST 是在夏季月份提前时钟以利用额外一小时的自然光(节省加热功率、照明功率、改善情绪等)的做法。

它被[多个国家](https://en.wikipedia.org/wiki/Daylight_saving_time_by_country)/地区使用，在处理日期和时间戳时需要考虑在内。

在本教程中，我们将看到如何根据不同的位置在Java中正确处理 DST。

## 2. JRE 和 DST 可变性

首先，了解全球 DST 区域[经常变化](http://www.oracle.com/technetwork/java/javase/dst-faq-138158.html#change)并且没有中央机构协调它是非常重要的。

一个国家，或者在某些情况下甚至是一个城市，可以决定是否以及如何申请或撤销它。

每次发生时，更改都会记录在[IANA 时区数据库](https://www.iana.org/time-zones)中，并且更新将在[JRE 的未来版本中推出](http://www.oracle.com/technetwork/java/javase/tzdata-versions-138805.html)。

如果无法等待，我们可以通过[Java SE 下载页面](http://www.oracle.com/technetwork/java/javase/downloads/index.html)上提供的名为[Java Time Zone Updater Tool](http://www.oracle.com/technetwork/java/javase/documentation/timezones-137583.html)的官方 Oracle 工具将包含新 DST 设置的修改后的时区数据强制导入 JRE 。

## 3. 错误方式：三字母时区 ID

回到 JDK 1.1 时代，API 允许三个字母的时区 ID，但这导致了几个问题。

首先，这是因为同一个三字母 ID 可以指代多个时区。例如，CST可以是美国的“Central Standard Time”，也可以是“China Standard Time”。Java 平台只能识别其中之一。

另一个问题是标准时区从不考虑夏令时。多个地区/地区/城市可以在同一标准时区内设置本地夏令时，因此标准时间不会遵守它。

由于向后兼容，仍然可以使用三个字母的 ID实例化[java.util.Timezone 。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/TimeZone.html)但是，此方法已弃用，不应再使用。

## 4. 正确方法：TZDB Timezone ID

在Java中处理 DST 的正确方法是使用特定的 TZDB 时区 ID 实例化时区，例如。“欧洲/罗马”。

然后，我们会将其与特定时间类(如java.util.Calendar )结合使用，以正确配置TimeZone 的原始偏移量(相对于 GMT 时区)和自动 DST 偏移调整。

让我们看看在使用正确的时区时如何自动处理从GMT+1到GMT+2的转换(发生在意大利，时间是 2018 年 3 月 25 日凌晨 02:00)：

```java
TimeZone tz = TimeZone.getTimeZone("Europe/Rome");
TimeZone.setDefault(tz);
Calendar cal = Calendar.getInstance(tz, Locale.ITALIAN);
DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALIAN);
Date dateBeforeDST = df.parse("2018-03-25 01:55");
cal.setTime(dateBeforeDST);
 
assertThat(cal.get(Calendar.ZONE_OFFSET)).isEqualTo(3600000);
assertThat(cal.get(Calendar.DST_OFFSET)).isEqualTo(0);
```

正如我们所见，ZONE_OFFSET是 60 分钟(因为意大利是GMT+1)，而DST_OFFSET是 0 。

让我们向Calendar添加十分钟：

```java
cal.add(Calendar.MINUTE, 10);

```

现在DST_OFFSET也变成了 60 分钟，并且该国已将其本地时间从CET(中欧时间)转换为CEST(中欧夏令时)，即GMT+2：

```java
Date dateAfterDST = cal.getTime();
 
assertThat(cal.get(Calendar.DST_OFFSET))
  .isEqualTo(3600000);
assertThat(dateAfterDST)
  .isEqualTo(df.parse("2018-03-25 03:05"));

```

如果我们在控制台中显示这两个日期，我们也会看到时区发生变化：

```java
Before DST (00:55 UTC - 01:55 GMT+1) = Sun Mar 25 01:55:00 CET 2018
After DST (01:05 UTC - 03:05 GMT+2) = Sun Mar 25 03:05:00 CEST 2018
```

作为最终测试，我们可以测量两个Date之间的距离，1:55 和 3:05：

```java
Long deltaBetweenDatesInMillis = dateAfterDST.getTime() - dateBeforeDST.getTime();
Long tenMinutesInMillis = (1000L  60  10);
 
assertThat(deltaBetweenDatesInMillis)
  .isEqualTo(tenMinutesInMillis);

```

正如我们所料，距离是 10 分钟而不是 70 分钟。

我们已经了解了如何通过正确使用TimeZone和Locale来避免陷入使用Date时可能遇到的常见陷阱。

## 5. 最佳方式：Java 8 Date/Time API

使用这些线程不安全且并不总是用户友好的java.util类一直很困难，特别是由于兼容性问题导致无法正确重构它们。

为此，Java 8 引入了一个全新的包java.time和一个全新的 API 集，即[Date/Time API。](https://www.baeldung.com/java-8-date-time-intro)这是以 ISO 为中心的，完全线程安全的，并且深受著名图书馆 Joda-Time 的启发。

让我们仔细看看这个新类，从java.util.Date的继任者java.time.LocalDateTime 开始：

```java
LocalDateTime localDateTimeBeforeDST = LocalDateTime
  .of(2018, 3, 25, 1, 55);
 
assertThat(localDateTimeBeforeDST.toString())
  .isEqualTo("2018-03-25T01:55");

```

我们可以观察LocalDateTime如何符合ISO8601配置文件，这是一种标准且广泛采用的日期时间表示法。

不过，它完全不知道Zones和Offsets，这就是为什么我们需要将其转换为完全支持 DST 的java.time.ZonedDateTime：

```java
ZoneId italianZoneId = ZoneId.of("Europe/Rome");
ZonedDateTime zonedDateTimeBeforeDST = localDateTimeBeforeDST
  .atZone(italianZoneId);
 
assertThat(zonedDateTimeBeforeDST.toString())
  .isEqualTo("2018-03-25T01:55+01:00[Europe/Rome]"); 

```

正如我们所见，现在日期包含两个基本的尾随信息：+01:00是ZoneOffset，而[Europe/Rome]是ZoneId。

和前面的例子一样，让我们通过增加十分钟来触发夏令时：

```java
ZonedDateTime zonedDateTimeAfterDST = zonedDateTimeBeforeDST
  .plus(10, ChronoUnit.MINUTES);
 
assertThat(zonedDateTimeAfterDST.toString())
  .isEqualTo("2018-03-25T03:05+02:00[Europe/Rome]");

```

同样，我们看到时间和时区偏移量是如何向前移动的，并且仍然保持相同的距离：

```java
Long deltaBetweenDatesInMinutes = ChronoUnit.MINUTES
  .between(zonedDateTimeBeforeDST,zonedDateTimeAfterDST);
assertThat(deltaBetweenDatesInMinutes)
  .isEqualTo(10);

```

## 六，总结

我们已经了解了什么是夏令时以及如何通过不同版本的Java核心 API 中的一些实际示例来处理它。

使用Java8 及更高版本时，由于易于使用且具有标准的线程安全特性，因此鼓励使用新的java.time包。