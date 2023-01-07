## 1. 概述

在本快速指南中，我们将讨论java.util 中可用的contains()方法 的性能。HashSet和java.util。数组列表。它们都是用于存储和操作对象的集合。

HashSet是一个用于存储唯一元素的集合。要了解有关 HashSet 的更多信息，请查看[此链接](https://www.baeldung.com/java-hashset)。

ArrayList是java.util.List接口的流行实现。

我们在[此处](https://www.baeldung.com/java-arraylist)提供了一篇关于ArrayList的扩展文章。

## 2.HashSet.contains ()

在内部， HashSet 实现基于HashMap 实例。contains()方法 调用HashMap.containsKey(object)。

在这里，它检查对象是否在内部映射中。内部映射将数据存储在节点内部，称为存储桶。每个桶对应一个使用 hashCode() 方法生成的哈希码。所以contains() 实际上是使用 hashCode() 方法来查找 对象的 位置。

现在让我们确定查找时间复杂度。在继续之前，请确保你熟悉[Big-O 表示法](https://www.baeldung.com/big-o-notation)。

平均而言，HashSet的contains ()运行时间为O(1)。获取对象的桶位置是一个常量时间操作。考虑到可能的冲突，查找时间可能会上升到log(n)，因为内部桶结构是TreeMap。

这是对Java7 的改进，Java 7 将LinkedList用于内部桶结构。通常，散列码冲突很少见。因此我们可以将元素查找复杂度视为 O(1)。

## 3. ArrayList.c ontains()

在内部，ArrayList使用indexOf(object)方法检查对象是否在列表中。indexOf(object)方法迭代整个 数组并将每个元素与equals(object)方法进行比较。

回到复杂性分析，ArrayList。contains()方法需要O(n)时间。 因此，我们在这里查找特定对象所花费的时间取决于我们在数组中的项数。

## 4.基准测试

现在，让我们通过性能基准测试来预热 JVM。我们将使用 JMH(Java Microbenchmark Harness)OpenJDK 产品。要了解有关设置和执行的更多信息，请查看我们[有用的指南](https://www.baeldung.com/java-microbenchmark-harness)。

首先，让我们创建一个简单的CollectionsBenchmark类：

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5)
public class CollectionsBenchmark {

    @State(Scope.Thread)
    public static class MyState {
        private Set<Employee> employeeSet = new HashSet<>();
        private List<Employee> employeeList = new ArrayList<>();

        private long iterations = 1000;

        private Employee employee = new Employee(100L, "Harry");

        @Setup(Level.Trial)
        public void setUp() {

            for (long i = 0; i < iterations; i++) {
                employeeSet.add(new Employee(i, "John"));
                employeeList.add(new Employee(i, "John"));
            }

            employeeList.add(employee);
            employeeSet.add(employee);
        }
    }
}
```

在这里，我们创建并初始化HashSet和Employee 对象的ArrayList ：

```java
public class Employee {

    private Long id;
    private String name;

    // constructor and getter setters go here
}
```

我们将 employee = new Employee(100L, “Harry”) 实例作为最后一个元素添加到两个集合中。因此，我们 针对最坏的可能情况测试员工对象的查找时间。

@OutputTimeUnit(TimeUnit.NANOSECONDS)表示我们想要以纳秒为单位的结果。在我们的例子中，默认的 @Warmup迭代次数是 5 次。@BenchmarkMode设置为 Mode.AverageTime ，这意味着我们有兴趣计算平均运行时间。对于第一次执行，我们将iterations = 1000项放入我们的集合中。

之后，我们将基准方法添加到CollectionsBenchmark类中：

```java
@Benchmark
public boolean testArrayList(MyState state) {
    return state.employeeList.contains(state.employee);
}
```

这里我们检查employeeList是否包含employee对象。

同样，我们对employeeSet进行了熟悉的测试：

```java
@Benchmark
public boolean testHashSet(MyState state) {
    return state.employeeSet.contains(state.employee);
}
```

最后，我们可以运行测试：

```java
public static void main(String[] args) throws Exception {
    Options options = new OptionsBuilder()
      .include(CollectionsBenchmark.class.getSimpleName())
      .forks(1).build();
    new Runner(options).run();
}
```

以下是结果：

```plaintext
Benchmark                           Mode  Cnt     Score     Error  Units
CollectionsBenchmark.testArrayList  avgt   20  4035.646 ± 598.541  ns/op
CollectionsBenchmark.testHashSet    avgt   20     9.456 ±   0.729  ns/op
```

我们可以清楚地看到testArrayList方法的平均查找分数为4035.646 ns ，而testHashSet执行速度更快，平均为9.456 ns。

现在，让我们增加测试中的元素数量并运行它迭代 = 10.000 个项目：

```plaintext
Benchmark                           Mode  Cnt      Score       Error  Units
CollectionsBenchmark.testArrayList  avgt   20  57499.620 ± 11388.645  ns/op
CollectionsBenchmark.testHashSet    avgt   20     11.802 ±     1.164  ns/op
```

在这里，HashSet 中的contains ( )也比ArrayList具有巨大的性能优势。

## 5.总结

这篇简短的文章解释了HashSet和ArrayList集合的contains()方法的性能。在 JMH 基准测试的帮助下，我们展示了 每种类型集合的contains()的性能。

作为总结，我们可以了解到，与ArrayList相比，contains ()方法在HashSet中工作得更快。