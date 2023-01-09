## 1. 简介

在[上一篇文章中，我们开始探索](https://www.baeldung.com/guava-21-new)common.collect包中引入的新功能。

在这篇简短的文章中，我们将介绍对common.util.concurrent包的添加。

## 2.AtomicLongMap

在并发场景中，标准的HashMap可能无法很好地工作，因为它根本不是并发的。在这种特殊情况下，AtomicLongMap通过以线程安全的方式存储Long值来帮助你摆脱困境。

AtomicLongMap很久以前就在 Guava 11 中引入了。现在，添加了四个新方法。

### 2.1. accumulateAndGet()

accumulateAndGet()方法通过使用累加器函数将其与现有值合并来更新与键链接的值。然后，它返回更新后的值：

```java
@Test
public void accumulateAndGet_withLongBinaryOperator_thenSuccessful() {
    long noOfStudents = 56;
    long oldValue = courses.get(SPRING_COURSE_KEY);

    long totalNotesRequired = courses.accumulateAndGet(
      "Guava", 
      noOfStudents, 
      (x, y) -> (x  y));

    assertEquals(totalNotesRequired, oldValue  noOfStudents);
}
```

### 2.2. getAndAccumulate()

此方法具有与上面定义的类似的功能，但它返回旧值而不是更新后的值(如操作顺序所示)。

### 2.3. 更新并获取()

updateAndGet()方法使用作为第二个参数提供的指定函数更新键的当前值。然后，它返回键的更新值：

```java
@Test
public void updateAndGet_withLongUnaryOperator_thenSuccessful() {
    long beforeUpdate = courses.get(SPRING_COURSE_KEY);
    long onUpdate = courses.updateAndGet(
      "Guava",
      (x) -> (x / 2));
    long afterUpdate = courses.get(SPRING_COURSE_KEY);

    assertEquals(onUpdate, afterUpdate);
    assertEquals(afterUpdate, beforeUpdate / 2);
}
```

### 2.4. 获取更新()

此方法的工作方式与updateAndGet()非常相似，但它返回键的旧值而不是更新后的值。

## 3.监控

监视器类被视为ReentrantLock的替代品，它也更具可读性和更不容易出错。

### 3.1. 监视器.newGuard()

Guava 21 添加了一个新方法——newGuard() ——它返回一个Monitor.Guard实例，用作线程可以等待的布尔条件：

```java
public class MonitorExample {
    private List<String> students = new ArrayList<String>();
    private static final int MAX_SIZE = 100;

    private Monitor monitor = new Monitor();

    public void addToCourse(String item) throws InterruptedException {
        Monitor.Guard studentsBelowCapacity = monitor.newGuard(this::isStudentsCapacityUptoLimit);
        monitor.enterWhen(studentsBelowCapacity);
        try {
            students.add(item);
        } finally {
            monitor.leave();
        }
    }

    public Boolean isStudentsCapacityUptoLimit() {
        return students.size() > MAX_SIZE;
    }
}
```

## 4. 更多执行器

此类中没有添加任何内容，但删除了sameThreadExecutor() API。此方法自 v18.0 起已弃用，建议改用directExecutor()或newDirectExecutorService()。

## 5.ForwardingBlockingDeque

ForwardingBlockingDeque是一个现有类，已从common.collect中移出，因为BlockingQueue更像是一种并发集合类型，而不是标准集合。

## 六. 总结

Guava 21 不仅试图引入新的实用程序以跟上Java8 的步伐，而且还改进现有模型以使其更有意义。