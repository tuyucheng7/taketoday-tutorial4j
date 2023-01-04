## 1. 概述

在本文中，我们将讨论Spring表单和到控制器的数据绑定。此外，我们还将了解Spring MVC中的主要注解之一，即@ModelAttribute。

当然，Spring MVC是一个复杂的主题，你需要了解很多东西才能充分发挥它的潜力，因此一定要在[这里](https://www.baeldung.com/category/spring-mvc/)深入了解该框架。

## 2. 模型

首先，让我们定义一个简单的实体，我们将要显示并绑定到表单：

```java
public class Employee {
    private String name;
    private long id;
    private String contactNumber;

    // standard getters and setters
}
```

这将是我们的表单支持对象。

## 3. 视图

接下来，让我们定义实际的表单，当然还有包含它的HTML文件。我们将使用创建/注册新员工的页面：

```html
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
</head>
<body>
<h3>Welcome, Enter The Employee Details</h3>
<form:form method="POST"
           action="/spring-mvc-xml/addEmployee" modelAttribute="employee">
    <table>
        <tr>
            <td>
                <form:label path="name">Name</form:label>
            </td>
            <td>
                <form:input path="name"/>
            </td>
        </tr>
        <tr>
            <td>
                <form:label path="id">Id</form:label>
            </td>
            <td>
                <form:input path="id"/>
            </td>
        </tr>
        <tr>
            <td>
                <form:label path="contactNumber">
                    Contact Number
                </form:label>
            </td>
            <td>
                <form:input path="contactNumber"/>
            </td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit"/></td>
        </tr>
    </table>
</form:form>
</body>
</html>
```

首先——注意我们在JSP页面中包括了一个标签库——表单标签库——以帮助定义我们的表单。

接下来，\<form:form\>标签在这里扮演着重要的角色；它与常规的HTML <form\>标签非常相似，但modelAttribute属性是关键，它指定支持此表单的模型对象的名称：

```html
<form:form method="POST" 
  action="/SpringMVCFormExample/addEmployee" modelAttribute="employee">
```

这将对应于稍后在控制器中的@ModelAttribute。

接下来，每个输入字段都使用了另一个来自Spring Form标签库的有用标签form:prefix。这些字段中的每一个都指定了一个路径属性，这必须对应于模型属性(在本例中为Employee类)的getter/setter。加载页面时，输入字段由Spring填充，它调用绑定到输入字段的每个字段的getter。提交表单时，将调用setter将表单的值保存到对象中。

最后——提交表单时，控制器中的POST处理程序被调用，表单自动绑定到我们传入的员工参数。

<img src="../assets/img.png">

## 4. 控制器

现在，让我们看看将要处理后端的控制器：

```java
@Controller
public class EmployeeController {

    @RequestMapping(value = "/employee", method = RequestMethod.GET)
    public ModelAndView showForm() {
        return new ModelAndView("employeeHome", "employee", new Employee());
    }

    @RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
    public String submit(@Valid @ModelAttribute("employee")Employee employee,
                         BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "error";
        }
        model.addAttribute("name", employee.getName());
        model.addAttribute("contactNumber", employee.getContactNumber());
        model.addAttribute("id", employee.getId());
        return "employeeView";
    }
}
```

控制器定义了两个简单的操作——用于在表单中显示数据的GET和用于通过表单提交创建操作的POST。

另请注意，如果名为“employee”的对象未添加到模型中，当我们尝试访问JSP时Spring会报错，因为JSP将设置为将表单绑定到“employee”模型属性：

```bash
java.lang.IllegalStateException: 
  Neither BindingResult nor plain target object 
    for bean name 'employee' available as request attribute
  at o.s.w.s.s.BindStatus.<init>(BindStatus.java:141)
```

要访问我们的表单支持对象，我们需要通过@ModelAttribute注解注入它。

方法参数上的一个`<em>@ModelAttribute </em>`表示将从模型中检索参数。如果模型中不存在，参数将首先被实例化，然后添加到模型中。

## 5. 处理绑定错误

默认情况下，当请求绑定过程中发生错误时，Spring MVC会抛出异常。这通常不是我们想要的，相反，我们应该将这些错误呈现给用户。我们将通过将一个作为参数添加到我们的控制器方法来使用BindingResult：

```java
public String submit(
  @Valid @ModelAttribute("employee") Employee employee,
  BindingResult result,
  ModelMap model)
```

BindingResult参数需要紧跟在我们的表单支持对象之后——这是方法参数顺序很重要的罕见情况之一。否则，我们将遇到以下异常：

```bash
java.lang.IllegalStateException: 
  Errors/BindingResult argument declared without preceding model attribute. 
    Check your handler method signature!
```

现在不再抛出异常；相反，错误将在传递给提交方法的BindingResult上注册。此时，我们可以通过多种方式处理这些错误，例如，可以取消操作：

```java
@RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
public String submit(@Valid @ModelAttribute("employee")Employee employee, BindingResult result,  ModelMap model) {
    if (result.hasErrors()) {
        return "error";
    }
    
    //Do Something
    return "employeeView";
}
```

请注意，如果结果包含错误，我们将如何向用户返回另一个视图以正确显示这些错误，让我们看一下那个视图，error.jsp：

```html
<html>
    <head>
    </head>

    <body>
        <h3>Please enter the correct details</h3>
        <table>
            <tr>
                <td><a href="employee">Retry</a></td>
            </tr>
        </table>
    </body>
</html>
```

## 6. 显示员工

最后，除了创建一个新员工之外，我们还可以简单地显示一个，这是快速查看代码：

```html
<body>
    <h2>Submitted Employee Information</h2>
    <table>
        <tr>
            <td>Name :</td>
            <td>${name}</td>
        </tr>
        <tr>
            <td>ID :</td>
            <td>${id}</td>
        </tr>
        <tr>
            <td>Contact Number :</td>
            <td>${contactNumber}</td>
        </tr>
    </table>
</body>
```

JSP页面只是使用EL表达式来显示模型中Employee对象的属性值。

## 7. 测试应用

可以部署简单的应用程序(例如在Tomcat服务器中)，并在本地访问：

http://localhost:8080/spring-mvc-xml/employee

这是包含主表单的视图，在提交操作之前：

<img src="../assets/img_1.png">

Spring MVC表单示例，提交

提交后显示数据：

<img src="../assets/img_2.png">

Spring MVC表单示例，视图

就是这样，一个使用Spring MVC的简单表单的工作示例，带有验证。

这个Spring MVC教程的实现可以在[GitHub项目](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-mvc-forms-jsp)中找到，这是一个基于Maven的项目，所以应该很容易导入和运行。

最后，正如我在文章开头所说的那样，你绝对应该[更深入地研究Spring MVC](https://www.baeldung.com/category/spring-mvc/)。