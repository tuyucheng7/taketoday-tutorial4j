## 1. 概述

在本教程中，我们将探索MockConsumer，它是[Kafka](https://www.baeldung.com/tag/kafka/)的Consumer实现之一。

首先，我们将讨论测试 Kafka Consumer时要考虑的主要事项。然后，我们将看到如何使用MockConsumer来实现测试。

## 2. 测试 Kafka消费者

从 Kafka 消费数据包括两个主要步骤。首先，我们必须手动订阅主题或分配主题分区。其次，我们使用poll 方法轮询成批的记录。

轮询通常在无限循环中完成。那是因为我们通常希望连续使用数据。

例如，让我们考虑仅由订阅和轮询循环组成的简单消费逻辑：

```java
void consume() {
    try {
        consumer.subscribe(Arrays.asList("foo", "bar"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            records.forEach(record -> processRecord(record));
        }
    } catch (WakeupException ex) {
        // ignore for shutdown
    } catch (RuntimeException ex) {
        // exception handling
    } finally {
        consumer.close();
    }
}
```

看看上面的代码，我们可以看到有几件事我们可以测试：

-   订阅
-   轮询循环
-   异常处理
-   消费者是否正确关闭

我们有多种选择来测试消费逻辑。

我们可以使用内存中的 Kafka 实例。但是，这种方法有一些缺点。通常，内存中的 Kafka 实例会使测试变得非常繁重和缓慢。此外，设置它不是一项简单的任务，并且可能导致不稳定的测试。

或者，我们可以使用模拟框架来模拟消费者。 虽然使用这种方法可以使测试变得轻量级，但设置起来可能有些棘手。

最后一个选项，也许是最好的选项，是使用MockConsumer，这是一个用于测试的Consumer实现。它不仅可以帮助我们构建轻量级测试，而且设置起来也很容易。

让我们看一下它提供的功能。

## 3.使用模拟消费者

MockConsumer实现了kafka-clients库提供的Consumer接口。因此，它模拟了真实消费者的整个行为，而无需我们编写大量代码。

让我们看一下MockConsumer的一些使用示例。特别是，我们将采用在测试消费者应用程序时可能遇到的一些常见场景，并使用MockConsumer实现它们。

对于我们的示例，让我们考虑一个从 Kafka 主题消费国家人口更新的应用程序。更新仅包含国家名称及其当前人口：

```java
class CountryPopulation {

    private String country;
    private Integer population;

    // standard constructor, getters and setters
}
```

我们的消费者只是使用 Kafka消费者实例轮询更新，处理它们，最后，使用commitSync方法提交偏移量：

```java
public class CountryPopulationConsumer {

    private Consumer<String, Integer> consumer;
    private java.util.function.Consumer<Throwable> exceptionConsumer;
    private java.util.function.Consumer<CountryPopulation> countryPopulationConsumer;

    // standard constructor

    void startBySubscribing(String topic) {
        consume(() -> consumer.subscribe(Collections.singleton(topic)));
    }

    void startByAssigning(String topic, int partition) {
        consume(() -> consumer.assign(Collections.singleton(new TopicPartition(topic, partition))));
    }

    private void consume(Runnable beforePollingTask) {
        try {
            beforePollingTask.run();
            while (true) {
                ConsumerRecords<String, Integer> records = consumer.poll(Duration.ofMillis(1000));
                StreamSupport.stream(records.spliterator(), false)
                    .map(record -> new CountryPopulation(record.key(), record.value()))
                    .forEach(countryPopulationConsumer);
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            System.out.println("Shutting down...");
        } catch (RuntimeException ex) {
            exceptionConsumer.accept(ex);
        } finally {
            consumer.close();
        }
    }

    public void stop() {
        consumer.wakeup();
    }
}
```

### 3.1. 创建MockConsumer实例

接下来，让我们看看如何创建MockConsumer的实例：

```java
@BeforeEach
void setUp() {
    consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
    updates = new ArrayList<>();
    countryPopulationConsumer = new CountryPopulationConsumer(consumer, 
      ex -> this.pollException = ex, updates::add);
}
```

基本上，我们需要提供的只是偏移重置策略。

请注意，我们使用 更新来收集countryPopulationConsumer 将收到的记录。这将有助于我们断言预期的结果。

同样，我们使用 pollException 来收集和断言异常。

对于所有测试用例，我们将使用上述设置方法。现在，让我们看一下消费者应用程序的几个测试用例。

### 3.2. 分配主题分区

首先，让我们为startByAssigning方法创建一个测试：

```java
@Test
void whenStartingByAssigningTopicPartition_thenExpectUpdatesAreConsumedCorrectly() {
    // GIVEN
    consumer.schedulePollTask(() -> consumer.addRecord(record(TOPIC, PARTITION, "Romania", 19_410_000)));
    consumer.schedulePollTask(() -> countryPopulationConsumer.stop());

    HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
    TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
    startOffsets.put(tp, 0L);
    consumer.updateBeginningOffsets(startOffsets);

    // WHEN
    countryPopulationConsumer.startByAssigning(TOPIC, PARTITION);

    // THEN
    assertThat(updates).hasSize(1);
    assertThat(consumer.closed()).isTrue();
}
```

首先，我们设置MockConsumer。我们首先使用addRecord方法向消费者添加一条记录。

首先要记住的是，我们不能在分配或订阅主题之前添加记录。这就是我们使用schedulePollTask 方法安排轮询任务的原因。我们安排的任务将在获取记录之前的第一次轮询上运行。因此，记录的添加将在分配发生后发生。

同样重要的是，我们不能将不属于分配给它的主题和分区的记录添加到MockConsumer中。

然后，为了确保消费者不会无限期地运行，我们将其配置为在第二次轮询时关闭。

此外，我们必须设置起始偏移量。我们使用updateBeginningOffsets 方法执行此操作。

最后，我们检查我们是否正确消费了更新，并关闭了消费者。

### 3.3. 订阅主题

现在，让我们为startBySubscribing方法创建一个测试：

```java
@Test
void whenStartingBySubscribingToTopic_thenExpectUpdatesAreConsumedCorrectly() {
    // GIVEN
    consumer.schedulePollTask(() -> {
        consumer.rebalance(Collections.singletonList(new TopicPartition(TOPIC, 0)));
        consumer.addRecord(record("Romania", 1000, TOPIC, 0));
    });
    consumer.schedulePollTask(() -> countryPopulationConsumer.stop());

    HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
    TopicPartition tp = new TopicPartition(TOPIC, 0);
    startOffsets.put(tp, 0L);
    consumer.updateBeginningOffsets(startOffsets);

    // WHEN
    countryPopulationConsumer.startBySubscribing(TOPIC);

    // THEN
    assertThat(updates).hasSize(1);
    assertThat(consumer.closed()).isTrue();
}
```

在这种情况下，添加记录之前要做的第一件事是重新平衡。我们通过调用模拟重新平衡的重新平衡 方法来做到这一点。

其余与startByAssigning测试用例相同。

### 3.4. 控制轮询循环

我们可以通过多种方式控制轮询循环。

第一个选项是像我们在上面的测试中所做的那样安排轮询任务。我们通过schedulePollTask 执行此操作， 它以Runnable作为参数。当我们调用poll方法时，我们安排的每个任务都会运行。

我们的第二个选择是调用唤醒 方法。通常，这就是我们中断长轮询调用的方式。实际上，我们就是这样实现CountryPopulationConsumer中的stop方法的。

最后，我们可以使用setPollException方法设置要抛出的异常：

```java
@Test
void whenStartingBySubscribingToTopicAndExceptionOccurs_thenExpectExceptionIsHandledCorrectly() {
    // GIVEN
    consumer.schedulePollTask(() -> consumer.setPollException(new KafkaException("poll exception")));
    consumer.schedulePollTask(() -> countryPopulationConsumer.stop());

    HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
    TopicPartition tp = new TopicPartition(TOPIC, 0);
    startOffsets.put(tp, 0L);
    consumer.updateBeginningOffsets(startOffsets);

    // WHEN
    countryPopulationConsumer.startBySubscribing(TOPIC);

    // THEN
    assertThat(pollException).isInstanceOf(KafkaException.class).hasMessage("poll exception");
    assertThat(consumer.closed()).isTrue();
}
```

### 3.5. 模拟结束偏移量和分区信息

如果我们的消费逻辑基于结束偏移量或分区信息，我们也可以使用MockConsumer模拟这些。

当我们想要模拟结束偏移量时，我们可以使用addEndOffsets和updateEndOffsets方法。

而且，如果我们想要模拟分区信息，我们可以使用updatePartitions方法。

## 4. 总结

在本文中，我们探索了如何使用MockConsumer来测试 Kafka 消费者应用程序。

首先，我们已经查看了消费者逻辑的示例以及要测试的基本部分。然后，我们使用MockConsumer测试了一个简单的 Kafka 消费者应用程序。

在此过程中，我们研究了MockConsumer的特性以及如何使用它。