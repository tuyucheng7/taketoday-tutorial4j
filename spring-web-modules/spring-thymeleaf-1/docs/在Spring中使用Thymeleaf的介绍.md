## 1. 概述

[Thymeleaf](http://www.thymeleaf.org/)是一个Java模板引擎，用于处理和创建HTML、XML、JavaScript、CSS和文本。

在本教程中，我们将讨论如何在Spring MVC应用程序的视图层中使用Thymeleaf和Spring以及一些基本用例。

该库具有极强的可扩展性，其自然的模板功能确保我们可以在没有后端的情况下对模板进行原型设计。与其他流行的模板引擎(如JSP)相比，这使得开发速度非常快。

## 2. 将Thymeleaf与Spring集成

首先，让我们看看与Spring集成所需的配置。集成需要thymeleaf-spring库。

我们会将以下依赖项添加到我们的Maven POM文件中：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
```

请注意，对于Spring 4项目，我们必须使用thymeleaf-spring4库而不是thymeleaf-spring5。

Spring TemplateEngine类执行所有配置步骤。

我们可以在Java配置文件中将这个类配置为一个bean：

```java
@Bean
@Description("Thymeleaf Template Resolver")
public ServletContextTemplateResolver templateResolver() {
    ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
    templateResolver.setPrefix("/WEB-INF/views/");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode("HTML5");

    return templateResolver;
}

@Bean
@Description("Thymeleaf Template Engine")
public SpringTemplateEngine templateEngine() {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver());
    templateEngine.setTemplateEngineMessageSource(messageSource());
    return templateEngine;
}
```

templateResolver bean属性前缀和后缀分别指示视图页面在webapp目录中的位置及其文件扩展名。

Spring MVC中的ViewResolver接口将控制器返回的视图名称映射到实际的视图对象。ThymeleafViewResolver实现了ViewResolver接口，它用于在给定视图名称的情况下确定要呈现哪些Thymeleaf视图。

集成的最后一步是将ThymeleafViewResolver添加为bean：

```java
@Bean
@Description("Thymeleaf View Resolver")
public ThymeleafViewResolver viewResolver() {
    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
    viewResolver.setTemplateEngine(templateEngine());
    viewResolver.setOrder(1);
    return viewResolver;
}
```

## 3. Spring Boot中的Thymeleaf

Spring Boot通过添加[spring-boot-starter-thymeleaf](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)依赖项为Thymeleaf提供自动配置：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.3.3.RELEASE</version>
</dependency>
```

无需显式配置。默认情况下，HTML文件应放在资源/模板位置。

## 4. 显示来自消息源(属性文件)的值

我们可以使用th:text="#{key}"标签属性来显示属性文件中的值。

为此，我们需要将属性文件配置为messageSource bean：

```java
@Bean
@Description("Spring Message Resolver")
public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    return messageSource;
}
```

这是Thymeleaf HTML代码，用于显示与键welcome.message关联的值：

```html
<span th:text="#{welcome.message}" />
```

## 5. 显示模型属性

### 5.1 简单属性

我们可以使用th:text="${attributename}"标签属性来显示模型属性的值。

让我们在控制器类中添加一个名为serverTime的模型属性：

```java
model.addAttribute("serverTime", dateFormat.format(new Date()));
```

下面是显示serverTime属性值的HTML代码：

```html
Current time is <span th:text="${serverTime}" />
```

### 5.2 集合属性

如果模型属性是对象的集合，我们可以使用th:each标签属性对其进行迭代。

让我们定义一个包含两个字段id和name的Student模型类：

```java
public class Student implements Serializable {
    private Integer id;
    private String name;
    // standard getters and setters
}
```

现在我们将在控制器类中添加一个学生列表作为模型属性：

```java
List<Student> students = new ArrayList<Student>();
// logic to build student data
model.addAttribute("students", students);
```

最后，我们可以使用Thymeleaf模板代码迭代学生列表并显示所有字段值：

```html
<tbody>
    <tr th:each="student: ${students}">
        <td th:text="${student.id}" />
        <td th:text="${student.name}" />
    </tr>
</tbody>
```

## 6. 条件评估

### 6.1 if和unless

如果满足条件，我们使用th:if="${condition}"属性显示视图的一部分。如果不满足条件，我们使用th:unless="${condition}"属性显示视图的一部分。

让我们在Student模型中添加一个性别字段：

```java
public class Student implements Serializable {
    private Integer id;
    private String name;
    private Character gender;
    
    // standard getters and setters
}
```

假设该字段有两个可能的值(M或F)来指示学生的性别。

如果我们希望显示单词“Male”或“Female”而不是单个字符，我们可以使用以下Thymeleaf代码来实现：

