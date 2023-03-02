## 1. 概述

在本教程中，我们将演示如何在[Feign](https://www.baeldung.com/intro-to-feign)中处理异常。Feign是微服务开发者的利器，**支持[ErrorDecoder](https://appdoc.app/artifact/com.netflix.feign/feign-core/8.11.0/feign/codec/ErrorDecoder.html)和[FallbackFactory](https://github.com/OpenFeign/feign/blob/master/hystrix/src/main/java/feign/hystrix/FallbackFactory.java)进行异常处理**。

## 2. Maven依赖

首先，让我们通过包含[spring-cloud-starter-openfeign](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign)创建一个[Spring Boot](https://www.baeldung.com/spring-boot)项目。**spring-cloud-starter-openfeign包含feign-core依赖**：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>3.1.3</version>
</dependency>
```

或者我们可以将[feign-core](https://search.maven.org/artifact/io.github.openfeign/feign-core)依赖添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-core</artifactId>
    <version>11.9.1</version>
</dependency>
```

## 3. ErrorDecoder异常处理 

**我们可以通过配置ErrorDecoder来处理异常**，这也允许我们在需要时自定义消息。发生错误时，Feign客户端会禁止显示原始消息。要检索它，我们可以编写一个自定义的ErrorDecoder。让我们覆盖默认的ErrorDecoder实现：

```java
public class RetreiveMessageErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();
    
    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionMessage message = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        switch (response.status()) {
            case 400:
                return new BadRequestException(message.getMessage() != null ? message.getMessage() : "Bad Request");
            case 404:
                return new NotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }
}
```

在上面的编码器中，我们覆盖了默认行为以更好地控制异常。

## 4. Fallback异常处理

我们也可以通过配置fallback来处理异常。让我们先创建一个客户端并配置回退：

```java
@FeignClient(name = "file", url = "http://localhost:8081",
      configuration = FeignSupportConfig.class, fallback = FileUploadClientWithFallbackImpl.class)
public interface FileUploadClientWithFallBack {
    @PostMapping(value = "/upload-error", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String fileUpload(@RequestPart(value = "file") MultipartFile file);
}
```

现在，让我们创建FileUploadClientWithFallbackImpl来根据我们的要求处理异常：

```java
@Component
public class FileUploadClientWithFallbackImpl implements FileUploadClientWithFallBack {
    @Override
    public String fileUpload(MultipartFile file) {
        try {
            throw new NotFoundException("hi, something wrong");
        } catch (Exception ex) {
            if (ex instanceof BadRequestException) {
                return "Bad Request!!!";
            }
            if (ex instanceof NotFoundException) {
                return "Not Found!!!";
            }
            if (ex instanceof Exception) {
                return "Exception!!!";
            }
            return "Successfully Uploaded file!!!";
        }
    }
}
```

现在让我们创建一个简单的测试来验证回退选项：

```java
@Test(expected = NotFoundException.class)
public void whenFileUploadClientFallback_thenFileUploadError() throws IOException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    File file = new File(classloader.getResource(FILE_NAME).getFile());
    Assert.assertTrue(file.exists());
    FileInputStream input = new FileInputStream(file);
    MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
        IOUtils.toByteArray(input));
    uploadService.uploadFileWithFallback(multipartFile);
}
```

## 5. FallbackFactory异常处理

我们也可以通过配置FallbackFactory来处理异常。让我们先创建一个客户端并配置FallbackFactory：

```java
@FeignClient(name = "file", url = "http://localhost:8081",
      configuration = FeignSupportConfig.class, fallbackFactory = FileUploadClientFallbackFactory.class)
public interface FileUploadClient {
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String fileUpload(@RequestPart(value = "file") MultipartFile file);
}
```

现在，让我们创建FileUploadClientFallbackFactory来根据我们的要求处理异常：

```java
@Component
public class FileUploadClientFallbackFactory implements FallbackFactory<FileUploadClient> {
    @Override
    public FileUploadClient create(Throwable cause) {
        return new FileUploadClient() {
            @Override
            public String fileUpload(MultipartFile file) {
                if (cause instanceof BadRequestException) {
                    return "Bad Request!!!";
                }
                if (cause instanceof NotFoundException) {
                    return "Not Found!!!";
                }
                if (cause instanceof Exception) {
                    return "Exception!!!";
                }
                return "Successfully Uploaded file!!!";
            }
        };
    }
}
```

现在让我们创建一个简单的测试来验证FallbackFactory选项：

```java
@Test(expected = NotFoundException.class)
public void whenFileUploadClientFallbackFactory_thenFileUploadError() throws IOException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    File file = new File(classloader.getResource(FILE_NAME).getFile());
    Assert.assertTrue(file.exists());
    FileInputStream input = new FileInputStream(file);
    MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
        IOUtils.toByteArray(input));
    uploadService.uploadFileWithFallbackFactory(multipartFile);
}
```

## 6. 总结

在本文中，我们演示了如何在Feign中处理异常。