## 1. 概述

在本教程中，我们将专注于从Map到String的转换以及相反的转换。

首先，我们将了解如何使用核心Java方法实现这些，然后，我们将使用一些第三方库。

## 2. 基本地图示例

在所有示例中，我们将使用相同的Map实现：

```java
Map<Integer, String> wordsByKey = new HashMap<>();
wordsByKey.put(1, "one");
wordsByKey.put(2, "two");
wordsByKey.put(3, "three");
wordsByKey.put(4, "four");
```

## 3.通过迭代将Map转换为String

让我们遍历Map中的所有键，并为每个键将键值组合附加到生成的[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer) 对象。

出于格式化目的，我们可以将结果用大括号括起来：

```java
public String convertWithIteration(Map<Integer, ?> map) {
    StringBuilder mapAsString = new StringBuilder("{");
    for (Integer key : map.keySet()) {
        mapAsString.append(key + "=" + map.get(key) + ", ");
    }
    mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
    return mapAsString.toString();
}
```

要检查我们是否正确转换了 Map，让我们运行以下测试：

```java
@Test
public void givenMap_WhenUsingIteration_ThenResultingStringIsCorrect() {
    String mapAsString = MapToString.convertWithIteration(wordsByKey);
    Assert.assertEquals("{1=one, 2=two, 3=three, 4=four}", mapAsString);
}
```

## 4.使用JavaStreams将映射转换为字符串

要使用流执行转换，我们首先需要从可用的Map键中创建一个流。

其次，我们将每个键映射到人类可读的String。

最后，我们加入这些值，为了方便起见，我们使用 Collectors.joining()方法添加一些格式化规则：

```java
public String convertWithStream(Map<Integer, ?> map) {
    String mapAsString = map.keySet().stream()
      .map(key -> key + "=" + map.get(key))
      .collect(Collectors.joining(", ", "{", "}"));
    return mapAsString;
}
```

## 5.使用 Guava将Map转换为字符串

让我们将[Guava](https://search.maven.org/search?q=g:com.google.guava AND a:guava)添加到我们的项目中，看看我们如何在一行代码中实现转换：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

要使用 Guava 的Joiner类执行转换 ，我们需要定义不同Map条目之间的分隔符以及键和值之间的分隔符：

```java
public String convertWithGuava(Map<Integer, ?> map) {
    return Joiner.on(",").withKeyValueSeparator("=").join(map);
}
```

## 6.使用 Apache Commons将映射转换为字符串

要使用[Apache Commons](https://search.maven.org/search?q=a:commons-collections4 AND g:org.apache.commons)，让我们先添加以下依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.2</version>
</dependency>
```

连接非常简单——我们只需要调用StringUtils.join方法：

```java
public String convertWithApache(Map map) {
    return StringUtils.join(map);
}
```

特别值得一提的是 Apache Commons 中可用的debugPrint方法。它对于调试目的非常有用。

当我们打电话时：

```java
MapUtils.debugPrint(System.out, "Map as String", wordsByKey);
```

调试文本将写入控制台：

```powershell
Map as String = 
{
    1 = one java.lang.String
    2 = two java.lang.String
    3 = three java.lang.String
    4 = four java.lang.String
} java.util.HashMap
```

## 7.使用流将字符串转换为映射

要执行从String到Map的转换，让我们定义拆分位置以及如何提取键和值：

```java
public Map<String, String> convertWithStream(String mapAsString) {
    Map<String, String> map = Arrays.stream(mapAsString.split(","))
      .map(entry -> entry.split("="))
      .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
    return map;
}
```

## 8.使用 Guava将字符串转换为映射

上面的一个更紧凑的版本是依靠 Guava 在一行中为我们做拆分和转换：

```java
public Map<String, String> convertWithGuava(String mapAsString) {
    return Splitter.on(',').withKeyValueSeparator('=').split(mapAsString);
}
```

## 9.总结

在本文中，我们了解了如何使用核心Java方法和第三方库将Map转换为String以及相反的方法。