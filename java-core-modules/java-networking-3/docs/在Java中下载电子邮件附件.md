## 1. 概述

在本教程中，我们将了解如何使用Java下载电子邮件附件。为此，我们需要[JavaMail API](https://www.baeldung.com/java-email)。JavaMail API 可以作为 Maven 依赖项或单独的 jar 提供。

## 2.JavaMail API 概述

JavaMail API 用于撰写、发送和接收来自 Gmail 等电子邮件服务器的电子邮件。它为使用抽象类和接口的电子邮件系统提供了一个框架。API 支持大多数 RFC822 和 MIME Internet 消息传递协议，如 SMTP、POP、IMAP、MIME 和 NNTP。

## 3. JavaMail API 设置

我们需要在我们的Java项目中添加[javax.mail](https://search.maven.org/search?q=g:com.sun.mail a:javax.mail) Maven 依赖项以使用 JavaMail API：

```xml
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>javax.mail</artifactId> 
    <version>1.6.2</version>
</dependency>
```

## 4.下载电子邮件附件

为了在Java中处理电子邮件，我们使用javax.mail包中的Message类。消息实现javax.mail.Part接口。

Part接口有BodyPart和属性。带有附件的内容是一个名为MultiPart的BodyPart。如果[电子邮件有任何附件](https://www.baeldung.com/java-send-emails-attachments)，它的处置等于“ Part.ATTACHMENT ”。如果没有附件，则处置为null。来自Part接口的getDisposition方法为我们提供了配置。

我们通过一个简单的基于 Maven 的项目来了解下载电子邮件附件的工作原理。我们将专注于获取要下载的电子邮件并将附件保存到磁盘。

我们的项目有一个实用程序可以处理下载电子邮件并将它们保存到我们的磁盘。我们还显示附件列表。

要下载附件，我们首先检查内容类型是否包含多部分内容。如果是，我们可以进一步处理，检查该零件是否有附件。要检查内容类型，我们写：

```java
if (contentType.contains("multipart")) {
    //send to the download utility...
}
```

如果我们有一个多部分，我们首先检查它是否属于Part.ATTACHMENT类型，如果是，我们使用saveFile方法将文件保存到我们的目标文件夹。因此，在下载实用程序中，我们将检查：

```java
if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
    String file = part.getFileName();
    part.saveFile(downloadDirectory + File.separator + part.getFileName());
    downloadedAttachments.add(file);
}
```

由于我们使用的 JavaMail API 版本高于 1.4，因此我们可以使用Part接口中的saveFile方法。saveFile方法适用于File对象或String。我们在示例中使用了一个字符串。此步骤将附件保存到我们指定的文件夹中。我们还维护一个显示附件列表。

在 JavaMail API 1.4 版之前，我们必须使用FileStream 和InputStream逐字节写入整个文件。在我们的示例中，我们为 Gmail 帐户使用了 Pop3 服务器。因此，要调用示例中的方法，我们需要一个有效的 Gmail 用户名和密码以及一个用于下载附件的文件夹。

让我们看看下载附件并将它们保存到磁盘的示例代码：

```java
public List<String> downloadAttachments(Message message) throws IOException, MessagingException {
    List<String> downloadedAttachments = new ArrayList<String>();
    Multipart multiPart = (Multipart) message.getContent();
    int numberOfParts = multiPart.getCount();
    for (int partCount = 0; partCount < numberOfParts; partCount++) {
        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
            String file = part.getFileName();
            part.saveFile(downloadDirectory + File.separator + part.getFileName());
            downloadedAttachments.add(file);
        }
    }
    return downloadedAttachments;
}  
```

## 5.总结

本文展示了如何使用本机 JavaMail 库以Java下载电子邮件以下载电子邮件附件。