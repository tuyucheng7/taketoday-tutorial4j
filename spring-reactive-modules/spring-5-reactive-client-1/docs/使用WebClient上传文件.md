## 1. 概述

我们的应用程序通常必须通过HTTP请求处理文件上传。从Spring 5开始，我们现在可以让这些请求成为响应式的。对[响应式编程](https://www.baeldung.com/java-reactive-systems)的附加支持允许我们使用少量线程和[背压](https://www.baeldung.com/spring-webflux-backpressure)以非阻塞方式工作。

在本文中，我们将使用[WebClient](https://www.baeldung.com/spring-5-webclient)(一个非阻塞、响应式HTTP客户端)来说明如何上传文件。WebClient是名为Project Reactor的响应式编程库的一部分。我们将介绍使用BodyInserter上传文件的两种不同方法。

## 2. 使用WebClient上传文件

为了使用WebClient，我们需要将spring-boot-starter-webflux依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>. 
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### 2.1 从资源上传文件

首先，我们要声明我们的URL：

```java
URI url = UriComponentsBuilder.fromHttpUrl(EXTERNAL_UPLOAD_URL).build().toUri();
```

假设在此示例中我们要上传PDF。**我们将使用MediaType.APPLICATION_PDF作为我们的ContentType**。我们的上传端点返回一个HttpStatus。由于我们只期望一个结果，因此我们将其包装在Mono中：

```java
Mono<HttpStatus> httpStatusMono = webClient.post()
    .uri(url)
    .contentType(MediaType.APPLICATION_PDF)
    .body(BodyInserters.fromResource(resource))
    .exchangeToMono(response -> {
        if (response.statusCode().equals(HttpStatus.OK)) {
            return response.bodyToMono(HttpStatus.class).thenReturn(response.statusCode());
        } else {
            throw new ServiceException("Error uploading file");
        }
     });
```

使用这个方法的方法也可以返回一个Mono，我们可以继续，直到我们真正需要访问结果。准备就绪后，我们可以调用Mono对象的block()方法。

fromResource()方法使用传递的资源的InputStream写入输出消息。

### 2.2 从Multipart资源上传文件

**如果我们的外部上传端点接收multipart-form数据，我们可以使用MultiPartBodyBuilder来处理这些部分**：

```java
MultipartBodyBuilder builder = new MultipartBodyBuilder();
builder.part("file", multipartFile.getResource());
```

在这里，我们可以根据需要添加各种部件。map中的值可以是对象或HttpEntity。

当我们调用WebClient时，我们使用BodyInserter.fromMultipartData并构建对象：

```java
.body(BodyInserters.fromMultipartData(builder.build()))
```

我们将内容类型更新为MediaType.MULTIPART_FORM_DATA以反映更改。

让我们看一下整个调用：

```java
Mono<HttpStatus> httpStatusMono = webClient.post()
    .uri(url)
    .contentType(MediaType.MULTIPART_FORM_DATA)
    .body(BodyInserters.fromMultipartData(builder.build()))
    .exchangeToMono(response -> {
        if (response.statusCode().equals(HttpStatus.OK)) {
            return response.bodyToMono(HttpStatus.class).thenReturn(response.statusCode());
        } else {
            throw new ServiceException("Error uploading file");
        }
    });
```

## 3. 总结

在本教程中，我们展示了两种通过WebClient使用BodyInserter上传文件的方法。