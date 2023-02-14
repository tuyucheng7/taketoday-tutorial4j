## 1. 概述

在这篇简短的文章中，我们介绍Spring中的“HttpMessageNotWritableException:
no converter for [class ...] with preset Content-Type”异常。

首先，我们将阐明该异常背后的主要原因。然后，我们将深入探讨如何使用一个实际示例来重现它，最后说明如何解决它。

## 2. 原因

在深入讨论细节之前，让我们尝试理解该异常的含义。

异常的堆栈跟踪说明了一切：它告诉我们，**Spring无法找到能够将Java对象转换为HTTP响应的合适的HttpMessageConverter**。

基本上，Spring依赖于“Accept”头来检测它需要响应的媒体类型。

**因此，使用没有预先注册消息转换器的媒体类型将导致Spring失败并抛出此异常**。

## 3. 重现

现在我们知道了Spring抛出此异常的原因，让我们看看如何使用一个实际示例来重现它。

我们编写一个控制器方法，并假装指定一个没有注册HttpMessageConverter的媒体类型(用于响应)。

例如，让我们使用APPLICATION_XML_VALUE或“application/xml”：

```java

@RestController
@RequestMapping(value = "/api")
public class StudentRestController {

    @GetMapping(value = "/student/v3/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Student> getV3(@PathVariable("id") int id) {
        return ResponseEntity.ok(new Student(id, "Robert", "Miller", "BB"));
    }
}
```

接下来，我们向http://localhost:8080/api/student/v3/1发送请求，看看会发生什么：

```shell
curl http://localhost:8080/api/student/v3/1
```

端点返回以下响应：

```json
{
    "timestamp": "2022-09-18T09:45:37.242+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/api/student/v3/1"
}
```

通过查看日志，Spring失败并出现HttpMessageNotWritableException异常：

```text
[org.springframework.http.converter.HttpMessageNotWritableException: No converter for [class cn.tuyucheng.boot.noconverterfound.model.Student] with preset Content-Type 'null']
```

因此，**抛出异常是因为没有HttpMessageConverter能够将Student对象与XML进行转换**。

最后，让我们编写一个测试用例，来确认Spring抛出HttpMessageNotWritableException并带有指定的消息：

```java

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentRestController.class)
class NoConverterFoundIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenConverterNotFound_thenThrowException() throws Exception {
        String url = "/api/student/v3/1";

        this.mockMvc.perform(get(url))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(HttpMessageNotWritableException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains("No converter for [class cn.tuyucheng.boot.noconverterfound.model.Student] with preset Content-Type"));
    }
}
```

## 4. 解决方案

**只有一种方法可以解决此异常 - 通过使用具有注册消息转换器的媒体类型**。

Spring Boot依赖于自动配置来注册内置的消息转换器。

例如，**如果类路径中存在Jackson 2依赖项，它将自动注册MappingJackson2HttpMessageConverter**。

综上所述，并且我们知道spring-boot-web-starter中包含Jackson依赖，因此我们我们使用APPLICATION_JSON_VALUE媒体类型创建一个新端点：

```java

@RestController
@RequestMapping(value = "/api")
public class StudentRestController {

    @GetMapping(value = "/student/v2/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Student> getV2(@PathVariable("id") int id) {
        return ResponseEntity.ok(new Student(id, "Kevin", "Cruyff", "AA"));
    }
}
```

现在，让我们创建一个测试用例来确认一切正常：

```java
class NoConverterFoundIntegrationTest {

    @Test
    void whenJsonConverterIsFound_thenReturnResponse() throws Exception {
        String url = "/api/student/v2/1";

        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"firstName\":\"Kevin\",\"lastName\":\"Cruyff\", \"grade\":\"AA\"}"));
    }
}
```

正如我们所见，Spring没有抛出HttpMessageNotWritableException，这要归功于MappingJackson2HttpMessageConverter，它在幕后处理Student对象到JSON的转换。

## 5. 总结

在这个简短的教程中，我们详细讨论了导致Spring抛出
“HttpMessageNotWritableException No converter for [class ...] with preset Content-Type”的原因。

并且我们演示了如何重现该异常以及如何在实践中解决它。