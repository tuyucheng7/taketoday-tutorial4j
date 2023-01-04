## 1. 概述

[Apache Commons FileLoad](https://commons.apache.org/proper/commons-fileupload/index.html)库帮助我们使用multipart/form-data内容类型通过HTTP协议上传大文件。

在本快速教程中，我们将了解如何将其与Spring集成。

## 2. Maven依赖

要使用该库，我们需要commons-fileupload工件：

```xml
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.4</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/search?q=a:commons-fileupload)上找到。

## 3. 一次全部转移

出于演示目的，我们将创建一个控制器处理带有文件有效负载的请求：

```java
@PostMapping("/upload")
public String handleUpload(HttpServletRequest request) throws Exception {
    boolean isMultipart = ServletFileUpload.isMultipartContent(request);

    DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
    factory.setSizeThreshold(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
    factory.setFileCleaningTracker(null);

    ServletFileUpload upload = new ServletFileUpload(factory);

    List items = upload.parseRequest(request);

    Iterator iter = items.iterator();
    while (iter.hasNext()) {
        FileItem item = iter.next();

        if (!item.isFormField()) {
            try (InputStream uploadedStream = item.getInputStream();
                OutputStream out = new FileOutputStream("file.mov");) {

                IOUtils.copy(uploadedStream, out);
            }
        }
    }    
    return "success!";
}

```

一开始，我们需要使用库中ServletFileUpload类中的isMultipartContent方法检查请求是否包含多部分内容。

默认情况下，Spring具有一个MultipartResolver，我们需要禁用它才能使用此库。否则，它将在请求到达我们的控制器之前读取请求的内容。

我们可以通过在application.properties文件中包含此配置来实现此目的：

```properties
spring.http.multipart.enabled=false
```

现在，我们可以设置要保存文件的目录、库决定写入磁盘的阈值以及请求结束后是否应删除文件。

该库提供了一个DiskFileItemFactory类，负责文件保存和清理的配置。setRepository方法设置目标目录，示例中显示默认值。

接下来，setSizeThreshold设置最大文件大小。

然后，我们有setFileCleaningTracker方法，当设置为null时，临时文件保持不变。默认情况下，它会在请求完成后删除它们。

现在我们可以继续实际的文件处理。

首先，我们通过包含之前创建的工厂来创建我们的ServletFileUpload；然后我们继续解析请求并生成一个FileItem列表，它们是表单字段库的主要抽象。

现在，如果我们知道它不是一个普通的表单字段，那么我们将继续提取InputStream并从IOUtils调用有用的方法(有关更多选项，你可以查看[本教程](https://www.baeldung.com/convert-input-stream-to-a-file))。

现在我们将文件存储在必要的文件夹中。这通常是处理这种情况的更方便的方法，因为它允许轻松访问文件，但时间/内存效率也不是最佳的。

在下一节中，我们将了解流式API。

## 4. 流式API

流式API易于使用，使其成为处理大文件的好方法，无需到临时位置：

```java
ServletFileUpload upload = new ServletFileUpload();
FileItemIterator iterStream = upload.getItemIterator(request);
while (iterStream.hasNext()) {
    FileItemStream item = iterStream.next();
    String name = item.getFieldName();
    InputStream stream = item.openStream();
    if (!item.isFormField()) {
        // Process the InputStream
    } else {
        String formFieldValue = Streams.asString(stream);
    }
}
```

我们可以在前面的代码片段中看到我们不再包含DiskFileItemFactory。这是因为，在使用流式API时，我们不需要它。

接下来，为了处理字段，该库提供了一个FileItemIterator，在我们使用下一个方法从请求中提取它们之前，它不会读取任何内容。

最后，我们可以看到如何获取其他表单域的值。

## 5. 总结

在本文中，我们回顾了如何将Apache Commons FileLoad库与Spring一起使用来上传和处理大文件。