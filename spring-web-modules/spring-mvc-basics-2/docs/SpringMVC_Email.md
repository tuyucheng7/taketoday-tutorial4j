## 1. 概述

在本教程中，我们介绍从普通Spring应用程序和Spring Boot应用程序发送电子邮件所需的步骤。
对于前者，我们使用JavaMail库，后者将使用spring-boot-starter-mail依赖项。

## 2. Maven依赖

首先，我们需要将依赖项添加到pom.xml中。

### 2.1 Spring

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>5.3.13</version>
</dependency>
```

### 2.2 Spring Boot

对于Spring Boot：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
    <version>2.6.1</version>
</dependency>
```

## 3. 邮件服务器属性

Spring框架中Java mail支持的接口和类组织如下：

1. **MailSender接口**：提供发送简单电子邮件的基本功能的顶层接口。
2. **JavaMailSender接口**：上述MailSender的子接口，它支持MIME消息，并且主要与MimeMessageHelper类一起用于创建MimeMessage。
   建议将MimeMessagePreparator机制用于此接口。
3. **JavaMailSenderImpl类**：提供JavaMailSender接口的实现，它支持MimeMessage和SimpleEmailMessage。
4. **SimpleMailMessage类**：用于创建包含发件人、收件人、抄送、主题和文本字段的简单邮件。
5. **MimeMessagePreparator接口**：提供用于准备MIME消息的回调接口。
6. **MimeMessageHelper类**：用于创建MIME消息的工具类，它支持HTML布局中的图像、经典邮件附件和文本内容。

在下面的部分中，我们演示如何使用这些接口和类。

### 3.1 Spring邮件服务器属性

指定SMTP服务器所需的邮件属性可以使用JavaMailSenderImpl定义。

对于Gmail，可以如下配置：

```java
/*@formatter:off*/
@Bean
public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);
    
    mailSender.setUsername("my.gmail@gmail.com");
    mailSender.setPassword("password");
    
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");
    
    return mailSender;
}
/*@formatter:on*/
```

### 3.2 Spring Boot邮件服务器属性

如果我们添加了依赖，下一步就是在application.properties中通过spring.mail.*指定邮件服务器属性。

我们可以通过以下方式指定Gmail SMTP服务器的属性：

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<login user to smtp server>
spring.mail.password=<login password to smtp server>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

有些SMTP服务器需要TLS连接，所以我们使用spring.mail.properties.mail.smtp.starttls.enable属性以启用受TLS保护的连接。

#### 3.2.1 Gmail SMTP属性

我们可以通过Gmail SMTP服务器发送电子邮件。

我们的application.properties文件已经定义了使用Gmail SMTP的相关属性.(如上节所述).

请注意，我们帐户密码不应该是普通密码，而是为我们的谷歌帐户生成的应用程序密码。

### 3.2.2 SES SMTP属性

为了使用亚马逊SES发送电子邮件，我们配置application.properties：

```properties
spring.mail.host=email-smtp.us-west-2.amazonaws.com
spring.mail.username=username
spring.mail.password=password
spring.mail.properties.mail.transport.protocol=smtp
spring.mail.properties.mail.smtp.port=25
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### 3.2.3 163 SMTP属性

要使用网易邮箱发送电子邮件，我们配置application.properties：

```properties
spring.mail.host=smtp.163.com
spring.mail.username=username
spring.mail.password=password
spring.mail.default-encoding=UTF-8
```

## 4. 发送电子邮件

一旦依赖和配置到位，我们就可以使用前面提到的JavaMailSender发送电子邮件。

由于普通的Spring框架和它的Spring Boot版本都以类似的方式处理电子邮件的编写和发送，因此我们不必在下面的小节中区分这两者。

### 4.1 发送简单的电子邮件

让我们首先撰写并发送一封没有任何附件的简单电子邮件：

```java
@Service("EmailService")
public class EmailServiceImpl implements EmailService {

    private static final String NOREPLY_ADDRESS = "tuyucheng2000@gmail.com";

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }
}
```

请注意，尽管不强制提供发件人地址，但许多SMTP服务器会拒绝此类邮件。
这就是为什么我们在EmailServiceImpl中使用tuyucheng2000@gmail.com电子邮件地址。

### 4.2 发送带有附件的电子邮件

有时，Spring的简单消息传递对于我们的用例来说是不够的。

例如，我们希望发送一封附有发票的订单确认电子邮件。
在这种情况下，我们应该使用来自JavaMail库的MIME Multipart Message，而不是SimpleEmailMessage。
Spring通过org.springframework.mail.javamail.MimeMessageHelper类支持JavaMail消息。

首先，我们向EmailServiceImpl添加一个方法来发送带有附件的电子邮件：

```java
@Service("EmailService")
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            // pass 'true' to the constructor to create a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(NOREPLY_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
```

### 4.3 简单电子邮件模板

SimpleEmailMessage类支持文本格式，我们可以通过在配置中定义模板bean来创建电子邮件模板：

```java
public class EmailConfiguration {

    @Bean
    public SimpleMailMessage templateSimpleMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("This is the test email template for your email:\n%s\n");
        return message;
    }
}
```

现在我们可以使用这个bean作为电子邮件模板，只需要为模板提供必要的参数：

```java
/*@formatter:off*/
@Autowired
public SimpleMailMessage template;
...
String text = String.format(template.getText(), templateArgs);  
sendSimpleMessage(to, subject, text);
/*@formatter:on*/
```

## 5. 处理发送错误

JavaMail提供SendFailedException来处理无法发送消息的情况。
但是，当我们向错误的地址发送电子邮件时，可能不会出现此异常。原因如下：

RFC 821中的SMTP协议规范指定了SMTP服务器在尝试将电子邮件发送到错误地址时应返回的550返回码。
但大多数公共SMTP服务器不这样做，相反，他们会发送一封”发送失败“的电子邮件，或者根本不提供反馈。

例如，Gmail SMTP服务器发送一条”delivery failed“消息，我们的程序中不会出现异常。

因此，我们有几个选择来处理这种情况：

1. 捕获SendFailedException，它永远不会被抛出。
2. 在一段时间内，检查我们的发件人邮箱，查看"delivery failed"消息。这并不简单，时间段也不确定。
3. 如果我们的邮件服务器根本不提供反馈，我们就无能为力。

## 6. 总结

在这篇文章中，我们演示了如何从Spring Boot应用程序设置和发送电子邮件。