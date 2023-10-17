## 一、简介

在本文中，我们将探讨分页对于检索信息的重要性，将 Spring Data Reactive 分页与 Spring Data 进行比较，并通过示例演示如何实现分页。

## 2. 分页的意义

在处理返回大量资源集合的端点时，分页是一个基本概念。它通过将数据分解为更小的、可管理的块（称为“页面”）来实现数据的高效检索和呈现。

考虑一个显示产品详细信息的 UI 页面，该页面可以显示 10 到 10,000 条记录。假设 UI 设计为从后端获取并显示整个目录。在这种情况下，它将消耗额外的后端资源并导致用户等待很长的时间。

**实施分页系统可以显着增强用户体验。与立即获取整个记录集相比，最初检索一些记录并提供根据请求加载下一组记录的选项会更有效。**

使用分页，后端可以返回具有较小子集（例如 10 条记录）的初始响应，并使用偏移量或下一页链接检索后续页面。这种方法将获取和显示记录的负载分布在多个页面上，从而改善了整体应用程序体验。

## 3. Spring Data 中的分页与分页 Spring 数据反应式

[Spring Data](https://www.baeldung.com/spring-data)是更大的 Spring Framework 生态系统中的一个项目，旨在简化和增强 Java 应用程序中的数据访问。Spring Data 提供了一组通用的抽象和功能，通过减少样板代码和推广最佳实践来简化开发过程。

[正如Spring Data Pagination 示例](https://www.baeldung.com/spring-data-jpa-pagination-sorting)中所解释的，[*PageRequest*](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageRequest.html)对象接受*page*、*size*和*sort*参数，可用于配置和请求不同的页面。Spring Data 提供了*[PagingAndSortingRepository，](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html)*它提供了使用分页和排序抽象来检索实体的方法。存储库方法接受*[Pageable](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/Pageable.html)*和[Sort](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/Sort.html)对象，可用于配置返回的*[Page](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/Page.html)*信息。该*Page*对象包含*[totalElements](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html#getTotalElements())*和*[totalPages](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html#getTotalPages())*通过在内部执行附加查询来填充的属性。该信息可用于请求后续页面的信息。

相反，Spring Data Reactive 并不完全支持分页。原因就在于Spring Reactive对异步非阻塞的支持。它必须等待（或阻塞），直到返回特定页面大小的所有数据，这不是很有效。然而，**Spring Data Reactive 仍然支持\*[Pageable](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/Pageable.html)\*。[\*我们可以使用PageRequest\*](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageRequest.html)对象配置它来检索特定的数据块，并添加显式查询来获取记录总数。**

**当使用 Spring Data 时，我们可以获得响应的[\*Flux\*](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)，而不是\*Page\*，其中包含有关页面上记录的元数据。**

## 4. 基本应用

### 4.1. Spring WebFlux 和 Spring Data Reactive 中分页的实现

在本文中，我们将使用一个简单的 Spring R2DBC 应用程序，该应用程序通过 GET /products 公开分页产品信息。

让我们考虑一个简单的产品模型：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Product {

    @Id
    @Getter
    private UUID id;

    @NotNull
    @Size(max = 255, message = "The property 'name' must be less than or equal to 255 characters.")
    private String name;

    @NotNull
    private double price;
}复制
```

*[我们可以通过传递Pageable](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/Pageable.html)*对象从产品存储库中获取产品列表，该对象包含[*页面*](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/AbstractPageRequest.html#getPageNumber())和[大小等配置：](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/AbstractPageRequest.html#getPageSize())

```java
@Repository
public interface ProductRepository extends ReactiveSortingRepository<Product, UUID> {
    Flux<Product> findAllBy(Pageable pageable);   
}复制
```

此查询将结果集响应为*Flux*，而不是*Page*，因此需要单独查询记录总数以填充*Page*响应。

让我们添加一个带有[*PageRequest*](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageRequest.html)对象的控制器，该对象还运行一个附加查询来获取记录总数。这是因为我们的存储库不会返回[*页面*](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html)信息，而是返回*Flux<Product>*：

```java
@GetMapping("/products")
public Mono<Page<Product>> findAllProducts(Pageable pageable) {
    return this.productRepository.findAllBy(pageable)
      .collectList()
      .zipWith(this.productRepository.count())
      .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
}复制
```

最后，我们必须将查询结果集和最初接收到的*Pageable*对象发送到[*PageImpl*](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageImpl.html)。此类具有计算*页面*信息的辅助方法，其中包括有关页面的元数据以获取下一组记录。

现在，当我们尝试访问端点时，我们应该收到带有页面元数据的产品列表：

```json
{
  "content": [
    {
      "id": "cdc0c4e6-d4f6-406d-980c-b8c1f5d6d106",
      "name": "product_A",
      "price": 1
    },
    {
      "id": "699bc017-33e8-4feb-aee0-813b044db9fa",
      "name": "product_B",
      "price": 2
    },
    {
      "id": "8b8530dc-892b-475d-bcc0-ec46ba8767bc",
      "name": "product_C",
      "price": 3
    },
    {
      "id": "7a74499f-dafc-43fa-81e0-f4988af28c3e",
      "name": "product_D",
      "price": 4
    }
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 4,
  "totalPages": 1,
  "first": true,
  "numberOfElements": 4,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "empty": false
}复制
```

与 Spring Data 一样，我们使用某些[查询参数](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#core.web.basic.paging-and-sorting)导航到不同的页面，并通过扩展*[WebMvcConfigurationSupport](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurationSupport.html)*来配置默认属性。

让我们将默认页面大小从 20 更改为 100，并通过重写*addArgumentResolvers*方法将默认页面设置为 0：

```java
@Configuration
public class CustomWebMvcConfigurationSupport extends WebMvcConfigurationSupport {

    @Bean
    public PageRequest defaultPageRequest() {
        return PageRequest.of(0, 100);
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        SortHandlerMethodArgumentResolver argumentResolver = new SortHandlerMethodArgumentResolver();
        argumentResolver.setSortParameter("sort");
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver(argumentResolver);
        resolver.setFallbackPageable(defaultPageRequest());
        resolver.setPageParameterName("page");
        resolver.setSizeParameterName("size");
        argumentResolvers.add(resolver);
    }
}复制
```

现在，我们可以从第 0 页开始发出请求，最多 100 条记录：

```bash
$ curl --location 'http://localhost:8080/products?page=0&size=50&sort=price,DESC'复制
```

如果不指定页面和大小参数，则默认页面索引为 0，每页有 100 条记录。但请求将页面大小设置为 50：

```json
{
  "content": [
    ....
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "pageNumber": 0,
    "pageSize": 50,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 4,
  "totalPages": 1,
  "first": true,
  "numberOfElements": 4,
  "size": 50,
  "number": 0,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "empty": false
}复制
```

## 5. 结论

在本文中，我们了解了 Spring Data Reactive 分页的独特本质。我们还实现了一个返回带分页的产品列表的端点。