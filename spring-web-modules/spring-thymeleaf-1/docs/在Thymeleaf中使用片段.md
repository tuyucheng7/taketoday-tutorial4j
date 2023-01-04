## 1. 概述

在本教程中，我们将展示如何使用Thymeleaf Fragments来重用站点的一些公共部分。在建立一个非常简单的Spring MVC项目之后，我们将关注视图。

如果你是Thymeleaf的新手，你可以查看本网站上的其他文章，例如[这篇介绍](https://www.baeldung.com/thymeleaf-in-spring-mvc)，以及[这篇](https://www.baeldung.com/spring-thymeleaf-3)关于3.0版本引擎的文章。

## 2. Maven依赖

我们需要一些依赖项来启用Thymeleaf：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
```

 可以在Maven Central上找到最新版本的[thymeleaf](https://search.maven.org/search?q=a:thymeleaf)和[thymeleaf-spring5](https://search.maven.org/search?q=a:thymeleaf-spring5)。

## 3. Spring项目

### 3.1 Spring MVC配置

要启用Thymeleaf并设置模板后缀，我们需要使用视图解析器和模板解析器配置MVC。

我们还将为一些静态资源设置目录：

```java
@Bean
public ViewResolver htmlViewResolver() {
    ThymeleafViewResolver resolver = new ThymeleafViewResolver();
    resolver.setTemplateEngine(templateEngine(htmlTemplateResolver()));
    resolver.setContentType("text/html");
    resolver.setCharacterEncoding("UTF-8");
    resolver.setViewNames(ArrayUtil.array("*.html"));
    return resolver;
}

private ITemplateResolver htmlTemplateResolver() {
    SpringResourceTemplateResolver resolver
      = new SpringResourceTemplateResolver();
    resolver.setApplicationContext(applicationContext);
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setCacheable(false);
    resolver.setTemplateMode(TemplateMode.HTML);
    return resolver;
}

@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**", "/css/**")
      .addResourceLocations("/WEB-INF/resources/", "/WEB-INF/css/");
}
```

请注意，如果我们使用的是Spring Boot，则可能不需要此配置，除非我们需要应用自己的自定义设置。

### 3.2 控制器

在这种情况下，控制器只是视图的载体。每个视图显示不同的片段使用场景。

最后一个加载一些通过模型传递的数据以显示在视图上：

```java
@Controller
public class FragmentsController {

    @GetMapping("/fragments")
    public String getHome() {
        return "fragments.html";
    }

    @GetMapping("/markup")
    public String markupPage() {
        return "markup.html";
    }

    @GetMapping("/params")
    public String paramsPage() {
        return "params.html";
    }

    @GetMapping("/other")
    public String otherPage(Model model) {
        model.addAttribute("data", StudentUtils.buildStudents());
        return "other.html";
    }
}
```

请注意，由于我们配置解析器的方式，视图名称必须包含“.html”后缀。 当我们引用片段名称时，我们还将指定后缀。

## 4. 观点

### 4.1 简单片段包含

首先，我们将在页面中重用公共部分。

我们可以将这些部分定义为片段，可以在独立的文件中，也可以在公共页面中。在这个项目中，这些可重用的部分被定义在一个名为fragments的文件夹中。

包含片段内容的三种基本方法：

-   insert：在标签内插入内容
-   replace：用定义片段的标签替换当前标签
-   include：这已被弃用，但它可能仍会出现在遗留代码中

下一个示例fragments.html显示了所有三种方式的使用。这个Thymeleaf模板在文档的头部和主体中添加了片段：

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<title>Thymeleaf Fragments: home</title>
<!--/*/ <th:block th:include="fragments/general.html :: headerfiles">
        </th:block> /*/-->
</head>
<body>
    <header th:insert="fragments/general.html :: header"> </header>
    <p>Go to the next page to see fragments in action</p>
    <div th:replace="fragments/general.html :: footer"></div>
</body>
</html>
```

现在，让我们看一下包含一些片段的页面。它被称为general.html，它就像一个完整的页面，其中一些部分定义为准备使用的片段：

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="headerfiles">
    <meta charset="UTF-8"/>
    <link th:href="@{/css/styles.css}" rel="stylesheet">
</head>
<body>
<div th:fragment="header">
    <h1>Thymeleaf Fragments sample</h1>
</div>
<p>Go to the next page to see fragments in action</p>
<aside>
    <div>This is a sidebar</div>
</aside>
<div class="another">This is another sidebar</div>
<footer th:fragment="footer">
    <a th:href="@{/fragments}">Fragments Index</a> |
    <a th:href="@{/markup}">Markup inclussion</a> |
    <a th:href="@{/params}">Fragment params</a> |
    <a th:href="@{/other}">Other</a>
</footer>
</body>
</html>
```

<head\>部分仅包含一个样式表，但我们可以直接或使用Webjars应用其他工具，例如Bootstrap、jQuery或Foundation。

请注意，此模板的所有可重用标签都具有th:fragment属性，但接下来我们将了解如何包含页面的任何其他部分。

渲染和片段包含后，返回内容为：

```html
<!DOCTYPE HTML>
<html>
<head>
    <title>Thymeleaf Fragments: home</title>
    <meta charset="UTF-8"/>
    <link href="/spring-thymeleaf/css/styles.css" rel="stylesheet">
</head>
<body>
<header>
    <div>
        <h1>Thymeleaf Fragments sample</h1>
    </div>
</header>
<p>Go to the next page to see fragments in action</p>
<footer>
    <a href="/spring-thymeleaf/fragments">Fragments Index</a> |
    <a href="/spring-thymeleaf/markup">Markup inclussion</a> |
    <a href="/spring-thymeleaf/params">Fragment params</a> |
    <a href="/spring-thymeleaf/other">Other</a>
</footer>
</body>
</html>
```

### 4.2 片段的标签选择器

Thymeleaf Fragments的一大优点是我们还可以仅使用简单的选择器、类、ID或简单地通过标签来获取模板的任何部分。

例如，此页面包含来自general.html文件的一些组件：aside块和div.another块：

```html
<body>
    <header th:insert="fragments/general.html :: header"> </header>
    <div th:replace="fragments/general.html :: aside"></div>
    <div th:replace="fragments/general.html :: div.another"></div>
    <div th:replace="fragments/general.html :: footer"></div>
</body>
```

### 4.3 参数化片段

我们可以将参数传递给 片段以更改它的某些特定部分。为此，片段必须定义为函数调用，我们必须在其中声明参数列表。

在此示例中，我们为通用表单字段定义了一个片段：

```html
<div th:fragment="formField (field, value, size)">
    <div>
        <label th:for="${#strings.toLowerCase(field)}"> <span
                th:text="${field}">Field</span>
        </label>
    </div>
    <div>
        <input type="text" th:id="${#strings.toLowerCase(field)}"
               th:name="${#strings.toLowerCase(field)}" th:value="${value}"
               th:size="${size}">
    </div>
</div>
```

这是我们向其传递参数的片段的简单用法：

```html
<body>
<header th:insert="fragments/general.html :: header"></header>
<div th:replace="fragments/forms.html
      :: formField(field='Name', value='John Doe',size='40')">
</div>
<div th:replace="fragments/general.html :: footer"></div>
</body>
```

这就是返回字段的外观：

```html
<div>
    <div>
        <label for="name"> <span>Name</span>
        </label>
    </div>
    <div>
        <input type="text" id="name"
               name="name" value="John Doe"
               size="40">
    </div>
</div>
```

### 4.4 片段包含表达式

Thymeleaf片段提供了其他有趣的选项，例如支持条件表达式以确定是否包含片段。

将Elvis运算符与Thymeleaf提供的任何表达式(例如安全性、字符串和集合)结合使用，我们能够加载不同的片段。

例如，我们可以使用我们将根据给定条件显示的一些内容来定义此片段。这可能是一个包含不同类型块的文件：

```html
<div th:fragment="dataPresent">Data received</div>
<div th:fragment="noData">No data</div>
```

这就是我们如何用表达式加载它们：

```html
<div
    th:replace="${#lists.size(data) > 0} ? 
        ~{fragments/menus.html :: dataPresent} : 
        ~{fragments/menus.html :: noData}">
</div>
```

要了解有关Thymeleaf表达式的更多信息，请在[此处](https://www.baeldung.com/spring-thymeleaf-3-expressions)查看我们的文章。

### 4.5 灵活的布局

下一个示例还展示了片段的另外两个有趣的用途，用于呈现包含数据的表格。这是可重用的表格片段，包含两个重要部分：可以更改的表格标题和呈现数据的主体：

```html
<table>
    <thead th:fragment="fields(theadFields)">
    <tr th:replace="${theadFields}">
    </tr>
    </thead>
    <tbody th:fragment="tableBody(tableData)">
    <tr th:each="row: ${tableData}">
        <td th:text="${row.id}">0</td>
        <td th:text="${row.name}">Name</td>
    </tr>
    </tbody>
    <tfoot>
    </tfoot>
</table>
```

当我们要使用这个表时，我们可以使用fields函数传递我们自己的表头。标头由类myFields引用。通过将数据作为参数传递给tableBody函数来加载表体：

```html
<body>
<header th:replace="fragments/general.html :: header"></header>
<table>
    <thead th:replace="fragments/tables.html
              :: fields(~{ :: .myFields})">
    <tr class="myFields">

        <th>Id</th>
        <th>Name</th>
    </tr>
    </thead>
    <div th:replace="fragments/tables.html
          :: tableBody(tableData=${data})">
    </div>
</table>
<div th:replace="fragments/general.html :: footer"></div>
</body>
```

这就是最终页面的外观：

```html
<body>
<div>
    <h1>Thymeleaf Fragments sample</h1>
</div>
<div>Data received</div>
<table>
    <thead>
    <tr class="myFields">

        <th>Id</th>
        <th>Name</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>1001</td>
        <td>John Smith</td>
    </tr>
    <tr>
        <td>1002</td>
        <td>Jane Williams</td>
    </tr>
    </tbody>
</table>
<footer>
    <a href="/spring-thymeleaf/fragments">Fragments Index</a> |
    <a href="/spring-thymeleaf/markup">Markup inclussion</a> |
    <a href="/spring-thymeleaf/params">Fragment params</a> |
    <a href="/spring-thymeleaf/other">Other</a>
</footer>
</body>
```

## 5. 总结

在本文中，我们展示了如何通过使用Thymeleaf Fragments重用视图组件，Thymeleaf Fragments是一种可以简化模板管理的强大工具。

我们还介绍了一些超出基础知识的其他有趣功能。在选择Thymeleaf作为我们的视图渲染引擎时，我们应该考虑这些因素。

如果你想了解Thymeleaf的其他功能，你一定要看看我们关于[布局方言](https://www.baeldung.com/thymeleaf-spring-layouts)的文章。