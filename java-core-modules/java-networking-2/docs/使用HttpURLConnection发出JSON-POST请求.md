## 1. 概述

在本教程中，我们将演示如何使用[HttpURLConnection](https://www.baeldung.com/java-http-request)[发出JSON](https://www.baeldung.com/category/json/) POST 请求。

## 延伸阅读：

## [用Java做一个简单的 HTTP 请求](https://www.baeldung.com/java-http-request)

使用Java的内置 HttpUrlConnection 执行基本 HTTP 请求的快速实用指南。

[阅读更多](https://www.baeldung.com/java-http-request)→

## [使用 HttpUrlConnection 进行身份验证](https://www.baeldung.com/java-http-url-connection)

了解如何使用 HttpUrlConnection 对 HTTP 请求进行身份验证。

[阅读更多](https://www.baeldung.com/java-http-url-connection)→

## [通过 CoreJava中的代理服务器进行连接](https://www.baeldung.com/java-connect-via-proxy-server)

了解如何使用系统属性或更灵活的 Proxy 类连接到Java中的代理服务器。

[阅读更多](https://www.baeldung.com/java-connect-via-proxy-server)→

## 2. 使用HttpURLConnection构建 JSON POST 请求

### 2.1. 创建一个URL对象

让我们创建一个带有目标 URI 字符串的URL对象，该对象通过 HTTP POST 方法接受 JSON 数据：

```java
URL url = new URL ("https://reqres.in/api/users");
```

### 2.2. 打开连接

从上面的URL对象，我们可以调用openConnection方法来获取HttpURLConnection对象。

我们不能直接实例化HttpURLConnection，因为它是一个抽象类：

```java
HttpURLConnection con = (HttpURLConnection)url.openConnection();
```

### 2.3. 设置请求方式

要发送 POST 请求，我们必须将请求方法属性设置为 POST：

```java
con.setRequestMethod("POST");
```

### 2.4. 设置请求内容类型标头参数

将“content-type”请求头设置为“application/json”，以JSON形式发送请求内容。必须设置此参数才能以 JSON 格式发送请求正文。

如果不这样做，服务器将返回 HTTP 状态代码“400-bad request”：

```java
con.setRequestProperty("Content-Type", "application/json");
```

### 2.5. 设置响应格式类型

将“Accept”请求标头设置 为“application/json”以读取所需格式的响应：

```java
con.setRequestProperty("Accept", "application/json");
```

### 2.6. 确保连接将用于发送内容

要发送请求内容，让我们将URLConnection对象的doOutput属性设置为true。

否则，我们将无法将内容写入连接输出流：

```java
con.setDoOutput(true);
```

### 2.7. 创建请求主体

创建自定义 JSON 字符串后：

```java
String jsonInputString = "{"name": "Upendra", "job": "Programmer"}";
```

我们需要这样写：

```java
try(OutputStream os = con.getOutputStream()) {
    byte[] input = jsonInputString.getBytes("utf-8");
    os.write(input, 0, input.length);			
}
```

### 2.8. 从输入流读取响应

获取输入流读取响应内容。请记住使用 try-with-resources 自动关闭响应流。

通读整个响应内容，并打印最终的响应字符串：

```java
try(BufferedReader br = new BufferedReader(
  new InputStreamReader(con.getInputStream(), "utf-8"))) {
    StringBuilder response = new StringBuilder();
    String responseLine = null;
    while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
    }
    System.out.println(response.toString());
}
```

如果响应是 JSON 格式，请使用任何第三方 JSON 解析器(例如[Jackson ](https://www.baeldung.com/jackson)库、[Gson](https://www.baeldung.com/gson-string-to-jsonobject)或[org.json)](https://www.baeldung.com/java-org-json)来解析响应。

## 3.总结

在本文中，我们学习了如何使用HttpURLConnection使用 JSON 内容主体发出 POST 请求。