## 一、概述

我们可以使用[Swagger UI](https://swagger.io/tools/swagger-ui/)作为平台，以方便的方式可视化 API 接口并与之交互。它是一个功能强大的工具，只需最少的配置即可生成 API 结构。

在本文中，我们将专注于将[Swagger 与 Spring Boot REST API 结合](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)使用。具体来说，我们将探讨在 Swagger UI 中隐藏请求字段的不同方法。

## 2.简介

为了简单起见，我们将创建一个基本的 Spring Boot 应用程序并使用 Swagger UI 探索 API。

让我们使用 Spring Boot创建一个简单的ArticleApplication 。我们使用ArticlesController公开了两个 API 。使用GET API，我们希望接收与所有文章相关的详细信息。

另一方面，我们使用POST API 为新文章添加详细信息：

```java
@RestController
@RequestMapping("/articles")
public class ArticlesController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("")
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @PostMapping("")
    public void addArticle(@RequestBody Article article) {
        articleService.addArticle(article);
    }

}
```

我们将使用Article类作为这些 API 的数据传输对象 (DTO)。现在，让我们在Article类中添加一些字段：

```java
public class Article {

    private int id;
    private String title;
    private int numOfWords;
    
    // standard getters and setters

}


```

我们可以在http://localhost:8080/swagger-ui/#/articles-controller访问 Swagger UI 。让我们运行应用程序并查看上述两个 API 的默认行为：

[![Img1 e1648650028181](https://www.baeldung.com/wp-content/uploads/2022/04/2_BAEL-5329-Img1-e1648650028181.png)](https://www.baeldung.com/wp-content/uploads/2022/04/2_BAEL-5329-Img1-e1648650028181.png)[![Img2 e1648650100457](https://www.baeldung.com/wp-content/uploads/2022/04/2_BAEL-5329-Img2-e1648650100457.png)](https://www.baeldung.com/wp-content/uploads/2022/04/2_BAEL-5329-Img2-e1648650100457.png)

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

在POST API 中，我们接受来自用户的所有详细信息，即id、title和numOfWords。在GET API 中，我们在响应中返回相同的字段。我们可以看到，默认情况下，Swagger 会为这两个 API 显示所有字段。

现在，假设我们要使用单独的后端逻辑来设置id字段。在这种情况下，我们不希望用户输入与id字段相关的信息。为避免混淆，我们希望在 Swagger UI 中隐藏此字段。

我们想到的一个直接选择是创建一个单独的 DTO 并在其中隐藏所需的字段。如果我们想为 DTO 添加额外的逻辑，此方法会很有帮助。如果它适合我们的整体要求，我们可以选择使用此选项。

对于本文，让我们使用不同的注解来隐藏 Swagger UI 中的字段。

## 3.使用@JsonIgnore

@JsonIgnore是标准的[Jackson 注解](https://www.baeldung.com/jackson-annotations)。我们可以用它来指定 Jackson 在序列化和反序列化过程中要忽略的字段。我们可以只将注解添加到要忽略的字段，它会隐藏指定字段的 getter 和 setter。

试一试吧：

```java
@JsonIgnore
private int id;


```

让我们重新运行应用程序并检查 Swagger UI：[![图像4](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img4.png)](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img4.png)[![图 3](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img-3.png)](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img-3.png)

我们可以看到，现在API 描述中没有显示id字段。Swagger 还提供注解来实现类似的行为。

## 4.使用@ApiModelProperty

@ApiModelProperty 提供与模型对象的属性相关的元数据。我们可以使用注解的隐藏属性来隐藏Swagger UI 模型对象定义中的字段。

让我们尝试一下 id字段：

```java
@ApiModelProperty(hidden = true)
private int id;
```

在上述场景中，我们发现对于GET和POST API ， id字段都是隐藏的。假设我们希望允许用户查看ID详细信息作为GET API 响应的一部分。在这种情况下，我们需要寻找其他选择。

Swagger 还提供了一个替代属性readOnly。我们可以使用它在更新操作期间隐藏指定的字段，但在检索操作时仍然显示它。

让我们检查一下：

```java
@ApiModelProperty(readOnly = true)
private int id;
```

现在让我们检查更新后的 Swagger UI：

[![图5](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img5.png)](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img5.png)[![图片6](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img6.png)](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img6.png)

我们可以看到id字段现在对于GET API 是可见的，但对于POST API仍然隐藏——它支持只读操作。

从 1.5.19 版开始，此属性被标记为已弃用。对于更高版本，让我们探索其他注解。

## 5.使用@JsonProperty

Jackson 提供了@JsonProperty注解。我们可以使用它来添加 与 POJO 字段的 getter/setter 相关的元数据，这些元数据可以在对象的序列化/反序列化期间使用。我们可以设置注解的访问属性，只允许对特定字段进行读操作：

```java
@JsonProperty(access = JsonProperty.Access.READ_ONLY)
private int id;
```

通过这种方式，我们可以隐藏POST API 模型定义的id字段，但仍可以在GET API 响应中显示它。 让我们探索另一种实现所需功能的方法。

## 6.使用@ApiParam

@ApiParam也是一个 Swagger 注解，我们可以使用它来指定与请求参数相关的元数据。我们可以将hidden属性设置为true以隐藏任何属性。但是，我们在这里有一个限制：它仅在我们使用@ModelAttribute而不是@RequestBody来访问请求数据时才有效。

让我们试试看：

```java
@PostMapping("")
public void addArticle(@ModelAttribute Article article) {
    articleService.addArticle(article);
}


@ApiParam(hidden = true)
private int id;
```

让我们检查此案例的 Swagger UI 规范：

[![图8](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img8.png)](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img8.png)[![图片7](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img-7.png)](https://www.baeldung.com/wp-content/uploads/2022/04/BAEL-5329-Img-7.png)

 

我们成功地隐藏了POST API 请求数据定义中的id字段。

## 七、总结

我们探索了不同的选项来修改模型对象属性在 Swagger UI 中的可见性。所讨论的注解还提供了其他几个功能，我们可以使用它们来更新 Swagger 规范。我们应该根据我们的要求使用适当的方法。