## 1. 概述

在本教程中，我们将讨论在Java中从字符串中删除停用词的不同方法。在我们想要从文本中删除不需要或不允许的词的情况下，这是一个有用的操作，例如在线站点用户添加的评论或评论。

我们将使用一个简单的循环、Collection.removeAll()和正则表达式。

最后，我们将使用[Java Microbenchmark Harness](https://www.baeldung.com/java-microbenchmark-harness)比较它们的性能。

## 2.加载停用词

首先，我们将从文本文件中加载停用词。

这里我们有english_stopwords.txt文件，其中包含我们认为是停用词的单词列表，例如I、he、she和the。

我们将使用Files.readAllLines()将停用词加载到字符串列表中：

```java
@BeforeClass
public static void loadStopwords() throws IOException {
    stopwords = Files.readAllLines(Paths.get("english_stopwords.txt"));
}
```

## 3.手动删除停用词

对于我们的第一个解决方案，我们将通过遍历每个单词并检查它是否是停用词来手动删除停用词：

```java
@Test
public void whenRemoveStopwordsManually_thenSuccess() {
    String original = "The quick brown fox jumps over the lazy dog"; 
    String target = "quick brown fox jumps lazy dog";
    String[] allWords = original.toLowerCase().split(" ");

    StringBuilder builder = new StringBuilder();
    for(String word : allWords) {
        if(!stopwords.contains(word)) {
            builder.append(word);
            builder.append(' ');
        }
    }
    
    String result = builder.toString().trim();
    assertEquals(result, target);
}
```

## 4. 使用Collection.removeAll()

接下来，我们可以使用Collection.removeAll()一次删除所有停用词，而不是遍历String中的每个单词：

```java
@Test
public void whenRemoveStopwordsUsingRemoveAll_thenSuccess() {
    ArrayList<String> allWords = 
      Stream.of(original.toLowerCase().split(" "))
            .collect(Collectors.toCollection(ArrayList<String>::new));
    allWords.removeAll(stopwords);

    String result = allWords.stream().collect(Collectors.joining(" "));
    assertEquals(result, target);
}
```

在此示例中，将我们的String拆分为单词数组后，我们将其转换为ArrayList以便能够应用removeAll()方法。

## 5.使用正则表达式

最后，我们可以从停用词列表中创建一个正则表达式，然后用它来替换String中的停用词：

```java
@Test
public void whenRemoveStopwordsUsingRegex_thenSuccess() {
    String stopwordsRegex = stopwords.stream()
      .collect(Collectors.joining("|", "b(", ")bs?"));

    String result = original.toLowerCase().replaceAll(stopwordsRegex, "");
    assertEquals(result, target);
}
```

生成的停用词正则表达式的格式为“b(he|she|the|…)bs?”。在此正则表达式中，“b”指的是单词边界，以避免替换“heat”中的“he”，例如“s?” 指零个或一个空格，在替换停用词后删除多余的空格。

## 六、性能比较

现在，让我们看看哪种方法的性能最好。

首先，让我们设置基准。我们将使用一个相当大的文本文件作为我们的字符串的来源，称为shakespeare-hamlet.txt：

```java
@Setup
public void setup() throws IOException {
    data = new String(Files.readAllBytes(Paths.get("shakespeare-hamlet.txt")));
    data = data.toLowerCase();
    stopwords = Files.readAllLines(Paths.get("english_stopwords.txt"));
    stopwordsRegex = stopwords.stream().collect(Collectors.joining("|", "b(", ")bs?"));
}
```

然后我们将有我们的基准方法，从 removeManually()开始：

```java
@Benchmark
public String removeManually() {
    String[] allWords = data.split(" ");
    StringBuilder builder = new StringBuilder();
    for(String word : allWords) {
        if(!stopwords.contains(word)) {
            builder.append(word);
            builder.append(' ');
        }
    }
    return builder.toString().trim();
}
```

接下来，我们有removeAll()基准：

```java
@Benchmark
public String removeAll() {
    ArrayList<String> allWords = 
      Stream.of(data.split(" "))
            .collect(Collectors.toCollection(ArrayList<String>::new));
    allWords.removeAll(stopwords);
    return allWords.stream().collect(Collectors.joining(" "));
}
```

最后，我们将为replaceRegex()添加基准：

```java
@Benchmark
public String replaceRegex() {
    return data.replaceAll(stopwordsRegex, "");
}
```

这是我们的基准测试结果：

```bash
Benchmark                           Mode  Cnt   Score    Error  Units
removeAll                           avgt   60   7.782 ±  0.076  ms/op
removeManually                      avgt   60   8.186 ±  0.348  ms/op
replaceRegex                        avgt   60  42.035 ±  1.098  ms/op
```

似乎使用Collection.removeAll()的执行时间最快，而使用正则表达式的执行时间最慢。

## 七、总结

在这篇快速文章中，我们学习了在Java中从String中删除停用词的不同方法。我们还对它们进行了基准测试，以查看哪种方法具有最佳性能。

