## 1. 概述

在本教程中，我们将看到几种可用于下载文件的方法。

我们将介绍从JavaIO 的基本用法到 NIO 包以及一些常用库(如 AsyncHttpClient 和 Apache Commons IO)的示例。

最后，我们将讨论如果在读取整个文件之前连接失败，我们如何恢复下载。

## 2. 使用JavaIO

我们可以用来下载文件的最基本的 API 是[Java IO](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/package-summary.html)。我们可以使用URL 类打开到我们要下载的文件的连接。

为了有效地读取文件，我们将使用 openStream()方法来获取InputStream：

```java
BufferedInputStream in = new BufferedInputStream(new URL(FILE_URL).openStream())
```

从InputStream读取时，建议将其包装在 BufferedInputStream中 以提高性能。

性能提升来自缓冲。当使用read()方法一次读取一个字节时，每个方法调用都意味着对底层文件系统的系统调用。当 JVM 调用read() 系统调用时，程序执行上下文从用户模式切换到内核模式并返回。

从性能的角度来看，这种上下文切换是昂贵的。当我们读取大量字节时，由于涉及大量上下文切换，应用程序性能会很差。

为了将从 URL 读取的字节写入我们的本地文件，我们将使用FileOutputStream 类的write()方法 ：

```java
try (BufferedInputStream in = new BufferedInputStream(new URL(FILE_URL).openStream());
  FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME)) {
    byte dataBuffer[] = new byte[1024];
    int bytesRead;
    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
    }
} catch (IOException e) {
    // handle exception
}
```

使用BufferedInputStream时，read()方法将读取我们为缓冲区大小设置的字节数。在我们的示例中，我们已经通过一次读取 1024 字节的块来执行此操作，因此 BufferedInputStream 不是必需的。

上面的示例非常冗长，但幸运的是，从Java7 开始，我们有了包含用于处理 IO 操作的辅助方法的Files类。

我们可以使用Files.copy()方法从InputStream读取所有字节并将它们到本地文件：

```java
InputStream in = new URL(FILE_URL).openStream();
Files.copy(in, Paths.get(FILE_NAME), StandardCopyOption.REPLACE_EXISTING);
```

我们的代码运行良好，但可以改进。它的主要缺点是字节被缓冲到内存中。

幸运的是，Java 为我们提供了 NIO 包，它具有在两个通道之间直接传输字节而无需缓冲的方法。

我们将在下一节中详细介绍。

## 3.使用蔚来

Java [NIO包提供了在两个](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/package-summary.html)通道之间传输字节而不将它们缓冲到应用程序内存中的可能性。

要从 我们的 URL 读取文件，我们将从 URL 流创建一个新 的ReadableByteChannel：

```java
ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
```

从ReadableByteChannel读取的字节 将被传输到与将要下载的文件对应的FileChannel ：

```java
FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
FileChannel fileChannel = fileOutputStream.getChannel();
```

我们将使用 ReadableByteChannel类中的transferFrom()方法 将字节从给定的 URL 下载到我们的FileChannel：

```java
fileOutputStream.getChannel()
  .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
```

transferTo ()和transferFrom()方法比简单地使用缓冲区从流中读取更有效。根据底层操作系统，数据可以直接从文件系统缓存传输到我们的文件，而无需将任何字节到应用程序内存中。

在 Linux 和 UNIX 系统上，这些方法使用零拷贝技术来减少内核模式和用户模式之间的上下文切换次数。

## 4. 使用库

我们在上面的示例中看到了如何仅使用Java核心功能从 URL 下载内容。

当不需要调整性能时，我们还可以利用现有库的功能来简化我们的工作。

例如，在真实场景中，我们需要异步下载代码。

我们可以将所有逻辑包装到一个Callable中，或者我们可以为此使用现有的库。

### 4.1. AsyncHttpClient

