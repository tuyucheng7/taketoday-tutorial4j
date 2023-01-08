## 1. 概述

Java 8 引入了一个新的Stream API，它可以让我们以声明的方式处理数据。

在这篇简短的文章中，我们将学习如何使用Stream API 将逗号分隔的字符串拆分为字符串列表，以及如何将字符串数组连接到逗号分隔的字符串中。

我们还将了解如何使用Stream API 将字符串数组转换为映射。

几乎所有时候我们都会遇到这样的情况，我们需要迭代一些Java 集合并根据一些过滤逻辑过滤集合。在针对此类情况的传统方法中，我们会使用大量循环和 if-else 操作来获得所需的结果。

如果你想阅读有关Stream API 的更多信息，请查看[这篇文章](https://www.baeldung.com/java-8-streams)。

## 2. 使用Stream API连接字符串

让我们使用Stream API 创建一个函数，它将一个String数组连接成一个逗号分隔的String：

```java
public static String join(String[] arrayOfString){
    return Arrays.asList(arrayOfString)
      .stream()
      //.map(...)
      .collect(Collectors.joining(","));
}
```

这里需要注意的地方：

-   stream()函数将任何Collection转换为数据流
-   map()函数用于处理数据
-   还有另一个函数，名为filter()，我们可以在其中包含过滤条件

在某些情况下，我们可能希望加入具有固定前缀和后缀的字符串。使用Stream API，我们可以通过以下方式做到这一点：

```java
public static String joinWithPrefixPostfix(String[] arrayOfString){
    return Arrays.asList(arrayOfString)
      .stream()
      //.map(...)
      .collect(Collectors.joining(",","[","]"));
}
```

正如我们在Collectors.joining()方法中看到的那样，我们将前缀声明为“[”，将后缀声明为“]”；因此生成的字符串将以声明的[…..]格式创建。

## 3.使用Stream API拆分字符串

现在，让我们创建一个函数，使用Stream API将逗号分隔的字符串拆分为字符串列表：

```java
public static List<String> split(String str){
    return Stream.of(str.split(","))
      .map (elem -> new String(elem))
      .collect(Collectors.toList());
}
```

也可以使用Stream API将String直接转换为Character列表：

```java
public static List<Character> splitToListOfChar(String str) {
    return str.chars()
      .mapToObj(item -> (char) item)
      .collect(Collectors.toList());
}
```

这里要注意的一个有趣事实是chars()方法将String转换为Integer流，其中每个Integer值表示每个Char序列的ASCII值。这就是为什么我们需要在mapToObj()方法中显式转换映射器对象。

## 4.使用Stream API映射的字符串数组

我们还可以使用split 和 Collectors.toMap将String数组转换为映射，前提是数组中的每个项目都包含一个由分隔符连接的键值实体：

```java
public static Map<String, String> arrayToMap(String[] arrayOfString) {
	return Arrays.asList(arrayOfString)
	  .stream()
	  .map(str -> str.split(":"))
	  .collect(toMap(str -> str[0], str -> str[1]));
}
```

这里，“:”是String数组中所有元素的键值分隔符。

请记住，为了避免编译错误，我们需要确保代码是使用Java1.8 编译的。为此，我们需要在 pom.xml中添加以下插件：

```xml
<build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.3</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>
    </plugins>        
</build>
```

## 5. 测试

由于我们已经完成了函数的创建，让我们创建测试用例来验证结果。

首先，让我们测试一下我们的简单连接方法：

```java
@Test
public void givenArray_transformedToStream_convertToString() {
    String[] programmingLanguages = {"java", "python", "nodejs", "ruby"};
    String expectation = "java,python,nodejs,ruby";

    String result  = JoinerSplitter.join(programmingLanguages);
    assertEquals(result, expectation);
}
```

接下来，让我们创建另一个来测试我们简单的拆分功能：

```java
@Test
public void givenString_transformedToStream_convertToList() {
    String programmingLanguages = "java,python,nodejs,ruby";

    List<String> expectation = new ArrayList<>();
    expectation.add("java");
    expectation.add("python");
    expectation.add("nodejs");
    expectation.add("ruby");

    List<String> result  = JoinerSplitter.split(programmingLanguages);

    assertEquals(result, expectation);
}
```

最后，让我们测试我们的 String数组映射功能：

```java
@Test
public void givenStringArray_transformedToStream_convertToMap() {

    String[] programming_languages = new String[] {"language:java","os:linux","editor:emacs"};
    
    Map<String,String> expectation=new HashMap<>();
    expectation.put("language", "java");
    expectation.put("os", "linux");
    expectation.put("editor", "emacs");
    
    Map<String, String> result = JoinerSplitter.arrayToMap(programming_languages);
    assertEquals(result, expectation);
    
}
```

同样，我们需要创建其余的测试用例。

## 六，总结

Stream API 为我们提供了复杂的数据处理技术。这种新的代码编写方式在多线程环境下的堆内存管理方面非常高效。