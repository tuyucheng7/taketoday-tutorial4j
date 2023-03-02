## 1. 概述

在本教程中，我们将介绍Netflix Zuul的后置过滤器。

[Netflix Zuul](https://www.baeldung.com/spring-rest-with-zuul-proxy)是一个位于API客户端和大量微服务之间的边缘服务提供商。

后置过滤器在最终响应发送到API客户端之前运行。这使我们有机会对原始响应主体进行操作，并执行我们想要的日志记录和其他数据转换等操作。

## 2. 依赖关系

我们将在Spring Cloud环境中使用Zuul。因此，让我们将以下内容添加到pom.xml的依赖管理部分：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2020.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        <version>2.2.2.RELEASE</version>
    </dependency>
</dependencies>
```

可以在Maven Central上找到最新版本的[Spring Cloud](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1)依赖项和[spring-cloud-starter-netflix-zuul](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-zuul/2.2.10.RELEASE)。

## 3. 创建后置过滤器

**后置过滤器是一个常规类，它扩展了抽象类ZuulFilter并具有post过滤器类型**：

```java
public class ResponseLogFilter extends ZuulFilter {

	@Override
	public String filterType() {
		return POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		return null;
	}
}
```

请注意，我们在filterType()方法中返回了POST_TYPE。这实际上是此过滤器与其他类型的区别。

另一个需要注意的重要方法是shouldFilter()方法。我们在这里返回true因为我们希望过滤器在过滤器链中运行。

在生产就绪的应用程序中，我们可以将此配置外部化以获得更好的灵活性。

让我们仔细看看每当我们的过滤器运行时都会调用的run()。

## 4. 修改响应体

如前所述，Zuul介于微服务和他们的客户端之间。因此，它可以访问响应主体并可以选择在传递响应正文之前对其进行修改。

例如，我们可以读取响应主体并记录其内容：

```java
@Override
public Object run() throws ZuulException {
    RequestContext context = RequestContext.getCurrentContext();
    try (final InputStream responseDataStream = context.getResponseDataStream()) {
        if(responseDataStream == null) {
            logger.info("BODY: {}", "");
            return null;
        }

        String responseData = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));
        logger.info("BODY: {}", responseData);

        context.setResponseBody(responseData);
    }
    catch (Exception e) {
        throw new ZuulException(e, INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    return null;
}
```

上面的代码片段显示了我们之前创建的ResponseLogFilter中run()方法的完整实现。首先，我们获得了RequestContext的一个实例。从该上下文中，我们能够在尝试使用资源构造时获得响应数据InputStream。

请注意，响应输入流可能为空，这就是我们检查它的原因。这可能是由于服务超时或微服务上的其他意外异常造成的。在我们的例子中，当这种情况发生时，我们只是记录一个空的响应主体。

接下来，我们将输入流读入一个字符串，然后我们可以记录该字符串。

**非常重要的是，我们使用context.setResponseBody(responseData)将响应主体添加回上下文进行处理**。如果我们省略这一步，我们将得到如下几行的IOException:java.io.IOException: Attempted read on a closed stream。

## 5. 总结

总之，Zuul中的后置过滤器为开发人员提供了一个机会，可以在将服务响应发送到客户端之前对其执行一些操作。

但是，我们必须小心不要意外暴露敏感信息，从而导致泄露。

此外，我们应该警惕在我们的后置过滤器中执行长时间运行的任务，因为它会大大增加响应时间。