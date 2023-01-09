## 1. 概述

在本教程中，我们将说明如何使用 HttpClient 执行分段上传操作。

我们将使用http://echo.200please.com作为测试服务器，因为它是公开的并且接受大多数类型的内容。

如果你想更深入地研究并学习其他可以使用 HttpClient 做的很酷的事情——请继续[阅读主要的 HttpClient 教程](https://www.baeldung.com/httpclient-guide)。

## 2. 使用AddPart方法

让我们首先查看MultipartEntityBuilder对象，以将部件添加到 Http 实体，然后通过 POST 操作上传该实体。

这是将部件添加到表示表单的HttpEntity的通用方法。

示例 2.1。–上传包含两个文本部分和一个文件的表单

```java
File file = new File(textFileName);
HttpPost post = new HttpPost("http://echo.200please.com");
FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
StringBody stringBody1 = new StringBody("Message 1", ContentType.MULTIPART_FORM_DATA);
StringBody stringBody2 = new StringBody("Message 2", ContentType.MULTIPART_FORM_DATA);
// 
MultipartEntityBuilder builder = MultipartEntityBuilder.create();
builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
builder.addPart("upfile", fileBody);
builder.addPart("text1", stringBody1);
builder.addPart("text2", stringBody2);
HttpEntity entity = builder.build();
//
post.setEntity(entity);
HttpResponse response = client.execute(post);
```

请注意，我们还通过指定服务器要使用的ContentType值来实例化File对象。

另外，请注意addPart方法有两个参数，就像表单的键/值对一样。这些仅在服务器端实际期望并使用参数名称时才相关——否则，它们将被忽略。

## 3. 使用addBinaryBody和addTextBody方法

创建多部分实体的更直接的方法是使用addBinaryBody和AddTextBody方法。这些方法适用于上传文本、文件、字符数组和InputStream对象。让我们用简单的例子来说明如何。

示例 3.1。–上传文本和文本文件部分

```java
HttpPost post = new HttpPost("http://echo.200please.com");
File file = new File(textFileName);
String message = "This is a multipart post";
MultipartEntityBuilder builder = MultipartEntityBuilder.create();
builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
builder.addBinaryBody("upfile", file, ContentType.DEFAULT_BINARY, textFileName);
builder.addTextBody("text", message, ContentType.DEFAULT_BINARY);
// 
HttpEntity entity = builder.build();
post.setEntity(entity);
HttpResponse response = client.execute(post);
```

请注意，此处不需要FileBody和StringBody对象。

同样重要的是，大多数服务器不检查文本正文的ContentType，因此addTextBody方法可能会省略ContentType值。

addBinaryBody API 接受ContentType——但也可以仅从二进制主体和保存文件的表单参数的名称创建实体。如前一节所述，如果未指定ContentType值，某些服务器将无法识别该文件。

接下来，我们将添加一个 zip 文件作为InputStream，而图像文件将作为File对象添加：

示例 3.2。–上传 Zip 文件、图像文件和文本部分

```java
HttpPost post = new HttpPost("http://echo.200please.com");
InputStream inputStream = new FileInputStream(zipFileName);
File file = new File(imageFileName);
String message = "This is a multipart post";
MultipartEntityBuilder builder = MultipartEntityBuilder.create();         
builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
builder.addBinaryBody
  ("upfile", file, ContentType.DEFAULT_BINARY, imageFileName);
builder.addBinaryBody
  ("upstream", inputStream, ContentType.create("application/zip"), zipFileName);
builder.addTextBody("text", message, ContentType.TEXT_PLAIN);
// 
HttpEntity entity = builder.build();
post.setEntity(entity);
HttpResponse response = client.execute(post);
```

请注意，ContentType值可以即时创建，就像上面 zip 文件示例中的情况一样。

最后，并非所有服务器都承认InputStream部分。我们在第一行代码中实例化的服务器识别InputStream。

现在让我们看另一个示例，其中addBinaryBody直接使用字节数组：

示例 3.3。–上传字节数组和文本

```java
HttpPost post = new HttpPost("http://echo.200please.com");
String message = "This is a multipart post";
byte[] bytes = "binary code".getBytes(); 
// 
MultipartEntityBuilder builder = MultipartEntityBuilder.create();
builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
builder.addBinaryBody("upfile", bytes, ContentType.DEFAULT_BINARY, textFileName);
builder.addTextBody("text", message, ContentType.TEXT_PLAIN);
// 
HttpEntity entity = builder.build();
post.setEntity(entity);
HttpResponse response = client.execute(post);
```

注意ContentType——它现在指定二进制数据。

## 4. 总结

本文介绍了MultipartEntityBuilder作为一个灵活的对象，它提供了多种 API 选择来创建多部分表单。

这些示例还展示了如何使用HttpClient上传类似于表单实体的 HttpEntity 。