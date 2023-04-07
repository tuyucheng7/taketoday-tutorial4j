## 1.概述

在本教程中，我们将讨论来自JavaCollectionAPI的不同集合的性能。当我们谈论集合时，通常会想到List、Map和Set数据结构，以及它们的常见实现。

首先，我们将了解常见操作的Big-O复杂性见解。然后我们将显示一些收集操作的运行时间的真实数字。

## 2.时间复杂度

通常，当我们谈论时间复杂度时，我们指的是大O表示法。简而言之，该符号描述了执行算法的时间如何随着输入大小而增长。

有用的文章可用于了解有关Big-O符号[理论](https://www.baeldung.com/big-o-notation)和实际[Java示例](https://www.baeldung.com/java-algorithm-complexity)的更多信息。

## 3.清单

让我们从一个简单的列表开始，它是一个有序的集合。

在这里，我们将查看ArrayList、LinkedList和CopyOnWriteArrayList实现的性能概述。

### 3.1.数组列表

Java中的ArrayList由数组支持。这有助于理解其实现的内部逻辑。[本文](https://www.baeldung.com/java-arraylist)提供了更全面的ArrayList指南。

因此，让我们首先关注高层常见操作的时间复杂度：

-   add()–花费O(1)时间；然而，最坏的情况是，当必须创建一个新数组并将所有元素到它时，它是O(n)
-   add(index,element)–平均运行时间为O(n)
-   get()–始终是常数时间O(1)操作
-   remove()–在线性O(n)时间内运行。我们必须遍历整个数组以找到符合删除条件的元素。
-   indexOf()–也以线性时间运行。它遍历内部数组并逐个检查每个元素，因此此操作的时间复杂度始终需要O(n)时间。
-   contains()–实现基于indexOf()，因此它也将在O(n)时间内运行。

### 3.2.CopyOnWriteArrayList

List接口的这种实现在处理多线程应用程序时很有用。它是线程安全的，并且在[此处的本指南](https://www.baeldung.com/java-copy-on-write-arraylist)中有很好的解释。

这是CopyOnWriteArrayList的Big-O表示法性能概述：

-   add()–取决于我们添加值的位置，所以复杂度是O(n)
-   get()–是O(1)常数时间操作
-   remove()–花费O(n)时间
-   contains()–同样，复杂度为O(n)

如我们所见，由于add()方法的性能特征，使用此集合的成本非常高。

### 3.3.链表

LinkedList是一种线性数据结构，由保存数据字段的节点和对另一个节点的引用组成。有关LinkedList的更多特性和功能，请在[此处查看这篇文章](https://www.baeldung.com/java-linkedlist)。

让我们展示执行一些基本操作所需的平均时间估计：

-   add()–将一个元素追加到列表的末尾。它只更新尾部，因此，它是O(1)常数时间复杂度。
-   add(index,element)–平均运行时间为O(n)
-   get()–搜索元素需要O(n)时间。
-   remove(element)–要删除一个元素，我们首先需要找到它。这个操作是O(n)。
-   remove(index)–要按索引删除元素，我们首先需要从头开始跟踪链接；因此，整体复杂度为O(n)。
-   contains()–也有O(n)时间复杂度

### 3.4.预热JVM

现在，为了证明这个理论，让我们来玩玩实际数据。更准确地说，我们将展示最常见的集合操作的JMH(JavaMicrobenchmarkHarness)测试结果。

如果我们不熟悉JMH工具，我们可以查看这个[有用的指南](https://www.baeldung.com/java-microbenchmark-harness)。

首先，我们将介绍基准测试的主要参数：

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 10)
public class ArrayListBenchmark {
}
```

然后我们将预热迭代次数设置为10。请注意，我们还希望看到以微秒为单位显示的结果的平均运行时间。

### 3.5.基准测试

现在是时候运行我们的性能测试了。首先，我们将从ArrayList开始：

```java
@State(Scope.Thread)
public static class MyState {

    List<Employee> employeeList = new ArrayList<>();

    long iterations = 100000;

    Employee employee = new Employee(100L, "Harry");

    int employeeIndex = -1;

    @Setup(Level.Trial)
    public void setUp() {
        for (long i = 0; i < iterations; i++) {
            employeeList.add(new Employee(i, "John"));
        }

        employeeList.add(employee);
        employeeIndex = employeeList.indexOf(employee);
    }
}
```

在我们的ArrayListBenchmark中，我们添加了State类来保存初始数据。

在这里，我们创建了一个Employee对象的ArrayList。然后我们在setUp()方法中用100.000个项目初始化它。@State表示@Benchmark测试可以完全访问同一线程中声明的变量。

最后，是时候为add()、contains()、indexOf()、remove()和get()方法添加基准测试了：

```java
@Benchmark
public void testAdd(ArrayListBenchmark.MyState state) {
    state.employeeList.add(new Employee(state.iterations + 1, "John"));
}

@Benchmark
public void testAddAt(ArrayListBenchmark.MyState state) {
    state.employeeList.add((int) (state.iterations), new Employee(state.iterations, "John"));
}

@Benchmark
public boolean testContains(ArrayListBenchmark.MyState state) {
    return state.employeeList.contains(state.employee);
}

@Benchmark
public int testIndexOf(ArrayListBenchmark.MyState state) {
    return state.employeeList.indexOf(state.employee);
}

@Benchmark
public Employee testGet(ArrayListBenchmark.MyState state) {
    return state.employeeList.get(state.employeeIndex);
}

@Benchmark
public boolean testRemove(ArrayListBenchmark.MyState state) {
    return state.employeeList.remove(state.employee);
}
```

### 3.6.试验结果

所有结果都以微秒为单位显示：

```plaintext
Benchmark                        Mode  Cnt     Score     Error
ArrayListBenchmark.testAdd       avgt   20     2.296 ±   0.007
ArrayListBenchmark.testAddAt     avgt   20   101.092 ±  14.145
ArrayListBenchmark.testContains  avgt   20   709.404 ±  64.331
ArrayListBenchmark.testGet       avgt   20     0.007 ±   0.001
ArrayListBenchmark.testIndexOf   avgt   20   717.158 ±  58.782
ArrayListBenchmark.testRemove    avgt   20   624.856 ±  51.101
```

从结果中，我们了解到testContains()和testIndexOf()方法几乎同时运行。我们还可以从其余结果中清楚地看到testAdd()和testGet()方法得分之间的巨大差异。添加一个元素需要2.296微秒，获取一个元素需要0.007微秒的操作。

此外，搜索或删除一个元素大约需要700微秒。这些数字是理论部分的证明，其中我们了解到add()和get()的时间复杂度为O(1)，其他方法为O(n)。在我们的示例中n=10.000个元素。

同样，我们可以为CopyOnWriteArrayList集合编写相同的测试。我们需要做的就是将employeeList中的ArrayList替换为CopyOnWriteArrayList实例。

以下是基准测试的结果：

```plaintext
Benchmark                          Mode  Cnt    Score     Error
CopyOnWriteBenchmark.testAdd       avgt   20  652.189 ±  36.641
CopyOnWriteBenchmark.testAddAt     avgt   20  897.258 ±  35.363
CopyOnWriteBenchmark.testContains  avgt   20  537.098 ±  54.235
CopyOnWriteBenchmark.testGet       avgt   20    0.006 ±   0.001
CopyOnWriteBenchmark.testIndexOf   avgt   20  547.207 ±  48.904
CopyOnWriteBenchmark.testRemove    avgt   20  648.162 ± 138.379
```

在这里，数字再次证实了这一理论。正如我们所见，testGet()平均运行时间为0.006毫秒，我们可以将其视为O(1)。与ArrayList相比，我们还注意到testAdd()方法结果之间的显着差异，因为这里add()方法的复杂度为O(n)与ArrayList的O(1)相比。

我们可以清楚地看到时间的线性增长，因为性能数字是878.166与0.051相比。

现在是LinkedList时间：

```plaintext
Benchmark        Cnt     Score       Error
testAdd          20     2.580        ± 0.003
testContains     20     1808.102     ± 68.155
testGet          20     1561.831     ± 70.876 
testRemove       20     0.006        ± 0.001
```

我们从分数可以看出，LinkedList中添加和删除元素是相当快的。

此外，添加/删除和获取/包含操作之间存在显着的性能差距。

## 4.地图

在最新的JDK版本中，我们见证了Map实现的显着性能改进，例如将LinkedList替换为HashMap中的平衡树节点结构，以及LinkedHashMap内部实现。这将HashMap冲突期间元素查找最坏情况的时间从O(n)缩短到O(log(n))。

但是，如果我们实施适当的.equals()和.hashcode()方法，则不太可能发生冲突。

要了解有关HashMap冲突的更多信息，请查看[这篇](https://www.baeldung.com/java-hashmap)文章。从本文中，我们还将了解到从HashMap存储和检索元素需要常数O(1)时间。

### 4.1.测试O(1)操作

现在让我们看看一些实际数字。首先，HashMap：

```plaintext
Benchmark                         Mode  Cnt  Score   Error
HashMapBenchmark.testContainsKey  avgt   20  0.009 ± 0.002
HashMapBenchmark.testGet          avgt   20  0.011 ± 0.001
HashMapBenchmark.testPut          avgt   20  0.019 ± 0.002
HashMapBenchmark.testRemove       avgt   20  0.010 ± 0.001
```

正如我们所见，这些数字证明了运行上面列出的方法的O(1)常数时间。现在让我们将HashMap测试分数与其他Map实例分数进行比较。

对于所有列出的方法，HashMap、LinkedHashMap、IdentityHashMap、WeakHashMap、EnumMap和ConcurrentHashMap的复杂度为O(1)。

让我们以表格的形式展示剩余考试成绩的结果：

```plaintext
Benchmark      LinkedHashMap  IdentityHashMap  WeakHashMap  ConcurrentHashMap
testContainsKey    0.008         0.009          0.014          0.011
testGet            0.011         0.109          0.019          0.012
testPut            0.020         0.013          0.020          0.031
testRemove         0.011         0.115          0.021          0.019
```

从输出数字，我们可以确认O(1)时间复杂度的说法。

### 4.2.测试O(log(n))操作

对于树结构TreeMap和ConcurrentSkipListMap，put()、get()、remove()和containsKey()操作时间为O(log(n))。

在这里，我们要确保我们的性能测试将大致以对数时间运行。因此，我们将使用n=1000、10,000、100,000、1,000,000个项目连续初始化地图。

在这种情况下，我们对执行的总时间感兴趣：

```plaintext
items count (n)         1000      10,000     100,000   1,000,000
all tests total score   00:03:17  00:03:17   00:03:30  00:05:27

```

当n=1000时，我们总共有00:03:17毫秒的执行时间。在n=10,000时，时间几乎没有变化，为00:03:18毫秒。n=100,000在00:03:30有小幅增加。最后，当n=1,000,000时，运行在00:05:27毫秒内完成。

将运行时数字与每个n的log(n)函数进行比较后，我们可以确认这两个函数的相关性匹配。

## 5.设置

通常，Set是唯一元素的集合。在这里，我们将检查Set接口的HashSet、LinkedHashSet、EnumSet、TreeSet、CopyOnWriteArraySet和ConcurrentSkipListSet实现。

为了更好地了解HashSet的内部结构，[本指南](https://www.baeldung.com/java-hashset)可为你提供帮助。

现在让我们跳到前面来展示时间复杂度数字。对于HashSet、LinkedHashSet和EnumSet，由于内部HashMap实现，add()、remove()和contains()操作的成本恒定为O(1)时间。

同样，TreeSet对于上一组中列出的操作具有O(log(n))时间复杂度。这是因为TreeMap的实现。ConcurrentSkipListSet的时间复杂度也是O(log(n))时间，因为它基于跳跃列表数据结构。

对于CopyOnWriteArraySet，add()、remove()和contains()方法的平均时间复杂度为O(n)。

### 5.1.测试方法

现在让我们跳到我们的基准测试：

```java
@Benchmark
public boolean testAdd(SetBenchMark.MyState state) {
    return state.employeeSet.add(state.employee);
}

@Benchmark
public Boolean testContains(SetBenchMark.MyState state) {
    return state.employeeSet.contains(state.employee);
}

@Benchmark
public boolean testRemove(SetBenchMark.MyState state) {
    return state.employeeSet.remove(state.employee);
}
```

我们将保留其余的基准配置。

### 5.2.比较数字

让我们看看n=1000的HashSet和LinkedHashSet的运行时执行分数的行为；10,000；100,000个项目。

对于HashSet，数字是：

```plaintext
Benchmark      1000    10,000    100,000
.add()         0.026   0.023     0.024
.remove()      0.009   0.009     0.009
.contains()    0.009   0.009     0.010
```

同样，LinkedHashSet的结果是：

```plaintext
Benchmark      1000    10,000    100,000
.add()         0.022   0.026     0.027
.remove()      0.008   0.012     0.009
.contains()    0.008   0.013     0.009
```

正如我们所看到的，每个操作的分数几乎保持不变。当我们将它们与HashMap测试输出进行比较时，它们看起来也一样。

结果，我们确认所有经过测试的方法都以恒定的O(1)时间运行。

## 六，总结

本文介绍了Java数据结构最常见实现的时间复杂度。

我们通过JVM基准测试看到了每种类型集合的实际运行时性能。我们还比较了不同集合中相同操作的性能。因此，我们学会了如何选择合适的系列来满足我们的需求。