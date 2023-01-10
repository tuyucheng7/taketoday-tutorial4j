## 1. 概述

Yet Another Report Generator (YARG) 是一个开源的Java报告库，由 Haulmont 开发。它允许以最常见的格式(.doc、.docs、.xls、.xlsx、.html、.ftl、.csv)或自定义文本格式创建模板，并用 SQL、Groovy 或 JSON 加载的数据填充它。

在本文中，我们将演示如何使用 Spring @RestController输出带有 JSON 加载数据的.docx文档。

## 2.设置示例

为了开始使用 YARG，我们需要将以下依赖项添加到我们的pom：

```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-cuba-platform-main</id>
        <name>bintray</name>
        <url>http://dl.bintray.com/cuba-platform/main</url>
    </repository>
</repositories>
...
<dependency> 
    <groupId>com.haulmont.yarg</groupId> 
    <artifactId>yarg</artifactId> 
    <version>2.0.4</version> 
</dependency>
```

接下来，我们需要一个数据模板；我们将使用一个简单的Letter.docx：

```plaintext
${Main.title}

Hello ${Main.name},

${Main.content}

```

请注意 YARG 如何使用标记/模板语言——它允许在不同的部分插入内容。这些部分根据它们所属的数据组进行划分。

在此示例中，我们有一个“Main”组，其中包含信件的标题、名称和内容。

这些组在 YARG 中称为ReportBand，它们对于分离可能拥有的不同类型的数据非常有用。

## 3. 将 Spring 与 YARG 集成

使用报告生成器的最佳方式之一是创建一个可以为我们返回文档的服务。

因此，我们将使用 Spring 并实现一个简单的@RestController，它将负责读取模板、获取 JSON、将其加载到文档中并返回格式化的.docx。

首先，让我们创建一个DocumentController：

```java
@RestController
public class DocumentController {

    @GetMapping("/generate/doc")
    public void generateDocument(HttpServletResponse response)
      throws IOException {
    }
}

```

这会将文档的创建公开为一项服务。

现在我们将为模板添加加载逻辑：

```java
ReportBuilder reportBuilder = new ReportBuilder();
ReportTemplateBuilder reportTemplateBuilder = new ReportTemplateBuilder()
  .documentPath("./src/main/resources/Letter.docx")
  .documentName("Letter.docx")
  .outputType(ReportOutputType.docx)
  .readFileFromPath();
reportBuilder.template(reportTemplateBuilder.build());

```

ReportBuilder类将负责创建报告，将模板和数据分组。ReportTemplateBuilder加载我们之前定义的Letter。docx 模板，方法是指定文档的路径、名称和输出类型。

然后我们将加载的模板添加到报告生成器。

现在我们需要定义要插入到文档中的数据，这将是一个包含以下内容的Data.json文件：

```javascript
{
    "main": {
        "title" : "INTRODUCTION TO YARG",
        "name" : "Baeldung",
        "content" : "This is the content of the letter, can be anything we like."
    }
}
```

这是一个简单的 JSON 结构，带有一个“主要”对象，其中包含我们的模板所需的标题、名称和内容。

现在，让我们继续将数据加载到我们的ReportBuilder中：

```java
BandBuilder bandBuilder = new BandBuilder();
String json = FileUtils.readFileToString(
  new File("./src/main/resources/Data.json"));
ReportBand main = bandBuilder.name("Main")
  .query("Main", "parameter=param1 $.main", "json")
  .build();
reportBuilder.band(main);
Report report = reportBuilder.build();
```

这里我们定义一个BandBuilder以创建一个ReportBand，这是 YARG 用于我们之前在模板文档中定义的数据组的抽象。

我们可以看到我们定义了与“Main”完全相同的部分的名称，然后我们使用查询方法找到“Main”部分并声明一个参数，该参数将用于查找填充模板所需的数据。

重要的是要注意 YARG 使用[JsonPath](https://github.com/json-path/JsonPath)遍历 JSON，这就是我们看到这种“$.main”语法的原因。

接下来，让我们在查询中指定数据格式为“json”，然后将 band 添加到报表中，最后构建它。

最后一步是定义Reporting对象，它负责将数据插入模板并生成最终文档：

```java
Reporting reporting = new Reporting();
reporting.setFormatterFactory(new DefaultFormatterFactory());
reporting.setLoaderFactory(
  new DefaultLoaderFactory().setJsonDataLoader(new JsonDataLoader()));
response.setContentType(
 "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
reporting.runReport(
  new RunParams(report).param("param1", json),
  response.getOutputStream());
```

我们使用支持文章开头列出的常见格式的DefaultFormatterFactory 。之后，我们设置负责解析JSON的JsonDataLoader 。

在最后一步，我们为 .docx 格式设置适当的内容类型并运行报告。这将连接 JSON 数据并将其插入到模板中，将结果输出到响应输出流中。

现在我们可以访问/generate/doc URL 来下载文档，我们将在生成的 .docx 中看到以下结果：

[![雅格简介](https://www.baeldung.com/wp-content/uploads/2017/09/doc-300x121.png)](https://www.baeldung.com/wp-content/uploads/2017/09/doc.png)

## 4. 总结

在本文中，我们展示了如何轻松地将 YARG 与 Spring 集成，并使用其强大的 API 以简单的方式创建文档。

我们使用 JSON 作为数据输入，但也支持 Groovy 和 SQL。

如果想了解更多信息，可以在[此处](https://github.com/cuba-platform/yarg)找到文档。