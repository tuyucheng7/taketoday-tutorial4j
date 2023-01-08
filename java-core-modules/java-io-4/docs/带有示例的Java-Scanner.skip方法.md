## 1. 概述

java.util.Scanner有很多方法可以用来验证输入。其中之一是skip()方法。

在本教程中，我们将学习skip()方法的用途以及如何使用它。

## 2.Scanner.skip ()方法

skip ()方法属于JavaScanner类。它用于跳过与方法参数中传递的指定模式匹配的输入，忽略分隔符。

### 2.1. 句法

skip ()方法有两个重载方法签名：

-   skip(Pattern pattern) – 将Scanner应该跳过的模式作为参数
-   skip(String pattern) – 将指定要跳过的模式的字符串作为参数

### 2.2. 退货

skip()返回满足方法参数中指定模式的Scanner对象。它还可以抛出两种类型的异常：IllegalStateException(如果扫描器已关闭)和NoSuchElementException(如果未找到指定模式的匹配项)。

请注意，可以通过使用无法匹配任何内容的模式来跳过某些内容而不会冒NoSuchElementException的风险——例如，skip (“[ t]”)。

## 3.例子

正如我们前面提到的，skip方法有两种重载形式。首先，让我们看看如何将skip方法与Pattern一起使用：

```java
String str = "Java scanner skip tutorial"; 
Scanner sc = new Scanner(str); 
sc.skip(Pattern.compile(".ava"));
```

在这里，我们使用了skip(Pattern)方法来跳过符合“.ava”模式的文本

同样，skip(String)方法将跳过满足从给定String构造的给定模式的文本。在我们的示例中，我们跳过字符串“Java”：

```java
String str = "Java scanner skip tutorial";
Scanner sc = new Scanner(str); 
sc.skip("Java");
```

简而言之，无论是使用模式还是使用字符串，这两种方法的结果都是一样的。

## 4。总结

在这篇简短的文章中，我们检查了如何使用String或Pattern参数来使用java.util.Scanner类的skip()方法。