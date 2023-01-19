## 1. 概述

在本教程中，我们将探讨 Apache [Meecrowave](https://openwebbeans.apache.org/meecrowave/)框架的基本功能。

Meecrowave 是 Apache 的一个轻量级微服务框架，它与 CDI、JAX-RS 和 JSON API 配合得很好。它的设置和部署非常简单。它还消除了部署重型应用程序服务器(如 Tomcat、Glassfish、Wildfly 等)的麻烦。

## 2.Maven依赖

要使用 Meecrowave，让我们在 pom.xml 中定义依赖项：

```xml
<dependency>
    <groupId>org.apache.meecrowave</groupId>
    <artifactId>meecrowave-core</artifactId>
    <version>1.2.1</version>
</dependency>
```

[在Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.meecrowave" AND a%3A"meecrowave-core")检查最新版本。

## 3. 启动一个简单的服务器

我为了启动一个 Meecrowave 服务器，我们需要做的就是编写 main 方法，创建一个Meecrowave 实例并调用主 bake() 方法：

```java
public static void main(String[] args) {
    try (Meecrowave meecrowave = new Meecrowave()) {
        meecrowave.bake().await();
    }
}
```

如果我们将应用程序打包为分发包，则不需要此 main 方法；我们将在后面的部分中对此进行研究。主类在从 IDE 测试应用程序时很有用。

作为一个优势，在 IDE 中开发时，一旦我们使用主类运行应用程序，它会随着代码更改自动重新加载，从而省去了一次又一次重新启动服务器进行测试的麻烦。

请注意，如果我们使用Java9，请不要忘记将 javax .xml.bind模块添加到 VM：

```shell
--add-module javax.xml.bind
```

以这种方式创建服务器将以默认配置启动它。我们可以使用 Meecrowave.Builder类以编程方式更新默认配置：

```java
Meecrowave.Builder builder = new Meecrowave.Builder();
builder.setHttpPort(8080);
builder.setScanningPackageIncludes("com.baeldung.meecrowave");
builder.setJaxrsMapping("/api/");
builder.setJsonpPrettify(true);
```

并在烘焙服务器时使用此 构建器 实例：

```java
try (Meecrowave meecrowave = new Meecrowave(builder)) { 
    meecrowave.bake().await();
}
```

[这里](https://openwebbeans.apache.org/meecrowave/meecrowave-core/configuration.html)有更多可配置的属性 。

## 4.REST 端点

现在，一旦服务器准备就绪，让我们创建一些 REST 端点：

```java
@RequestScoped
@Path("article")
public class ArticleEndpoints {
    
    @GET
    public Response getArticle() {
        return Response.ok().entity(new Article("name", "author")).build();      
    }
    
    @POST 
    public Response createArticle(Article article) { 
        return Response.status(Status.CREATED).entity(article).build(); 
    }
}
```

请注意，我们主要使用 JAX-RS 注解来创建 REST 端点。[在此处](https://www.baeldung.com/jax-rs-spec-and-implementations)阅读有关 JAX-RS 的更多信息。

在下一节中，我们将了解如何测试这些端点。

## 5. 测试

为使用 Meecrowave 编写的 REST API 编写单元测试用例与编写带注解的 JUnit 测试用例一样简单。

让我们首先将测试依赖项添加到我们的 pom.xml 中：

```xml
<dependency>
    <groupId>org.apache.meecrowave</groupId>
    <artifactId>meecrowave-junit</artifactId>
    <version>1.2.1</version>
    <scope>test</scope>
</dependency>

```

要查看最新版本，请查看[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.meecrowave" AND a%3A"meecrowave-junit")。

另外，让我们添加[OkHttp](https://www.baeldung.com/guide-to-okhttp)作为我们测试的 HTTP 客户端：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>3.10.0</version>
</dependency>
```

[在此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.okhttp3" AND a%3A"okhttp")查看最新版本。

现在有了依赖项，让我们继续编写测试：

```java
@RunWith(MonoMeecrowave.Runner.class)
public class ArticleEndpointsIntegrationTest {
    
    @ConfigurationInject
    private Meecrowave.Builder config;
    private static OkHttpClient client;
    
    @BeforeClass
    public static void setup() {
        client = new OkHttpClient();
    }
    
    @Test
    public void whenRetunedArticle_thenCorrect() {
        String base = "http://localhost:" + config.getHttpPort();
        
        Request request = new Request.Builder()
          .url(base + "/article")
          .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
    }
}
```

在编写测试用例时，使用MonoMeecrowave.Runner类注解测试类 ，同时注入配置，以访问Meecrowave用于测试服务器的随机端口

## 6.依赖注入

要将依赖项注入类，我们需要在特定范围内注解这些类。

让我们以ArticleService 类为例 ：

```java
@ApplicationScoped
public class ArticleService {
    public Article createArticle(Article article) {
        return article;
    }
}
```

现在让我们使用javax.inject.Inject注解将它注入我们的 ArticleEndpoints实例 ：

```java
@Inject
ArticleService articleService;
```

## 7.打包应用

使用 Meecrowave Maven 插件创建分发包变得非常简单：

```xml
<build>
    ...
    <plugins>
        <plugin>
            <groupId>org.apache.meecrowave</groupId>
            <artifactId>meecrowave-maven-plugin</artifactId>
            <version>1.2.1</version>
        </plugin>
    </plugins>
</build>
```

一旦我们有了插件，让我们使用 Maven 目标 meecrowave:bundle 来打包应用程序。

打包后，它将在目标目录中创建一个 zip：

```shell
meecrowave-meecrowave-distribution.zip
```

此 zip 包含部署应用程序所需的工件：

```shell
|____meecrowave-distribution
| |____bin
| | |____meecrowave.sh
| |____logs
| | |____you_can_safely_delete.txt
| |____lib
| |____conf
| | |____log4j2.xml
| | |____meecrowave.properties
```

让我们导航到 bin 目录并启动应用程序：

```shell
./meecrowave.sh start
```

要停止应用程序：

```shell
./meecrowave.sh stop
```

## 八. 总结

在本文中，我们了解了如何使用 Apache Meecrowave 创建微服务。此外，我们研究了有关该应用程序的一些基本配置并准备了一个分发包。