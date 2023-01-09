## 1 . 概述

在本文中，我们将研究来自 Guava 库的[EvictingQueue](https://google.github.io/guava/releases/19.0/api/docs/com/google/common/collect/EvictingQueue.html)和[MinMaxPriorityQueue构造。](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/collect/MinMaxPriorityQueue.html) EvictingQueue是循环缓冲区概念的实现。MinMaxPriorityQueue使用提供的比较器让我们可以访问它的最低和最大元素。

## 2.驱逐队列

让我们从构造开始——构造队列实例时，我们需要提供最大队列大小作为参数。

当我们想向EvictingQueue添加一个新项目时，队列已满，它会自动从其 head 中驱逐一个元素。

与标准队列行为相比，将元素添加到完整队列不会阻塞，但会移除头部元素并向尾部添加新项目。

我们可以将EvictingQueue想象成一个环，我们以仅追加方式向其中插入元素。如果我们要添加新元素的位置上有一个元素，我们只需覆盖给定位置的现有元素即可。

让我们构造一个最大大小为 10 的EvictingQueue实例。接下来，我们将向其中添加 10 个元素：

```java
Queue<Integer> evictingQueue = EvictingQueue.create(10);

IntStream.range(0, 10)
  .forEach(evictingQueue::add);

assertThat(evictingQueue)
  .containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
```

如果我们有标准队列实现，将新项目添加到完整队列会阻塞生产者。

EvictingQueue实现不是这种情况。向其添加新元素将导致头部从中移除，新元素将添加到尾部：

```java
evictingQueue.add(100);

assertThat(evictingQueue)
  .containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 100);
```

通过使用EvictingQueue作为循环缓冲区，我们可以创建非常高效的并发程序。

## 3.最小最大优先级队列

MinMaxPriorityQueue提供对其最小和最大元素的恒定时间访问。

为了获得最少的元素，我们需要调用peekFirst()方法。为了获得最大的元素，我们可以调用peekLast()方法。请注意，这些不会从队列中删除元素，它们只会检索它。

元素的排序由需要传递给此队列的构造函数的比较器完成。

假设我们有一个CustomClass类，它有一个整数类型的值字段：

```java
class CustomClass {
    private Integer value;

    // standard constructor, getters and setters
}
```

让我们创建一个将在int类型上使用比较器的MinMaxPriorityQueue 。接下来，我们将 10 个CustomClass类型的对象添加到队列中：

```java
MinMaxPriorityQueue<CustomClass> queue = MinMaxPriorityQueue
  .orderedBy(Comparator.comparing(CustomClass::getValue))
  .maximumSize(10)
  .create();

IntStream
  .iterate(10, i -> i - 1)
  .limit(10)
  .forEach(i -> queue.add(new CustomClass(i)));
```

由于MinMaxPriorityQueue和传递的Comparator的特性，队列头部的元素将等于 1，队列尾部的元素将等于 10：

```java
assertThat(
  queue.peekFirst().getValue()).isEqualTo(1);
assertThat(
  queue.peekLast().getValue()).isEqualTo(10);
```

由于我们的队列容量为 10，并且我们添加了 10 个元素，因此队列已满。向其中添加新元素将导致删除队列中的最后一个元素。让我们添加一个值为-1的CustomClass：

```java
queue.add(new CustomClass(-1));
```

在该操作之后，队列中的最后一个元素将被删除，它尾部的新项目将等于 9。新的头部将是 -1，因为根据我们传递的比较器，这是新的最小元素构造我们的队列：

```java
assertThat(
  queue.peekFirst().getValue()).isEqualTo(-1);
assertThat(
  queue.peekLast().getValue()).isEqualTo(9);
```

根据MinMaxPriorityQueue 的规范，如果队列已满，添加一个大于当前最大元素的元素将删除相同的元素——有效地忽略它。

让我们添加一个 100 的数字并测试该项目在该操作之后是否在队列中：

```java
queue.add(new CustomClass(100));
assertThat(queue.peekFirst().getValue())
  .isEqualTo(-1);
assertThat(queue.peekLast().getValue())
  .isEqualTo(9);
```

正如我们看到的，队列中的第一个元素仍然等于 -1，最后一个元素等于 9。因此，添加一个整数被忽略，因为它大于队列中已经最大的元素。

## 4. 总结

在本文中，我们了解了 Guava 库中的EvictingQueue和MinMaxPriorityQueue构造。

我们看到了如何使用EvictingQueue作为循环缓冲区来实现非常高效的程序。

我们将MinMaxPriorityQueue与Comparator结合使用，以恒定时间访问其最小和最大元素。

记住两个呈现队列的特征很重要，因为向它们添加新元素将覆盖队列中已有的元素。这与标准队列实现相反，在标准队列实现中，向完整队列添加新元素将阻塞生产者线程或抛出异常。