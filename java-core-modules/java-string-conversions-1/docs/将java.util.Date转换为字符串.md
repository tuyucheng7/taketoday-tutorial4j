## 1. 概述

在本教程中，我们将展示如何在Java中将Date对象转换为String对象。为此，我们将使用旧的 java.util.Date类型以及Java 8 中引入的新日期/时间API。

如果你想学习如何进行相反的转换，即从String到Date类型，你可以在此处查看[本教程](https://www.baeldung.com/java-string-to-date)。

有关新日期/时间API 的更多详细信息，请参阅[此相关教程](https://www.baeldung.com/java-8-date-time-intro)。

## 2. 将java.util.Date转换为字符串

尽管在使用Java8 时我们不应该使用java.util.Date，但有时我们别无选择(例如，我们从不受我们控制的库中接收Date对象)。

在这种情况下，我们可以使用多种方法将 java.util.Date转换为String 。

### 2.1. 准备 日期对象

让我们首先声明我们日期的预期字符串表示，并定义所需日期格式的模式：

```java
private static final String EXPECTED_STRING_DATE = "Aug 1, 2018 12:00 PM";
private static final String DATE_FORMAT = "MMM d, yyyy HH:mm a";
```

现在我们需要我们想要转换的实际 Date对象。我们将使用 Calendar实例来创建它：

```java
TimeZone.setDefault(TimeZone.getTimeZone("CET"));
Calendar calendar = Calendar.getInstance();
calendar.set(2018, Calendar.AUGUST, 1, 12, 0);
Date date = calendar.getTime();
```

我们已将默认时区设置为CET ，以防止以后使用新 API 时出现问题。我们应该注意到Date本身没有任何时区，但它的toString ()使用当前默认时区。

我们将在下面的所有示例中使用此Date实例。

### 2.2. 使用 SimpleDateFormat类

在此示例中，我们将使用 [SimpleDateFormat](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/SimpleDateFormat.html)类的format()方法。让我们使用我们的日期格式创建它的一个实例：

```java
DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
```

在此之后，我们可以格式化我们的日期并将其与预期输出进行比较：

```java
String formattedDate = formatter.format(date);

assertEquals(EXPECTED_STRING_DATE, formattedDate);
```

### 2.3. 使用抽象DateFormat类

正如我们所见， SimpleDateFormat是抽象DateFormat类的子类。此类提供各种日期和时间格式化方法。

我们将使用它来实现与上面相同的输出：

```java
String formattedDate = DateFormat
  .getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
  .format(date);
```

通过这种方法，我们传递了样式模式—— 在我们的例子中，日期为MEDIUM ，时间为SHORT。

## 3.使用 格式化程序类

获得与前面示例中 相同的字符串的另一种简单方法是使用Formatter类。

虽然这可能不是最易读的解决方案，但它是一种线程安全的单行代码，可能很有用，尤其是在多线程环境中(我们应该记住SimpleDateFormat不是线程安全的)：

```java
String formattedDate = String.format("%1$tb %1$te, %1$tY %1$tI:%1$tM %1$Tp", date);
```

我们使用1$表示我们将只传递一个参数以用于每个标志。可以在Formatter类的[日期/时间转换部分](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Formatter.html#dt)找到对标志的详细说明 。

## 4. 使用Java8日期/时间 API进行转换

Java 8 中的日期/时间API 比java.util.Date 和 java.util.Calendar 类强大得多 ，我们应该尽可能使用它。让我们看看如何将它用于将我们现有的Date对象转换为String。

这一次，我们将使用 [DateTimeFormatter](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html) 类及其 format() 方法，以及在 2.1 节中声明的相同日期模式：

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
```

要使用新的 API，我们需要将Date对象转换为 Instant对象：

```java
Instant instant = date.toInstant();
```

由于我们预期的String具有日期和时间部分，因此我们还需要将Instant对象转换为 LocalDateTime：

```java
LocalDateTime ldt = instant
  .atZone(ZoneId.of("CET"))
  .toLocalDateTime();
```

最后，我们可以轻松获得格式化的String：

```java
String formattedDate = ldt.format(formatter);
```

## 5.总结

在本文中，我们说明了将java.util.Date对象转换为String的几种方法。我们首先展示了如何使用旧的 java.util.Date和 java.util.Calendar 类以及相应的日期格式化类来实现这一点。

然后我们使用Formatter类，最后使用Java8 Date/Time API。