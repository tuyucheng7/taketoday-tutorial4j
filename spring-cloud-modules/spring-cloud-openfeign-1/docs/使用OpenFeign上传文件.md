## 1. 概述

在本教程中，我们将演示如何使用 Open Feign 上传文件。[Feign](https://www.baeldung.com/intro-to-feign)是微服务开发人员以声明方式通过 REST API 与其他微服务进行通信的强大工具。

## 2. 先决条件

假设为文件上传公开了一个 RESTful Web 服务，下面给出了详细信息：

```java
POST http://localhost:8081/upload-file
```

因此，为了解释通过Feign客户端上传文件，我们将调用暴露的 Web 服务 API，如下所示：

```java
@PostMapping(value = "/upload-file")
public String handleFileUpload(@RequestPart(value = "file") MultipartFile file) {
    // File upload logic
}
```

## 3.依赖关系

为了支持文件上传的application/x-www-form-urlencoded和multipart/form-data编码类型，我们需要[feign-core](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-core")、[feign-form](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign.form" AND a%3A"feign-form")和feign [-form-spring](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign.form" AND a%3A"feign-form-spring")模块。

因此，我们将向 Maven 添加以下依赖项：

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-core</artifactId>
    <version>10.12</version>
</dependency>
<dependency>
    <groupId>io.github.openfeign.form</groupId>
    <artifactId>feign-form</artifactId>
    <version>3.8.0</version>
</dependency>
<dependency>
    <groupId>io.github.openfeign.form</groupId>
    <artifactId>feign-form-spring</artifactId>
    <version>3.8.0</version>
</dependency>
```

我们还可以使用[spring-cloud-starter-openfeign](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-core")，它内部有feign -core：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>3.1.0</version>
</dependency>
```

## 4.配置

让我们将@EnableFeignClients添加到我们的主类。详情可以访问[spring cloud open feign](https://www.baeldung.com/spring-cloud-openfeign) 教程：

```java
@SpringBootApplication
@EnableFeignClients
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

@EnableFeignClients注解允许组件扫描声明为 Feign 客户端的接口。

## 5. Feign客户端上传文件

### 5.1. 通过带注解的客户端

让我们为带注解的@FeignClient类创建所需的编码器：

```java
public class FeignSupportConfig {
    @Bean
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
            @Override
            public HttpMessageConverters getObject() throws BeansException {
                return new HttpMessageConverters(new RestTemplate().getMessageConverters());
            }
        }));
    }
}
```

注意FeignSupportConfig不需要注解@Configuration。

现在，让我们创建一个接口并用@FeignClient注解它。我们还将添加名称和配置属性及其相应的值：

```java
@FeignClient(name = "file", url = "http://localhost:8081", configuration = FeignSupportConfig.class)
public interface UploadClient {
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String fileUpload(@RequestPart(value = "file") MultipartFile file);
}

```

UploadClient指向先决条件中提到的 API 。

在使用Hystrix时，我们将使用fallback属性作为替代添加。这是在上传 API 失败时完成的。

现在我们的@FeignClient 看起来像这样：

```java
@FeignClient(name = "file", url = "http://localhost:8081", fallback = UploadFallback.class, configuration = FeignSupportConfig.class)
```

最后，我们可以直接从服务层调用UploadClient ：

```java
public String uploadFile(MultipartFile file) {
    return client.fileUpload(file);
}
```

### 5.2. 通过Feign.builder

在某些情况下，我们的Feign Clients需要进行自定义，这在上面介绍的注解方式中是做不到的。在这种情况下，我们使用Feign.builder() API 创建客户端。

让我们构建一个代理接口，其中包含一个针对文件上传的 REST API 的文件上传方法：

```java
public interface UploadResource {
    @RequestLine("POST /upload-file")
    @Headers("Content-Type: multipart/form-data")
    Response uploadFile(@Param("file") MultipartFile file);
}
```

注解@RequestLine定义了HTTP方法和API的相对资源路径，@Headers指定了Content-Type等headers。

现在，让我们在代理接口中调用指定的方法。我们将从我们的服务类中执行此操作：

```java
public boolean uploadFileWithManualClient(MultipartFile file) {
    UploadResource fileUploadResource = Feign.builder().encoder(new SpringFormEncoder())
      .target(UploadResource.class, HTTP_FILE_UPLOAD_URL);
    Response response = fileUploadResource.uploadFile(file);
    return response.status() == 200;
}
```

在这里，我们使用了Feign.builder()实用程序来构建UploadResource代理接口的实例。我们还使用了SpringFormEncoder和基于 RESTful Web 服务的 URL。

## 6.验证

让我们创建一个测试来验证带注解的客户端的文件上传：

```java
@SpringBootTest
public class OpenFeignFileUploadLiveTest {
    
    @Autowired
    private UploadService uploadService;
    
    private static String FILE_NAME = "fileupload.txt";
    
    @Test
    public void whenAnnotatedFeignClient_thenFileUploadSuccess() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource(FILE_NAME).getFile());
        Assert.assertTrue(file.exists());
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
          IOUtils.toByteArray(input));
        String uploadFile = uploadService.uploadFile(multipartFile);

        Assert.assertNotNull(uploadFile);
    }
}
```

现在，让我们创建另一个测试来使用Feign.Builder()验证文件上传：

```java
@Test
public void whenFeignBuilder_thenFileUploadSuccess() throws IOException {
    // same as above
    Assert.assertTrue(uploadService.uploadFileWithManualClient(multipartFile));
}
```

## 七. 总结

在本文中，我们展示了如何使用 OpenFeign 实现多部分文件上传，以及将其包含在简单应用程序中的各种方法。

我们还了解了如何配置 Feign 客户端或使用Feign.Builder()来执行相同的操作。