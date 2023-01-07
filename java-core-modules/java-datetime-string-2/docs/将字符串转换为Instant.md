## 1. 概述

在本快速教程中，我们将解释如何使用java.time包中的类使用 Java将String转换为Instant 。首先，我们将使用LocalDateTime类实施解决方案。然后，我们将使用Instant类获取时区内的瞬间。

## 2. 使用LocalDateTime类

java.time.LocalDateTime表示没有时区的日期和/或时间。它是一个本地时间对象，因为它仅在特定上下文中有效并且不能在该上下文之外使用。此上下文通常是执行代码的机器。

要从String获取时间，我们可以使用DateTimeFormatter创建格式化对象并将此格式化程序传递给LocalDateTime的解析方法。此外，我们可以定义自己的格式化程序或使用DateTimeFormatter类提供的预定义格式化程序。

让我们看看如何使用LocalDateTime.parse()从String获取时间：

```java
String stringDate = "09:15:30 PM, Sun 10/09/2022"; 
String pattern = "hh:mm:ss a, EEE M/d/uuuu"; 
DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.US); 
LocalDateTime localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter);
```

在上面的示例中，我们使用LocalDateTime类(用于表示带有时间的日期的标准类)来解析日期String。我们也可以使用java.time.LocalDate只表示没有时间的日期。

## 3.使用即时类

java.time.Instant类是[Date-Time API](https://www.baeldung.com/java-8-date-time-intro)的主要类之一，它封装了时间轴上的一个点。此外，它类似于java.util.Date类，但提供纳秒精度。

在我们的下一个示例中，我们将使用之前的LocalDateTime来获取具有分配的ZoneId的瞬间：

```java
String stringDate = "09:15:30 PM, Sun 10/09/2022"; 
String pattern = "hh:mm:ss a, EEE M/d/uuuu"; 
DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.US); 
LocalDateTime localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter); 
ZoneId zoneId = ZoneId.of("America/Chicago"); 
ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId); 
Instant instant = zonedDateTime.toInstant();
```

在上面的示例中，我们首先创建一个ZoneId对象，用于标识时区，然后我们提供LocalDateTime和Instant之间的转换规则。

接下来，我们使用ZonedDateTime，它封装了带有时区的日期和时间及其相应的偏移量。ZonedDateTime类是 Date-Time API中最接近java.util.GregorianCalendar类的类。最后，我们使用ZonedDateTime.toInstant()方法获得Instant，该方法将时区的时刻调整为 UTC。

## 4。总结

在本快速教程中，我们解释了如何使用java.time包中的类使用 Java将String转换为Instant。