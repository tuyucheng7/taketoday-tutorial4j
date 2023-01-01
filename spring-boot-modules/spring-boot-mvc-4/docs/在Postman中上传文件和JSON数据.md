## 一、概述

[Postman](https://www.baeldung.com/tag/postman)是一个流行的 API 平台，它优化了 API 开发生命周期的各个步骤。无需编写任何代码即可使用 Postman 来[测试我们的 API 。](https://www.baeldung.com/postman-testing-collections)我们可以使用独立应用程序或浏览器扩展。

在本教程中，我们将了解如何使用 Postman 上传文件和 JSON 数据。

## 2. 应用设置

让我们设置一个基本的Spring Boot 应用程序，它公开端点以上传数据。

### 2.1. 依赖关系

我们在pom.xml中定义了一个具有[spring-boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)依赖关系的基本 spring 应用程序：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 2.2. 模型

接下来，让我们为 JSON 输入定义一个简单的模型类：

```java
public class JsonRequest {
    int id;
    String name;
}
```

为简洁起见，我们删除了构造函数、getter/setter 等的声明。

### 2.3. 端点

最后，让我们根据用例设置端点以将请求作为文件处理：

```java
@PostMapping("/uploadFile")
public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file){
    return ResponseEntity.ok().body("file received successfully");
}
```

在方法handleFileUpload() 中，我们期望将MultipartFile作为输入，然后返回带有静态文本的200状态消息。我们保持简单，没有探索保存或处理输入文件。

[MultipartFile](https://www.baeldung.com/sprint-boot-multipart-requests)由 Spring-Web 提供，它代表一个上传的文件。然后将该文件存储在内存中或临时存储在磁盘上，一旦请求处理完成，该文件随后就会被清除。

我们还创建一个处理 JSON 数据的端点：

```java
@PostMapping("/uploadJson")
public ResponseEntity<String> handleJsonInput(@RequestBody JsonRequest json){
    return ResponseEntity.ok().body(json.getId()+json.getName());
}
```

在这里，handleJsonInput()，我们期望一个JsonRequest 类型的对象，即我们定义的模型类。该方法返回200 HTTP 状态代码，响应中包含输入详细信息ID和名称。

我们使用了注解[@RequestBody](https://www.baeldung.com/spring-request-response-body)将输入反序列化为JsonRequest对象。通过这种方式，我们看到了对 JSON 进行简单处理以验证输入。

## 3.上传数据

我们已经设置了应用程序，现在让我们检查向应用程序提供输入的两种方式。

### 3.1. 将 JSON 上传到 Postman

JSON 是端点的文本输入类型之一。我们将按照以下步骤将其传递给公开的端点。

默认方法设置为GET。所以一旦我们添加了本地主机URL，我们需要选择POST作为方法：


[![img](https://www.baeldung.com/wp-content/uploads/2022/10/JsonStep1.jpg)](https://www.baeldung.com/wp-content/uploads/2022/10/JsonStep1.jpg)

让我们点击Body选项卡，然后选择raw。在显示Text的下拉列表中，让我们选择JSON作为输入：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/JsonStep2.jpg)](https://www.baeldung.com/wp-content/uploads/2022/10/JsonStep2.jpg)

我们需要粘贴输入的 JSON，然后单击发送：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/JsonStep3.jpg)](https://www.baeldung.com/wp-content/uploads/2022/10/JsonStep3.jpg)

正如我们在快照底部看到的那样，我们收到了一个200状态代码作为响应。此外，输入中的id和名称在响应正文中返回，确认 JSON 已在端点处正确处理。

### 3.2. 将文件上传到 Postman

让我们在这里以文档文件为例，因为我们没有定义任何关于端点可以使用哪些文件类型的约束。

让我们添加本地主机URL 并选择POST作为方法，因为该方法默认为GET：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/FileUploadStep1.jpg)](https://www.baeldung.com/wp-content/uploads/2022/10/FileUploadStep1.jpg)

让我们单击正文选项卡，然后选择表单数据。在键值对的第一行，我们单击键字段右上角的下拉菜单，然后选择文件作为输入：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/FileUploadStep2.jpg)](https://www.baeldung.com/wp-content/uploads/2022/10/FileUploadStep2.jpg)

我们需要在键列中添加作为端点的@RequestParam的文本文件 ，并浏览值列所需的文件。

最后，让我们点击发送：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/FileUploadStep3.jpg)](https://www.baeldung.com/wp-content/uploads/2022/10/FileUploadStep3.jpg)

当我们点击Send时，我们会得到一个200 HTTP 状态代码，其中包含端点定义中定义的静态文本。这意味着我们的文件已成功传送到端点，没有错误或异常。

## 4。总结

在本文中，我们构建了一个简单的 Spring Boot 应用程序，并研究了通过 Postman 向公开端点提供数据的两种不同方式。