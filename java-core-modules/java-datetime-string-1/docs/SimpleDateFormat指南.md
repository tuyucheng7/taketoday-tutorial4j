## 1. 概述

在本教程中，我们将深入了解 SimpleDateFormat 类。

我们将了解简单的实例化 和格式化样式，以及该类公开的用于处理语言环境和时区的有用方法。

## 2.简单实例化

首先，让我们看看如何实例化一个新的 SimpleDateFormat 对象。

有[4 个可能的构造函数](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/SimpleDateFormat.html#constructor.summary)——但为了与名称保持一致，让我们保持简单。我们需要开始的只是 我们想要的[日期模式的](https://www.baeldung.com/java-simple-date-format#date_time_patterns)字符串 表示。

让我们从破折号分隔的日期模式开始，如下所示：

```java
"dd-MM-yyyy"
```

这将正确地格式化日期，从当月的当天开始，到当年的当月，最后是当年。我们可以用一个简单的单元测试来测试我们的新格式化程序。我们将实例化一个新的 SimpleDateFormat 对象，并传入一个已知日期：

```java
SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
assertEquals("24-05-1977", formatter.format(new Date(233345223232L)));

```

在上面的代码中，格式化程序将毫秒 长转换为人类可读的日期——1977 年5 月 24 日。

### 2.1. 工厂方法

虽然SimpleDateFormat是一个方便的类，可以快速构建日期格式化程序，但我们鼓励使用DateFormat类上的工厂方法 getDateFormat()、getDateTimeFormat()、getTimeFormat()。

使用这些工厂方法时，上面的示例看起来有点不同：

```java
DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
assertEquals("5/24/77", formatter.format(new Date(233345223232L)));
```

从上面我们可以看出，格式化选项的数量是由[DateFormat ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/DateFormat.html#field.detail)[类](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/DateFormat.html#field.detail)[上的字段](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/DateFormat.html#field.detail)预先确定的。这在很大程度上 限制了我们可用的格式化选项，这就是我们 在本文中坚持使用SimpleDateFormat 的原因。

### 2.2. 线程安全

SimpleDateFormat 的[JavaDoc](https://github.com/openjdk/jdk/blob/76507eef639c41bffe9a4bb2b8a5083291f41383/src/java.base/share/classes/java/text/SimpleDateFormat.java#L427)明确指出： 

>   日期格式不同步。建议为每个线程创建单独的格式实例。如果多个线程同时访问一个格式，则必须在外部进行同步。

所以 SimpleDateFormat 实例不是线程安全的，我们应该在并发环境中谨慎使用它们。

解决此问题的最佳方法 是将它们与[ThreadLocal](https://www.baeldung.com/java-threadlocal)结合使用。这样，每个线程都以自己的 SimpleDateFormat 实例结束，并且共享的缺乏使程序线程安全： 

```java
private final ThreadLocal<SimpleDateFormat> formatter = ThreadLocal
  .withInitial(() -> new SimpleDateFormat("dd-MM-yyyy"));
```

[withInitial](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ThreadLocal.html#withInitial(java.util.function.Supplier)) 方法 的参数 是SimpleDateFormat 实例的供应商。每次 ThreadLocal 需要创建一个实例时，它都会使用这个供应商。

然后我们可以通过ThreadLocal实例使用格式化程序：

```java
formatter.get().format(date)
```

ThreadLocal.get() 方法首先 为当前线程初始化 SimpleDateFormat ，然后重用该实例。

我们将这种技术称为线程限制，因为我们将每个实例的使用限制在一个特定的线程中。

还有两种其他方法可以解决同一问题：

-   使用 同步 块 或ReentrantLock
-   按需创建丢弃的 SimpleDateFormat 实例

这两种方式都不推荐：前者在竞争激烈的情况下对性能有很大影响，后者会创建大量对象，对垃圾回收造成压力。

值得一提的是，从Java8 开始，引入了一个新的[DateTimeFormatter](https://www.baeldung.com/java-datetimeformatter)类。新的DateTimeFormatter类是不可变的并且是线程安全的。如果我们使用Java8 或更高版本，建议使用新的DateTimeFormatter类。

## 3.解析日期

SimpleDateFormat 和 DateFormat 不仅允许我们格式化日期——我们还可以反向操作。使用 parse 方法，我们可以输入日期的 String 表示形式并返回等效的 Date 对象：

```java
SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
Date myDate = new Date(233276400000L);
Date parsedDate = formatter.parse("24-05-1977");
assertEquals(myDate.getTime(), parsedDate.getTime());
```

这里需要注意的是，构造函数中提供的模式应该与使用 parse 方法解析的日期格式相同。

## 4.日期时间模式

SimpleDateFormat 在格式化日期时提供了大量不同的选项。[虽然JavaDocs](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/SimpleDateFormat.html)中提供了完整列表，但让我们探索一些更常用的选项：

| 信件 | 日期组件 | 例子       |
| ---- | -------- | ---------- |
| 米   | 月       | 12; 十二月 |
| 是   | 年       | 94         |
| d    | 日       | 23; 我的   |
| H    | 小时     | 03         |
| 米   | 分钟     | 57         |

日期组件返回的输出在很大程度上也取决于String中使用的 字符数。例如，让我们以六月为例。如果我们将日期字符串定义为：

```java
"MM"
```

然后我们的结果将显示为数字代码 - 06。但是，如果我们在日期字符串中添加另一个 M：

```java
"MMM"
```

然后我们得到的格式化日期显示为Jun一词。

## 5. 应用语言环境

SimpleDateFormat 类还支持在调用构造函数时设置的 多种语言环境。

让我们通过格式化法语日期来付诸实践。我们将实例化一个 SimpleDateFormat 对象，同时向构造函数提供 Locale.FRANCE 。

```java
SimpleDateFormat franceDateFormatter = new SimpleDateFormat("EEEEE dd-MMMMMMM-yyyy", Locale.FRANCE);
Date myFriday = new Date(1539341312904L);
assertTrue(franceDateFormatter.format(myFriday).startsWith("vendredi"));
```

通过提供一个给定的日期，一个星期三的下午，我们可以断言我们的 franceDateFormatter 已经正确地格式化了日期。新日期正确地以 Vendredi 开始——周五是法语！

值得注意的[是构造函数的 Locale 版本中的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/SimpleDateFormat.html#(java.lang.String,java.util.Locale))一个小陷阱 ——虽然支持许多语言环境，但不能保证完全覆盖。Oracle 建议在 DateFormat 类上使用工厂方法以确保语言环境覆盖。

## 6. 更改时区

由于 SimpleDateFormat 扩展了 DateFormat 类，我们还可以使用setTimeZone 方法来操作时区 。让我们看一下实际效果：

```java
Date now = new Date();

SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MMM-yy HH:mm:ssZ");

simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
logger.info(simpleDateFormat.format(now));

simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
logger.info(simpleDateFormat.format(now));
```

在上面的例子中，我们在同 一个SimpleDateFormat对象上为两个不同的时区提供了相同的日期 。我们还在模式字符串的末尾添加了“Z”字符以 指示时区差异。然后为用户记录格式 方法的输出 。

点击运行，我们可以看到相对于两个时区的当前时间：

```java
INFO: Friday 12-Oct-18 12:46:14+0100
INFO: Friday 12-Oct-18 07:46:14-0400
```

## 七、总结

在本教程中，我们深入探讨了 SimpleDateFormat的复杂性。

我们已经研究了如何实例化 SimpleDateFormat 以及模式 String 如何影响日期的格式化方式。

在最终尝试使用 time zones之前，我们尝试更改输出 String 的语言环境。