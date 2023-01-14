## 1. 简介

在本文中，我们将快速介绍如何在Java中使用 Asciidoctor。我们将演示如何从 AsciiDoc 文档生成 HTML5 或 PDF。

## 2. 什么是 AsciiDoc？

AsciiDoc 是一种文本文档格式。它可用于编写文档、书籍、网页、手册页和许多其他内容。

由于它的可配置性很强，AsciiDoc 文档可以转换成许多其他格式，如 HTML、PDF、手册页、EPUB 等。

由于 AsciiDoc 语法非常基础，因此在各种浏览器插件、编程语言插件和其他工具中得到了广泛支持，因此变得非常流行。

要了解有关该工具的更多信息，我们建议阅读[官方文档](http://www.methods.co.nz/asciidoc/)，可以在其中找到许多有用的资源来学习将 AsciiDoc 文档导出为其他格式的正确语法和方法。

## 3.什么是Asciidoctor？

[Asciidoctor](http://asciidoctor.org/)是一个文本处理器，用于将 AsciiDoc 文档转换为 HTML、PDF 和其他格式。它用 Ruby 编写并打包为 RubyGem。

如上所述，AsciiDoc 是一种非常流行的文档编写格式，因此可以很容易地在许多 GNU Linux 发行版(如 Ubuntu、Debian、Fedora 和 Arch)中找到 Asciidoctor 作为标准包。

因为我们想在 JVM 上使用 Asciidoctor，所以我们将讨论 AsciidoctorJ——它是带有Java的 Asciidoctor。

## 4.依赖关系

要在我们的应用程序中包含 AsciidoctorJ 包，需要以下pom.xml条目：

```xml
<dependency>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctorj</artifactId>
    <version>1.5.5</version>
</dependency>
<dependency>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctorj-pdf</artifactId>
    <version>1.5.0-alpha.15</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.asciidoctor" AND a%3A"asciidoctorj")和[此处找到最新版本的库。](https://search.maven.org/classic/#search|gav|1|g%3A"org.asciidoctor" AND a%3A"asciidoctorj-pdf")

## 5.AsciidoctorJ API

AsciidoctorJ 的入口点是AsciidoctorJava接口。

这些方法是：

-   convert – 从String或Stream解析 AsciiDoc 文档并将其转换为提供的格式类型
-   convertFile – 从提供的File对象解析 AsciiDoc 文档并将其转换为提供的格式类型
-   convertFiles – 与之前相同，但方法接受多个File对象
-   convertDirectory – 解析提供文件夹中的所有 AsciiDoc 文档并将它们转换为提供的格式类型

### 5.1. API 在代码中的使用

要创建Asciidoctor实例，需要从提供的工厂方法中检索实例：

```java
import static org.asciidoctor.Asciidoctor.Factory.create;
import org.asciidoctor.Asciidoctor;
..
//some code
..
Asciidoctor asciidoctor = create();

```

使用检索实例，我们可以很容易地转换 AsciiDoc 文档：

```java
String output = asciidoctor
  .convert("Hello _Baeldung_!", new HashMap<String, Object>());
```

如果我们想从文件系统转换文本文档，我们将使用convertFile方法：

```java
String output = asciidoctor
  .convertFile(new File("baeldung.adoc"), new HashMap<String, Object>());

```

为了转换多个文件，convertFiles方法接受List对象作为第一个参数并返回String对象数组。
更有趣的是如何使用 AsciidoctorJ 转换整个目录。

如上所述，要转换整个目录——我们应该调用convertDirectory方法。这将扫描提供的路径并搜索所有具有 AsciiDoc 扩展名(.adoc、.ad、.asciidoc、.asc)的文件并转换它们。要扫描所有文件，应向该方法提供一个DirectoryWalker实例。

目前，Asciidoctor 提供了上述接口的两个内置实现：

-   AsciiDocDirectoryWalker – 转换给定文件夹及其子文件夹的所有文件。忽略所有以“_”开头的文件
-   GlobDirectoryWalker – 按照 glob 表达式转换给定文件夹的所有文件

```java
String[] result = asciidoctor.convertDirectory(
  new AsciiDocDirectoryWalker("src/asciidoc"),
  new HashMap<String, Object>());

```

此外，我们可以使用提供的java.io.Reader和java.io.Writer接口调用 convert 方法。 Reader接口用作源，Writer接口用于写入转换后的数据：

```java
FileReader reader = new FileReader(new File("sample.adoc"));
StringWriter writer = new StringWriter();
 
asciidoctor.convert(reader, writer, options().asMap());
 
StringBuffer htmlBuffer = writer.getBuffer();
```

### 5.2. PDF生成

要从 Asciidoc 文档生成 PDF 文件，我们需要在选项中指定生成文件的类型。如果更仔细地查看前面的示例，会注意到任何 convert 方法的第二个参数都是一个Map——代表选项对象。

我们将 in_place 选项设置为 true 以便我们的文件自动生成并保存到文件系统：

```java
Map<String, Object> options = options()
  .inPlace(true)
  .backend("pdf")
  .asMap();

String outfile = asciidoctor.convertFile(new File("baeldung.adoc"), options);
```

## 6. Maven 插件

在上一节中，我们展示了如何使用自己的Java实现直接生成 PDF 文件。在本节中，我们将展示如何在 Maven 构建期间生成 PDF 文件。Gradle 和 Ant 存在类似的插件。

要在构建期间启用 PDF 生成，需要将此依赖项添加到的pom.xml：

```xml
<plugin>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>1.5.5</version>
    <dependencies>
        <dependency>
            <groupId>org.asciidoctor</groupId>
            <artifactId>asciidoctorj-pdf</artifactId>
            <version>1.5.0-alpha.15</version>
        </dependency>
    </dependencies>
</plugin>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|asciidoctor-maven-plugin)找到最新版本的 Maven 插件依赖项。

### 6.1. 用法

要在构建中使用插件，必须在pom.xml 中定义它：

```xml
<plugin>
    <executions>
        <execution>
            <id>output-html</id> 
            <phase>generate-resources</phase> 
            <goals>
                <goal>process-asciidoc</goal> 
            </goals>
        </execution>
    </executions>
</plugin>
```

由于插件不在任何特定阶段运行，因此必须设置要启动它的阶段。

与 Asciidoctorj 插件一样，我们也可以在这里使用各种选项来生成 PDF。

让我们快速浏览一下基本选项，同时可以在[文档](https://github.com/asciidoctor/asciidoctor-maven-plugin)中找到其他选项：

-   sourceDirectory – 存放 Asciidoc 文档的目录位置
-   outputDirectory – 要存储生成的 PDF 文件的目录位置
-   后端——Asciidoctor 的输出类型。对于pdf生成集for pdf

这是一个如何在插件中定义基本选项的示例：

```xml
<plugin>
    <configuration>
        <sourceDirectory>src/main/doc</sourceDirectory>
        <outputDirectory>target/docs</outputDirectory>
        <backend>pdf</backend>
    </configuration>
</plugin>
```

运行构建后，可以在指定的输出目录中找到 PDF 文件。

## 七. 总结

尽管 AsciiDoc 非常易于使用和理解，但它是管理文档和其他文档的非常强大的工具。

在本文中，我们演示了一种从 AsciiDoc 文档生成 HTML 和 PDF 文件的简单方法。