## 1. 概述

在本教程中，我们将通过一个快速实用的示例学习如何使用[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)作为模板引擎生成 PDF。

## 2.Maven依赖

首先，让我们添加[Thymeleaf](https://search.maven.org/artifact/org.thymeleaf/thymeleaf)依赖项：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
```

Thymeleaf 本身只是一个模板引擎，它不能自己生成 PDF。为此，我们要将[flying-saucer-pdf](https://search.maven.org/artifact/org.xhtmlrenderer/flying-saucer-pdf)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.xhtmlrenderer</groupId>
    <artifactId>flying-saucer-pdf</artifactId>
    <version>9.1.20</version>
</dependency>
```

## 3.生成PDF

接下来，让我们创建一个简单的 Thymeleaf HTML 模板——thymeleaf_template.html：

```html
<html xmlns:th="http://www.thymeleaf.org">
  <body>
    <h3 style="text-align: center; color: green">
      <span th:text="'Welcome to ' + ${to} + '!'"></span>
    </h3>
  </body>
</html>
```

然后，我们将创建一个简单的函数——parseThymeleafTemplate——它将解析我们的模板并返回一个 HTML字符串：

```java
private String parseThymeleafTemplate() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);

    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    Context context = new Context();
    context.setVariable("to", "Baeldung");

    return templateEngine.process("thymeleaf_template", context);
}
```

最后，让我们实现一个简单的函数，接收先前生成的 HTML 作为输入，并将 PDF 写入我们的主文件夹：

```java
public void generatePdfFromHtml(String html) {
    String outputFolder = System.getProperty("user.home") + File.separator + "thymeleaf.pdf";
    OutputStream outputStream = new FileOutputStream(outputFolder);

    ITextRenderer renderer = new ITextRenderer();
    renderer.setDocumentFromString(html);
    renderer.layout();
    renderer.createPDF(outputStream);

    outputStream.close();
}
```

运行代码后，我们会注意到用户主目录中有一个名为thymeleaf.pdf的文件，如下所示：[![百隆pdf](https://www.baeldung.com/wp-content/uploads/2020/05/baeldung_pdf.png)](https://www.baeldung.com/wp-content/uploads/2020/05/baeldung_pdf.png)

正如我们所看到的，文本是绿色的并且与我们的内联 CSS 中定义的中心对齐。这是用于自定义我们的 PDF 的极其强大的工具。

我们应该记住，Thymeleaf 与 Flying Saucer 完全分离，这意味着我们可以使用任何其他模板引擎来创建 PDF，例如[Apache FreeMarker](https://www.baeldung.com/freemarker-operations)。

## 4. 总结

在本快速教程中，我们学习了如何使用 Thymeleaf 作为模板引擎轻松生成 PDF。