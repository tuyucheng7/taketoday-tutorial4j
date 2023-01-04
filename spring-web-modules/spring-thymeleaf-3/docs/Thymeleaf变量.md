## 1. 简介

在本教程中，我们将了解[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)中的变量。我们将创建一个Spring Boot示例，该示例将获取Tuyucheng文章列表并将它们显示在Thymeleaf HTML模板中。

## 2. Maven依赖

要使用Thymeleaf，我们需要添加[spring-boot-starter-thymeleaf](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)和[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 3. Web控制器

首先，我们将创建一个带有GET端点的Web控制器，该端点返回一个包含Tuyucheng文章列表的页面。

用@GetMapping注解的方法将采用单个参数——模型。它包含可以在Thymeleaf模板中进一步使用的所有全局变量。在我们的例子中，模型只有一个参数-文章列表。

Article类将包含两个String字段，name和url：

```java
public class Article {
    private String name;
    private String url;

    // constructor, getters and setters
}
```

我们控制器方法的返回值应该是所需Thymeleaf模板的名称。此名称应与位于src/resource/template目录中的HTML文件相对应。在我们的例子中，它将是src/resource/template/articles-list.html。

让我们快速浏览一下我们的Spring控制器：

```java
@Controller
@RequestMapping("/api/articles")
public class ArticlesController {

    @GetMapping
    public String allArticles(Model model) {
        model.addAttribute("articles", fetchArticles());
        return "articles-list";
    }

    private List<Article> fetchArticles() {
        return Arrays.asList(
              new Article(
                    "Introduction to Using Thymeleaf in Spring",
                    "https://www.tuyucheng.com/thymeleaf-in-spring-mvc"
              )
              // a few other articles
        );
    }
}
```

运行应用程序后，文章页面将在http://localhost:8080/articles可用。

## 4. Thymeleaf模板

现在，让我们进入Thymeleaf HTML模板。它应该具有标准的HTML文档结构，仅带有附加的Thymeleaf命名空间定义：

```html
<html xmlns:th="http://www.thymeleaf.org">
```

我们将在进一步的示例中使用它作为模板，我们将只替换<main\>标签的内容：

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Thymeleaf Variables</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<main>
    ...
</main>
</body>
</html>
```

## 5. 定义变量

我们可以通过两种方式在Thymeleaf模板中定义变量。第一个选项是在遍历数组时获取单个元素：

```html
<div th:each="article : ${articles}">
    <a th:text="${article.name}" th:href="${article.url}"></a>
</div>
```

结果，我们将得到一个<div\>，其中包含多个<a\>元素，对应于articles变量中的文章数。

另一种方法是根据另一个变量定义一个新变量。例如，我们可以获取文章数组的第一个元素：

```html
<div th:with="firstArticle=${articles[0]}">
    <a th:text="${firstArticle.name}" th:href="${firstArticle.url}"></a>
</div>
```

或者我们可以创建一个只包含文章名称的新变量：

```html
<div th:each="article : ${articles}", th:with="articleName=${article.name}">
    <a th:text="${articleName}" th:href="${article.url}"></a>
</div>
```

在上面的示例中，${article.name}和${articleName}片段是可替换的。

也可以定义多个变量。例如，我们可以创建两个单独的变量来保存文章名称和URL：

```html
<div th:each="article : ${articles}" th:with="articleName=${article.name}, articleUrl=${article.url}">
    <a th:text="${articleName}" th:href="${articleUrl}"></a>
</div>
```

## 6. 变量作用域

在控制器中传递给模型的变量具有全局范围。这意味着它们可以用在我们HTML模板的每个地方。

另一方面，在HTML模板中定义的变量具有局部作用域。它们只能在定义它们的元素范围内使用。

例如，下面的代码是正确的，因为<a\>元素在firstDiv中：

```xml
<div id="firstDiv" th:with="firstArticle=${articles[0]}">
    <a th:text="${firstArticle.name}" th:href="${firstArticle.url}"></a>
</div>
```

另一方面，当我们尝试在另一个div中使用firstArticle时：

```html
<div id="firstDiv" th:with="firstArticle=${articles[0]}">
    <a th:text="${firstArticle.name}" th:href="${firstArticle.url}"></a>
</div>
<div id="secondDiv">
    <h2 th:text="${firstArticle.name}"></h2>
</div>
```

我们会在编译时得到一个异常，说firstArticle是null：

```bash
org.springframework.expression.spel.SpelEvaluationException: EL1007E: Property or field 'name' cannot be found on null
```

这是因为<h2\>元素试图使用firstDiv 中定义的变量，该变量超出范围。

如果我们仍然需要在secondDiv中使用firstArticle变量，我们需要在secondDiv中再次定义它，或者将这两个div标签包装在一个公共元素中并在其中定义firstArticle。

## 7. 改变一个变量的值

也可以在给定范围内覆盖变量的值：

```xml
<div id="mainDiv" th:with="articles = ${ { articles[0], articles[1] } }">
    <div th:each="article : ${articles}">
        <a th:text="${article.name}" th:href="${article.url}"></a>
    </div>
</div>
```

在上面的示例中，我们将articles变量重新定义为只有两个前元素。

请注意，在mainDiv之外，articles变量仍将在控制器中传递其原始值。

## 8. 总结

在本教程中，我们学习了如何在Thymeleaf中定义和使用变量。