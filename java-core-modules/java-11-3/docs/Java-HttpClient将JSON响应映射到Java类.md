## 1. 概述

众所周知，Java 11中引入的[HttpClient](https://www.baeldung.com/java-9-http-client)类有助于从服务器请求HTTP资源。它支持同步和异步编程模式。

在本教程中，我们将探讨将HTTP响应从HttpClient映射到普通旧Java对象(POJO)类的不同方法。

## 2. 示例设置

让我们写一个简单的程序，Todo。该程序将使用伪造的REST API。我们将执行GET请求，然后处理响应。

### 2.1 Maven依赖项

我们将使用Maven管理我们的依赖关系。让我们将[Gson](https://search.maven.org/search?q=g:com.google.code.gson)和[Jackson](https://search.maven.org/search?q=a:jackson-databind)依赖项添加到我们的pom.xml中，以使这些库可用于我们的程序：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10</version>
</dependency>
        
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.14.1</version>
</dependency>
```

### 2.2 示例项目

在本教程中，我们将使用伪造的REST [API](https://jsonplaceholder.typicode.com/todos)进行快速原型制作。

首先，让我们查看调用GET请求时的示例API响应：

```json
[
    {
        "userId": 1,
        "id": 1,
        "title": "delectus aut autem",
        "completed": false
    }
]
```

示例API返回具有四个属性的JSON响应。JSON响应有多个对象，但为简单起见，我们将跳过它们。

接下来，让我们创建一个用于数据绑定的POJO类。类字段匹配JSON数据属性。我们将包括构造函数、getter、setter、equals()和toString()：

```java
public class Todo {

    int userId;
    int id;
    String title;
    boolean completed;

    // Standard constructors, getters, setters, equals(), and toString()
}
```

然后，让我们创建一个包含我们的逻辑的TodoAppClient类：

```java
public class TodoAppClient {

    ObjectMapper objectMapper = new ObjectMapper();
    Gson gson = new GsonBuilder.create();

    // ...
}
```

此外，我们创建了ObjectMapper和GsonBuilder的新实例。这使得任何方法都可以访问和重用它。在方法中创建ObjectMapper的实例可能是一项昂贵的操作。

最后，我们将编写一个在示例API上同步执行GET请求的方法：

```java
public class TodoAppClient {

    // ...   

    String sampleApiRequest() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create("https://jsonplaceholder.typicode.com/todos"))
              .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body();
    }

    // ...
}
```

BodyHandlers的ofString()方法有助于将响应主体字节转换为String。响应是一个JSON字符串，以便于在程序中进行操作。在后面的部分中，我们将探索将响应映射到POJO类的方法。

让我们为sampleApiRequest()方法编写一个单元测试：

```java
@Test
public void givenSampleRestApi_whenApiIsConsumedByHttpClient_thenCompareJsonString() throws Exception {
    TodoAppClient sampleApi = new TodoAppClient();
    assertNotNull(sampleApi.sampleApiRequest());
}
```

该测试确定API调用的响应不为null。

## 3. 使用Jackson将响应映射到POJO类

[Jackson](https://www.baeldung.com/jackson)**是一个流行的Java JSON库。它有助于序列化和反序列化JSON以进行进一步操作**。我们将使用它反序列化来自示例设置的JSON响应。我们会将响应映射到Todo POJO类。

让我们增强包含客户端逻辑的类。我们将创建一个新方法并调用sampleApiRequest()方法以使响应可用于映射：

```java
public Todo syncJackson() throws Exception {
    
    String response = sampleApiRequest();  
    Todo[] todo = objectMapper.readValue(response, Todo[].class); 
    
    return todo[0];
 }
