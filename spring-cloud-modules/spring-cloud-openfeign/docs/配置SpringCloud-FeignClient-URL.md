##  一、简介

在本文中，我们将了解如何将目标 URL 提供给[Feign Client](https://www.baeldung.com/spring-cloud-openfeign)界面。

## 2.概述

为了快速开始，我们将使用来自[JSONPlaceholder网站的](https://jsonplaceholder.typicode.com/)*Albums、Posts*和*Todos*对象的模拟响应。

让我们看看*专辑*类：

```java
public class Album {
    
    private Integer id;
    private Integer userId;
    private String title;
    
   // standard getters and setters
}复制
```

和*邮政*类：

```typescript
public class Post {
    
    private Integer id;
    private Integer userId;
    private String title;
    private String body;
    
    // standard getters and setters
}复制
```

最后，*Todo*类：

```typescript
public class Todo {
    
    private Integer id;
    private Integer userId;
    private String title;
    private Boolean completed;
    
    // standard getters and setters
}
复制
```

## 3.在注解中添加基本URL

**我们可以在客户端界面\*@FeignClient注解的\**url\*属性中设置base URL**。然后，我们将使用相关的 HTTP 动词注释方法并添加所需的端点：

```java
@FeignClient(name = "albumClient", url = "https://jsonplaceholder.typicode.com/albums/")
public interface AlbumClient {
    
    @GetMapping(value = "/{id}")
    Album getAlbumById(@PathVariable(value = "id") Integer id);
}复制
```

让我们添加一个 REST 控制器来测试我们的客户端：

```java
@RestController
public class ConfigureFeignUrlController {
    
    private final AlbumClient albumClient;
    // standard constructor
    
    @GetMapping(value = "albums/{id}")
    public Album getAlbumById(@PathVariable(value = "id") Integer id) {
        return albumClient.getAlbumById(id);
    }
    
   // other controller methods
}复制
```

当目标 URL 在整个应用程序的生命周期中都是静态的时，此选项很有用。

## 4. 使用配置属性

或者，**对于 Spring Cloud 2022.0.1 或更高版本，我们可以使用\*application.properties\*将 URL 提供给 Feign Client 接口**。属性*spring.cloud.openfeign.client.config.<interface-name>.url*用于此。在这里，<interface-name>*是*我们在*@FeignClient注解中提供的**name*属性的值：

```java
@FeignClient(name = "postClient")
public interface PostClient {
    
    @GetMapping(value = "/{id}")
    Post getPostById(@PathVariable(value = "id") Integer id);
}
复制
```

我们将在 application.properties 中添加基本 URL：

```yaml
spring.cloud.openfeign.client.config.postClient.url=https://jsonplaceholder.typicode.com/posts/复制
```

**对于低于 2022.0.1 的 Spring Cloud 版本，我们可以将\*url\*属性设置为\*@FeignClient以从\**application.properties\*****中读取值**：

```java
@FeignClient(name = "postClient", url = "${spring.cloud.openfeign.client.config.postClient.url}")复制
```

接下来，让我们将这个客户端注入到我们之前创建的控制器中：

```java
@RestController
public class FeignClientController {
    private final PostClient postClient;
    
    // other attributes and standard constructor
    
    @GetMapping(value = "posts/{id}")
    public Post getPostById(@PathVariable(value = "id") Integer id) {
        return postClient.getPostById(id);
    }
    
   // other controller methods
}复制
```

如果目标 URL 根据应用程序的环境而变化，则此选项很有用。例如，我们可能将模拟服务器用于开发环境，将实际服务器用于生产环境。

## 5.使用*请求线*

Spring Cloud 提供了一个特性，我们可以在其中覆盖目标 URL 或在运行时直接提供 URL。这是通过**使用\*@RequestLine\*注释并使用 Feign Builder API 手动创建 Feign 客户端来注册客户端来**实现的：

```java
@FeignClient(name = "todoClient")
public interface TodoClient {
    
    @RequestLine(value = "GET")
    Todo getTodoById(URI uri);
}复制
```

我们需要在我们的控制器中手动创建这个假客户端：

```java
@RestController
@Import(FeignClientsConfiguration.class)
public class FeignClientController {
    
    private final TodoClient todoClient;
    
    // other variables
    
    public FeignClientController(Decoder decoder, Encoder encoder) {
        this.todoClient = Feign.builder().encoder(encoder).decoder(decoder).target(Target.EmptyTarget.create(TodoClient.class));
        // other initialisation
   }
    
    @GetMapping(value = "todo/{id}")
    public Todo getTodoById(@PathVariable(value = "id") Integer id) {
        return todoClient.getTodoById(URI.create("https://jsonplaceholder.typicode.com/todos/" + id));
    }
    
    // other controller methods
}复制
```

这里，我们首先通过FeignClientsConfiguration.class导入默认的feign客户端配置*。*Feign.Builder用于为 API 接口自定义这些属性*。**我们可以配置encoder*、*decoder*、*connectTimeout*、*readTimeout*、 authentication 等属性。

target属性定义了这些属性将应用于哪个接口*。*这个接口有两个实现：我们在这里使用的*EmptyTarget和**HardCodedTarget*。EmptyTarget类在编译*时* 不需要 URL，而*HardCodedTarget* 需要。

值得注意的是，作为参数提供的 URI 参数将覆盖*@feignClient* 注释中提供的 URL 和配置属性中的 URL。*同样， @FeignClient* 注释中提供的 URL将覆盖配置属性中提供的 URL。

## 6. 使用*请求拦截器*

或者，另一种在运行时提供目标 URL 的方法是向Feign.Builder 提供自定义*RequestInterceptor* *。* 在这里，我们将覆盖 *RestTemplate* 的*target* 属性，以将 URL 更新为通过*requestInterceptor提供给**Feign.Builder 的*URL ：

```java
public class DynamicUrlInterceptor implements RequestInterceptor {
    private final Supplier<String> urlSupplier;
    // standard constructor

    @Override
    public void apply(RequestTemplate template) {
        String url = urlSupplier.get();
        if (url != null) {
            template.target(url);
        }
    }
}复制
```

让我们向*AlbumClient.java 添加另一个方法：*

```java
@GetMapping(value = "/{id}")
Album getAlbumByIdAndDynamicUrl(@PathVariable(name = "id") Integer id);复制
```

*我们不会在构造函数中使用 Builder，而是在ConfigureFeignUrlController*的方法中使用它 来创建 AlbumClient 的实例*：*

```java
@RestController
@Import(FeignClientsConfiguration.class)
public class ConfigureFeignUrlController {
    
    private final ObjectFactory<HttpMessageConverters> messageConverters;
    
    private final ObjectProvider<HttpMessageConverterCustomizer> customizers;
    
    // other variables, standard constructor and other APIs
    
    @GetMapping(value = "/dynamicAlbums/{id}")
    public Album getAlbumByIdAndDynamicUrl(@PathVariable(value = "id") Integer id) {
        AlbumClient client = Feign.builder()
          .requestInterceptor(new DynamicUrlInterceptor(() -> "https://jsonplaceholder.typicode.com/albums/"))
          .contract(new SpringMvcContract())
          .encoder(new SpringEncoder(messageConverters))
          .decoder(new SpringDecoder(messageConverters, customizers))
          .target(Target.EmptyTarget.create(AlbumClient.class));
     
        return client.getAlbumByIdAndDynamicUrl(id);
    }
}复制
```

在这里，我们添加了上面创建的*DynamicUrlInterceptor*，它接受一个 URL 来覆盖*AlbumClient*的默认 URL。我们还将客户端配置为使用*SpringMvcContract、SpringEncoder*和*SpringDecoder。*

[当我们需要在我们的应用程序中提供对webhook](https://www.baeldung.com/cs/webhooks-polling-real-time-information)的支持时，最后两个选项将很有用，因为目标 URL 会因每个客户端而异。

## 七、结论

在本文中，我们了解了如何以不同方式为 Feign Client 接口配置目标 URL。