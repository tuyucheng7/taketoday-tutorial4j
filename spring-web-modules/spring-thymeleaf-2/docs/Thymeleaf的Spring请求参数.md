## 1. 简介

在我们的[在Spring中使用Thymeleaf的介绍](../../spring-thymeleaf-1/docs/在Spring中使用Thymeleaf的介绍.md)一文中，我们看到了如何将用户输入绑定到对象。

我们使用Thymeleaf模板中的th:object和th:field以及控制器中的@ModelAttribute将数据绑定到Java对象。在本文中，我们将了解如何将Spring注解@RequestParam与Thymeleaf结合使用。

## 2. 表单中的参数

让我们首先创建一个接受四个可选请求参数的简单控制器：

```java
@Controller
public class MainController {
    @RequestMapping("/")
    public String index(
          @RequestParam(value = "participant", required = false) String participant,
          @RequestParam(value = "country", required = false) String country,
          @RequestParam(value = "action", required = false) String action,
          @RequestParam(value = "id", required = false) Integer id,
          Model model
    ) {
        model.addAttribute("id", id);
        List<Integer> userIds = asList(1,2,3,4);
        model.addAttribute("userIds", userIds);
        return "index";
    }
}
```

我们的Thymeleaf模板的名称是index.html。在接下来的三个部分中，我们将使用不同的HTML表单元素让用户将数据传递给控制器。

### 2.1 input元素

首先，让我们创建一个简单的表单，其中包含一个文本输入字段和一个用于提交表单的按钮：

```html
<form th:action="@{/}">
<input type="text" th:name="participant"/> 
<input type="submit"/> 
</form>
```

属性th:name="participant"将输入字段的值绑定到控制器的参数participant上。为此，我们需要使用@RequestParam(value = "participant")注解参数。

### 2.2 select元素

同样对于HTML select元素：

```html
<form th:action="@{/}">
    <input type="text" th:name="participant"/>
    <select th:name="country">
        <option value="de">Germany</option>
        <option value="nl">Netherlands</option>
        <option value="pl">Poland</option>
        <option value="lv">Latvia</option>
    </select>
</form>
```

所选选项的值绑定到参数country，用@RequestParam(value = "country")注解。

### 2.3 button元素

我们可以使用th:name的另一个元素是button元素：

```html
<form th:action="@{/}">
    <button type="submit" th:name="action" th:value="in">check-in</button>
    <button type="submit" th:name="action" th:value="out">check-out</button>
</form>
```

根据是按下第一个还是第二个按钮来提交表单，参数action的值将是check-in或check-out。

## 3. 超链接中的参数

另一种将请求参数传递给控制器的方法是通过超链接：

```html
<a th:href="@{/index}">
```

我们可以在括号中添加参数：

```html
<a th:href="@{/index(param1='value1',param2='value2')}">
```

Thymeleaf将以上内容评估为：

```html
<a href="/index?param1=value1¶m2=value2">
```

如果我们想根据变量分配参数值，使用Thymeleaf表达式生成超链接特别有用。例如，让我们为每个用户ID生成一个超链接：

```html
<th:block th:each="userId: ${userIds}">
    <a th:href="@{/(id=${userId})}"> User [[${userId}]]</a> <br/>
</th:block>
```

我们可以将用户ID列表作为属性传递给模板：

```java
List<Integer> userIds = asList(1,2,3);
model.addAttribute("userIds", userIds);
```

生成的HTML将是：

```html
<a th:href="/?id=1"> User 1</a> <br/>
<a th:href="/?id=2"> User 2</a> <br/>
<a th:href="/?id=3"> User 3</a> <br/>
```

超链接中的参数id绑定参数id，注解为@RequestParam(value = "id")。

## 4. 总结

在这篇简短的文章中，我们了解了如何将Spring请求参数与Thymeleaf结合使用。

首先，我们创建了一个接受请求参数的简单控制器。其次，我们研究了如何使用Thymeleaf生成可以调用我们的控制器的HTML页面。