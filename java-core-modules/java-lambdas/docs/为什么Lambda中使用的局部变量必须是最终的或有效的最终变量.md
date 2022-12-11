## 1. 简介

Java 8为我们提供了lambda，并通过关联提供了有效最终变量的概念。有没有想过为什么在lambda中捕获的局部变量必须是最终的或实际上是最终的？

好吧，当[JLS](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.27.2)说“对有效final变量的限制禁止访问动态更改的局部变量时，JLS给了我们一些提示，这些局部变量的捕获可能会引入并发问题。” 但是，这意味着什么？

在接下来的部分中，我们将更深入地研究这个限制，看看为什么Java引入了它。我们将通过示例来说明它如何影响单线程和并发应用程序，并且我们还将揭穿解决此限制的常见反模式。

## 2. 捕获Lambda

Lambda表达式可以使用在外部作用域中定义的变量。我们将这些lambda称为捕获lambda。它们可以捕获静态变量、实例变量和局部变量，**但只有局部变量必须是最终的或实际上是最终的**。

在早期的Java版本中，当一个匿名内部类捕获包围它的方法的局部变量时，我们会遇到这种情况，我们需要在局部变量之前添加final关键字，以使编译器满意。

作为一个语法糖，现在编译器可以识别出虽然final关键字不存在但引用根本没有改变的情况，这意味着它实际上是最终的。如果编译器不会抱怨我们将其声明为final，则我们可以说该变量实际上是final。

## 3. 捕获Lambda中的局部变量

简单地说，**这不会编译**：

```java
Supplier<Integer> incrementer(int start) {
    return () -> start++;
}
```

start是一个局部变量，我们试图在lambda表达式中修改它。

这不会编译的基本原因是lambda正在捕获start的值，这意味着复制它。强制变量为final避免给人这样的印象，即在lambda中递增start实际上可以修改start方法参数。

但是，它为什么要复制？好吧，请注意我们正在从我们的方法中返回lambda。因此，在start方法参数被垃圾收集之前，lambda不会运行。Java必须创建start的副本，以便此lambda在此方法之外存在。

### 3.1 并发问题

为了好玩，让我们想象一下Java确实允许局部变量以某种方式与它们捕获的值保持联系。

我们应该在这里做什么：

```java
public void localVariableMultithreading() {
    boolean run = true;
    executor.execute(() -> {
        while (run) {
            // do operation
        }
    });
    
    run = false;
}
```

虽然这看起来很无辜，但它有一个潜在的“可见性”问题。回想一下，每个线程都有自己的堆栈，那么我们如何确保我们的while循环看到另一个堆栈中运行变量的变化？在其他上下文中，答案可能是使用同步块或volatile关键字。

然而，因为Java强加了有效的最终限制，因此我们不必担心这样的复杂性。

## 4. 捕获Lambda中的静态或实例变量

如果我们将前面的示例与在lambda表达式中使用静态或实例变量进行比较，可能会引发一些问题。

我们可以通过将start变量转换为实例变量来编译我们的第一个示例：

```java
private int start = 0;

Supplier<Integer> incrementer() {
    return () -> start++;
}
```

但是，为什么我们可以在这里改变start的值呢？

简单的说，这是关于成员变量的存储位置。局部变量在栈上，而成员变量在堆上。因为我们处理的是堆内存，所以编译器可以保证lambda可以访问start的最新值。

我们可以通过执行相同的操作来修复我们的第二个示例：

```java
private volatile boolean run = true;

public void instanceVariableMultithreading() {
    executor.execute(() -> {
        while (run) {
            // do operation
        }
    });

    run = false;
}
```

run变量现在对lambda可见，即使它在另一个线程中执行，因为我们添加了volatile关键字 。

一般来说，在捕获实例变量时，我们可以认为是捕获最终变量this。**无论如何，编译器不报错并不意味着我们不应该采取预防措施，尤其是在多线程环境中**。

## 5. 避免变通

为了绕过对局部变量的限制，有人可能会想到使用变量持有者来修改局部变量的值。

让我们看一个在单线程应用程序中使用数组存储变量的示例：

```java
public int workaroundSingleThread() {
    int[] holder = new int[] { 2 };
    IntStream sums = IntStream
        .of(1, 2, 3)
        .map(val -> val + holder[0]);

    holder[0] = 0;

    return sums.sum();
}
```

我们可以认为流对每个值加2，但它实际上为加0，因为这是执行lambda时可用的最新值。

让我们更进一步，在另一个线程中执行求和：

```java
public void workaroundMultithreading() {
    int[] holder = new int[] { 2 };
    Runnable runnable = () -> System.out.println(IntStream.of(1, 2, 3)
      .map(val -> val + holder[0])
      .sum());

    new Thread(runnable).start();

    // simulating some processing
    try {
        Thread.sleep(new Random().nextInt(3) * 1000L);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }

    holder[0] = 0;
}
```

我们在这里计算出的值是什么？这取决于我们的模拟处理需要多长时间。如果它足够短，可以让方法的执行在另一个线程执行之前终止，它将打印6，否则，它将打印12。

一般来说，这些变通方法容易出错并且会产生不可预知的结果，因此我们应该始终避免使用它们。

## 6. 总结

在本文中，我们解释了为什么lambda表达式只能使用final或有效的final局部变量。正如我们所看到的，这种限制来自于这些变量的不同性质以及Java在内存中存储它们的方式。我们还演示了使用通用解决方法的危险。