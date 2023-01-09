## 1. 简介

在本快速教程中，我们将了解如何从 servlet 上传文件。

为实现这一点，我们将首先看到原始的 Jakarta EE 解决方案，它具有由原生@MultipartConfig注解提供的文件上传功能。

然后，我们将查看 Apache Commons [FileUpload](https://commons.apache.org/proper/commons-fileupload/using.html) 库，用于早期版本的 Servlet API。

## 2. 使用 Jakarta EE @MultipartConfig

Jakarta EE 能够支持开箱即用的多部分上传。

因此，在使用文件上传支持丰富 Jakarta EE 应用程序时，它可能是默认的首选。

首先，让我们在 HTML 文件中添加一个表单：

```html
<form method="post" action="multiPartServlet" enctype="multipart/form-data">
    Choose a file: <input type="file" name="multiPartServlet" />
    <input type="submit" value="Upload" />
</form>
```

表单应该使用enctype=”multipart/form-data”属性来定义，以表示分段上传。

接下来，我们要使用@MultipartConfig注解为我们的HttpServlet 注解正确的信息 ：

```java
@MultipartConfig(fileSizeThreshold = 1024  1024,
  maxFileSize = 1024  1024  5, 
  maxRequestSize = 1024  1024  5  5)
public class MultipartServlet extends HttpServlet {
    //...
}

```

然后，让我们确保设置了默认的服务器上传文件夹：

```java
String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
File uploadDir = new File(uploadPath);
if (!uploadDir.exists()) uploadDir.mkdir();

```

最后，我们可以使用getParts()方法从请求中轻松检索入站文件，并将其保存到磁盘：

```java
for (Part part : request.getParts()) {
    fileName = getFileName(part);
    part.write(uploadPath + File.separator + fileName);
}

```

请注意，在此示例中，我们使用了辅助方法 getFileName()：

```java
private String getFileName(Part part) {
    for (String content : part.getHeader("content-disposition").split(";")) {
        if (content.trim().startsWith("filename"))
            return content.substring(content.indexOf("=") + 2, content.length() - 1);
        }
    return Constants.DEFAULT_FILENAME;
}
```

对于 Servlet 3.1。项目，我们可以选择使用Part.getSubmittedFileName()方法：

```java
fileName = part.getSubmittedFileName();
```

## 3. 使用 Apache Commons FileUpload

如果我们不是在 Servlet 3.0 项目上，我们可以直接使用 Apache Commons FileUpload 库。

### 3.1. 设置

我们需要使用以下pom.xml依赖项来运行我们的示例：

```xml
<dependency> 
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.4</version>
</dependency>
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

通过快速搜索 Maven 的中央存储库可以找到最新版本：[commons-fileupload](https://search.maven.org/classic/#search|ga|1|a%3A"commons-fileupload" AND g%3A"commons-fileupload")和[commons-io](https://search.maven.org/classic/#search|ga|1|g%3A"commons-io" AND a%3A"commons-io")。

### 3.2. 上传Servlet

合并 Apache 的FileUpload库的三个主要部分如下：

-   .jsp页面中的上传表单。
-   配置你的DiskFileItemFactory和ServletFileUpload对象。
-   处理多部分文件上传的实际内容。

上传表单与上一节中的相同。

让我们继续创建我们的 Jakarta EE servlet。

在我们的请求处理方法中，我们可以将传入的HttpRequest包装起来，检查它是否是分段上传。

我们还将在我们的DiskFileItemFactory上指定临时(在处理时)分配给文件上传的资源 。

最后，我们将创建一个代表实际文件本身的ServletFileUpload对象。它将公开最终持久性服务器端的多部分上传的内容：

```java
if (ServletFileUpload.isMultipartContent(request)) {

    DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setSizeThreshold(MEMORY_THRESHOLD);
    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

    ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setFileSizeMax(MAX_FILE_SIZE);
    upload.setSizeMax(MAX_REQUEST_SIZE);
    String uploadPath = getServletContext().getRealPath("") 
      + File.separator + UPLOAD_DIRECTORY;
    File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
        uploadDir.mkdir();
    }
    //...
}
```

然后，我们可以提取这些内容并将它们写入磁盘：

```java
if (ServletFileUpload.isMultipartContent(request)) {
    //...
    List<FileItem> formItems = upload.parseRequest(request);
    if (formItems != null && formItems.size() > 0) {
        for (FileItem item : formItems) {
	    if (!item.isFormField()) {
	        String fileName = new File(item.getName()).getName();
	        String filePath = uploadPath + File.separator + fileName;
                File storeFile = new File(filePath);
                item.write(storeFile);
                request.setAttribute("message", "File "
                  + fileName + " has uploaded successfully!");
	    }
        }
    }
}
```

## 4.运行示例

在我们将项目编译成.war后，我们可以将其放入本地 Tomcat 实例并启动它。

从那里，我们可以调出主上传视图，其中会显示一个表单：

[![选择文件](https://www.baeldung.com/wp-content/uploads/2018/05/choosefile.png)](https://www.baeldung.com/wp-content/uploads/2018/05/choosefile.png)

成功上传文件后，我们应该看到消息：

[![文件成功](https://www.baeldung.com/wp-content/uploads/2018/05/filesuccess.png)](https://www.baeldung.com/wp-content/uploads/2018/05/filesuccess.png)

最后，我们可以检查我们的 servlet 中指定的位置：

[![图片保存](https://www.baeldung.com/wp-content/uploads/2018/05/imagesaved.png)](https://www.baeldung.com/wp-content/uploads/2018/05/imagesaved.png)

## 5.总结

而已！我们已经学习了如何使用 Jakarta EE 以及 Apache 的 Common FileUpload库提供多部分文件上传！