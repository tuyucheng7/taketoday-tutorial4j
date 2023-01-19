## 1. 概述

本文介绍如何在Spring MVC项目中实现Content Negotiation。

通常，有三种方式可以确定请求的媒体类型：

+ (已弃用)在请求中使用URL后缀(扩展名)(例如.xml/.json)
+ 在请求中使用URL参数(例如?format=json)
+ 在请求中使用Accept头

默认情况下，这是Spring Content Negotiation管理器尝试使用这三种策略的顺序。如果这些都未启用，我们可以指定默认内容类型。

## 2. Content Negotiation策略

首先我们需要添加必要的依赖，由于我们使用JSON和XML表示，因此在本文中，我们将使用Jackson来表示JSON：

```text
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.13.0</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

## 3. URL后缀策略

在Spring Boot 2.6.x版本中，将请求路径与注册的Spring MVC HandlerMapping匹配的默认策略已从AntPathMatcher更改为PathPatternParser。

并且由于PathPatternParser不支持后缀模式匹配，所以在使用该策略之前，我们首先需要使用之前的路径匹配器，即AntPathMatcher。

我们可以在application.properties文件中添加spring.mvc.pathmatch.matching-strategy属性将默认路径匹配器切换回AntPathMatcher。

默认情况下，此策略是禁用的，我们需要通过在application.properties中将spring.mvc.pathmatch.use-suffix-pattern设置为true来启用它：

```properties
spring.mvc.pathmatch.use-suffix-pattern=true
spring.mvc.pathmatch.matching-strategy=ant-path-matcher
```

启用后，框架可以直接从URL检查路径扩展，以确定输出内容类型。

在进行配置之前，让我们快速浏览一个例子。在典型的Spring控制器中有以下简单的API方法实现：

```java

@Controller
public class EmployeeController {

    Map<Long, Employee> employeeMap = new HashMap<>();

    @RequestMapping(value = "/employee/{Id}", produces = {"application/json", "application/xml"}, method = RequestMethod.GET)
    public @ResponseBody Employee getEmployeeById(@PathVariable final Long Id) {
        return employeeMap.get(Id);
    }
}
```

让我们通过使用JSON扩展来指定资源的媒体类型来调用它：

```shell
curl http://localhost:8080/spring-mvc-basics/employee/1.json
```

如果我们使用JSON扩展，会得到以下结果：

```json
{
    "id": 1,
    "name": "John",
    "contactNumber": "223334411",
    "workingArea": "rh"
}
```

下面是使用XML的例子：

```shell
curl http://localhost:8080/spring-mvc-basics/employee/1.xml
```

响应体：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<employee>
    <contactNumber>223334411</contactNumber>
    <id>1</id>
    <name>John</name>
    <workingArea>rh</workingArea>
</employee>
```

如果我们不使用任何扩展名或使用未配置的扩展名，则将返回默认内容类型：

```shell
curl http://localhost:8080/spring-mvc-basics/employee/1
```

现在让我们看看如何同时使用Java和XML配置这个策略。

### 3.1 Java配置

```java

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true).
                favorParameter(false).
                ignoreAcceptHeader(true).
                useJaf(false).
                defaultContentType(MediaType.APPLICATION_JSON);
    }
}
```

首先，我们启用路径扩展策略。值得一提的是，从Spring 5.2.4开始，
不推荐使用favoritePathExtension(boolean)方法，以阻止使用路径扩展进行Content Negotiation。

然后，我们禁用URL参数策略以及Accept头策略，因为我们只想依赖路径扩展方式来确定内容的类型。

然后我们关闭Java Activation Framework；如果传入的请求与我们配置的任何策略都不匹配，JAF可以用作选择输出格式的后备机制。
我们禁用它是因为我们要将JSON配置为默认内容类型。请注意，从Spring 5开始不推荐使用useJaf()方法。

最后，我们将JSON设置为默认值。这意味着如果这两种策略都不匹配，则所有传入的请求都将映射到返回JSON的控制器方法。

### 3.2 XML配置

下面是等效的XML配置：

