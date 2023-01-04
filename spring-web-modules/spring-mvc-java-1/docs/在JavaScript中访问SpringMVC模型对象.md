## 1. 概述

在本教程中，我们将展示如何在包含JavaScript代码的Thymeleaf视图中访问Spring MVC对象。我们将在示例中使用Spring Boot和Thymeleaf模板引擎，但这个想法也适用于其他模板引擎。

我们将描述两种情况：当JavaScript代码嵌入或在引擎生成的网页内部时，以及当它在页面外部时，例如，在单独的JavaScript文件中。

## 2. 设置

假设我们已经配置了一个使用Thymeleaf模板引擎的Spring Boot Web应用程序。否则，你可能会发现这些教程很有用：

-   [引导一个简单的应用程序](https://www.baeldung.com/spring-boot-start)：关于如何从头开始创建一个Spring Boot应用程序
-   [Spring MVC + Thymeleaf 3.0：新特性](https://www.baeldung.com/spring-thymeleaf-3)：关于如何使用Thymeleaf语法

此外，假设我们的应用程序有一个对应于端点/index的控制器，它从名为index.html的模板呈现视图。该模板可能包含嵌入式或外部JavaScript代码，例如script.js。

我们的目标是能够从嵌入式或外部JavaScript(JS)代码访问Spring MVC参数。

## 3. 访问参数

首先，我们需要从JS代码创建我们想要使用的模型变量。

在Spring MVC中，有多种方法可以做到这一点。让我们使用ModelAndView方法：

```java
@RequestMapping("/index")
public ModelAndView thymeleafView(Map<String, Object> model) {
    model.put("number", 1234);
    model.put("message", "Hello from Spring MVC");
    return new ModelAndView("thymeleaf/index");
}
```

我们可以在有关[Spring MVC中的Model、ModelMap和ModelView](https://www.baeldung.com/spring-mvc-model-model-map-model-view)的教程中找到其他可能性。

## 4. 嵌入JS代码

嵌入式JS代码只不过是位于<script\>元素内的index.html文件的一部分，我们可以非常直接地传递Spring MVC变量：

```javascript
<script>
    var number = [[${number}]];
    var message = "[[${message}]]";
</script>
```

Thymeleaf模板引擎用当前执行范围内可用的值替换每个表达式。这意味着模板引擎将上述代码转换为以下HTML代码：

```javascript
<script>
    var number = 1234;
    var message = "Hello from Spring MVC!";
</script>
```

## 5. 外部JS代码

假设我们的外部JS代码 使用相同的<script\>标签包含在index.html文件中，我们在其中指定了src属性：

```javascript
<script src="/js/script.js"></script>
```

现在，如果我们想使用script.js中的Spring MVC参数，我们应该：

1.  像我们在上一节中所做的那样在嵌入式JS代码中定义JS变量
2.  从script.js文件访问这些变量

注意外部JS代码应该在嵌入JS代码的变量初始化之后调用。

这可以通过两种方式实现：通过指定JS代码执行的顺序或使用异步JS代码执行。

### 5.1 指定执行顺序

我们可以通过在index.html中的嵌入代码之后声明外部JS代码来做到这一点：

```javascript
<script>
    var number = [[${number}]];
    var message = "[[${message}]]";
</script>
<script src="/js/script.js"></script>
```

### 5.2 异步代码执行

在这种情况下，我们在index.html中声明外部和嵌入式JS代码的顺序并不重要，但我们应该将script.js中的代码放入典型的页面加载包装器中：

```javascript
window.onload = function() {
    // JS code
};
```

尽管这段代码很简单，但最常见的做法是改用jQuery，我们将这个库作为第一个<script\>元素包含在index.html文件中：

```html
<!DOCTYPE html>
<html>
    <head>
        <script src="/js/jquery.js"></script>
        ...
    </head>
 ...
</html>
```

现在，我们可以将JS代码放在以下jQuery包装器中：

```javascript
$(function () {
    // JS code
});
```

使用这个包装器，我们可以保证仅在整个页面内容以及所有其他嵌入的JS代码完全加载时才执行JS代码。

## 6. 一点解释

在Spring MVC中，Thymeleaf模板引擎仅解析模板文件(在我们的示例中为index.html)并将其转换为HTML文件。该文件本身可能包含对模板引擎范围之外的其他资源的引用。解析这些资源并呈现HTML视图的是用户的浏览器。

因此，这些资源不会被模板引擎解析，我们可以将控制器中定义的变量注入到嵌入式JS代码中，然后这些代码可用于外部JS代码。

## 7. 总结

在本教程中，我们学习了如何在JavaScript代码中访问Spring MVC参数。