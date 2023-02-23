## 1. 概述

在本文中，我们将了解[RxJava](https://github.com/ReactiveX/RxJava)库如何帮助我们处理背压。

简单地说- 通过引入Observables来利用响应流的概念，一个或多个Observers可以订阅。**处理可能无限的流是非常具有挑战性地，因为我们需要面对背压问题**。

在这种情况下，Observable发出元素的速度比订阅者消费它们的速度更快。我们将研究解决未消费元素缓冲区增长问题的不同解决方案。

## 2. 热Observables与冷Observables

首先，让我们创建一个简单的消费者函数，它将用作我们稍后定义的Observables元素的消费者：

```java
public class ComputeFunction {
    public static void compute(Integer v) {
        try {
            System.out.println("compute integer v: " + v);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

我们的compute()函数只是打印参数。这里需要注意的重要一点是对Thread.sleep(1000)方法调用-我们这样做是为了模拟一些长时间运行的任务，这将导致Observable更快地填充元素，以使Observer可以消费它们。

我们有两种类型的Observables-热和冷，它们在背压处理方面完全不同。

### 2.1 冷Observables

一个冷的Observable发出一个特定的元素序列，但可以在它的观察者发现它方便时开始发出这个序列，并且以观察者想要的任何速率，而不会破坏序列的完整性。**冷Observable以惰性的方式提供元素**。

Observer仅在准备好处理该元素时才获取元素，并且元素不需要在Observable中进行缓冲，因为它们是以拉取方式请求的。

例如，如果你基于从1到100万的静态范围内的元素创建一个Observable，那么无论观察这些元素的频率如何，该Observable都会发出相同的元素序列：

```java
Observable.range(1, 1_000_000)
    .observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute);
```

当我们启动我们的程序时，元素将由Observer延迟计算并以拉取方式请求。Schedulers.computation()方法意味着我们希望在RxJava的计算线程池中运行我们的Observer。

程序的输出将包含一个compute()方法的结果，该方法从一个Observable中一个一个地调用：

```shell
compute integer v: 1
compute integer v: 2
compute integer v: 3
compute integer v: 4
...
```

冷Observables不需要任何形式的背压，因为它们以拉取方式工作。冷Observable发出的元素示例可能包括数据库查询、文件检索或Web请求的结果。

### 2.2 热Observables

热Observable开始生成元素并在创建时立即发出它们，它与冷Observables拉取处理模型相反。**热Observable以自己的速度发出元素，由其观察者来跟上**。

当Observer无法像Observable生成元素那样快速地消费元素时，它们需要被缓冲或以其他方式处理，因为它们会填满内存，最终导致OutOfMemoryException。

让我们考虑一个热Observable的例子，它向正在处理这些元素的最终消费者生产100万个元素。当Observer中的compute()方法需要一些时间来处理每个元素时，Observable开始用元素填满内存，导致程序失败：

```java
PublishSubject<Integer> source = PublishSubject.<Integer>create();

source.observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute, Throwable::printStackTrace);

IntStream.range(1, 1_000_000).forEach(source::onNext);
```

运行该程序将因MissingBackpressureException而失败，因为我们没有定义处理过度生成Observable的方法。

热Observable发出的元素示例可能包括鼠标和键盘事件、系统事件或股票价格。

## 3. 缓冲过度生成的Observable

处理过度生成的Observable的第一种方法是为Observer无法处理的元素定义某种缓冲区。

我们可以通过调用buffer()方法来做到这一点：

```java
PublishSubject<Integer> source = PublishSubject.<Integer>create();
        
source.buffer(1024)
    .observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute, Throwable::printStackTrace);
```

定义一个大小为1024的缓冲区会给观察者一些时间来赶上生产过剩的源。缓冲区将存储尚未处理的元素。

我们可以增加缓冲区大小，以便为生成的值留出足够的空间。

但是请注意，一般来说，**这可能只是一个临时修复**，因为如果源过度生成预测的缓冲区大小，溢出仍然可能发生。

## 4. 批处理发出的元素

我们可以在N个元素的窗口中批量生产过剩的元素。

当Observable生成元素的速度快于Observer处理它们的速度时，我们可以通过将生成的元素分组在一起并将一批元素发送到能够处理元素集合而不是逐个处理元素的Observer来缓解这种情况：

```java
PublishSubject<Integer> source = PublishSubject.<Integer>create();

