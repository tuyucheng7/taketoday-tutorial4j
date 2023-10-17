---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java
copyright: java
excerpt: Java
---

## 1.概述

[Javadoc](https://www.baeldung.com/javadoc)是一种从Java源代码生成HTML格式文档的方法。

在本教程中，我们将重点关注文档注释中的@version和@since标记。

## 2.@version和@since的用法

在本节中，我们将讨论如何正确使用@version和@since标签。

### 2.1.@版本

@version标签的格式很简单：

```plaintext
@version  version-text
```

例如，我们可以用它来表示JDK 1.7：

```java
/**
 * @version JDK 1.7
 */
```

当我们使用@version标签时，它有两种不同的使用场景：

-   记录单个文件的版本
-   标记整个软件的版本

显然，我们可以看到这两种情况之间存在差异。那是因为单个文件的版本可能与软件的版本不兼容。此外，不同的文件可能有不同的文件版本。那么，我们应该如何使用@version标签呢？

过去，Sun使用@version标记来记录单个文件的版本。并且建议@version标签使用SCCS字符串“%I%,%G%”。然后，当我们检出文件时，SCCS会将“%I%”替换为文件的当前版本，将“%G%”替换为日期“mm/dd/yy”。例如，它看起来像“1.39,02/28/97”(mm/dd/yy)。此外，每次我们编辑和删除(delta+get)文件时，%I%都会增加。

SCCS也称为源代码控制系统。如果我们想了解更多关于SCCSCommand的信息，可以参考[这里](https://www.ibm.com/docs/en/aix/7.2?topic=s-sccs-command)。此外，SCCS是一个老式的源代码版本控制系统。

目前，我们倾向于使用@version标签来表示整个软件的版本。鉴于此，它使得@version标记不必要地放置在单个文件中。

是不是意味着单个文件的版本已经不重要了？这实际上不是真的。现在，我们有了现代化的版本控制软件，比如Git、SVN、CVS等等。每个版本控制软件都有其独特的记录每个文件版本的方式，不需要依赖@version标签。

我们以OracleJDK 8为例。如果我们查看src.zip文件中的源代码，我们可能会发现只有java.awt.Color类有@version标签：

```java
/**
 * @version     10 Feb 1997
 */
```

因此，我们可以推断使用@version标记来指示单个文件的版本正在淡化。因此，[Oracle文档](https://docs.oracle.com/en/java/javase/11/docs/api/jdk.javadoc/com/sun/javadoc/package-summary.html)建议我们使用@version标签来记录软件的当前版本号。

### 2.2.@自从

@since标签的格式非常简单：

```plaintext
@since  since-text
```

例如，我们可以用它来标记JDK 1.7中引入的一个特性：

```java
/**
 * @since JDK 1.7
 */
```

简而言之，我们使用@since标记来描述更改或功能首次存在的时间。同样，它使用的是整个软件的发布版本，而不是单个文件的版本。Oracle[文档](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html#@since)为我们提供了一些有关如何使用@since标记的详细说明：

-   当引入一个新包时，我们应该在包描述和它的每个类中指定一个@since标签。
-   当添加一个新的类或接口时，我们应该在类描述中指定一个@since标签，而不是在类成员的描述中。
-   如果我们向现有类添加新成员，我们应该只为新添加的成员指定@since标签，而不是在类描述中。
-   如果我们在以后的版本中将类成员从protected更改为public，我们不应该更改@since标记。

有时，@since标签相当重要，因为它提供了一个重要的提示，即软件用户应该只在某个特定的发行版本之后期待特定的功能。

如果我们再次查看src.zip文件，我们可能会发现许多@since标记用法。我们以java.lang.FunctionalInterface类为例：

```java
/**
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FunctionalInterface {}
```

从这个代码片段中，我们可以了解到FunctionalInterface类仅在JDK 8及以上版本中可用。

## 3.@version和@since的相似之处

在本节中，让我们看看@version和@since标签之间的相似之处。

### 3.1.两者都属于块标签

首先，@version和@since都属于块标签。

在文档评论中，标签可以分为两类：

-   块标记
-   内嵌标签

块标记的形式为@tag。它应该出现在一行的开头，忽略前导星号、空格和分隔符(/)。例如，我们可以在标签部分使用@version和@since：

```java
/**
 * Some description here.
 * 
 * @version 1.2
 * @since 1.1
 */
```

但是，内联标签具有{@tag}的形式。它可以存在于描述或评论中的任何位置。例如，如果我们有一个{@link}标签，我们可以在描述中使用它：

```java
/**
 * We can use a {@link java.lang.StringBuilder} class here.
 */
```

### 3.2.两者都可以多次使用

其次，@version和@since都可以多次使用。起初，我们可能会对这种用法感到震惊。那么，我们可能会疑惑@version标签怎么会在一个类中出现多次。但这是真的，并且记录[在此处](https://docs.oracle.com/javase/1.5.0/docs/tooldocs/windows/javadoc.html#javadoctags)。并且它解释了我们可以在多个API中使用相同的程序元素。因此，我们可以附加具有相同程序元素的不同版本。

例如，如果我们在不同版本的ADK和JDK中使用相同的类或接口，我们可以提供不同的@version和@since消息：

```java
/**
 * Some description here.
 *
 * @version ADK 1.6
 * @version JDK 1.7
 * @since ADK 1.3
 * @since JDK 1.4
 */
```

在生成的HTML页面中，Javadoc工具将在名称之间插入逗号(,)和空格。因此，版本文本如下所示：

```plaintext
ADK 1.6, JDK 1.7
```

而且，因为文本看起来像：

```plaintext
ADK 1.3, JDK 1.4
```

## 4.@version和@since的区别

在本节中，让我们看看@version和@since标签之间的区别。

### 4.1.他们的内容是否改变

@version文本是不断变化的，而@since文本是稳定的。随着时间的推移，软件也在不断发展。新的特性会加入，所以它的版本会不断变化。但是，@since标记仅标识过去出现新更改或功能的时间点。

### 4.2.他们可以在哪里使用

这两个标签的用法略有不同：

-   @version:概述、包、类、接口
-   @since：概述、包、类、接口、字段、构造函数、方法

@since标签的使用范围更广，在任何文档注释中都有效。相比之下，@version标签的使用范围更窄，我们不能在字段、构造函数或方法中使用它。

### 4.3.它们是否默认出现

默认情况下，这两个标记在生成的HTML页面中具有不同的行为：

-   @version文本默认不显示
-   @since文本默认出现

如果我们想在生成的文档中包含“版本文本”，我们可以使用-version选项：

```plaintext
javadoc -version -d docs/ src/*.java
```

同样，如果我们想在生成的文档中省略“sincetext”，我们可以使用-nosince选项：

```plaintext
javadoc -nosince -d docs/ src/*.java
```

## 5.总结

在本教程中，我们首先讨论了如何正确使用@version和@since标签。然后我们描述了它们之间的相同点和不同点。简而言之，@version标签保存软件的当前版本号，@since标签描述更改或功能首次存在的时间。
