## 1. 概述

在本教程中，我们将快速浏览一下GregorianCalendar类。

## 2.公历

GregorianCalendar是抽象类java.util.Calendar的具体实现。毫不奇怪，公历 是世界上使用最广泛的民用 日历 。 

### 2.1. 获取实例

有两个选项可用于获取GregorianCalendar 的实例： Calendar.getInstance()和使用其中一个构造函数。

使用静态工厂方法 Calendar.getInstance() 不是推荐的方法，因为它会根据默认语言环境返回一个实例。

它可能会返回泰国的BuddhistCalendar或日本的 JapaneseImperialCalendar。不知道返回实例的类型可能会导致 ClassCastException ：

```java
@Test(expected = ClassCastException.class)
public void test_Class_Cast_Exception() {
    TimeZone tz = TimeZone.getTimeZone("GMT+9:00");
    Locale loc = new Locale("ja", "JP", "JP");
    Calendar calendar = Calendar.getInstance(loc);
    GregorianCalendar gc = (GregorianCalendar) calendar;
}
```

使用七个重载构造函数之一，我们可以根据操作系统的区域设置使用默认日期和时间初始化Calendar对象，或者我们可以指定日期、时间、区域设置和时区的组合。

让我们了解可以实例化GregorianCalendar对象的不同构造函数 。

默认构造函数将使用操作系统时区和区域设置中的当前日期和时间初始化日历：

```java
new GregorianCalendar();
```

我们可以使用默认语言环境为默认时区指定年、月、月日、日小时、分钟和秒：

```java
new GregorianCalendar(2018, 6, 27, 16, 16, 47);
```

请注意，我们不必指定hourOfDay、minute和second，因为还有其他构造函数没有这些参数。

我们可以将时区作为参数传递，以使用默认语言环境在该时区创建日历：

```java
new GregorianCalendar(TimeZone.getTimeZone("GMT+5:30"));
```

我们可以将语言环境作为参数传递，以在该语言环境中使用默认时区创建日历：

```java
new GregorianCalendar(new Locale("en", "IN"));
```

最后，我们可以将时区和语言环境作为参数传递：

```java
new GregorianCalendar(TimeZone.getTimeZone("GMT+5:30"), new Locale("en", "IN"));
```

### 2.2.Java8 的新方法

在Java8 中， GregorianCalendar引入了新方法。

from() 方法从 ZonedDateTime 对象获取具有默认语言环境的GregorianCalendar实例。

使用getCalendarType() 我们可以获得日历实例的类型。可用的日历类型是“gregory”、“buddhist”和“japanese”。

例如，我们可以使用它来确保在继续我们的应用程序逻辑之前我们有一个特定类型的日历：

```java
@Test
public void test_Calendar_Return_Type_Valid() {
    Calendar calendar = Calendar.getInstance();
    assert ("gregory".equals(calendar.getCalendarType()));
}
```

调用 toZonedDateTime()我们可以将日历对象转换为ZonedDateTime 对象，该对象表示时间轴上与此GregorianCalendar 相同的点。

### 2.3. 修改日期

可以使用方法add()、roll()和set()修改日历字段。

add()方法允许我们根据 日历的内部规则集以指定的单位向日历添加时间：

```java
@Test
public void test_whenAddOneDay_thenMonthIsChanged() {
    int finalDay1 = 1;
    int finalMonthJul = 6; 
    GregorianCalendar calendarExpected = new GregorianCalendar(2018, 5, 30);
    calendarExpected.add(Calendar.DATE, 1);
    System.out.println(calendarExpected.getTime());
 
    assertEquals(calendarExpected.get(Calendar.DATE), finalDay1);
    assertEquals(calendarExpected.get(Calendar.MONTH), finalMonthJul);
}
```

我们还可以使用add()方法从日历对象中减去时间：

```java
@Test
public void test_whenSubtractOneDay_thenMonthIsChanged() {
    int finalDay31 = 31;
    int finalMonthMay = 4; 
    GregorianCalendar calendarExpected = new GregorianCalendar(2018, 5, 1);
    calendarExpected.add(Calendar.DATE, -1);

    assertEquals(calendarExpected.get(Calendar.DATE), finalDay31);
    assertEquals(calendarExpected.get(Calendar.MONTH), finalMonthMay);
}
```

