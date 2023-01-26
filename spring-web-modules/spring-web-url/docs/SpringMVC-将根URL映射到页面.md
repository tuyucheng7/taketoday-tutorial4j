## 一、概述

在本教程中，我们将了解如何将根 URL 映射到[Spring MVC](https://www.baeldung.com/spring-mvc-tutorial)中的页面。

首先，我们将看看 Spring MVC 的默认行为。然后，我们将讨论抑制此行为的场景。最后，我们将学习提供我们自己的自定义映射的方法。

## 2.项目设置

我们可以使用 Spring Initializr 来生成项目，同时添加 Spring Web Starter 依赖。

如果手动添加[依赖](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)项，我们需要将以下内容添加到*pom.xml* 文件中：![img]()

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
复制
```

### 2.1. 创建索引页

*让我们在src/main/resources/templates*文件夹中创建一个页面。我们将此页面命名为*index.html*：![img]()

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Index Page</title>
</head>
<body>
    <h1>Hello World!</h1>
</body>
</html>
复制
```

### 2.2. 默认行为

让我们运行应用程序并查看 Spring MVC 的默认行为。

应用程序启动并运行后，让我们导航到根 URL： *http://localhost:8080/*：

[![索引页面加载正常并显示 hello world](https://www.baeldung.com/wp-content/uploads/2023/01/index-page-e1673539470769.png)](https://www.baeldung.com/wp-content/uploads/2023/01/index-page.png)

正如我们所看到的，索引页面无需任何映射即可显示。

## 3.改变默认行为

让我们看一下默认行为被抑制的场景。

### 3.1. @EnableWebMvc

让我们将 [*@EnableWebMvc*](https://www.baeldung.com/spring-controllers#Controller-1) 注释添加到我们的 *RootMappingApplication* 类：

```java
@SpringBootApplication
@EnableWebMvc
public class RootMappingApplication {
    public static void main(String[] args) {
        SpringApplication.run(RootMappingApplication.class, args);
    }
}
复制
```

让我们运行应用程序并导航到根 URL： *http://localhost:8080/*。这一次，我们得到一个错误：

[![显示一个错误页面，显示 404-Not Found 错误](https://www.baeldung.com/wp-content/uploads/2023/01/404-error-page-1-e1673539498205.png)](https://www.baeldung.com/wp-content/uploads/2023/01/404-error-page-1.png)

**这是因为\*@EnableWebMvc\*注释禁用了 Spring Boot 完成的自动 Web 应用程序配置。**

要解决此问题，我们需要提供自己的自定义映射。让我们看看执行此操作的不同方法。

## 4.自定义映射

让我们看看提供我们自己的自定义映射的不同方法。

### 4.1. 使用控制器

提供路径和文件映射的一种方法是使用[控制器](https://www.baeldung.com/spring-controllers#Controller-1)。

让我们从创建一个控制器类开始：

```java
@Controller
public class RootController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
复制
```

这个控制器类有一个映射到“/”路径的方法。该方法只返回字符串“ *index* ”。 ***在解释返回值时，Spring MVC 会在src/main/resources/templates\*** **文件夹****中查找同名模板。**

如果我们运行应用程序并导航到根 URL： *http://localhost:8080/*，我们将看到显示的索引页面。

这是提供自定义映射的简单方法。当我们要映射单个页面时，使用这种方法就可以了。但是，如果我们有多个页面要映射，这种方法就会变得很麻烦。

让我们看看提供自定义映射的更有效方法。

### 4.2. 使用*WebMvcConfigurer*

提供路径和文件映射的另一种方法是使用*WebMvcConfigurer* 接口。

让我们从创建一个配置类开始：![img]()

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }
}
复制
```

这个配置类实现了 *WebMvcConfigurer* 接口。 **它覆盖了\*addViewControllers()\* 方法来添加一个视图控制器。视图控制器映射到“/”路径并返回“\*索引\*”视图。**

同样，如果我们运行应用程序并导航到根 URL：*http://localhost:8080/*，我们将看到索引页面。

我们应该注意，**如果控制器和配置都为同一路径提供映射，则控制器优先。**

## 5.结论

在本文中，我们了解了如何将根 URL 映射到 Spring MVC 中的页面。我们讨论了 Spring MVC 的默认行为以及它如何被自定义配置覆盖。

我们还研究了两种提供我们自己的自定义映射的方法——使用控制器和使用*WebMvcConfigurer* 接口。