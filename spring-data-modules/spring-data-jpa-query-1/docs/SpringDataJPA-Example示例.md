## 1. 概述

在本教程中，我们将学习如何使用Spring Data [Example API](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example)查询数据。

首先，我们将定义要查询的数据表。接下来，我们将检查Spring Data中的一些相关类。然后，我们将通过几个例子来演示。

## 2. 测试数据

我们的测试数据是乘客姓名列表以及他们占用的座位。

| First Name  | Last Name  | Seat Number |
|:-----------:|:----------:|:-----------:|
|    Jill     |   Smith    |     50      |
|     Eve     |  Jackson   |     94      |
|    Fred     |   Bloggs   |     22      |
|    Ricki    |   Bobbie   |     36      |
|    Siya     |   Kolisi   |     85      |

## 3. 实体

让我们创建我们需要的[Spring Data Repository](https://www.baeldung.com/spring-data-repositories)并提供我们的域类和id类型。

首先，我们将乘客建模为JPA实体：

```java
@Entity
class Passenger {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false)
    private String firstName;

    @Basic(optional = false)
    @Column(nullable = false)
    private String lastName;

    @Basic(optional = false)
    @Column(nullable = false)
    private int seatNumber;
    // constructor, getters ...
}
```

我们可以将其建模为另一个抽象，而不是使用JPA。

## 4. 通过Example API查询

首先，让我们看一下JpaRepository接口。正如我们所看到的，它扩展了[QueryByExampleExecutor](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/query/QueryByExampleExecutor.html)接口以支持Example查询：

```java
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
}
```

这个接口引入了更多我们从Spring Data中熟悉的find()方法的变体。但是，每个方法也接收一个[Example](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Example.html)的实例作为参数：

```java
public interface QueryByExampleExecutor<T> {
    <S extends T> Optional<S> findOne(Example<S> var1);

    <S extends T> Iterable<S> findAll(Example<S> var1);

    <S extends T> Iterable<S> findAll(Example<S> var1, Sort var2);

    <S extends T> Page<S> findAll(Example<S> var1, Pageable var2);

    <S extends T> long count(Example<S> var1);

    <S extends T> boolean exists(Example<S> var1);
}
```

其次，Example接口公开了访问probe和[ExampleMatcher](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/ExampleMatcher.html)的方法。

重要的是要知道probe是我们实体的实例：

```java
public interface Example<T> {

    static <T> org.springframework.data.domain.Example<T> of(T probe) {
        return new TypedExample(probe, ExampleMatcher.matching());
    }

    static <T> org.springframework.data.domain.Example<T> of(T probe, ExampleMatcher matcher) {
        return new TypedExample(probe, matcher);
    }

    T getProbe();

    ExampleMatcher getMatcher();

    default Class<T> getProbeType() {
        return ProxyUtils.getUserClass(this.getProbe().getClass());
    }
}
```

总之，我们的probe和ExampleMatcher一起指定了我们的查询。

## 5. 局限性

与所有事物一样，Example API也有一些限制。例如：

+ 不支持嵌套和分组语句，例如：(firstName = ?0 and lastName= ?1) or seatNumber=?2
+ 字符串匹配仅包括精确、不区分大小写、以...开始、以...结束、contains和正则表达式
+ 除String以外的所有类型都只能完全匹配

现在我们对API及其限制有了更多的了解，让我们深入研究一些示例。

## 6. 示例

### 6.1 区分大小写匹配

让我们从一个简单的例子开始，讨论它的默认行为：

```java
@DataJpaTest(showSql = false)
@ExtendWith(SpringExtension.class)
class PassengerRepositoryIntegrationTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PassengerRepository passengerRepository;

    @BeforeEach
    void before() {
        entityManager.persist(Passenger.from("Jill", "Smith", 50));
        entityManager.persist(Passenger.from("Eve", "Jackson", 95));
        entityManager.persist(Passenger.from("Fred", "Bloggs", 22));
        entityManager.persist(Passenger.from("Ricki", "Bobbie", 36));
        entityManager.persist(Passenger.from("Siya", "Kolisi", 85));
    }

    @Test
    void givenPassengers_whenFindByExampleDefaultMatcher_thenExpectedReturned() {
        Example<Passenger> example = Example.of(Passenger.from("Fred", "Bloggs", null));
        Optional<Passenger> actual = passengerRepository.findOne(example);
        assertTrue(actual.isPresent());
        assertEquals(Passenger.from("Fred", "Bloggs", 22), actual.get());
    }
}
```

特别是，静态Example.of()方法使用ExampleMatcher.matching()构建一个Example。

换句话说，**将对Passenger的所有非空属性执行精确匹配**。因此，匹配对String属性区分大小写。

但是，如果我们所能做的只是对所有非null属性进行精确匹配，那它就不会太有用了。

这就是ExampleMatcher的用武之地。通过构建我们自己的ExampleMatcher，我们可以自定义行为以满足我们的需求。

### 6.2 不区分大小写的匹配

考虑到这一点，让我们看另一个例子，这次使用withIgnoreCase()来实现不区分大小写的匹配：

```java
@Test
void givenPassengers_whenFindByExampleCaseInsensitiveMatcher_thenExpectedReturned() {
	ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
	Example<Passenger> example = Example.of(Passenger.from("fred", "bloggs", null),
	    caseInsensitiveExampleMatcher);
    
	Optional<Passenger> actual = repository.findOne(example);
    
	assertTrue(actual.isPresent());
	assertEquals(Passenger.from("Fred", "Bloggs", 22), actual.get());
}
```

在此示例中，请注意我们首先调用了ExampleMatcher.matchingAll()-它与我们在前面的例子中使用的ExampleMatcher.matching()具有相同的行为。

### 6.3 自定义匹配

我们还可以**在每个属性的基础上调整匹配器的行为**，并使用ExampleMatcher.matchingAny()匹配任何属性：

```java
@Test
void givenPassengers_whenFindByExampleCustomMatcher_thenExpectedReturned() {
	Passenger jill = Passenger.from("Jill", "Smith", 50);
	Passenger eve = Passenger.from("Eve", "Jackson", 95);
	Passenger fred = Passenger.from("Fred", "Bloggs", 22);
	Passenger siya = Passenger.from("Siya", "Kolisi", 85);
	Passenger ricki = Passenger.from("Ricki", "Bobbie", 36);
    
	ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny().withMatcher("firstName",
	    ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()).withMatcher("lastName",
	    ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
    
	Example<Passenger> example = Example.of(Passenger.from("e", "s", null),
	    customExampleMatcher);
    
	List<Passenger> passengers = repository.findAll(example);
    
	assertThat(passengers, contains(jill, eve, fred, siya));
	assertThat(passengers, not(contains(ricki)));
}
```

### 6.4 忽略属性

另一方面，我们也可能只想**查询我们属性的一个子集**。

我们通过使用ExampleMatcher.ignorePaths(String...paths)忽略一些属性来实现这一点：

```java
@Test
void givenPassengers_whenFindByIgnoringMatcher_thenExpectedReturned() {
	Passenger jill = Passenger.from("Jill", "Smith", 50);
	Passenger eve = Passenger.from("Eve", "Jackson", 95);
	Passenger fred = Passenger.from("Fred", "Bloggs", 22);
	Passenger siya = Passenger.from("Siya", "Kolisi", 85);
	Passenger ricki = Passenger.from("Ricki", "Bobbie", 36);
    
	ExampleMatcher ignoringExampleMatcher = ExampleMatcher.matchingAny().withMatcher("lastName",
	    ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase()).withIgnorePaths("firstName", "seatNumber");
    
	Example<Passenger> example = Example.of(Passenger.from(null, "b", null),
	    ignoringExampleMatcher);
    
	List<Passenger> passengers = repository.findAll(example);
    
	assertThat(passengers, contains(fred, ricki));
	assertThat(passengers, not(contains(jill)));
	assertThat(passengers, not(contains(eve)));
	assertThat(passengers, not(contains(siya)));
}
```

## 7. 总结

在本文中，我们演示了如何使用Example API。

我们已经演示了如何使用[Example](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Example.html)和[ExampleMatcher](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/ExampleMatcher.html)以及[QueryByExampleExecutor](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/query/QueryByExampleExecutor.html)接口来使用示例数据实例查询表。