## 1. 概述

在本文中，我们将对两个著名的Java框架Spring Boot和 Quarkus 进行简单的比较。最后，我们将更好地了解它们的异同和一些特殊性。

此外，我们将执行一些测试来衡量他们的表现并观察他们的行为。

## 2. 弹簧靴

Spring Boot 是一个基于Java的框架，专注于企业应用。它连接所有 Spring 项目，并通过提供许多生产就绪的集成来帮助提高开发人员的生产力。

通过这样做，它减少了配置和样板文件的数量。此外，由于其约定优于配置方法，它会根据运行时类路径中可用的依赖项自动注册默认配置，Spring Boot 大大缩短了许多Java应用程序的上市时间。

## 3.夸尔库斯

Quarkus 是另一个框架，其方法与上述 Boot 中提到的 Spring 类似，但还承诺提供具有快速启动时间、更好的资源利用率和效率的更小的工件。

它针对云、无服务器和容器化环境进行了优化。但是，尽管侧重点略有不同，但 Quarkus 还可以与最流行的Java框架很好地集成。

## 4.比较

如上所述，这两个框架都可以很好地与其他项目和框架集成。但是，它们的内部实现和架构是不同的。例如，Spring Boot 提供两种形式的 Web 功能：[阻塞 (Servlets)](https://www.baeldung.com/spring-mvc-tutorial)和[非阻塞 (WebFlux)](https://www.baeldung.com/spring-webflux)。

另一方面，Quarkus 也提供了这两种方法，但与Spring Boot不同的是，它[允许我们同时使用阻塞和非阻塞策略](https://developers.redhat.com/blog/2019/11/18/how-quarkus-brings-imperative-and-reactive-programming-together)。此外，Quarkus 在其架构中嵌入了响应式方法。

出于这个原因，我们将使用两个完全响应式的应用程序，它们是通过 Spring WebFlux 和 Quarkus 响应式功能实现的，以便在我们的比较中有一个更准确的场景。

此外，Quarkus 项目中最重要的功能之一是能够创建本机图像(二进制和特定于平台的可执行文件)。因此，我们还将在比较中包括两个原生图像，但[就 Spring 而言，原生图像支持仍处于试验阶段](https://www.baeldung.com/spring-native-intro)。为此，我们需要[GraalVM](https://www.graalvm.org/)。

### 4.1. 测试应用程序

我们的应用程序将公开三个 API：一个允许用户创建邮政编码，另一个用于查找特定邮政编码的信息，最后按城市查询邮政编码。如前所述，这些 API 是使用Spring Boot和 Quarkus 完全使用反应式方法并使用 MySQL 数据库实现的。

目标是拥有一个简单的示例应用程序，但比 HelloWorld 应用程序稍微复杂一些。当然，这会影响我们的比较，因为数据库驱动程序和序列化框架等事物的实现会影响结果。但是，大多数应用程序也可能会处理这些事情。

因此，我们的比较并不旨在成为关于哪个框架更好或性能更高的最终真相，而是一个将分析这些特定实现的案例研究。

### 4.2. 测试计划

为了测试这两个实现，我们将使用[Wrk](https://github.com/wg/wrk)执行测试，并使用其指标报告来分析我们的发现。此外，我们将使用[VisualVM](https://visualvm.github.io/)来监控测试执行期间应用程序的资源利用率。

测试将运行 7 分钟，期间将调用所有 API，从预热期开始，然后增加连接数直到达到 100 个。Wrk 可以使用此设置生成大量负载：

[![测试计划工作](https://www.baeldung.com/wp-content/uploads/2021/10/test-planning-wrk.png)](https://www.baeldung.com/wp-content/uploads/2021/10/test-planning-wrk.png)

所有测试均在具有以下规格的机器上执行：

[![机器规格 1](https://www.baeldung.com/wp-content/uploads/2021/10/machine-specifications-1.png)](https://www.baeldung.com/wp-content/uploads/2021/10/machine-specifications-1.png)

虽然由于缺乏与其他后台进程的隔离而不理想，但该测试仅旨在说明所建议的比较。如前所述，我们无意对这两个框架的性能进行广泛而详细的分析。

还有一点值得一提的是，根据我们的机器规格，我们可能需要调整连接数、线程数等。

### 4.3. 了解我们的测试

确保我们测试的是正确的东西是必不可少的，为此，我们将使用 Docker 容器来部署我们的基础设施。这将使我们能够控制应用程序和数据库的资源限制。目标是现在强调应用程序的底层系统，我们的数据库。对于此示例，仅限制可用 CPU 的数量就足够了，但这可能会根据我们机器中可用的资源而改变。

要限制可用的源，我们可以使用[Docker 设置](https://www.baeldung.com/ops/docker-memory-limit)、[cpulimit](https://manpages.ubuntu.com/manpages/trusty/man1/cpulimit.1.html)命令或我们喜欢的任何其他工具。此外，我们可能会使用[docker stats](https://docs.docker.com/engine/reference/commandline/stats/)和 [top](https://www.baeldung.com/linux/top-command)命令来监控系统的资源。最后，关于内存，我们将测量堆使用情况以及[RSS](https://en.wikipedia.org/wiki/Resident_set_size)，为此让我们使用[ps](https://man7.org/linux/man-pages/man1/ps.1.html) ( ps -o pid,rss,command -p <pid> ) 命令。

## 5. 调查结果

这两个项目的开发人员体验都很好，但值得一提的是，Spring Boot 拥有比我们在网上找到的更好的文档和更多的材料。Quarkus 在这方面正在改进，并拥有大量有助于提高生产力的功能。不过考虑到文档和堆栈溢出问题，还是落后了。

在指标方面，我们有：

[![quarkus 指标](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-metrics.png)](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-metrics.png)

通过这个实验，我们可以观察到Quarkus 在 JVM 和本机版本的启动时间方面都比Spring Boot快。此外，对于原生图像，Quarkus 构建时间也快得多。构建耗时 91 秒(Quarkus)对 113 秒(Spring Boot)，而 JVM 构建耗时 5.24 秒(Quarkus)对 1.75 秒(Spring Boot)，所以在这一个中指向 Spring。

关于工件大小，Spring Boot 和 Quarkus 生成的可运行工件在 JVM 版本方面相似，但在原生工件的情况下，Quarkus 做得更好。

然而，关于其他指标. 总结并不简单。因此，让我们更深入地了解其中的一些。

### 5.1. 中央处理器

如果我们关注 CPU 使用率，我们会看到JVM 版本在预热阶段开始时消耗更多的 CPU 。之后，CPU占用趋于稳定，各版本消耗相对均等。

以下是 JVM 和本机版本中 Quarkus 的 CPU 消耗，按顺序排列：

(春季 JVM)

[![弹簧处理器](https://www.baeldung.com/wp-content/uploads/2021/10/spring-cpu.png)](https://www.baeldung.com/wp-content/uploads/2021/10/spring-cpu.png)

(Quarkus JVM)

[![Quarkus 处理器](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-cpu.png)](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-cpu.png)

(春季本土)

[![春季本机CPU](https://www.baeldung.com/wp-content/uploads/2021/10/spring-native-cpu.png)](https://www.baeldung.com/wp-content/uploads/2021/10/spring-native-cpu.png)

(Quarkus 本机)

[![quarkus 原生 cpu](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-native-cpu.png)](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-native-cpu.png)

Quarkus 在这两种情况下都做得更好。然而，差异是如此之小，以至于也可以考虑打平。另一点值得一提的是，在图中，我们看到了基于机器中可用 CPU 数量的消耗。尽管如此，为了确保我们强调的是选项而不是系统的其他部分，我们已将应用程序可用的内核数量限制为三个。

### 5.2. 记忆

关于内存，就更复杂了。首先，两个框架的 JVM 版本都为堆保留了更多的内存，几乎相同的内存量。关于堆使用，JVM 版本比原生版本消耗更多内存，但从配对来看，Quarkus 似乎比 JVM 版本的 Spring 消耗略少。但是，同样，差异非常小。

(春季启动 JVM)

[![春天的记忆](https://www.baeldung.com/wp-content/uploads/2021/10/spring-memory.png)](https://www.baeldung.com/wp-content/uploads/2021/10/spring-memory.png)

(Quarkus JVM)

[![夸克记忆](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-memory.png)](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-memory.png)

然后，看看原生图像，事情似乎发生了变化。Spring Native 版本似乎更频繁地收集内存并保持较低的内存占用。

(Spring Boot 本机)

[![春季本机内存](https://www.baeldung.com/wp-content/uploads/2021/10/spring-native-memory.png)](https://www.baeldung.com/wp-content/uploads/2021/10/spring-native-memory.png)

(Quarkus 本机)

[![quarkus 本机内存](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-native-memory.png)](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-native-memory.png)

另一个重要的亮点是，在 RSS 内存测量方面，Quarkus 似乎在两个版本中都超过了 Spring。我们只在启动时添加了 RSS 比较，但我们也可以在测试期间使用相同的命令。

尽管如此，在这个比较中，我们只使用了默认参数。因此，没有对 GC、JVM 选项或任何其他参数进行任何更改。不同的应用程序可能需要不同的设置，我们在实际环境中使用它们时应该记住这一点。

### 5.3. 响应时间

最后，我们将使用不同的方法来处理响应时间，因为许多可用的基准测试工具都存在称为[Coordinated Omission](https://www.slideshare.net/InfoQ/how-not-to-measure-latency-60111840)的问题。我们将使用[hyperfoil](https://hyperfoil.io/)，这是一种旨在避免此问题的工具。在测试过程中，会创建许多请求，但其目的是不要对应用程序施加过多压力，而只是足以测量其响应时间。

不过，测试结构与前一个非常相似。

(春季启动 JVM)

[![弹簧响应时间](https://www.baeldung.com/wp-content/uploads/2021/10/spring-response-time.png)](https://www.baeldung.com/wp-content/uploads/2021/10/spring-response-time.png)

(Quarkus JVM)

[![夸克响应时间](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-response-time.png)](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-response-time.png)

吞吐量和响应时间虽然相关，但不是一回事，它们衡量的是不同的事物。Quarkus JVM 版本在压力下和中等负载下都有良好的性能。它似乎具有更高的吞吐量和稍低的响应时间。

(Spring Boot 本机)

[![春季本机响应时间](https://www.baeldung.com/wp-content/uploads/2021/10/spring-native-response-time.png)](https://www.baeldung.com/wp-content/uploads/2021/10/spring-native-response-time.png)

(Quarkus 本机)

[![quarkus 原生响应时间](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-native-response-time.png)](https://www.baeldung.com/wp-content/uploads/2021/10/quarkus-native-response-time.png)

查看原生版本，数字再次发生变化。现在，Spring 的响应时间似乎略短，但总体吞吐量更高。然而，查看所有数字，我们可以看到差异太小，无法定义任何明显的赢家。

### 5.4. 连接点

综合考虑，这两个框架都被证明是实现Java应用程序的绝佳选择。

本机应用程序速度快且资源消耗低，是无服务器、短期应用程序和低资源消耗至关重要的环境的绝佳选择。

另一方面，JVM 应用程序似乎有更多的开销，但随着时间的推移具有出色的稳定性和高吞吐量，非常适合健壮、长期存在的应用程序。

最后，关于性能，所有版本在比较时都具有强大的性能，至少对于我们的示例而言。差异是如此之小，以至于我们可以说它们具有相似的性能。当然，我们可以争辩说，JVM 版本在吞吐量方面更好地处理了重负载，同时消耗了更多资源，另一方面，本机版本消耗更少。但是，根据用例，这种差异甚至可能无关紧要。

最后，我必须指出，在 Spring 应用程序中，我们不得不切换 DB 驱动程序，因为文档推荐的驱动程序有[问题](https://github.com/spring-projects/spring-data-r2dbc/issues/766)。相比之下，Quarkus 开箱即用，没有任何问题。

## 六. 总结

本文比较了Spring Boot和 Quarkus 框架以及它们不同的部署模式，JVM 和 Native。我们还研究了这些应用程序的其他指标和方面。