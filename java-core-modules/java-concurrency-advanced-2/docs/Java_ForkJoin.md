## 1. 概述

fork/join框架是在Java 7中提出的。
它提供了一些工具，通过尝试使用所有可用的处理器核心来帮助加速并行处理，这是通过分治的方法来实现的。

实际上，这意味着框架首先“fork”，递归地将任务分解为更小的独立子任务，直到它们足够简单，可以异步执行为止。

之后，“join”部分开始，其中所有子任务的结果递归地连接到单个结果中，如果任务返回void，程序只需等待每个子任务执行。

为了提供有效的并行执行，fork/join框架使用了一个称为ForkJoinPool的线程池，它管理ForkJoinWorkerThread类型的工作线程。

## 2. ForkJoinPool

ForkJoinPool是该框架的核心。它是ExecutorService的一个实现，它管理工作线程，并为我们提供获取线程池状态和性能信息的工具。

工作线程一次只能执行一个任务，但ForkJoinPool不会为每个子任务创建单独的线程。
相反，池中的每个线程都有自己的双端队列(或deque)，用于存储任务。

该体系结构对于借助工作窃取算法平衡线程的工作负载至关重要。

### 2.1 工作窃取算法

简单地说，空闲线程试图从繁忙线程的deque中“窃取”工作。

默认情况下，工作线程从自己的deque头部获取任务。
当它为空时，线程从另一个繁忙线程的deque尾部或全局入口队列中获取任务，因为这可能是最多的任务所在的位置。

这种方法将线程竞争任务的可能性降至最低。它还减少了线程寻找任务的次数，因为它首先处理最大的可用任务块。

### 2.2 ForkJoinPool实例化

在Java 8中，访问ForkJoinPool实例最方便的方法是使用其静态方法commonPool()。
顾名思义，这将提供对公共池的引用，公共池是每个ForkJoinTask的默认线程池。

根据Oracle的文档，使用预定义的公共池可以减少资源消耗，因为这不鼓励为每个任务创建单独的线程池。

```text
ForkJoinPool commonPool = ForkJoinPool.commonPool();
```

在Java 7中，可以通过创建ForkJoinPool并将其分配给实用程序类的公共静态字段来实现相同的行为：

```java
public class PoolUtil {
    public static ForkJoinPool forkJoinPool = new ForkJoinPool(2);
}
```

现在我们可以轻松访问它：

```text
ForkJoinPool forkJoinPool = PoolUtil.forkJoinPool;
```

使用ForkJoinPool的构造函数，我们可以创建具有特定并行级别、线程工厂和异常处理程序的自定义线程池。
在上面的示例中，线程池的并行级别为2。这意味着该线程池将使用2个处理器核心。

## 3. ForkJoinTask<V>

ForkJoinTask是在ForkJoinPool内执行的任务的基本类型。
实际上，我们应该继承它的两个子类中的一个子类：用于void返回类型任务的RecursiveAction和用于返回值的任务的RecursiveTask。
它们都有一个抽象方法compute()，其中定义了任务的逻辑。

### 3.1 RecursiveAction

在下面的示例中，要处理的工作单元由一个名为workload的字符串表示。出于演示目的，该任务是一个无意义的任务：它只是将其输入转为大写并记录下来。

为了演示框架的fork行为，该示例使用createSubtask()方法在workload.length()大于指定THRESHOLD时拆分任务。

字符串被递归地划分为子字符串，创建基于这些子字符串的CustomRecursiveTask实例。

因此，该方法返回一个List<CustomRecursiveAction>。

使用invokeAll()方法将集合提交到ForkJoinPool：

