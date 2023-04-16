## 1. 简介

在本文中，我们将快速介绍如何在Java中使用Asciidoctor。我们将演示如何从AsciiDoc文档生成HTML5或PDF。

## 2. 什么是AsciiDoc？

AsciiDoc是一种文本文档格式。它可用于**编写文档、书籍、网页、手册页和许多其他内容**。

由于它的可配置性很强，AsciiDoc文档可以转换成许多其他格式，如HTML、PDF、手册页、EPUB等。

由于AsciiDoc语法非常基础，因此它已经变得非常流行，在各种浏览器插件、编程语言插件和其他工具中得到了大量支持。

要了解有关该工具的更多信息，我们建议你阅读[官方文档](http://www.methods.co.nz/asciidoc/)，你可以在其中找到许多有用的资源来学习将AsciiDoc文档导出为其他格式的正确语法和方法。

## 3. 什么是Asciidoctor？

**[Asciidoctor](http://asciidoctor.org/)是一个文本处理器，用于将AsciiDoc文档转换为HTML、PDF和其他格式**。它用Ruby编写并打包为RubyGem。

如上所述，AsciiDoc是一种非常流行的文档编写格式，因此你可以很容易地在许多GNU Linux发行版(如Ubuntu、Debian、Fedora和Arch)中找到Asciidoctor作为标准包。

因为我们想在JVM上使用Asciidoctor，所以我们将讨论AsciidoctorJ-它是使用Java的Asciidoctor。

## 4. 依赖

要在我们的应用程序中包含AsciidoctorJ包，需要以下pom.xml条目：

```xml
<dependency>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctorj</artifactId>
    <version>2.5.7</version>
</dependency>
<dependency>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctorj-pdf</artifactId>
    <version>2.3.4</version>
</dependency>
```

可以在[此处](https://central.sonatype.com/artifact/org.asciidoctor/asciidoctorj/2.5.7)和[此处](https://central.sonatype.com/artifact/org.asciidoctor/asciidoctorj-pdf/2.3.4)找到最新版本的库。

## 5. AsciidoctorJ API

AsciidoctorJ的入口点是Asciidoctor Java接口。

这些方法是：

-   convert：从String或Stream解析AsciiDoc文档并将其转换为提供的格式类型
-   convertFile：从提供的File对象解析AsciiDoc文档并将其转换为提供的格式类型
-   convertFiles：与上面的相同，但方法接收多个File对象
-   convertDirectory：解析提供文件夹中的所有AsciiDoc文档并将它们转换为提供的格式类型

### 5.1 API在代码中的使用

要创建Asciidoctor实例，你需要从提供的工厂方法中检索实例：

```java
import static org.asciidoctor.Asciidoctor.Factory.create;
import org.asciidoctor.Asciidoctor;
..
//some code
..
Asciidoctor asciidoctor = create();
```

使用检索到的实例，我们可以很容易地转换AsciiDoc文档：

```java
String output = asciidoctor
    .convert("Hello _Baeldung_!", new HashMap<String, Object>());
```

如果我们想从文件系统转换文本文档，我们将使用convertFile方法：

```java
String output = asciidoctor
    .convertFile(new File("baeldung.adoc"), new HashMap<String, Object>());
```

为了转换多个文件，convertFiles方法接收List对象作为第一个参数并返回String对象数组。更有趣的是如何使用AsciidoctorJ转换整个目录。

如上所述，要转换整个目录-我们应该调用convertDirectory方法。这将扫描提供的路径并搜索所有具有AsciiDoc扩展名(.adoc、.ad、.asciidoc、.asc)的文件并转换它们。要扫描所有文件，应向该方法提供一个DirectoryWalker实例。

目前，Asciidoctor提供了上述接口的两个内置实现：

-   **AsciiDocDirectoryWalker**：转换给定文件夹及其子文件夹的所有文件。忽略所有以“_”开头的文件
-   **GlobDirectoryWalker**：按照glob表达式转换给定文件夹的所有文件

```java
String[] result = asciidoctor.convertDirectory(
    new AsciiDocDirectoryWalker("src/asciidoc"),
    new HashMap<String, Object>());
```

此外，**我们可以使用提供的java.io.Reader和java.io.Writer接口调用convert方法**。Reader接口用作源，Writer接口用于写入转换后的数据：

```java
FileReader reader = new FileReader(new File("sample.adoc"));
StringWriter writer = new StringWriter();
 
asciidoctor.convert(reader, writer, options().asMap());
 
StringBuffer htmlBuffer = writer.getBuffer();
```

### 5.2 PDF生成

**要从Asciidoc文档生成PDF文件，我们需要在选项中指定生成文件的类型**。如果你更仔细地查看前面的示例，你会注意到任何convert方法的第二个参数都是一个Map-代表选项对象。

我们将in_place选项设置为true以便我们的文件自动生成并保存到文件系统：

```java
Map<String, Object> options = options()
    .inPlace(true)
    .backend("pdf")
    .asMap();

String outfile = asciidoctor.convertFile(new File("tuyucheng.adoc"), options);
```

## 6. Maven插件

在上一节中，我们展示了如何使用你自己的Java实现直接生成PDF文件。在本节中，我们将展示如何在Maven构建期间生成PDF文件。**Gradle和Ant存在类似的插件**。

要在构建期间启用PDF生成，你需要将此依赖项添加到pom.xml：

```xml
<plugin>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>2.2.2</version>
    <dependencies>
        <dependency>
            <groupId>org.asciidoctor</groupId>
            <artifactId>asciidoctorj-pdf</artifactId>
            <version>2.3.4</version>
        </dependency>
    </dependencies>
</plugin>
```

可以在[此处](https://central.sonatype.com/artifact/org.asciidoctor/asciidoctor-maven-plugin/2.2.2)找到最新版本的Maven插件依赖项。

### 6.1 用法

要在构建中使用插件，你必须在pom.xml中定义它：

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

由于插件不在任何特定阶段运行，因此你必须设置要启动它的阶段。

与Asciidoctorj插件一样，我们也可以在这里使用各种选项来生成PDF。

让我们快速浏览一下基本选项，同时你可以在[文档](https://github.com/asciidoctor/asciidoctor-maven-plugin)中找到其他选项：

-   **sourceDirectory**：存放Asciidoc文档的目录位置
-   **outputDirectory**：要存储生成的PDF文件的目录位置
-   **backend**：Asciidoctor的输出类型。对于PDF生成集为pdf

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

运行构建后，可以在指定的输出目录中找到PDF文件。

## 7. 总结

尽管AsciiDoc非常易于使用和理解，但它是管理文档和其他文档的非常强大的工具。

在本文中，我们演示了一种从AsciiDoc文档生成HTML和PDF文件的简单方法。