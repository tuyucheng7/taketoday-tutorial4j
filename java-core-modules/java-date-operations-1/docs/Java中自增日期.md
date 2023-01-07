## 1. 概述

在本教程中，我们将研究使用Java将日期增加一天的方法。在Java8 之前，标准的Java日期和时间库对用户来说不是很友好。因此，在Java8 之前，Joda-Time 成为Java事实上的标准日期和时间库。

还有其他类和库可用于完成任务，例如 java.util.Calendar和 Apache Commons。

Java 8 包含一个更好的日期和时间 API 来解决其旧库的缺点。

因此，我们正在研究如何使用Java8、Joda-Time API、Java 的日历类和 Apache Commons 库将日期增加一天。

## 2.Maven依赖

pom.xml文件中应包含以下依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>joda-time</groupId>
        <artifactId>joda-time</artifactId>
        <version>2.10</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
</dependencies>
```

[你可以在Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"joda-time" AND a%3A"joda-time")上找到最新版本的 Joda-Time ，以及最新版本的[Apache Commons Lang](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")。

## 3.使用java.time

java.time.LocalDate 类是不可变的日期时间表示，通常被视为年月日。 

LocalDate 有许多用于日期操作的方法，让我们看看如何使用它来完成相同的任务：

```java
public static String addOneDay(String date) {
    return LocalDate
      .parse(date)
      .plusDays(1)
      .toString();
}
```

在此示例中，我们使用 java.time.LocalDate 类及其plusDays() 方法将日期增加一天。

现在，让我们验证此方法是否按预期工作：

```java
@Test
public void givenDate_whenUsingJava8_thenAddOneDay() 
  throws Exception {
 
    String incrementedDate = addOneDay("2018-07-03");
    assertEquals("2018-07-04", incrementedDate);
}
```

## 4. 使用java.util.Calendar

另一种方法是使用 java.util.Calendar 及其add() 方法来增加日期。

我们将把它与 java.text.SimpleDateFormat 一起用于日期格式化目的：

```java
public static String addOneDayCalendar(String date) 
  throws ParseException {
 
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c = Calendar.getInstance();
    c.setTime(sdf.parse(date));
    c.add(Calendar.DATE, 1);
    return sdf.format(c.getTime());
}
```

java.text.SimpleDateFormat 用于确保使用预期的日期格式。通过add() 方法增加日期。

再一次，让我们确保这种方法按预期工作：

```java
@Test
public void givenDate_whenUsingCalendar_thenAddOneDay() 
  throws Exception {
 
    String incrementedDate = addOneDayCalendar("2018-07-03");
    assertEquals("2018-07-04", incrementedDate);
}
```

## 5. 使用 Joda-Time

org.joda.time.DateTime类有许多方法可以 帮助正确处理日期和时间。

让我们看看我们如何使用它来将日期增加一天：

```java
public static String addOneDayJodaTime(String date) {
    DateTime dateTime = new DateTime(date);
    return dateTime
      .plusDays(1)
      .toString("yyyy-MM-dd");
}
```

在这里，我们使用 org.joda.time.DateTime 类及其plusDays() 方法将日期增加一天。

我们可以验证上面的代码是否适用于以下单元测试：

```java
@Test
public void givenDate_whenUsingJodaTime_thenAddOneDay() throws Exception {
    String incrementedDate = addOneDayJodaTime("2018-07-03");
    assertEquals("2018-07-04", incrementedDate);
}
```

## 6. 使用 Apache Commons

另一个常用于日期操作(除其他外)的库是 Apache Commons。它是一套围绕使用 java.util.Calendar和java.util.Date对象的实用程序。

对于我们的任务，我们可以使用org.apache.commons.lang3.time.DateUtils 类及其 addDays() 方法(注意SimpleDateFormat再次用于日期格式化)：

```java
public static String addOneDayApacheCommons(String date) 
  throws ParseException {
 
    SimpleDateFormat sdf
      = new SimpleDateFormat("yyyy-MM-dd");
    Date incrementedDate = DateUtils
      .addDays(sdf.parse(date), 1);
    return sdf.format(incrementedDate);
}
```

像往常一样，我们将通过单元测试来验证结果：

```java
@Test
public void givenDate_whenUsingApacheCommons_thenAddOneDay()
  throws Exception {
 
    String incrementedDate = addOneDayApacheCommons(
      "2018-07-03");
    assertEquals("2018-07-04", incrementedDate);
}
```

## 七、总结

在这篇简短的文章中，我们研究了处理将日期递增一天这一简单任务的各种方法。我们已经展示了如何使用Java的核心 API 以及一些流行的第 3 方库来完成它。