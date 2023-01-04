## 1. 概述

在本快速教程中，我们将了解如何在Thymeleaf中使用数组。为了便于设置，我们将使用Spring Boot Initializer来引导我们的应用程序。

可以在[此处](https://www.baeldung.com/thymeleaf-in-spring-mvc)找到Spring MVC和Thymeleaf的基础知识。

## 2. Thymeleaf依赖

在我们的pom.xml文件中，我们需要添加的唯一依赖项是Spring MVC和Thymeleaf：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 3. 控制器

为简单起见，让我们使用一个只有一种方法的控制器来处理GET请求。

这通过将数组传递给模型对象来响应，这将使视图可以访问它：

```java
@Controller
public class ThymeleafArrayController {

    @GetMapping("/arrays")
    public String arrayController(Model model) {
        String[] continents = {
              "Africa", "Antarctica", "Asia", "Australia",
              "Europe", "North America", "Sourth America"
        };

        model.addAttribute("continents", continents);

        return "continents";
    }
}
```

## 4. 视图

在视图页面中，我们将通过我们从上面的控制器传递给它的名称(continents)来访问数组continents。

### 4.1 属性和索引

我们要检查的第一个属性是数组的长度。这是我们检查它的方式：

```html
<p>...<span th:text="${continents.length}"></span>...</p>
```

看看上面的代码片段，它来自视图页面，我们应该注意到关键字th:text的使用。我们用它来打印花括号内变量的值，在本例中为数组的长度。

因此，我们通过索引访问数组continents的每个元素的值，就像我们在普通Java代码中所做的那样：

```xml
<ol>
    <li th:text="${continents[2]}"></li>
    <li th:text="${continents[0]}"></li>
    <li th:text="${continents[4]}"></li>
    <li th:text="${continents[5]}"></li>
    <li th:text="${continents[6]}"></li>
    <li th:text="${continents[3]}"></li>
    <li th:text="${continents[1]}"></li>
</ol>
```

正如我们在上面的代码片段中看到的，每个元素都可以通过其索引访问。我们可以去[这里](https://www.baeldung.com/spring-thymeleaf-3-expressions)了解更多关于Thymeleaf中的表达式。

### 4.2 迭代

同样，我们可以顺序迭代数组的元素。

在Thymeleaf中，我们可以通过以下方式实现这一目标：

```html
<ul th:each="continet : ${continents}">
    <li th:text="${continent}"></li>
</ul>
```

当使用th:each关键字遍历数组的元素时，我们并不仅限于使用列表标签。我们可以使用任何能够在页面上显示文本的HTML标签。例如：

```html
<h4 th:each="continent : ${continents}" th:text="${continent}"></h4>
```

在上面的代码片段中，每个元素都将显示在自己单独的<h4\></h4\>标签上。

### 4.3 工具函数

最后，我们将使用实用类函数来检查数组的其他一些属性。

让我们来看看这个：

```html
<p>The greatest <span th:text="${#arrays.length(continents)}"></span> continents.</p>

<p>Europe is a continent: <span th:text="${#arrays.contains(continents, 'Europe')}"></span>.</p>

<p>Array of continents is empty <span th:text="${#arrays.isEmpty(continents)}"></span>.</p>
```

我们首先查询数组的长度，然后检查Europe是否是数组continents的元素。

最后，我们检查数组continents是否为空。

## 5.总结

在本文中，我们学习了如何在Thymeleaf中使用数组，方法是检查其长度并使用索引访问其元素。我们还学习了如何在Thymeleaf中迭代其元素。

最后，我们看到了使用实用函数来检查数组的其他属性。