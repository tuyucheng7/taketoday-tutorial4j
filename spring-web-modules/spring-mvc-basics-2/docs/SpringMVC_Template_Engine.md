## 1. 概述

Spring Web框架是围绕MVC(模型-视图-控制器)模式构建的，这使得在应用程序中分离关注点变得更加容易。
允许使用不同的视图技术，从成熟的JSP技术到各种模板引擎。

在本文中，我们介绍可以与Spring一起使用的主要模板引擎、它们的配置和使用示例。

## 2. Spring视图技术

考虑到Spring MVC应用程序中的关注点是完全分离的，从一种视图技术切换到另一种视图技术主要是配置问题。

为了呈现每种视图类型，我们需要定义一个与每种技术对应的ViewResolver bean。
这意味着我们可以从@Controller映射方法返回视图名称，就像我们通常返回JSP文件一样。

在下面的小节中，我们介绍更传统的技术，例如JSP，以及可以与Spring一起使用的主要模板引擎：Thymeleaf、Groovy、FreeMarker、Jade。

对于其中的每一个，我们会介绍标准Spring应用程序和使用Spring Boot构建的应用程序中所需的配置。

## 3. JSP

JSP是Java应用程序最流行的视图技术之一，Spring开箱即用地支持它。
对于渲染JSP文件，一种常用的ViewResolver bean类型是InternalResourceViewResolver：

```java
@EnableWebMvc
@Configuration
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Bean
    public ViewResolver jspViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/views/");
        bean.setSuffix(".jsp");
        return bean;
    }
}
```

接下来，我们可以开始在/WEB-INF/views文件夹中创建JSP文件：

```html
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>User Registration</title>
</head>
<body>
<form:form method="POST" modelAttribute="user" action="register">
    <form:label path="email">Email:</form:label>
    <form:input path="email" type="text"/>
    <br/>
    <form:label path="password">Password:</form:label>
    <form:input path="password" type="password"/>
    <br/>
    <input type="submit" value="Submit"/>
</form:form>
</body>
</html>
```

如果我们将文件添加到Spring Boot应用程序，那么我们可以在application.properties文件中定义以下属性：

```properties
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

基于这些属性，Spring Boot会自动配置必要的ViewResolver。

## 4. Thymeleaf

Thymeleaf是一个Java模板引擎，可以处理HTML、XML、文本、JavaScript或CSS文件。
与其他模板引擎不同，Thymeleaf允许将模板用作原型，这意味着它们可以被视为静态文件。

### 4.1 Maven依赖

要将Thymeleaf与Spring集成，我们需要添加thymeleaf和thymeleaf-spring5依赖项：

```xml
<!-- @formatter:off -->
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.0.12.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.12.RELEASE</version>
</dependency>
<!-- @formatter:on -->
```

### 4.2 Spring配置

接下来，我们需要添加SpringTemplateEngine bean的配置，以及指定视图文件的位置和类型的TemplateResolver bean。

SpringResourceTemplateResolver集成了Spring的资源解析机制：

```java
@Configuration
@EnableWebMvc
public class ThymeleafConfiguration {

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        return templateResolver;
    }
}
```

此外，我们需要一个ThymeleafViewResolver类型的ViewResolver bean：

```java
public class ThymeleafConfiguration {

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }
}
```

### 4.3 Thymeleaf模板

现在我们可以在WEB-INF/views文件夹中添加一个HTML文件：

```html
<html>
<head>
    <meta charset="UTF-8"/>
    <title>User Registration</title>
</head>
<body>
<form action="#" method="post" th:action="@{/register}" th:object="${user}">
    Email:<input th:field="*{email}" type="text"/> <br/>
    Password:<input th:field="*{password}" type="password"/> <br/>
    <input type="submit" value="Submit"/>
