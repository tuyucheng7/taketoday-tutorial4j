## 1. 概述

默认情况下，Spring Batch作业会因执行期间出现的任何错误而失败。但是，有时我们可能希望提高应用程序的弹性以处理间歇性故障。

在本快速教程中，**我们将探讨如何在Spring Batch框架中配置重试逻辑**。

## 2. 示例用例

假设我们有一个读取输入CSV文件的批处理作业：

```csv
username, userid, transaction_date, transaction_amount
sammy, 1234, 31/10/2015, 10000
john, 9999, 3/12/2015, 12321
```

然后，它通过访问REST端点以获取用户的年龄和邮政编码属性来处理每条记录：

```java
public class RetryItemProcessor implements ItemProcessor<Transaction, Transaction> {

    @Override
    public Transaction process(Transaction transaction) throws IOException {
        log.info("RetryItemProcessor, attempting to process: {}", transaction);
        HttpResponse response = fetchMoreUserDetails(transaction.getUserId());
        // parse user's age and postCode from response and update transaction
        // ...
        return transaction;
    }
    // ...
}
```

最后，它生成一个合并的输出XML：

```xml
<transactionRecord>
    <transactionRecord>
        <amount>10000.0</amount>
        <transactionDate>2015-10-31 00:00:00</transactionDate>
        <userId>1234</userId>
        <username>sammy</username>
        <age>10</age>
        <postCode>430222</postCode>
    </transactionRecord>
    <!--...-->
</transactionRecord>
```

## 3. 向ItemProcessor添加重试

现在，如果由于某些网络缓慢而导致与REST端点的连接超时怎么办？如果是这样，我们的批处理作业就会失败。

在这种情况下，我们希望将失败的项目处理重试几次。因此，**让我们将批处理作业配置为在失败的情况下最多执行三次重试**：

```java
@Bean
public Step retryStep(ItemProcessor<Transaction, Transaction> processor, ItemWriter<Transaction> writer) throws ParseException {
    return stepBuilderFactory
        .get("retryStep")
        .<Transaction, Transaction>chunk(10)
        .reader(itemReader(inputCsv))
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .retryLimit(3)
        .retry(ConnectTimeoutException.class)
        .retry(DeadlockLoserDataAccessException.class)
        .build();
}
```

在这里，我们调用了faultTolerant()以启用重试功能。此外，**我们使用retry和retryLimit分别定义符合重试条件的异常和项目的最大重试次数**。

## 4. 测试重试

假设我们有一个测试场景，其中REST端点返回age和postCode只是暂时关闭。在此测试场景中，我们将仅针对前两次API调用获得ConnectTimeoutException，第三次调用将成功：

```java
@Test
public void whenEndpointFailsTwicePasses3rdTime_thenSuccess() throws Exception {
    FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
    FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

    when(httpResponse.getEntity())
        .thenReturn(new StringEntity("{ \"age\":10, \"postCode\":\"430222\" }"));
 
    //fails for first two calls and passes third time onwards
    when(httpClient.execute(any()))
        .thenThrow(new ConnectTimeoutException("Timeout count 1"))
        .thenThrow(new ConnectTimeoutException("Timeout count 2"))
        .thenReturn(httpResponse);

    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(defaultJobParameters());
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    assertThat(actualJobInstance.getJobName(), is("retryBatchJob"));
    assertThat(actualJobExitStatus.getExitCode(), is("COMPLETED"));
    AssertFile.assertFileEquals(expectedResult, actualResult);
}
```

在这里，我们的作业成功完成了。此外，从日志中可以明显看出，**id=1234的第一条记录失败了两次，最后在第三次重试时成功了**：

```shell
19:06:57.742 [main] INFO  o.s.batch.core.job.SimpleStepHandler - Executing step: [retryStep]
19:06:57.758 [main] INFO  c.t.t.batch.service.RetryItemProcessor - Attempting to process user with id=1234
19:06:57.758 [main] INFO  c.t.t.batch.service.RetryItemProcessor - Attempting to process user with id=1234
19:06:57.758 [main] INFO  c.t.t.batch.service.RetryItemProcessor - Attempting to process user with id=1234
19:06:57.758 [main] INFO  c.t.t.batch.service.RetryItemProcessor - Attempting to process user with id=9999
19:06:57.773 [main] INFO  o.s.batch.core.step.AbstractStep - Step: [retryStep] executed in 31ms
```

同样，**让我们再来一个测试用例，看看当所有重试都用完时会发生什么**：

```java
@Test
public void whenEndpointAlwaysFail_thenJobFails() throws Exception {
    when(httpClient.execute(any()))
        .thenThrow(new ConnectTimeoutException("Endpoint is down"));

    JobExecution jobExecution = jobLauncherTestUtils
        .launchJob(defaultJobParameters());
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    assertThat(actualJobInstance.getJobName(), is("retryBatchJob"));
    assertThat(actualJobExitStatus.getExitCode(), is("FAILED"));
    assertThat(actualJobExitStatus.getExitDescription(),
        containsString("org.apache.http.conn.ConnectTimeoutException"));
}
```

在这种情况下，**在作业最终因ConnectTimeoutException而失败之前，对第一条记录进行了三次重试**。

## 5. 使用XML配置重试

最后，让我们看一下上述配置的XML等价物：

```xml
<batch:job id="retryBatchJob">
    <batch:step id="retryStep">
        <batch:tasklet>
            <batch:chunk reader="itemReader" writer="itemWriter"
                         processor="retryItemProcessor" commit-interval="10"
                         retry-limit="3">
                <batch:retryable-exception-classes>
                    <batch:include class="org.apache.http.conn.ConnectTimeoutException"/>
                    <batch:include class="org.springframework.dao.DeadlockLoserDataAccessException"/>
                </batch:retryable-exception-classes>
            </batch:chunk>
        </batch:tasklet>
    </batch:step>
</batch:job>
```

## 6. 总结

在本文中，我们学习了如何在Spring Batch中配置重试逻辑。我们查看了Java和XML配置。

我们还使用单元测试来查看重试在实践中的效果。