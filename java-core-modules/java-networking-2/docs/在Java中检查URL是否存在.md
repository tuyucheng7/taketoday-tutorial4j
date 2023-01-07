## 1. 概述

在本教程中，我们将通过使用GET和HEAD [HTTP 方法的](https://www.baeldung.com/java-http-request)Java 示例来了解如何检查 URL 是否存在。

## 2. URL存在

在编程中，有时我们必须在访问资源之前知道给定 URL 中是否存在资源，或者我们甚至可能需要检查 URL 以了解资源的健康状况。

我们通过查看其响应代码来确定资源在 URL 中的存在。通常我们寻找200，这意味着“OK”并且请求已成功。

## 3. 使用 GET 请求

首先，要发出GET请求，我们可以创建java.net.URL的实例并将我们想要访问的 URL 作为构造函数参数传递。之后，我们只需打开连接并获取响应代码：

```java
URL url = new URL("http://www.example.com");
HttpURLConnection huc = (HttpURLConnection) url.openConnection();
 
int responseCode = huc.getResponseCode();
 
Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode);
```

当在 URL 找不到资源时，我们会收到404响应代码：

```java
URL url = new URL("http://www.example.com/xyz"); 
HttpURLConnection huc = (HttpURLConnection) url.openConnection();
 
int responseCode = huc.getResponseCode();
 
Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);
```

由于HttpURLConnection中的默认 HTTP 方法是GET，因此我们不会在本节的示例中设置请求方法。我们将在下一节中看到如何覆盖默认方法。

## 4. 使用 HEAD 请求 

HEAD 也是一种 HTTP 请求方法，与 GET 相同，只是它不返回响应主体。 

如果使用 GET 方法请求相同的资源，它会获取响应代码以及我们将收到的响应标头。

要创建 HEAD 请求，我们可以在获取响应代码之前简单地将 Request Method 设置为 HEAD：

```java
URL url = new URL("http://www.example.com");
HttpURLConnection huc = (HttpURLConnection) url.openConnection();
huc.setRequestMethod("HEAD");
 
int responseCode = huc.getResponseCode();
 
Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode);
```

同样，当在 URL 找不到资源时：

```java
URL url = new URL("http://www.example.com/xyz");
HttpURLConnection huc = (HttpURLConnection) url.openConnection();
huc.setRequestMethod("HEAD");
 
int responseCode = huc.getResponseCode();
 
Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);
```

通过使用 HEAD 方法而不是下载响应主体，我们减少了响应时间和带宽，并提高了性能。

尽管大多数现代服务器都支持 HEAD 方法，但一些本土或遗留服务器可能会拒绝 HEAD 方法并出现无效的方法类型错误。所以，我们应该谨慎使用 HEAD 方法。

## 5. 跟随重定向

最后，在寻找 URL 存在时，最好不要遵循重定向。但这也可能取决于我们查找 URL 的原因。

移动 URL 时，服务器可以将请求重定向到具有 3xx 响应代码的新 URL。默认是跟随重定向。我们可以根据需要选择跟随或忽略重定向。

为此，我们可以为所有HttpURLConnection覆盖followRedirects的默认值：

```java
URL url = new URL("http://www.example.com");
HttpURLConnection.setFollowRedirects(false);
HttpURLConnection huc = (HttpURLConnection) url.openConnection();
 
int responseCode = huc.getResponseCode();
 
Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode);
```

或者，我们可以使用setInstanceFollowRedirects()方法为单个连接禁用跟随重定向：

```java
URL url = new URL("http://www.example.com");
HttpURLConnection huc = (HttpURLConnection) url.openConnection();
huc.setInstanceFollowRedirects(false);
 
int responseCode = huc.getResponseCode();
 
Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode);
```

## 六，总结

在本文中，我们查看了检查响应代码以查找 URL 的可用性。此外，我们研究了使用 HEAD 方法来节省带宽并获得更快响应可能是个好主意。