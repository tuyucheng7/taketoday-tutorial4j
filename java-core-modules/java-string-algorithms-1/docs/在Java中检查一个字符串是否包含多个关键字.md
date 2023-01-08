## 1. 概述

在本快速教程中，我们将了解如何检测字符串中的多个单词。

## 2. 我们的例子

假设我们有字符串：

```java
String inputString = "hello there, Baeldung";
```

我们的任务是找出 inputString 是否包含“hello”和“Baeldung”这两个词。

因此，让我们将关键字放入一个数组中：

```java
String[] words = {"hello", "Baeldung"};
```

此外，单词的顺序并不重要，匹配应该区分大小写。

## 3. 使用String.contains()

首先，我们将展示如何使用[String.contains()](https://www.baeldung.com/string/contains)方法来实现我们的目标。

让我们遍历关键字数组并检查 inputString 中每个项目的出现：

```java
public static boolean containsWords(String inputString, String[] items) {
    boolean found = true;
    for (String item : items) {
        if (!inputString.contains(item)) {
            found = false;
            break;
        }
    }
    return found;
}
```

如果 inputString包含给定的 item，contains()方法将返回true。当我们的字符串中没有任何关键字时，我们可以停止前进并立即返回false。

尽管我们需要编写更多代码，但此解决方案对于简单用例来说还是很快的。

## 4. 使用 String.indexOf()

类似于使用String.contains()方法的解决方案，我们可以使用[String.indexOf()](https://www.baeldung.com/string/index-of)方法检查关键字的索引。为此，我们需要一个接受输入字符串和关键字列表的方法：

```java
public static boolean containsWordsIndexOf(String inputString, String[] words) {
    boolean found = true;
    for (String word : words) {
        if (inputString.indexOf(word) == -1) {
            found = false;
            break;
        }
    }
    return found;
}
```

indexOf()方法返回inputString 中单词的索引。当文本中没有单词时，索引将为 -1。

## 5.使用正则表达式

现在，让我们使用[正则表达式](https://www.baeldung.com/regular-expressions-java)来匹配我们的单词。为此，我们将使用Pattern类。

首先，让我们定义字符串表达式。由于我们需要匹配两个关键字，因此我们将使用两个先行构建我们的正则表达式规则：

```java
Pattern pattern = Pattern.compile("(?=.hello)(?=.Baeldung)");
```

对于一般情况：

```java
StringBuilder regexp = new StringBuilder();
for (String word : words) {
    regexp.append("(?=.").append(word).append(")");
}
```

之后，我们将使用 matcher()方法find()出现：

```java
public static boolean containsWordsPatternMatch(String inputString, String[] words) {

    StringBuilder regexp = new StringBuilder();
    for (String word : words) {
        regexp.append("(?=.").append(word).append(")");
    }

    Pattern pattern = Pattern.compile(regexp.toString());

    return pattern.matcher(inputString).find();
}
```

但是，正则表达式有性能成本。如果我们有多个词要查找，这个解决方案的性能可能不是最优的。

## 6. 使用Java8 和列表

最后，我们可以使用Java8 的[Stream API](https://www.baeldung.com/java-8-streams-introduction)。但首先，让我们对初始数据做一些小的转换：

```java
List<String> inputString = Arrays.asList(inputString.split(" "));
List<String> words = Arrays.asList(words);
```

现在，是时候使用 Stream API 了：

```java
public static boolean containsWordsJava8(String inputString, String[] words) {
    List<String> inputStringList = Arrays.asList(inputString.split(" "));
    List<String> wordsList = Arrays.asList(words);

    return wordsList.stream().allMatch(inputStringList::contains);
}
```

如果输入字符串包含我们所有的关键字，上面的操作管道将返回true 。

或者，我们可以简单地使用[Collections 框架的](https://www.baeldung.com/java-collections)containsAll()方法 来实现所需的结果：

```java
public static boolean containsWordsArray(String inputString, String[] words) {
    List<String> inputStringList = Arrays.asList(inputString.split(" "));
    List<String> wordsList = Arrays.asList(words);

    return inputStringList.containsAll(wordsList);
}
```

但是，此方法仅适用于整个单词。因此，只有当它们在文本中以空格分隔时，它才会找到我们的关键字。

## 7. 使用Aho-Corasick算法

简单来说，[Aho-Corasick算法](https://en.wikipedia.org/wiki/Aho–Corasick_algorithm)就是针对多关键词的文本搜索。无论我们要搜索多少关键字或文本长度多长，它的时间复杂度都是O(n) 。

让我们 在pom.xml中包含[Aho-Corasick 算法依赖项](https://search.maven.org/search?q=g:org.ahocorasick a:ahocorasick)：

```xml
<dependency>
    <groupId>org.ahocorasick</groupId>
    <artifactId>ahocorasick</artifactId>
    <version>0.4.0</version>
</dependency>
```

首先，让我们用关键字的单词 数组构建 trie 管道。为此，我们将使用[Trie](https://www.baeldung.com/trie-java)数据结构：

```java
Trie trie = Trie.builder().onlyWholeWords().addKeywords(words).build();
```

之后，让我们用inputString文本调用解析器方法，我们希望在其中查找关键字并将结果保存在emits集合中：

```java
Collection<Emit> emits = trie.parseText(inputString);
```

最后，如果我们打印结果：

```java
emits.forEach(System.out::println);
```

对于每个关键字，我们将看到关键字在文本中的起始位置、结束位置和关键字本身：

```plaintext
0:4=hello
13:20=Baeldung
```

最后，让我们看看完整的实现：

```java
public static boolean containsWordsAhoCorasick(String inputString, String[] words) {
    Trie trie = Trie.builder().onlyWholeWords().addKeywords(words).build();

    Collection<Emit> emits = trie.parseText(inputString);
    emits.forEach(System.out::println);

    boolean found = true;
    for(String word : words) {
        boolean contains = Arrays.toString(emits.toArray()).contains(word);
        if (!contains) {
            found = false;
            break;
        }
    }

    return found;
}
```

在此示例中，我们只查找整个单词。因此，如果我们不仅要匹配 inputString还要匹配“helloBaeldung”，我们应该简单地 从Trie构建器管道中删除onlyWholeWords()属性。

此外，请记住，我们还从emits集合中删除了重复的元素，因为同一个关键字可能有多个匹配项。

## 八、总结

在本文中，我们学习了如何在字符串中查找多个关键字。此外，我们还展示了使用核心 JDK 以及 Aho-Corasick库的示例。