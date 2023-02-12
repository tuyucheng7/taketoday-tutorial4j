## 1. 概述

CyclicBarriers是作为java.util.concurrent包的一部分随Java 5引入的同步结构。

在本文中，我们将在并发场景中探讨此实现。

## 2. Java并发-同步器

java.util.concurrent包含一些有助于管理一组相互协作的线程的类。其中包括：

+ CyclicBarrier
+ Phaser
+ CountDownLatch
+ Exchanger
+ Semaphore
+ SynchronousQueue

这些类为线程之间的常见交互模式提供了开箱即用的功能。

如果我们有一组相互通信的线程，它们类似于一种常见的管理线程协作的模式，那么**我们可以简单地重用适当的类(也称为同步器)，而不是尝试使用一组锁和Condition对象以及synchronized关键字来创建自定义方案**。

让我们继续关注CyclicBarrier。

## 3. CyclicBarrier

CyclicBarrier是一个同步器，它允许一组线程相互等待到达一个共同的执行点，也称为屏障。

> CyclicBarrier用于在程序中，我们有固定数量的线程，这些线程在继续执行之前必须相互等待到达公共点。

**屏障之所以称为循环屏障，是因为它可以在等待线程被释放后重新使用**。

## 4. 用法

CyclicBarrier的构造函数很简单。它接收单个整数值表示需要在CyclicBarrier实例上调用await()方法以表示达到公共执行点的线程数：

```java
public CyclicBarrier(int parties)
```

需要同步执行的线程也被称为parties(参与方)，调用await()方法是我们注册某个线程已到达屏障点的方式。

此调用是同步的，调用此方法的线程会暂停执行，直到指定数量的线程在屏障上调用了相同的方法。**在这种情况下，所需数量的线程调用了await()，称为触发屏障**。

或者，我们可以将第二个参数传递给构造函数，它是一个Runnable实例。它的逻辑将由最后一个到达屏障的线程运行：

```java
public CyclicBarrier(int parties, Runnable barrierAction)
```

## 5. 实现

要了解CyclicBarrier的实际应用，让我们考虑以下场景：

有一个操作由固定数量的线程执行并将相应的结果存储在列表中。当所有线程完成它们的操作时，其中一个线程(通常是最后一个触发屏障的线程)开始处理每个线程获取的数据。

让我们实现所有操作发生的主类：

```java
public class CyclicBarrierDemo {
    private CyclicBarrier cyclicBarrier;
    private final List<List<Integer>> partialResults = Collections.synchronizedList(new ArrayList<>());
    private final Random random = new Random();
    private int NUM_PARTIAL_RESULTS;
    private int NUM_WORKERS;
    // ...
}
```

这个类非常简单-NUM_WORKERS是将要执行的线程数，NUM_PARTIAL_RESULTS是每个工作线程将要生成的结果数。

最后，我们有一个partialResults列表，它将存储每个工作线程的结果。请注意，此列表是一个SynchronizedList，因为多个线程将同时向其写入数据，而add()方法在普通的ArrayList上不是线程安全的。

现在让我们实现每个工作线程的逻辑：

```java
public class CyclicBarrierDemo {
    // ...

    class NumberCruncherThread implements Runnable {

        @Override
        public void run() {
            String thisThreadName = currentThread().getName();
            List<Integer> partialResult = new ArrayList<>();

            // Crunch some numbers and store the partial result
            for (int i = 0; i < NUM_PARTIAL_RESULTS; i++) {
                int num = random.nextInt(10);
                System.out.println(thisThreadName + ": Crunching some numbers! Final result - " + num);
                partialResult.add(num);
            }
            partialResults.add(partialResult);
            try {
                System.out.println(thisThreadName + " waiting for others to reach barrier");
                cyclicBarrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

现在，我们将实现当屏障被触发时运行的逻辑。

为了简单起见，我们将partialResults列表中的所有数字相加：

```java
public class CyclicBarrierDemo {

    // ...

    class AggregatorThread implements Runnable {

        @Override
        public void run() {
            String thisThreadName = currentThread().getName();
            System.out.println(thisThreadName + ": Computing final sum of " + NUM_WORKERS + " workers, having " + NUM_PARTIAL_RESULTS + " results each.");
            int sum = 0;
            
            for (List<Integer> threadResult : partialResults) {
                System.out.print("Adding ");
                for (Integer partialResult : threadResult) {
                    System.out.print(partialResult + " ");
                    sum += partialResult;
                }
                System.out.println();
            }
            System.out.println(Thread.currentThread().getName() + ": Final result = " + sum);
        }
    }
}
```

最后一步是构造CyclicBarrier并使用main()方法启动：

```java
public class CyclicBarrierDemo {

    // Previous code

    public static void main(String[] args) {
        CyclicBarrierDemo play = new CyclicBarrierDemo();
        play.runSimulation(5, 3);
    }

    private void runSimulation(int numWorkers, int numberOfPartialResults) {
        NUM_PARTIAL_RESULTS = numberOfPartialResults;
        NUM_WORKERS = numWorkers;
        
        cyclicBarrier = new CyclicBarrier(NUM_WORKERS, new AggregatorThread());
        
        System.out.println("Spawning " + NUM_WORKERS + " worker threads to compute " + NUM_PARTIAL_RESULTS + " partial results each");
        for (int i = 0; i < NUM_WORKERS; i++) {
            Thread worker = new Thread(new NumberCruncherThread());
            worker.start();
        }
    }
}
```

在上面的代码中，我们用5个线程初始化CyclicBarrier，每个线程生成3个整数作为它们计算的一部分，并将其存储在partialResult列表中。

一旦屏障被触发，触发屏障的最后一个线程将执行AggregatorThread中指定的逻辑，即将线程生成的所有数字相加。

## 6. 输出结果

这是上述程序的一次执行的输出，每次执行可能会产生不同的结果，因为线程可以以不同的顺序生成：

```shell
Spawning 5 worker threads to compute 3 partial results each
Thread 3: Crunching some numbers! Final result - 3
Thread 3: Crunching some numbers! Final result - 2
Thread 2: Crunching some numbers! Final result - 4
Thread 2: Crunching some numbers! Final result - 1
Thread 2: Crunching some numbers! Final result - 3
Thread 0: Crunching some numbers! Final result - 4
Thread 0: Crunching some numbers! Final result - 6
Thread 0: Crunching some numbers! Final result - 3
Thread 4: Crunching some numbers! Final result - 1
Thread 3: Crunching some numbers! Final result - 2
Thread 4: Crunching some numbers! Final result - 0
Thread 0 waiting for others to reach barrier.
Thread 2 waiting for others to reach barrier.
Thread 3 waiting for others to reach barrier.
Thread 1: Crunching some numbers! Final result - 0
Thread 1: Crunching some numbers! Final result - 1
Thread 1: Crunching some numbers! Final result - 9
Thread 1 waiting for others to reach barrier.
Thread 4: Crunching some numbers! Final result - 0
Thread 4 waiting for others to reach barrier.
Thread 4: Computing final sum of 5 worker, having 3 results each.
4 1 3 
4 6 3 
3 2 2 
0 1 9 
1 0 0 
Thread 4: Final result = 39
```

如上面的输出所示，线程4是触发屏障并执行最终聚合逻辑的线程。线程实际上也没有必要按照上面示例所示的启动顺序运行。

## 7. 总结

在本文中，我们了解了CyclicBarrier是什么，以及它在什么样的情况下有用。

我们还实现了一个场景，在继续其他程序逻辑之前，我们需要固定数量的线程才能到达固定的执行点。