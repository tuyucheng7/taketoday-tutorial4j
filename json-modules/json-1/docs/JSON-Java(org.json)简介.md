## 1. 概述

JSON(JavaScript 对象表示法)是一种轻量级数据交换格式，我们最常将其用于客户端-服务器通信。它既易于读/写又与语言无关。JSON 值可以是另一个 JSON object、array、number、string、boolean (true/false) 或null。

在本教程中，我们将了解如何使用可用的 JSON 处理库之一——JSON [-Java](https://stleary.github.io/JSON-java/index.html)库，也称为org.json来创建、操作和解析 JSON 。

## 延伸阅读：

## [遍历 org.json.JSONObject 的一个实例](https://www.baeldung.com/jsonobject-iteration)

了解如何迭代和遍历 JSONObject

[阅读更多](https://www.baeldung.com/jsonobject-iteration)→

## [在Java中转义 JSON 字符串](https://www.baeldung.com/java-json-escaping)

了解转义 JSON String 核心Java或库的方法

[阅读更多](https://www.baeldung.com/java-json-escaping)→

## 2. 先决条件

我们首先需要在我们的pom.xml中添加以下依赖项：

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20180130</version>
</dependency>
```

最新版本可以在[Maven Central Repository](https://search.maven.org/classic/#search|gav|1|g%3A"org.json" AND a%3A"json")中找到。

请注意，此包已包含在 Android SDK 中，因此我们不应在使用时包含它。

## 3.Java中的 JSON [package org.json]

[JSON-Java](https://stleary.github.io/JSON-java/index.html)库也称为org.json(不要与 Google 的[org.json.simple](https://code.google.com/archive/p/json-simple/)混淆)为我们提供了用于在Java中解析和操作 JSON 的类。

此外，该库还可以在 JSON、XML、HTTP 标头、Cookie、逗号分隔列表或文本等之间进行转换。

在本教程中，我们将了解以下类：

1.  JSONObject – 类似于Java的原生Map类对象，它存储无序的键值对
2.  JSONArray – 类似于Java的本机 Vector 实现的有序值序列
3.  JSONTokener – 一种将一段文本分解为一系列标记的工具， JSONObject或JSONArray可以使用这些来解析 JSON 字符串
4.  CDL——一种提供将逗号分隔文本转换为JSONArray的方法的工具，反之亦然
5.  Cookie – 从 JSON字符串转换为 cookie，反之亦然
6.  HTTP – 用于将 JSON字符串转换为 HTTP 标头，反之亦然
7.  JSONException – 此库抛出的标准异常

## 4.JSON对象

[JSONObject](https://stleary.github.io/JSON-java/org/json/JSONObject.html) 是键值对的无序集合，类似于Java的本机Map实现。

-   键是唯一的字符串，不能为null。
-   值可以是任何东西，从Boolean、Number、String或JSONArray到甚至JSONObject.NULL对象。
-   JSONObject可以用花括号括起来的字符串表示，键和值由冒号分隔，成对由逗号分隔。
-   它有几个构造函数来构造JSONObject。

它还支持以下主要方法：

1.  get(String key) – 获取与提供的键关联的对象，如果找不到该键则抛出JSONException
2.  opt(String key) – 获取与提供的键关联的对象，否则为null
3.  put(String key, Object value) –在当前JSONObject中插入或替换键值对。

put()方法是一个重载方法，它接受一个String类型的键和多个类型的值。

有关JSONObject支持的方法的完整列表，[请访问官方文档。](https://stleary.github.io/JSON-java/org/json/JSONObject.html)

现在让我们讨论这个类支持的一些主要操作。

### 4.1. 直接从JSONObject创建 JSON

JSONObject公开了一个类似于Java的Map接口的 API 。

我们可以使用put()方法并将键和值作为参数提供：

```java
JSONObject jo = new JSONObject();
jo.put("name", "jon doe");
jo.put("age", "22");
jo.put("city", "chicago");
```

现在我们的JSONObject看起来像这样：

```plaintext
{"city":"chicago","name":"jon doe","age":"22"}
```

JSONObject.put()方法有七种不同的重载签名。虽然键只能是唯一的非空字符串，但值可以是任何东西。

### 4.2. 从地图创建 JSON

我们可以构造一个自定义Map，然后将其作为参数传递给JSONObject的构造函数，而不是直接将键和值放入JSONObject中。

此示例将产生与上述相同的结果：

```java
Map<String, String> map = new HashMap<>();
map.put("name", "jon doe");
map.put("age", "22");
map.put("city", "chicago");
JSONObject jo = new JSONObject(map);
```

### 4.3. 从 JSON字符串创建JSONObject

要将 JSON String解析为JSONObject，我们只需将String传递给构造函数即可。

此示例将产生与上述相同的结果：

```java
JSONObject jo = new JSONObject(
  "{"city":"chicago","name":"jon doe","age":"22"}"
);
```

传递的String参数必须是有效的 JSON；否则，此构造函数可能会抛出JSONException。

### 4.4. 将Java对象序列化为 JSON

JSONObject的构造函数之一将 POJO 作为其参数。在下面的示例中，包使用DemoBean类中的 getter 并为其创建适当的JSONObject。

要从Java对象获取JSONObject，我们必须使用一个有效的[Java Bean](https://en.wikipedia.org/wiki/JavaBeans)类：

```java
DemoBean demo = new DemoBean();
demo.setId(1);
demo.setName("lorem ipsum");
demo.setActive(true);

JSONObject jo = new JSONObject(demo);
```

这是JSONObject jo：

```plaintext
{"name":"lorem ipsum","active":true,"id":1}
```

虽然我们有办法将Java对象序列化为 JSON 字符串，但无法使用此库将其转换回来。如果我们想要那种灵活性，我们可以切换到其他库，例如[Jackson](https://www.baeldung.com/jackson)。

## 5. JSON数组

[JSONArray](https://stleary.github.io/JSON-java/org/json/JSONArray.html)是有序的值集合，类似于Java的本机Vector实现。

-   值可以是从Number、String、Boolean、JSONArray或JSONObject到甚至JSONObject.NULL对象的任何值。
-   它由包含在方括号中的字符串表示，由逗号分隔的值集合组成。
-   与JSONObject一样，它有一个构造函数，该构造函数接受源String并对其进行解析以构造JSONArray。

这些是JSONArray类的主要方法：

1.  get(int index) – 返回指定索引处的值(介于 0 和总长度 - 1 之间)，否则抛出JSONException
2.  opt(int index) – 返回与索引关联的值(介于 0 和总长度 – 1 之间)。如果该索引处没有值，则返回null。
3.  put(Object value) – 将对象值附加到此JSONArray。此方法已重载并支持多种数据类型。

有关 JSONArray 支持的方法的完整列表，[请访问官方文档](https://stleary.github.io/JSON-java/org/json/JSONArray.html)。

### 5.1. 创建JSONArray

初始化 JSONArray 对象后，我们可以使用put()和get()方法简单地添加和检索元素：

```java
JSONArray ja = new JSONArray();
ja.put(Boolean.TRUE);
ja.put("lorem ipsum");

JSONObject jo = new JSONObject();
jo.put("name", "jon doe");
jo.put("age", "22");
jo.put("city", "chicago");

ja.put(jo);
```

以下是我们的JSONArray的内容(为清楚起见，代码经过格式化)：

```java
[
    true,
    "lorem ipsum",
    {
        "city": "chicago",
        "name": "jon doe",
        "age": "22"
    }
]
```

### 5.2. 直接从 JSON 字符串创建JSONArray

与JSONObject一样，JSONArray也有一个构造函数，可以直接从 JSON String创建一个Java对象：

```java
JSONArray ja = new JSONArray("[true, "lorem ipsum", 215]");
```

如果源String不是有效的 JSON String ，此构造函数可能会抛出JSONException。

### 5.3. 直接从集合或数组创建JSONArray

JSONArray的构造函数还支持集合和数组对象作为参数。

我们只需将它们作为参数传递给构造函数，它将返回一个JSONArray对象：

```java
List<String> list = new ArrayList<>();
list.add("California");
list.add("Texas");
list.add("Hawaii");
list.add("Alaska");

JSONArray ja = new JSONArray(list);
```

现在我们的JSONArray包含以下内容：

```plaintext
["California","Texas","Hawaii","Alaska"]
```

## 6. JSONTokens

JSONTokener将源字符串作为其构造函数的输入，并[从中](https://stleary.github.io/JSON-java/org/json/JSONTokener.html)提取字符和标记。此包的类(如JSONObject、JSONArray)在内部使用它来解析 JSON Strings。

我们可能不会直接使用此类，因为我们可以使用其他更简单的方法(如string.toCharArray())实现相同的功能：

```java
JSONTokener jt = new JSONTokener("lorem");

while(jt.more()) {
    Log.info(jt.next());
}
```

现在我们可以像迭代器一样访问JSONTokener ，使用more()方法检查是否还有剩余元素，使用next()方法访问下一个元素。

以下是从上一个示例中收到的令牌：

```plaintext
l
o
r
e
m
```

## 7.CDL _

我们提供了一个[CDL](https://stleary.github.io/JSON-java/org/json/CDL.html)(逗号分隔列表)类，用于将逗号分隔文本转换为JSONArray，反之亦然。

### 7.1. 直接从逗号分隔文本生成JSONArray

为了直接从逗号分隔的文本生成JSONArray ，我们可以使用静态方法rowToJSONArray()，它接受JSONTokener：

```java
JSONArray ja = CDL.rowToJSONArray(new JSONTokener("England, USA, Canada"));
```

这是我们的JSONArray现在包含的内容：

```plaintext
["England","USA","Canada"]
```

### 7.2. 从 JSONArray 生成逗号分隔的文本

让我们看看如何反转上一步并从JSONArray取回逗号分隔的文本：

```java
JSONArray ja = new JSONArray("["England","USA","Canada"]");
String cdt = CDL.rowToString(ja);
```

String cdt现在包含以下内容：

```plaintext
England,USA,Canada
```

### 7.3. 使用逗号分隔文本生成JSONObject的JSONArray

要生成JSONObject的JSONArray，我们将使用包含以逗号分隔的标头和数据的文本字符串。

我们使用回车符(r)或换行符(n) 分隔不同的行。

第一行被解释为标题列表，所有后续行都被视为数据：

```java
String string = "name, city, age n" +
  "john, chicago, 22 n" +
  "gary, florida, 35 n" +
  "sal, vegas, 18";

JSONArray result = CDL.toJSONArray(string);
```

对象JSONArray 结果现在包含以下内容(为清楚起见格式化输出)：

```plaintext
[
    {
        "name": "john",
        "city": "chicago",
        "age": "22"
    },
    {
        "name": "gary",
        "city": "florida",
        "age": "35"
    },
    {
        "name": "sal",
        "city": "vegas",
        "age": "18"
    }
]
```

请注意，数据和标头都是在同一个String中提供的。我们有另一种方法可以实现相同的功能，方法是提供一个JSONArray 来获取标头和一个以逗号分隔的字符串作为数据。

同样，我们使用回车符(r)或换行符(n)分隔不同的行：

```java
JSONArray ja = new JSONArray();
ja.put("name");
ja.put("city");
ja.put("age");

String string = "john, chicago, 22 n"
  + "gary, florida, 35 n"
  + "sal, vegas, 18";

JSONArray result = CDL.toJSONArray(ja, string);
```

在这里我们将像以前一样得到对象result的内容。

## 8.饼干

[Cookie](https://stleary.github.io/JSON-java/org/json/Cookie.html)类处理 Web 浏览器 cookie，并具有将浏览器 cookie 转换为JSONObject的方法，反之亦然。

以下是Cookie类的主要方法：

1.  toJsonObject(String sourceCookie) –将 cookie 字符串转换为JSONObject
    
2.  toString(JSONObject jo) – 与之前的方法相反，将JSONObject转换为 cookie String

### 8.1. 将 Cookie字符串转换为JSONObject

要将 cookie String转换为JSONObject，我们将使用静态方法Cookie.toJSONObject()：

```java
String cookie = "username=John Doe; expires=Thu, 18 Dec 2013 12:00:00 UTC; path=/";
JSONObject cookieJO = Cookie.toJSONObject(cookie);
```

### 8.2. 将JSONObject转换为 Cookie字符串

现在我们将把JSONObject转换成 cookie String。这与上一步相反：

```java
String cookie = Cookie.toString(cookieJO);
```

## 9.HTTP _

[HTTP](https://stleary.github.io/JSON-java/org/json/HTTP.html)类包含用于将 HTTP 标头转换为JSONObject的静态方法，反之亦然。

这个类还有两个主要方法：

1.  toJsonObject(String sourceHttpHeader) –将HttpHeader 字符串转换为JSONObject
2.  toString(JSONObject jo) – 将提供的JSONObject转换为字符串

### 9.1. 将JSONObject转换为 HTTP 标头

HTTP.toString()方法用于将JSONObject转换为 HTTP 标头String：

```java
JSONObject jo = new JSONObject();
jo.put("Method", "POST");
jo.put("Request-URI", "http://www.example.com/");
jo.put("HTTP-Version", "HTTP/1.1");
String httpStr = HTTP.toString(jo);
```

这是我们的String httpStr将包含的内容：

```plaintext
POST "http://www.example.com/" HTTP/1.1
```

请注意，在转换 HTTP 请求标头时，JSONObject必须包含“Method”、“Request-URI”和“HTTP-Version”键。对于响应标头，对象必须包含“HTTP-Version”、“Status-Code”和“Reason-Phrase”参数。

### 9.2. 将 HTTP 标头字符串转换回JSONObject

在这里，我们将把我们在上一步中获得的 HTTP 字符串转换回我们在该步骤中创建的JSONObject：

```java
JSONObject obj = HTTP.toJSONObject("POST "http://www.example.com/" HTTP/1.1");
```

## 10.JSON异常

JSONException是此包在遇到任何错误时抛出的标准异常[。](https://stleary.github.io/JSON-java/org/json/JSONException.html)

这用于此包中的所有类。异常后通常会跟有一条消息，说明究竟出了什么问题。

## 11.总结

在本文中，我们查看了使用Java的 JSON — org.json — 我们重点介绍了此处可用的一些核心功能。