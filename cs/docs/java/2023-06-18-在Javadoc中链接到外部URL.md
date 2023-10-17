---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java
copyright: java
excerpt: Java
---

## 1.概述

在编写代码时，我们可能会参考Internet上的文章，例如wiki页面、指南或图书馆的官方文档。在Javadoc中添加指向此类参考文章的链接可能是个好主意。

在本教程中，我们将学习如何在Javadoc中引用外部URL。

## 2.创建内嵌链接

Java没有提供任何特殊的外部链接工具，但我们可以使用标准的HTML。以下语法用于创建内联链接：

```java
/**
 * Some text <a href="URL#value">label</a> 
 */
```

此处，URL#value可以是相对或绝对URL。

让我们考虑一个例子：

```java
/** 
 * Refer to <a href="http://www.tuyucheng.com">Tuyucheng</a> 
 */
```

这将呈现为：

参考[Baeldung](https://www.baeldung.com/)

## 3.创建带标题的内联链接

另一种方法是创建一个包含链接的标题。@see标记用于实现此目的如下：

```java
/**
 * @see <a href="URL#value">label</a>
 */
```

考虑以下示例：

```java
/**
 * @see <a href="http://www.tuyucheng.com">Tuyucheng</a> 
 */
```

这将创建一个包含链接的“另请参阅”标题：
另请参阅：
[Baeldung](https://www.baeldung.com/)

## 4.创建到另一个类的Javadoc的链接

@link标签专门用于链接到其他类和方法的Javadoc。这是一个内联标记，可转换为指向给定类或方法引用文档的HTML超链接：

{@link<类或方法参考>}

假设我们有一个包含方法demo的DemoOne类：

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
}
```

现在，我们可以通过以下方式从另一个类链接到上述类和方法的Javadoc：

```java
/** 
 * See also {@link org.demo.DemoOne}
 */
/**
 * See also {@link org.demo.DemoOne#demo()}
 */
```

这个标签可以用在任何可以写评论的地方，而@see创建它自己的部分。

总而言之，当我们在描述中使用类或方法名时，@link是首选。另一方面，当描述中未提及相关参考或替代指向同一参考的多个链接时，使用@see。

## 5.总结

在本文中，我们了解了在Javadoc中创建外部链接的方法。我们还研究了@see和@link标签之间的区别。
