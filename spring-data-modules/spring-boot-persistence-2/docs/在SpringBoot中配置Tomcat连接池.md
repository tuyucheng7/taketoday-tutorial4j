## 1. 概述

[Spring Boot](https://spring.io/projects/spring-boot)是一个自以为是但功能强大的抽象层，构建于普通Spring平台之上，这使得开发独立应用程序和Web应用程序变得轻而易举。Spring Boot提供了一些方便的“启动器”依赖项，旨在以最小的占用空间运行和测试Java应用程序。

**这些启动器依赖项的一个关键组件是[spring-boot-starter-data-jpa](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.0.3)**，这允许我们使用[JPA](https://en.wikipedia.org/wiki/Java_Persistence_API)并通过使用一些流行的JDBC连接池实现(例如[HikariCP](https://github.com/brettwooldridge/HikariCP)和[Tomcat JDBC连接池](https://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html))来处理生产数据库。

在本教程中，**我们介绍如何在Spring Boot中配置Tomcat连接池**。

## 2. Maven依赖

Spring Boot使用HikariCP作为默认连接池，因为它具有卓越的性能和企业级功能。

**以下是Spring Boot自动配置连接池数据源的方式**：

1.  Spring Boot将在类路径中查找HikariCP，并在存在时默认使用它
2.  如果在类路径中找不到HikariCP，那么Spring Boot将选择Tomcat JDBC连接池(如果可用)
3.  如果这些选项都不可用，Spring Boot将选择[Apache Commons DBCP2](https://commons.apache.org/proper/commons-dbcp/)(如果可用)

要配置Tomcat JDBC连接池而不是默认的HikariCP，我们将**从spring-boot-starter-data-jpa依赖项中排除HikariCP，并将[tomcat-jdbc](https://search.maven.org/artifact/org.apache.tomcat/tomcat-jdbc/9.0.11/jar) Maven依赖项添加到我们的pom.xml中**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-jdbc</artifactId>
    <version>9.0.10</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.197</version>
    <scope>runtime</scope>
</dependency>
```

这种简单的方法允许让Spring Boot使用Tomcat连接池，而无需编写[@Configuration](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html)类并以编程方式定义[DataSource](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/javax/sql/DataSource.html) bean。

**还值得注意的是，在本例中，我们使用的是H2内存数据库。Spring Boot将为我们自动配置H2，而无需指定数据库URL、用户和密码**。

我们只需要在pom.xml文件中包含相应的依赖项，Spring Boot就会为我们完成剩下的工作。

或者，**可以跳过Spring Boot使用的连接池扫描算法，并使用“spring.datasource.type”属性在application.properties文件中显式指定连接池数据源**：

```properties
spring.datasource.type=org.apache.tomcat.jdbc.pool.DataSource
# other spring datasource properties
```

## 3. 使用application.properties文件调整连接池

**一旦我们在Spring Boot中成功配置了Tomcat连接池，我们很可能需要设置一些额外的属性，以优化其性能并满足某些特定要求**。

我们可以在application.properties文件中实现这一点：

```properties
spring.datasource.tomcat.initial-size=15
spring.datasource.tomcat.max-wait=20000
spring.datasource.tomcat.max-active=50
spring.datasource.tomcat.max-idle=15
spring.datasource.tomcat.min-idle=8
spring.datasource.tomcat.default-auto-commit=true
```

请注意，我们配置了一些额外的连接池属性，例如池的初始大小以及空闲连接的最大和最小数量。

我们还可以指定一些Hibernate特定的属性：

```properties
# Hibernate specific properties
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=update
spring.jpa.hibernate.naming-strategy=org.hibernate.cfg.ImprovedNamingStrategy
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.id.new_generator_mappings=false
```

## 4. 测试连接池

让我们编写一个简单的集成测试来检查Spring Boot是否正确配置了连接池：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootTomcatConnectionPoolIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void givenTomcatConnectionPoolInstance_whenCheckedPoolClassName_thenCorrect() {
        assertThat(dataSource.getClass().getName()).isEqualTo("org.apache.tomcat.jdbc.pool.DataSource");
    }
}
```

## 5. 命令行应用程序示例

设置好所有连接池管道后，我们构建一个简单的命令行应用程序。

在这样做的过程中，我们可以看到如何使用[Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)(以及传递依赖的Spring Boot)开箱即用的强大[DAO](https://www.baeldung.com/java-dao-pattern)层对H2数据库执行一些CRUD操作。

有关如何使用Spring Data JPA的详细指南，请查看[这篇](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)文章。

### 5.1 客户实体类

首先我们定义一个简单的Customer实体类：

```java
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "first_name")
    private String firstName;

    // standard constructors / getters / setters / toString
}
```

### 5.2 CustomerRepository接口

在这种情况下，我们只想对几个Customer实体执行CRUD操作。此外，我们需要获取与给定lastName匹配的所有客户。

因此，**我们所要做的就是扩展Spring Data JPA的[CrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)接口并定义一个自定义的方法**：

```java
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    List<Customer> findByLastName(String lastName);
}
```

现在我们可以轻松地通过lastName获取Customer实体。

### 5.3 CommandLineRunner实现

最后，我们至少需要在数据库中持久化一些Customer实体，并**验证我们的Tomcat连接池是否实际有效**。

让我们创建Spring Boot的CommandLineRunner接口的实现，Spring Boot将在启动应用程序之前引导实现：

```java
public class CommandLineCrudRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineCrudRunner.class);

    @Autowired
    private final CustomerRepository repository;

    public void run(String... args) throws Exception {
        repository.save(new Customer("John", "Doe"));
        repository.save(new Customer("Jennifer", "Wilson"));

        logger.info("Customers found with findAll():");
        repository.findAll().forEach(c -> logger.info(c.toString()));

        logger.info("Customer found with findById(1L):");
        Customer customer = repository.findById(1L)
              .orElseGet(() -> new Customer("Non-existing customer", ""));
        logger.info(customer.toString());

        logger.info("Customer found with findByLastName('Wilson'):");
        repository.findByLastName("Wilson").forEach(c -> {
            logger.info(c.toString());
        });
    }
}
```

简而言之，CommandLineCrudRunner类首先在数据库中保存几个Customer实体。接下来，它使用findById()方法获取第一个。最后，它使用findByLastName()方法检索客户。

### 5.4 运行Spring Boot应用程序

当然，我们需要做的最后一件事就是运行示例应用程序，然后我们可以看到Spring Boot/Tomcat连接池的串联运行：

```java
@SpringBootApplication
public class SpringBootConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootConsoleApplication.class);
    }
}
```

## 6. 总结

在本教程中，我们学习了如何在Spring Boot中配置和使用Tomcat连接池。此外，我们开发了一个基本的命令行应用程序来演示Spring Boot、Tomcat连接池和H2数据库的使用。