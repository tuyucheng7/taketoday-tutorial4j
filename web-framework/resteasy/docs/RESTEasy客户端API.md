## 1. 简介

在[上一篇文章中](https://www.baeldung.com/resteasy-tutorial)，我们重点介绍了JAX-RS 2.0的RESTEasy服务器端实现。

JAX-RS 2.0引入了一个新的客户端 API，以便你可以向远程 RESTful Web 服务发出 HTTP 请求。Jersey、Apache CXF、Restlet 和 RESTEasy 只是最流行实现的一部分。

在本文中，我们将探索如何通过使用RESTEasy API发送请求来使用REST API。

## 2.项目设置

在你的pom.xml中添加以下依赖项：

```xml
<properties>
    <resteasy.version>4.7.2.Final</resteasy.version>
</properties>
<dependencies>
    <dependency>
        <groupId>org.jboss.resteasy</groupId>
        <artifactId>resteasy-client</artifactId>
        <version>${resteasy.version}</version>
    </dependency>

    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>4.0.1</version>
    </dependency>
    ...
</dependencies>
```

## 3.客户端代码

客户端实现非常小，由 3 个主要类组成：

-   -   客户
    -   网络目标
    -   回复

客户端接口是WebTarget实例的构建器。

WebTarget表示一个独特的 URL 或 URL 模板，你可以从中构建更多子资源 WebTargets 或调用请求。

实际上有两种创建客户端的方法：

-   标准方法是使用org.jboss.resteasy.client.ClientRequest
-   RESTeasy 代理框架：通过使用ResteasyClientBuilder类

我们将在这里重点介绍 RESTEasy 代理框架。

客户端框架不使用 JAX-RS 注解将传入请求映射到你的 RESTFul Web 服务方法，而是构建一个 HTTP 请求，用于调用远程 RESTful Web 服务。

因此，让我们开始编写Java接口并在方法和接口上使用 JAX-RS 注解。

### 3.1. 服务客户端接口

```java
@Path("/movies")
public interface ServicesInterface {

    @GET
    @Path("/getinfo")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Movie movieByImdbId(@QueryParam("imdbId") String imdbId);

    @POST
    @Path("/addmovie")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Response addMovie(Movie movie);

    @PUT
    @Path("/updatemovie")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Response updateMovie(Movie movie);

    @DELETE
    @Path("/deletemovie")
    Response deleteMovie(@QueryParam("imdbId") String imdbId);
}

```

### 3.2. 电影课

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "movie", propOrder = { "imdbId", "title" })
public class Movie {

    protected String imdbId;
    protected String title;

    // getters and setters
}
```

### 3.3. 请求创建

我们现在将生成一个代理客户端，我们可以使用它来使用 API：

```java
String transformerImdbId = "tt0418279";
Movie transformerMovie = new Movie("tt0418279", "Transformer 2");
UriBuilder FULL_PATH = UriBuilder.fromPath("http://127.0.0.1:8082/resteasy/rest");
 
ResteasyClient client = (ResteasyClient)ClientBuilder.newClient();
ResteasyWebTarget target = client.target(FULL_PATH);
ServicesInterface proxy = target.proxy(ServicesInterface.class);

// POST
Response moviesResponse = proxy.addMovie(transformerMovie);
System.out.println("HTTP code: " + moviesResponse.getStatus());
moviesResponse.close();

// GET
Movie movies = proxy.movieByImdbId(transformerImdbId);

// PUT
transformerMovie.setTitle("Transformer 4");
moviesResponse = proxy.updateMovie(transformerMovie);
moviesResponse.close();

// DELETE
moviesResponse = proxy.deleteMovie(batmanMovie.getImdbId());
moviesResponse.close();

```

请注意，RESTEasy 客户端 API 是基于 Apache HttpClient的。

另请注意，在每次操作之后，我们需要先关闭响应，然后才能执行新操作。这是必要的，因为默认情况下，客户端只有一个可用的 HTTP 连接。

最后，请注意我们是如何直接使用 DTO 的——我们没有处理JSON或XML之间的编组/解组逻辑；这发生在幕后使用JAXB或Jackson，因为Movie类被正确注解了。

### 3.4. 使用连接池创建请求

上一个示例中的一个注意事项是我们只有一个可用连接。如果——例如，我们尝试做：

```java
Response batmanResponse = proxy.addMovie(batmanMovie);
Response transformerResponse = proxy.addMovie(transformerMovie);

```

不在batmanResponse上调用close() ——执行第二行时将抛出异常：

```java
java.lang.IllegalStateException:
Invalid use of BasicClientConnManager: connection still allocated.
Make sure to release the connection before allocating another one.

```

同样——这仅仅是因为RESTEasy 使用的默认HttpClient是org.apache.http.impl.conn.SingleClientConnManager——这当然只使单个连接可用。

现在——为了解决这个限制——必须以不同的方式创建RestEasyClient实例(使用连接池)：

```java
PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
cm.setMaxTotal(200); // Increase max total connection to 200
cm.setDefaultMaxPerRoute(20); // Increase default max connection per route to 20
ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient);

ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
ResteasyWebTarget target = client.target(FULL_PATH);
ServicesInterface proxy = target.proxy(ServicesInterface.class);
```

现在我们可以从适当的连接池中受益，并且可以通过我们的客户端运行多个请求，而不必每次都释放连接。

## 4. 总结

在本快速教程中，我们介绍了RESTEasy 代理框架，并使用它构建了一个超级简单的客户端 API。

该框架为我们提供了更多的帮助方法来配置客户端，并且可以定义为与 JAX-RS 服务器端规范相反的镜像。