## 1. 简介

在这篇简短的文章中，我们将演示如何从 AsciiDoc 文档生成一本书，以及如何使用各种样式选项自定义的书。

如果不熟悉Java中的 AsciiDoc，可以阅读我们[的 AsciiDoctor 简介](https://www.baeldung.com/asciidoctor)。

## 2.后端书籍类型

使用 AsciiDoctorj 生成书籍的最简单方法是像前面提到的文章中那样使用 Maven。唯一的区别是必须指定doctype标签并将其设置为“book”。

```xml
<backend>pdf</backend>
<doctype>book</doctype>
```

有了定义的文档类型，AsciiDoctorj 知道你想要构建一本书，所以它创建：

-   扉页
-   目录
-   正文内容的第一页
-   部分和章节

要获得提到的部分，Asciidoc 文档应该定义书名、章节和其他正常的部分。

## 3.定义自定义样式

在写书时，我们很自然地想要使用一些自定义样式。可以使用简单 YAML 文件中定义的 AsciiDoc 特定格式化语言来执行此操作。

例如，这段代码将定义书中每一页的外观。我们想要在纵向模式下，在 A4 纸张格式上顶部和底部留出 0.75 英寸的边距，两侧留出 1 英寸的边距：

```plaintext
page:
    layout: portrait
    margin: [0.75in, 1in, 0.75in, 1in]
    size: A4
```

此外，我们可以为书籍的页脚和页眉定义自定义样式：

```plaintext
header:
  height: 0.5in
  line_height: 1
  recto_content:
    center: '{document-title}'
  verso_content:
    center: '{document-title}'

footer:
  height: 0.5in
  line_height: 1
  recto_content:
    right: '{chapter-title} | {page-number}'
  verso_content:
    left: '{page-number} | {chapter-title}
```

更多格式化选项可以在[AsciiDoctorj 的 Github 页面上找到](https://github.com/asciidoctor/asciidoctor-pdf/blob/master/docs/theming-guide.adoc)。

要在图书生成过程中包含自定义主题，我们必须定义样式文件所在的路径。该位置在pom.xml 的属性部分中指定：

```plaintext
<pdf-stylesdir>${project.basedir}/src/themes</pdf-stylesdir>
<pdf-style>custom</pdf-style>
```

第一行定义了定义样式的路径，第二行指定了不带扩展名的文件名。

通过这些更改，我们的pom.xml如下所示：

```xml
<configuration>
    <sourceDirectory>src/docs/asciidoc</sourceDirectory>
    <outputDirectory>target/docs/asciidoc</outputDirectory>
    <attributes>
        <pdf-stylesdir>${project.basedir}/src/themes</pdf-stylesdir>
        <pdf-style>custom</pdf-style>
    </attributes>
    <backend>pdf</backend>
    <doctype>book</doctype>
</configuration>
```

## 4.生成书

要生成你的书，你只需要在项目目录中运行 Maven，生成的书可以在target/docs/asciidoctor/目录中找到。

## 5.总结

在本教程中，我们向展示了如何使用 Maven 生成装饰简单的书籍。