## 1. 概述

在本快速教程中，我们将了解如何使用核心Java邮件库发送带附件和不带附件的电子邮件。

## 2.项目设置和依赖

对于本文，我们将使用一个简单的基于 Maven 的项目，该项目依赖于Java邮件库：

```xml
<dependency>
    <groupId>javax.mail</groupId>
    <artifactId>mail</artifactId>
    <version>1.5.0-b01</version>
</dependency>
```

[最新版本可以在这里](https://search.maven.org/classic/#search|gav|1|g%3A"javax.mail" AND a%3A"mail")找到。

## 3. 发送纯文本和 HTML 电子邮件

首先，我们需要使用电子邮件服务提供商的凭据配置库。然后我们将创建一个会话 ，用于构建我们要发送的消息。

配置是通过JavaProperties 对象进行的：

```java
Properties prop = new Properties();
prop.put("mail.smtp.auth", true);
prop.put("mail.smtp.starttls.enable", "true");
prop.put("mail.smtp.host", "smtp.mailtrap.io");
prop.put("mail.smtp.port", "25");
prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
```

在上面的属性配置中，我们将电子邮件主机配置为 Mailtrap，并使用该服务提供的端口。

现在让我们用我们的用户名和密码创建一个会话：

```java
Session session = Session.getInstance(prop, new Authenticator() {
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
});
```

用户名和密码由邮件服务提供商与主机和端口参数一起提供。

现在我们有了一个邮件 Session 对象，让我们创建一个用于发送的Mime消息 ：

```java
Message message = new MimeMessage(session);
message.setFrom(new InternetAddress("from@gmail.com"));
message.setRecipients(
  Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
message.setSubject("Mail Subject");

String msg = "This is my first email using JavaMailer";

MimeBodyPart mimeBodyPart = new MimeBodyPart();
mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

Multipart multipart = new MimeMultipart();
multipart.addBodyPart(mimeBodyPart);

message.setContent(multipart);

Transport.send(message);
```

在上面的代码片段中，我们首先创建了一个 具有必要属性的消息 实例——收件人、发件人和主题。这之后是一个mimeBodyPart ，它具有text/html的编码 ，因为我们的消息是用 HTML 设计的。

接下来，我们创建了一个MimeMultipart 对象的实例，我们可以使用它来包装 我们创建的mimeBodyPart 。

最后，我们将multipart 对象设置为消息的内容 ，并使用Transport 对象的 send()进行邮件发送。

因此，我们可以说 mimeBodyPart包含 在message中包含的 multipart 中。这样，一个多部分 可以包含多个 mimeBodyPart。

这将是下一节的重点。

## 4.发送带附件的邮件

接下来，要发送附件，我们只需要创建另一个 MimeBodyPart并将文件附加到它：

```java
MimeBodyPart attachmentBodyPart = new MimeBodyPart();
attachmentBodyPart.attachFile(new File("path/to/file"));
```

然后我们可以将新的正文部分添加到 我们之前创建的MimeMultipart对象中：

```java
multipart.addBodyPart(attachmentBodyPart);
```

这就是我们需要做的。

我们再次将multipart 实例设置为消息 对象的内容 ，最后我们将使用send() 进行邮件发送。

## 5. 格式化电子邮件文本

要格式化和设计我们的电子邮件文本，我们可以使用 HTML 和 CSS 标签。

例如，如果我们希望我们的文本是粗体，我们将实现 <b>标签。为了给文本着色，我们可以使用 style标签。如果我们想要额外的属性，比如粗体，我们也可以将 HTML 标签与 CSS 标签结合起来。

让我们创建一个包含粗体红色文本的字符串：

```java
String msgStyled = "This is my <b style='color:red;'>bold-red email</b> using JavaMailer";
```

这个字符串将保存我们要在电子邮件正文中发送的样式文本。

## 六，总结

在本文中，我们了解了如何使用本机Java邮件库发送带有附件的电子邮件。