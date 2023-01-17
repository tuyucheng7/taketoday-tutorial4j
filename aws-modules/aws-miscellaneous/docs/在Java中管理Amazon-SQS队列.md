## 1. 概述

在本教程中，我们将探讨如何使用Java SDK使用 Amazon 的[SQS](https://aws.amazon.com/sqs/) (简单队列服务) 。

## 2.先决条件

使用适用于 SQS 的 Amazon AWS SDK 所需的 Maven 依赖项、AWS 帐户设置和客户端连接与[此处本文](https://www.baeldung.com/aws-s3-java)中的相同。

假设我们已经创建了 AWSCredentials的实例，如上一篇文章所述，我们可以继续创建我们的 SQS 客户端：

```java
AmazonSQS sqs = AmazonSQSClientBuilder.standard()
  .withCredentials(new AWSStaticCredentialsProvider(credentials))
  .withRegion(Regions.US_EAST_1)
  .build();

```

## 3.创建队列

一旦我们设置了 SQS 客户端，创建队列就相当简单了。

### 3.1. 创建标准队列

让我们看看如何创建标准队列。为此，我们需要创建一个 CreateQueueRequest 实例：

```java
CreateQueueRequest createStandardQueueRequest = new CreateQueueRequest("baeldung-queue");
String standardQueueUrl = sqs.createQueue(createStandardQueueRequest).getQueueUrl();

```

### 3.2. 创建 FIFO 队列

创建 FIFO 类似于创建标准队列。我们仍然会使用 CreateQueueRequest的一个实例，就像我们之前所做的那样。只有这一次，我们必须传入队列属性，并将FifoQueue属性设置为true：

```java
Map<String, String> queueAttributes = new HashMap<>();
queueAttributes.put("FifoQueue", "true");
queueAttributes.put("ContentBasedDeduplication", "true");
CreateQueueRequest createFifoQueueRequest = new CreateQueueRequest(
  "baeldung-queue.fifo").withAttributes(queueAttributes);
String fifoQueueUrl = sqs.createQueue(createFifoQueueRequest)
  .getQueueUrl();

```

## 4. 将消息发布到队列

一旦我们设置好队列，我们就可以开始发送消息了。

### 4.1. 将消息发布到标准队列

要将消息发送到标准队列，我们必须创建一个 SendMessageRequest 实例。

然后我们将消息属性映射附加到此请求：

```java
Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
messageAttributes.put("AttributeOne", new MessageAttributeValue()
  .withStringValue("This is an attribute")
  .withDataType("String"));  
    
SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
  .withQueueUrl(standardQueueUrl)
  .withMessageBody("A simple message.")
  .withDelaySeconds(30)
  .withMessageAttributes(messageAttributes);

sqs.sendMessage(sendMessageStandardQueue);

```

withDelaySeconds () 指定消息应在多长时间后到达队列。

### 4.2. 将消息发布到 FIFO 队列

在这种情况下，唯一的区别是我们必须指定 消息所属的[组：](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/FIFO-queues.html)

```java
SendMessageRequest sendMessageFifoQueue = new SendMessageRequest()
  .withQueueUrl(fifoQueueUrl)
  .withMessageBody("Another simple message.")
  .withMessageGroupId("baeldung-group-1")
  .withMessageAttributes(messageAttributes);
```

正如你在上面的代码示例中看到的，我们使用 withMessageGroupId() 指定组。

### 4.3. 将多条消息发布到队列

我们还可以使用单个请求将多条消息发布到队列中。我们将创建一个 SendMessageBatchRequestEntry列表，我们将使用 SendMessageBatchRequest实例发送该列表：

```java
List <SendMessageBatchRequestEntry> messageEntries = new ArrayList<>();
messageEntries.add(new SendMessageBatchRequestEntry()
  .withId("id-1")
  .withMessageBody("batch-1")
  .withMessageGroupId("baeldung-group-1"));
messageEntries.add(new SendMessageBatchRequestEntry()
  .withId("id-2")
  .withMessageBody("batch-2")
  .withMessageGroupId("baeldung-group-1"));

SendMessageBatchRequest sendMessageBatchRequest
 = new SendMessageBatchRequest(fifoQueueUrl, messageEntries);
sqs.sendMessageBatch(sendMessageBatchRequest);
```

## 5. 从队列中读取消息

我们可以通过在ReceiveMessageRequest实例上 调用 receiveMessage() 方法来接收来自队列的消息：

```java
ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(fifoQueueUrl)
  .withWaitTimeSeconds(10)
  .withMaxNumberOfMessages(10);

List<Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();

```

使用withMaxNumberOfMessages()，我们指定从队列中获取多少消息——尽管应该注意最大值是10。

withWaitTimeSeconds()方法启用[长轮询](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-long-polling.html)。 长轮询是一种限制我们发送给 SQS 的接收消息请求数量的方法。 

简而言之，这意味着我们将等待指定的秒数来检索消息。如果在这段时间内队列中没有消息，则请求将返回空。如果消息在此期间到达队列，它将被返回。

我们可以获得给定消息的属性和正文：

```java
sqsMessages.get(0).getAttributes();
sqsMessages.get(0).getBody();
```

## 6. 从队列中删除消息

要删除消息，我们将使用 DeleteMessageRequest：

```java
sqs.deleteMessage(new DeleteMessageRequest()
  .withQueueUrl(fifoQueueUrl)
  .withReceiptHandle(sqsMessages.get(0).getReceiptHandle()));

```

## 7. 死信队列

[死信队列](https://en.wikipedia.org/wiki/Dead_letter_queue)的 类型必须与其基本队列相同—— 如果基本队列是 FIFO，则死信队列必须是 FIFO，如果基本队列是标准队列，则必须是标准队列。对于此示例，我们将使用标准队列。

我们需要做的第一件事是创建将成为我们的死信队列的东西：

```java
String deadLetterQueueUrl = sqs.createQueue("baeldung-dead-letter-queue").getQueueUrl();

```

接下来，我们将获取新创建队列的[ARN(亚马逊资源名称)](https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html)：

```java
GetQueueAttributesResult deadLetterQueueAttributes = sqs.getQueueAttributes(
  new GetQueueAttributesRequest(deadLetterQueueUrl)
    .withAttributeNames("QueueArn"));

String deadLetterQueueARN = deadLetterQueueAttributes.getAttributes()
  .get("QueueArn");

```

最后，我们将这个新创建的队列设置为我们原来标准队列的死信队列：

```java
SetQueueAttributesRequest queueAttributesRequest = new SetQueueAttributesRequest()
  .withQueueUrl(standardQueueUrl)
  .addAttributesEntry("RedrivePolicy",
    "{"maxReceiveCount":"2", "
      + ""deadLetterTargetArn":"" + deadLetterQueueARN + ""}");

sqs.setQueueAttributes(queueAttributesRequest);

```

我们在构建SetQueueAttributesRequest 实例时在addAttributesEntry() 方法 中设置的 JSON 数据包 包含我们需要的信息： maxReceiveCount是2，这意味着如果一条消息被接收了这么多次，则认为它没有被正确处理，并且被发送到我们的死信队列。

deadLetterTargetArn属性将 我们的标准队列指向我们新创建的死信队列。

## 8.监控

我们可以检查给定队列中当前有多少消息，以及有多少消息正在[使用](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html)SDK。首先，我们需要创建一个 GetQueueAttributesRequest。 

从那里我们将检查队列的状态：

```java
GetQueueAttributesRequest getQueueAttributesRequest 
  = new GetQueueAttributesRequest(standardQueueUrl)
    .withAttributeNames("All");
GetQueueAttributesResult getQueueAttributesResult 
  = sqs.getQueueAttributes(getQueueAttributesRequest);
System.out.println(String.format("The number of messages on the queue: %s", 
  getQueueAttributesResult.getAttributes()
    .get("ApproximateNumberOfMessages")));
System.out.println(String.format("The number of messages in flight: %s", 
  getQueueAttributesResult.getAttributes()
    .get("ApproximateNumberOfMessagesNotVisible")));
```

[使用Amazon Cloud Watch](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-monitoring-using-cloudwatch.html)可以实现更深入的监控。

## 9.总结

在本文中，我们了解了如何使用 AWSJavaSDK 管理 SQS 队列。