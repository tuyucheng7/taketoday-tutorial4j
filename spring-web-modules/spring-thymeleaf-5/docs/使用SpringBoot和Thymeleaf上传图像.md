## 1. 概述

在本快速教程中，我们将了解如何使用Spring Boot和Thymeleaf在Java Web应用程序中上传图像。

## 2. 依赖关系

我们只需要两个依赖项Spring Boot Web和Thymeleaf：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

## 3. Spring Boot控制器

我们的第一步是创建一个Spring Boot Web控制器来处理我们的请求：

```java
@Controller
public class UploadController {

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @GetMapping("/uploadimage")
    public String displayUploadForm() {
        return "imageupload/index";
    }

    @PostMapping("/upload")
    public String uploadImage(Model model, @RequestParam("image") MultipartFile file) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        fileNames.append(file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
        model.addAttribute("msg", "Uploaded images: " + fileNames.toString());
        return "imageupload/index";
    }
}
```

我们定义了两种方法来处理HTTP GET请求。displayUploadForm()方法处理GET请求并返回要显示给用户的Thymeleaf模板的名称，以便让他导入图像。

uploadImage()方法处理图像上传。它接受关于图像上传的multipart/form-data POST请求，并将图像保存在本地文件系统中。MultipartFile接口是Spring Boot提供的一种特殊的数据结构，用于表示多部分请求中的上传文件。

最后，我们创建了一个上传文件夹，用于存储所有上传的图像。我们还添加了一条消息，其中包含上传图像的名称，以在用户提交表单后显示。

## 4. Thymeleaf模板

第二步是创建一个Thymeleaf模板，我们将在路径src/main/resources/templates中调用index.html。此模板显示一个HTML表单以允许用户选择和上传图像。此外，我们使用accept="image/"属性来允许用户只导入图像，而不是导入任何类型的文件。

让我们看看index.html文件的结构：

```xml
<body>
    <section class="my-5">
        <div class="container">
            <div class="row">
                <div class="col-md-8 mx-auto">
                    <h2>Upload Image Example</h2>
                    <p th:text="${message}" th:if="${message ne null}" class="alert alert-primary"></p>
                    <form method="post" th:action="@{/upload}" enctype="multipart/form-data">
                        <div class="form-group">
                            <input type="file" name="image" accept="image/*" class="form-control-file">
                        </div>
                        <button type="submit" class="btn btn-primary">Upload image</button>
                    </form>
                    <span th:if="${msg != null}" th:text="${msg}"></span>
                </div>
            </div>
        </div>
    </section>
</body>
```

## 5. 自定义文件大小

如果我们尝试上传大文件，将抛出MaxUploadSizeExceededException异常。但是，我们可以通过在application.properties文件中定义的属性spring.servlet.multipart.max-file-size和spring.servlet.multipart.max-request-size来调整文件上传限制：

```properties
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

## 6. 总结

在这篇快速文章中，我们介绍了如何在基于Spring Boot和Thymeleaf的Java Web应用程序中上传图像。