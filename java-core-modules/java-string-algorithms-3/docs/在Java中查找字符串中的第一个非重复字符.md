## 1. 概述

在本教程中，我们将研究在Java中查找字符串中第一个非重复字符的不同方法。

我们还将尝试分析解决方案的运行时间复杂性。

## 2.问题陈述

给定一个字符串作为输入，找到字符串中的第一个非重复字符。这里有一些例子：

示例 1： 摇篮曲

在此示例中，L 重复了三次。第一个非重复字符遇到发生在我们到达字符u 时。 

示例 2：Baeldung

此示例中的所有字符均不重复。根据问题陈述，我们选择第一个，B。

示例 3：工作

此示例中没有非重复字符 - 所有字符都重复一次。所以这里的输出是空的。

最后，这里有一些额外的要点要记住这个问题：

-   输入字符串可以是任意长度，并且可以包含大写和小写字符的混合
-   我们的解决方案应该处理空输入或空输入
-   输入 String 可以没有非重复字符，或者换句话说，可以有一个所有字符至少重复一次的输入，在这种情况下输出为null

有了这种理解，让我们尝试解决这个问题。

## 3.解决方案

### 3.1. 蛮力解决方案

首先，我们尝试找出一个蛮力解决方案来查找字符串的第一个非重复字符。我们从字符串的开头开始，一次取一个字符，并将该字符与字符串中的每个其他字符进行比较。如果找到匹配项，则意味着该字符在字符串的其他地方重复出现，因此我们继续处理下一个字符。如果没有匹配的字符，我们就找到了解决方案，我们就用这个字符退出程序。

代码如下所示：

```java
public Character firstNonRepeatingCharBruteForceNaive(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    for (int outer = 0; outer < inputString.length(); outer++) {
        boolean repeat = false;
        for (int inner = 0; inner < inputString.length(); inner++) {
            if (inner != outer && inputString.charAt(outer) == inputString.charAt(inner)) {
                repeat = true;
                break;
            }
        }
        if (!repeat) {
            return inputString.charAt(outer);
        }
    }
    return null;
}
```

 由于我们有两个嵌套循环，上述解决方案的[时间复杂度为 O(n²)。](https://www.baeldung.com/java-algorithm-complexity)对于我们访问的每个字符，我们正在访问输入字符串的所有字符。

下面还介绍了相同代码的更紧凑的解决方案，它利用了String类的[lastIndexOf ](https://www.baeldung.com/string/last-index-of)方法。当我们找到一个字符，其在字符串中的第一个索引也是最后一个索引时，就确定该字符仅存在于字符串中的该索引处，因此成为第一个非重复字符。

这个的时间复杂度也是O(n)。应该注意的是lastIndexOf方法在另一个 O(n) 时间运行，除了已经在运行的外循环，我们一次获取一个字符，从而使它成为一个 O(n²) 解决方案，类似于上一个。

```java
public Character firstNonRepeatingCharBruteForce(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    for (Character c : inputString.toCharArray()) {
        int indexOfC = inputString.indexOf(c);
        if (indexOfC == inputString.lastIndexOf(c)) {
            return c;
        }
    }
    return null;
}
```

### 3.2. 优化方案

让我们看看我们是否可以做得更好。我们讨论的方法的瓶颈在于，我们将每个字符与字符串中出现的每个其他字符进行比较，并且我们会继续此操作，除非我们到达字符串的末尾或找到答案。相反，如果我们能记住每个字符出现的次数，我们就不需要每次都进行比较，而只需查找该字符的出现频率即可。为此，我们可以使用Map ，更具体地说，是[HashMap](https://www.baeldung.com/java-hashmap)。

该地图会将字符存储为键，并将其频率存储在其值中。当我们访问每个角色时，我们有两个选择：

1.  如果角色已经出现在地图中，我们将当前位置附加到它的值
2.  如果该角色没有出现在目前构建的地图中，则这是一个新角色，我们增加该角色出现的次数

在我们完成对整个字符串的计算后，我们有一个映射告诉我们字符串中每个字符的计数。现在剩下我们要做的就是再次遍历字符串，地图中值大小为 1 的第一个字符就是我们的答案。

这是代码的样子：

```java
public Character firstNonRepeatingCharWithMap(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    Map<Character, Integer> frequency = new HashMap<>();
    for (int outer = 0; outer < inputString.length(); outer++) {
        char character = inputString.charAt(outer);
        frequency.put(character, frequency.getOrDefault(character, 0) + 1);
    }
    for (Character c : inputString.toCharArray()) {
        if (frequency.get(c) == 1) {
            return c;
        }
    }
    return null;
}
```

上面的解决方案要快得多，因为在地图上查找是一个常量时间操作，或 O(1)。这意味着获得结果的时间不会随着输入字符串大小的增加而增加。

### 3.3. 优化方案的补充说明

我们应该讨论关于我们之前讨论的优化解决方案的一些注意事项。最初的问题假定输入可以是任意长度并且可以包含任何字符。这使得为查找目的选择Map变得高效。

但是，如果我们可以将输入字符集限制为仅小写字符/大写字符/英文字母字符等，那么在 map 上使用固定大小的数组将是更好的设计选择。

例如，如果输入仅限于小写英文字符，我们可以使用大小为 26 的数组，其中数组中的每个索引都指向一个字母表，值可以表示字符在字符串中的出现频率。最后，字符串中数组中值为 1 的第一个字符就是答案。这是它的代码：

```java
public Character firstNonRepeatingCharWithArray(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    int[] frequency = new int[26];
    for (int outer = 0; outer < inputString.length(); outer++) {
        char character = inputString.charAt(outer);
        frequency[character - 'a']++;
    }
    for (Character c : inputString.toCharArray()) {
        if (frequency[c - 'a'] == 1) {
            return c;
        }
    }
    return null;
}
```

请注意，时间复杂度仍然是 O(n)，但我们将空间复杂度提高到了常量空间。这是因为，无论字符串的长度如何，我们用来存储频率的辅助空间(数组)的长度都是恒定的。

## 4。总结

在本文中，我们讨论了查找字符串中第一个非重复字符的不同方法。