## 1. 概述

在本教程中，我们将展示如何在[Apache 的 HttpClient](https://www.baeldung.com/httpclient-guide)中启用日志记录。此外，我们将解释如何在库中实现日志记录。之后，我们将展示如何启用不同级别的日志记录。

## 2.日志实现

HttpClient 库提供高效、最新且功能丰富的 HTTP 协议实现客户端站点。

实际上，作为一个库，HttpClient 并不强制执行日志记录。为此，4.5 版提供了带有[Commons Logging](https://commons.apache.org/proper/commons-logging/)的日志。同样，最新版本 5.1 使用[SLF4J](https://baeldung.com/slf4j-with-log4j2-logback)提供的日志外观。两个版本都使用层次架构来匹配记录器及其配置。

因此，可以为单个类或与相同功能相关的所有类设置记录器。

## 3.日志类型

让我们看一下库定义的日志级别。我们可以区分3种类型的日志：

-   context logging – 记录有关 HttpClient 的所有内部操作的信息。它还包含线路和标题日志。
-   wire logging – 仅记录传输到服务器和从服务器传输的数据
-   header logging – 仅记录 HTTP 标头

在 4.5 版中，相应的包是org.apache.http.impl.client和org.apache.http.wire，org.apache.http.headers。

因此在 5.1 版中有包org.apache.hc.client5.http、org.apache.hc.client5.http.wire和 org.apache.hc.client5.http.headers。

## 4.Log4j配置

让我们看看如何启用登录到两个版本。我们的目标是在两个版本中实现相同的灵活性。在 4.1 版中，我们会将日志重定向到 SLF4j。因此，可以使用不同的日志记录框架。

### 4.1. 版本 4.5 配置

让我们添加[httpclient](https://search.maven.org/artifact/org.apache.httpcomponents/httpclient)依赖：

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.8</version>
    <exclusions>
        <exclusion>
            <artifactId>commons-logging</artifactId>
            <groupId>commons-logging</groupId>
        </exclusion>
    </exclusions>
</dependency>
```

我们将使用 [jcl-over-slf4j](https://search.maven.org/artifact/org.slf4j/jcl-over-slf4j) 将日志重定向到 SLF4J。因此我们排除 了 commons-logging。然后让我们在 JCL 和 SLF4J 之间的桥上添加一个依赖项：

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>1.7.32</version>
</dependency>
```

因为 SLF4J 只是一个外观，所以我们需要一个绑定。在我们的示例中，我们将使用[logback](https://search.maven.org/artifact/ch.qos.logback/logback-classic)：

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>
```

现在让我们创建ApacheHttpClientUnitTest类：

```java
public class ApacheHttpClientUnitTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String DUMMY_URL = "https://postman-echo.com/get";

    @Test
    public void whenUseApacheHttpClient_thenCorrect() throws IOException {
        HttpGet request = new HttpGet(DUMMY_URL);

        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            logger.debug("Response -> {}",  EntityUtils.toString(entity));
        }
    }
}
```

该测试获取一个虚拟网页并将内容打印到日志中。

现在让我们用我们的logback.xml文件定义一个记录器配置：

```xml
<configuration debug="false">
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date [%level] %logger - %msg %n</pattern>
        </encoder>
    </appender>

    <logger name="com.baeldung.httpclient.readresponsebodystring" level="debug"/>
    <logger name="org.apache.http" level="debug"/>

    <root level="WARN">
        <appender-ref ref="stdout"/>
    </root>
</configuration>
```

运行我们的测试后，可以在控制台中找到所有 HttpClient 的日志：

```java
...
2021-06-19 22:24:45,378 [DEBUG] org.apache.http.impl.execchain.MainClientExec - Executing request GET /get HTTP/1.1 
2021-06-19 22:24:45,378 [DEBUG] org.apache.http.impl.execchain.MainClientExec - Target auth state: UNCHALLENGED 
2021-06-19 22:24:45,379 [DEBUG] org.apache.http.impl.execchain.MainClientExec - Proxy auth state: UNCHALLENGED 
2021-06-19 22:24:45,382 [DEBUG] org.apache.http.headers - http-outgoing-0 >> GET /get HTTP/1.1 
...
```

### 4.2. 版本 5.1 配置

现在让我们看看更高版本。它包含重新设计的日志记录。因此，它使用 SLF4J 而不是 Commons Logging。因此，记录器外观的绑定是唯一的附加依赖项。因此，我们将像第一个示例一样使用logback-classic 。

让我们添加[httpclient5](https://search.maven.org/artifact/org.apache.httpcomponents.client5/httpclient5)依赖项：

```xml
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.1</version>
</dependency>
```

让我们添加一个与前面示例类似的测试：

```java
public class ApacheHttpClient5UnitTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String DUMMY_URL = "https://postman-echo.com/get";

    @Test
    public void whenUseApacheHttpClient_thenCorrect() throws IOException, ParseException {
        HttpGet request = new HttpGet(DUMMY_URL);

        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            logger.debug("Response -> {}", EntityUtils.toString(entity));
        }
    }
}
```

接下来，我们需要在logback.xml文件中添加一个记录器：

```xml
<configuration debug="false">
...
    <logger name="org.apache.hc.client5.http" level="debug"/>
...
</configuration>
```

让我们运行测试类ApacheHttpClient5UnitTest并检查输出。它类似于旧版本：

```java
...
2021-06-19 22:27:16,944 [DEBUG] org.apache.hc.client5.http.impl.classic.InternalHttpClient - ep-0000000000 endpoint connected 
2021-06-19 22:27:16,944 [DEBUG] org.apache.hc.client5.http.impl.classic.MainClientExec - ex-0000000001 executing GET /get HTTP/1.1 
2021-06-19 22:27:16,944 [DEBUG] org.apache.hc.client5.http.impl.classic.InternalHttpClient - ep-0000000000 start execution ex-0000000001 
2021-06-19 22:27:16,944 [DEBUG] org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager - ep-0000000000 executing exchange ex-0000000001 over http-outgoing-0 
2021-06-19 22:27:16,960 [DEBUG] org.apache.hc.client5.http.headers - http-outgoing-0 >> GET /get HTTP/1.1 
...
```

## 5.总结

关于如何为 Apache 的 HttpClient 配置日志记录的简短教程到此结束。首先，我们解释了日志记录是如何在库中实现的。其次，我们配置了两个版本的日志记录并执行了简单的测试用例以显示输出。