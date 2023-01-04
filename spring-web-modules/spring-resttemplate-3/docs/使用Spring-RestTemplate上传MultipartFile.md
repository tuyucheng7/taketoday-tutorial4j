## 1. 概述

本快速教程重点介绍如何使用Spring的RestTemplate上传MultipartFile。

我们将同时看到单个文件和多个文件，使用RestTemplate上传。

## 2. 什么是HTTP Multipart请求？

简而言之，基本的HTTP POST请求主体以名称/值对的形式保存表单数据。

另一方面，HTTP客户端可以构造HTTP Multipart请求以向服务器发送文本或二进制文件；它主要用于上传文件。

另一个常见的用例是发送带有附件的电子邮件。Multipart文件请求将大文件分成较小的块，并使用边界标记来指示块的开始和结束。

在[此处](https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html)探索有关Multipart请求的更多信息。

## 3. Maven依赖

这个单一的依赖关系对于客户端应用程序来说已经足够了：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
```

## 4. 文件上传服务器

文件服务器API公开了两个REST端点，分别用于上传单个文件和多个文件：

-   POST /fileserver/singlefileupload/
-   POST /fileserver/multiplefileupload/

## 5. 上传单个文件

首先，让我们看看使用RestTemplate上传单个文件。

我们需要创建带有header和body的HttpEntity。将内容类型标头值设置为MediaType.MULTIPART_FORM_DATA。设置此标头后，RestTemplate会自动编组文件数据以及一些元数据。

元数据包括文件名、文件大小和文件内容类型(例如text/plain)：

```java
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.MULTIPART_FORM_DATA);
```

接下来，将请求主体构建为[LinkedMultiValueMap](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/LinkedMultiValueMap.html)类的实例。LinkedMultiValueMap包装了LinkedHashMap，为LinkedList中的每个键存储多个值。

在我们的示例中，getTestFile()方法动态生成一个虚拟文件并返回一个[FileSystemResource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/io/FileSystemResource.html)：

```java
MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
body.add("file", getTestFile());
```

最后，构造一个HttpEntity实例来包装标头和正文对象，并使用RestTemplate发布它。

请注意，单个文件上传指向/fileserver/singlefileupload/端点。

最后，调用[restTemplate.postForEntity()](https://www.baeldung.com/rest-template)完成连接到给定URL并将文件发送到服务器的工作：

```java
HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

String serverUrl = "http://localhost:8082/spring-rest/fileserver/singlefileupload/";

RestTemplate restTemplate = new RestTemplate();
ResponseEntity<String> response = restTemplate
    .postForEntity(serverUrl, requestEntity, String.class);
```

## 6. 上传多个文件

在多文件上传中，与单文件上传相比的唯一变化是构建请求的主体。

让我们创建多个文件并在MultiValueMap中使用相同的键添加它们。

显然，请求URL应该指的是多文件上传的端点：

```java
MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
body.add("files", getTestFile());
body.add("files", getTestFile());
body.add("files", getTestFile());
    
HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

String serverUrl = "http://localhost:8082/spring-rest/fileserver/multiplefileupload/";

RestTemplate restTemplate = new RestTemplate();
ResponseEntity<String> response = restTemplate
    .postForEntity(serverUrl, requestEntity, String.class);
```

始终可以使用多文件上传来模拟单个文件上传。

## 7. 总结

总之，我们看到了一个使用SpringRestTemplate传输MultipartFile的案例。