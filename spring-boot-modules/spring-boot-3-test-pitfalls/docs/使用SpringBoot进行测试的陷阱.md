## 1. 概述

编程中最重要的主题之一是测试。Spring Framework和Spring Boot通过提供测试框架扩展并引导我们编写最少的、可测试的代码并在后台进行大量自动化，从而提供了很好的支持。要运行Spring Boot集成测试，我们只需将@SpringBootTest添加到我们的测试类中。我们可以在[Spring Boot中的测试](https://www.baeldung.com/spring-boot-testing)中找到简短的介绍。即使我们不使用Spring Boot而使用Spring Framework，我们也可以非常高效地进行[集成测试](https://www.baeldung.com/integration-testing-in-spring)。

但是开发测试越容易，陷入陷阱的风险就越大。在本教程中，我们将探讨如何执行Spring Boot测试以及编写测试时必须考虑的事项。

## 2. 陷阱示例

让我们从一个小例子开始-实现一个管理宠物的服务(PetService)：

```java
public record Pet(String name) {}
```

```java
@Service
public class PetService {

    private final Set<Pet> pets = new HashSet<>();

    public Set<Pet> getPets() {
        return Collections.unmodifiableSet(pets);
    }

    public boolean add(Pet pet) {
        return this.pets.add(pet);
    }
}
```

该服务不应允许重复，因此测试可能如下所示：

```java
@SpringBootTest
class PetServiceIntegrationTest {

    @Autowired
    PetService service;

    @Test
    void shouldAddPetWhenNotAlreadyExisting() {
        var pet = new Pet("Dog");
        var result = service.add(pet);
        assertThat(result).isTrue();
        assertThat(service.getPets()).hasSize(1);
    }

    @Test
    void shouldNotAddPetWhenAlreadyExisting() {
        var pet = new Pet("Cat");
        var result = service.add(pet);
        assertThat(result).isTrue();
        // try a second time
        result = service.add(pet);
        assertThat(result).isFalse();
        assertThat(service.getPets()).hasSize(1);
    }
}
```

当我们分别执行每个测试时，一切都很好。但是当我们一起执行它们时，我们会得到一个测试失败：

<img src="../assets/img.png">

但是为什么测试会失败呢？我们如何防止这种情况发生？我们将澄清这一点，但首先，让我们从一些基础知识开始。

## 3. 功能测试的设计目标

我们编写功能测试来记录需求并确保应用程序代码正确实现它们。因此，测试本身也必须是正确的，并且必须易于理解，最好是不言自明。但是，对于本文，我们将关注进一步的设计目标：

-   回归：测试必须是可重复的。他们必须产生确定性的结果
-   隔离：测试可能不会相互影响。它们以什么顺序执行，甚至是否并行执行都无关紧要
-   性能：测试应该尽可能快地运行并尽可能节省资源，尤其是那些属于CI管道或TDD的测试

**关于Spring Boot测试，我们需要知道它们是一种集成测试**，因为它们会导致ApplicationContext的初始化，即bean使用依赖注入进行初始化和注入。因此隔离需要特别注意-而上面展示的例子似乎存在隔离问题。另一方面，良好的性能也是对Spring Boot测试的挑战。

**作为第一个结论，我们可以说避免集成测试是最重要的一点**。PetService测试的最佳解决方案是单元测试：

```java
// no annotation here
class PetServiceUnitTest {

    PetService service = new PetService();

    // ...
}
```

我们应该只在必要时编写Spring Boot测试，例如，当我们想要测试我们的应用程序代码是否被框架正确处理(生命周期管理、依赖注入、事件处理)或者如果我们想要测试一个特殊层(HTTP层、持久层)。

## 4. 上下文缓存

显然，当我们将@SpringBootTest添加到我们的测试类时，ApplicationContext就会启动，bean也会被初始化。但是，为了支持隔离，JUnit会为每个测试方法初始化此步骤。这将导致每个测试用例有一个ApplicationContext，从而显著降低测试性能。为了避免这种情况，Spring测试框架缓存上下文并允许将其重新用于多个测试用例。当然，这也会导致重用bean实例。这就是PetService测试失败的原因-两种测试方法都处理PetService的同一个实例。

不同的ApplicationContext仅在它们彼此不同时才会创建-例如，如果它们包含不同的beans或具有不同的应用程序属性。我们可以在[Spring测试框架文档](https://docs.spring.io/spring-framework/docs/6.0.0/reference/html/testing.html#testcontext-ctx-management-caching)中找到有关这方面的详细信息。因为ApplicationContext配置是在类级别完成的，所以默认情况下，测试类中的所有方法都共享相同的上下文。

下图显示了这种情况：

<img src="../assets/img_1.png">

**上下文缓存作为一种性能优化与隔离相矛盾**，因此只有在确保测试之间的隔离时，我们才能重用ApplicationContext。**这是Spring Boot测试只应在满足某些条件时才在同一JVM中并行运行的最重要原因**。我们可以使用不同的JVM进程运行测试(例如，通过[为Maven Surefire插件设置forkMode](https://maven.apache.org/plugins/maven-surefire-plugin/test-mojo.html#forkMode)，但随后我们绕过了缓存机制。

### 4.1 PetService示例解决方案

关于PetService测试，可能有多种解决方案。所有这些都适用，因为PetService是有状态的。

一种解决方案是使用@DirtiesContext标注每个测试方法。这会将ApplicationContext标记为脏，因此在测试后将其关闭并从缓存中删除。这会阻止性能优化，并且永远不应该是首选方式：

```java
@SpringBootTest
class PetServiceIntegrationTest {

    @Autowired
    PetService service;

    @Test
    @DirtiesContext
    void shouldAddPetWhenNotAlreadyExisting() {
        // ...
    }

    @Test
    @DirtiesContext
    void shouldNotAddPetWhenAlreadyExisting() {
        // ...
    }
}
```

另一种解决方案是在每次测试后重置PetService的状态：

```java
@SpringBootTest
class PetServiceIntegrationTest {

    @Autowired
    PetService service;

    @AfterEach
    void resetState() {
        service.clear(); // deletes all pets
    }

    // ...
}
```

**但是，最好的解决方案是实现PetService无状态**。目前，宠物没有存储在内存中，这永远不是一个好的做法，尤其是在可扩展的环境中。

### 4.2 陷阱：上下文太多

为了避免无意识地初始化额外的ApplicationContexts，我们需要知道是什么导致了不同的配置。最明显的是bean的直接配置，例如使用@ComponentScan、@Import、@AutoConfigureXXX(例如@AutoConfigureTestDatabase)。但是派生也可能是由启用Profiles(@ActiveProfiles)或记录事件(@RecordApplicationEvents)引起的：

```java
@SpringBootTest
// each of them derives from the original (cached) context
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.sample.blogposts")
@Import(PetServiceTestConfiguration.class)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@RecordApplicationEvents
class PetServiceIntegrationTest {
    // ...
}
```

我们可以在[Spring测试框架文档](https://docs.spring.io/spring-framework/docs/6.0.0/reference/html/testing.html#testcontext-ctx-management-caching)中找到详细信息。

### 4.3 陷阱：Mocking

Spring测试框架包括[Mockito](https://www.baeldung.com/mockito-series)来创建和使用Mock。使用@MockBean时，我们让Mockito创建一个Mock实例并将其放入ApplicationContext中。**此实例特定于测试类。结果是我们不能与其他测试类共享ApplicationContext**：

```java
@SpringBootTest
class PetServiceIntegrationTest {

    // context is not shareable with other test classes
    @MockBean
    PetServiceRepository repository;

    // ... 
}
```

一个建议可能是避免使用Mock并测试整个应用程序。但是如果我们想测试异常处理，我们不能总是阻止Mock。如果我们仍然想与其他测试类共享ApplicationContext，我们还必须共享Mock实例。当我们定义一个创建Mock并替换ApplicationContext中的原始bean的@TestConfiguration时，这是可能的。但是，我们必须意识到隔离问题。

正如我们所知，缓存和重用ApplicationContext假定我们在测试后重置上下文中的每个有状态bean。Mock是一种特殊的有状态bean，因为它们被配置为返回值或抛出异常，并且它们记录每个方法调用以对每个测试用例进行验证。测试后，我们也需要重置它们。这是在使用@MockBean时自动完成的，但是当我们在@TestConfiguration中创建Mock时，我们负责重置。幸运的是，Mockito本身提供了设置。所以整个解决方案可能是：

```java
@TestConfiguration
public class PetServiceTestConfiguration {

    @Primary
    @Bean
    PetServiceRepository createRepositoryMock() {
        return mock(
              PetServiceRepository.class,
              MockReset.withSettings(MockReset.AFTER)
        );
    }
}
```

```java
@SpringBootTest
@Import(PetServiceTestConfiguration.class) // if not automatically detected
class PetServiceIntegrationTest {

    @Autowired
    PetService repository;
    @Autowired // Mock
    PetServiceRepository repository;

    // ... 
}
```

### 4.4 配置上下文缓存

如果我们想了解在测试执行期间ApplicationContext的初始化频率，我们可以在application.properties中设置日志记录级别：

```properties
logging.level.org.springframework.test.context.cache=DEBUG
```

然后我们得到一个包含如下统计信息的日志输出：

```shell
org.springframework.test.context.cache:
  Spring test ApplicationContext cache statistics:
  [DefaultContextCache@34585ac9 size = 1, maxSize = 32, parentContextCount = 0, hitCount = 8, missCount = 1]
```

默认缓存大小为32(LRU)。如果我们想增加或减少它，我们可以指定另一个缓存大小：

```properties
spring.test.context.cache.maxSize=50
```

如果我们想深入研究缓存机制的代码，可以从org.springframework.test.context.cache.ContextCache接口开始。

## 5. 上下文配置

不仅为了缓存目的，而且为了ApplicationContext初始化性能，我们可能会优化配置。初始化越少，测试设置越快。我们可以为[惰性bean初始化](https://www.baeldung.com/spring-boot-lazy-initialization)配置测试，但我们必须注意潜在的副作用。另一种可能性是减少bean的数量。

### 5.1 配置检测

默认情况下，@SpringBootTest开始在测试类的当前包中搜索，然后在包结构中向上搜索，寻找带有@SpringBootConfiguration注解的类，然后从中读取配置以创建应用程序上下文。此类通常是我们的主应用程序，因为@SpringBootApplication注解包含@SpringBootConfiguration注解。然后，它会创建一个类似于将在生产环境中启动的应用程序上下文。

### 5.2 最小化ApplicationContext

如果我们的测试类需要一个不同的(最小的)ApplicationContext，我们可以创建一个静态内部@Configuration类：

```java
@SpringBootTest
class PetServiceIntegrationTest {

    @Autowired
    PetService service;

    @Configuration
    static class MyCustomConfiguration {

        @Bean
        PetService createMyPetService() {
            // create your custom pet service
        }
    }

    // ...
}
```

与使用@TestConfiguration相比，这完全阻止了@SpringBootConfiguration的自动检测。

另一种减小ApplicationContext大小的方法是使用@SpringBootTest(classes=...)。这也将忽略内部@Configuration类并仅初始化给定的类。

```java
@SpringBootTest(classes = PetService.class)
public class PetServiceIntegrationTest {

    @Autowired
    PetService service;

    // ...
}
```

如果我们不需要任何Spring Boot功能，例如Profile和读取应用程序属性，我们可以替换@SpringBootTest。让我们来看看这个注解背后的内容：

```java
@ExtendWith(SpringExtension.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
public @interface SpringBootTest {
    // ...
}
```

我们可以看到这个注解只启用了JUnit SpringExtension(它是Spring Framework的一部分，而不是Spring Boot的一部分)并声明了Spring Boot提供的[TestContextBootstrapper](https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/test/context/TestContextBootstrapper.html)，它实现了搜索机制。如果我们删除@BootstrapWith，则使用[DefaultTestContextBootstrapper](https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/test/context/support/DefaultTestContextBootstrapper.html)，它不是Spring Boot感知的。然后我们必须使用@ContextConfiguration指定上下文：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PetService.class)
class PetServiceIntegrationTest {

    @Autowired
    PetService service;

    // ...
}
```

### 5.3 测试切片

Spring Boot的自动配置系统适用于应用程序，但有时对于测试来说可能太过分了。仅加载测试应用程序“切片”所需的部分配置通常很有帮助。例如，我们可能想要测试Spring MVC控制器是否正确映射URL，并且我们不想在这些测试中涉及数据库调用；或者我们可能想要测试JPA实体，并且在这些测试运行时我们对Web层不感兴趣。

我们可以在[Spring Boot文档](https://docs.spring.io/spring-boot/docs/3.0.0/reference/html/test-auto-configuration.html#appendix.test-auto-configuration)中找到可用测试切片的概述。

### 5.4 上下文优化与缓存

上下文优化可以加快单个测试的启动时间，但我们应该意识到这将导致不同的配置，从而导致更多的ApplicationContext初始化。总而言之，整个测试执行时间可能会增加。因此，跳过上下文优化可能会更好，但使用符合测试用例要求的现有配置。

## 6. 建议：自定义切片

正如我们所了解的，我们必须在ApplicationContext的数量和大小之间找到一个折衷方案。挑战在于跟踪配置。解决这个问题的一个可能的解决方案是定义几个自定义切片(可能每层一个，整个应用程序一个)并在所有测试中专门使用它们，即我们必须避免在测试类中使用@MockBean进行进一步配置和Mock。

Pet域层的解决方案可能是：

```java
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = PetsDomainTest.class)
@Import(PetsDomainTest.PetServiceTestConfiguration.class)
// further features that can help to configure and execute tests
@ActiveProfiles({"test", "domain-test"})
@Tag("integration-test")
@Tag("domain-test")
public @interface PetsDomainTest {

    @TestConfiguration
    class PetServiceTestConfiguration {

        @Primary
        @Bean
        PetServiceRepository createRepositoryMock() {
            return mock(
                  PetServiceRepository.class,
                  MockReset.withSettings(MockReset.AFTER)
            );
        }
    }
}
```

然后可以按如下所示使用它：

```java
@PetsDomainTest
public class PetServiceIntegrationTest {

    @Autowired
    PetService service;
    @Autowired // Mock
    PetServiceRepository repository;

    // ...
}
```

## 7. 进一步的陷阱

### 7.1 派生测试配置

集成测试的一个原则是，我们尽可能接近生产状态测试应用程序。我们只针对特定的测试用例派生。不幸的是，测试框架本身会重新配置我们应用程序的行为，我们应该意识到这一点。例如，[内置的可观察性功能](https://www.baeldung.com/spring-boot-3-observability)在测试期间被禁用，因此如果我们想在我们的应用程序中测试观察，我们需要明确使用@AutoConfigureObservability重新启用它。

### 7.2 包结构

当我们想要测试应用程序的切片时，我们需要声明必须在ApplicationContext中初始化哪些组件。我们可以通过列出相应的类来做到这一点，但为了获得更稳定的测试配置，最好指定包。例如，我们有一个这样的映射器：

```java
@Component
public class PetDtoMapper {

    public PetDto map(Pet source) {
        // ...
    }
}
```

我们在测试中需要这个映射器；我们可以使用这个精益解决方案配置测试：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PetDtoMapper.class)
class PetDtoMapperIntegrationTest {

    @Autowired
    PetDtoMapper mapper;

    // ...
}
```

如果我们用MapStruct替换mapper实现，PetDtoMapper类型将成为一个接口，然后MapStruct在同一个包中生成实现类。因此，除非我们导入整个包，否则给定的测试将失败：

```java
@ExtendWith(SpringExtension.class)
public class PetDtoMapperIntegrationTest {

    @Configuration
    @ComponentScan(basePackageClasses = PetDtoMapper.class)
    static class PetDtoMapperTestConfig {}

    @Autowired
    PetDtoMapper mapper;

    // ...
}
```

这样做的副作用是初始化放置在同一包和子包中的所有其他bean。这就是为什么我们应该根据切片的结构创建一个包结构。这包括特定于域的组件、安全的全局配置、Web或持久层或事件处理程序。

## 8. 总结

在本教程中，我们探讨了编写Spring Boot测试的陷阱。我们了解到ApplicationContext是被缓存和重用的，因此我们需要考虑隔离。