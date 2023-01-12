## 1. 简介

在本文中，我们将解释过滤器和拦截器如何在 Jersey 框架中工作，以及它们之间的主要区别。

我们将在这里使用 Jersey 2，并使用 Tomcat 9 服务器测试我们的应用程序。

## 2. 应用设置

让我们首先在我们的服务器上创建一个简单的资源：

```java
@Path("/greetings")
public class Greetings {

    @GET
    public String getHelloGreeting() {
        return "hello";
    }
}
```

另外，让我们为我们的应用程序创建相应的服务器配置：

```java
@ApplicationPath("/")
public class ServerConfig extends ResourceConfig {

    public ServerConfig() {
        packages("com.baeldung.jersey.server");
    }
}
```

如果想更深入地了解如何使用 Jersey 创建 API，可以查看[这篇文章](https://www.baeldung.com/jersey-rest-api-with-spring)。

还可以查看[我们以客户端为中心的文章](https://www.baeldung.com/jersey-jax-rs-client)，了解如何使用 Jersey 创建Java客户端。

## 3.过滤器

现在，让我们开始使用过滤器。

简单地说，过滤器让我们修改请求和响应的属性——例如，HTTP 标头。过滤器可以应用于服务器端和客户端。

请记住，无论是否找到资源，过滤器始终会执行。

### 3.1. 实现请求服务器过滤器

让我们从服务器端的过滤器开始，创建一个请求过滤器。

我们将通过实现ContainerRequestFilter接口并将其注册为服务器中的Provider来实现：

```java
@Provider
public class RestrictedOperationsRequestFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        if (ctx.getLanguage() != null && "EN".equals(ctx.getLanguage()
          .getLanguage())) {
 
            ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
              .entity("Cannot access")
              .build());
        }
    }
}
```

这个简单的过滤器只是通过调用abortWith()方法拒绝请求中带有“EN”语言的请求。

如示例所示，我们只需要实现一个接收请求上下文的方法，我们可以根据需要修改它。

请记住，此过滤器是在资源匹配后执行的。

如果我们想在资源匹配之前执行一个过滤器，我们可以通过使用@PreMatching注解来注解我们的过滤器来使用预匹配过滤器：

```java
@Provider
@PreMatching
public class PrematchingRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        if (ctx.getMethod().equals("DELETE")) {
            LOG.info(""Deleting request");
        }
    }
}
```

如果我们现在尝试访问我们的资源，我们可以检查我们的预匹配过滤器是否首先执行：

```plaintext
2018-02-25 16:07:27,800 [http-nio-8080-exec-3] INFO  c.b.j.s.f.PrematchingRequestFilter - prematching filter
2018-02-25 16:07:27,816 [http-nio-8080-exec-3] INFO  c.b.j.s.f.RestrictedOperationsRequestFilter - Restricted operations filter
```

### 3.2. 实施响应服务器过滤器

我们现在将在服务器端实现一个响应过滤器，它只会向响应添加一个新的标头。

为此，我们的过滤器必须实现ContainerResponseFilter接口并实现其唯一方法：

```java
@Provider
public class ResponseServerFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, 
      ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("X-Test", "Filter test");
    }
}
```

请注意，ContainerRequestContext参数仅用作只读 - 因为我们已经在处理响应。

### 2.3. 实施客户端过滤器

我们现在将在客户端使用过滤器。这些过滤器的工作方式与服务器过滤器相同，我们必须实现的接口与服务器端的接口非常相似。

让我们看看它是如何使用过滤器向请求添加属性的：

```java
@Provider
public class RequestClientFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.setProperty("test", "test client request filter");
    }
}
```

让我们也创建一个 Jersey 客户端来测试这个过滤器：

```java
public class JerseyClient {

    private static String URI_GREETINGS = "http://localhost:8080/jersey/greetings";

    public static String getHelloGreeting() {
        return createClient().target(URI_GREETINGS)
          .request()
          .get(String.class);
    }

    private static Client createClient() {
        ClientConfig config = new ClientConfig();
        config.register(RequestClientFilter.class);

        return ClientBuilder.newClient(config);
    }
}
```

请注意，我们必须将过滤器添加到客户端配置中才能注册它。

最后，我们还将为客户端中的响应创建一个过滤器。

这与服务器中的工作方式非常相似，但实现了ClientResponseFilter接口：

```java
@Provider
public class ResponseClientFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext, 
      ClientResponseContext responseContext) throws IOException {
        responseContext.getHeaders()
          .add("X-Test-Client", "Test response client filter");
    }

}
```

同样，ClientRequestContext用于只读目的。

## 4.拦截器

拦截器更多地与包含在请求和响应中的 HTTP 消息体的编组和解组相关。它们既可以在服务器端使用，也可以在客户端使用。

请记住，它们是在过滤器之后执行的，并且仅当存在消息正文时才执行。

有两种类型的拦截器：ReaderInterceptor和WriterInterceptor，它们对于服务器端和客户端都是相同的。

接下来，我们将在我们的服务器上创建另一个资源——它通过 POST 访问并在主体中接收一个参数，因此在访问它时将执行拦截器：

```java
@POST
@Path("/custom")
public Response getCustomGreeting(String name) {
    return Response.status(Status.OK.getStatusCode())
      .build();
}
```

我们还将向我们的 Jersey 客户端添加一个新方法——来测试这个新资源：

```java
public static Response getCustomGreeting() {
    return createClient().target(URI_GREETINGS + "/custom")
      .request()
      .post(Entity.text("custom"));
}
```

### 4.1. 实现一个ReaderInterceptor

Reader 拦截器允许我们操作入站流，因此我们可以使用它们来修改服务器端的请求或客户端的响应。

让我们在服务器端创建一个拦截器，以在拦截的请求正文中写入自定义消息：

```java
@Provider
public class RequestServerReaderInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) 
      throws IOException, WebApplicationException {
        InputStream is = context.getInputStream();
        String body = new BufferedReader(new InputStreamReader(is)).lines()
          .collect(Collectors.joining("n"));

        context.setInputStream(new ByteArrayInputStream(
          (body + " message added in server reader interceptor").getBytes()));

        return context.proceed();
    }
}
```

请注意，我们必须调用proceed()方法 来调用链中的下一个拦截器。一旦所有的拦截器都被执行，适当的消息体阅读器将被调用。

### 3.2. 实现一个WriterInterceptor

编写器拦截器的工作方式与读取器拦截器非常相似，但它们操纵出站流——因此我们可以将它们用于客户端的请求或服务器端的响应。

让我们在客户端创建一个编写器拦截器来向请求添加一条消息：

```java
@Provider
public class RequestClientWriterInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) 
      throws IOException, WebApplicationException {
        context.getOutputStream()
          .write(("Message added in the writer interceptor in the client side").getBytes());

        context.proceed();
    }
}
```

同样，我们必须调用方法proceed()来调用下一个拦截器。

当所有的拦截器都被执行时，相应的消息正文编写器将被调用。

不要忘记你必须在客户端配置中注册这个拦截器，就像我们之前对客户端过滤器所做的那样：

```java
private static Client createClient() {
    ClientConfig config = new ClientConfig();
    config.register(RequestClientFilter.class);
    config.register(RequestWriterInterceptor.class);

    return ClientBuilder.newClient(config);
}
```

## 5. 执行顺序

让我们在一张图表中总结到目前为止我们所看到的所有内容，该图表显示了在从客户端到服务器的请求期间何时执行过滤器和拦截器：

[![球衣2](https://www.baeldung.com/wp-content/uploads/2018/03/Jersey2.png)](https://www.baeldung.com/wp-content/uploads/2018/03/Jersey2.png)

如我们所见，过滤器总是先执行，拦截器在调用适当的消息体读取器或写入器之前执行。

如果我们看一下我们创建的过滤器和拦截器，它们将按以下顺序执行：

1.  请求客户端过滤器
2.  RequestClientWriterInterceptor
3.  预匹配请求过滤器
4.  受限操作请求过滤器
5.  RequestServerReader拦截器
6.  响应服务器过滤器
7.  响应客户端过滤器

此外，当我们有多个过滤器或拦截器时，我们可以通过使用@Priority注解来指定确切的执行顺序。

优先级用整数指定，并按请求的升序和响应的降序对过滤器和拦截器进行排序。

让我们为RestrictedOperationsRequestFilter添加一个优先级：

```java
@Provider
@Priority(Priorities.AUTHORIZATION)
public class RestrictedOperationsRequestFilter implements ContainerRequestFilter {
    // ...
}
```

请注意，我们使用预定义的优先级进行授权。

## 6.名称绑定

到目前为止我们看到的过滤器和拦截器称为全局的，因为它们针对每个请求和响应执行。

但是，也可以将它们定义为仅针对特定资源方法执行，这称为名称绑定。

### 6.1. 静态绑定

进行名称绑定的一种方法是静态地创建将在所需资源中使用的特定注解。此注解必须包含@NameBinding元注解。

让我们在我们的应用程序中创建一个：

```java
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface HelloBinding {
}
```

之后，我们可以用这个@HelloBinding注解来注解一些资源：

```java
@GET
@HelloBinding
public String getHelloGreeting() {
    return "hello";
}
```

最后，我们也将使用此注解来注解我们的过滤器之一，因此此过滤器将仅针对访问getHelloGreeting()方法的请求和响应执行：

```java
@Provider
@Priority(Priorities.AUTHORIZATION)
@HelloBinding
public class RestrictedOperationsRequestFilter implements ContainerRequestFilter {
    // ...
}
```

请记住，我们的RestrictedOperationsRequestFilter将不再为其余资源触发。

### 6.2. 动态绑定

另一种方法是使用动态绑定，它在启动期间加载到配置中。

让我们首先为这个部分添加另一个资源到我们的服务器：

```java
@GET
@Path("/hi")
public String getHiGreeting() {
    return "hi";
}
```

现在，让我们通过实现DynamicFeature接口为这个资源创建一个绑定：

```java
@Provider
public class HelloDynamicBinding implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (Greetings.class.equals(resourceInfo.getResourceClass()) 
          && resourceInfo.getResourceMethod().getName().contains("HiGreeting")) {
            context.register(ResponseServerFilter.class);
        }
    }
}
```

在这种情况下，我们将getHiGreeting()方法与我们之前创建的ResponseServerFilter相关联。

重要的是要记住，我们必须从此过滤器中删除@Provider注解，因为我们现在是通过DynamicFeature配置它的。

如果我们不这样做，过滤器将被执行两次：一次作为全局过滤器，另一次作为绑定到getHiGreeting()方法的过滤器。

## 七. 总结

在本教程中，我们重点了解过滤器和拦截器在 Jersey 2 中的工作方式以及我们如何在 Web 应用程序中使用它们。