执行 add()方法会强制立即重新计算日历的毫秒数和所有字段。

请注意，使用add()也可能更改更高的日历字段(在本例中为 MONTH)。

roll()方法在 不更改较大字段的情况下将带符号的金额添加到指定的日历字段。更大的字段代表更大的时间单位。例如，DAY_OF_MONTH大于HOUR。

让我们看一个如何汇总月份的示例。

在这种情况下，YEAR作为一个较大的字段将不会增加：

```java
@Test
public void test_whenRollUpOneMonth_thenYearIsUnchanged() {
    int rolledUpMonthJuly = 7, orginalYear2018 = 2018;
    GregorianCalendar calendarExpected = new GregorianCalendar(2018, 6, 28);
    calendarExpected.roll(Calendar.MONTH, 1);
 
    assertEquals(calendarExpected.get(Calendar.MONTH), rolledUpMonthJuly);
    assertEquals(calendarExpected.get(Calendar.YEAR), orginalYear2018);
}
```

同样，我们可以向下滚动月份：

```java
@Test
public void test_whenRollDownOneMonth_thenYearIsUnchanged() {
    int rolledDownMonthJune = 5, orginalYear2018 = 2018;
    GregorianCalendar calendarExpected = new GregorianCalendar(2018, 6, 28);
    calendarExpected.roll(Calendar.MONTH, -1);
 
    assertEquals(calendarExpected.get(Calendar.MONTH), rolledDownMonthJune);
    assertEquals(calendarExpected.get(Calendar.YEAR), orginalYear2018);
}
```

我们可以使用set()方法直接将日历字段设置为指定值。在下一次调用get()、getTime()、add()或roll() 之前，不会重新计算日历的时间值(以毫秒为单位) 。

因此，多次调用set()不会触发不必要的计算。

让我们看一个将月份字段设置为 3(即四月)的示例：

```java
@Test
public void test_setMonth() {
    GregorianCalendarExample calendarDemo = new GregorianCalendarExample();
    GregorianCalendar calendarActual = new GregorianCalendar(2018, 6, 28);
    GregorianCalendar calendarExpected = new GregorianCalendar(2018, 6, 28);
    calendarExpected.set(Calendar.MONTH, 3);
    Date expectedDate = calendarExpected.getTime();

    assertEquals(expectedDate, calendarDemo.setMonth(calendarActual, 3));
}
```

### 2.4. 使用XMLGregorianCalendar

JAXB 允许将Java类映射到 XML 表示。javax.xml.datatype.XMLGregorianCalendar类型可以帮助映射基本的  XSD 架构类型，例如 xsd:date、xsd:time和xsd:dateTime。

让我们看一个从GregorianCalendar类型转换为 XMLGregorianCalendar类型的例子：

```java
@Test
public void test_toXMLGregorianCalendar() throws Exception {
    GregorianCalendarExample calendarDemo = new GregorianCalendarExample();
    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    GregorianCalendar calendarActual = new GregorianCalendar(2018, 6, 28);
    GregorianCalendar calendarExpected = new GregorianCalendar(2018, 6, 28);
    XMLGregorianCalendar expectedXMLGregorianCalendar = datatypeFactory
      .newXMLGregorianCalendar(calendarExpected);
 
    assertEquals(
      expectedXMLGregorianCalendar, 
      alendarDemo.toXMLGregorianCalendar(calendarActual));
}
```

一旦日历对象被转换为 XML 格式，它就可以用于任何需要序列化日期的用例，例如消息传递或 Web 服务调用。

让我们看一个关于如何将XMLGregorianCalendar类型转换回GregorianCalendar的示例：

