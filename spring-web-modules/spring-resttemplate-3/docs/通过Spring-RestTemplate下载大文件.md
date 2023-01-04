## 1. 概述

在本教程中，我们将展示有关如何使用RestTemplate[下载大文件](https://www.baeldung.com/java-download-file)的不同技术。

## 2. RestTemplate

[RestTemplate](https://www.baeldung.com/rest-template)是Spring 3中引入的阻塞和同步HTTP客户端。根据[Spring文档](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)，它在未来将被弃用，因为他们在版本5中引入了[WebClient](https://www.baeldung.com/spring-5-webclient)作为响应式非阻塞HTTP客户端。

## 3. 陷阱

通常，当我们下载一个文件时，我们将它存储在我们的文件系统中或者以字节数组的形式加载到内存中。但是当它是一个大文件时，内存加载可能会导致[OutOfMemoryError](https://www.baeldung.com/java-gc-overhead-limit-exceeded)。因此，我们必须在读取响应块时将数据存储在文件中。

让我们先看看一些行不通的方法：

首先，如果我们返回Resource作为我们的返回类型会发生什么：

```java
Resource download() {
    return new ClassPathResource(locationForLargeFile);
}
```

这不起作用的原因是ResourceHttpMessageConverter会将整个响应主体加载到ByteArrayInputStream中，仍然会增加我们想要避免的内存压力。

其次，如果我们返回一个InputStreamResource并配置ResourceHttpMessageConverter#supportsReadStreaming会怎么样？好吧，这也不起作用，因为当我们可以调用InputStreamResource.getInputStream()时，我们得到一个“socket closed”错误！这是因为“execute”在退出之前关闭了响应输入流。

那么我们可以做些什么来解决这个问题呢？实际上，这里也有两件事：

-   编写一个支持File作为返回类型的自定义[HttpMessageConverter](https://www.baeldung.com/spring-httpmessageconverter-rest)
-   使用带有自定义ResponseExtractor的RestTemplate.execute将输入流存储在文件中

在本教程中，我们将使用第二种解决方案，因为它更灵活并且需要的工作更少。

## 4. 不续传下载

让我们实现一个ResponseExtractor来将正文写入一个临时文件：

```java
File file = restTemplate.execute(FILE_URL, HttpMethod.GET, null, clientHttpResponse -> {
    File ret = File.createTempFile("download", "tmp");
    StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
    return ret;
});

Assert.assertNotNull(file);
Assertions
    .assertThat(file.length())
    .isEqualTo(contentLength);
```

在这里，我们使用StreamUtils.copy将响应输入流到FileOutputStream中， 但也可以使用[其他技术和库](https://www.baeldung.com/convert-input-stream-to-a-file)。

## 5. 下载暂停和恢复

由于我们要下载一个大文件，因此考虑在我们因某种原因暂停后下载是合理的。

所以首先让我们检查一下下载网址是否支持恢复：

```java
HttpHeaders headers = restTemplate.headForHeaders(FILE_URL);

Assertions
    .assertThat(headers.get("Accept-Ranges"))
    .contains("bytes");
Assertions
    .assertThat(headers.getContentLength())
    .isGreaterThan(0);
```

然后我们可以实现一个RequestCallback来设置“Range”标头并恢复下载：

```java
restTemplate.execute(FILE_URL, HttpMethod.GET,
    clientHttpRequest -> clientHttpRequest.getHeaders().set(
        "Range",
        String.format("bytes=%d-%d", file.length(), contentLength)),
        clientHttpResponse -> {
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(file, true));
    return file;
});

Assertions
    .assertThat(file.length())
    .isLessThanOrEqualTo(contentLength);
```

如果我们不知道确切的内容长度，我们可以使用String.format设置Range标头值：

```java
String.format("bytes=%d-", file.length())
```

## 6. 总结

我们已经讨论了下载大文件时可能出现的问题。我们在使用RestTemplate时也提出了一个解决方案。最后，我们展示了如何实现可恢复下载。