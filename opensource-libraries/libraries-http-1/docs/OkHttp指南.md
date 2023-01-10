## 1. 简介

在本教程中，我们将探讨发送不同类型的 HTTP 请求以及接收和解释 HTTP 响应的基础知识。然后我们将学习如何[使用 OkHttp](https://square.github.io/okhttp/)配置客户端。

最后，我们将讨论使用自定义标头、超时、响应缓存等配置客户端的更高级用例。

## 2.OkHttp 概述

OkHttp 是适用于 Android 和Java应用程序的高效 HTTP 和 HTTP/2 客户端。

它具有高级功能，例如连接池(如果 HTTP/2 不可用)、透明 GZIP 压缩和响应缓存，以完全避免网络重复请求。

它还能够从常见的连接问题中恢复；在连接失败时，如果服务有多个 IP 地址，它可以重试对备用地址的请求。

在高层次上，客户端是为阻塞同步调用和非阻塞异步调用而设计的。

OkHttp 支持 Android 2.3 及以上版本。对于 Java，最低要求是 1.7。

现在我们已经给出了一个简短的概述，让我们看一些使用示例。

## 3.Maven依赖

首先，我们将库作为依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.9.1</version>
</dependency>
```

要查看此库的最新依赖项，请查看[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.okhttp3" AND a%3A"okhttp")上的页面。

## 4.与OkHttp同步GET

要发送同步 GET 请求，我们需要基于URL构建一个Request对象并进行Call。执行后，我们将返回一个Response实例：

```java
@Test
public void whenGetRequest_thenCorrect() throws IOException {
    Request request = new Request.Builder()
      .url(BASE_URL + "/date")
      .build();

    Call call = client.newCall(request);
    Response response = call.execute();

    assertThat(response.code(), equalTo(200));
}
```

## 5. OkHttp异步GET

要进行异步 GET，我们需要将Call加入队列。回调允许我们在可读时读取响应。这发生在响应标头准备好之后。

读取响应主体可能仍然会阻塞。OkHttp 当前不提供任何异步 API 来接收部分响应主体：

```java
@Test
public void whenAsynchronousGetRequest_thenCorrect() {
    Request request = new Request.Builder()
      .url(BASE_URL + "/date")
      .build();

    Call call = client.newCall(request);
    call.enqueue(new Callback() {
        public void onResponse(Call call, Response response) 
          throws IOException {
            // ...
        }
        
        public void onFailure(Call call, IOException e) {
            fail();
        }
    });
}
```

## 6. GET 查询参数

最后，要向我们的 GET 请求添加查询参数，我们可以利用HttpUrl.Builder。

在我们构建 URL 之后，我们可以将它传递给我们的Request对象：

```java
@Test
public void whenGetRequestWithQueryParameter_thenCorrect() 
  throws IOException {
    
    HttpUrl.Builder urlBuilder 
      = HttpUrl.parse(BASE_URL + "/ex/bars").newBuilder();
    urlBuilder.addQueryParameter("id", "1");

    String url = urlBuilder.build().toString();

    Request request = new Request.Builder()
      .url(url)
      .build();
    Call call = client.newCall(request);
    Response response = call.execute();

    assertThat(response.code(), equalTo(200));
}
```

## 7.POST请求 

现在让我们看一个简单的 POST 请求，我们在其中构建一个RequestBody 来发送参数 “username” 和 “password”：

```java
@Test
public void whenSendPostRequest_thenCorrect() 
  throws IOException {
    RequestBody formBody = new FormBody.Builder()
      .add("username", "test")
      .add("password", "test")
      .build();

    Request request = new Request.Builder()
      .url(BASE_URL + "/users")
      .post(formBody)
      .build();

    Call call = client.newCall(request);
    Response response = call.execute();
    
    assertThat(response.code(), equalTo(200));
}
```

我们的文章[A Quick Guide to Post Requests with OkHttp](https://www.baeldung.com/okhttp-post)有更多使用 OkHttp 的 POST 请求示例。

## 8.文件上传

### 8.1. 上传一个文件

在此示例中，我们将演示如何上传File。我们将使用MultipartBody.Builder上传“ test.ext”文件：

```java
@Test
public void whenUploadFile_thenCorrect() throws IOException {
    RequestBody requestBody = new MultipartBody.Builder()
      .setType(MultipartBody.FORM)
      .addFormDataPart("file", "file.txt",
        RequestBody.create(MediaType.parse("application/octet-stream"), 
          new File("src/test/resources/test.txt")))
      .build();

    Request request = new Request.Builder()
      .url(BASE_URL + "/users/upload")
      .post(requestBody)
      .build();

    Call call = client.newCall(request);
    Response response = call.execute();

    assertThat(response.code(), equalTo(200));
}
```

### 8.2. 获取文件上传进度

然后我们将学习如何获取文件上传的进度。我们将扩展RequestBody以获得对上传过程的可见性。

下面是上传方法：

```java
@Test
public void whenGetUploadFileProgress_thenCorrect() 
  throws IOException {
    RequestBody requestBody = new MultipartBody.Builder()
      .setType(MultipartBody.FORM)
      .addFormDataPart("file", "file.txt",
        RequestBody.create(MediaType.parse("application/octet-stream"), 
          new File("src/test/resources/test.txt")))
      .build();
      
    ProgressRequestWrapper.ProgressListener listener 
      = (bytesWritten, contentLength) -> {
        float percentage = 100f  bytesWritten / contentLength;
        assertFalse(Float.compare(percentage, 100) > 0);
    };

    ProgressRequestWrapper countingBody
      = new ProgressRequestWrapper(requestBody, listener);

    Request request = new Request.Builder()
      .url(BASE_URL + "/users/upload")
      .post(countingBody)
      .build();

    Call call = client.newCall(request);
    Response response = call.execute();

    assertThat(response.code(), equalTo(200));
}

```

现在这里是ProgressListener 接口，它使我们能够观察上传进度：

```java
public interface ProgressListener {
    void onRequestProgress(long bytesWritten, long contentLength);
}
```

接下来是ProgressRequestWrapper，它是RequestBody的扩展版本：

```java
public class ProgressRequestWrapper extends RequestBody {

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink;

        countingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(countingSink);

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }
}
```

最后，这是CountingSink，它是ForwardingSink的扩展版本：

```java
protected class CountingSink extends ForwardingSink {

