## 1. 概述

在本教程中，我们将介绍在Java中设置线程名称的不同方法。首先，我们将创建一个运行两个线程的示例，一个线程打印偶数，另一个线程打印奇数。然后，我们将为线程指定一个自定义名称并显示它们。

## 2. 设置线程名称的方法

线程是可以并发执行的轻量级进程，Java中的Thread类为线程提供了一个默认名称。

在某些情况下，我们可能需要知道哪个线程正在运行，因此为线程指定一个自定义名称可以更容易地在其他正在运行的线程中找到它。

让我们先定义一个简单的类来创建两个线程，第一个线程将打印1到N之间的偶数，第二个线程将打印1到N之间的奇数。在我们的示例中，N是5。

我们还将打印线程的默认名称。

首先，让我们创建两个线程：

```java
public class CustomThreadName {
    public int currentNumber = 1;
    public int N = 5;

    public static void main(String[] args) {
        CustomThreadName test = new CustomThreadName();
        Thread oddThread = new Thread(test::printOddNumber);
        Thread evenThread = new Thread(test::printEvenNumber);
        evenThread.start();
        oddThread.start();
    }
    // printEvenNumber() and printOddNumber()
}
```

在printEvenNumber()和printOddNumber()方法中，我们检查当前数字是偶数还是奇数，并将数字与线程名称一起打印：

```java
public void printEvenNumber() {
    synchronized (this) {
        while (currentNumber < N) {
            while (currentNumber % 2 == 1) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " --> " + currentNumber);
            currentNumber++;
            notify();
        }
    }
}

public void printOddNumber() {
    synchronized (this) {
        while (currentNumber < N) {
            while (currentNumber % 2 == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " --> " + currentNumber);
            currentNumber++;
            notify();
        }
    }
}
```

运行该代码的输出如下所示：

```shell
Thread-0 --> 1
Thread-1 --> 2
Thread-0 --> 3
Thread-1 --> 4
Thread-0 --> 5
```

所有线程都有一个默认名称，Thread-0、Thread-1等等。

### 2.1 使用Thread构造函数

Thread类提供了一些构造函数，我们可以在线程创建时提供线程名称，例如：

+ Thread(Runnable target, String name)
+ Thread(String name)

在本例中，参数name是线程名。

使用Thread构造函数，我们可以在线程创建时提供线程名称。

让我们为我们的线程提供一个自定义名称：

```java
Thread oddThread = new Thread(test::printOddNumber, "ODD");
Thread evenThread = new Thread(test::printEvenNumber, "EVEN");
```

现在，当我们运行代码时，会显示自定义名称：

```shell
ODD --> 1
EVEN --> 2
ODD --> 3
EVEN --> 4
ODD --> 5
```

### 2.2 使用setName()方法

此外，Thread类还提供了一个setName()方法。

让我们通过调用Thread.currentThread().setName()来设置线程名称。

```java
Thread oddThread = new Thread(() -> {
    Thread.currentThread().setName("ODD");
    test.printOddNumber();
});

Thread evenThread = new Thread(() -> {
    Thread.currentThread().setName("EVEN");
    test.printEvenNumber();
});
```

或者，通过Thread.setName()：

```java
Thread oddThread = new Thread(test::printEvenNumber);
oddThread.setName("ODD");

Thread evenThread = new Thread(test::printOddNumber);
evenThread.setName("EVEN");
```

同样，运行代码会显示线程的自定义名称：

```shell
ODD --> 1
EVEN --> 2
ODD --> 3
EVEN --> 4
ODD --> 5
```

## 3. 总结

在本文中，我们介绍了如何在Java中设置线程的名称。首先，我们使用默认名称创建了一个线程，然后使用Thread构造函数设置了一个自定义名称，然后使用setName()方法设置自定义名称。