[AsyncHttpClient](https://www.baeldung.com/async-http-client)是一个流行的库，用于使用 Netty 框架执行异步 HTTP 请求。我们可以用它来对文件URL执行GET请求，获取文件内容。

首先，我们需要创建一个 HTTP 客户端：

```java
AsyncHttpClient client = Dsl.asyncHttpClient();
```

下载的内容将被放入FileOutputStream中：

```java
FileOutputStream stream = new FileOutputStream(FILE_NAME);
```

接下来，我们创建一个 HTTP GET 请求并注册一个 AsyncCompletionHandler处理程序来处理下载的内容：

```java
client.prepareGet(FILE_URL).execute(new AsyncCompletionHandler<FileOutputStream>() {

    @Override
    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) 
      throws Exception {
        stream.getChannel().write(bodyPart.getBodyByteBuffer());
        return State.CONTINUE;
    }

    @Override
    public FileOutputStream onCompleted(Response response) 
      throws Exception {
        return stream;
    }
})
```

请注意，我们已经覆盖了onBodyPartReceived()方法。 默认实现将接收到的 HTTP 块累积到ArrayList中。这可能会导致高内存消耗，或者在尝试下载大文件时出现OutOfMemory异常。

我们没有将每个 HttpResponseBodyPart累积到内存中，而是使用FileChannel将字节直接写入本地文件。我们将使用 getBodyByteBuffer()方法通过ByteBuffer访问正文部分内容 。

ByteBuffer的优点是内存是在 JVM 堆之外分配的，因此它不会影响我们的应用程序内存。

### 4.2. 阿帕奇公共 IO

另一个被广泛使用的 IO 操作库是[Apache Commons IO](https://commons.apache.org/proper/commons-io/)。我们可以从 Javadoc 中看到，有一个名为[FileUtils](https://commons.apache.org/proper/commons-io/apidocs/index.html?org/apache/commons/io/FileUtils.html)的实用程序类，我们将其用于一般的文件操作任务。

要从 URL 下载文件，我们可以使用这个单行代码：

```java
FileUtils.copyURLToFile(
  new URL(FILE_URL), 
  new File(FILE_NAME), 
  CONNECT_TIMEOUT, 
  READ_TIMEOUT);
```

从性能的角度来看，这段代码与第 2 节中的代码相同。

底层代码使用在循环中从 InputStream 读取一些字节并将它们写入OutputStream的相同概念。

一个区别是这里使用URLConnection类来控制连接超时，以便下载不会阻塞很长时间：

```java
URLConnection connection = source.openConnection();
connection.setConnectTimeout(connectionTimeout);
connection.setReadTimeout(readTimeout);
```

## 5.断点续传

考虑到 Internet 连接有时会失败，能够恢复下载而不是从字节 0 再次下载文件是很有用的。

让我们重写前面的第一个示例以添加此功能。

首先要知道的是，我们可以从给定的 URL 读取文件的大小，而无需使用 HTTP HEAD 方法实际下载它：

```java
URL url = new URL(FILE_URL);
HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
httpConnection.setRequestMethod("HEAD");
long removeFileSize = httpConnection.getContentLengthLong();
```

现在我们有了文件的总内容大小，我们可以检查我们的文件是否已部分下载。

如果是这样，我们将从磁盘上记录的最后一个字节开始恢复下载：

```java
long existingFileSize = outputFile.length();
if (existingFileSize < fileLength) {
    httpFileConnection.setRequestProperty(
      "Range", 
      "bytes=" + existingFileSize + "-" + fileLength
    );
}
```

在这里，我们配置了URLConnection以请求特定范围内的文件字节。该范围将从最后下载的字节开始，到与远程文件大小对应的字节结束。

使用Range标头的另一种常见方法是通过设置不同的字节范围来分块下载文件。例如，要下载 2 KB 的文件，我们可以使用范围 0 – 1024 和 1024 – 2048。

与第 2 节中的代码的另一个细微差别是，FileOutputStream是在append参数设置为 true 的情况下打开的：

```java
OutputStream os = new FileOutputStream(FILE_NAME, true);
```

进行此更改后，其余代码与第 2 节中的代码相同。

## 六，总结

我们在本文中看到了几种在Java中从 URL 下载文件的方法。

最常见的实现是在执行读/写操作时缓冲字节。即使对于大文件，此实现也可以安全使用，因为我们不会将整个文件加载到内存中。

我们还了解了如何使用JavaNIO Channels实现零拷贝下载。这很有用，因为它最大限度地减少了读取和写入字节时进行的上下文切换次数，并且通过使用直接缓冲区，字节不会加载到应用程序内存中。

此外，因为下载文件通常是通过 HTTP 完成的，我们已经展示了如何使用 AsyncHttpClient 库来实现这一点。