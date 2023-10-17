## 1. 概述

在本快速教程中，我们将了解Spring Data中对Vavr的支持-这是在2.0.0 Spring构建快照中添加的。

更具体地说，我们将展示一个使用VavrOption和Vavr集合作为Spring Data JPA Repository的返回类型的示例。

## 2. Maven依赖

首先，让我们设置一个Spring Boot项目，因为它可以通过将spring-boot-parent依赖项添加到pom.xml来更快地配置Spring Data：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
    <relativePath />
</parent>
```

显然，我们还需要vavr依赖项，以及一些用于Spring Data和测试的其他依赖项：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>
```

可以从Maven Central下载最新版本的[vavr](https://search.maven.org/search?q=a:vavr)、[spring-boot-starter-data-jpa](https://search.maven.org/search?q=a:spring-boot-starter-data-jpa)、[spring-boot-starter-test](https://search.maven.org/search?q=spring-boot-starter-test)和[h2](https://search.maven.org/search?q=a:h2)。

在此示例中，我们仅使用Spring Boot，因为它提供了Spring Data自动配置。如果你在非Boot项目中工作，则可以直接添加具有Vavr支持的spring-data-commons依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-commons</artifactId>
    <version>2.0.0.RELEASE</version>
</dependency>
```

## 3. 带有Vavr的Spring Data JPA Repository

Spring Data现在支持使用Vavr的Option和Vavr集合定义Repository查询方法：Seq、Set和Map作为返回类型。

首先，让我们创建一个简单的实体类来操作：

```java
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;

	// standard constructor, getters, setters
}
```

接下来，让我们通过实现Repository接口并定义两个查询方法来创建JPARepository：

```java
public interface VavrUserRepository extends Repository<User, Long> {

	Option<User> findById(long id);

	Seq<User> findByName(String name);

	User save(User user);
}
```

在这里，我们将Vavr Option用于返回零个或一个结果的方法，并将Vavr Seq用于返回多个用户记录的查询方法。

我们还需要一个主要的Spring Boot类来自动配置Spring Data并引导我们的应用程序：

```java
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

由于我们添加了h2依赖项，Spring Boot将使用内存中的H2数据库自动配置数据源。

## 4. 测试JPARepository

让我们添加一个JUnit测试来验证我们的Repository方法：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class VavrRepositoryIntegrationTest {

	@Autowired
	private VavrUserRepository userRepository;

	@Before
	public void setup() {
		User user1 = new User();
		user1.setName("John");
		User user2 = new User();
		user2.setName("John");

		userRepository.save(user1);
		userRepository.save(user2);
	}

	@Test
	public void whenAddUsers_thenGetUsers() {
		Option<User> user = userRepository.findById(1L);
		assertFalse(user.isEmpty());
		assertTrue(user.get().getName().equals("John"));

		Seq<User> users = userRepository.findByName("John");
		assertEquals(2, users.size());
	}
}
```

在上面的测试中，我们首先将两条用户记录添加到数据库中，然后调用Repository的查询方法。如你所见，这些方法返回正确的Vavr对象。

## 5. 总结

在这个快速示例中，我们展示了如何使用Vavr类型定义Spring Data Repository。