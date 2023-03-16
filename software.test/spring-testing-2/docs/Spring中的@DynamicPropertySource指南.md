## 1. 概述

如今的应用程序并不是孤立存在的：我们通常需要连接到各种外部组件，例如PostgreSQL、Apache Kafka、Cassandra、Redis和其他外部API。

在本教程中，我们将了解Spring框架5.2.5如何通过引入[动态属性](https://github.com/spring-projects/spring-framework/issues/24540)来促进测试此类应用程序。

首先，我们将从问题定义开始，看看我们过去是如何以不太理想的方式解决问题的。然后，我们将介绍[@DynamicPropertySource](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/context/DynamicPropertySource.html)注解，看看它如何为同一问题提供更好的解决方案。最后，我们还将看一下来自测试框架的另一种解决方案，它与纯Spring解决方案相比更胜一筹。

## 2. 问题：动态属性

假设我们正在开发一个使用PostgreSQL作为其数据库的典型应用程序。我们将从一个简单的[JPA实体](https://www.baeldung.com/jpa-entities)开始：

```java
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private String content;

    // getters and setters
}
```

为了确保这个实体按预期工作，我们应该为它编写一个测试来验证它的数据库交互。由于此测试需要与真实数据库通信，因此我们应该事先设置一个PostgreSQL实例。

**在测试执行期间有不同的方法来设置此类基础设施工具**。事实上，此类解决方案主要分为三类：

+ 为测试设置单独的数据库服务器
+ 使用一些轻量级、特定于测试的内存数据库，例如H2
+ 让测试自己管理数据库的生命周期

由于我们不应该区分测试环境和生产环境，因此与使用[H2等测试替身](https://monaa.dev/posts/mocks-considered-harmful/)相比，有更好的替代方案。**第三种选择，除了使用真实数据库外，还为测试提供了更好的隔离**。此外，借助Docker和[Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)等技术，很容易实现第三种选择。

如果我们使用Testcontainers等技术，我们的测试工作流程将如下所示：

1. 在所有测试执行之前设置一个组件，例如PostgreSQL。通常，这些组件监听随机端口
2. 运行测试
3. 销毁组件

**如果我们的PostgreSQL容器每次都要监听一个随机端口，那么我们应该以某种方式动态设置和更改spring.datasource.url配置属性**。基本上，每个测试都应该有自己的配置属性版本。

当配置是静态的时，我们可以使用Spring Boot的[配置管理工具](https://www.baeldung.com/properties-with-spring)轻松地管理它们。然而，当我们面对动态配置时，同样的任务可能具有挑战性。

现在我们知道了问题所在，让我们看看它的传统解决方案。

## 3. 传统方案

**实现动态属性的第一种方法是使用自定义[ApplicationContextInitializer](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/ApplicationContextInitializer.html)**。基本上，我们首先设置我们的基础设施并使用第一步中的信息来自定义[ApplicationContext](https://www.baeldung.com/spring-application-context)：

```java
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = ArticleTraditionalLiveTest.EnvInitializer.class)
class ArticleTraditionalLiveTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11")
          .withDatabaseName("prop")
          .withUsername("postgres")
          .withPassword("pass")
          .withExposedPorts(5432);

    static class EnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                  String.format("spring.datasource.url=jdbc:postgresql://localhost:%d/prop", postgres.getFirstMappedPort()),
                  "spring.datasource.username=postgres",
                  "spring.datasource.password=pass"
            ).applyTo(applicationContext);
        }
    }

    // omitted 
}
```

让我们来看看这个有点复杂的设置。JUnit将首先创建并启动容器。容器准备就绪后，Spring扩展将调用EnvInitializer以将动态配置应用到Spring[Environment](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/Environment.html)。**显然，这种方法有点冗长和复杂**。

只有在这些步骤之后，我们才能编写我们的测试：

```java
@Autowired
private ArticleRepository articleRepository;

@Test
void givenAnArticle_whenPersisted_thenShouldBeAbleToReadIt() {
    Article article = new Article();
    article.setTitle("A Guide to @DynamicPropertySource in Spring");
    article.setContent("Today's applications...");

    articleRepository.save(article);

    Article persisted = articleRepository.findAll().get(0);
    assertThat(persisted.getId()).isNotNull();
    assertThat(persisted.getTitle()).isEqualTo("A Guide to @DynamicPropertySource in Spring");
    assertThat(persisted.getContent()).isEqualTo("Today's applications...");
}
```

## 4. @DynamicPropertySource

**Spring框架5.2.5引入了@DynamicPropertySource注解以方便添加具有动态值的属性**。我们所要做的就是创建一个用@DynamicPropertySource标注的静态方法，并且只有一个[DynamicPropertyRegistry](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/context/DynamicPropertyRegistry.html)实例作为输入：

```java
@SpringBootTest
@Testcontainers
public class ArticleLiveTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11")
          .withDatabaseName("prop")
          .withUsername("postgres")
          .withPassword("pass")
          .withExposedPorts(5432);

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
              () -> String.format("jdbc:postgresql://localhost:%d/prop", postgres.getFirstMappedPort()));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "pass");
    }

    // tests are same as before
}
```

如上所示，我们在给定的DynamicPropertyRegistry上使用[add(String, Supplier)](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/context/DynamicPropertyRegistry.html#add-java.lang.String-java.util.function.Supplier-)方法向Spring Environment添加一些属性。与我们之前看到的EnvInitializer相比，这种方法要简洁得多。**请注意，使用@DynamicPropertySource标注的方法必须声明为静态的，并且必须只能接收一个类型为DynamicPropertyRegistry的参数**。

基本上，@DynamicPropertySource注解背后的主要动机是更轻松地促进已经成为可能的事情。虽然它最初设计为与Testcontainers一起使用，但我们可以在需要使用动态配置的任何地方使用它。

## 5. 替代方案：测试夹具

到目前为止，在这两种方法中，**夹具设置和测试代码都紧密地交织在一起**。有时，两个关注点的这种紧密耦合会使测试代码复杂化，尤其是当我们要设置多个内容时。想象一下，如果我们在单个测试中使用PostgreSQL和Apache Kafka，基础设施设置会是什么样子。

除此之外，**基础架构设置和应用动态配置将在所有需要它们的测试中重复**。

为了避免这些缺点，**我们可以使用大多数测试框架提供的测试夹具工具**。例如，在JUnit 5中，我们可以定义一个[Extension](https://www.baeldung.com/junit-5-extensions)，该Extension在测试类中的所有测试之前启动PostgreSQL实例，配置Spring Boot，并在运行测试后停止PostgreSQL实例：

```java
public class PostgreSQLExtension implements BeforeAllCallback, AfterAllCallback {

    private PostgreSQLContainer<?> postgres;

    @Override
    public void beforeAll(ExtensionContext context) {
        postgres = new PostgreSQLContainer<>("postgres:11")
              .withDatabaseName("prop")
              .withUsername("postgres")
              .withPassword("pass")
              .withExposedPorts(5432);

        postgres.start();
        String jdbcUrl = String.format("jdbc:postgresql://localhost:%d/prop", postgres.getFirstMappedPort());
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", "postgres");
        System.setProperty("spring.datasource.password", "pass");
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // do nothing, Testcontainers handles container shutdown
    }
}
```

在这里，我们实现了[AfterAllCallback](https://junit.org/junit5/docs/5.1.1/api/org/junit/jupiter/api/extension/AfterAllCallback.html)和[BeforeAllCallback](https://junit.org/junit5/docs/5.1.1/api/org/junit/jupiter/api/extension/BeforeAllCallback.html)来创建JUnit 5扩展。这样，JUnit 5将在运行所有测试之前执行beforeAll()逻辑，并在运行测试之后执行afterAll()方法中的逻辑。使用这种方法，我们的测试代码将为：

```java
@SpringBootTest
@ExtendWith(PostgreSQLExtension.class)
@DirtiesContext
public class ArticleTestFixtureLiveTest {
    // just the test code
}
```

在这里，我们还将[@DirtiesContext](https://www.baeldung.com/spring-dirtiescontext)注解添加到测试类中。重要的是，**这会重新创建应用程序上下文，并允许我们的测试类与运行在随机端口上的单独PostgreSQL实例进行交互**。因此，这将针对单独的数据库实例在彼此完全隔离的情况下执行我们的测试。

除了更具可读性之外，我们还可以通过添加@ExtendWith(PostgreSQLExtension.class)注解轻松地重用相同的功能。无需像我们在其他两种方法中所做的那样，将整个PostgreSQL设置复制粘贴到我们需要的任何地方。

## 6. 总结

在本教程中，我们首先看到了测试依赖于数据库之类的Spring组件有多么困难。然后，我们针对这个问题引入了三个解决方案，每个解决方案都改进了之前的解决方案所提供的功能。