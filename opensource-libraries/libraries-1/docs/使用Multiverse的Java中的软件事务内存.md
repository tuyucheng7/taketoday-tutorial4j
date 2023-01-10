## 1. 概述

在本文中，我们将研究[Multiverse](https://github.com/pveentjer/Multiverse) 库——它可以帮助我们在 Java中实现软件事务内存的概念。

使用这个库的构造，我们可以在共享状态上创建一个同步机制——这是比Java核心库的标准实现更优雅和可读的解决方案。

## 2.Maven依赖

要开始，我们需要将[multiverse-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.multiverse" AND a%3A"multiverse-core")库添加到我们的 pom 中：

```xml
<dependency>
    <groupId>org.multiverse</groupId>
    <artifactId>multiverse-core</artifactId>
    <version>0.7.0</version>
</dependency>
```

## 3.多元宇宙API

让我们从一些基础知识开始。

软件事务内存 (STM) 是从 SQL 数据库世界移植过来的概念——其中每个操作都在满足ACID(原子性、一致性、隔离性、持久性)属性的事务中执行。这里只满足原子性、一致性和隔离性，因为该机制在内存中运行。

Multiverse 库中的主要接口是TxnObject——每个事务对象都需要实现它，库为我们提供了一些我们可以使用的特定子类。

需要放置在关键部分中的每个操作，只能由一个线程访问并使用任何事务对象——需要包装在StmUtils.atomic()方法中。临界区是程序中不能被多个线程同时执行的地方，因此对它的访问应该受到某种同步机制的保护。

如果事务中的操作成功，事务将被提交，新状态将可供其他线程访问。如果发生错误，事务将不会提交，因此状态不会改变。

最后，如果两个线程想要修改事务中的相同状态，则只有一个线程会成功并提交其更改。下一个线程将能够在其事务中执行其操作。

## 4. 使用STM实现账户逻辑

现在让我们看一个例子。

假设我们要使用Multiverse库提供的 STM 创建银行帐户逻辑。我们的帐户对象将具有 TxnLong 类型的lastUpadate时间戳，以及存储给定帐户的当前余额且为TxnInteger类型的余额字段。

TxnLong和TxnInteger是来自Multiverse的类。它们必须在事务中执行。否则，将抛出异常。我们需要使用StmUtils来创建事务对象的新实例：

```java
public class Account {
    private TxnLong lastUpdate;
    private TxnInteger balance;

    public Account(int balance) {
        this.lastUpdate = StmUtils.newTxnLong(System.currentTimeMillis());
        this.balance = StmUtils.newTxnInteger(balance);
    }
}
```

接下来，我们将创建adjustBy()方法——它将按给定的金额增加余额。该操作需要在事务中执行。

如果其中抛出任何异常，事务将结束而不提交任何更改：

```java
public void adjustBy(int amount) {
    adjustBy(amount, System.currentTimeMillis());
}

public void adjustBy(int amount, long date) {
    StmUtils.atomic(() -> {
        balance.increment(amount);
        lastUpdate.set(date);

        if (balance.get() <= 0) {
            throw new IllegalArgumentException("Not enough money");
        }
    });
}
```

如果我们想获得给定账户的当前余额，我们需要从 balance 字段中获取值，但它也需要用原子语义调用：

```java
public Integer getBalance() {
    return balance.atomicGet();
}
```

## 5. 测试账户

让我们测试我们的帐户逻辑。首先，我们想简单地按给定的金额减少账户余额：

```java
@Test
public void givenAccount_whenDecrement_thenShouldReturnProperValue() {
    Account a = new Account(10);
    a.adjustBy(-5);

    assertThat(a.getBalance()).isEqualTo(5);
}
```

接下来，假设我们从帐户中取款，使余额为负。该操作应抛出异常，并保持帐户完好无损，因为该操作是在事务中执行的并且未提交：

```java
@Test(expected = IllegalArgumentException.class)
public void givenAccount_whenDecrementTooMuch_thenShouldThrow() {
    // given
    Account a = new Account(10);

    // when
    a.adjustBy(-11);
}

```

现在让我们测试一个并发问题，当两个线程想要同时减少余额时可能会出现这种问题。

如果一个线程想要将它减 5 而第二个线程想要减 6，那么这两个操作之一应该失败，因为给定帐户的当前余额等于 10。

我们将向ExecutorService提交两个线程，并使用CountDownLatch同时启动它们：

```java
ExecutorService ex = Executors.newFixedThreadPool(2);
Account a = new Account(10);
CountDownLatch countDownLatch = new CountDownLatch(1);
AtomicBoolean exceptionThrown = new AtomicBoolean(false);

ex.submit(() -> {
    try {
        countDownLatch.await();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    try {
        a.adjustBy(-6);
    } catch (IllegalArgumentException e) {
        exceptionThrown.set(true);
    }
});
ex.submit(() -> {
    try {
        countDownLatch.await();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    try {
        a.adjustBy(-5);
    } catch (IllegalArgumentException e) {
        exceptionThrown.set(true);
    }
});
```

同时盯着两个动作后，其中一个会抛出异常：

```java
countDownLatch.countDown();
ex.awaitTermination(1, TimeUnit.SECONDS);
ex.shutdown();

assertTrue(exceptionThrown.get());
```

## 6. 从一个账户转账到另一个账户

假设我们要将钱从一个帐户转移到另一个帐户。我们可以在Account类上实现transferTo()方法，方法是传递我们要将给定金额转入的另一个Account ：

```java
public void transferTo(Account other, int amount) {
    StmUtils.atomic(() -> {
        long date = System.currentTimeMillis();
        adjustBy(-amount, date);
        other.adjustBy(amount, date);
    });
}
```

所有逻辑都在事务中执行。这将保证当我们想要转账的金额高于给定账户的余额时，两个账户都将完好无损，因为交易不会提交。

让我们测试传输逻辑：

```java
Account a = new Account(10);
Account b = new Account(10);

a.transferTo(b, 5);

assertThat(a.getBalance()).isEqualTo(5);
assertThat(b.getBalance()).isEqualTo(15);
```

我们只需创建两个账户，将钱从一个账户转移到另一个账户，一切都按预期进行。接下来，假设我们要转账的金额超过账户可用的金额。transferTo()调用将抛出IllegalArgumentException ，并且不会提交更改：

```java
try {
    a.transferTo(b, 20);
} catch (IllegalArgumentException e) {
    System.out.println("failed to transfer money");
}

assertThat(a.getBalance()).isEqualTo(5);
assertThat(b.getBalance()).isEqualTo(15);
```

请注意，a和b帐户的余额与调用transferTo()方法之前的余额相同。

## 7. STM 是死锁安全的

当我们使用标准的Java同步机制时，我们的逻辑很容易出现死锁，并且无法从死锁中恢复。

当我们想把钱从账户a转移到账户b时，就会发生死锁。在标准Java实现中，一个线程需要锁定帐户a，然后锁定帐户b。假设与此同时，另一个线程想要将钱从账户b转移到账户a。另一个线程锁定账户b等待账户a被解锁。

不幸的是，帐户a的锁由第一个线程持有，帐户b的锁由第二个线程持有。这种情况会导致我们的程序无限期阻塞。

幸运的是，当使用 STM 实现transferTo()逻辑时，我们无需担心死锁，因为 STM 是死锁安全的。让我们使用我们的transferTo()方法来测试它。

假设我们有两个线程。第一个线程想要将一些钱从账户a转移到账户b，第二个线程想要将一些钱从账户b转移到账户a。我们需要创建两个帐户并启动两个将同时执行transferTo()方法的线程：

```java
ExecutorService ex = Executors.newFixedThreadPool(2);
Account a = new Account(10);
Account b = new Account(10);
CountDownLatch countDownLatch = new CountDownLatch(1);

ex.submit(() -> {
    try {
        countDownLatch.await();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    a.transferTo(b, 10);
});
ex.submit(() -> {
    try {
        countDownLatch.await();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    b.transferTo(a, 1);

});
```

开始处理后，两个账户都会有正确的余额字段：

```java
countDownLatch.countDown();
ex.awaitTermination(1, TimeUnit.SECONDS);
ex.shutdown();

assertThat(a.getBalance()).isEqualTo(1);
assertThat(b.getBalance()).isEqualTo(19);
```

## 八. 总结

在本教程中，我们了解了Multiverse库，以及我们如何使用它来利用软件事务内存中的概念创建无锁和线程安全的逻辑。

我们测试了已实现逻辑的行为，发现使用 STM 的逻辑是无死锁的。