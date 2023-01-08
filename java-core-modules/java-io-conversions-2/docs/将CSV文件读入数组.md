## 1. 概述

简而言之，CSV(逗号分隔值)文件包含由逗号分隔符分隔的组织信息。

在本教程中，我们将研究将 CSV 文件读入数组的不同方法。

## 2. java.io 中的 BufferedReader

首先，我们将使用BufferedReader 中的readLine()逐行读取记录。

然后我们将根据逗号分隔符将行拆分为标记：

```java
List<List<String>> records = new ArrayList<>();
try (BufferedReader br = new BufferedReader(new FileReader("book.csv"))) {
    String line;
    while ((line = br.readLine()) != null) {
        String[] values = line.split(COMMA_DELIMITER);
        records.add(Arrays.asList(values));
    }
}
```

请注意，更复杂的 CSV(例如，引用或包括逗号作为值)将不会按预期使用此方法进行解析。

## 3. java.util 中的扫描器

接下来，我们将使用java.util.Scanner来遍历文件的内容并逐行逐行检索：

```java
List<List<String>> records = new ArrayList<>();
try (Scanner scanner = new Scanner(new File("book.csv"));) {
    while (scanner.hasNextLine()) {
        records.add(getRecordFromLine(scanner.nextLine()));
    }
}
```

然后我们将解析这些行并将其存储到一个数组中：

```java
private List<String> getRecordFromLine(String line) {
    List<String> values = new ArrayList<String>();
    try (Scanner rowScanner = new Scanner(line)) {
        rowScanner.useDelimiter(COMMA_DELIMITER);
        while (rowScanner.hasNext()) {
            values.add(rowScanner.next());
        }
    }
    return values;
}
```

像以前一样，更复杂的 CSV 将不会按预期使用此方法进行解析。

## 4. 打开CSV

我们可以使用 OpenCSV 处理更复杂的 CSV 文件。

OpenCSV 是一个第三方库，它提供了一个 API 来处理 CSV 文件。

我们将使用CSVReader中的readNext()方法来读取文件中的记录：

```java
List<List<String>> records = new ArrayList<List<String>>();
try (CSVReader csvReader = new CSVReader(new FileReader("book.csv"));) {
    String[] values = null;
    while ((values = csvReader.readNext()) != null) {
        records.add(Arrays.asList(values));
    }
}
```

要深入挖掘并了解有关 OpenCSV 的更多信息，请查看我们的[OpenCSV 教程](https://www.baeldung.com/opencsv)。

## 5.总结

在这篇快速文章中，我们探讨了将 CSV 文件读入数组的不同方法。