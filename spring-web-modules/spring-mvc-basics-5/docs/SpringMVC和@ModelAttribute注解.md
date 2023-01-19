## 1. 概述

最重要的[Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html)注解之一是[@ModelAttribute](https://docs.spring.io/spring/docs/3.1.x/javadoc-api/org/springframework/web/bind/annotation/ModelAttribute.html)注解。

@ModelAttribute是一个注解，将方法参数或方法返回值绑定到命名的模型属性，然后将其暴露给 Web 视图。

在本教程中，我们将通过一个通用概念(公司员工提交的表单)演示此注解的可用性和功能。

## 延伸阅读：

## [Spring MVC 中的模型、ModelMap 和 ModelAndView](https://www.baeldung.com/spring-mvc-model-model-map-model-view)

了解Spring MVC 提供的接口Model 、ModelMap和ModelAndView 。

[阅读更多](https://www.baeldung.com/spring-mvc-model-model-map-model-view)→

## [Spring @RequestParam注解](https://www.baeldung.com/spring-request-param)

Spring的@RequestParam注解详解

[阅读更多](https://www.baeldung.com/spring-request-param)→

## 2.深入@ModelAttribute

正如介绍性段落所揭示的那样，我们可以将@ModelAttribute用作方法参数或在方法级别使用。

### 2.1. 在方法层面

当我们在方法级别使用注解时，它表明该方法的目的是添加一个或多个模型属性。[这些方法支持与@RequestMapping](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html)方法相同的参数类型，但它们不能直接映射到请求。

让我们在这里看一个简单的例子来理解它是如何工作的：

```html
@ModelAttribute
public void addAttributes(Model model) {
    model.addAttribute("msg", "Welcome to the Netherlands!");
}

```

在上面的例子中，我们看到一个方法，它向控制器类中定义的所有模型添加一个名为msg的属性。

当然，我们将在本文后面的部分看到它的实际应用。

通常，Spring MVC 总是先调用该方法，然后再调用任何请求处理程序方法。基本上，在调用带有@RequestMapping注解的控制器方法之前调用@ModelAttribute方法。这是因为必须在控制器方法内部开始任何处理之前创建模型对象。

同样重要的是，我们将相应的类注解为[@ControllerAdvice](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)。因此，我们可以在模型中添加将被标识为全局的值。这实际上意味着对于每个请求，响应中的每个方法都存在一个默认值。

### 2.2. 作为方法参数

当我们使用注解作为方法参数时，它表示从模型中检索参数。当注解不存在时，应该首先实例化它，然后将其添加到模型中。一旦出现在模型中，arguments 字段应该从所有具有匹配名称的请求参数中填充。

在以下代码片段中，我们将使用提交到addEmployee端点的表单中的数据填充员工模型属性。Spring MVC 在调用提交方法之前在幕后执行此操作：

```html
@RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
public String submit(@ModelAttribute("employee") Employee employee) {
    // Code that uses the employee object

    return "employeeView";
}
```

在本文后面，我们将看到一个完整的示例，说明如何使用employee对象来填充employeeView模板。

它将表单数据与 bean 绑定。用@RequestMapping注解的控制器可以有用 @ModelAttribute注解的自定义类参数。

在Spring MVC中，我们将此称为数据绑定，这是一种通用机制，使我们不必单独解析每个表单字段。

## 3.表格范例

在本节中，我们将查看概述部分中概述的示例，这是一个非常基本的表单，提示用户(特别是公司员工)输入一些个人信息(特别是姓名和ID)。提交完成且没有任何错误后，用户希望在另一个屏幕上看到之前提交的数据。

### 3.1. 风景

让我们首先创建一个带有 id 和 name 字段的简单表单：

```html
<form:form method="POST" action="/spring-mvc-basics/addEmployee" 
  modelAttribute="employee">
    <form:label path="name">Name</form:label>
    <form:input path="name" />
    
    <form:label path="id">Id</form:label>
    <form:input path="id" />
    
    <input type="submit" value="Submit" />
</form:form>

```

### 3.2. 控制器

这是控制器类，我们将在其中实现上述视图的逻辑：

```java
@Controller
@ControllerAdvice
public class EmployeeController {

    private Map<Long, Employee> employeeMap = new HashMap<>();

    @RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
    public String submit(
      @ModelAttribute("employee") Employee employee,
      BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "error";
        }
        model.addAttribute("name", employee.getName());
        model.addAttribute("id", employee.getId());

        employeeMap.put(employee.getId(), employee);

        return "employeeView";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("msg", "Welcome to the Netherlands!");
    }
}
```

在submit()方法中，我们有一个绑定到View的Employee对象。我们可以像那样简单地将表单字段映射到对象模型。在该方法中，我们从表单中获取值并将它们设置为[ModelMap](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/ui/ModelMap.html)。

最后，我们返回employeeView，这意味着我们调用相应的 JSP 文件作为视图代表。

此外，还有一个addAttributes()方法。它的目的是在模型中添加将被全局识别的值。也就是说，对每个控制器方法的每个请求都将返回一个默认值作为响应。我们还必须将特定类注解为@ControllerAdvice。

### 3.3. 该模型

如前所述，Model对象非常简单，包含“前端”属性所需的所有内容。现在让我们看一个例子：

```java
@XmlRootElement
public class Employee {

    private long id;
    private String name;

    public Employee(long id, String name) {
        this.id = id;
        this.name = name;
    }

    // standard getters and setters removed
}
```

### 3.4. 包起来

@ControllerAdvice协助控制器，特别是适用于所有@RequestMapping方法的@ModelAttribute方法。当然，我们的addAttributes()方法将是最先运行的，先于其余的@RequestMapping方法。

记住这一点，在运行submit()和addAttributes()之后，我们可以在从Controller类返回的视图中引用它们，方法是在美元化的花括号二重奏中提及它们的给定名称，例如${name}。

### 3.5. 结果视图

现在让我们打印从表单中收到的内容：

```html
<h3>${msg}</h3>
Name : ${name}
ID : ${id}

```

## 4. 总结

在本文中，我们研究了@ModelAttribute注解在方法参数和方法级用例中的使用。