## 1. 简介

[Velocity](https://velocity.apache.org/)是 Apache 软件基金会的模板引擎，可以处理普通文本文件、SQL、XML、Java 代码和许多其他类型。

在本文中，我们将重点关注在典型的Spring MVCWeb 应用程序中使用 Velocity。

## 2.Maven依赖

让我们从启用 Velocity 支持开始——具有以下依赖项：

```xml
<dependency>
    <groupId>org.apache.velocity</groupId>
    <artifactId>velocity</artifactId>
    <version>1.7</version>
</dependency>
        
<dependency>
    <groupId>org.apache.velocity</groupId>
    <artifactId>velocity-tools</artifactId>
    <version>2.0</version>
</dependency>
```

两者的最新版本都可以在这里找到：[velocity](https://mvnrepository.com/artifact/org.apache.velocity/velocity)和[velocity-tools](https://mvnrepository.com/artifact/org.apache.velocity/velocity-tools)。

## 三、配置

### 3.1. 网络配置

如果我们不想使用web.xml，让我们使用Java和初始化程序配置我们的 Web 项目：

```java
public class MainWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.register(WebConfig.class);

        sc.addListener(new ContextLoaderListener(root));

        ServletRegistration.Dynamic appServlet = 
          sc.addServlet("mvc", new DispatcherServlet(new GenericWebApplicationContext()));
        appServlet.setLoadOnStartup(1);
    }
}
```

或者，我们当然可以使用传统的web.xml：

```xml
<web-app ...>
    <display-name>Spring MVC Velocity</display-name>
    <servlet>
        <servlet-name>mvc</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/mvc-servlet.xml</param-value>
     </init-param>
     <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>mvc</servlet-name>
    <url-pattern>/</url-pattern>
    </servlet-mapping>
 
    <context-param>
        <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/spring-context.xml</param-value>
    </context-param>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
</web-app>
```

请注意，我们将 servlet 映射到“/”路径。

### 3.2. 弹簧配置

现在让我们回顾一个简单的 Spring 配置——再次从Java开始：

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages= {
  "com.baeldung.mvc.velocity.controller",
  "com.baeldung.mvc.velocity.service" }) 
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/resources/")
          .addResourceLocations("/resources/");
    }
 
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public ViewResolver viewResolver() {
        VelocityLayoutViewResolver bean = new VelocityLayoutViewResolver();
        bean.setCache(true);
        bean.setPrefix("/WEB-INF/views/");
        bean.setLayoutUrl("/WEB-INF/layouts/layout.vm");
        bean.setSuffix(".vm");
        return bean;
    }
    
    @Bean
    public VelocityConfigurer velocityConfig() {
        VelocityConfigurer velocityConfigurer = new VelocityConfigurer();
        velocityConfigurer.setResourceLoaderPath("/");
        return velocityConfigurer;
    }
}
```

让我们也快速浏览一下配置的 XML 版本：

```xml
<beans ...>
    <context:component-scan base-package="com.baeldung.mvc.velocity." />
    <context:annotation-config /> 
    <bean id="velocityConfig" 
      class="org.springframework.web.servlet.view.velocity.VelocityConfigurer">
        <property name="resourceLoaderPath">
            <value>/</value>
        </property>
    </bean> 
    <bean id="viewResolver"
      class="org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver">
        <property name="cache" value="true" />
        <property name="prefix" value="/WEB-INF/views/" />
        <property name="layoutUrl" value="/WEB-INF/layouts/layout.vm" />
        <property name="suffix" value=".vm" />
    </bean>
</beans>
```

在这里，我们告诉 Spring 去哪里寻找带注解的 bean 定义：

```java
<context:component-scan base-package="com.baeldung.mvc.velocity." />
```

我们表示我们将在我们的项目中使用以下行的注解驱动配置：

```java
<context:annotation-config />
```

通过创建“ velocityConfig ”和“ viewResolver ”bean，我们告诉VelocityConfigurer到哪里寻找模板，以及VelocityLayoutViewResolver到哪里寻找视图和布局。

## 4.速度模板

最后，让我们创建我们的模板——从一个通用的标题开始：

```html
<div style="...">
    <div style="float: left">
        <h1>Our tutorials</h1>
    </div>
</div>
```

和页脚：

```html
<div style="...">
    @Copyright baeldung.com
</div>
```

让我们为我们的网站定义一个通用布局，我们将在下面的代码中使用上面的片段和解析：

```html
<html>
    <head>
        <title>Spring & Velocity</title>  
    </head>
    <body>
        <div>
            #parse("/WEB-INF/fragments/header.vm")
        </div>  
        <div>
            <!-- View index.vm is inserted here -->
            $screen_content
        </div>  
        <div>
            #parse("/WEB-INF/fragments/footer.vm")
        </div>
    </body>
</html>
```

你可以检查$screen_content变量是否包含页面内容。

最后，我们将为主要内容创建一个模板：

```html
<h1>Index</h1>
 
<h2>Tutorials list</h2>
<table border="1">
    <tr>
        <th>Tutorial Id</th>
        <th>Tutorial Title</th>
        <th>Tutorial Description</th>
        <th>Tutorial Author</th>
    </tr>
    #foreach($tut in $tutorials)
    <tr>
        <td>$tut.tutId</td>
        <td>$tut.title</td>
        <td>$tut.description</td>
        <td>$tut.author</td>
    </tr>
    #end
</table>
```

## 5. 控制器端

我们创建了一个简单的控制器，它返回一个教程列表作为我们要填充的布局的内容：

```java
@Controller
@RequestMapping("/")
public class MainController {
 
    @Autowired
    private ITutorialsService tutService;

    @RequestMapping(value ="/", method = RequestMethod.GET)
    public String defaultPage() {
        return "index";
    }

    @RequestMapping(value ="/list", method = RequestMethod.GET)
    public String listTutorialsPage(Model model) { 
        List<Tutorial> list = tutService.listTutorials();
        model.addAttribute("tutorials", list);
        return "index";
    }
}

```

最后，我们可以在本地访问这个简单的示例——例如：[localhost:8080/spring-mvc-velocity/](http://localhost:8080/spring-mvc-velocity/)

## 六. 总结

在这个简单的教程中，我们使用Velocity模板引擎配置了Spring MVC Web 应用程序。