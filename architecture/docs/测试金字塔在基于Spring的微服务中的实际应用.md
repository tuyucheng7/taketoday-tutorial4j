## 1. 概述

在本教程中，我们将了解称为测试金字塔的流行软件测试模型。

我们将看到它在微服务世界中的相关性。在此过程中，我们将开发一个示例应用程序和相关测试以符合此模型。此外，我们将尝试了解使用模型的好处和界限。

## 2.让我们退后一步

在我们开始理解任何特定模型(如测试金字塔)之前，必须了解我们为什么需要一个模型。

测试软件的需要是与生俱来的，也许与软件开发本身的历史一样古老。软件测试从手动到自动化已经走过了漫长的道路。然而，目标保持不变 —交付符合规范的软件。

### 2.1. 测试类型

在实践中有几种不同类型的测试，它们侧重于特定的目标。可悲的是，词汇甚至对这些测试的理解都存在很大差异。

让我们回顾一些流行的和可能明确的：

-   单元测试：单元测试是针对小代码单元的测试，最好是孤立的。这里的目标是验证最小的可测试代码段的行为，而不用担心代码库的其余部分。这自动意味着任何依赖项都需要替换为模拟或存根或类似的构造。
-   集成测试：虽然单元测试侧重于一段代码的内部，但事实仍然是它之外有很多复杂性。代码单元需要协同工作，并且通常需要与数据库、消息代理或 Web 服务等外部服务协同工作。集成测试是在与外部依赖项集成时针对应用程序行为的测试。
-   UI 测试：我们开发的软件通常通过界面使用，消费者可以与之交互。通常，应用程序有一个 Web 界面。然而，API 接口正变得越来越流行。UI 测试针对这些界面的行为，这些界面通常本质上是高度交互的。现在，这些测试可以以端到端的方式进行，或者也可以单独测试用户界面。

### 2.2. 手动与自动测试

自测试开始以来，软件测试一直是手动完成的，即使在今天，它也得到了广泛的实践。但是，不难理解，手动测试是有限制的。为了使测试有用，它们必须全面且经常运行。

这在敏捷开发方法论和云原生微服务架构中更为重要。然而，测试自动化的需求很早就被意识到了。

如果我们回想一下我们之前讨论的不同类型的测试，随着我们从单元测试转向集成和 UI 测试，它们的复杂性和范围会增加。出于同样的原因，单元测试的自动化更容易并且也具有大部分好处。随着我们走得更远，自动化测试变得越来越困难，而且收益可能会越来越小。

除非某些方面，否则现在可以对大多数软件行为进行自动化测试。然而，与自动化所需的努力相比，这必须与收益进行合理权衡。

## 3. 什么是测试金字塔？

现在我们已经收集了足够多的关于测试类型和工具的上下文，是时候了解什么是测试金字塔了。我们已经看到我们应该编写不同类型的测试。

但是，我们应该如何决定为每种类型编写多少测试呢？需要注意哪些好处或陷阱？这些是测试金字塔等测试自动化模型解决的一些问题。

