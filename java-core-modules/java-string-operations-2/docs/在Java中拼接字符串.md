## 1. 概述

Java 提供了大量专用于连接 字符串的方法和类。

在本教程中，我们将深入研究其中的几个，并概述一些常见的陷阱和不良做法。

## 2.字符串生成器

首先是不起眼的 StringBuilder。 此类提供一组 字符串构建实用程序，使 字符串 操作变得容易。

让我们使用 StringBuilder 类构建一个 字符串 连接的快速示例：

```java
StringBuilder stringBuilder = new StringBuilder(100);

stringBuilder.append("Baeldung");
stringBuilder.append(" is");
stringBuilder.append(" awesome");

assertEquals("Baeldung is awesome", stringBuilder.toString());
```

在内部， StringBuilder 维护一个可变的字符数组。在我们的代码示例中，我们已通过 StringBuilder 构造函数声明其初始大小为 100 。由于此大小声明， StringBuilder 可以成为连接 字符串的一种非常有效的方法。

还值得注意的是， StringBuffer 类是 StringBuilder 的同步 版本。 

尽管同步通常是线程安全的同义词，但由于 StringBuffer 的 构建器模式，不建议在多线程应用程序中使用它。虽然对同步方法的单个调用是线程安全的，但[多个调用不是](https://dzone.com/articles/why-synchronized-stringbuffer)。

## 3.加法运算符

接下来是加法运算符 (+)。这是导致数字相加并在应用于 字符串时重载以连接的相同运算符。

让我们快速看一下它是如何工作的：

```java
String myString = "The " + "quick " + "brown " + "fox...";

assertEquals("The quick brown fox...", myString);
```

乍一看，这似乎比 StringBuilder 选项简洁得多。但是，当源代码编译时，+ 符号会转换为 StringBuilder.append() 调用链。因此，混合使用 StringBuilder 和 +连接方法被认为是不好的做法。

此外， 应避免在循环中使用 + 运算符进行字符串 连接。由于 String 对象是不可变的，因此每次调用串联都会导致创建一个新的 String 对象。

## 4. 字符串方法

String 类本身提供了大量用于连接字符串的 方法。

### 4.1. 字符串.concat

不出所料， String.concat 方法是我们尝试连接 String 对象时的第一个调用端口。此方法返回一个 String 对象，因此将方法链接在一起是一个有用的功能。

```java
String myString = "Both".concat(" fickle")
  .concat(" dwarves")
  .concat(" jinx")
  .concat(" my")
  .concat(" pig")
  .concat(" quiz");

assertEquals("Both fickle dwarves jinx my pig quiz", myString);
```

在此示例中，我们的链以 String 文字开始， 然后concat 方法允许我们链接调用以附加更多Strings。

### 4.2. 字符串格式

接下来是[String.format 方法 ，](https://www.baeldung.com/java-string-formatter)它允许我们将各种 Java对象 注入到 String 模板中。

String.format 方法签名采用单个 String 表示 我们的 template。此模板包含“%”字符以表示各种 对象 应放置在其中的位置。

一旦我们的模板被声明，它就会接受一个 注入到模板中的[可变参数](https://www.baeldung.com/java-varargs)对象 数组。

让我们通过一个简单的例子看看它是如何工作的：

```java
String myString = String.format("%s %s %.2f %s %s, %s...", "I",
  "ate",
  2.5056302,
  "blueberry",
  "pies",
  "oops");

assertEquals("I ate 2.51 blueberry pies, oops...", myString);
```

正如我们在上面看到的，该方法已将我们的 字符串 注入正确的格式。

### 4.3. 字符串连接(Java 8+)

如果我们的应用程序运行在Java8 或更高版本上，我们可以利用 String.join 方法。有了这个，我们可以用一个公共分隔符连接一个字符串 数组 ，确保没有遗漏任何空格。

```java
String[] strings = {"I'm", "running", "out", "of", "pangrams!"};

String myString = String.join(" ", strings);

assertEquals("I'm running out of pangrams!", myString);

```

这种方法的一个巨大优势是不必担心字符串之间的分隔符。

## 5. 字符串连接器 (Java 8+)

StringJoiner 将所有 String.join 功能抽象为一个简单易用的类。构造函数采用分隔符，带有可选的前缀和后缀。我们可以 使用名副其实的 add 方法附加字符串 。

```java
StringJoiner fruitJoiner = new StringJoiner(", ");

fruitJoiner.add("Apples");
fruitJoiner.add("Oranges");
fruitJoiner.add("Bananas");

assertEquals("Apples, Oranges, Bananas", fruitJoiner.toString());
```

通过使用这个类，而不是 String.join 方法，我们可以 在程序运行时追加字符串 ；无需先创建数组！

前往我们[关于StringJoiner](https://www.baeldung.com/java-string-joiner)的文章了解更多信息和示例。

## 6. 数组到字符串

关于数组， [Array 类](https://www.baeldung.com/java-util-arrays)还包含一个方便的 toString 方法，它可以很好地格式化对象数组。阵列。toString 方法还会调用任何封闭对象的 toString 方法——所以我们需要确保我们已经定义了一个。

```java
String[] myFavouriteLanguages = {"Java", "JavaScript", "Python"};

String toString = Arrays.toString(myFavouriteLanguages);

assertEquals("[Java, JavaScript, Python]", toString);
```

不幸的是， 数组。toString 方法不可自定义，仅输出 包含在方括号中的字符串 。

## 7. 收集器.joining (Java 8+)

最后，让我们看一下 Collectors.joining 方法，它允许我们将 Stream 的输出汇集到单个 String 中。

```java
List<String> awesomeAnimals = Arrays.asList("Shark", "Panda", "Armadillo");

String animalString = awesomeAnimals.stream().collect(Collectors.joining(", "));

assertEquals("Shark, Panda, Armadillo", animalString);
```

使用流可以解锁与[Java 8 Stream API](https://www.baeldung.com/java-streams)相关的所有功能，例如过滤、映射、迭代等。

## 8. 总结

在本文中，我们深入探讨了用于连接Java语言中的字符串的大量类和方法。