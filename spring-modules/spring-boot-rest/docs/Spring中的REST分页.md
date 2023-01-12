## 1. 概述

本教程将重点介绍使用 Spring MVC 和 Spring Data 在 REST API 中实现分页。

## 延伸阅读：

## [使用 Spring REST 和 AngularJS 表进行分页](https://www.baeldung.com/pagination-with-a-spring-rest-api-and-an-angularjs-table)

广泛了解如何使用 Spring 实现带有分页的简单 API，以及如何使用 AngularJS 和 UI Grid 使用它。

[阅读更多](https://www.baeldung.com/pagination-with-a-spring-rest-api-and-an-angularjs-table)→

## [JPA分页](https://www.baeldung.com/jpa-pagination)

JPA 中的分页 - 如何使用 JQL 和 Criteria API 正确地进行分页。

[阅读更多](https://www.baeldung.com/jpa-pagination)→

## [REST API 可发现性和 HATEOAS](https://www.baeldung.com/restful-web-service-discoverability)

HATEOAS 和 REST 服务的可发现性 - 由测试驱动。

[阅读更多](https://www.baeldung.com/restful-web-service-discoverability)→

## 2. 页面作为资源 vs 页面作为表现

在 RESTful 架构的上下文中设计分页时，第一个问题是将页面视为实际资源还是只是资源的表示。

将页面本身视为一种资源会引入许多问题，例如不再能够在调用之间唯一地标识资源。这一点，再加上在持久层中，页面不是一个适当的实体，而是一个在必要时构建的持有者，使得选择变得简单明了；该页面是表示的一部分。

REST 上下文中分页设计的下一个问题是在何处包含分页信息：

-   在 URI 路径中：/foo/page/1
-   URI 查询：/foo?page=1

请记住，页面不是 Resource，在 URI 中编码页面信息不是一个选项。

我们将通过在 URI 查询中编码分页信息来使用解决此问题的标准方法。

## 3. 控制器

现在开始实施。用于分页的 Spring MVC 控制器很简单：

```java
@GetMapping(params = { "page", "size" })
public List<Foo> findPaginated(@RequestParam("page") int page, 
  @RequestParam("size") int size, UriComponentsBuilder uriBuilder,
  HttpServletResponse response) {
    Page<Foo> resultPage = service.findPaginated(page, size);
    if (page > resultPage.getTotalPages()) {
        throw new MyResourceNotFoundException();
    }
    eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Foo>(
      Foo.class, uriBuilder, response, page, resultPage.getTotalPages(), size));

    return resultPage.getContent();
}
```

在这个例子中，我们 通过@RequestParam在 Controller 方法中注入两个查询参数， size和page 。

或者，我们可以使用 Pageable对象，它会自动映射page、 size和 sort参数。此外，PagingAndSortingRepository实体提供开箱即用的方法，支持使用Pageable作为参数。

我们还注入了 Http Response 和UriComponentsBuilder以帮助实现 Discoverability，我们通过自定义事件将其解耦。如果这不是 API 的目标，我们可以简单地删除自定义事件。

最后，注意本文的重点只是 REST 和 web 层；要更深入地了解分页的数据访问部分，我们可以[查看这篇](http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-part-seven-pagination/)关于使用 Spring Data 进行分页的文章。

## 4. REST 分页的可发现性

在分页范围内，满足REST 的 HATEOAS 约束意味着使 API 的客户端能够根据导航中的当前页面发现下一页和上一页。为此，我们将使用Link HTTP 标头，以及“ next”、 “ prev”、 “ first ”和“ last ”链接关系类型。

在 REST 中，可发现性是一个横切关注点，不仅适用于特定操作，还适用于操作类型。例如，每次创建资源时，客户端应该可以发现该资源的 URI。由于此要求与 ANY Resource 的创建相关，因此我们将单独处理。

正如我们在[上一篇关注](https://www.baeldung.com/rest-api-discoverability-with-spring)REST 服务的可发现性的文章中所讨论的那样，我们将使用事件来解决这些问题。在分页的情况下，事件PaginatedResultsRetrievedEvent在控制器层中触发。然后我们将使用此事件的自定义侦听器实现可发现性。

简而言之，侦听器将检查导航是否允许下一页、 上一页、 第一页 和 最后一页。如果是，它会将相关的 URI 作为“链接”HTTP 标头添加到响应中。

现在让我们一步一步来。从控制器传递的UriComponentsBuilder仅包含基本 URL(主机、端口和上下文路径)。因此，我们必须添加其余部分：

```java
void addLinkHeaderOnPagedResourceRetrieval(
 UriComponentsBuilder uriBuilder, HttpServletResponse response,
 Class clazz, int page, int totalPages, int size ){

   String resourceName = clazz.getSimpleName().toString().toLowerCase();
   uriBuilder.path( "/admin/" + resourceName );

    // ...
   
}
```

接下来，我们将使用StringJoiner 连接每个链接。我们将使用uriBuilder生成 URI。让我们看看我们如何处理到下一页的链接：

```java
StringJoiner linkHeader = new StringJoiner(", ");
if (hasNextPage(page, totalPages)){
    String uriForNextPage = constructNextPageUri(uriBuilder, page, size);
    linkHeader.add(createLinkHeader(uriForNextPage, "next"));
}
```

我们来看看 constructNextPageUri方法的逻辑：

```java
String constructNextPageUri(UriComponentsBuilder uriBuilder, int page, int size) {
    return uriBuilder.replaceQueryParam(PAGE, page + 1)
      .replaceQueryParam("size", size)
      .build()
      .encode()
      .toUriString();
}
```

对于我们想要包含的其余 URI，我们将以类似的方式进行。

最后，我们将输出添加为响应标头：

```java
response.addHeader("Link", linkHeader.toString());
```

请注意，为简洁起见，仅包含部分代码示例，[完整代码在此处](https://gist.github.com/1622997)。

## 5. 试驾分页

分页和可发现性的主要逻辑都包含在小型、集中的集成测试中。与[上一篇文章](https://www.baeldung.com/restful-web-service-discoverability)一样，我们将使用 [REST-assured 库](https://github.com/rest-assured/rest-assured)来使用 REST 服务并验证结果。

这些是分页集成测试的几个例子；如需完整的测试套件，请查看 GitHub 项目(文章末尾的链接)：

```java
@Test
public void whenResourcesAreRetrievedPaged_then200IsReceived(){
    Response response = RestAssured.get(paths.getFooURL() + "?page=0&size=2");

    assertThat(response.getStatusCode(), is(200));
}
@Test
public void whenPageOfResourcesAreRetrievedOutOfBounds_then404IsReceived(){
    String url = getFooURL() + "?page=" + randomNumeric(5) + "&size=2";
    Response response = RestAssured.get.get(url);

    assertThat(response.getStatusCode(), is(404));
}
@Test
public void givenResourcesExist_whenFirstPageIsRetrieved_thenPageContainsResources(){
   createResource();
   Response response = RestAssured.get(paths.getFooURL() + "?page=0&size=2");

   assertFalse(response.body().as(List.class).isEmpty());
}
```

## 6. 测试分页可发现性

测试分页是否可被客户端发现相对简单，尽管有很多内容需要涵盖。

测试将关注当前页面在导航中的位置，以及应该从每个位置发现的不同 URI：

```java
@Test
public void whenFirstPageOfResourcesAreRetrieved_thenSecondPageIsNext(){
   Response response = RestAssured.get(getFooURL()+"?page=0&size=2");

   String uriToNextPage = extractURIByRel(response.getHeader("Link"), "next");
   assertEquals(getFooURL()+"?page=1&size=2", uriToNextPage);
}
@Test
public void whenFirstPageOfResourcesAreRetrieved_thenNoPreviousPage(){
   Response response = RestAssured.get(getFooURL()+"?page=0&size=2");

   String uriToPrevPage = extractURIByRel(response.getHeader("Link"), "prev");
   assertNull(uriToPrevPage );
}
@Test
public void whenSecondPageOfResourcesAreRetrieved_thenFirstPageIsPrevious(){
   Response response = RestAssured.get(getFooURL()+"?page=1&size=2");

   String uriToPrevPage = extractURIByRel(response.getHeader("Link"), "prev");
   assertEquals(getFooURL()+"?page=0&size=2", uriToPrevPage);
}
@Test
public void whenLastPageOfResourcesIsRetrieved_thenNoNextPageIsDiscoverable(){
   Response first = RestAssured.get(getFooURL()+"?page=0&size=2");
   String uriToLastPage = extractURIByRel(first.getHeader("Link"), "last");

   Response response = RestAssured.get(uriToLastPage);

   String uriToNextPage = extractURIByRel(response.getHeader("Link"), "next");
   assertNull(uriToNextPage);
}
```

请注意，负责通过rel关系提取 URI 的extractURIByRel的完整低级代码[位于此处](https://gist.github.com/eugenp/8269915)。

## 7.获取所有资源

关于分页和可发现性的相同主题，如果允许客户端一次检索系统中的所有资源，或者如果客户端必须请求它们分页，则必须做出选择。

如果确定客户端无法通过单个请求检索所有资源，并且需要分页，则可以使用多个选项来响应获取请求。一个选项是返回 404(未找到)并使用Link标头使第一页可发现：

>   链接=<http://localhost:8080/rest/api/admin/foo?page=0&size=2>; rel=”first”, <http://localhost:8080/rest/api/admin/foo?page=103&size=2>; rel =“最后”

另一种选择是将重定向 303 (参见其他)返回到第一页。更保守的方法是简单地为 GET 请求返回一个 405(方法不允许)给客户端。

## 8. 使用范围HTTP 标头的 REST 分页

实现分页的一种相对不同的方法是使用HTTP Range标头、 Range、Content-Range、If-Range、Accept-Ranges和HTTP 状态代码206(部分内容)、413(请求实体太大)和416(请求的范围不可满足)。

这种方法的一种观点是 HTTP 范围扩展不用于分页，它们应该由服务器管理，而不是由应用程序管理。基于 HTTP Range 标头扩展实现分页在技术上是可行的，尽管不如本文讨论的实现那么普遍。

## 9. Spring Data REST 分页

在 Spring Data 中，如果我们需要从完整的数据集中返回一些结果，我们可以使用任何Pageable存储库方法，因为它总是会返回一个页面。将根据页码、页面大小和排序方向返回结果。

[Spring Data REST](https://www.baeldung.com/spring-data-rest-intro)自动识别页面、大小、排序等URL 参数。

要使用任何存储库的分页方法，我们需要扩展PagingAndSortingRepository：

```java
public interface SubjectRepository extends PagingAndSortingRepository<Subject, Long>{}
```

如果我们调用 http://localhost:8080/subjects， Spring 会自动通过 API添加页面、大小、排序参数建议：

```java
"_links" : {
  "self" : {
    "href" : "http://localhost:8080/subjects{?page,size,sort}",
    "templated" : true
  }
}
```

默认情况下，页面大小为 20，但我们可以通过调用http://localhost:8080/subjects?page=10 来更改它。

如果我们想在我们自己的自定义存储库 API 中实现分页，我们需要传递一个额外的Pageable参数并确保 API 返回一个页面：

```java
@RestResource(path = "nameContains")
public Page<Subject> findByNameContaining(@Param("name") String name, Pageable p);
```

每当我们添加自定义 API 时，/search端点都会添加到生成的链接中。因此，如果我们调用 http://localhost:8080/subjects/search，我们将看到一个支持分页的端点：

```java
"findByNameContaining" : {
  "href" : "http://localhost:8080/subjects/search/nameContains{?name,page,size,sort}",
  "templated" : true
}
```

所有实现 PagingAndSortingRepository的 API 都将返回一个页面。如果我们需要从页面返回结果列表，页面的getContent() API 会提供作为 Spring Data REST API 结果获取的记录列表。

## 10. 将列表转换为页面

假设我们有一个Pageable对象作为输入，但我们需要检索的信息包含在列表中而不是PagingAndSortingRepository中。在这些情况下，我们可能需要将List转换为Page。

例如，假设我们有一个来自[SOAP](https://www.baeldung.com/spring-boot-soap-web-service)服务的结果列表：

```java
List<Foo> list = getListOfFooFromSoapService();
```

我们需要访问发送给我们的Pageable对象指定的特定位置的列表。所以让我们定义起始索引：

```java
int start = (int) pageable.getOffset();
```

和结束索引：

```java
int end = (int) ((start + pageable.getPageSize()) > fooList.size() ? fooList.size()
  : (start + pageable.getPageSize()));
```

有了这两个，我们可以创建一个页面来获取它们之间的元素列表：

```java
Page<Foo> page 
  = new PageImpl<Foo>(fooList.subList(start, end), pageable, fooList.size());
```

就是这样！我们现在可以将页面作为有效结果返回。

请注意，如果我们还想支持[sorting](https://www.baeldung.com/java-8-sort-lambda)，我们需要在子列表之前对列表进行排序。

## 11.总结

本文说明了如何使用 Spring 在 REST API 中实现分页，并讨论了如何设置和测试 Discoverability。

如果我们想深入了解持久层的分页，我们可以查看[JPA](https://www.baeldung.com/jpa-pagination)或[Hibernate](https://www.baeldung.com/hibernate-pagination)分页教程。