## 1. 简介

在这个简短的教程中，我们将了解如何使用 Jackson 将 JSON 转换为 CSV，反之亦然。

有可用的替代库，例如[org.json 中的 CDL 类](https://www.baeldung.com/java-org-json#cdl)，但我们在这里只关注 Jackson 库。

在查看示例数据结构后，我们将结合使用[ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial)和 CSVMapper 在 JSON 和 CSV 之间进行转换。

## 2.依赖关系

让我们添加对 Jackson CSV 数据格式化程序的依赖：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-csv</artifactId>
    <version>2.13.0</version>
</dependency>
```

[我们总能在Maven Central](https://search.maven.org/search?q=g:com.fasterxml.jackson.dataformat AND a:jackson-dataformat-csv&core=gav)上找到这个依赖的最新版本。

我们还将为核心 Jackson 数据绑定添加依赖项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

[同样，我们可以在Maven Central](https://search.maven.org/search?q=g:com.fasterxml.jackson.core AND a:jackson-databind&core=gav)上找到此依赖项的最新版本。

## 3. 数据结构

在我们将 JSON 文档重新格式化为 CSV 之前，我们需要考虑我们的数据模型在两种格式之间的映射效果如何。

首先，让我们考虑一下不同格式支持的数据：

-   我们使用 JSON 来表示各种对象结构，包括包含数组和嵌套对象的对象结构
-   我们使用 CSV 来表示对象列表中的数据，列表中的每个对象都出现在新行中

这意味着如果我们的 JSON 文档有一个对象数组，我们可以将每个对象重新格式化为我们的 CSV 文件的新行。因此，作为示例，让我们使用一个包含以下订单项目列表的 JSON 文档：

```javascript
[ {
  "item" : "No. 9 Sprockets",
  "quantity" : 12,
  "unitPrice" : 1.23
}, {
  "item" : "Widget (10mm)",
  "quantity" : 4,
  "unitPrice" : 3.45
} ]
```

我们将使用 JSON 文档中的字段名称作为列标题，并将其重新格式化为以下 CSV 文件：

```plaintext
item,quantity,unitPrice
"No. 9 Sprockets",12,1.23
"Widget (10mm)",4,3.45
```

## 4.读取JSON并写入CSV

首先，我们使用 Jackson 的ObjectMapper将示例 JSON 文档读入JsonNode对象树中：

```java
JsonNode jsonTree = new ObjectMapper().readTree(new File("src/main/resources/orderLines.json"));
```

接下来，让我们创建一个CsvSchema。这决定了 CSV 文件中的列标题、类型和列顺序。为此，我们创建一个CsvSchema Builder并设置列标题以匹配 JSON 字段名称：

```java
Builder csvSchemaBuilder = CsvSchema.builder();
JsonNode firstObject = jsonTree.elements().next();
firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
```

然后，我们使用 CsvSchema 创建一个CsvMapper ，最后，我们将jsonTree写入我们的 CSV 文件：

```java
CsvMapper csvMapper = new CsvMapper();
csvMapper.writerFor(JsonNode.class)
  .with(csvSchema)
  .writeValue(new File("src/main/resources/orderLines.csv"), jsonTree);
```

当我们运行此示例代码时，我们的示例 JSON 文档将转换为预期的 CSV 文件。

## 5.读取CSV并写入JSON

现在，让我们使用 Jackson 的CsvMapper将我们的 CSV 文件读入OrderLine对象列表。为此，我们首先将OrderLine类创建为一个简单的 POJO：

```java
public class OrderLine {
    private String item;
    private int quantity;
    private BigDecimal unitPrice;
 
    // Constructors, Getters, Setters and toString
}
```

我们将使用 CSV 文件中的列标题来定义我们的CsvSchema。然后，我们使用CsvMapper将数据从 CSV 读取到OrderLine对象的MappingIterator中：

```java
CsvSchema orderLineSchema = CsvSchema.emptySchema().withHeader();
CsvMapper csvMapper = new CsvMapper();
MappingIterator<OrderLine> orderLines = csvMapper.readerFor(OrderLine.class)
  .with(orderLineSchema)
  .readValues(new File("src/main/resources/orderLines.csv"));
```

接下来，我们将使用MappingIterator来获取OrderLine对象的列表。然后，我们使用 Jackson 的ObjectMapper将列表写成 JSON 文档：

```java
new ObjectMapper()
  .configure(SerializationFeature.INDENT_OUTPUT, true)
  .writeValue(new File("src/main/resources/orderLinesFromCsv.json"), orderLines.readAll());
```

当我们运行此示例代码时，我们的示例 CSV 文件将转换为预期的 JSON 文档。

## 6.配置CSV文件格式

让我们使用 Jackson 的一些注解来调整 CSV 文件的格式。我们将把'item'列标题更改为'name'，将'quantity'列标题更改为'count'，删除'unitPrice'列，并将'count'作为第一列。

因此，我们预期的 CSV 文件变为：

```plaintext
count,name
12,"No. 9 Sprockets"
4,"Widget (10mm)"
```

我们将创建一个新的抽象类来定义 CSV 文件所需的格式：

```java
@JsonPropertyOrder({
    "count",
    "name"
})
public abstract class OrderLineForCsv {
    
    @JsonProperty("name")
    private String item; 
    
    @JsonProperty("count")
    private int quantity; 
    
    @JsonIgnore
    private BigDecimal unitPrice;

}
```

然后，我们使用我们的OrderLineForCsv类来创建一个CsvSchema：

```java
CsvMapper csvMapper = new CsvMapper();
CsvSchema csvSchema = csvMapper
  .schemaFor(OrderLineForCsv.class)
  .withHeader(); 

```

我们还将OrderLineForCsv用作 Jackson Mixin。这告诉 Jackson 在处理OrderLine对象时使用我们添加到OrderLineForCsv类的注解：

```java
csvMapper.addMixIn(OrderLine.class, OrderLineForCsv.class); 

```

最后，我们使用ObjectMapper将 JSON 文档读入OrderLine数组，并使用我们的csvMapper将其写入 CSV 文件：

```java
OrderLine[] orderLines = new ObjectMapper()
    .readValue(new File("src/main/resources/orderLines.json"), OrderLine[].class);
    
csvMapper.writerFor(OrderLine[].class)
    .with(csvSchema)
    .writeValue(new File("src/main/resources/orderLinesReformated.csv"), orderLines);

```

当我们运行此示例代码时，我们的示例 JSON 文档将转换为预期的 CSV 文件。

## 七. 总结

在本快速教程中，我们学习了如何使用 Jackson 数据格式库读写 CSV 文件。我们还查看了一些配置选项，这些选项可以帮助我们按照我们想要的方式获取数据。