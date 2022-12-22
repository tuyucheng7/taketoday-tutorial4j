## 1. 概述

在构建Web应用程序时，**[JavaServer Pages(JSP)]()是我们可以用作HTML页面模板机制的一种选择**。

另一方面，**[Spring Boot]()是一个流行的框架，我们可以使用它来引导我们的Web应用程序**。

在本教程中，我们介绍如何将JSP与Spring Boot结合使用来构建Web应用程序。

首先，我们了解如何设置我们的应用程序以在不同的部署场景中工作，然后我们将看看JSP的一些常见用法，最后，我们探讨打包应用程序时的各种选项。

这里需要注意的是，**JSP自身有局限性，当与Spring Boot结合使用时更是如此**。因此，我们应该将[Thymeleaf]()或 [FreeMarker]()视为JSP的更好替代品。

## 2. Maven依赖

让我们看看使用JSP支持Spring Boot需要哪些依赖项。

我们还将注意到将我们的应用程序作为独立应用程序运行与在Web容器中运行之间的细微差别。

### 2.1 作为独立应用程序运行

首先，让我们包含[spring-boot-starter-web](https://search.maven.org/classic/#search|ga|1|g%3Aorg.springframework.boot a%3Aspring-boot-starter-web)依赖项。

此依赖项提供了使Web应用程序与Spring Boot一起运行的所有核心要求以及默认的嵌入式Tomcat Servlet容器：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.4.4</version>
</dependency>
```

查看我们的文章[比较Spring Boot中的嵌入式Servlet容器]()，了解有关如何配置Tomcat以外的嵌入式Servlet容器的更多信息。

我们应该特别注意，**Undertow在用作嵌入式Servlet容器时不支持JSP**。

接下来，我们需要包含[tomcat-embed-jasper](https://search.maven.org/classic/#search|ga|1|g%3Aorg.apache.tomcat.embed a%3Atomcat-embed-jasper)依赖项以允许我们的应用程序编译和呈现JSP页面：

```xml
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <version>9.0.44</version>
</dependency>
```

虽然上面两个依赖可以手动提供，但通常最好让Spring Boot来管理这些依赖的版本，而我们只管理Spring Boot的版本。

这种版本管理可以通过使用Spring Boot父级POM来完成，如我们的文章[Spring Boot教程–引导一个简单的应用程序]()所示，或者通过使用依赖项管理来完成，如我们的文章[Spring Boot依赖项管理和自定义父级]()所示。

最后，我们需要包含[jstl](https://search.maven.org/classic/#search|ga|1|g%3Ajavax.servlet a%3Ajstl)库，它提供JSP页面所需的JSTL标签支持：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
```

### 2.2 在Web容器(Tomcat)中运行

在Tomcat Web容器中运行时，我们仍然需要上述依赖项。

但是，**为了避免我们的应用程序提供的依赖项与Tomcat运行时提供的依赖项发生冲突，我们需要设置两个具有provided范围的依赖项**：

```xml
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <version>9.0.44</version>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <version>2.4.4</version>
    <scope>provided</scope>
</dependency>
```

请注意，我们必须显式定义[spring-boot-starter-tomcat](https://search.maven.org/classic/#search|ga|1|g%3Aorg.springframework.boot a%3Aspring-boot-starter-tomcat)并使用provided范围对其进行标记，这是因为它已经是由spring-boot-starter-web提供的传递依赖。

## 3. 视图解析器配置

按照惯例，我们将JSP文件放在${project.basedir}/main/webapp/WEB-INF/jsp/目录中。

我们需要通过在application.properties文件中配置两个属性来让Spring知道在哪里可以找到这些JSP文件：

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

在编译时，Maven将确保生成的WAR文件将上述jsp目录放在WEB-INF目录中，然后由我们的应用程序提供服务。

## 4. 引导我们的应用程序

我们的主应用程序类将受到我们计划作为独立应用程序还是在Web容器中运行的影响。

**当作为独立应用程序运行时，我们的应用程序类将是一个简单的@SpringBootApplication注解类以及main方法**：

```java
@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday.boot.jsp")
public class SpringBootJspApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJspApplication.class);
    }
}
```

但是，**如果我们需要部署在Web容器中，则需要扩展SpringBootServletInitializer**。

**这会将我们应用程序的Servlet、Filter和ServletContextInitializer绑定到运行时服务器**，这是我们的应用程序运行所必需的：

```java
@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday.boot.jsp")
public class SpringBootJspApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringBootJspApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJspApplication.class);
    }
}
```

## 5. 提供一个简单的网页

JSP页面依赖于JavaServer Pages标准标签库(JSTL)来提供常见的模板功能，如分支、迭代和格式化，它甚至提供了一组预定义的函数。

让我们创建一个简单的网页，显示保存在应用程序中的书籍列表。

假设我们有一个BookService可以帮助我们查找所有Book对象：

```java
public class Book {
    private String isbn;
    private String name;
    private String author;

    // getters, setters, constructors and toString
}

public interface BookService {
    Collection<Book> getBooks();

    Book addBook(Book book);
}
```

我们可以编写一个Spring MVC控制器来将其公开为API：

```java
@Controller
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/viewBooks")
    public String viewBooks(Model model) {
        model.addAttribute("books", bookService.getBooks());
        return "view-books";
    }
}
```

请注意，上面的BookController将返回一个名为view-books的视图模板，根据我们之前在application.properties中的配置，Spring MVC将在/WEB-INF/jsp/目录中查找view-books.jsp。

我们需要在该位置创建这个文件：

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>View Books</title>
    <link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css">
</head>
<body>
<table>
    <thead>
    <tr>
        <th>ISBN</th>
        <th>Name</th>
        <th>Author</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${books}" var="book">
        <tr>
            <td>${book.isbn}</td>
            <td>${book.name}</td>
            <td>${book.author}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
```

上面的例子向我们展示了如何使用JSTL <c:url>标签链接到JavaScript和CSS等外部资源，我们通常将它们放在${project.basedir}/main/resources/static/目录下。

我们还可以看到如何使用JSTL <c:forEach>标签来迭代BookController提供的书籍模型属性。

## 6. 处理表单提交

现在让我们看看如何使用JSP处理表单提交。

我们的BookController需要提供MVC端点来为表单提供服务以添加书籍并处理表单提交：

```java
public class BookController {

    // already existing code

    @GetMapping("/addBook")
    public String addBookView(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping("/addBook")
    public RedirectView addBook(@ModelAttribute("book") Book book, RedirectAttributes redirectAttributes) {
        final RedirectView redirectView = new RedirectView("/book/addBook", true);
        Book savedBook = bookService.addBook(book);
        redirectAttributes.addFlashAttribute("savedBook", savedBook);
        redirectAttributes.addFlashAttribute("addBookSuccess", true);
        return redirectView;
    }
}
```

我们将创建以下add-book.jsp文件(记住将其放在正确的目录中)：

```html
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Book</title>
</head>
<body>
<c:if test="${addBookSuccess}">
    <div>Successfully added Book with ISBN: ${savedBook.isbn}</div>
</c:if>

<c:url var="add_book_url" value="/book/addBook"/>
<form:form action="${add_book_url}" method="post" modelAttribute="book">
    <form:label path="isbn">ISBN:</form:label>
    <form:input type="text" path="isbn"/>
    <form:label path="name">Book Name:</form:label>
    <form:input type="text" path="name"/>
    <form:label path="author">Author Name:</form:label>
    <form:input path="author"/>
    <input type="submit" value="submit"/>
</form:form>
</body>
</html>
```

我们使用<form:form>标签提供的modelAttribute参数，将BookController中addBookView ()方法中添加的book属性绑定到表单上，该属性将在提交表单时填充。

由于使用了这个标签，我们需要单独定义表单操作URL，因为我们不能将标签放在标签中。我们还使用<form:input>标签中的路径path将每个输入字段绑定到Book对象中的一个属性。

有关如何处理表单提交的更多详细信息，请参阅我们的文章[Spring MVC中的表单入门]()。

## 7. 处理错误

**由于将Spring Boot与JSP结合使用的现有限制，我们无法提供自定义error.html来自定义默认/error映射**。相反，我们需要创建自定义错误页面来处理不同的错误。

### 7.1 静态错误页面

如果我们想为不同的HTTP错误显示自定义错误页面，我们可以提供一个静态错误页面。

假设我们需要为我们的应用程序抛出的所有4xx错误提供一个错误页面，我们可以简单地将一个名为4xx.html的文件放在${project.basedir}/main/resources/static/error/目录下。

如果我们的应用程序抛出4xx HTTP错误，Spring将解决此错误并返回提供的4xx.html页面。

### 7.2 动态错误页面

我们可以通过多种方式处理异常，以提供自定义的错误页面以及上下文信息。让我们看看Spring MVC如何使用@ControllerAdvice和@ExceptionHandler注解为我们提供这种支持。

假设我们的应用程序定义了一个DuplicateBookException：

```java
public class DuplicateBookException extends RuntimeException {
    private final Book book;

    public DuplicateBookException(Book book) {
        this.book = book;
    }

    // getter methods
}
```

另外，假设如果我们尝试添加两本具有相同ISBN的书，我们的BookServiceImpl类将抛出上述DuplicateBookException：

```java
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    // constructors, other override methods

    @Override
    public Book addBook(Book book) {
        final Optional<BookData> existingBook = bookRepository.findById(book.getIsbn());
        if (existingBook.isPresent()) {
            throw new DuplicateBookException(book);
        }

        final BookData savedBook = bookRepository.add(convertBook(book));
        return convertBookData(savedBook);
    }

    // conversion logic
}
```

下面的LibraryControllerAdvice类将定义我们想要处理的错误，以及我们将如何处理每个错误：

```java
@ControllerAdvice
public class LibraryControllerAdvice {

    @ExceptionHandler(value = DuplicateBookException.class)
    public ModelAndView duplicateBookException(DuplicateBookException e) {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("ref", e.getBook().getIsbn());
        modelAndView.addObject("object", e.getBook());
        modelAndView.addObject("message", "Cannot add an already existing book");
        modelAndView.setViewName("error-book");
        return modelAndView;
    }
}
```

我们需要定义error-book.jsp文件，这样上面的错误才会在这里得到解决。确保将其放在${project.basedir}/main/webapp/WEB-INF/jsp/目录下，因为这不再是静态HTML，而是需要编译的JSP模板。

## 8. 创建可执行文件

**如果我们计划将我们的应用程序部署在Tomcat等Web容器中，那么选择很简单，我们将使用war打包来实现这一点**。

但是，我们应该注意，如果我们使用带有嵌入式Servlet容器的JSP和Spring Boot，则不能使用jar打包。因此，如果作为独立应用程序运行，我们唯一的选择是war打包。

在任何一种情况下，我们的pom.xml都需要将其packaging标签设置为war：

```xml
<packaging>war</packaging>
```

如果我们没有使用Spring Boot父POM来管理依赖项，我们**需要包含[spring-boot-maven-plugin]()以确保生成的war文件能够作为独立应用程序运行**。

我们现在可以使用嵌入式Servlet容器运行我们的独立应用程序，或者简单地将生成的war文件放入Tomcat并让它为我们的应用程序服务。

## 9. 总结

我们在本教程中涉及了各种主题，让我们回顾一些关键的考虑因素：

-   JSP包含一些固有的限制，请考虑使用Thymeleaf或FreeMarker。
-   如果在Web容器上部署，请记住将必要的依赖项标记为provided。
-   如果使用Undertow用作嵌入式Servlet容器，则不支持JSP。
-   如果在Web容器中部署，我们的@SpringBootApplication注解类应该扩展SpringBootServletInitializer并提供必要的配置选项。
-   我们不能用JSP覆盖默认的/error页面，相反，我们需要提供自定义错误页面。
-   如果我们将JSP与Spring Boot一起使用，则JAR打包不是一个选项。