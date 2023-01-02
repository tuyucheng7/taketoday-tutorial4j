## 1. 概述

在本教程中，我们将学习如何在[Maven](https://www.baeldung.com/maven)中设置字符编码。

我们将展示如何为一些常见的Maven插件设置编码。

此外，我们还将了解如何在项目级别以及通过命令行设置编码。

## 2. 什么是编码，我们为什么要关心？

世界上有许多不同的语言使用不同的字符。

一种称为Unicode的映射字符系统拥有超过100,000个字符、符号，甚至表情符号(emoji)。

为了不使用大量内存，**我们使用称为**[编码](https://www.baeldung.com/java-char-encoding)**的映射系统在位和字节之间转换字符，并在屏幕上转换为人类可读的字符**。

现在有很多编码系统，**为了读取文件，我们必须知道使用的是哪种编码系统**。

### 2.1 如果我们不在Maven中声明编码会发生什么？

Maven认为编码足够重要，如果我们不声明编码，那么它将注销警告。

事实上，这个警告占据了Apache Maven站点上[FAQ](https://maven.apache.org/general.html#encoding-warning)页面的头号位置。

要看到这个警告，让我们在构建中添加几个插件。

首先，让我们添加[maven-resources-plugin](https://maven.apache.org/plugins/maven-resources-plugin/)，它将资源复制到输出目录中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.2.0</version>
</plugin>
```

我们还想编译我们的代码文件，所以让我们添加[maven-compiler-plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
</plugin>
```

当我们在一个多模块项目中工作时，父POM可能已经为我们设置了编码。出于演示目的，让我们通过覆盖编码属性来清除编码属性(别担心，我们稍后会回到这个问题)：

```xml
<properties>
    <project.build.sourceEncoding></project.build.sourceEncoding>
</properties>
```

让我们使用标准Maven命令运行插件：

```bash
mvn clean install
```

像这样取消设置我们的编码可能会破坏构建！我们将在日志中看到收到以下警告：

```bash
[INFO] --- maven-resources-plugin:3.2.0:resources (default-resources) @ maven-properties ---
  [WARNING] Using platform encoding (Cp1252 actually) to copy filtered resources, i.e. build is platform dependent!
```

警告指出，**如果未指定编码系统，Maven将使用平台默认值**。 

通常在Windows上，默认值为[Windows-1252](https://en.wikipedia.org/wiki/Windows-1252)(也称为CP-1252或Cp1252)。

此默认值可能会根据本地环境而改变，我们将在下面看到如何从我们的构建中删除这种平台依赖性。

### 2.2 如果我们在Maven中声明一个错误的编码会发生什么？

Maven是一个需要能够读取源文件的构建工具。

为了读取源文件，**必须将Maven设置为使用与源文件编码相同的编码**。

Maven还生成通常分发到另一台计算机的文件。因此，使用预期的编码编写入输出文件非常重要，**不在预期编码中的输出文件可能无法在不同的系统上读取**。

为了说明这一点，让我们添加一个使用非ASCII字符的简单Java类：

```java
public class NonAsciiString {

    public static String getNonAsciiString() {

        String nonAsciiŞŧř = "ÜÝÞßàæç";
        return nonAsciiŞŧř;
    }
}
```

在我们的POM中，让我们将构建设置为使用[ASCII](https://en.wikipedia.org/wiki/ASCII)编码：

```xml
<properties>
    <project.build.sourceEncoding>US-ASCII</project.build.sourceEncoding>
</properties>
```

使用`mvn clean install`运行它，我们看到我们得到了许多形式的构建错误：

```bash
[ERROR] /taketoday-tutorials4j/maven-modules/maven-properties/src/main/java/
cn/tuyucheng/taketoday/maven/properties/NonAsciiString.java:[15,31] unmappable character (0xC3) for encoding US-ASCII
```

我们之所以看到这一点，是因为我们的文件包含非ASCII字符，因此无法通过ASCII编码读取它们。

在可能的情况下，保持简单并避免使用非ASCII字符是个好主意。

在下一节中，我们将看到将Maven设置为使用[UTF-8](https://en.wikipedia.org/wiki/UTF-8)编码以避免任何问题也是一个好主意。

## 3. 我们如何在Maven配置中设置编码？

首先，让我们看看如何在插件级别设置编码。

然后我们将看到我们可以设置项目范围的属性，这意味着我们不需要在每个插件中声明编码。

### 3.1 我们如何在Maven插件中设置编码参数？

**大多数插件都带有一个encoding参数**，这使得这非常简单。

我们需要在maven-resources-plugin和maven-compiler-plugin中设置编码，我们可以简单地将encoding参数添加到我们的每个Maven插件中：

```xml
<configuration>
    <encoding>UTF-8</encoding>
</configuration>
```

让我们使用`mvn clean install`运行此代码并查看日志记录：

```bash
[INFO] --- maven-resources-plugin:3.2.0:resources (default-resources) @ maven-properties ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
```

我们可以看到该插件现在使用的是UTF-8，并且我们已经解决了上面的警告。

### 3.2 我们如何在Maven构建中设置项目范围的编码参数？

记住为我们声明的每个插件设置编码是非常麻烦的。

值得庆幸的是，大多数Maven插件使用相同的全局Maven属性作为其编码参数的默认值。

正如我们之前看到的，让我们从插件中删除encoding参数，而是设置：

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

运行我们的构建会产生与我们在上面看到的相同的UTF-8日志记录行。

在多模块项目中，我们通常会在父POM中设置此属性。

**此属性将被设置的任何特定于插件的属性覆盖**。

重要的是要记住，插件没有义务使用这个属性。例如，早期版本(<2.2)的maven-war-plugin会忽略此属性。

### 3.3 我们如何为报告插件设置项目范围的编码参数？

也许令人惊讶的是，**我们必须设置两个属性以保证我们为所有情况设置了项目范围的编码**。

为了说明这一点，我们将使用[properties-maven-plugin](https://www.mojohaus.org/properties-maven-plugin/)：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>properties-maven-plugin</artifactId>
    <version>1.1.0</version>
</plugin>
```

我们还将一个新的系统范围的属性设置为空：

```xml
<project.reporting.outputEncoding></project.reporting.outputEncoding>
```

如果我们现在运行`mvn clean install`，我们的构建将失败并记录：

```bash
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-pmd-plugin:3.13.0:pmd (pmd) on project maven-properties: Execution pmd of goal 
  org.apache.maven.plugins:maven-pmd-plugin:3.13.0:pmd failed: org.apache.maven.reporting.MavenReportException: : UnsupportedEncodingException -> [Help 1]
```

即使我们已经设置了project.build.sourceEncoding，这个插件也使用了不同的属性。要理解为什么会这样，我们必须了解[Maven构建配置和Maven报告配置之间的区别](https://www.baeldung.com/maven-plugin-management#plugin-configuration)。

**插件可以在构建过程或报告过程中使用，后者使用单独的属性键**。

这意味着仅仅设置project.build.sourceEncoding是不够的，我们还需要为报告过程添加以下属性：

```xml
<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
```

**建议在项目范围内设置这两个属性**。

### 3.4 我们如何在命令行上设置Maven编码？

**我们能够通过命令行参数设置属性，而无需向POM文件添加任何配置**。我们可能会这样做，因为我们没有对pom.xml文件的写入访问权限。

让我们运行以下命令来指定构建应该使用的编码：

```bash
mvn clean install -Dproject.build.sourceEncoding=UTF-8 -Dproject.reporting.outputEncoding=UTF-8
```

**命令行参数覆盖任何现有配置**。

因此，即使我们删除pom.xml文件中设置的任何编码属性，这也可以让我们成功运行构建。

## 4. 在同一个Maven项目中使用多种类型的编码

在整个项目中使用单一类型的编码是个好主意。

但是，我们可能被迫在同一构建中处理多种类型的编码。例如，我们的资源文件可能具有不同的编码系统，这可能是我们无法控制的。

我们有办法做到这一点吗？嗯，这取决于情况。

我们看到我们可以在逐个插件的基础上设置编码参数，因此，如果我们需要CP-1252格式的代码，但想以UTF-8格式输出测试结果，那么我们可以做到这一点。

我们甚至可以通过使用不同的<execution\>在同一个插件中使用多种类型的编码。

特别是，我们之前看到的maven-resources-plugin内置了额外的功能。

我们之前看到了encoding参数，该插件还提供了一个propertiesEncoding参数，以允许以不同于其他资源的方式对属性文件进行编码：

```xml
<configuration>
    <encoding>UTF-8</encoding>
    <propertiesEncoding>ISO-8859-1</propertiesEncoding>
</configuration>
```

当使用`mvn clean install`运行构建时，这会给出：

```bash
[INFO] --- maven-resources-plugin:3.2.0:resources (default-resources) @ maven-properties ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Using 'ISO-8859-1' encoding to copy filtered properties files.
```

在研究插件如何使用编码时，总是值得参考[maven.apache.org](https://maven.apache.org/)上的技术文档。

## 5. 总结

在本文中，我们看到**声明编码有助于确保代码在任何环境中都以相同的方式构建**。

我们看到我们可以在插件级别设置编码参数。

然后，我们了解到**可以在项目级别设置两个属性**，它们是project.build.sourceEncoding和project.reporting.outputEncoding。

我们还看到可以通过命令行传递编码，这使我们无需编辑Maven POM文件即可设置编码类型。

最后，我们研究了如何在同一个项目中使用多种类型的编码。