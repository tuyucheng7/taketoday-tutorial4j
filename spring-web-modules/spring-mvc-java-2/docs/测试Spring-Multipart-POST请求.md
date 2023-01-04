## 1. 概述

在本快速教程中，我们将了解如何使用[MockMvc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/MockMvc.html)在Spring中测试Multipart POST请求。

## 2. Maven依赖

在开始之前，让我们在pom.xml中添加最新的[JUnit](https://search.maven.org/artifact/junit/junit/4.13.2/jar)和[Spring测试](https://search.maven.org/search?q=a:spring-test)依赖项：

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>5.1.16.RELEASE</version>
    <scope>test</scope>
</dependency>
```

## 3. 测试Multipart POST请求

让我们在REST控制器中创建一个简单的端点：

```java
@PostMapping(path = "/upload")
public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    return file.isEmpty() ? new ResponseEntity<String>(HttpStatus.NOT_FOUND) : new ResponseEntity<String>(HttpStatus.OK);
}
```

在这里，uploadFile方法接受Multipart POST请求。在此方法中，如果文件存在，我们将发送状态码200；否则，我们将发送状态码404。

现在，让我们使用MockMvc测试上述方法。

首先，让我们在单元测试类中自动装配[WebApplicationContext](https://www.baeldung.com/integration-testing-in-spring#2-the-webapplicationcontext-object)：

```java
@Autowired
private WebApplicationContext webApplicationContext;
```

现在，让我们编写一个方法来测试上面定义的Multipart POST请求：

```java
@Test
public void whenFileUploaded_thenVerifyStatus()throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mockMvc.perform(multipart("/upload").file(file))
        .andExpect(status().isOk());
}
```

在这里，我们使用[MockMultipartFile](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/mock/web/MockMultipartFile.html)构造函数定义了一个hello.txt文件，然后我们使用之前定义的webApplicationContext对象构建了mockMvc对象。

我们将使用[MockMvc#perform](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/MockMvc.html#perform-org.springframework.test.web.servlet.RequestBuilder-)方法调用REST端点并将文件对象传递给它。最后，我们将检查状态码200来验证我们的测试用例。

## 4. 总结

在本文中，我们了解了如何借助示例使用MockMvc测试Spring Multipart POST请求。