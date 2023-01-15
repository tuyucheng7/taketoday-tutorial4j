## 1. 概述

在本文中，我们介绍Java 9 Reactive Streams。简而言之，我们能够使用Flow类，它包含用于构建响应流处理逻辑的主要构建块。

Reactive Streams是具有非阻塞背压的异步流处理标准。该规范在[Reactive Manifesto](http://www.reactive-streams.org/)中定义，并且有各种实现，例如RxJava和Akka-Streams、Reactor。

## 2. 响应式API概述

为了构建Flow，我们可以使用三个主要的抽象组件并将它们组合成异步处理逻辑。

每个Flow都需要处理由Publisher实例发布给它的事件；Publisher有一个方法subscribe()。

如果任何订阅者想要接收它发布的事件，他们需要订阅给定的Publisher。

消息的接收者需要实现Subscriber接口，通常这是每个Flow处理的结束，因为它的实例不会进一步发送消息。

我们可以将Subscriber视为Sink，这有四个需要重写的方法：onSubscribe()、onNext()、onError()和onComplete()，我们将在下一节中介绍这些内容。

如果我们想转换传入的消息并将其进一步传递给下一个Subscriber，我们需要实现Processor接口。它既充当Subscriber，因为它接收消息，又充当Publisher，因为它处理这些消息并将它们发送以供进一步处理。

## 3. 发布和消费消息

假设我们要创建一个简单的Flow(流)，其中我们有一个发布消息的Publisher(发布者)，以及一个在消息到达时消费消息的简单Subscriber(订阅者)——一次一个。

让我们创建一个EndSubscriber类，我们需要实现Subscriber接口。接下来，我们重写所需的方法。

在处理开始之前调用onSubscribe()方法，Subscription的实例作为参数传递。它是一个类，用于控制订阅者和发布者之间的消息流：

```java
public class EndSubscriber<T> implements Subscriber<T> {
    private Subscription subscription;
    public List<T> consumedElements = new LinkedList<>();

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }
}
```

我们还初始化了一个空的consumedElements集合，它用于测试目的。

现在，我们需要实现Subscriber接口剩余的方法。这里的主要方法是onNext() - 每当发布者发布新消息时都会调用该方法：

```java
@Override
public void onNext(T item) {
    System.out.println("Got : " + item);
    consumedElements.add(item);
    subscription.request(1);
}
```

请注意，当我们在onSubscribe()方法中启动订阅并处理消息时，我们需要调用Subscription上的request()方法，来表示当前订阅者已准备好消费更多消息。

最后，我们需要实现onError() - 在处理过程中抛出一些异常时调用它；以及onComplete() - 在Publisher关闭时调用：

```java
@Override
public void onError(Throwable t) {
	t.printStackTrace();
}

@Override
public void onComplete() {
	System.out.println("Done");
}
```

让我们为处理流程编写一个测试。我们将使用SubmissionPublisher类——来自java.util.concurrent的构造——它实现了Publisher接口。

我们将向Publisher提交N个元素——我们的EndSubscriber将收到：

```java
@Test
void givenPublisher_whenSubscribeToIt_thenShouldConsumeAllElements() throws InterruptedException {
	// given
	SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
	EndSubscriber<String> subscriber = new EndSubscriber<>();
	publisher.subscribe(subscriber);
	List<String> items = List.of("1", "x", "2", "x", "3", "x");
    
	// when
	assertThat(publisher.getNumberOfSubscribers()).isEqualTo(1);
	items.forEach(publisher::submit);
	publisher.close();
    
	// then
	await().atMost(1000, TimeUnit.MILLISECONDS).untilAsserted(
			() -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(items));
}
```

请注意，我们在EndSubscriber的实例上调用close()方法。它将在给定发布者的每个订阅者上调用onComplete()回调。

运行该程序将产生以下输出：

```shell
Got : 1
Got : x
Got : 2
Got : x
Got : 3
Got : x
Done
```

## 4. 消息的转换

假设我们想要在Publisher和Subscriber之间构建类似的逻辑，但还要应用一些转换。

我们将创建实现Processor并扩展SubmissionPublisher的TransformProcessor类——因为它既是发布者又是订阅者。

我们传入一个将输入转换为输出的函数：

```java
public class TransformProcessor<T, R> extends SubmissionPublisher<R> implements Flow.Processor<T, R> {

    private Function<T, R> function;
    private Flow.Subscription subscription;

    public TransformProcessor(Function<T, R> function) {
        super();
        this.function = function;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        submit(function.apply(item));
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onComplete() {
        close();
    }
}
```

现在让我们使用Publisher发布String元素的处理流编写一个快速测试。

我们的TransformProcessor将把String解析为Integer，这意味着这里需要进行转换：

```java
@Test
void givenPublisher_whenSubscribeAndTransformElements_thenShouldConsumeAllElements() throws InterruptedException {
	// given
	SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
	TransformProcessor<String, Integer> transformProcessor = new TransformProcessor<>(Integer::parseInt);
	EndSubscriber<Integer> subscriber = new EndSubscriber<>();
	List<String> items = List.of("1", "2", "3");
	List<Integer> expectedResult = List.of(1, 2, 3);
    
	// when
	publisher.subscribe(transformProcessor);
	transformProcessor.subscribe(subscriber);
	items.forEach(publisher::submit);
	publisher.close();
    
	// then
	await().atMost(1000, TimeUnit.MILLISECONDS).untilAsserted(
			() -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(expectedResult));
}
```

请注意，调用基础Publisher上的close()方法将导致调用TransformProcessor上的onComplete()方法。记住，处理链中的所有发布者都需要以这种方式关闭。

## 5. 使用Subscription控制消息需求

假设我们只想消费Subscription中的第一个元素，应用一些逻辑并完成处理。我们可以使用request()方法来实现这一点。

让我们修改EndSubscriber类，使其只消费N条消息。我们将该数字作为howMuchMessagesConsume构造函数参数传递：

```java
public class EndSubscriber<T> implements Subscriber<T> {
	private final AtomicInteger howMuchMessagesToConsume;
	private Subscription subscription;
	public List<T> consumedElements = new LinkedList<>();

	public EndSubscriber(Integer howMuchMessagesToConsume) {
		this.howMuchMessagesToConsume = new AtomicInteger(howMuchMessagesToConsume);
	}

	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(1);
	}

	@Override
	public void onNext(T item) {
		howMuchMessagesToConsume.decrementAndGet();
		System.out.println("Got : " + item);
		consumedElements.add(item);
		if (howMuchMessagesToConsume.get() > 0) {
			subscription.request(1);
		}
	}

	// ...
}
```

我们可以根据需要请求元素。让我们编写一个测试，其中我们只想消费给定Subscription中的一个元素：

```java
@Test
void givenPublisher_whenRequestForOnlyOneElement_thenShouldConsumeOnlyThatOne() throws InterruptedException {
	// given
	SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
	EndSubscriber<String> subscriber = new EndSubscriber<>(1);
	publisher.subscribe(subscriber);
	List<String> items = List.of("1", "x", "2", "x", "3", "x");
	List<String> expected = List.of("1");
    
	// when
	assertThat(publisher.getNumberOfSubscribers()).isEqualTo(1);
	items.forEach(publisher::submit);
	publisher.close();
    
	// then
	await().atMost(1000, TimeUnit.MILLISECONDS).untilAsserted(
			() -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(expected));
}
```

尽管发布者发布了六个元素，但我们的EndSubscriber将只消费一个元素，因为它表示只需要处理一个元素。

通过在Subscription上使用request()方法，我们可以实现更复杂的背压机制来控制消息消费的速度。

## 6. 总结

在本文中，我们介绍了Java 9 Reactive Streams。我们演示了如何创建一个由发布者和订阅者组成的处理流，并使用Processors转换元素创建了一个更复杂的处理流。最后，我们通过使用Subscription来控制订阅者对元素的需求。
