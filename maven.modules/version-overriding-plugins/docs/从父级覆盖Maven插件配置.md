## 1. 概述

在[Maven多模块](https://www.baeldung.com/maven-multi-module)项目中，**effective POM是合并模块及其父模块中定义的所有配置的结果**。

为了避免模块之间的冗余和重复，我们经常将公共配置保存在共享父级中。但是，如果我们需要在不影响其所有同级模块的情况下为子模块进行自定义配置，则可能会遇到挑战。

在本教程中，我们将学习**如何覆盖父插件配置**。

## 2. 默认配置继承

[插件](https://maven.apache.org/guides/introduction/introduction-to-plugins.html)配置允许我们跨项目重用通用的构建逻辑，如果父级有一个插件，则子级将自动拥有它，而无需进行其他配置，这就像Java中的继承一样。

为了实现这一点，**Maven在元素级别合并XML文件**，如果子级定义了一个具有不同值的元素，它将替换父级中的元素。让我们看看实际效果。

### 2.1 项目结构

首先，让我们定义一个多模块Maven项目来进行实验，我们的项目将由一个parent和两个child组成：

```bash
+ parent
     + child-a
     + child-b
```

假设我们要配置maven-compiler-plugin以在模块之间使用不同的Java版本，让我们将我们的项目配置为一般使用Java 11，但让子模块使用Java 8。

我们将从父配置开始：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>11</source>
        <target>11</target>
        <maxmem>512m</maxmem>
    </configuration>
</plugin>
```

这里我们指定了一个额外的属性maxmem，我们也想使用它。但是，我们希望child-a有自己的编译器设置。

因此，让我们将child-a配置为使用Java 8：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
    </configuration>
</plugin>
```

现在我们有了我们的示例，让我们看看这对effective POM做了什么。

### 2.2 了解effective POM

effective POM受多种因素影响，如继承、配置文件、外部设置等。为了查看实际的POM，让我们从child-a目录运行mvn help:effective-pom：

```bash
mvn help:effective-pom

...
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <maxmem>512m</maxmem>
    </configuration>
</plugin>
```

正如预期的那样，child-a有自己的source值和target值变体。然而，另一个惊喜是它还具有来自其父级的maxmem属性。

这意味着**如果定义了任何子级属性，它将获胜，否则将使用父级属性**。

## 3. 高级配置继承

当我们想要微调合并策略时，我们可以使用属性，这些属性放在我们要控制的XML元素上。此外，它们将被继承并且只会影响第一级子级。

### 3.1 使用列表

在前面的示例中，我们看到了如果子模块具有不同的值会发生什么；现在，我们将看到子模块具有不同元素列表的情况。作为一个例子，让我们看一下使用[maven-resources-plugin](https://www.baeldung.com/maven-resources-plugin)包含多个资源目录。

作为基线，让我们将父级配置为包含parent-resources目录中的资源：

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <configuration>
        <resources>
            <resource>
                <directory>parent-resources</directory>
            </resource>
        </resources>
    </configuration>
</plugin>
```

此时，child-a将从其父级继承此插件配置。但是，假设我们想为child-a定义一个额外的资源目录：

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <configuration>
        <resources>
            <resource>
                <directory>child-a-resources</directory>
            </resource>
        </resources>
    </configuration>
</plugin>
```

现在，让我们回顾一下effective POM：

```bash
mvn help:effective-pom
...
<configuration>
    <resources>
        <resource>
            <directory>child-a-resources</directory>
        </resource>
    </resources>
</configuration>
```

在这种情况下，**整个列表被子配置覆盖**。

### 3.2 附加父配置

也许我们希望一些子级使用一个公共资源目录并定义其他资源目录，为此，**我们可以通过在父级的resources元素上使用combine.children="append"来附加父级配置**：

```xml
<resources combine.children="append">
    <resource>
        <directory>parent-resources</directory>
    </resource>
</resources>
```

因此，effective POM将同时包含它们：

```bash
mvn help:effective-pom
....
<resources combine.children="append">
    <resource>
        <directory>parent-resources</directory>
    </resource>
    <resource>
        <directory>child-a-resources</directory>
    </resource>
</resources>
```

combine属性不会传播到任何嵌套元素。因此，如果resources部分是一个复杂的结构，则嵌套元素将使用默认策略进行合并。

### 3.3 覆盖子配置

在前面的示例中，由于父级组合策略，子级并不完全控制最终的POM。**子级可以通过在其resources元素上添加combine.self="override"来否决父级**：

```xml
<resources combine.self="override">
    <resource>
        <directory>child-a-resources</directory>
    </resource>
</resources>
```

在这种情况下，子级重新获得控制权：

```bash
mvn help:effective-pom
...
<resources combine.self="override">
    <resource>
        <directory>child-a-resources</directory>
    </resource>
</resources>
```

## 4. 不可继承的插件

前面的属性适合微调，但当子级完全不同意其父级时，它们就不合适了。

**为了完全避免插件被继承，我们可以在父级别添加属性<inherited\>false</inherited\>**：

```xml
<plugin>
    <inherited>false</inherited>
    <groupId>org.apache.maven.plugins</groupId>
    ...
</plugin>
```

这意味着该插件将仅应用于父级，而不会传播到其子级。

## 5. 总结

在本文中，我们看到了如何覆盖父插件配置。

首先，我们探讨了默认行为。然后我们看到了父级如何定义合并策略以及子级如何拒绝它。最后，我们了解了如何将插件标记为不可继承的，以避免所有的子级都必须覆盖它。