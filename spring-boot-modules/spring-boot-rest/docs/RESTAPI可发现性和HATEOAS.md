## 1. 概述

本文将重点介绍REST API、HATEOAS 的可发现性和测试驱动的实际场景。

## 2. 为什么要使 API 可发现

API 的可发现性是一个没有得到足够重视的话题。因此，很少有 API 能做到这一点。如果处理得当，它还可以使 API 不仅具有 REST 风格和可用性，而且还很优雅。

要了解可发现性，我们需要了解超媒体作为应用程序状态引擎 (HATEOAS) 约束。REST API 的这种约束是关于来自超媒体(实际上是超文本)的资源上的操作/转换的完全可发现性，作为应用程序状态的[唯一驱动](http://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven)程序。

如果交互是由 API 通过对话本身驱动的，具体是通过超文本，那么就没有文档。这将迫使客户端做出实际上超出 API 上下文的假设。

总之，服务器应该具有足够的描述性，以指导客户端如何仅通过超文本使用 API。在 HTTP 对话的情况下，我们可以通过Link标头实现这一点。

## 3. 可发现性场景(由测试驱动)

那么 REST 服务可被发现意味着什么？

在本节中，我们将使用 Junit、[rest-assured](https://github.com/rest-assured/rest-assured)和[Hamcrest](https://code.google.com/archive/p/hamcrest/)测试可发现性的各个特征。由于[REST 服务之前已得到保护](https://www.baeldung.com/securing-a-restful-web-service-with-spring-security)，因此每个测试在使用 API 之前首先需要进行[身份验证。](https://gist.github.com/1341570)

### 3.1. 发现有效的 HTTP 方法

当使用无效的 HTTP 方法使用 REST 服务时，响应应该是 405 METHOD NOT ALLOWED。

API 还应该帮助客户端发现特定资源允许的有效 HTTP 方法。为此，我们可以在响应中使用允许HTTP 标头：

```java
@Test
public void
  whenInvalidPOSTIsSentToValidURIOfResource_thenAllowHeaderListsTheAllowedActions(){
    // Given
    String uriOfExistingResource = restTemplate.createResource();

    // When
    Response res = givenAuth().post(uriOfExistingResource);

    // Then
    String allowHeader = res.getHeader(HttpHeaders.ALLOW);
    assertThat( allowHeader, AnyOf.anyOf(
      containsString("GET"), containsString("PUT"), containsString("DELETE") ) );
}
```

### 3.2. 发现新创建资源的 URI

创建新资源的操作应始终在响应中包含新创建资源的 URI。为此，我们可以使用位置HTTP 标头。

现在，如果客户端在该 URI 上执行 GET，资源应该可用：

```java
@Test
public void whenResourceIsCreated_thenUriOfTheNewlyCreatedResourceIsDiscoverable() {
    // When
    Foo newResource = new Foo(randomAlphabetic(6));
    Response createResp = givenAuth().contentType("application/json")
      .body(unpersistedResource).post(getFooURL());
    String uriOfNewResource= createResp.getHeader(HttpHeaders.LOCATION);

    // Then
    Response response = givenAuth().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .get(uriOfNewResource);

    Foo resourceFromServer = response.body().as(Foo.class);
    assertThat(newResource, equalTo(resourceFromServer));
}
```

测试遵循一个简单的场景：创建一个新的 Foo资源，然后使用 HTTP 响应来发现资源现在可用的 URI。然后它还在该 URI 上执行 GET 以检索资源并将其与原始资源进行比较。这是为了确保它已正确保存。

### 3.3. 发现 URI 以获取该类型的所有资源

当我们获取任何特定的Foo资源时，我们应该能够发现下一步可以做什么：我们可以列出所有可用的Foo资源。因此，检索资源的操作应始终在其响应中包含获取该类型所有资源的 URI。

为此，我们可以再次使用Link标头：

```java
@Test
public void whenResourceIsRetrieved_thenUriToGetAllResourcesIsDiscoverable() {
    // Given
    String uriOfExistingResource = createAsUri();

    // When
    Response getResponse = givenAuth().get(uriOfExistingResource);

    // Then
    String uriToAllResources = HTTPLinkHeaderUtil
      .extractURIByRel(getResponse.getHeader("Link"), "collection");

    Response getAllResponse = givenAuth().get(uriToAllResources);
    assertThat(getAllResponse.getStatusCode(), is(200));
}
```

[请注意，此处显示了](https://gist.github.com/eugenp/8269915)extractURIByRel的完整低级代码——负责通过rel关系提取 URI 。

该测试涵盖了 REST 中链接关系的棘手主题：检索所有资源的 URI 使用rel=”collection”语义。

这种类型的链接关系尚未标准化，但已被多种微格式[使用](http://microformats.org/wiki/existing-rel-values#non_HTML_rel_values)并提议标准化。非标准链接关系的使用开启了关于 RESTful Web 服务中微格式和更丰富语义的讨论。

## 4. 其他潜在的可发现 URI 和微格式

其他 URI 可能会通过Link标头发现，但是现有类型的链接关系仅允许这么多，而无需移动到更丰富的语义标记，例如[定义自定义链接关系](https://tools.ietf.org/html/rfc5988#section-6.2.1)、[Atom 发布协议](https://datatracker.ietf.org/doc/html/rfc5023)或[微格式](https://en.wikipedia.org/wiki/Microformat)，这将是主题另一篇文章。

例如，当对特定资源执行GET时，客户端应该能够发现 URI 以创建新资源。不幸的是，模型创建语义没有链接关系。

幸运的是，标准做法是用于创建的 URI 与用于获取该类型所有资源的 URI 相同，唯一的区别在于 POST HTTP 方法。

## 5.总结

我们已经看到REST API 是如何从根目录中完全被发现的，并且无需先验知识——这意味着客户端能够通过在根目录上执行 GET 来导航它。展望未来，所有状态更改都由客户端使用 REST API 在表示中提供的可用且可发现的转换(因此是Representational State Transfer)来驱动。

本文涵盖了 REST Web 服务上下文中可发现性的一些特征，讨论了 HTTP 方法发现、创建和获取之间的关系、获取所有资源的 URI 的发现等。