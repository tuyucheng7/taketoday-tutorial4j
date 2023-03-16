## 1. 概述

Spring 5带来了一个[新的PathPatternParser](https://jira.spring.io/browse/SPR-14544)用于解析URI模板模式。这是以前使用的AntPathMatcher的替代方案。

AntPathMatcher是Ant风格路径模式匹配的实现。PathPatternParser将路径分解为PathElement的链表。PathElements链由 PathPattern类获取，用于快速匹配模式。

通过PathPatternParser，还引入了对新URI变量语法的支持。

在本文中，**我们将介绍Spring 5.0 WebFlux中引入的新的/更新的URL模式匹配器**，以及自旧版本Spring以来一直存在的匹配器。

## 2. Spring 5.0中新的URL模式匹配器

Spring 5.0版本添加了一个非常易于使用的URI变量语法：{\*foo}来捕获模式末尾任意数量的路径段。

### 2.1 使用处理程序方法的URI变量语法{\*foo}

让我们看一个使用@GetMapping和处理程序方法的URI变量模式{\*foo}的示例。我们在“/spring5”之后的路径中给出的任何内容都将存储在路径变量“id”中：

```java
@GetMapping("/spring5/{*id}")
public String URIVariableHandler(@PathVariable String id) {
    return id;
}
```

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Spring5ReactiveApplication.class)
@WithMockUser
class PathPatternsUsingHandlerMethodIntegrationTest {
    private static WebTestClient client;

    @BeforeAll
    static void beforeAll() {
        client = WebTestClient.bindToController(new PathPatternController())
              .build();
    }

    @Test
    void givenHandlerMethod_whenMultipleURIVariablePattern_then200() {
        client.get()
              .uri("/spring5/tuyucheng/tutorial")
              .exchange()
              .expectStatus()
              .is2xxSuccessful()
              .expectBody()
              .equals("/tuyucheng/tutorial");

        client.get()
              .uri("/spring5/tuyucheng")
              .exchange()
              .expectStatus()
              .is2xxSuccessful()
              .expectBody()
              .equals("/tuyucheng");
    }
}
```

### 2.2 使用RouterFunction的URI变量语法{\*foo}

让我们看一个使用RouterFunction的新URI变量路径模式的示例：

```java
public class ExploreSpring5URLPatternUsingRouterFunctions {

    private RouterFunction<ServerResponse> routingFunction() {
        return route(GET("/test/{*id}"), 
              serverRequest -> ok().body(fromValue(serverRequest.pathVariable("id"))));
    }
}
```

在这种情况下，我们在“/test”之后写入的任何路径都将被捕获到路径变量“id”中，所以它的测试用例可能是：

```java
@Test
void givenRouter_whenMultipleURIVariablePattern_thenGotPathVariable() {
    client.get()
        .uri("/test/ab/cd")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("/ab/cd");
}
```

### 2.3 使用URI变量语法{\*foo}访问资源

如果我们想访问资源，那么我们需要编写与上一个示例中类似的路径模式。

因此假设我们的模式是：“/files/{*filepaths}”。在这种情况下，如果路径是/files/hello.txt，则路径变量“filepaths”的值将是“/hello.txt”，而如果路径是/files/test/test.txt，则“filepaths”的值为“/test/test.txt”。

我们用于访问/files/目录下文件资源的路由函数：

```java
private RouterFunction<ServerResponse> routingFunction() {
    return route(RouterFunctions.resources("/files/{*filepaths}", new ClassPathResource("files/")));
}
```

假设我们的文本文件hello.txt和test.txt的内容分别为“hello”和“test”。这可以通过JUnit测试用例来演示：

```java
@Test
void givenResources_whenAccess_thenGot() {
    client.get()
        .uri("/files/test/test.txt")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("test");

    client.get()
        .uri("/files/hello.txt")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("hello");
}
```

## 3. 以前版本中的现有URL模式

现在让我们看看旧版本的Spring支持的所有其他URL模式匹配器。所有这些模式都适用于带有@GetMapping的路由函数和处理程序方法。

### 3.1 ‘?’精确匹配一个字符

如果我们将路径模式指定为：“/t?st”，这将匹配“/test”和“/tast”等路径，但不匹配“/tst”和“/teest”。

使用RouterFunction及其JUnit测试用例的示例代码：：

```java
private RouterFunction<ServerResponse> routingFunction() {
    return route(GET("/t?st"), 
        serverRequest -> ok().body(fromValue("Path /t?st is accessed")));
}

