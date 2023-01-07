## 1. 概述

在这个简短的教程中，我们将了解java.lang.IllegalMonitorStateException。 

我们将创建一个抛出此异常的简单发送者-接收者应用程序。然后，我们将讨论防止它的可能方法。最后，我们将展示如何正确实现这些发送方和接收方类。

## 2. 什么时候抛出？

IllegalMonitorStateException与Java中的多线程编程有关。如果我们有一个我们想要同步的[监视器，则抛出此异常以指示线程试图等待或通知其他线程在该监视器上等待，而不拥有它。](https://www.baeldung.com/cs/monitor)简而言之，如果我们在[同步](https://www.baeldung.com/java-synchronized)块之外调用Object类的wait()、notify()或notifyAll()方法之一，我们将得到这个异常。

现在让我们构建一个抛出IllegalMonitorStateException的示例。为此，我们将同时使用wait()和notifyAll()方法来同步发送方和接收方之间的数据交换。

首先，让我们看一下保存我们要发送的消息的数据类：

```java
public class Data {
    private String message;

    public void send(String message) {
        this.message = message;
    }

    public String receive() {
        return message;
    }
}
```

其次，让我们创建在调用时抛出IllegalMonitorStateException 的发送者类。为此，我们将调用notifyAll()方法而不将其包装在同步块中：

```java
class UnsynchronizedSender implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(UnsychronizedSender.class);
    private final Data data;

    public UnsynchronizedSender(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);

            data.send("test");

            data.notifyAll();
        } catch (InterruptedException e) {
            log.error("thread was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
```

接收方还将抛出IllegalMonitorStateException。与前面的示例类似，我们将在同步块外调用wait()方法：

```java
public class UnsynchronizedReceiver implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(UnsynchronizedReceiver.class);
    private final Data data;
    private String message;

    public UnsynchronizedReceiver(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        try {
            data.wait();
            this.message = data.receive();
        } catch (InterruptedException e) {
            log.error("thread was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public String getMessage() {
        return message;
    }
}
```

最后，让我们实例化这两个类并在它们之间发送消息：

```java
public void sendData() {
    Data data = new Data();

    UnsynchronizedReceiver receiver = new UnsynchronizedReceiver(data);
    Thread receiverThread = new Thread(receiver, "receiver-thread");
    receiverThread.start();

    UnsynchronizedSender sender = new UnsynchronizedSender(data);
    Thread senderThread = new Thread(sender, "sender-thread");
    senderThread.start();

    senderThread.join(1000);
    receiverThread.join(1000);
}
```

当我们尝试运行这段代码时，我们将收到来自UnsynchronizedReceiver和UnsynchronizedSender类的IllegalMonitorStateException：

```bash
[sender-thread] ERROR illegalmonitorstate.cn.tuyucheng.taketoday.exceptions.UnsynchronizedSender - illegal monitor state exception occurred
java.lang.IllegalMonitorStateException: null
	at java.base/java.lang.Object.notifyAll(Native Method)
	at illegalmonitorstate.cn.tuyucheng.taketoday.exceptions.UnsynchronizedSender.run(UnsynchronizedSender.java:15)
	at java.base/java.lang.Thread.run(Thread.java:844)

[receiver-thread] ERROR illegalmonitorstate.cn.tuyucheng.taketoday.exceptions.UnsynchronizedReceiver - illegal monitor state exception occurred
java.lang.IllegalMonitorStateException: null
	at java.base/java.lang.Object.wait(Native Method)
	at java.base/java.lang.Object.wait(Object.java:328)
	at illegalmonitorstate.cn.tuyucheng.taketoday.exceptions.UnsynchronizedReceiver.run(UnsynchronizedReceiver.java:12)
	at java.base/java.lang.Thread.run(Thread.java:844)

```

## 3. 如何解决

要摆脱 IllegalMonitorStateException ， 我们需要在同步块中对wait()、notify() 和notifyAll()方法进行每次调用。考虑到这一点，让我们看看Sender类的正确实现应该是什么样的：

```java
class SynchronizedSender implements Runnable {
    private final Data data;

    public SynchronizedSender(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        synchronized (data) {
            data.send("test");

            data.notifyAll();
        }
    }
}
```

请注意，我们在稍后调用其notifyAll()方法的同一数据实例上使用同步块。

让我们以同样的方式修复接收器：

```java
class SynchronizedReceiver implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SynchronizedReceiver.class);
    private final Data data;
    private String message;

    public SynchronizedReceiver(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        synchronized (data) {
            try {
                data.wait();
                this.message = data.receive();
            } catch (InterruptedException e) {
                log.error("thread was interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public String getMessage() {
        return message;
    }
}
```

如果我们再次创建这两个类并尝试在它们之间发送相同的消息，一切都会正常进行，并且不会抛出任何异常。

## 4。总结

在本文中，我们了解了导致IllegalMonitorStateException 的原因以及如何防止它。