## 1. 简介

在这个简短的教程中，我们将学习如何使用[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)使用Spring路径变量创建URL。

当我们想要将值作为URL的一部分传递时，我们使用路径变量。在Spring控制器中，我们使用[@PathVariable](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/PathVariable.html)注解访问这些值。

## 2. 使用路径变量

首先，让我们通过创建一个简单的Item类来设置我们的示例：

```java
public class Item {
    private int id;
    private String name;

    // Constructor and standard getters and setters
}
```

现在，让我们来创建我们的控制器：

```java
@Controller
public class PathVariablesController {

    @GetMapping("/pathvars")
    public String start(Model model) {
        List<Item> items = new ArrayList<Item>();
        items.add(new Item(1, "First Item"));
        items.add(new Item(2, "Second Item"));
        model.addAttribute("items", items);
        return "pathvariables/index";
    }

    @GetMapping("/pathvars/single/{id}")
    public String singlePathVariable(@PathVariable("id") int id, Model model) {
        if (id == 1) {
            model.addAttribute("item", new Item(1, "First Item"));
        } else {
            model.addAttribute("item", new Item(2, "Second Item"));
        }

        return "pathvariables/view";
    }
}
```

在我们的index.html模板中，让我们遍历我们的项目并创建调用singlePathVariable方法的链接：

```html
<div th:each="item : ${items}">
    <a th:href="@{/pathvars/single/{id}(id = ${item.id})}">
        <span th:text="${item.name}"></span>
    </a>
</div>
```

我们刚刚创建的代码生成如下URL：

```bash
http://localhost:8080/pathvars/single/1
```

这是在URL中使用表达式的标准Thymeleaf语法。

我们也可以使用连接来达到相同的结果：

```html
<div th:each="item : ${items}">
    <a th:href="@{'/pathvars/single/' + ${item.id}}">
        <span th:text="${item.name}"></span>
    </a>
</div>
```

## 3. 使用多个路径变量

现在我们已经介绍了在Thymeleaf中创建路径变量URL的基础知识，让我们快速介绍一下使用多个路径变量。

首先，我们将创建一个Detail类并修改我们的Item类以获得它们的列表：

```java
public class Detail {
    private int id;
    private String description;

    // constructor and standard getters and setters
}
```

接下来，让我们向Item添加一个Detail列表：

```java
private List<Detail> details;
```

现在，让我们更新我们的控制器以添加一个使用多个@PathVariable注解的方法：

```java
@GetMapping("/pathvars/item/{itemId}/detail/{dtlId}")
public String multiplePathVariable(@PathVariable("itemId") int itemId, @PathVariable("dtlId") int dtlId, Model model) {
    for (Item item : items) {
        if (item.getId() == itemId) {
            model.addAttribute("item", item);
            for (Detail detail : item.getDetails()) {
                if (detail.getId() == dtlId) {
                    model.addAttribute("detail", detail);
                }
            }
        }
    }
    return "pathvariables/view";
}
```

最后，让我们修改我们的index.html模板来为每个详细记录创建URL：

```html
<ul>
    <li th:each="detail : ${item.details}">
        <a th:href="@{/pathvars/item/{itemId}/detail/{dtlId}(itemId = ${item.id}, dtlId = ${dtl.id})}">
            <span th:text="${detail.description}"></span>
        </a>
    </li>
</ul>
```

## 4. 总结

在本快速教程中，我们学习了如何使用Thymeleaf创建带有路径变量的URL。我们首先创建一个只有一个的简单URL。后来，我们扩展了示例以使用多个路径变量。