@Test
void givenRouter_whenGetPathWithSingleCharWildcard_thenGotPathPattern() {
    client.get()
        .uri("/test")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("Path /t?st is accessed");
}
```

### 3.2 ‘\*’匹配路径段中的0个或多个字符

如果我们将路径模式指定为：“/tuyucheng/\*Id”，这将匹配以下路径模式：“/tuyucheng/Id”、“/tuyucheng/tutorialId”、“/tuyucheng/articleId”等：

```java
private RouterFunction<ServerResponse> routingFunction() {
    return route(GET("/tuyucheng/*Id"), 
        serverRequest -> ok().body(fromValue("/tuyucheng/*Id path was accessed")));
}

@Test
void givenRouter_whenGetMultipleCharWildcard_thenGotPathPattern() {
    client.get()
        .uri("/tuyucheng/tutorialId")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("/tuyucheng/*Id path was accessed");
}
```

### 3.3 ‘\*\*’匹配0个或多个路径段，直到路径结束

在这种情况下，模式匹配不限于单个路径段。如果我们将模式指定为“/resources/\*\*”，它将匹配所有路径到“/resources/”之后任意数量的路径段：

```java
private RouterFunction<ServerResponse> routingFunction() {
    return route(RouterFunctions.resources("/resources/**", 
		new ClassPathResource("resources/")));
}

@Test
void givenRouter_whenAccess_thenGot() {
    client.get()
        .uri("/resources/test/test.txt")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("test");
}
```

### 3.4 '{tuyucheng:[a-z]+}'路径变量中的正则表达式

我们还可以为路径变量的值指定一个正则表达式。因此，如果我们的模式类似于“/{tuyucheng:[a-z]+}”，则路径变量“tuyucheng”的值将是与给定正则表达式匹配的任何路径段：

```java
private RouterFunction<ServerResponse> routingFunction() {
    return route(GET("/{tuyucheng:[a-z]+}"), 
		serverRequest -> ok()
			.body(fromValue("/{tuyucheng:[a-z]+} was accessed and tuyucheng=" + serverRequest.pathVariable("tuyucheng"))));
}

@Test
void givenRouter_whenGetRegexInPathVariable_thenGotPathVariable() {
    client.get()
        .uri("/abcd")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("/{tuyucheng:[a-z]+} was accessed and tuyucheng=abcd");

    client.get()
        .uri("/1234")
        .exchange()
        .expectStatus()
        .is4xxClientError();
}
```

### 3.5 '/{var1}_{var2}'同一路径段中的多个路径变量

Spring 5确保只有在用分隔符分隔时才允许在单个路径段中使用多个路径变量。只有这样，Spring才能区分两个不同的路径变量：

```java
private RouterFunction<ServerResponse> routingFunction() {
    return route(GET("/{var1}_{var2}"), 
		serverRequest -> ok()
			.body(fromValue(serverRequest.pathVariable("var1") + " , " + serverRequest.pathVariable("var2"))));
}

@Test
void givenRouter_whenGetMultiplePathVariableInSameSegment_thenGotPathVariable() {
    client.get()
        .uri("/tuyucheng_tutorial")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("tuyucheng , tutorial");
}
```

## 4. 总结

在本文中，我们介绍了Spring 5中的新URL匹配器，以及旧版本Spring中可用的匹配器。