## 1. 概述

本文将重点关注Spring REST服务中可发现性的实现以及满足HATEOAS约束。

本文主要关于Spring MVC。我们的文章[Spring HATEOAS简介](https://www.baeldung.com/spring-hateoas-tutorial)描述了如何在Spring Boot中使用HATEOAS。

## 2. 通过事件解耦可发现性

可发现性作为Web层的一个单独方面或关注点应该与处理HTTP请求的控制器分离。为此，控制器将为需要对响应进行额外操作的所有操作触发事件。

首先，让我们创建事件：

```java
public class SingleResourceRetrieved extends ApplicationEvent {
    private HttpServletResponse response;

    public SingleResourceRetrieved(Object source, HttpServletResponse response) {
        super(source);

        this.response = response;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}

public class ResourceCreated extends ApplicationEvent {
    private HttpServletResponse response;
    private long idOfNewResource;

    public ResourceCreated(Object source, HttpServletResponse response, long idOfNewResource) {
        super(source);

        this.response = response;
        this.idOfNewResource = idOfNewResource;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public long getIdOfNewResource() {
        return idOfNewResource;
    }
}
```

然后，控制器包含两个简单的操作-findById和create：

```java
@RestController
@RequestMapping(value = "/foos")
public class FooController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private IFooService service;

    @GetMapping(value = "foos/{id}")
    public Foo findById(@PathVariable("id") Long id, HttpServletResponse response) {
        Foo resourceById = Preconditions.checkNotNull(service.findOne(id));

        eventPublisher.publishEvent(new SingleResourceRetrieved(this, response));
        return resourceById;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Foo resource, HttpServletResponse response) {
        Preconditions.checkNotNull(resource);
        Long newId = service.create(resource).getId();

        eventPublisher.publishEvent(new ResourceCreated(this, response, newId));
    }
}
```

然后我们可以使用任意数量的解耦监听器来处理这些事件。其中每一个都可以专注于自己的特定情况，并有助于满足整体HATEOAS约束。

监听器应该是调用栈中的最后一个对象，不需要直接访问它们；因此，它们不是公开的。

## 3. 使新创建的资源的URI可发现

正如[之前关于HATEOAS的文章](https://www.baeldung.com/restful-web-service-discoverability)中所讨论的，创建新资源的操作应该在响应的Location HTTP标头中返回该资源的URI。

我们将使用监听器处理此问题：

```java
@Component
class ResourceCreatedDiscoverabilityListener implements ApplicationListener<ResourceCreated>{

    @Override
    public void onApplicationEvent(ResourceCreated resourceCreatedEvent){
        Preconditions.checkNotNull(resourceCreatedEvent);

        HttpServletResponse response = resourceCreatedEvent.getResponse();
        long idOfNewResource = resourceCreatedEvent.getIdOfNewResource();

        addLinkHeaderOnResourceCreation(response, idOfNewResource);
    }

    void addLinkHeaderOnResourceCreation(HttpServletResponse response, long idOfNewResource){
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().
              path("/{idOfNewResource}").buildAndExpand(idOfNewResource).toUri();
        response.setHeader("Location", uri.toASCIIString());
    }
}
```

在这个例子中，我们使用了ServletUriComponentsBuilder-这有助于使用当前的请求。这样，我们不需要传递任何东西，我们可以简单地静态访问它。

如果API返回ResponseEntity-我们也可以使用[Location支持](https://github.com/spring-projects/spring-framework/issues/12675)。

## 4. 获取单个资源

在检索单个资源时，客户端应该能够发现URI以获取该类型的所有资源：

```java
@Component
class SingleResourceRetrievedDiscoverabilityListener implements ApplicationListener<SingleResourceRetrieved>{

    @Override
    public void onApplicationEvent(SingleResourceRetrieved resourceRetrievedEvent){
        Preconditions.checkNotNull(resourceRetrievedEvent);

        HttpServletResponse response = resourceRetrievedEvent.getResponse();
        addLinkHeaderOnSingleResourceRetrieval(request, response);
    }

    void addLinkHeaderOnSingleResourceRetrieval(HttpServletResponse response){
        String requestURL = ServletUriComponentsBuilder.fromCurrentRequestUri().
              build().toUri().toASCIIString();
        int positionOfLastSlash = requestURL.lastIndexOf("/");
        String uriForResourceCreation = requestURL.substring(0, positionOfLastSlash);

        String linkHeaderValue = LinkUtil
              .createLinkHeader(uriForResourceCreation, "collection");
        response.addHeader(LINK_HEADER, linkHeaderValue);
    }
}
```

请注意，链接关系的语义使用“collection”关系类型，它在[多种微格式](http://microformats.org/wiki/existing-rel-values#non_HTML_rel_values)中指定和使用，但尚未标准化。

Link标头是用于可发现性的最常用的HTTP标头之一。创建此标头的实用程序非常简单：

```java
public class LinkUtil {
    public static String createLinkHeader(String uri, String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
```

## 5. 根部的可发现性

根是整个服务的入口点-这是客户端在第一次使用API时接触到的。

如果要在整个过程中考虑并实施HATEOAS约束，那么这就是起点。因此，系统的所有主要URI都必须可以从根目录中发现。

现在让我们看一下控制器：

```java
@GetMapping("/")
@ResponseStatus(value = HttpStatus.NO_CONTENT)
public void adminRoot(final HttpServletRequest request, final HttpServletResponse response) {
    String rootUri = request.getRequestURL().toString();

    URI fooUri = new UriTemplate("{rootUri}{resource}").expand(rootUri, "foos");
    String linkToFoos = LinkUtil.createLinkHeader(fooUri.toASCIIString(), "collection");
    response.addHeader("Link", linkToFoos);
}
```

当然，这是对概念的说明，重点是Foo资源的单个示例URI。类似地，真正的实现应该为发布给客户端的所有资源添加URI。

### 5.1 可发现性与更改URI无关

这可能是一个有争议的点-一方面，HATEOAS的目的是让客户端发现API的URI，而不是依赖于硬编码值。另一方面-这不是Web的工作方式：是的，URI被发现了，但它们也被添加了书签。

一个微妙但重要的区别是API的演变-旧的URI应该仍然有效，但是任何发现API的客户端都应该发现新的URI，这允许API动态变化，即使API发生变化，好的客户端也能很好地工作。

总之-仅仅因为RESTful Web服务的所有URI都应被视为[酷](https://www.w3.org/TR/cooluris/)[URI](https://www.w3.org/TR/cooluris/)(并且酷URI[不会改变](https://www.w3.org/Provider/Style/URI.html))——这并不意味着在发展API时遵守HATEOAS约束不是非常有用[。](https://www.w3.org/TR/cooluris/)

## 6. 可发现性警告

正如之前文章的一些讨论所述，可发现性的首要目标是尽量减少或不使用文档，并让客户端通过获得的响应学习和理解如何使用API。

事实上，这不应该被视为如此遥不可及的理想-这就是我们在没有任何文档的情况下使用每个新网页的方式。所以，如果这个概念在REST的上下文中更有问题，那么它一定是技术实现的问题，而不是它是否可能的问题。

话虽这么说，从技术上讲，我们离一个完全可行的解决方案还很远-规范和框架支持仍在不断发展，因此，我们必须做出一些妥协。

## 7. 总结

本文介绍了在使用Spring MVC的RESTful服务的上下文中可发现性的一些特征的实现，并从根本上触及了可发现性的概念。