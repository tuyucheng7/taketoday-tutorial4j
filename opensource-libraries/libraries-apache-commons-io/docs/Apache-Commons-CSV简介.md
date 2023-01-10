## 1. 概述

[Apache Commons CSV 库](https://commons.apache.org/proper/commons-csv/)具有许多用于创建和读取 CSV 文件的有用功能。

在本快速教程中，我们将通过一个简单示例了解如何使用该库。

## 2.Maven依赖

首先，我们将使用 Maven 导入该库的最新版本：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-csv</artifactId>
    <version>1.9.0</version>
</dependency>

```

要检查这个库的最新版本——[去这里](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-csv")。

## 3. 读取 CSV 文件

考虑以下名为 book.csv 的 CSV 文件，其中包含一本书的属性：

```plaintext
author,title
Dan Simmons,Hyperion
Douglas Adams,The Hitchhiker's Guide to the Galaxy
```

让我们看看如何阅读它：

```java
Map<String, String> AUTHOR_BOOK_MAP = new HashMap<>() {
    {
        put("Dan Simmons", "Hyperion");
        put("Douglas Adams", "The Hitchhiker's Guide to the Galaxy");
    }
});
String[] HEADERS = { "author", "title"};

@Test
public void givenCSVFile_whenRead_thenContentsAsExpected() throws IOException {
    Reader in = new FileReader("book.csv");
    Iterable<CSVRecord> records = CSVFormat.DEFAULT
      .withHeader(HEADERS)
      .withFirstRecordAsHeader()
      .parse(in);
    for (CSVRecord record : records) {
        String author = record.get("author");
        String title = record.get("title");
        assertEquals(AUTHOR_BOOK_MAP.get(author), title);
    }
}
```

我们在跳过第一行后读取 CSV 文件的记录，因为它是标题。

有不同类型的CSVFormat指定 CSV 文件的格式，可以在下一段中看到一个示例。

## 4. 创建 CSV 文件

让我们看看如何创建与上面相同的 CSV 文件：

```java
public void createCSVFile() throws IOException {
    FileWriter out = new FileWriter("book_new.csv");
    try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
      .withHeader(HEADERS))) {
        AUTHOR_BOOK_MAP.forEach((author, title) -> {
            printer.printRecord(author, title);
        });
    }
}
```

新的 CSV 文件将使用适当的标题创建，因为我们已在CSVFormat声明中指定了它们。

## 5. 标题和阅读栏

有多种读取和写入标头的方法。同样，有不同的方法来读取列值。

让我们一一浏览：

### 5.1. 按索引访问列

这是读取列值的最基本方法。这可以在 CSV 文件的标题未知时使用：

```java
Reader in = new FileReader("book.csv");
Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
for (CSVRecord record : records) {
    String columnOne = record.get(0);
    String columnTwo = record.get(1);
}
```

### 5.2. 通过预定义的标题访问列

与通过索引访问相比，这是一种更直观的访问列的方法：

```java
Iterable<CSVRecord> records = CSVFormat.DEFAULT
  .withHeader("author", "title").parse(in);
for (CSVRecord record : records) {
    String author = record.get("author");
    String title = record.get("title");
}
```

### 5.3. 使用枚举作为标题

使用字符串访问列值可能容易出错。使用 Enums 而不是 Strings 将使代码更加标准化和更容易理解：

```java
enum BookHeaders {
    author, title
}

Iterable<CSVRecord> records = CSVFormat.DEFAULT
  .withHeader(BookHeaders.class).parse(in);
for (CSVRecord record : records) {
    String author = record.get(BookHeaders.author);
    String title = record.get(BookHeaders.title);
}
```

### 5.4. 跳过标题行

通常，CSV 文件在第一行包含标题。因此，在大多数情况下，跳过它并从第二行开始阅读是安全的。

这将自动检测标题访问列值：

```java
Iterable<CSVRecord> records = CSVFormat.DEFAULT
  .withFirstRowAsHeader().parse(in);
for (CSVRecord record : records) {
    String author = record.get("author");
    String title = record.get("title");
}
```

### 5.5. 创建带有标题的文件

同样，我们可以创建一个 CSV 文件，第一行包含标题：

```java
FileWriter out = new FileWriter("book_new.csv");
CSVPrinter printer = CSVFormat.DEFAULT
  .withHeader("author", "title").print(out);
```

## 六. 总结

我们通过一个简单的例子介绍了 Apache 的 Commons CSV 库的使用。[可以在此处](https://commons.apache.org/proper/commons-csv/user-guide.html)阅读有关图书馆的更多信息。