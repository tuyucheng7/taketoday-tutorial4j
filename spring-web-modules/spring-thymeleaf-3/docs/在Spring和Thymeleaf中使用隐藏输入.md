## 1. 简介

[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)是Java生态系统中最流行的模板引擎之一，它使我们能够轻松地使用来自Java应用程序的数据来创建动态HTML页面。

在本教程中，我们将了解通过Spring和Thymeleaf使用隐藏输入的几种方法。

## 2. 带有HTML表单的Thymeleaf

在我们研究如何使用隐藏字段之前，让我们退后一步，看看Thymeleaf通常如何使用HTML表单。

最常见的用例是在我们的应用程序中使用直接映射到DTO的HTML表单。

例如，假设我们正在编写一个博客应用程序并有一个代表单个博客文章的DTO：

```java
class BlogDTO {
    long id;
    String title;
    String body;
    String category;
    String author;
    Date publishedDate;  
}
```

我们可以使用HTML表单使用Thymeleaf和Java创建此DTO的新实例：

```xml
<form action="#" method="post" th:action="@{/blog}" th:object="${blog}">
    <input type="text" th:field="*{title}">
    <input type="text" th:field="*{category}">
    <textarea th:field="*{body}"></textarea>
</form>
```

请注意，我们的博客文章DTO中的字段映射到HTML表单中的单个输入。这在大多数情况下效果很好，但哪些字段不应该是可编辑的？这是隐藏输入可以提供帮助的地方。

例如，每篇博文都有一个唯一的ID字段，不允许用户编辑。使用隐藏输入，我们可以将ID字段传递到HTML表单中，而不允许显示或编辑它。

## 3. 使用th:field属性

为隐藏输入赋值的最快方法是使用th:field属性：

```xml
<input type="hidden" th:field="*{blogId}" id="blogId">
```

这是最简单的方法，因为我们不必指定value属性，但旧版本的Thymeleaf可能不支持它。

## 4. 使用th:attr属性

下一个我们可以使用Thymeleaf隐藏输入的方法是使用内置的th:attr属性：

```xml
<input type="hidden" th:value="${blog.id}" th:attr="name='blogId'"/>
```

在这种情况下，我们必须使用blog对象引用id字段。

## 5. 使用name属性

另一种不那么冗长的方法是使用标准的HTML name属性：

```xml
<input type="hidden" th:value="${blog.id}" name="blogId" />
```

它完全依赖于标准的HTML属性。在这种情况下，我们还必须使用blog对象引用id字段。

## 6. 总结

在本教程中，我们研究了几种在Thymeleaf中使用隐藏输入的方法。这是将只读字段从我们的DTO传递到HTML表单的有用技术。