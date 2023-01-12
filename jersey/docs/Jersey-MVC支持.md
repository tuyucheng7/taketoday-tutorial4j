## 1. 概述

[Jersey](https://jersey.github.io/)是一个用于开发 RESTFul Web 服务的开源框架。

除了用作 JAX-RS 参考实现之外，它还包括许多扩展以进一步简化 Web 应用程序开发。

在本教程中，我们将创建一个使用 Jersey 提供的模型-视图-控制器 (MVC) 扩展的小型示例应用程序。

要了解如何使用 Jersey 创建 API，请[在此处](https://www.baeldung.com/jersey-rest-api-with-spring)查看这篇文章。

## 2.泽西岛的MVC

Jersey 包含支持模型-视图-控制器 (MVC) 设计模式的扩展。

首先，在 Jersey 组件的上下文中，MVC 模式中的 Controller 对应于一个资源类或方法。

同样，视图对应于绑定到资源类或方法的模板。最后，模型表示从资源方法(控制器)返回的Java对象。

要在我们的应用程序中使用 Jersey MVC 的功能，我们首先需要注册我们希望使用的 MVC 模块扩展。

在我们的示例中，我们将使用流行的Java模板引擎[Freemarker](https://freemarker.apache.org/)。这是 Jersey 开箱即用的渲染引擎之一，还有 [Mustache](https://github.com/spullara/mustache.java)和标准JavaServer Pages (JSP)。

有关 MVC 工作原理的更多信息，请参阅本[教程](https://www.baeldung.com/mvc-servlet-jsp)。

## 3. 应用设置

在本节中，我们将从在 pom.xml 中配置必要的 Maven 依赖项开始。

然后，我们将了解如何使用简单的嵌入式[Grizzly](http://grizzly.java.net/)服务器配置和运行我们的服务器。

### 3.1. Maven 依赖项

让我们从添加 Jersey MVC Freemarker 扩展开始。

我们可以从 [Maven Central](https://search.maven.org/classic/#search|ga|1|jersey-mvc-freemarker)获取最新版本：

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext</groupId>
    <artifactId>jersey-mvc-freemarker</artifactId>
    <version>2.27</version>
</dependency>

```

我们还需要 Grizzly servlet 容器。

我们可以再次在 [Maven Central](https://search.maven.org/classic/#search|ga|1|jersey-container-grizzly2-servlet)中找到最新版本：

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-grizzly2-servlet</artifactId>
    <version>2.27</version>
</dependency>
```

### 3.2. 配置服务器

要在我们的应用程序中使用 Jersey MVC 模板支持，我们需要注册 MVC 模块提供的特定 JAX-RS 功能。

考虑到这一点，我们定义了一个自定义资源配置：

```java
public class ViewApplicationConfig extends ResourceConfig {    
    public ViewApplicationConfig() {
        packages("com.baeldung.jersey.server");
        property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "templates/freemarker");
        register(FreemarkerMvcFeature.class);;
    }
}
```

在上面的例子中我们配置了三项：

-   首先，我们使用packages 方法告诉 Jersey 扫描com.baeldung.jersey.server包中用@Path 注解的类。 这将注册我们的FruitResource
-   接下来，我们配置基本路径以解析我们的模板。这告诉 Jersey 在 /src/main/resources/templates/freemarker 中查找 Freemarker 模板
-   最后，我们通过FreemarkerMvcFeature 类注册处理 Freemarker 渲染的功能 

### 3.3. 运行应用程序

现在让我们看看如何运行我们的 Web 应用程序。我们将使用[exec-maven-plugin](https://www.mojohaus.org/exec-maven-plugin/)配置我们的pom.xml来执行我们的嵌入式 Web 服务器：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <configuration>                
        <mainClass>com.baeldung.jersey.server.http.EmbeddedHttpServer</mainClass>
    </configuration>
</plugin>
```

现在让我们使用 Maven 编译并运行我们的应用程序：

```bash
mvn clean compile exec:java
...
Jul 28, 2018 6:21:08 PM org.glassfish.grizzly.http.server.HttpServer start
INFO: [HttpServer] Started.
Application started.
Try out http://localhost:8082/fruit
Stop the application using CTRL+C

```

转到浏览器 URL – http://localhost:8080/fruit。瞧，“欢迎水果索引页！” 被展示。

## 4.MVC模板

在 Jersey 中，MVC API 由两个类组成，用于将模型绑定到视图，即Viewable和@Template。 

在本节中，我们将解释将模板链接到视图的三种不同方式：

-   使用 可视类
-   使用 @Template 注解
-   如何使用 MVC 处理错误并将它们传递给特定模板

### 4.1. 在资源类中使用Viewable

让我们从查看Viewable开始：

```java
@Path("/fruit")
public class FruitResource {
    @GET
    public Viewable get() {
        return new Viewable("/index.ftl", "Fruit Index Page");
    }
}
```

在此示例中，FruitResource JAX-RS 资源类是控制器。Viewable实例封装了引用的数据模型，它是一个简单的字符串。

此外，我们还包括对关联视图模板的命名引用 - index.ftl。

### 4.2. 在资源方法上使用@Template

每次我们想将模型绑定到模板时，都不需要使用Viewable。

在下一个示例中，我们将简单地使用@Template注解我们的资源方法：

```java
@GET
@Template(name = "/all.ftl")
@Path("/all")
@Produces(MediaType.TEXT_HTML)
public Map<String, Object> getAllFruit() {
    List<Fruit> fruits = new ArrayList<>();
    fruits.add(new Fruit("banana", "yellow"));
    fruits.add(new Fruit("apple", "red"));
    fruits.add(new Fruit("kiwi", "green"));

    Map<String, Object> model = new HashMap<>();
    model.put("items", fruits);
    return model;
}
```

在这个例子中，我们使用了 @Template注解。这避免了通过Viewable将我们的模型直接包装在模板引用中，并使我们的资源方法更具可读性。

该模型现在由我们带注解的资源方法的返回值表示——一个Map<String, Object>。这直接传递给模板all.ftl ，它只显示我们的水果列表。

### 4.3. 使用 MVC 处理错误

现在让我们看看如何使用@ErrorTemplate注解来处理错误：

```java
@GET
@ErrorTemplate(name = "/error.ftl")
@Template(name = "/named.ftl")
@Path("{name}")
@Produces(MediaType.TEXT_HTML)
public String getFruitByName(@PathParam("name") String name) {
    if (!"banana".equalsIgnoreCase(name)) {
        throw new IllegalArgumentException("Fruit not found: " + name);
    }
    return name;
}

```

一般来说， @ErrorTemplate注解的目的是将模型绑定到错误视图。 当在处理请求期间抛出异常时，此错误处理程序将负责呈现响应。

在我们简单的 Fruit API 示例中，如果在处理过程中没有发生错误，则使用named.ftl模板来呈现页面。否则，如果引发异常，则会向用户显示error.ftl模板。

在这种情况下，模型就是抛出的异常本身。 这意味着在我们的模板中，我们可以直接在异常对象上调用方法。

让我们快速看一下我们的error.ftl模板中的片段以突出显示：

```html
<body>
    <h1>Error - ${model.message}!</h1>
</body>
```

在我们的最后一个例子中，我们将看一个简单的单元测试：

```java
@Test
public void givenGetFruitByName_whenFruitUnknown_thenErrorTemplateInvoked() {
    String response = target("/fruit/orange").request()
      .get(String.class);
    assertThat(response, containsString("Error -  Fruit not found: orange!"));
}

```

在上面的示例中，我们使用了水果资源的响应。我们检查响应是否包含来自抛出的IllegalArgumentException的消息。

## 5.总结

在本文中，我们探索了 Jersey 框架 MVC 扩展。

我们首先介绍了 MVC 在 Jersey 中的工作原理。接下来，我们了解了如何配置、运行和设置示例 Web 应用程序。

最后，我们研究了将 MVC 模板与 Jersey 和 Freemarker 结合使用的三种方式，以及如何处理错误。