## 1. 概述

在本教程中，我们介绍HAL浏览器是什么，以及它有什么用。然后，我们使用Spring构建一个包含一些端点的简单REST API，并使用一些测试数据初始化数据库。

最后，使用HAL浏览器，我们将探索我们的REST API并发现如何遍历其中包含的数据。

## 2. HAL和HAL浏览器

[JSON超文本应用程序语言(HAL)](http://stateless.co/hal_specification.html)是一种简单的格式，它提供了一种一致且简单的方法来在我们的API中的资源之间进行超链接。在我们的REST API中包含HAL使其对用户来说更容易探索，并且本质上是自我记录的。

它的工作原理是返回JSON格式的数据，其中概述了有关API的相关信息。

**HAL模型围绕两个简单的概念展开**。

资源，其中包含：

-   相关URI的链接
-   嵌入式资源
-   状态

链接：

-   目标URI
-   与链接的关系
-   其他一些可选属性，可帮助折旧、内容协商等

**HAL浏览器是由开发HAL的同一个人创建的，并提供浏览器内GUI来遍历你的REST API**。

现在，我们将构建一个简单的REST API，插入HAL浏览器并探索这些功能。

## 3. 依赖关系

下面是将HAL浏览器集成到我们的REST API中所需的唯一依赖项。

首先，基于Maven项目的[依赖](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.data" AND a%3A"spring-data-rest-hal-browser")为：

```java
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-rest-hal-explorer</artifactId>
    <version>3.4.1.RELEASE</version>
</dependency>
```

如果你使用Gradle进行构建，则可以将以下行添加到你的build.gradle文件中：

```groovy
implementation group: 'org.springframework.data', name: 'spring-data-rest-hal-explorer', version: '3.4.1.RELEASE'
```

## 4. 构建一个简单的REST API

### 4.1 简单数据模型

在我们的示例中，我们将设置一个简单的REST API来访问不同的Book对象。

在下面，我们定义了一个简单的Book实体，其中包含适当的JPA注解，以便我们可以使用Hibernate持久化数据：

```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(columnDefinition = "VARCHAR", length = 100)
    private String title;

    @NotNull
    @Column(columnDefinition = "VARCHAR", length = 100)
    private String author;

    @Column(columnDefinition = "VARCHAR", length = 1000)
    private String blurb;

    private int pages;

    // usual getters, setters and constructors
}
```

### 4.2 Repository

接下来，我们需要一些端点。为此，我们可以利用PagingAndSortingRepository并指定我们要从Book实体获取数据。

**这个类提供了简单的CRUD功能，以及开箱即用的分页和排序功能**：

```java
@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

    @RestResource(rel = "title-contains", path="title-contains")
    Page<Book> findByTitleContaining(@Param("query") String query, Pageable page);

    @RestResource(rel = "author-contains", path="author-contains", exported = false)
    Page<Book> findByAuthorContaining(@Param("query") String query, Pageable page);
}
```

我们通过添加两个新端点扩展了Repository：

-   findByTitleContaining：返回包含标题中包含的查询的Book
-   findByAuthorContaining：从数据库中返回书籍作者包含查询的Book

请注意，我们的第二个端点包含export = false属性。此属性停止为此端点生成HAL链接，并且将无法通过HAL浏览器使用。

最后，我们在Spring启动时通过定义一个实现ApplicationRunner接口的类来加载我们的数据。

## 5. 安装HAL浏览器

在使用Spring构建REST API时，HAL浏览器的设置非常简单。只要我们添加了依赖项，Spring就会自动配置浏览器，并通过默认端点使其可用。

我们现在需要做的就是运行并切换到浏览器。HAL浏览器将在http://localhost:8080/上可用

## 6. 使用HAL浏览器探索我们的REST API

**HAL浏览器分为两部分：资源管理器和检查器**。

### 6.1 HAL浏览器

听起来，资源管理器致力于探索API中与当前端点相关的新部分。它包含一个搜索栏，以及用于显示当前端点的自定义请求标头和属性的文本框。

在这些下面，我们有链接部分和一个可点击的嵌入式资源列表。

### 6.2 使用链接

如果我们导航到/books端点，我们可以查看现有链接：

![链接-1](https://www.baeldung.com/wp-content/uploads/2018/08/Links-1.png)

这些链接是从相邻部分中的HAL生成的：

```json
"_links": {
    "first": {
      "href": "http://localhost:8080/books?page=0&size=20"
    },
    "self": {
      "href": "http://localhost:8080/books{?page,size,sort}",
      "templated": true
    },
    "next": {
      "href": "http://localhost:8080/books?page=1&size=20"
    },
    "last": {
      "href": "http://localhost:8080/books?page=4&size=20"
    },
    "profile": {
      "href": "http://localhost:8080/profile/books"
    },
    "search": {
      "href": "http://localhost:8080/books/search"
    }
},
```

如果我们移动到search端点，我们还可以查看我们使用PagingAndSortingRepository创建的自定义端点：

```json
{
    "_links": {
        "title-contains": {
            "href": "http://localhost:8080/books/search/title-contains{?query,page,size,sort}",
            "templated": true
        },
        "self": {
            "href": "http://localhost:8080/books/search"
        }
    }
}
```

上面的HAL显示了我们的title-contains端点显示了合适的搜索条件。请注意author-contains端点是如何丢失的，因为我们定义了它不应该被导出。

### 6.3 查看嵌入式资源

嵌入式资源在我们的/books端点上显示单个Book记录的详细信息。每个资源还包含自己的属性和链接部分：

![嵌入 2](https://www.baeldung.com/wp-content/uploads/2018/08/embed-2.png)

### 6.4 使用表单

链接部分中GET列中的问号按钮表示可以使用表单模式输入自定义搜索条件。

这是我们的title-contains端点的形式：

![HAL浏览器选择表](https://www.baeldung.com/wp-content/uploads/2018/08/modal.png)

我们的自定义URI返回20个Book对象的第一页，其中标题包含单词“Java”。

### 6.5 Hal检查器

检查器构成浏览器的右侧，包含响应标头和响应正文。此HAL数据用于呈现我们在本教程前面看到的链接和嵌入式资源。

## 7. 总结

在本文中，我们总结了HAL是什么、它有什么用以及为什么它可以帮助我们创建卓越的自文档化REST API。

我们使用Spring构建了一个简单的REST API，它实现了PagingAndSortingRepository，并定义了我们自己的端点。我们还了解了如何从HAL浏览器中排除某些端点。

在定义了我们的API之后，我们初始化了一些测试数据，并在HAL浏览器的帮助下对其进行了详细探索。我们看到了HAL浏览器的结构，以及允许我们逐步执行API和探索其数据的UI控件。