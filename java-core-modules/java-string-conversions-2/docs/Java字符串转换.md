## 1. 概述

在这篇快速文章中，我们将探索String对象到Java支持的不同数据类型的一些简单转换。

## 2. String转int或Integer

如果我们需要将String转换为原始int或Integer包装类型，我们可以使用parseInt()或valueOf() API 来获取相应的int或Integer返回值：

```java
@Test
public void whenConvertedToInt_thenCorrect() {
    String beforeConvStr = "1";
    int afterConvInt = 1;

    assertEquals(Integer.parseInt(beforeConvStr), afterConvInt);
}

@Test
public void whenConvertedToInteger_thenCorrect() {
    String beforeConvStr = "12";
    Integer afterConvInteger = 12;

    assertEquals(Integer.valueOf(beforeConvStr).equals(afterConvInteger), true);
}
```

## 3. String转long或Long

如果我们需要将String转换为原始long或Long包装类型，我们可以分别使用parseLong()或valueOf()：

```java
@Test
public void whenConvertedTolong_thenCorrect() {
    String beforeConvStr = "12345";
    long afterConvLongPrimitive = 12345;

    assertEquals(Long.parseLong(beforeConvStr), afterConvLongPrimitive);
}

@Test
public void whenConvertedToLong_thenCorrect() {
    String beforeConvStr = "14567";
    Long afterConvLong = 14567l;

    assertEquals(Long.valueOf(beforeConvStr).equals(afterConvLong), true);
}
```

## 4. 将String转换为double或Double

如果我们需要将String转换为原始double或Double包装类型，我们可以分别使用parseDouble()或valueOf()：

```java
@Test
public void whenConvertedTodouble_thenCorrect() {
    String beforeConvStr = "1.4";
    double afterConvDoublePrimitive = 1.4;

    assertEquals(Double.parseDouble(beforeConvStr), afterConvDoublePrimitive, 0.0);
}

@Test
public void whenConvertedToDouble_thenCorrect() {
    String beforeConvStr = "145.67";
    double afterConvDouble = 145.67d;

    assertEquals(Double.valueOf(beforeConvStr).equals(afterConvDouble), true);
}
```

## 5. 将字符串转换为字节数组

为了将字符串转换为字节数组，getBytes()使用平台的默认字符集将字符串编码为字节序列，并将结果存储到新的字节数组中。

当无法使用默认字符集对传递的字符串进行编码时，未指定getBytes()的行为。根据 java[文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)，当需要对编码过程进行更多控制时，应使用[java.nio.charset.CharsetEncoder类：](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/CharsetEncoder.html)

```java
@Test
public void whenConvertedToByteArr_thenCorrect() {
    String beforeConvStr = "abc";
    byte[] afterConvByteArr = new byte[] { 'a', 'b', 'c' };

    assertEquals(Arrays.equals(beforeConvStr.getBytes(), afterConvByteArr), true);
}
```

## 6. 将字符串转换为CharArray

为了将String转换为CharArray实例，我们可以简单地使用toCharArray()：

```java
@Test
public void whenConvertedToCharArr_thenCorrect() {
    String beforeConvStr = "hello";
    char[] afterConvCharArr = { 'h', 'e', 'l', 'l', 'o' };

    assertEquals(Arrays.equals(beforeConvStr.toCharArray(), afterConvCharArr), true);
}
```

## 7. 将字符串转换为布尔值或布尔值

要将String实例转换为原始boolean或Boolean包装类型，我们可以分别使用parseBoolean()或valueOf() API：

```java
@Test
public void whenConvertedToboolean_thenCorrect() {
    String beforeConvStr = "true";
    boolean afterConvBooleanPrimitive = true;

    assertEquals(Boolean.parseBoolean(beforeConvStr), afterConvBooleanPrimitive);
}

@Test
public void whenConvertedToBoolean_thenCorrect() {
    String beforeConvStr = "true";
    Boolean afterConvBoolean = true;

    assertEquals(Boolean.valueOf(beforeConvStr), afterConvBoolean);
}
```

## 8. 将字符串转换为日期或LocalDateTime

Java 6 提供了java.util.Date数据类型来表示日期。Java 8 为日期和时间引入了新的 API，以解决旧的java.util.Date和java.util.Calendar的缺点。

你可以阅读[本文](https://www.baeldung.com/java-8-date-time-intro)了解更多详情。

### 8.1. 将字符串转换为java.util.Date

为了将String对象转换为Date对象，我们需要首先通过传递描述日期和时间格式的模式来构造一个SimpleDateFormat对象。

例如，模式的可能值可以是“MM-dd-yyyy”或“yyyy-MM-dd”。接下来，我们需要调用传递String的parse方法。

作为参数传递的String应采用与模式相同的格式。否则，将在运行时抛出ParseException ：

```java
@Test
public void whenConvertedToDate_thenCorrect() throws ParseException {
    String beforeConvStr = "15/10/2013";
    int afterConvCalendarDay = 15;
    int afterConvCalendarMonth = 9;
    int afterConvCalendarYear = 2013;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy");
    Date afterConvDate = formatter.parse(beforeConvStr);
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(afterConvDate);

    assertEquals(calendar.get(Calendar.DAY_OF_MONTH), afterConvCalendarDay);
    assertEquals(calendar.get(Calendar.MONTH), afterConvCalendarMonth);
    assertEquals(calendar.get(Calendar.YEAR), afterConvCalendarYear);
}
```

### 8.2. 将字符串转换为java.time.LocalDateTime

LocalDateTime是一个不可变的日期时间对象，代表一个时间，通常被视为年-月-日-时-分-秒。

为了将 String 对象转换为LocalDateTime对象，我们可以简单地使用解析API：

```java
@Test
public void whenConvertedToLocalDateTime_thenCorrect() {
    String str = "2007-12-03T10:15:30";
    int afterConvCalendarDay = 03;
    Month afterConvCalendarMonth = Month.DECEMBER;
    int afterConvCalendarYear = 2007;
    LocalDateTime afterConvDate 
      = new UseLocalDateTime().getLocalDateTimeUsingParseMethod(str);

    assertEquals(afterConvDate.getDayOfMonth(), afterConvCalendarDay);
    assertEquals(afterConvDate.getMonth(), afterConvCalendarMonth);
    assertEquals(afterConvDate.getYear(), afterConvCalendarYear);
}
```

String必须根据[java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE_TIME)表示有效时间。否则，将在运行时抛出ParseException 。

例如，' 2011-12-03 ' 代表一个有效的字符串格式，其中 4 位数字表示年份，2 位数字表示一年中的月份，2 位数字表示月份中的第几天。

## 9.总结

在本快速教程中，我们介绍了将 S tring对象转换为 java 支持的不同数据类型的不同实用方法。