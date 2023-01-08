## 1. 简介

在这个简短的教程中，我们将学习如何使用[Jackson](https://www.baeldung.com/jackson)来读写 YAML 文件。

完成示例结构后，我们将使用[ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial)将 YAML 文件读入Java对象，并将对象写入文件。

## 2.依赖关系

让我们添加 Jackson YAML 数据格式的依赖：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-yaml</artifactId>
    <version>2.13.0</version>
</dependency>
```

[我们总能在Maven Central](https://search.maven.org/search?q=g:com.fasterxml.jackson.dataformat AND a:jackson-dataformat-yaml&core=gav)上找到这个依赖的最新版本。

我们的Java对象使用LocalDate，所以我们还要为 JSR-310 数据类型添加依赖项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.13.0</version>
</dependency>
```

[同样，我们可以在Maven Central](https://search.maven.org/search?q=g:com.fasterxml.jackson.datatype AND a:jackson-datatype-jsr310&core=gav)上查找其最新版本。

## 3.数据和对象结构

解决了我们的依赖关系后，我们现在将转向我们的输入文件和我们将使用的Java类。

让我们首先看一下我们将要读取的文件：

```plaintext
orderNo: A001
date: 2019-04-17
customerName: Customer, Joe
orderLines:
    - item: No. 9 Sprockets
      quantity: 12
      unitPrice: 1.23
    - item: Widget (10mm)
      quantity: 4
      unitPrice: 3.45
```

然后，让我们定义Order类：

```java
public class Order {
    private String orderNo;
    private LocalDate date;
    private String customerName;
    private List<OrderLine> orderLines;

    // Constructors, Getters, Setters and toString
}
```

最后，让我们创建我们的OrderLine类：

```java
public class OrderLine {
    private String item;
    private int quantity;
    private BigDecimal unitPrice;

    // Constructors, Getters, Setters and toString
}
```

## 4.读取YAML

我们将使用 Jackson 的ObjectMapper将我们的 YAML 文件读入一个Order对象，所以让我们现在设置它：

```java
mapper = new ObjectMapper(new YAMLFactory());
```

我们需要使用 findAndRegisterModules方法，以便 Jackson 正确处理我们的日期：

```java
mapper.findAndRegisterModules();
```

一旦我们配置了ObjectMapper，我们只需使用readValue：

```java
Order order = mapper.readValue(new File("src/main/resources/orderInput.yaml"), Order.class);
```

我们会发现我们的Order对象是从文件中填充的，包括OrderLine的列表。

## 5. 编写 YAML

我们还将使用ObjectMapper将Order写入文件。但首先，让我们为其添加一些配置：

```java
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
```

添加该行告诉 Jackson只需将我们的日期写为字符串而不是单个数字部分。

默认情况下，我们的文件将以三个破折号开头。这对 YAML 格式完全有效，但我们可以通过禁用YAMLFactory上的功能来关闭它：

```java
mapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
```

完成额外的设置后，让我们创建一个Order：

```java
List<OrderLine> lines = new ArrayList<>();
lines.add(new OrderLine("Copper Wire (200ft)", 1, 
  new BigDecimal(50.67).setScale(2, RoundingMode.HALF_UP)));
lines.add(new OrderLine("Washers (1/4")", 24, 
  new BigDecimal(.15).setScale(2, RoundingMode.HALF_UP)));
Order order = new Order(
  "B-9910", 
  LocalDate.parse("2019-04-18", DateTimeFormatter.ISO_DATE),
  "Customer, Jane", 
  lines);
```

让我们使用writeValue编写我们的订单：

```java
mapper.writeValue(new File("src/main/resources/orderOutput.yaml"), order);
```

当我们查看 orderOutput.yaml时，它应该类似于：

```plaintext
orderNo: "B-9910"
date: "2019-04-18"
customerName: "Customer, Jane"
orderLines:
- item: "Copper Wire (200ft)"
  quantity: 1
  unitPrice: 50.67
- item: "Washers (1/4")"
  quantity: 24
  unitPrice: 0.15
```

## 六. 总结

在这个快速教程中，我们学习了如何使用 Jackson 库从文件读取和写入 YAML。我们还查看了一些配置项，它们将帮助我们按照我们想要的方式获取数据。