```java
@Test
public void test_toDate() throws DatatypeConfigurationException {
    GregorianCalendar calendarActual = new GregorianCalendar(2018, 6, 28);
    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    XMLGregorianCalendar expectedXMLGregorianCalendar = datatypeFactory
      .newXMLGregorianCalendar(calendarActual);
    expectedXMLGregorianCalendar.toGregorianCalendar().getTime();
    assertEquals(
      calendarActual.getTime(), 
      expectedXMLGregorianCalendar.toGregorianCalendar().getTime() );
}
```

### 2.5. 比较日期

我们可以使用Calendar类的 compareTo()方法来比较日期。如果基准日期在未来，结果将为正；如果基准数据在我们比较的日期的过去，则结果将为负：

```java
@Test
public void test_Compare_Date_FirstDate_Greater_SecondDate() {
    GregorianCalendar firstDate = new GregorianCalendar(2018, 6, 28);
    GregorianCalendar secondDate = new GregorianCalendar(2018, 5, 28);
    assertTrue(1 == firstDate.compareTo(secondDate));
}

@Test
public void test_Compare_Date_FirstDate_Smaller_SecondDate() {
    GregorianCalendar firstDate = new GregorianCalendar(2018, 5, 28);
    GregorianCalendar secondDate = new GregorianCalendar(2018, 6, 28);
    assertTrue(-1 == firstDate.compareTo(secondDate));
}

@Test
public void test_Compare_Date_Both_Dates_Equal() {
    GregorianCalendar firstDate = new GregorianCalendar(2018, 6, 28);
    GregorianCalendar secondDate = new GregorianCalendar(2018, 6, 28);
    assertTrue(0 == firstDate.compareTo(secondDate));
}
```

### 2.6. 格式化日期

我们可以 使用ZonedDateTime和DateTimeFormatter的组合将GregorianCalendar转换为特定格式以获得所需的输出：

```java
@Test
public void test_dateFormatdMMMuuuu() {
    String expectedDate = new GregorianCalendar(2018, 6, 28).toZonedDateTime()
      .format(DateTimeFormatter.ofPattern("d MMM uuuu"));
    assertEquals("28 Jul 2018", expectedDate);
}
```

### 2.7. 获取有关日历的信息

GregorianCalendar提供了几种获取方法，可用于获取不同的日历属性。让我们看看我们有不同的选择：

-   getActualMaximum(int field) –

    返回指定日历字段的最大值，同时考虑当前时间值。以下示例将为

    DAY_OF_MONTH

     字段返回值 30，因为 6 月有 30 天：

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(30 == calendar.getActualMaximum(calendar.DAY_OF_MONTH));
    ```

-   getActualMinimum(int field) –

    返回指定日历字段的最小值，同时考虑当前时间值：

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(1 == calendar.getActualMinimum(calendar.DAY_OF_MONTH));
    ```

-   getGreatestMinimum(int field) –

    返回给定日历字段的最高最小值：

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(1 == calendar.getGreatestMinimum(calendar.DAY_OF_MONTH));
    ```

-   getLeastMaximum(int field) –

    返回给定日历字段的最小最大值。对于

    DAY_OF_MONTH

    字段，这是 28，因为二月可能只有 28 天：

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(28 == calendar.getLeastMaximum(calendar.DAY_OF_MONTH));
    ```

-   getMaximum(int field) –

    返回给定日历字段的最大值：

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(31 == calendar.getMaximum(calendar.DAY_OF_MONTH));
    ```

-   getMinimum(int field) –

    返回给定日历字段的最小值：

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(1 == calendar.getMinimum(calendar.DAY_OF_MONTH));
    ```

-   getWeekYear() –

     返回此GregorianCalendar表示

    的星期几 

    ：

    

    

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(2018 == calendar.getWeekYear());
    ```

-   getWeeksInWeekYear() –

     返回日历年的周数：

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(52 == calendar.getWeeksInWeekYear());
    ```

-   isLeapYear() –

     如果年份是闰年，则返回 true：

    ```java
    GregorianCalendar calendar = new GregorianCalendar(2018 , 5, 28);
    assertTrue(false == calendar.isLeapYear(calendar.YEAR));
    ```

## 3.总结

在本文中，我们探讨了GregorianCalendar的某些方面。