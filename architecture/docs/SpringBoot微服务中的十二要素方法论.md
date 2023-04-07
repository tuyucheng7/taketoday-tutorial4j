## 1. 概述

在本教程中，我们将了解[十二要素应用程序方法论](https://12factor.net/)。

我们还将了解如何在 Spring Boot 的帮助下开发微服务。在此过程中，我们将了解如何应用十二因素方法来开发此类微服务。

## 2. 什么是十二因素法？

十二要素方法是一组十二个最佳实践，用于开发作为服务运行的应用程序。[这最初是由 Heroku 在 2011 年为在其云平台上作为服务部署的应用程序起草的。随着时间的推移，这已被证明对于任何软件即服务](https://en.wikipedia.org/wiki/Software_as_a_service)(SaaS) 开发都足够通用。

那么，软件即服务是什么意思？传统上，我们设计、开发、部署和维护软件解决方案以从中获取商业价值。但是，我们不一定非要参与这个过程才能达到相同的结果。例如，计算适用税是许多领域的通用功能。

现在，我们可以决定自己构建和管理此服务或订阅商业服务产品。此类服务产品就是我们所知的软件即服务。

虽然软件即服务不会对其开发的架构施加任何限制；采用一些最佳实践非常有用。

如果我们将软件设计为在现代云平台上模块化、可移植和可扩展，那么它就非常适合我们的服务产品。这就是十二因素方法的用武之地。我们将在本教程的后面部分看到它们的实际应用。

## 3. Spring Boot微服务

[微服务](https://www.baeldung.com/spring-microservices-guide)是一种将软件开发为松耦合服务的架构风格。这里的关键要求是服务应该围绕业务域边界进行组织。这通常是最难识别的部分。

此外，此处的服务对其数据拥有唯一权限，并将操作公开给其他服务。服务之间的通信通常通过轻量级协议(如 HTTP)进行。这导致可独立部署和可扩展的服务。

现在，微服务架构和软件即服务不再相互依赖。但是，不难理解，在开发软件即服务时，利用微服务架构是非常有益的。它有助于实现我们之前讨论的许多目标，例如模块化和可扩展性。

[Spring Boot](https://spring.io/projects/spring-boot)是一个基于 Spring 的应用程序框架，它消除了许多与开发企业应用程序相关的样板文件。它为我们提供了一个[高度自以为是](https://www.baeldung.com/cs/opinionated-software-design)但灵活的平台来开发微服务。在本教程中，我们将利用 Spring Boot 使用十二要素方法交付微服务。

## 4. 应用十二因素方法论

现在让我们定义一个简单的应用程序，我们将尝试使用我们刚刚讨论的工具和实践来开发它。我们都喜欢看电影，但跟踪我们已经看过的电影是一项挑战。

现在，谁愿意开始拍一部电影，然后又放弃呢？我们需要的是一个简单的服务来记录和查询我们看过的电影：[![12 事实应用程序](https://www.baeldung.com/wp-content/uploads/2019/09/12-factpr-app.jpg)](https://www.baeldung.com/wp-content/uploads/2019/09/12-factpr-app.jpg)

这是一个非常简单且标准的微服务，带有数据存储和 REST 端点。我们还需要定义一个映射到持久性的模型：

```java
@Entity
public class Movie {
    @Id
    private Long id;
    private String title;
    private String year;
    private String rating;
    // getters and setters
}复制
```

我们定义了一个带有 id 和一些其他属性的 JPA 实体。现在让我们看看 REST 控制器是什么样子的：

```java
@RestController
public class MovieController {
 
    @Autowired
    private MovieRepository movieRepository;
    @GetMapping("/movies")
    public List<Movie> retrieveAllStudents() {
        return movieRepository.findAll();
    }

    @GetMapping("/movies/{id}")
    public Movie retrieveStudent(@PathVariable Long id) {
        return movieRepository.findById(id).get();
    }

    @PostMapping("/movies")
    public Long createStudent(@RequestBody Movie movie) {
        return movieRepository.save(movie).getId();
    }
}复制
```

这涵盖了我们简单服务的基础。当我们在以下小节中讨论如何实施十二因素方法时，我们将完成应用程序的其余部分。

### 4.1. 代码库

十二因素应用程序的第一个最佳实践是在版本控制系统中对其进行跟踪。[Git](https://git-scm.com/)是当今最流行的版本控制系统，几乎无处不在。该原则规定应用程序应在单个代码存储库中进行跟踪，并且不得与任何其他应用程序共享该存储库。

Spring Boot 提供了许多方便的方法来引导应用程序，包括命令行工具和[Web 界面](https://start.spring.io/)。生成引导程序应用程序后，我们可以将其转换为 git 存储库：

```powershell
git init复制
```

此命令应从应用程序的根目录运行。这个阶段的应用程序已经包含一个 .gitignore 文件，它有效地限制生成的文件不受版本控制。所以，我们可以直接创建一个初始提交：

```powershell
git add .
git commit -m "Adding the bootstrap of the application."复制
```

最后，如果我们愿意，我们可以添加一个远程并将我们的提交推送到远程(这不是严格的要求)：

```powershell
git remote add origin https://github.com/<username>/12-factor-app.git
git push -u origin master复制
```

### 4.2. 依赖关系

接下来，十二因素应用程序应始终显式声明其所有依赖项。我们应该使用依赖声明清单来做到这一点。Java 有多种依赖管理工具，如 Maven 和 Gradle。我们可以使用其中之一来实现这个目标。

因此，我们的简单应用程序依赖于一些外部库，例如促进 REST API 和连接到数据库的库。让我们看看如何使用 Maven 以声明方式定义它们。

Maven 要求我们在 XML 文件中描述项目的依赖关系，通常称为[项目对象模型](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html)(POM)：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>复制
```

尽管这看起来简单明了，但这些依赖项通常具有其他传递依赖项。这在一定程度上使其复杂化，但有助于我们实现目标。现在，我们的应用程序没有未明确描述的直接依赖项。

### 4.3. 配置

应用程序通常有很多配置，其中一些配置可能因部署而异，而其他配置则保持不变。

在我们的示例中，我们有一个持久数据库。我们需要要连接的数据库的地址和凭据。这很可能在部署之间发生变化。

一个十二因素应用程序应该外部化所有这些因部署而异的配置。此处的建议是对此类配置使用环境变量。这导致配置和代码完全分离。

Spring 提供了一个配置文件，我们可以在其中声明这样的配置并将其附加到环境变量中：

```powershell
spring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/movies
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}复制
```

在这里，我们将数据库 URL 和凭据定义为配置，并映射了要从环境变量中选取的实际值。

在 Windows 上，我们可以在启动应用程序之前设置环境变量：

```powershell
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=movies
set MYSQL_PASSWORD=password复制
```

[我们可以使用像Ansible](https://www.ansible.com/)或[Chef](https://www.chef.io/)这样的配置管理工具来自动化这个过程。

### 4.4. 支持服务

支持服务是应用程序运行所依赖的服务。例如数据库或消息代理。十二要素应用程序应将所有此类支持服务视为附加资源。这实际上意味着它不需要任何代码更改来交换兼容的支持服务。唯一的变化应该是配置。

在我们的应用程序中，我们使用[MySQL](https://www.mysql.com/)作为支持服务来提供持久性。

[Spring JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)使代码与实际的数据库提供者完全无关。我们只需要定义一个提供所有标准操作的存储库：

```java
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}复制
```

正如我们所看到的，这并不直接依赖于 MySQL。Spring 在类路径上检测 MySQL 驱动程序，并动态提供此接口的特定于 MySQL 的实现。此外，它直接从配置中提取其他细节。

因此，如果我们必须从 MySQL 更改为 Oracle，我们所要做的就是替换依赖项中的驱动程序并替换配置。

### 4.5. 构建、发布和运行

十二因素方法严格地将代码库转换为正在运行的应用程序的过程分为三个不同的阶段：

-   构建阶段：这是我们获取代码库、执行静态和动态检查，然后生成可执行包(如 JAR)的地方。[使用像Maven](https://maven.apache.org/)这样的工具，这是非常简单的：

```powershell
     mvn clean compile test package复制
```

-   发布阶段：这是我们获取可执行包并将其与正确配置相结合的阶段。在这里，我们可以使用[Packer和像](https://www.packer.io/)[Ansible](https://www.ansible.com/)这样的配置器来创建 Docker 镜像：

```powershell
     packer build application.json复制
```

-   运行阶段：最后，这是我们在目标执行环境中运行应用程序的阶段。如果我们使用[Docker](https://www.docker.com/)作为容器来发布我们的应用程序，那么运行应用程序就足够简单了：

```powershell
     docker run --name <container_id> -it <image_id>复制
```

最后，我们不必手动执行这些阶段。这就是[Jenkins](https://jenkins.io/)的声明式管道非常方便的地方。

### 4.6. 进程

十二要素应用程序应作为无状态进程在执行环境中运行。换句话说，它们不能在请求之间在本地存储持久状态。它们可能会生成需要存储在一个或多个有状态支持服务中的持久数据。

在我们的示例中，我们公开了多个端点。对这些端点中的任何一个的请求完全独立于在它之前发出的任何请求。例如，如果我们在内存中跟踪用户请求并使用该信息来满足未来的请求，这就违反了十二因素应用程序。

因此，十二因素应用程序不会像粘性会话那样强加这种限制。这使得这样的应用程序具有高度的可移植性和可扩展性。在提供自动缩放的云执行环境中，这是应用程序非常理想的行为。

### 4.7. 端口绑定

Java 中的传统 Web 应用程序被开发为 WAR 或 Web 存档。这通常是具有依赖性的 Servlet 的集合，它需要一个像 Tomcat 这样的一致的容器运行时。相反，十二因素应用程序不希望有这种运行时依赖性。它是完全独立的，只需要像 Java 一样的执行运行时。

在我们的案例中，我们使用 Spring Boot 开发了一个应用程序。除了许多其他好处之外，Spring Boot 还为我们提供了一个默认的嵌入式应用程序服务器。因此，我们之前使用 Maven 生成的 JAR 完全能够在任何环境中执行，只需具有兼容的 Java 运行时：

```powershell
java -jar application.jar复制
```

在这里，我们的简单应用程序通过 HTTP 绑定将其端点公开到特定端口(如 8080)。像我们上面那样启动应用程序后，应该可以访问导出的服务，如 HTTP。

应用程序可以通过绑定到多个端口来导出多个服务，如 FTP 或[WebSocket](https://www.baeldung.com/websockets-spring)。

### 4.8. 并发

Java 提供线程作为经典模型来处理应用程序中的并发性。线程就像轻量级进程，代表程序中的多个执行路径。线程功能强大，但在帮助应用程序扩展方面存在局限性。

十二因素方法建议应用程序依赖于扩展过程。这实际上意味着应用程序应该设计为跨多个进程分配工作负载。但是，各个进程可以在内部自由利用像Thread这样的并发模型。

Java 应用程序在启动时会获得一个绑定到底层 JVM 的进程。我们实际上需要的是一种启动应用程序的多个实例并在它们之间进行智能负载分配的方法。由于我们已经将我们的应用程序打包为[Docker](https://www.baeldung.com/docker-java-api)容器，因此[Kubernetes](https://www.baeldung.com/kubernetes)是此类编排的自然选择。

### 4.9. 一次性使用

应用程序进程可以有意或因意外事件而关闭。无论哪种情况，十二因素应用程序都应该能够优雅地处理它。换句话说，应用程序过程应该是完全一次性的，没有任何不需要的副作用。此外，流程应该快速启动

例如，在我们的应用程序中，端点之一是为电影创建新的数据库记录。现在，处理此类请求的应用程序可能会意外崩溃。但是，这应该不会影响应用程序的状态。当客户端再次发送相同的请求时，不应导致重复记录。

总之，应用程序应该公开[幂等](https://www.baeldung.com/cs/idempotent-operations)服务。这是用于云部署的服务的另一个非常理想的属性。这提供了随时停止、移动或旋转新服务的灵活性，无需任何其他考虑。

### 4.10. 开发/生产对等

应用程序通常在本地机器上开发，在其他一些环境中进行测试，最后部署到生产环境中。通常情况下，这些环境是不同的。例如，开发团队在 Windows 机器上工作，而生产部署在 Linux 机器上进行。

十二因素方法建议尽可能减少开发和生产环境之间的差距。这些差距可能是由于较长的开发周期、涉及的不同团队或使用的不同技术堆栈造成的。

现在，Spring Boot 和 Docker 等技术在很大程度上自动弥合了这一差距。无论我们在哪里运行，容器化应用程序都应该表现相同。我们也必须使用相同的支持服务——比如数据库。

此外，我们应该有正确的流程，如持续集成和交付，以促进进一步弥合这一差距。

### 4.11. 日志

日志是应用程序在其生命周期中生成的基本数据。它们为应用程序的工作提供了宝贵的见解。通常，应用程序可以生成具有不同详细信息的多个级别的日志，并以多种不同的格式输出 ii。

然而，十二因素应用程序将自己与日志生成及其处理分开。对于这样的应用程序，日志只不过是按时间顺序排列的事件流。它只是将这些事件写入执行环境的标准输出。此类流的捕获、存储、管理和归档应由执行环境处理。

为此，我们可以使用多种工具。首先，我们可以使用[SLF4J](https://www.baeldung.com/slf4j-with-log4j2-logback)在我们的应用程序中抽象地处理日志记录。[此外，我们可以使用像Fluentd](https://www.fluentd.org/)这样的工具来收集来自应用程序和支持服务的日志流。

我们可以将其输入[Elasticsearch](https://www.elastic.co/)进行存储和索引。[最后，我们可以在Kibana](https://www.elastic.co/products/kibana)中生成有意义的可视化仪表板。

### 4.12. 管理进程

我们经常需要用我们的应用程序状态执行一些一次性任务或例行程序。例如，修复不良记录。现在，我们可以通过多种方式实现这一目标。由于我们可能不经常需要它，我们可以编写一个小脚本来独立于另一个环境运行它。

现在，十二因素方法强烈建议将此类管理脚本与应用程序代码库放在一起。在这样做时，它应该遵循与我们应用于主应用程序代码库相同的原则。还建议使用执行环境的内置 REPL 工具在生产服务器上运行此类脚本。

在我们的示例中，我们如何使用到目前为止已经看过的电影为我们的应用程序播种？虽然我们可以使用我们可爱的小端点，但这似乎不切实际。我们需要的是一个执行一次性加载的脚本。我们可以编写一个小的 Java 函数来从文件中读取电影列表并将它们批量保存到数据库中。

此外，我们可以使用[与 Java 运行时集成的 Groovy](https://www.baeldung.com/groovy-java-applications)来启动此类进程。

## 五、实际应用

所以，现在我们已经看到了十二因素方法所建议的所有因素。将应用程序开发为十二要素应用程序当然有其好处，尤其是当我们希望将它们作为服务部署在云上时。但是，就像所有其他指南、框架、模式一样，我们必须问，这是灵丹妙药吗？

老实说，在软件设计和开发中，没有任何一种方法可以声称是灵丹妙药。十二因素法也不例外。虽然其中一些因素非常直观，而且很可能我们已经在做，但其他因素可能不适用于我们。必须在我们的目标背景下评估这些因素，然后做出明智的选择。

重要的是要注意，所有这些因素都是为了帮助我们开发一个模块化、独立、可移植、可扩展和可观察的应用程序。根据应用程序的不同，我们也许可以通过其他方式更好地实现它们。也没有必要同时采用所有因素，即使采用其中一些因素也可能使我们比以前更好。

最后，这些因素非常简单和优雅。在我们要求我们的应用程序具有更高的吞吐量和更低的延迟并且几乎没有停机和故障的时代，它们变得更加重要。采用这些因素让我们从一开始就有了正确的开始。结合微服务架构和应用程序容器化，它们似乎恰到好处。

## 六，总结

在本教程中，我们介绍了十二因素方法论的概念。我们讨论了如何利用带有 Spring Boot 的微服务架构来有效地交付它们。此外，我们详细探讨了每个因素以及如何将它们应用到我们的应用程序中。我们还探索了几种工具来成功地有效地应用这些单独的因素。