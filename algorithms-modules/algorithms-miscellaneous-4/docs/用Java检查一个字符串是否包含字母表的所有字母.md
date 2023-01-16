## 1. 概述

在本教程中，我们将了解如何检查 String 是否包含字母表中的所有字母。

这是一个简单的例子：“农夫杰克意识到黄色的大被子很贵。”——它实际上包含了字母表中的所有字母。

我们将讨论三种方法。

首先，我们将使用命令式方法对算法建模。然后将使用正则表达式。最后，我们将利用使用Java8 的更具声明性的方法。

此外，我们将讨论所采用方法的大 O 复杂性。

## 2. 命令式算法

让我们实现一个命令式算法。为此，首先，我们将创建一个已访问的布尔数组。然后，我们将逐个字符地遍历输入字符串并将该字符标记为已访问。

请注意，大写和小写被认为是相同的。因此索引 0 代表 A 和 a，同样，索引 25 代表 Z 和 z。

最后，我们将检查访问数组中的所有字符是否都设置为 true：

```java
public class EnglishAlphabetLetters {

    public static boolean checkStringForAllTheLetters(String input) {
        int index = 0;
        boolean[] visited = new boolean[26];

        for (int id = 0; id < input.length(); id++) {
            if ('a' <= input.charAt(id) && input.charAt(id) <= 'z') {
                index = input.charAt(id) - 'a';
            } else if ('A' <= input.charAt(id) && input.charAt(id) <= 'Z') {
                index = input.charAt(id) - 'A';
            }
            visited[index] = true;
        }

        for (int id = 0; id < 26; id++) {
            if (!visited[id]) {
                return false;
            }
        }
        return true;
    }
}
```

该程序的大 O 复杂度为 O(n)，其中n 是字符串的长度。

请注意，有许多方法可以优化算法，例如从集合中删除字母并在集合为空时立即中断。不过，出于练习的目的，该算法已经足够好了。

## 3.使用正则表达式

使用正则表达式，我们可以很容易地用几行代码得到相同的结果：

```java
public static boolean checkStringForAllLetterUsingRegex(String input) {
    return input.toLowerCase()
      .replaceAll("[^a-z]", "")
      .replaceAll("(.)(?=.1)", "")
      .length() == 26;
}
```

在这里，我们首先从输入中消除除字母以外的所有字符。然后我们删除重复的字符。最后，我们正在数字母并确保我们拥有所有字母，26 个。

尽管性能较低，但这种方法的 Big-O-Complexity 也趋于 O(n)。

## 4.Java8 流

使用Java8 功能，我们可以使用 Stream 的过滤器 和 不同的方法以更紧凑和声明的方式轻松实现相同的结果 ：

```java
public static boolean checkStringForAllLetterUsingStream(String input) {
    long c = input.toLowerCase().chars()
      .filter(ch -> ch >= 'a' && ch <= 'z')
      .distinct()
      .count();
    return c == 26;
}
```

这种方法的大 O 复杂度也将是 O(n)。

## 4.测试

让我们为我们的算法测试一条快乐的路径：

```java
@Test
public void givenString_whenContainsAllCharacter_thenTrue() {
    String sentence = "Farmer jack realized that big yellow quilts were expensive";
    assertTrue(EnglishAlphabetLetters.checkStringForAllTheLetters(sentence));
}
```

在这里，句子包含字母表中的所有字母，因此，我们期望结果为真。

## 5.总结

在本教程中，我们介绍了如何检查 String 是否包含字母表中的所有字母。

我们首先看到了几种使用传统命令式编程、正则表达式和Java8 流来实现这一点的方法。