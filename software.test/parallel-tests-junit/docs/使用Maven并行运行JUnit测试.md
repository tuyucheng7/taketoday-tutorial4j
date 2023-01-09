## 一、简介

尽管大多数时候串行执行测试工作得很好，但我们可能希望将它们并行化以加快速度。

在本教程中，我们将介绍如何使用 JUnit 和 Maven 的 Surefire 插件并行化测试。首先，我们将在单个 JVM 进程中运行所有测试，然后我们将在一个多模块项目中进行尝试。

## 2.Maven依赖

让我们从导入所需的依赖项开始。**我们需要使用[JUnit 4.7 或更高版本](https://search.maven.org/classic/#search|gav|1|g%3A"junit" AND a%3A"junit")以及[Surefire 2.16 或更高版本：](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.maven.surefire" AND a%3A"surefire")**

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>复制
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.21.0</version>
</plugin>复制
```

简而言之，Surefire 提供了两种并行执行测试的方法：

-   单个 JVM 进程中的多线程
-   分叉多个 JVM 进程

## 3. 运行并行测试

**要并行运行测试，我们应该使用扩展*org.junit.runners.ParentRunner*的测试运行器。**

但是，即使没有声明显式测试运行程序的测试也可以工作，因为默认运行程序扩展了此类。

接下来，为了演示并行测试执行，我们将使用一个包含两个测试类的测试套件，每个测试类都有一些方法。事实上，任何 JUnit 测试套件的标准实现都可以。

### 3.1。使用并行参数

*首先，让我们使用parallel* 参数在 Surefire 中启用并行行为 。**它说明了我们希望应用并行性的粒度级别。**

可能的值是：

-   *方法 -* 在单独的线程中运行测试方法
-   *类 -*在单独的线程中运行测试类
-   *classesAndMethods –*在单独的线程中运行类和方法
-   *套件 -*并行运行套件
-   *suitesAndClasses –* 在单独的线程中运行套件和类
-   *suitesAndMethods –* 为类和方法创建单独的线程
-   *all –*在单独的线程中运行套件、类和方法

在我们的示例中，我们使用*all*：

```xml
<configuration>
    <parallel>all</parallel>
</configuration>复制
```

其次，让我们定义我们希望 Surefire 创建的线程总数。我们可以通过两种方式做到这一点：

使用*threadCount* 定义了 Surefire 将创建的最大线程数：

```xml
<threadCount>10</threadCount>复制
```

或者使用*useUnlimitedThreads*参数，每个 CPU 核心创建一个线程：

```xml
<useUnlimitedThreads>true</useUnlimitedThreads>复制
```

默认情况下，*threadCount*是每个 CPU 内核。我们可以使用参数*perCoreThreadCount* 来启用或禁用此行为：

```xml
<perCoreThreadCount>true</perCoreThreadCount>复制
```

### 3.2. 使用线程数限制

现在，假设我们要在方法、类和套件级别定义要创建的线程数。**我们可以使用 *threadCountMethods*、*threadCountClasses*和*threadCountSuites*参数来做到这一点。**

让我们将这些参数与 之前配置中的*threadCount结合起来：* 

```xml
<threadCountSuites>2</threadCountSuites>
<threadCountClasses>2</threadCountClasses>
<threadCountMethods>6</threadCountMethods>复制
```

由于我们*都是**并行使用的，* 我们已经为方法、套件和类定义了线程数。但是，定义叶子参数不是强制性的。如果叶子参数被省略，Surefire 会推断要使用的线程数。

例如，如果 省略了*threadCountMethods*，那么我们只需要确保 *threadCount* > *threadCountClasses* + *threadCountSuites。*

有时我们可能希望限制为类或套件或方法创建的线程数量，即使我们使用无限数量的线程。

在这种情况下，我们也可以应用线程数限制：

```xml
<useUnlimitedThreads>true</useUnlimitedThreads>
<threadCountClasses>2</threadCountClasses>复制
```

### 3.3. 设置超时

有时我们可能需要确保测试执行是有时间限制的。

**为此，我们可以使用 *parallelTestTimeoutForcedInSeconds* 参数。**这将中断当前正在运行的线程，并且在超时后不会执行任何排队的线程：

```xml
<parallelTestTimeoutForcedInSeconds>5</parallelTestTimeoutForcedInSeconds>复制
```

**另一种选择是使用 *parallelTestTimeoutInSeconds*。**

在这种情况下，只有排队的线程会停止执行：

```xml
<parallelTestTimeoutInSeconds>3.5</parallelTestTimeoutInSeconds>复制
```

然而，对于这两个选项，测试将在超时后以错误消息结束。

### 3.4. 注意事项

Surefire 在父线程中调用带有*@Parameters*、*@BeforeClass*和*@AfterClass注释的静态方法。*因此，请确保在并行运行测试之前检查潜在的内存不一致或竞争条件。

此外，改变共享状态的测试绝对不是并行运行的好选择。

## 4. 多模块 Maven 项目中的测试执行

到目前为止，我们一直专注于在 Maven 模块中并行运行测试。

但是假设我们在一个 Maven 项目中有多个模块。由于这些模块是按顺序构建的，因此每个模块的测试也是按顺序执行的。

***我们可以通过使用 Maven 的-T*参数来更改此默认行为，该参数并行构建模块**。这可以通过两种方式完成。

我们可以在构建项目时指定要使用的确切线程数：

```shell
mvn -T 4 surefire:test复制
```

或者使用便携式版本并指定每个 CPU 内核要创建的线程数：

```shell
mvn -T 1C surefire:test复制
```

无论哪种方式，我们都可以加快测试以及构建执行时间。

## 5. 分叉 JVM

**通过*并行* 选项执行并行测试，并发在 JVM 进程中使用线程发生**。

由于线程共享相同的内存空间，这在内存和速度方面是有效的。但是，我们可能会遇到意外的竞争条件或其他与并发相关的微妙测试失败。事实证明，共享同一个内存空间既是福也是祸。

为了防止线程级并发问题，Surefire 提供了另一种并行测试执行模式：**分叉和进程级并发**。分叉进程的想法实际上非常简单。Surefire 不是产生多个线程并在它们之间分配测试方法，而是创建新进程并执行相同的分配。

由于不同进程之间没有共享内存，我们不会受到那些微妙的并发错误的困扰。当然，这是以消耗更多内存和降低速度为代价的。

无论如何，**为了启用分叉，我们只需要使用 *forkCount* 属性并将其设置为任何正值：**

```xml
<forkCount>3</forkCount>复制
```

在这里，surefire 最多会从 JVM 创建三个分支并在其中运行测试。*forkCount* 的默认值为 1，这意味着*maven-surefire-plugin*创建一个新的 JVM 进程来执行一个 Maven 模块中的所有测试。

forkCount 属性支持与*-T* 相同的*语法*。也就是说，如果我们将*C* 附加到该值，该值将乘以我们系统中可用 CPU 内核的数量。例如：

```xml
<forkCount>2.5C</forkCount>复制
```

然后在两核机器上，Surefire 最多可以创建五个 fork 用于并行测试执行。

默认情况下，**Surefire 会将创建的分支重用于其他测试**。但是，如果我们将 *reuseForks* 属性设置为 *false*，它将在运行一个测试类后销毁每个 fork。

此外，为了禁用分叉，我们可以将 *forkCount* 设置为零。

## 六，结论

总而言之，我们首先启用多线程行为并使用*并行*参数定义并行度。随后，我们对 Surefire 应创建的线程数进行了限制。稍后，我们设置超时参数来控制测试执行时间。

最后，我们研究了如何减少构建执行时间，从而在多模块 Maven 项目中测试执行时间。