```xml

<bean id="contentNegotiationManager"
      class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean">
    <property name="favorPathExtension" value="true"/>
    <property name="favorParameter" value="false"/>
    <property name="ignoreAcceptHeader" value="true"/>
    <property name="defaultContentType" value="application/json"/>
    <property name="useJaf" value="false"/>
</bean>
```

## 4. URL参数策略

我们在上一节中使用了路径扩展，现在让我们设置Spring MVC来使用路径参数。

我们可以通过将favorParameter属性的值设置为true来启用此策略。

让我们快速了解一下这如何与我们之前的示例一起使用：

```shell
curl http://localhost:8080/spring-mvc-basics/employee/1?mediaType=json
```

以下是JSON响应正文的内容：

```json
{
    "id": 1,
    "name": "John",
    "contactNumber": "223334411",
    "workingArea": "rh"
}
```

如果我们使用XML参数，输出将是XML格式：

```shell
curl http://localhost:8080/spring-mvc-basics/employee/1?mediaType=xml
```

响应主体：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<employee>
    <contactNumber>223334411</contactNumber>
    <id>1</id>
    <name>John</name>
    <workingArea>rh</workingArea>
</employee>
```

### 4.1 Java配置

```java

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).
                favorParameter(true).
                parameterName("mediaType").
                ignoreAcceptHeader(true).
                useJaf(false).
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType("xml", MediaType.APPLICATION_XML).
                mediaType("json", MediaType.APPLICATION_JSON);
    }
}
```

在这里，路径扩展和Accept头策略被禁用(以及JAF)，其余配置相同。

### 4.2 XML配置

```xml

<bean id="contentNegotiationManager"
      class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean">
    <property name="favorPathExtension" value="false"/>
    <property name="favorParameter" value="true"/>
    <property name="parameterName" value="mediaType"/>
    <property name="ignoreAcceptHeader" value="true"/>
    <property name="defaultContentType" value="application/json"/>
    <property name="useJaf" value="false"/>

    <property name="mediaTypes">
        <map>
            <entry key="json" value="application/json"/>
            <entry key="xml" value="application/xml"/>
        </map>
    </property>
</bean>
```

此外，我们可以同时启用两种策略：

```java

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true).
                favorParameter(true).
                parameterName("mediaType").
                ignoreAcceptHeader(true).
                useJaf(false).
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType("xml", MediaType.APPLICATION_XML).
                mediaType("json", MediaType.APPLICATION_JSON);
    }
}
```

在这种情况下，Spring将首先查找路径扩展，如果不存在则查找路径参数。如果输入请求中这两个都不可用，则将返回默认内容类型。

## 5. Accept头策略

如果启用Accept头，Spring MVC将在传入的请求中获取其值以确定表示类型。

我们必须将ignoreAcceptHeader的值设置为false才能启用此方法，并且我们禁用其他两种策略，以便我们只依赖于Accept头。

### 5.1 Java配置

```java

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true).
                favorParameter(false).
                parameterName("mediaType").
                ignoreAcceptHeader(false).
                useJaf(false).
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType("xml", MediaType.APPLICATION_XML).
                mediaType("json", MediaType.APPLICATION_JSON);
    }
}
```

### 5.2 XML配置

```xml

<bean id="contentNegotiationManager"
      class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean">
    <property name="favorPathExtension" value="true"/>
    <property name="favorParameter" value="false"/>
    <property name="parameterName" value="mediaType"/>
    <property name="ignoreAcceptHeader" value="false"/>
    <property name="defaultContentType" value="application/json"/>
    <property name="useJaf" value="false"/>

    <property name="mediaTypes">
        <map>
            <entry key="json" value="application/json"/>
            <entry key="xml" value="application/xml"/>
        </map>
    </property>
</bean>
```

最后，我们需要通过将contentNegotiationManager添加到整体配置中来启用它：

```xml

<mvc:annotation-driven content-negotiation-manager="contentNegotiationManager"/>
```

## 6. 总结

我们研究了Spring MVC中的Content Negotiation是如何工作的，并重点介绍了一些设置它以使用各种策略来确定内容类型的示例。