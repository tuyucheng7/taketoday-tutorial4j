## 1. 概述

在本文中，我们将介绍Spring Boot中新的[@ServletComponentScan](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/ServletComponentScan.html)注解。

目的是支持以下Servlet 3.0注解：

-   javax.servlet.annotation.WebFilter
-   javax.servlet.annotation.WebListener
-   javax.servlet.annotation.WebServlet

通过在@Configuration类上标注@ServletComponentScan并指定包，@WebServlet、@WebFilter和@WebListener注解类可以自动注册到嵌入式Servlet容器。

我们在[Java Servlets简介](https://www.baeldung.com/intro-to-servlets)中介绍了@WebServlet的基本用法，在Java[拦截器过滤模式简介](https://www.baeldung.com/intercepting-filter-pattern-in-java)中介绍了@WebFilter。对于@WebListener，你可以查看[这篇文章](https://www.baeldung.com/httpsessionlistener_with_metrics)，其中演示了Web监听器的典型用例。

## 2. Servlets、Filters、Listeners

在深入了解@ServletComponentScan之前，让我们先看看注解：@WebServlet、@WebFilter和@WebListener在@ServletComponentScan发挥作用之前是如何使用的。

### 2.1 @WebServlet

现在我们将首先定义一个服务于GET请求并响应“hello”的Servlet：

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getOutputStream().write("hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 2.2 @WebFilter

然后是一个过滤器，用于过滤以“/hello”为目标的请求，并在输出前加上“filtering”：

```java
@WebFilter("/hello")
public class HelloFilter implements Filter {

    //...
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
          throws IOException, ServletException {
        servletResponse.getOutputStream().print("filtering ");
        filterChain.doFilter(servletRequest, servletResponse);
    }
    //...
}
```

### 2.3 @WebListener

最后，在ServletContext中设置自定义属性的监听器：

```java
@WebListener
public class AttrListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().setAttribute("servlet-context-attr", "test");
    }
    //...
}
```

### 2.4 部署到Servlet容器

现在我们已经构建了一个简单的Web应用程序的基本组件，我们可以将其打包并部署到Servlet容器中。通过将打包的war文件部署到[Jetty](https://www.baeldung.com/deploy-to-jetty)、[Tomcat](https://www.baeldung.com/tomcat-deploy-war)或任何支持Servlet 3.0的Servlet容器，可以很容易地验证每个组件的行为。

## 3. 在Spring Boot中使用@ServletComponentScan

你可能想知道，既然我们可以在大多数Servlet容器中使用这些注解而无需任何配置，为什么我们需要@ServletComponentScan？问题在于嵌入式Servlet容器。

由于嵌入式容器不支持@WebServlet、@WebFilter和@WebListener注解，Spring Boot非常依赖嵌入式容器，引入了这个新的注解@ServletComponentScan来支持一些使用这3个注解的依赖jar。

详细讨论可以在[Github上的这个issues](https://github.com/spring-projects/spring-boot/issues/2290)中找到。

### 3.1 Maven依赖项

要使用@ServletComponentScan，我们需要1.3.0或更高版本的Spring Boot，让我们将最新版本的[spring-boot-starter-parent](https://search.maven.org/search?q=a:spring-boot-starter-web)和[spring-boot-starter-web](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-web)添加到pom中：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
    <relativePath /> <!-- lookup parent from repository -->
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>2.4.0</version>
    </dependency>
</dependencies>
```

### 3.2 使用@ServletComponentScan

Spring Boot应用程序非常简单，我们添加@ServletComponentScan以启用对@WebFilter、@WebListener和@WebServlet的扫描：

```java
@ServletComponentScan
@SpringBootApplication
public class SpringBootAnnotatedApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAnnotatedApp.class, args);
    }
}
```

无需对之前的Web应用程序进行任何更改，它就可以正常工作：

```java
@Autowired private TestRestTemplate restTemplate;

@Test
public void givenServletFilter_whenGetHello_thenRequestFiltered() {
    ResponseEntity<String> responseEntity = restTemplate.getForEntity("/hello", String.class);
 
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("filtering hello", responseEntity.getBody());
}
```

```java
@Autowired private ServletContext servletContext;

@Test
public void givenServletContext_whenAccessAttrs_thenFoundAttrsPutInServletListner() {
    assertNotNull(servletContext);
    assertNotNull(servletContext.getAttribute("servlet-context-attr"));
    assertEquals("test", servletContext.getAttribute("servlet-context-attr"));
}
```

### 3.3 指定要扫描的包

默认情况下，@ServletComponentScan将从被标注的类的包中扫描，要指定要扫描的包，我们可以使用它的属性：

-   value
-   basePackages
-   basePackageClasses

默认属性value是basePackages的别名。

假设我们的SpringBootAnnotatedApp在cn.tuyucheng.taketoday.annotation包下，并且我们想扫描上面web应用程序创建的cn.tuyucheng.taketoday.annotation.components包中的类，以下配置是等效的：

```java
@ServletComponentScan
```
```java
@ServletComponentScan("cn.tuyucheng.taketoday.annotation.components")
```
```java
@ServletComponentScan(basePackages = "cn.tuyucheng.taketoday.annotation.components")
```
```java
@ServletComponentScan(basePackageClasses = {AttrListener.class, HelloFilter.class, HelloServlet.class})
```

## 4. 底层原理

@ServletComponentScan注解由[ServletComponentRegisteringPostProcessor](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/web/servlet/ServletComponentRegisteringPostProcessor.java)处理。在为@WebFilter、@WebListener和@WebServlet注解扫描指定包后，[ServletComponentHandlers](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/web/servlet/ServletComponentHandler.java)列表将处理它们的注解属性，并注册扫描的bean：

```java
class ServletComponentRegisteringPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static final List<ServletComponentHandler> HANDLERS;

    static {
        List<ServletComponentHandler> handlers = new ArrayList<>();
        handlers.add(new WebServletHandler());
        handlers.add(new WebFilterHandler());
        handlers.add(new WebListenerHandler());
        HANDLERS = Collections.unmodifiableList(handlers);
    }

    //...

    private void scanPackage(ClassPathScanningCandidateComponentProvider componentProvider, String packageToScan){
        //...
        for (ServletComponentHandler handler : HANDLERS) {
            handler.handle(((ScannedGenericBeanDefinition) candidate),
                  (BeanDefinitionRegistry) this.applicationContext);
        }
    }
}
```

正如[官方Javadoc](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/ServletComponentScan.html)中所说，**@ServletComponentScan注解仅适用于嵌入式Servlet容器**，这是Spring Boot默认自带的。

## 5. 总结

在本文中，我们介绍了@ServletComponentScan以及如何使用它来支持依赖于任何注解的应用程序：@WebServlet、@WebFilter、@WebListener。