## 一、简介 

在本快速教程中，我们将学习如何在 Kotlin 中处理日期。

我们将研究 与日期相关的操作，例如创建、格式化和操作日期。

## 2. 创建日期

创建Date对象的最快方法是使用LocalDate的parse()方法：

```java
var date = LocalDate.parse("2018-12-12")
```

parse() 方法默认使用标准[日期格式](https://en.wikipedia.org/wiki/ISO_8601) yyyy-MM-dd。

我们也可以通过我们自己的格式来解析一个日期字符串：

```java
var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
var date = LocalDate.parse("31-12-2018", formatter)
```

而且，如果我们需要更多控制，我们可以 使用LocalDate的 of()方法明确指定 年、日和月：

```java
var date = LocalDate.of(2018, 12, 31)
```

## 3.格式化日期

接下来，让我们看看如何将日期对象格式化回字符串。

在Kotlin 中使用默认格式格式化 Date 的默认方式 是调用 toString() 方法。

让我们创建一个日期

```java
var date = LocalDate.parse("2018-12-31")
```

并查看使用 toString的默认输出：

```java
assertThat(date.toString()).isEqualTo("2018-12-31")
```

这看起来可读，因为输出格式是yyyy-MM-dd，但同样，我们可能需要根据我们的用例将日期格式化为自定义格式。

要将我们的日期格式化为不同的格式，我们可以使用 LocalDate的 format() 方法并使用DateTimeFormatter为其提供我们的自定义格式：

```java
var formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
var formattedDate = date.format(formatter)
```

这会输出一个格式良好的日期：

```java
assertThat(formattedDate).isEqualTo("31-December-2018")
```

## 4. 提取日期组件

LocalDate 提供了许多我们可以用来从Date 中提取特定组件的方法。

其中一些非常简单，例如从 Date中提取年、月或日：

```java
var date = LocalDate.parse("2018-12-31")
assertThat(date.year).isEqualTo(2018)
assertThat(date.month).isEqualTo(Month.DECEMBER)
assertThat(date.dayOfMonth).isEqualTo(31)
```

我们还可以提取其他信息，如 e ra、 d ayOfTheWeek或 d ayOfTheMonth：

```java
assertThat(date.era.toString()).isEqualTo("CE")        
assertThat(date.dayOfWeek).isEqualTo(DayOfWeek.MONDAY)
assertThat(date.dayOfYear).isEqualTo(365)
```

## 5. 处理经期

最后，让我们研究一下在Kotlin中使用句点。

周期代表时间轴上的距离。我们可以使用Period的类工厂方法创建Period ：

```java
var period = Period.of(1, 2, 3)
```

这将创建一个1 年 2 个月零 3 天的Period 。

要将此Period添加到现有日期，我们使用 LocalDate的 plus() 方法：

```java
var date = LocalDate.of(2018, 6, 25)
var modifiedDate = date.plus(period)
```

这会将给定日期增加 1 年、2 个月和 3 天，并生成修改后的日期：

```java
assertThat(modifiedDate).isEqualTo("2019-08-28")
```

同样，我们可以从给定日期中减去Period ：

```java
var date = LocalDate.of(2018, 6, 25)
var modifiedDate = date.minus(period)
```

正如预期的那样，修改日期将是：

```java
assertThat(modifiedDate).isEqualTo("2017-04-22")
```

此外，我们可以使用句点来表示两个日期之间的距离。

假设我们有两个日期，彼此正好相隔 6 个月：

```java
var date1 = LocalDate.parse("2018-06-25")
var date2 = LocalDate.parse("2018-12-25")
```

现在，我们可以使用 Period 的between 方法表示这两个日期之间的距离：

```java
var period = Period.between(date1, date2)
```

period变量将产生以下内容：

```java
assertThat(period.toString()).isEqualTo("P6M")
```

P代表Period，6M 代表6个月。

## 六，总结

在本文中，我们学习了如何在 Kotlin 中使用日期的基础知识。

我们研究了如何使用各种方法创建日期实例以及如何将日期对象格式化回可读文本。

此外，我们研究了从Date对象中提取组件，最后研究了如何在 Kotlin 中使用周期。