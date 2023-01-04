## 1. 概述

本文探讨了Spring Boot TestRestTemplate。它可以被视为[RestTemplate指南](https://www.baeldung.com/rest-template)的后续，我们强烈建议在关注TestRestTemplate之前阅读它。TestRestTemplate可以被认为是RestTemplate的一个有吸引力的替代品。

## 2. Maven依赖

要使用TestRestTemplate，你需要具有适当的依赖关系，例如：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-test</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

你可以在[Maven Central](https://search.maven.org/search?q=a:spring-boot-test)上找到最新版本。

## 3. 测试RestTemplate和RestTemplate 

这两个客户端都非常适合编写集成测试，并且可以很好地处理与HTTP API的通信。

例如，它们为我们提供了相同的方法标准方法、标头和其他HTTP结构。

所有这些操作在[RestTemplate指南](https://www.baeldung.com/rest-template)中都有很好的描述，因此我们不会在这里重新访问它们。

这是一个简单的GET请求示例：

```java
TestRestTemplate testRestTemplate = new TestRestTemplate();
ResponseEntity<String> response = testRestTemplate.getForEntity(FOO_RESOURCE_URL + "/1", String.class);
 
Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
```

尽管这两个类非常相似，但TestRestTemplate没有扩展RestTemplate并且提供了一些非常令人兴奋的新功能。

## 4. TestRestTemplate有什么新功能？

### 4.1 具有基本Auth凭据的构造函数

TestRestTemplate提供了一个构造函数，我们可以使用它创建一个具有指定凭据的模板以进行基本身份验证。

使用此实例执行的所有请求都将使用提供的凭据进行身份验证：

```java
TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd");
ResponseEntity<String> response = testRestTemplate.getForEntity(URL_SECURED_BY_AUTHENTICATION, String.class);
 
Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
```

### 4.2 带有HttpClientOption的构造函数 

TestRestTemplate还使我们能够使用HttpClientOption自定义底层Apache HTTP客户端，它是TestRestTemplate中的枚举，具有以下选项：ENABLE_COOKIES、ENABLE_REDIRECTS和SSL。

让我们看一个简单的例子：

```java
TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd", TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
ResponseEntity<String> response = testRestTemplate.getForEntity(URL_SECURED_BY_AUTHENTICATION, String.class);
 
Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
```

在上面的示例中，我们将选项与基本身份验证一起使用。

如果我们不需要身份验证，我们仍然可以使用简单的构造函数创建一个模板：

TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES)

### 4.3 新方法

构造函数不仅可以创建具有指定凭据的模板。我们还可以在创建模板后添加凭据。TestRestTemplate为我们提供了一个withBasicAuth()方法，它可以将凭据添加到一个已经存在的模板中：

```java
TestRestTemplate testRestTemplate = new TestRestTemplate();
ResponseEntity<String> response = testRestTemplate.withBasicAuth("user", "passwd").getForEntity(URL_SECURED_BY_AUTHENTICATION, String.class);
 
Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
```

## 5. 同时使用TestRestTemplate和RestTemplate

TestRestTemplate可以作为RestTemplate的包装器，例如，如果我们因为处理遗留代码而被迫使用它。你可以在下面看到如何创建这样一个简单的包装器：

```java
RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
restTemplateBuilder.configure(restTemplate);
TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder);
ResponseEntity<String> response = testRestTemplate.getForEntity(FOO_RESOURCE_URL + "/1", String.class);
 
Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
```

## 6. 总结

TestRestTemplate不是RestTemplate的扩展，而是一种简化集成测试并促进测试期间身份验证的替代方案。它有助于定制Apache HTTP客户端，但也可以用作RestTemplate的包装器。