```java
public class CustomRecursiveAction extends RecursiveAction {
    final Logger logger = LoggerFactory.getLogger(CustomRecursiveAction.class);

    private final String workLoad;
    private static final int THRESHOLD = 4;

    public CustomRecursiveAction(String workLoad) {
        this.workLoad = workLoad;
    }

    @Override
    protected void compute() {
        if (workLoad.length() > THRESHOLD)
            ForkJoinTask.invokeAll(createSubtasks());
        else
            processing(workLoad);
    }

    private Collection<CustomRecursiveAction> createSubtasks() {
        List<CustomRecursiveAction> subtasks = new ArrayList<>();

        String partOne = workLoad.substring(0, workLoad.length() / 2);
        String partTwo = workLoad.substring(workLoad.length() / 2);

        subtasks.add(new CustomRecursiveAction(partOne));
        subtasks.add(new CustomRecursiveAction(partTwo));

        return subtasks;
    }

    private void processing(String work) {
        String result = work.toUpperCase();
        logger.debug("This result - (" + result + ") - was processed by " + Thread.currentThread().getName());
    }
}
```

此模式可用于开发自己的RecursiveAction类。
为此，我们创建一个表示总工作量的对象，选择一个合适的阈值，定义一个划分工作量的方法，并定义一个执行工作量的方法。

### 3.2 RecursiveTask<V>

对于返回值的任务，此处的逻辑类似，只是每个子任务的结果统一在一个结果中：

```java
public class CustomRecursiveTask extends RecursiveTask<Integer> {
    private final int[] array;
    private static final int THRESHOLD = 20;

    public CustomRecursiveTask(int[] array) {
        this.array = array;
    }

    @Override
    protected Integer compute() {
        if (array.length > THRESHOLD)
            return ForkJoinTask.invokeAll(createSubTasks()).stream().mapToInt(ForkJoinTask::join).sum();
        else
            return processing(array);
    }

    private Collection<CustomRecursiveTask> createSubTasks() {
        List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
        dividedTasks.add(new CustomRecursiveTask(Arrays.copyOfRange(array, 0, array.length / 2)));
        dividedTasks.add(new CustomRecursiveTask(Arrays.copyOfRange(array, array.length / 2, array.length)));
        return dividedTasks;
    }

    private Integer processing(int[] array) {
        return Arrays.stream(array).filter(a -> a > 10 && a < 27).map(a -> a  10).sum();
    }
}
```

在本例中，任务由存储在CustomRecursiveTask类的array字段中的数组表示。
createSubtasks()方法递归地将任务划分为更小的任务块，直到每个任务块都小于阈值。
然后，invokeAll()方法将子任务提交到公共池，并返回Future集合。

要触发执行，需要为每个子任务调用join()方法。

在本例中，这是使用Java 8的Stream API实现的；sum()方法用于表示将子结果合并到最终结果中。

## 4. 将任务提交到ForkJoinPool

要将任务提交到线程池，可以使用几种方法。

```text
forkJoinPool.execute(customRecursiveTask);
int result = customRecursiveTask.join();
```

invoke()方法fork任务并等待结果，不需要任何手动连接：

```text
int result = forkJoinPool.invoke(customRecursiveTask);
```

invokeAll()方法是向ForkJoinPool提交一系列ForkJoinTask最方便的方法。
它将任务作为参数(两个任务、可变参数或一个集合)，然后fork按照生成的顺序返回Future对象的集合。

或者，可以使用单独的fork()和join()方法。fork()方法将任务提交到池，但不会触发其执行。
为此，必须使用join()方法。对于RecursiveAction，join()只返回null；对于RecursiveTask，它返回任务执行的结果：

```text
customRecursiveTaskFirst.fork();
result = customRecursiveTaskLast.join();
```

在我们的RecursiveTask示例中，我们使用invokeAll()方法向线程池提交一系列子任务。
我们可以使用fork()和join()完成相同的工作，不过这会影响结果的顺序。

为了避免混淆，通常最好使用invokeAll()方法向ForkJoinPool提交多个任务。

## 5. 总结

使用fork/join框架可以加快大型任务的处理速度，但要实现这一结果，应遵循以下准则：

+ 使用尽可能少的线程池：在大多数情况下，最好的决策是为每个应用程序或系统使用一个线程池。
+ 如果不需要特定的调优，则使用默认的公共线程池。
+ 使用合理的阈值将ForkJoinTask拆分为子任务。
+ 避免ForkJoinTasks中的任何阻塞。