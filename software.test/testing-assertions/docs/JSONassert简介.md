## 1. 概述

在本文中，我们将介绍[JSONAssert库](http://jsonassert.skyscreamer.org/)-一个专注于理解JSON数据并使用该数据编写复杂[JUnit](http://junit.org/junit4/)测试的库。

## 2. Maven依赖

首先，让我们添加Maven依赖项：

```xml
<dependency>
    <groupId>org.skyscreamer</groupId>
    <artifactId>jsonassert</artifactId>
    <version>1.5.0</version>
</dependency>
```

请在[此处](https://central.sonatype.com/artifact/org.skyscreamer/jsonassert/1.5.1)查看该库的最新版本。

## 3. 使用简单的JSON数据

### 3.1 使用LENIENT模式

让我们从一个简单的JSON字符串比较开始我们的测试：

```java
String actual = "{id:123, name:\"John\"}";
JSONAssert.assertEquals("{id:123,name:\"John\"}", actual, JSONCompareMode.LENIENT);
```

测试将作为预期的JSON字符串传递，与实际的JSON字符串相同。

**比较模式LENIENT意味着即使实际的JSON包含扩展字段，测试仍然会通过**：

```java
String actual = "{id:123, name:\"John\", zip:\"33025\"}";
JSONAssert.assertEquals("{id:123,name:\"John\"}", actual, JSONCompareMode.LENIENT);
```

正如我们所看到的，actual变量包含一个额外的字段zip，该字段不存在于预期的String中。尽管如此，测试还是会通过。

这个概念在应用程序开发中很有用。这意味着我们的API可以增长，根据需要返回额外的字段，而不会破坏现有的测试。

### 3.2 使用STRICT模式

使用STRICT比较模式可以轻松更改上一小节中提到的行为：

```java
String actual = "{id:123,name:\"John\"}";
JSONAssert.assertNotEquals("{name:\"John\"}", actual, JSONCompareMode.STRICT);
```

请注意上面示例中assertNotEquals()的使用。

### 3.3 使用Boolean代替JSONCompareMode

比较模式也可以通过使用接收boolean而不是JSONCompareMode的重载方法来定义，其中LENIENT = false和STRICT = true：

```java
String actual = "{id:123,name:\"John\",zip:\"33025\"}";
JSONAssert.assertEquals("{id:123,name:\"John\"}", actual, JSONCompareMode.LENIENT);
JSONAssert.assertEquals("{id:123,name:\"John\"}", actual, false);

actual = "{id:123,name:\"John\"}";
JSONAssert.assertNotEquals("{name:\"John\"}", actual, JSONCompareMode.STRICT);
JSONAssert.assertNotEquals("{name:\"John\"}", actual, true);
```

### 3.4 逻辑比较

如前所述，JSONAssert对数据进行逻辑比较。这意味着在处理JSON对象时元素的顺序无关紧要：

```java
String result = "{id:1,name:\"John\"}";
JSONAssert.assertEquals("{name:\"John\",id:1}", result, JSONCompareMode.STRICT);
JSONAssert.assertEquals("{name:\"John\",id:1}", result, JSONCompareMode.LENIENT);
```

不管严格与否，上述测试在这两种情况下都会通过。

另一个逻辑比较的例子可以通过对相同的值使用不同的类型来演示：

```java
JSONObject expected = new JSONObject();
JSONObject actual = new JSONObject();
expected.put("id", Integer.valueOf(12345));
actual.put("id", Double.valueOf(12345));

JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
```

这里首先要注意的是，我们使用JSONObject而不是像前面的例子那样使用String。接下来是我们使用Integer表示expected，使用Double表示actual。无论类型如何，测试都将通过，因为它们的逻辑值12345是相同的。

即使在我们有嵌套对象表示的情况下，这个库也能很好地工作：

```java
String result = "{id:1,name:\"Juergen\", address:{city:\"Hollywood\", state:\"LA\", zip:91601}}";
JSONAssert.assertEquals("{id:1,name:\"Juergen\", address:{city:\"Hollywood\", state:\"LA\", zip:91601}}", result, false);
```

### 3.5 用户指定消息的断言

所有的assertEquals()和assertNotEquals()方法都接收String消息作为第一个参数。此消息通过在测试失败的情况下提供有意义的信息来为我们的测试用例提供一些自定义：

```java
String actual = "{id:123,name:\"John\"}";
String failureMessage = "Only one field is expected: name";
try {
    JSONAssert.assertEquals(failureMessage, "{name:\"John\"}", actual, JSONCompareMode.STRICT);
} catch (AssertionError ae) {
    assertThat(ae.getMessage()).containsIgnoringCase(failureMessage);
}
```

在任何失败的情况下，整个错误消息将更有意义：

```shell
Only one field is expected: name 
Unexpected: id
```

第一行是用户指定的消息，第二行是库提供的附加消息。

## 4. 使用JSON数组

与JSON对象相比，JSON数组的比较规则略有不同。

### 4.1 数组中元素的顺序

第一个区别是**数组中元素的顺序在STRICT比较模式下必须完全相同**。但是，对于LENIENT比较模式，顺序无关紧要：

```java
String result = "[Alex, Barbera, Charlie, Xavier]";
JSONAssert.assertEquals("[Charlie, Alex, Xavier, Barbera]", result, JSONCompareMode.LENIENT);
JSONAssert.assertEquals("[Alex, Barbera, Charlie, Xavier]", result, JSONCompareMode.STRICT);
JSONAssert.assertNotEquals("[Charlie, Alex, Xavier, Barbera]", result, JSONCompareMode.STRICT);
```

这在API返回已排序元素数组的场景中非常有用，我们希望验证响应是否已排序。

### 4.2 数组中的扩展元素

另一个区别是**在处理JSON数组时不允许扩展元素**：

```java
String result = "[1,2,3,4,5]";
JSONAssert.assertEquals("[1,2,3,4,5]", result, JSONCompareMode.LENIENT);
JSONAssert.assertNotEquals("[1,2,3]", result, JSONCompareMode.LENIENT);
JSONAssert.assertNotEquals("[1,2,3,4,5,6]", result, JSONCompareMode.LENIENT);
```

上面的例子清楚地表明，即使使用LENIENT比较模式，预期数组中的元素也必须与实际数组中的元素完全匹配。添加或删除，即使是单个元素，也会导致失败。

### 4.3 数组特定操作

我们还有一些其他技术可以进一步验证数组的内容。

假设我们要验证数组的大小，这可以通过使用具体语法作为期望值来实现：

```java
String names = "{names:[Alex, Barbera, Charlie, Xavier]}";
JSONAssert.assertEquals(
    "{names:[4]}", 
    names, 
    new ArraySizeComparator(JSONCompareMode.LENIENT));
```

字符串“{names:[4\]}”指定数组的预期大小。

让我们看一下另一种比较技术：

```java
String ratings = "{ratings:[3.2,3.5,4.1,5,1]}";
JSONAssert.assertEquals(
    "{ratings:[1,5]}", 
    ratings, 
    new ArraySizeComparator(JSONCompareMode.LENIENT));
```

上面的示例验证数组中的所有元素的值必须介于[1，5\]之间，包括1和5。如果有任何值小于1或大于5，则上述测试将失败。

## 5. 高级比较示例

考虑我们的API返回多个id的用例，每个id都是一个整数值。这意味着所有的id都可以使用一个简单的正则表达式'\d'来验证。

上面的正则表达式可以与CustomComparator结合使用并应用于所有id的所有值。如果任何id与正则表达式不匹配，则测试将失败：

```java
JSONAssert.assertEquals("{entry:{id:x}}", "{entry:{id:1, id:2}}", 
    new CustomComparator(
    JSONCompareMode.STRICT, 
    new Customization("entry.id", 
    new RegularExpressionValueMatcher<Object>("\\d"))));

JSONAssert.assertNotEquals("{entry:{id:x}}", "{entry:{id:1, id:as}}", 
    new CustomComparator(JSONCompareMode.STRICT, 
    new Customization("entry.id", 
    new RegularExpressionValueMatcher<Object>("\\d"))));
```

上面示例中的“{id:x}”只是一个占位符-x可以被任何东西替换。因为它是将应用正则表达式模式'\d'的地方。由于id本身位于另一个字段entry中，因此Customization指定了id的位置，以便CustomComparator可以执行比较。

## 6. 总结

在这篇快速文章中，我们研究了JSONAssert可以提供帮助的各种场景。我们从一个超级简单的示例开始，然后进行了更复杂的比较。