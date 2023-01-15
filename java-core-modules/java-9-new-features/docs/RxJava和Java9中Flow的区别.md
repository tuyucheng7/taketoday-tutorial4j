## 1. 简介

Java Flow API作为Reactive Stream规范的实现在Java 9中被引入。

在本教程中，我们首先介绍响应式流，然后介绍它与RxJava和Flow API的关系。

## 2. 什么是响应式流

[Reactive Manifesto](https://www.reactive-streams.org/)引入了Reactive Streams来指定具有非阻塞背压的异步流处理标准。

Reactive Stream规范的范围是定义一组最小的接口来实现这些目的：

-   [org.reactivestreams.Publisher](http://www.reactive-streams.org/reactive-streams-1.0.0-javadoc/org/reactivestreams/Publisher.html)是一个数据提供者，它根据订阅者的需求向订阅者发布数据
    
-   [org.reactivestreams.Subscriber](http://www.reactive-streams.org/reactive-streams-1.0.0-javadoc/org/reactivestreams/Subscriber.html)是数据的消费者，它可以在订阅发布者后接收数据
    
-   [org.reactivestreams.Subscription](http://www.reactive-streams.org/reactive-streams-1.0.0-javadoc/org/reactivestreams/Subscription.html)在发布者接受订阅者时创建
    
-   [org.reactivestreams.Processor](http://www.reactive-streams.org/reactive-streams-1.0.0-javadoc/org/reactivestreams/Processor.html)既是订阅者又是发布者；它订阅发布者，处理数据，然后将处理后的数据传递给订阅者

Flow API源自规范，RxJava早于它，但从2.0开始，RxJava也支持该规范。我们将深入探讨两者，但首先，让我们看一个实际用例。

## 3. 用例

在本教程中，我们使用直播视频服务作为我们的用例。与点播视频流相反，直播视频流不依赖于消费者。因此，服务器以自己的速度发布流，而适应是消费者的责任。

在最简单的形式中，我们的模型由一个视频流发布者和一个作为订阅者的视频播放器组成。

让我们实现VideoFrame作为我们的数据项：

```java
public class VideoFrame {
    private long number;
    // additional data fields

    // constructor, getters, setters
}
```

然后，让我们逐一介绍我们的Flow API和RxJava实现。

## 4. 使用Flow API实现

JDK 9中的Flow API对应于Reactive Streams规范。使用Flow API，如果应用程序最初请求N个项目，则发布者最多向订阅者推送N个项目。

Flow API接口都在[java.util.concurrent.Flow](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Flow.html)接口中，它们在语义上等同于它们各自的Reactive Streams 对应物。

让我们将VideoStreamServer实现为VideoFrame的发布者：

```java
static class VideoStreamServer extends SubmissionPublisher<VideoFrame> {
	
	public VideoStreamServer() {
		super(Executors.newSingleThreadExecutor(), 5);
	}
}
```

我们从SubmissionPublisher扩展了VideoStreamServer，而不是直接实现Flow::Publisher。SubmissionPublisher是Flow::Publisher的JDK实现，用于与订阅者进行异步通信，因此它可以让我们的VideoStreamServer以自己的速度发出。

此外，它对背压和缓冲区处理也很有帮助，因为当SubmissionPublisher::subscribe被调用时，它会创建一个BufferedSubscription实例，然后将新的订阅添加到其订阅链中。BufferedSubscription可以将已发布的项目缓冲到SubmissionPublisher#maxBufferCapacity。

现在让我们定义VideoPlayer，它消费一个VideoFrame流，因此它必须实现Flow::Subscriber。

```java
public class VideoPlayer implements Flow.Subscriber<VideoFrame> {

    Flow.Subscription subscription = null;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(VideoFrame item) {
        log.info("play #{}", item.getNumber());
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("There is an error in video streaming:{}", throwable.getMessage());

    }

    @Override
    public void onComplete() {
        log.error("Video has ended");
    }
}
```

VideoPlayer订阅VideoStreamServer，订阅成功后调用VideoPlayer::onSubscribe方法，并请求一帧。VideoPlayer::onNext接收帧并请求新的帧，请求的帧数取决于用例和Subscriber实现。

最后，让我们把事情放在一起：

```java
VideoStreamServer streamServer = new VideoStreamServer();
streamServer.subscribe(new VideoPlayer());

// submit video frames

ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
AtomicLong frameNumber = new AtomicLong();
executor.scheduleWithFixedDelay(() -> {
    streamServer.offer(new VideoFrame(frameNumber.getAndIncrement()), (subscriber, videoFrame) -> {
        subscriber.onError(new RuntimeException("Frame#" + videoFrame.getNumber()
        + " droped because of backpressure"));
        return true;
    });
}, 0, 1, TimeUnit.MILLISECONDS);

sleep(1000);
```

## 5. 使用RxJava实现

RxJava是[ReactiveX](http://reactivex.io/)的Java实现，ReactiveX(或Reactive Extensions)项目旨在提供响应式编程概念。它是观察者模式、迭代器模式和函数式编程的组合。

RxJava的最新主要版本是3.x；RxJava从2.x版开始支持Reactive Streams及其Flowable基类，但它比 Reactive Streams具有多个基类(如Flowable、Observable、Single、Completable)更重要的集合。

Flowable作为响应流顺应性组件是具有背压处理的0到N个项目的流。Flowable从Reactive Streams扩展了Publisher。因此，许多RxJava运算符直接接收Publisher并允许与其他Reactive Streams实现直接互操作。

现在，让我们创建一个无限惰性流的视频流生成器：

```java
Stream<VideoFrame> videoStream = Stream.iterate(new VideoFrame(0), videoFrame -> {
    // sleep for 1ms;
    return new VideoFrame(videoFrame.getNumber() + 1);
});
```

然后我们定义一个Flowable实例来在单独的线程上生成帧：

```java
Flowable
    .fromStream(videoStream)
    .subscribeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
```

需要注意的是，无限流对我们来说已经足够了，但如果我们需要一种更灵活的方式来生成流，那么Flowable.create是一个不错的选择。

```java
Flowable
    .create(new FlowableOnSubscribe<VideoFrame>() {
        AtomicLong frame = new AtomicLong();
        
        @Override
        public void subscribe(@NonNull FlowableEmitter<VideoFrame> emitter) {
            while (true) {
                emitter.onNext(new VideoFrame(frame.incrementAndGet()));
                //sleep for 1 ms to simualte delay
            }
        }
    }, / Set Backpressure Strategy Here /)
```

然后，VideoPlayer订阅此Flowable并在单独的线程上观察元素。

```java
videoFlowable
    .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
    .subscribe(item -> {
        log.info("play #" + item.getNumber());
        // sleep for 30 ms to simualate frame display
    });
```

最后，我们配置背压策略。如果我们想在帧丢失的情况下停止视频，因此我们必须在缓冲区已满时使用BackpressureOverflowStrategy::ERROR。

```java
Flowable
    .fromStream(videoStream)
    .subscribeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
    .onBackpressureBuffer(5, null, BackpressureOverflowStrategy.ERROR)
    .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
    .subscribe(item -> {
         log.info("play #" + item.getNumber()); 
         // sleep for 30 ms to simualate frame display 
    });
```

## 6. RxJava和Flow API的比较

即使是实现一个这么简单的案例，我们也可以看出RxJava提供的API是多么的丰富，尤其是在缓冲区管理、错误处理和背压策略方面。它通过其流式的API为我们提供了更多选择和更少的代码。现在让我们考虑更复杂的情况。

假设我们的播放器在没有编解码器的情况下无法显示视频帧。因此，使用Flow API，我们需要实现一个Processor来模拟编解码器并置于服务器和播放器之间。使用RxJava，我们可以使用Flowable::flatMap或Flowable::map来实现。

或者让我们想象一下，我们的播放器也将播放实时翻译音频，因此我们必须合并来自不同发布者的视频和音频流。使用 RxJava，我们可以使用Flowable::combineLatest，但是使用Flow API，这不是一件容易的事。

尽管如此，可以编写一个订阅两个流并将组合数据发送到我们的VideoPlayer的自定义Processor。然而，实现是一个令人头疼的问题。

## 7. 为什么选择Flow API

此时，我们可能会有一个疑问，Flow API背后的哲学是什么？

如果在JDK中搜索哪里使用了Flow API，我们可以在java.net.http和jdk.internal.net.http中找到它的踪影。

此外，我们可以在Reactor或Reactive Stream包中找到适配器。例如，org.reactivestreams.FlowAdapters具有将Flow API接口转换为Reactive Stream接口的方法，反之亦然。因此，它有助于Flow API和具有响应流支持的库之间的互操作性。

所有这些事实都有助于我们理解Flow API的目的：它是在JDK中创建的一组响应式规范接口，不依赖第三方。此外，Java期望Flow API被接受为响应式规范的标准接口，并在JDK或其他实现中间件和实用程序的响应式规范、基于Java的库中使用。

## 8. 总结

在本教程中，我们介绍了Reactive Stream规范、Flow API和RxJava。此外，我们演示了用于实时视频流的 Flow API和RxJava实现的实际示例。
