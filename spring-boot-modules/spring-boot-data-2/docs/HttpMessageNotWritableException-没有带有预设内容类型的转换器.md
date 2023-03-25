## 1. 概述

在这篇简短的文章中，我们将仔细研究Spring异常“HttpMessageNotWritableException: no converter for [class...] with preset Content-Type”。

首先，我们将阐明异常背后的主要原因。然后，我们将看看如何使用实际示例重现它，最后，如何解决它。

## 2. 原因

在深入细节之前，让我们试着理解异常的含义。

异常的堆栈跟踪说明了一切：它告诉我们**Spring未能找到合适的[HttpMessageConverter](https://www.baeldung.com/spring-httpmessageconverter-rest)能够将Java对象转换为HTTP响应**。

基本上，Spring依靠“Accept”标头来检测它需要响应的媒体类型。

因此，**使用没有预注册消息转换器的媒体类型将导致Spring失败并出现异常**。

## 3. 重现异常

现在我们知道是什么原因导致Spring抛出异常，让我们看看如何使用实际示例重现它。

让我们创建一个处理程序方法并假装指定一个没有注册HttpMessageConverter的媒体类型(用于响应)。

例如，让我们使用APPLICATION_XML_VALUE或“application/xml”：

```java
@GetMapping(value = "/student/v3/{id}", produces = MediaType.APPLICATION_XML_VALUE)
public ResponseEntity<Student> getV3(@PathVariable("id") int id) {
    return ResponseEntity.ok(new Student(id, "Robert", "Miller", "BB"));
}
```

接下来，让我们向http://localhost:8080/api/student/v3/1发送请求，看看会发生什么：

```shell
curl http://localhost:8080/api/student/v3/1
```

端点发回以下响应：

```json
{
    "timestamp": "2022-02-01T18:23:37.490+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/api/student/v3/1"
}
```

事实上，查看日志，Spring失败并出现HttpMessageNotWritableException异常：

```shell
[org.springframework.http.converter.HttpMessageNotWritableException: No converter for [class cn.tuyucheng.taketoday.boot.noconverterfound.model.Student] with preset Content-Type 'null']
```

因此，抛出异常是因为**没有HttpMessageConverter能够将Student对象编组到XML和从XML中解组**。

最后，让我们创建一个测试用例来确认Spring抛出带有指定消息的HttpMessageNotWritableException：

```java
@Test
public void whenConverterNotFound_thenThrowException() throws Exception {
    String url = "/api/student/v3/1";

    this.mockMvc.perform(get(url))
        .andExpect(status().isInternalServerError())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(HttpMessageNotWritableException.class))
        .andExpect(result -> assertThat(result.getResolvedException()
            .getMessage()).contains("No converter for [class cn.tuyucheng.taketoday.boot.noconverterfound.model.Student] with preset Content-Type"));
}
```

## 4. 解决方案

**只有一种方法可以修复异常-使用具有已注册消息转换器的媒体类型**。

Spring Boot依靠自动配置来注册[内置的消息转换器](https://www.baeldung.com/spring-httpmessageconverter-rest#2-the-default-message-converters)。

例如，**如果类路径中存在[Jackson 2](https://www.baeldung.com/jackson)依赖项，它将自动注册MappingJackson2HttpMessageConverter**。

话虽如此，并且知道Spring Boot在[web starter](https://www.baeldung.com/spring-boot-starters#Starter)中包含Jackson，让我们创建一个具有APPLICATION_JSON_VALUE媒体类型的新端点：

```java
@GetMapping(value = "/student/v2/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Student> getV2(@PathVariable("id") int id) {
    return ResponseEntity.ok(new Student(id, "Kevin", "Cruyff", "AA"));
}
```

现在，让我们创建一个测试用例来确认一切正常：

```java
@Test
public void whenJsonConverterIsFound_thenReturnResponse() throws Exception {
    String url = "/api/student/v2/1";

    this.mockMvc.perform(get(url))
        .andExpect(status().isOk())
        .andExpect(content().json("{'id':1,'firstName':'Kevin','lastName':'Cruyff', 'grade':'AA'}"));
}
```

正如我们所见，Spring不会抛出HttpMessageNotWritableException，这要归功于MappingJackson2HttpMessageConverter，它在后台处理Student对象到JSON的转换。

## 5. 总结

在这个简短的教程中，我们详细讨论了导致Spring抛出“HttpMessageNotWritableException No converter for [class...] with preset Content-Type”的原因。

在此过程中，我们展示了如何产生异常以及如何在实践中修复它。