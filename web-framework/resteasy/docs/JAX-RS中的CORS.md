## 1. 概述

在这篇简短的文章中，我们将了解如何在基于JAX-RS 的系统中启用[CORS](https://www.baeldung.com/cs/cors-preflight-requests)(跨源资源共享) 。我们将在JAX-RS之上设置一个应用程序以启用CORS机制。

## 二、如何开启CORS机制

我们可以通过两种方式在 JAX-RS 中启用 CORS。第一种也是最基本的方法是创建一个过滤器以在每个请求的运行时注入必要的响应标头。另一种是在每个 URL 端点中手动添加适当的标头。

理想情况下，应使用第一种解决方案；然而，当这不是一个选项时，更多的手动选项在技术上也是可行的。

### 2.1. 使用过滤器

JAX-RS有[ContainerResponseFilter](https://docs.oracle.com/javaee/7/api/javax/ws/rs/container/ContainerResponseFilter.html)接口——由容器响应过滤器实现。通常，此过滤器实例全局应用于任何 HTTP 响应。

我们将实现此接口以创建一个自定义过滤器，它将Access-Control-Allow-标头注入每个传出请求并启用CORS机制：

```java
@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, 
      ContainerResponseContext responseContext) throws IOException {
          responseContext.getHeaders().add(
            "Access-Control-Allow-Origin", "");
          responseContext.getHeaders().add(
            "Access-Control-Allow-Credentials", "true");
          responseContext.getHeaders().add(
           "Access-Control-Allow-Headers",
           "origin, content-type, accept, authorization");
          responseContext.getHeaders().add(
            "Access-Control-Allow-Methods", 
            "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}
```

这里有几点：

-   实现ContainerResponseFilter的过滤器必须用@Provider显式注解才能被 JAX-RS 运行时发现
-   我们正在注入带有“”的“ Access-Control-Allow- ”标头，这意味着可以通过任何域访问此服务器实例的任何 URL 端点；如果我们想明确限制跨域访问，我们必须在此标头中提及该域

### 2.2. 在每个端点中使用标头修改

如前所述，我们也可以在端点级别显式注入“ Access-Control-Allow- ”标头：

```java
@GET
@Path("/")
@Produces({MediaType.TEXT_PLAIN})
public Response index() {
    return Response
      .status(200)
      .header("Access-Control-Allow-Origin", "")
      .header("Access-Control-Allow-Credentials", "true")
      .header("Access-Control-Allow-Headers",
        "origin, content-type, accept, authorization")
      .header("Access-Control-Allow-Methods", 
        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
      .entity("")
      .build();
}
```

这里需要注意的一点是，如果我们试图在大型应用程序中启用CORS，我们不应该尝试这种方法，因为在这种情况下，我们必须手动将标头注入每个 URL 端点，这会引入额外的开销。

但是，此技术可用于我们只需要在某些 URL 端点中启用CORS 的应用程序。

## 3. 测试

应用程序启动后，我们可以使用 curl 命令测试标头。示例标头输出应如下所示：

```plaintext
HTTP/1.1 200 OK
Date : Tue, 13 May 2014 12:30:00 GMT
Connection : keep-alive
Access-Control-Allow-Origin : 
Access-Control-Allow-Credentials : true
Access-Control-Allow-Headers : origin, content-type, accept, authorization
Access-Control-Allow-Methods : GET, POST, PUT, DELETE, OPTIONS, HEAD
Transfer-Encoding : chunked
```

更重要的是，我们可以创建一个简单的 AJAX 函数并检查跨域功能：

```javascript
function call(url, type, data) {
    var request = $.ajax({
      url: url,
      method: "GET",
      data: (data) ? JSON.stringify(data) : "",
      dataType: type
    });
 
    request.done(function(resp) {
      console.log(resp);
    });
 
    request.fail(function(jqXHR, textStatus) {
      console.log("Request failed: " + textStatus);
    });
};
```

当然，为了实际执行检查，我们必须在与我们正在使用的 API 不同的源上运行它。

通过在单独的端口上运行客户端应用程序，你可以很容易地在本地完成此操作——因为端口确实确定了来源。

## 4. 总结

在本文中，我们展示了如何在基于 JAX-RS 的应用程序中实现CORS机制。