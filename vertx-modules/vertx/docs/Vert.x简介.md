## 1. 概述

在本文中，我们将讨论[Vert.x](http://vertx.io/)，涵盖其核心概念并使用它创建一个简单的RESTful Web服务。

我们将从涵盖有关工具包的基础概念开始，慢慢前进到HTTP服务器，然后构建RESTful服务。

## 2. 关于Vert.x

Vert.x是来自Eclipse开发人员的开源、响应式和多语言软件开发工具包。

响应式编程是一种编程范例，与异步流相关联，可以响应任何更改或事件。

类似地，Vert.x使用事件总线与应用程序的不同部分进行通信，并在事件可用时将事件异步传递给处理程序。

我们称它为多语言，因为它支持多种JVM和非JVM语言，如Java、Groovy、Ruby、Python和JavaScript。

## 3. 设置

要使用Vert.x，我们需要添加Maven依赖项：

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-core</artifactId>
    <version>3.4.1</version>
</dependency>
```

可以在[此处](https://search.maven.org/search?q=a:vertx-core)找到最新版本的依赖项。

## 4. Verticles

Verticles是Vert.x引擎执行的代码片段。该工具包为我们提供了许多抽象的垂直类，可以根据我们的需要进行扩展和实现。

作为多语言，verticles可以用任何支持的语言编写。应用程序通常由运行在同一个Vert.x实例中的多个Verticle组成，并通过事件总线使用事件相互通信。

要在Java中创建Verticle，该类必须实现io.vertx.core.Verticle接口或其任何一个子类。

## 5. 事件总线

它是任何Vert.x应用程序的神经系统。

作为响应性的，Verticles在收到消息或事件之前保持休眠状态。Verticles通过事件总线相互通信。消息可以是从字符串到复杂对象的任何内容。

消息处理理想情况下是异步的，消息排队到事件总线，控制权返回给发送者。后来它出列到监听verticle。使用Future和回调方法发送响应。

## 6. 简单的Vert.x应用

让我们创建一个带有Verticle的简单应用程序，并使用vertx实例部署它。要创建我们的Verticle，我们将扩展io.vertx.core.AbstractVerticle类并覆盖start()方法：

```java
public class HelloVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> future) {
		LOGGER.info("Welcome to Vertx");
	}
}
```

部署Verticle时，vertx实例将调用start()方法。该方法以io.vertx.core.Future作为参数，可用于发现Verticle异步部署的状态。

现在让我们部署verticle：

```java
public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new HelloVerticle());
}
```

同样，我们可以覆盖AbstractVerticle类中的stop()方法，该方法将在关闭Verticle时调用：

```java
@Override
public void stop() {
    LOGGER.info("Shutting down application");
}
```

## 7. HTTP服务器

现在让我们使用Verticle启动一个HTTP服务器：

```java
@Override
public void start(Future<Void> future) {
    vertx.createHttpServer()
      	.requestHandler(r -> r.response().end("Welcome toVert.xIntro");
      	})
      	.listen(config().getInteger("http.port", 9090), 
        	result -> {
          		if (result.succeeded()) {
              		future.complete();
          		} else {
              		future.fail(result.cause());
          	}
      	});
}
```

我们已经重写了start()方法来创建一个HTTP服务器并为其附加一个请求处理程序。每次服务器收到请求时都会调用requestHandler()方法。

最后，服务器绑定到一个端口，AsyncResult<HttpServer\>处理程序被传递给listen()方法，无论连接或服务器启动是否成功，使用future.complete()或future.fail()在任何错误的情况。

请注意：config.getInteger()方法正在读取从外部conf.json文件加载的HTTP端口配置的值。

让我们测试我们的服务器：

```java
@Test
public void whenReceivedResponse_thenSuccess(TestContext testContext) {
    Async async = testContext.async();

    vertx.createHttpClient()
      	.getNow(port, "localhost", "/", response -> {
        	response.handler(responseBody -> {
          		testContext.assertTrue(responseBody.toString().contains("Hello"));
          		async.complete();
        	});
      	});
}
```

为了进行测试，让我们将vertx-unit与JUnit一起使用：

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-unit</artifactId>
    <version>3.4.1</version>
    <scope>test</scope>
</dependency>
```

