## 1. 简介

在本文中，我们将展示几种用于在大文本中搜索模式的算法。我们将使用提供的代码和简单的数学背景来描述每个算法。

请注意，提供的算法并不是在更复杂的应用程序中进行全文搜索的最佳方式。为了正确地进行全文搜索，我们可以使用[Solr](https://www.baeldung.com/full-text-search-with-solr)或[ElasticSearch](https://www.baeldung.com/elasticsearch-full-text-search-rest-api)。

## 2.算法

我们将从一个朴素的文本搜索算法开始，这是最直观的算法，有助于发现与该任务相关的其他高级问题。

### 2.1. 辅助方法

在我们开始之前，让我们定义一下我们在 Rabin Karp 算法中使用的计算素数的简单方法：

```java
public static long getBiggerPrime(int m) {
    BigInteger prime = BigInteger.probablePrime(getNumberOfBits(m) + 1, new Random());
    return prime.longValue();
}
private static int getNumberOfBits(int number) {
    return Integer.SIZE - Integer.numberOfLeadingZeros(number);
}

```

### 2.2. 简单的文本搜索

该算法的名称比任何其他解释都更好地描述了它。这是最自然的解决方案：

```java
public static int simpleTextSearch(char[] pattern, char[] text) {
    int patternSize = pattern.length;
    int textSize = text.length;

    int i = 0;

    while ((i + patternSize) <= textSize) {
        int j = 0;
        while (text[i + j] == pattern[j]) {
            j += 1;
            if (j >= patternSize)
                return i;
        }
        i += 1;
    }
    return -1;
}
```

该算法的思想很简单：遍历文本，如果模式的第一个字母匹配，则检查模式的所有字母是否都与文本匹配。

如果m是模式中字母的数量， n是文本中字母的数量，则该算法的时间复杂度为O(m(nm + 1))。

最坏的情况发生在String有很多部分出现的情况下：

```plaintext
Text: baeldunbaeldunbaeldunbaeldun
Pattern: baeldung
```

### 2.3. 拉宾卡普算法

如上所述，当模式很长且模式中有很多重复元素时，简单文本搜索算法效率非常低。

[Rabin Karp 算法](https://www.baeldung.com/cs/rabin-karp-algorithm)的思想是使用散列法在文本中找到一个模式。在算法开始时，我们需要计算模式的哈希值，稍后将在算法中使用。这个过程称为指纹计算，我们可以[在这里](https://en.wikipedia.org/wiki/Rabin–Karp_algorithm)找到详细的解释。

预处理步骤的重要之处在于它的时间复杂度是O(m)并且通过文本的迭代将花费O(n)这给出了整个算法的时间复杂度O(m+n)。

算法代码：

```java
public static int RabinKarpMethod(char[] pattern, char[] text) {
    int patternSize = pattern.length;
    int textSize = text.length;      

    long prime = getBiggerPrime(patternSize);

    long r = 1;
    for (int i = 0; i < patternSize - 1; i++) {
        r = 2;
        r = r % prime;
    }

    long[] t = new long[textSize];
    t[0] = 0;

    long pfinger = 0;

    for (int j = 0; j < patternSize; j++) {
        t[0] = (2  t[0] + text[j]) % prime;
        pfinger = (2  pfinger + pattern[j]) % prime;
    }

    int i = 0;
    boolean passed = false;

    int diff = textSize - patternSize;
    for (i = 0; i <= diff; i++) {
        if (t[i] == pfinger) {
            passed = true;
            for (int k = 0; k < patternSize; k++) {
                if (text[i + k] != pattern[k]) {
                    passed = false;
                    break;
                }
            }

            if (passed) {
                return i;
            }
        }

        if (i < diff) {
            long value = 2  (t[i] - r  text[i]) + text[i + patternSize];
            t[i + 1] = ((value % prime) + prime) % prime;
        }
    }
    return -1;

}
```

在最坏的情况下，该算法的时间复杂度为O(m(n-m+1))。然而，平均而言，该算法具有O(n+m)时间复杂度。

此外，该算法还有蒙特卡洛版本，速度更快，但可能会导致错误匹配(误报)。

### 2.4. Knuth-Morris-Pratt 算法

在简单文本搜索算法中，我们看到如果文本中有很多部分与模式匹配，算法会变得很慢。

[Knuth-Morris-Pratt 算法](https://www.baeldung.com/cs/knuth-morris-pratt)的思想是计算移位表，它为我们提供了应该在何处搜索模式候选者的信息。

KMP算法的Java实现：

```java
public static int KnuthMorrisPrattSearch(char[] pattern, char[] text) {
    int patternSize = pattern.length;
    int textSize = text.length;

    int i = 0, j = 0;

    int[] shift = KnuthMorrisPrattShift(pattern);

    while ((i + patternSize) <= textSize) {
        while (text[i + j] == pattern[j]) {
            j += 1;
            if (j >= patternSize)
                return i;
        }

        if (j > 0) {
            i += shift[j - 1];
            j = Math.max(j - shift[j - 1], 0);
        } else {
            i++;
            j = 0;
        }
    }
    return -1;
}
```

下面是我们如何计算移位表：

```java
public static int[] KnuthMorrisPrattShift(char[] pattern) {
    int patternSize = pattern.length;

    int[] shift = new int[patternSize];
    shift[0] = 1;

    int i = 1, j = 0;
    
    while ((i + j) < patternSize) {
        if (pattern[i + j] == pattern[j]) {
            shift[i + j] = i;
            j++;
        } else {
            if (j == 0)
                shift[i] = i + 1;
            
            if (j > 0) {
                i = i + shift[j - 1];
                j = Math.max(j - shift[j - 1], 0);
            } else {
                i = i + 1;
                j = 0;
            }
        }
    }
    return shift;
}
```

该算法的时间复杂度也是O(m+n)。

### 2.5. 简单的 Boyer-Moore 算法

Boyer 和 Moore 两位科学家提出了另一个想法。为什么不从右到左而不是从左到右比较模式与文本，同时保持移位方向相同：

```java
public static int BoyerMooreHorspoolSimpleSearch(char[] pattern, char[] text) {
    int patternSize = pattern.length;
    int textSize = text.length;

    int i = 0, j = 0;
    
    while ((i + patternSize) <= textSize) {
        j = patternSize - 1;
        while (text[i + j] == pattern[j]) {
            j--;
            if (j < 0)
                return i;
        }
        i++;
    }
    return -1;
}
```

正如预期的那样，这将在O(m  n)时间内运行。但是这个算法导致了发生和匹配启发式的实现，从而显着加快了算法的速度。我们可以在[这里](https://en.wikipedia.org/wiki/Boyer–Moore_string_search_algorithm)找到更多。

### 2.6. Boyer-Moore-Horspool 算法

Boyer-Moore 算法的启发式实现有多种变体，最简单的一种是 Horspool 变体。

这个版本的算法称为 Boyer-Moore-Horspool，这个变体解决了负偏移问题(我们可以在 Boyer-Moore 算法的描述中阅读负偏移问题)。

与 Boyer-Moore 算法一样，最坏情况下的时间复杂度为O(m  n)，而平均复杂度为 O(n)。空间使用不取决于模式的大小，而仅取决于字母表的大小，即 256，因为这是英文字母表中 ASCII 字符的最大值：

```java
public static int BoyerMooreHorspoolSearch(char[] pattern, char[] text) {

    int shift[] = new int[256];
    
    for (int k = 0; k < 256; k++) {
        shift[k] = pattern.length;
    }
    
    for (int k = 0; k < pattern.length - 1; k++){
        shift[pattern[k]] = pattern.length - 1 - k;
    }

    int i = 0, j = 0;

    while ((i + pattern.length) <= text.length) {
        j = pattern.length - 1;

        while (text[i + j] == pattern[j]) {
            j -= 1;
            if (j < 0)
                return i;
        }
        
        i = i + shift[text[i + pattern.length - 1]];
    }
    return -1;
}
```

## 4. 总结

在本文中，我们介绍了几种用于文本搜索的算法。由于一些算法需要更强的数学背景，我们试图表达每个算法的主要思想并以简单的方式提供。