</form>
</body>
</html>
```

Thymeleaf模板在语法上与HTML模板非常相似。

在Spring应用程序中使用Thymeleaf时可用的一些功能包括：

+ 支持定义表单行为
+ 将表单输入绑定到数据模型
+ 表单输入验证
+ 显示来自消息源的值
+ 渲染模板片段

你可以在[Thymeleaf in Spring MVC]()一文中阅读有关使用Thymeleaf模板的更多信息。

### 4.4 Spring Boot中的Thymeleaf

Spring Boot通过添加spring-boot-starter-thymeleaf依赖项为Thymeleaf提供自动配置：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.6.1</version>
</dependency>
```

无需任何其他显式配置，默认情况下，HTML文件应放置在resources/templates文件夹中。

## 5. FreeMarker

FreeMarker是由Apache软件基金会构建的基于Java的模板引擎。
它可用于生成网页，也可用于生成源代码、XML文件、配置文件、电子邮件和其他基于文本的格式。

生成是基于使用FreeMarker模板语言编写的模板文件完成的。

### 5.1 Maven依赖

我们需要添加freemarker依赖项：

```xml
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.27-incubating</version>
</dependency>
```

对于Spring集成，我们还需要spring-context-support依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>5.3.13</version>
</dependency>
```

### 5.2 Spring配置

将FreeMarker与Spring MVC集成需要定义一个FreemarkerConfigurer bean，它指定模板文件的位置：

```java
@Configuration
@EnableWebMvc
public class FreemarkerConfiguration {

    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("/WEB-INF/views/");
        return freeMarkerConfigurer;
    }
}
```

接下来，我们需要定义一个FreeMarkerViewResolver类型的ViewResolver bean：

```java
public class FreemarkerConfiguration {

    @Bean
    public FreeMarkerViewResolver freemarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(true);
        resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        return resolver;
    }
}
```

### 5.3 FreeMarker模板

我们可以在WEB-INF/views文件夹中使用FreeMarker创建一个HTML模板：

```html
<#import "/spring.ftl" as spring/>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>User Registration</title>
</head>
<body>
<form action="register" method="post">
    <@spring.bind path="user" />
    Email:<@spring.formInput "user.email"/> <br/>
    Password:<@spring.formPasswordInput "user.password"/> <br/>
    <input type="submit" value="Submit"/>
</form>
</body>
</html>
```

在上面的代码中，我们导入了一组由Spring定义的宏，用于处理FreeMarker中的表单，包括将表单输入绑定到数据模型。

此外，FreeMarker模板语言包含大量标签、指令和表达式，用于处理集合、流控制结构、逻辑运算符、格式化和解析字符串、数字和其他许多功能。

### 5.4 Spring Boot中的FreeMarker

在Spring Boot应用程序中，我们可以通过使用spring-boot-starter-freemarker依赖项来简化所需的配置：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
    <version>2.6.1</version>
</dependency>
```

该启动器添加了必要的自动配置，我们需要做的就是将模板文件放在resources/templates文件夹中。

## 6. Groovy

Spring MVC视图也可以使用Groovy Markup模板引擎生成。
该引擎基于生成器语法，可用于生成任何文本格式。

### 6.1 Maven依赖

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-templates</artifactId>
    <version>2.4.12</version>
</dependency>
```

### 6.2 Spring配置

Markup模板引擎与Spring MVC的集成需要定义一个GroovyMarkupConfigurer bean和一个GroovyMarkupViewResolver类型的ViewResolver：

```java
@Configuration
@EnableWebMvc
public class GroovyConfiguration {

    @Bean
    public GroovyMarkupConfigurer groovyMarkupConfigurer() {
        GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
        configurer.setResourceLoaderPath("/WEB-INF/views/");
        return configurer;
    }

