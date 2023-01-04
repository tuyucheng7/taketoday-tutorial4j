## 1. 简介

RestTemplate类是在Spring中执行客户端HTTP操作的核心工具。它提供了多种用于构建HTTP请求和处理响应的实用方法。

由于RestTemplate与[Jackson](https://github.com/FasterXML/jackson)集成得很好，它可以毫不费力地将大多数对象序列化/反序列化到JSON或从JSON中反序列化。然而，处理对象集合并不是那么简单。

在本教程中，我们将学习如何使用RestTemplate获取和发布对象列表。

## 2. 示例服务

我们将使用具有两个HTTP端点的员工API，获取所有并创建：

-   GET /employees
-   POST /employees

对于客户端和服务器之间的通信，我们将使用一个简单的DTO来封装基本的员工数据：

```java
public class Employee {
    public long id;
    public String title;

    // standard constructor and setters/getters
}
```

现在我们准备好编写使用RestTemplate获取和创建Employee对象列表的代码。

## 3. 使用RestTemplate获取对象列表

通常在调用GET时，我们可以使用RestTemplate中的一种简化方法，例如：

getForObject(URI url, Class<T\> responseType)

这将使用GET动词向指定的URI发送请求，并将响应主体转换为请求的Java类型。这对大多数课程来说都很好，但它有一个限制； 我们不能发送对象列表。

问题是由于Java泛型的泛型擦除。当应用程序运行时，它不知道列表中的对象是什么类型。这意味着无法将列表中的数据反序列化为适当的类型。

幸运的是，我们有两种选择来解决这个问题。

### 3.1 使用数组

首先，我们可以使用RestTemplate。getForEntity()通过responseType参数获取对象数组。我们在那里指定的任何类都将匹配ResponseEntity的参数类型：

```java
ResponseEntity<Employee[]> response = restTemplate.getForEntity(
    "http://localhost:8080/employees/",
    Employee[].class);
Employee[] employees = response.getBody();
```

我们也可以使用RestTemplate.exchange来获得相同的结果。

请注意，这里执行繁重工作的协作者是ResponseExtractor，因此如果我们需要进一步定制，我们可以调用execute并提供我们自己的实例。

### 3.2 使用包装类

某些API将返回包含员工列表的顶级对象，而不是直接返回列表。为了处理这种情况，我们可以使用包含员工列表的包装类。

```java
public class EmployeeList {
    private List<Employee> employees;

    public EmployeeList() {
        employees = new ArrayList<>();
    }

    // standard constructor and getter/setter
}
```

现在我们可以使用更简单的getForObject()方法来获取员工列表：

```java
EmployeeList response = restTemplate.getForObject("http://localhost:8080/employees", EmployeeList.class);
List<Employee> employees = response.getEmployees();
```

这段代码要简单得多，但需要一个额外的包装器对象。

## 4. 使用RestTemplate发布对象列表

现在让我们看看如何将对象列表从客户端发送到服务器。和上面一样，RestTemplate提供了一个简化的调用POST的方法：

postForObject(URI url, Object request, Class<T\> responseType)

这会将带有可选请求正文的HTTP POST发送到给定的URI，并将响应转换为指定的类型。与上面的GET场景不同，我们不必担心类型擦除。

这是因为现在我们要从Java对象转向JSON，JVM知道对象列表及其类型，因此它们将被正确序列化：

```java
List<Employee> newEmployees = new ArrayList<>();
newEmployees.add(new Employee(3, "Intern"));
newEmployees.add(new Employee(4, "CEO"));

restTemplate.postForObject("http://localhost:8080/employees/", newEmployees, ResponseEntity.class);
```

### 4.1 使用包装类

如果我们需要使用包装类来与上面的GET场景保持一致，那也很简单。我们可以使用RestTemplate发送一个新列表：

```java
List<Employee> newEmployees = new ArrayList<>();
newEmployees.add(new Employee(3, "Intern"));
newEmployees.add(new Employee(4, "CEO"));

restTemplate.postForObject("http://localhost:8080/employees", new EmployeeList(newEmployees), ResponseEntity.class);
```

## 5. 总结

使用RestTemplate是一种构建HTTP客户端以与我们的服务进行通信的简单方法。

它提供了许多方法来处理每个HTTP方法和简单对象。通过一些额外的代码，我们可以轻松地使用它来处理对象列表。