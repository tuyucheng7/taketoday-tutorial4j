## 1.概述

在本教程中，我们将讨论Java中的字符串插值主题。我们将查看几个不同的示例，然后查看一些细节。

## 2.Java中的字符串插值

字符串插值是一种将变量值注入字符串的直接而精确的方法。它允许用户将变量引用直接嵌入到处理过的字符串文字中。[与Scala](https://www.baeldung.com/scala/string-interpolation)等语言相比，Java缺乏对String插值的原生支持。

但是，有一些方法可以在Java中完成此行为。在以下部分中，我们将解释其中的每一种方法。

##  3.加运算符

首先，我们有[“+”运算符](https://www.baeldung.com/java-strings-concatenation)。我们可以使用“+”运算符连接我们的变量和字符串值。变量被它的值替换，所以我们实现了字符串的插值或连接。让我们看一个代码示例：

```java
@Test
public void givenTwoString_thenInterpolateWithPlusSign() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String first = "Interpolation";
    String second = "Java";
    String result = "String " + first + " in " + second + " with some " + second + " examples.";
    assertEquals(EXPECTED_STRING, result);
}
```

正如我们在前面的示例中注意到的那样，使用此运算符，生成的字符串包含变量的值“插值”与其他字符串值。由于可以根据特定需要对其进行调整，因此这种字符串连接方法是最直接和最有价值的方法之一。使用运算符时，我们不需要将文本放在引号中。

## 4.format()函数

另一种方法是使用String类中的[format()](https://www.baeldung.com/string/format)方法。与“+”运算符相反，在这种情况下，我们需要使用占位符来获得字符串插值中的预期结果。下面的代码片段演示了它是如何运作的：

```java
@Test
public void givenTwoString_thenInterpolateWithFormat() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String first = "Interpolation";
    String second = "Java";
    String result = String.format("String %s in %s with some %s examples.", first, second, second);
    assertEquals(EXPECTED_STRING, result);
}
```

此外，如果我们想避免在我们的格式调用中重复变量，我们可以引用一个特定的参数。让我们看看这个行为：

```java
@Test
public void givenTwoString_thenInterpolateWithFormatStringReference() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String first = "Interpolation";
    String second = "Java";
    String result = String.format("String %1$s in %2$s with some %2$s examples.", first, second);
    assertEquals(EXPECTED_STRING, result);
}
```

现在，我们减少了不必要的变量重复。相反，我们使用参数列表中参数的索引。

## 5.StringBuilder类_

我们下面的方法是[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)类。我们实例化一个StringBuilder对象，然后调用append()函数来构建String。在此过程中，我们的变量被添加到结果String中：

```java
@Test
public void givenTwoString_thenInterpolateWithStringBuilder() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String first = "Interpolation";
    String second = "Java";
    StringBuilder builder = new StringBuilder();
    builder.append("String ")
      .append(first)
      .append(" in ")
      .append(second)
      .append(" with some ")
      .append(second)
      .append(" examples.");
    String result = builder.toString();
    assertEquals(EXPECTED_STRING, result);
}
```

正如我们在上面的代码示例中注意到的那样，我们可以通过链接append函数来使用必要的文本插入字符串，该函数接受参数作为变量(在本例中为两个Strings)。

## 6.消息格式类

使用[MessageFormat](https://www.baeldung.com/java-localization-messages-formatting)类是在Java中获取String插值的一种鲜为人知的方法。使用MessageFormat，我们可以创建串联消息而不用担心底层语言。这是创建面向用户的消息的标准方法。它获取一个对象集合，格式化其中包含的字符串，并将它们插入到模式中的适当位置。

MessageFormat的格式化方法与String的格式化方法几乎相同，除了占位符的编写方式。{0}、{1}、{2}等索引表示此函数中的占位符：

```java
@Test
public void givenTwoString_thenInterpolateWithMessageFormat() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String first = "Interpolation";
    String second = "Java";
    String result = MessageFormat.format("String {0} in {1} with some {1} examples.", first, second);
    assertEquals(EXPECTED_STRING, result);
}
```

关于性能，StringBuilder仅将文本附加到动态缓冲区；但是，MessageFormat在附加数据之前解析给定的格式。正因为如此，StringBuilder在效率上优于MessageFormat。

## 7.阿帕奇公地

最后，我们有来自[ApacheCommons的](https://www.baeldung.com/java-apache-commons-text)StringSubstitutor。在此类的上下文中，值被替换为包含在String中的变量。这个类接受一段文本并替换所有变量。变量的默认定义是${variableName}。构造函数和设置方法可用于更改前缀和后缀。变量值的解析通常涉及使用映射。但是，我们可以通过使用系统属性或提供专门的变量解析器来解析它们：

```java
@Test
public void givenTwoString_thenInterpolateWithStringSubstitutor() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String baseString = "String ${first} in ${second} with some ${second} examples.";
    String first = "Interpolation";
    String second = "Java";
    Map<String, String> parameters = new HashMap<>();
    parameters.put("first", first);
    parameters.put("second", second);
    StringSubstitutor substitutor = new StringSubstitutor(parameters);
    String result = substitutor.replace(baseString);
    assertEquals(EXPECTED_STRING, result);
}
```

从我们的代码示例中，我们可以看到我们创建了一个Map。键名与将在String中替换的变量名相同。然后，我们将每个键对应的值放入Map中。接下来，我们将它作为构造函数参数传递给StringSubstitutor类。最后，实例化对象调用replace()函数。此函数接收带有占位符的文本作为参数。结果，我们得到了一个内插文本。就是这样，很简单。

## 8. 总结

最后，我们对什么是字符串你插值做了一个简单的描述。接下来，我们使用本地Java运算符(来自你String你类的你format()你方法)以某种方式在Java语言中实现这一点。此外，我们还探索了你ApacheCommons中鲜为人知的选项，例如你你MessageFormat你和你StringSubstitutor你。