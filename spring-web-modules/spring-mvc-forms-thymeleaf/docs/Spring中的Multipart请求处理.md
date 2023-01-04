## 1. 简介

在本教程中，我们将重点介绍在Spring Boot中发送多部分请求的各种机制。多部分请求包括发送由边界分隔的许多不同类型的数据，作为单个HTTP方法调用的一部分。

通常，我们可以在这个请求中发送复杂的JSON、XML或CSV数据，以及传输multipart文件。多部分文件的示例包括音频或图像文件。此外，我们可以将简单的键/值对数据作为多部分请求与多部分文件一起发送。

现在让我们看看发送这些数据的各种方式。

## 2. 使用@ModelAttribute

让我们考虑一个简单的用例，即使用表单发送由姓名和文件组成的员工数据。

首先，我们将创建一个Employee抽象来存储表单数据：

```java
public class Employee {
    private String name;
    private MultipartFile document;
}
```

然后我们将使用[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)生成表单：

```html
<form action="#" th:action="@{/employee}" th:object="${employee}" method="post" enctype="multipart/form-data">
    <p>name: <input type="text" th:field="*{name}" /></p>
    <p>document:<input type="file" th:field="*{document}" multiple="multiple"/>
        <input type="submit" value="upload" />
        <input type="reset" value="Reset" /></p>
</form>
```

需要注意的重要一点是我们在视图中将enctype声明为multipart/form-data。

最后，我们将创建一个接受表单数据的方法，包括多部分文件：

```java
@RequestMapping(path = "/employee", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
public String saveEmployee(@ModelAttribute Employee employee) {
    employeeService.save(employee);
    return "employee/success";
}
```

这里，两个特别重要的细节是：

-   consumes属性值设置为multipart/form-data
-   @ModelAttribute已经将所有的表单数据抓取到Employee POJO中，包括上传的文件

## 3. 使用@RequestPart

此注解将多部分请求的一部分与方法参数相关联，这对于将复杂的多属性数据作为有效负载(例如JSON或XML)发送很有用。

让我们创建一个带有两个参数的方法，第一个是Employee类型，第二个是MultipartFile类型。此外，我们将使用@RequestPart注解这两个参数：

```java
@RequestMapping(path = "/requestpart/employee", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
public ResponseEntity<Object> saveEmployee(@RequestPart Employee employee, @RequestPart MultipartFile document) {
    employee.setDocument(document);
    employeeService.save(employee);
    return ResponseEntity.ok().build();
}
```

现在，要查看此注解的实际效果，我们将使用MockMultipartFile创建测试：

```java
@Test
public void givenEmployeeJsonAndMultipartFile_whenPostWithRequestPart_thenReturnsOK() throws Exception {
    MockMultipartFile employeeJson = new MockMultipartFile("employee", null, "application/json", "{\"name\": \"Emp Name\"}".getBytes());

    mockMvc.perform(multipart("/requestpart/employee")
        .file(A_FILE)
        .file(employeeJson))
        .andExpect(status().isOk());
}
```

上面要注意的重要一点是，我们将Employee部分的内容类型设置为application/JSON。除了多部分文件之外，我们还将此数据作为JSON文件发送。

可以在[此处](https://www.baeldung.com/spring-multipart-post-request-test#testing-a-multipart-post-request)找到有关如何测试多部分请求的更多详细信息。

## 4. 使用@RequestParam

发送多部分数据的另一种方法是使用@RequestParam，这对于简单数据特别有用，它作为键/值对与文件一起发送：

```java
@RequestMapping(path = "/requestparam/employee", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
public ResponseEntity<Object> saveEmployee(@RequestParam String name, @RequestPart MultipartFile document) {
    Employee employee = new Employee(name, document);
    employeeService.save(employee);
    return ResponseEntity.ok().build();
}
```

让我们为这个方法编写测试来演示：

```java
@Test
public void givenRequestPartAndRequestParam_whenPost_thenReturns200OK() throws Exception {
    mockMvc.perform(multipart("/requestparam/employee")
        .file(A_FILE)
        .param("name", "testname"))
        .andExpect(status().isOk());
}
```

## 5. 总结

在本文中，我们学习了如何在Spring Boot中有效地处理多部分请求。

最初，我们使用模型属性发送多部分表单数据。然后我们研究了如何使用@RequestPart和@RequestParam注解分别接收多部分数据。