## 1. 概述

默认情况下，Spring Data JPA的查询区分大小写。换句话说，字段值的比较区分大小写。

在本教程中，**我们将探讨如何在Spring Data JPA Repository中快速创建不区分大小写的查询**。

## 2. Maven依赖

首先，让我们确保我们的pom.xml中有[Spring Data](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)和[H2](https://www.baeldung.com/java-in-memory-databases)数据库依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
    <version>1.4.200</version>
</dependency>
```

## 3. 初始设置

假设我们有一个包含id、firstName和lastName属性的Passenger实体：

```java
@Entity
public class Passenger {

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
    
    // constructor, static factory, getters, setters ...
}
```

此外，让我们通过使用一些示例Passenger数据填充数据库来准备我们的测试类：

```java
@DataJpaTest(showSql = false)
@ExtendWith(SpringExtension.class)
class PassengerRepositoryIntegrationTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PassengerRepository repository;

    @BeforeEach
    void before() {
        entityManager.persist(Passenger.from("Jill", "Smith"));
        entityManager.persist(Passenger.from("Eve", "Jackson"));
        entityManager.persist(Passenger.from("Fred", "Bloggs"));
        entityManager.persist(Passenger.from("Ricki", "Bobbie"));
        entityManager.persist(Passenger.from("Siya", "Kolisi"));
    }
}
```

## 4. 不区分大小写查询

现在，假设我们要执行不区分大小写的搜索，以查找具有给定firstName的所有乘客。

为此，我们将PassengerRepository定义为：

```java
@Repository
interface PassengerRepository extends JpaRepository<Passenger, Long> {
    List<Passenger> findByFirstNameIgnoreCase(String firstName);
}
```

在这里，**IgnoreCase关键字确保查询匹配不区分大小写**。

我们还可以在JUnit测试的帮助下进行测试：

```java
@Test
void givenPassengers_whenMatchingIgnoreCase_thenExpectedReturned() {
	Passenger jill = Passenger.from("Jill", "Smith");
	Passenger eve = Passenger.from("Eve", "Jackson");
	Passenger fred = Passenger.from("Fred", "Bloggs");
	Passenger siya = Passenger.from("Siya", "Kolisi");
	Passenger ricki = Passenger.from("Ricki", "Bobbie");
	
	List<Passenger> passengers = repository.findByFirstNameIgnoreCase("FrED");
	
	assertThat(passengers, contains(fred));
	assertThat(passengers, not(contains(eve)));
	assertThat(passengers, not(contains(siya)));
	assertThat(passengers, not(contains(jill)));
	assertThat(passengers, not(contains(ricki)));
}
```

尽管我们使用“FrED”作为参数传递，但我们返回的Passenger列表中包含一个firstName为“Fred”的Passenger。显然，在IgnoreCase关键字的帮助下，我们实现了不区分大小写的匹配。

## 5. 总结

在这个快速教程中，我们学习了如何在Spring Data Repository中创建不区分大小写的查询。