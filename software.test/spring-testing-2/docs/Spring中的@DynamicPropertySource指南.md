## 1. 概述

如今的应用程序并不是孤立存在的：我们通常需要连接到各种外部组件，例如Mysql、MongoDB、PostgreSQL、Kafka、Cassandra、Redis和其他外部API。

在本教程中，我们将了解Spring Framework 5.2.5如何通过引入动态属性来促进测试此类应用程序。

首先，我们将从定义问题开始，看看我们过去是如何以不太理想的方式解决问题的。
然后，我们将介绍@DynamicPropertySource注解，看看它如何为同一问题提供更好的解决方案。
最后，我们还将看一下测试框架中的另一种解决方案，它与纯Spring解决方案相比可能更胜一筹。

## 2. 问题：动态属性

假设我们正在开发一个使用PostgreSQL作为其数据库的典型应用程序。我们将从一个简单的JPA实体开始：

```java

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private String content;

    // getters and setters ...
}
```

为了确保这个实体类按预期工作，我们应该为它编写一个测试来验证它与数据库的交互。
由于这个测试需要与一个真实的数据库对话，我们应该事先启动一个PostgreSQL实例。

**在测试执行期间有不同的方法来设置这样的基础设施工具**。事实上，此类解决方案主要分为三类：

+ 为测试设置单独的数据库服务器。
+ 使用一些轻量级、特定于测试的内存数据库，例如H2。
+ 让测试自己管理数据库的生命周期。

由于我们不应该区分我们的测试和生产环境，因此与使用H2等测试替身相比，有更好的选择。
**第三个方法，除了使用真实数据库之外，还为测试提供了更好的隔离**。此外，借助Docker和Testcontainers等技术，很容易实现第三种方法。

如果我们使用像Testcontainers这样的技术，我们的测试工作流程如下所示：

1. 在所有测试执行之前设置一个组件，例如PostgreSQL。通常，这些组件监听随机端口。
2. 运行测试。
3. 销毁组件。

**如果我们的PostgreSQL容器每次都要监听一个随机端口，那么我们应该以某种方式动态设置和更改spring.datasource.url配置属性**。
基本上，每个测试都应该有自己的配置属性版本。

当配置是静态的时，我们可以使用Spring Boot的配置管理工具轻松管理它们。然而，当我们面临动态配置时，同样的任务可能具有挑战性。

现在我们知道了问题所在，让我们看看它的传统解决方案。

## 3. 传统解决方案

**实现动态属性的第一种方法是使用自定义ApplicationContextInitializer**。
基本上，我们首先设置我们的基础设施，并使用第一步中的信息来自定义ApplicationContext：

```java

@SpringBootTest
@Testcontainers
@ActiveProfiles("pg")
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
            TestPropertyValues.of(String.format(
                            "spring.datasource.url=jdbc:postgresql://localhost:%d/prop", postgres.getFirstMappedPort()),
                    "spring.datasource.username=postgres",
                    "spring.datasource.password=pass"
            ).applyTo(applicationContext);
        }
    }
}
```

让我们来看看这个有点复杂的设置。JUnit将首先创建并启动容器。
容器准备好后，Spring Extension将调用initializer将动态配置应用到Spring环境。显然，这种方法有点冗长和复杂。

只有经过这些步骤，我们才能编写我们的测试：

```java
class ArticleTraditionalLiveTest {
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
}
```

