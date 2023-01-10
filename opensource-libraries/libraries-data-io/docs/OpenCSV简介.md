## 1. 简介

在本快速教程中，我们将介绍 OpenCSV 4，这是一个用于编写、读取、序列化、反序列化和/或解析.csv文件的出色库。然后我们将通过几个示例演示如何设置和使用 OpenCSV 4 来完成我们的工作。

## 2. 设置

首先，我们将通过pom.xml依赖项将 OpenCSV 添加到我们的项目中：

```xml
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>4.1</version>
</dependency>

```

OpenCSV的.jars可以在[官方站点找到，也可以通过在](http://opencsv.sourceforge.net/)[Maven Repository](https://mvnrepository.com/artifact/com.opencsv/opencsv)快速搜索找到。

我们的.csv文件非常简单；我们将其保留为两列和四行：

```plaintext
colA,colB
A,B
C,D
G,G
G,F
```

## 3. 吃豆子还是不吃豆子

将 OpenCSV 添加到我们的pom.xml后，我们可以通过两种方便的方式实现 CSV 处理方法：

1.  使用方便的CSVReader和CSVWriter对象(为了简化操作)
2.  使用CsvToBean将.csv文件转换为 beans(作为带注解的 plain-old-java-objects实现)

我们将在本文中坚持使用同步(或阻塞)示例，因此我们可以专注于基础知识。

请记住，同步方法将阻止周围或后续代码在完成之前执行。生产环境可能会使用异步(或非阻塞)方法，这些方法将允许其他进程或方法在异步方法完成时完成。

我们将在以后的文章中深入探讨 OpenCSV 的异步示例。

### 3.1. CSV阅读器

让我们通过提供的readAll()和readNext()方法探索CSVReader。我们将看看如何同步使用readAll ()：

```java
public List<String[]> readAllLines(Path filePath) throws Exception {
    try (Reader reader = Files.newBufferedReader(filePath)) {
        try (CSVReader csvReader = new CSVReader(reader)) {
            return csvReader.readAll();
        }
    }
}
```

然后我们可以通过传入文件Path来调用该方法：

```java
public List<String[]> readAllExample() throws Exception {
    Path path = Paths.get(
      ClassLoader.getSystemResource("csv/twoColumn.csv").toURI())
    );
    return CsvReaderExamples.readAllLines(path);
}
```

同样，我们可以抽象readNext ()，逐行读取提供的.csv：

```java
public List<String[]> readLineByLine(Path filePath) throws Exception {
    List<String[]> list = new ArrayList<>();
    try (Reader reader = Files.newBufferedReader(filePath)) {
        try (CSVReader csvReader = new CSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                list.add(line);
            }
        }
    }
    return list;
}
```

最后，我们可以通过传入文件路径来调用该方法：

```java
public List<String[]> readLineByLineExample() throws Exception {
    Path path = Paths.get(
      ClassLoader.getSystemResource("csv/twoColumn.csv").toURI())
    );
    return CsvReaderExamples.readLineByLine(path);
}

```

或者，为了获得更大的灵活性和配置选项，我们可以使用CSVReaderBuilder：

```java
CSVParser parser = new CSVParserBuilder()
    .withSeparator(',')
    .withIgnoreQuotations(true)
    .build();

CSVReader csvReader = new CSVReaderBuilder(reader)
    .withSkipLines(0)
    .withCSVParser(parser)
    .build();
```

CSVReaderBuilder允许我们跳过列标题并通过CSVParserBuilder设置解析规则。

使用CSVParserBuilder，我们可以选择自定义列分隔符，忽略或处理引号，说明我们将如何处理空字段，以及我们将如何解释转义字符。有关这些配置设置的更多信息，请参阅官方规范[文档](http://opencsv.sourceforge.net/apidocs/index.html)。

与往常一样，我们需要记住关闭所有Reader以防止内存泄漏。

### 3.2. CSV 编写器

CSVWriter同样提供一次性或逐行写入.csv文件的能力。

让我们看看如何逐行写入.csv ：

```java
public String writeLineByLine(List<String[]> lines, Path path) throws Exception {
    try (CSVWriter writer = new CSVWriter(new FileWriter(path.toString()))) {
        for (String[] line : lines) {
            writer.writeNext(array);
        }
    return Helpers.readFile(path);
}

```

然后我们将指定我们要保存该文件的位置，并调用我们刚刚编写的方法：

```java
public String writeLinebylineExample() throws Exception {
    Path path = Paths.get(
      ClassLoader.getSystemResource("csv/writtenOneByOne.csv").toURI()
    ); 
    return CsvWriterExamples.writeLineByLine(Helpers.fourColumnCsvString(), path); 
}
```

我们还可以通过传入一个代表我们的 .csv 行的字符串数组列表来一次编写我们的.csv ：

```java
public String writeAllLines(List<String[]> lines, Path path) throws Exception {
    try (CSVWriter writer = new CSVWriter(new FileWriter(path.toString()))) {
        writer.writeAll(stringArray);
    }
    return Helpers.readFile(path);
}
```

最后，我们是这样称呼它的：

```java
public String writeAllExample() throws Exception {
    Path path = Paths.get(
      ClassLoader.getSystemResource("csv/writtenAll.csv").toURI()
    ); 
    return CsvWriterExamples.writeAllLines(Helpers.fourColumnCsvString(), path);
}
```

### 3.3. 豆基阅读

OpenCSV 能够将.csv文件序列化为作为带注解的Javapojo bean 实现的预设和可重用模式。CsvToBean是使用CsvToBeanBuilder构建的。从 OpenCSV 4 开始，CsvToBeanBuilder是使用com.opencsv.bean.CsvToBean的推荐方式。

这是一个简单的 bean，我们可以使用它来序列化之前的两列.csv：

```java
public class SimplePositionBean  {
    @CsvBindByPosition(position = 0)
    private String exampleColOne;

    @CsvBindByPosition(position = 1)
    private String exampleColTwo;

    // getters and setters
}

```

.csv文件中的每一列都与 bean 中的一个字段相关联。我们可以使用@CsvBindByPosition或@CsvBindByName注解执行.csv列标题之间的映射，它们分别指定按位置或标题字符串匹配的映射。

首先，我们将创建一个名为CsvBean的超类，它将允许我们重用和概括我们将在下面构建的方法：

```java
public class CsvBean { }
```

这是一个示例子类：

```java
public class NamedColumnBean extends CsvBean {

    @CsvBindByName(column = "name")
    private String name;

    // Automatically infer column name as 'Age'
    @CsvBindByName
    private int age;

    // getters and setters
}
```

接下来，我们将使用CsvToBean抽象一个同步返回的列表：

```java
public List<CsvBean> beanBuilderExample(Path path, Class clazz) throws Exception {
    CsvTransfer csvTransfer = new CsvTransfer();

    try (Reader reader = Files.newBufferedReader(path)) {
        CsvToBean<CsvBean> cb = new CsvToBeanBuilder<CsvBean>(reader)
         .withType(clazz)
         .build();

        csvTransfer.setCsvList(cb.parse());
    }
    return csvTransfer.getCsvList();
}
```

然后我们传入我们的 bean ( clazz )，这样做时，我们将 bean 的字段与.csv行的相应列相关联。

我们可以在这里使用上面编写的CsvBean的SimplePositionBean子类调用它：

```java
public List<CsvBean> simplePositionBeanExample() throws Exception {
    Path path = Paths.get(
      ClassLoader.getSystemResource("csv/twoColumn.csv").toURI()); 
    return BeanExamples.beanBuilderExample(path, SimplePositionBean.class); 
}
```

我们也可以在这里使用CsvBean 的另一个子类NamedColumnBean调用它：

```java
public List<CsvBean> namedColumnBeanExample() throws Exception {
    Path path = Paths.get(
      ClassLoader.getSystemResource("csv/namedColumn.csv").toURI()); 
    return BeanExamples.beanBuilderExample(path, NamedColumnBean.class);
}
```

### 3.4. 基于 Bean 的写作

最后，让我们看看如何使用StatefulBeanToCsv类写入.csv文件：

```java
public String writeCsvFromBean(Path path) throws Exception {

    List<CsvBean> sampleData = Arrays.asList(
        new WriteExampleBean("Test1", "sfdsf", "fdfd"),
        new WriteExampleBean("Test2", "ipso", "facto")
    );
    
    try (Writer writer  = new FileWriter(path.toString())) {

        StatefulBeanToCsv<CsvBean> sbc = new StatefulBeanToCsvBuilder<CsvBean>(writer)
          .withQuotechar(''')
          .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
          .build();

        sbc.write(sampleData);
    }
    return Helpers.readFile(path);
}
```

在这里，我们指定了我们将如何分隔和引用我们的数据，这些数据作为指定CsvBean对象的列表提供。

然后我们可以在传入所需的输出文件路径后调用我们的方法writeCsvFromBean() ：

```java
public String writeCsvFromBeanExample() {
    Path path = Paths.get(
      ClassLoader.getSystemResource("csv/writtenBean.csv").toURI()); 
    return BeanExamples.writeCsvFromBean(path); 
}
```

## 4. 总结

在这篇简短的文章中，我们讨论了使用 beans、CSVReader和CSVWriter的OpenCSV同步代码示例。有关更多信息，请在此处查看官方文档[。](http://opencsv.sourceforge.net/)