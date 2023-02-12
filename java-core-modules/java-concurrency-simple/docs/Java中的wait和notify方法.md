## 1. 概述

在本教程中，我们将了解Java中最基本的机制之一-线程同步。

我们将首先讨论一些基本的并发相关术语和方法。

然后我们编写一个简单的应用程序来处理并发问题，目的是更好地理解wait()和notify()。

## 2. Java中的线程同步

在多线程环境中，多个线程可能会尝试修改同一资源。当然，不正确地管理线程会导致一致性问题。

### 2.1 Java中的保护块

我们可以用来协调Java中多个线程的操作的一种工具是受保护的块。这些块在恢复执行之前会检查特定条件。

考虑到这一点，我们将使用以下方法：

+ [Object.wait()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#wait())：挂起一个线程
+ [Object.notify()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#notify())：唤醒一个线程

从以下描述线程生命周期的图中，我们可以更好地理解这一点：

<img src="../assets/img_1.png">

请注意，有许多方法可以控制此生命周期。但是，在本文中，我们将只关注wait()和notify()。

## 3. wait()方法

简单地说，调用wait()会强制当前线程等待，直到某个其他线程在同一对象上调用notify()或notifyAll()。

为此，当前线程必须拥有对象的[监视器](https://www.baeldung.com/cs/monitor)(monitor)锁。根据[Java文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#notify())，这可以通过以下方式发生：

+ 当我们对给定对象执行同步实例方法时
+ 当我们在给定对象上执行同步块的主体时
+ 通过对Class类型的对象执行同步静态方法

**请注意，一次只有一个活动线程可以拥有一个对象的监视器**。

这个wait()方法包含三个重载，让我们来看看这些。

### 3.1 wait()

wait()方法使当前线程无限期地等待，直到另一个线程为此对象调用notify()或notifyAll()。

### 3.2 wait(long timeout)

使用这个方法，我们可以指定一个超时时间，在此之后线程将自动唤醒。可以使用notify()或notifyAll()在达到超时之前唤醒线程。

请注意，调用wait(0)与调用wait()的效果相同。

### 3.3 wait(long timeout, int nanos)

这是提供相同功能的另一个重载，这里唯一的区别是我们可以提供更高的精度。

总超时时间(以纳秒为单位)计算为1_000_000 * timeout + nanos。

## 4. notify()和notifyAll()

我们使用notify()方法来唤醒等待访问此对象监视器的线程。

有两种方法可以通知等待线程。

### 4.1 notify()

对于在此对象的监视器上等待的所有线程(通过使用wait()方法中的任何一个)，notify()方法通知它们中的任何一个任意唤醒。究竟选择唤醒哪个线程是不确定的，取决于实现。

由于notify()唤醒单个随机线程，我们可以使用它在执行类似任务的线程中实现互斥锁定。但在大多数情况下，实现notifyAll()会更可行。

### 4.2 notifyAll()

这个方法简单地唤醒所有在这个对象的监视器上等待的线程。

被唤醒的线程将以通常的方式竞争，就像任何其他试图在此对象上同步的线程一样。

但在我们允许它们继续执行之前，请始终**定义一个快速检查以确定继续执行线程所需的条件**。这是因为在某些情况下，线程可能会在没有收到通知的情况下被唤醒(这种情况将在稍后的示例中讨论)。

## 5. 发送-接收同步问题

现在我们了解了基础知识，让我们来看一个简单的Sender-Receiver应用程序，它将使用wait()和notify()方法来设置它们之间的同步：

+ 发送方应该向接收方发送数据包。
+ 在发送方完成发送之前，接收方无法处理数据包。
+ 同样，除非接收方已经处理了前一个数据包，否则发送方不应尝试发送另一个数据包。

让我们首先创建一个Data类，它包含将从Sender发送到Receiver的数据包。我们将使用wait()和notifyAll()来设置它们之间的同步：

```java
public class Data {
    private String packet;

    // True if receiver should wait, False if sender should wait
    private boolean transfer = true;

    public synchronized String receive() {
        while (transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
            }
        }
        transfer = true;

        String returnPacket = packet;
        notifyAll();
        return returnPacket;
    }

    public synchronized void send(String packet) {
        while (!transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread Interrupted");
            }

            transfer = false;
            this.packet = packet;
            notifyAll();
        }
    }
}
```

让我们来剖析一下上面的代码：

+ packet变量表示通过网络传输的数据。
+ 我们有一个布尔变量transfer，发送方和接收方将使用它进行同步：
    + 如果此变量为true，则接收方应等待发送方发送消息。
    + 如果为false，发送方应等待接收方接收消息。
+ 发送方使用send()方法向接收方发送数据
    + 如果transfer为false，我们将通过在此线程上调用wait()来等待。
    + 但是当它为true时，我们切换transfer的状态，设置我们的消息，并调用notifyAll()唤醒其他线程以指定发送者发送了数据，并检查它们是否可以继续执行。
+ 类似地，接收方将使用receive()方法接收数据：
    + 如果发送方将transfer设置为false，那么它才会继续，否则我们将在此线程上调用wait()。
    + 当条件满足时，我们切换transfer状态，通知所有等待的线程唤醒，并返回接收到的数据包。

### 5.1 为什么要将wait()包含在while循环中？

由于notify()和notifyAll()随机唤醒在该对象监视器上等待的线程，因此满足条件并不总是很重要。有时线程被唤醒，但条件实际上尚未满足。

我们还可以定义一个检查来避免虚假唤醒-线程可以在没有收到通知的情况下从等待中醒来。

### 5.2 为什么我们需要同步send()和receive()方法？

我们将这些方法设置为同步方法以提供内部锁。如果调用wait()方法的线程不拥有固有的锁，则会抛出错误。

现在，我们将创建Sender和Receiver，两者均实现Runnable接口，以便它们的实例可以由线程执行。

首先，我们编写Sender的逻辑实现：

```java
public class Sender implements Runnable {
    private final Data data;

    public Sender(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        String[] packets = {
              "First packet",
              "Second packet",
              "Third packet",
              "Fourth packet",
              "End"
        };

        for (String packet : packets) {
            data.send(packet);

            // Thread.sleep() to mimic heavy server-side processing
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
            }
        }
    }
}
```

让我们仔细看看这个Sender类：

+ 我们正在创建一些随机数据包，这些数据包将以packets[]数组的形式通过网络发送。
+ 对于每个数据包，我们只是调用 send()。
+ 然后我们以随机间隔调用Thread.sleep()来模拟繁重的服务器端处理。

最后，让我们实现我们的Receiver类：

```java
public class Receiver implements Runnable {
    private final Data data;

    public Receiver(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        for (String receivedMessage = data.receive(); !"End".equals(receivedMessage); receivedMessage = data.receive()) {
            System.out.println(receivedMessage);

            // Thread.sleep() to mimic heavy server-side processing
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
            }
        }
    }
}
```

在这里，我们只是在循环中调用data.receive()直到我们接收到最后一个数据包“end”。

现在让我们来看看这个应用程序的运行情况：

```java
public class NetworkDriver {

    public static void main(String[] args) {
        Data data = new Data();

        Thread sender = new Thread(new Sender(data));
        Thread receiver = new Thread(new Receiver(data));

        sender.start();
        receiver.start();
    }
}
```

当运行该类时，我们得到以下输出：

```shell
First packet
Second packet
Third packet
Fourth packet
```

**我们已经按照正确的顺序接收了所有数据包**，并成功地在我们的发送方和接收方之间建立了正确的通信。

## 6. 总结

在本文中，我们讨论了Java中的一些核心同步概念。更具体地说，我们专注于如何使用wait()和notify()来解决同步问题。最后，我们通过一个代码示例在实践中应用了这些概念。

值得一提的是，所有这些低级API，比如wait()、notify()和notifyAll()，都是运行良好的传统方法，但更高级别的机制通常更简单、更好-例如Java的原生Lock和Condition接口(在java.util.concurrent.locks包中可用)。

有关java.util.concurrent包的更多信息，请访问我们对[java.util.concurrent概述](https://www.baeldung.com/java-util-concurrent)的文章。Lock和Condition包含在[java.util.concurrent.Locks指南](https://www.baeldung.com/java-concurrent-locks)中。