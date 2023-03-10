## 1. 概述

在测试依赖于持久层(例如JPA)的Spring应用程序时，我们可能希望设置一个测试数据源以使用与我们用于运行应用程序的数据库不同的更小、更快的数据库，以便更轻松地运行我们的测试。

在Spring中配置数据源需要定义一个DataSource类型的bean。我们可以手动执行此操作，或者如果使用Spring Boot，则通过标准应用程序属性执行此操作。

在本快速教程中，我们将学习几种**配置单独数据源以在Spring中进行测试的方法**。

## 2. Maven依赖

我们将使用Spring JPA和测试创建一个Spring Boot应用程序，因此我们需要以下依赖项：

```xml
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

可以从Maven Central下载最新版本的[spring-boot-starter-data-jpa](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.0.4)和[spring-boot-starter-test](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-test/3.0.3)。

现在让我们看一下配置DataSource进行测试的几种不同方法。

## 3. 在Spring Boot中使用标准属性文件

Spring Boot在运行应用程序时自动获取的标准属性文件称为application.properties。它位于src/main/resources文件夹中。

如果我们想为测试使用不同的属性，我们可以通过在src/test/resources中放置另一个同名文件来覆盖main文件夹中的属性文件。

src/test/resources文件夹中的application.properties文件应包含配置数据源所需的标准键值对。这些属性以spring.datasource为前缀。

例如，让我们配置一个H2内存数据库作为测试的数据源：

```properties
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
```

Spring Boot将使用这些属性来自动配置DataSource bean。

让我们使用Spring JPA定义一个非常简单的GenericEntity和Repository：

```java
@Entity
public class GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String value;

    // standard constructors, getters, setters
}
```

```java
public interface GenericEntityRepository extends JpaRepository<GenericEntity, Long> { }
```

接下来，让我们为Repository编写一个JUnit测试。为了使Spring Boot应用程序中的测试能够获取我们定义的标准数据源属性，我们必须使用@SpringBootTest对其进行标注：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SpringBootJPAIntegrationTest {

    @Autowired
    private GenericEntityRepository genericEntityRepository;

    @Test
    public void givenGenericEntityRepository_whenSaveAndRetreiveEntity_thenOK() {
        GenericEntity genericEntity = genericEntityRepository
              .save(new GenericEntity("test"));
        GenericEntity foundEntity = genericEntityRepository
              .findOne(genericEntity.getId());

        assertNotNull(foundEntity);
        assertEquals(genericEntity.getValue(), foundEntity.getValue());
    }
}
```

## 4. 使用自定义属性文件

如果我们不想使用标准的application.properties文件和属性键，或者如果我们不使用Spring Boot，我们可以定义一个带有自定义属性键的自定义.properties文件，然后在@Configuration类中读取这个文件来创建基于它包含的值的DataSource bean。

该文件将放置在应用程序正常运行模式下的src/main/resources文件夹中，并放置在src/test/resources中以供测试使用。

让我们创建一个名为persistence-generic-entity.properties的文件，它使用H2内存数据库进行测试，并将其放在src/test/resources文件夹中：

```properties
jdbc.driverClassName=org.h2.Driver
jdbc.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
jdbc.username=sa
jdbc.password=sa
```

接下来，我们可以在@Configuration类中基于这些属性定义DataSource bean，该类将我们的persistence-generic-entity.properties作为属性源加载：

```java
@Configuration
@EnableJpaRepositories(basePackages = "cn.tuyucheng.taketoday.repository")
@PropertySource("persistence-generic-entity.properties")
@EnableTransactionManagement
public class H2JpaConfig {
    // ...
}
```

有关此配置的更详细示例，我们可以阅读我们之前关于[使用内存数据库进行自包含测试](https://www.baeldung.com/spring-jpa-test-in-memory-database)的文章中的“JPA配置”部分。

然后我们可以创建一个类似于前一个的JUnit测试，除了它会加载我们的配置类：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, H2JpaConfig.class})
public class SpringBootH2IntegrationTest {
    // ...
}
```

## 5. 使用Spring Profile

我们可以配置单独的DataSource进行测试的另一种方法是利用Spring Profiles来定义仅在test Profile中可用的DataSource bean。

为此，我们可以像以前一样使用.properties文件，或者我们可以在类本身中写入值。

让我们在将由我们的测试加载的@Configuration类中为test Profile定义一个DataSource bean：

```java
@Configuration
@EnableJpaRepositories(basePackages = {
      "cn.tuyucheng.taketdoay.repository",
      "cn.tuyucheng.taketdoay.boot.repository"
})
@EnableTransactionManagement
public class H2TestProfileJPAConfig {

    @Bean
    @Profile("test")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");

        return dataSource;
    }

    // configure entityManagerFactory
    // configure transactionManager
    // configure additional Hibernate properties
}
```

然后，在JUnit测试类中，我们需要通过添加@ActiveProfiles注解来指定我们要使用test Profile：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
      Application.class,
      H2TestProfileJPAConfig.class})
@ActiveProfiles("test")
public class SpringBootProfileIntegrationTest {
    // ...
}
```

## 6. 总结

在这篇简短的文章中，我们探讨了几种配置单独数据源以在Spring中进行测试的方法。