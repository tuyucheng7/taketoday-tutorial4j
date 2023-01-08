## 1. 概述

本快速教程将展示如何使用 Java查找两个字符串之间的差异。

对于本教程，我们将使用两个现有的Java库并比较它们解决此问题的方法。

## 2.问题

让我们考虑以下需求：我们想要找到字符串“ ABCDELMN”和“ABCFGLMN”之间的区别。

根据我们需要的输出格式，忽略编写自定义代码的可能性，我们发现了两个主要选项。

第一个是 Google 编写的名为 [diff-match-patch](https://github.com/google/diff-match-patch)的库。正如他们声称的那样，该库提供了用于同步纯文本的强大算法。

另一个选项是来自 Apache Commons Lang的[StringUtils类。](https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/StringUtils.java) 

让我们探讨一下这两者之间的区别。

## 3.差异匹配补丁

出于本文的目的，我们将使用[原始 Google 库的分支，因为原始库](https://search.maven.org/search?q=org.bitbucket.cowwoc diff-match-patch)的工件未在 Maven Central 上发布。此外，一些类名与原始代码库不同，更符合Java标准。

首先，我们需要在我们的pom.xml 文件中包含它的依赖项：

```xml
<dependency>
    <groupId>org.bitbucket.cowwoc</groupId>
    <artifactId>diff-match-patch</artifactId>
    <version>1.2</version>
</dependency>
```

然后，让我们考虑这段代码：

```java
String text1 = "ABCDELMN";
String text2 = "ABCFGLMN";
DiffMatchPatch dmp = new DiffMatchPatch();
LinkedList<Diff> diff = dmp.diffMain(text1, text2, false);
```

如果我们运行上面的代码——它产生了text1和text2之间的差异——打印变量diff将产生这个输出：

```java
[Diff(EQUAL,"ABC"), Diff(DELETE,"DE"), Diff(INSERT,"FG"), Diff(EQUAL,"LMN")]
```

事实上，输出将是一个Diff对象列表，每个对象都由一种操作类型(INSERT、DELETE或EQUAL)以及与该操作关联的文本部分构成。

在运行text2和text1之间的差异时，我们将得到以下结果：

```java
[Diff(EQUAL,"ABC"), Diff(DELETE,"FG"), Diff(INSERT,"DE"), Diff(EQUAL,"LMN")]
```

## 4.字符串工具

Apache Commons中的类具有更简单的方法。

首先，我们将[Apache Commons Lang 依赖](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3)项添加到我们的pom.xml 文件中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

然后，要使用 Apache Commons 查找两个文本之间的差异，我们将调用StringUtils#Difference：

```java
StringUtils.difference(text1, text2)
```

产生的输出将是一个简单的字符串：

```java
FGLMN
```

而运行text2和text1之间的差异将返回：

```java
DELMN
```

可以使用 StringUtils.indexOfDifference()增强这种简单的方法，它将返回 两个字符串开始不同的索引(在我们的例子中，字符串的第四个字符)。此索引可用于获取原始字符串的子字符串，以显示两个输入之间的共同点以及不同点。

## 5.性能

对于我们的基准测试，我们生成了一个包含 10,000 个字符串的列表，其中固定部分为 10 个字符，后跟20 个随机字母字符。

然后我们遍历列表并在列表的第n个元素和第n+1个元素之间执行差异：

```java
@Benchmark
public int diffMatchPatch() {
    for (int i = 0; i < inputs.size() - 1; i++) {
        diffMatchPatch.diffMain(inputs.get(i), inputs.get(i + 1), false);
    }
    return inputs.size();
}
@Benchmark
public int stringUtils() {
    for (int i = 0; i < inputs.size() - 1; i++) {
        StringUtils.difference(inputs.get(i), inputs.get(i + 1));
    }
    return inputs.size();
}
```

最后，让我们运行基准测试并比较两个库：

```java
Benchmark                                   Mode  Cnt    Score   Error  Units
StringDiffBenchmarkUnitTest.diffMatchPatch  avgt   50  130.559 ± 1.501  ms/op
StringDiffBenchmarkUnitTest.stringUtils     avgt   50    0.211 ± 0.003  ms/op
```

## 六，总结

就纯执行速度而言，StringUtils显然性能更高，尽管它只返回两个字符串开始不同的子字符串。

同时，Diff-Match-Patch以牺牲性能为代价提供了更彻底的比较结果。