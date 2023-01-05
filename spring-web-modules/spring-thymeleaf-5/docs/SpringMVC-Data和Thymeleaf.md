## 1. 简介

在本教程中，我们将讨论使用Thymeleaf访问Spring MVC数据的不同方法。

我们将从使用Thymeleaf创建电子邮件模板开始，我们将使用来自Spring应用程序的数据对其进行增强。

## 2. 项目设置

首先，我们需要添加[Thymeleaf](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-thymeleaf)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.7.2</version>
</dependency>
```

其次，让我们包含[Spring Boot Web Starter](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-web)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>
```

这种依赖性为我们提供了REST支持，我们稍后将使用它来创建一些端点。

我们将创建一些Thymeleaf模板来覆盖我们的示例案例并将它们存储在resources/mvcdata中。本教程的每个部分将实现不同的模板：

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<!-- data -->
</html>
```

最后，我们需要实现一个控制器类，我们将在其中存储我们的业务逻辑：

```java
@Controller
public class EmailController {
    private ServletContext servletContext;

    public EmailController(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
```

我们的控制器类并不总是依赖于Servlet上下文，但我们将其添加到此处以便稍后演示特定的Thymeleaf功能。

## 3. 模型属性

模型属性在控制器类内部使用，这些类准备数据以在视图内呈现。

我们可以向模型添加属性的一种方法是要求模型实例作为控制器方法中的参数。

让我们将emailData作为属性传递：

```java
@GetMapping(value = "/email/modelattributes")
public String emailModel(Model model) {
    model.addAttribute("emailData", emailData);
    return "mvcdata/email-model-attributes";
}
```

当请求/email/modelattributes时，Spring会为我们注入一个Model实例。

然后，我们可以在Thymeleaf表达式中引用我们的emailData模型属性：

```html
<p th:text="${emailData.emailSubject}">Subject</p>
```

我们可以做到的另一种方法是通过使用@ModelAttribute告诉我们的Spring容器在我们的视图中需要什么属性：

```java
@ModelAttribute("emailModelAttribute")
EmailData emailModelAttribute() {
    return emailData;
}
```

然后我们可以将视图中的数据表示为：

```html
<p th:each="emailAddress : ${emailModelAttribute.getEmailAddresses()}">
    <span th:text="${emailAddress}"></span>
</p>
```

有关模型数据的更多示例，请查看我们的[Spring MVC](https://www.baeldung.com/spring-mvc-model-model-map-model-view)教程中的模型、模型映射和模型视图。

## 4. 请求参数

另一种访问数据的方法是通过请求参数：

```java
@GetMapping(value = "/email/requestparameters")
public String emailRequestParameters(
    @RequestParam(value = "emailsubject") String emailSubject) {
    return "mvcdata/email-request-parameters";
}
```

同时，在我们的模板中，我们需要使用关键字param指定哪个参数包含数据：

```html
<p th:text="${param.emailsubject}"></p>
```

我们也可以有多个同名的请求参数：

```java
@GetMapping(value = "/email/requestparameters")
public String emailRequestParameters(
    @RequestParam(value = "emailsubject") String emailSubject,
    @RequestParam(value = "emailaddress") String emailAddress1,
    @RequestParam(value = "emailaddress") String emailAddress2) {
    return "mvcdata/email-request-parameters";
}
```

然后，我们将有两个选项来显示数据。

首先，我们可以使用th:each遍历每个同名参数：

```html
<p th:each="emailaddress : ${param.emailaddress}">
    <span th:text="${emailaddress}"></span>
</p>
```

其次，我们可以使用参数数组的索引：

```html
<p th:text="${param.emailaddress[0]}"></p>
<p th:text="${param.emailaddress[1]}"></p>
```

## 5. 会话属性

或者，我们可以将数据放在HttpSession属性中：

```java
@GetMapping("/email/sessionattributes")
public String emailSessionAttributes(HttpSession httpSession) {
    httpSession.setAttribute("emaildata", emailData);
    return "mvcdata/email-session-attributes";
}
```

然后，与请求参数类似，我们可以使用session关键字：

```html
<p th:text="${session.emaildata.emailSubject}"></p>
```

## 6. ServletContext属性

使用ServletContext，我们将无法使用表达式来访问emailData的属性。

为了解决这个问题，我们将把每个值作为一个单独的属性传递：

```java
@GetMapping("/email/servletcontext")
public String emailServletContext() {
    servletContext.setAttribute("emailsubject", emailData.getEmailSubject());
    servletContext.setAttribute("emailcontent", emailData.getEmailBody());
    servletContext.setAttribute("emailaddress", emailData.getEmailAddress1());
    servletContext.setAttribute("emaillocale", emailData.getEmailLocale());
    return "mvcdata/email-servlet-context";
}
```

然后，我们可以通过servletContext变量检索每个：

```html
<p th:text="${#servletContext.getAttribute('emailsubject')}"></p>
```

## 7. Bean

最后，我们还可以使用上下文bean提供数据：

```java
@Bean
public EmailData emailData() {
    return new EmailData();
}
```

Thymeleaf允许使用@beanName语法访问bean：

```html
<p th:text="${@emailData.emailSubject}"></p>
```

## 8. 总结

在这个小教程中，我们学习了如何通过Thymeleaf访问数据。

首先，我们添加了适当的依赖项。其次，我们实现了一些REST方法，以便我们可以将数据传递给我们的模板。