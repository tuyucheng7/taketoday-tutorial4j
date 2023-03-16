## 1. 简介

在本快速教程中，我们将使用[WebClient](https://www.baeldung.com/spring-5-webclient)从服务器流式传输一个大文件。为了说明，我们将创建一个简单的[控制器](https://www.baeldung.com/spring-controllers)和两个客户端。**最终，我们将了解如何以及何时使用[Spring](https://www.baeldung.com/spring-tutorial)的[DataBuffer](https://www.baeldung.com/spring-reactive-read-flux-into-inputstream)和[DataBufferUtils](https://www.baeldung.com/spring-reactive-read-flux-into-inputstream#bodyextractors-databufferutils)**。

## 2. 使用简单服务器的场景

**我们将从一个用于[下载](https://www.baeldung.com/spring-controller-return-image-file)任意文件的简单控制器开始**。首先，我们将构造一个[FileSystemResource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/io/FileSystemResource.html)，传递一个文件[Path](https://www.baeldung.com/java-nio-2-path)，然后将其作为主体包装到我们的[ResponseEntity](https://www.baeldung.com/spring-response-entity)：

```java
@RestController
@RequestMapping("/large-file")
public class LargeFileController {

    @GetMapping
    ResponseEntity<Resource> get() {
        return ResponseEntity.ok()
              .body(new FileSystemResource(Paths.get("/tmp/large.dat")));
    }
}
```

其次，我们需要生成我们正在引用的文件。**由于内容对于理解本教程并不重要，因此我们将使用fallocate在磁盘上保留指定的大小而无需写入任何内容**。所以，让我们通过运行以下命令来[创建我们的大文件](https://www.baeldung.com/linux/create-large-file)：

```shell
fallocate -l 128M /tmp/large.dat
```

最后，我们有一个客户可以下载的文件。所以，我们准备开始写我们的客户端。

## 3. WebClient与大文件的ExchangeStrategies

我们将从一个简单但有限的[WebClient](https://www.baeldung.com/spring-5-webclient)开始下载我们的文件。**我们将使用[ExchangeStrategies](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/ExchangeStrategies.html)来提高可用于exchange()操作的内存限制**。这样，我们可以操作更多的字节，但我们仍然受限于JVM可用的最大内存。让我们使用bodyToMono()从服务器获取Mono<byte[]\>：

```java
public class LimitedFileDownloadWebClient {

    public static long fetch(WebClient client, String destination) {
        Mono<byte[]> mono = client.get()
              .retrieve()
              .bodyToMono(byte[].class);

        byte[] bytes = mono.block();

        Path path = Paths.get(destination);
        Files.write(path, bytes);
        return bytes.length;
    }

    // ...
}
```

换句话说，**我们将整个响应内容检索到一个byte[]中**。之后，我们将这些字节写入我们的路径并返回下载的字节数。让我们创建一个[main()](https://www.baeldung.com/java-main-method)方法来测试它：

```java
public static void main(String... args) {
    String baseUrl = args[0];
    String destination = args[1];

    WebClient client = WebClient.builder()
        .baseUrl(baseUrl)
        .exchangeStrategies(useMaxMemory())
        .build();

    long bytes = fetch(client, destination);
    System.out.printf("downloaded %d bytes", bytes);
}
```

此外，我们还需要两个[参数](https://www.baeldung.com/java-command-line-arguments)：下载URL和将其保存在本地的目的地。**为了避免客户端出现[DataBufferLimitException](https://www.baeldung.com/spring-webflux-databufferlimitexception)，让我们配置一个交换策略来限制可加载到内存中的字节数**。我们将使用[Runtime](https://www.baeldung.com/java-heap-memory-api#3-maximum-memory)获取为我们的应用程序配置的总内存，而不是定义固定大小。**请注意，不推荐这样做，仅用于演示目的**：

```java
private static ExchangeStrategies useMaxMemory() {
    long totalMemory = Runtime.getRuntime().maxMemory();

    return ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs()
        	.maxInMemorySize((int) totalMemory)
        )
        .build();
}
```

澄清一下，交换策略定制了我们的客户端处理请求的方式。在这种情况下，我们使用构建器中的codecs()方法，因此我们不替换任何默认设置。

### 3.1 通过内存调整运行我们的客户端

随后，我们将项目打包为/tmp/app.jar中的[jar](https://www.baeldung.com/java-create-jar)，并在[localhost:8081](https://www.baeldung.com/spring-boot-change-port)上运行我们的服务器。然后，让我们定义一些变量并从命令行运行我们的客户端：

```shell
limitedClient='com.baeldung.streamlargefile.client.LimitedFileDownloadWebClient' 
endpoint='http://localhost:8081/large-file' 
java -Xmx256m -cp /tmp/app.jar $limitedClient $endpoint /tmp/download.dat 
```

**请注意，我们允许我们的应用程序使用两倍于128M文件大小的内存**。事实上，我们将下载我们的文件并获得以下输出：

```shell
downloaded 134217728 bytes
```

另一方面，**如果我们没有分配足够的内存，我们会得到一个[OutOfMemoryError](https://www.baeldung.com/java-permgen-space-error)**：

```shell
$ java -Xmx64m -cp /tmp/app.jar $limitedClient $endpoint /tmp/download.dat
reactor.netty.ReactorNetty$InternalNettyException: java.lang.OutOfMemoryError: Direct buffer memory
```

这种方法不依赖于Spring Core实用程序。但是，它是有限的，因为**我们无法下载任何大小接近应用程序最大内存的文件**。

## 4. 使用DataBuffer的任何文件大小的WebClient

**一种更安全的方法是使用DataBuffer和[DataBufferUtils](https://www.baeldung.com/spring-reactive-read-flux-into-inputstream#bodyextractors-databufferutils)以块的形式流式传输我们的下载，这样整个文件就不会加载到内存中**。然后，这一次，我们将使用bodyToFlux()来检索Flux<DataBuffer\>，将其写入我们的path，并以字节为单位返回其大小：

```java
public class LargeFileDownloadWebClient {

    public static long fetch(WebClient client, String destination) {
        Flux<DataBuffer> flux = client.get()
              .retrieve()
              .bodyToFlux(DataBuffer.class);

        Path path = Paths.get(destination);
        DataBufferUtils.write(flux, path)
              .block();

        return Files.size(path);
    }

    // ...
}
```

**最后，让我们编写main方法来接收我们的参数，创建一个WebClient并获取我们的文件**：

```java
public static void main(String... args) {
    String baseUrl = args[0];
    String destination = args[1];

    WebClient client = WebClient.create(baseUrl);

    long bytes = fetch(client, destination);
    System.out.printf("downloaded %d bytes", bytes);
}
```

就是这样。**这种方法更通用，因为我们不依赖于文件或内存大小**。让我们将最大内存设置为文件大小的四分之一，并使用之前的相同端点运行它：

```bash
client='cn.tuyucheng.taketoday.streamlargefile.client.LargeFileDownloadWebClient'
java -Xmx32m -cp /tmp/app.jar $client $endpoint /tmp/download.dat
```

最后，我们将获得成功的输出，即使我们的应用程序的总内存小于文件的大小：

```shell
downloaded 134217728 bytes
```

## 5. 总结

在本文中，我们了解了使用WebClient下载任意大文件的不同方法。首先，我们了解了如何定义可用于WebClient操作的内存量。然后，我们看到了这种方法的缺点。最重要的是，我们学会了如何让我们的客户端有效地使用内存。