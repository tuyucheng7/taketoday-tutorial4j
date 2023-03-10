## 1. 概述

交易机器人是一种计算机程序，可以在不需要人工干预的情况下自动向市场或交易所下订单。

在本教程中，我们将使用[Cassandre](https://github.com/cassandre-tech/cassandre-trading-bot)创建一个简单的加密货币交易机器人，它会在我们认为最佳时机生成头寸。

## 2. 机器人概述

交易意味着“用一件物品交换另一件物品”。

**在金融市场上，它是购买股票、期货、期权、掉期、债券，或者在我们的例子中，购买一定数量的加密货币。这里的想法是以特定价格购买加密货币并以更高的价格出售以获利(即使如果空头头寸价格下跌我们仍然可以获利)**。

我们将使用沙盒交换；沙盒是一个虚拟系统，我们在其中拥有“假”资产，我们可以在其中下订单和接收行情。

首先，让我们看看我们要做什么：

-   将Cassandre Spring Boot Starter添加到我们的项目中
-   添加连接到交易所所需的配置
-   创建策略：
    -   从交易所接收行情
    -   选择何时购买
    -   当需要购买时，检查我们是否有足够的资产并创建头寸
    -   显示日志以查看何时开仓/平仓以及我们获得了多少收益
-   对历史数据进行测试，看看我们是否可以获利

## 3. Maven依赖

让我们开始向我们的pom.xml添加必要的依赖项，首先是[Cassandre Spring Boot Starter](https://search.maven.org/artifact/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter)：

```xml
<dependency>
    <groupId>tech.cassandre.trading.bot</groupId>
    <artifactId>cassandre-trading-bot-spring-boot-starter</artifactId>
    <version>4.2.1</version>
</dependency>
```

Cassandre依靠[XChange](https://github.com/knowm/XChange)连接到加密货币交易所。对于本教程，我们将使用[Kucoin XChange](https://search.maven.org/artifact/org.knowm.xchange/xchange-kucoin)库：

```xml
<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-kucoin</artifactId>
    <version>5.0.8</version>
</dependency>
```

我们还使用[hsqld](https://search.maven.org/artifact/org.hsqldb/hsqldb)来存储数据：

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.5.2</version>
</dependency>
```

为了根据历史数据测试我们的交易机器人，我们还添加了[用于测试的Cassandre Spring Boot Starter](https://search.maven.org/artifact/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter-test)：

```xml
<dependency>
    <groupId>tech.cassandre.trading.bot</groupId>
    <artifactId>cassandre-trading-bot-spring-boot-starter-test</artifactId>
    <version>4.2.1</version>
    <scope>test</scope>
</dependency>
```

## 4. 配置

让我们创建并编辑application.properties来设置我们的配置：

```properties
# Exchange configuration
cassandre.trading.bot.exchange.name=kucoin
cassandre.trading.bot.exchange.username=kucoin.cassandre.test@gmail.com
cassandre.trading.bot.exchange.passphrase=cassandre
cassandre.trading.bot.exchange.key=6054ad25365ac6000689a998
cassandre.trading.bot.exchange.secret=af080d55-afe3-47c9-8ec1-4b479fbcc5e7
# Modes
cassandre.trading.bot.exchange.modes.sandbox=true
cassandre.trading.bot.exchange.modes.dry=false
# Exchange API calls rates (ms or standard ISO 8601 duration like 'PT5S')
cassandre.trading.bot.exchange.rates.account=2000
cassandre.trading.bot.exchange.rates.ticker=2000
cassandre.trading.bot.exchange.rates.trade=2000
# Database configuration
cassandre.trading.bot.database.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
cassandre.trading.bot.database.datasource.url=jdbc:hsqldb:mem:cassandre
cassandre.trading.bot.database.datasource.username=sa
cassandre.trading.bot.database.datasource.password=
```

配置分有四类：

-   **交换配置**：我们为我们设置的交换凭证连接到Kucoin上[现有的沙盒帐户](https://trading-bot.cassandre.tech/ressources/how-tos/how-to-create-a-kucoin-account.html)
-   **模式**：我们要使用的模式。在我们的例子中，我们要求Cassandre使用沙盒数据
-   **交易所API调用率**：指示我们希望以何种速度从交易所检索数据(账户、订单、交易和股票代码)。当心；所有交易所都有我们可以调用它们的最高汇率
-   **数据库配置**：Cassandre使用数据库来存储头寸、订单和交易。对于本教程，我们将使用一个简单的hsqld内存数据库。当然，在生产中，我们应该使用持久化数据库

现在让我们在测试目录的application.properties中创建相同的文件，但我们将cassandre.trading.bot.exchange.modes.dry更改为true，因为在测试期间，我们不想向沙盒发送真实订单。我们只想模拟它们。

## 5. 策略

交易策略是为实现盈利回报而设计的固定计划；我们可以通过添加一个用@CassandreStrategy注解的Java类并扩展BasicCassandreStrategy来创建我们的。

让我们在MyFirstStrategy.java中创建我们的策略类：

```java
@CassandreStrategy
public class MyFirstStrategy extends BasicCassandreStrategy {

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        return Set.of(new CurrencyPairDTO(BTC, USDT));
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        return accounts.stream()
              .filter(a -> "trade".equals(a.getName()))
              .findFirst();
    }
}
```

实现BasicCassandreStrategy迫使我们实现两种方法getRequestedCurrencyPairs()和getTradeAccount()：

在getRequestedCurrencyPairs()中，我们必须返回我们希望从交易所接收的货币对更新列表。货币对是两种不同货币的报价，一种货币对另一种货币的报价。在我们的示例中，我们希望使用BTC/USDT。

为了更清楚，我们可以使用以下curl命令手动检索代码：

```bash
curl -s https://api.kucoin.com/api/v1/market/orderbook/level1?symbol=BTC-USDT
```

我们会得到类似的东西：

```json
{
    "time": 1620227845003,
    "sequence": "1615922903162",
    "price": "57263.3",
    "size": "0.00306338",
    "bestBid": "57259.4",
    "bestBidSize": "0.00250335",
    "bestAsk": "57260.4",
    "bestAskSize": "0.01"
}
```

price值表示1 BTC价值57263.3 USDT。

我们必须实现的另一个方法是getTradeAccount()。在交易所，我们通常有多个账户，Cassandre需要知道其中哪个账户是交易账户。为此，我们必须实现getTradeAccount()方法，该方法将我们拥有的账户列表作为参数提供给我们，我们必须从该列表中返回我们想要用于交易的账户。

在我们的示例中，我们在交易所的交易账户名为“trade”，因此我们只需将其返回即可。

## 6. 创建仓位

要收到新数据的通知，我们可以覆盖BasicCassandreStrategy的以下方法：

-   onAccountUpdate()接收关于账户的更新
-   onTickerUpdate()接收新的股票代码
-   onOrderUpdate()接收关于订单的更新
-   onTradeUpdate()接收有关交易的更新
-   onPositionUpdate()接收有关仓位的更新
-   onPositionStatusUpdate()接收有关仓位状态变化的更新

对于本教程，我们将实现一个愚蠢的算法：**我们检查收到的每个新股票代码。如果1 BTC的价格低于56000 USDT，我们认为是时候买入了**。

为了简化收益计算、订单、交易和平仓，Cassandre提供了一个类来自动管理头寸。

要使用它，第一步是通过PositionRulesDTO类为仓位创建规则，例如：

```java
PositionRulesDTO rules = PositionRulesDTO.builder()
    .stopGainPercentage(4f)
    .stopLossPercentage(25f)
    .create();
```

然后，让我们使用该规则创建仓位：

```java
createLongPosition(new CurrencyPairDTO(BTC, USDT), new BigDecimal("0.01"), rules);
```

此时，Cassandre将创建一个0.01 BTC的买单。持仓状态为OPENING，当对应的交易全部到位后，状态变为OPENED。从现在开始，对于收到的每个代码，Cassandre将自动计算新价格，如果以该价格平仓会触发我们的两个规则之一(4%止损或25%止损)。

如果触发了一个规则，Cassandre将自动创建一个0.01 BTC的卖单。持仓状态会转为CLOSING，当对应的交易全部到位后，状态会转为CLOSED。

这是我们将拥有的代码：

```java
@Override
public void onTickerUpdate(TickerDTO ticker) {
    if (new BigDecimal("56000").compareTo(ticker.getLast()) == -1) {

        if (canBuy(new CurrencyPairDTO(BTC, USDT), new BigDecimal("0.01"))) {
            PositionRulesDTO rules = PositionRulesDTO.builder()
              .stopGainPercentage(4f)
              .stopLossPercentage(25f)
              .build();
            createLongPosition(new CurrencyPairDTO(BTC, USDT), new BigDecimal("0.01"), rules);
        }
    }
}
```

总结一下：

-   对于每个新的股票代码，我们检查价格是否低于56000
-   如果我们的交易账户中有足够的USDT，我们将开仓0.01 BTC
-   从现在开始，对于每个股票代码：
    -   如果新价格计算出的收益超过4%收益或25%损失，Cassandre将通过出售所购买的0.01 BTC来关闭我们创建的头寸

## 7. 跟踪日志中的仓位演变

我们最终将执行onPositionStatusUpdate()以查看何时开仓/平仓：

```java
@Override
public void onPositionStatusUpdate(PositionDTO position) {
    if (position.getStatus() == OPENED) {
        logger.info("> New position opened : {}", position.getPositionId());
    }
    if (position.getStatus() == CLOSED) {
        logger.info("> Position closed : {}", position.getDescription());
    }
}
```

## 8. 回测

简而言之，回测策略是在前期测试交易策略的过程。Cassandre交易机器人让我们能够模拟机器人对历史数据的反应。

第一步是将我们的历史数据(CSV或TSV文件)放入我们的src/test/resources文件夹中。

如果我们在Linux下，这里有一个简单的脚本来生成它们：

```shell
startDate=`date --date="3 months ago" +"%s"`
endDate=`date +"%s"`
curl -s "https://api.kucoin.com/api/v1/market/candles?type=1day&symbol=BTC-USDT&startAt=${startDate}&endAt=${endDate}" \
| jq -r -c ".data[] | @tsv" \
| tac $1 > tickers-btc-usdt.tsv
```

它将创建一个名为tickers-btc-usdt.tsv的文件，其中包含BTC-USDT从startDate(3个月前)到endDate(现在)的历史汇率。

第二步是创建我们的虚拟账户余额来模拟我们想要投资的资产的确切数量。

在这些文件中，我们为每个账户设置了每种加密货币的余额。例如，这是模拟我们的交易账户资产的user-trade.csv的内容：

```bash
BTC 1
USDT 10000
ETH 10
```

该文件还必须位于src/test/resources文件夹中。

现在，我们可以添加一个测试：

```java
@SpringBootTest
@Import(TickerFluxMock.class)
@DisplayName("Simple strategy test")
public class MyFirstStrategyLiveTest {

    @Autowired
    private MyFirstStrategy strategy;

    private final Logger logger = LoggerFactory.getLogger(MyFirstStrategyLiveTest.class);

    @Autowired
    private TickerFluxMock tickerFluxMock;

    @Test
    @DisplayName("Check gains")
    public void whenTickersArrives_thenCheckGains() {
        await().forever().until(() -> tickerFluxMock.isFluxDone());

        HashMap<CurrencyDTO, GainDTO> gains = strategy.getGains();

        logger.info("Cumulated gains:");
        gains.forEach((currency, gain) -> logger.info(currency + " : " + gain.getAmount()));

        logger.info("Position still opened :");
        strategy.getPositions()
              .values()
              .stream()
              .filter(p -> p.getStatus().equals(OPENED))
              .forEach(p -> logger.info(" - {} " + p.getDescription()));

        assertTrue(gains.get(USDT).getPercentage() > 0);
    }
}
```

来自TickerFluxMock的@Import将从我们的src/test/resources文件夹加载历史数据并将它们发送到我们的策略。然后我们使用await()方法来确保从文件加载的所有股票代码都已发送到我们的策略。最后，我们显示已平仓头寸、未平仓头寸和全局收益。

## 9. 总结

本教程说明了如何创建与加密货币交易所交互的策略并根据历史数据对其进行测试。

当然，我们的算法很简单；在现实生活中，目标是找到一种有前途的技术、好的算法和好的数据，才能知道什么时候可以创造头寸。例如，我们可以使用技术分析，因为Cassandre集成了[ta4j](https://github.com/ta4j/ta4j)。