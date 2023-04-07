## 一、简介

**在本文中，我们将全面讨论使用 Spring 的集成测试以及如何优化它们。**

首先，我们将简要讨论集成测试的重要性及其在以 Spring 生态系统为中心的现代软件中的位置。

稍后，我们将介绍多个场景，重点放在网络应用程序上。

**接下来，我们将讨论一些提高测试速度的策略**，通过了解可能影响我们塑造测试的方式和塑造应用程序本身的方式的不同方法。

在开始之前，请务必记住这是一篇基于经验的评论文章。有些东西可能适合你，有些可能不适合。

最后，本文使用 Kotlin 作为代码示例以使其尽可能简洁，但这些概念并不特定于这种语言，代码片段应该对 Java 和 Kotlin 开发人员都有意义。

## 2. 集成测试

**集成测试是自动化测试套件的基本组成部分。**[尽管如果我们遵循健康的测试金字塔，](https://martinfowler.com/articles/practical-test-pyramid.html)它们不应该像单元测试那样多。依赖 Spring 等框架，我们需要进行大量的集成测试，以降低系统某些行为的风险。

**我们越是通过使用 Spring 模块（数据、安全、社交……）来简化我们的代码，对集成测试的需求就越大。** 当我们将基础设施的点点滴滴移动到 *@Configuration*类中时，这一点尤其正确。

我们不应该“测试框架”，但我们当然应该验证框架的配置是否满足我们的需求。

集成测试帮助我们建立信心，但它们是有代价的：

-   这是较慢的执行速度，这意味着较慢的构建
-   此外，集成测试意味着更广泛的测试范围，这在大多数情况下并不理想

考虑到这一点，我们将尝试找到一些解决方案来缓解上述问题。

## 3. 测试网络应用

Spring 带来了一些用于测试 Web 应用程序的选项，大多数 Spring 开发人员都熟悉它们，它们是：

-   [*MockMvc*](https://www.baeldung.com/integration-testing-in-spring)：模拟 servlet API，对非反应性网络应用程序很有用
-   [*TestRestTemplate*](https://www.baeldung.com/spring-boot-testresttemplate)：可用于指向我们的应用程序，对于不需要模拟 servlet 的非反应式 Web 应用程序很有用
-   [WebTestClient](https://www.baeldung.com/spring-5-webclient)：是用于响应式 Web 应用程序的测试工具，具有模拟请求/响应或命中真实服务器

由于我们已经有涵盖这些主题的文章，因此我们不会花时间谈论它们。

如果您想深入了解，请随时查看。

## 4.优化执行时间

集成测试很棒。他们给了我们很大的信心。此外，如果实施得当，他们可以以非常清晰的方式描述我们应用程序的意图，减少模拟和设置噪音。

然而，随着我们应用的成熟和开发的积累，构建时间不可避免地会增加。随着构建时间的增加，每次都运行所有测试可能变得不切实际。

此后，影响我们的反馈循环并开始最佳开发实践。

此外，集成测试本质上是昂贵的。启动某种持久性，通过发送请求（即使他们永远不会离开*localhost*），或者做一些 IO 只是需要时间。

**密切关注我们的构建时间（包括测试执行）至关重要。我们可以在 Spring 中应用一些技巧来保持低电平。**

在接下来的部分中，我们将介绍一些帮助我们优化构建时间的要点以及可能影响其速度的一些陷阱：

-   明智地使用配置文件——配置文件如何影响性能
-   重新考虑 *@MockBean——* 模拟如何影响性能
-   重构 *@MockBean——* 提高性能的替代方案
-   仔细思考@ *DirtiesContext——* 一个有用但危险的注解以及如何不使用它
-   使用测试切片——一个很酷的工具，可以帮助我们或让我们继续前进
-   使用类继承——一种以安全方式组织测试的方法
-   状态管理——避免不稳定测试的良好实践
-   重构到单元测试中——获得可靠且快速构建的最佳方式

让我们开始吧！

### 4.1. 明智地使用配置文件

[配置文件](https://www.baeldung.com/spring-profiles)是一个非常简洁的工具。即，可以启用或禁用我们应用程序某些区域的简单标签。我们甚至可以用它们[实现功能标志！](https://www.baeldung.com/spring-feature-flags)

随着我们的配置文件越来越丰富，在我们的集成测试中时不时地交换是很诱人的。有一些方便的工具可以做到这一点，比如*@ActiveProfiles*。但是，**每次我们使用新配置文件进行测试时，都会创建一个新的\*ApplicationContext 。\***

使用一个没有任何内容的普通 spring boot 应用程序，创建应用程序上下文可能会很快。添加一个 ORM 和一些模块，它会迅速飙升至 7 秒以上。

添加一堆配置文件，并通过一些测试分散它们，我们将快速获得 60 秒以上的构建（假设我们将测试作为构建的一部分运行——我们应该这样做）。

一旦我们面对一个足够复杂的应用程序，解决这个问题就会让人望而生畏。然而，如果我们提前仔细计划，保持合理的构建时间就变得微不足道了。

当涉及到集成测试中的配置文件时，我们可以记住一些技巧：

-   创建一个聚合配置文件，即*test*，包括所有需要的配置文件 - 坚持我们的测试配置文件无处不在
-   在设计我们的配置文件时考虑到可测试性。如果我们最终不得不切换配置文件，也许有更好的方法
-   在一个集中的地方声明我们的测试配置文件——我们稍后会讨论这个
-   避免测试所有配置文件组合。或者，我们可以在每个环境中使用一个 e2e 测试套件来测试具有该特定配置文件集的应用程序

### 4.2. *@MockBean*的问题 

*@MockBean*是一个非常强大的工具。

当我们需要一些 Spring 魔法但又想模拟特定组件时， *@MockBean* 就派上用场了。但这样做是有代价的。

**每次\*@MockBean\*出现在类中时，\*ApplicationContext\*缓存都会被标记为脏，因此运行器将在测试类完成后清理缓存。**这再次为我们的构建增加了额外的一串秒数。

这是一个有争议的问题，但尝试使用实际的应用程序而不是针对此特定场景进行模拟可能会有所帮助。当然，这里没有灵丹妙药。当我们不允许自己模拟依赖关系时，界限会变得模糊。

我们可能会想：当我们想要测试的只是我们的 REST 层时，我们为什么还要坚持？这是一个公平的观点，并且总是存在妥协。

然而，考虑到一些原则，这实际上可以转化为一种优势，可以更好地设计测试和我们的应用程序，并减少测试时间。

### 4.3. 重构 *@MockBean*

***在本节中，我们将尝试使用@MockBean\*重构一个“慢速”测试，以使其重用缓存的\*ApplicationContext\*。**

假设我们要测试创建用户的 POST。如果我们在模拟——使用*@MockBean*，我们可以简单地验证我们的服务是否被一个很好的序列化用户调用。

如果我们正确地测试了我们的服务，这种方法就足够了：

```java
class UsersControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var mvc: MockMvc
    
    @MockBean
    lateinit var userService: UserService

    @Test
    fun links() {
        mvc.perform(post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""{ "name":"jose" }"""))
          .andExpect(status().isCreated)
        
        verify(userService).save("jose")
    }
}

interface UserService {
    fun save(name: String)
}复制
```

不过我们想避免使用*@MockBean*。所以我们最终会持久化实体（假设这就是服务所做的）。

这里最天真的方法是测试副作用：POST 后，我的用户在我的数据库中，在我们的示例中，这将使用 JDBC。

然而，这违反了测试边界：

```java
@Test
fun links() {
    mvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content("""{ "name":"jose" }"""))
      .andExpect(status().isCreated)

    assertThat(
      JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"))
      .isOne()
}复制
```

在这个特定的示例中，我们违反了测试边界，因为我们将我们的应用程序视为一个 HTTP 黑盒来发送给用户，但后来我们使用实现细节进行断言，也就是说，我们的用户已保存在某个数据库中。

如果我们通过 HTTP 运行我们的应用程序，我们是否也可以通过 HTTP 断言结果？

```java
@Test
fun links() {
    mvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content("""{ "name":"jose" }"""))
      .andExpect(status().isCreated)

    mvc.perform(get("/users/jose"))
      .andExpect(status().isOk)
}复制
```

如果我们采用最后一种方法，则有一些优点：

-   我们的测试将启动得更快（可以说，执行可能需要一点点时间，但应该会有回报）
-   此外，我们的测试不知道与 HTTP 边界无关的副作用，即数据库
-   最后，我们的测试清楚地表达了系统的意图：如果你发布，你将能够获取用户

当然，由于各种原因，这可能并不总是可行：

-   我们可能没有“副作用”端点：这里的一个选项是考虑创建“测试端点”
-   复杂度太高，无法影响整个应用程序：这里的一个选择是考虑切片（我们稍后会讨论）

### 4.4. 仔细思考*@DirtiesContext*

有时，我们可能需要在测试中修改*ApplicationContext 。*对于这种情况， *@DirtiesContext*提供了该功能。

出于与上述相同的原因， *@DirtiesContext* 在执行时间方面是一种极其昂贵的资源，因此，我们应该小心。

***@DirtiesContext\* 的一些误用包括应用程序缓存重置或内存数据库重置。**在集成测试中有更好的方法来处理这些场景，我们将在后面的部分中介绍一些方法。

### 4.5. 使用测试切片

**测试切片是 1.4 中引入的 Spring Boot 特性。这个想法相当简单，Spring 将为您的应用程序的特定部分创建一个简化的应用程序上下文。**

此外，该框架将负责配置最低限度的配置。

Spring Boot 中有大量开箱即用的切片，我们也可以创建自己的切片：

-   *@JsonTest：* 注册JSON相关组件
-   *@DataJpaTest*：注册 JPA bean，包括可用的 ORM
-   *@JdbcTest*：对原始 JDBC 测试很有用，负责数据源和内存数据库，没有 ORM 装饰
-   *@DataMongoTest*：尝试提供内存中的 mongo 测试设置
-   *@WebMvcTest*：没有应用程序其余部分的模拟 MVC 测试切片
-   ......（我们可以检查[来源](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-test-autoconfigure/src/main/java/org/springframework/boot/test/autoconfigure)以找到它们）

如果使用得当，这个特殊功能可以帮助我们构建狭窄的测试，而不会在性能方面造成如此大的损失，特别是对于中小型应用程序。

但是，如果我们的应用程序不断增长，它也会堆积起来，因为它会为每个切片创建一个（小的）应用程序上下文。

### 4.6. 使用类继承

使用单个 *AbstractSpringIntegrationTest* 类作为我们所有集成测试的父类是一种保持快速构建的简单、强大且实用的方法。

**如果我们提供可靠的设置，我们的团队将简单地扩展它，知道一切都“正常工作”。**这样我们就可以更少地担心管理状态或配置框架，并专注于手头的问题。

我们可以在那里设置所有测试要求：

-   Spring 跑步者——或者最好是规则，以防我们以后需要其他跑步者
-   配置文件 - 理想情况下我们的综合 *测试* 配置文件
-   初始配置——设置我们应用程序的状态

让我们看一下一个简单的基类，它处理了前面的几点：

```java
@SpringBootTest
@ActiveProfiles("test")
abstract class AbstractSpringIntegrationTest {

    @Rule
    @JvmField
    val springMethodRule = SpringMethodRule()

    companion object {
        @ClassRule
        @JvmField
        val SPRING_CLASS_RULE = SpringClassRule()
    }
}复制
```

### 4.7. 状态管理

重要的是要记住单元测试中的“单元”[来自哪里](https://content.pivotal.io/blog/what-is-a-unit-test-the-answer-might-surprise-you)。简而言之，这意味着我们可以在任何时候运行单个测试（或子集）以获得一致的结果。

因此，在每次测试开始之前，状态应该是干净的并且是已知的。

换句话说，无论是单独执行还是与其他测试一起执行，测试的结果都应该是一致的。

这个想法同样适用于集成测试。在开始新测试之前，我们需要确保我们的应用程序具有已知（且可重复）的状态。我们重用越多组件来加快速度（应用程序上下文、数据库、队列、文件……），获得状态污染的机会就越大。

假设我们全力以赴地使用类继承，那么现在，我们有一个中心位置来管理状态。

让我们增强我们的抽象类，以确保我们的应用程序在运行测试之前处于已知状态。

在我们的示例中，我们假设有几个存储库（来自各种数据源）和一个*Wiremock*服务器：

```java
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8666)
@AutoConfigureMockMvc
abstract class AbstractSpringIntegrationTest {

    //... spring rules are configured here, skipped for clarity

    @Autowired
    protected lateinit var wireMockServer: WireMockServer

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var repos: Set<MongoRepository<*, *>>

    @Autowired
    lateinit var cacheManager: CacheManager

    @Before
    fun resetState() {
        cleanAllDatabases()
        cleanAllCaches()
        resetWiremockStatus()
    }

    fun cleanAllDatabases() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "table1", "table2")
        jdbcTemplate.update("ALTER TABLE table1 ALTER COLUMN id RESTART WITH 1")
        repos.forEach { it.deleteAll() }
    }

    fun cleanAllCaches() {
        cacheManager.cacheNames
          .map { cacheManager.getCache(it) }
          .filterNotNull()
          .forEach { it.clear() }
    }

    fun resetWiremockStatus() {
        wireMockServer.resetAll()
        // set default requests if any
    }
}复制
```

### 4.8. 重构为单元测试

这可能是最重要的一点之一。我们会发现自己一遍又一遍地进行一些集成测试，这些测试实际上是在执行我们应用程序的一些高级策略。

**每当我们发现一些集成测试测试了一堆核心业务逻辑的案例时，就该重新考虑我们的方法并将它们分解为单元测试了。**

成功完成此任务的可能模式可能是：

-   识别正在测试核心业务逻辑的多个场景的集成测试
-   复制套件，并将副本重构为单元测试——在此阶段，我们可能还需要分解生产代码以使其可测试
-   让所有测试都变绿
-   在集成套件中留下一个足够出色的快乐路径示例——我们可能需要重构或加入并重塑一些
-   删除剩余的集成测试

Michael Feathers 在 Working Effectively with Legacy Code 中介绍了许多实现这一目标的技术。

## 5.总结

在本文中，我们介绍了以 Spring 为重点的集成测试。

首先，我们讨论了集成测试的重要性以及为什么它们与 Spring 应用程序特别相关。

之后，我们总结了一些可能对 Web 应用程序中某些类型的集成测试派上用场的工具。

最后，我们列出了会减慢测试执行时间的潜在问题，以及改进它的技巧。