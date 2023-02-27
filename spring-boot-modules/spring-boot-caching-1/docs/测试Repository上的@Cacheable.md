## 1. 概述

除了实现之外，我们还可以**使用Spring的声明式缓存机制将缓存注解用于接口**。
例如，我们可以在Spring Data Repository上声明缓存注解。

## 2. 快速开始

首先，我们创建一个简单的模型：

```java

@Entity
public class Book {

    @Id
    private UUID id;
    private String title;
    // getter setter ...
}
```

然后，我们创建一个Repository接口，其中包含指定了@Cacheable注解的方法：

```java
public interface BookRepository extends CrudRepository<Book, UUID> {

    @Cacheable(value = "books", unless = "#a0=='Foundation'")
    Optional<Book> findFirstByTitle(String title);
}
```

此处的unless参数不是强制性的，它只是帮助我们稍后测试一些缓存未命中的场景。

另外，请注意SpEL表达式“#a0”，而不是更可读的“#title”。
我们这样做是因为代理不会保留参数名称。因此，我们使用替代的#root.arg[0]、p0或a0表示法。

## 3. 测试

我们测试的目标是**确保缓存机制有效**，因此这里不过多讲解Spring Data Repository实现或持久层方面。

### 3.1 Spring Boot

让我们从一个简单的Spring Boot测试开始。

首先，我们注入测试依赖bean，添加一些测试数据，并编写一个简单的工具方法来检查一个Book对象是否在缓存中：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CacheApplication.class)
@EntityScan(basePackageClasses = Book.class)
@EnableJpaRepositories(basePackageClasses = BookRepository.class)
class BookRepositoryIntegrationTest {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.save(new Book(UUID.randomUUID(), "Dune"));
        bookRepository.save(new Book(UUID.randomUUID(), "Foundation"));
    }

    private Optional<Book> getCacheBook(String title) {
        return ofNullable(cacheManager.getCache("books")).map(c -> c.get(title, Book.class));
    }
}
```

现在，我们确保**在请求获取一个Book对象后，它会被放入缓存中**：

```java
class BookRepositoryIntegrationTest {

    @Test
    void givenBookThatShouldBeCached_whenFindByTitle_thenResultShouldBePutInCache() {
        Optional<Book> dune = bookRepository.findFirstByTitle("Dune");
        assertEquals(dune, getCacheBook("Dune"));
    }
}
```

另外，**在某些情况下(title为Foundation)Book对象不会放入缓存中**：

```java
class BookRepositoryIntegrationTest {

    @Test
    void givenBookThatShouldNotBeCached_whenFindByTitle_thenResultShouldNotBePutInCache() {
        bookRepository.findFirstByTitle("Foundation");
        assertEquals(empty(), getCacheBook("Foundation"));
    }
}
```

在这个测试中，我们使用Spring提供的CacheManager，
**并根据@Cacheable规则检查每次bookRepository.findFirstByTitle操作后CacheManager是否包含(或不包含)Book对象**。

### 3.2 Spring

为了改变我们的Spring集成测试，这次我们mock BookRepository接口，然后我们在不同的测试用例中验证与它的交互。

首先创建一个@Configuration，它为我们的BookRepository提供mock实现：

```java

@ContextConfiguration
@ExtendWith(SpringExtension.class)
class BookRepositoryCachingIntegrationTest {

    private static final Book DUNE = new Book(UUID.randomUUID(), "Dune");
    private static final Book FOUNDATION = new Book(UUID.randomUUID(), "Foundation");

    private BookRepository mock;

    @Autowired
    private BookRepository bookRepository;

    @EnableCaching
    @Configuration
    public static class CachingTestConfig {

        @Bean
        public BookRepository bookRepositoryMockImplementation() {
            return mock(BookRepository.class);
        }

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("books");
        }
    }
}
```

在继续设置我们的mock行为之前，有两个方面值得一提的是：

+ **BookRepository是我们mock的代理**。因此，为了使用Mockito verify，我们通过AopTestUtils.getTargetObject检索实际的mock。
+ 我们**确保在测试之间调用reset(mock)**，因为CachingTestConfig只加载一次。

```java
class BookRepositoryCachingIntegrationTest {

    @BeforeEach
    void setUp() {
        mock = AopTestUtils.getTargetObject(bookRepository);
        reset(mock);

        when(mock.findFirstByTitle(eq("Foundation"))).thenReturn(of(FOUNDATION));
        when(mock.findFirstByTitle(eq("Dune")))
                .thenReturn(of(DUNE))
                .thenThrow(new RuntimeException("Book should be cached!"));
    }
}
```

现在，我们可以添加我们的测试方法。
**首先，我们需要确保一个Book对象被放入缓存后，当稍后尝试检索该Book对象时，不应该再与Repository实现交互**：

```java
class BookRepositoryCachingIntegrationTest {

    @Test
    void givenCacheBook_whenFindByTitle_thenRepositoryShouldNotBeHit() {
        assertEquals(of(DUNE), bookRepository.findFirstByTitle("Dune"));
        verify(mock).findFirstByTitle("Dune");

        assertEquals(of(DUNE), bookRepository.findFirstByTitle("Dune"));
        assertEquals(of(DUNE), bookRepository.findFirstByTitle("Dune"));
        verifyNoMoreInteractions(mock);
    }
}
```

此外，对于**没资格缓存的Book对象，我们每次都需要调用Repository**：

```java
class BookRepositoryCachingIntegrationTest {

    @Test
    void givenNotCachedBook_whenFindByTitle_thenRepositoryShouldBeHit() {
        assertEquals(of(FOUNDATION), bookRepository.findFirstByTitle("Foundation"));
        assertEquals(of(FOUNDATION), bookRepository.findFirstByTitle("Foundation"));
        assertEquals(of(FOUNDATION), bookRepository.findFirstByTitle("Foundation"));

        verify(mock, times(3)).findFirstByTitle("Foundation");
    }
}
```

## 4. 总结

综上所述，我们使用Spring、Mockito和Spring Boot实现了一系列集成测试，以确保应用于接口的缓存机制正常工作。

请注意，我们也可以结合上述方法。例如，我们可以在Spring Boot中使用mock或在普通Spring测试中对CacheManager执行检查。