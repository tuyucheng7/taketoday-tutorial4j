## 1. 概述

良好的API文档是促成软件项目全面成功的众多因素之一。

幸运的是，所有现代版本的JDK都提供了Javadoc工具-用于从源代码中的注释生成API文档。

先决条件：

1.  JDK 1.4(推荐使用JDK 7+最新版本的Maven Javadoc插件)
2.  JDK/bin文件夹添加到PATH环境变量中
3.  带有内置工具的IDE(可选)

## 2. Javadoc注释

让我们从注释开始。

**Javadoc注释结构看起来与常规的多行注释非常相似**，但主要区别在于开头的额外星号：

```java
// This is a single line comment

/*
 * This is a regular multi-line comment
 */

/**
 * This is a Javadoc
 */
```

**Javadoc样式注释也可以包含HTML标签**。

### 2.1 文档格式

**Javadoc注释可以放在我们想要记录的任何类、方法或字段之上**。

这些注释通常由两部分组成：

1.  我们正在注释的内容的描述
2.  描述特定元数据的独立块标签(标有“@”符号)

我们将在示例中使用一些更常见的块标签。有关块标签的完整列表，请访问[参考指南](https://docs.oracle.com/en/java/javase/11/tools/javadoc.html)。

### 2.2 类级别的Javadoc

让我们看一下类级别的Javadoc注释是什么样的：

```java
/**
 * Hero is the main entity we'll be using to . . .
 *
 * Please see the {@link Person} class for true identity
 * @author Tuyucheng
 */
public class SuperHero extends Person {
	// fields and methods
}
```

我们有一个简短的描述和两个不同的块标签-独立的和内联的：

-   独立标签出现在描述之后，标签作为一行中的第一个单词，例如，@author标签
-   **内联标签可以出现在任何地方，并用大括号括起来**，例如，描述中的@link标签

在我们的示例中，我们还可以看到使用了两种块标签：

-   {@link}提供指向我们源代码引用部分的内联链接
-   @author添加被注释的类、方法或字段的作者姓名

### 2.3 字段级别的Javadoc

我们还可以在SuperHero类中使用不带任何块标签的描述：

```java
/**
 * The public name of a hero that is common knowledge
 */
private String heroName;
```

私有字段不会为它们生成Javadoc，除非我们明确地将-private选项传递给Javadoc命令。

### 2.4 方法级别的Javadoc

方法可以包含各种Javadoc块标签。

让我们看一下我们正在使用的方法：

```java
/**
 * <p>This is a simple description of the method. . .
 * <a href="http://www.supermanisthegreatest.com">Superman!</a>
 * </p>
 * @param incomingDamage the amount of incoming damage
 * @return the amount of health hero has after attack
 * @see <a href="http://www.link_to_jira/HERO-402">HERO-402</a>
 * @since 1.0
 */
public int successfullyAttacked(int incomingDamage) {
    // do things
    return 0;
}
```

successfullyAttacked方法包含描述和许多独立的块标签。

有许多块标签可以帮助生成正确的文档，我们可以包含各种不同类型的信息。我们甚至可以在注释中使用基本的HTML标签。

让我们回顾一下在上面示例中使用到的标签：

-   @param提供有关方法参数或期望输入的任何有用描述
-   @return提供方法将返回或可以返回的内容的描述
-   @see将生成一个类似于{@link}标签的链接，但更多的是在引用的上下文中而不是内联
-   @since指定类、字段或方法添加到项目的版本
-   @version指定软件的版本，常与%I%和%G%宏一起使用
-   @throws用于进一步解释软件预期异常的情况
-   @deprecated解释了代码被弃用的原因、何时可能被弃用以及替代方案是什么

虽然这两个部分在技术上都是可选的，但我们至少需要一个部分，以便Javadoc工具生成任何有意义的内容。

## 3. Javadoc生成

为了生成我们的Javadoc页面，我们需要看一下JDK附带的命令行工具和Maven插件。

### 3.1 Javadoc命令行工具

Javadoc命令行工具非常强大，但也有一定的复杂性。

在没有任何选项或参数的情况下运行命令javadoc将导致错误并输出它期望的参数。

我们至少需要指定希望为哪个包或类生成文档。

让我们打开命令行并导航到项目目录。

假设类都在项目目录下的src文件夹中：

```shell
user@tuyucheng:~$ javadoc -d doc src\*
```

这将在-d标志指定的名为doc的目录中生成文档。如果存在多个包或文件，我们需要提供所有这些包或文件。

当然，使用具有内置功能的IDE更容易，通常建议使用。

### 3.2 使用Maven插件的Javadoc

我们还可以使用Maven Javadoc插件：

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-javadoc-plugin</artifactId>
			<version>3.0.0</version>
			<configuration>
				<source>1.8</source>
				<target>1.8</target>
			</configuration>
			<tags>
				<!--...-->
			</tags>
		</plugin>
	</plugins>
</build>
```

在项目的基目录中，我们运行命令将Javadoc生成到target\site中的目录：

```shell
user@tuyucheng:~$ mvn javadoc:javadoc
```

[Maven插件](https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-javadoc-plugin/3.5.0)非常强大，可以无缝地生成复杂的文档。

现在让我们看看生成的Javadoc页面是什么样的：

<img src="../assets/img.png">

我们可以看到SuperHero类扩展的类的树视图。我们可以看到我们的描述、字段和方法，我们可以点击链接获取更多信息。

我们方法的详细视图如下所示：

<img src="../assets/img_1.png">

### 3.3 自定义Javadoc标签

除了使用预定义的块标签来格式化我们的文档之外，**我们还可以创建自定义块标签**。

为此，我们只需要以<tag-name\>:<locations-allowed\>:<header\>的格式在Javadoc命令行中包含一个-tag选项。

为了创建一个名为@locationallowedanywhere的自定义标签，它显示在我们生成的文档的“Notable Locations”标题中，我们需要运行：

```shell
user@tuyucheng:~$ javadoc -tag location:a:"Notable Locations:" -d doc src\*
```

为了使用这个标签，我们可以将它添加到Javadoc注释的块部分：

```java
/**
 * This is an example...
 * @location New York
 * @returns blah blah
 */
```

Maven Javadoc插件足够灵活，也允许在我们的pom.xml中定义我们的自定义标签。

为了为我们的项目设置上面相同的标签，我们可以将以下内容添加到插件的<tags\>部分：

```xml
...
<tags>
	<tag>
		<name>location</name>
		<placement>a</placement>
		<head>Notable Places:</head>
	</tag>
</tags>
...
```

这种方式可以让我们一次指定自定义标签，而不是每次都指定。

## 4. 总结

这个快速入门教程介绍了如何编写基本的Javadoc并使用Javadoc命令行生成它们。

生成文档的更简单方法是使用任何内置IDE选项或将Maven插件包含到我们的pom.xml文件中并运行适当的命令。