---
layout: post
title:  从字符串中删除重复字符
category: java-string
copyright: java-string
excerpt: Java String
---

## 1. 概述

在本教程中，我们将讨论Java中有关如何从字符串中删除重复字符的几种技术。

对于每种技术，我们还将简要讨论其时间和空间复杂性。

## 2. 使用不同的

让我们首先使用Java 8中引入的distinct方法从字符串中删除重复项。

下面，我们从给定的字符串对象中获取一个IntS流的实例。然后，我们使用distinct方法删除重复项。最后，我们调用forEach方法来遍历不同的字符并将它们附加到我们的[StringBuilder](https://www.tuyucheng.com/java-string-builder-string-buffer)：

```java
StringBuilder sb = new StringBuilder();
str.chars().distinct().forEach(c -> sb.append((char) c));
```

时间复杂度：O(n)–循环的运行时间与输入字符串的大小成正比

辅助空间：O(n)-因为distinct在内部使用LinkedHashSet，我们还将生成的字符串存储在StringBuilder对象中

MaintainsOrder：是-因为LinkedHashSet维护其元素的顺序

而且，虽然Java 8如此出色地为我们完成了这项任务是件好事，但让我们将其与我们自己的努力进行比较。

## 3. 使用indexOf

从字符串中删除重复项的简单方法只涉及循环输入并使用[indexOf方法](https://www.tuyucheng.com/string/index-of)检查当前字符是否已存在于结果字符串中：

```java
StringBuilder sb = new StringBuilder();
int idx;
for (int i = 0; i < str.length(); i++) {
    char c = str.charAt(i);
    idx = str.indexOf(c, i + 1);
    if (idx == -1) {
        sb.append(c);
    }
}
```

时间复杂度：O(n * n)–对于每个字符，indexOf方法遍历剩余的字符串

辅助空间：O(n)–需要线性空间，因为我们使用StringBuilder来存储结果

维持秩序：是

此方法与第一种方法具有相同的空间复杂度，但执行速度要慢得多。

## 4. 使用字符数组

我们还可以通过将字符串转换为char数组然后遍历每个字符并将其与所有后续字符进行比较来从字符串中删除重复项。

正如我们在下面看到的，我们正在创建两个for循环并检查每个元素是否在字符串中重复。如果找到重复项，我们不会将其附加到StringBuilder：

```java
char[] chars = str.toCharArray();
StringBuilder sb = new StringBuilder();
boolean repeatedChar;
for (int i = 0; i < chars.length; i++) {
    repeatedChar = false;
    for (int j = i + 1; j < chars.length; j++) {
        if (chars[i] == chars[j]) {
            repeatedChar = true;
            break;
        }
    }
    if (!repeatedChar) {
        sb.append(chars[i]);
    }
}
```

时间复杂度：O(n * n)-我们有一个内部循环和一个外部循环都遍历输入字符串

辅助空间：O(n)–需要线性空间，因为chars变量存储字符串输入的新副本，我们还使用StringBuilder来保存结果

维持秩序：是

同样，与CoreJava产品相比，我们的第二次尝试表现不佳，但让我们看看下一次尝试的结果。

## 5. 使用排序

或者，可以通过对输入字符串进行排序以对重复字符进行分组来消除重复字符。为此，我们必须将字符串转换为char数组并使用Arrays对其进行排序。排序方法。最后，我们将迭代排序的char数组。

在每次迭代期间，我们将数组的每个元素与前一个元素进行比较。如果元素不同，那么我们会将当前字符附加到StringBuilder：

```java
StringBuilder sb = new StringBuilder();
if(!str.isEmpty()) {
    char[] chars = str.toCharArray();
    Arrays.sort(chars);

    sb.append(chars[0]);
    for (int i = 1; i < chars.length; i++) {
        if (chars[i] != chars[i - 1]) {
            sb.append(chars[i]);
        }
    }
}
```

时间复杂度：O(nlogn)-排序使用[双枢轴快速](https://www.tuyucheng.com/arrays-sortobject-vs-sortint)排序，它在许多数据集上提供O(nlogn)的性能

辅助空间：O(n)-因为toCharArray方法了输入字符串

维持秩序：否

让我们在最后一次尝试中再试一次。

## 6. 使用一套

从字符串中删除重复字符的另一种方法是使用Set。如果我们不关心输出字符串中字符的顺序，我们可以使用[HashSet](https://www.tuyucheng.com/java-hashset)。否则，我们可以使用LinkedHashSet来维护插入顺序。

在这两种情况下，我们将遍历输入字符串并将每个字符添加到Set中。一旦字符被插入到集合中，我们将迭代它以将它们添加到StringBuilder并返回结果字符串：

```java
StringBuilder sb = new StringBuilder();
Set<Character> linkedHashSet = new LinkedHashSet<>();

for (int i = 0; i < str.length(); i++) {
    linkedHashSet.add(str.charAt(i));
}

for (Character c : linkedHashSet) {
    sb.append(c);
}
```

时间复杂度：O(n)–循环的运行时间与输入字符串的大小成正比

辅助空间：O(n)–Set所需的空间取决于输入字符串的大小；另外，我们使用StringBuilder来存储结果

维持秩序：LinkedHashSet–是，HashSet–否

现在，我们已经匹配了CoreJava方法！发现这与distinct已经做的非常相似并不令人震惊。

## 7. 总结

在本文中，我们介绍了几种从Java中删除字符串中重复字符的方法。我们还研究了每种方法的时间和空间复杂度。

与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-string-algorithms-1)上获得。