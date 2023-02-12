## 1. 概述

在本文中，我们将讨论Java[线程状态](https://www.baeldung.com/java-thread-lifecycle)-具体来说，Thread.State.WAITING。我们将研究线程进入此状态的方法以及它们之间的区别。最后，我们将仔细研究LockSupport类，该类提供了几个用于同步的静态工具方法。

## 2. 进入Thread.State.WAITING

Java提供了多种将线程置于WAITING状态的方法。

### 2.1 Object.wait()

我们可以将线程置于WAITING状态的最标准方法之一是通过[wait()方法](https://www.baeldung.com/java-wait-notify)。**当一个线程拥有一个对象的[监视器](https://www.baeldung.com/cs/monitor)时，我们可以暂停它的执行，直到另一个线程完成一些工作并使用notify()方法将其唤醒**。暂停执行时，线程处于WAITING(在对象监视器上)状态，程序的[线程转储](https://www.baeldung.com/java-thread-dump)中也报告了这一点：

```bash
"WAITING-THREAD" #11 prio=5 os_prio=0 tid=0x000000001d6ff800 nid=0x544 in Object.wait() [0x000000001de4f000]
   java.lang.Thread.State: WAITING (on object monitor)
```

### 2.2 Thread.join()

我们可以用来暂停线程执行的另一种方法是通过[join()调用](https://www.baeldung.com/java-thread-join)。**当我们的主线程需要等待工作线程先完成时，我们可以从主线程调用工作线程实例上的join()**。执行将被暂停，主线程将进入WAITING状态，从[jstack](https://www.baeldung.com/java-thread-dump#1-jstack)报告为WAITING(在对象监视器上)：

```bash
"MAIN-THREAD" #12 prio=5 os_prio=0 tid=0x000000001da4f000 nid=0x25f4 in Object.wait() [0x000000001e28e000]
   java.lang.Thread.State: WAITING (on object monitor)
```

### 2.3 LockSupport.park()

最后，我们还可以使用LockSupport类的park()静态方法将线程设置为WAITING状态。**调用park()将停止执行当前线程并将其置于WAITING状态-更具体地说，jstack报告将显示的WAITING(parking)状态**：

```bash
"PARKED-THREAD" #11 prio=5 os_prio=0 tid=0x000000001e226800 nid=0x43cc waiting on condition [0x000000001e95f000]
   java.lang.Thread.State: WAITING (parking)
```

由于我们想更好地理解线程parking和unparking，让我们仔细看看它是如何工作的。

## 3. Parking和Unparking

正如我们之前看到的，我们可以使用LockSupport类提供的工具来park和unpark线程。这个类是[Unsafe](https://www.baeldung.com/java-unsafe)类的包装器，它的大部分方法都立即委托给它。**但是，由于Unsafe被认为是内部Java API，不应使用，因此LockSupport是我们可以访问parking实用程序的官方方式**。

### 3.1 如何使用LockSupport

使用LockSupport非常简单。如果我们想停止线程的执行，我们调用park()方法。我们不必提供对线程对象本身的引用-代码会停止调用它的线程。

让我们看一个简单的parking示例：

```java
public class Application {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            int acc = 0;
            for (int i = 1; i <= 100; i++) {
                acc += i;
            }
            System.out.println("Work finished");
            LockSupport.park();
            System.out.println(acc);
        });
        t.setName("PARK-THREAD");
        t.start();
    }
}
```

我们创建了一个最小的控制台应用程序，用于累积从1到100的数字并将它们打印出来。如果我们运行它，我们会看到它打印“Work finished”而不打印结果。当然，这是因为我们之前调用了park()。

**要让PARK-THREAD完成，我们必须unpark它**。为此，我们必须使用不同的线程。我们可以使用主线程(运行main()方法的线程)或创建一个新线程。

为了简单起见，让我们使用主线程：

```java
t.setName("PARK-THREAD");
t.start();

Thread.sleep(1000);
LockSupport.unpark(t);
```

我们在主线程中添加一秒钟的睡眠，让PARK-THREAD完成累加并自行park。之后，我们通过调用unpark(Thread)来unpark它。正如预期的那样，在unparking期间，我们必须提供对要启动的线程对象的引用。

通过我们的更改，程序现在可以正确终止并打印结果5050。

### 3.2 Unpark许可

parking API的内部结构通过使用许可来工作。实际上，这类似于单个许可的[信号量](https://www.baeldung.com/java-semaphore)。park许可在内部用于管理线程的状态，**park()方法使用它，而unpark()使它可用。**

**由于每个线程只能有一个可用的许可，因此多次调用unpark()方法没有任何效果**。单个park()调用将禁用该线程。

然而，有趣的是，parked的线程等待许可变得可用以再次启用它自己。**如果在调用park()时许可已经可用，则该线程永远不会被禁用**。许可被消耗，park()调用立即返回，线程继续执行。

我们可以通过在前面的代码片段中删除对sleep()的调用来看到这种效果：

```java
// Thread.sleep(1000);
LockSupport.unpark(t);
```

如果我们再次运行我们的程序，我们会看到PARK-THREAD的执行没有延迟。这是因为我们立即调用unpark()，这使得许可可用于park()。

### 3.3 Park重载

LockSupport类包含park(Object blocker)重载方法。blocker参数是负责线程parking的同步对象。**我们提供的对象不会影响该parking过程，但它会在线程转储中报告，这可以帮助我们诊断并发问题。**

让我们更改代码以包含一个同步器对象：

```java
Object syncObj = new Object();
LockSupport.park(syncObj);
```

如果我们删除对unpark()的调用并再次运行应用程序，它就会挂起。如果我们使用jstack来查看PARK-THREAD在做什么，我们将得到：

```bash
"PARK-THREAD" #11 prio=5 os_prio=0 tid=0x000000001e401000 nid=0xfb0 waiting on condition [0x000000001eb4f000]
   java.lang.Thread.State: WAITING (parking)
        at sun.misc.Unsafe.park(Native Method)
        - parking to wait for  <0x000000076b4a8690> (a java.lang.Object)
```

正如我们所看到的，最后一行包含PARK-THREAD正在等待的对象。**这对于调试目的很有帮助，这就是为什么我们应该更倾向于park(Object)重载。**

## 4. Parking与Waiting

由于这两个API都为我们提供了类似的功能，我们应该更喜欢哪个？通常，LockSupport类及其功能被视为低级API。此外，API很容易被滥用，从而导致难以理解的死锁。**对于大多数情况，我们应该使用Thread类的wait()和join()**。

使用parking的好处是我们不需要进入synchronized块来禁用线程。这很重要，因为同步块在代码中建立了[happens-before关系](https://www.baeldung.com/java-volatile#happens-before)，这会强制刷新所有变量，如果不需要，可能会降低性能。但是，这种优化应该很少发挥作用。

## 5. 总结

在本文中，我们探讨了LockSupport类及其parking API。我们研究了如何使用它来禁用线程并在内部解释了它是如何工作的。最后，我们将它与更常见的wait()/join() API进行了比较，并展示了它们的差异。