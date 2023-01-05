## 1. 概述

[Thymeleaf](https://www.thymeleaf.org/)是Java生态系统中流行的模板引擎，它有助于将数据从控制器层绑定到视图层。Thymeleaf属性使用[表达式](https://www.baeldung.com/spring-thymeleaf-3-expressions)设置。在本教程中，我们将通过示例讨论表达式类型。

## 2. 示例设置

我们将使用一个简单的Web应用程序Dino作为示例，这是一个用于创建恐龙档案的简单网络应用程序。

首先，让我们为我们的恐龙创建一个模型类：

```java
public class Dino {
    private int id;
    private String name;
    // constructors   
    // getter and setter
}
```

接下来，让我们创建一个控制器类：

```java
@Controller
public class DinoController {

    @RequestMapping("/")
    public String dinoList(Model model) {
        Dino dinos = new Dino(1, "alpha", "red", 50);
        model.addAttribute("dinos", dinos);
        return "index";
    }
}
```

通过我们的示例设置，我们将能够将Dino实例注入到我们的模板文件中。

## 3. 变量表达式

变量表达式有助于将控制器中的数据注入到模板文件中，它将模型属性暴露给Web视图。

变量表达式语法是美元符号和花括号的组合，我们的变量名位于花括号内：

```java
${...}
```

让我们将Dino数据注入模板文件：

```html
<span th:text="${dinos.id}"></span> 
<span th:text="${dinos.name}"></span>
```

[条件](https://www.baeldung.com/spring-thymeleaf-conditionals)和[迭代](https://www.baeldung.com/thymeleaf-iteration)也可以使用变量表达式：

```html
<!-- for iterating -->
<div th:each="dinos : ${dinos}">

<!-- in conditionals -->
<div th:if="${dinos.id == 2}">
```

## 4. 选择表达式

选择表达式对先前选择的对象进行操作，它帮助我们选择所选对象的子对象。

选择表达式语法是星号和花括号的组合，我们的子对象位于花括号内：

```java
*{...}
```

让我们选择我们的Dino实例的ID和名称并将其注入到我们的模板文件中：

```html
<div th:object="${dinos}">
    <p th:text="*{id}">
    <p th:text="*{name}">
</div>
```

此外，选择表达式主要用在HTML的表单中。它有助于将表单输入与模型属性绑定。

与变量表达式不同，不需要单独处理每个输入元素。以我们的Dino网络应用程序为例，让我们创建一个新的Dino实例并将其绑定到我们的模型属性：

```html
<form action="#" th:action="@{/dino}" th:object="${dinos}" method="post">
    <p>Id: <input type="text" th:field="*{id}"/></p>
    <p>Name: <input type="text" th:field="*{name}"/></p>
</form>
```

## 5. 消息表达式

此表达式有助于将外部化文本带入我们的模板文件中。它也称为文本外化。

我们的文本所在的外部源可以是*.properties*文件。此表达式在具有占位符时是动态的。

消息表达式语法是散列和花括号的组合。我们的密钥位于花括号内：

```html
#{...}
```

例如，假设我们想要在Dino网络应用程序的整个页面中显示特定消息。我们可以将消息放在messages.properties文件中：

```properties
welcome.message=welcome to Dino world.
```

要将欢迎消息绑定到我们的视图模板，我们可以通过它的键来引用它：

```html
<h2 th:text="#{welcome.message}"></h2>
```

我们可以通过在外部文件中添加占位符来让消息表达式接受参数：

```properties
dino.color=red is my favourite, mine is {0}
```

在我们的模板文件中，我们将引用消息并向占位符添加一个值：

```html
<h2 th:text="#{dino.color('blue')}"></h2>
```

此外，我们可以通过注入变量表达式作为占位符的值来使占位符动态化：

```html
<h2 th:text="#{dino.color(${dino.color})}"></h2>
```

这种表达也称为国际化。它可以帮助我们调整我们的 Web 应用程序以适应不同的语言。

## 6. 链接表达式

链接表达式在URL构建中是不可或缺的，此表达式绑定到指定的URL。

链接表达式语法是“@”符号和花括号的组合，我们的链接位于花括号内：

```html
@{...}
```

URL可以是绝对的或相对的，当使用带有绝对URL的链接表达式时，它会绑定到以“http(s)”开头的完整URL：

```html
<a th:href="@{http://www.tuyucheng.com}"> Tuyucheng Home</a>
```

另一方面，相对链接绑定到我们的Web服务器的上下文，我们可以轻松地浏览控制器中定义的模板文件：

```java
@RequestMapping("/create")
public String dinoCreate(Model model) {
    model.addAttribute("dinos", new Dino());
    return "form";
}
```

我们可以按照@RequestMapping中指定的方式请求页面：

```html
<a th:href="@{/create}">Submit Another Dino</a>
```

它可以通过路径变量来获取参数，假设我们想提供一个链接来编辑现有实体。我们可以通过它的id来调用我们想要编辑的对象，链接表达式可以接受id作为参数：

```html
<a th:href="/@{'/edit/' + ${dino.id}}">Edit</a>
```

链接表达式可以设置协议相关的URL，相对协议就像一个绝对URL。URL将使用HTTP或HTTPS协议方案，具体取决于服务器的协议：

```html
<a th:href="@{//tuyucheng.com}">Tuyucheng</a>
```

## 7. 片段表达式

片段表达式可以帮助我们在模板文件之间移动标记，该表达式使我们能够生成可移动的标记片段。

片段表达式语法是波浪号和大括号的组合，我们的片段位于花括号内：

```http
~{...}
```

对于我们的Dino网络应用程序，让我们在我们的index.html文件中创建一个页脚，其中包含一个fragment属性：

```html
<div th:fragment="footer">
    <p>Copyright 2022</p>
</div>
```

我们现在可以将页脚注入到其他模板文件中：

```html
<div th:replace="~{index :: footer}"></div>
```

## 8. 总结

在本文中，我们查看了各种Thymeleaf简单表达式和示例。