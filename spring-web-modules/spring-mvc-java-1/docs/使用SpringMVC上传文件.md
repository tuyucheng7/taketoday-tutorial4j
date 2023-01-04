## 1. 概述

在前面的教程中，我们介绍了[表单处理](https://www.baeldung.com/spring-mvc-form-tutorial)的基础知识，并探索了Spring MVC中的[表单标签库](https://www.baeldung.com/spring-mvc-form-tags)。

在本教程中，我们重点介绍Spring为Web应用程序中的多部分(文件上传)支持提供的功能。

Spring允许我们使用可插入的MultipartResolver对象启用这种多部分支持。该框架提供了一个用于Commons FileUpload的MultipartResolver实现，以及另一个用于Servlet 3.0多部分请求解析的实现。

配置MultipartResolver后，我们将看到如何上传单个文件和多个文件。

我们还将涉及Spring Boot。

## 2. 共享文件上传

要使用CommonsMultipartResolver处理文件上传，我们需要添加以下依赖：

```xml
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.4</version>
</dependency>
```

现在我们可以在我们的Spring配置中定义CommonsMultipartResolver bean。

这个MultipartResolver带有一系列设置方法来定义属性，例如上传的最大大小：

```java
@Bean(name = "multipartResolver")
public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    multipartResolver.setMaxUploadSize(100000);
    return multipartResolver;
}
```

这里我们需要在Bean定义本身中控制CommonsMultipartResolver的不同属性。

## 3. 使用Servlet 3.0

为了使用Servlet 3.0多部分解析，我们需要配置几个应用程序。

首先，我们需要在DispatcherServlet注册中设置一个MultipartConfigElement：

```java
public class MainWebAppInitializer implements WebApplicationInitializer {

    private String TMP_FOLDER = "/tmp";
    private int MAX_UPLOAD_SIZE = 5 * 1024 * 1024;

    @Override
    public void onStartup(ServletContext sc) throws ServletException {

        ServletRegistration.Dynamic appServlet = sc.addServlet("mvc", new DispatcherServlet(
              new GenericWebApplicationContext()));

        appServlet.setLoadOnStartup(1);

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(TMP_FOLDER,
              MAX_UPLOAD_SIZE, MAX_UPLOAD_SIZE * 2, MAX_UPLOAD_SIZE / 2);

        appServlet.setMultipartConfig(multipartConfigElement);
    }
}
```

在MultipartConfigElement对象中，我们配置了存储位置、最大单个文件大小、最大请求大小(单个请求中多个文件的情况)以及文件上传进度刷新到存储位置的大小。

这些设置必须在Servlet注册级别应用，因为Servlet 3.0不允许它们像CommonsMultipartResolver那样在MultipartResolver中注册。

完成后，我们可以将StandardServletMultipartResolver添加到我们的Spring配置中：

```java
@Bean
public StandardServletMultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
}
```

## 4. 上传文件 

要上传我们的文件，我们可以构建一个简单的表单，其中我们使用带有type='file'的HTML输入标签。

无论我们选择了哪种上传处理配置，我们都需要将表单的编码属性设置为multipart/form-data。

这让浏览器知道如何编码表单：

```html
<form:form method="POST" action="/spring-mvc-xml/uploadFile" enctype="multipart/form-data">
    <table>
        <tr>
            <td>
                <form:label path="file">Select a file to upload</form:label>
            </td>
            <td><input type="file" name="file"/></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit"/></td>
        </tr>
    </table>
</form:form>
```

要存储上传的文件，我们可以使用MultipartFile变量。

我们可以从控制器方法中的请求参数中检索此变量：

```java
@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap) {
    modelMap.addAttribute("file", file);
    return "fileUploadView";
}
```

MultipartFile类提供对上传文件详细信息的访问，包括文件名、文件类型等。

我们可以使用一个简单的HTML页面来显示这些信息：

```html
<h2>Submitted File</h2>
<table>
    <tr>
        <td>OriginalFileName:</td>
        <td>${file.originalFilename}</td>
    </tr>
    <tr>
        <td>Type:</td>
        <td>${file.contentType}</td>
    </tr>
</table>
```

## 5. 上传多个文件

要在单个请求中上传多个文件，我们只需在表单中放置多个输入文件字段：

```html
<form:form method="POST" action="/spring-mvc-java/uploadMultiFile" enctype="multipart/form-data">
    <table>
        <tr>
            <td>Select a file to upload</td>
            <td><input type="file" name="files"/></td>
        </tr>
        <tr>
            <td>Select a file to upload</td>
            <td><input type="file" name="files"/></td>
        </tr>
        <tr>
            <td>Select a file to upload</td>
            <td><input type="file" name="files"/></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit"/></td>
        </tr>
    </table>
</form:form>
```

我们需要注意每个输入字段都具有相同的名称，以便可以将其作为MultipartFile的数组进行访问：

```java
@RequestMapping(value = "/uploadMultiFile", method = RequestMethod.POST)
public String submit(@RequestParam("files") MultipartFile[] files, ModelMap modelMap) {
    modelMap.addAttribute("files", files);
    return "fileUploadView";
}
```

现在我们可以简单地遍历该数组来显示文件信息：

```html
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Spring MVC File Upload</title>
</head>
<body>
<h2>Submitted Files</h2>
<table>
    <c:forEach items="${files}" var="file">
        <tr>
            <td>OriginalFileName:</td>
            <td>${file.originalFilename}</td>
        </tr>
        <tr>
            <td>Type:</td>
            <td>${file.contentType}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
```

## 6. 上传带有附加表格数据的文件

我们还可以将附加信息与正在上传的文件一起发送到服务器。

我们只需要在表单中包含必填字段：

```html
<form:form method="POST"
           action="/spring-mvc-java/uploadFileWithAddtionalData"
           enctype="multipart/form-data">
    <table>
        <tr>
            <td>Name</td>
            <td><input type="text" name="name"/></td>
        </tr>
        <tr>
            <td>Email</td>
            <td><input type="text" name="email"/></td>
        </tr>
        <tr>
            <td>Select a file to upload</td>
            <td><input type="file" name="file"/></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit"/></td>
        </tr>
    </table>
</form:form>
```

在控制器中，我们可以使用@RequestParam注解获取所有表单数据：

```java
@PostMapping("/uploadFileWithAddtionalData")
public String submit(@RequestParam MultipartFile file, @RequestParam String name, @RequestParam String email, ModelMap modelMap) {
    modelMap.addAttribute("name", name);
    modelMap.addAttribute("email", email);
    modelMap.addAttribute("file", file);
    return "fileUploadView";
}
```

与前面几节类似，我们可以使用带有JSTL标签的HTML页面来显示信息。

我们还可以将所有表单字段封装在一个模型类中，并在控制器中使用@ModelAttribute注解。当文件中有很多附加字段时，这会很有帮助。

让我们看一下代码：

```java
public class FormDataWithFile {

    private String name;
    private String email;
    private MultipartFile file;

    // standard getters and setters
}
```

```java
@PostMapping("/uploadFileModelAttribute")
public String submit(@ModelAttribute FormDataWithFile formDataWithFile, ModelMap modelMap) {

    modelMap.addAttribute("formDataWithFile", formDataWithFile);
    return "fileUploadView";
}
```

## 7. Spring Boot文件上传

如果我们使用的是Spring Boot，那么到目前为止我们所看到的一切仍然适用。

然而，Spring Boot使配置和启动一切变得更加容易，而且几乎没有麻烦。

特别是，不需要配置任何Servlet，因为Boot会为我们注册和配置它，前提是我们在依赖项中包含web模块：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>
```

我们可以在Maven Central上找到最新版本的[spring-boot-starter-web](https://search.maven.org/search?q=spring-boot-starter-web)。

如果我们想控制最大文件上传大小，我们可以编辑我们的application.properties：

```properties
spring.servlet.multipart.max-file-size=128KB
spring.servlet.multipart.max-request-size=128KB
```

我们还可以控制是否启用文件上传以及文件上传的位置：

```properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.location=${java.io.tmpdir}
```

请注意，我们使用${java.io.tmpdir}来定义上传位置，以便我们可以为不同的操作系统使用临时位置。

## 8. 总结

在本文中，我们研究了在Spring中配置多部分支持的不同方法。使用这些，我们可以在我们的网络应用程序中支持文件上传。

本教程的实现可以在[GitHub项目](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-mvc-java)中找到。

项目在本地运行时，表单示例可以访问http://localhost:8080/spring-mvc-java/fileUpload。