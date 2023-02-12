## 1. 概述

在之前的文章中，我们了解到[AtomicStampedReference可以防止ABA问题](https://www.baeldung.com/cs/aba-concurrency)。

在本教程中，我们将更深入地了解如何最好地使用它。

## 2. 为什么我们需要AtomicStampedReference？

首先，**AtomicStampedReference为我们提供了一个对象引用变量和一个我们可以原子读写的戳记。我们可以将戳记看作时间戳或版本号**。

简单地说，**添加戳记可以让我们检测到另一个线程何时将共享引用从原始引用A更改为新的引用B，然后又更改回原始引用A**。

让我们看看它在实践中的表现。

## 3. 银行账户示例

考虑一个有两条数据的银行账户：余额balance和上次修改日期lastModifyDate。每次更改余额时都会更新上次修改日期，通过观察这个上次修改日期，我们可以知道该帐户已经更新。

### 3.1 读取值及其版本号

首先，让我们假设我们的引用持有账户余额：

```java
AtomicStampedReference<Integer> account = new AtomicStampedReference<>(100, 0);
```

请注意，我们初始化balance为100，版本号为0。

要访问余额，我们可以使用AtomicStampedReference.getReference()方法。

类似地，我们可以通过AtomicStampedReference.getStamp()获取版本号。

### 3.2 更改值及其版本号

现在，让我们回顾一下如何以原子方式设置AtomicStampedReference的值。

如果我们想更改账户的余额，我们需要同时更改余额和版本号：

```java
if (!account.compareAndSet(balance, balance + 100, stamp, stamp + 1)) {
    // retry
}
```

compareAndSet方法返回一个表示成功或失败的布尔值。失败意味着余额或版本号自我们上次读取以来已经发生变化。

正如我们所看到的，**使用它们的getter方法很容易获取引用对象和版本号**。

但是，如上所述，**当我们想使用CAS更新它们的值时，我们需要它们的最新版本**。要以原子方式检索这两条信息，我们需要同时获取它们。

幸运的是，AtomicStampedReference为我们提供了一个基于数组的API来实现这一点。让我们通过实现Account类的withdraw()方法来演示它的用法：

```java
public boolean withdrawal(int funds) {
    int[] stamps = new int[1];
    int current = this.account.get(stamps);
    int newStamp = this.stamp.incrementAndGet();
    return this.account.compareAndSet(current, current - funds, stamps[0], newStamp);
}
```

同样，我们可以添加deposit()方法：

```java
public boolean deposit(int funds) {
    int[] stamps = new int[1];
    int current = this.account.get(stamps);
    int newStamp = this.stamp.incrementAndGet();
    return this.account.compareAndSet(current, current + funds, stamps[0], newStamp);
}
```

我们刚刚写的内容的好处是，我们可以在取款或存款之前知道没有其他线程改变余额，甚至回到我们上次读取后的状态。

**例如，考虑以下线程交错执行**：

余额设置为100，线程1运行deposit(100)直到以下点：

```java
int[] stamps = new int[1];
int current = this.account.get(stamps);
int newStamp = this.stamp.incrementAndGet(); 
// Thread 1 is paused here
```

这意味着存款尚未完成。

然后，线程2运行deposit(100)和withdraw(100)，使余额达到200，然后又回到100。

最后，线程1运行：

```java
return this.account.compareAndSet(current, current + 100, stamps[0], newStamp);
```

线程1将成功检测到自上次读取以来其他线程已更改了帐户余额，即使余额本身与线程1读取时的余额相同。

### 3.3 测试

**测试起来很棘手，因为这取决于非常具体的线程交错**。但是，让我们至少编写一个简单的单元测试来验证存款和取款是否有效：

```java
@Test
void givenMultiThread_whenStampedAccount_thenSetBalance() throws InterruptedException {
    StampedAccount account = new StampedAccount();

    Thread t1 = new Thread(() -> {
        while (!account.deposit(100)) {
            Thread.yield();
        }
    });
    t1.start();

    Thread t2 = new Thread(() -> {
        while (!account.withdrawal(100)) {
            Thread.yield();
        }
    });
    t2.start();

    t1.join(10_000);
    t2.join(10_000);

    assertFalse(t1.isAlive());
    assertFalse(t2.isAlive());

    assertEquals(0, account.getBalance());
    assertTrue(account.getStamp() > 0);
}
```

### 3.4 选择下一个版本号

从语义上讲，戳记就像时间戳或版本号，**因此它通常总是递增的**。也可以使用随机数生成器。

这样做的原因是，如果戳记可以更改为之前的内容，这可能会破坏AtomicStampedReference的目的。**AtomicStampedReference本身并没有强制执行此约束，因此我们需要遵循这种做法**。

## 4. 总结

总之，AtomicStampedReference是一个功能强大的并发工具类，它提供了可以原子读取和更新的引用和戳记的方式。它是为ABA检测而设计的，应该优先于其他并发类，例如关注ABA问题的AtomicReference。