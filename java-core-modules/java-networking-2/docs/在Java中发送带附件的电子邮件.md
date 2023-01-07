## 1. 概述

在本快速教程中，我们将学习如何使用JavaMail API 在Java中发送带有单个和多个附件的电子邮件。

## 2.项目设置

在本文中，我们将创建一个具有[javax.mail](https://search.maven.org/classic/#search|gav|1|g%3A"javax.mail" AND a%3A"mail")依赖项的简单 Maven 项目：

```xml
<dependency>
    <groupId>javax.mail</groupId>
    <artifactId>mail</artifactId>
    <version>1.5.0-b01</version>
</dependency>
```

## 3.发送带附件的邮件

首先，我们[需要配置电子邮件服务提供商的凭据](https://www.baeldung.com/java-email#sending-a-plain-text-and-an-html-email)。然后，通过提供电子邮件主机、端口、用户名和密码来创建会话对象。所有这些详细信息均由电子邮件托管服务提供。我们可以为我们的代码使用任何假的 SMTP 测试服务器。

Session对象将作为连接工厂来处理JavaMail的配置和身份验证。

现在我们有了一个Session对象，让我们进一步创建MimeMessage和MimeBodyPart对象。我们使用这些对象来创建电子邮件消息：

```java
Message message = new MimeMessage(session); 
message.setFrom(new InternetAddress(from)); 
message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); 
message.setSubject("Test Mail Subject"); 

BodyPart messageBodyPart = new MimeBodyPart(); 
messageBodyPart.setText("Mail Body");

```

在上面的代码片段中，我们创建了MimeMessage对象，其中包含所需的详细信息，例如发件人、收件人和主题。然后，我们有一个带有电子邮件正文的MimeBodyPart对象。

现在，我们需要创建另一个MimeBodyPart以在我们的邮件中添加附件：

```java
MimeBodyPart attachmentPart = new MimeBodyPart();
attachmentPart.attachFile(new File("path/to/file"));

```

对于一个邮件会话，我们现在有两个MimeBodyPart对象。所以我们需要创建一个MimeMultipart对象，然后将两个MimeBodyPart对象添加到其中：

```java
Multipart multipart = new MimeMultipart();
multipart.addBodyPart(messageBodyPart);
multipart.addBodyPart(attachmentPart);

```

最后，将MimeMultiPart添加到MimeMessage对象作为我们的邮件内容，并调用Transport.send()方法发送消息：

```java
message.setContent(multipart);
Transport.send(message);

```

总而言之，Message包含MimeMultiPart，它进一步包含多个MimeBodyPart(s)。这就是我们组装完整电子邮件的方式。

此外，要发送多个附件，你只需添加另一个MimeBodyPart即可。

## 4。总结

在本教程中，我们学习了如何使用Java发送带有单个和多个附件的电子邮件。