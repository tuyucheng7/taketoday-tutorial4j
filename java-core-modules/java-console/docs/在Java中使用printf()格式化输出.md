## 1. 概述

在本教程中，我们将演示使用 printf() 方法进行格式化的不同示例。

该方法是java.io.PrintStream类的一部分，提供类似于C 中的printf()函数的字符串格式化。

## 延伸阅读：

## [java.util.Formatter 指南](https://www.baeldung.com/java-string-formatter)

使用 java.util.Formatter 在Java中格式化字符串的介绍。

[阅读更多](https://www.baeldung.com/java-string-formatter)→

## [DateTimeFormatter 指南](https://www.baeldung.com/java-datetimeformatter)

了解如何使用Java8 DateTimeFormatter 类来格式化和解析日期和时间

[阅读更多](https://www.baeldung.com/java-datetimeformatter)→

## [在Java中用零或空格填充字符串](https://www.baeldung.com/java-pad-string)

了解如何使用特定字符填充Java中的字符串。

[阅读更多](https://www.baeldung.com/java-pad-string)→

## 2.语法

我们可以使用这些PrintStream方法之一来格式化输出：

```java
System.out.printf(format, arguments);
System.out.printf(locale, format, arguments);
```

我们使用格式参数指定格式规则。规则以%字符开头 。

在我们深入了解各种格式化规则的细节之前，让我们看一个简单的例子：

```java
System.out.printf("Hello %s!%n", "World");
```

这会产生以下输出：

```plaintext
Hello World!
```

如上所示，格式字符串包含纯文本和两个格式规则。第一条规则用于格式化字符串参数。第二条规则在字符串末尾添加一个换行符。

### 2.1. 格式规则

让我们更仔细地看一下格式字符串。它由文字和格式说明符组成。 格式说明符按以下顺序包括标志、宽度、精度和转换字符：

```plaintext
%[flags][width][.precision]conversion-character
```

括号中的说明符是可选的。

在内部，printf()使用[java.util.Formatter](https://www.baeldung.com/java-string-formatter) 类来解析格式字符串并生成输出。[可以在Formatter Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Formatter.html#syntax)中找到其他格式字符串选项。

### 2.2. 转换字符

转换字符是必需的，它决定参数的格式。

转换字符仅对某些数据类型有效。以下是一些常见的：

-   s格式化字符串。
-   d格式化十进制整数。
-   f格式化浮点数。
-   t格式化日期/时间值。

我们将在本教程的后面探索这些和其他一些。

### 2.3. 可选修饰符

[ flags] 定义了修改输出的标准方法，最常用于格式化整数和浮点数。

[width] 指定输出参数的字段宽度。它表示写入输出的最少字符数。

[.precision]指定输出浮点值时 的精度位数。此外，我们可以使用它来定义要从String中提取的子字符串的长度。

## 3. 行分隔符

为了将字符串分成单独的行，我们有一个 % n说明符：

```java
System.out.printf("baeldung%nline%nterminator");
```

上面的代码片段将产生以下输出：

```plaintext
baeldung
line
terminator
```

% n分隔符 printf() 将自动插入主机系统的本机行分隔符。

## 4.布尔格式

要格式化布尔值，我们使用%b格式。

根据[文档](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/Formatter.html#syntax)，它的工作方式如下：如果第二个参数为null，则结果为“false”。如果参数是布尔值或Boolean ，则结果是String.valueOf(arg)返回的字符串。否则，结果为“真”。

因此，如果我们执行以下操作：

```java
System.out.printf("%b%n", null);
System.out.printf("%B%n", false);
System.out.printf("%B%n", 5.3);
System.out.printf("%b%n", "random text");
```

然后我们会看到：

```plaintext
false
FALSE
TRUE
true

```

请注意，我们可以使用%B进行大写格式化。

## 5. 字符串格式化

要格式化一个简单的字符串，我们将使用%s组合。此外，我们可以将字符串设为大写：

```java
printf("'%s' %n", "baeldung");
printf("'%S' %n", "baeldung");
```

这是输出：

```plaintext
'baeldung' 
'BAELDUNG'
```

此外，要指定最小长度，我们可以指定宽度：

```java
printf("'%15s' %n", "baeldung");
```

这给了我们：

```plaintext
'       baeldung'
```

如果我们需要左对齐我们的字符串，我们可以使用– 标志：

```java
printf("'%-10s' %n", "baeldung");
```

这是输出：

```plaintext
'baeldung  '
```

更重要的是，我们可以通过指定精度来限制输出中的字符数：

```java
System.out.printf("%2.2s", "Hi there!");
```

%x.ys语法中的第一个x 数字是填充。y 是字符数。

对于我们这里的例子，输出是Hi。

## 6. 字符格式化

%c的结果是一个 Unicode 字符：

```java
System.out.printf("%c%n", 's');
System.out.printf("%C%n", 's');
```

大写字母C会将结果大写：

```plaintext
s
S
```

但是如果我们给它一个无效的参数，那么Formatter将抛出IllegalFormatConversionException。

## 7.数字格式

### 7.1. 整数格式

printf ()方法接受语言中可用的所有整数——byte 、 short 、 int 、 long和BigInteger ( 如果我们使用 %d)：

```plaintext
System.out.printf("simple integer: %d%n", 10000L);
```

在d字符的帮助下，我们将得到以下结果：

```plaintext
simple integer: 10000
```

如果我们需要用千位分隔符格式化我们的数字，我们可以使用, 标志。 我们还可以为不同的语言环境格式化我们的结果：

```java
System.out.printf(Locale.US, "%,d %n", 10000);
System.out.printf(Locale.ITALY, "%,d %n", 10000);
```

正如我们所见，美国的格式与意大利的不同：

```plaintext
10,000 
10.000
```

### 7.2. 浮动和双重格式

要格式化浮点数，我们需要f格式：

```java
System.out.printf("%f%n", 5.1473);
```

这将输出：

```plaintext
5.147300
```

当然，首先想到的是控制精度：

```java
System.out.printf("'%5.2f'%n", 5.1473);
```

这里我们定义数字的宽度为5，小数部分的长度为2 ：

```plaintext
' 5.15'
```

这里我们从数字的开头填充一个空格以支持预定义的宽度。

要以科学记数法输出，我们只需使用e 转换字符：

```java
System.out.printf("'%5.2e'%n", 5.1473);
```

这是我们的结果：

```plaintext
'5.15e+00'
```

## 8. 日期和时间格式

对于日期和时间格式，转换字符串是两个字符的序列：t或T 字符和转换后缀。

让我们通过示例探索最常见的时间和日期格式后缀字符。

当然，对于更高级的格式化，我们可以使用[DateTimeFormatter](https://www.baeldung.com/java-datetimeformatter)，它从Java8 开始可用。

### 8.1. 时间格式

首先，让我们看看一些有用的时间格式后缀字符列表：

-   H、M、S字符负责从输入Date中提取小时、分钟和秒。
-   L , N相应地表示以毫秒和纳秒为单位的时间。
-   p添加 am/pm 格式。
-   z打印出时区偏移量。

现在，假设我们要打印出Date的时间部分：

```java
Date date = new Date();
System.out.printf("%tT%n", date);
```

上面的代码与%tT 组合产生以下输出：

```plaintext
13:51:15
```

如果我们需要更详细的格式，我们可以调用不同的时间段：

```java
System.out.printf("hours %tH: minutes %tM: seconds %tS%n", date, date, date);
```

使用H、M 和S后，我们得到以下结果：

```plaintext
hours 13: minutes 51: seconds 15
```

但是，多次列出日期 是一件痛苦的事情。

或者，为了摆脱多个参数，我们可以使用输入参数的索引引用，在我们的例子中是1$：

```java
System.out.printf("%1$tH:%1$tM:%1$tS %1$tp %1$tL %1$tN %1$tz %n", date);
```

在这里，我们希望将当前时间、上午/下午、以毫秒和纳秒为单位的时间以及时区偏移量作为输出：

```plaintext
13:51:15 pm 061 061000000 +0400
```

### 8.2. 日期格式

和时间格式化一样，我们有特殊的格式化字符用于日期格式化：

-   A 打印出一周中的整天。
-   d 格式化一个月中的两位数日期。
-   B 是完整的月份名称。
-   m 格式化两位数的月份。
-   Y 以四位数输出年份。
-   y输出年份的最后两位数字。

假设我们要显示星期几，然后是月份：

```java
System.out.printf("%1$tA, %1$tB %1$tY %n", date);
```

然后，使用A、B 和Y，我们将得到以下输出：

```plaintext
Thursday, November 2018
```

为了让我们的结果全部采用数字格式，我们可以将A、B、Y字母替换为d、m、y：

```java
System.out.printf("%1$td.%1$tm.%1$ty %n", date);
```

这将导致：

```plaintext
22.11.18
```

## 9.总结

在本文中，我们讨论了如何使用PrintStream#printf 方法来格式化输出。我们研究了用于控制常见数据类型输出的不同格式模式。