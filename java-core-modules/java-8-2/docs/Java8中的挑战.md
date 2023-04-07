## **一、概述**

Java 8 引入了一些新特性，主要围绕 lambda 表达式的使用展开。在这篇简短的文章中，我们将看看其中一些的缺点。

而且，虽然这不是完整列表，但它是关于 Java 8 新特性的最常见和最流行的抱怨的主观集合。

## **2. Java 8 流和线程池**

首先，Parallel Streams 旨在使序列的简单并行处理成为可能，并且对于简单的场景来说效果很好。

Stream 使用默认的通用*[ForkJoinPool](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ForkJoinPool.html)* – 将序列拆分为更小的块并使用多个线程执行操作。

但是，有一个陷阱。没有好的方法来指定**要使用****哪个\*ForkJoinPool\***，因此，如果其中一个线程被卡住，所有其他使用共享池的线程将不得不等待长时间运行的任务完成。

幸运的是，有一个解决方法：

```java
ForkJoinPool forkJoinPool = new ForkJoinPool(2);
forkJoinPool.submit(() -> /*some parallel stream pipeline */)
  .get();复制
```

这将创建一个新的、独立的*ForkJoinPool*，并行流生成的所有任务将使用指定的池，而不是共享的默认池。

值得注意的是，还有另一个潜在问题：*“这种将任务提交到 fork-join 池并在该池中运行并行流的技术是一种实现‘技巧’，并不能保证有效”*，根据 Stuart Marks 的说法– 来自 Oracle 的 Java 和 OpenJDK 开发人员。使用此技术时要记住一个重要的细微差别。

## **3. 可调试性降低**

**新的编码风格简化了我们的源代码，但** **在调试时可能会让人头疼**。

首先，让我们看一下这个简单的例子：

```java
public static int getLength(String input) {
    if (StringUtils.isEmpty(input) {
        throw new IllegalArgumentException();
    }
    return input.length();
}

List lengths = new ArrayList();

for (String name : Arrays.asList(args)) {
    lengths.add(getLength(name));
}复制
```

这是不言自明的标准命令式 Java 代码。

如果我们将空*字符串*作为输入传递——结果——代码将抛出异常，在调试控制台中，我们可以看到：

```bash
at LmbdaMain.getLength(LmbdaMain.java:19)
at LmbdaMain.main(LmbdaMain.java:34)复制
```

现在，让我们使用 Stream API 重写相同的代码，看看传递空*字符串时会发生什么：*

```java
Stream lengths = names.stream()
  .map(name -> getLength(name));复制
```

调用堆栈将如下所示：

```bash
at LmbdaMain.getLength(LmbdaMain.java:19)
at LmbdaMain.lambda$0(LmbdaMain.java:37)
at LmbdaMain$$Lambda$1/821270929.apply(Unknown Source)
at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
at java.util.Spliterators$ArraySpliterator.forEachRemaining(Spliterators.java:948)
at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:512)
at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:502)
at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
at java.util.stream.LongPipeline.reduce(LongPipeline.java:438)
at java.util.stream.LongPipeline.sum(LongPipeline.java:396)
at java.util.stream.ReferencePipeline.count(ReferencePipeline.java:526)
at LmbdaMain.main(LmbdaMain.java:39)复制
```

这就是我们在代码中利用多个抽象层所付出的代价。然而，IDE 已经开发出用于调试 Java Streams 的可靠工具。

## 4. 返回*Null*或*Optional 的方法*

[*Optional*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html)是在 Java 8 中引入的，以提供一种类型安全的方式来表达可选性。

*可选*，明确指示返回值可能不存在。因此，调用一个方法可能会返回一个值，而*Optional*用于将那个值包装在里面——结果证明这很方便。

不幸的是，由于 Java 的向后兼容性，我们有时会以混合两种不同约定的 Java API 告终。在同一个类中，我们可以找到返回 null 的方法以及返回*Optional 的方法。*

## **5.功能接口太多**

在*[java.util.function](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/package-summary.html)*包中，我们有一组用于 lambda 表达式的目标类型。我们可以将它们区分并分组为：

-   *Consumer* – 表示接受一些参数但不返回任何结果的操作
-   *Function——*表示一个接受一些参数并产生结果的函数
-   *运算符*——表示对某些类型参数的操作，并返回与操作数相同类型的结果
-   *谓词*——表示一些参数的谓词（*布尔*值函数）
-   *Supplier* – 代表不接受参数并返回结果的供应商

此外，我们还有其他类型可用于处理原语：

-   *消费者*
-   *内部函数*
-   *Int谓词*
-   *国际供应商*
-   *IntToDoubleFunction*
-   *IntToLong函数*
-   ......以及*长牌*和*双牌的相同选择*

此外，参数为 2 的函数的特殊类型：

-   *双消费者*
-   *双谓词*
-   *二元运算符*
-   *双函数*

因此，整个包包含 44 种功能类型，这肯定会让人感到困惑。

## **6. 检查异常和 Lambda 表达式**

在 Java 8 之前，检查异常一直是一个有问题和有争议的问题。自从 Java 8 到来以来，新的问题出现了。

检查异常必须立即捕获或声明。由于*java.util.function*函数接口没有声明抛出异常，抛出检查异常的代码在编译过程中会失败：

```java
static void writeToFile(Integer integer) throws IOException {
    // logic to write to file which throws IOException
}复制
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(i -> writeToFile(i));复制
```

解决此问题的一种方法是将已检查的异常包装在*try-catch*块中并重新抛出*RuntimeException*：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(i -> {
    try {
        writeToFile(i);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
});复制
```

这会起作用。然而，抛出*RuntimeException*与检查异常的目的相矛盾，并使整个代码被样板代码包裹，我们正试图通过利用 lambda 表达式来减少这种情况。一种 hacky 解决方案是[依靠 sneaky-throw hack。](https://4comprehension.com/sneakily-throwing-exceptions-in-lambda-expressions-in-java/)

另一种解决方案是编写一个可以抛出异常的消费者功能接口：

```java
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}复制
static <T> Consumer<T> throwingConsumerWrapper(
  ThrowingConsumer<T, Exception> throwingConsumer) {
  
    return i -> {
        try {
            throwingConsumer.accept(i);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    };
}复制
```

不幸的是，我们仍然将已检查的异常包装在运行时异常中。

最后，为了深入解决问题和解释问题，我们可以深入探索以下内容：[Java 8 Lambda 表达式中的异常](https://www.baeldung.com/java-lambda-exceptions)。

## **8** **. 结论**

在这篇简短的文章中，我们讨论了 Java 8 的一些缺点。

虽然其中一些是 Java 语言架构师经过深思熟虑做出的设计选择，并且在许多情况下都有变通方法或替代解决方案；我们确实需要了解他们可能存在的问题和局限性。