## 1. 概述

在本快速教程中，我们将展示如何使用两种不同的方法将[BufferedReader](https://www.baeldung.com/java-buffered-reader)转换为[JSONObject](https://www.baeldung.com/java-org-json#jsonobject) 。

## 2. 依赖

在开始之前，我们需要将[org.json](https://search.maven.org/search?q=g:org.json AND a:json&core=gav)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20200518</version>
</dependency>
```

## 3. JSONTokens

最新版本的org.json库带有一个JSONTokener构造函数。它直接接受一个Reader作为参数。

因此，让我们使用以下方法将BufferedReader转换为JSONObject：

```java
@Test
public void givenValidJson_whenUsingBufferedReader_thenJSONTokenerConverts() {
    byte[] b = "{ "name" : "John", "age" : 18 }".getBytes(StandardCharsets.UTF_8);
    InputStream is = new ByteArrayInputStream(b);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
    JSONTokener tokener = new JSONTokener(bufferedReader);
    JSONObject json = new JSONObject(tokener);

    assertNotNull(json);
    assertEquals("John", json.get("name"));
    assertEquals(18, json.get("age"));
}

```

## 4.首先转换成字符串

现在，让我们看看通过首先将BufferedReader转换为String来获取JSONObject 的另一种方法。

在旧版本的org.json中工作时可以使用这种方法：

```java
@Test
public void givenValidJson_whenUsingString_thenJSONObjectConverts()
  throws IOException {
    // ... retrieve BufferedReader<br />
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
    }
    JSONObject json = new JSONObject(sb.toString());

    // ... same checks as before
}

```

在这里，我们将BufferedReader转换为String，然后使用JSONObject构造函数将String转换为JSONObject。



## 5.总结



在本文中，我们通过简单示例了解了将BufferedReader转换为JSONObject的两种不同方法。毫无疑问，最新版本的org.json提供了一种简洁明了的方法，可以用更少的代码行将BufferedReader转换为JSONObject 。