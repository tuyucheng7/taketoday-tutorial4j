## 1. 概述

在本文中，我们将学习如何在多行中分解 YAML 字符串。

为了解析和测试我们的 YAML 文件，我们将使用[SnakeYAML 库](https://www.baeldung.com/java-snake-yaml)。

## 2. 多行字符串

在我们开始之前，让我们创建一个方法来简单地将 YAML 密钥从文件读取到String中：

```java
String parseYamlKey(String fileName, String key) {
    InputStream inputStream = this.getClass()
      .getClassLoader()
      .getResourceAsStream(fileName);
    Map<String, String> parsed = yaml.load(inputStream);
    return parsed.get(key);
}
```

在接下来的小节中，我们将研究一些将字符串拆分为多行的策略。

我们还将了解 YAML 如何处理由块开头和结尾处的空行表示的前导和结束换行符。

## 3.文字风格

文字运算符由竖线(“|”)符号表示。它保留我们的换行符，但将字符串末尾的空行减少为单个换行符。

让我们看一下 YAML 文件literal.yaml：

```plaintext
key: |
  Line1
  Line2
  Line3
```

我们可以看到我们的换行符被保留了下来：

```java
String key = parseYamlKey("literal.yaml", "key");
assertEquals("Line1nLine2nLine3", key);
```

接下来，让我们看一下literal2.yaml，它有一些开头和结尾的换行符：

```plaintext
key: |


  Line1

  Line2

  Line3


...
```

我们可以看到每个换行符都存在，除了结束换行符，它减少到一个：

```java
String key = parseYamlKey("literal2.yaml", "key");
assertEquals("nnLine1nnLine2nnLine3n", key);
```

接下来，我们将讨论 block chomping 以及它如何让我们更好地控制开始和结束换行符。

我们可以使用两种 chomping 方法来更改默认行为：keep 和 strip。

### 3.1. 保持

正如我们在literal_keep.yaml中看到的那样，Keep 由“+”表示：

```plaintext
key: |+
  Line1
  Line2
  Line3


...
```

通过覆盖默认行为，我们可以看到保留了每个结束空行：

```java
String key = parseYamlKey("literal_keep.yaml", "key");
assertEquals("Line1nLine2nLine3nn", key);
```

### 3.2. 跳闸

正如我们在literal_strip.yaml中看到的那样，条带由“-”表示：

```plaintext
key: |-
  Line1
  Line2
  Line3

...
```

正如我们所料，这会导致删除每个结尾的空行：

```java
String key = parseYamlKey("literal_strip.yaml", "key");
assertEquals("Line1nLine2nLine3", key);
```

## 4.折叠式

正如我们在folded.yaml中看到的那样，折叠运算符由“>”表示：

```plaintext
key: >
  Line1
  Line2
  Line3
```

默认情况下，对于连续的非空行，换行符将替换为空格字符：

```java
String key = parseYamlKey("folded.yaml", "key");
assertEquals("Line1 Line2 Line3", key);
```

让我们看一个类似的文件folded2.yaml，它有几个结尾空行：

```plaintext
key: >
  Line1
  Line2


  Line3


...
```

我们可以看到保留了空行，但结束换行符也减少为一个：

```java
String key = parseYamlKey("folded2.yaml", "key");
assertEquals("Line1 Line2nnLine3n", key);
```

我们应该记住block chomping 影响折叠样式的方式与它影响文字样式的方式相同。

## 5.报价

让我们快速看一下在双引号和单引号的帮助下拆分字符串。

### 5.1. 双引号

使用双引号，我们可以使用“ n ”轻松创建多行字符串：

```plaintext
key: "Line1nLine2nLine3"
String key = parseYamlKey("plain_double_quotes.yaml", "key");
assertEquals("Line1nLine2nLine3", key);
```

### 5.2. 单引号

另一方面，单引号将“ n ”视为字符串的一部分，因此插入换行符的唯一方法是使用空行：

```plaintext
key: 'Line1nLine2

  Line3'
String key = parseYamlKey("plain_single_quotes.yaml", "key");
assertEquals("Line1nLine2nLine3", key);
```

## 六. 总结

在本快速教程中，我们通过快速实用的示例研究了在多行上拆分 YAML 字符串的多种方法。