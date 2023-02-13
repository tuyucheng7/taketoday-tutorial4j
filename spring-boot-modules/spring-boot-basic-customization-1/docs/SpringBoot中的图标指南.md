## 1. 概述

网站图标是显示在浏览器中的一个小网站图标，通常位于地址旁边。

通常我们不想满足于各种框架(如Spring Boot)提供的默认框架。

在本快速教程中，我们将通过研究自定义网站图标的各种方法来讨论如何**自定义Spring Boot应用程序的网站图标**。

## 2. 覆盖网站图标

覆盖Spring Boot应用程序默认图标的最简单方法是**将新图标放在resources目录中**：

```text
src/main/resources/favicon.ico
```

图标文件的名称应该为”favicon.ico“。

我们也可以将该文件放在项目resources目录中的static目录中：

```text
src/main/resources/static/favicon.ico
```

Spring Boot在启动时扫描根resources位置中的favicon.ico文件，然后扫描static内容位置。

## 3. 使用自定义的位置

我们可能不想将网站图标放在资源目录的根目录中，而是希望将它与应用程序的其他图像放在一起。

我们可以通过禁用application.properties文件中的默认图标来做到这一点：

```properties
spring.mvc.favicon.enabled=false
```

**值得注意的是，从Spring Boot 2.2开始，这个配置属性已被弃用**。此外，Spring Boot不再提供默认的图标，因为这个图标可以归类为信息泄漏。

然后实现我们的处理程序：

```java
@Configuration
public class FaviconConfiguration {

    @Bean
    public SimpleUrlHandlerMapping myFaviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);
        mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));
        return mapping;
    }

    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        ClassPathResource classPathResource = new ClassPathResource("cn/tuyucheng/taketoday/images/");
        List<Resource> locations = List.of(classPathResource);
        requestHandler.setLocations(locations);
        return requestHandler;
    }
}
```

我们对SimpleUrlHandlerMapping设置setOrder(Integer.MIN_VALUE)，以确保它有最高优先级。

通过此配置，**我们可以将图标文件存储在应用程序结构中的任何位置**。

## 4. 优雅的禁用图标

如果我们不希望我们的应用程序有任何网站图标，我们可以通过将属性spring.mvc.favicon.enabled设置为false来禁用它。但是当浏览器查找时，它们会收到“404 Not Found”错误。

**我们可以通过自定义FaviconController来避免这种情况，该控制器返回一个空响应**：

```java
@Controller
static class FaviconController {

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }
}
```

## 5. 总结

在本文中，我们看到了如何覆盖Spring Boot应用程序的默认图标，为图标使用自定义位置，以及如果我们不想使用图标时，如何避免404错误。