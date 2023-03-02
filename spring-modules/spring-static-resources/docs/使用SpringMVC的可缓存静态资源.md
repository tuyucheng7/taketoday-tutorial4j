## 1. 概述

本文重点介绍在使用Spring Boot和Spring MVC服务时缓存静态资源(例如Javascript和CSS文件)。

我们还将涉及“完美缓存”的概念，本质上是确保在更新文件时不会从缓存中错误地提供旧版本。

## 2. 缓存静态资源

为了使静态资源可缓存，我们需要配置其对应的资源处理器。

这里有一个简单的例子来说明如何做到这一点-将响应上的Cache-Control标头设置为max-age=31536000，这会导致浏览器使用文件的缓存版本一年：

```java
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**")
              .addResourceLocations("/js/")
              .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }
}
```

我们有这么长的缓存有效期的原因是我们希望客户端在文件更新之前使用文件的缓存版本，根据[Cache-Control的RFC](https://www.ietf.org/rfc/rfc2616.txt)，365天是我们可以使用的最大标头值。

**因此，当客户端第一次请求foo.js时，他将通过网络接收整个文件(在本例中为37字节)，状态代码为200 OK**。响应将具有以下标头来控制缓存行为：

```html
Cache-Control: max-age=31536000
```

这指示浏览器缓存有效期为一年的文件，作为以下响应的结果：

<img src="../assets/img.png">

**当客户端第二次请求同一个文件时，浏览器不会再向服务器发起请求**。相反，它将直接从其缓存中提供文件并避免网络往返，因此页面加载速度会快得多：

<img src="../assets/img_1.png">

Chrome浏览器用户在测试时需要小心，因为如果你通过按屏幕上的刷新按钮或按F5键刷新页面，Chrome将不会使用缓存。你需要按地址栏上的Enter键来观察缓存行为。更多信息，请点击[此处](https://stackoverflow.com/questions/3401049/chrome-doesnt-cache-images-js-css/16510707#16510707)。

### 2.1 Spring Boot

要在Spring Boot中自定义Cache-Control标头，我们可以使用[spring.resources.cache.cachecontrol](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web)属性命名空间下的属性。例如，要将max-age更改为1年，我们可以将以下内容添加到我们的application.properties：

```properties
spring.resources.cache.cachecontrol.max-age=365d
```

**这适用于Spring Boot提供的[所有静态资源](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration.java#L307)**。因此，如果我们只想将缓存策略应用于请求的子集，我们应该使用普通的Spring MVC方法。

除了max-age之外，还可以自定义其他[Cache-Control参数](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control)，例如具有类似配置属性的no-store或no-cache。

## 3. 对静态资源进行版本控制

使用缓存为静态资源提供服务可以使页面加载非常快，但它有一个重要的警告。当你更新文件时，客户端不会获得文件的最新版本，因为它不会与服务器检查文件是否是最新的，而只是从浏览器缓存中提供文件。

为了使浏览器仅在文件更新时从服务器获取文件，我们需要做以下事情：

-   在包含版本的URL下提供文件。例如，foo.js应该在/js/foo-46944c7e3a9bd20cc30fdc085cae46f2.js下提供
-   使用新URL更新指向文件的链接
-   每当文件更新时更新URL的版本部分。例如，当foo.js更新时，它现在应该在/js/foo-a3d8d7780349a12d739799e9aa7d2623.js下提供。

当文件更新时，客户端将向服务器请求文件，因为该页面将有一个指向不同URL的链接，因此浏览器将不会使用其缓存。如果文件未更新，则其版本(因此其URL)将不会更改，客户端将继续使用该文件的缓存。

通常，我们需要手动完成所有这些操作，但Spring开箱即用地支持这些操作，包括计算每个文件的哈希值并将它们附加到URL。让我们看看如何配置Spring应用程序来为我们完成所有这些工作。

### 3.1 带有版本的URL

我们需要将VersionResourceResolver添加到路径中，以便在其URL中为其下的文件提供更新的版本字符串：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/js/**")
        .addResourceLocations("/js/")
        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
        .resourceChain(false)
        .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
}
```

这里我们使用内容版本策略。/js文件夹中的每个文件都将在一个URL下提供，该URL具有根据其内容计算的版本。这称为指纹识别。例如，foo.js现在将在URL /js/foo-46944c7e3a9bd20cc30fdc085cae46f2.js下提供。

使用此配置，当客户端请求http://localhost:8080/js/ 46944c7e3a9bd20cc30fdc085cae46f2.js时：

```bash
curl -i http://localhost:8080/js/foo-46944c7e3a9bd20cc30fdc085cae46f2.js
```

服务器将使用Cache-Control标头进行响应，以告诉客户端浏览器将文件缓存一年：

```bash
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Last-Modified: Tue, 09 Aug 2016 06:43:26 GMT
Cache-Control: max-age=31536000
```

### 3.2 Spring Boot

要在Spring Boot中启用相同的基于内容的版本控制，我们只需在[spring.resources.chain.strategy.content](https://github.com/spring-projects/spring-boot/blob/bb568c5bffcf70169245d749f3642bfd9dd33143/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/servlet/WebMvcAutoConfiguration.java#L532)属性命名空间下使用一些配置。例如，我们可以通过添加以下配置来实现与之前相同的结果：

```properties
spring.resources.chain.strategy.content.enabled=true
spring.resources.chain.strategy.content.paths=/**
```

与Java配置类似，这为与/**路径模式匹配的所有资源启用了基于内容的版本控制。

### 3.3 使用新URL更新链接

在我们将版本插入URL之前，我们可以使用一个简单的script标签来导入foo.js：

```html
<script type="text/javascript" src="/js/foo.js">
```

现在我们在带有版本的URL下提供相同的文件，我们需要将其反映在页面上：

```html
<script type="text/javascript" src="<em>/js/foo-46944c7e3a9bd20cc30fdc085cae46f2.js</em>">
```

处理所有这些长路径变得很乏味。Spring为这个问题提供了一个更好的解决方案。我们可以使用ResourceUrlEncodingFilter和JSTL的url标签来重写版本化链接的URL。

ResourceURLEncodingFilter可以像往常一样在web.xml下注册：

```xml
<filter>
    <filter-name>resourceUrlEncodingFilter</filter-name>
    <filter-class>
        org.springframework.web.servlet.resource.ResourceUrlEncodingFilter
    </filter-class>
</filter>
<filter-mapping>
    <filter-name>resourceUrlEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

我们的JSP页面需要导入JSTL核心标签库才可以使用url标签：

```html
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
```

然后，我们可以使用url标签导入foo.js，如下所示：

```html
<script type="text/javascript" src="<c:url value="/js/foo.js" />">
```

呈现此JSP页面时，文件的URL被正确重写以包含其中的版本：

```html
<script type="text/javascript" src="/js/foo-46944c7e3a9bd20cc30fdc085cae46f2.js">
```

### 3.4 更新版本URL的一部分

每当更新文件时，都会再次计算其版本，并在包含新版本的URL下提供文件。我们不必为此做任何额外的工作，VersionResourceResolver会为我们处理这个问题。

## 4. 修复CSS链接

CSS文件可以使用@import指令导入其他CSS文件。例如，myCss.css文件导入另一个.css文件：

```css
@import "another.css";
```

这通常会导致版本控制的静态资源出现问题，因为浏览器会请求another.css文件，但该文件是在版本化路径下提供的，例如another-9556ab93ae179f87b178cfad96a6ab72.css。

为了解决这个问题并向正确的路径发出请求，我们需要在资源处理程序配置中引入CssLinkResourceTransformer：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**")
        .addResourceLocations("/resources/", "classpath:/other-resources/")
        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
        .resourceChain(false)
        .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
        .addTransformer(new CssLinkResourceTransformer());
}
```

这会修改myCss.css的内容并将import语句替换为以下内容：

```css
@import "another-9556ab93ae179f87b178cfad96a6ab72.css";
```

## 5. 总结

利用HTTP缓存可以极大地提高网站性能，但在使用缓存时避免提供过时资源可能会很麻烦。

在本文中，我们实现了一个很好的策略，即在使用Spring MVC提供静态资源的同时使用HTTP缓存，并在文件更新时清除缓存。