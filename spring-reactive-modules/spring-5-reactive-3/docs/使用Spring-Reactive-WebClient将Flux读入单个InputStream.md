## 1. 概述

在本教程中，我们将深入研究[Java响应式编程](https://www.baeldung.com/spring-reactive-guide)，以解决一个有趣的问题，即**如何将[Flux](https://www.baeldung.com/reactor-core)<DataBuffer\>读取到单个InputStream**中。

## 2. 请求设置

作为解决将Flux<DataBuffer\>读入单个InputStream的问题的第一步，我们将使用[Spring响应式WebClient](https://www.baeldung.com/spring-5-webclient)来发出GET请求。此外，我们可以将[gorest.co.in](https://gorest.co.in/)**托管的公共API端点**之一用于此类测试场景：

```java
String REQUEST_ENDPOINT = "https://gorest.co.in/public/v2/users";
```

接下来，让我们定义用于获取WebClient实例的getWebClient()方法：

```java
public class DataBufferToInputStream {

    private static WebClient getWebClient() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        return webClientBuilder.build();
    }
}
```

此时，我们已准备好向/public/v2/users端点发出GET请求。但是，我们必须将响应主体作为Flux<DataBuffer\>对象获取。因此，让我们继续下一节关于BodyExtractor的内容来精确地执行此操作。

## 3. BodyExtractor和DataBufferUtils

我们可以**使用[Spring-Webflux](https://www.baeldung.com/spring-webflux)中提供的BodyExtractors类的toDataBuffers()方法将响应体提取到Flux<DataBuffer\>中**。

让我们继续创建body作为Flux<DataBuffer\>类型的实例：

```java
Flux<DataBuffer> body = client.get()
    .uri(url)
    .exchangeToFlux(clientResponse -> {
        return clientResponse.body(BodyExtractors.toDataBuffers());
    });
```

接下来，由于我们需要将这些DataBuffer流收集到单个InputStream中，实现此目的的一个好策略是使用[PipedInputStream](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/io/class-use/PipedInputStream.html)和[PipedOutputStream](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/io/class-use/PipedOutputStream.html)。

此外，我们打算写入PipedOutputStream并最终从PipedInputStream读取。因此，让我们看看如何创建这两个连接的流：

```java
PipedOutputStream outputStream = new PipedOutputStream();
PipedInputStream inputStream = new PipedInputStream(1024 * 10);
inputStream.connect(outputStream);
```

我们必须注意，默认大小是1024字节。但是，我们预计从Flux<DataBuffer\>收集的结果可能会超过默认值。因此，我们需要明确指定一个更大的值，在本例中为1024 * 10。

最后，我们使用DataBufferUtils类中提供的write()工具方法将body作为发布者写入outputStream：

```java
DataBufferUtils.write(body, outputStream).subscribe();
```

我们必须注意，我们**在声明时将inputStream连接到outputStream**。因此，我们最好从inputStream中读取。让我们继续下一节，看看它的实际效果。

## 4. 从PipedInputStream读取

首先，让我们定义一个工具方法readContent()，以将InputStream读取为String对象：

```java
private static String readContent(InputStream stream) throws IOException {
    StringBuilder contentStringBuffer = new StringBuilder();
    byte[] tmp = new byte[stream.available()];
    int byteCount = stream.read(tmp, 0, tmp.length);
    logger.info(String.format("read %d bytes from the streamn", byteCount));
    contentStringBuffer.append(new String(tmp));
    return String.valueOf(contentStringBuffer);
}
```

接下来，因为[在不同的线程中读取PipedInputStream](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/PipedInputStream.html)是一种典型的做法，所以让我们创建readContentFromPipedInputStream()方法，该方法在内部生成一个新线程，通过调用readContent()方法将PipedInputStream中的内容读取到String对象中：

```java
private static String readContentFromPipedInputStream(PipedInputStream stream) throws IOException {
    StringBuffer contentStringBuffer = new StringBuffer();
    try (stream) {
        Thread pipeReader = new Thread(() -> {
            try {
                contentStringBuffer.append(readContent(stream));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        pipeReader.start();
        pipeReader.join();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    return String.valueOf(contentStringBuffer);
}
```

在此阶段，我们的代码已准备好用于模拟。让我们看看它的实际效果：

```java
public static void main(String[] args) throws IOException, InterruptedException {
	WebClient webClient = getWebClient();
	InputStream inputStream = getResponseAsInputStream(webClient, REQUEST_ENDPOINT);
	Thread.sleep(3000);
	String content = readContentFromPipedInputStream((PipedInputStream) inputStream);
	logger.info("response content: \n{}", content.replace("}","}\n"));
}
```

当我们处理异步系统时，我们在从流中读取之前将读取延迟任意3秒，以便我们能够看到完整的响应。此外，在日志记录时，我们插入换行符以将长输出分成多行。

最后，让我们验证代码执行生成的输出：

```shell
16:44:04.120 [main] INFO cn.tuyucheng.taketoday.databuffer.DataBufferToInputStream - response content: 
[{"id":2642,"name":"Bhupen Trivedi","email":"bhupen_trivedi@renner-pagac.name","gender":"male","status":"active"}
,{"id":2637,"name":"Preity Patel","email":"patel_preity@abshire.info","gender":"female","status":"inactive"}
,{"id":2633,"name":"Brijesh Shah","email":"brijesh_shah@morar.co","gender":"male","status":"inactive"}
...
,{"id":2623,"name":"Mohini Mishra","email":"mishra_mohini@hamill-ledner.info","gender":"female","status":"inactive"}
]
```

## 5. 总结

在本文中，我们使用了管道流的概念以及**BodyExtractors和DataBufferUtils类中可用的工具方法**来将Flux<DataBuffer\>读入单个InputStream中。