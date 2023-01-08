## 1. 概述

字符串通常包含单词和其他定界符的混合。有时，这些字符串可能会在没有空格的情况下通过更改来分隔单词。例如，驼峰式大小写将第一个 之后的每个单词大写，而标题大小写(或 Pascal 大小写)将每个单词大写。

我们可能希望将这些字符串解析回单词以便处理它们。

在这个简短的教程中，我们将了解如何使用[正则表达式](https://www.baeldung.com/regular-expressions-java)在大小写混合的字符串中查找单词，以及如何将它们转换成句子或标题。

## 2. 解析大写字符串的用例

处理驼峰式字符串的一个常见用例可能是文档中的字段名称。假设一个文档有一个字段 “firstName”——我们可能希望在屏幕上将该字段显示为“First name”或“First Name” 。

同样，如果我们要通过反射扫描应用程序中的类型或函数，以便使用它们的名称生成报告，我们通常会发现我们可能希望转换的驼峰式或标题式标识符。

在解析这些表达式时我们需要解决的一个额外问题是单字母单词导致连续的大写字母。

为清楚起见：

-   thisIsAnExampleOfCamelCase
-   这是标题案例
-   thisHasASingleLetterWord

现在我们知道了需要解析的标识符种类，让我们使用正则表达式来查找单词。

## 3. 使用正则表达式查找单词

### 3.1. 定义一个正则表达式来查找单词

让我们定义一个[正则表达式](https://www.baeldung.com/tag/regex/)来定位仅由小写字母、单个大写字母后跟小写字母或单独的大写字母组成的单词：

```java
Pattern WORD_FINDER = Pattern.compile("(([A-Z]?[a-z]+)|([A-Z]))");
```

该表达式为正则表达式引擎提供了两个选项。第一个使用 “[AZ]？” 表示“一个可选的第一个大写字母”，然后 “[az]+”表示“一个或多个小写字母”。之后是“|” 要提供的字符或逻辑，后跟表达式 “[AZ]”，意思是“单个大写字母”。

现在我们有了正则表达式，让我们解析我们的字符串。

### 3.2. 在字符串中查找单词

我们将定义一个方法来使用这个正则表达式：

```java
public List<String> findWordsInMixedCase(String text) {
    Matcher matcher = WORD_FINDER.matcher(text);
    List<String> words = new ArrayList<>();
    while (matcher.find()) {
        words.add(matcher.group(0));
    }
    return words;
}
```

这使用 由正则表达式的 Pattern创建的Matcher来帮助我们[找到单词](https://www.baeldung.com/java-matcher-find-vs-matches)。我们在 matcher 仍然有 matches 的时候迭代它，将它们添加到我们的列表中。

这应该提取任何符合我们的词定义的东西。让我们测试一下。

### 3.3. 测试单词查找器

我们的单词查找器应该能够找到由任何非单词字符以及大小写变化分隔的单词。让我们从一个简单的例子开始：

```java
assertThat(findWordsInMixedCase("some words"))
  .containsExactly("some", "words");
```

该测试通过并向我们展示了我们的算法正在运行。接下来，我们将尝试驼峰案例：

```java
assertThat(findWordsInMixedCase("thisIsCamelCaseText"))
  .containsExactly("this", "Is", "Camel", "Case", "Text");
```

在这里，我们看到单词是从驼峰式字符串中提取出来的，并且大小写不变。例如，“Is”在原文中以大写字母开头，提取出来后转为大写。

我们也可以尝试使用标题大小写：

```java
assertThat(findWordsInMixedCase("ThisIsTitleCaseText"))
  .containsExactly("This", "Is", "Title", "Case", "Text");
```

另外，我们可以检查是否按照我们的预期提取了单字母单词：

```java
assertThat(findWordsInMixedCase("thisHasASingleLetterWord"))
  .containsExactly("this", "Has", "A", "Single", "Letter", "Word");
```

到目前为止，我们已经构建了一个单词提取器，但是这些单词的大写方式可能不适合输出。

## 4. 将单词列表转换为人类可读的格式

提取单词列表后，我们可能想使用[toUpperCase或 toLowerCase](https://www.baeldung.com/java-string-convert-case)之类的方法对它们进行规范化。然后我们可以使用[String.join](https://www.baeldung.com/java-strings-concatenation#3stringjoin-java-8)将它们连接回带有分隔符的单个字符串。让我们看看用这些实现真实世界用例的几种方法。

### 4.1. 转换成句子

句子以大写字母开头，以句号结尾—— “.” . 我们需要能够让单词以大写字母开头：

```java
private String capitalizeFirst(String word) {
    return word.substring(0, 1).toUpperCase()
      + word.substring(1).toLowerCase();
}
```

然后我们可以遍历单词，将第一个大写，并将其他小写：

```java
public String sentenceCase(List<String> words) {
    List<String> capitalized = new ArrayList<>();
    for (int i = 0; i < words.size(); i++) {
        String currentWord = words.get(i);
        if (i == 0) {
            capitalized.add(capitalizeFirst(currentWord));
        } else {
            capitalized.add(currentWord.toLowerCase());
        }
    }
    return String.join(" ", capitalized) + ".";
}
```

这里的逻辑是第一个单词的第一个字符大写，其余的都是小写。我们以空格作为分隔符加入它们，并在末尾添加一个句点。

让我们测试一下：

```java
assertThat(sentenceCase(Arrays.asList("these", "Words", "Form", "A", "Sentence")))
  .isEqualTo("These words form a sentence.");
```

### 4.2. 转换为标题大小写

[Title case](https://www.baeldung.com/java-string-title-case)的规则比句子稍微复杂一些。每个单词都必须有一个大写字母，除非它是一个通常不大写的特殊停用词。但是，整个标题必须以大写字母开头。

我们可以通过定义停用词来实现这一点：

```java
Set<String> STOP_WORDS = Stream.of("a", "an", "the", "and", 
  "but", "for", "at", "by", "to", "or")
  .collect(Collectors.toSet());
```

在此之后，我们可以修改 循环中的if语句，以将任何不是停用词的单词大写，以及第一个：

```less
if (i == 0 || 
  !STOP_WORDS.contains(currentWord.toLowerCase())) {
    capitalized.add(capitalizeFirst(currentWord));
 }
```

组合单词的算法是相同的，尽管我们最后没有添加句点。

让我们测试一下：

```java
assertThat(capitalizeMyTitle(Arrays.asList("title", "words", "capitalize")))
  .isEqualTo("Title Words Capitalize");

assertThat(capitalizeMyTitle(Arrays.asList("a", "stop", "word", "first")))
  .isEqualTo("A Stop Word First");
```

## 5.总结

在这篇简短的文章中，我们研究了如何使用正则表达式在字符串中查找单词。我们了解了如何定义此正则表达式以使用大写字母作为单词边界来查找不同的单词。

我们还研究了一些简单的算法，用于获取单词列表并将它们转换为句子或标题的正确大写。