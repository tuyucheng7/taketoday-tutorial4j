## 1. 概述

集成测试通过验证系统的端到端行为在应用程序开发周期中发挥重要作用。

在本教程中，我们将学习如何利用Spring MVC测试框架编写和运行集成测试来测试控制器，而无需显式启动Servlet容器。

## 2. 准备

我们需要几个Maven依赖项来运行我们将在本文中使用的集成测试。首先，我们需要最新的[junit-jupiter-engine](https://search.maven.org/artifact/org.junit.jupiter/junit-jupiter-engine)、[junit-jupiter-api](https://search.maven.org/search?q=a:junit-jupiter-api)和[Spring测试](https://search.maven.org/search?q=g:org.springframework)依赖项：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>5.3.3</version>
    <scope>test</scope>
</dependency>

```

为了有效地断言结果，我们还将使用[Hamcrest](https://search.maven.org/search?q=a:hamcrest-library)和[JSON Path](https://search.maven.org/search?q=a:json-path)：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-library</artifactId>
    <version>2.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <version>2.5.0</version>
    <scope>test</scope>
</dependency>
```

## 3. Spring MVC测试配置

现在让我们看看如何配置和运行启用Spring的测试。

### 3.1 使用JUnit 5在测试中启用Spring

JUnit 5定义了一个扩展接口，类可以通过该接口与JUnit测试集成。

我们可以通过将@ExtendWith注解添加到我们的测试类并指定要加载的扩展类来启用此扩展。要运行Spring测试，我们使用SpringExtension.class。

我们还需要@ContextConfiguration注解来加载上下文配置并引导我们的测试将使用的上下文。

我们来看一下：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@WebAppConfiguration
public class GreetControllerIntegrationTest {
    // ....
}
```

请注意，在@ContextConfiguration中，我们提供了ApplicationConfig.class配置类，它加载了我们为此特定测试所需的配置。

我们将在此处使用Java配置类来指定上下文配置。同样，我们可以使用基于XML的配置：

```java
@ContextConfiguration(locations={""})
```

最后，我们还将使用@WebAppConfiguration注解测试，这将加载Web应用程序上下文。

默认情况下，它会在路径src/main/webapp中查找根Web应用程序，我们可以通过简单地传递value属性来覆盖这个位置：

```java
@WebAppConfiguration(value = "")
```

### 3.2 WebApplicationContext对象

WebApplicationContext提供Web应用程序配置。它将所有应用程序bean和控制器加载到上下文中。

现在我们将能够将Web应用程序上下文直接连接到测试中：

```java
@Autowired
private WebApplicationContext webApplicationContext;
```

### 3.3 Mock Web上下文Bean

MockMvc提供对Spring MVC测试的支持，它封装了所有Web应用程序bean并使它们可用于测试。

让我们看看如何使用它：

```java
private MockMvc mockMvc;
@BeforeEach
public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
}
```

我们将在@BeforeEach注解方法中初始化mockMvc对象，这样我们就不必在每个测试中都初始化它。

### 3.4 验证测试配置

让我们验证一下我们是否正确加载了WebApplicationContext对象(webApplicationContext)，我们还将检查是否附加了正确的servletContext：

```java
@Test
public void givenWac_whenServletContext_thenItProvidesGreetController() {
    ServletContext servletContext = webApplicationContext.getServletContext();
    
    Assert.assertNotNull(servletContext);
    Assert.assertTrue(servletContext instanceof MockServletContext);
    Assert.assertNotNull(webApplicationContext.getBean("greetController"));
}
```

请注意，我们还在检查Web上下文中是否存在GreetController.java bean，这确保正确加载Spring bean。至此，集成测试的设置就完成了。现在，我们将了解如何使用MockMvc对象测试资源方法。

## 4. 编写集成测试

在本节中，我们将介绍通过测试框架可用的基本操作。

我们将研究如何发送带有路径变量和参数的请求，我们还将通过几个示例来展示如何断言正确的视图名称已解析，或者响应主体是否符合预期。

下面显示的片段使用来自MockMvcRequestBuilders或MockMvcResultMatchers类的静态导入。

### 4.1 验证视图名称

我们可以从测试中调用/homePage端点：

```bash
http://localhost:8080/spring-mvc-test/
```

或者

```bash
http://localhost:8080/spring-mvc-test/homePage
```

首先，让我们看一下测试代码：

```java
@Test
public void givenHomePageURI_whenMockMVC_thenReturnsIndexJSPViewName() {
    this.mockMvc.perform(get("/homePage")).andDo(print())
        .andExpect(view().name("index"));
}
```

让我们分解一下：

-   perform()方法将调用GET请求方法，该方法返回ResultActions。使用这个结果，我们可以对响应有断言预期，比如它的内容、HTTP状态或标头。
-   andDo(print())将打印请求和响应。这有助于在出现错误时获得详细视图。
-   andExpect()将期望提供的参数。在我们的例子中，我们期望通过MockMvcResultMatchers.view()返回“index”。

### 4.2 验证响应主体

我们将从测试中调用/greet端点：

```bash
http://localhost:8080/spring-mvc-test/greet
```

预期的输出将是：

```json
{
    "id": 1,
    "message": "Hello World!!!"
}
```

让我们看看测试代码：

```java
@Test
public void givenGreetURI_whenMockMVC_thenVerifyResponse() {
    MvcResult mvcResult = this.mockMvc.perform(get("/greet"))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Hello World!!!"))
        .andReturn();
    
    Assert.assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
}
```

让我们看看到底发生了什么：

-   andExpect(MockMvcResultMatchers.status().isOk())将验证响应HTTP状态是否为Ok(200)，这确保请求已成功执行。
-   andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello World!!!"))将验证响应内容是否与参数“Hello World!!!”匹配，在这里，我们使用了jsonPath，它提取响应内容并提供请求的值。
-   andReturn()将返回MvcResult对象，当我们必须验证某些库无法直接实现的内容时会使用该对象。在这种情况下，我们添加了assertEquals以匹配从MvcResult对象中提取的响应的内容类型。

### 4.3 发送带有路径变量的GET请求

我们将从测试中调用/greetWithPathVariable/{name}端点：

```bash
http://localhost:8080/spring-mvc-test/greetWithPathVariable/John
```

预期的输出将是：

```json
{
    "id": 1,
    "message": "Hello World John!!!"
}
```

让我们看看测试代码：

```java
@Test
public void givenGreetURIWithPathVariable_whenMockMVC_thenResponseOK() {
    this.mockMvc
        .perform(get("/greetWithPathVariable/{name}", "John"))
        .andDo(print()).andExpect(status().isOk())
      
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.message").value("Hello World John!!!"));
}
```

MockMvcRequestBuilders.get("/greetWithPathVariable/{name}", "John")将以“/greetWithPathVariable/John”发送请求。

就可读性和了解URL中动态设置的参数而言，这变得更加容易。请注意，我们可以根据需要传递任意数量的路径参数。

### 4.4 发送带有查询参数的GET请求

我们将从测试中调用/greetWithQueryVariable?name={name}端点：

```bash
http://localhost:8080/spring-mvc-test/greetWithQueryVariable?name=John%20Doe
```

在这种情况下，预期输出将是：

```json
{
    "id": 1,
    "message": "Hello World John Doe!!!"
}
```

现在，让我们看一下测试代码：

```java
@Test
public void givenGreetURIWithQueryParameter_whenMockMVC_thenResponseOK() {
    this.mockMvc.perform(get("/greetWithQueryVariable")
        .param("name", "John Doe")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.message").value("Hello World John Doe!!!"));
}
```

param("name", "John Doe")将在GET请求中附加查询参数，这类似于“/greetWithQueryVariable?name=John%20Doe“

查询参数也可以使用URI模板样式实现：

```java
this.mockMvc.perform(get("/greetWithQueryVariable?name={name}", "John Doe"));
```

### 4.5 发送POST请求

我们将从测试中调用/greetWithPost端点：

```bash
http://localhost:8080/spring-mvc-test/greetWithPost
```

我们应该获得输出：

```json
{
    "id": 1,
    "message": "Hello World!!!"
}
```

而我们的测试代码是：

```java
@Test
public void givenGreetURIWithPost_whenMockMVC_thenVerifyResponse() {
    this.mockMvc.perform(post("/greetWithPost")).andDo(print())
        .andExpect(status().isOk()).andExpect(content()
        .contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.message").value("Hello World!!!"));
}
```

MockMvcRequestBuilders.post("/greetWithPost")将发送POST请求，我们可以像以前一样设置路径变量和查询参数，而表单数据只能通过param()方法设置，类似于查询参数：

```bash
http://localhost:8080/spring-mvc-test/greetWithPostAndFormData
```

那么数据将是：

```bash
id=1;name=John%20Doe
```

所以我们应该得到：

```json
{
    "id": 1,
    "message": "Hello World John Doe!!!"
}
```

让我们看看我们的测试：

```java
@Test
public void givenGreetURI_whenMockMVC_thenVerifyResponse() throws Exception {
    MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/greet"))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Hello World!!!"))
        .andReturn();
 
   assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
}
```

在上面的代码片段中，我们添加了两个参数：id为“1”，name为“John Doe”。

## 5. MockMvc限制

MockMvc提供了一个优雅且易于使用的API来调用Web端点并同时检查和断言它们的响应。尽管它有很多好处，但它也有一些局限性。

首先，它确实使用了[DispatcherServlet](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html)的子类来处理测试请求。更具体地说，[TestDispatcherServlet](https://github.com/spring-projects/spring-framework/blob/622ccc57672ffed758220b33a08f8215334cdb2d/spring-test/src/main/java/org/springframework/test/web/servlet/TestDispatcherServlet.java#L53)负责调用控制器并执行所有熟悉的Spring魔法。

MockMvc类在内部[包装](https://github.com/spring-projects/spring-framework/blob/622ccc57672ffed758220b33a08f8215334cdb2d/spring-test/src/main/java/org/springframework/test/web/servlet/MockMvc.java#L72)了这个TestDispatcherServlet。所以每次我们使用perform()方法发送请求时，MockMvc都会直接使用底层的TestDispatcherServlet。因此，没有建立真正的网络连接，因此，我们不会在使用MockMvc时测试整个网络堆栈。

此外，由于Spring准备了一个伪造的Web应用程序上下文来模拟HTTP请求和响应，它可能不支持完整的Spring应用程序的所有功能。

例如，此模拟设置不支持[HTTP重定向](https://github.com/spring-projects/spring-boot/issues/7321)。乍一看，这似乎并不那么重要。但是，Spring Boot通过将当前请求重定向到/error端点来处理一些错误。因此，如果我们使用MockMvc，我们可能无法测试某些API故障。

作为MockMvc的替代方案， 我们可以设置一个更真实的应用程序上下文，然后使用[RestTemplate](https://www.baeldung.com/rest-template)甚至[REST-assured](https://www.baeldung.com/rest-assured-tutorial)来测试我们的应用程序。

例如，使用Spring Boot很容易：

```java
@SpringBootTest(webEnvironment = DEFINED_PORT)
public class GreetControllerRealIntegrationTest {

    @Before
    public void setUp() {
        RestAssured.port = DEFAULT_PORT;
    }

    @Test
    public void givenGreetURI_whenSendingReq_thenVerifyResponse() {
        given().get("/greet")
              .then()
              .statusCode(200);
    }
}
```

在这里，我们甚至不需要添加@ExtendWith(SpringExtension.class)。

这样，每个测试都会向在随机TCP端口上监听的应用程序发出真正的HTTP请求。

## 6. 总结

在本文中，我们实现了一些简单的支持Spring的集成测试。

我们还查看了WebApplicationContext和MockMvc对象的创建，它们在调用应用程序的端点方面起着重要作用。

更进一步，我们讨论了如何发送具有不同参数传递的GET和POST请求，以及如何验证HTTP响应状态、标头和内容。

然后我们评估了MockMvc的一些局限性。了解这些限制可以指导我们就如何实施测试做出明智的决定。