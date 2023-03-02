## 1. 概述

本教程将探讨如何使用XML和Java配置**通过Spring提供静态资源**。

### 延伸阅读

### [使用Spring MVC的可缓存静态资源](https://www.baeldung.com/cachable-static-assets-with-spring-mvc)

本文展示了在使用Spring MVC服务时如何缓存静态资源，例如Javascript和CSS文件。

[阅读更多](https://www.baeldung.com/cachable-static-assets-with-spring-mvc)→

### [WebJar简介](https://www.baeldung.com/maven-webjars)

将WebJars与Spring结合使用的快速实用指南。

[阅读更多](https://www.baeldung.com/maven-webjars)→

### [使用Maven压缩JS和CSS资源](https://www.baeldung.com/maven-minification-of-js-and-css-assets)

使用Maven压缩Java Web项目中的Javascript和CSS文件的快速指南。

[阅读更多](https://www.baeldung.com/maven-minification-of-js-and-css-assets)→

## 2. 使用Spring Boot

Spring Boot带有[ResourceHttpRequestHandler](https://github.com/spring-projects/spring-framework/blob/master/spring-webmvc/src/main/java/org/springframework/web/servlet/resource/ResourceHttpRequestHandler.java)的预配置实现，以促进提供静态资源。

**默认情况下，此处理程序提供来自classpath上的任何/static、/public、/resources和/META-INF/resources目录的静态内容**。由于src/main/resources默认情况下通常位于类路径中，因此我们可以将这些目录中的任何一个放在那里。

例如，如果我们将about.html文件放在类路径中的/static目录中，那么我们可以通过[http://localhost:8080/about.html](http://localhost:8080/about.html)访问该文件。同样，我们可以通过将该文件添加到其他提到的目录中来获得相同的结果。

### 2.1 自定义路径模式

默认情况下，Spring Boot在请求的根部分/**下提供所有静态内容。尽管它似乎是一个很好的默认配置，但我们可以通过[spring.mvc.static-path-pattern](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties.java#L105)配置属性进行更改。 

例如，如果我们想通过http://localhost:8080/content/about.html访问同一个文件，我们可以在我们的application.properties中这样写：

```properties
spring.mvc.static-path-pattern=/content/**
```

在WebFlux环境中，我们应该使用[spring.webflux.static-path-pattern](https://github.com/spring-projects/spring-boot/blob/c71ed407e43e561333719573d63464ae9db388c1/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties.java#L38)属性。

### 2.2 自定义目录

与路径模式类似，**也可以通过[spring.web.resources.static-locations](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/WebProperties.java#L86)配置属性更改默认资源位置**。此属性可以接收多个以逗号分隔的资源位置：

```properties
spring.web.resources.static-locations=classpath:/files/,classpath:/static-files
```

在这里，我们从类路径中的/files和/static-files目录提供静态内容。此外，Spring Boot可以从类路径外部提供静态文件：

```properties
spring.web.resources.static-locations=file:/opt/files
```

在这里，我们使用[文件资源签名](https://docs.spring.io/spring/docs/5.2.4.RELEASE/spring-framework-reference/core.html#resources-resourceloader)file:/来提供本地磁盘中的文件。

## 3. XML配置

如果我们需要采用基于XML的配置的老式方式，我们可以充分利用mvc:resources元素来指向具有特定公共URL模式的资源位置。

例如，以下行将通过在我们的应用程序根文件夹下的“/resources/”目录中搜索来处理所有使用公共URL模式(如“/resources/**”)的资源请求：

```xml
<mvc:resources mapping="/resources/**" location="/resources/" />
```

现在我们可以像下面的HTML页面一样访问CSS文件：

```html
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <link href="<c:url value="/resources/myCss.css" />" rel="stylesheet">
    <title>Home</title>
</head>
<body>
<h1>Hello world!</h1>
</body>
</html>
```

## 4. ResourceHttpRequestHandler

Spring 3.1引入了ResourceHandlerRegistry来配置ResourceHttpRequestHandler以从类路径、WAR或文件系统提供静态资源。我们可以在我们的Web上下文配置类中以编程方式配置ResourceHandlerRegistry。

### 4.1 提供存储在WAR中的资源

为了说明这一点，我们将使用与之前相同的URL指向myCss.css，但现在实际文件将位于WAR的webapp/resources文件夹中，这是我们在部署Spring 3.1+应用程序时应该放置静态资源的地方:

```java
@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
              .addResourceHandler("/resources/**")
              .addResourceLocations("/resources/");
    }
}
```

让我们稍微分析一下这个例子。首先，我们通过定义资源处理程序来配置面向外部的URI路径。然后我们将该面向外部的URI路径在内部映射到资源实际所在的物理路径。

当然，我们可以使用这个简单而灵活的API定义多个资源处理程序。

现在，html页面中的以下行将获取webapp/resources目录中的myCss.css资源：

```html
<link href="<c:url value="/resources/myCss.css" />" rel="stylesheet">
```

### 4.2 提供存储在文件系统中的资源

假设我们希望在请求与/files/**模式匹配的公共URL时提供存储在/opt/files/目录中的资源。我们只需配置URL模式并将其映射到磁盘上的特定位置：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/files/**")
        .addResourceLocations("file:/opt/files/");
 }
```

对于Windows用户，此示例中传递给addResourceLocations的参数为“file:///C:/opt/files/”。

一旦我们配置了资源位置，我们就可以在我们的home.html中使用映射的URL模式来加载存储在文件系统中的图像：

```html
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <link href="<c:url value="
    /resources/myCss.css" />" rel="stylesheet">
    <title>Home</title>
</head>
<body>
<h1>Hello world!</h1>
<img alt="image" src="<c:url value=" files/myImage.png" />">
</body>
</html>
```

### 4.3 为资源配置多个位置

如果我们想在多个位置查找资源怎么办？

我们可以使用addResourceLocations方法包含多个位置。将按顺序搜索位置列表，直到找到资源：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/resources/**")
        .addResourceLocations("/resources/","classpath:/other-resources/");
}
```

以下curl请求将显示存储在应用程序的webappp/resources或类路径中的other-resources文件夹中的Hello.html页面：

```shell
curl -i http://localhost:8080/handling-spring-static-resources/resources/Hello.html
```

## 5. 新的资源解析器

Spring 4.1通过新的**ResourceResolvers**提供不同类型的资源解析器，这些资源解析器可用于在加载静态资源时优化浏览器性能。这些解析器可以链接并缓存在浏览器中以优化请求处理。

### 5.1 PathResourceResolver

这是最简单的解析器，其目的是在给定公共URL模式的情况下查找资源。事实上，如果我们不向ResourceChainRegistration添加ResourceResolver，这就是默认的解析器。

让我们看一个例子：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/resources/**")
        .addResourceLocations("/resources/","/other-resources/")
        .setCachePeriod(3600)
        .resourceChain(true)
        .addResolver(new PathResourceResolver());
}
```

注意事项：

-   我们在资源链中将PathResourceResolver注册为其中唯一的ResourceResolver。我们可以参考4.3节，查看如何链接多个ResourceResolver
-   提供的资源将在浏览器中缓存3600秒
-   该链最终使用方法resourceChain(true)配置

现在，对于HTML代码，它与PathResourceResolver一起在webapp/resources或webapp/other-resources文件夹中找到foo.js脚本：

```html
<script type="text/javascript" src="<c:url value="/resources/foo.js" />">
```

### 5.2 EncodedResourceResolver

此解析器尝试根据Accept-Encoding请求标头值查找编码资源。

例如，我们可能需要通过使用gzip内容编码提供静态资源的压缩版本来优化带宽。

要配置EncodedResourceResolver，我们需要在ResourceChain中配置它，就像我们配置PathResourceResolver一样：

```java
registry
    .addResourceHandler("/other-files/**")
    .addResourceLocations("file:/Users/Me/")
    .setCachePeriod(3600)
    .resourceChain(true)
    .addResolver(new EncodedResourceResolver());
```

默认情况下，EncodedResourceResolver配置为支持br和gzip编码。

因此，以下curl请求将获取位于文件系统Users/Me/目录中的Home.html文件的压缩版本：

```shell
curl -H  "Accept-Encoding:gzip" 
  http://localhost:8080/handling-spring-static-resources/other-files/Hello.html
```

请注意我们是如何**将标头的“Accept-Encoding”值设置为gzip**的。这很重要，因为只有当gzip内容对响应有效时，这个特定的解析器才会启动。

最后，请注意，与以前一样，压缩版本将在浏览器中缓存的时间段内保持可用，在本例中为3600秒。

### 5.3 链接ResourceResolvers

为了优化资源查找，ResourceResolvers可以将资源处理委托给其他解析器。唯一不能委托给链的解析器是PathResourceResolver，我们应该将其添加到链的末尾。

事实上，如果resourceChain没有设置为true，那么默认情况下只会使用PathResourceResolver来提供资源。在这里，如果GzipResourceResolver不成功，我们链接PathResourceResolver来解析资源：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/js/**")
        .addResourceLocations("/js/")
        .setCachePeriod(3600)
        .resourceChain(true)
        .addResolver(new GzipResourceResolver())
        .addResolver(new PathResourceResolver());
}
```

现在我们已经将/js/**模式添加到ResourceHandler中，让我们包含位于home.html页面的webapp/js/目录中的foo.js资源：

```html
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <link href="<c:url value="
    /resources/bootstrap.css" />" rel="stylesheet" />
    <script type="text/javascript" src="<c:url value="
    /js/foo.js" />"></script>
    <title>Home</title>
</head>
<body>
<h1>This is Home!</h1>
<img alt="bunny hop image" src="<c:url value=" files/myImage.png" />" />
<input type="button" value="Click to Test Js File" onclick="testing();"/>
</body>
</html>
```

值得一提的是，从Spring框架5.1开始，[GzipResourceResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/resource/GzipResourceResolver.html)已被弃用，取而代之的是EncodedResourceResolver。因此，我们以后应该避免使用它。

## 6. 额外的安全配置

如果使用Spring Security，则允许访问静态资源很重要。我们需要添加访问资源URL的相应权限：

```xml
<intercept-url pattern="/files/**" access="permitAll" />
<intercept-url pattern="/other-files/**/" access="permitAll" />
<intercept-url pattern="/resources/**" access="permitAll" />
<intercept-url pattern="/js/**" access="permitAll" />
```

## 7. 总结

在本文中，我们说明了Spring应用程序提供静态资源的各种方式。

基于XML的资源配置是一个**遗留选项**，如果我们还不能走Java配置路线，我们可以使用它。

Spring 3.1通过其ResourceHandlerRegistry对象提出了一个基本的编程替代方案。

最后，随Spring 4.1一起提供的新的开箱即用的ResourceResolvers和ResourceChainRegistration对象。提供资源加载优化功能，如缓存和资源处理器链接，以提高提供静态资源的效率。