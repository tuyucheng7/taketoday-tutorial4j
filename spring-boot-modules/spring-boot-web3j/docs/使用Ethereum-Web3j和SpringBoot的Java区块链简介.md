## 1. 概述

过去几个月，区块链是IT界的流行语之一。该术语与加密货币有关，并与比特币一起创建。它是分散的、不可变的数据结构，分为多个块，这些块使用加密算法进行链接和保护。

此结构中的每个块通常包含前一个块的加密哈希、时间戳和交易数据。区块链由点对点网络管理，在节点间通信期间，每个新块在添加之前都会经过验证。

这是关于区块链的一小部分理论。简而言之，这是一项允许我们以去中心化方式管理两方之间交易的技术。现在，问题是我们如何在我们的系统中实现它。

以太坊(Ethereum)来了。它是由Vitarik Buterin创建的去中心化平台，为应用程序的开发提供脚本语言。它基于比特币的想法，并由称为Ether的新加密货币驱动。如今，以太币是仅次于比特币的第二大加密货币。

以太坊技术的核心是EVM(以太坊虚拟机)，它可以被视为类似于JVM的东西，但使用完全去中心化的节点网络。为了在Java世界中实现基于以太坊的交易，我们使用Web3j库。这是一个轻量级、响应式、类型安全的Java和Android库，用于与以太坊区块链上的节点集成。更多详细信息可以在其网站[https://web3j.io](https://web3j.io/)上找到。

## 2. 在本地运行以太坊

尽管网络上有很多关于区块链和以太坊的文章，但要找到描述如何在本地机器上运行现成可用的以太坊实例的解决方案并不容易。值得一提的是，通常我们可以使用两个最流行的以太坊客户端：Geth和Parity。

事实证明，我们可以使用Docker容器轻松地在本地运行Geth节点。默认情况下，它将节点连接到以太坊主网络。或者，你可以将其连接到测试网络或Rinkeby网络。但是开始的最佳选择是通过--dev在Docker容器运行命令上设置参数以开发模式运行它。

下面是在开发模式下启动Docker容器并在端口8545上公开以太坊RPC API的命令：

```shell
$ docker run -d --name ethereum -p 8545:8545 -p 30303:30303 ethereum/client-go --rpc --rpcaddr "0.0.0.0" --rpcapi="db,eth,net,web3,personal" --rpccorsdomain "*" --dev
```

在开发模式下运行该容器时，一个非常好的消息是你的默认测试帐户上有大量以太币。在这种情况下，你无需开采任何以太币即可开始测试。

现在，让我们创建一些其他测试帐户并检查一些内容。为了实现它，我们需要在Docker容器中运行Geth的交互式JavaScript控制台。

```shell
$ docker exec -it ethereum geth attach ipc:/tmp/geth.ipc
```

## 3. 使用JavaScript控制台管理以太坊节点

运行JavaScript控制台后，你可以轻松显示默认账户(coinbase)、所有可用账户及其余额的列表。这是显示我的以太坊节点结果的屏幕。

<img src="../assets/img.png">

现在，我们必须创建一些测试帐户。我们可以通过调用personal.newAccount(password)函数来做到这一点。

创建所需账户后，你可以使用JavaScript控制台执行一些测试交易，并将部分资金从基础账户转移到新创建的账户。以下是用于创建帐户和执行交易的命令。

<img src="../assets/img_1.png">

## 4. Spring Boot系统的区块链

我们的例系统的架构非常简单。我不想让任何事情复杂化，只是向你展示如何将交易发送到Geth节点并接收通知。

当transaction-service向以太坊节点发送新交易时，bonus-service观察节点并监听传入交易。然后，每从他的账户收到10笔交易，它就会向发件人的账户发送一次奖金。下面的图表说明了我们的示例系统的架构：

<img src="../assets/img_2.png">

## 5. 为以太坊Spring Boot应用启用Web3j

我认为，现在我们已经明确了我们想要做什么。因此，让我们继续实现。首先，我们应该包含所有必需的依赖项，以便能够在Spring Boot应用程序中使用Web3j库。幸运的是，可以包含一个启动器。

```xml
<dependency>
    <groupId>org.web3j</groupId>
    <artifactId>web3j-spring-boot-starter</artifactId>
    <version>1.6.0</version>
</dependency>
```

因为我们在Docker容器上运行以太坊Geth客户端，所以我们需要更改Web3j自动配置的客户端地址。

```yaml
spring:
    application:
        name: transaction-service
server:
    port: ${PORT:8090}
web3j:
    client-address: http://192.168.99.100:8545
```

## 6. 构建以太坊Spring Boot应用

如果我们将Web3j启动器包含到项目依赖项中，你只需要自动装配Web3j bean。Web3j负责向Geth客户端节点发送交易。如果它已被节点接受，它会收到带有交易哈希的响应；如果它已被拒绝，则它会收到带有错误对象的响应。创建交易对象时，将gas限制设置为最低21000很重要。如果你发送较低的值，则可能会收到错误Error: intrinsic gas too low。

```java
@Service
public class BlockchainService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainService.class);

    @Autowired
    Web3j web3j;

    public BlockchainTransaction process(BlockchainTransaction trx) throws IOException {
        EthAccounts accounts = web3j.ethAccounts().send();
        EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(accounts.getAccounts().get(trx.getFromId()), DefaultBlockParameterName.LATEST).send();
        Transaction transaction = Transaction.createEtherTransaction(accounts.getAccounts().get(trx.getFromId()), transactionCount.getTransactionCount(), BigInteger.valueOf(trx.getValue()), BigInteger.valueOf(21_000), accounts.getAccounts().get(trx.getToId()),BigInteger.valueOf(trx.getValue()));
        EthSendTransaction response = web3j.ethSendTransaction(transaction).send();
        if (response.getError() != null) {
            trx.setAccepted(false);
            return trx;
        }
        trx.setAccepted(true);
        String txHash = response.getTransactionHash();
        LOGGER.info("Tx hash: {}", txHash);
        trx.setId(txHash);
        EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
        if (receipt.getTransactionReceipt().isPresent()) {
            LOGGER.info("Tx receipt: {}", receipt.getTransactionReceipt().get().getCumulativeGasUsed().intValue());
        }
        return trx;
    }
}
```

上面可见的@Service bean由控制器调用。POST方法的实现以BlockchainTransaction对象为参数。你可以发送发件人ID、收件人ID和交易金额。发送人和接收人ID相当于查询eth.account[index\]中的索引。

```java
@RestController
public class BlockchainController {

    @Autowired
    BlockchainService service;

    @PostMapping("/transaction")
    public BlockchainTransaction execute(@RequestBody BlockchainTransaction transaction) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        return service.process(transaction);
    }
}
```

你可以使用以下命令调用POST方法来发送测试交易。

```shell
$ curl --header "Content-Type: application/json" --request POST --data '{"fromId":2,"toId":1,"value":3}' http://localhost:8090/transaction
```

在发送任何交易之前，你还应该解锁发件人帐户。

<img src="../assets/img_3.png">

应用程序bonus-service监听以太坊节点处理的交易。它通过调用web3j.transactionObservable().subscribe(...)方法订阅来自Web3j库的通知。每从该地址发送10笔交易，它会将收到的交易金额返回到发送人的帐户一次。这是应用程序bonus-service中observable方法的实现。

```java
@Autowired
Web3j web3j;

@PostConstruct
public void listen() {
    Subscription subscription = web3j.transactionObservable().subscribe(tx -> {
        LOGGER.info("New tx: id={}, block={}, from={}, to={}, value={}", tx.getHash(), tx.getBlockHash(), tx.getFrom(), tx.getTo(), tx.getValue().intValue());
        try {
            EthCoinbase coinbase = web3j.ethCoinbase().send();
            EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(tx.getFrom(), DefaultBlockParameterName.LATEST).send();
            LOGGER.info("Tx count: {}", transactionCount.getTransactionCount().intValue());
            if (transactionCount.getTransactionCount().intValue() % 10 == 0) {
                EthGetTransactionCount tc = web3j.ethGetTransactionCount(coinbase.getAddress(), DefaultBlockParameterName.LATEST).send();
                Transaction transaction = Transaction.createEtherTransaction(coinbase.getAddress(), tc.getTransactionCount(), tx.getValue(), BigInteger.valueOf(21_000), tx.getFrom(), tx.getValue());
                web3j.ethSendTransaction(transaction).send();
            }
        } catch (IOException e) {
           LOGGER.error("Error getting transactions", e);
        }
    });
    LOGGER.info("Subscribed");
}
```

## 7. 总结

区块链和加密货币不是容易开始的话题。以太坊通过提供完整的脚本语言简化了使用区块链的应用程序的开发。将Web3j库与以太坊Geth客户端的Spring Boot和Docker镜像一起使用，可以快速开始实现区块链技术的解决方案的本地开发。