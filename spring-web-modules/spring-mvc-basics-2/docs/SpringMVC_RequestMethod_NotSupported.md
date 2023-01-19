## 1. 概述

本文主要介绍一个常见错误 - ”Request Method not Supported – 405“，开发人员在使用Spring MVC为特定HTTP方法公开API时可能会遇到这个错误。

## 2. HTTP请求方法基础

MVC HTTP方法是请求可以在服务器上触发的基本操作。
例如，有些HTTP请求方法从服务器获取数据，有些方法向服务器提交数据，有些方法可能删除数据等。

@RequestMapping注解指定请求支持的方法。

Spring在枚举类RequestMethod中声明所有受支持的请求方法；该枚举指定了标准的GET、HEAD、POST、PUT、PATCH、DELETE、OPTIONS和TRACE动词。

默认情况下，Spring DispatcherServlet支持所有这些，OPTIONS和TRACE除外；@RequestMapping使用RequestMethod枚举指定支持哪些方法。

## 3. 简单的MVC场景

现在，让我们来看一个映射所有HTTP方法的代码示例：

```java

@RestController
@RequestMapping(value = "/api")
public class RequestMethodController {

    @Autowired
    EmployeeService service;

    @RequestMapping(value = "/employees", produces = "application/json")
    public List<Employee> findEmployees() throws InvalidRequestException {
        return service.getEmployeeList();
    }
}
```

注意findEmployee()方法是如何声明的，它没有指定任何特定的请求方法，这意味着该URL支持所有默认方法。

我们可以使用不同的HTTP方法请求API，例如使用curl：

```shell
$ curl --request POST http://localhost:8080/api/employees
[{"id":100,"name":"Steve Martin","contactNumber":"333-777-999"},
{"id":200,"name":"Adam Schawn","contactNumber":"444-111-777"}]
```

当然，我们可以通过多种方式发送请求，例如通过简单的curl命令、Postman、AJAX等。

## 4. 问题场景 - HTTP 405

但是，我们将在这里讨论的是请求不成功的场景。

”405 Method Not Allowed“是我们在处理Spring请求时遇到的最常见错误之一。

让我们看看如果我们在Spring MVC中专门定义和处理GET请求会发生什么，比如：

```java
/*@formatter:off*/
@RequestMapping(value = "/employees", produces = "application/json", method = RequestMethod.GET)
public List<Employee> findEmployees() {
    // ...
}
/*@formatter:on*/
```

```shell
// send the PUT request using CURL
$ curl --request PUT http://localhost:8080/api/employees
{"timestamp":1539720588712,"status":405,"error":"Method Not Allowed",
"exception":"org.springframework.web.HttpRequestMethodNotSupportedException",
"message":"Request method 'PUT' not supported","path":"/api/employees"}
```

## 5. 405 Not Support原因及解决方案

**在前面的场景中，我们得到的是带有405状态码的HTTP响应，这是一个客户端错误，表明服务器不支持请求中发送的方法/动词**。

顾名思义，此错误的原因是使用不受支持的方法发送请求。

因此，我们可以通过在现有的方法映射中为PUT定义显式映射来解决这个问题：

```text
@RequestMapping(value = "/employees", produces = "application/json", method = {RequestMethod.GET, RequestMethod.PUT}) 
...
```

或者，我们可以单独定义新方法/映射：

```java
/*@formatter:off*/
@RequestMapping(value = "/employees", produces = "application/json", method=RequestMethod.PUT)
public List<Employee> postEmployees(){
    //
}
/*@formatter:on*/
```

## 6. 总结

请求方法/动词是HTTP通信中的一个关键方面，我们需要清楚在服务器端定义的操作的确切语义，以及我们发送的确切请求。