```

接下来，我们声明了一个Todo数组。最后，我们从ObjectMapper调用readValue()将JSON字符串映射到POJO类。

让我们通过将返回的Todo与预期的Todo新实例进行比较来测试该方法：

```java
@Test
public void givenSampleApiCall_whenResponseIsMappedByJackson_thenCompareMappedResponseByJackson() throws Exception {
    Todo expectedResult = new Todo(1, 1, "delectus aut autem", false); 
    TodoAppClient jacksonTest = new TodoAppClient();
    assertEquals(expectedResult, jacksonTest.syncJackson());
}
```

该测试将预期结果与映射的JSON进行比较。它确定它们是相等的。

## 4. 使用Gson将响应映射到POJO类

[Gson](https://www.baeldung.com/gson-deserialization-guide)是Google的一个Java库，它在Java生态系统中与[Jackson](https://www.baeldung.com/jackson-vs-gson)一样受欢迎。**它有助于将JSON String映射到Java对象以供进一步处理**，该库还可以将Java对象转换为JSON。

我们将使用它将示例设置中的JSON响应映射到其等效的POJO类Todo。让我们在包含我们的逻辑的类中编写一个新方法syncGson()。

我们将调用sampleApi()使JSON字符串可用于反序列化：

```java
public Todo syncGson() throws Exception {
    String response = sampleApiRequest();
    List<Todo> todo = gson.fromJson(response, new TypeToken<List<Todo>>(){}.getType());
    return todo.get(0);
}
```

首先，我们创建了一个Todo类型的列表。然后我们调用fromJson()方法将JSON字符串映射到POJO类。

JSON字符串现在映射到POJO类以供进一步操作和处理。

让我们通过将预期结果与返回的Todo进行比较来为syncGson()编写单元测试：

```java
@Test
public void givenSampleApiCall_whenResponseIsMappedByGson_thenCompareMappedGsonJsonResponse() throws Exception {
    Todo expectedResult = new Todo(1, 1, "delectus aut autem", false);   
    TodoAppClient gsonTest = new TodoAppClient();
    assertEquals(expectedResult, gsonTest.syncGson()); 
}
```

测试表明预期结果与返回值匹配。

## 5. 异步调用

现在，让我们[异步](https://www.baeldung.com/java-asynchronous-programming)实现API调用。在异步模式中，线程不等待彼此完成。这种编程模式使获取数据更加健壮和可扩展。

让我们异步获取示例API并将JSON响应映射到POJO类。

### 5.1 使用Jackson异步调用和映射到POJO类

在本教程中，我们使用两个Java库来反序列化JSON响应。让我们用Jackson实现异步调用映射。首先，让我们在TodoAppClient中创建一个方法readValueJackson()。

该方法反序列化JSON响应并将其映射到POJO类：

```java
List<Todo> readValueJackson(String content) {
    try {
        return objectMapper.readValue(content, new TypeReference<List<Todo>>(){});
    } catch (IOException ioe) {
        throw new CompletionException(ioe);
    }
}
```

然后，让我们向我们的逻辑类添加一个新方法：

```java
public Todo asyncJackson() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://jsonplaceholder.typicode.com/todos"))
        .build(); 
  
    TodoAppClient todoAppClient = new TodoAppClient();
    List<Todo> todo = HttpClient.newHttpClient()
        .sendAsync(request, BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(todoAppClient::readValueJackson)
        .get();
 
    return todo.get(0);
}
```

该方法发出异步GET请求并通过调用readValueJackson()将JSON字符串映射到POJO类。

最后，让我们编写一个单元测试来比较反序列化的JSON响应与预期的Todo实例：

```java
@Test
public void givenSampleApiAsyncCall_whenResponseIsMappedByJackson_thenCompareMappedJacksonJsonResponse() throws Exception {
    Todo expectedResult = new Todo(1, 1, "delectus aut autem", false);  
    TodoAppClient sampleAsyncJackson = new TodoAppClient();
    assertEquals(expectedResult, sampleAsyncJackson.asyncJackson());
}
```

预期结果和映射的JSON响应相等。

### 5.2 使用Gson异步调用和映射到POJO类

让我们通过将异步JSON响应映射到POJO类来进一步增强程序。首先，让我们创建一个方法来反序列化TodoAppClient中的JSON字符串：

```java
List<Todo> readValueGson(String content) {
    return gson.fromJson(content, new TypeToken<List<Todo>>(){}.getType());
}
```

接下来，让我们向包含我们的程序逻辑的类添加一个新方法：

```java
public Todo asyncGson() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://jsonplaceholder.typicode.com/todos"))
        .build();
    TodoAppClient todoAppClient = new TodoAppClient();
    List<Todo> todo = HttpClient.newHttpClient()
        .sendAsync(request, BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(todoAppClient::readValueGson)
        .get();
  
    return todo.get(0);
}
```

该方法发出异步GET请求并将JSON响应映射到POJO类。最后，我们调用了readValueGson()来执行将响应映射到POJO类的过程。

让我们写一个单元测试。我们会将预期的Todo新实例与映射响应进行比较：

```java
@Test
public void givenSampleApiAsyncCall_whenResponseIsMappedByGson_thenCompareMappedGsonResponse() throws Exception {
    Todo expectedResult = new Todo(1, 1, "delectus aut autem", false); 
    TodoAppClient sampleAsyncGson = new TodoAppClient();
    assertEquals(expectedResult, sampleAsyncGson.asyncGson());
}
```

测试表明预期结果与映射的JSON响应匹配。

## 6. 总结

在本文中，我们学习了四种在使用HttpClient时将JSON响应映射到POJO类的方法。此外，我们还深入研究了HttpClient的同步和异步编程模式。此外，我们使用Jackson和Gson库反序列化JSON响应并将JSON字符串映射到POJO类。