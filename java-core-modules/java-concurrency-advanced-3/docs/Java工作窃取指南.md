## 1. 概述

在本教程中，我们将了解**Java中工作窃取的概念**。

## 2. 什么是工作窃取？

Java中引入了工作窃取，目的是**减少多线程应用程序中的争用**。这是使用[fork/join框架](https://www.baeldung.com/java-fork-join)完成的。

### 2.1 分而治之的方法

在fork/join框架中，**问题或任务被递归地分解为子任务**。然后单独解决子任务，将子结果组合起来形成最终结果：

```java
Result solve(Problem problem) {
    if (problem is small)
        directly solve problem
    else {
        split problem into independent parts
        fork new subtasks to solve each part
        join all subtasks
        compose result from subresults
    }
}
```

### 2.2 工作线程

**分解的任务在[线程池](https://www.baeldung.com/thread-pool-java-and-guava)提供的工作线程的帮助下得到解决**。每个工作线程都有它负责的子任务，这些任务存储在双端队列([deque](https://www.baeldung.com/java-queue#3-deques))中。

每个工作线程通过不断地从deque头部弹出一个子任务来从其deque中获取子任务。当一个工作线程的deque为空时，意味着所有的子任务都已弹出并完成。

此时，工作线程随机选择一个它可以“窃取”工作的对等线程池线程。然后，它使用先进先出的方法(FIFO)从被窃取者deque的尾部获取子任务。

## 3. Fork/Join框架实现

**我们可以使用[ForkJoinPool](https://www.baeldung.com/java-fork-join)类或[Executors](https://www.baeldung.com/java-executor-service-tutorial)类创建工作窃取线程池**：

```java
ForkJoinPool commonPool = ForkJoinPool.commonPool();
ExecutorService workStealingPool = Executors.newWorkStealingPool();
```

Executors类有一个重载的newWorkStealingPool方法，该方法接收表示**并行级别**的整数参数。

Executors.newWorkStealingPool()是ForkJoinPool.commonPool()的抽象。唯一的区别是Executors.newWorkStealingPool()以异步模式创建线程一个池，而ForkJoinPool.commonPool则不会。

## 4. 同步线程池与异步线程池

**ForkJoinPool.commonPool使用后进先出(LIFO)队列配置，而Executors.newWorkStealingPool使用先进先出(FIFO)方法**。

根据[Doug Lea](http://gee.cs.oswego.edu/dl/papers/fj.pdf)的说法，FIFO方法比LIFO具有以下优势：

+ 它通过让窃取者作为所有者在deque的另一端操作来减少争用
+ 它利用了递归分治算法的特性来尽早生成“大型”任务

上面的第二点意味着可以通过窃取它的线程进一步分解一个较旧的被盗任务。

根据[Java文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ForkJoinPool.html)，将asyncMode设置为true可能适合用于从未加入的事件式任务。

## 5. 案例-查找素数

我们将使用从数字集合中查找素数的示例来展示**工作窃取框架的计算时间优势**。我们还将展示使用同步线程池和异步线程池之间的区别。

### 5.1 素数问题

从数字集合中查找素数可能是一个计算成本高昂的过程，这主要是由于数字集合的大小。

这里通过下面的PrimeNumbers类帮助我们查找素数：

```java
public class PrimeNumbers extends RecursiveAction {
    private final int lowerBound;
    private final int upperBound;
    private final int granularity;
    static final List<Integer> GRANULARITIES = Arrays.asList(1, 10, 100, 1000, 10000);
    private final AtomicInteger noOfPrimeNumbers;

    PrimeNumbers(int lowerBound, int upperBound, int granularity, AtomicInteger noOfPrimeNumbers) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.granularity = granularity;
        this.noOfPrimeNumbers = noOfPrimeNumbers;
    }

    // other constructors and methods

    private List<PrimeNumbers> subTasks() {
        List<PrimeNumbers> subTasks = new ArrayList<>();

        for (int i = 1; i <= this.upperBound / granularity; i++) {
            int upper = i * granularity;
            int lower = (upper - granularity) + 1;
            subTasks.add(new PrimeNumbers(lower, upper, noOfPrimeNumbers));
        }
        return subTasks;
    }

    @Override
    protected void compute() {
        if (((upperBound + 1) - lowerBound) > granularity)
            ForkJoinTask.invokeAll(subTasks());
        else
            findPrimeNumbers();
    }

    void findPrimeNumbers() {
        for (int num = lowerBound; num <= upperBound; num++)
            if (isPrime(num))
                noOfPrimeNumbers.getAndIncrement();
    }

    private boolean isPrime(int number) {
        if (number == 2)
            return true;

        if (number == 1 || number % 2 == 0)
            return false;

        int noOfNaturalNumbers = 0;

        for (int i = 1; i <= number; i++)
            if (number % i == 0)
                noOfNaturalNumbers++;

        return noOfNaturalNumbers == 2;
    }

    public int noOfPrimeNumbers() {
        return noOfPrimeNumbers.intValue();
    }
}
```

关于这个类需要注意的一些重要事项：

+ 它扩展了RecursiveAction，RecursiveAction允许我们使用线程池实现计算任务中使用的compute()方法
+ 它根据granularity的值递归地将任务分解为子任务
+ 构造函数接收作为lower和upper参数，这些值控制我们要确定素数的数字范围
+ 它使我们能够使用工作窃取线程池或单个线程来确定素数

### 5.2 使用线程池更快地解决问题

让我们以单线程方式确定素数，并使用工作窃取线程池。

首先，让我们看看**单线程方法**：

```java
PrimeNumbers primes = new PrimeNumbers(10000);
primes.findPrimeNumbers();
```

然后，**ForkJoinPool.commonPool()方法**：

```java
PrimeNumbers primes = new PrimeNumbers(10000);
ForkJoinPool pool = ForkJoinPool.commonPool();
pool.invoke(primes);
pool.shutdown();
```

最后，我们来看看**Executors.newWorkStealingPool()方法**：

```java
PrimeNumbers primes = new PrimeNumbers(10000);
int parallelism = ForkJoinPool.getCommonPoolParallelism();
ForkJoinPool stealer = (ForkJoinPool) Executors.newWorkStealingPool(parallelism);
stealer.invoke(primes);
stealer.shutdown();
```

我们使用ForkJoinPool类的invoke()方法将任务传递给线程池。此方法接收RecursiveAction子类的实例。通过[Java Microbench Harness](https://www.baeldung.com/java-microbenchmark-harness)，我们根据每个操作的平均时间对这些不同的方法进行了基准测试：

```shell
# Run complete. Total time: 00:04:50

Benchmark                                                      Mode  Cnt    Score   Error  Units
PrimeNumbersUnitTest.Benchmarker.commonPoolBenchmark           avgt   20  119.885 ± 9.917  ms/op
PrimeNumbersUnitTest.Benchmarker.newWorkStealingPoolBenchmark  avgt   20  119.791 ± 7.811  ms/op
PrimeNumbersUnitTest.Benchmarker.singleThread                  avgt   20  475.964 ± 7.929  ms/op
```

很明显，**ForkJoinPool.commonPool和Executors.newWorkStealingPool都允许我们比使用单线程方法更快地确定素数**。

fork/join池框架允许我们将任务分解为子任务。我们将10000个整数的集合分解为1-100、101-200、201-300等批次。然后我们确定每个批次的素数，并使用我们的noOfPrimeNumbers方法得到素数的总数。

### 5.3 工作窃取用于计算

使用同步线程池，**只要任务仍在进行中，ForkJoinPool.commonPool就会将线程放入池中。因此，工作窃取的级别不依赖于任务粒度级别**。

**异步Executors.newWorkStealingPool更受管理，允许工作窃取的级别取决于任务粒度级别**。

我们使用ForkJoinPool类的getStealCount方法获得工作窃取的级别：

```java
long steals = forkJoinPool.getStealCount();
```

确定Executors.newWorkStealingPool和ForkJoinPool.commonPool的工作窃取计数会给我们不同的行为：

```shell
Executors.newWorkStealingPool ->
Granularity: [1], Steals: [6564]
Granularity: [10], Steals: [572]
Granularity: [100], Steals: [56]
Granularity: [1000], Steals: [60]
Granularity: [10000], Steals: [1]

ForkJoinPool.commonPool ->
Granularity: [1], Steals: [6923]
Granularity: [10], Steals: [7540]
Granularity: [100], Steals: [7605]
Granularity: [1000], Steals: [7681]
Granularity: [10000], Steals: [7681]
```

**当Executors.newWorkStealingPool的粒度从细变粗(1到10000)时，工作窃取的级别会降低**。因此，当任务未被分解时，窃取计数为1(粒度为10000)。

**ForkJoinPool.commonPool有不同的行为**。工作窃取的级别总是很高，并且不受任务粒度变化的太大影响。

从技术上讲，我们的素数示例是支持事件式任务异步处理的示例。这是因为我们的实现不强制执行结果的拼接。

可以证明Executors.newWorkStealingPool在解决问题时提供了资源的最佳使用。

## 6. 总结

在本文中，我们研究了工作窃取以及如何使用fork/join框架应用它，我们还研究了工作窃取的示例以及它如何改进处理时间和资源使用。