## 1. 简介

尽管大多数时候串行执行测试都可以正常工作，但我们可能希望将它们并行化以加快速度。

在本教程中，我们将介绍如何使用JUnit和Maven的Surefire插件并行化测试。首先，我们将在单个JVM进程中运行所有测试，然后我们将在一个多模块项目中进行尝试。

## 2. Maven依赖

让我们从导入所需的依赖项开始。**我们需要使用[JUnit 4.7](https://central.sonatype.com/artifact/junit/junit/4.13.2)或更高版本以及[Surefire 2.16](https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-surefire-plugin/3.0.0-M9)或更高版本**：

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.21.0</version>
</plugin>
```

简而言之，Surefire提供了两种并行执行测试的方法：

- 单个JVM进程内的多线程
- fork多个JVM进程

## 3. 运行并行测试

**要并行运行测试，我们应该使用扩展org.junit.runners.ParentRunner的测试运行器**。

但是，即使没有声明显式测试运行器的测试也可以工作，因为默认运行器扩展了此类。

接下来，为了演示并行测试执行，我们将使用一个包含两个测试类的测试套件，每个测试类都有一些方法。事实上，任何JUnit测试套件的标准实现都可以。

### 3.1 使用Parallel参数

首先，让我们使用parallel参数在Surefire中启用并行行为。**它说明了我们希望应用并行性的粒度级别**。

可能的值为：

-   methods：在单独的线程中运行测试方法
-   classes：在单独的线程中运行测试类
-   classesAndMethods：在单独的线程中运行类和方法
-   suites：并行运行套件
-   suitesAndClasses：在单独的线程中运行套件和类
-   suitesAndMethods：为类和方法创建单独的线程
-   all：在单独的线程中运行套件、类和方法

在我们的示例中，我们使用all：

```xml
<configuration>
    <parallel>all</parallel>
</configuration>
```

其次，让我们定义我们希望Surefire创建的线程总数。我们可以通过两种方式做到这一点：

使用threadCount来定义Surefire将创建的最大线程数：

```xml
<threadCount>10</threadCount>
```

或者使用useUnlimitedThreads参数，其中每个CPU核心创建一个线程：

```xml
<useUnlimitedThreads>true</useUnlimitedThreads>
```

默认情况下，threadCount是按CPU核心计算的。我们可以使用参数perCoreThreadCount来启用或禁用此行为：

```xml
<perCoreThreadCount>true</perCoreThreadCount>
```

### 3.2 使用线程数限制

现在，假设我们要在方法、类和套件级别定义要创建的线程数。**我们可以使用threadCountMethods、threadCountClasses和threadCountSuites参数来做到这一点**。

让我们将这些参数与之前配置中的threadCount结合起来：

```xml
<threadCountSuites>2</threadCountSuites>
<threadCountClasses>2</threadCountClasses>
<threadCountMethods>6</threadCountMethods>
```

由于我们已将parallel设置为all，因此我们为方法、套件和类定义了线程数。但是，定义叶子参数不是强制性的。如果叶子参数被省略，Surefire会推断要使用的线程数。

例如，如果省略了threadCountMethods，那么我们只需要确保threadCount > threadCountClasses + threadCountSuites。

有时我们可能希望限制为类或套件或方法创建的线程数量，即使我们使用的线程数不受限制。

在这种情况下，我们也可以应用线程数限制：

```xml
<useUnlimitedThreads>true</useUnlimitedThreads>
<threadCountClasses>2</threadCountClasses>
```

### 3.3 设置超时

有时我们可能需要确保测试执行是有时间限制的。

**为此，我们可以使用parallelTestTimeoutForcedInSeconds参数**。这将中断当前正在运行的线程，并且在超时后不会执行任何排队的线程：

```xml
<parallelTestTimeoutForcedInSeconds>5</parallelTestTimeoutForcedInSeconds>
```

**另一种选择是使用parallelTestTimeoutInSeconds**。

在这种情况下，只有排队的线程将被阻止执行：

```xml
<parallelTestTimeoutInSeconds>3.5</parallelTestTimeoutInSeconds>
```

但是，对于这两个选项，测试将在超时后以错误消息结束。

### 3.4 注意事项

Surefire在父线程中调用带有@Parameters、@BeforeClass和@AfterClass注解的静态方法。因此，请确保在并行运行测试之前检查潜在的内存不一致或争用条件。

此外，改变共享状态的测试绝对不适合并行运行。

## 4. 多模块Maven项目中的测试执行

到目前为止，我们一直专注于在单个Maven模块中并行运行测试。

但是假设我们在一个Maven项目中有多个模块。由于这些模块是按顺序构建的，因此每个模块的测试也是按顺序执行的。

**我们可以通过使用Maven的-T参数来更改此默认行为，该参数并行构建模块**。这可以通过两种方式完成。

我们可以在构建项目时指定要使用的确切线程数：

```shell
mvn -T 4 surefire:test
```

或者使用便捷式版本并指定每个CPU核心要创建的线程数：

```shell
mvn -T 1C surefire:test
```

无论哪种方式，我们都可以加快测试速度以及构建执行时间。

## 5. Fork JVM

**通过parallel选项执行并行测试，并发发生在使用线程的JVM进程内部**。

由于线程共享相同的内存空间，因此这在内存和速度方面是有效的。但是，我们可能会遇到意外的争用条件或其他与并发相关的微妙测试失败。事实证明，共享同一个内存空间既是福也是祸。

为了防止线程级并发问题，Surefire提供了另一种并行测试执行模式：**fork和进程级并发**。fork进程的想法实际上非常简单。Surefire不是生成多个线程并在它们之间分配测试方法，而是创建新进程并执行相同的分配。

由于不同进程之间没有共享内存，因此我们不会受到那些微妙的并发错误的困扰。当然，这是以消耗更多内存和降低速度为代价的。

无论如何，**为了启用fork，我们只需要使用forkCount属性并将其设置为任何正数值**：

```xml
<forkCount>3</forkCount>
```

在这里，surefire最多会从JVM创建三个fork并在其中运行测试。forkCount的默认值为1，这意味着maven-surefire-plugin创建一个新的JVM进程来执行一个Maven模块中的所有测试。

forkCount属性支持与-T相同的语法。也就是说，如果我们将C附加到该值，则该值将乘以我们系统中可用CPU核心的数量。例如：

```xml
<forkCount>2.5C</forkCount>
```

然后在双核机器上，Surefire最多可以创建五个fork用于并行测试执行。

默认情况下，**Surefire会将创建的fork重用于其他测试**。但是，如果我们将reuseForks属性设置为false，它将在运行一个测试类后销毁每个fork。

此外，为了禁用fork，我们可以将forkCount设置为0。

## 6. 总结

总而言之，我们首先启用多线程行为并使用parallel参数定义并行度。随后，我们对Surefire应创建的线程数施加了限制。然后，我们设置超时参数来控制测试执行时间。

最后，我们研究了如何减少构建执行时间，从而减少多模块Maven项目中的测试执行时间。