```html
<td>
    <span th:if="${student.gender} == 'M'" th:text="Male" /> 
    <span th:unless="${student.gender} == 'M'" th:text="Female" />
</td>
```

### 6.2 switch和case

我们使用th:switch和th:case属性使用switch语句结构有条件地显示内容。

让我们使用th:switch和th:case属性重写之前的代码：

```html
<td th:switch="${student.gender}">
    <span th:case="'M'" th:text="Male" /> 
    <span th:case="'F'" th:text="Female" />
</td>
```

## 7. 处理用户输入

我们可以使用th:action="@{url}"和th:object="${object}"属性处理表单输入。我们使用th:action提供表单操作URL，使用th:object指定提交的表单数据将绑定到的对象。

使用th:field="{name}"属性映射各个字段，其中名称是对象的匹配属性。

对于Student类，我们可以创建一个输入表单：

```html
<form action="#" th:action="@{/saveStudent}" th:object="${student}" method="post">
    <table border="1">
        <tr>
            <td><label th:text="#{msg.id}" /></td>
            <td><input type="number" th:field="*{id}" /></td>
        </tr>
        <tr>
            <td><label th:text="#{msg.name}" /></td>
            <td><input type="text" th:field="*{name}" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
</form>
```

在上面的代码中，/saveStudent是表单操作URL，学生是保存提交的表单数据的对象。

StudentController类处理表单提交：

```java
@Controller
public class StudentController {
    @RequestMapping(value = "/saveStudent", method = RequestMethod.POST)
    public String saveStudent(@ModelAttribute Student student, BindingResult errors, Model model) {
        // logic to process input data
    }
}
```

@RequestMapping注解将控制器方法映射到表单中提供的URL。带注解的方法saveStudent()对提交的表单执行所需的处理。最后，@ModelAttribute注解将表单字段绑定到学生对象。

## 8. 显示验证错误

我们可以使用#fields.hasErrors()函数来检查字段是否有任何验证错误。我们使用#fields.errors()函数来显示特定字段的错误。字段名称是这两个函数的输入参数。

让我们看一下用于迭代并显示表单中每个字段的错误的HTML代码：

```html
<ul>
    <li th:each="err : ${#fields.errors('id')}" th:text="${err}" />
    <li th:each="err : ${#fields.errors('name')}" th:text="${err}" />
</ul>
```

上述函数不接受字段名，而是接受通配符或常量all来指示所有字段。我们使用th:each属性来迭代每个字段可能存在的多个错误。

这是使用通配符重写的先前HTML代码：

```html
<ul>
    <li th:each="err : ${#fields.errors('*')}" th:text="${err}" />
</ul>
```

在这里我们使用常量all：

```html
<ul>
    <li th:each="err : ${#fields.errors('all')}" th:text="${err}" />
</ul>
```

同样，我们可以使用全局常量在Spring中显示全局错误。

下面是显示全局错误的HTML代码：

```html
<ul>
    <li th:each="err : ${#fields.errors('all')}" th:text="${err}" />
</ul>
```

此外，我们可以使用th:errors属性来显示错误消息。

可以使用th:errors属性重写之前在表单中显示错误的代码：

```html
<ul>
    <li th:errors="*{id}" />
    <li th:errors="*{name}" />
</ul>
```

## 9. 使用转换

我们使用双括号语法{{}}来格式化数据以供显示。这使用了为上下文文件的conversionService bean中的该类型字段配置的格式化程序。

让我们看看如何格式化Student类中的名称字段：

```html
<tr th:each="student: ${students}">
    <td th:text="${{student.name}}" />
</tr>
```

上面的代码使用了NameFormatter类，通过覆盖WebMvcConfigurer接口中的addFormatters()方法进行配置。

为此，我们的@Configuration类覆盖了WebMvcConfigurerAdapter类：

```java
@Configuration
public class WebMVCConfig extends WebMvcConfigurerAdapter {
    // ...
    @Override
    @Description("Custom Conversion Service")
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new NameFormatter());
    }
}
```

NameFormatter类实现了SpringFormatter接口。

我们还可以使用#conversions实用程序来转换对象以供显示。效用函数的语法是#conversions.convert(Object, Class)，其中Object被转换为Class类型。

以下是如何显示已删除小数部分的学生对象百分比字段：

```html
<tr th:each="student: ${students}">
    <td th:text="${#conversions.convert(student.percentage, 'Integer')}" />
</tr>
```

## 10. 总结

在本文中，我们了解了如何在Spring MVC应用程序中集成和使用Thymeleaf。

我们还看到了如何显示字段、接受输入、显示验证错误以及转换数据以供显示的示例。