## 1. 概述

如今，Quarkus使开发健壮和干净的应用程序变得非常容易。但是测试呢？

在本教程中，**我们将仔细研究如何测试Quarkus应用程序**。我们将探讨Quarkus提供的测试可能性，**并介绍依赖管理和注入、Mock、Profile配置等概念，以及更具体的内容，如Quarkus注解和测试本机可执行文件**。

## 2. 设置

让我们从我们之前的[QuarkusIO指南](https://www.baeldung.com/quarkus-io)中配置的基本Quarkus项目开始。

首先，我们将添加[quarkus-resteasy-jackson](https://search.maven.org/search?q=a:quarkus-resteasy-jackson)、[quarkus-hibernate-orm-panache](https://search.maven.org/search?q=a:quarkus-hibernate-orm-panache)、[quarkus-jdbc-h2](https://search.maven.org/search?q=a:quarkus-jdbc-h2)、[quarkus-junit5-mockito](https://search.maven.org/search?q=a:quarkus-junit5-mockito)和[quarkus-test-h2](https://search.maven.org/search?q=a:quarkus-test-h2) Maven依赖项：

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-jackson</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-h2</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5-mockito</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-test-h2</artifactId>
</dependency>
```

接下来，让我们创建我们的域实体：

```java
public class Book extends PanacheEntity {
    private String title;
    private String author;
}
```

我们继续添加一个简单的Panache Repository，以及一种搜索书籍的方法：

```java
public class BookRepository implements PanacheRepository {

    public Stream<Book> findBy(String query) {
        return find("author like :query or title like :query", with("query", "%" + query + "%")).stream();
    }
}
```

现在，让我们编写一个LibraryService来保存任何业务逻辑：

```java
public class LibraryService {

    public Set<Book> find(String query) {
        if (query == null) {
            return bookRepository.findAll().stream().collect(toSet());
        }
        return bookRepository.findBy(query).collect(toSet());
    }
}
```

最后，让我们通过创建LibraryResource通过HTTP公开我们的服务功能：

```java
@Path("/library")
public class LibraryResource {

    @GET
    @Path("/book")
    public Set findBooks(@QueryParam("query") String query) {
        return libraryService.find(query);
    }
}
```

## 3. @Alternative实现

在编写任何测试之前，让我们确保我们的Repository中有一些书籍。借助Quarkus，**我们可以使用CDI @Alternative机制为我们的测试提供自定义bean实现**。让我们创建一个扩展BookRepository的TestBookRepository：

```java
@Priority(1)
@Alternative
@ApplicationScoped
public class TestBookRepository extends BookRepository {

    @PostConstruct
    public void init() {
        persist(new Book("Dune", "Frank Herbert"),
              new Book("Foundation", "Isaac Asimov"));
    }
}
```

我们将这个替代bean放在我们的测试包中，并且由于@Priority(1)和@Alternative注解，我们确信任何测试都会在实际的BookRepository实现上选择它。**这是我们可以提供所有Quarkus测试都可以使用的全局Mock的一种方式**。我们稍后将探索更狭隘的Mock，但现在，让我们继续创建我们的第一个测试。

## 4. HTTP集成测试

让我们从创建一个简单的REST-assured集成测试开始：

```java
@QuarkusTest
class LibraryResourceIntegrationTest {

    @Test
    void whenGetBooksByTitle_thenBookShouldBeFound() {

        given().contentType(ContentType.JSON).param("query", "Dune")
              .when().get("/library/book")
              .then().statusCode(200)
              .body("size()", is(1))
              .body("title", hasItem("Dune"))
              .body("author", hasItem("Frank Herbert"));
    }
}
```

**这个用@QuarkusTest标注的测试首先启动Quarkus应用程序**，然后对我们资源的端点执行一系列HTTP请求。

现在，让我们利用一些Quarkus机制来尝试进一步改进我们的测试。

### 4.1 使用@TestHTTPResource进行URL注入

让我们注入资源URL，而不是硬编码HTTP端点的路径：

```java
@TestHTTPResource("/library/book")
URL libraryEndpoint;
```

然后，让我们在请求中使用它：

```java
given().param("query", "Dune")
    .when().get(libraryEndpoint)
    .then().statusCode(200);
```

或者，在不使用Rest-assured的情况下，让我们简单地打开一个到注入URL的连接并测试响应：

```java
@Test
void whenGetBooks_thenBooksShouldBeFound() throws IOException {
    assertTrue(IOUtils.toString(libraryEndpoint.openStream(), defaultCharset()).contains("Asimov"));
}
```

正如我们所见，@TestHTTPResource URL注入为我们提供了一种简单灵活的方式来访问我们的端点。

### 4.2 @TestHTTPEndpoint

让我们更进一步，使用Quarkus提供的@TestHTTPEndpoint注解来配置我们的端点：

```java
@TestHTTPEndpoint(LibraryResource.class)
@TestHTTPResource("book")
URL libraryEndpoint;
```

这样，如果我们决定更改LibraryResource的路径，测试将选择正确的路径，而无需我们修改它。

**@TestHTTPEndpoint也可以在类级别应用，在这种情况下，REST-assured将自动为所有请求添加LibraryResource的路径前缀**：

```java
@QuarkusTest
@TestHTTPEndpoint(LibraryResource.class)
class LibraryHttpEndpointIntegrationTest {

    @Test
    void whenGetBooks_thenShouldReturnSuccessfully() {
        given().contentType(ContentType.JSON)
              .when().get("book")
              .then().statusCode(200);
    }
}
```

## 5. 上下文和依赖注入

当涉及到依赖注入时，**在Quarkus测试中，我们可以对任何需要的依赖使用@Inject**。让我们通过为我们的LibraryService创建一个测试来了解这一点：

```java
@QuarkusTest
class LibraryServiceIntegrationTest {

    @Inject
    LibraryService libraryService;

    @Test
    void whenFindByAuthor_thenBookShouldBeFound() {
        assertFalse(libraryService.find("Frank Herbert").isEmpty());
    }
}
```

现在，让我们尝试测试我们的Panache BookRepository：

```java
class BookRepositoryIntegrationTest {

    @Inject
    BookRepository bookRepository;

    @Test
    void givenBookInRepository_whenFindByAuthor_thenShouldReturnBookFromRepository() {
        assertTrue(bookRepository.findBy("Herbert").findAny().isPresent());
    }
}
```

但是当我们运行测试时，它失败了。那是因为**它需要在事务的上下文中运行并且没有活动状态**。这可以简单地通过将@Transactional添加到测试类来解决。或者，如果我们愿意，我们可以定义自己的构造型来捆绑@QuarkusTest和@Transactional。让我们通过创建@QuarkusTransactionalTest注解来做到这一点：

```java
@QuarkusTest
@Stereotype
@Transactional
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface QuarkusTransactionalTest {
}
```

现在，让我们将它应用到我们的测试中：

```java
@QuarkusTransactionalTest
class BookRepositoryIntegrationTest
```

正如我们所看到的，因为**Quarkus测试是完整的CDI beans**，我们可以利用所有CDI的好处，如依赖注入、事务上下文和CDI拦截器。

## 6. Mocking

Mock是任何测试工作的一个关键方面。正如我们在上面已经看到的，Quarkus测试可以利用CDI @Alternative机制。现在让我们更深入地了解Quarkus必须提供的Mock功能。

### 6.1 @Mock

**作为@Alternative方法的轻微简化**，我们可以使用@Mock构造型注解。这将@Alternative和@Primary(1)注解捆绑在一起。

### 6.2 @QuarkusMock

如果我们不想拥有一个全局定义的Mock，而是希望**我们的Mock只在一个测试的范围内**，我们可以使用@QuarkusMock：

```java
@QuarkusTest
class LibraryServiceQuarkusMockUnitTest {

    @Inject
    LibraryService libraryService;

    @BeforeEach
    void setUp() {
        BookRepository mock = Mockito.mock(TestBookRepository.class);
        Mockito.when(mock.findBy("Asimov"))
              .thenReturn(Arrays.stream(new Book[] {
                    new Book("Foundation", "Isaac Asimov"),
                    new Book("I Robot", "Isaac Asimov")}));
        QuarkusMock.installMockForType(mock, BookRepository.class);
    }

    @Test
    void whenFindByAuthor_thenBooksShouldBeFound() {
        assertEquals(2, libraryService.find("Asimov").size());
    }
}
```

### 6.3 @InjectMock

让我们稍微简化一下并**使用Quarkus@InjectMock注解而不是@QuarkusMock**：

```java
@QuarkusTest
class LibraryServiceInjectMockUnitTest {

    @Inject
    LibraryService libraryService;

    @InjectMock
    BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        when(bookRepository.findBy("Frank Herbert"))
              .thenReturn(Arrays.stream(new Book[] {
                    new Book("Dune", "Frank Herbert"),
                    new Book("Children of Dune", "Frank Herbert")}));
    }

    @Test
    void whenFindByAuthor_thenBooksShouldBeFound() {
        assertEquals(2, libraryService.find("Frank Herbert").size());
    }
}
```

### 6.4 @InjectSpy

如果我们只对spying而不是替换bean行为感兴趣，我们可以使用提供的@InjectSpy注解：

```java
@QuarkusTest
class LibraryResourceInjectSpyIntegrationTest {

    @InjectSpy
    LibraryService libraryService;

    @Test
    void whenGetBooksByAuthor_thenBookShouldBeFound() {
        given().contentType(ContentType.JSON).param("query", "Asimov")
              .when().get("/library/book")
              .then().statusCode(200);

        verify(libraryService).find("Asimov");
    }
}
```

## 7. 测试Profile

我们可能希望**在不同的配置中运行我们的测试**。为此，**Quarkus提供了测试Profile的概念**。让我们使用BookRepository的自定义版本创建一个针对不同数据库引擎运行的测试，它还将在与已配置的路径不同的路径上公开我们的HTTP资源。

为此，我们首先实现一个QuarkusTestProfile：

```java
public class CustomTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Collections.singletonMap("quarkus.resteasy.path", "/custom");
    }

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Collections.singleton(TestBookRepository.class);
    }

    @Override
    public String getConfigProfile() {
        return "custom-profile";
    }
}
```

现在让我们通过添加一个custom-profile配置属性来配置我们的application.properties，它将我们的H2存储从内存更改为文件：

```shell
%custom-profile.quarkus.datasource.jdbc.url = jdbc:h2:file:./testdb
```

最后，在所有资源和配置就绪的情况下，让我们编写测试：

```java
@QuarkusTest
@TestProfile(CustomBookRepositoryProfile.class)
class CustomLibraryResourceManualTest {

