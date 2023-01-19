## 1. 概述

在本教程中，我们将使用Reactor基础知识来学习一些创建Flux的方法。

## 2. Maven依赖

```text
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>${reactor.version}</version>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <version>${reactor.version}</version>
    <scope>test</scope>
</dependency>

<properties>
    <reactor.version>3.4.12</reactor.version>
</properties>
```

## 3. 同步发射

创建Flux的最简单方法是Flux#generate。**此方法依赖于生成器函数generator来生成元素序列**。

```java
public class SequenceGenerator {
    // methods that will follow ...
}
```

### 3.1 具有新状态的生成器

让我们看看如何使用Reactor生成斐波那契数列：

```java
public class SequenceGenerator {

    public Flux<Integer> generateFibonacciWithTuples() {
        return Flux.generate(
                () -> Tuples.of(0, 1),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                }
        );
    }
}
```

不难看出，这个generate()方法使用两个函数作为它的参数，分别是一个Callable和一个BiFunction：

+ Callable函数为生成器设置初始状态 - 在本例中，它是一个包含元素0和1的元组。
+ BiFunction函数是一个生成器，它消耗一个SynchronousSink，然后在每一轮中使用sink的next()方法和当前状态发出一个元素。

顾名思义，SynchronousSink对象是同步工作的。但是，**请注意，我们不能在每次生成器调用时多次调用此对象的next()方法**。

让我们使用StepVerifier验证生成的序列：

```java
class SequenceUnitTest {

    @Test
    void whenGeneratingNumbersWithTuplesState_thenFibonacciSequenceIsProduced() {
        SequenceGenerator sequenceGenerator = new SequenceGenerator();
        Flux<Integer> fibonacciFlux = sequenceGenerator.generateFibonacciWithTuples().take(5);

        StepVerifier.create(fibonacciFlux)
                .expectNext(0, 1, 1, 2, 3)
                .expectComplete()
                .verify();
    }
}
```

在本例中，订阅者仅请求五个元素，因此生成的序列以数字3结束。

正如我们所看到的，**生成器返回一个新的状态对象**，以便在下一次传递中使用。
**不过，没有必要这样做。我们可以为生成器的所有调用重用一个状态实例**。

### 3.2 具有可变状态的生成器

假设我们要生成具有循环状态的斐波那契数列。为了演示这个用例，让我们首先定义一个类：

```java

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FibonacciState {
    private int former;
    private int latter;
}
```

我们将使用这个类的一个实例来保存生成器的状态。这个实例的两个属性former和latter存储序列中的两个连续数字。

修改我们的初始示例，我们现在将使用可变状态生成器：

```java
class SequenceGenerator {

    public Flux<Integer> generateFibonacciWithCustomClass(int limit) {
        return Flux.generate(
                () -> new FibonacciState(0, 1),
                (state, sink) -> {
                    sink.next(state.getFormer());
                    if (state.getLatter() > limit)
                        sink.complete();
                    int temp = state.getFormer();
                    state.setFormer(temp);
                    state.setLatter(temp + state.getLatter());
                    return state;
                });
    }
}
```

与前面的示例类似，generate()方法接收状态提供者和生成器参数。

Callable类型的状态提供者只需创建一个初始属性为0和1的FibonacciState对象。此状态对象将在生成器的整个生命周期中重复使用。

就像Fibonacci-with-Tuples示例中的SynchronousSink一样，这里的sink一个一个地生成元素。
但是，与该示例不同的是，**生成器每次调用时都返回相同的状态对象**。

还要注意，**为了避免无限序列**，我们指示sink在产生的值达到限制时调用complete()。

现在我们测试以确认它是否有效：

```java
class SequenceGenerator {

    @Test
    void whenGeneratingNumbersWithCustomClass_thenFibonacciSequenceIsProduced() {
        SequenceGenerator sequenceGenerator = new SequenceGenerator();

        StepVerifier.create(sequenceGenerator.generateFibonacciWithCustomClass(10))
                .expectNext(0, 1, 1, 2, 3, 5, 8)
                .expectComplete()
                .verify();
    }
}
```

### 3.3 无状态变体

generate()方法有另一个重载，只有一个Consumer<SynchronousSink\>类型的参数。
该重载仅适用于产生预定序列，因此没有那么强大。这里就不详细介绍了。

## 4. 异步发射

同步发射并不是以编程方式创建Flux的唯一解决方案。

相反，**我们可以使用create()和push()运算符以异步方式在一轮发射中生成多个元素**。

### 4.1 create()方法

**使用create()方法，我们可以从多个线程中生成元素**。在本例中，我们将来自两个不同源的元素收集到一个序列中。

首先，让我们看看create()与generate()有何不同：

```java
public class SequenceCreator {
    public Consumer<List<Integer>> consumer;

    public Flux<Integer> createNumberSequence() {
        return Flux.create(sink -> SequenceCreator.this.consumer = items -> items.forEach(sink::next));
    }
}
```

