## 1. 概述

在本快速教程中，我们将学习如何使用Java写入 CSV 文件。CSV 代表逗号分隔值，它是系统之间批量数据传输的通用格式。

要编写我们的 CSV 文件，我们将使用java.io包中的类。

我们将讨论特殊字符以及如何处理它们。我们的目标是在 Microsoft Excel 和 Google 表格中打开我们的输出文件。

在我们的Java示例之后，我们将简要了解一些用于处理 CSV 文件的可用第三方库。

## 2. 用PrintWriter写

我们将使用PrintWriter来编写我们的 CSV 文件。有关使用java.io写入文件的更详细信息，请参阅我们[关于写入文件的文章](https://www.baeldung.com/java-write-to-file)。

### 2.1. 编写 CSV

首先，让我们创建一个方法来格式化表示为String数组的单行数据：

```java
public String convertToCSV(String[] data) {
    return Stream.of(data)
      .map(this::escapeSpecialCharacters)
      .collect(Collectors.joining(","));
}
```

不过，在我们调用此方法之前，让我们构建一些示例数据：

```java
List<String[]> dataLines = new ArrayList<>();
dataLines.add(new String[] 
  { "John", "Doe", "38", "Comment DatanAnother line of comment data" });
dataLines.add(new String[] 
  { "Jane", "Doe, Jr.", "19", "She said "I'm being quoted"" });
```

有了这些数据，让我们用convertToCSV 转换每一行， 并将其写入文件：

```java
public void givenDataArray_whenConvertToCSV_thenOutputCreated() throws IOException {
    File csvOutputFile = new File(CSV_FILE_NAME);
    try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
        dataLines.stream()
          .map(this::convertToCSV)
          .forEach(pw::println);
    }
    assertTrue(csvOutputFile.exists());
}
```

### 2.2. 处理特殊字符

在 CSV 文件中，某些字符是有问题的，作为开发人员，我们很少能完全控制数据的质量。那么现在让我们看看如何处理特殊字符。

对于我们的示例，我们将重点关注逗号、引号和换行符。包含逗号或引号的字段会被双引号包围，双引号会被双引号转义。我们将删除新行并用空格替换它们。

有问题的字符及其处理方式可能因用例而异。

我们的convertToCSV方法 在构建字符串时对每条数据 调用 escapeSpecialCharacters方法。

现在让我们实现我们的escapeSpecialCharacters方法：

```java
public String escapeSpecialCharacters(String data) {
    String escapedData = data.replaceAll("R", " ");
    if (data.contains(",") || data.contains(""") || data.contains("'")) {
        data = data.replace(""", """");
        escapedData = """ + data + """;
    }
    return escapedData;
}
```

## 3. 第三方库

正如我们在示例中看到的那样，当我们开始考虑特殊字符以及如何处理它们时，编写 CSV 文件会变得很复杂。

对我们来说幸运的是，有许多第三方库可用于处理 CSV 文件，其中许多库可以处理这些特殊字符和其他可能发生的异常情况。

让我们来看看其中的几个：

-   [Apache Commons CSV](https://www.baeldung.com/apache-commons-csv)：Apache 的 CSV 产品，用于处理 CSV 文件
-   [Open CSV](https://www.baeldung.com/opencsv)：另一个流行且积极维护的 CSV 库
-   [Flatpack](http://flatpack.sourceforge.net/)：一个正在积极开发的开源 CSV 库
-   [CSVeed](http://csveed.org/)：开源和积极维护

## 4。总结

在这篇简短的文章中，我们讨论了如何使用Java的PrintWriter类编写 CSV 文件。接下来，我们讨论并处理了输出数据中的特殊字符。

在我们的普通Java示例之后，我们查看了可用的第三方库的概述。