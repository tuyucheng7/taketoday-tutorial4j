## 1. 概述

在本教程中，我们将研究Java中最基本的机制之一，线程同步。

我们首先讨论一些与并发相关的基本术语。

然后编写一个简单的应用程序来处理并发问题，目的是更好地理解wait()和notify()。

## 2. Java中的线程同步

在多线程环境中，多个线程可能会尝试修改同一资源。不正确地管理线程当然会导致一致性问题。

### 2.1 Java中的保护块

在Java中，我们可以用来协调多个线程的操作的一个工具是保护块。这样的代码块会在恢复执行之前检查特定条件。

考虑到这一点，我们将使用以下方法：

+ Object.wait()：挂起线程
+ Object.notify()：唤醒另一个线程

从以下描述线程生命周期的图中，我们可以更好地理解这一点：

<img src="../assets/img_1.png">

请注意，有很多方法可以控制这个生命周期。在本文中，我们将只关注wait()和notify()。

## 3. wait()方法

简单地说，调用wait()会强迫当前线程等待，直到其他线程在同一对象上调用notify()或notifyAll()。

为此，当前线程必须拥有对象的监视器(monitor)锁。根据Java文档，这可以通过以下方式实现：

+ 当我们对给定对象执行同步实例方法时
+ 当我们在给定对象上执行同步块的主体时
+ 通过对Class类型的对象执行同步静态方法

请注意，一次只有一个活动线程可以拥有对象的监视器(monitor)锁。

这个wait()方法包含三个重载。让我们看看这些。

### 3.1 wait()

wait()方法使当前线程无限期地等待，直到另一个线程为此对象调用notify()或notifyAll()。

### 3.2 wait(long timeout)

使用此方法，我们可以指定一个超时时间，在此之后线程将自动唤醒。可以使用notify()或notifyAll()在到达超时之前唤醒线程。

请注意，调用wait(0)与调用wait()地效果相同。

### 3.3 wait(long timeout, int nanos)

这是提供相同功能的另一个重载。唯一的区别是我们可以提供更高的精度。

总超时时间(以纳秒为单位)计算为1_000_000  timeout + nanos。

## 4. notify()和notifyAll()

我们使用notify()方法来唤醒等待访问该对象监视器锁的线程。

有两种唤醒等待线程的方法。

### 4.1 notify()

对于在该对象的监视器锁上等待的所有线程(通过使用wait()方法中的任何一个)，notify()方法通知它们中的任何一个任意唤醒。
要唤醒哪个线程的选择是不确定的，取决于实现。

由于notify()唤醒单个随机线程，因此我们可以使用它在线程执行类似任务时实现互斥锁定。但在大多数情况下，实现notifyAll()更可行。

### 4.2 notifyAll()

此方法只会唤醒等待此对象监视器锁的所有线程。

唤醒的线程将以通常的方式完成，就像其他线程一样。

但是，在我们允许它们继续执行之前，请始终定义一个快速检查，以确定继续执行线程所需的条件。
这是因为在某些情况下，线程可能会在没有收到通知的情况下被唤醒(这个场景将在后面的示例中讨论)。

## 5. Sender-Receiver同步问题

现在我们已经了解了基本知识，让我们来看一个简单的Sender-Receiver应用程序，它使用wait()和notify()方法来设置它们之间的同步：

+ 发送方应该向接收方发送数据包。
+ 在发送方完成发送之前，接收方无法处理数据包。
+ 同样，发送方不应尝试发送另一个数据包，除非接收方已经处理了前一个数据包。

让我们首先创建一个Data类，它由将从发送方发送到接收方的packet组成。我们使用wait()和notifyAll()在它们之间设置同步：

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
    + 如果transfer为false，我们将在此线程上调用wait()进行等待。
    + 但当它为true时，我们切换transfer的状态，设置消息，并调用notifyAll()唤醒其他线程，以指定发送者发送了数据，并检查是否可以继续执行。
+ 类似地，接收方将使用receive()方法接收数据：
    + 如果发送方将transfer设置为false，那么它将继续，否则我们将在此线程上调用wait()。
    + 当条件满足时，我们切换transfer状态，通知所有等待的线程唤醒，并返回接收到的数据包。

### 5.1 为什么要将wait()包含在while循环中？

由于notify()和notifyAll()会随机唤醒在该对象监视器上等待的线程，因此满足条件并不总是很重要的。有时，线程被唤醒，但条件实际上尚未满足。

我们还可以定义一个检查来避免虚假唤醒 - 线程可以在没有收到通知的情况下从等待中唤醒。

### 5.2 为什么我们需要同步send()和receive()方法？

我们将这些方法设置为同步方法，以提供内部锁。如果调用wait()方法的线程不拥有固有的锁，则会抛出一个错误。

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

+ 我们创建一些随机数据包，这些数据包将以packets[]数组的形式通过网络发送。
+ 对于每个数据包，我们只调用send()。
+ 然后我们调用随机几秒睡眠时长的sleep()，以模拟繁重的服务器端处理。

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

在这里，我们只是在循环中调用data.receive()，直到我们接收到最后一个数据包“end”。

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

```
First packet
Second packet
Third packet
Fourth packet
```

我们已按正确的顺序接收所有数据包，并成功地在发送方和接收方之间建立了正确的通信。

## 6. 总结

在本文中，我们讨论了Java中的一些核心同步概念。
更具体地说，我们关注如何使用wait()和notify()来解决同步问题。最后，我们通过一个代码示例，在实践中应用了这些概念。

值得一提的是，所有这些低级API，比如wait()、notify()和notifyAll()，都是传统方法，
高级机制通常更简单、更好，比如Java的本地锁和Condition接口(在Java.util.concurrent.locks包中提供)。