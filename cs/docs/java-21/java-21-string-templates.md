## 一、简介

在本教程中，我们将讨论 Java 对字符串插值的回答——字符串模板。此预发布预览功能是作为 Java 21 和[JEP 430](https://openjdk.org/jeps/430)的一部分引入的。

## 2. Java中的字符串组合

我们使用*字符串*来表示数字、字母和符号的序列来表示代码中的文本。*字符串*在编程中无处不在，我们经常需要组合字符串以在代码中使用。有多种方法可以做到这一点，每种技术都有其缺点。

### 2.1. 字符串连接

[字符串连接](https://www.baeldung.com/java-strings-concatenation)是我们用来构建字符串的最基本操作。我们采用字符串文字和表达式，然后使用*+*符号将它们组合在一起：

```java
String composeUsingPlus(String feelsLike, String temperature, String unit){
    return "Today's weather is " + feelsLike + 
      ", with a temperature of " + temperature + " degrees " + unit;
}复制
```

**该代码实现了所需的功能，但难以阅读，尤其是带有所有加号，并且也难以维护和更改。**

### 2.2. *StringBuffer*或*StringBuilder*

我们可以使用Java提供的实用程序类，例如[*StringBuilder*](https://www.baeldung.com/java-string-builder-string-buffer)[和](https://www.baeldung.com/java-string-builder-string-buffer)[*StringBuffer*](https://www.baeldung.com/java-string-builder-string-buffer)类。这些类为我们提供了*append()* 库函数来组合字符串，从而消除了字符串组合中*+*的使用：

```java
String composeUsingStringBuilder(String feelsLike, String temperature, String unit) {
    return new StringBuilder()
      .append("Today's weather is ")
      .append(feelsLike)
      .append(", with a temperature of ")
      .append(temperature)
      .append(" degrees ")
      .append(unit)
      .toString();
}复制
```

*StringBuilder*和*StringBuffer*类提供高效的字符串操作和组合技术，同时减少内存开销。**然而，它们遵循\*Builder\*设计模式，因此变得相当冗长。**

### 2.3. 字符串格式化程序

Java 为我们提供了使用*[String.format()](https://www.baeldung.com/string/format)*或*formatted()*方法分离 String 的静态部分和参数的能力，例如温度*和*单位*：*

```java
String composeUsingFormatters(String feelsLike, String temperature, String unit) {
    return String.format("Today's weather is %s, with a temperature of %s degrees %s", 
      feelsLike, temperature, unit);
}复制
```

**基本模板字符串保持静态。然而，此处传递的参数的顺序和数量对于其响应的正确性至关重要。**

### 2.4. *消息格式*类

Java 提供了*Java.text*包的*MessageFormat*类，可帮助使用动态数据的占位符组合文本消息。[*本地化*](https://www.baeldung.com/java-localization-messages-formatting)和*国际化*大量使用了这一点。我们可以在纯字符串组合中使用*MessageFormat.format() ：*

```java
String composeUsingMessageFormatter(String feelsLike, String temperature, String unit) {
    return MessageFormat.format("Today''s weather is {0}, with a temperature of {1} degrees {2}",
      feelsLike, temperature, unit);
}复制
```

这也有与上述类似的缺点。此外，语法结构与我们在代码中编写和使用字符串的方式不同。

## 3. 字符串模板简介

正如我们所看到的，上面提到的所有字符串组合技术都有其缺点。让我们看看字符串模板如何帮助解决这些问题。

### 3.1. 目标

将字符串模板引入 Java 编程生态系统是为了实现以下目标：

- 使用可在运行时编译的值简化表达*字符串*的过程
- 增强*字符串组合的可读性，克服与**StringBuilder* 和*StringBuffer*类相关的冗长内容
- *克服了其他编程语言允许的字符串*插值技术的安全问题，权衡了少量的不便
- 允许 Java 库定义生成的字符串文字的自定义格式语法

### 3.2. 模板表达式

字符串模板最重要的概念围绕模板表达式，这是 Java 中一种新型的可编程表达式。**可编程模板表达式可以执行插值，但也为我们提供了安全有效地组合字符串的灵活性。**

**模板表达式可以将结构化文本转换为任何对象，而不仅限于字符串。**

模板表达式由三个组成部分组成：

- 一个处理器
- 包含带有嵌入表达式的数据的模板
- 点 (.) 字符

## 4. 模板处理器

**模板处理器负责评估嵌入的表达式（模板），并在运行时将其与\*String\*文字组合以生成最终的\*String\*。**Java 提供了使用 Java 提供的内置模板处理器或使用我们自己的自定义处理器进行切换的能力。

这是 Java 21 中的预览功能；因此，我们必须启用预览模式。

### 4.1. *STR*模板处理器

Java 提供了一些开箱即用的模板处理器。***STR\*****模板处理器通过迭代地将所提供模板的每个嵌入表达式替换为该表达式的字符串化值来执行字符串插值**。我们将在前面的示例中应用*STR处理器字符串模板：*

```java
String interpolationUsingSTRProcessor(String feelsLike, String temperature, String unit) {
    return STR
      . "Today's weather is \{ feelsLike }, with a temperature of \{ temperature } degrees \{ unit }" ;
}复制
```

***STR\* 是一个公共静态最终字段，会自动导入到每个 Java 编译单元中。**

我们不仅可以将上述实现扩展到单行字符串，还可以扩展到多行表达式。*对于多行文本块，我们用“””*包围文本块。让我们以插入表示 JSON 的字符串为例：

```java
String interpolationOfJSONBlock(String feelsLike, String temperature, String unit) {
    return STR
      . """
      {
        "feelsLike": "\{ feelsLike }",
        "temperature": "\{ temperature }",
        "unit": "\{ unit }"
      }
      """ ;
}复制
```

**我们还可以内联注入表达式，它将在运行时编译：**

```java
String interpolationWithExpressions() {
    return STR
      . "Today's weather is \{ getFeelsLike() }, with a temperature of \{ getTemperature() } degrees \{ getUnit() }";
}复制
```

### 4.2. *FMT*模板处理器

Java 提供的另一个处理器是*FMT*模板处理器。**它添加了对提供给处理器的理解格式化程序的支持，并根据提供的格式化样式格式化数据。**

提供的*格式化程序*应该类似于*[java.util.Formatter](https://www.baeldung.com/java-string-formatter)：*

```java
String interpolationOfJSONBlockWithFMT(String feelsLike, float temperature, String unit) {
    return FMT
      . """
      {
        "feelsLike": "%1s\{ feelsLike }",
        "temperature": "%2.2f\{ temperature }",
        "unit": "%1s\{ unit }"
      }
      """ ;
}复制
```

**这里，我们使用\*%s\*和\*%f\*将字符串和温度格式化为特定格式。**

### 4.3. 模板表达式的求值

该行中模板表达式的计算涉及几个步骤：

```java
STR
  . "Today's weather is \{ feelsLike }, with a temperature of \{ temperature } degrees \{ unit }" ;复制
```

上面是我们将看到的几个步骤的简写。

首先，通过评估点的左侧获得模板处理器的实例*StringTemplate.Processor<R, E> 。*在我们的例子中，它是*STR*模板处理器。

 接下来，我们通过计算点右侧的值来获取模板*StringTemplate*的实例：

```java
StringTemplate str = RAW
  . "Today's weather is \{ getFeelsLike() }, with a temperature of \{ getTemperature() } degrees \{ getUnit() }" ;复制
```

*RAW*是标准模板处理器，可生成未处理的*StringTemplate*类型对象。

最后，我们将*StringTemplate str*实例传递给处理器的 process() 方法（在我们的例子中是*STR）*：

```java
return STR.process(str);复制
```

## 5. 字符串插值和字符串模板

我们现在已经看到了使用字符串模板作为字符串组合技术的示例，我们可以看到它与字符串插值非常相似。但是，字符串模板提供了其他平台上的字符串插值通常无法保证的安全性。

**模板表达式是有意设计的，因此不可能将包含嵌入表达式的字符串文字或文本块直接插入到输出字符串中。**处理器的存在确保危险或不正确的字符串不会通过代码传播。**处理器有责任验证插值是否安全且正确。**

**缺少任何模板处理器将生成编译时错误。此外，如果处理器无法插值，它可能会生成\*Exception\*。**

Java根据嵌入表达式的存在来决定将*“<some text>”*视为*StringLiteral*或*StringTemplate 。**“””<some text>”””*也是如此，以区分*TextBlock*和*TextBlockTemplate*。**这种区别对 Java 很重要，因为尽管在这两种情况下，字符串模板都用双引号 ( \*“”\* ) 括起来，但 String 模板的类型是\*java.lang.StringTemplate，\* 是一个接口，而** **不是\*java.lang.String。\***

## 六，结论

在本文中，我们讨论了几种字符串组合技术并了解字符串插值背后的思想。我们还了解了 Java 如何借助字符串模板引入字符串插值的思想。最后，我们研究了字符串模板如何比一般字符串插值更好、更安全地使用。