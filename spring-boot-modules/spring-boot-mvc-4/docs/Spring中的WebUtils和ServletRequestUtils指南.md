## 一、概述

在这篇简短的文章中，我们将探索 Spring MVC 中的内置 Web 请求实用程序 – WebUtils、ServletRequestUtils。

## 2. WebUtils和ServletRequestUtils 

在几乎所有应用程序中，我们都会遇到需要从传入的 HTTP 请求中获取一些参数的情况。

为此，我们必须创建一些非常繁忙的代码段，例如：

```java
HttpSession session = request.getSession(false);
if (session != null) {
    String foo = session.getAttribute("parameter");
}

String name = request.getParameter("parameter");
if (name == null) {
    name = "DEFAULT";
}
```

使用WebUtils和ServletRequestUtils，我们只需一行代码即可完成。

要了解这些实用程序的工作原理，让我们创建一个简单的 Web 应用程序。

## 3.示例页面

我们需要创建示例页面以便能够链接 URL。我们将使用[Spring Boot](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot")和[Thymeleaf](https://search.maven.org/classic/#search|ga|1|a%3A"thymeleaf")作为我们的模板引擎。我们需要为它们添加所需的依赖项。

让我们创建一个具有简单表单的页面：

```html
<form action="setParam" method="POST">
    <h3>Set Parameter:  </h3>
    <p th:text="${parameter}" class="param"/>
    <input type="text" name="param" id="param"/>
    <input type="submit" value="SET"/>
</form>
<br/>
<a href="other">Another Page</a>
```

如我们所见，我们正在创建一个用于发起POST请求的表单。

还有一个链接，它将用户转发到下一页，我们将在其中显示会话属性中提交的参数。

让我们创建第二个页面：

```html
Parameter set by you: <p th:text="${parameter}" class="param"/>
```

## 4.用法

现在我们已经完成了视图的构建，让我们创建我们的控制器并使用ServletRequestUtils并获取请求参数：

```java
@PostMapping("/setParam")
public String post(HttpServletRequest request, Model model) {
    String param 
      = ServletRequestUtils.getStringParameter(
        request, "param", "DEFAULT");

    WebUtils.setSessionAttribute(request, "parameter", param);

    model.addAttribute("parameter", "You set: " + (String) WebUtils
      .getSessionAttribute(request, "parameter"));

    return "utils";
}
```

请注意我们如何使用ServletRequestUtils中的getStringParameter API来获取请求参数名称param；如果没有值进入控制器，则默认值将分配给请求参数。

当然，请注意WebUtils中用于在会话属性中设置值的setSessionAttribute API 。我们不需要显式检查会话是否已经存在，也不需要在 vanilla servlet 中进行链接。Spring 会即时配置它。

以同样的方式，让我们创建另一个显示以下会话属性的处理程序：

```java
@GetMapping("/other")
public String other(HttpServletRequest request, Model model) {
    
    String param = (String) WebUtils.getSessionAttribute(
      request, "parameter");
    
    model.addAttribute("parameter", param);
    
    return "other";
}
```

这就是我们创建应用程序所需的全部。

这里需要注意的一点是ServletRequestUtils有一些很棒的内置功能，可以根据我们的需要自动对请求参数进行类型转换。

以下是我们如何将请求参数转换为Long：

```java
Long param = ServletRequestUtils.getLongParameter(request, "param", 1L);
```

同样，我们可以将请求参数转换为其他类型：

```java
boolean param = ServletRequestUtils.getBooleanParameter(
  request, "param", true);

double param = ServletRequestUtils.getDoubleParameter(
  request, "param", 1000);

float param = ServletRequestUtils.getFloatParameter(
  request, "param", (float) 1.00);

int param = ServletRequestUtils.getIntParameter(
  request, "param", 100);
```

另外需要注意的是，ServletRequestUtils还有一个方法getRequiredStringParameter(ServletRequest request, String name)用于获取请求参数。不同之处在于，如果在传入请求中找不到该参数，它将抛出ServletRequestBindingException。当我们需要处理关键数据时，这可能很有用。

下面是一个示例代码片段：

```java
try {
    ServletRequestUtils.getRequiredStringParameter(request, "param");
} catch (ServletRequestBindingException e) {
    e.printStackTrace();
}
```

我们还可以创建一个简单的 JUnit 测试用例来测试应用程序：

```java
@Test
public void givenParameter_setRequestParam_andSetSessionAttribute() 
  throws Exception {
      String param = "testparam";
 
      this.mockMvc.perform(
        post("/setParam")
          .param("param", param)
          .sessionAttr("parameter", param))
          .andExpect(status().isOk());
  }
```

## 5.总结

在本文中，我们看到使用WebUtils和ServletRequestUtils可以大大减少大量样板代码的开销。但是，另一方面，它肯定会增加对 Spring 框架的依赖性——如果你担心这一点，请记住这一点。