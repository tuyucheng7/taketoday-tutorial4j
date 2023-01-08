## 1. 概述

在较大的文本字符串中搜索字符模式或单词的繁琐工作在各个领域中完成。例如，在生物信息学中，我们可能需要在染色体中找到一个 DNA 片段。

在媒体中，编辑会在大量文本中找到特定的短语。数据监控通过查找数据中嵌入的可疑词来检测诈骗或垃圾邮件。

在任何情况下，搜索都是众所周知且令人生畏的苦差事，以至于它通常被称为“大海捞针问题”。在本教程中，我们将演示一个简单的算法，该算法使用Java String类的[indexOf(String str, int fromIndex)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#indexOf(java.lang.String,int)) 方法来查找字符串中出现的所有单词。

## 2. 简单算法

我们的算法不是简单地计算一个单词在较大文本中的出现次数，而是将查找并识别特定单词在文本中存在的每个位置。我们解决这个问题的方法简短而简单，因此：

1.  搜索甚至会在文本中的单词中找到该单词。因此，如果我们要搜索“able”这个词，那么我们会在“comfortable”和“tablet”中找到它。
2.  搜索将不区分大小写。
3.  该算法基于朴素的字符串搜索方法。这意味着由于我们对单词和文本字符串中字符的性质一无所知，因此我们将使用蛮力检查文本的每个位置以查找搜索词的实例。

### 2.1. 执行

现在我们已经为我们的搜索定义了参数，让我们写一个简单的解决方案：

```java
public class WordIndexer {

    public List<Integer> findWord(String textString, String word) {
        List<Integer> indexes = new ArrayList<Integer>();
        String lowerCaseTextString = textString.toLowerCase();
        String lowerCaseWord = word.toLowerCase();

        int index = 0;
        while(index != -1){
            index = lowerCaseTextString.indexOf(lowerCaseWord, index);
            if (index != -1) {
                indexes.add(index);
                index++;
            }
        }
        return indexes;
    }
}
```

### 2.2. 测试解决方案

为了测试我们的算法，我们将使用莎士比亚的哈姆雷特中一段著名段落的片段并搜索出现五次的单词“or”：

```java
@Test
public void givenWord_whenSearching_thenFindAllIndexedLocations() {
    String theString;
    WordIndexer wordIndexer = new WordIndexer();

    theString = "To be, or not to be: that is the question: "
      + "Whether 'tis nobler in the mind to suffer "
      + "The slings and arrows of outrageous fortune, "
      + "Or to take arms against a sea of troubles, "
      + "And by opposing end them? To die: to sleep; "
      + "No more; and by a sleep to say we end "
      + "The heart-ache and the thousand natural shocks "
      + "That flesh is heir to, 'tis a consummation "
      + "Devoutly to be wish'd. To die, to sleep; "
      + "To sleep: perchance to dream: ay, there's the rub: "
      + "For in that sleep of death what dreams may come,";

    List<Integer> expectedResult = Arrays.asList(7, 122, 130, 221, 438);
    List<Integer> actualResult = wordIndexer.findWord(theString, "or");
    assertEquals(expectedResult, actualResult);
}
```

当我们运行测试时，我们得到了预期的结果。搜索“or”会产生以各种方式嵌入文本字符串中的五个实例：

```plaintext
index of 7, in "or"
index of 122, in "fortune"
index of 130, in "Or
index of 221, in "more"
index of 438, in "For"
```

在数学术语中，该算法具有O(m(nm))的大 O 表示法，其中m是单词的长度，n是文本字符串的长度。这种方法可能适用于几千个字符的 haystack 文本字符串，但如果有数十亿个字符，则速度将难以忍受。

## 3.改进算法

上面的简单示例演示了一种在文本字符串中搜索给定单词的天真、蛮力方法。因此，它适用于任何搜索词和任何文本。

如果我们事先知道搜索词不包含重复的字符模式，例如“aaa”，那么我们可以编写一个稍微更高效的算法。

在这种情况下，我们可以安全地避免进行备份以重新检查文本字符串中的每个位置作为潜在的起始位置。在我们调用 indexOf()方法之后，我们将简单地滑到刚好在找到的最新事件结束之后的位置。这个简单的调整产生了O(n)的最佳情况。

让我们看一下早期findWord()方法的这个增强版本。

```java
public List<Integer> findWordUpgrade(String textString, String word) {
    List<Integer> indexes = new ArrayList<Integer>();
    StringBuilder output = new StringBuilder();
    String lowerCaseTextString = textString.toLowerCase();
    String lowerCaseWord = word.toLowerCase();
    int wordLength = 0;

    int index = 0;
    while(index != -1){
        index = lowerCaseTextString.indexOf(lowerCaseWord, index + wordLength);  // Slight improvement
        if (index != -1) {
            indexes.add(index);
        }
        wordLength = word.length();
    }
    return indexes;
}
```

## 4。总结

在本教程中，我们介绍了一种不区分大小写的搜索算法，用于在较大的文本字符串中查找单词的所有变体。但是不要让这掩盖了一个事实，即JavaString类的 indexOf() 方法本质上是区分大小写的，例如可以区分“Bob”和“bob”。

总而言之， indexOf() 是一种查找隐藏在文本字符串中的字符序列的便捷方法，无需对子字符串操作进行任何编码。