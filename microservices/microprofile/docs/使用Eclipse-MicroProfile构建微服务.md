## 1. 概述

在本文中，我们将重点介绍如何基于Eclipse MicroProfile构建微服务。

我们将了解如何使用JAX-RS、CDI和JSON-P API编写REST fulWeb应用程序。

## 2. 微服务架构

简单地说，微服务是一种软件架构风格，它作为几个独立服务的集合形成一个完整的系统。

每个服务都专注于一个功能范围，并使用与语言无关的协议(例如REST)与其他服务进行通信。

## 3. Eclipse MicroProfile

Eclipse MicroProfile是一项旨在为微服务架构优化企业Java的计划。它基于Jakarta EE WebProfile API的一个子集，因此我们可以像构建Jakarta EE应用程序一样构建MicroProfile应用程序。

MicroProfile的目标是定义用于构建微服务的标准API，并跨多个MicroProfile运行时交付可移植应用程序。

## 4. Maven依赖

构建Eclipse MicroProfile应用程序所需的所有依赖项均由此BOM依赖项提供：

```xml
<dependency>
    <groupId>org.eclipse.microprofile</groupId>
    <artifactId>microprofile</artifactId>
    <version>1.2</version>
    <type>pom</type>
    <scope>provided</scope>
</dependency>
```

范围设置为provided的，因为MicroProfile运行时已经包含API和实现。

## 5. 表示模型

让我们从创建一个快速资源类开始：

```java
public class Book {
    private String id;
    private String name;
    private String author;
    private Integer pages;
    // ...
}
```

正如我们所看到的，这个Book类没有注解。

## 6. 使用CDI

简单的说，CDI是一个提供依赖注入和生命周期管理的API。它简化了企业bean在Web应用程序中的使用。

现在让我们创建一个CDI托管bean作为书籍表示的存储：

```java
@ApplicationScoped
public class BookManager {

    private ConcurrentMap<String, Book> inMemoryStore = new ConcurrentHashMap<>();

    public String add(Book book) {
        // ...
    }

    public Book get(String id) {
        // ...
    }

    public List getAll() {
        // ...
    }
}
```

我们使用@ApplicationScoped标注这个类，因为我们只需要一个状态由所有客户端共享的实例。为此，我们使用ConcurrentMap作为类型安全的内存数据存储。然后我们添加了用于CRUD操作的方法。

现在我们的bean已准备好CDI，可以使用@Inject注解将其注入到bean BookEndpoint中。

## 7. JAX-RS API

要使用JAX-RS创建REST应用程序，我们需要创建一个用@ApplicationPath标注的Application类和一个用@Path标注的资源。

### 7.1 JAX-RS应用程序

JAX-RS应用程序标识我们在Web应用程序中公开资源的基本URI。

让我们创建以下JAX-RS应用程序：

```java
@ApplicationPath("/library")
public class LibraryApplication extends Application {
}
```

在此示例中，Web应用程序中的所有JAX-RS资源类都与LibraryApplication相关联，使它们位于相同的库路径下，这就是ApplicationPath注解的值。

这个带注解的类告诉JAX-RS运行时它应该自动查找资源并公开它们。

### 7.2 JAX-RS端点

Endpoint类，也称为Resource类，应该定义一种资源，尽管许多相同的类型在技术上是可行的。

每个用@Path标注的Java类，或者至少有一个用@Path或@HttpMethod标注的方法都是一个端点。

现在，我们将创建一个公开该表示的JAX-RS端点：

```java
@Path("books")
@RequestScoped
public class BookEndpoint {

    @Inject
    private BookManager bookManager;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@PathParam("id") String id) {
        return Response.ok(bookManager.get(id)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBooks() {
        return Response.ok(bookManager.getAll()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(Book book) {
        String bookId = bookManager.add(book);
        return Response.created(UriBuilder
                    .fromResource(this.getClass())
                    .path(bookId)
                    .build())
              .build();
    }
}
```

此时，我们可以在Web应用程序中的/library/books路径下访问BookEndpointResource。

### 7.3 JAX-RS JSON媒体类型

JAX-RS支持许多用于与REST客户端通信的媒体类型，但Eclipse MicroProfile限制使用JSON，因为它指定使用JSON-P API。因此，我们需要使用@Consumes(MediaType.APPLICATION_JSON)和@Produces(MediaType.APPLICATION_JSON)来标注我们的方法。

@Consumes注解限制了接受的格式-在这个例子中，只接受JSON数据格式。HTTP请求标头Content-Type应该是application/json。

@Produces注解也是同样的想法。JAX-RS运行时应将响应编组为JSON格式。请求HTTP标头Accept应该是application/json。

## 8. JSON-P

JAX-RS运行时支持开箱即用的JSON-P，因此我们可以使用JsonObject作为方法输入参数或返回类型。

但在现实世界中，我们经常使用POJO类。因此我们需要一种方法来执行JsonObject和POJO之间的映射。这是JAX-RS实体提供者发挥作用的地方。

为了将JSON输入流编组到Book POJO，即调用一个带有Book类型参数的资源方法，我们需要创建一个BookMessageBodyReader类：

```java
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class BookMessageBodyReader implements MessageBodyReader<Book> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(Book.class);
    }

    @Override
    public Book readFrom(
          Class type, Type genericType,
          Annotation[] annotations,
          MediaType mediaType,
          MultivaluedMap<String, String> httpHeaders,
          InputStream entityStream) throws IOException, WebApplicationException {

        return BookMapper.map(entityStream);
    }
}
```

