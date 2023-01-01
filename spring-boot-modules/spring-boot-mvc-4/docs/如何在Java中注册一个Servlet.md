## 一、简介

本文将概述如何在 Jakarta EE 和 Spring Boot 中注册 servlet。具体来说，我们将研究两种在 Jakarta EE 中注册 Java Servlet 的方法——一种使用web.xml文件，另一种使用注解。然后我们将使用 XML 配置、Java 配置并通过可配置属性在 Spring Boot 中注册 servlet。

可以在[此处](https://www.baeldung.com/intro-to-servlets)找到一篇关于 servlet 的精彩介绍性文章。

## 2. 在 Jakarta EE 中注册 Servlet

让我们来看看在 Jakarta EE 中注册 servlet 的两种方法。首先，我们可以通过web.xml注册一个 servlet 。或者，我们可以使用 Jakarta EE @WebServlet注解。

### 2.1. 通过web.xml

在 Jakarta EE 应用程序中注册 servlet 的最常见方法是将其添加到web.xml文件中：

```xml
<welcome-file-list>
    <welcome-file>index.html</welcome-file>
    <welcome-file>index.htm</welcome-file>
    <welcome-file>index.jsp</welcome-file>
</welcome-file-list>
<servlet>
    <servlet-name>Example</servlet-name>
    <servlet-class>com.baeldung.Example</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>Example</servlet-name>
    <url-pattern>/Example</url-pattern>
</servlet-mapping>
```

如你所见，这涉及两个步骤：(1) 将我们的 servlet 添加到servlet标记，确保还指定 servlet 所在类的源路径，以及 (2) 指定 servlet 将公开的 URL 路径在url-pattern标签中。

Jakarta EE web.xml文件通常位于WebContent/WEB-INF中。

### 2.2. 通过注解

现在让我们在自定义 servlet 类上使用@WebServlet注解注册我们的 servlet。这消除了server.xml中的 servlet 映射和web.xml中的 servlet 注册的需要：

```java
@WebServlet(
  name = "AnnotationExample",
  description = "Example Servlet Using Annotations",
  urlPatterns = {"/AnnotationExample"}
)
public class Example extends HttpServlet {	
 
    @Override
    protected void doGet(
      HttpServletRequest request, 
      HttpServletResponse response) throws ServletException, IOException {
 
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<p>Hello World!</p>");
    }
}
```

上面的代码演示了如何将该注解直接添加到 servlet。该 servlet 仍可在与以前相同的 URL 路径中使用。

## 3.在Spring Boot中注册Servlets

现在我们已经展示了如何在 Jakarta EE 中注册 servlet，让我们来看看在 Spring Boot 应用程序中注册 servlet 的几种方法。

### 3.1. 程序化注册

Spring Boot 支持 Web 应用程序的 100% 编程配置。

首先，我们将实现WebApplicationInitializer接口，然后实现 WebMvcConfigurer接口，它允许你覆盖预设的默认值，而不必指定每个特定的配置设置，从而节省你的时间并允许你使用几个经过验证的真实设置-开箱即用。

让我们看一个示例WebApplicationInitializer实现：

```java
public class WebAppInitializer implements WebApplicationInitializer {
 
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext ctx
          = new AnnotationConfigWebApplicationContext();
        ctx.register(WebMvcConfigure.class);
        ctx.setServletContext(container);

        ServletRegistration.Dynamic servlet = container.addServlet(
          "dispatcherExample", new DispatcherServlet(ctx));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
     }
}
```

接下来我们来实现WebMvcConfigurer 接口：

```java
@Configuration
public class WebMvcConfigure implements WebMvcConfigurer {

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver
          = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void configureDefaultServletHandling(
      DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/")
          .addResourceLocations("/resources/").setCachePeriod(3600)
          .resourceChain(true).addResolver(new PathResourceResolver());
    }
}
```

上面我们明确指定了 JSP servlet 的一些默认设置，以支持.jsp视图和静态资源服务。

### 3.2. XML配置

在 Spring Boot 中配置和注册 servlet 的另一种方法是通过web.xml：

```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/spring/dispatcher.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

用于在 Spring 中指定配置的web.xml与 Jakarta EE 中的类似。在上面，你可以看到我们如何通过servlet标记下的属性指定更多参数。

这里我们使用另一个XML来完成配置：

```xml
<beans ...>
    
    <context:component-scan base-package="com.baeldung"/>

    <bean 
      class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
</beans>
```

请记住，你的 Spring web.xml通常位于src/main/webapp/WEB-INF中。

### 3.3. 结合 XML 和程序化注册

让我们将 XML 配置方法与 Spring 的编程配置相结合：

```java
public void onStartup(ServletContext container) throws ServletException {
   XmlWebApplicationContext xctx = new XmlWebApplicationContext();
   xctx.setConfigLocation('classpath:/context.xml');
   xctx.setServletContext(container);

   ServletRegistration.Dynamic servlet = container.addServlet(
     "dispatcher", new DispatcherServlet(ctx));
   servlet.setLoadOnStartup(1);
   servlet.addMapping("/");
}
```

我们还配置调度程序 servlet：

```xml
<beans ...>

    <context:component-scan base-package="com.baeldung"/>
    <bean class="com.baeldung.configuration.WebAppInitializer"/>
</beans>
```

### 3.4. Bean注册

我们还可以使用ServletRegistrationBean以编程方式配置和注册我们的 servlet 。下面我们将这样做以注册一个HttpServlet(它实现了javax.servlet.Servlet接口)：

```java
@Bean
public ServletRegistrationBean exampleServletBean() {
    ServletRegistrationBean bean = new ServletRegistrationBean(
      new CustomServlet(), "/exampleServlet/");
    bean.setLoadOnStartup(1);
    return bean;
}
```

这种方法的主要优点是它使你能够将多个 servlet 以及不同种类的 servlet 添加到你的 Spring 应用程序。

我们将使用一个更简单的HttpServlet子类实例，而不是仅仅使用DispatcherServlet，它是一种更具体的HttpServlet ，也是我们在 3.1 节中探索的WebApplicationInitializer编程配置方法中最常用的一种，它公开了四个基本的HttpRequest操作通过四个函数：doGet()、doPost()、doPut()和doDelete()，就像在 Jakarta EE 中一样。

请记住，HttpServlet 是一个抽象类(因此无法实例化)。不过，我们可以轻松地创建一个自定义扩展：

```java
public class CustomServlet extends HttpServlet{
    ...
}
```

## 4. 使用属性注册 Servlet

另一种虽然不常见的配置和注册 servlet 的方法是使用通过PropertyLoader、PropertySource或PropertySources实例对象加载到应用程序中的自定义属性文件。

这提供了一种中间类型的配置和以其他方式自定义application.properties的能力，这为非嵌入式 servlet 提供了很少的直接配置。

### 4.1. 系统属性方法

我们可以将一些自定义设置添加到我们的application.properties文件或另一个属性文件中。让我们添加一些设置来配置我们的DispatcherServlet：

```plaintext
servlet.name=dispatcherExample
servlet.mapping=/dispatcherExampleURL
```

让我们将自定义属性加载到我们的应用程序中：

```java
System.setProperty("custom.config.location", "classpath:custom.properties");
```

现在我们可以通过以下方式访问这些属性：

```java
System.getProperty("custom.config.location");
```

### 4.2. 自定义属性方法

让我们从custom.properties文件开始：

```plaintext
servlet.name=dispatcherExample
servlet.mapping=/dispatcherExampleURL
```

然后我们可以使用普通的 Property Loader：

```java
public Properties getProperties(String file) throws IOException {
  Properties prop = new Properties();
  InputStream input = null;
  input = getClass().getResourceAsStream(file);
  prop.load(input);
  if (input != null) {
      input.close();
  }
  return prop;
}
```

现在我们可以将这些自定义属性作为常量添加到我们的WebApplicationInitializer实现中：

```java
private static final PropertyLoader pl = new PropertyLoader(); 
private static final Properties springProps
  = pl.getProperties("custom_spring.properties"); 

public static final String SERVLET_NAME
  = springProps.getProperty("servlet.name"); 
public static final String SERVLET_MAPPING
  = springProps.getProperty("servlet.mapping");
```

然后我们可以使用它们来配置我们的调度程序 servlet：

```java
ServletRegistration.Dynamic servlet = container.addServlet(
  SERVLET_NAME, new DispatcherServlet(ctx));
servlet.setLoadOnStartup(1);
servlet.addMapping(SERVLET_MAPPING);
```

这种方法的优点是不需要.xml维护，但具有易于修改的配置设置，不需要重新部署代码库。

### 4.3. PropertySource方法_

完成上述任务的更快方法是使用 Spring 的PropertySource，它允许访问和加载配置文件。

PropertyResolver是由ConfigurableEnvironment实现的接口，它使应用程序属性在 servlet 启动和初始化时可用：

```java
@Configuration 
@PropertySource("classpath:/com/yourapp/custom.properties") 
public class ExampleCustomConfig { 
    @Autowired 
    ConfigurableEnvironment env; 

    public String getProperty(String key) { 
        return env.getProperty(key); 
    } 
}
```

上面，我们将依赖项自动装配到类中并指定自定义属性文件的位置。然后我们可以通过调用传入字符串值的函数getProperty()来获取显着属性。

### 4.4. PropertySource 程序化方法

我们可以将上述方法(涉及获取属性值)与下面的方法(允许我们以编程方式指定这些值)结合起来：

```java
ConfigurableEnvironment env = new StandardEnvironment(); 
MutablePropertySources props = env.getPropertySources(); 
Map map = new HashMap(); map.put("key", "value"); 
props.addFirst(new MapPropertySource("Map", map));
```

我们已经创建了一个将键链接到值的映射，然后将该映射添加到PropertySources以根据需要启用调用。

## 5. 注册嵌入式 Servlet

最后，我们还将了解 Spring Boot 中嵌入式 servlet 的基本配置和注册。

嵌入式 servlet 提供完整的 Web 容器(Tomcat、Jetty 等)功能，而无需单独安装或维护 Web 容器。

你可以为简单的实时服务器部署添加所需的依赖项和配置，只要此类功能得到轻松、紧凑和快速的支持。

我们将只关注如何执行此 Tomcat，但可以对 Jetty 和替代方案采用相同的方法。

让我们在pom.xml中指定嵌入式 Tomcat 8 Web 容器的依赖项：

```xml
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
     <artifactId>tomcat-embed-core</artifactId>
     <version>8.5.11</version>
</dependency>
```

现在让我们添加成功将 Tomcat 添加到Maven 在构建时生成的.war所需的标签：

```xml
<build>
    <finalName>embeddedTomcatExample</finalName>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>appassembler-maven-plugin</artifactId>
            <version>2.0.0</version>
            <configuration>
                <assembleDirectory>target</assembleDirectory>
                <programs>
                    <program>
                        <mainClass>launch.Main</mainClass>
                        <name>webapp</name>
                    </program>
            </programs>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>assemble</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

如果你使用的是 Spring Boot，则可以将 Spring 的spring-boot-starter-tomcat依赖项添加到你的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

### 5.1. 通过属性注册

Spring Boot 支持通过application.properties配置大多数可能的 Spring 设置。在将必要的嵌入式 servlet 依赖项添加到你的pom.xml之后，你可以使用几个这样的配置选项来自定义和配置你的嵌入式 servlet：

```plaintext
server.jsp-servlet.class-name=org.apache.jasper.servlet.JspServlet 
server.jsp-servlet.registered=true
server.port=8080
server.servlet-path=/
```

以上是一些可用于配置DispatcherServlet和静态资源共享的应用程序设置。嵌入式 servlet、SSL 支持和会话的设置也可用。

配置参数确实太多，无法在此处列出，但你可以在[Spring Boot 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)中查看完整列表。

### 5.2. 通过 YAML 配置

同样，我们可以使用 YAML 配置我们的嵌入式 servlet 容器。这需要使用专门的 YAML 属性加载器——YamlPropertySourceLoader——它公开我们的 YAML 并使其中的键和值可在我们的应用程序中使用。

```java
YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
PropertySource<?> yamlProps = sourceLoader.load("yamlProps", resource, null);
```

### 5.3. 通过 TomcatEmbeddedServletContainerFactory 进行编程配置

嵌入式 servlet 容器的编程配置可以通过EmbeddedServletContainerFactory的子类实例实现。例如，你可以使用TomcatEmbeddedServletContainerFactory来配置嵌入式 Tomcat servlet。

TomcatEmbeddedServletContainerFactory包装org.apache.catalina.startup.Tomcat对象，提供额外的配置选项：

```java
@Bean
public ConfigurableServletWebServerFactory servletContainer() {
    TomcatServletWebServerFactory tomcatContainerFactory
      = new TomcatServletWebServerFactory();
    return tomcatContainerFactory;
}
```

然后我们可以配置返回的实例：

```java
tomcatContainerFactory.setPort(9000);
tomcatContainerFactory.setContextPath("/springboottomcatexample");
```

这些特定设置中的每一个都可以使用前面描述的任何方法进行配置。

我们也可以直接访问和操作org.apache.catalina.startup.Tomcat对象：

```java
Tomcat tomcat = new Tomcat();
tomcat.setPort(port);
tomcat.setContextPath("/springboottomcatexample");
tomcat.start();
```

## 六，总结

在本文中，我们回顾了在 Jakarta EE 和 Spring Boot 应用程序中注册 Servlet 的几种方法。