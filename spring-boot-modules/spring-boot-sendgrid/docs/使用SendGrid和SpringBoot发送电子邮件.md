## 1. 概述

在过去的几周里，我一直在寻找一个很好的解决方案来向我的一个副项目的用户发送电子邮件。使用Spring Boot Mail Starter发送纯文本格式的电子邮件非常简单，因为它是使用JavaMailSender接口和自动配置的抽象。也可以发送HTML格式的电子邮件或使用像Thymeleaf这样的模板引擎来生成内容。使用此设置，我必须自己使用我的编辑器生成不同的电子邮件模板。由于电子邮件设计(尤其是使用HTML)不是我的主要优势之一，我花了很多时间并开始寻找一种解决方案，在这种解决方案中，“业务”可以使用所见即所得的方式自行模板化电子邮件，因为他们没有更深入的HTML知识。

对于这个任务，我找到了一个名为[SendGrid](https://sendgrid.com/)的服务。借助SendGrid，你可以使用具有拖放功能的Web编辑器创建美观的电子邮件模板。你还可以在电子邮件模板中定义变量，例如将用户名或链接添加到正文中。SendGrid的另一个好处是它已集成到大多数云提供商(例如Azure、Heroku、Pivotal Web Services...)中，并且使用免费版本的SendGrid，你可以涵盖中小型企业的大部分用例项目。

<img src="../assets/img_1.png">

内置的电子邮件编辑器让你有机会添加自己的HTML代码或为你的电子邮件使用一些预构建模块，例如按钮、社交媒体图标和取消订阅链接。电子邮件的变量用–VARIABLE_NAME-括起来：

<img src="../assets/img_1.png">

要重现以下代码示例，你需要一个自己的SendGrid帐户，并且必须创建一个API密钥以与内部SendGrid API进行通信。

## 2. Spring Boot项目设置

举个简单的例子，我们将创建一个带有REST控制器的Spring Boot Web项目，该控制器将触发发送电子邮件。

首先，我们必须在Maven中添加[sendgrid-java](https://central.sonatype.com/artifact/com.sendgrid/sendgrid-java/4.9.3)、[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.6)、[spring-boot-starter-test](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-test/3.0.6)：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.sendgrid</groupId>
        <artifactId>sendgrid-java</artifactId>
        <version>4.6.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

如果你的类路径上有spring-boot-autoconfigure，它是例如spring-boot-starter-web的传递依赖，你只需要将你的API密钥添加到你的application.properties文件中，Spring Boot将负责创建用于与SendGrid API通信的核心bean：

```properties
spring.sendgrid.api-key=YOUR_API_KEY_HERE
```

## 3. 使用SendGrid SDK发送电子邮件

REST端点如下所示：

```java
@RestController
public class MailController {

    @Autowired
    private SendGrid sendGrid;

    @Value("${templateId}")
    private String EMAIL_TEMPLATE_ID;

    @GetMapping("/sendgrid")
    public String sendEmailWithSendGrid(@RequestParam("msg") String message) {
        Email from = new Email("yourname@yourhostname.de");
        String subject = "Hello World!";
        Email to = new Email("yourname@yourhostname.de");
        Content content = new Content("text/html", "I'm replacing the <strong>body tag</strong>" + message);

        Mail mail = new Mail(from, subject, to, content);

        mail.setReplyTo(new Email("yourname@yourhostname.de"));
        mail.personalization.get(0).addSubstitution("-username-", "Some blog user");
        mail.setTemplateId(EMAIL_TEMPLATE_ID);

        Request request = new Request();
        Response response = null;

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            response = sendGrid.api(request);

            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return "email was successfully send";
    }
}
```

中央通信接口是SendGrid bean，它提供了一个用于发送电子邮件的良好编程API。变量的替换是通过以下代码行实现的：

```java
mail.personalization.get(0).addSubstitution("-username-", "Some blog user");
```

要找到正确的电子邮件模板，你必须正确设置模板ID。浏览器中的简单的HTTP GET调用[http://localhost:8080/sendgrid?msg=Hello%20World](http://localhost:8080/sendgrid?msg=Hello%20World)将发送以下电子邮件：

<img src="../assets/img_2.png">

## 4. 总结

你可以在[GitHub](https://github.com/rieckpil/blog-tutorials/tree/master/send-emails-with-sendgrid-and-spring-boot)上找到完整的代码示例。要在本地使用它，你必须替换application.properties文件中的SendGrid API密钥和模板ID。