## 1. 简介

在本快速教程中，我们将学习如何在[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)模板中使用CSS和JavaScript。

首先，我们将检查预期的文件夹结构，以便我们知道将文件放在哪里。之后，我们将看到我们需要做什么才能从Thymeleaf模板访问这些文件。

我们将从向页面添加CSS样式开始，然后继续添加一些JavaScript函数。

## 2. 设置

为了在我们的应用程序中使用Thymeleaf，让我们将Thymeleaf的[Spring Boot Starter](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)添加到我们的Maven配置中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

## 3. 基本示例

### 3.1 目录结构

现在提醒一下，Thymeleaf是一个模板库，可以轻松地与Spring Boot应用程序集成。默认情况下，Thymeleaf希望我们将这些模板放在src/main/resources/templates文件夹中。我们可以创建子文件夹，因此我们将在此示例中使用名为cssandjs的子文件夹。

对于CSS和JavaScript文件，默认目录是src/main/resources/static。让我们分别为我们的CSS和JS文件创建static/styles/cssandjs和static/js/cssandjs文件夹。

### 3.2 添加CSS

让我们在static/styles/cssandjs文件夹中创建一个名为main.css的简单CSS文件，并定义一些基本样式：

```css
h2 {
    font-family: sans-serif;
    font-size: 1.5em;
    text-transform: uppercase;
}

strong {
    font-weight: 700;
    background-color: yellow;
}

p {
    font-family: sans-serif;
}
```

接下来，让我们在templates/cssandjs文件夹中创建一个名为styledPage.html的Thymeleaf模板来使用这些样式：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>AddCSSand JS to Thymeleaf</title>
    <link th:href="@{/styles/cssandjs/main.css}" rel="stylesheet" />
</head>
<body>
    <h2>Carefully Styled Heading</h2>
    <p>
        This is text on which we want to apply <strong>very special</strong> styling.
    </p>
</body>
</html>
```

我们使用带有Thymeleaf的特殊th:href属性的链接标签加载样式表。如果我们使用了预期的目录结构，我们只需要在src/main/resources/static下指定路径。在这种情况下，这是/styles/cssandjs/main.css。@{/styles/cssandjs/main.css}语法是Thymeleaf进行URL链接的方式。

如果我们运行我们的应用程序，我们将看到我们的样式已被应用：

<img src="../assets/img.png">

### 3.3 使用JavaScript

接下来，我们将学习如何将JavaScript文件添加到我们的Thymeleaf页面。

让我们首先向src/main/resources/static/js/cssandjs/actions.js中的文件添加一些JavaScript：

```javascript
function showAlert() {
    alert("The button was clicked!");
}
```

然后我们跳回到我们的Thymeleaf模板并添加一个指向我们的JavaScript文件的<script\>标签：

```html
<script type="text/javascript" th:src="@{/js/cssandjs/actions.js}"></script>
```

现在，我们从我们的模板中调用我们的方法：

```html
<button type="button" th:onclick="showAlert()">Show Alert</button>
```

当我们运行我们的应用程序并单击Show Alert按钮时，我们将看到警报窗口。

在我们总结之前，让我们通过学习如何在我们的JavaScript中使用来自Spring控制器的数据来稍微构建这个示例。

让我们从修改我们的控制器开始，为我们的页面提供一个名称：

```java
@GetMapping("/styled-page")
public String getStyledPage(Model model) {
    model.addAttribute("name", "Baeldung Reader");
    return "cssandjs/styledPage";
}
```

接下来，让我们在actions.js文件中添加一个函数以在警报中使用此名称：

```javascript
function showName(name) {
    alert("Here's the name: " + name);
}
```

最后，为了使用控制器中的数据调用我们的函数，我们需要使用[脚本内联](https://www.baeldung.com/spring-mvc-model-objects-js)。因此，让我们将名称值放在本地JavaScript变量中：

```html
<script th:inline="javascript">
    var nameJs = /*[[${name}]]*/;
</script>
```

通过这样做，我们创建了一个局部JavaScript变量，其中包含我们控件中的名称模型值，然后我们可以在页面其余部分的JavaScript中使用它。

现在我们已经完成了，我们可以使用nameJs变量调用我们的JavaScript函数：

```html
<button type="button" th:onclick="showName(nameJs);">Show Name</button>
```

## 4. 总结

在这个简短的教程中，我们学习了如何将CSS样式和外部JavaScript功能应用于我们的Thymeleaf页面。我们从推荐的目录结构开始，逐步使用Spring控制器类中提供的数据调用JavaScript。