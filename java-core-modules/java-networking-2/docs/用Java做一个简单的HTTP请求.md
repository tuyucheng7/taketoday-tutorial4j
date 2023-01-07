## 1. 概述

在本快速教程中，我们介绍了一种在Java中执行 HTTP 请求的方法——使用内置的Java类HttpUrlConnection。 

[请注意，从 JDK 11 开始，Java 提供了一个用于执行 HTTP 请求的](https://www.baeldung.com/java-9-http-client)新 API，这意味着 HttpUrlConnection 的替代品，即HttpClient [API](https://www.baeldung.com/java-9-http-client)。

## 延伸阅读：

## [Java 中的 HTTP Cookie 指南](https://www.baeldung.com/cookies-java)

Java 中 HTTP Cookie 的快速实用指南

[阅读更多](https://www.baeldung.com/cookies-java)→

## [探索Java中的新 HTTP 客户端](https://www.baeldung.com/java-9-http-client)

探索新的JavaHttpClient API，它提供了很多灵活性和强大的功能。

[阅读更多](https://www.baeldung.com/java-9-http-client)→

## [Java 的 Web 和应用程序服务器](https://www.baeldung.com/java-servers)

可用Java中的 Web 和应用程序服务器的快速列表。

[阅读更多](https://www.baeldung.com/java-servers)→

## 2.HttpUrlConnection _

HttpUrlConnection类允许我们在不使用任何额外库的情况下执行基本的 HTTP 请求。我们需要的所有类都是java.net包的一部分。

使用此方法的缺点是代码可能比其他 HTTP 库更繁琐，并且它不提供更高级的功能，例如用于添加标头或身份验证的专用方法。

## 3.创建请求

我们可以使用URL类的openConnection()方法创建一个HttpUrlConnection实例。请注意，此方法仅创建一个连接对象，但尚未建立连接。

HttpUrlConnection类用于所有类型的请求，方法是将 requestMethod 属性设置为以下值之一：GET、POST、HEAD、OPTIONS、PUT、DELETE、TRACE。

让我们使用 GET 方法创建到给定 URL 的连接：

```java
URL url = new URL("http://example.com");
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.setRequestMethod("GET");
```

## 4.添加请求参数

如果我们想向请求添加参数，我们必须将doOutput属性设置为true，然后将param1=value¶m2=value形式的字符串写入HttpUrlConnection实例的OutputStream：

```java
Map<String, String> parameters = new HashMap<>();
parameters.put("param1", "val");

con.setDoOutput(true);
DataOutputStream out = new DataOutputStream(con.getOutputStream());
out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
out.flush();
out.close();
```

为了方便参数 Map的转换，我们编写了一个名为ParameterStringBuilder的实用程序类，其中包含一个静态方法getParamsString()，它将Map转换为所需格式的String ：

```java
public class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) 
      throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
}
```

## 5.设置请求头

可以使用setRequestProperty()方法向请求添加标头：

```java
con.setRequestProperty("Content-Type", "application/json");
```

要从连接中读取标头的值，我们可以使用getHeaderField()方法：

```java
String contentType = con.getHeaderField("Content-Type");
```

## 6.配置超时

HttpUrlConnection类允许设置连接和读取超时。这些值定义了等待与服务器建立连接或等待数据可供读取的时间间隔。

要设置超时值，我们可以使用setConnectTimeout()和setReadTimeout()方法：

```java
con.setConnectTimeout(5000);
con.setReadTimeout(5000);
```

在示例中，我们将两个超时值都设置为五秒。

## 7. 处理 Cookie

java.net包包含简化使用 cookie 的类，例如CookieManager和HttpCookie。

首先，要从响应中读取 cookie ，我们可以检索Set-Cookie标头的值并将其解析为HttpCookie对象列表：

```java
String cookiesHeader = con.getHeaderField("Set-Cookie");
List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
```

接下来，我们将把cookie 添加到 cookie 存储中：

```java
cookies.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));
```

让我们检查一个名为username的 cookie是否存在，如果不存在，我们将把它添加到 cookie 存储中，值为“john”：

```java
Optional<HttpCookie> usernameCookie = cookies.stream()
  .findAny().filter(cookie -> cookie.getName().equals("username"));
if (usernameCookie == null) {
    cookieManager.getCookieStore().add(null, new HttpCookie("username", "john"));
}
```

最后，要将cookie 添加到请求中，我们需要在关闭并重新打开连接后设置Cookie标头：

```java
con.disconnect();
con = (HttpURLConnection) url.openConnection();

con.setRequestProperty("Cookie", 
  StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));
```

## 8. 处理重定向

我们可以使用带有true或false参数的setInstanceFollowRedirects()方法为特定连接自动启用或禁用跟随重定向：

```java
con.setInstanceFollowRedirects(false);
```

也可以为所有连接启用或禁用自动重定向：

```java
HttpUrlConnection.setFollowRedirects(false);
```

默认情况下，该行为已启用。

当请求返回状态代码 301 或 302 时，表示重定向，我们可以检索Location标头并创建对新 URL 的新请求：

```java
if (status == HttpURLConnection.HTTP_MOVED_TEMP
  || status == HttpURLConnection.HTTP_MOVED_PERM) {
    String location = con.getHeaderField("Location");
    URL newUrl = new URL(location);
    con = (HttpURLConnection) newUrl.openConnection();
}
```

## 9.阅读回应

读取请求的响应可以通过解析HttpUrlConnection实例的InputStream来完成。

要执行请求，我们可以使用getResponseCode()、connect()、getInputStream()或getOutputStream()方法：

```java
int status = con.getResponseCode();
```

最后，让我们读取请求的响应并将其放入内容字符串中：

```html
BufferedReader in = new BufferedReader(
  new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer content = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    content.append(inputLine);
}
in.close();
```

要关闭连接，我们可以使用disconnect()方法：

```java
con.disconnect();

```

## 10.阅读失败请求的响应

如果请求失败，尝试读取HttpUrlConnection实例的InputStream将不起作用。相反，我们可以使用HttpUrlConnection.getErrorStream()提供的流。

我们可以通过比较[HTTP 状态码](https://www.baeldung.com/cs/http-status-codes)来决定使用哪个InputStream：

```java
int status = con.getResponseCode();

Reader streamReader = null;

if (status > 299) {
    streamReader = new InputStreamReader(con.getErrorStream());
} else {
    streamReader = new InputStreamReader(con.getInputStream());
}
```

最后，我们可以 像上一节一样读取streamReader 。

## 11.建立完整的回应

使用HttpUrlConnection 实例无法获得完整的响应表示 。

但是，我们可以使用HttpUrlConnection 实例提供的一些方法来构建它 ：

```java
public class FullResponseBuilder {
    public static String getFullResponse(HttpURLConnection con) throws IOException {
        StringBuilder fullResponseBuilder = new StringBuilder();

        // read status and message

        // read headers

        // read response content

        return fullResponseBuilder.toString();
    }
}
```

在这里，我们正在读取响应的部分，包括状态代码、状态消息和标头，并将它们添加到StringBuilder实例中。

首先，让我们添加响应状态信息：

```java
fullResponseBuilder.append(con.getResponseCode())
  .append(" ")
  .append(con.getResponseMessage())
  .append("n");
```

接下来，我们将使用getHeaderFields()获取标头，并将它们以 HeaderName: HeaderValues格式添加到我们的StringBuilder中：

```java
con.getHeaderFields().entrySet().stream()
  .filter(entry -> entry.getKey() != null)
  .forEach(entry -> {
      fullResponseBuilder.append(entry.getKey()).append(": ");
      List headerValues = entry.getValue();
      Iterator it = headerValues.iterator();
      if (it.hasNext()) {
          fullResponseBuilder.append(it.next());
          while (it.hasNext()) {
              fullResponseBuilder.append(", ").append(it.next());
          }
      }
      fullResponseBuilder.append("n");
});
```

最后，我们将像之前一样读取响应内容并将其附加。

请注意， getFullResponse 方法将验证请求是否成功，以决定是否需要使用 con.getInputStream()或 con.getErrorStream()来检索请求的内容。

## 12.总结

在本文中，我们展示了如何使用HttpUrlConnection类执行 HTTP 请求。