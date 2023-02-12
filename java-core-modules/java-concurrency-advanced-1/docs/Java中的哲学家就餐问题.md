## 1. 概述

[哲学家进餐问题](https://www.baeldung.com/cs/dining-philosophers)是用于**描述多线程环境中的同步问题并说明解决这些问题**的技术的经典问题之一。Dijkstra首先阐述了这个问题，并提出了有关计算机访问磁带驱动器外围设备的问题。

目前的公式是由托尼·霍尔(Tony Hoare)给出的，他还以发明快速排序算法而闻名。在本文中，我们分析了这个众所周知的问题并编写了一种可行的解决方案。

## 2. 问题描述

<img src="../assets/img_3.png">

上图说明了问题所在。五位沉默的哲学家(P1-P5)围坐在一张圆桌旁，一辈子都在吃饭和思考。

有五把叉子供他们共用(1-5)，为了能够吃东西，哲学家需要双手都拿着叉子。吃完后，他把它们都放下，然后由另一位重复相同循环的哲学家获取。

> 我们的目标是提出一个方案/协议，帮助哲学家实现他们的目标，即在不饿死的情况下吃饭和思考。

## 3. 解决方案

一个初步的解决方案是让每个哲学家都遵循以下协议：

```java
while(true) { 
    // Initially, thinking about life, universe, and everything
    think();

    // Take a break from thinking, hungry now
    pick_up_left_fork();
    pick_up_right_fork();
    eat();
    put_down_right_fork();
    put_down_left_fork();

    // Not hungry anymore. Back to thinking!
}
```

正如上面的伪代码所描述的，每个哲学家最初都在思考。**一定时间后，哲学家饿了，想吃饭**。

这时，**他伸手去拿两边的叉子，一旦他拿到了两把叉子，就开始吃东西**。一旦吃完，哲学家就会放下叉子，以便他的邻居可以使用叉子。

## 4. 实现

我们将每个哲学家建模为实现Runnable接口的类，这样我们就可以将它们作为单独的线程运行。每一位哲学家都可以使用他左右两侧的两个叉子：

```java
public class Philosopher implements Runnable {
    private final Object leftFork;
    private final Object rightFork;

    Philosopher(Object left, Object right) {
        this.leftFork = left;
        this.rightFork = right;
    }

    @Override
    public void run() {
        // ...
    }
}
```

我们还有一个方法可以指导哲学家执行一个动作-吃饭、思考，或获取叉子来准备吃饭：

```java
public class Philosopher implements Runnable {
    // Member variables, standard constructor

    private void doAction(String action) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " " + action);
        Thread.sleep(((int) (Math.random() * 100)));
    }

    // Rest of the methods written earlier
}
```

如上面的代码所示，每个操作都是通过将调用线程挂起随机一段时间来模拟的，这样执行顺序就不会仅由时间强制执行。

现在，让我们来实现哲学家的核心逻辑。

为了模拟获取一个叉子，我们需要锁定它，这样就不会有两个哲学家线程同时获取它。

为了实现这一点，我们使用synchronized关键字获取叉子对象的内部监视器，并防止其他线程执行同样的操作。可以在[此处](https://www.baeldung.com/java-synchronized)找到Java中的synchronized关键字指南。现在，我们继续在哲学家类中实现run()方法：

```java
public class Philosopher implements Runnable {
    // Member variables, methods defined earlier

    @Override
    public void run() {
        try {
            while (true) {
                doAction(System.nanoTime() + ": Thinking"); // thinking
                synchronized (leftFork) {
                    doAction(System.nanoTime() + ": Picked up left fork");
                    synchronized (rightFork) {
                        doAction(System.nanoTime() + ": Picked up right fork - eating"); // eating
                        doAction(System.nanoTime() + ": Put down right fork");
                    }
                    doAction(System.nanoTime() + ": Put down left fork. Back to thinking"); // Back to thinking
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }
}
```

这个方案正好实现了前面描述的方案：一位哲学家思考了一会儿，然后决定吃饭。

之后，他拿起左右两边的叉子开始吃东西。完成后，他放下叉子。我们还为每个动作添加时间戳，这将有助于我们了解事件发生的顺序。

为了启动整个过程，我们编写了一个客户端，它创建5个哲学家作为线程并启动所有线程：

```java
public class DiningPhilosophers {

    public static void main(String[] args) throws Exception {
        Philosopher[] philosophers = new Philosopher[5];
        Object[] forks = new Object[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];
            philosophers[i] = new Philosopher(leftFork, rightFork);
            Thread t = new Thread(philosophers[i], "Philosopher " + (i + 1));
            t.start();
        }
    }
}
```

我们将每个叉子建模为Java Object对象，并根据哲学家的数量创建它们。我们向每个哲学家传递他试图使用synchronized关键字锁定的左叉子和右叉子。

运行这段代码会产生类似于以下内容的输出。你的输出很可能与下面给出的不同，主要是因为调用sleep()方法的时间间隔不同：

```shell
Philosopher 1 8038014601251: Thinking
Philosopher 2 8038014828862: Thinking
Philosopher 3 8038015066722: Thinking
Philosopher 4 8038015284511: Thinking
Philosopher 5 8038015468564: Thinking
Philosopher 1 8038016857288: Picked up left fork
Philosopher 1 8038022332758: Picked up right fork - eating
Philosopher 3 8038028886069: Picked up left fork
Philosopher 4 8038063952219: Picked up left fork
Philosopher 1 8038067505168: Put down right fork
Philosopher 2 8038089505264: Picked up left fork
Philosopher 1 8038089505264: Put down left fork. Back to thinking
Philosopher 5 8038111040317: Picked up left fork
```

所有的哲学家最初都是从思考开始的，我们看到哲学家1开始拿起左叉和右叉，然后吃东西，然后把它们都放下，然后哲学家5拿起。

## 5. 解决方案的问题是：[死锁](https://www.baeldung.com/cs/os-deadlock)

虽然上面的解决方案看起来是正确的，但是会出现死锁的问题。

> 死锁是指当每个进程都在等待获取其他进程持有的资源时，系统进程停止的情况。

我们可以通过多次运行上面的代码并检查是否有几次代码挂起来确认这一点。下面是一个示例输出，演示了上述问题：

```shell
Philosopher 1 8487540546530: Thinking
Philosopher 2 8487542012975: Thinking
Philosopher 3 8487543057508: Thinking
Philosopher 4 8487543318428: Thinking
Philosopher 5 8487544590144: Thinking
Philosopher 3 8487589069046: Picked up left fork
Philosopher 1 8487596641267: Picked up left fork
Philosopher 5 8487597646086: Picked up left fork
Philosopher 4 8487617680958: Picked up left fork
Philosopher 2 8487631148853: Picked up left fork
```

在这种情况下，每个哲学家都获得了他的左叉，但无法获得他的右叉，因为他的邻居已经获得了他的右叉子。这种情况通常称为**循环等待**，是导致死锁并阻止系统前进的条件之一。

## 6. 打破死锁

正如我们在上面看到的，死锁的主要原因是循环等待条件，其中每个进程都在等待由其他进程持有的资源。因此，为了避免死锁情况，我们需要确保循环等待条件被打破。有几种方法可以实现这一点，最简单的方法如下：

> 所有哲学家都先伸手去拿他们的左叉子，只有一个哲学家会先伸手去拿他的右叉子。

我们通过在现有代码中对代码进行相对较小的更改来实现这一点：

```java
public class DiningPhilosophers {

    public static void main(String[] args) throws Exception {
        final Philosopher[] philosophers = new Philosopher[5];
        Object[] forks = new Object[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];
            if (i == philosophers.length - 1) {
                // The last philosopher picks up the right fork first
                philosophers[i] = new Philosopher(rightFork, leftFork);
            } else {
                philosophers[i] = new Philosopher(leftFork, rightFork);
            }
            Thread t = new Thread(philosophers[i], "Philosopher " + (i + 1));
            t.start();
        }
    }
}
```

更改出现在上述代码的第14-16行，在其中我们引入了一个条件，使最后一位哲学家首先拿右叉子，而不是左叉子。这打破了循环等待条件，我们可以避免死锁。

以下输出显示了所有哲学家都有机会思考和吃饭，而不会造成僵局的情况：

```shell
Philosopher 1 88519839556188: Thinking
Philosopher 2 88519840186495: Thinking
Philosopher 3 88519840647695: Thinking
Philosopher 4 88519840870182: Thinking
Philosopher 5 88519840956443: Thinking
Philosopher 3 88519864404195: Picked up left fork
Philosopher 5 88519871990082: Picked up left fork
Philosopher 4 88519874059504: Picked up left fork
Philosopher 5 88519876989405: Picked up right fork - eating
Philosopher 2 88519935045524: Picked up left fork
Philosopher 5 88519951109805: Put down right fork
Philosopher 4 88519997119634: Picked up right fork - eating
Philosopher 5 88519997113229: Put down left fork. Back to thinking
Philosopher 5 88520011135846: Thinking
Philosopher 1 88520011129013: Picked up left fork
Philosopher 4 88520028194269: Put down right fork
Philosopher 4 88520057160194: Put down left fork. Back to thinking
Philosopher 3 88520067162257: Picked up right fork - eating
Philosopher 4 88520067158414: Thinking
Philosopher 3 88520160247801: Put down right fork
Philosopher 4 88520249049308: Picked up left fork
Philosopher 3 88520249119769: Put down left fork. Back to thinking
```

通过多次运行代码可以验证，系统没有出现之前出现的死锁情况。

## 7. 总结

在本文中，我们探讨了著名的哲学家进餐问题以及循环等待和死锁的概念。我们编写了一个导致死锁的简单解决方案，并做了一个简单的更改来打破循环等待并避免死锁。这只是一个开始，对于该问题，有更多并且更好的解决方案存在。