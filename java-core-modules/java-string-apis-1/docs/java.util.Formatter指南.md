## 1. 概述

在本文中，我们将使用[java.util.Formatter](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Formatter.html)类讨论Java 中的字符串格式化，它为布局对齐和对齐提供支持。

## 2.格式化程序的使用方法

还记得 C 的printf 吗？在Java中格式化字符串感觉非常相似。

Formatter的format()方法通过String类的静态方法公开。此方法接受一个模板字符串和一个参数列表来填充模板：

```java
String greetings = String.format(
  "Hello Folks, welcome to %s !", 
  "Baeldung");
```

结果字符串是：

```java
"Hello Folks, welcome to Baeldung !"
```

模板是一个包含一些静态文本和一个或多个格式说明符的字符串，格式说明符指示将哪个参数放置在特定位置。

在这种情况下，只有一个格式说明符%s，它被相应的参数替换。

## 3.格式说明符

### 3.1. 一般语法

General、Character和Numeric类型的格式说明符语法为：

```java
%[argument_index$][flags][width][.precision]conversion
```

说明符argument_index、flag、width和precision是可选的。

-   argument_index部分是一个整数i – 表示此处应使用参数列表中的第 i 个参数
-   flags是一组用于修改输出格式的字符
-   width是一个正整数，表示要写入输出的最少字符数
-   precision是一个整数，通常用于限制字符数，具体行为取决于转换
-   是必填部分。它是一个字符，指示应如何格式化参数。给定参数的有效转换集取决于参数的数据类型

在我们上面的例子中，如果我们想明确指定参数的数量，我们可以使用1$和2$参数索引来编写它。

这两个分别是第一个和第二个参数：

```java
String greetings = String.format(
  "Hello %2$s, welcome to %1$s !", 
  "Baeldung", 
  "Folks");
```

### 3.2. 对于日期/时间表示

```java
%[argument_index$][flags][width]conversion
```

同样，argument_index、flags和width是可选的。

让我们举个例子来理解这一点：

```java
@Test
public void whenFormatSpecifierForCalendar_thenGotExpected() {
    Calendar c = new GregorianCalendar(2017, 11, 10);
    String s = String.format(
      "The date is: %tm %1$te,%1$tY", c);

    assertEquals("The date is: 12 10,2017", s);
}
```

在这里，对于每个格式说明符，将使用第一个参数，因此1$。在这里，如果我们跳过第二个和第三个格式说明符的argument_index，它会尝试找到 3 个参数，但我们需要对所有 3 个格式说明符使用相同的参数。

所以，如果我们不为第一个指定参数 _index就可以了，但我们需要为其他两个指定它。

这里的旗帜是由两个字符组成的。第一个字符始终是't'或'T'。第二个字符取决于要显示日历的哪一部分。

在我们的示例中，第一个格式说明符tm表示格式为两位数的月份，te表示月份中的日期，tY表示格式为四位数字的年份。

### 3.3. 不带参数的格式说明符

```java
%[flags][width]conversion
```

可选的标志和宽度与上面部分中定义的相同。

所需的转换是一个字符或字符串，指示要在输出中插入的内容。目前，只有'%'和换行符'n'可以使用这个打印：

```java
@Test
public void whenNoArguments_thenExpected() {
    String s = String.format("John scored 90%% in Fall semester");
 
    assertEquals("John scored 90% in Fall semester", s);
}

```

在format()内部，如果我们想打印'%' – 我们需要使用'%%'转义它。

## 4.转换

现在让我们深入研究格式说明符语法的每个细节，从转换开始。请注意，你可以在[Formatter javadocs](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Formatter.html)中找到所有详细信息。

正如我们在上面的例子中注意到的，转换部分在所有格式说明符中都是必需的，它可以分为几类。

让我们通过示例来了解每一个。

### 4.1. 一般的 

用于任何参数类型。一般的转换是：

1.  'b'或'B' – 用于布尔值
2.  'h'或'H' – 用于HashCode
3.  's'或'S' – 对于String，如果为null，则打印“null”，否则打印arg.toString()

我们现在将尝试使用相应的转换来显示布尔值和字符串值：

```java
@Test
public void givenString_whenGeneralConversion_thenConvertedString() {
    String s = String.format("The correct answer is %s", false);
    assertEquals("The correct answer is false", s);

    s = String.format("The correct answer is %b", null);
    assertEquals("The correct answer is false", s);

    s = String.format("The correct answer is %B", true);
    assertEquals("The correct answer is TRUE", s);
}
```

### 4.2. 特点 

用于表示 Unicode 字符的基本类型：char、Character、byte、Byte、short和Short。当Character.isValidCodePoint(int)为它们返回true时，此转换也可用于类型int和Integer。

它可以根据我们想要的大小写为“c”或“C” 。

让我们尝试打印一些字符：

```java
@Test
public void givenString_whenCharConversion_thenConvertedString() {
    String s = String.format("The correct answer is %c", 'a');
    assertEquals("The correct answer is a", s);

    s = String.format("The correct answer is %c", null);
    assertEquals("The correct answer is null", s);

    s = String.format("The correct answer is %C", 'b');
    assertEquals("The correct answer is B", s);

    s = String.format("The valid unicode character: %c", 0x0400);
    assertTrue(Character.isValidCodePoint(0x0400));
    assertEquals("The valid unicode character: Ѐ", s);
}
```

让我们再举一个无效代码点的例子：

