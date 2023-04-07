## 一、概述

在 Java 语言中，我们可以使用[Javadoc](https://www.baeldung.com/javadoc)从 Java 源代码生成 HTML 格式的文档。在本教程中，我们将了解在 Javadoc 中添加对方法参数的引用的不同方法。

## 2. 添加对方法参数的引用的不同方式

在本节中，我们将讨论如何在 Javadoc 中添加对方法参数的引用。我们将在 Javadoc 中看到内联标记*{@code}*和 HTML 样式标记*</co* *de>*的用法。

此外，我们将看到 *{@code}*和*<code>*标签如何处理一些特殊情况：

-   显示特殊字符“<”、“>”和“@”
-   缩进和换行
-   处理 HTML 代码的转义——例如，< 转换为符号“<”

### 2.1. { @code *}*标签

*{@code text}是*[JDK 1.5](https://docs.oracle.com/javase/7/docs/technotes/guides/javadoc/whatsnew-1.5.0.html)中包含的内联标记。

*{@code}*标签 以代码字体格式化文字文本。*{@code abc}*等同于 *<code>{@literal abc}</code>*。

**我们不需要手动转义\*{@code}\*标签内使用的任何特殊字符。**

当我们使用*{@code}*标签时，它：

-   正确显示“<”和“>”
-   正确显示“@”
-   不需要通过 HTML 数字代码转义特殊字符
-   更具可读性和简洁性

让我们在类中创建一个简单的方法，并使用*{@code}*标签添加一些 Javadoc：

```java
/**
  * This method takes a {@code String} 
  * and searches in the given list {@code List<String>}
  * 
  * @param name
  *        Name of the person
  * @param avengers
  *        list of Avengers names
  * @return true if found, false otherwise
  */
public Boolean isAvenger(String name, List<String> avengers) {
    return avengers.contains(name);
}
复制
```

**在这里，我们可以看到我们不需要对特殊字符“<”和“>”进行转义。**

生成的 Javadoc 会将 HTML 输出呈现为：

 

**[![方法一](https://www.baeldung.com/wp-content/uploads/2021/09/method1-1024x482-1.png)](https://www.baeldung.com/wp-content/uploads/2021/09/method1-1024x482-1.png)**

**同样，我们可以看到我们不需要转义 '@' 字符：**

```java
/**
  * This is sample for showing @ use without any manual escape.
  * {@code @AnyAnnotaion}
  * 
  */
public void javadocTest() {
}
复制
```

这将呈现为 HTML Javadoc 为：

 

[![方法二](https://www.baeldung.com/wp-content/uploads/2021/09/method2-1024x320-1.png)](https://www.baeldung.com/wp-content/uploads/2021/09/method2-1024x320-1.png)

对于 Javadoc 中的多行代码片段，*{@code}*不会保留缩进和换行符。为了克服这个问题，我们可以使用 HTML 标签*<pre>*和*{@code}*。但是，在这种情况下，我们需要转义“@”字符。

### 2.2. <code>标签*_*

<code>是 Javadoc 支持的 HTML 样式标签。

当我们使用*<code>*标签时，它：

-   没有正确显示“<”和“>”
-   需要通过 HTML 数字代码转义特殊字符
-   不是那么可读

让我们再次考虑同一个例子。我们可以看到生成的 Javadoc HTML**在我们的段落中的*****List\*****之后****缺少\*<String>\***参数化类型：

```java
/**
  * This method takes a <code>String</code>
  * and searches in the given <code>List<String></code>
  * 
  * @param name
  *        Name of the person
  * @param avengers
  *        list of Avengers names
  * @return true if found, false otherwise
  */
public Boolean isAvenger(String name, List<String> avengers) {
    return avengers.contains(name);
}
复制
```

[![方法三](https://www.baeldung.com/wp-content/uploads/2021/09/method3-1024x459-1.png)](https://www.baeldung.com/wp-content/uploads/2021/09/method3-1024x459-1.png)
在这里，如果我们在方法注​​释中转义特殊字符“<”和“>”，那么它将在 Javadoc 中呈现正确的*<String> ：*

```java
/**
  * This method takes a <code>String</code>
  * and searches in the given <code>List<String></code>
  * 
  * @param name
  *        Name of the person
  * @param avengers
  *        list of Avengers names
  * @return true if found, false otherwise
  */
public Boolean isAvenger(String name, List<String> avengers) {
    return avengers.contains(name);
}
复制
```

 

[![方法4](https://www.baeldung.com/wp-content/uploads/2021/09/method4-1024x482-1.png)](https://www.baeldung.com/wp-content/uploads/2021/09/method4-1024x482-1.png)

## 3.结论

在本教程中，我们首先讨论了如何使用*{@code}*和*<code>*来引用 Javadoc 中的方法参数。然后我们描述了这些标签对特殊字符的处理。总之，我们现在了解了如何在 Javadoc 中添加对方法参数的引用，并且**我们可以看到\*{@code}比\**<code>\*****任何一天都要好**。