## 1. 概述

在本文中，我们将介绍[BookKeeper](https://bookkeeper.apache.org/)，这是一种实现分布式容错记录存储系统的服务。

## 2. 什么是簿记员？

BookKeeper 最初由 Yahoo 作为[ZooKeeper](https://www.baeldung.com/java-zookeeper)子项目开发，并于 2015 年成为顶级项目。BookKeeper 的核心目标是成为一个可靠的高性能系统，以数据结构存储日志条目序列(又名Records )称为 分类帐。

分类账的一个重要特征是它们只能追加且不可变。这使得 BookKeeper 成为某些应用程序的良好候选者，例如分布式日志系统、Pub-Sub 消息传递应用程序和实时流处理。

## 3. BookKeeper 概念

### 3.1. 日志条目

日志条目包含客户端应用程序存储到 BookKeeper 或从中读取的不可分割的数据单元。当存储在分类帐中时，每个条目都包含提供的数据和一些元数据字段。

这些元数据字段包括一个entryId，它在给定的账本中必须是唯一的。还有一个身份验证代码，BookKeeper 使用它来检测条目何时损坏或被篡改。

BookKeeper 本身不提供序列化功能，因此客户必须设计自己的方法来将更高级别的结构与字节数组相互转换。

### 3.2. 账本

分类帐是 BookKeeper 管理的基本存储单元，存储有序的日志条目序列。如前所述，分类帐具有仅附加语义，这意味着记录一旦添加到其中就无法修改。

此外，一旦客户端停止写入分类账并将其关闭，BookKeeper 就会将其密封，我们无法再向其中添加数据，即使是在稍后的时间也是如此。这是在围绕 BookKeeper 设计应用程序时要牢记的重要一点。分类帐不是直接实现更高级别构造(例如队列)的好候选者。相反，我们看到分类帐更常用于创建支持这些更高级别概念的更基本的数据结构。

例如，[Apache 的分布式日志](https://bookkeeper.apache.org/distributedlog/)项目使用分类帐作为日志段。这些片段被聚合到分布式日志中，但底层分类账对普通用户是透明的。

BookKeeper 通过跨多个服务器实例日志条目来实现分类账的弹性。三个参数控制保留多少服务器和副本：

-   Ensemble size：用于写入账本数据的服务器数量
-   写入仲裁大小：用于给定日志条目的服务器数量
-   Ack quorum size：必须确认给定日志条目写入操作的服务器数量

通过调整这些参数，我们可以调整给定账本的性能和弹性特征。当写入账本时，BookKeeper 只有在最小法定人数的集群成员确认操作时才会认为操作成功。

除了其内部元数据，BookKeeper 还支持将自定义元数据添加到账本中。这些是客户端在创建时传递的键/值对映射，BookKeeper 将其存储在 ZooKeeper 中。

### 3.3. 博彩公司

Bookies 是持有一个或多个模式分类账的服务器。BookKeeper 集群由许多在给定环境中运行的 bookie 组成，通过纯 TCP 或 TLS 连接为客户端提供服务。

Bookies 使用 ZooKeeper 提供的集群服务来协调操作。这意味着，如果我们想要实现一个完全容错的系统，我们至少需要一个 3 实例的 ZooKeeper 和一个 3 实例的 BookKeeper 设置。如果任何单个实例发生故障并且仍然能够正常运行，这样的设置将能够容忍损失，至少对于默认分类帐设置：3 节点整体大小、2 节点写入仲裁和 2 节点确认仲裁。

## 4.本地设置

在本地运行 BookKeeper 的基本要求非常适中。首先，我们需要启动并运行一个 ZooKeeper 实例，它为 BookKeeper 提供账本元数据存储。接下来，我们部署一个 bookie，它为客户端提供实际的服务。

虽然手动执行这些步骤当然是可能的，但在这里我们将使用一个使用官方 Apache 图像的docker-compose文件来简化此任务：

```bash
$ cd <path to docker-compose.yml>
$ docker-compose up
```

这个 docker-compose创建了三个 bookie 和一个 ZooKeeper 实例。由于所有 bookie 都在同一台机器上运行，因此它仅用于测试目的。官方文档包含配置完全容错集群的必要步骤。

让我们使用 bookkeeper 的 shell 命令listbookies做一个基本测试来检查它是否按预期工作 ：

```bash
$ docker exec -it apache-bookkeeper_bookie_1 /opt/bookkeeper/bin/bookkeeper 
  shell listbookies -readwrite
ReadWrite Bookies :
192.168.99.101(192.168.99.101):4181
192.168.99.101(192.168.99.101):4182
192.168.99.101(192.168.99.101):3181

```

输出显示可用的bookies列表，由三个 bookies 组成。请注意，显示的 IP 地址将根据本地 Docker 安装的具体情况而变化。

## 5. 使用账本 API

Ledger API 是与 BookKeeper 交互的最基本方式。它允许我们直接与 Ledger对象交互，但另一方面，它缺乏对更高级别抽象(如流)的直接支持。对于这些用例，BookKeeper 项目提供了另一个库 DistributedLog，它支持这些功能。

使用 Ledger API 需要在我们的项目中添加[bookkeeper-server](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.bookkeeper" AND a%3A"bookkeeper-server")依赖项：

```xml
<dependency>
    <groupId>org.apache.bookkeeper</groupId>
    <artifactId>bookkeeper-server</artifactId>
    <version>4.10.0</version>
</dependency>
```

注意：如文档中所述，使用此依赖项还将包括[protobuf](https://www.baeldung.com/google-protocol-buffer)和[guava](https://www.baeldung.com/category/guava/)库的依赖项。如果我们的项目也需要这些库，但版本与 BookKeeper 使用的不同，我们可以使用[替代依赖来隐藏这些库](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.bookkeeper" AND a%3A"bookkeeper-server-shaded")：

```xml
<dependency>
    <groupId>org.apache.bookkeeper</groupId>
    <artifactId>bookkeeper-server-shaded</artifactId>
    <version>4.10.0</version>
</dependency>

```

### 5.1. 连接到博彩公司

BookKeeper类是 Ledger API 的主要入口点，提供了一些连接到我们的 BookKeeper 服务的方法。在最简单的形式中，我们需要做的就是创建此类的新实例，传递 BookKeeper 使用的其中一个 ZooKeeper 服务器的地址：

```java
BookKeeper client = new BookKeeper("zookeeper-host:2131");

```

此处， zookeeper-host应设置为持有 BookKeeper 集群配置的 ZooKeeper 服务器的 IP 地址或主机名。在我们的例子中，这通常是“localhost”或 DOCKER_HOST 环境变量指向的主机。

如果我们需要更多地控制可用于微调我们的客户端的几个参数，我们可以使用ClientConfiguration实例并使用它来创建我们的客户端：

```java
ClientConfiguration cfg = new ClientConfiguration();
cfg.setMetadataServiceUri("zk+null://zookeeper-host:2131");

// ... set other properties
 
BookKeeper.forConfig(cfg).build();
```

### 5.2. 创建分类帐

一旦我们有了一个 BookKeeper实例，创建一个新的账本就很简单了：

```java
LedgerHandle lh = bk.createLedger(BookKeeper.DigestType.MAC,"password".getBytes());
```

在这里，我们使用了此方法的最简单变体。它将使用默认设置创建一个新分类帐，使用 MAC 摘要类型来确保条目完整性。

如果我们想将自定义元数据添加到我们的分类帐中，我们需要使用一个接受所有参数的变体：

```java
LedgerHandle lh = bk.createLedger(
  3,
  2,
  2,
  DigestType.MAC,
  "password".getBytes(),
  Collections.singletonMap("name", "my-ledger".getBytes()));
```

这一次，我们使用了完整版的 createLedger()方法。前三个参数分别是整体大小、写入仲裁和确认仲裁值。接下来，我们有和以前一样的摘要参数。最后，我们传递带有自定义元数据的Map。

在上述两种情况下， createLedger都是同步操作。BookKeeper 还提供使用回调的异步账本创建：

```java
bk.asyncCreateLedger(
  3,
  2,
  2,
  BookKeeper.DigestType.MAC, "passwd".getBytes(),
  (rc, lh, ctx) -> {
      // ... use lh to access ledger operations
  },
  null,
  Collections.emptyMap());

```

较新版本的 BookKeeper (>= 4.6) 也支持流畅风格的 API 和CompletableFuture以实现相同的目标：

```java
CompletableFuture<WriteHandle> cf = bk.newCreateLedgerOp()
  .withDigestType(org.apache.bookkeeper.client.api.DigestType.MAC)
  .withPassword("password".getBytes())
  .execute();

```

请注意，在这种情况下，我们得到一个WriteHandle而不是LedgerHandle。正如我们稍后将看到的，我们可以使用它们中的任何一个来访问我们的分类帐，因为 LedgerHandle实现了 WriteHandle。

### 5.3. 写入数据

一旦我们获得了LedgerHandle或WriteHandle ，我们就使用append()方法变体之一将数据写入关联的分类帐。让我们从同步变体开始：

```java
for(int i = 0; i < MAX_MESSAGES; i++) {
    byte[] data = new String("message-" + i).getBytes();
    lh.append(data);
}

```

在这里，我们使用了一个采用字节数组的变体。该 API 还支持 Netty 的ByteBuf和JavaNIO 的ByteBuffer，这可以在时间关键的场景中实现更好的内存管理。

对于异步操作，API 会因我们获取的特定句柄类型而略有不同。WriteHandle使用 CompletableFuture， 而LedgerHandle 也支持基于回调的方法：

```java
// Available in WriteHandle and LedgerHandle
CompletableFuture<Long> f = lh.appendAsync(data);

// Available only in LedgerHandle
lh.asyncAddEntry(
  data,
  (rc,ledgerHandle,entryId,ctx) -> {
      // ... callback logic omitted
  },
  null);
```

选择哪一个在很大程度上取决于个人选择，但一般来说，使用基于CompletableFuture的 API 往往更易于阅读。此外，还有一个附带的好处，我们可以直接从中构造一个Mono，从而更容易将 BookKeeper 集成到反应式应用程序中。

### 5.4. 读取数据

从 BookKeeper 账本中读取数据的方式与写入类似。首先，我们使用BookKeeper 实例创建一个 LedgerHandle ：

```java
LedgerHandle lh = bk.openLedger(
  ledgerId, 
  BookKeeper.DigestType.MAC,
  ledgerPassword);

```

除了我们稍后将介绍的ledgerId参数外，这段代码看起来很像我们之前看到的createLedger()方法。但是，有一个重要的区别；此方法返回一个只读的LedgerHandle实例。如果我们尝试使用任何可用的append()方法，我们得到的只是一个异常。

或者，更安全的方法是使用流畅风格的 API：

```java
ReadHandle rh = bk.newOpenLedgerOp()
  .withLedgerId(ledgerId)
  .withDigestType(DigestType.MAC)
  .withPassword("password".getBytes())
  .execute()
  .get();

```

ReadHandle具有从分类账中读取数据所需的方法：

```java
long lastId = lh.readLastConfirmed();
rh.read(0, lastId).forEach((entry) -> {
    // ... do something 
});
```

在这里，我们只是使用同步读取变体请求了该分类帐中的所有可用数据。正如预期的那样，还有一个异步变体：

```java
rh.readAsync(0, lastId).thenAccept((entries) -> {
    entries.forEach((entry) -> {
        // ... process entry
    });
});
```

如果我们选择使用旧的openLedger()方法，我们将找到支持异步方法回调样式的其他方法：

```java
lh.asyncReadEntries(
  0,
  lastId,
  (rc,lh,entries,ctx) -> {
      while(entries.hasMoreElements()) {
          LedgerEntry e = ee.nextElement();
      }
  },
  null);
```

### 5.5. 上市分类账

我们之前已经看到，我们需要分类账的 ID来打开和读取它的数据。那么，我们如何获得一个呢？一种方法是使用LedgerManager接口，我们可以从 BookKeeper 实例访问它。该接口主要处理账本元数据，但也有asyncProcessLedgers()方法。使用这种方法——以及一些有助于形成并发原语的方法——我们可以枚举所有可用的分类账：

```java
public List listAllLedgers(BookKeeper bk) {
    List ledgers = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch processDone = new CountDownLatch(1);

    bk.getLedgerManager()
      .asyncProcessLedgers(
        (ledgerId, cb) -> {
            ledgers.add(ledgerId);
            cb.processResult(BKException.Code.OK, null, null);
        }, 
        (rc, s, obj) -> {
            processDone.countDown();
        },
        null,
        BKException.Code.OK,
        BKException.Code.ReadException);
 
    try {
        processDone.await(1, TimeUnit.MINUTES);
        return ledgers;
    } catch (InterruptedException ie) {
        throw new RuntimeException(ie);
    }
}

```

让我们来消化这段代码，对于一个看似微不足道的任务来说，它比预期的要长一些。asyncProcessLedgers ()方法需要两个回调。

第一个收集列表中的所有分类帐 ID。我们在这里使用同步列表，因为可以从多个线程调用此回调。除了账本 ID 之外，此回调还接收一个回调参数。我们必须调用它的processResult()方法来确认我们已经处理了数据并表明我们已准备好获取更多数据。

当所有分类帐都已发送到处理器回调或出现故障时，将调用第二个回调。在我们的例子中，我们省略了错误处理。相反，我们只是递减CountDownLatch，这反过来将完成await操作并允许该方法返回所有可用分类账的列表。

## 六. 总结

在本文中，我们介绍了 Apache BookKeeper 项目，了解其核心概念并使用其低级 API 访问 Ledgers 并执行读/写操作。