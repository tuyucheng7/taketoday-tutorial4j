## 1. 简介

AWS 通过它的许多 API 提供许多服务，我们可以使用他们的官方 SDK 从Java访问这些 API。不过直到最近，该 SDK 才提供对反应式操作的支持，并且对异步访问的支持也很有限。

随着 AWS SDK forJava2.0 的发布，我们现在可以在完全非阻塞 I/O 模式下使用这些 API，这要归功于它采用了 Reactive Streams 标准。

在本教程中，我们将通过在Spring Boot中实现一个简单的 blob 存储 REST API 来探索这些新功能，该 API 使用众所周知的 S3 服务作为其存储后端。

## 二、AWS S3操作概述

在深入实施之前，让我们快速概述一下我们想要在这里实现的目标。典型的 blob 存储服务公开前端应用程序使用的 CRUD 操作，以允许最终用户上传、列出、下载和删除多种类型的内容，例如音频、图片和文档。

传统实现必须处理的一个常见问题是如何有效地处理大文件或慢速连接。在早期版本(servlet 3.0 之前)中，JavaEE 规范必须提供的只是一个阻塞 API，因此我们需要为每个并发 blob 存储客户端提供一个线程。这种模型的缺点是需要更多的服务器资源(因此，更大的机器)并使它们更容易受到 DoS 类攻击：

