## 1. 简介

在本快速教程中，我们将创建一个小型 Web 应用程序并探索如何从Servlet返回 JSON 响应。

## 2.专家

对于我们的 Web 应用程序，我们将在pom.xml中包含javax.servlet-api 和 Gson 依赖项：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>${javax.servlet.version}</version>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>${gson.version}</version>
</dependency>

```

可以在此处找到最新版本的依赖项：[javax.servlet-api](https://search.maven.org/classic/#search|ga|1|a%3Ajavax.servlet-api g%3Ajavax.servlet)和[gson](https://search.maven.org/classic/#search|ga|1|a%3A"gson" g%3A"com.google.code.gson")。

我们还需要配置一个 Servlet 容器来部署我们的应用程序。 [本文](https://www.baeldung.com/tomcat-deploy-war)是开始了解如何在 Tomcat 上部署 WAR 的好地方。

## 3.创建实体

让我们创建一个Employee实体，稍后它将作为 JSON 从Servlet返回：

```java
public class Employee {
	
    private int id;
    
    private String name;
    
    private String department;
   
    private long salary;

    // constructors
    // standard getters and setters.
}
```

## 4.实体转JSON

要从Servlet发送 JSON 响应，我们首先需要将Employee对象转换为其 JSON 表示形式。

有许多Java库可用于将对象转换为 JSON 表示形式，反之亦然。其中最突出的是 Gson 和 Jackson 库。要了解 GSON 和 Jackson 之间的区别，请查看 [这篇文章](https://www.baeldung.com/jackson-vs-gson)。

使用 Gson 将对象转换为 JSON 表示的快速示例是：

```java
String employeeJsonString = new Gson().toJson(employee);
```

## 5.响应和内容类型

对于 HTTP Servlet，填充响应的正确过程：

1.  从响应中检索输出流
2.  填写响应头
3.  将内容写入输出流
4.  提交响应

在响应中，Content-Type标头告诉客户端返回内容的内容类型实际上是什么。

要生成 JSON 响应，内容类型应为application/json：

```java
PrintWriter out = response.getWriter();
response.setContentType("application/json");
response.setCharacterEncoding("UTF-8");
out.print(employeeJsonString);
out.flush();
```

必须始终在提交响应之前设置响应标头。提交响应后，Web 容器将忽略任何设置或添加标头的尝试。

在PrintWriter上调用flush()会提交响应。

## 6. 示例 Servlet

现在让我们看一个返回 JSON 响应的示例Servlet ：

```java
@WebServlet(name = "EmployeeServlet", urlPatterns = "/employeeServlet")
public class EmployeeServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doGet(
      HttpServletRequest request, 
      HttpServletResponse response) throws IOException {
        
        Employee employee = new Employee(1, "Karan", "IT", 5000);
        String employeeJsonString = this.gson.toJson(employee);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(employeeJsonString);
        out.flush();   
    }
}
```

## 七. 总结

本文展示了如何从 Servlet 返回 JSON 响应。这对于使用 Servlet 实现 REST 服务的 Web 应用程序很有帮助。