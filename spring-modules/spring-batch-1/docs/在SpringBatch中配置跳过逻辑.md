## 1. 简介

默认情况下，在[Spring Batch](https://spring.io/projects/spring-batch)作业处理期间遇到的任何错误都会导致相应的步骤失败。但是，在许多情况下，对于某些异常，我们宁愿跳过当前处理的项目。

在本教程中，**我们将探讨两种在Spring Batch框架中配置跳过逻辑的方法**。

## 2. 我们的用例

出于示例的目的，**我们将重用我们的[Spring Batch介绍性文章](https://www.baeldung.com/introduction-to-spring-batch)中已经介绍的一个简单的、面向块的作业**。

此作业将一些财务数据从CSV格式转换为XML格式。

### 2.1 输入数据

首先，让我们在原始CSV文件中添加几行：

```csv
username, user_id, transaction_date, transaction_amount
devendra, 1234, 31/10/2015, 10000
john, 2134, 3/12/2015, 12321
robin, 2134, 2/02/2015, 23411
, 2536, 3/10/2019, 100
mike, 9876, 5/11/2018, -500
, 3425, 10/10/2017, 9999
```

正如我们所见，最后三行包含一些无效数据-第5行和第7行缺少用户名字段，第6行的交易金额为负数。

**在后面的部分中，我们将配置批处理作业以跳过这些无效的记录**。

## 3. 配置跳过限制和可跳过的异常

### 3.1 使用skip和skipLimit

现在让我们讨论在失败的情况下将我们的作业配置为跳过项目的两种方法中的第一种-skip和skipLimit方法：

```java
@Bean
public Step skippingStep(ItemProcessor<Transaction, Transaction> processor, ItemWriter<Transaction> writer) throws ParseException {
    return stepBuilderFactory
        .get("skippingStep")
        .<Transaction, Transaction>chunk(10)
        .reader(itemReader(invalidInputCsv))
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .skipLimit(2)
        .skip(MissingUsernameException.class)
        .skip(NegativeAmountException.class)
        .build();
}
```

首先，**要启用跳过功能，我们需要在步骤构建过程中包含对faultTolerant()的调用**。

在skip()和skipLimit()中，我们定义了要跳过的异常以及跳过的最大项目数。

在上面的示例中，如果在读取、处理或写入阶段抛出MissingUsernameException或NegativeAmountException，则当前处理的项目将被忽略并计入总限制2。

因此，**如果第三次抛出任何异常，则整个步骤将失败**。

### 3.2 使用noSkip

在前面的示例中，除了MissingUsernameException和NegativeAmountException之外的任何其他异常都会使我们的步骤失败。

然而，在某些情况下，**识别应该使我们的步骤失败的异常并跳过任何其他异常可能更合适**。

让我们看看如何使用skip、skipLimit和noSkip进行配置：

```java
@Bean
public Step skippingStep(ItemProcessor<Transaction, Transaction> processor, ItemWriter<Transaction> writer) throws ParseException {
    return stepBuilderFactory
        .get("skippingStep")
        .<Transaction, Transaction>chunk(10)
        .reader(itemReader(invalidInputCsv))
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .skipLimit(2)
        .skip(Exception.class)
        .noSkip(SAXException.class)
        .build();
}
```

通过上述配置，我们指示Spring Batch框架跳过除SAXException之外的任何异常(在配置的限制内)。**这意味着SAXException总是会导致步骤失败**。

skip()和noSkip()调用的顺序无关紧要。

## 4. 使用自定义SkipPolicy

有时我们可能需要更复杂的跳过检查机制。为此，**Spring Batch框架提供了[SkipPolicy](https://docs.spring.io/spring-batch/4.1.x/api/org/springframework/batch/core/step/skip/SkipPolicy.html)接口**。

然后我们可以提供我们自己的跳过逻辑实现并将其插入到我们的步骤定义中。

牢记前面的示例，假设我们仍然想定义两个项目的跳过限制，并且只使MissingUsernameException和NegativeAmountException可跳过。

然而，**一个额外的约束是我们可以跳过NegativeAmountException，但前提是金额不超过定义的限制**。

让我们实现我们的自定义SkipPolicy：

```java
public class CustomSkipPolicy implements SkipPolicy {

    private static final int MAX_SKIP_COUNT = 2;
    private static final int INVALID_TX_AMOUNT_LIMIT = -1000;

    @Override
    public boolean shouldSkip(Throwable throwable, int skipCount) throws SkipLimitExceededException {
        if (throwable instanceof MissingUsernameException && skipCount < MAX_SKIP_COUNT) {
            return true;
        }

        if (throwable instanceof NegativeAmountException && skipCount < MAX_SKIP_COUNT ) {
            NegativeAmountException ex = (NegativeAmountException) throwable;
            if(ex.getAmount() < INVALID_TX_AMOUNT_LIMIT) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
```

现在，我们可以在步骤定义中使用我们的自定义策略：

```java
@Bean
public Step skippingStep(ItemProcessor<Transaction, Transaction> processor, ItemWriter<Transaction> writer) throws ParseException {
    return stepBuilderFactory
        .get("skippingStep")
        .<Transaction, Transaction>chunk(10)
        .reader(itemReader(invalidInputCsv))
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .skipPolicy(new CustomSkipPolicy())
        .build();
}
```

而且，与我们之前的示例类似，我们仍然需要使用faultTolerant()来启用跳过功能。

但是，这一次我们不调用skip()或noSkip()。相反，**我们使用skipPolicy()方法来提供我们自己的SkipPolicy接口实现**。

正如我们所看到的，**这种方法为我们提供了更大的灵活性，因此在某些用例中它可能是一个不错的选择**。

## 5. 总结

在本教程中，我们介绍了两种使Spring Batch作业具有容错能力的方法。

尽管将skipLimit()与skip()和noSkip()方法一起使用似乎更受欢迎，但我们可能会发现在某些情况下实现自定义SkipPolicy会更方便。