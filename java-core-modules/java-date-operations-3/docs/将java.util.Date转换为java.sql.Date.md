## 1. 概述

在这个简短的教程中，我们将探讨将java.util.Date转换 为 java.sql.Date的几种策略。

首先，我们将了解标准转换，然后，我们将检查一些被认为是最佳实践的备选方案。

## 2. java.util.Date与java.sql.Date

这两个日期类都用于特定场景，并且是不同Java标准包的一部分：

-   [java.util](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/package-summary.html) 包是 JDK 的一部分，包含各种实用程序类以及日期和时间工具。
-   [java.sql](https://docs.oracle.com/en/java/javase/12/docs/api/java.sql/java/sql/package-summary.html) 包是 JDBC API 的一部分，从Java7 开始默认包含在 JDK 中。

[java.util.Date](https://docs.oracle.com/en/java/javase/12/docs/api/java.sql/java/sql/Date.html) 表示一个特定的时刻，精度为毫秒：

```java
java.util.Date date = new java.util.Date(); 
System.out.println(date);
// Wed Mar 27 08:22:02 IST 2015
```

[java.sql.Date](https://docs.oracle.com/javase/7/docs/api/java/sql/Date.html) 是一个围绕毫秒值的薄包装器，允许 JDBC 驱动程序将其识别为 SQL DATE 值。该类的值无非是从[Unix 纪元](https://www.baeldung.com/java-date-unix-timestamp)开始计算的以毫秒为单位计算的特定日期的年月日。任何比日期更细化的时间信息都将被截断：

```java
long millis=System.currentTimeMillis(); 
java.sql.Date date = new java.sql.Date(millis); 
System.out.println(date);
// 2015-03-30
```

## 3. 为什么需要转换

虽然java.util.Date的用法更为普遍，但java.sql.Date用于启用Java应用程序与数据库的通信。因此，在这些情况下需要转换为java.sql.Date 。

显式[引用转换](https://www.baeldung.com/java-type-casting)也不起作用，因为我们正在处理一个完全不同的类层次结构：没有可用的向下转换或向上转换。如果我们尝试将这些日期中的一个转换为另一个，我们将收到[ClassCastException](https://www.baeldung.com/java-classcastexception#:~:text=ClassCastException is an unchecked exception,how we can avoid them.)：

```java
java.sql.Date date = (java.sql.Date) new java.util.Date() // not allowed
```

## 4.如何转换为java.sql.Date

有几种策略可以将java.util.Date转换 为 java.sql.Date，我们将在下面探讨这些策略。

### 4.1. 标准转换

正如我们在上面看到的，java.util.Date包含时间信息而java.sql.Date不包含。因此，我们通过使用 java.sql.Date的构造方法实现了有损转换，它接受从 Unix 纪元开始以毫秒表示的输入时间：

```java
java.util.Date utilDate = new java.util.Date();
java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
```

事实上，丢失表示值的时间部分可能会由于不同的时区而导致报告不同的日期：

```java
SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
isoFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

java.util.Date date = isoFormat.parse("2010-05-23T22:01:02");
TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
java.sql.Date sqlDate = new java.sql.Date(date.getTime());
System.out.println(sqlDate);
// This will print 2010-05-23

TimeZone.setDefault(TimeZone.getTimeZone("Rome"));
sqlDate = new java.sql.Date(date.getTime());
System.out.println(sqlDate);
// This will print 2010-05-24
```

出于这个原因，我们可能要考虑我们将在下一小节中检查的转换备选方案之一。

### 4.2. 使用java.sql.Timestamp而不是java.sql.Date

要考虑的第一个替代方案是使用java.sql.Timestamp类而不是java.sql.Date。此类还包含有关时间的信息：

```java
java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
System.out.println(date); //Mon May 24 07:01:02 CEST 2010
System.out.println(timestamp); //2010-05-24 07:01:02.0
```

当然，如果我们查询一个DATE类型的数据库列，这个解决方案可能不是正确的。

### 4.3. 使用java.time包中的类

第二种也是最好的选择是将这两个类都转换为[java.time](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/time/package-summary.html)包中提供的新类。此转换的唯一先决条件是使用 JDBC 4.2(或更高版本)。JDBC 4.2 于 2014 年 3 月随[Java SE 8](https://www.baeldung.com/java-8-new-features)一起发布。

从Java8 开始，不鼓励使用Java早期版本中提供的日期时间类，而推荐使用新的java.time包中提供的日期时间类。这些增强类可以更好地满足所有日期/时间需求，包括通过[JDBC 驱动程序](https://www.baeldung.com/java-jdbc)与数据库通信。

如果我们采用这种策略，java.util.Date应该转换为java.time.Instant：

```java
Date date = new java.util.Date();
Instant instant = date.toInstant().atZone(ZoneId.of("Rome");
```

并且java.sql.Date应该转换为java.time.LocalDate：

```java
java.sql.Date sqlDate = new java.sql.Date(timeInMillis);
java.time.LocalDate localDate = sqlDate.toLocalDate();
```

java.time.Instant类可用于映射 SQL DATETIME 列，java.time.LocalDate可用于映射 SQL DATE 列。

例如，现在让我们生成一个带有时区信息的java.util.Date ：

```java
SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
isoFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
Date date = isoFormat.parse("2010-05-23T22:01:02");

```

接下来，让我们从java.util.Date生成一个LocalDate：

```java
TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
java.time.LocalDate localDate = date.toInstant().atZone(ZoneId.of("America/Los_Angeles")).toLocalDate();
Asserts.assertEqual("2010-05-23", localDate.toString());
```

如果我们随后尝试切换默认时区，LocalDate将保持相同的值：

```java
TimeZone.setDefault(TimeZone.getTimeZone("Rome"));
localDate = date.toInstant().atZone(ZoneId.of("America/Los_Angeles")).toLocalDate();
Asserts.assertEqual("2010-05-23", localDate.toString())

```

由于我们在转换期间指定时区的明确引用，这按预期工作。

## 5.总结

在本教程中。我们已经了解了如何将标准的java.util Date转换为java.sql包 中提供的日期。连同标准转换，我们研究了两种选择。第一个使用Timestamp，第二个依赖于较新的java.time类。