## 1. 简介

在本教程中，我们将快速了解[Univocity Parsers](https://www.univocity.com/pages/univocity_parsers_tutorial.html)，这是一个用于在Java中解析 CSV、TSV 和固定宽度文件的库。

在继续从Javabean 读取和写入文件之前，我们将从读取和写入文件的基础知识开始。然后，我们将在结束之前快速浏览一下配置选项。

## 2.设置

要使用解析器，我们需要将最新的[Maven 依赖](https://search.maven.org/artifact/com.univocity/univocity-parsers)项添加到我们的项目pom.xml文件中：

```xml
<dependency>
    <groupId>com.univocity</groupId>
    <artifactId>univocity-parsers</artifactId>
    <version>2.8.4</version>
</dependency>
```

## 3. 基本用法

### 3.1. 阅读

在 Univocity 中，我们可以快速将整个文件解析为代表文件中每一行的字符串数组集合。

首先，让我们通过为我们的 CSV 文件提供一个Reader到一个具有默认设置的CsvParser来解析一个 CSV 文件：

```java
try (Reader inputReader = new InputStreamReader(new FileInputStream(
  new File("src/test/resources/productList.csv")), "UTF-8")) {
    CsvParser parser = new CsvParser(new CsvParserSettings());
    List<String[]> parsedRows = parser.parseAll(inputReader);
    return parsedRows;
} catch (IOException e) {
    // handle exception
}
```

通过切换到TsvParser并为其提供TSV 文件，我们可以轻松切换此逻辑以解析TSV 文件。

处理固定宽度的文件只是稍微复杂一点。主要区别在于我们需要在解析器设置中提供字段宽度。

让我们通过向FixedWidthParserSettings提供FixedWidthFields对象来读取固定宽度的文件：

```java
try (Reader inputReader = new InputStreamReader(new FileInputStream(
  new File("src/test/resources/productList.txt")), "UTF-8")) {
    FixedWidthFields fieldLengths = new FixedWidthFields(8, 30, 10);
    FixedWidthParserSettings settings = new FixedWidthParserSettings(fieldLengths);

    FixedWidthParser parser = new FixedWidthParser(settings);
    List<String[]> parsedRows = parser.parseAll(inputReader);
    return parsedRows;
} catch (IOException e) {
    // handle exception
}
```

### 3.2. 写作

现在我们已经介绍了使用解析器读取文件，让我们学习如何编写它们。

写入文件与读取文件非常相似，因为我们向与我们的文件类型匹配的解析器提供了一个Writer以及我们所需的设置。

让我们创建一个方法来以所有三种可能的格式写入文件：

```java
public boolean writeData(List<Object[]> products, OutputType outputType, String outputPath) {
    try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(outputPath)),"UTF-8")){
        switch(outputType) {
            case CSV:
                CsvWriter writer = new CsvWriter(outputWriter, new CsvWriterSettings());
                writer.writeRowsAndClose(products);
                break;
            case TSV:
                TsvWriter writer = new TsvWriter(outputWriter, new TsvWriterSettings());
                writer.writeRowsAndClose(products);
                break;
            case FIXED_WIDTH:
                FixedWidthFields fieldLengths = new FixedWidthFields(8, 30, 10);
                FixedWidthWriterSettings settings = new FixedWidthWriterSettings(fieldLengths);
                FixedWidthWriter writer = new FixedWidthWriter(outputWriter, settings);
                writer.writeRowsAndClose(products);
                break;
            default:
                logger.warn("Invalid OutputType: " + outputType);
                return false;
        }
        return true;
    } catch (IOException e) {
        // handle exception
    }
}
```

与读取文件一样，写入 CSV 文件和 TSV 文件几乎相同。对于固定宽度的文件，我们必须为我们的设置提供字段宽度。

### 3.3. 使用行处理器

Univocity 提供了许多我们可以使用的行处理器，还为我们提供了创建自己的行处理器的能力。

为了感受使用行处理器的感觉，让我们使用BatchedColumnProcessor以五行为一组来处理一个更大的 CSV 文件：

```java
try (Reader inputReader = new InputStreamReader(new FileInputStream(new File(relativePath)), "UTF-8")) {
    CsvParserSettings settings = new CsvParserSettings();
    settings.setProcessor(new BatchedColumnProcessor(5) {
        @Override
        public void batchProcessed(int rowsInThisBatch) {}
    });
    CsvParser parser = new CsvParser(settings);
    List<String[]> parsedRows = parser.parseAll(inputReader);
    return parsedRows;
} catch (IOException e) {
    // handle exception
}
```

要使用这个行处理器，我们在CsvParserSettings中定义它，然后我们所要做的就是调用parseAll。

### 3.4. 读取和写入JavaBean

String数组列表没问题，但我们经常使用Javabean 中的数据。Univocity 还允许读取和写入特别注解的Javabean。

让我们使用 Univocity 注解定义一个Product bean：

```java
public class Product {

    @Parsed(field = "product_no")
    private String productNumber;
    
    @Parsed
    private String description;
    
    @Parsed(field = "unit_price")
    private float unitPrice;

    // getters and setters
}
```

主要注解是@Parsed注解。

如果我们的列标题与字段名称匹配，我们可以使用@Parsed而不指定任何值。如果我们的列标题与字段名称不同，我们可以使用字段属性指定列标题。

现在我们已经定义了Product bean，让我们将 CSV 文件读入其中：

```java
try (Reader inputReader = new InputStreamReader(new FileInputStream(
  new File("src/test/resources/productList.csv")), "UTF-8")) {
    BeanListProcessor<Product> rowProcessor = new BeanListProcessor<Product>(Product.class);
    CsvParserSettings settings = new CsvParserSettings();
    settings.setHeaderExtractionEnabled(true);
    settings.setProcessor(rowProcessor);
    CsvParser parser = new CsvParser(settings);
    parser.parse(inputReader);
    return rowProcessor.getBeans();
} catch (IOException e) {
    // handle exception
}
```

我们首先使用带注解的类构建了一个特殊的行处理器BeanListProcessor 。然后，我们将其提供给CsvParserSettings 并使用它来读取Product的列表。

接下来，让我们将Product列表写到一个固定宽度的文件中：

```java
try (Writer outputWriter = new OutputStreamWriter(new FileOutputStream(new File(outputPath)), "UTF-8")) {
    BeanWriterProcessor<Product> rowProcessor = new BeanWriterProcessor<Product>(Product.class);
    FixedWidthFields fieldLengths = new FixedWidthFields(8, 30, 10);
    FixedWidthWriterSettings settings = new FixedWidthWriterSettings(fieldLengths);
    settings.setHeaders("product_no", "description", "unit_price");
    settings.setRowWriterProcessor(rowProcessor);
    FixedWidthWriter writer = new FixedWidthWriter(outputWriter, settings);
    writer.writeHeaders();
    for (Product product : products) {
        writer.processRecord(product);
    }
    writer.close();
    return true;
} catch (IOException e) {
    // handle exception
}
```

显着的区别是我们在设置中指定了列标题。

## 4.设置

Univocity 有许多我们可以应用于解析器的设置。正如我们之前看到的，我们可以使用设置将行处理器应用于解析器。

还有许多其他设置可以更改以满足我们的需要。虽然许多配置在三种文件类型中是通用的，但每个解析器也有特定于格式的设置。

让我们调整我们的 CSV 解析器设置以对我们正在读取的数据施加一些限制：

```java
CsvParserSettings settings = new CsvParserSettings();
settings.setMaxCharsPerColumn(100);
settings.setMaxColumns(50);
CsvParser parser = new CsvParser(new CsvParserSettings());
```

## 5.总结

在本快速教程中，我们学习了使用 Univocity 库解析文件的基础知识。

我们学习了如何将文件读写到字符串数组和Javabean 的列表中。在进入Javabean 之前，我们快速了解了使用不同的行处理器。最后，我们简要介绍了如何自定义设置。