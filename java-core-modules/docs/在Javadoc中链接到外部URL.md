## 一、简介

在编写代码时，我们可能会参考 Internet 上的文章，例如 wiki 页面、指南或图书馆的官方文档。**在 Javadoc 中添加指向此类参考文章的链接可能是个好主意。**

在本教程中，我们将学习如何在 Javadoc 中引用外部 URL。

## 2. 创建内嵌链接

**Java 没有提供任何特殊的外部链接工具，但我们可以使用标准的 HTML。**以下语法用于创建内联链接：

```java
/**
 * Some text <a href="URL#value">label</a> 
 */复制
```

此处，*URL#value*可以是相对或绝对 URL。

让我们考虑一个例子：

```java
/** 
 * Refer to <a href="http://www.baeldung.com">Baeldung</a> 
 */复制
```

这将呈现为：

**参考[Baeldung](https://www.baeldung.com/)**

## 3.创建带标题的内联链接

**另一种方法是创建一个包含链接的标题。***@see*标记用于实现此目的如下：

```java
/**
 * @see <a href="URL#value">label</a>
 */复制
```

考虑以下示例：

```java
/**
 * @see <a href="http://www.baeldung.com">Baeldung</a> 
 */复制
```

这将创建一个包含链接的“另请参阅”标题：
**另请参阅：**
[Baeldung](https://www.baeldung.com/)

## 4. 创建到另一个类的 Javadoc 的链接

***@link\*****标签**专门**用于链接到其他类和方法的Javadoc。**这是一个内联标记，可转换为指向给定类或方法引用文档的 HTML 超链接：

*{@link <类或方法参考>}*

假设我们有一个包含方法*demo的**DemoOne*类：

```java
/** 
 * Javadoc
 */
class DemoOne {
  
  /**
   * Javadoc
  */
  void demo() {
    //some code
  }
}复制
```

现在，我们可以通过以下方式从另一个类链接到上述类和方法的 Javadoc：

```java
/** 
 * See also {@link org.demo.DemoOne}
 */复制
/**
 * See also {@link org.demo.DemoOne#demo()}
 */复制
```

这个标签可以用在任何可以写评论的地方，而*@see*创建它自己的部分。

总而言之，当我们在描述中使用类或方法名时，*@link是首选。*另一方面，当描述中未提及相关参考或替代指向同一参考的多个链接时，使用*@see 。*

## 5.结论

在本文中，我们了解了在 Javadoc 中创建外部链接的方法。*我们还研究了@see*和*@link*标签之间的区别。