我们可以在[这里](https://search.maven.org/search?q=a:vertx-unit)获得最新版本。

verticle被部署在单元测试的setup()方法中的一个vertx实例中：

```java
@Before
public void setup(TestContext testContext) {
    vertx = Vertx.vertx();

    vertx.deployVerticle(SimpleServerVerticle.class.getName(), testContext.asyncAssertSuccess());
}
```

同样，vertx实例在@AfterClass tearDown()方法中被关闭：

```java
@After
public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
}
```

请注意，@BeforeClass setup()方法采用TestContext参数。这有助于控制和测试测试的异步行为。例如，Verticle部署是异步的，所以基本上我们不能测试任何东西，除非它部署正确。

我们有deployVerticle()方法的第二个参数，testContext.asyncAssertSuccess()。这用于了解服务器是否正确部署或发生任何故障。它等待调用服务器Verticle中的future.complete() 或 future.fail()。在失败的情况下，它无法通过测试。

## 8. RESTful Web服务

我们已经创建了一个HTTP服务器，现在让我们使用它来托管RESTfull WebService。为此，我们需要另一个名为vertx-web的Vert.x模块。这为vertx-core之上的Web开发提供了许多附加功能。

让我们将依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-web</artifactId>
    <version>3.4.1</version>
</dependency>
```

我们可以在[这里](https://search.maven.org/search?q=a:vertx-web)找到最新版本。

### 8.1 路由器和路由

让我们为我们的WebService创建一个路由器，该路由器将采用GET方法和处理程序方法getArtilces()的简单路由：

```java
Router router = Router.router(vertx);
router.get("/api/tuyucheng/articles/article/:id")
  	.handler(this::getArticles);
```

getArticle()方法是一个返回新Article对象的简单方法：

```java
private void getArticles(RoutingContext routingContext) {
    String articleId = routingContext.request()
      	.getParam("id");
    Article article = new Article(articleId, "This is an intro to vertx", "tuyucheng", "01-02-2017", 1578);

    routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200)
        .end(Json.encodePrettily(article));
}
```

路由器在收到请求时，会查找匹配的路由，然后进一步传递请求。具有与之关联的处理程序方法的路由对请求进行处理。

在我们的例子中，处理程序调用getArticle()方法。它接收routingContext对象作为参数。导出路径参数id，并用它创建一个Article对象。

在方法的最后一部分，让我们调用routingContext对象的response()方法并放置标头，设置HTTP响应代码，并使用JSON编码的文章对象结束响应。

### 8.2 将路由器添加到服务器

现在让我们将在上一节中创建的路由器添加到 HTTP 服务器：

```java
vertx.createHttpServer()
  	.requestHandler(router::accept)
  	.listen(config().getInteger("http.port", 8080), 
    	result -> {
      		if (result.succeeded()) {
          		future.complete();
      		} else {
          		future.fail(result.cause());
      	}
});
```

请注意，我们已将requestHandler(router::accept)添加到服务器，这指示服务器在收到任何请求时调用路由器对象的accept()。

现在让我们测试我们的WebService：

```java
@Test
public void givenId_whenReceivedArticle_thenSuccess(TestContext testContext) {
    Async async = testContext.async();

    vertx.createHttpClient()
      	.getNow(8080, "localhost", "/api/tuyucheng/articles/article/12345", 
        	response -> {
            	response.handler(responseBody -> {
            	testContext.assertTrue(responseBody.toString().contains("\"id\" : \"12345\""));
            	async.complete();
        	});
      	});
}
```

## 9. 打包Vert.x应用

要将应用程序打包为可部署的Java存档(.jar)，让我们使用Maven Shade插件和execution标签中的configuration：

```xml
<configuration>
	<transformers>
		<transformer
			implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
			<manifestEntries>
				<Main-Class>io.vertx.core.Starter</Main-Class>
				<Main-Verticle>cn.tuyucheng.taketoday.SimpleServerVerticle</Main-Verticle>
			</manifestEntries>
		</transformer>
	</transformers>
	<artifactSet/>
	<outputFile>
		${project.build.directory}/${project.artifactId}-${project.version}-app.jar
	</outputFile>
</configuration>
```

在manifestEntries中，Main-Verticle表示应用程序的起点，Main-Class是一个Vert.x类，它创建vertx实例并部署Main-Verticle。

## 10. 总结

在这篇介绍性文章中，我们讨论了Vert.x工具包及其基本概念。了解了如何使用Vert.x和RESTFull WebService创建HTTP服务器，并展示了如何使用vertx-unit测试它们。

最后将应用程序打包为可执行jar。