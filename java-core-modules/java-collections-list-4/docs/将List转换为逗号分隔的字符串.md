## 1. 概述

[列表转换](https://www.baeldung.com/java-string-with-separator-to-list)仍然是一个热门话题，因为它是我们作为Java开发人员经常做的事情。在本教程中，我们将学习如何使用四种不同的方法将字符串列表转换为逗号分隔的字符串。

## 2. 使用Java8+

我们将使用自Java8 起可用的三个不同的类及其方法进行转换。

让我们将以下列表作为接下来示例的输入：

```java
List<String> arraysAsList = Arrays.asList("ONE", "TWO", "THREE");
```

### 2.1. 细绳

首先，我们将使用String类，它有许多用于处理String的实用程序，并提供转换方法join()：

```java
String commaSeparatedString = String.join(",", arraysAsList);

assertThat(commaSeparatedString).isEqualTo("ONE,TWO,THREE");
```

### 2.2. 字符串连接器

其次，我们将使用[StringJoiner](https://www.baeldung.com/java-string-joiner)类，它有一个接受CharSequence定界符作为参数的构造函数：

```java
StringJoiner stringJoiner = new StringJoiner(",");
arraysAsList.forEach(stringJoiner::add);
String commaSeparatedString = stringJoiner.toString();

assertThat(commaSeparatedString).isEqualTo("ONE,TWO,THREE");
```

还有另一个构造函数，它接受一个CharSequence定界符，一个CharSequence作为前缀，另一个作为后缀：

```java
StringJoiner stringJoinerWithDelimiterPrefixSuffix = new StringJoiner(",", "[", "]");
arraysAsList.forEach(stringJoinerWithDelimiterPrefixSuffix::add);
String commaSeparatedStringWithDelimiterPrefixSuffix = stringJoinerWithDelimiterPrefixSuffix.toString();

assertThat(commaSeparatedStringWithDelimiterPrefixSuffix).isEqualTo("[ONE,TWO,THREE]");
```

### 2.3. 收藏家

第三，[Collectors](https://www.baeldung.com/java-list-to-string#custom-implementation-using-collectors)类提供了各种具有不同签名的实用程序和joining()方法。

首先让我们看一下如何使用Collectors.joining()方法将collect()方法应用于Stream ，该方法将CharSequence定界符作为输入： 

```java
String commaSeparatedUsingCollect = arraysAsList.stream()
  .collect(Collectors.joining(","));

assertThat(commaSeparatedUsingCollect).isEqualTo("ONE,TWO,THREE");
```

在下一个示例中，我们将看到如何使用map()方法将列表中的每个对象转换为字符串，然后应用方法collect()和Collectors。加入()：

```java
String commaSeparatedObjectToString = arraysAsList.stream()
  .map(Object::toString)
  .collect(Collectors.joining(","));

assertThat(commaSeparatedObjectToString).isEqualTo("ONE,TWO,THREE");
```

接下来，我们将使用map()方法将列表元素转换为字符串，然后应用方法collect()和Collectors.joining()：

```java
String commaSeparatedStringValueOf = arraysAsList.stream()
  .map(String::valueOf)
  .collect(Collectors.joining(","));

assertThat(commaSeparatedStringValueOf).isEqualTo("ONE,TWO,THREE");
```

现在，让我们像上面那样使用map()，然后是Collectors。joining()方法，输入一个CharSequence分隔符，一个CharSequence作为前缀，一个CharSequence作为后缀：

```java
String commaSeparatedStringValueOfWithDelimiterPrefixSuffix = arraysAsList.stream()
  .map(String::valueOf)
  .collect(Collectors.joining(",", "[", "]"));

assertThat(commaSeparatedStringValueOfWithDelimiterPrefixSuffix).isEqualTo("[ONE,TWO,THREE]");
```

最后，我们将看到如何使用reduce()方法而不是collect()来转换列表：

```java
String commaSeparatedUsingReduce = arraysAsList.stream()
  .reduce((x, y) -> x + "," + y)
  .get();

assertThat(commaSeparatedUsingReduce).isEqualTo("ONE,TWO,THREE");
```

## 3. 使用 Apache Commons Lang

或者，我们也可以使用[Apache Commons Lang](https://www.baeldung.com/java-list-to-string#using-an-external-library)库提供的实用程序类来代替Java类。

我们必须向我们的 pom.xml文件添加[依赖](https://search.maven.org/search?q=g:org.apache.commons a:commons-lang3)项才能使用 Apache 的StringUtils类：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.0</version>
</dependency>
```

join()方法有多种实现，可以接受输入，例如一系列元素、值的迭代器，以及多种形式的分隔符，例如String或char：

```java
String commaSeparatedString = StringUtils.join(arraysAsList, ",");

assertThat(commaSeparatedString).isEqualTo("ONE,TWO,THREE");
```

如果传递给join()的信息是一个Object数组，它还需要一个int作为 startIndex 和一个int作为 endIndex：

```java
String commaSeparatedStringIndex = StringUtils.join(arraysAsList.toArray(), ",", 0, 3);

assertThat(commaSeparatedStringIndex).isEqualTo("ONE,TWO,THREE");
```

## 4.使用弹簧核心

Spring Core 库同样提供了一个实用程序类，其中包含用于此类转换的方法。

我们首先向我们的 pom.xml文件添加一个[依赖](https://search.maven.org/search?q=g:org.springframework a:spring-core)项以使用 Spring 的核心StringUtils类：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.22</version>
</dependency>
```

Spring 的 Core StringUtils类提供了一个方法collectionToCommaDelimitedString()，它以逗号作为默认分隔符，以一个要转换的Collection作为参数：

```java
String collectionToCommaDelimitedString = StringUtils.collectionToCommaDelimitedString(arraysAsList);

assertThat(collectionToCommaDelimitedString).isEqualTo("ONE,TWO,THREE");
```

第二种方法collectionToDelimitedString()将要转换的Collection和String分隔符作为参数：

```java
String collectionToDelimitedString = StringUtils.collectionToDelimitedString(arraysAsList, ",");

assertThat(collectionToDelimitedString).isEqualTo("ONE,TWO,THREE");
```

## 5.使用谷歌番石榴

最后，我们将使用 Google Guava 库。

我们必须向我们的 pom.xml文件添加[依赖](https://search.maven.org/search?q=g:com.google.guava a:guava)项 才能使用 Google 的 Guava Joiner类：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>
```

Google 的 Guava Joiner类提供了多种我们可以应用的方法。

第一个方法是on()，它将一个String作为分隔符参数，然后第二个方法是join()方法，它将具有要转换的值的Iterable 作为参数：

```java
String commaSeparatedString = Joiner.on(",")
  .join(arraysAsList);

assertThat(commaSeparatedString).isEqualTo("ONE,TWO,THREE");
```

让我们为下一个示例使用另一个包含一些空值的列表：

```java
List<String> arraysAsListWithNull = Arrays.asList("ONE", null, "TWO", null, "THREE");
```

鉴于此，我们可以在on()和join()之间使用其他方法，其中之一就是skipNulls()方法。我们可以使用它来避免从Iterable内部转换空值：

```java
String commaSeparatedStringSkipNulls = Joiner.on(",")
  .skipNulls()
  .join(arraysAsListWithNull);

assertThat(commaSeparatedStringSkipNulls).isEqualTo("ONE,TWO,THREE");
```

另一种选择是使用useForNull()，它以一个String值作为参数来替换Iterable内部的空值进行转换：

```java
String commaSeparatedStringUseForNull = Joiner.on(",")
  .useForNull(" ")
  .join(arraysAsListWithNull);

assertThat(commaSeparatedStringUseForNull).isEqualTo("ONE, ,TWO, ,THREE");
```

## 六，总结

在本文中，我们看到了将List of String转换为逗号分隔String的各种示例。最后，由我们来选择更适合我们目的的库和实用程序类。