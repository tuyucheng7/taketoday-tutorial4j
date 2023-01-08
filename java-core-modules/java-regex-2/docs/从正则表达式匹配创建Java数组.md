## 1. 概述

[在本教程中，我们将学习如何从正则表达式 ( regex](https://www.baeldung.com/regular-expressions-java) )输出创建数组。

## 2.简介

对于我们的示例，让我们解析一个长字符串。我们将找到具有 10 位电话号码的模式。然后我们会将输出生成为一个数组。

Oracle为其 regex 实现提供了java.util.regex包。我们将使用此包中提供的类进行演示。找到匹配项后，我们将获取该输出并创建一个数组。

数组是固定大小的变量。我们必须在使用它们之前声明它们的大小。如果数组没有正确实现，也有可能会浪费内存。为此，我们从List开始，然后将List动态转换为数组。

## 3.实施

让我们通过代码逐步实施此解决方案。首先，让我们创建一个[ArrayList](https://www.baeldung.com/java-arraylist)来存储匹配项：

```java
List<String> matchesList = new ArrayList<String>();
```

我们将存储一个长字符串，其中嵌入了电话号码，如下所示：

```java
String stringToSearch =
  "7801111111blahblah  780222222 mumbojumbo7803333333 thisnthat 7804444444";
```

我们使用compile()方法， [Pattern](https://www.baeldung.com/java-regex-pre-compile)类中的静态工厂方法 。它返回正则 表达式的等效模式对象：

```java
Pattern p1 = Pattern.compile("780{1}d{7}");
```

一旦我们有了一个Pattern对象，我们就可以使用 matcher()方法创建一个Matcher对象：

```java
Matcher m1 = p1.matcher(stringToSearch); 
```

在这里，我们可以使用Matcher 类中的find()方法，如果找到匹配项，它会返回一个布尔值：

```java
while (m1.find()) {
    matchesList.add(m1.group());
}
```

我们刚刚使用的group ()方法在Matcher类中。它生成一个表示匹配模式的字符串。

要将matchesList转换为数组，我们会找到匹配的项目数。然后我们在创建一个新的数组来存储结果时使用它：

```java
int sizeOfNewArray = matchesList.size(); 
String newArrayOfMatches[] = new String[sizeOfNewArray]; 
matchesList.toArray(newArrayOfMatches);
```

现在让我们看看我们的代码如何与一些示例一起工作。如果我们传递一个包含四个匹配模式的字符串，我们的代码会生成一个包含这四个匹配项的新字符串数组：

```java
RegexMatches rm = new RegexMatches();
String actual[] = rm.regexMatch("7801111211fsdafasdfa  7802222222  sadfsadfsda7803333333 sadfdasfasd 7804444444");

assertArrayEquals(new String[] {"7801111211", "7802222222", "7803333333", "7804444444"}, actual, "success");
```

如果我们传递一个没有匹配项的字符串，我们会得到一个空的字符串数组：

```java
String actual[] = rm.regexMatch("78011111fsdafasdfa  780222222  sadfsadfsda78033333 sadfdasfasd 7804444");

assertArrayEquals(new String[] {}, actual, "success");
```

## 4。总结

在本教程中，我们学习了如何在Java文本字符串中查找模式。我们还找到了一种在数组中列出输出的方法。