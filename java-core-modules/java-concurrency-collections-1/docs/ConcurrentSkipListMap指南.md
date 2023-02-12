## 1. 概述

在这篇简短的文章中，我们将研究java.util.concurrent包中的[ConcurrentSkipListMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ConcurrentSkipListMap.html)类。

这种构造允许我们以无锁的方式创建线程安全的逻辑。当我们想要在其他线程仍在向Map中插入数据时制作数据的不可变快照时，它是解决问题的理想选择。

**我们想要解决的一个问题是对事件流进行排序，并使用该构造获取过去60秒内到达的事件的快照**。

## 2. 流排序逻辑

假设我们有一个不断来自多个线程的事件流。我们需要能够获取过去60秒的事件，以及超过60秒的事件。

首先，让我们定义事件数据的结构：

```java
public class Event {
    private ZonedDateTime eventTime;
    private String content;
    // standard constructors/getters
}
```

我们希望使用eventTime字段对事件进行排序。要使用ConcurrentSkipListMap实现此目的，我们需要在创建它的实例时将Comparator传递给它的构造函数：

```java
class EventWindowSort {
    private final ConcurrentSkipListMap<ZonedDateTime, String> events 
          = new ConcurrentSkipListMap<>(
                Comparator.comparingLong(value -> value.toInstant().toEpochMilli()));
}
```

我们将使用时间戳比较所有到达的事件。我们使用comparingLong()方法并传递keyExtractor函数，该函数可以从ZonedDateTime获取时间戳。

当我们的事件到达时，我们只需要使用put()方法将它们添加到Map中。请注意，此方法不需要任何显式同步：

```java
void acceptEvent(Event event) {
    events.put(event.getEventTime(), event.getContent());
}
```

ConcurrentSkipListMap将使用在构造函数中传递给它的Comparator来处理这些事件的排序。

ConcurrentSkipListMap最显著的优点是可以以无锁方式为其数据制作不可变快照的方法。要获取过去一分钟内到达的所有事件，我们可以使用tailMap()方法并传递我们想要获取元素的时间：

```java
ConcurrentNavigableMap<ZonedDateTime, String> getEventsFromLastMinute() {
    return events.tailMap(ZonedDateTime.now().minusMinutes(1));
}
```

它将返回过去一分钟的所有事件。这将是一个不可变的快照，最重要的是其他写入线程可以将新事件添加到ConcurrentSkipListMap而无需执行显式锁定。

现在，我们可以使用headMap()方法获取一分钟后到达的所有事件：

```java
ConcurrentNavigableMap<ZonedDateTime, String> getEventsOlderThatOneMinute() {
    return events.headMap(ZonedDateTime.now().minusMinutes(1));
}
```

这将返回超过一分钟的所有事件的不可变快照。以上所有方法都属于EventWindowSort类，我们将在下一节中使用它们。

## 3. 测试流排序逻辑

一旦我们使用ConcurrentSkipListMap实现了我们的排序逻辑，我们现在可以**通过创建两个写线程来测试它**，每个线程将发送100个事件：

```java
ExecutorService executorService = Executors.newFixedThreadPool(3);
EventWindowSort eventWindowSort = new EventWindowSort();
int numberOfThreads = 2;

Runnable producer = () -> IntStream
    .rangeClosed(0, 100)
    .forEach(index -> eventWindowSort.acceptEvent(
        new Event(ZonedDateTime.now().minusSeconds(index), UUID.randomUUID().toString()))
    );
                
for (int i = 0; i < numberOfThreads; i++) {
    executorService.execute(producer);
}
```

每个线程都在调用acceptEvent()方法，发送具有eventTime从现在到“现在减去100秒”的事件。

同时，我们可以调用getEventsFromLastMinute()方法，该方法将返回一分钟窗口内的事件快照：

```java
ConcurrentNavigableMap<ZonedDateTime, String> eventsFromLastMinute = eventWindowSort.getEventsFromLastMinute();
```

eventsFromLastMinute中的事件数量在每次测试运行中都会有所不同，具体取决于生产者线程将事件发送到EventWindowSort的速度。不过我们可以断言返回的快照中没有一个事件超过一分钟：

```java
long eventsOlderThanOneMinute = eventsFromLastMinute
    .entrySet()
    .stream()
    .filter(e -> e.getKey().isBefore(ZonedDateTime.now().minusMinutes(1)))
    .count();

assertEquals(eventsOlderThanOneMinute, 0);
```

并且事件快照中一分钟窗口内的事件数量超过零：

```java
long eventsYoungerThanOneMinute = eventsFromLastMinute
    .entrySet()
    .stream()
    .filter(e -> e.getKey().isAfter(ZonedDateTime.now().minusMinutes(1)))
    .count();

assertTrue(eventsYoungerThanOneMinute > 0);
```

我们的getEventsFromLastMinute()方法在背后调用tailMap()。

现在让我们测试使用ConcurrentSkipListMap中的headMap()方法的getEventsOlderThatOneMinute()：

```java
ConcurrentNavigableMap<ZonedDateTime, String> eventsFromLastMinute = eventWindowSort.getEventsOlderThatOneMinute();
```

这次我们得到的是超过一分钟的事件的快照，我们可以断言这样的事件大于零：

```java
long eventsOlderThanOneMinute = eventsFromLastMinute
    .entrySet()
    .stream()
    .filter(e -> e.getKey().isBefore(ZonedDateTime.now().minusMinutes(1)))
    .count();
        
assertTrue(eventsOlderThanOneMinute > 0);
```

接下来，没有一个事件是在最后一分钟内发生的：

```java
long eventYoungerThanOneMinute = eventsFromLastMinute
    .entrySet()
    .stream()
    .filter(e -> e.getKey().isAfter(ZonedDateTime.now().minusMinutes(1)))
    .count();

assertEquals(eventYoungerThanOneMinute, 0);
```

需要注意的最重要的一点是，**我们可以在其他线程仍在向ConcurrentSkipListMap添加新值时获取数据快照**。

## 4. 总结

在这个快速教程中，我们了解了ConcurrentSkipListMap的基础知识以及一些实际示例。

我们利用ConcurrentSkipListMap的高性能来实现一种非阻塞算法，即使有多个线程同时更新Map，该算法也可以为我们提供不可变的数据快照。