## 1. 概述

在本教程中，我们将介绍使用 Java对给定字符串中的单词进行计数的不同方法。

## 2.使用StringTokenizer

在Java中对字符串中的单词进行计数的一种简单方法是使用StringTokenizer类：

```java
assertEquals(3, new StringTokenizer("three blind mice").countTokens());
assertEquals(4, new StringTokenizer("seethowttheytrun").countTokens());
```

请注意，StringTokenizer会自动为我们处理空格，例如制表符和回车符。

但是，它可能在某些地方出错，比如连字符：

```java
assertEquals(7, new StringTokenizer("the farmer's wife--she was from Albuquerque").countTokens());
```

在这种情况下，我们希望“wife”和“she”是不同的词，但由于它们之间没有空格，默认值使我们失望。

幸运的是， StringTokenizer 附带了另一个构造函数。我们可以将定界符传递给构造函数来完成上述工作：

```java
assertEquals(7, new StringTokenizer("the farmer's wife--she was from Albuquerque", " -").countTokens());
```

当尝试从CSV 文件之类的文件中计算字符串中的单词时，这会派上用场：

```java
assertEquals(10, new StringTokenizer("did,you,ever,see,such,a,sight,in,your,life", ",").countTokens());
```

所以，StringTokenizer很简单，它让我们完成了大部分工作。

让我们看看正则表达式能给我们带来什么额外的马力。

## 3.正则表达式

为了让我们为这个任务想出一个有意义的正则表达式，我们需要定义我们所认为的单词：单词以字母开头，以空格字符或标点符号结尾。

考虑到这一点，给定一个字符串，我们要做的是在遇到空格和标点符号的每个点处拆分该字符串，然后计算生成的单词。

```java
assertEquals(7, countWordsUsingRegex("the farmer's wife--she was from Albuquerque"));
```

让我们稍微改进一下，看看正则表达式的威力：

```java
assertEquals(9, countWordsUsingRegex("no&one#should%ever-write-like,this;but:well"));
```

仅通过将定界符传递给StringTokenizer来解决这个问题是不切实际的，因为我们必须定义一个非常长的定界符来尝试列出所有可能的标点符号。

事实证明我们真的不需要做太多，将正则表达式 [pPs&&[^']]+传递给 String类的 split 方法 就可以了：

```java
public static int countWordsUsingRegex(String arg) {
    if (arg == null) {
        return 0;
    }
    final String[] words = arg.split("[pPs&&[^']]+");
    return words.length;
}
```

正则表达式[pPs&&[^']]+查找任意长度的标点符号或空格，并忽略撇号标点符号。

要了解有关正则表达式的更多信息，请参阅[Baeldung 上的正](https://www.baeldung.com/regular-expressions-java)则表达式。

## 4. 循环和字符串 API

另一种方法是使用一个标志来跟踪遇到的单词。

我们在遇到新词时将标志设置为WORD并增加字数，然后在遇到非词(标点符号或空格字符)时返回SEPARATOR 。

这种方法为我们提供了与使用正则表达式相同的结果：

```java
assertEquals(9, countWordsManually("no&one#should%ever-write-like,this but   well"));

```

我们确实必须注意标点符号不是真正的单词分隔符的特殊情况，例如：

```java
assertEquals(6, countWordsManually("the farmer's wife--she was from Albuquerque"));
```

这里我们要的是将“farmer's”算作一个词，虽然撇号“'”是一个标点符号。

在正则表达式版本中，我们可以使用正则表达式灵活地定义不符合字符条件的内容。但是现在我们正在编写自己的实现，我们必须在一个单独的方法中定义这个排除：

```java
private static boolean isAllowedInWord(char charAt) {
    return charAt == ''' || Character.isLetter(charAt);
}

```

所以我们在这里所做的是在一个单词中允许所有字符和合法的标点符号，在这种情况下是撇号。

我们现在可以在我们的实现中使用这个方法：

```java
public static int countWordsManually(String arg) {
    if (arg == null) {
        return 0;
    }
    int flag = SEPARATOR;
    int count = 0;
    int stringLength = arg.length();
    int characterCounter = 0;

    while (characterCounter < stringLength) {
        if (isAllowedInWord(arg.charAt(characterCounter)) && flag == SEPARATOR) {
            flag = WORD;
            count++;
        } else if (!isAllowedInWord(arg.charAt(characterCounter))) {
            flag = SEPARATOR;
        }
        characterCounter++;
    }
    return count;
}
```

第一个条件在遇到一个词时标记一个词，并增加计数器。第二个条件检查字符是否不是字母，并将标志设置为SEPARATOR。

## 5.总结

在本教程中，我们研究了使用多种方法计算单词的方法。我们可以根据我们的特定用例选择任何一个。