    private long bytesWritten = 0;

    public CountingSink(Sink delegate) {
        super(delegate);
    }

    @Override
    public void write(Buffer source, long byteCount)
      throws IOException {
        super.write(source, byteCount);
        
        bytesWritten += byteCount;
        listener.onRequestProgress(bytesWritten, contentLength());
    }
}
```

注意：

-   当将ForwardingSink扩展为“CountingSink”时，我们重写了write()方法来计算写入(传输)的字节数
-   当将RequestBody扩展到“ ProgressRequestWrapper ”时，我们覆盖了writeTo()方法以使用我们的“ForwardingSink”

## 9. 设置自定义标题

### 9.1. 在请求上设置标头

要在请求上设置任何自定义标头，我们可以使用简单的addHeader调用：

```java
@Test
public void whenSetHeader_thenCorrect() throws IOException {
    Request request = new Request.Builder()
      .url(SAMPLE_URL)
      .addHeader("Content-Type", "application/json")
      .build();

    Call call = client.newCall(request);
    Response response = call.execute();
    response.close();
}
```

### 9.2. 设置默认标题

在此示例中，我们将了解如何在客户端本身上配置默认标头，而不是在每个请求上都设置它。

例如，如果我们想为每个请求设置一个内容类型“application/json”，我们需要为我们的客户端设置一个拦截器：

```java
@Test
public void whenSetDefaultHeader_thenCorrect() 
  throws IOException {
    
    OkHttpClient client = new OkHttpClient.Builder()
      .addInterceptor(
        new DefaultContentTypeInterceptor("application/json"))
      .build();

    Request request = new Request.Builder()
      .url(SAMPLE_URL)
      .build();

    Call call = client.newCall(request);
    Response response = call.execute();
    response.close();
}
```

这是DefaultContentTypeInterceptor，它是Interceptor的扩展版本：

```java
public class DefaultContentTypeInterceptor implements Interceptor {
    
    public Response intercept(Interceptor.Chain chain) 
      throws IOException {

        Request originalRequest = chain.request();
        Request requestWithUserAgent = originalRequest
          .newBuilder()
          .header("Content-Type", contentType)
          .build();

        return chain.proceed(requestWithUserAgent);
    }
}
```

请注意，拦截器将标头添加到原始请求中。

## 10.不要跟随重定向

在此示例中，我们将了解如何配置OkHttpClient以停止跟随重定向。

默认情况下，如果使用HTTP 301 Moved Permanently 回答 GET 请求，则自动遵循重定向。在某些用例中，这完全没问题，但在其他用例中并不需要。

为了实现这种行为，当我们构建客户端时，我们需要将followRedirects设置为false。

请注意，响应将返回HTTP 301状态代码：

```java
@Test
public void whenSetFollowRedirects_thenNotRedirected() 
  throws IOException {

    OkHttpClient client = new OkHttpClient().newBuilder()
      .followRedirects(false)
      .build();
    
    Request request = new Request.Builder()
      .url("http://t.co/I5YYd9tddw")
      .build();

    Call call = client.newCall(request);
    Response response = call.execute();

    assertThat(response.code(), equalTo(301));
}

