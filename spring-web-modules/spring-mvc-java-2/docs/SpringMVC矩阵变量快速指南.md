## 1. 概述

URI规范[RFC 3986](https://tools.ietf.org/html/rfc3986#section-3.3)将URI路径参数定义为名称-值对。矩阵变量是Spring创造的术语，是传递和解析URI路径参数的替代实现。

矩阵变量支持在Spring MVC 3.2中可用，旨在简化具有大量参数的请求。

在本文中，我们将展示如何简化在URI的不同路径段内使用变量或可选路径参数的复杂GET请求。

## 2. 配置

要启用Spring MVC矩阵变量，让我们从配置开始：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }
}
```

否则，默认情况下它们是禁用的。

## 3. 如何使用矩阵变量

这些变量可以出现在路径的任何部分，字符等号('=')用于赋值，分号(';')用于分隔每个矩阵变量。在同一路径上，我们也可以重复相同的变量名或使用字符逗号(',')分隔不同的值。

我们的示例有一个提供有关员工信息的控制器。每个员工都有一个工作区域，我们可以按该属性进行搜索。以下请求可用于搜索：

```bash
http://localhost:8080/spring-mvc-java-2/employeeArea/workingArea=rh,informatics,admin
```

或者像这样：

```bash
http://localhost:8080/spring-mvc-java-2
  /employeeArea/workingArea=rh;workingArea=informatics;workingArea=admin
```

当我们想在Spring MVC中引用这些变量时，我们应该使用注解@MatrixVariable。

在我们的示例中，我们将使用Employee类：

```java
public class Employee {

    private long id;
    private String name;
    private String contactNumber;

    // standard setters and getters 
}
```

还有公司类：

```java
public class Company {

    private long id;
    private String name;

    // standard setters and getters
}
```

这两个类将绑定请求参数。

## 4. 定义矩阵变量属性

我们可以为变量指定必需的或默认的属性。在下面的示例中，contactNumber是必需的，因此它必须包含在我们的路径中，如下所示：

```bash
http://localhost:8080/spring-mvc-java-2/employeesContacts/contactNumber=223334411
```

该请求将通过以下方法处理：

```java
@RequestMapping(value = "/employeesContacts/{contactNumber}", method = RequestMethod.GET)
@ResponseBody
public ResponseEntity<List<Employee>> getEmployeeByContactNumber(@MatrixVariable(required = true) String contactNumber) {
    List<Employee> employeesList = new ArrayList<Employee>();
    // ...
    return new ResponseEntity<List<Employee>>(employeesList, HttpStatus.OK);
}
```

结果，我们将获得所有联系电话为223334411的员工。

## 5. 补充参数

矩阵变量可以补充路径变量。

例如，我们正在搜索员工的姓名，但我们也可以包括他/她联系电话的起始号码。

这个搜索的请求应该是这样的：

```bash
http://localhost:8080/spring-mvc-java-2/employees/John;beginContactNumber=22001
```

该请求将通过以下方法处理：

```java
@RequestMapping(value = "/employees/{name}", method = RequestMethod.GET)
@ResponseBody
public ResponseEntity<List<Employee>> getEmployeeByNameAndBeginContactNumber(@PathVariable String name, @MatrixVariable String beginContactNumber) {
    List<Employee> employeesList = new ArrayList<Employee>();
    // ...
    return new ResponseEntity<>(employeesList, HttpStatus.OK);
}
```

结果，我们将获得所有联系电话为22001或姓名为John的员工。

## 6. 绑定所有矩阵变量

如果出于某种原因，我们想要获取路径上所有可用的变量，我们可以将它们绑定到Map：

```bash
http://localhost:8080/spring-mvc-java-2/employeeData/id=1;name=John;contactNumber=2200112334
```

该请求将通过以下方法处理：

```java
@GetMapping("employeeData/{employee}")
@ResponseBody
public ResponseEntity<Map<String, String>> getEmployeeData(@MatrixVariable Map<String, String> matrixVars) {
    return new ResponseEntity<>(matrixVars, HttpStatus.OK);
}
```

当然，我们可以限制绑定到路径特定部分的矩阵变量。例如，如果我们有这样的请求：

```bash
http://localhost:8080/spring-mvc-java-2/
  companyEmployee/id=2;name=Xpto/employeeData/id=1;name=John;
  contactNumber=2200112334
```

而我们只想获取属于employeeData的所有变量；那么我们应该将其用作输入参数：

```java
@RequestMapping(value = "/companyEmployee/{company}/employeeData/{employee}", method = RequestMethod.GET)
@ResponseBody
public ResponseEntity<Map<String, String>> getEmployeeDataFromCompany(@MatrixVariable(pathVar = "employee") Map<String, String> matrixVars) {
    // ...
}
```

## 7. 部分绑定

除了简单之外，灵活性是另一个好处，矩阵变量可以以多种不同的方式使用。例如，我们可以从每个路径段中获取每个变量。考虑以下请求：

```bash
http://localhost:8080/spring-mvc-java-2/
  companyData/id=2;name=Xpto/employeeData/id=1;name=John;
  contactNumber=2200112334
```

如果我们只想知道companyData段的矩阵变量名称，那么，我们应该使用以下作为输入参数：

```java
@MatrixVariable(value="name", pathVar="company") String name
```

## 8. 防火墙设置

如果应用程序使用Spring Security，则默认使用StrictHttpFirewall，这会阻止看似恶意的请求，包括带有分号分隔符的矩阵变量。

我们可以 在应用程序配置中[自定义](https://www.baeldung.com/spring-security-request-rejected-exception#2-stricthttpfirewall)此实现并允许此类变量，同时拒绝其他可能的恶意请求。

但是，通过这种方式，我们可以打开应用程序进行攻击。因此，我们应该在仔细分析应用程序和安全要求后才实施它。

## 9. 总结

本文说明了矩阵变量的一些使用方法。

了解这个新工具如何处理过于复杂的请求或帮助我们添加更多参数来界定我们的搜索非常重要。