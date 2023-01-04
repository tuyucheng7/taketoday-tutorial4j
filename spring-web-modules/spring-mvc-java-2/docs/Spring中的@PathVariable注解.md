## 1. 概述

在本快速教程中，我们将探索Spring的@PathVariable注解。

简单的说，@PathVariable注解可以用来处理请求URI映射中的模板变量，并将其设置为方法参数。

让我们看看如何使用@PathVariable及其各种属性。

## 2. 一个简单的映射

@PathVariable注解的一个简单用例是一个端点，该端点标识具有主键的实体：

```java
@GetMapping("/api/employees/{id}")
@ResponseBody
public String getEmployeesById(@PathVariable String id) {
    return "ID: " + id;
}
```

在此示例中，我们使用@PathVariable注解来提取URI的模板化部分，由变量{id}表示。

对/api/employees/{id}的简单GET请求将使用提取的id值调用getEmployeesById：

```bash
http://localhost:8080/api/employees/111 
---- 
ID: 111
```

现在让我们进一步探索这个注解，看看它的属性。

## 3. 指定路径变量名

在前面的示例中，我们跳过了定义模板路径变量的名称，因为方法参数和路径变量的名称是相同的。

但是，如果路径变量名称不同，我们可以在@PathVariable注解的参数中指定它：

```java
@GetMapping("/api/employeeswithvariable/{id}")
@ResponseBody
public String getEmployeesByIdWithVariableName(@PathVariable("id") String employeeId) {
    return "ID: " + employeeId;
}
```

```bash
http://localhost:8080/api/employeeswithvariable/1 
---- 
ID: 1
```

为了清楚起见，我们还可以将路径变量名称定义为@PathVariable(value="id")而不是PathVariable("id")。

## 4. 单个请求中的多个路径变量

根据用例，我们可以在控制器方法的请求URI中有多个路径变量，它也有多个方法参数：

```java
@GetMapping("/api/employees/{id}/{name}")
@ResponseBody
public String getEmployeesByIdAndName(@PathVariable String id, @PathVariable String name) {
    return "ID: " + id + ", name: " + name;
}
```

```bash
http://localhost:8080/api/employees/1/bar 
---- 
ID: 1, name: bar
```

我们还可以使用java.util.Map<String, String>类型的方法参数来处理多个@PathVariable参数：

```java
@GetMapping("/api/employeeswithmapvariable/{id}/{name}")
@ResponseBody
public String getEmployeesByIdAndNameWithMapVariable(@PathVariable Map<String, String> pathVarsMap) {
    String id = pathVarsMap.get("id");
    String name = pathVarsMap.get("name");
    if (id != null && name != null) {
        return "ID: " + id + ", name: " + name;
    } else {
        return "Missing Parameters";
    }
}
```

```bash
http://localhost:8080/api/employees/1/bar 
---- 
ID: 1, name: bar
```

但是，当路径变量字符串包含点(.)字符时，在处理多个@PathVariable参数时会遇到一个小问题。我们在[这里](https://www.baeldung.com/spring-mvc-pathvariable-dot)详细讨论了这些极端情况。

## 5. 可选路径变量

在Spring中，默认需要@PathVariable注解的方法参数：

```java
@GetMapping(value = { "/api/employeeswithrequired", "/api/employeeswithrequired/{id}" })
@ResponseBody
public String getEmployeesByIdWithRequired(@PathVariable String id) {
    return "ID: " + id;
}
```

鉴于它的外观，上面的控制器应该处理/api/employeeswithrequired和/api/employeeswithrequired/1请求路径。但是，由于@PathVariables注解的方法参数默认是必填的，所以它不处理发送到/api/employeeswithrequired路径的请求：

```bash
http://localhost:8080/api/employeeswithrequired 
---- 
{"timestamp":"2020-07-08T02:20:07.349+00:00","status":404,"error":"Not Found","message":"","path":"/api/employeeswithrequired"} 

http://localhost:8080/api/employeeswithrequired/1 
---- 
ID: 111
```

我们可以用两种不同的方式处理这个问题。

### 5.1 将@PathVariable设置为不需要

我们可以将@PathVariable的required属性设置为false以使其成为可选的。因此，修改我们之前的示例，我们现在可以处理带有和不带有路径变量的URI版本：

```java
@GetMapping(value = { "/api/employeeswithrequiredfalse", "/api/employeeswithrequiredfalse/{id}" })
@ResponseBody
public String getEmployeesByIdWithRequiredFalse(@PathVariable(required = false) String id) {
    if (id != null) {
        return "ID: " + id;
    } else {
        return "ID missing";
    }
}
```

```bash
http://localhost:8080/api/employeeswithrequiredfalse 
---- 
ID missing
```

### 5.2 使用java.util.Optional

自从引入Spring 4.1以来，我们还可以使用[java.util.Optional](https://www.baeldung.com/java-optional)(在Java 8+中可用)来处理非强制路径变量：

```java
@GetMapping(value = { "/api/employeeswithoptional", "/api/employeeswithoptional/{id}" })
@ResponseBody
public String getEmployeesByIdWithOptional(@PathVariable Optional<String> id) {
    if (id.isPresent()) {
        return "ID: " + id.get();
    } else {
        return "ID missing";
    }
}
```

现在，如果我们不在请求中指定路径变量id，我们会得到默认响应：

```bash
http://localhost:8080/api/employeeswithoptional 
----
ID missing 
```

### 5.3 使用类型为Map<String, String>的方法参数

如前所述，我们可以使用类型为java.util.Map的单个方法参数来处理请求URI中的所有路径变量。我们还可以使用此策略来处理可选路径变量的情况：

```java
@GetMapping(value = { "/api/employeeswithmap/{id}", "/api/employeeswithmap" })
@ResponseBody
public String getEmployeesByIdWithMap(@PathVariable Map<String, String> pathVarsMap) {
    String id = pathVarsMap.get("id");
    if (id != null) {
        return "ID: " + id;
    } else {
        return "ID missing";
    }
}
```

## 6. @PathVariable的默认值

开箱即用，没有为用@PathVariable注解的方法参数定义默认值的规定。但是，我们可以使用上面讨论的相同策略来满足@PathVariable的默认值情况，我们只需要检查路径变量上的空值。

例如，使用java.util.Optional<String, String\>，我们可以确定路径变量是否为空。如果它是null，那么我们可以用默认值响应请求：

```java
@GetMapping(value = { "/api/defaultemployeeswithoptional", "/api/defaultemployeeswithoptional/{id}" })
@ResponseBody
public String getDefaultEmployeesByIdWithOptional(@PathVariable Optional<String> id) {
    if (id.isPresent()) {
        return "ID: " + id.get();
    } else {
        return "ID: Default Employee";
    }
}
```

## 7. 总结

在本文中，我们讨论了如何使用Spring的@PathVariable注解。我们还确定了有效使用@PathVariable注解以适应不同用例的各种方法，例如可选参数和处理默认值。