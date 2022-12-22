## 1. 概述

在本文中，我们将介绍如何使用JUnit 5执行并行单元测试。
首先，我们将介绍基本配置和运行并行测试的最基本要求。接下来，我们将演示不同情况下的代码示例，最后我们介绍共享资源的同步。

并行执行测试是自5.3版本以来可作为选择加入的实验性功能。

## 2. 配置

**首先，我们需要在src/test/resources文件夹中创建一个junit-platform.properties文件以启用并行执行**。
我们通过在上述文件中添加以下属性配置来启用并行功能：

```properties
junit.jupiter.execution.parallel.enabled=true
```

让我们通过运行一些测试来检查我们的配置。首先，我们创建FirstParallelUnitTest类和两个测试方法：

```java
class FirstParallelUnitTest {

    @Test
    void first() throws Exception {
        System.out.println("FirstParallelUnitTest first() start => " + Thread.currentThread().getName());
        Thread.sleep(500);
        System.out.println("FirstParallelUnitTest first() end => " + Thread.currentThread().getName());
    }

    @Test
    void second() throws Exception {
        System.out.println("FirstParallelUnitTest second() start => " + Thread.currentThread().getName());
        Thread.sleep(500);
        System.out.println("FirstParallelUnitTest second() end => " + Thread.currentThread().getName());
    }
}
```

当我们运行测试时，控制台的输出如下所示：

```text
FirstParallelUnitTest second() start => ForkJoinPool-1-worker-1
FirstParallelUnitTest second() end => ForkJoinPool-1-worker-1
FirstParallelUnitTest first() start => ForkJoinPool-1-worker-1
FirstParallelUnitTest first() end => ForkJoinPool-1-worker-1
```

在这个输出中，我们可以注意到两点。首先，我们的测试按顺序运行。其次，我们可以看到使用了ForkJoin线程池。
**通过启用并行执行，JUnit platform开始使用ForkJoin线程池**。

**接下来，我们需要添加一个配置来利用这个线程池，也就是选择一种并行化策略。
JUnit提供了两种实现(动态和固定)和一个自定义选项来创建我们的实现**。

动态策略根据处理器/内核的数量乘以使用以下属性指定的因子参数(默认为1)确定线程数：

```text
junit.jupiter.execution.parallel.config.dynamic.factor
```

即(处理器/内核数) * (junit.jupiter.execution.parallel.config.dynamic.factor的值)。

另一方面，固定策略依赖于使用以下属性指定的预定义线程数：

```text
junit.jupiter.execution.parallel.config.fixed.parallelism
```

要使用自定义策略，我们需要实现ParallelExecutionConfigurationStrategy接口。

## 3. 测试类内部的并行化

有两种在测试类中并行执行测试的配置方法：一种是使用@Execution(ExecutionMode.CONCURRENT)注解，第二种是添加以下属性：

```properties
junit.jupiter.execution.parallel.mode.default=concurrent
```

选择你喜欢的配置方式并运行FirstParallelUnitTest类，我们可以看到以下输出：

```text
FirstParallelUnitTest second() start => ForkJoinPool-1-worker-2
FirstParallelUnitTest first() start => ForkJoinPool-1-worker-1
FirstParallelUnitTest second() end => ForkJoinPool-1-worker-2
FirstParallelUnitTest first() end => ForkJoinPool-1-worker-1
```

从输出中我们可以看到两个测试方法同时在两个不同的线程中启动。

还有一个选项可以在同一个线程中运行FirstParallelUnitTest类中的所有测试。
在当前范围内，使用并行性和相同的线程选项是不可行的，因此让我们扩展范围，在下一节中再添加一个测试类。

## 4. 模块内的测试并行化

首先创建一个类似FirstParallelUnitTest的测试类：

```java
class SecondParallelUnitTest {

    @Test
    void first() throws Exception {
        System.out.println("SecondParallelUnitTest first() start => " + Thread.currentThread().getName());
        Thread.sleep(500);
        System.out.println("SecondParallelUnitTest first() end => " + Thread.currentThread().getName());
    }

    @Test
    void second() throws Exception {
        System.out.println("SecondParallelUnitTest second() start => " + Thread.currentThread().getName());
        Thread.sleep(500);
        System.out.println("SecondParallelUnitTest second() end => " + Thread.currentThread().getName());
    }
}
```

