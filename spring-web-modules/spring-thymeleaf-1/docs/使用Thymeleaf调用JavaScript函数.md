## 1. 概述

在本教程中，我们将在[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)模板中调用[JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript)函数。

我们将从设置我们的依赖项开始。然后，我们将添加Spring控制器和Thymeleaf模板。最后，我们将展示根据输入调用JavaScript函数的方法。

## 2. 设置

为了在我们的应用程序中使用Thymeleaf，让我们将[Thymeleaf Spring 5](https://search.maven.org/search?q=a:thymeleaf-spring5)依赖项添加到我们的Maven配置中：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
```

然后，让我们根据我们的[Student](https://www.baeldung.com/thymeleaf-in-spring-mvc#2-collection-attributes)模型将其添加到我们的Spring控制器中：

```java
@Controller
public class FunctionCallController {

    @RequestMapping(value = "/function-call", method = RequestMethod.GET)
    public String getExampleHTML(Model model) {
        model.addAttribute("totalStudents", StudentUtils.buildStudents().size());
        model.addAttribute("student", StudentUtils.buildStudents().get(0));
        return "functionCall.html";
    }
}
```

最后，我们将这两个JavaScript函数添加到src/main/webapp/WEB-INF/views下的functionCall.html模板中：

```xml
<script  th:inline="javascript">
    function greetWorld() {
        alert("hello world")
    }

    function salute(name) {
        alert("hello: " + name)
    }
</script>
```

我们将在下一节中使用这两个函数来说明我们的示例。

如果有任何问题，我们可以随时检查[如何将JavaScript添加到Thymeleaf](https://www.baeldung.com/spring-thymeleaf-css-js)。

## 3. 在Thymeleaf中调用JavaScript函数

### 3.1 在没有输入的情况下使用函数

下面是我们如何调用上面的greetWorld函数：

```xml
<button th:onclick="greetWorld()">using no variable</button>
```

它适用于任何自定义或内置的JavaScript函数。

### 3.2 使用带有静态输入的函数

如果我们在JavaScript函数中不需要任何动态变量，可以这样调用它：

```xml
<button th:onclick="'alert(\'static variable used here.\');'">using static variable</button>
```

这会转义单引号并且不需要[SpringEL](https://www.baeldung.com/spring-expression-language)。

### 3.3 使用具有动态输入的函数

有四种方式调用带变量的JavaScript函数。

第一种插入变量的方法是使用内联变量：

```xml
<button th:onclick="'alert(\'There are exactly '  + ${totalStudents} +  ' students\');'">using inline dynamic variable</button>
```

另一种选择是调用javascript:function：

```xml
<button th:onclick="'javascript:alert(\'There are exactly ' + ${totalStudents} + ' students\');'">using javascript:function</button>
```

第三种方法是使用[数据属性](https://www.baeldung.com/thymeleaf-custom-html-attributes)：

```xml
<button th:data-name="${student.name}" th:onclick="salute(this.getAttribute('data-name'))">using data attribute</button>
```

此方法在调用[JavaScript事件](https://developer.mozilla.org/en-US/docs/Web/Events)(如onClick和onLoad)时派上用场。

最后，我们可以使用[双方括号语法调用我们的](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#expression-inlining)salute函数：

```xml
<button th:onclick="salute([[${student.name}]])">using double brackets</button>
```

双方括号之间的表达式被视为Thymeleaf中的内联表达式，这就是为什么我们可以使用在th:text属性中也有效的任何类型的表达式。

## 4. 总结

在本教程中，我们学习了如何在[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)模板中调用JavaScript函数。我们首先设置我们的依赖项。然后，我们构建了我们的控制器和模板。最后，我们探索了在Thymeleaf中调用任何JavaScript函数的方法。