```java
@Test(expected = IllegalFormatCodePointException.class)
public void whenIllegalCodePointForConversion_thenError() {
    String s = String.format("The valid unicode character: %c", 0x11FFFF);
 
    assertFalse(Character.isValidCodePoint(0x11FFFF));
    assertEquals("The valid unicode character: Ā", s);
}
```

### 4.3. 数字 -积分 

这些用于Java整数类型：byte、Byte、short、Short、int和Integer、long、Long和BigInteger。此类别中有三种转换：

1.  'd' – 十进制数
2.  'o' – 八进制数
3.  'X'或'x' – 十六进制数

让我们尝试打印其中的每一个：

```java
@Test
public void whenNumericIntegralConversion_thenConvertedString() {
    String s = String.format("The number 25 in decimal = %d", 25);
    assertEquals("The number 25 in decimal = 25", s);

    s = String.format("The number 25 in octal = %o", 25);
    assertEquals("The number 25 in octal = 31", s);

    s = String.format("The number 25 in hexadecimal = %x", 25);
    assertEquals("The number 25 in hexadecimal = 19", s);
}
```

### 4.4. 数字 - 浮点数

用于Java浮点类型：float、Float、double、Double和BigDecimal

1.  'e'或'E' –格式化为计算机化科学记数法中的十进制数
2.  'f' –格式化为十进制数
3.  'g'或'G' –基于四舍五入后的精度值，此转换格式为计算机科学记数法或十进制格式

让我们尝试打印浮点数：

```java
@Test
public void whenNumericFloatingConversion_thenConvertedString() {
    String s = String.format(
      "The computerized scientific format of 10000.00 "
      + "= %e", 10000.00);
 
    assertEquals(
      "The computerized scientific format of 10000.00 = 1.000000e+04", s);
    
    String s2 = String.format("The decimal format of 10.019 = %f", 10.019);
    assertEquals("The decimal format of 10.019 = 10.019000", s2);
}
```

### 4.5. 其他转换 

-   日期/时间——对于能够编码日期或时间的Java类型：long、Long、Calendar、 Date和TemporalAccessor。为此，我们需要使用前缀't'或'T'，正如我们之前看到的
-   百分比– 打印文字'%' ('u0025')
-   行分隔符- 打印特定于平台的行分隔符

让我们看一个简单的例子：

```java
@Test
public void whenLineSeparatorConversion_thenConvertedString() {
    String s = String.format("First Line %nSecond Line");
 
    assertEquals("First Line n" + "Second Line", s);
}
```

## 5.旗帜

通常，标志用于格式化输出。而在日期和时间的情况下，它们用于指定要显示日期的哪一部分，正如我们在第 4 节示例中看到的那样。

有许多标志可用，可以在文档中找到其中的列表。

让我们看一个标志示例来了解它的用法。'-'用于将输出格式化为左对齐：

```java
@Test
public void whenSpecifyFlag_thenGotFormattedString() {
    String s = String.format("Without left justified flag: %5d", 25);
    assertEquals("Without left justified flag:    25", s);

    s = String.format("With left justified flag: %-5d", 25);
    assertEquals("With left justified flag: 25   ", s);
}
```

## 6.精度

对于一般转换，精度只是要写入输出的最大字符数。而 f或浮点转换的精度是小数点后的位数。

第一个语句是浮点数精度的示例，第二个语句是一般转换的示例：

```java
@Test
public void whenSpecifyPrecision_thenGotExpected() {
    String s = String.format(
      "Output of 25.09878 with Precision 2: %.2f", 25.09878);
 
    assertEquals("Output of 25.09878 with Precision 2: 25.10", s);

    String s2 = String.format(
      "Output of general conversion type with Precision 2: %.2b", true);
 
    assertEquals("Output of general conversion type with Precision 2: tr", s2);
}
```

## 7. 参数索引

如前所述，argument_index是一个整数，表示参数在参数列表中的位置。1$表示第一个参数，2$表示第二个参数，依此类推。

此外，还有另一种按位置引用参数的方法，即使用'<' ('u003c')标志，这意味着来自先前格式说明符的参数将被重新使用。例如，这两个语句将产生相同的输出：

```java
@Test
public void whenSpecifyArgumentIndex_thenGotExpected() {
    Calendar c = Calendar.getInstance();
    String s = String.format("The date is: %tm %1$te,%1$tY", c);
    assertEquals("The date is: 12 10,2017", s);

    s = String.format("The date is: %tm %<te,%<tY", c);
    assertEquals("The date is: 12 10,2017", s);
}
```

## 8. 其他使用Formatter的方法

到目前为止，我们看到了Formatter类的format()方法的使用。我们还可以创建一个Formatter实例，并使用它来调用format()方法。

我们可以通过传入Appendable、OutputStream、File或文件名来创建实例。基于此，格式化后的String分别存储在一个Appendable、OutputStream、File中。

让我们看一个将它与Appendable 一起使用的示例。我们可以用同样的方式与其他人一起使用它。

### 8.1. 将Formatter与Appendable一起使用

让我们创建一个 S tringBuilder实例sb，并使用它创建一个Formatter。然后我们将调用format()来格式化一个String：

```java
@Test
public void whenCreateFormatter_thenFormatterWithAppendable() {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    formatter.format("I am writting to a %s Instance.", sb.getClass());
    
    assertEquals(
      "I am writting to a class java.lang.StringBuilder Instance.", 
      sb.toString());
}
```

## 9.总结

在本文中，我们看到了java.util.Formatter类提供的格式化工具。我们看到了可用于格式化String的各种语法以及可用于不同数据类型的转换类型。