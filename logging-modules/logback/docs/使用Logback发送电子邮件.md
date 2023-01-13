## 1. 概述

[Logback](https://www.baeldung.com/logback)是基于Java的应用程序最流行的日志记录框架之一。它内置了对旧日志文件的高级过滤、归档和删除以及通过电子邮件发送日志消息的支持。

在本快速教程中，我们将配置 Logback 以针对任何应用程序错误发送电子邮件通知。

## 2.设置

Logback 的电子邮件通知功能需要使用SMTPAppender。SMTPAppender使用Java Mail API，后者又依赖于 JavaBeans Activation Framework。

让我们在POM中添加这些依赖项：

```xml
<dependency>
    <groupId>javax.mail</groupId>
    <artifactId>mail</artifactId>
    <version>1.4.7</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
    <scope>runtime</scope>
</dependency>
```

我们可以在 Maven Central 上找到最新版本的[Java Mail API](https://search.maven.org/classic/#search|gav|1|g%3A"javax.mail" AND a%3A"mail")和[JavaBeans Activation Framework 。](https://search.maven.org/classic/#search|ga|1|g%3A"javax.activation" AND a%3A"activation")

## 3.配置SMTPAppender

默认情况下，Logback 的SMTPAppender会在记录ERROR事件时触发一封电子邮件。

它将所有日志记录事件保存在一个循环缓冲区中，默认最大容量为 256 个事件。缓冲区变满后，它会丢弃所有旧的日志事件。

让我们在logback.xml中配置一个SMTPAppender：

```xml
<appender name="emailAppender" class="ch.qos.logback.classic.net.SMTPAppender">
    <smtpHost>OUR-SMTP-HOST-ADDRESS</smtpHost>
    <!-- one or more recipients are possible -->
    <to>EMAIL-RECIPIENT-1</to>
    <to>EMAIL-RECIPIENT-2</to>
    <from>SENDER-EMAIL-ADDRESS</from>
    <subject>BAELDUNG: %logger{20} - %msg</subject>
    <layout class="ch.qos.logback.classic.PatternLayout">
        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} - %msg%n</pattern>
    </layout>
</appender>
```

此外，我们将把这个附加程序添加到我们的 Logback 配置的根元素中：

```xml
<root level="INFO">
    <appender-ref ref="emailAppender"/>
</root>
```

因此，对于任何被记录的应用程序错误，它会发送一封电子邮件，其中包含由PatternLayout格式化的所有缓冲日志记录事件。

我们可以进一步用 HTMLLayout 替换PatternLayout来格式化HTML 表格中的日志消息：
[![SampleEmail-1](https://www.baeldung.com/wp-content/uploads/2019/12/SampleEmail-1.png)](https://www.baeldung.com/wp-content/uploads/2019/12/SampleEmail-1.png)

## 4.自定义缓冲区大小

我们现在知道，默认情况下，外发电子邮件将包含最后 256 条日志记录事件消息。但是，我们可以通过包含cyclicBufferTracker配置并指定所需的bufferSize来自定义此行为。

要触发仅包含最新五个日志记录事件的电子邮件通知，我们将：

```xml
<appender name="emailAppender" class="ch.qos.logback.classic.net.SMTPAppender">
    <smtpHost>OUR-SMTP-HOST-ADDRESS</smtpHost>
    <to>EMAIL-RECIPIENT</to>
    <from>SENDER-EMAIL-ADDRESS</from>
    <subject>BAELDUNG: %logger{20} - %msg</subject>
    <layout class="ch.qos.logback.classic.html.HTMLLayout"/>
    <cyclicBufferTracker class="ch.qos.logback.core.spi.CyclicBufferTracker"> 
        <bufferSize>5</bufferSize>
    </cyclicBufferTracker>
</appender>
```

## 5. Gmail 的SMTPAppender

如果我们使用 Gmail 作为我们的 SMTP 提供商，我们将必须通过 SSL 或 STARTTLS 进行身份验证。

要通过 STARTTLS 建立连接，客户端首先向服务器发出 STARTTLS 命令。如果服务器支持此通信，则连接会切换到 SSL。

现在让我们使用 STARTTLS 为 Gmail 配置我们的附加程序：

```xml
<appender name="emailAppender" class="ch.qos.logback.classic.net.SMTPAppender">
    <smtpHost>smtp.gmail.com</smtpHost>
    <smtpPort>587</smtpPort>
    <STARTTLS>true</STARTTLS>
    <asynchronousSending>false</asynchronousSending>
    <username>SENDER-EMAIL@gmail.com</username>
    <password>GMAIL-ACCT-PASSWORD</password>
    <to>EMAIL-RECIPIENT</to>
    <from>SENDER-EMAIL@gmail.com</from>
    <subject>BAELDUNG: %logger{20} - %msg</subject>
    <layout class="ch.qos.logback.classic.html.HTMLLayout"/>
</appender>
```

## 六. 总结

在本文中，我们探讨了如何配置 Logback 的SMTPAppender以在应用程序出现错误时发送电子邮件。