[![反应式上传](https://www.baeldung.com/wp-content/uploads/2019/12/rective-upload.png)](https://www.baeldung.com/wp-content/uploads/2019/12/rective-upload.png)[![每个客户端线程](https://www.baeldung.com/wp-content/uploads/2019/12/thread-per-client-1.png)](https://www.baeldung.com/wp-content/uploads/2019/12/thread-per-client-1.png)

通过使用反应式堆栈，我们可以使我们的服务对于相同数量的客户端来说占用的资源更少。反应器实现使用少量线程，这些线程是为响应 I/O 完成事件而分派的，例如有新数据可供读取或先前的写入已完成。

这意味着同一个线程继续处理这些事件——这些事件可以源自任何活动的客户端连接——直到没有更多可用的工作要做。这种方法大大减少了上下文切换的次数——一个相当昂贵的操作——并允许非常有效地使用可用资源：

[![直接上传](https://www.baeldung.com/wp-content/uploads/2019/12/rective-upload.png)](https://www.baeldung.com/wp-content/uploads/2019/12/rective-upload.png)

反应式上传

## 3.项目设置

我们的演示项目是一个标准的[Spring Boot WebFlux](https://www.baeldung.com/spring-webflux)应用程序，其中包括常用的支持依赖项，例如 Lombok 和 JUnit。

除了这些库之外，我们还需要引入 AWS SDK forJavaV2 依赖项：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>bom</artifactId>
            <version>2.10.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
        <scope>compile</scope>
    </dependency>

    <dependency>
        <artifactId>netty-nio-client</artifactId>
        <groupId>software.amazon.awssdk</groupId>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

AWS SDK[提供了一个定义所有依赖项所需版本的 BOM](https://search.maven.org/search?q=g:software.amazon.awssdk a:bom)，因此我们无需在 POM 文件的依赖项部分中指定它们。

我们添加了 S3 客户端库，它将带来来自 SDK 的其他核心依赖项。我们还需要 Netty 客户端库，这是必需的，因为我们将使用异步 API 与 AWS 交互。

[官方 AWS 文档](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/client-configuration-http.html)包含有关可用传输的更多详细信息。

## 4. AWS S3 客户端创建

S3 操作的入口点是S3AsyncClient类，我们将使用它来启动新的 API 调用。

因为我们只需要这个类的一个实例，所以让我们创建一个带有@Bean方法的@Configuration类来构建它，这样我们就可以在需要的地方注入它：

```java
@Configuration
@EnableConfigurationProperties(S3ClientConfigurarionProperties.class)
public class S3ClientConfiguration {
    @Bean
    public S3AsyncClient s3client(S3ClientConfigurarionProperties s3props, 
      AwsCredentialsProvider credentialsProvider) {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
          .writeTimeout(Duration.ZERO)
          .maxConcurrency(64)
          .build();
        S3Configuration serviceConfiguration = S3Configuration.builder()
          .checksumValidationEnabled(false)
          .chunkedEncodingEnabled(true)
          .build();
        S3AsyncClientBuilder b = S3AsyncClient.builder().httpClient(httpClient)
          .region(s3props.getRegion())
          .credentialsProvider(credentialsProvider)
          .serviceConfiguration(serviceConfiguration);

        if (s3props.getEndpoint() != null) {
            b = b.endpointOverride(s3props.getEndpoint());
        }
        return b.build();
    }
}
```

对于这个演示，我们使用了一个最小的@ConfigurationProperties类(在我们的存储库中可用)，它包含访问 S3 服务所需的以下信息：

-   region：有效的 AWS 区域标识符，例如us-east-1
-   accessKeyId/secretAccessKey：我们的 AWS API 密钥和标识符
-   端点：一个可选的 URI，我们可以使用它来覆盖 S3 的默认服务端点。主要用例是将演示代码与提供 S3 兼容 API 的替代存储解决方案一起使用(例如 minio 和 localstack)
-   bucket：我们将存储上传文件的存储桶的名称

关于客户端的初始化代码，有几点值得一提。首先，我们禁用写入超时并增加最大并发数，因此即使在低带宽情况下也可以完成上传。

其次，我们将禁用校验和验证并启用分块编码。我们这样做是因为我们希望在数据以流方式到达我们的服务时立即开始将数据上传到存储桶。

最后，我们没有解决存储桶创建本身的问题，因为我们假设它已经由管理员创建和配置。

至于凭据，我们提供了一个自定义的 AwsCredentialsProvider ，它可以从 Spring 属性中恢复凭据。这开启了通过 Spring 的环境抽象及其所有支持的 PropertySource 实现注入这些值的可能性，例如 Vault 或 Config Server：

```java
@Bean
public AwsCredentialsProvider awsCredentialsProvider(S3ClientConfigurarionProperties s3props) {
    if (StringUtils.isBlank(s3props.getAccessKeyId())) {
        return DefaultCredentialsProvider.create();
    } else {
        return () -> {
            return AwsBasicCredentials.create(
              s3props.getAccessKeyId(),
              s3props.getSecretAccessKey());
        };
    }
}
```

## 五、上传服务概述

我们现在将实现一个上传服务，我们可以通过/inbox路径访问它。

对此资源路径的POST将根据随机生成的密钥将文件存储在我们的 S3 存储桶中。我们会将原始文件名存储为元数据键，因此我们可以使用它为浏览器生成适当的 HTTP 下载标头。

我们需要处理两种截然不同的场景：简单上传和分段上传。让我们继续创建一个@RestController并添加方法来处理这些场景：

```java
@RestController
@RequestMapping("/inbox")
@Slf4j
public class UploadResource {
    private final S3AsyncClient s3client;
    private final S3ClientConfigurarionProperties s3config;

    public UploadResource(S3AsyncClient s3client, S3ClientConfigurarionProperties s3config) {
        this.s3client = s3client;
        this.s3config = s3config;        
    }
    
    @PostMapping
    public Mono<ResponseEntity<UploadResult>> uploadHandler(
      @RequestHeader HttpHeaders headers, 
      @RequestBody Flux<ByteBuffer> body) {
      // ... see section 6
    }

    @RequestMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      method = {RequestMethod.POST, RequestMethod.PUT})
    public Mono<ResponseEntity<UploadResult>> multipartUploadHandler(
      @RequestHeader HttpHeaders headers,
      @RequestBody Flux<Part> parts ) {
      // ... see section 7
    }
}
```

处理程序签名反映了两种情况之间的主要区别：在简单情况下，主体包含文件内容本身，而在多部分情况下，它可以有多个“部分”，每个部分对应一个文件或表单数据。

为方便起见，我们将支持使用 POST 或 PUT 方法进行分段上传。这样做的原因是某些工具(特别是[cURL](https://www.baeldung.com/linux/curl-wget))在使用-F选项上传文件时默认使用后者。

在这两种情况下，我们都会返回一个 包含操作结果的UploadResult和客户端应该用来恢复原始文件的生成的文件密钥——稍后会详细介绍！

## 6. 单文件上传

在这种情况下，客户端通过简单的 POST 操作发送内容，请求正文包含原始数据。要在 Reactive Web 应用程序中接收此内容，我们所要做的就是声明一个带有Flux<ByteBuffer> 参数的@PostMapping 方法 。

在这种情况下，将此通量流式传输到新的 S3 文件很简单。

我们所需要做的就是使用生成的密钥、文件长度、MIME 内容类型构建一个 PutObjectRequest ，并将其传递给S3 客户端中的putObject()方法：

```java
@PostMapping
public Mono<ResponseEntity<UploadResult>> uploadHandler(@RequestHeader HttpHeaders headers,
  @RequestBody Flux<ByteBuffer> body) {
    // ... some validation code omitted
    String fileKey = UUID.randomUUID().toString();
    MediaType mediaType = headers.getContentType();

    if (mediaType == null) {
        mediaType = MediaType.APPLICATION_OCTET_STREAM;
    }
    CompletableFuture future = s3client
      .putObject(PutObjectRequest.builder()
        .bucket(s3config.getBucket())
        .contentLength(length)
        .key(fileKey.toString())
        .contentType(mediaType.toString())
        .metadata(metadata)
        .build(), 
      AsyncRequestBody.fromPublisher(body));

    return Mono.fromFuture(future)
      .map((response) -> {
        checkResult(response);
        return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(new UploadResult(HttpStatus.CREATED, new String[] {fileKey}));
        });
}
```

这里的关键点是我们如何将传入的 Flux传递给 putObject() 方法。

此方法需要一个 按需提供内容的AsyncRequestBody对象。基本上，它是一个带有一些额外便利方法的常规Publisher 。在我们的例子中，我们将利用fromPublisher ()方法将我们的 Flux转换为所需的类型。

此外，我们假设客户端将发送具有正确值的Content-Length HTTP 标头。如果没有此信息，调用将失败，因为这是必填字段。

SDK V2 中的异步方法总是返回一个CompletableFuture对象。我们使用它的 fromFuture()工厂方法将它改编为Mono 。这会映射到最终的UploadResult对象。

## 7.上传多个文件

处理多部分/表单数据上传似乎很容易，尤其是在使用为我们处理所有细节的库时。那么，我们是否可以简单地对每个上传的文件使用前面的方法呢？嗯，是的，但这是有代价的：缓冲。

要使用之前的方法，我们需要该部分的长度，但分块文件传输并不总是包含此信息。一种方法是将零件存储在临时文件中，然后将其发送到 AWS，但这会减慢总上传时间。这也意味着我们的服务器需要额外的存储空间。

作为替代方案，我们将在此处使用 AWS 分段上传。此功能允许将单个文件的上传分成多个块，我们可以并行且无序地发送这些块。

步骤如下，我们需要发送：

-   createMultipartUpload请求 – AWS 以我们将在下一次调用中使用的uploadId响应
-   包含uploadId、序列号和内容的文件块——AWS为每个部分响应一个ETag标识符
-   包含uploadId和收到的所有 ETag的 完整上传请求

请注意：我们将为 每个收到的FilePart重复这些步骤！

### 7.1. 顶级管道

我们@Controller类中的 multipartUploadHandler负责处理多部分文件上传。在这种情况下，每个部分都可以有任何类型的数据，由其 MIME 类型标识。Reactive Web 框架将这些部分作为实现Part接口的对象 的Flux传递给我们的处理程序，我们将依次处理：

```java
return parts
  .ofType(FilePart.class)
  .flatMap((part)-> saveFile(headers, part))
  .collect(Collectors.toList())
  .map((keys)-> new UploadResult(HttpStatus.CREATED, keys)));
```

此管道首先过滤对应于实际上传文件的部分，该文件始终是实现FilePart接口的对象。然后将每个部分传递给 saveFile 方法，该方法处理单个文件的实际上传并返回生成的文件密钥。

我们将所有键收集到一个列表中，最后构建最终的UploadResult。我们总是在创建新资源，因此我们将返回更具描述性的 CREATED状态 (202) 而不是常规的OK。

### 7.2. 处理单个文件上传

我们已经概述了使用 AWS 的多部分方法上传文件所需的步骤。不过有一个问题：S3 服务要求除最后一个部分之外的每个部分都必须具有给定的最小大小——目前为 5 兆字节。

这意味着我们不能直接获取接收到的块并立即发送它们。相反，我们需要在本地缓冲它们，直到我们达到最小大小或数据结束。由于我们还需要一个地方来跟踪我们发送了多少部分以及生成的CompletedPart结果，我们将创建一个简单的UploadState内部类来保存此状态：

```java
class UploadState {
    String bucket;
    String filekey;
    String uploadId;
    int partCounter;
    Map<Integer, CompletedPart> completedParts = new HashMap<>();
    int buffered = 0;
    // ... getters/setters omitted
    UploadState(String bucket, String filekey) {
        this.bucket = bucket;
        this.filekey = filekey;
    }
}
```

考虑到所需的步骤和缓冲，我们最终的实现乍一看可能看起来有点吓人：

```java
Mono<String> saveFile(HttpHeaders headers,String bucket, FilePart part) {
    String filekey = UUID.randomUUID().toString();
    Map<String, String> metadata = new HashMap<String, String>();
    String filename = part.filename();
    if ( filename == null ) {
        filename = filekey;
    }       
    metadata.put("filename", filename);    
    MediaType mt = part.headers().getContentType();
    if ( mt == null ) {
        mt = MediaType.APPLICATION_OCTET_STREAM;
    }
    UploadState uploadState = new UploadState(bucket,filekey);     
    CompletableFuture<CreateMultipartUploadResponse> uploadRequest = s3client
      .createMultipartUpload(CreateMultipartUploadRequest.builder()
        .contentType(mt.toString())
        .key(filekey)
        .metadata(metadata)
        .bucket(bucket)
        .build());

    return Mono
      .fromFuture(uploadRequest)
      .flatMapMany((response) -> {
          checkResult(response);              
          uploadState.uploadId = response.uploadId();
          return part.content();
      })
      .bufferUntil((buffer) -> {
          uploadState.buffered += buffer.readableByteCount();
          if ( uploadState.buffered >= s3config.getMultipartMinPartSize() ) {
              uploadState.buffered = 0;
              return true;
          } else {
              return false;
          }
      })
      .map((buffers) -> concatBuffers(buffers))
      .flatMap((buffer) -> uploadPart(uploadState,buffer))
      .reduce(uploadState,(state,completedPart) -> {
          state.completedParts.put(completedPart.partNumber(), completedPart);              
          return state;
      })
      .flatMap((state) -> completeUpload(state))
      .map((response) -> {
          checkResult(response);
          return  uploadState.filekey;
      });
}
```

我们首先收集一些文件元数据并使用它来创建 createMultipartUpload() API 调用所需的请求对象。此调用返回一个 CompletableFuture，它是我们流式传输管道的起点。

让我们回顾一下这个管道的每个步骤做了什么：

-   在收到包含 S3 生成的 uploadId的初始结果后，我们将其保存在上传状态对象中并开始流式传输文件的主体。注意这里使用了 flatMapMany，它将 Mono变成了 Flux
-   我们使用bufferUntil()来累积所需的字节数。此时的管道从 DataBuffer 对象的 Flux 变为 List < DataBuffer>对象的Flux 
-   将每个List<DataBuffer>转换为ByteBuffer
-   将 ByteBuffer 发送到 S3(请参阅下一节)并向下游返回生成的CompletedPart值
-   将生成的CompletedPart值减少到uploadState
-   向 S3 发出我们已完成上传的信号(稍后会详细介绍)
-   返回生成的文件密钥

### 7.3. 上传文件部分

再次说明，这里的“文件部分”是指单个文件的一部分(例如，一个 100MB 文件的前 5MB)，而不是恰好是文件的消息的一部分，因为它在顶级流！

文件上传管道使用两个参数调用uploadPart()方法：上传状态和 ByteBuffer。从那里，我们构建一个UploadPartRequest实例并使用 S3AsyncClient 中可用的uploadPart ()方法发送数据：

```java
private Mono<CompletedPart> uploadPart(UploadState uploadState, ByteBuffer buffer) {
    final int partNumber = ++uploadState.partCounter;
    CompletableFuture<UploadPartResponse> request = s3client.uploadPart(UploadPartRequest.builder()
        .bucket(uploadState.bucket)
        .key(uploadState.filekey)
        .partNumber(partNumber)
        .uploadId(uploadState.uploadId)
        .contentLength((long) buffer.capacity())
        .build(), 
        AsyncRequestBody.fromPublisher(Mono.just(buffer)));
    
    return Mono
      .fromFuture(request)
      .map((uploadPartResult) -> {              
          checkResult(uploadPartResult);
          return CompletedPart.builder()
            .eTag(uploadPartResult.eTag())
            .partNumber(partNumber)
            .build();
      });
}
```

在这里，我们使用uploadPart()请求的返回值来构建 CompletedPart实例。这是我们稍后在构建关闭上传的最终请求时需要的 AWS SDK 类型。

### 7.4. 完成上传

最后但同样重要的是，我们需要通过向 S3 发送completeMultipartUpload()请求来完成多部分文件上传。这很容易，因为上传管道将我们需要的所有信息作为参数传递：

```java
private Mono<CompleteMultipartUploadResponse> completeUpload(UploadState state) {        
    CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
        .parts(state.completedParts.values())
        .build();
    return Mono.fromFuture(s3client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
        .bucket(state.bucket)
        .uploadId(state.uploadId)
        .multipartUpload(multipartUpload)
        .key(state.filekey)
        .build()));
}
```

## 8. 从 AWS 下载文件

与分段上传相比，从 S3 存储桶下载对象要简单得多。在这种情况下，我们不必担心块或类似的东西。SDK API 提供了带有两个参数的getObject()方法：

-   包含请求的存储桶和文件密钥的GetObjectRequest对象
-   一个AsyncResponseTransformer，它允许我们将传入的流式响应映射到其他东西

SDK 提供了后者的几个实现，使流适应Flux 成为可能， 但同样需要付出代价：它们在数组 buffer 内部缓冲数据。由于这种缓冲会导致我们的演示服务的客户端响应时间很短，因此我们将实现我们自己的适配器——这没什么大不了的，正如我们将看到的那样。

### 8.1. 下载控制器

我们的下载控制器是一个标准的 Spring Reactive @RestController，带有一个处理下载请求的@GetMapping方法。我们期望通过@PathVariable参数获取文件键，我们将返回一个 包含文件内容的异步ResponseEntity ：

```java
@GetMapping(path="/{filekey}")
Mono<ResponseEntity<Flux<ByteBuffer>>> downloadFile(@PathVariable("filekey") String filekey) {    
    GetObjectRequest request = GetObjectRequest.builder()
      .bucket(s3config.getBucket())
      .key(filekey)
      .build();
    
    return Mono.fromFuture(s3client.getObject(request, AsyncResponseTransformer.toPublisher()))
      .map(response -> {
        checkResult(response.response());
        String filename = getMetadataItem(response.response(),"filename",filekey);            
        return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_TYPE, response.response().contentType())
          .header(HttpHeaders.CONTENT_LENGTH, Long.toString(response.response().contentLength()))
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="" + filename + """)
          .body(Flux.from(response));
      });
}
```

在这里， getMetadataItem()只是一个辅助方法，它以不区分大小写的方式在响应中查找给定的元数据键。

这是一个重要的细节：S3 使用特殊的 HTTP 标头返回元数据信息，但这些标头不区分大小写(请参阅[RFC 7230，第 3.2 节](https://tools.ietf.org/html/rfc7230#section-3.2))。这意味着实现可能会随意更改给定项目的大小写——这实际上发生在使用[MinIO](https://min.io/)时。

## 9.总结

在本教程中，我们介绍了使用 AWS SDK V2 库中提供的反应式扩展的基础知识。我们这里的重点是 AWS S3 服务，但我们可以将相同的技术扩展到其他支持反应的服务，例如 DynamoDB。