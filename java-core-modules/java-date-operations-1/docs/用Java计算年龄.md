## 1. 概述

在本快速教程中，我们将了解如何使用Java8、Java 7 和 Joda-Time 库计算年龄。

在所有情况下，我们都会将出生日期和当前日期作为输入并返回计算出的年龄(以年为单位)。

## 2. 使用Java8

Java 8 引入[了一个新的日期时间 API](https://www.baeldung.com/migrating-to-java-8-date-time-api)，用于处理日期和时间，主要基于 Joda-Time 库。

在Java8 中，我们可以使用java.time.LocalDate作为我们的出生日期和当前日期，然后使用 Period来计算它们的年差：

```java
public int calculateAge(
  LocalDate birthDate,
  LocalDate currentDate) {
    // validate inputs ...
    return Period.between(birthDate, currentDate).getYears();
}
```

[LocalDate](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/LocalDate.html) 在这里很有用，因为它只代表一个日期，而Java的Date类则同时代表日期和时间。 LocalDate.now()可以为我们提供当前日期。

 当我们需要考虑以年、月和日为单位的时间段时，Period 会很有帮助 [。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Period.html)

如果我们想获得更准确的年龄，比如以秒为单位，那么我们会想分别查看 LocalDateTime和 Duration(也许返回一个 long 值)。

## 3. 使用 Joda-Time

如果Java8 不是一个选项，我们仍然可以从 Joda-Time 获得相同类型的结果，[Joda-Time](http://www.joda.org/joda-time/)是Java8 之前世界中日期时间操作的实际标准。

我们需要将[Joda-Time 依赖](https://search.maven.org/classic/#artifactdetails|joda-time|joda-time|2.10|jar)项添加到我们的 pom：

```xml
<dependency>
  <groupId>joda-time</groupId>
  <artifactId>joda-time</artifactId>
  <version>2.10</version>
</dependency>
```

然后我们可以编写一个类似的方法来计算年龄，这次使用来自[Joda-Time 的](https://www.baeldung.com/joda-time)[LocalDate](http://www.joda.org/joda-time/apidocs/index.html)和 [Years](http://joda-time.sourceforge.net/apidocs/org/joda/time/Years.html)：

```java
public int calculateAgeWithJodaTime(
  org.joda.time.LocalDate birthDate,
  org.joda.time.LocalDate currentDate) {
    // validate inputs ...
    Years age = Years.yearsBetween(birthDate, currentDate);
    return age.getYears();   
}
```

## 4. 使用Java7

在Java7 中没有专用的 API，我们只能自己动手，所以有很多方法。

作为一个例子，我们可以使用 [java.util.Date](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Date.html)：

```java
public int calculateAgeWithJava7(
  Date birthDate, 
  Date currentDate) {            
    // validate inputs ...                                                                               
    DateFormat formatter = new SimpleDateFormat("yyyyMMdd");                           
    int d1 = Integer.parseInt(formatter.format(birthDate));                            
    int d2 = Integer.parseInt(formatter.format(currentDate));                          
    int age = (d2 - d1) / 10000;                                                       
    return age;                                                                        
}
```

在这里，我们将给定的birthDate和currentDate对象转换为整数并找出它们之间的差异，只要我们不是在 8000 年后仍然使用Java7，这种方法到那时应该有效。

## 5.总结

在本文中，我们展示了如何使用Java8、Java 7 和 Joda-Time 库轻松计算年龄。

要了解有关Java8 的数据时间支持的更多信息，请查看[我们的Java8 日期时间介绍](https://www.baeldung.com/java-8-date-time-intro)。