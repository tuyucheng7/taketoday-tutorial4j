## 1. 概述

当我们有一个大数据集并且我们希望将其以较小的块呈现给用户时，分页通常很有用。

此外，我们经常需要在分页时根据某些条件对数据进行排序。

在本教程中，**我们将学习如何使用Spring Data JPA进行分页和排序**。

## 延伸阅读

### [Spring Data JPA @Query](https://www.baeldung.com/spring-data-jpa-query)

了解如何使用Spring Data JPA中的@Query注解来使用JPQL和原生SQL定义自定义查询。

[阅读更多](https://www.baeldung.com/spring-data-jpa-query)→

### [Spring Data JPA Repository中的派生查询方法](https://www.baeldung.com/spring-data-derived-queries)

探索Spring Data JPA中的查询派生机制。

[阅读更多](https://www.baeldung.com/spring-data-derived-queries)→

## 2. 初始设置

首先，假设我们有一个Product实体作为我们的域类：

```java
@Entity
public class Product {

    @Id
    private long id;
    private String name;
    private double price;

    // constructors, getters and setters
}
```

我们的每个产品实例都有一个唯一的标识符：id，它的名称(name)和与之相关的价格(price)。

## 3. 创建Repository

要访问我们的Product，我们需要一个ProductRepository：

```java
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    List<Product> findAllByPrice(double price, Pageable pageable);
}
```

**通过让它扩展[PagingAndSortingRepository](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html)，我们得到了findAll(Pageable pageable)和findAll(Sort sort)方法来进行分页和排序**。

相反，我们可以选择扩展[JpaRepository](https://www.baeldung.com/spring-data-repositories)，因为它也扩展了PagingAndSortingRepository。

一旦我们扩展了PagingAndSortingRepository，**我们就可以添加我们自己的方法，将Pageable和Sort作为参数**，就像我们在上面的findAllByPrice方法中所指定的参数那样。

让我们来看看如何使用我们的新方法对我们的Product进行分页。

## 4. Pagination

一旦我们的Repository从PagingAndSortingRepository扩展，我们只需要：

1.  创建或获取PageRequest对象，该对象是Pageable接口的实现
2.  将PageRequest对象作为参数传递给我们要使用的Repository方法

我们可以通过传入请求的页数和页面大小来创建一个PageRequest对象。

这里的**页数从0开始**：

```java
Pageable firstPageWithTwoElements = PageRequest.of(0, 2);

Pageable secondPageWithFiveElements = PageRequest.of(1, 5);
```

在Spring MVC中，我们还可以选择使用[Spring Data Web Support](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.web)在我们的控制器中获取Pageable实例。

一旦我们有了PageRequest对象，我们就可以在调用Repository的方法时将其传入：

```java
Page<Product> allProducts = productRepository.findAll(firstPageWithTwoElements);

List<Product> allTenDollarProducts = productRepository.findAllByPrice(10, secondPageWithFiveElements);
```

findAll(Pageable pageable)方法默认返回一个Page<T\>对象。

但是，**我们可以选择从任何返回分页数据的自定义方法返回Page<T\>、Slice<T\>或List<T\>**。

Page<T\>实例除了拥有Product的列表外，还可以获取可用页面的总数。**它会触发一个额外的计数查询来实现它。为了避免这种开销成本，我们可以改为返回Slice<T\>或List<T\>**。

Slice只知道下一个分片是否可用。

## 5. 分页和排序

同样，为了仅对查询结果进行排序，我们可以简单地将[Sort](https://www.baeldung.com/spring-data-sorting)的实例传递给该方法：

```java
Page<Product> allProductsSortedByName = productRepository.findAll(Sort.by("name"));
```

但是，如果我们想要**对数据进行排序和分页**怎么办？

我们可以通过将排序细节传递给我们的PageRequest对象本身来做到这一点：

```java
Pageable sortedByName = PageRequest.of(0, 3, Sort.by("name"));

Pageable sortedByPriceDesc = PageRequest.of(0, 3, Sort.by("price").descending());

Pageable sortedByPriceDescNameAsc = PageRequest.of(0, 5, Sort.by("price").descending().and(Sort.by("name")));
```

根据我们的排序要求，我们可以**在创建PageRequest实例时指定排序字段和排序方向**。

像往常一样，然后我们可以将此Pageable类型实例传递给Repository的方法。

## 6. 总结

在本文中，我们学习了如何在Spring Data JPA中对查询结果进行分页和排序。