```

如果我们使用true参数打开重定向(或完全删除它)，客户端将遵循重定向并且测试将失败，因为返回码将是 HTTP 200。

## 11.超时

当无法访问其对等方时，我们可以使用超时来使调用失败。网络故障可能是由于客户端连接问题、服务器可用性问题或介于两者之间的任何问题。OkHttp 支持连接、读取和写入超时。

在这个例子中，我们用 1 秒的readTimeout构建了我们的客户端，而 URL 有 2 秒的延迟：

```java
@Test
public void whenSetRequestTimeout_thenFail() 
  throws IOException {
    OkHttpClient client = new OkHttpClient.Builder()
      .readTimeout(1, TimeUnit.SECONDS)
      .build();

    Request request = new Request.Builder()
      .url(BASE_URL + "/delay/2")
      .build();
 
    Call call = client.newCall(request);
    Response response = call.execute();

    assertThat(response.code(), equalTo(200));
}
```

请注意，测试将失败，因为客户端超时时间低于资源响应时间。

## 12.取消通话

我们可以使用Call.cancel()立即停止正在进行的通话。如果线程当前正在写入请求或读取响应，则会抛出IOException。

当不再需要调用时，我们使用此方法来节省网络，例如当我们的用户离开应用程序时：

```java
@Test(expected = IOException.class)
public void whenCancelRequest_thenCorrect() 
  throws IOException {
    ScheduledExecutorService executor
      = Executors.newScheduledThreadPool(1);

    Request request = new Request.Builder()
      .url(BASE_URL + "/delay/2")  
      .build();

    int seconds = 1;
    long startNanos = System.nanoTime();

    Call call = client.newCall(request);

    executor.schedule(() -> {
        logger.debug("Canceling call: "  
            + (System.nanoTime() - startNanos) / 1e9f);

        call.cancel();
            
        logger.debug("Canceled call: " 
            + (System.nanoTime() - startNanos) / 1e9f);
        
    }, seconds, TimeUnit.SECONDS);

    logger.debug("Executing call: " 
      + (System.nanoTime() - startNanos) / 1e9f);

    Response response = call.execute();
	
    logger.debug(Call was expected to fail, but completed: " 
      + (System.nanoTime() - startNanos) / 1e9f, response);
}
```

## 13.响应缓存

要创建Cache，我们需要一个可以读写的缓存目录，以及对缓存大小的限制。

客户端将使用它来缓存响应：

```java
@Test
public void  whenSetResponseCache_thenCorrect() 
  throws IOException {
    int cacheSize = 10  1024  1024;

    File cacheDirectory = new File("src/test/resources/cache");
    Cache cache = new Cache(cacheDirectory, cacheSize);

    OkHttpClient client = new OkHttpClient.Builder()
      .cache(cache)
      .build();

    Request request = new Request.Builder()
      .url("http://publicobject.com/helloworld.txt")
      .build();

    Response response1 = client.newCall(request).execute();
    logResponse(response1);

    Response response2 = client.newCall(request).execute();
    logResponse(response2);
}
```

启动测试后，不会缓存第一次调用的响应。调用方法cacheResponse将返回null，而调用方法networkResponse将返回来自网络的响应。

缓存文件夹也将充满缓存文件。

第二次调用执行会产生相反的效果，因为响应已经被缓存了。这意味着对networkResponse的调用将返回null，而对cacheResponse的调用将从缓存返回响应。

为了防止响应使用缓存，我们可以使用CacheControl.FORCE_NETWORK。为了防止它使用网络，我们可以使用CacheControl.FORCE_CACHE。

需要注意的是，如果我们使用FORCE_CACHE，并且响应需要网络，OkHttp将返回一个 504 Unsatisfiable Request 响应。

## 14.总结

在本文中，我们探讨了如何将 OkHttp 用作 HTTP 和 HTTP/2 客户端的几个示例。