    @Bean
    public GroovyMarkupViewResolver thymeleafViewResolver() {
        GroovyMarkupViewResolver viewResolver = new GroovyMarkupViewResolver();
        viewResolver.setSuffix(".tpl");
        return viewResolver;
    }
}
```

### 6.3 Groovy Markup模板

模板是用Groovy语言编写的，具有以下几个特点：

+ 它们被编译成字节码
+ 它们包含对片段和布局的支持
+ 他们为国际化提供支持
+ 渲染速度很快

让我们为“用户注册”表单创建一个Groovy模板，其中包括数据绑定：

```groovy
yieldUnescaped '<!DOCTYPE html>'
html(lang: 'en') {
    head {
        meta('http-equiv': '"Content-Type" content="text/html; charset=utf-8"')
        title('User Registration')
    }
    body {
        form(id: 'userForm', action: 'register', method: 'post') {
            label(for: 'email', 'Email')
            input(name: 'email', type: 'text', value: user.email ?: '')
            label(for: 'password', 'Password')
            input(name: 'password', type: 'password', value: user.password ?: '')
            div(class: 'form-actions') {
                input(type: 'submit', value: 'Submit')
            }
        }
    }
}
```

### 6.4 Spring Boot中的Groovy模板引擎

Spring Boot包含Groovy模板引擎的自动配置，它是通过包含spring-boot-starter-groovy-templates依赖项启用的：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-groovy-templates</artifactId>
    <version>2.6.1</version>
</dependency>
```

模板的默认位置是/resources/templates。

## 7. Jade4j

Jade4j是用于Javascript的Pug模板引擎(最初称为Jade)的Java实现，Jade4j模板可用于生成HTML文件。

### 7.1 Maven依赖

对于Spring集成，我们需要添加spring-jade4j依赖项：

```xml
<dependency>
    <groupId>de.neuland-bfi</groupId>
    <artifactId>spring-jade4j</artifactId>
    <version>1.2.5</version>
</dependency>
```

### 7.2 Spring配置

要将Jade4j与Spring一起使用，我们必须定义一个配置模板位置的SpringTemplateLoader bean，以及一个JadeConfiguration bean：

```java
public class JadeTemplateConfiguration {

    @Bean
    public JadeConfiguration jadeConfiguration() {
        JadeConfiguration configuration = new JadeConfiguration();
        configuration.setCaching(false);
        configuration.setTemplateLoader(templateLoader());
        return configuration;
    }

    @Bean
    public SpringTemplateLoader templateLoader() {
        SpringTemplateLoader templateLoader = new SpringTemplateLoader();
        templateLoader.setBasePath("/WEB-INF/views/");
        templateLoader.setSuffix(".jade");
        return templateLoader;
    }
}
```

接下来，我们需要定义类型为JadeViewResolver的ViewResolver bean：

```java
public class JadeTemplateConfiguration {

    @Bean
    public ViewResolver viewResolver() {
        JadeViewResolver viewResolver = new JadeViewResolver();
        viewResolver.setConfiguration(jadeConfiguration());
        return viewResolver;
    }
}
```

### 7.3 Jade4j模板

Jade4j模板的特点是易于使用的区分空格的语法：

```jade
doctype html
html
    head
        title User Registration
    body
        form(action="register" method="post" )
            label(for="email") Email:
            input(type="text" name="email")
            label(for="password") Password:
            input(type="password" name="password")
            input(type="submit" value="Submit")
```

该项目还提供了一个非常有用的[交互式文档](https://naltatis.github.io/jade-syntax-docs/)，你可以在编写模板时查看模板的输出。

Spring Boot不提供Jade4j启动器，因此在Spring Boot项目中，我们必须添加与上面定义的相同Spring配置。

## 8. 其他模板引擎

除了到目前为止描述的模板引擎之外，还有很多模板引擎可以使用。

[Velocity](https://velocity.apache.org/)是一个比较老的模板引擎，
它非常复杂，缺点是Spring自4.3版以来已弃用它，并在Spring 5.0.1中完全删除。

[JMustache](https://github.com/samskivert/jmustache)是一个模板引擎，
可以通过使用spring-boot-starter-mustache依赖项轻松集成到Spring Boot应用程序中。

[Pebble](https://pebbletemplates.io/)在其库中包含对Spring和Spring Boot的支持。

也可以使用在JSR-223脚本引擎(例如[Nashorn](https://openjdk.org/projects/nashorn/))之上运行的其他模板库，
如[Handlebars](https://handlebarsjs.com/)或[React](https://reactjs.org/)。

## 9. 总结

在本文中，我们介绍了一些最常见的Spring Web应用程序模板引擎。