## 1. 概述 

在这个快速教程中，我们将构建一个简单的应用程序来使用Spring和Thymeleaf显示带有分页的项目列表。

有关如何将Thymeleaf与Spring集成的介绍，请在[此处](https://www.baeldung.com/thymeleaf-in-spring-mvc)查看我们的文章。

## 2. Maven依赖

除了通常的Spring依赖项之外，我们还将添加Thymeleaf和Spring Data Commons的依赖项：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-commons</artifactId>
    <version>2.3.2.RELEASE</version>
</dependency>
```

我们可以在Maven中央存储库中找到最新的[thymeleaf-spring5](https://search.maven.org/search?q=a:thymeleaf-spring5)和[spring-data-commons](https://search.maven.org/search?q=a:spring-data-commons)依赖项。

## 3. 模型

我们的示例应用程序将演示书籍列表的分页。

首先，让我们定义一个Book类，它有两个字段和一个全参数构造函数：

```java
public class Book {
    private int id;
    private String name;

    // standard constructor, setters and getters
}
```

## 4. Service

然后我们将创建一个Service，使用Spring Data Commons库为请求的页面生成分页图书列表：

```java
@Service
public class BookService {

    final private List<Book> books = BookUtils.buildBooks();

    public Page<Book> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Book> list;

        if (books.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, books.size());
            list = books.subList(startItem, toIndex);
        }

        Page<Book> bookPage = new PageImpl<Book>(list, PageRequest.of(currentPage, pageSize), books.size());

        return bookPage;
    }
}
```

在上面的服务中，我们创建了一个方法来根据请求的页面返回选定的页面，该页面由Pageable接口表示。PageImpl类有助于过滤掉分页的书籍列表。

## 5. Spring控制器

我们需要一个Spring控制器来在给定页面大小和当前页码时检索所选页面的图书列表。

要使用所选页面和页面大小的默认值，我们可以简单地访问/listBooks中的资源，无需任何参数。

如果需要任何页面大小或特定页面，我们可以添加参数page和size。

例如，/listBooks?page=2&size=6将检索第二页，每页有六个项目：

```java
@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @RequestMapping(value = "/listBooks", method = RequestMethod.GET)
    public String listBooks(Model model, 
                            @RequestParam("page") Optional<Integer> page, 
                            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Book> bookPage = bookService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("bookPage", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                  .boxed()
                  .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "listBooks.html";
    }
}
```

为了为视图准备分页，我们在Spring控制器中添加了模型属性，包括选定的页面和页码列表。

## 6. Thymeleaf模板

现在是时候创建一个Thymeleaf模板“listBooks.html”，它根据我们的Spring控制器的模型属性显示带有分页的书籍列表。

首先，我们迭代书籍列表并将它们显示在表格中。然后当总页数大于零时显示分页。

每次我们点击并选择一个页面，就会显示相应的图书列表，并突出显示当前页面链接：

```html
<table border="1">
    <thead>
    <tr>
        <th th:text="#{msg.id}" />
        <th th:text="#{msg.name}" />
    </tr>
    </thead>
    <tbody>
    <tr th:each="book, iStat : ${bookPage.content}"
        th:style="${iStat.odd}? 'font-weight: bold;'"
        th:alt-title="${iStat.even}? 'even' : 'odd'">
        <td th:text="${book.id}" />
        <td th:text="${book.name}" />
    </tr>
    </tbody>
</table>
<div th:if="${bookPage.totalPages > 0}" class="pagination"
     th:each="pageNumber : ${pageNumbers}">
    <a th:href="@{/listBooks(size=${bookPage.size}, page=${pageNumber})}"
       th:text=${pageNumber}
       th:class="${pageNumber==bookPage.number + 1} ? active"></a>
</div>
```

## 7. 总结

在本文中，我们演示了如何使用Thymeleaf和Spring框架对列表进行分页。