在我们同时运行多个测试类之前，我们需要设置以下属性：

```properties
#junit-platform.properties
junit.jupiter.execution.parallel.mode.classes.default=concurrent
```

当我们运行这两个测试类时(在IDEA中我们可以按住ctrl，然后鼠标左击选择这两个类，右键选择Run就可以同时运行多个测试类)，
控制台的输出如下：

```text
FirstParallelUnitTest second() start => ForkJoinPool-1-worker-4
SecondParallelUnitTest second() start => ForkJoinPool-1-worker-3
FirstParallelUnitTest first() start => ForkJoinPool-1-worker-2
SecondParallelUnitTest first() start => ForkJoinPool-1-worker-1
FirstParallelUnitTest second() end => ForkJoinPool-1-worker-4
FirstParallelUnitTest first() end => ForkJoinPool-1-worker-2
SecondParallelUnitTest second() end => ForkJoinPool-1-worker-3
SecondParallelUnitTest first() end => ForkJoinPool-1-worker-1
```

从输出中，我们可以看到所有四个测试方法都在不同的线程中并行运行。

结合我们在本节和上一节中提到的两个属性(junit.jupiter.execution.parallel.mode.default
和junit.jupiter.execution.parallel.mode.classes.default)及其值(same_thread和concurrent)，我们可以得出四种不同的并行执行模式：

1. (same_thread, same_thread) – 所有测试都按顺序运行。
2. (same_thread, concurrent) - 同一个类中的测试按顺序运行，但多个类并行运行。
3. (concurrent, same_thread) - 同一个类中的测试并行运行，但每个类串按顺序运行。
4. (concurrent, concurrent) - 完全并行运行。

## 5. 同步

在理想情况下，我们所有的单元测试都应该是独立、互不干扰的。
但是，这通常很难实现，因为它们依赖于共享资源。因此，在并行运行测试时，我们需要同步测试中的公共资源。
JUnit 5以@ResourceLock注解的形式为我们提供了这样的机制。

同样，我们新建一个ParallelResourceLockUnitTest类：

```java
class ParallelResourceLockUnitTest {
    private List<String> resources;

    @BeforeEach
    void before() {
        resources = new ArrayList<>();
        resources.add("test");
    }

    @AfterEach
    void after() {
        resources.clear();
    }

    @Test
    @ResourceLock(value = "resources")
    void first() throws Exception {
        System.out.println("ParallelResourceLockUnitTest first() start => " + Thread.currentThread().getName());
        resources.add("first");
        System.out.println(resources);
        Thread.sleep(500);
        System.out.println("ParallelResourceLockUnitTest first() end => " + Thread.currentThread().getName());
    }

    @Test
    @ResourceLock(value = "resources")
    void second() throws Exception {
        System.out.println("ParallelResourceLockUnitTest second() start => " + Thread.currentThread().getName());
        resources.add("second");
        System.out.println(resources);
        Thread.sleep(500);
        System.out.println("ParallelResourceLockUnitTest second() end => " + Thread.currentThread().getName());
    }
}
```

**@ResourceLock允许我们指定共享的资源以及我们想要使用的锁类型(默认为ResourceAccessMode.READ_WRITE)**。
使用当前设置，JUnit engine会检测到我们的测试都使用共享资源并将按顺序执行它们：

```text
ParallelResourceLockUnitTest second() start => ForkJoinPool-1-worker-2
[test, second]
ParallelResourceLockUnitTest second() end => ForkJoinPool-1-worker-2
ParallelResourceLockUnitTest first() start => ForkJoinPool-1-worker-1
[test, first]
ParallelResourceLockUnitTest first() end => ForkJoinPool-1-worker-1
```

## 6. 总结

在本文中，我们首先介绍了如何配置测试的并行执行。接下来，介绍了有哪些可用的并行策略以及如何配置多个线程。
之后，我们介绍了不同的配置对测试执行的影响。最后，我们介绍了共享资源的同步。