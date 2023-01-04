## 1. 概述

在本快速教程中，我们将展示如何在 Thymeleaf中绑定List对象。

要了解如何将Thymeleaf与Spring集成，你可以[在此处](https://www.baeldung.com/thymeleaf-in-spring-mvc)查看我们的主要Spring文章，你还可以在其中学习如何显示字段、接受输入、显示验证错误或转换数据以供显示。

## 2. Thymeleaf示例中的列表

我们将首先展示 如何在Thymeleaf页面中显示列表的元素，以及如何将对象列表绑定为Thymeleaf表单中的用户输入。

为此，我们将使用如下代码所示的简单模型：

```java
public class Book {
    private long id;

    private String title;

    private String author;
	
    // getters and setters
}
```

除了在我们的示例中显示现有书籍外，我们还将使用户能够将多本书添加到集合中并同时编辑所有现有书籍。

## 3. 显示列表元素

让我们看一下以下返回allBooks页面的Controller方法：

```java
@GetMapping("/all")
public String showAll(Model model) {
    model.addAttribute("books", bookService.findAll());
    return "books/allBooks";
}
```

在这里，我们添加了Book对象列表作为发送到视图的模型属性，我们将在视图中使用HTML表格显示它：

```html
<table>
    <thead>
        <tr>
            <th> Title </th>
            <th> Author </th>
        </tr>
    </thead>
    <tbody>
	<tr th:if="${books.empty}">
            <td colspan="2"> No Books Available </td>
        </tr>
        <tr th:each="book : ${books}">
            <td><span th:text="${book.title}"> Title </span></td>
            <td><span th:text="${book.author}"> Author </span></td>
        </tr>
    </tbody>
</table>
```

在这里，我们使用th:each属性遍历列表并显示其中每个对象的属性。

## 4. 使用选择表达式绑定列表

要通过表单提交将对象列表从视图发送到控制器，我们不能使用List对象本身。

相反，我们必须添加一个包装器对象来保存提交的列表：

```java
public class BooksCreationDto {
    private List<Book> books;

    // default and parameterized constructor

    public void addBook(Book book) {
        this.books.add(book);
    }
	
    // getter and setter
}
```

现在让我们允许用户在一个表单提交中添加三本书。

首先，我们将准备表单页面，将我们的命令对象作为模型属性传递：

```java
@GetMapping("/create")
public String showCreateForm(Model model) {
    BooksCreationDto booksForm = new BooksCreationDto();

    for (int i = 1; i <= 3; i++) {
        booksForm.addBook(new Book());
    }

    model.addAttribute("form", booksForm);
    return "books/createBooksForm";
}
```

如我们所见，我们通过包装类将3个空Book对象的列表传递给视图。

接下来，我们需要将表单添加到Thymeleaf页面：

```html
<form action="#" th:action="@{/books/save}" th:object="${form}"
      method="post">
    <fieldset>
        <input type="submit" id="submitButton" th:value="Save">
        <input type="reset" id="resetButton" name="reset" th:value="Reset"/>
        <table>
            <thead>
            <tr>
                <th> Title</th>
                <th> Author</th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="book, itemStat : {books}">
                <td><input th:field="{books[__${itemStat.index}__].title}"/></td>
                <td><input th:field="{books[__${itemStat.index}__].author}"/></td>
            </tr>
            </tbody>
        </table>
    </fieldset>
</form>
```

这就是上面的页面的样子：

<img src="../assets/img.png">

让我们仔细看看我们在这里做了什么。首先，我们使用th:object=”${form}”来指定命令对象(我们作为模型属性传递的对象)。

接下来值得注意的是，我们使用以下选择表达式访问了列表：

```html
<tr th:each="book, itemStat : *{books}">
```

最后，我们使用th:field将我们的输入映射为列表元素的属性。

但是，我们还需要使用 itemStat变量来定义我们指的是哪个列表元素，如下所示：

```html
th:field="*{books[__${itemStat.index}__].title}"
```

最后一步实际上是在后端操作提交的数据。我们将在控制器的@PostMapping 方法中使用命令对象作为@ModelAttribute，保存检索到的书籍列表并将所有现有书籍返回给用户：

```java
@PostMapping("/save")
public String saveBooks(@ModelAttribute BooksCreationDto form, Model model) {
    bookService.saveAll(form.getBooks());

    model.addAttribute("books", bookService.findAll());
    return "redirect:/books/all";
}
```

将表单提交到/save端点后，我们将获得包含所有新添加书籍的页面：

<img src="../assets/img_1.png">

## 5. 使用变量表达式绑定列表

对于此示例，我们首先将所有现有书籍加载到命令对象中：

```java
@GetMapping("/edit")
public String showEditForm(Model model) {
    List<Book> books = new ArrayList<>();
    bookService.findAll().iterator().forEachRemaining(books::add);

    model.addAttribute("form", new BooksCreationDto(books));
    return "books/editBooksForm";
}
```

HTML页面类似，最值得注意的区别在于th:each块：

```html
<tr th:each="book, itemStat : ${form.books}">
    <td>
        <input hidden th:name="|books[${itemStat.index}].id|" th:value="${book.getId()}"/>
    </td>
    <td>
        <input th:name="|books[${itemStat.index}].title|" th:value="${book.getTitle()}"/>
    </td>
    <td>
        <input th:name="|books[${itemStat.index}].author|" th:value="${book.getAuthor()}"/>
    </td>
</tr>
```

如<tr th:each=”book, itemStat : ${form.books}”>所示，我们以稍微不同的方式访问列表，这次使用变量表达式。特别相关的是要注意我们为输入元素提供了名称和值以正确提交数据。

我们还必须添加隐藏输入，这将绑定当前书籍的ID，因为我们不想创建新书籍，而是要编辑现有书籍。

## 6. 总结

在本文中，我们说明了如何在Thymeleaf和Spring MVC中使用List对象。我们已经展示了如何显示发送到视图的对象列表，但我们主要关注将用户输入绑定为Thymeleaf表单列表的两种方式。