[Mike Cohn在他的《 ](https://www.mountaingoatsoftware.com/blog)[Succeeding with Agile](https://www.pearson.com/us/higher-education/program/Cohn-Succeeding-with-Agile-Software-Development-Using-Scrum/PGM201415.html) 》一书中提出了一个名为测试金字塔的结构。这呈现了我们应该在不同粒度级别编写的测试数量的可视化表示。

这个想法是，它应该在最细粒度的级别上最高，并且应该随着我们扩大测试范围而开始下降。这给出了金字塔的典型形状，因此得名：

[![金字塔](https://www.baeldung.com/wp-content/uploads/2019/10/Screenshot-2019-10-31-at-22.27.41-1024x645.png)](https://www.baeldung.com/wp-content/uploads/2019/10/Screenshot-2019-10-31-at-22.27.41.png)

虽然这个概念非常简单和优雅，但有效地采用它通常是一个挑战。重要的是要明白，我们绝不能拘泥于模型的形状和它提到的测试类型。关键要点应该是：

-   我们必须编写具有不同粒度级别的测试
-   我们必须编写更少的测试，因为我们的范围越来越大

## 4. 测试自动化工具

所有主流编程语言都有多种工具可用于编写不同类型的测试。我们将介绍 Java 世界中的一些流行选择。

### 4.1. 单元测试

-   测试框架：Java 中最流行的选择是[JUnit ，它有一个称为](https://www.baeldung.com/junit)[JUnit5 的](https://www.baeldung.com/junit-5)下一代版本。该领域的其他流行选择包括[TestNG](https://www.baeldung.com/testng)，与 JUnit5 相比，它提供了一些差异化的功能。但是，对于大多数应用，这两种都是合适的选择。
-   模拟：正如我们之前看到的，我们肯定希望在执行单元测试时扣除大部分(如果不是全部)依赖项。为此，我们需要一种机制来用模拟或存根等测试替身替换依赖项。[Mockito](https://www.baeldung.com/mockito-series)是一个优秀的框架，可以为 Java 中的真实对象提供模拟。

### 4.2. 集成测试

-   测试框架：集成测试的范围比单元测试更广泛，但入口点通常是更高抽象的相同代码。因此，适用于单元测试的相同测试框架也适用于集成测试。
-   模拟：集成测试的目的是测试具有真实集成的应用程序行为。但是，我们可能不想访问实际的数据库或消息代理进行测试。许多数据库和类似服务提供[可嵌入的版本](https://www.baeldung.com/java-in-memory-databases)来编写集成测试。

### 4.3. 界面测试

-   测试框架：UI 测试的复杂性因处理软件 UI 元素的客户端而异。例如，网页的行为可能因设备、浏览器甚至操作系统而异。[Selenium](https://www.baeldung.com/java-selenium-with-junit-and-testng)是使用 Web 应用程序模拟浏览器行为的流行选择。然而，对于 REST API，像[REST-assured](https://www.baeldung.com/rest-assured-tutorial)这样的框架是更好的选择。
-   Mocking：用户界面正变得更具交互性，并且使用[Angular](https://angular.io/)和[React](https://reactjs.org/)等 JavaScript 框架在客户端呈现。[使用Jasmine](https://jasmine.github.io/)和[Mocha](https://mochajs.org/)等测试框架单独测试此类 UI 元素更为合理。显然，我们应该结合端到端测试来做到这一点。

## 5. 在实践中采用原则

让我们开发一个小应用程序来演示我们目前讨论的原则。我们将开发一个小型微服务并了解如何编写符合测试金字塔的测试。

[微服务架构](https://www.baeldung.com/spring-microservices-guide)有助于将应用程序构建为围绕域边界绘制的松散耦合服务的集合。[Spring Boot](https://www.baeldung.com/spring-boot)提供了一个极好的平台，可以在几乎短时间内引导具有用户界面和依赖项(如数据库)的微服务。

我们将利用这些来演示测试金字塔的实际应用。

### 5.1. 应用架构

我们将开发一个基本应用程序，允许我们存储和查询我们看过的电影：

[![持久数据存储](https://www.baeldung.com/wp-content/uploads/2019/10/Screenshot-2019-10-31-at-22.28.37.png)](https://www.baeldung.com/wp-content/uploads/2019/10/Screenshot-2019-10-31-at-22.28.37.png)

正如我们所见，它有一个简单的 REST 控制器，暴露了三个端点：

```java
@RestController
public class MovieController {
 
    @Autowired
    private MovieService movieService;
 
    @GetMapping("/movies")
    public List<Movie> retrieveAllMovies() {
        return movieService.retrieveAllMovies();
    }
 
    @GetMapping("/movies/{id}")
    public Movie retrieveMovies(@PathVariable Long id) {
        return movieService.retrieveMovies(id);
    }
 
    @PostMapping("/movies")
    public Long createMovie(@RequestBody Movie movie) {
        return movieService.createMovie(movie);
    }
}复制
```

除了处理数据编组和解组之外，控制器仅路由到适当的服务：

```java
@Service
public class MovieService {
 
    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> retrieveAllMovies() {
        return movieRepository.findAll();
    }
 
    public Movie retrieveMovies(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id)
          .get();
        Movie response = new Movie();
        response.setTitle(movie.getTitle()
          .toLowerCase());
        return response;
    }
 
    public Long createMovie(@RequestBody Movie movie) {
        return movieRepository.save(movie)
          .getId();
    }
}复制
```

此外，我们有一个 JPA 存储库映射到我们的持久层：

```java
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}复制
```

最后，我们简单的领域实体来保存和传递电影数据：

```java
@Entity
public class Movie {
    @Id
    private Long id;
    private String title;
    private String year;
    private String rating;

    // Standard setters and getters
}复制
```

通过这个简单的应用程序，我们现在可以探索具有不同粒度和数量的测试。

### 5.2. 单元测试

首先，我们将了解如何为我们的应用程序编写简单的单元测试。从这个应用中可以看出，大部分逻辑都倾向于在服务层积累。这要求我们对此进行广泛和更频繁的测试——非常适合单元测试：

```java
public class MovieServiceUnitTests {
 
    @InjectMocks
    private MovieService movieService;
 
    @Mock
    private MovieRepository movieRepository;
 
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
 
    @Test
    public void givenMovieServiceWhenQueriedWithAnIdThenGetExpectedMovie() {
        Movie movie = new Movie(100L, "Hello World!");
        Mockito.when(movieRepository.findById(100L))
          .thenReturn(Optional.ofNullable(movie));
 
        Movie result = movieService.retrieveMovies(100L);
 
        Assert.assertEquals(movie.getTitle().toLowerCase(), result.getTitle());
    }
}复制
```

在这里，我们使用 JUnit 作为我们的测试框架，使用 Mockito 来模拟依赖关系。我们的服务，出于一些奇怪的要求，预计会返回小写的电影标题，这就是我们打算在这里测试的。可能有几种这样的行为，我们应该用这样的单元测试来广泛覆盖。

### 5.3. 集成测试

在我们的单元测试中，我们模拟了存储库，这是我们对持久层的依赖。虽然我们已经彻底测试了服务层的行为，但当它连接到数据库时我们仍然可能会遇到问题。这就是集成测试的用武之地：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieControllerIntegrationTests {
 
    @Autowired
    private MovieController movieController;
 
    @Test
    public void givenMovieControllerWhenQueriedWithAnIdThenGetExpectedMovie() {
        Movie movie = new Movie(100L, "Hello World!");
        movieController.createMovie(movie);
 
        Movie result = movieController.retrieveMovies(100L);
 
        Assert.assertEquals(movie.getTitle().toLowerCase(), result.getTitle());
    }
}复制
```

请注意这里的一些有趣差异。现在，我们没有模拟任何依赖关系。但是，我们可能仍然需要根据情况模拟一些依赖项。此外，我们正在使用SpringRunner运行这些测试。

这实质上意味着我们将有一个 Spring 应用程序上下文和实时数据库来运行此测试。难怪，这会运行得更慢！因此，我们在这里选择较少的场景进行测试。

### 5.4. 界面测试

最后，我们的应用程序有 REST 端点可以使用，它们可能有自己的细微差别需要测试。由于这是我们应用程序的用户界面，因此我们将重点关注在我们的 UI 测试中覆盖它。现在让我们使用 REST-assured 来测试应用程序：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieApplicationE2eTests {
 
    @Autowired
    private MovieController movieController;
 
    @LocalServerPort
    private int port;
 
    @Test
    public void givenMovieApplicationWhenQueriedWithAnIdThenGetExpectedMovie() {
        Movie movie = new Movie(100L, "Hello World!");
        movieController.createMovie(movie);
 
        when().get(String.format("http://localhost:%s/movies/100", port))
          .then()
          .statusCode(is(200))
          .body(containsString("Hello World!".toLowerCase()));
    }
}复制
```

正如我们所见，这些测试与正在运行的应用程序一起运行，并通过可用的端点访问它。我们专注于测试与 HTTP 相关的典型场景，例如响应代码。由于显而易见的原因，这些将是运行最慢的测试。

因此，我们必须非常有讲究地选择要在这里测试的场景。我们应该只关注我们在以前的更精细的测试中无法涵盖的复杂性。

## 6.微服务测试金字塔

现在我们已经了解了如何编写具有不同粒度的测试并适当地构建它们。但是，关键目标是通过更精细和更快的测试来捕获大部分应用程序复杂性。

虽然在单体应用程序中解决这个问题可以为我们提供所需的金字塔结构，但这对于其他架构可能不是必需的。

正如我们所知，微服务架构接受一个应用程序并为我们提供一组松散耦合的应用程序。这样一来，它就将应用程序固有的一些复杂性外化了。

现在，这些复杂性体现在服务之间的通信中。通过单元测试并不总是可以捕获它们，我们必须编写更多的集成测试。

虽然这可能意味着我们背离了经典的金字塔模型，但这并不意味着我们也背离了原则。请记住，我们仍在使用尽可能精细的测试来捕获大部分复杂性。只要我们清楚这一点，一个可能不符合完美金字塔的模型仍然有价值。

这里要理解的重要一点是，模型只有在交付价值时才有用。通常，该值取决于上下文，在本例中是我们为应用程序选择的架构。因此，虽然使用模型作为指导很有帮助，但我们应该关注基本原则，并最终选择在我们的架构环境中有意义的东西。

## 7. 与 CI 集成

当我们将它们集成到持续集成管道中时，自动化测试的力量和好处在很大程度上得以实现。[Jenkins](https://jenkins.io/)是一种以声明方式定义构建和[部署管道的](https://www.baeldung.com/jenkins-pipelines)流行选择。

我们可以集成我们在 Jenkins 管道中自动化的任何测试。但是，我们必须明白，这会增加管道执行的时间。持续集成的主要目标之一是快速反馈。如果我们开始添加使其变慢的测试，这可能会发生冲突。

关键要点应该是将快速测试(如单元测试)添加到预计运行更频繁的管道中。例如，我们可能不会从将 UI 测试添加到每次提交时触发的管道中获益。但是，这只是一个指南，最后，它取决于我们正在处理的应用程序的类型和复杂性。

## 八、总结

在本文中，我们了解了软件测试的基础知识。我们了解不同的测试类型以及使用可用工具之一对其进行自动化的重要性。

此外，我们了解了测试金字塔的含义。我们使用使用 Spring Boot 构建的微服务实现了这一点。

最后，我们了解了测试金字塔的相关性，尤其是在微服务等架构的背景下。