source.window(500)
    .observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute, Throwable::printStackTrace);
```

使用带有参数500的window()方法，将告诉Observable将元素分组到500个大小的批次中。当Observer能够比一个一个地处理元素更快地处理一批元素时，这种技术可以减少过度生产Observable的问题。

## 5. 跳过元素

如果可以安全地忽略Observable生成的某些值，我们可以使用特定时间内的sample和throttling运算符。

sample()和throttleFirst()方法将持续时间作为参数：

-   sample()方法定期查看元素序列，并发出在指定为参数的持续时间内产生的最后一项
-   throttleFirst()方法发出在指定为参数的持续时间之后生成的第一个元素

持续时间是从生成的元素序列中挑选一个特定元素的时间。我们可以通过跳过元素来指定处理背压的策略：

```java
PublishSubject<Integer> source = PublishSubject.<Integer>create();

source.sample(100, TimeUnit.MILLISECONDS)
    .observeOn(Schedulers.computation())
    .subscribe(ComputeFunction::compute, Throwable::printStackTrace);
```

我们指定跳过元素的策略将是sample()方法，我们想要一个持续时间为100毫秒的序列样本，该元素将被发送到观察者。

但是请记住，这些运算符只会降低下游观察者接收值的速率，因此它们仍可能导致MissingBackpressureException。

## 6.  处理填充Observable缓冲区

如果我们的采样或批处理元素策略无法帮助填充缓冲区，我们需要实施一种策略来处理缓冲区填满时的情况。

我们需要使用onBackpressureBuffer()方法来防止BufferOverflowException。

onBackpressureBuffer()方法采用三个参数：Observable缓冲区的容量、当缓冲区填满时调用的方法、以及处理需要从缓冲区中丢弃的元素的策略。溢出策略在BackpressureOverflow类中。

当缓冲区填满时，可以执行4种类型的操作：

-   ON_OVERFLOW_ERROR：这是缓冲区已满时发出BufferOverflowException信号的默认行为
-   ON_OVERFLOW_DEFAULT：目前与ON_OVERFLOW_ERROR相同
-   ON_OVERFLOW_DROP_LATEST：如果发生溢出，当前值将被简单地忽略，一旦下游观察者请求，只有旧值将被传递
-   ON_OVERFLOW_DROP_OLDEST：删除缓冲区中最旧的元素并将当前值添加到其中

让我们看看如何指定该策略：

```java
Observable.range(1, 1_000_000)
    .onBackpressureBuffer(16, () -> {}, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)
    .observeOn(Schedulers.computation())
    .subscribe(e -> {}, Throwable::printStackTrace);
```

在这里，我们处理溢出缓冲区的策略是删除缓冲区中最旧的元素并添加由Observable生成的最新元素。

请注意，最后两种策略在丢弃元素时会导致流中的不连续性。此外，它们不会发出BufferOverflowException信号。

## 7. 丢弃所有生产过剩的元素

每当下游Observer未准备好接收元素时，我们可以使用onBackpressureDrop()方法从序列中删除该元素。

我们可以将该方法视为一个onBackpressureBuffer()方法，其缓冲区容量设置为零，策略为ON_OVERFLOW_DROP_LATEST。

当我们可以安全地忽略来自源Observable的值(例如鼠标移动或当前GPS位置信号)时，此运算符很有用，因为稍后会有更多最新值：

```java
Observable.range(1, 1_000_000)
    .onBackpressureDrop()
    .observeOn(Schedulers.computation())
    .doOnNext(ComputeFunction::compute)
    .subscribe(v -> {}, Throwable::printStackTrace);
```

onBackpressureDrop()方法消除了过度生产Observable的问题，但需要谨慎使用。

## 8. 总结

在本文中，我们研究了过度生成Observable的问题以及处理背压的方法。我们研究了当Observer无法像Observable生成元素那样快地消耗元素时缓冲、批处理和跳过元素的策略。