    public static final String BOOKSTORE_ENDPOINT = "/custom/library/book";

    @Test
    void whenGetBooksGivenNoQuery_thenAllBooksShouldBeReturned() {
        given().contentType(ContentType.JSON)
              .when().get(BOOKSTORE_ENDPOINT)
              .then().statusCode(200)
              .body("size()", is(2))
              .body("title", hasItems("Foundation", "Dune"));
    }
}
```

正如我们从@TestProfile注解中看到的那样，此测试将使用CustomTestProfile。它将向Profile的getConfigOverrides方法中覆盖的自定义端点发出HTTP请求。此外，它将使用在getEnabledAlternatives方法中配置的替代BookRepository实现。最后，通过使用getConfigProfile中定义的custom-profile，它将数据保存在文件中而不是内存中。

需要注意的一件事是，**在执行此测试之前，Quarkus将关闭然后使用新Profile重新启动**。这会在关闭/重启发生时增加一些时间，但这是为额外的灵活性付出的代价。

## 8. 测试本机可执行文件

Quarkus提供了测试本机可执行文件的可能性。让我们创建一个本机镜像测试：

```java
@NativeImageTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class NativeLibraryResourceIT extends LibraryHttpEndpointIntegrationTest {
}
```

现在，通过运行：

```bash
mvn verify -Pnative
```

我们将看到正在构建的本机镜像以及针对它运行的测试。

@NativeImageTest注解指示Quarkus针对本机镜像运行此测试，而@QuarkusTestResource将在测试开始之前将H2实例启动到单独的进程中。后者是针对本机可执行文件运行测试所必需的，因为数据库引擎未嵌入到本机镜像中。

@QuarkusTestResource注解也可用于启动自定义服务，例如Testcontainers。我们需要做的就是实现QuarkusTestResourceLifecycleManager接口并使用以下内容标注我们的测试：

```java
@QuarkusTestResource(OurCustomResourceImpl.class)
```

你将需要一个[GraalVM](https://quarkus.io/guides/building-native-image#configuring-graalvm)来构建本机镜像。

另外请注意，目前，注入不适用于本机镜像测试。**唯一在本机运行的是Quarkus应用程序，而不是测试本身**。

## 9. 总结

在本文中，我们了解了Quarkus如何为测试我们的应用程序提供出色的支持。从依赖管理、注入和Mock等简单的事情，到Profile和本机镜像等更复杂的方面，Quarkus为我们提供了许多工具来创建强大而干净的测试。