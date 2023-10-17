## 1. 简介

[GraphQL](../../graphql-java/docs/GraphQL简介.md)是一种用于Web API的查询和操作语言，[SPQR](https://github.com/leangen/graphql-spqr)是为了让GraphQL的使用更加无缝而发起的库之一。

在本教程中，我们将学习GraphQL SPQR的基础知识，并在一个简单的Spring Boot项目中看到它的实际应用。

## 2. 什么是GraphQL SPQR？

GraphQL是Facebook创建的一种著名的查询语言，它的核心是模式-我们在其中定义自定义类型和函数的文件。

在传统方法中，如果我们想将GraphQL添加到我们的项目中，我们必须遵循两个步骤。首先，我们必须将GraphQL模式文件添加到项目中；其次，我们需要编写相应的Java POJO来表示模式中的每种类型，**这意味着我们将在两个地方维护相同的信息：模式文件和Java类**。这种方法容易出错，并且需要更多的精力来维护项目。

**GraphQL Schema Publisher & Query Resolver，简称SPQR，起源于为了减少上述问题-它只是从带注解的Java类中生成GraphQL模式**。

## 3. 使用Spring Boot引入GraphQL SPQR

要查看SPQR的实际应用，我们需要设置一个简单的服务，我们将使用[GraphQL Spring Boot Starter](https://mvnrepository.com/artifact/com.graphql-java/graphql-spring-boot-starter/5.0.2)和GraphQL SPQR。

### 3.1 设置

首先我们将SPQR和Spring Boot的依赖项添加到我们的POM中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <scope>test</scope>
<dependency>
    <groupId>io.leangen.graphql</groupId>
    <artifactId>spqr</artifactId>
    <version>0.11.2</version>
</dependency>
```

### 3.2 编写模型Book类

现在我们已经添加了必要的依赖项，让我们创建一个简单的Book类：

```java
public class Book {
    private Integer id;
    private String author;
    private String title;
}
```

正如我们在上面看到的，它不包含任何SPQR注解，**如果我们不拥有源代码但希望从这个库中受益，这可能非常有用**。

### 3.3 编写BookService

为了管理书籍的收藏，让我们创建一个BookService接口：

```java
public interface BookService {
    Book getBookWithTitle(String title);

    List<Book> getAllBooks();

    Book addBook(Book book);

    Book updateBook(Book book);

    boolean deleteBook(Book book);
}
```

然后，我们提供接口的实现：

```java
@Service
public class BookServiceImpl implements BookService {

    private static final Set<Book> BOOKS_DATA = initializeData();

    @Override
    public Book getBookWithTitle(String title) {
        return BOOKS_DATA.stream()
              .filter(book -> book.getTitle().equals(title))
              .findFirst()
              .orElse(null);
    }

    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<>(BOOKS_DATA);
    }

    @Override
    public Book addBook(Book book) {
        BOOKS_DATA.add(book);
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        BOOKS_DATA.removeIf(b -> Objects.equals(b.getId(), book.getId()));
        BOOKS_DATA.add(book);
        return book;
    }

    @Override
    public boolean deleteBook(Book book) {
        return BOOKS_DATA.remove(book);
    }

    private static Set<Book> initializeData() {
        Book book = new Book(1, "J. R. R. Tolkien", "The Lord of the Rings");
        Set<Book> books = new HashSet<>();
        books.add(book);
        return books;
    }
}
```

### 3.4 使用graphql-spqr公开服务

剩下的唯一事情就是创建一个解析器，它将公开GraphQL突变和查询。**为此，我们将使用两个重要的SPQR注解-@GraphQLMutation和@GraphQLQuery**：

```java
@Service
public class BookResolver {

    @Autowired
    BookService bookService;

    @GraphQLQuery(name = "getBookWithTitle")
    public Book getBookWithTitle(@GraphQLArgument(name = "title") String title) {
        return bookService.getBookWithTitle(title);
    }

    @GraphQLQuery(name = "getAllBooks", description = "Get all books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GraphQLMutation(name = "addBook")
    public Book addBook(@GraphQLArgument(name = "newBook") Book book) {
        return bookService.addBook(book);
    }

    @GraphQLMutation(name = "updateBook")
    public Book updateBook(@GraphQLArgument(name = "modifiedBook") Book book) {
        return bookService.updateBook(book);
    }

    @GraphQLMutation(name = "deleteBook")
    public void deleteBook(@GraphQLArgument(name = "book") Book book) {
        bookService.deleteBook(book);
    }
}
```

如果我们不想在每个方法中都写上@GraphQLArgument，并且对将GraphQL参数命名为输入参数感到满意，我们可以使用-parameters参数编译代码。

### 3.5 控制器

最后，我们定义一个Spring @RestController，**为了使用SPQR公开服务，我们将配置GraphQLSchema和GraphQL对象**：

```java
@RestController
public class GraphqlController {

    private final GraphQL graphQL;

    @Autowired
    public GraphqlController(BookResolver bookResolver) {
        GraphQLSchema schema = new GraphQLSchemaGenerator()
              .withBasePackages("cn.tuyucheng.taketoday")
              .withOperationsFromSingleton(bookResolver)
              .generate();
        this.graphQL = new GraphQL.Builder(schema)
              .build();
    }
}
```

重要的是要注意我们**必须将BookResolver注册为单例**。

我们使用SPQR的最后一个任务是创建一个/graphql端点，它将作为与我们服务的单一联系点，并将执行请求的查询和突变：

```java
@PostMapping(value = "/graphql")
public Map<String, Object> execute(@RequestBody Map<String, String> request, HttpServletRequest raw) throws GraphQLException {
    ExecutionResult result = graphQL.execute(request.get("query"));
    return result.getData();
}
```

### 3.6 结果

我们可以通过检查/graphql端点来检查结果。例如，让我们通过执行以下cURL命令来检索所有Book记录：

```bash
curl -g 
  -X POST 
  -H "Content-Type: application/json" 
  -d '{"query":"{getAllBooks {id author title }}"}' 
  http://localhost:8080/graphql
```

### 3.7 测试

完成配置后，我们就可以测试我们的项目了。我们将使用@SpringBootTest来测试我们的新端点并验证响应，让我们定义JUnit测试并自动注入所需的WebTestClient：

```java
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpqrApplication.class)
class SpqrApplicationIntegrationTest {

    private static final String GRAPHQL_PATH = "/graphql";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenGetAllBooks_thenValidResponseReturned() {
        String getAllBooksQuery = "{getAllBooks{ id title author }}";

        webTestClient.post()
              .uri(GRAPHQL_PATH)
              .contentType(MediaType.APPLICATION_JSON)
              .body(Mono.just(toJSON(getAllBooksQuery)), String.class)
              .exchange()
              .expectStatus().isOk()
              .expectBody()
              .jsonPath("$.getAllBooks").isNotEmpty();
    }

    @Test
    void whenAddBook_thenValidResponseReturned() {
        String addBookMutation = "mutation { addBook(newBook: {id: 123, author: \"J. K. Rowling\", "
              + "title: \"Harry Potter and Philosopher's Stone\"}) { id author title } }";

        webTestClient.post()
              .uri(GRAPHQL_PATH)
              .contentType(MediaType.APPLICATION_JSON)
              .body(Mono.just(toJSON(addBookMutation)), String.class)
              .exchange()
              .expectStatus().isOk()
              .expectBody()
              .jsonPath("$.addBook.id").isEqualTo("123")
              .jsonPath("$.addBook.title").isEqualTo("Harry Potter and Philosopher's Stone")
              .jsonPath("$.addBook.author").isEqualTo("J. K. Rowling");
    }

    private static String toJSON(String query) {
        try {
            return new JSONObject().put("query", query).toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
```

## 4. 使用GraphQL SPQR Spring Boot Starter

致力于SPQR的团队创建了一个Spring Boot starter，这使得它的使用更加容易。

### 4.1 设置

首先我们需要将spqr-spring-boot-starter添加到POM文件：

```xml
<dependency>
    <groupId>io.leangen.graphql</groupId>
    <artifactId>graphql-spqr-spring-boot-starter</artifactId>
    <version>0.0.6</version>
</dependency>
```

### 4.2 BookService

然后，我们需要对BookServiceImpl添加两个修改。首先，它必须使用@GraphQLApi注解进行标注，此外，我们希望在API中公开的每个方法都必须具有相应的注解：

```java
@Service
@GraphQLApi
public class BookServiceImpl implements BookService {

    private static final Set<Book> BOOKS_DATA = initializeData();

    @GraphQLQuery(name = "getBookWithTitle")
    public Book getBookWithTitle(@GraphQLArgument(name = "title") String title) {
        return BOOKS_DATA.stream()
              .filter(book -> book.getTitle().equals(title))
              .findFirst()
              .orElse(null);
    }

    @GraphQLQuery(name = "getAllBooks", description = "Get all books")
    public List<Book> getAllBooks() {
        return new ArrayList<>(BOOKS_DATA);
    }

    @GraphQLMutation(name = "addBook")
    public Book addBook(@GraphQLArgument(name = "newBook") Book book) {
        BOOKS_DATA.add(book);
        return book;
    }

    @GraphQLMutation(name = "updateBook")
    public Book updateBook(@GraphQLArgument(name = "modifiedBook") Book book) {
        BOOKS_DATA.removeIf(b -> Objects.equals(b.getId(), book.getId()));
        BOOKS_DATA.add(book);
        return book;
    }

    @GraphQLMutation(name = "deleteBook")
    public boolean deleteBook(@GraphQLArgument(name = "book") Book book) {
        return BOOKS_DATA.remove(book);
    }

    private static Set<Book> initializeData() {
        Book book = new Book(1, "J. R. R. Tolkein", "The Lord of the Rings");
        Set<Book> books = new HashSet<>();
        books.add(book);
        return books;
    }
}
```

正如我们所看到的，我们基本上将代码从BookResolver移到了BookServiceImpl。此外，**我们不需要GraphqlController类-也就是自动添加一个/graphql端点**。

## 5. 总结

GraphQL是一个令人兴奋的框架，也是传统RESTful端点的替代品，在提供很大灵活性的同时，它也带来了一些繁琐的任务，例如维护模式文件。**而SPQR致力于让使用GraphQL变得更容易、更不容易出错**。

在本文中，我们了解了如何将SPQR添加到现有的POJO中并将其配置为提供查询和突变。然后，我们在GraphQL中看到了一个新的端点。最后，我们使用Spring的测试支持验证了应用程序的行为。

与往常一样，此处使用的示例代码可在[GitHub上]()获得。此外，[GitHub上]()提供了SPQR Spring Boot初学者工具包的代码。