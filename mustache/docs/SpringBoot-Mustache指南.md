## 1. 概述

在本文中，我们将专注于使用 Mustache 模板在Spring Boot应用程序中生成 HTML 内容。

它是一个用于创建动态内容的无逻辑模板引擎，由于其简单性而广受欢迎。

如果想了解基础知识，请查看我们[对 Mustache 的介绍](https://www.baeldung.com/mustache)文章。

## 2.Maven依赖

为了能够将 Mustache 与Spring Boot一起使用，我们需要将[专用的Spring Boot启动器](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-mustache")添加到我们的pom.xml 中：

```xml
<dependency>			
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mustache</artifactId>
</dependency>
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-web</artifactId> 
</dependency>
```

此外，我们还需要 [spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web) 依赖。

## 3. 创建模板

让我们展示一个示例，并使用 Spring-Boot 创建一个简单的 MVC 应用程序，该应用程序将在网页上提供文章。

让我们编写文章内容的第一个模板：

```html
<div class="starter-template">
    {{#articles}}
    <h1>{{title}}</h1>
    <h3>{{publishDate}}</h3>
    <h3>{{author}}</h3>
    <p>{{body}}</p>
    {{/articles}}
</div>
```

我们将保存这个 HTML 文件，比如article.html，并在我们的index.html中引用它：

```html
<div class="container">
    {{>layout/article}}
</div>
```

这里，layout是一个子目录，article是模板文件的文件名。

请注意，默认的 mustache 模板文件扩展名现在是 . 小胡子。我们可以用一个属性覆盖这个配置：

```plaintext
spring.mustache.suffix:.html
```

## 4.控制器

现在让我们编写服务文章的控制器：

```java
@GetMapping("/article")
public ModelAndView displayArticle(Map<String, Object> model) {

    List<Article> articles = IntStream.range(0, 10)
      .mapToObj(i -> generateArticle("Article Title " + i))
      .collect(Collectors.toList());

    model.put("articles", articles);

    return new ModelAndView("index", model);
}
```

控制器返回要在页面上呈现的文章列表。在文章模板中，以 # 开头并以 / 结尾的标签文章负责列表。

这将迭代传递的模型并分别呈现每个元素，就像在 HTML 表格中一样：

```html
 {{#articles}}...{{/articles}}

```

generateArticle()方法使用一些随机数据创建一个Article实例。

请注意，控制器返回的文章模型中的键应与文章模板标签的键相同。

现在，让我们测试我们的应用程序：

```java
@Test
public void givenIndexPage_whenContainsArticle_thenTrue() {

    ResponseEntity<String> entity 
      = this.restTemplate.getForEntity("/article", String.class);
 
    assertTrue(entity.getStatusCode()
      .equals(HttpStatus.OK));
    assertTrue(entity.getBody()
      .contains("Article Title 0"));
}
```

我们还可以通过以下方式部署来测试应用程序：

```bash
mvn spring-boot:run
```

部署后，我们可以点击localhost:8080/article，我们将列出我们的文章：

[![第1条](https://www.baeldung.com/wp-content/uploads/2017/09/article-1-300x226.jpeg)](https://www.baeldung.com/wp-content/uploads/2017/09/article-1.jpeg)

## 5. 处理默认值

在 Mustache 环境中，如果我们没有为占位符提供值，MustacheException将被抛出并显示一条消息“No method or field with name ”variable-name …”。

为了避免此类错误，最好为所有占位符提供一个默认的全局值：

```java
@Bean
public Mustache.Compiler mustacheCompiler(
  Mustache.TemplateLoader templateLoader, 
  Environment environment) {

    MustacheEnvironmentCollector collector
      = new MustacheEnvironmentCollector();
    collector.setEnvironment(environment);

    return Mustache.compiler()
      .defaultValue("Some Default Value")
      .withLoader(templateLoader)
      .withCollector(collector);
}
```

## 6. Spring MVC 的小胡子

现在，让我们讨论一下如果我们决定不使用 Spring Boot，如何与 Spring MVC 集成。首先，让我们添加依赖项：

```xml
<dependency>
    <groupId>com.github.sps.mustache</groupId>
    <artifactId>mustache-spring-view</artifactId>
    <version>1.4</version>
</dependency>
```

最新的可以在[这里](https://search.maven.org/classic/#search|ga|1|g%3A"com.github.sps.mustache" AND a%3A"mustache-spring-view")找到。

接下来，我们需要配置MustacheViewResolver而不是 Spring 的InternalResourceViewResolver：

```java
@Bean
public ViewResolver getViewResolver(ResourceLoader resourceLoader) {
    MustacheViewResolver mustacheViewResolver
      = new MustacheViewResolver();
    mustacheViewResolver.setPrefix("/WEB-INF/views/");
    mustacheViewResolver.setSuffix("..mustache");
    mustacheViewResolver.setCache(false);
    MustacheTemplateLoader mustacheTemplateLoader 
      = new MustacheTemplateLoader();
    mustacheTemplateLoader.setResourceLoader(resourceLoader);
    mustacheViewResolver.setTemplateLoader(mustacheTemplateLoader);
    return mustacheViewResolver;
}

```

我们只需要配置存储模板的后缀，前缀模板的扩展名，以及负责加载模板的 templateLoader。

## 七. 总结

在本快速教程中，我们了解了如何将 Mustache 模板与Spring Boot结合使用，在 UI 中呈现一组元素，并为变量提供默认值以避免错误。

最后，我们讨论了如何使用MustacheViewResolver 将它与 Spring 集成。