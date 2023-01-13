## 1. 概述

在本教程中，我们将学习如何使用 Gson 序列化和反序列化原始值。Google 开发了 Gson 库来序列化和反序列化 JSON。此外，我们将了解 Gson 库在处理基元时的一些特殊问题。

另一方面，如果我们需要使用数组、集合、嵌套对象或其他定制，我们还有关于 [使用 Gson 序列化和使用 Gson](https://www.baeldung.com/gson-serialization-guide)反[序列](https://www.baeldung.com/gson-deserialization-guide)化的额外教程。

## 2.Maven依赖

要使用 Gson，我们必须将[Gson](https://search.maven.org/search?q=g:com.google.code.gson AND a:gson)依赖项添加到 pom：

```xml
<dependency> 
    <groupId>com.google.code.gson</groupId> 
    <artifactId>gson</artifactId> 
    <version>2.8.5</version> 
</dependency>
```

## 3. 序列化原始类型

使用 Gson 进行序列化非常简单。我们将使用以下模型作为示例：

```java
public class PrimitiveBundle {
    public byte byteValue;
    public short shortValue;
    public int intValue;
    public long longValue;
    public float floatValue;
    public double doubleValue;
    public boolean booleanValue;
    public char charValue;
}
```

首先，让我们用一些测试值初始化一个实例：

```java
PrimitiveBundle primitiveBundle = new PrimitiveBundle();
primitiveBundle.byteValue = (byte) 0x00001111;
primitiveBundle.shortValue = (short) 3;
primitiveBundle.intValue = 3;
primitiveBundle.longValue = 3;
primitiveBundle.floatValue = 3.5f;
primitiveBundle.doubleValue = 3.5;
primitiveBundle.booleanValue = true;
primitiveBundle.charValue = 'a';
```

接下来，我们可以序列化它：

```java
Gson gson = new Gson();
String json = gson.toJson(primitiveBundle);
```

最后，我们可以看到序列化后的结果：

```plaintext
{  
   "byteValue":17,
   "shortValue":3,
   "intValue":3,
   "longValue":3,
   "floatValue":3.5,
   "doubleValue":3.5,
   "booleanValue":true,
   "charValue":"a"
}
```

我们应该注意示例中的一些细节。对于初学者来说，字节值不像在模型中那样被序列化为一串位。除此之外，short、int 和 long 之间没有区别。此外，float 和 double 之间没有区别。

另一件需要注意的事情是字符串表示字符值。

其实最后这三件事和Gson没有任何关系，而是JSON的定义方式。

### 3.1. 序列化特殊浮点值

Java 有常量 Float.POSITIVE_INFINITY和NEGATIVE_INFINITY来表示无穷大。Gson 无法序列化这些特殊值：

```java
public class InfinityValuesExample {
    public float negativeInfinity;
    public float positiveInfinity;
}
InfinityValuesExample model = new InfinityValuesExample();
model.negativeInfinity = Float.NEGATIVE_INFINITY;
model.positiveInfinity = Float.POSITIVE_INFINITY;

Gson gson = new Gson();
gson.toJson(model);
```

尝试这样做会引发IllegalArgumentException。

尝试序列化NaN 也会引发IllegalArgumentException，因为 JSON 规范不允许使用此值。

同理，尝试序列化 Double。POSITIVE_INFINITY、NEGATIVE_INFINITY 或 NaN也会抛出IllegalArgumentException。

## 4.反序列化原始类型

现在让我们看一下如何反序列化在上一个示例中获得的 JSON 字符串。

反序列化和序列化一样简单：

```java
Gson gson = new Gson();
PrimitiveBundle model = gson.fromJson(json, PrimitiveBundle.class);
```

最后，我们可以验证模型包含所需的值：

```java
assertEquals(17, model.byteValue);
assertEquals(3, model.shortValue);
assertEquals(3, model.intValue);
assertEquals(3, model.longValue);
assertEquals(3.5, model.floatValue, 0.0001);
assertEquals(3.5, model.doubleValue, 0.0001);
assertTrue(model.booleanValue);
assertEquals('a', model.charValue);

```

### 4.1. 反序列化字符串值

当将有效值放入字符串中时，Gson 会解析它并按预期处理它：

```java
String json = "{"byteValue": "15", "shortValue": "15", "
  + ""intValue": "15", "longValue": "15", "floatValue": "15.0""
  + ", "doubleValue": "15.0"}";

Gson gson = new Gson();
PrimitiveBundleInitialized model = gson.fromJson(json, PrimitiveBundleInitialized.class);

assertEquals(15, model.byteValue);
assertEquals(15, model.shortValue);
assertEquals(15, model.intValue);
assertEquals(15, model.longValue);
assertEquals(15, model.floatValue, 0.0001);
assertEquals(15, model.doubleValue, 0.0001);
```

值得注意的是，字符串值不能反序列化为布尔类型。

### 4.2. 反序列化空字符串值

另一方面，让我们尝试用空字符串反序列化以下 JSON：

```java
String json = "{"byteValue": "", "shortValue": "", "
  + ""intValue": "", "longValue": "", "floatValue": """
  + ", "doubleValue": ""}";

Gson gson = new Gson();
gson.fromJson(json, PrimitiveBundleInitialized.class);
```

这会引发JsonSyntaxException，因为在反序列化基元时不需要空字符串。

### 4.3. 反序列化空值

尝试反序列化值为null的字段将导致 Gson 忽略该字段。例如，使用以下类：

```java
public class PrimitiveBundleInitialized {
    public byte byteValue = (byte) 1;
    public short shortValue = (short) 1;
    public int intValue = 1;
    public long longValue = 1L;
    public float floatValue = 1.0f;
    public double doubleValue = 1;
}
```

Gson 忽略空字段：

```java
String json = "{"byteValue": null, "shortValue": null, "
  + ""intValue": null, "longValue": null, "floatValue": null"
  + ", "doubleValue": null}";

Gson gson = new Gson();
PrimitiveBundleInitialized model = gson.fromJson(json, PrimitiveBundleInitialized.class);

assertEquals(1, model.byteValue);
assertEquals(1, model.shortValue);
assertEquals(1, model.intValue);
assertEquals(1, model.longValue);
assertEquals(1, model.floatValue, 0.0001);
assertEquals(1, model.doubleValue, 0.0001);
```

### 4.4. 反序列化溢出的值

这是Gson意外处理的一个非常有趣的案例。尝试反序列化：

```plaintext
{"value": 300}
```

随着模型：

```java
class ByteExample {
    public byte value;
}
```

结果，该对象的值为 44。处理不当，因为在这些情况下可能会引发异常。这将防止无法检测的错误通过应用程序传播。

### 4.5. 反序列化浮点数

接下来，让我们尝试将以下 JSON 反序列化为ByteExample对象：

```plaintext
{"value": 2.3}
```

Gson 在这里做了正确的事情并引发了一个JsonSyntaxException ，其子类型是NumberFormatException。无论我们使用哪种离散类型(byte、short、 int 或long)，我们都会得到相同的结果。

如果该值以“.0”结尾，Gson 将按预期反序列化该数字。

### 4.6. 反序列化数字布尔值

有时，布尔值被编码为 0 或 1，而不是“真”或“假”。Gson 默认不允许这样做。例如，如果我们尝试反序列化：

```plaintext
{"value": 1}
```

进入模型：

```java
class BooleanExample {
    public boolean value;
}
```

Gson 引发一个JsonSyntaxException异常子类型为IllegalStateException。这与数字不匹配时引发的NumberFormatException形成对比。如果我们想改变它，我们可以使用[自定义反序列化器](https://www.baeldung.com/gson-deserialization-guide)。

### 4.7. 反序列化 Unicode 字符

值得注意的是，Unicode 字符的反序列化不需要额外的配置。

例如，JSON：

```plaintext
{"value": "u00AE"}
```

将产生 ® 字符。

## 5.总结

正如我们所见，Gson 提供了一种直接使用 JSON 和Java原始类型的方法。即使在处理简单的原始类型时，也需要注意一些意想不到的行为。