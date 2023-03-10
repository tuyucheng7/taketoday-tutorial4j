## 1. 概述

Spring Data JPA提供了一种创建数据库查询并使用嵌入式H2数据库测试它们的简单方法。

但在某些情况下，**在真实数据库上进行测试更有利可图**，特别是如果我们使用依赖于提供程序的查询。

在本教程中，我们将演示**如何使用[Testcontainer](https://www.baeldung.com/docker-test-containers)与Spring Data JPA和PostgreSQL数据库进行集成测试**。

在我们之前的教程中，我们主要使用[@Query](https://www.baeldung.com/spring-data-jpa-query)注解创建了一些数据库查询，我们现在将对其进行测试。

## 2. 配置

要在我们的测试中使用PostgreSQL数据库，**我们必须添加具有测试范围的[Testcontainers](https://central.sonatype.com/artifact/org.testcontainers/postgresql/1.17.6)依赖项**：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.17.3</version>
    <scope>test</scope>
</dependency>
```

我们还在测试资源目录下创建一个application.properties文件，我们在其中指示Spring使用正确的驱动程序类并在每次测试运行时创建模式：

```properties
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
spring.jpa.hibernate.ddl-auto=create
```

## 3. 单次测试用法

要在单个测试类中开始使用PostgreSQL实例，我们必须先创建容器定义，然后使用其参数建立连接：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {UserRepositoryTCIntegrationTest.Initializer.class})
public class UserRepositoryTCIntegrationTest extends UserRepositoryCommonIntegrationTests {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                  "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                  "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                  "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
```

在上面的示例中，我们使用JUnit中的@ClassRule**在执行测试方法之前**设置数据库容器。我们还创建了一个实现ApplicationContextInitializer的静态内部类。作为最后一步，我们将@ContextConfiguration注解应用于我们的测试类，并将Initializer类作为参数。

**通过执行这三个操作，我们可以在发布Spring上下文之前设置连接属性**。

现在让我们使用上一篇文章中的两个UPDATE查询：

```java
@Modifying
@Query("update User u set u.status = :status where u.name = :name")
int updateUserSetStatusForName(@Param("status") Integer status, @Param("name") String name);

@Modifying
@Query(value = "UPDATE Users u SET u.status = ? WHERE u.name = ?", nativeQuery = true)
int updateUserSetStatusForNameNative(Integer status, String name);
```

并使用配置的环境测试它们：

```java
@Test
@Transactional
public void givenUsersInDB_WhenUpdateStatusForNameModifyingQueryAnnotationJPQL_ThenModifyMatchingUsers(){
    insertUsers();
    int updatedUsersSize = userRepository.updateUserSetStatusForName(0, "SAMPLE");
    assertThat(updatedUsersSize).isEqualTo(2);
}

@Test
@Transactional
public void givenUsersInDB_WhenUpdateStatusForNameModifyingQueryAnnotationNative_ThenModifyMatchingUsers(){
    insertUsers();
    int updatedUsersSize = userRepository.updateUserSetStatusForNameNative(0, "SAMPLE");
    assertThat(updatedUsersSize).isEqualTo(2);
}

private void insertUsers() {
    userRepository.save(new User("SAMPLE", "email@example.com", 1));
    userRepository.save(new User("SAMPLE1", "email2@example.com", 1));
    userRepository.save(new User("SAMPLE", "email3@example.com", 1));
    userRepository.save(new User("SAMPLE3", "email4@example.com", 1));
    userRepository.flush();
}
```

在上述情况下，第一个测试以成功结束，但第二个测试抛出InvalidDataAccessResourceUsageException并显示以下消息：

```shell
Caused by: org.postgresql.util.PSQLException: ERROR: column "u" of relation "users" does not exist
```

如果我们使用H2嵌入式数据库运行相同的测试，两个测试都会成功完成，但PostgreSQL不接受SET子句中的别名。我们可以通过删除有问题的别名来快速修复查询：

```java
@Modifying
@Query(value = "UPDATE Users u SET status = ? WHERE u.name = ?", nativeQuery = true)
int updateUserSetStatusForNameNative(Integer status, String name);
```

这次两个测试都成功完成。在此示例中，**我们使用Testcontainer来识别原生查询的问题，否则在切换到生产环境中的真实数据库后会发现该问题**。我们还应该注意到，使用JPQL查询通常更安全，因为Spring会根据所使用的数据库提供程序正确地转换它们。

### 3.1 每个测试一个数据库

到目前为止，我们已经使用JUnit 4 Rule在测试类中运行所有测试之前启动数据库实例。最终，这种方法将在每个测试类之前创建一个数据库实例，并在每个类中运行所有测试后将其拆除。

**这种方法在测试实例之间创建了最大程度的隔离**。此外，多次启动数据库的开销会使测试变慢。

除了JUnit 4 Rule方法之外，**我们还可以[修改JDBC URL](https://www.testcontainers.org/modules/databases/jdbc/)并指示测试容器为每个测试类创建一个数据库实例**。这种方法不需要我们在测试中编写一些基础代码就可以工作。

例如，为了重写上面的例子，我们所要做的就是将它添加到我们的application.properties中：

```properties
spring.datasource.url=jdbc:tc:postgresql:11.1:///integration-tests-db
```

“tc:”将使Testcontainer实例化数据库实例而无需更改任何代码。因此，我们的测试类会很简单：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserRepositoryTCJdbcLiveTest extends UserRepositoryCommon {

    @Test
    @Transactional
    public void givenUsersInDB_WhenUpdateStatusForNameModifyingQueryAnnotationNative_ThenModifyMatchingUsers() {
        // same as above
    }
}
```

如果我们要为每个测试类创建一个数据库实例，那么这种方法是首选方法。

## 4. 共享数据库实例

在上一节中，我们描述了如何在单个测试中使用Testcontainer。在真实案例场景中，由于启动时间相对较长，我们希望在多个测试中复用同一个数据库容器。

现在让我们通过扩展PostgreSQLContainer并覆盖start()和stop()方法来创建一个用于创建数据库容器的通用类：

```java
public class TuyuchengPostgresqlContainer extends PostgreSQLContainer<TuyuchengPostgresqlContainer> {
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static TuyuchengPostgresqlContainer container;

    private TuyuchengPostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    public static TuyuchengPostgresqlContainer getInstance() {
        if (container == null) {
            container = new TuyuchengPostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        // do nothing, JVM handles shut down
    }
}
```

通过将stop()方法留空，我们允许JVM处理容器关闭。我们还实现了一个简单的单例模式，其中只有第一个测试触发容器启动，而每个后续测试都使用现有实例。在start()方法中，我们使用System#setProperty将连接参数设置为环境变量。

现在我们可以将它们放在我们的application.properties文件中：

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

现在让我们在测试定义中使用我们的实用程序类：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTCAutoIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = TuyuchengPostgresqlContainer.getInstance();

    // tests
}
```

与前面的示例一样，我们将@ClassRule注解应用于保存容器定义的字段。这样，在创建Spring上下文之前，DataSource连接属性将使用正确的值填充。

**现在，我们只需定义一个用我们的TuyuchengPostgresqlContainer实用程序类实例化的带@ClassRule注解的字段，就可以使用同一个数据库实例实现多个测试**。

## 5. 总结

在本文中，我们说明了使用Testcontainer对真实数据库实例执行测试的方法。

我们查看了单个测试用法的示例，使用Spring的ApplicationContextInitializer机制，以及实现一个用于可重用数据库实例化的类。

我们还展示了Testcontainers如何帮助识别跨多个数据库提供商的兼容性问题，尤其是对于原生查询。