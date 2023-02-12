## 1. 概述

虽然Java中的[volatile](https://www.baeldung.com/java-volatile)关键字通常可以确保线程安全，但情况并非总是如此。

在本教程中，我们将了解共享volatile变量可能导致争用条件的场景。

## 2. 什么是volatile变量？

与其他变量不同，volatile变量是写入主内存和从主内存读取的。**CPU不会缓存volatile变量的值**。

让我们看看如何声明一个volatile变量：

```java
static volatile int count = 0;
```

## 3. volatile变量的性质

在本节中，我们将了解volatile变量的一些重要特性。

### 3.1 可见性保证

假设我们有两个线程，在不同的CPU上运行，访问一个共享的非volatile变量。让我们进一步假设第一个线程正在写入一个变量，而第二个线程正在读取同一个变量。

出于性能原因，每个线程都将变量的值从主内存复制到其各自的CPU缓存中。

**对于非volatile变量，JVM不保证何时将值从缓存写回主内存**。

如果来自第一个线程的更新值没有立即刷新回主内存，则第二个线程可能最终会读取较旧的值。

下图描述了上述场景：

<img src="../assets/img.png">

这里，第一个线程已经将变量count的值更新为5。但是，更新后的值不会立即刷新回主内存。因此，第二个线程读取较旧的值。在多线程环境中，这可能会导致错误的结果。

另一方面，**如果我们将count变量声明为volatile，则每个线程都会在主内存中看到其最新更新的值，而不会有任何延迟**。

这称为volatile关键字的可见性保证。它有助于避免上述数据不一致问题。

### 3.2 Happens-Before保证

JVM和CPU有时会重新排序独立的指令并并行执行它们以提高性能。

例如，让我们看两条独立且可以同时运行的指令：

```java
a = b + c;
d = d + 1;
```

但是，**有些指令不能并行执行，因为后一条指令取决于前一条指令的结果**：

```java
a = b + c;
d = a + e;
```

此外，还可以对独立指令进行重新排序。这可能会导致多线程应用程序出现不正确的行为。

假设我们有两个线程访问两个不同的变量：

```java
int num = 10;
boolean flag = false;
```

此外，让我们假设第一个线程正在递增num的值，然后将flag设置为true，而第二个线程等待直到flag设置为true。一旦flag的值设置为true，第二个线程就会读取num的值。

因此，第一个线程应按以下顺序执行指令：

```java
num = num + 10;
flag = true;
```

但是，假设CPU将指令重新排序为：

```java
flag = true;
num = num + 10;
```

在这种情况下，一旦flag设置为true，第二个线程就会开始执行。并且由于变量num尚未更新，所以第二个线程会读取num的旧值，即10。这会导致不正确的结果。

但是，如果我们将flag声明为volatile，则不会发生上述指令重新排序。

**对变量应用volatile关键字可以通过提供happens-before保证来防止指令重新排序**。

这样可以确保在写入volatile变量之前的所有指令都不会被重新排序为在写入之后发生。同样，读取volatile变量之后的指令不能重新排序为发生在它之前。

## 4. volatile关键字何时提供线程安全？

volatile关键字在两种  多线程场景中很有用：

+ 当只有一个线程写入volatile变量，而其他线程读取其值时。因此，读取线程可以看到变量的最新值。
+ 当多个线程写入共享变量时，操作是原子的。这意味着写入的新值不依赖于先前的值。

## 5. volatile何时不能提供线程安全？

volatile关键字是一种轻量级的同步机制。

与同步方法或块不同，它不会在一个线程处理临界区时让其他线程等待。因此，**在对共享变量执行非原子操作或复合操作时，volatile关键字不提供线程安全**。

像递增和递减这样的操作是复合操作。这些操作在内部涉及三个步骤：读取变量的值，更新它，然后将更新的值写回内存。

读取值和将新值写回内存之间的短时间间隔可能会产生争用条件。在该时间间隔内，处理同一变量的其他线程可能会读取旧值并对其进行操作。

此外，如果多个线程对同一个共享变量执行非原子操作，它们可能会覆盖彼此的结果。

因此，**在线程需要首先读取共享变量的值以计算出下一个值的情况下，将变量声明为volatile不起作用**。

## 6. 示例

现在，我们将尝试通过一个示例来理解上述情况，即在将变量声明为volatile时不是线程安全的。

为此，我们将声明一个名为count的共享volatile变量并将其初始化为0。我们还将定义一个方法来自增这个变量：

```java
public class VolatileVarNotThreadSafe {
    private static volatile int count = 0;

    public static void increment() {
        count++;
    }

    public static int getCount() {
        return count;
    }
}
```

接下来，我们将创建两个线程t1和t2。这些线程调用上述increment操作一千次：

```java
public class VolatileVarNotThreadSafe {
    private static final int MAX_LIMIT = 1000;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int index = 0; index < MAX_LIMIT; index++) {
                increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int index = 0; index < MAX_LIMIT; index++) {
                increment();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
```

从上面的程序中，**我们可能预期count变量的最终值为2000。但是，每次执行程序时，结果都会有所不同。有时，它会打印出“正确”的值(2000)，有时则不会**。

让我们看一下运行示例程序时得到的两个不同输出：

```shell
...value of counter variable: 1978

...value of counter variable: 2000
```

上述不可预测的行为是因为**两个线程都在对共享count变量执行递增操作**。如前所述，**自增操作不是原子操作**。它执行三个操作-读取、更新，然后将变量的新值写入主内存。因此，当t1和t2同时运行时，很有可能会发生这些操作的交错执行。

假设t1和t2同时运行，t1对count变量执行递增操作。但是，在将更新后的值写回主内存之前，线程t2从主内存中读取count变量的值。**在这种情况下，t2将读取一个旧值并对其执行递增操作。这可能会导致更新到主内存的count变量的值不正确**。因此，结果将与预期2000的不同。

## 7. 总结

在本文中，我们看到将共享变量声明为volatile并不总是线程安全的。

我们了解到，要为非原子操作提供线程安全并避免争用条件，使用同步方法、同步块、或原子变量都是可行的解决方案。