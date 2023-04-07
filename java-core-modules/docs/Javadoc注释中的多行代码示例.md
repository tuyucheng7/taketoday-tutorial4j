## 一、概述

在本教程中，我们将探讨格式化 Javadoc 注释的不同方式。我们将**重点分析作为文档注释编写的代码片段的格式**。

## 2.简介

Javadoc 是为 Java 类生成文档的工具。它**处理 Java 源文件中指定的文档注释并生成相应的 HTML 页面**。 

请参阅我们的文章以了解有关[Javadoc 文档的](https://www.baeldung.com/javadoc)更多信息。

## 3. 代码片段作为 Javadoc 注释

我们可以将代码片段作为 Java 类的文档注释的一部分。我们想在生成的 HTML 页面上查看具有正确缩进和字符的代码片段。

让我们尝试添加一个 Java 代码片段作为我们评论的一部分：

```java
/**
* This is an example to show default behavior of code snippet formatting in Javadocs
* 
* public class Application(){
* 
* }
* 
*/复制
```

对应的HTML页面：

[![默认的 Javadoc](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Default.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Default.png)

**默认情况下，Javadoc 注释中不保留换行符和空格**。这会导致缩进不正确，尤其是在多行代码片段的情况下。

让我们找到一种方法来在我们的评论中保持正确的缩进。

### 3.1. 使用*<pre>*标签

HTML 提供了*<pre>*标签来指示预先格式化的文本。**它保留了封闭文本的空格和换行符**，从而保留了代码片段所需的缩进：

```java
/**
* This is an example to show usage of HTML pre tag while code snippet formatting in Javadocs
* 
* <pre>
* public class Application(){
*     List<Integer> nums = new ArrayList<>();
* }
* 
* </pre>
*/复制
```

对应的HTML页面：

[![Javadoc 前](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-PRE.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-PRE.png)

在这里，我们成功地保留了代码片段所需的空格和换行符。不过，我们现在遇到了一个不同的问题：我们无法看到作为代码片段的一部分输入的*[泛型。](https://www.baeldung.com/java-generics)*

当评论被解析成 HTML 页面时，**部分代码片段可能会被误解为 HTML 标签**，如上例中的*<Integer> 。*

让我们探索保持评论中嵌入的 HTML 字符格式正确的方法。

### 3.2. 使用 HTML*字符*实体

如果我们的代码片段包含 HTML 保留字符，如“ *<* ”、“ *>* ”或“ *&* ”，则在解析注释时这些可以被解释为 HTML 字符。为了保留这些字符，**我们可以使用\*Character\*实体**来代替所需的字符。

例如，我们可以使用 < 表示 ' *<* ' 字符：

```java
/**
* This is an example to show usage of HTML character entities while code snippet formatting in Javadocs
* 
* <pre>
* public class Application(){
*     List<Integer> nums = new ArrayList<>();
* }
* 
* </pre>
*/复制
```

对应的HTML页面：

[![Javadoc 字符实体](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-CharacterEntities.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-CharacterEntities.png)

### 3.3. 使用*@code*标签

J avadoc 提供了一个 *@code*标签，它将**括号内嵌入的注释视为源代码而不是 HTML 字符**。这使我们能够直接使用 HTML 保留字符，而无需使用*Character*实体对它们进行转义：

```java
/**
* This is an example to show usage of javadoc code tag while code snippet formatting in Javadocs
* 
* <pre>
* 
* public class Application(){
*     {@code List<Integer> nums = new ArrayList<>(); }
* }
*
* </pre>
*/复制
```

对应的HTML页面：

[![Javadoc 代码标签](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Code-Tag.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Code-Tag.png)

请注意， ***@code\*标签并未解决**我们评论中涉及的缩进问题。为此，我们需要额外使用*<pre>*标签。

正如我们在上面看到的， Javadoc 使用“ ***@\*** **”字符****来识别标签**。如果我们将“ *@* ”作为代码片段的一部分，它会被 Javadoc 误解，从而导致注释呈现不正确。

让我们看一个例子：

```java
/**
* This is an example to show issue faced while using annotations in Javadocs
* 
* <pre>
* 
* public class Application(){
*            @Getter
*     {@code List<Integer> nums = new ArrayList<>(); }
* }
*
* </pre>
*/复制
```

对应的HTML页面：

[![Javadoc 注释问题](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Annotation-Issue.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Annotation-Issue.png)

 

如我们所见，Javadoc 将*@Getter*处理为标记，并且注释未正确呈现。我们可以在Javadoc 提供的***@code\*****标记中****嵌入注释（或使用\*@\*字符的代码） ：**

```java
/**
* This is an example to show usage of javadoc code tag for handling '@' character
* 
* <pre>
* 
* public class Application(){
*     {@code @Getter}
*     {@code List<Integer> nums = new ArrayList<>(); }
* }
*
* </pre>
*/复制
```

对应的HTML页面：

[![Javadoc 注释代码标记](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Annotations-Code-Tag.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Annotations-Code-Tag.png)

### 3.4. 使用*@literal*标签

我们也可以**通过使用 \*@literal\*标签实现类似的行为。***@code*标签和*@literal*标签之间的唯一区别 是***@code\*标签将封闭的文本格式化为代码字体**：

```java
/**
* This is an example to show difference in javadoc literal and code tag
* 
* <p>
* 
* {@literal @Getter}
* {@literal List<Integer> nums = new ArrayList<>(); }
*   
* <br />
* {@code @Getter}
* {@code List<Integer> nums = new ArrayList<>(); }
* </p>
*/复制
```

对应的HTML页面：

[![Javadoc 文字与代码标签](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Literal-Vs-Code-Tag.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-Literal-Vs-Code-Tag.png)

因此，我们在 HTML 页面上正确呈现了我们的代码片段。

### 3.5. 格式化 jQuery 代码片段

在这里，我们以 Java 代码片段为例。相同的功能也适用于任何其他语言。

让我们包含一个简单的 jQuery 代码片段作为文档注释：

```java
/**
* This is an example to illustrate a basic jQuery code snippet embedded in documentation comments
* <pre>
* {@code <script>}
* $document.ready(function(){
*     console.log("Hello World!);
* })
* {@code </script>}
* </pre>
*/复制
```

对应的HTML页面：

[![Javadoc jQuery 代码](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-jQuery-Code.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-jQuery-Code.png)

### 3.6. 格式化 HTML 代码片段

到目前为止，我们已经意识到，一方面，Javadoc 使我们能够使用 HTML 标记来格式化我们的注释，而另一方面，当我们想要使用没有标记的 HTML 字符时，它也会导致问题。

例如，我们可能希望在评论中插入 HTML 代码片段。

让我们插入一个 HTML 代码片段作为文档注释的一部分，看看它的行为方式：

```java
/**
* This is an example to illustrate an HTML code snippet embedded in documentation comments
* <pre>
* <html>
* <body>
* <h1>Hello World!</h1>
* </body>
* </html>
* </pre>
* 
*/复制
```

对应的HTML页面：

[![Javadoc HTML 代码片段](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-HTML-Code-Snippet.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-HTML-Code-Snippet.png)

在这里，我们可以看到嵌入评论中的代码片段已被**解析为带有 HTML 标记的 HTML 页面**。我们可以使用上面讨论的***@code\*****标签****来解决这个问题 ：**

```java
/**
* This is an example to illustrate an HTML code snippet embedded in documentation comments
* <pre>{@code
* <html>
* <body>
* <h1>Hello World!</h1>
* </body>
* </html>}
* </pre>
* 
*/复制
```

对应的HTML页面：

[![修复了 Javadoc HTML 代码片段](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-HTML-Code-Snippet-Fixed.png)](https://www.baeldung.com/wp-content/uploads/2021/12/Javadoc-HTML-Code-Snippet-Fixed.png)

## 4。结论

我们探索了格式化 Javadoc 注释的不同方法。**我们可以根据我们的要求选择格式选项来生成格式良好的文档。**

我们可以使用 HTML 标记来增强 Javadoc 注释的格式，并在适合我们的要求时转义它们。