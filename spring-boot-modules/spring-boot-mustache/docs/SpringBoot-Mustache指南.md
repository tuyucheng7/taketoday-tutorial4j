## 1. 概述

在本文中，我们将重点介绍如何使用Mustache模板在Spring Boot应用程序中生成HTML内容。

**它是一个用于创建动态内容的无逻辑模板引擎**，由于其简单性而广受欢迎。

如果想了解基础知识，请查看我们对[Mustache的介绍](https://www.baeldung.com/mustache)文章。

## 2. Maven依赖

为了能够将Mustache与Spring Boot一起使用，我们需要将[专用的Spring Boot启动器](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-mustache/3.0.5)添加到我们的pom.xml中：

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

此外，我们还需要[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.5)依赖。

## 3. 创建模板

让我们展示一个示例，并使用Spring Boot创建一个简单的MVC应用程序，该应用程序将在网页上提供文章。

让我们为文章内容编写第一个模板：

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

我们将保存这个HTML文件，比如article.html，并在我们的index.html中引用它：

```html
<div class="container">
    {{>layout/article}}
</div>
```

这里，layout是一个子目录，article是模板文件的文件名。

请注意，默认的mustache模板文件扩展名现在是.mustache。我们可以用一个属性覆盖这个配置：

```properties
spring.mustache.suffix=.html
```

## 4. 控制器

现在让我们编写用于提供文章的控制器：

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

控制器返回要在页面上呈现的文章列表。在文章模板中，以#开头并以/结尾的标签articles负责处理列表。

这将遍历传递的模型并分别呈现每个元素，就像在HTML表格中一样：

```html
{{#articles}}...{{/articles}}
```

generateArticle()方法使用一些随机数据创建一个Article实例。

请注意，控制器返回的文章模型中的键应与article模板标签的键相同。

现在，让我们测试我们的应用程序：

```java
@Test
public void givenIndexPage_whenContainsArticle_thenTrue() {
    ResponseEntity<String> entity = this.restTemplate.getForEntity("/article", String.class);
 
    assertTrue(entity.getStatusCode().equals(HttpStatus.OK));
    assertTrue(entity.getBody().contains("Article Title 0"));
}
```

我们还可以通过以下方式部署来测试应用程序：

```bash
mvn spring-boot:run
```

部署后，我们可以访问[localhost:8080/article](http://localhost:8080/article)：

<img src="../assets/img.png">

## 5. 处理默认值

在Mustache环境中，如果我们没有为占位符提供值，则会抛出MustacheException并显示一条消息“No method or field with name ”variable-name...”。

为了避免此类错误，最好为所有占位符提供一个默认的全局值：

```java
@Bean
public Mustache.Compiler mustacheCompiler(Mustache.TemplateLoader templateLoader, Environment environment) {
    MustacheEnvironmentCollector collector = new MustacheEnvironmentCollector();
    collector.setEnvironment(environment);

    return Mustache.compiler()
        .defaultValue("Some Default Value")
        .withLoader(templateLoader)
        .withCollector(collector);
}
```

## 6. 使用Spring MVC的Mustache

现在，让我们讨论一下如果我们决定不使用Spring Boot，如何与Spring MVC集成。首先，让我们添加依赖项：

```xml
<dependency>
    <groupId>com.github.sps.mustache</groupId>
    <artifactId>mustache-spring-view</artifactId>
    <version>1.4</version>
</dependency>
```

最新的可以在[这里](https://central.sonatype.com/artifact/com.github.sps.mustache/mustache-spring-view/1.4)找到。

接下来，我们需要配置MustacheViewResolver而不是Spring的InternalResourceViewResolver：

```java
@Bean
public ViewResolver getViewResolver(ResourceLoader resourceLoader) {
    MustacheViewResolver mustacheViewResolver = new MustacheViewResolver();
    mustacheViewResolver.setPrefix("/WEB-INF/views/");
    mustacheViewResolver.setSuffix("..mustache");
    mustacheViewResolver.setCache(false);
    MustacheTemplateLoader mustacheTemplateLoader = new MustacheTemplateLoader();
    mustacheTemplateLoader.setResourceLoader(resourceLoader);
    mustacheViewResolver.setTemplateLoader(mustacheTemplateLoader);
    return mustacheViewResolver;
}
```

我们只需要配置存储模板的后缀，前缀模板的扩展名，以及负责加载模板的templateLoader。

## 7. 总结

在这个快速教程中，我们了解了如何将Mustache模板与Spring Boot结合使用，在UI中呈现一组元素，并为变量提供默认值以避免错误。

最后，我们讨论了如何使用MustacheViewResolver将它与Spring集成。