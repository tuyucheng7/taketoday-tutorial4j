## 1. 概述

在本快速教程中，我们将了解Spring Framework 5.2+ 中提供的新的特定于 Kotlin 的MockMvc支持。

注意：由于 5.2 版本还没有 GA，我们需要使用 Spring [Milestone 存储库](https://www.baeldung.com/spring-maven-repository)。

## 2.要测试的控制器

让我们设置我们将要测试的控制器。

我们将使用以下域：

```java
// Payload
data class Name(val first: String, val last: String)

// Web request
data class Request(val name: Name) 

// Web response
@JsonInclude(JsonInclude.Include.NON_NULL) data class Response(val error: String?)
```

以及一个 REST 控制器，用于验证它接收到的有效负载：

```java
@RestController
@RequestMapping("/mockmvc")
class MockMvcController {
 
    @RequestMapping(value = ["/validate"], method = [RequestMethod.POST], 
      produces = [MediaType.APPLICATION_JSON_VALUE])
    fun validate(@RequestBody request: Request): Response {
        val error = if (request.name.first == "admin") {
            null
        } else {
            ERROR
        }
        return Response(error)
    }
 
    companion object {
        const val ERROR = "invalid user"
    }
}
```

在这里，我们有一个 POST 端点，它接收序列化为 JSON 的自定义Request类的实例，并返回序列化为 JSON 的自定义Response类的实例。

## 3.经典测试方法

我们可以使用标准的MockMvc方法测试上面的控制器：

```java
mockMvc.perform(MockMvcRequestBuilders
  .post("/mockmvc/validate")
  .accept(MediaType.APPLICATION_JSON)
  .contentType(MediaType.APPLICATION_JSON)
  .content(mapper.writeValueAsString(Request(Name("admin", "")))))
  
  .andExpect(MockMvcResultMatchers.status().isOk)
  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
  .andExpect(MockMvcResultMatchers.content().string("{}"))
```

在上面的示例中，我们使用标准测试支持来验证几个方面：

-   HTTP 响应代码是 200
-   响应的 Content-Type 是application/json
-   响应内容是一个空的 JSON 对象

实际上，它看起来相当不错——干净且富有表现力。但是我们可以使用 Kotlin DSL 让它变得更干净。

## 4. 现代测试方法

相同的测试可以重写为：

```java
mockMvc.post("/mockmvc/validate") {
  contentType = MediaType.APPLICATION_JSON
  content = mapper.writeValueAsString(Request(Name("admin", "")))
  accept = MediaType.APPLICATION_JSON
}.andExpect {
    status { isOk }
    content { contentType(MediaType.APPLICATION_JSON) }
    content { json("{}") }
}
```

让我们检查一下这是如何实现的。首先，我们需要使用 Spring Framework 5.2+，其中包含[MockMvcExtensions.kt](https://github.com/spring-projects/spring-framework/blob/master/spring-test/src/main/kotlin/org/springframework/test/web/servlet/MockMvcExtensions.kt) — MockMvc 的 自定义[DSL](https://www.baeldung.com/kotlin-dsl)。

我们首先使用MockHttpServletRequestDsl[扩展方法](https://www.baeldung.com/kotlin-extension-methods)调用扩展函数MockMvc.post()：

```java
mockMvc.post("/mockmvc/validate") {
    // This is extension method's body
}
```

这意味着该方法是使用MockHttpServletRequestDsl对象作为此引用执行的，因此我们刚刚设置了它的contentType、content和accept属性。

然后我们以类似的方式定义期望——通过提供MockMvcResultMatchersDsl扩展方法：

```java
andExpect {
    // Extension method body
}
```

## 5.进一步进化

如果我们想添加更多测试，我们可以重构现有代码以避免重复。

让我们将这里的公共代码提取到一个有用的doTest方法中：

```java
private fun doTest(input: Request, expectation: Response) {
    mockMvc.post("/mockmvc/validate") {
      contentType = MediaType.APPLICATION_JSON
      content = mapper.writeValueAsString(input)
      accept = MediaType.APPLICATION_JSON
    }.andExpect {
      status { isOk }
      content { contentType(MediaType.APPLICATION_JSON) }
      content { json(mapper.writeValueAsString(expectation)) }
    }
}
```

现在，实际测试将被简化：

```java
@Test
fun `when supported user is given then validation is successful`() {
    doTest(Request(Name("admin", "")), Response(null))
}

@Test
fun `when unsupported user is given then validation is failed`() {
    doTest(Request(Name("some-name", "some-surname")), Response(MockMvcController.ERROR))
}
```

## 六，总结

在本文中，我们检查了如何使用MockMvc Kotlin DSL 来使我们的测试代码更简洁。这不是灵丹妙药，只是让测试代码更加简洁的一个小功能。

由于这是自定义 DSL 用法的又一个示例，它也可以作为在其他项目中开始使用自定义 DSL 的动力。