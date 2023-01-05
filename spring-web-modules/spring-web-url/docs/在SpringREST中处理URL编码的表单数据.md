## 1. 概述

对于最终用户来说，表单提交的过程很方便，在某种程度上，相当于输入数据并点击提交按钮。但是，从工程的角度来看，需要一种编码机制来可靠地从客户端向服务器端发送和接收这些数据，以供后端处理。

对于本教程的范围，我们将专注于创建一个表单，该表单在Spring Web应用程序中将其数据作为application/x-www-form-urlencoded内容类型发送。

## 2. 表单数据编码

最常用的表单提交HTTP方法是POST。但是，对于[幂等](https://www.baeldung.com/cs/idempotent-operations)的表单提交，我们也可以使用HTTP GET方法。并且，指定方法的方式是通过[表单的方法属性](https://www.w3.org/TR/html4/interact/forms.html#adef-method)。

对于使用GET方法的表单，整个表单数据作为查询字符串的一部分发送。但是，如果我们使用POST方法，那么它的数据将作为HTTP请求正文的一部分发送。

此外，在后一种情况下，我们还可以使用表单的[enctype](https://www.w3.org/TR/html4/interact/forms.html#adef-enctype)属性指定数据的编码，该属性可以取两个值，即[application/x-www-form-urlencoded](https://en.wikipedia.org/wiki/Percent-encoding#The_application.2Fx-www-form-urlencoded_type)和[multipart/form-data](https://en.wikipedia.org/wiki/MIME#Form-Data)。

### 2.1 媒体类型application/x-www-form-urlencoded

HTML表单的enctype属性的默认值为application/x-www-form-urlencoded，因为它处理数据完全是文本的基本用例。然而，如果我们的用例涉及支持文件数据，那么我们将不得不用multipart/form-data的值覆盖它。

本质上，它将表单数据作为由与号(&)字符分隔的键值对发送。此外，相应的键和值用等号(=)分隔。此外，所有保留字符和非字母数字字符都使用[percent-encoding](https://en.wikipedia.org/wiki/Percent-encoding#The_application/x-www-form-urlencoded_type)进行编码。

## 3. 浏览器表单提交

现在我们已经涵盖了基础知识，让我们继续看看我们如何处理URL编码的表单数据，以用于Spring Web应用程序中反馈提交的简单用例。

### 3.1 领域模型

对于我们的反馈表，我们需要捕获提交者的电子邮件标识符以及评论。因此，让我们在反馈类中创建我们的领域模型：

```java
public class Feedback {
    private String emailId;
    private String comment;
}
```

### 3.2 创建表单

要使用简单的HTML模板来创建我们的动态Web表单，我们需要在我们的项目中[配置Thymeleaf](https://www.baeldung.com/spring-web-flash-attributes#1-thymeleaf-configuration)。在此之后，我们准备添加一个GET端点/feedback，它将为表单的反馈视图提供服务：

```java
@GetMapping(path = "/feedback")
public String getFeedbackForm(Model model) {
    Feedback feedback = new Feedback();
    model.addAttribute("feedback", feedback);
    return "feedback";
}
```

请注意，我们使用反馈作为模型属性来捕获用户输入。接下来，让我们在feedback.html模板中创建反馈视图：

```html
<form action="#" method="post" th:action="@{/web/feedback}" th:object="${feedback}">
    <!-- form fields for feedback's submitter and comment info -->
</form>
```

当然，我们不需要明确指定enctype属性，因为它会选择默认值application/x-www-form-urlencoded。

### 3.3 程序流

由于我们通过浏览器反馈表单接受用户输入，因此我们必须实施[POST/REDIRECT/GET(PRG)](https://www.baeldung.com/spring-web-flash-attributes#1-postredirectget-pattern)提交工作流程以避免重复提交。

首先，让我们实现POST端点/web/feedback，它将充当反馈表单的操作处理程序：

```java
@PostMapping(
    path = "/web/feedback",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
public String handleBrowserSubmissions(Feedback feedback) throws Exception {
    // Save feedback data
    return "redirect:/feedback/success";
}
```

接下来，我们可以实现服务于GET请求的重定向端点/feedback/success：

```java
@GetMapping("/feedback/success")
public ResponseEntity<String> getSuccess() {
    return new ResponseEntity<String>("Thank you for submitting feedback.", HttpStatus.OK);
}
```

为了在浏览器中验证表单提交工作流程的功能，让我们访问localhost:8080/feedback：

<img src="../assets/img.png">

最后，我们还可以检查表单数据是否以URL编码形式发送：

```bash
emailId=abc%40example.com&comment=Sample+Feedback
```

## 4. 非浏览器请求

有时，我们可能没有基于浏览器的HTTP客户端。相反，我们的客户端可以是[cURL](https://www.baeldung.com/curl-rest)或[Postman](https://www.baeldung.com/postman-testing-collections)等实用程序。在这种情况下，我们不需要HTML Web表单。相反，我们可以实现一个服务于POST请求的/feedback端点：

```java
@PostMapping(
    path = "/feedback",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
public ResponseEntity<String> handleNonBrowserSubmissions(@RequestBody Feedback feedback) throws Exception {
    // Save feedback data
    return new ResponseEntity<String>("Thank you for submitting feedback", HttpStatus.OK);
}
```

在我们的数据流中没有HTML表单的情况下，我们不一定需要实现PRG模式。但是，我们必须指定该资源接受APPLICATION_FORM_URLENCODED_VALUE媒体类型。

最后，我们可以使用cURL请求对其进行测试：

```shell
curl -X POST \
  http://localhost:8080/feedback \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'emailId=abc%40example.com&comment=Sample%20Feedback'
```

### 4.1 FormHttpMessageConverter基础知识

发送application/x-www-form-urlencoded数据的HTTP请求必须在Content-Type标头中指定。在内部，Spring使用[FormHttpMessageConverter](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/converter/FormHttpMessageConverter.html)类来读取此数据并将其与方法参数绑定。

如果我们的方法参数是MultiValueMap类型，我们可以使用[@RequestParam](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestParam.html)或[@RequestBody](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestBody.html)注解将其与HTTP请求的主体适当绑定。这是因为Servlet API将查询参数和表单数据组合到一个名为parameters的映射中，其中包括请求正文的自动解析：

```java
@PostMapping(
    path = "/feedback",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
public ResponseEntity<String> handleNonBrowserSubmissions(
  @RequestParam MultiValueMap<String,String> paramMap) throws Exception {
    // Save feedback data
    return new ResponseEntity<String>("Thank you for submitting feedback", HttpStatus.OK);
}
```

但是，对于MultiValueMap以外类型的方法参数，例如我们的Feedback域对象，我们必须只使用@RequestBody注解。

## 5. 总结

在本教程中，我们简要了解了Web表单中表单数据的编码。我们还探讨了如何通过在Spring Boot Web应用程序中实现反馈表单来处理浏览器和非浏览器HTTP请求的URL编码数据。