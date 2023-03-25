## 1. 概述

在本教程中，我们介绍使用[Jackson](https://www.baeldung.com/jackson)(Java的JSON处理库)比较两个JSON对象。

## 2. Maven依赖

首先，我们添加[jackson-databind](https://search.maven.org/classic/#search|ga|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind") Maven依赖项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>
```

## 3. 使用Jackson比较两个JSON对象

我们将使用[ObjectMapper](https://static.javadoc.io/com.fasterxml.jackson.core/jackson-databind/2.9.8/com/fasterxml/jackson/databind/ObjectMapper.html)类将JSON对象读取为[JsonNode](https://static.javadoc.io/com.fasterxml.jackson.core/jackson-databind/2.9.8/com/fasterxml/jackson/databind/JsonNode.html)。

让我们创建一个ObjectMapper：

```java
ObjectMapper mapper = new ObjectMapper();
```

### 3.1 比较两个简单的JSON对象

首先我们从[JsonNode.equals](https://static.javadoc.io/com.fasterxml.jackson.core/jackson-databind/2.9.8/com/fasterxml/jackson/databind/JsonNode.html#equals-java.lang.Object-)方法开始，equals()方法执行完整(深度)比较。

假设我们有一个定义为s1变量的JSON字符串：

```json
{
    "employee":
    {
        "id": "1212",
        "fullName": "John Miles",
        "age": 34
    }
}
```

我们想将它与另一个JSON s2进行比较：

```json
{   
    "employee":
    {
        "id": "1212",
        "age": 34,
        "fullName": "John Miles"
    }
}
```

让我们将输入JSON读取为JsonNode并进行比较：

```java
assertEquals(mapper.readTree(s1), mapper.readTree(s2));
```

需要注意的是，即使输入JSON变量s1和s2中的属性顺序不同，equals()方法也会忽略顺序并将它们视为相等。

### 3.2 比较两个带有嵌套元素的JSON对象

接下来，我们演示如何比较两个具有嵌套元素的JSON对象。

首先我们定义JSON对象s1：

```json
{ 
    "employee":
    {
        "id": "1212",
        "fullName":"John Miles",
        "age": 34,
        "contact":
        {
            "email": "john@xyz.com",
            "phone": "9999999999"
        }
    }
}
```

正如我们所见，JSON包含一个嵌套元素contact。

我们想将它与定义的另一个JSON s2进行比较：

```json
{
    "employee":
    {
        "id": "1212",
        "age": 34,
        "fullName": "John Miles",
        "contact":
        {
            "email": "john@xyz.com",
            "phone": "9999999999"
        }
    }
}
```

让我们将输入JSON读取为JsonNode并进行比较：

```java
assertEquals(mapper.readTree(s1), mapper.readTree(s2));
```

同样，我们应该注意到equals()还可以将两个输入JSON对象与嵌套元素进行比较。

### 3.3 比较两个包含列表元素的JSON对象

同样，我们也可以比较两个包含列表元素的JSON对象。

考虑以下定义为s1的JSON：

```json
{
    "employee":
    {
        "id": "1212",
        "fullName": "John Miles",
        "age": 34,
        "skills": ["Java", "C++", "Python"]
    }
}
```

我们将它与另一个JSON s2进行比较：

```json
{
    "employee":
    {
        "id": "1212",
        "age": 34,
        "fullName": "John Miles",
        "skills": ["Java", "C++", "Python"] 
    } 
}
```

下面将输入JSON读取为JsonNode并进行比较：

```java
assertEquals(mapper.readTree(s1), mapper.readTree(s2));
```

重要的是要知道两个列表元素只有在它们具有完全相同的顺序的相同值时才会被比较为相等。

## 4. 使用自定义比较器比较两个 JSON 对象

[JsonNode.equals](https://static.javadoc.io/com.fasterxml.jackson.core/jackson-databind/2.9.8/com/fasterxml/jackson/databind/JsonNode.html#equals-java.lang.Object-)在大多数情况下都足够使用，不过Jackson还提供了[JsonNode.equals(comarator, JsonNode)](https://static.javadoc.io/com.fasterxml.jackson.core/jackson-databind/2.9.8/com/fasterxml/jackson/databind/JsonNode.html#equals-java.util.Comparator-com.fasterxml.jackson.databind.JsonNode-)来配置自定义Java Comparato对象。

### 4.1 自定义比较器来比较数值

让我们看看如何使用自定义Comparator来比较两个具有数值的JSON元素。

我们使用以下JSON作为输入s1：

```json
{
    "name": "John",
    "score": 5.0
}
```

并与另一个定义为s2的JSON进行比较：

```json
{
    "name": "John",
    "score": 5
}
```

我们需要观察输入s1和s2中的属性score的值是不一样的。让我们将输入JSON读取为JsonNode并进行比较：

```java
JsonNode actualObj1 = mapper.readTree(s1);
JsonNode actualObj2 = mapper.readTree(s2);

assertNotEquals(actualObj1, actualObj2);
```

请注意，这两个对象并不相等；标准的equals()方法会将值5.0和5视为不同。但是，我们可以使用自定义Comparator来比较值5和5.0并将它们视为相等。

首先我们创建一个Comparator来比较两个NumericNode对象：

```java
public class NumericNodeComparator implements Comparator<JsonNode> {
    @Override
    public int compare(JsonNode o1, JsonNode o2) {
        if (o1.equals(o2)){
           return 0;
        }
        if ((o1 instanceof NumericNode) && (o2 instanceof NumericNode)){
            Double d1 = ((NumericNode) o1).asDouble();
            Double d2 = ((NumericNode) o2).asDouble(); 
            if (d1.compareTo(d2) == 0) {
               return 0;
            }
        }
        return 1;
    }
}
```

接下来，让我们看看如何使用这个Comparator：

```java
NumericNodeComparator cmp = new NumericNodeComparator();
assertTrue(actualObj1.equals(cmp, actualObj2));
```

### 4.2 自定义比较器比较文本值

让我们看一下自定义比较器的另一个示例，用于对两个JSON值进行不区分大小写的比较。

这里使用以下JSON作为输入s1：

```json
{
    "name": "john", 
    "score": 5 
}
```

并与另一个定义为s2的JSON进行比较：

```json
{ 
    "name": "JOHN", 
    "score": 5 
}
```

正如我们所看到的，属性name在s1中是小写的，而在s2中是大写的。

同样，我们创建一个Comparator来比较两个TextNode对象：

```java
public class TextNodeComparator implements Comparator<JsonNode> {
    @Override
    public int compare(JsonNode o1, JsonNode o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        if ((o1 instanceof TextNode) && (o2 instanceof TextNode)) {
            String s1 = ((TextNode) o1).asText();
            String s2 = ((TextNode) o2).asText();
            if (s1.equalsIgnoreCase(s2)) {
                return 0;
            }
        }
        return 1;
    }
}
```

下面使用TextNodeComparator比较s1和s2：

```java
JsonNode actualObj1 = mapper.readTree(s1);
JsonNode actualObj2 = mapper.readTree(s2);

TextNodeComparator cmp = new TextNodeComparator();

assertNotEquals(actualObj1, actualObj2);
assertTrue(actualObj1.equals(cmp, actualObj2));
```

最后，我们可以看到，当输入的JSON元素值不完全相同但我们仍希望将它们视为相等时，比较两个JSON对象时使用自定义比较器对象非常有用。

## 5. 总结

在这篇快速文章中，我们了解了如何使用Jackson比较两个JSON对象并使用自定义比较器。