## 1. 概述

在本文中，我们将介绍[Spring](https://spring.io/)作为最流行的 Java 框架之一的主要价值主张。

更重要的是，我们将尝试理解我们选择 Spring 框架的原因。Spring 及其组成部分的详细信息已[在我们之前的教程中广泛介绍](https://www.baeldung.com/spring-intro)。因此，我们将跳过介绍性的“如何”部分，主要关注“为什么”。

## 2. 为什么使用任何框架？

在我们开始特别讨论 Spring 之前，让我们首先了解为什么我们首先需要使用任何框架。

像 Java 这样的通用编程语言能够支持各种各样的应用程序。更不用说 Java 每天都在积极开发和改进。

而且，有无数的开源库和专有库在这方面支持 Java。

那么到底为什么我们需要一个框架呢？老实说，并非绝对需要使用框架来完成任务。但是，出于以下几个原因，通常建议使用一个：

-   帮助我们专注于核心任务，而不是与之相关的样板文件
-   以设计模式的形式汇集了多年的智慧
-   帮助我们遵守行业和监管标准
-   降低应用程序的总拥有成本

我们在这里只是触及了皮毛，我们必须说，好处是不容忽视的。但这不可能都是积极的，所以有什么问题：

-   迫使我们以特定方式编写应用程序
-   绑定到特定版本的语言和库
-   添加到应用程序的资源足迹

坦率地说，软件开发中没有灵丹妙药，框架当然也不例外。因此，应该从上下文中驱动选择哪个框架或不选择哪个框架。

希望到本文结束时，我们能够更好地做出关于 Java 中的 Spring 的决定。

## 三、Spring生态系统简介

在我们开始对 Spring Framework 进行定性评估之前，让我们仔细看看 Spring 生态系统是什么样的。

Spring 出现于 2003 年的某个时候，当时 Java 企业版发展迅速，开发企业应用程序令人兴奋，但仍然乏味！

Spring 最初是[作为 Java 的控制反转 (IoC) 容器](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)。我们仍然主要将 Spring 与它联系起来，事实上，它构成了框架的核心以及在它之上开发的其他项目。

### 3.1. 弹簧框架

Spring 框架被[划分为多个模块](https://docs.spring.io/spring/docs/current/spring-framework-reference/index.html)，这使得在任何应用程序中选择和使用部分变得非常容易：

-   [核心](https://docs.spring.io/spring/docs/5.1.8.RELEASE/spring-framework-reference/core.html#spring-core)：提供核心功能，如 DI(依赖注入)、国际化、验证和 AOP(面向切面编程)
-   [数据访问](https://docs.spring.io/spring/docs/5.1.8.RELEASE/spring-framework-reference/data-access.html#spring-data-tier)：支持通过 JTA(Java Transaction API)、JPA(Java Persistence API)和 JDBC(Java Database Connectivity)进行数据访问
-   [Web](https://docs.spring.io/spring/docs/5.1.8.RELEASE/spring-framework-reference/web.html#spring-web)：同时支持 Servlet API ( [Spring MVC](https://docs.spring.io/spring/docs/5.1.8.RELEASE/spring-framework-reference/web.html#spring-web) ) 和最近的 Reactive API ( [Spring WebFlux](https://docs.spring.io/spring/docs/5.1.8.RELEASE/spring-framework-reference/web-reactive.html#spring-webflux) )，另外还支持 WebSockets、STOMP 和 WebClient
-   [集成](https://docs.spring.io/spring/docs/5.1.8.RELEASE/spring-framework-reference/integration.html#spring-integration)：支持通过 JMS(Java 消息服务)、JMX(Java 管理扩展)和 RMI(远程方法调用)集成到 Enterprise Java
-   [测试](https://docs.spring.io/spring/docs/5.1.8.RELEASE/spring-framework-reference/testing.html#testing)：通过模拟对象、测试夹具、上下文管理和缓存广泛支持单元和集成测试

### 3.2. 春季项目

但是，让 Spring 更有价值的是一个强大的生态系统，多年来围绕它发展壮大并继续积极发展。这些被构造为在 Spring 框架之上开发的[Spring 项目。](https://spring.io/projects)

尽管 Spring 项目列表很长而且还在不断变化，但还是有一些值得一提：

-   [Boot](https://www.baeldung.com/spring-boot)：为我们提供了一套非常自以为是但可扩展的模板，几乎可以立即创建基于 Spring 的各种项目。它使得使用嵌入式 Tomcat 或类似容器创建独立的 Spring 应用程序变得非常容易。
-   [云](https://www.baeldung.com/spring-cloud-series)：提供支持以轻松开发一些常见的分布式系统模式，如服务发现、断路器和 API 网关。它帮助我们减少在本地、远程甚至托管平台中部署此类样板模式的工作量。
-   [安全性](https://www.baeldung.com/security-spring)：提供了一种健壮的机制，以高度可定制的方式为基于 Spring 的项目开发身份验证和授权。通过最少的声明支持，我们可以防止会话固定、点击劫持和跨站点请求伪造等常见攻击。
-   [移动](https://www.baeldung.com/spring-mobile)：提供检测设备并相应地调整应用程序行为的功能。此外，支持设备感知视图管理以获得最佳用户体验、站点首选项管理和站点切换器。
-   [Batch](https://www.baeldung.com/introduction-to-spring-batch)：提供了一个轻量级框架，用于为数据归档等企业系统开发批处理应用程序。对调度、重启、跳过、收集指标和日志记录有直观的支持。此外，支持通过优化和分区为大批量作业进行扩展。

不用说，这是对 Spring 必须提供的内容的相当抽象的介绍。但它为我们提供了关于 Spring 的组织和广度的足够基础，可以进一步讨论。

## 4. 春天的行动

习惯上加一个hello-world程序来了解任何新技术。

让我们看看Spring 如何让编写一个不仅仅执行 hello-world 的程序变得轻而易举。我们将创建一个应用程序，它将 CRUD 操作公开为由内存数据库支持的域实体(如 Employee)的 REST API。更重要的是，我们将使用基本身份验证来保护我们的突变端点。最后，如果没有好的、旧的单元测试，任何应用程序都不可能真正完整。

### 4.1. 项目设置

[我们将使用Spring Initializr](https://start.spring.io/)设置我们的 Spring Boot 项目，这是一个方便的在线工具，可以使用正确的依赖项引导项目。我们将添加 Web、JPA、H2 和 Security 作为项目依赖项，以正确设置 Maven 配置。

[有关引导程序的](https://www.baeldung.com/spring-boot-start)更多详细信息，请参阅我们之前的一篇文章。

### 4.2. 领域模型和持久性

要做的事情很少，我们已经准备好定义我们的领域模型和持久性。

让我们首先将Employee定义为一个简单的 JPA 实体：

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    // Standard constructor, getters and setters
}复制
```

请注意我们在实体定义中包含的自动生成的 ID。

现在我们必须为我们的实体定义一个 JPA 存储库。这是 Spring 使它变得非常简单的地方：

```java
public interface EmployeeRepository 
  extends CrudRepository<Employee, Long> {
    List<Employee> findAll();
}复制
```

我们所要做的就是像这样定义一个接口，Spring JPA 将为我们提供一个充实了默认和自定义操作的实现。相当整洁！在我们的其他文章中查找有关[使用 Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)的更多详细信息。

### 4.3. 控制器

现在我们必须定义一个网络控制器来路由和处理我们传入的请求：

```java
@RestController
public class EmployeeController {
    @Autowired
    private EmployeeRepository repository;
    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return repository.findAll();
    }
    // Other CRUD endpoints handlers
}复制
```

实际上，我们所要做的就是注释类并定义路由元信息以及每个处理程序方法。

在我们之前的文章中非常详细地介绍了使用[Spring REST 控制器。](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration)

### 4.4. 安全

所以我们现在已经定义了所有内容，但是如何保护创建或删除员工等操作呢？我们不希望对这些端点进行未经身份验证的访问！

Spring Security 在这方面确实大放异彩：

```java
@EnableWebSecurity
public class WebSecurityConfig {
 
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/employees", "/employees/**")
            .permitAll()
          .anyRequest()
            .authenticated()
          .and()
            .httpBasic();
        return http.build();
    }
    // other necessary beans and definitions
}复制
```

这里有[更多细节需要注意](https://www.baeldung.com/spring-security-basic-authentication)理解，但最重要的一点是我们只允许 GET 操作不受限制的声明方式。

### 4.5. 测试

现在我们已经完成了一切，但是等等，我们如何测试它？

让我们看看 Spring 是否可以让为 REST 控制器编写单元测试变得容易：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerTests {
    @Autowired
    private MockMvc mvc;
    @Test
    @WithMockUser()
    public void givenNoEmployee_whenCreateEmployee_thenEmployeeCreated() throws Exception {
        mvc.perform(post("/employees").content(
            new ObjectMapper().writeValueAsString(new Employee("First", "Last"))
            .with(csrf()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status()
            .isCreated())
          .andExpect(jsonPath("$.firstName", is("First")))
          .andExpect(jsonPath("$.lastName", is("Last")));
    }
    // other tests as necessary
}复制
```

正如我们所见，Spring 为我们提供了必要的基础设施来编写简单的单元和集成测试，否则这些测试依赖于要初始化和配置的 Spring 上下文。

### 4.6. 运行应用程序

最后，我们如何运行这个应用程序？这是 Spring Boot 的另一个有趣的方面。尽管我们可以将其打包为常规应用程序并以传统方式部署在 Servlet 容器上。

可是这个那个有什么好玩的！Spring Boot 带有嵌入式 Tomcat 服务器：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}复制
```

这是一个作为引导程序的一部分预先创建的类，具有使用嵌入式服务器启动此应用程序所需的所有详细信息。

而且，[这是高度可定制的](https://www.baeldung.com/spring-boot-application-configuration)。

## 5. Spring 的替代品

虽然选择使用框架相对容易，但在框架之间进行选择通常会让我们感到畏惧。但是为此，我们必须至少对 Spring 必须提供的功能有哪些替代方案有一个粗略的了解。

正如我们之前所讨论的，Spring 框架及其项目为企业开发人员提供了广泛的选择。如果我们对当代 Java 框架进行快速评估，它们甚至无法接近 Spring 为我们提供的生态系统。

然而，对于特定领域，它们确实形成了一个令人信服的论据来选择替代方案：

-   [Guice](https://www.baeldung.com/guice)：为 Java 应用程序提供强大的 IoC 容器
-   [Play](https://www.baeldung.com/java-intro-to-the-play-framework)：非常适合作为具有响应式支持的 Web 框架
-   [Hibernate](https://www.baeldung.com/hibernate-4-spring)：一个既定的数据访问框架，支持 JPA

除了这些之外，还有一些最近添加的内容提供了比特定域更广泛的支持，但仍然没有涵盖 Spring 必须提供的所有内容：

-   [Micronaut](https://www.baeldung.com/micronaut)：一个基于 JVM 的框架，为云原生微服务量身定制
-   [Quarkus](https://www.baeldung.com/quarkus-io)：一个新时代的 Java 堆栈，承诺提供更快的启动时间和更小的占用空间

显然，完全遍历列表既没有必要也不可行，但我们确实在这里得到了广泛的想法。

## 6. 那么，为什么选择 Spring？

最后，我们已经构建了所有必需的上下文来解决我们的核心问题，为什么是 Spring？我们了解框架如何帮助我们开发复杂的企业应用程序。

此外，我们确实了解针对 Web、数据访问、框架集成等特定问题的选项，尤其是 Java。

现在，春天在所有这些中闪耀在哪里？让我们探索一下。

### 6.1. 可用性

任何框架流行的关键方面之一是开发人员使用它的难易程度。Spring 通过多个配置选项和 Convention over Configuration 使开发人员能够真正轻松地启动并准确配置他们需要的内容。

像 Spring Boot这样的项目使得引导一个复杂的 Spring 项目变得几乎微不足道。更不用说，它有出色的文档和教程来帮助任何人上手。

### 6.2. 模块化

Spring 受欢迎的另一个关键方面是其高度模块化的特性。我们可以选择使用整个 Spring 框架或仅使用必要的模块。此外，我们可以根据需要选择性地包含一个或多个 Spring 项目。

此外，我们还可以选择使用其他框架，例如 Hibernate 或 Struts！

### 6.3. 一致性

尽管 Spring不支持所有 Jakarta EE 规范，但它支持其所有技术，并经常在必要时改进对标准规范的支持。例如，Spring 支持基于 JPA 的存储库，因此可以轻松切换提供者。

此外，Spring 支持行业规范，如Spring Web Reactive 下的[Reactive Stream](https://www.reactive-streams.org/)和[Spring HATEOAS](https://www.baeldung.com/spring-hateoas-tutorial)下的 HATEOAS 。

### 6.4. 可测试性

任何框架的采用在很大程度上还取决于测试构建在其之上的应用程序的难易程度这一事实。以Spring为核心提倡和支持测试驱动开发(TDD)。

Spring 应用程序主要由 POJO 组成，这自然使单元测试相对简单得多。但是，Spring 确实为像 MVC 这样单元测试变得复杂的场景提供了模拟对象。

### 6.5. 到期

Spring 在创新、采用和标准化方面有着悠久的历史。多年来，它已经成熟到足以成为大型企业应用程序开发中面临的大多数常见问题的默认解决方案。

更令人兴奋的是它的开发和维护非常积极。每天都在开发对新语言功能和企业集成解决方案的支持。

### 6.6. 社区支持

最后但同样重要的是，任何框架甚至库都是通过创新在行业中生存下来的，没有比社区更好的创新场所了。Spring 是由 Pivotal Software 领导并得到大型组织和个人开发人员联盟支持的开源软件。

这意味着它仍然是上下文相关的并且通常是未来主义的，正如其保护伞下的项目数量所证明的那样。

## 7.不使用 Spring 的原因

有各种各样的应用程序可以从不同级别的 Spring 使用中受益，并且随着 Spring 的增长而变化得很快。

但是，我们必须了解，与任何其他框架一样，Spring 有助于管理应用程序开发的复杂性。它帮助我们避免常见的陷阱，并在应用程序随着时间的推移增长时保持其可维护性。

这是以额外的资源足迹和学习曲线为代价的，无论它有多小。如果真的有一个应用程序足够简单并且不会变得复杂，也许根本不使用任何框架可能会更有好处！

## 八、总结

在本文中，我们讨论了在应用程序开发中使用框架的好处。我们还特别简要地讨论了 Spring Framework。

在讨论这个主题时，我们还研究了一些可用于 Java 的替代框架。

最后，我们讨论了促使我们选择 Spring 作为 Java 框架的原因。

不过，我们应该以一条建议结束本文。无论听起来多么引人注目，在软件开发中通常没有单一的、放之四海而皆准的解决方案。

因此，我们必须运用我们的智慧，为我们要解决的具体问题选择最简单的解决方案。