## **一、简介**

我们最近研究了[Lightrun——](https://www.baeldung.com/lightrun-article1)一个开发人员可观察性平台——看看它能为我们提供什么来更好地观察和理解我们的应用程序。

Spring 大量使用注解来控制各种功能，这些功能可以以多种方式工作。这可以使编写应用程序变得异常高效——我们只需要添加一个适当的注释来启用功能。然而，当这不起作用时，诊断可能会令人沮丧，因为没有直接的方法调用可以查看。

**在本文中，我们将探索使用[Lightrun](https://www.baeldung.com/lightrun)来诊断 Spring 注释如何在我们的应用程序中工作。**

## **2.调试事务边界**

Spring 使用*@Transactional*注释来标记应该在事务中执行的方法。这是通过 Spring 在构建时检测注释并构建 JDK 代理来包装我们的类实例来实现的。该代理负责处理所有事务边界细节。这确保事务在我们的方法之前开始，并在它完成后正确整理。

**因此，调试交易意味着我们必须首先确定我们需要查看的位置。**这可以通过向我们的事务方法之一添加[快照](https://www.baeldung.com/lightrun-snapshots)并执行它来最轻松地完成，从而捕获堆栈跟踪：

[![相框](https://www.baeldung.com/wp-content/uploads/2023/02/frames.png)](https://www.baeldung.com/wp-content/uploads/2023/02/frames.png)

*在这里，我们可以看到我们的控制器 – deleteTask:74, TasksController* – 和我们的事务服务 – *deleteTaskById: 40, TasksService*之间的整个堆栈跟踪。由于我们的代码将此作为直接方法调用，因此 Spring 为我们插入了两者之间的所有内容。

所以现在我们需要确定其中哪些是重要的。这些条目中的许多条目都集中在使事情正常进行所需的代理和反射调用上。然而，中间的三个听起来非常专注于交易：

-   *调用：119，事务拦截器*
-   *invokeWithinTransaction:388，TransactionAspectSupport*
-   *proceedWithInvocation:123, TransactionInterceptor$1*

此外，我们可以从这些方法名称中大致推断出发生了什么。***invokeWithinTransaction\*****似乎很可能是 Spring 代码中管理事务边界的地方。**因此，这是我们应该专注于准确调试正在发生的事情的地方。

如果我们在我们的 IDE 中打开[这段代码](https://github.com/spring-projects/spring-framework/blob/v5.3.19/spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java#L380)，我们可以准确地看到这里发生了什么：

[![在事务中调用](https://www.baeldung.com/wp-content/uploads/2023/02/invokeWithinTransaction.png)](https://www.baeldung.com/wp-content/uploads/2023/02/invokeWithinTransaction.png)

为了更好地理解这如何影响我们的代码，我们可以使用 Lightrun 在运行时将[日志](https://www.baeldung.com/lightrun-logs)添加到一些适当的行。例如：

-   第 382 行之后的日志显示了新启动的事务。
-   第 392 行的日志显示异常是否已中止事务。
-   最后，第 408 行的日志显示事务结束时的结果。

如果我们随后将日志添加到我们的控制器和服务中——这样我们就可以看到事务内外的操作——那么我们就可以准确地看到发生了什么：

[![日志](https://www.baeldung.com/wp-content/uploads/2023/02/logs.png)](https://www.baeldung.com/wp-content/uploads/2023/02/logs.png) [![安慰](https://www.baeldung.com/wp-content/uploads/2023/02/console.png)](https://www.baeldung.com/wp-content/uploads/2023/02/console.png)

所以我们可以看到这里启动了三个 Spring 事务——一个在我们的服务调用之外，另外两个在它内部。服务内部的这两个与我们这里涉及的两个存储库调用相吻合。但是，因为它们都具有*PROPAGATION_REQUIRED*，所以它们实际上参与了同一个数据库事务。

这已经为我们提供了有关事务开始和结束的确切时间、它们是否会回滚以及输出是什么的信息。我们在没有中断正在运行的应用程序的情况下完成了所有这些工作。

## **3.调试缓存边界**

Spring通过向我们的方法调用添加适当的注释来支持[缓存方法结果。](https://www.baeldung.com/spring-cache-tutorial)这样做会导致 Spring 在方法调用周围自动插入代码以缓存结果并返回缓存的值，而不是在认为合适的情况下实际调用该方法。

**众所周知，缓存很难调试，因为缓存命中将意味着永远不会调用底层代码，因此不会触发其中的任何日志。**这包括我们直接写入代码的日志，也包括 Lightrun 添加的日志。然而，Lightrun 允许我们将这些日志放在我们的代码和缓存代码本身中。

和以前一样，我们可以使用快照来查看我们的代码以及 Spring 在其间注入的所有位：

[![快照](https://www.baeldung.com/wp-content/uploads/2023/02/snapshot-1.png)](https://www.baeldung.com/wp-content/uploads/2023/02/snapshot-1.png) [![抓拍框](https://www.baeldung.com/wp-content/uploads/2023/02/snapshotFrame.png)](https://www.baeldung.com/wp-content/uploads/2023/02/snapshotFrame.png)

在这里我们可以看到我们的控制器和服务以及 Spring 插入其中的所有调用。在这种情况下，感兴趣的类是*CacheInterceptor*和*CacheAspectSupport*。如果我们进一步观察，我们会发现*CacheInterceptor实际上是**CacheAspectSupport*的子类，所以这实际上都是一个类。

特别是，通过[检查代码](https://github.com/spring-projects/spring-framework/blob/v5.3.19/spring-context/src/main/java/org/springframework/cache/interceptor/CacheAspectSupport.java#L398)，我们可以看到有趣的功能在*CacheAspectSupport.execute()*中：

[![检查代码](https://www.baeldung.com/wp-content/uploads/2023/02/codeinspect.png)](https://www.baeldung.com/wp-content/uploads/2023/02/codeinspect.png)

在此期间，我们可以看到我们正在检查缓存是命中还是未命中并采取相应的行动。**因此，我们可以使用 Lightrun 在此处添加一些日志，以准确查看发生了什么，无论是缓存命中还是未命中：**

-   第 414 行的日志允许我们查看是否有缓存命中或未命中。
-   第 421 行的日志允许我们指示我们将调用我们的基础方法。
-   第 423 行之后的日志向我们显示了返回值，无论是缓存命中还是未命中。

我们现在可以准确地看到我们的缓存结果发生了什么：

[![日志](https://www.baeldung.com/wp-content/uploads/2023/02/logs2.png)](https://www.baeldung.com/wp-content/uploads/2023/02/logs2.png)

在这里我们可以看到两次调用来检索相同的资源。第一个进来，缓存未命中，然后继续调用真正的服务。第二个进入，缓存命中，所以真正的服务没有被调用。

**如果没有 Lightrun，我们最多只能看到控制器在两种情况下都被调用，而服务仅在第一种情况下被调用，但我们不知道为什么会这样。**

## **4.调试请求映射**

Spring WebMVC 是 Spring 框架的重要组成部分，也是它处理 HTTP 请求并将它们转发到正确控制器的基础。**但是，如果出现问题，准确地计算出发生了什么可能会令人沮丧。**

Lightrun 为我们提供了一些额外的工具，可以像以前一样准确地查看发生了什么。然而，这需要付出更多的努力才能实现，因为 Spring 的内部结构在这方面更为复杂。

和以前一样，首先，我们需要一种方法。我们通过向任何控制器添加一个快照，然后触发它来获取显示导致控制器方法的调用的堆栈跟踪来做到这一点：

[![Lightrun 快照](https://www.baeldung.com/wp-content/uploads/2023/02/lightrunsnapshots.png)](https://www.baeldung.com/wp-content/uploads/2023/02/lightrunsnapshots.png)

经过一些探索，我们可以看到*DispatcherServlet.doDispatch()*很有趣，因为它调用了*getHandler()*。这似乎是确定用于给定 HTTP 请求的处理程序的地方。这向我们展示了整个 Spring 中的几个堆栈框架，它们通向我们的控制器。

[看看这个](https://github.com/spring-projects/spring-framework/blob/v5.3.19/spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java#L1257)，我们可以看到它迭代了一组*HandlerMapping*实例，并依次询问每个实例是否可以处理请求：

[![获取处理程序](https://www.baeldung.com/wp-content/uploads/2023/02/gethandler.png)](https://www.baeldung.com/wp-content/uploads/2023/02/gethandler.png)

这是一个有几个选项的抽象方法，所以让我们在 like 1261 上添加一条日志语句，看看实际发生了什么：

[![日志](https://www.baeldung.com/wp-content/uploads/2023/02/logs3.png)](https://www.baeldung.com/wp-content/uploads/2023/02/logs3.png)

这立即向我们展示了涉及的处理程序映射。而且，重要的是，最后一个是为这个请求返回了一些东西的那个，所以我们接下来看一下。

*RequestMappingHandlerMapping*中*getHandler()*的实际实现是在超类*AbstractHandlerMapping*中。然而，这在 AbstractHandlerMethodMapping中的*getHandlerInternal()*方面立即起作用*。**这在lookupHandlerMethod()*方面有效，所以让我们看看那里。

第一个有趣的部分似乎是*addMatchingMappings()，*因此我们将向其中添加一个日志，看看到底发生了什么：

[![日志](https://www.baeldung.com/wp-content/uploads/2023/02/logs4.png)](https://www.baeldung.com/wp-content/uploads/2023/02/logs4.png)

毫不奇怪，其中大多数都没有返回匹配项。然而，有人做到了。这表明我们有一个与我们的请求相匹配的“GET /{id}”处理程序——这正是我们所期望的。如果这些都没有返回匹配项，我们会立即知道问题出在请求映射上。**例如，如果我们使用不受支持的 HTTP 方法或不正确的 URI 路径进行 HTTP 调用，那么我们会在这里看到所有内容的\*空\*匹配，这会告诉我们问题出在哪里：**

[![日志](https://www.baeldung.com/wp-content/uploads/2023/02/logs5.png)](https://www.baeldung.com/wp-content/uploads/2023/02/logs5.png)

到目前为止，如果需要，我们可以进一步诊断对处理程序方法的调用（如果这是问题所在）。但如果问题是我们根本没有被调用，那么我们已经理解为什么会这样并且可以解决问题。

## **5.结论**

**在本文中，我们查看了一些 Spring 注释示例以及如何使用 Lightrun 诊断它们发生了什么。**特别是，我们已经看到了如何使用其中一些工具来了解什么时候工作正常，什么时候不工作。

这些相同的技术同样适用于任何其他库和框架。为什么不在下次需要诊断正在使用的其他库时尝试一下呢？

如果您想阅读有关 Lightrun 的更多信息，请查看他们的[博客](http://lightrun.com/blog…)。