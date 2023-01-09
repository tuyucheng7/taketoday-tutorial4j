## 1. 概述

在本文中，我们将展示如何使用HttpClient扩展 URL 。

一个简单的例子是原始 URL 被缩短一次——通过bit.ly等服务。

一个更复杂的例子是当URL 被不同的此类服务多次缩短时，需要多次传递才能到达原始的完整 URL。

如果你想更深入地研究并学习其他可以使用 HttpClient 做的很酷的事情——请继续[阅读主要的 HttpClient 教程](https://www.baeldung.com/httpclient-guide)。

## 2. 展开一次 URL

让我们从简单的开始，通过扩展仅通过缩短 URL 服务传递一次的 URL。

我们首先需要的是一个不会自动遵循重定向的 HTTP 客户端：

```java
CloseableHttpClient client = 
  HttpClientBuilder.create().disableRedirectHandling().build();
```

这是必要的，因为我们需要手动拦截重定向响应并从中提取信息。

我们首先向缩短的 URL 发送请求——我们得到的响应将是301 Moved Permanently。

然后，我们需要提取指向下一个的Location标头，在本例中为最终 URL：

```java
public String expandSingleLevel(String url) throws IOException {
    HttpHead request = null;
    try {
        request = new HttpHead(url);
        HttpResponse httpResponse = client.execute(request);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 301 && statusCode != 302) {
            return url;
        }
        Header[] headers = httpResponse.getHeaders(HttpHeaders.LOCATION);
        Preconditions.checkState(headers.length == 1);
        String newUrl = headers[0].getValue();
        return newUrl;
    } catch (IllegalArgumentException uriEx) {
        return url;
    } finally {
        if (request != null) {
            request.releaseConnection();
        }
    }
}
```

最后，使用“未缩短”的 URL 进行简单的实时测试：

```java
@Test
public final void givenShortenedOnce_whenUrlIsExpanded_thenCorrectResult() throws IOException {
    final String expectedResult = "https://www.baeldung.com/rest-versioning";
    final String actualResult = expandSingleLevel("http://bit.ly/3LScTri");
    assertThat(actualResult, equalTo(expectedResult));
}
```

## 3. 处理多个 URL 级别

短 URL 的问题在于它们可能会被完全不同的服务缩短多次。扩展这样的 URL 将需要多次传递才能到达原始 URL。

我们将应用之前定义的expandSingleLevel原语操作来简单地遍历所有中间 URL 并到达最终目标：

```java
public String expand(String urlArg) throws IOException {
    String originalUrl = urlArg;
    String newUrl = expandSingleLevel(originalUrl);
    while (!originalUrl.equals(newUrl)) {
        originalUrl = newUrl;
        newUrl = expandSingleLevel(originalUrl);
    }
    return newUrl;
}
```

现在，有了扩展多级 URL 的新机制，让我们定义一个测试并将其付诸实践：

```java
@Test
public final void givenShortenedMultiple_whenUrlIsExpanded_thenCorrectResult() throws IOException {
    final String expectedResult = "https://www.baeldung.com/rest-versioning";
    final String actualResult = expand("http://t.co/e4rDDbnzmk");
    assertThat(actualResult, equalTo(expectedResult));
}
```

这一次，短 URL – http://t.co/e4rDDbnzmk – 实际上被缩短了两次 – 一次通过bit.ly，第二次通过t.co服务 – 被正确地扩展为原始 URL。

## 4. 检测重定向循环

最后，有些 URL 无法扩展，因为它们形成了重定向循环。此类问题会被HttpClient检测到，但由于我们关闭了重定向的自动跟踪，它不再检测到。

URL 扩展机制的最后一步是检测重定向循环并在发生此类循环时快速失败。

为了使其有效，我们需要一些来自我们之前定义的expandSingleLevel方法的额外信息——主要是，我们还需要返回响应的状态代码和 URL。

由于 java 不支持多个返回值，我们将把信息包装在一个org.apache.commons.lang3.tuple.Pair对象中——该方法的新签名现在将是：

```java
public Pair<Integer, String> expandSingleLevelSafe(String url) throws IOException {
```

最后，让我们在主要扩展机制中包含重定向循环检测：

```java
public String expandSafe(String urlArg) throws IOException {
    String originalUrl = urlArg;
    String newUrl = expandSingleLevelSafe(originalUrl).getRight();
    List<String> alreadyVisited = Lists.newArrayList(originalUrl, newUrl);
    while (!originalUrl.equals(newUrl)) {
        originalUrl = newUrl;
        Pair<Integer, String> statusAndUrl = expandSingleLevelSafe(originalUrl);
        newUrl = statusAndUrl.getRight();
        boolean isRedirect = statusAndUrl.getLeft() == 301 || statusAndUrl.getLeft() == 302;
        if (isRedirect && alreadyVisited.contains(newUrl)) {
            throw new IllegalStateException("Likely a redirect loop");
        }
        alreadyVisited.add(newUrl);
    }
    return newUrl;
}
```

就是这样 – expandSafe机制能够通过任意数量的 URL 缩短服务扩展 URL，同时在重定向循环上正确地快速失败。

## 5.总结

本教程讨论了如何使用 Apache HttpClient在 java 中扩展短 URL。

我们从一个简单的用例开始，该用例的 URL 仅被缩短一次，然后实施了一种更通用的机制，能够处理多级重定向并检测流程中的重定向循环。