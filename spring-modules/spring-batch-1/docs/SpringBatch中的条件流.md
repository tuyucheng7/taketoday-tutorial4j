## 1. 简介

我们使用[Spring Batch](https://www.baeldung.com/introduction-to-spring-batch)从读取、转换和写入数据的多个步骤中组合作业。如果作业中的步骤有多个路径，类似于在我们的代码中使用if语句，我们说作业流是有条件的。

在本教程中，我们将研究两种使用条件流创建Spring Batch作业的方法。

## 2. 退出状态和批处理状态

当我们使用Spring Batch框架指定条件步骤时，我们使用的是步骤或作业的退出状态。因此，我们需要在我们的步骤和作业中了解批处理状态和退出状态之间的区别：

-   **BatchStatus是表示步骤/作业状态的枚举，由Batch框架在内部使用**
-   可能的值有：ABANDONED、COMPLETED、FAILED、STARTED、STARTING、STOPPED、STOPPING、UNKNOWN
-   **ExitStatus是一个步骤执行完成时的状态，用于条件判断流程**

默认情况下，步骤或作业的ExitStatus与其BatchStatus相同。我们还可以设置自定义ExitStatus来驱动流程。

## 3. 条件流

假设我们有一个物联网设备可以向我们发送测量值。我们的设备测量值是整数数组，如果我们的任何测量值包含正整数，我们需要发送通知。

换句话说，我们需要在检测到阳性测量值时发送通知。

### 3.1 ExitStatus

重要的是，**我们使用步骤的退出状态来驱动条件流**。

**要设置步骤的退出状态，我们需要使用StepExecution对象的setExitStatus方法**。为此，我们需要创建一个扩展ItemListenerSupport并获取步骤的StepExecution的ItemProcessor。

当我们找到一个正数时，我们使用它来将我们步骤的退出状态设置为NOTIFY。**当我们根据批处理作业中的数据确定退出状态时，我们可以使用ItemProcessor**。

让我们看看我们的NumberInfoClassifier以了解我们需要的三个方法：

```java
public class NumberInfoClassifier extends ItemListenerSupport<NumberInfo, Integer>
      implements ItemProcessor<NumberInfo, Integer> {

    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.stepExecution.setExitStatus(new ExitStatus(QUIET));
    }

    @Override
    public Integer process(NumberInfo numberInfo) throws Exception {
        return Integer.valueOf(numberInfo.getNumber());
    }

    @Override
    public void afterProcess(NumberInfo item, Integer result) {
        super.afterProcess(item, result);
        if (item.isPositive()) {
            stepExecution.setExitStatus(new ExitStatus(NOTIFY));
        }
    }
}
```

注意：在此示例中，我们使用ItemProcessor来设置ExitStatus，但我们也可以在步骤的ItemReader或ItemWriter中轻松完成。

最后，当我们创建作业时，我们告诉JobBuilderFactory为状态为NOTIFY的任何退出步骤发送通知：

```java
jobBuilderFactory.get("Number generator - second dataset")
    .start(dataProviderStep)
    .on("NOTIFY").to(notificationStep)
    .end()
    .build();
```

另请注意，当我们有额外的条件分支和多个退出代码时，我们可以使用JobBuilderFactory的from和on方法将它们添加到我们的作业中：

```java
jobBuilderFactory.get("Number generator - second dataset")
    .start(dataProviderStep)
    .on("NOTIFY").to(notificationStep)
    .from(step).on("LOG_ERROR").to(errorLoggingStep)
    .end()
    .build();
```

现在，每当我们的ItemProcessor看到一个正数，它就会指示我们的作业运行notificationStep，它只是向System.out打印一条消息：

```shell
Second Dataset Processor 11
Second Dataset Processor -2
Second Dataset Processor -3
[Number generator - second dataset] contains interesting data!!
```

如果我们有一个没有正数的数据集，我们将看不到我们的notificationStep消息：

```shell
Second Dataset Processor -1
Second Dataset Processor -2
Second Dataset Processor -3
```

### 3.2 使用JobExecutionDecider的程序化分支

或者，我们可以使用实现JobExecutionDecider的类来确定作业流。**如果我们有确定执行流程的外部因素，这将特别有用**。

要使用此方法，我们首先需要修改ItemProcessor以删除ItemListenerSupport接口和@BeforeStep方法：

```java
public class NumberInfoClassifierWithDecider extends ItemListenerSupport<NumberInfo, Integer>
      implements ItemProcessor<NumberInfo, Integer> {

    @Override
    public Integer process(NumberInfo numberInfo) throws Exception {
        return Integer.valueOf(numberInfo.getNumber());
    }
}
```

接下来，我们创建一个决策类来确定我们步骤的通知状态：

```java
public class NumberInfoDecider implements JobExecutionDecider {

    private boolean shouldNotify() {
        return true;
    }

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        if (shouldNotify()) {
            return new FlowExecutionStatus(NOTIFY);
        } else {
            return new FlowExecutionStatus(QUIET);
        }
    }
}
```

然后，我们将Job设置为在流程中使用决策程序：

```java
jobBuilderFactory.get("Number generator - third dataset")
    .start(dataProviderStep)
    .next(new NumberInfoDecider()).on("NOTIFY").to(notificationStep)
    .end()
    .build();
```

## 4. 总结

在本快速教程中，我们探讨了使用Spring Batch实现条件流的两个选项。首先，我们研究了如何使用ExitStatus来控制我们的作业流程。

然后我们了解了如何通过定义我们自己的JobExecutionDecider以编程方式控制流程。