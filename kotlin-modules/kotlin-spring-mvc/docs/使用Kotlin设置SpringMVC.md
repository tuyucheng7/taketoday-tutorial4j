## 1. 概述

在本快速教程中，我们将了解如何使用 Kotlin 语言创建一个简单的 Spring MVC 项目。

本文重点介绍 Spring MVC。我们的文章 [Spring Boot 和 Kotlin](https://www.baeldung.com/spring-boot-kotlin) 描述了如何使用 Kotlin 设置 Spring Boot 应用程序。

## 2.专家

对于 Maven 配置，我们需要添加以下[Kotlin 依赖](https://search.maven.org/classic/#search|ga|1|a%3A"kotlin-stdlib-jre8")项：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jre8</artifactId>
    <version>1.1.4</version>
</dependency>
```

我们还需要添加以下[Spring 依赖](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND (a%3A"spring-web" OR a%3A"spring-webmvc"))项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>4.3.10.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>4.3.10.RELEASE</version>
</dependency>
```

要编译我们的代码，我们需要指定源目录并在pom.xml的构建部分配置[Kotlin Maven 插件](https://search.maven.org/classic/#search|ga|1|g%3A"org.jetbrains.kotlin" AND a%3A"kotlin-maven-plugin")：

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>1.1.4</version>
    <executions>
        <execution>
            <id>compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 3.Spring MVC配置

我们可以使用 Kotlin 注解配置或 XML 配置。

### 3.1. 科特林配置

注解配置非常简单。我们设置视图控制器、模板解析器和模板引擎。此后我们可以使用它们来配置视图解析器：

```java
@EnableWebMvc
@Configuration
open class ApplicationWebConfig : WebMvcConfigurerAdapter(), 
  ApplicationContextAware {

    private var applicationContext: ApplicationContext? = null

    override fun setApplicationContext(applicationContext: 
      ApplicationContext?) {
        this.applicationContext = applicationContext
    }

    override fun addViewControllers(registry:
      ViewControllerRegistry?) {
        super.addViewControllers(registry)

        registry!!.addViewController("/welcome.html")
    }
    @Bean
    open fun templateResolver(): SpringResourceTemplateResolver {
        return SpringResourceTemplateResolver()
          .apply { prefix = "/WEB-INF/view/" }
          .apply { suffix = ".html"}
          .apply { templateMode = TemplateMode.HTML }
          .apply { setApplicationContext(applicationContext) }
    }

    @Bean
    open fun templateEngine(): SpringTemplateEngine {
        return SpringTemplateEngine()
          .apply { setTemplateResolver(templateResolver()) }
    }

    @Bean
    open fun viewResolver(): ThymeleafViewResolver {
        return ThymeleafViewResolver()
          .apply { templateEngine = templateEngine() }
          .apply { order = 1 }
    }
}
```

接下来，让我们创建一个ServletInitializer类。该类应扩展AbstractAnnotationConfigDispatcherServletInitializer。这是传统web.xml配置的替代品：

```java
class ApplicationWebInitializer: 
  AbstractAnnotationConfigDispatcherServletInitializer() {

    override fun getRootConfigClasses(): Array<Class<>>? {
        return null
    }

    override fun getServletMappings(): Array<String> {
        return arrayOf("/")
    }

    override fun getServletConfigClasses(): Array<Class<>> {
        return arrayOf(ApplicationWebConfig::class.java)
    }
}
```

### 3.2. XML配置

ApplicationWebConfig类的 XML 等价物是：

```xml
<beans xmlns="...">
    <context:component-scan base-package="com.baeldung.kotlin.mvc" />

    <mvc:view-controller path="/welcome.html"/>

    <mvc:annotation-driven />

    <bean id="templateResolver" 
      class="org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver">
        <property name="prefix" value="/WEB-INF/view/" />
        <property name="suffix" value=".html" />
        <property name="templateMode" value="HTML" />
    </bean>

    <bean id="templateEngine"
          class="org.thymeleaf.spring4.SpringTemplateEngine">
        <property name="templateResolver" ref="templateResolver" />
    </bean>


    <bean class="org.thymeleaf.spring4.view.ThymeleafViewResolver">
        <property name="templateEngine" ref="templateEngine" />
        <property name="order" value="1" />
    </bean>

</beans>
```

在这种情况下，我们还必须指定web.xml配置：

```xml
<web-app xmlns=...>

    <display-name>Spring Kotlin MVC Application</display-name>

    <servlet>
        <servlet-name>spring-web-mvc</servlet-name>
        <servlet-class>
            org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
        <load-on-startup>1</load-on-startup>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/spring-web-config.xml</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>spring-web-mvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

## 4. HTML 视图

相应的 HTML 资源位于/WEB-INF/view目录下。在上面的视图控制器配置中，我们定义了一个基本的视图控制器，welcome.html。对应资源的内容为：

```html
<html>
    <head>Welcome</head>

    <body>
        <h1>Body of the welcome view</h1>
    </body>
</html>
```

## 5.总结

运行项目后，我们可以在http://localhost:8080/welcome.html访问配置好的欢迎页面。

在本文中，我们使用 Kotlin 和 XML 配置配置了一个简单的 Spring MVC 项目。