## 1. 简介

[Thymeleaf](http://www.thymeleaf.org/)是一个Java模板引擎，用于处理和创建 HTML、XML、JavaScript、CSS 和纯文本。有关Thymeleaf和Spring的介绍，请查看[这篇](https://www.baeldung.com/thymeleaf-in-spring-mvc)文章。

在本文中，我们将使用Thymeleaf应用程序在Spring MVC中讨论Thymeleaf 3.0的新功能。第3版本带有新功能和许多底层改进。更具体地说，我们将涵盖自然处理和Javascript内联的主题。

Thymeleaf 3.0包括三种新的文本模板模式：TEXT、JAVASCRIPT和CSS，它们分别用于处理纯文本、JavaScript和CSS模板。

## 2. Maven依赖

首先，让我们看看将Thymeleaf与Spring集成所需的配置；我们的依赖项中需要thymeleaf-spring库：

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

请注意，对于Spring 4项目， 必须使用thymeleaf-spring4库而不是thymeleaf-spring5。可以在[此处](https://search.maven.org/search?q=a:thymeleaf-spring5)找到最新版本的依赖项。

## 3. Java Thymeleaf配置

首先，我们需要配置新的模板引擎、视图和模板解析器。为此，我们需要更新创建的Java配置类

为此，我们需要更新在[此处](https://www.baeldung.com/thymeleaf-in-spring-mvc)创建的Java配置类。除了新类型的解析器之外，我们的模板还实现了Spring接口ApplicationContextAware：

```java
@Configuration
@EnableWebMvc
@ComponentScan({ "cn.tuyucheng.taketoday.thymeleaf" })
public class WebMVCConfig implements WebMvcConfigurer, ApplicationContextAware {

    private ApplicationContext applicationContext;

    // Javasetter

    @Bean
    public ViewResolver htmlViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine(htmlTemplateResolver()));
        resolver.setContentType("text/html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setViewNames(ArrayUtil.array("*.html"));
        return resolver;
    }

    @Bean
    public ViewResolver javascriptViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine(javascriptTemplateResolver()));
        resolver.setContentType("application/javascript");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setViewNames(ArrayUtil.array("*.js"));
        return resolver;
    }

    @Bean
    public ViewResolver plainViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine(plainTemplateResolver()));
        resolver.setContentType("text/plain");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setViewNames(ArrayUtil.array("*.txt"));
        return resolver;
    }
}
```

正如我们在上面观察到的，我们创建了三种不同的视图解析器，一种用于HTML视图，一种用于Javascript文件，一种用于纯文本文件。Thymeleaf将通过检查文件扩展名来区分它们，分别是.html、.js和.txt。

我们还创建了静态ArrayUtil类，以便使用方法array()创建具有视图名称的所需String[]数组。

在这个类的下一部分，我们需要配置模板引擎：

```java
private ISpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setTemplateResolver(templateResolver);
    return engine;
}
```

最后，我们需要创建三个独立的模板解析器：

```java
private ITemplateResolver htmlTemplateResolver() {
    SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
    resolver.setApplicationContext(applicationContext);
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setCacheable(false);
    resolver.setTemplateMode(TemplateMode.HTML);
    return resolver;
}
    
private ITemplateResolver javascriptTemplateResolver() {
    SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
    resolver.setApplicationContext(applicationContext);
    resolver.setPrefix("/WEB-INF/js/");
    resolver.setCacheable(false);
    resolver.setTemplateMode(TemplateMode.JAVASCRIPT);
    return resolver;
}
    
private ITemplateResolver plainTemplateResolver() {
    SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
    resolver.setApplicationContext(applicationContext);
    resolver.setPrefix("/WEB-INF/txt/");
    resolver.setCacheable(false);
    resolver.setTemplateMode(TemplateMode.TEXT);
    return resolver;
}
```

请注意，为了测试，最好使用非缓存模板，这就是为什么建议使用setCacheable(false)方法。

Javascript模板将存储在/WEB-INF/js/文件夹中，纯文本文件存储在/WEB-INF/txt/文件夹中，最后HTML文件的路径是/WEB-INF/html。

## 4. Spring控制器配置

为了测试我们的新配置，我们创建了以下 Spring 控制器：

```java
@Controller
public class InliningController {

    @RequestMapping(value = "/html", method = RequestMethod.GET)
    public String getExampleHTML(Model model) {
        model.addAttribute("title", "Tuyucheng");
        model.addAttribute("description", "<strong>Thymeleaf</strong> tutorial");
        return "inliningExample.html";
    }

    @RequestMapping(value = "/js", method = RequestMethod.GET)
    public String getExampleJS(Model model) {
        model.addAttribute("students", StudentUtils.buildStudents());
        return "studentCheck.js";
    }

    @RequestMapping(value = "/plain", method = RequestMethod.GET)
    public String getExamplePlain(Model model) {
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("students", StudentUtils.buildStudents());
        return "studentsList.txt";
    }
}
```

在HTML文件示例中，我们将展示如何使用新的内联功能，使用和不使用转义HTML标记。

对于JS示例，我们将生成一个AJAX请求，该请求将加载包含学生信息的js文件。请注意，我们在StudentUtils类中使用简单的buildStudents()方法，来自这篇[文章](https://www.baeldung.com/thymeleaf-in-spring-mvc)。

最后，在纯文本示例中，我们将学生信息显示为文本文件。使用纯文本模板模式的典型示例可以用于发送纯文本电子邮件。

作为附加功能，我们将使用SecurityContextHolder来获取记录的用户名。

## 5. Html/Js/Text示例文件

本教程的最后一部分是创建三种不同类型的文件，并测试新的Thymeleaf功能的使用。让我们从HTML文件开始：

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Inlining example</title>
</head>
<body>
<p>Title of tutorial: [[${title}]]</p>
<p>Description: [(${description})]</p>
</body>
</html>
```

在这个文件中，我们使用两种不同的方法。为了显示标题，我们使用转义语法，这将删除所有HTML标签，从而只显示文本。在描述的情况下，我们使用非转义语法来保留HTML标签。最终结果将如下所示：

```html
<p>Title of tutorial: Tuyucheng</p>
<p>Description: <strong>Thymeleaf</strong> tutorial</p>
```

这当然会被我们的浏览器解析，以粗体显示单词Thymeleaf。

接下来，我们继续测试js模板功能：

```javascript
var count = [[${students.size()}]];
alert("Number of students in group: " + count);
```

JAVASCRIPT模板模式中的属性将是JavaScript非转义的。这将导致创建js警报。我们使用jQuery AJAX在listStudents.html文件中加载此警报：

```javascript
<script>
    $(document).ready(function() {
        $.ajax({
            url : "/spring-thymeleaf/js",
            });
        });
</script>
```

我们要测试的最后但并非最不重要的功能是生成纯文本文件。我们创建了包含以下内容的studentsList.txt文件：

```plaintext
Dear [(${username})],

This is the list of our students:
[# th:each="s : ${students}"]
   - [(${s.name})]. ID: [(${s.id})]
[/]
Thanks,
The Tuyucheng University
```

请注意，与标记模板模式一样，标准方言仅包含一个可处理元素([#...])和一组可处理属性(th:text、th:utext、th:if、th:unless、th:each，ETC。)。结果将是一个文本文件，我们可以在电子邮件中使用它，正如第3节末尾提到的那样。

如何测试？我们的建议是先试用浏览器，然后再检查现有的 JUnit 测试。

## 6. Spring Boot中的Thymeleaf

Spring Boot通过添加[spring-boot-starter-thymeleaf](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)依赖项为Thymeleaf提供自动配置：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.3.3.RELEASE</version>
</dependency>
```

无需显式配置。默认情况下，HTML文件应放在resources/templates位置。

## 7. 总结

在本文中，我们讨论了Thymeleaf框架中实现的新功能，重点是版本3.0。

本教程的完整实现可以在[GitHub项目](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-web-modules/spring-thymeleaf)中找到，这是一个基于Eclipse的项目，很容易在每个现代Internet浏览器中进行测试。

最后，如果你计划将项目从版本2迁移到这个最新版本，请查看[此处的迁移指南](http://www.thymeleaf.org/doc/articles/thymeleaf3migration.html)。请注意，你现有的Thymeleaf模板与Thymeleaf 3.0几乎100%兼容，因此你只需对配置进行一些修改。