## 1. 概述

本教程将给出一个如何使用[OkHttp 库](https://www.baeldung.com/guide-to-okhttp)下载二进制文件的实际示例。

## 2.Maven依赖

我们将从添加基础库[okhttp](https://search.maven.org/artifact/com.squareup.okhttp3/okhttp)依赖项开始：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.9.1</version>
</dependency>
```

然后，如果我们想为使用 OkHttp 库实现的模块编写集成测试，我们可以使用[mockwebserver](https://search.maven.org/artifact/com.squareup.okhttp3/mockwebserver)库。该库具有模拟服务器及其响应的工具：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>mockwebserver</artifactId>
    <version>4.9.1</version>
    <scope>test</scope>
</dependency>
```

## 3.请求二进制文件

我们将首先实现一个类，该类接收作为参数的 URL，从中下载文件并创建和执行该 URL 的 HTTP 请求。

为了使类可测试，我们将在构造函数中注入OkHttpClient和编写器：

```java
public class BinaryFileDownloader implements AutoCloseable {

    private final OkHttpClient client;
    private final BinaryFileWriter writer;

    public BinaryFileDownloader(OkHttpClient client, BinaryFileWriter writer) {
        this.client = client;
        this.writer = writer;
    }
}
```

接下来，我们将实现从 URL 下载文件的方法：

```java
public long download(String url) throws IOException {
    Request request = new Request.Builder().url(url).build();
    Response response = client.newCall(request).execute();
    ResponseBody responseBody = response.body();
    if (responseBody == null) {
        throw new IllegalStateException("Response doesn't contain a file");
    }
    double length = Double.parseDouble(Objects.requireNonNull(response.header(CONTENT_LENGTH, "1")));
    return writer.write(responseBody.byteStream(), length);
}
```

下载文件的过程有四个步骤。使用 URL 创建请求。执行请求并接收响应。获取响应的主体，如果为空则失败。将响应主体的字节写入文件。

## 4. 将响应写入本地文件

为了将接收到的来自响应的字节写入本地文件，我们将实现一个BinaryFileWriter类，它将InputStream和OutputStream作为输入并将内容从[InputStream](https://www.baeldung.com/convert-input-stream-to-a-file)到OutputStream。

OutputStream将被注入到构造函数中，以便该类可以测试：

```java
public class BinaryFileWriter implements AutoCloseable {

    private final OutputStream outputStream;

    public BinaryFileWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
```

我们现在将实现将内容从InputStream到OutputStream的方法。该方法首先用BufferedInputStream包装InputStream ，以便我们可以一次读取更多字节。然后我们准备一个数据缓冲区，我们在其中临时存储来自InputStream的字节。

最后，我们将缓冲数据写入OutputStream。只要InputStream有数据要读取，我们就这样做：

```java
public long write(InputStream inputStream) throws IOException {
    try (BufferedInputStream input = new BufferedInputStream(inputStream)) {
        byte[] dataBuffer = new byte[CHUNK_SIZE];
        int readBytes;
        long totalBytes = 0;
        while ((readBytes = input.read(dataBuffer)) != -1) {
            totalBytes += readBytes;
            outputStream.write(dataBuffer, 0, readBytes);
        }
        return totalBytes;
    }
}
```

## 5.获取文件下载进度

在某些情况下，我们可能想告诉用户文件下载的进度。

我们首先需要创建一个[功能接口](https://www.baeldung.com/java-8-functional-interfaces)：

```java
public interface ProgressCallback {
    void onProgress(double progress);
}
```

然后，我们将在BinaryFileWriter类中使用它。这将为我们提供下载程序到目前为止写入的每一步的总字节数。

首先，我们将ProgressCallback作为一个字段添加到 writer 类。然后，我们将更新write方法以接收response的长度作为参数。这将帮助我们计算进度。

然后，我们将调用onProgress方法，其中包含从 目前写入 的totalBytes计算的进度和长度：

```java
public class BinaryFileWriter implements AutoCloseable {
    private final ProgressCallback progressCallback;
    public long write(InputStream inputStream, double length) {
        //...
        progressCallback.onProgress(totalBytes / length  100.0);
    }
}
```

最后，我们将更新BinaryFileDownloader类以使用总响应长度调用write方法。我们将从Content-Length标头中获取响应长度，然后将其传递给write方法：

```java
public class BinaryFileDownloader {
    public long download(String url) {
        double length = getResponseLength(response);
        return write(responseBody, length);
    }
    private double getResponseLength(Response response) {
        return Double.parseDouble(Objects.requireNonNull(response.header(CONTENT_LENGTH, "1")));
    }
}
```

## 六. 总结

在本文中，我们实现了一个简单而实用的示例，即使用 OkHttp 库从 URL 下载二进制文件。