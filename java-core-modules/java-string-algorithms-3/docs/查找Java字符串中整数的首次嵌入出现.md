## 一、概述

在本教程中，我们将探讨查找字符串中整数首次出现的不同方法。例如，给定字符串*“ba31dung123”*，我们只想找到第一个嵌入整数，即*31*。我们将看到如何使用正则表达式和纯 Java 来做到这一点。

## 2.正则表达式的解决方案

**[正则表达式 (regex)](https://www.baeldung.com/regular-expressions-java)是一种基于特定模式匹配和操作字符串的强大工具。**它们提供了一种指定字符串模式的简洁方法，我们可以使用它们来搜索特定的字符、单词或短语，替换文本并根据特定规则验证字符串。

### 2.1. 使用*匹配器*和*模式*类

java.util.regex包提供了两个*用于*与正则表达式进行模式匹配的主要类：

-   *匹配器*：**此类提供使用给定模式对字符串执行各种匹配操作的方法。***它是通过在Pattern*实例上调用*matcher()*方法获得的。
-   *Pattern*：**这个类表示编译后的正则表达式，并提供各种方法来对字符串执行匹配操作。***我们通过调用Pattern类的**compile()*方法从正则表达式创建模式。

我们可以利用它们来查找字符串中整数的第一次出现：

```java
@Test
void whenUsingPatternMatcher_findFirstInteger() {
    String str = "ba31dung123";
    Matcher matcher = Pattern.compile("\\d+").matcher(str);
    matcher.find();
    int i = Integer.valueOf(matcher.group());
    Assertions.assertEquals(31, i);
}复制
```

我们使用表达式*\\d+*来匹配一个或多个连续数字。

### 2.2. 使用*扫描仪*

**我们也可以使用[\*java.util.Scanner\* ](https://www.baeldung.com/java-scanner)类。**它是解析输入数据的强大工具。首先，我们将使用它的方法*useDelimiter()*来删除所有非数字。之后，我们可以使用*nextInt()*方法一个一个地提取数字：

```java
@Test
void whenUsingScanner_findFirstInteger() {
    int i = new Scanner("ba31dung123").useDelimiter("\\D+").nextInt();
    Assertions.assertEquals(31, i);
}复制
```

正则表达式*\\D+表示所有连续的非数字字符（与**\\d+*相反）。

### 2.3. 使用*拆分（）*

Java中的split()方法是String[*类*的](https://www.baeldung.com/string/split)*方法*。**它根据指定的分隔符将字符串拆分为子字符串，并返回子字符串数组。**分隔符可以是正则表达式或纯字符串：

```java
@Test
void whenUsingSplit_findFirstInteger() {
    String str = "ba31dung123";
    List<String> tokens = Arrays.stream(str.split("\\D+")).filter(s -> s.length() > 0).collect(Collectors.toList());
    Assertions.assertEquals(31, Integer.parseInt(tokens.get(0)));
}复制
```

我们使用与之前相同的正则表达式。但是，如果字符串以定界符开头，就像我们的例子一样，这个解决方案可以为我们提供一个空数组元素。[因此，为避免这种情况，我们使用Java Stream API](https://www.baeldung.com/java-streams)和*filter()*方法过滤我们的列表。

## 3.没有正则表达式的解决方案

我们看到正则表达式是解决这个问题的好方法，但没有它们我们也能做到。

让我们创建一个从字符串中提取第一个整数的方法：

```java
static Integer findFirstInteger(String s) {
    int i = 0;
    while (i < s.length() && !Character.isDigit(s.charAt(i))) {
        i++;
    }
    int j = i;
    while (j < s.length() && Character.isDigit(s.charAt(j))) {
        j++;
    }
    return Integer.parseInt(s.substring(i, j));
}复制
```

我们首先遍历字符串，*直到*找到第一个数字。然后**我们使用\*isDigit()\*方法来识别数字字符。**接下来，我们将第一个数字的索引存储在*i*变量中。然后，我们再次迭代，直到找到数字的结尾（等于第一个非数字字符）。*然后我们可以返回从i*到j的子串*。*

让我们测试一下我们的*findFirstInteger()*方法：

```java
@Test
void whenUsingCustomMethod_findFirstInteger() {
    String str = "ba31dung123";
    Integer i = FirstOccurrenceOfAnInteger.findFirstInteger(str);
    Assertions.assertEquals(31, i);
}复制
```

## 4。结论

在这篇快速文章中，我们探索了从字符串中提取第一个嵌入整数的不同替代方法。我们看到正则表达式在这个任务上有多种应用，但我们也可以在没有它的情况下完成。