我们执行相同的过程将Book解组为JSON输出流，即通过创建BookMessageBodyWriter调用返回类型为Book的资源方法：

```java
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class BookMessageBodyWriter
      implements MessageBodyWriter<Book> {

    @Override
    public boolean isWriteable(
          Class<?> type, Type genericType,
          Annotation[] annotations,
          MediaType mediaType) {

        return type.equals(Book.class);
    }

    // ...

    @Override
    public void writeTo(
          Book book, Class<?> type,
          Type genericType,
          Annotation[] annotations,
          MediaType mediaType,
          MultivaluedMap<String, Object> httpHeaders,
          OutputStream entityStream) throws IOException, WebApplicationException {

        JsonWriter jsonWriter = Json.createWriter(entityStream);
        JsonObject jsonObject = BookMapper.map(book);
        jsonWriter.writeObject(jsonObject);
        jsonWriter.close();
    }
}
```

由于BookMessageBodyReader和BookMessageBodyWriter使用@Provider进行标注，因此它们由JAX-RS运行时自动注册。

## 9. 构建和运行应用程序

MicroProfile应用程序是可移植的，应该在任何兼容的MicroProfile运行时中运行。我们将解释如何在[Open Liberty](https://openliberty.io/)中构建和运行我们的应用程序，但我们可以使用任何兼容的Eclipse MicroProfile。

我们通过配置文件server.xml配置OpenLiberty运行时：

```xml
<server description="OpenLiberty MicroProfile server">
    <featureManager>
        <feature>jaxrs-2.0</feature>
        <feature>cdi-1.2</feature>
        <feature>jsonp-1.0</feature>
    </featureManager>
    <httpEndpoint httpPort="${default.http.port}" httpsPort="${default.https.port}"
                  id="defaultHttpEndpoint" host="*"/>
    <applicationManager autoExpand="true"/>
    <webApplication context-root="${app.context.root}" location="${app.location}"/>
</server>
```

让我们将插件liberty-maven-plugin添加到我们的pom.xml中：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<plugin>
    <groupId>net.wasdev.wlp.maven.plugins</groupId>
    <artifactId>liberty-maven-plugin</artifactId>
    <version>2.1.2</version>
    <configuration>
        <assemblyArtifact>
            <groupId>io.openliberty</groupId>
            <artifactId>openliberty-runtime</artifactId>
            <version>17.0.0.4</version>
            <type>zip</type>
        </assemblyArtifact>
        <configFile>${basedir}/src/main/liberty/config/server.xml</configFile>
        <packageFile>${package.file}</packageFile>
        <include>${packaging.type}</include>
        <looseApplication>false</looseApplication>
        <installAppPackages>project</installAppPackages>
        <bootstrapProperties>
            <app.context.root>/</app.context.root>
            <app.location>${project.artifactId}-${project.version}.war</app.location>
            <default.http.port>9080</default.http.port>
            <default.https.port>9443</default.https.port>
        </bootstrapProperties>
    </configuration>
    <executions>
        <execution>
            <id>install-server</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>install-server</goal>
                <goal>create-server</goal>
                <goal>install-feature</goal>
            </goals>
        </execution>
        <execution>
            <id>package-server-with-apps</id>
            <phase>package</phase>
            <goals>
                <goal>install-apps</goal>
                <goal>package-server</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

这个插件是可配置的，例如以下属性：

```xml
<properties>
    <!--...-->
    <app.name>library</app.name>
    <package.file>${project.build.directory}/${app.name}-service.jar</package.file>
    <packaging.type>runnable</packaging.type>
</properties>
```

上面的exec目标生成一个可执行的jar文件，这样我们的应用程序将成为一个独立的微服务，可以单独部署和运行。我们也可以将其部署为Docker镜像。

要创建可执行jar，请运行以下命令：

```shell
mvn package
```

为了运行我们的微服务，我们使用以下命令：

```shell
java -jar target/library-service.jar
```

这将启动Open Liberty运行时并部署我们的服务。我们可以访问我们的端点并通过此URL获取所有书籍：

```shell
curl http://localhost:9080/library/books
```

结果是一个JSON：

```json
[
    {
        "id": "0001-201802",
        "isbn": "1",
        "name": "Building Microservice With Eclipse MicroProfile",
        "author": "tuyucheng",
        "pages": 420
    }
]
```

要获得单本书，我们请求以下URL：

```shell
curl http://localhost:9080/library/books/0001-201802
```

结果是JSON：

```json
{
    "id": "0001-201802",
    "isbn": "1",
    "name": "Building Microservice With Eclipse MicroProfile",
    "author": "tuyucheng",
    "pages": 420
}
```

现在我们将通过与API交互来添加一本新书：

```shell
curl 
  -H "Content-Type: application/json" 
  -X POST 
  -d '{"isbn": "22", "name": "Gradle in Action","author": "tuyucheng","pages": 420}' 
  http://localhost:9080/library/books
```

我们可以看到，响应的状态码是201，表示这本书创建成功，Location是我们可以访问的URI：

```shell
< HTTP/1.1 201 Created
< Location: http://localhost:9080/library/books/0009-201802
```

## 10. 总结

本文演示了如何基于Eclipse MicroProfile构建一个简单的微服务，讨论了JAX-RS、JSON-P和CDI。