## 1. 概述

[MockServer](http://www.mock-server.com/)是一个用于mocking/stubbing外部HTTP API的工具。

## 2. Maven依赖

要在我们的应用程序中使用MockServer，我们需要添加两个依赖项：

```xml
<dependency>
    <groupId>org.mock-server</groupId>
    <artifactId>mockserver-netty</artifactId>
    <version>3.10.8</version>
</dependency>
<dependency>
    <groupId>org.mock-server</groupId>
    <artifactId>mockserver-client-java</artifactId>
    <version>3.10.8</version>
</dependency>
```

最新版本的依赖项可作为[mockserver-netty](https://central.sonatype.com/artifact/org.mock-server/mockserver-netty/5.15.0)和[mockserver-client](https://central.sonatype.com/artifact/org.mock-server/mockserver-client-java/5.15.0)使用。

## 3. MockServer功能

简而言之，该工具可以：

-   生成并返回固定响应
-   将请求转发到另一台服务器
-   执行回调
-   验证请求

## 4. 如何运行MockServer

我们可以通过几种不同的方式启动服务器-让我们探索其中的一些方法。

### 4.1 通过Maven插件启动

这将在process-test-class阶段启动服务器，并在verify阶段停止：

```xml
<plugin>
    <groupId>org.mock-server</groupId>
    <artifactId>mockserver-maven-plugin</artifactId>
    <version>3.10.8</version>
    <configuration>
        <serverPort>1080</serverPort>
        <proxyPort>1090</proxyPort>
        <logLevel>DEBUG</logLevel>
        <initializationClass>org.mockserver.maven.ExampleInitializationClass</initializationClass>
    </configuration>
    <executions>
        <execution>
            <id>process-test-classes</id>
            <phase>process-test-classes</phase>
            <goals>
                <goal>start</goal>
            </goals>
        </execution>
        <execution>
            <id>verify</id>
            <phase>verify</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 4.2 通过Java API启动

我们可以使用startClientAndServer() Java API来启动服务器。通常，我们会在运行所有测试之前启动服务器：

```java
public class MockServerLiveTest {

    private ClientAndServer mockServer;

    @BeforeClass
    public void startServer() {
        mockServer = startClientAndServer(1080);
    }

    @AfterClass
    public void stopServer() {
        mockServer.stop();
    }

    // ...
}
```

## 5. Mock客户端

MockServerClient API用于提供连接到MockServer的功能。它对请求和来自服务器的相应响应进行建模。

它支持多种操作：

### 5.1 使用Mock响应创建期望

期望是一种机制，我们通过它模拟来自客户端的请求和来自MockServer的结果响应。

为了创建期望，我们需要定义一个请求匹配器和一个应该返回的响应。

可以使用以下方式匹配请求：

-   path：URL路径
-   query string：URL参数
-   headers：请求头
-   cookies：客户端cookies
-   body：具有XPATH、JSON、JSON模式、正则表达式、完全匹配的纯文本或正文参数的POST请求正文

以上所有参数都可以使用纯文本或正则表达式指定。

响应操作将包含：

-   status code：有效的HTTP状态码，例如200、400等
-   body：它是包含任何内容的字节序列
-   headers：带有名称和一个或多个值的响应头
-   cookies：带有名称和一个或多个值的响应cookie

让我们看看如何**创建期望**：

```java
public class TestMockServer {
    private void createExpectationForInvalidAuth() {
        new MockServerClient("127.0.0.1", 1080)
              .when(
                    request()
                          .withMethod("POST")
                          .withPath("/validate")
                          .withHeader("\"Content-type\", \"application/json\"")
                          .withBody(exact("{username: 'foo', password: 'bar'}")),
                    exactly(1))
              .respond(
                    response()
                          .withStatusCode(401)
                          .withHeaders(
                                new Header("Content-Type", "application/json; charset=utf-8"),
                                new Header("Cache-Control", "public, max-age=86400"))
                          .withBody("{ message: 'incorrect username and password combination' }")
                          .withDelay(TimeUnit.SECONDS,1)
              );
    }
    // ...
}
```

在这里，我们将POST请求stubbing到服务器。并指定了我们需要使用exactly(1)调用多少次来发出这个请求。

收到此请求后，我们Mock了一个包含状态码、标头和响应正文等字段的响应。

### 5.2 转发请求

可以设置期望来转发请求。一些参数可以描述转发操作：

-   **host**：要转发到的主机，例如www.taketoday.com
-   **port**：要转发请求的端口，默认端口为80
-   **schema**：使用的协议，例如HTTP或HTTPS

下面是一个转发请求的例子：

```java
private void createExpectationForForward() {
	new MockServerClient("127.0.0.1", 1080)
	    .when(request()
	        .withMethod("GET")
	        .withPath("/index.html"), exactly(1))
	    .forward(forward()
	        .withHost("www.mock-server.com")
	        .withPort(80)
	        .withScheme(HttpForward.Scheme.HTTP)
	    );
}
```

在这种情况下，我们Mock了一个请求，该请求将准确地命中MockServer，然后转发到另一台服务器。外部forward()方法指定转发操作，内部forward()方法调用有助于构建URL并转发请求。

### 5.3 执行回调

**服务器可以设置为在接收到特定请求时执行回调**。回调操作可以定义实现org.mockserver.mock.action.ExpectationCallback接口的回调类，它应该具有默认构造函数并且应该位于类路径上。

让我们看一个带有回调的期望示例：

```java
private void createExpectationForCallBack() {
	mockServer
	    .when(request()
	        .withPath("/callback"))
	    .callback(callback()
	        .withCallbackClass("cn.tuyucheng.taketoday.mock.server.ExpectationCallbackHandler")
	    );
}
```

这里，外层callback()指定回调操作，内层callback()方法指定回调方法类的实例。

在这种情况下，当MockServer收到带有/callback的请求时，将执行在指定类中实现的回调句柄方法：

```java
public class ExpectationCallbackHandler implements ExpectationCallback {

    public HttpResponse handle(HttpRequest httpRequest) {
        if (httpRequest.getPath().getValue().endsWith("/callback")) {
            return httpResponse;
        } else {
            return notFoundResponse();
        }
    }

    public static HttpResponse httpResponse = response().withStatusCode(200);
}
```

### 5.4 验证请求

MockServerClient能够检查被测系统是否发送了请求：

```java
private void verifyPostRequest() {
	new MockServerClient("localhost", 1080)
	    .verify(request()
	        .withMethod("POST")
	        .withPath("/validate")
	        .withBody(exact("{username: 'foo', password: 'bar'}")),
	    		VerificationTimes.exactly(1)
	    );
}
```

在这里，org.mockserver.verify.VerificationTimes类用于指定Mock Server应该匹配请求的次数。

## 6. 总结

在这篇快速文章中，我们探讨了MockServer的不同功能，并演示了该库提供的不同API以及如何将其用于测试复杂系统。