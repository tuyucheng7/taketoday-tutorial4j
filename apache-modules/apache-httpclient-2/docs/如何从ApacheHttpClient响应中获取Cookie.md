## 1. 概述

在这个简短的教程中，我们将了解如何从 Apache HttpClient 响应中获取 cookie。 

首先，我们将展示如何使用HttpClient请求发送自定义 cookie。然后，我们将看看如何从响应中获取它。

请注意，此处提供的代码示例基于HttpClient 4.3.x 及更高版本，因此它们不适用于旧版本的 API。

## 2.在请求中发送Cookie

在我们可以从响应中获取我们的 cookie 之前，我们需要创建它并在请求中发送它：

```java
BasicCookieStore cookieStore = new BasicCookieStore();
BasicClientCookie cookie = new BasicClientCookie("custom_cookie", "test_value");
cookie.setDomain("baeldung.com");
cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
cookie.setPath("/");
cookieStore.addCookie(cookie);

HttpClientContext context = HttpClientContext.create();
context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
    try (CloseableHttpResponse response = httpClient.execute(new HttpGet("http://www.baeldung.com/"), context)) {
        //do something with the response
    }
}
```

首先，我们创建一个基本的 cookie 存储和一个名为custom_cookie和值test_value的基本[cookie](https://www.baeldung.com/httpclient-4-cookies)。然后，我们创建一个HttpClientContext实例来保存 cookie 存储。最后，我们将创建的上下文作为参数传递给execute()方法。

## 3. 访问 Cookie

现在我们已经在请求中发送了自定义 cookie，让我们看看如何从响应中读取它：

```java
HttpClientContext context = HttpClientContext.create();
context.setAttribute(HttpClientContext.COOKIE_STORE, createCustomCookieStore());

try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
    try (CloseableHttpResponse response = httpClient.execute(new HttpGet(SAMPLE_URL), context)) {
        CookieStore cookieStore = context.getCookieStore();
        Cookie customCookie = cookieStore.getCookies()
          .stream()
          .peek(cookie -> log.info("cookie name:{}", cookie.getName()))
          .filter(cookie -> "custom_cookie".equals(cookie.getName()))
          .findFirst()
          .orElseThrow(IllegalStateException::new);

          assertEquals("test_value", customCookie.getValue());
    }
}

```

要从响应中获取我们的自定义 cookie，我们必须首先从上下文中获取 cookie 存储。然后，我们使用getCookies方法获取 cookies 列表。然后我们可以利用 Java[流](https://www.baeldung.com/java-streams) 对其进行迭代并搜索我们的 cookie。此外，我们记录商店中的所有 cookie 名称：

```bash
[main] INFO  c.b.h.c.HttpClientGettingCookieValueTest - cookie name:__cfduid
[main] INFO  c.b.h.c.HttpClientGettingCookieValueTest - cookie name:custom_cookie
```

## 4. 总结

在本文中，我们了解了如何从 Apache HttpClient 响应中获取 cookie。