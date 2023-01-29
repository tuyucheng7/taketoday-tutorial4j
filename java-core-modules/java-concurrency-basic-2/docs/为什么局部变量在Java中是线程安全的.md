## 1. 概述

之前，我们介绍了[什么是线程安全，以及如何实现它](https://www.baeldung.com/java-thread-safety)。

在本文中，我们将了解局部变量以及它们为什么是线程安全的。

## 2. 栈内存和线程

让我们首先回顾一下JVM内存模型。

最重要的是，JVM将其可用内存分为[栈内存和堆内存](https://www.baeldung.com/java-stack-heap)。首先，它将所有对象存储在堆中。其次，**它在栈上存储局部变量和局部对象引用**。

此外，重要的是要认识到每个线程，包括主线程，都有自己的私有堆栈。因此，**其他线程不共享我们的局部变量，这就是使得它们线程安全的原因**。

## 3. 案例

现在，让我们编写一个小得代码示例，其中包含一个局部原始类型local变量和一个原始类型字段field：

```java
public class LocalVariables implements Runnable {
    private int field;

    public static void main(String... args) {
        LocalVariables target = new LocalVariables();
        new Thread(target).start();
        new Thread(target).start();
    }

    @Override
    public void run() {
        field = new SecureRandom().nextInt();
        int local = new SecureRandom().nextInt();
        System.out.println(field + ":" + local);
    }
}
```

在第5行，我们实例化了LocalVariables类的一个实例。在接下来的两行中，我们启动了两个线程，两者都将执行同一实例的run方法。

在run方法中，我们更新LocalVariables类的field字段。其次，我们对局部原始类型local变量赋值。最后，我们将这两个变量打印到控制台。

让我们看看这些变量在JVM中的内存位置。

首先，field是LocalVariables类的字段，因此，它保存在堆上。其次，局部变量local是一个原始类型变量，因此，它位于栈上。

**println语句是运行这两个线程时可能出现问题的地方**。

首先，由于引用和对象都位于堆上，并且在我们的线程之间共享，所以field很有可能引发问题。由于local存在于堆栈中，因此原始类型local的值将正常，因为JVM不会在线程之间共享local变量。

因此，在执行时，控制台的可能输出如下：

```shell
-2136762774 - 224116125
-2136762774 - 1896311000
```

在这种情况下，我们可以看到**两个线程之间确实发生了冲突**。我们可以确认这一点的依据是因为两个线程生成相同的随机整数的可能性很小。

## 4. Lambdas中的局部变量

Lambda(和[匿名内部类](https://www.baeldung.com/java-anonymous-classes))可以在方法内部声明，并可以访问该方法的局部变量。然而，如果没有任何额外的防护准备，这可能会导致很多麻烦。

在JDK 8之前，有一条明确的规则，即**匿名内部类只能访问final的局部变量**。JDK 8引入了有效final的新概念，规则也变得不那么严格。我们之前已经比较了[final和有效final](https://www.baeldung.com/java-effectively-final)，并且我们还讨论了更多关于[使用Lambdas时的有效final](https://www.baeldung.com/java-lambda-effectively-final-local-variables)。

该规则的结果是，**在Lambda中访问的字段必须是final或有效final(没有被更改过)**，这使得它们是线程安全的，因为它们是不可变的。

我们可以在下面的例子中看到这种行为：

```java
public class LocalAndLambda {

    public static void main(String... args) {
        String text = "";
        // text = "675";
        new Thread(() -> System.out.println(text)).start();
    }
}
```

在这种情况下，取消第5行代码的注释会导致编译错误。因为这样，局部变量text就不再是有效final。

## 5. 总结

在本文中，我们研究了局部变量的线程安全性，这是JVM内存模型的结果。我们还研究了局部变量与Lambdas的结合使用，JVM通过要求不变性来保证线程安全。