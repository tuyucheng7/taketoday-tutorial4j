## 1. 简介

[Apache Commons Chain](https://commons.apache.org/proper/commons-chain/)是一个使用责任链[模式](https://en.wikipedia.org/wiki/Chain-of-responsibility_pattern)的库——通常用于组织复杂的处理流程，其中多个接收者可以处理一个请求。

在这篇简短的文章中，我们将通过一个表示从 ATM 取款的示例。

## 2.Maven依赖

首先，我们将使用 Maven 导入该库的最新版本：

```xml
<dependency>
    <groupId>commons-chain</groupId>
    <artifactId>commons-chain</artifactId>
    <version>1.2</version>
</dependency>

```

要检查这个库的最新版本——[去这里](https://search.maven.org/classic/#search|gav|1|g%3A"commons-chain" AND a%3A"commons-chain")。

## 3. 示例链

ATM 将数字作为输入并将其传递给负责执行不同操作的处理程序。这些涉及计算要分配的纸币数量，并向银行和客户发送有关交易的通知。

## 4.链上下文

上下文表示应用程序的当前状态，存储有关事务的信息。

对于我们的 ATM 取款请求，我们需要的信息是：

-   提款总额
-   100 面额纸币的数量
-   50面额纸币张数
-   10面额纸币的数量
-   剩余可提取金额

这个状态定义在一个类中：

```java
public class AtmRequestContext extends ContextBase {
    int totalAmountToBeWithdrawn;
    int noOfHundredsDispensed;
    int noOfFiftiesDispensed;
    int noOfTensDispensed;
    int amountLeftToBeWithdrawn;

    // standard setters & getters
}
```

## 5.命令

命令将C上下文作为输入并对其进行处理。

我们将把上面提到的每个步骤作为一个命令来实现：

```java
public class HundredDenominationDispenser implements Command {

    @Override
    public boolean execute(Context context) throws Exception {
        intamountLeftToBeWithdrawn = (int) context.get("amountLeftToBeWithdrawn);
        if (amountLeftToBeWithdrawn >= 100) {
            context.put("noOfHundredsDispensed", amountLeftToBeWithdrawn / 100);
            context.put("amountLeftToBeWithdrawn", amountLeftToBeWithdrawn % 100);
        }
        return false;
    }
}

```

FiftyDenominationDispenser和TenDenominationDispenser的Command是相似的。

## 6.链条

链是要按指定顺序执行的命令的集合。我们的Chain将由上面的Command和末尾的AuditFilter 组成：

```java
public class AtmWithdrawalChain extends ChainBase {

    public AtmWithdrawalChain() {
        super();
        addCommand(new HundredDenominationDispenser());
        addCommand(new FiftyDenominationDispenser());
        addCommand(new TenDenominationDispenser());
        addCommand(new AuditFilter());
    }
}
```

当Chain中的任何命令返回 true 时，它会强制Chain结束。

## 7.过滤

过滤器也是一个命令，但具有在链执行后调用的postProcess方法。

我们的过滤器将向客户和银行发送通知：

```java
public class AuditFilter implements Filter {

    @Override
    public boolean postprocess(Context context, Exception exception) {
        // send notification to bank and user
        return false;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        return false;
    }
}
```

## 8.链目录

它是具有逻辑名称的链和命令的集合。

在我们的例子中，我们的目录将包含AtmWithdrawalChain。

```java
public class AtmCatalog extends CatalogBase {

    public AtmCatalog() {
        super();
        addCommand("atmWithdrawalChain", new AtmWithdrawalChain());
    }
}
```

## 9. 使用链条

让我们看看如何使用上面的Chain来处理取款请求。我们将首先创建一个Context，然后将Chain 传递给它。Chain将处理Context。

我们将编写一个测试用例来演示我们的AtmWithdrawalChain：

```java
public class AtmChainTest {

    @Test
    public void givenInputsToContext_whenAppliedChain_thenExpectedContext() throws Exception {
        Context context = new AtmRequestContext();
        context.put("totalAmountToBeWithdrawn", 460);
        context.put("amountLeftToBeWithdrawn", 460);
        
        Catalog catalog = new AtmCatalog();
        Command atmWithdrawalChain = catalog.getCommand("atmWithdrawalChain");
        
        atmWithdrawalChain.execute(context);
        
        assertEquals(460, (int) context.get("totalAmountToBeWithdrawn"));
        assertEquals(0, (int) context.get("amountLeftToBeWithdrawn"));
        assertEquals(4, (int) context.get("noOfHundredsDispensed"));
        assertEquals(1, (int) context.get("noOfFiftiesDispensed"));
        assertEquals(1, (int) context.get("noOfTensDispensed"));
    }
}
```

## 10.总结

在本教程中，我们探索了一个使用 Apache 的 Apache Commons Chain 库的实际场景——可以在[此处](https://commons.apache.org/proper/commons-chain/cookbook.html)阅读更多相关信息。