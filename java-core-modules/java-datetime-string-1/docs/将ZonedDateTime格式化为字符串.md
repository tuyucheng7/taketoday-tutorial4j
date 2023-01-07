## 1. 概述

在本快速教程中，我们将了解如何将ZonedDateTime转换为字符串。

我们还将了解如何从字符串解析ZonedDateTime 。

## 2. 创建一个 ZonedDateTime

首先，我们将从时区为 UTC的ZonedDateTime开始。我们可以通过多种方式实现这一目标。

我们可以指定年、月、日等：

```java
ZonedDateTime zonedDateTimeOf = ZonedDateTime.of(2018, 01, 01, 0, 0, 0, 0, ZoneId.of("UTC"));
```

我们还可以从当前日期和时间创建一个ZonedDateTime ：

```java
ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));
```

或者，我们可以从现有的LocalDateTime创建一个ZonedDateTime：

```java
LocalDateTime localDateTime = LocalDateTime.now();
ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
```

## 3. ZonedDateTime 转字符串

现在，让我们将ZonedDateTime转换为字符串。为此，我们将使用DateTimeFormatter类。

我们可以使用一些特殊的格式化程序来显示时区数据。可以在[此处](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html)找到完整的格式化程序列表，但我们将看看一些更常见的。

如果我们想显示时区偏移量，我们可以使用格式化程序“Z”或“X”：

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");
String formattedString = zonedDateTime.format(formatter);
```

这会给我们这样的结果：

```plaintext
02/01/2018 - 13:45:30 +0000
```

要包含时区名称，我们可以使用小写的“z”：

```java
DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss z");
String formattedString2 = zonedDateTime.format(formatter2);
```

其输出将是：

```plaintext
02/01/2018 - 13:45:30 UTC
```

## 4.字符串转ZonedDateTime

这个过程也可以反向进行。我们可以获取一个字符串并将其转换回ZonedDateTime。

一种选择是使用 ZonedDateTime类的静态parse()方法：

```java
ZonedDateTime zonedDateTime = ZonedDateTime.parse("2011-12-03T10:15:30+01:00");
```

此方法使用ISO_ZONED_DATE_TIME格式化程序。还有一个采用DateTimeFormatter参数的方法的重载版本。但是，字符串必须包含区域标识符，否则我们会得到一个异常：

```java
assertThrows(DateTimeParseException.class, () -> 
  ZonedDateTime.parse("2011-12-03T10:15:30", DateTimeFormatter.ISO_DATE_TIME));
```

从字符串获取ZonedDateTime的第二个选项涉及 2 个步骤：将字符串转换为LocalDateTime，然后将此对象转换为ZonedDateTime：

```java
ZoneId timeZone = ZoneId.systemDefault();
ZonedDateTime zonedDateTime = LocalDateTime.parse("2011-12-03T10:15:30", 
  DateTimeFormatter.ISO_DATE_TIME).atZone(timeZone);

log.info(zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
```

这种间接方法只是将日期时间与区域 ID 结合起来：

```plaintext
INFO: 2011-12-03T10:15:30+02:00[Europe/Athens]
```

要了解有关将字符串解析为日期的更多信息，请查看我们更深入的日期解析 [文章](https://www.baeldung.com/java-string-to-date)。

## 5.总结

在本文中，我们了解了如何创建ZonedDateTime以及如何将其格式化为字符串。

我们还快速了解了如何解析日期时间字符串并将其转换为ZonedDateTime。