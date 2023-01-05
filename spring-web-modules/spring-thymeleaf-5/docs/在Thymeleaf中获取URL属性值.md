## 1. 概述

在这个简短的教程中，我们将阐明如何在Thymeleaf视图中获取URL属性。

## 2、如何获取URL参数属性

访问URL属性，或者我们所说的请求参数，可以使用两个特殊的Thymeleaf对象之一在Thymeleaf视图中轻松完成。第一种方式是使用param对象，第二种方式是使用#request对象。

出于演示目的，让我们考虑一个包含一个参数query的URL：

```bash
https://tuyucheng.com/search?query=Tuyucheng
```

### 2.1 使用param对象

首先，让我们看看如何使用param对象访问URL属性“query”：

```html
<div th:if="${param.query != null}">
    <p th:text="${param.query }"></p>
</div>
```

在上面的示例中，如果参数“query”不为空，则将显示“query”的值。另外，我们应该注意URL属性可以是多值的。让我们看一个具有多值属性的示例URL：

```bash
https://tuyucheng.com/search?query=Tuyucheng&query=Thymleaf
```

在这种情况下，我们可以使用方括号语法分别访问这些值：

```html
<div th:if="${param.query != null}">
    <p th:text="${param.query[0] + ' ' + param.query[1]}" th:unless="${param.query == null}"></p>
</div>
```

### 2.2 使用#request对象

接下来，让我们看看访问URL属性的第二种方式。我们可以使用特殊的#request对象，它使你可以直接访问javax.servlet.http.HttpServletRequest对象，该对象将请求分解为已解析的元素，例如查询属性和标头。

让我们看看如何在Thymeleaf视图中使用#request对象：

```html
<div th:if="${#request.getParameter('query') != null}">
    <p th:text="${#request.getParameter('query')}" th:unless="${#request.getParameter('query') == null}"></p>
</div>
```

在上面的示例中，我们使用了#request对象提供的特殊函数getParameter('query')。此方法将请求参数的值作为String返回，如果参数不存在则返回null 。

## 3. 总结

在这篇快速文章中，我们解释了如何使用param和#request对象在Thymeleaf视图中获取URL属性。