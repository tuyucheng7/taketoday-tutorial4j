## 1. 概述

在本教程中，我们将了解如何使用Thymeleaf在HTML5标签中定义自定义属性。它是一个模板引擎框架，允许使用纯HTML来显示动态数据。

有关Thymeleaf或其与Spring集成的介绍性文章，请参阅此[链接](https://www.baeldung.com/thymeleaf-in-spring-mvc)。

## 2. HTML5中的自定义属性

有时我们可能需要HTML页面中的额外信息，以便在客户端进行一些处理。例如，我们可能希望在表单输入标签中保存额外的数据，以便我们可以在使用AJAX提交表单时使用它们。

同样，我们可能希望向填写表单的用户显示自定义验证错误消息。

在任何情况下，我们都可以使用HTML5的自定义属性来提供这些附加数据。可以使用data-前缀在HTML标签中定义自定义属性。

现在，让我们看看如何使用Thymeleaf中的默认方言来定义这些属性。

## 3. Thymeleaf中的自定义HTML属性

我们可以使用以下语法在HTML标签中指定自定义属性：

```html
th:data-<attribute_name>=""
```

让我们创建一个简单的表单，允许学生注册课程以查看实际操作：

```html
<form action="#" th:action="@{/registerCourse}" th:object="${course}"
      method="post" onsubmit="return validateForm();">
    <span id="errormesg" style="color: red"></span> <span
        style="font-weight: bold" th:text="${successMessage}"></span>
    <table>
        <tr>
            <td>
                <label th:text="#{msg.courseName}+': '"></label>
            </td>
            <td>
                <select id="course" th:field="*{name}">
                    <option th:value="''" th:text="'Select'"></option>
                    <option th:value="'Mathematics'" th:text="'Mathematics'"></option>
                    <option th:value="'History'" th:text="'History'"></option>
                </select></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
</form>
```

此表单包含一个包含可用课程的下拉列表和一个提交按钮。

比方说，如果用户在没有选择课程的情况下点击提交，我们想向他们显示一条自定义错误消息。

我们可以将错误消息保存为select标签中的自定义属性，并创建一个JavaScript函数来验证其在提交表单时的值。

更新后的select标签看起来像这样：

```html
<select id="course" th:field="*{name}" th:data-validation-message="#{msg.courseName.mandatory}">
```

在这里，我们从资源包中获取错误消息。

现在，当用户在没有选择有效选项的情况下提交表单时，此JavaScript函数将向用户显示一条错误消息：

```javascript
function validateForm() {
    var e = document.getElementById("course");
    var value = e.options[e.selectedIndex].value;
    if (value == '') {
        var error = document.getElementById("errormesg");
        error.textContent = e.getAttribute('data-validation-message');
        return false;
    }
    return true;
}
```

类似地，我们可以通过为每个标签定义th:data-*属性来向HTML标签添加多个自定义属性。

## 4. 总结

在这篇简短的文章中，我们探讨了如何利用Thymeleaf的支持在HTML5模板中添加自定义属性。