## 1. 概述

Apache [Tiles](https://tiles.apache.org/)是一个免费的开源模板框架，完全建立在Composite设计模式之上。

复合设计模式是一种结构模式，它将对象组合成树结构以表示整体-部分层次结构，这种模式统一处理单个对象和对象的组合。换句话说，在Tiles中，页面是通过组装称为Tiles的子视图的组合来构建的。

该框架相对于其他框架的优势包括：

-   可重用性
-   易于配置
-   低性能开销

在本文中，我们将重点介绍Apache Tiles与Spring MVC的集成。

## 2. 依赖配置

这里的第一步是在pom.xml中添加必要的[依赖项](https://search.maven.org/search?q=a:tiles-jsp)：

```xml
<dependency>
    <groupId>org.apache.tiles</groupId>
    <artifactId>tiles-jsp</artifactId>
    <version>3.0.8</version>
</dependency>
```

## 3. Tiles布局文件

现在我们需要定义模板定义，特别是根据每个页面，我们将覆盖该特定页面的模板定义：

```xml
<tiles-definitions>
    <definition name="template-def"
                template="/WEB-INF/views/tiles/layouts/defaultLayout.jsp">
        <put-attribute name="title" value=""/>
        <put-attribute name="header"
                       value="/WEB-INF/views/tiles/templates/defaultHeader.jsp"/>
        <put-attribute name="menu"
                       value="/WEB-INF/views/tiles/templates/defaultMenu.jsp"/>
        <put-attribute name="body" value=""/>
        <put-attribute name="footer"
                       value="/WEB-INF/views/tiles/templates/defaultFooter.jsp"/>
    </definition>
    <definition name="home" extends="template-def">
        <put-attribute name="title" value="Welcome"/>
        <put-attribute name="body"
                       value="/WEB-INF/views/pages/home.jsp"/>
    </definition>
</tiles-definitions>
```

##  4. ApplicationConfiguration等类

作为配置的一部分，我们将创建三个特定的java类，分别称为ApplicationInitializer、ApplicationController和ApplicationConfiguration：

-   ApplicationInitializer初始化并检查ApplicationConfiguration类中指定的必要配置
-   ApplicationConfiguration类包含用于将Spring MVC与Apache Tiles框架集成的配置
-   ApplicationController类与tiles.xml文件同步工作，并根据传入请求重定向到必要的页面

让我们看看每个类的作用：

```java
@Controller
@RequestMapping("/")
public class TilesController {
    @RequestMapping(
          value = { "/"},
          method = RequestMethod.GET)
    public String homePage(ModelMap model) {
        return "home";
    }
    @RequestMapping(
          value = { "/apachetiles"},
          method = RequestMethod.GET)
    public String productsPage(ModelMap model) {
        return "apachetiles";
    }

    @RequestMapping(
          value = { "/springmvc"},
          method = RequestMethod.GET)
    public String contactUsPage(ModelMap model) {
        return "springmvc";
    }
}
```

```java
public class WebInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext container) throws ServletException {

        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();

        ctx.register(TilesApplicationConfiguration.class);

        container.addListener(new ContextLoaderListener(ctx));

        ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(ctx));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
}
```

有两个重要的类在Spring MVC应用程序中配置tile时起着关键作用。它们是TilesConfigurer和TilesViewResolver：

-   TilesConfigurer通过提供tiles-configuration文件的路径帮助将Tiles框架与Spring框架联系起来
-   TilesViewResolver是Spring API提供的用于解析tiles view的适配器类之一

最后，在ApplicationConfiguration类中，我们使用了TilesConfigurer和TilesViewResolver类来实现整合：

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.spring.controller.tiles")
public class TilesApplicationConfiguration implements WebMvcConfigurer {
    @Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tilesConfigurer = new TilesConfigurer();
        tilesConfigurer.setDefinitions(new String[] { "/WEB-INF/views/**/tiles.xml" });
        tilesConfigurer.setCheckRefresh(true);

        return tilesConfigurer;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        TilesViewResolver viewResolver = new TilesViewResolver();
        registry.viewResolver(viewResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
              .addResourceLocations("/static/");
    }
}
```

## 5. Tiles模板文件

到此为止，我们已经完成了Apache Tiles框架的配置以及整个应用程序中使用的模板和特定tile的定义。

在此步骤中，我们需要创建已在tiles.xml中定义的特定模板文件。

请找到可用作构建特定页面基础的布局片段：

```html
<html>
<head>
    <meta
            http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><tiles:getAsString name="title" /></title>
    <link href="<c:url value='/static/css/app.css' />"
          rel="stylesheet">
    </link>
</head>
<body>
<div class="flex-container">
    <tiles:insertAttribute name="header"/>
    <tiles:insertAttribute name="menu"/>
    <article class="article">
        <tiles:insertAttribute name="body"/>
    </article>
    <tiles:insertAttribute name="footer"/>
</div>
</body>
</html>
```

## 6. 总结

Spring MVC与Apache Tiles的集成到此结束。