与generate()运算符不同，**create()方法不维护状态。传递给此方法的发射器不是自己生成元素，而是从外部源接收元素**。

此外，我们可以看到create()运算符要求我们使用FluxSink作为参数而不是SynchronousSink。
**使用FluxSink，我们可以根据需要多次调用next()**。

在我们的例子中，我们将为集合中的每个元素调用next()，并逐个发出。稍后我们将看到如何填充元素。

在本例中，我们的外部源是一个虚构的消费者变量，尽管这可能是一些可观察的API。

让我们将create()运算符付诸行动，从两个数字序列开始：

```java
class SequenceUnitTest {

    @Test
    void whenCreatingNumbers_thenSequenceIsProducedAsynchronously() {
        SequenceGenerator sequenceGenerator = new SequenceGenerator();
        List<Integer> sequence1 = sequenceGenerator.generateFibonacciWithTuples().take(3).collectList().block();
        List<Integer> sequence2 = sequenceGenerator.generateFibonacciWithTuples().take(4).collectList().block();
        // 其他代码如下描述
    }
}
```

这些序列sequence1和sequence2，将作为生成序列的元素源。

接下来，将有两个线程对象将元素注入发布者：

```text
Thread produingThread1 = new Thread(() -> sequenceCreator.consumer.accept(sequence1));
Thread produingThread2 = new Thread(() -> sequenceCreator.consumer.accept(sequence2));
```

当调用accept()操作符时，元素开始流入序列源。

然后，我们可以监听或订阅新的合并序列：

```text
List<Integer> consolidated = new ArrayList<>();
sequenceCreator.createNumberSequence().subscribe(consolidated::add);
```

通过订阅我们的序列，我们指示序列发出的每个元素应该发生什么。在这里，它将来自不同源的每个元素添加到consolidated集合中。

现在，我们触发整个过程，看到元素在两个不同的线程上移动：

```text
producingThread1.start();
producingThread2.start();
producingThread1.join();
producingThread2.join();
```

最后一步是验证操作的结果：

```text
assertThat(consolidated).containsExactlyInAnyOrder(0, 1, 1, 0, 1, 1, 2);
```

接收到的序列中的前三个数字来自sequence1，后四个来自sequence2。由于异步操作的性质，不能保证这些序列中元素的顺序。

create()方法还有另一个重载，额外接收OverflowStrategy类型的参数。
顾名思义，当下游无法跟上发布者时，此参数管理背压。**默认情况下，发布者会在这种情况下缓冲所有元素**。

### 4.2 push()方法

除了create()运算符之外，Flux类还有另一个静态方法push()来异步发出序列。
此方法的工作原理与create()类似，**不同之处在于它一次只允许一个生产线程发出信号**。

我们可以用push()替换上节例子中的create()方法，代码仍然可以编译。

然而，有时我们可能会得到断言错误，因为push()操作符使FluxSink#next不会在不同线程上并发调用。
**因此，只有在我们不打算使用多线程时才应该使用push()**。

## 5. 处理序列

到目前为止，我们看到的所有方法都是静态的，并且允许从给定的源创建序列。
**Flux API还提供了一个名为handle()的实例方法，用于处理发布者生成的序列**。

这个handle运算符接收一个序列，进行一些处理并可能删除一些元素。
**在这方面，我们可以说handle操作符的工作方式与map和filter一样**。

让我们看一下handle()方法的简单示例：

```java
public class SequenceHandler {

    public Flux<Integer> handleIntegerSequence(Flux<Integer> sequence) {
        return sequence.handle((number, sink) -> {
            if (number % 2 == 0)
                sink.next(number / 2);
        });
    }
}
```

在本例中，handle运算符获取一个数字序列，如果是偶数，则将该值除以2。如果该值为奇数，则运算符不执行任何操作，这意味着忽略该值。

需要注意的另一件事是，与generate()方法一样，**handle()使用SynchronousSink并且仅允许逐个发射**。

最后，让我们使用StepVerifier来确认我们的SequenceHandler有效：

```java
class SequenceUnitTest {

    @Test
    void whenHandlingNumbers_thenSequenceIsMappedAndFiltered() {
        SequenceHandler sequenceHandler = new SequenceHandler();
        SequenceGenerator sequenceGenerator = new SequenceGenerator();
        Flux<Integer> sequence = sequenceGenerator.generateFibonacciWithTuples().take(10);

        StepVerifier.create(sequenceHandler.handleIntegerSequence(sequence))
                .expectNext(0, 1, 4, 17)
                .expectComplete()
                .verify();
    }
}
```

斐波那契数列的前10项中有四个偶数：0、2、8和34，因此我们将它们的一半传递给expectNext方法作为参数。

## 6. 总结

在本文中，我们介绍了Flux API的各种方法，这些方法可用于以编程方式生成序列，特别是generate和create运算符。