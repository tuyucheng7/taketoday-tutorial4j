## 1. 概述

[Spring Boot](https://www.baeldung.com/spring-boot)使用一种自以为是的算法来扫描和配置[DataSource](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/javax/sql/DataSource.html)。这使我们能够在默认情况下轻松获得完全配置的DataSource实现。

此外，Spring Boot会自动配置一个快如闪电的[连接池](https://www.baeldung.com/java-connection-pooling)，即[HikariCP](https://www.baeldung.com/hikaricp)、[Apache Tomcat](https://www.baeldung.com/spring-boot-tomcat-connection-pool)或[Commons DBCP](https://commons.apache.org/proper/commons-dbcp/)，顺序取决于类路径中的连接池。

**虽然Spring Boot的自动DataSource配置在大多数情况下工作得很好，但有时我们需要更高级别的控制**，所以我们必须设置我们自己的DataSource实现，从而跳过自动配置过程。

在本教程中，我们将学习如何在Spring Boot中以编程方式配置数据源。

## 延伸阅读

### [Spring JPA多数据库](https://www.baeldung.com/spring-data-jpa-multiple-databases)

如何设置Spring Data JPA以使用多个独立的数据库。

[阅读更多](https://www.baeldung.com/spring-data-jpa-multiple-databases)→

### [为测试配置单独的Spring DataSource](https://www.baeldung.com/spring-testing-separate-data-source)

一个快速、实用的教程，介绍如何配置单独的数据源以在Spring应用程序中进行测试。

[阅读更多](https://www.baeldung.com/spring-testing-separate-data-source)→

## 2. Maven依赖

**总体而言，以编程方式创建DataSource实现非常简单**。

为了了解如何实现这一点，我们将实现一个简单的Repository层，它将对一些[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)实体执行CRUD操作。

让我们看一下我们的演示项目的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.4.1</version> 
    <scope>runtime</scope> 
</dependency>
```

如上所示，我们将使用内存中的[H2数据库](https://central.sonatype.com/artifact/com.h2database/h2/2.1.212)实例来练习Repository层。这样，我们将能够测试以编程方式配置的数据源，而无需执行昂贵的数据库操作。

此外，让我们确保在Maven Central上检查最新版本的[spring-boot-starter-data-jpa](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.0.3)。

## 3. 以编程方式配置数据源

现在，如果我们坚持使用Spring Boot的自动数据源配置并在当前状态下运行我们的项目，它将按预期工作。

**Spring Boot将为我们完成所有繁重的基础设施工作**。这包括创建一个H2数据源实现，它将由HikariCP、Apache Tomcat或Commons DBCP自动处理，并设置一个内存数据库实例。

此外，我们甚至不需要创建application.properties文件，因为Spring Boot也会提供一些默认的数据库设置。

正如我们之前提到的，有时我们需要更高级别的自定义，因此我们必须以编程方式配置我们自己的DataSource实现。

**完成此操作的最简单方法是定义一个DataSource工厂方法，并将其放置在一个使用[@Configuration](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html)注解进行标注的类中**：

```java
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:test");
        dataSourceBuilder.username("SA");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }
}
```

在这种情况下，**我们使用方便的[DataSourceBuilder](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/jdbc/DataSourceBuilder.html)类([Joshua Bloch的构建器模式](https://www.pearson.com/us/higher-education/program/Bloch-Effective-Java-3rd-Edition/PGM1763855.html)的非流式版本)以编程方式创建我们的自定义DataSource对象**。

这种方法非常好，因为构建器使使用一些通用属性配置数据源变得容易。它还使用底层连接池。

## 4. 使用application.properties文件外部化数据源配置

当然，也可以部分外部化我们的DataSource配置。例如，我们可以在工厂方法中定义一些基本的DataSource属性：

```java
@Bean 
public DataSource getDataSource() { 
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create(); 
    dataSourceBuilder.username("SA"); 
    dataSourceBuilder.password(""); 
    return dataSourceBuilder.build(); 
}
```

然后我们可以在application.properties文件中指定一些额外的：

```properties
spring.datasource.url=jdbc:h2:mem:test
spring.datasource.driver-class-name=org.h2.Driver
```

**在外部源中定义的属性，例如上面的application.properties文件，或通过使用[@ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot)标注的类，将覆盖Java API中定义的属性**。

很明显，通过这种方法，我们将不再将数据源配置设置存储在一个位置。

另一方面，它允许我们将编译时和运行时配置设置很好地彼此分开。

这真的很好，因为它允许我们轻松设置配置绑定点。这样我们就可以包含来自其他来源的不同DataSource设置，而不必重构我们的bean工厂方法。

## 5. 测试数据源配置

测试我们的自定义数据源配置非常简单。整个过程归结为创建[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)实体、定义基本Repository接口和测试Repository层。

### 5.1 创建JPA实体

让我们从定义示例JPA实体类开始，该类将对用户进行建模：

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String email;

    // standard constructors / setters / getters / toString
}
```

### 5.2 一个简单的Repository层

接下来我们需要实现一个基本的Repository层，它允许我们对上面定义的User实体类的实例执行CRUD操作。

由于我们使用的是[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)，因此我们不必从头开始创建我们自己的[DAO实现](https://www.baeldung.com/java-dao-pattern)。我们只需要扩展[CrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)接口即可获得一个有效的Repository实现：

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {}
```

### 5.3 测试Repository层

最后，我们需要检查我们以编程方式配置的DataSource是否实际工作。我们可以通过集成测试轻松完成此操作：

```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenCalledSave_thenCorrectNumberOfUsers() {
        userRepository.save(new User("Bob", "bob@domain.com"));
        List<User> users = (List<User>) userRepository.findAll();

        assertThat(users.size()).isEqualTo(1);
    }
}
```

UserRepositoryIntegrationTest类是不言自明的。它只是使用两个Repository接口的CRUD方法来持久化和查找实体。

**请注意，无论我们决定以编程方式配置DataSource实现，还是将其拆分为Java配置方法和application.properties文件，我们都应该始终获得一个有效的数据库连接**。

### 5.4 运行示例应用程序

最后，我们可以使用标准的main()方法运行我们的演示应用程序：

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner run(UserRepository userRepository) throws Exception {
        return (String[] args) -> {
            User user1 = new User("John", "john@domain.com");
            User user2 = new User("Julie", "julie@domain.com");
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.findAll().forEach(user -> System.out.println(user);
        };
    }
}
```

我们已经测试了Repository层，因此我们确信我们的数据源已经配置成功。因此，如果我们运行示例应用程序，我们应该在控制台输出中看到存储在数据库中的用户实体列表。

## 6. 总结

在本文中，**我们学习了如何在Spring Boot中以编程方式配置DataSource实现**。