## 1. 概述

在我们之前对[Spring Batch的介绍](https://www.baeldung.com/introduction-to-spring-batch)中，我们介绍了该框架作为批处理工具。我们还探讨了单线程、单进程作业执行的配置细节和实现。

为了实现具有某些并行处理的作业，提供了一系列选项。在更高层次上，有两种并行处理模式：

1.  单进程，多线程
2.  多进程

在这篇快速文章中，我们将讨论Step的分区，它可以为单进程和多进程作业实现。

## 2. 对步骤进行分区

带分区的Spring Batch为我们提供了划分[Step](https://docs.spring.io/spring-batch/trunk/reference/html/scalability.html)执行的工具：

<img src="../assets/img.png">

上图显示了带有分区Step的Job的实现。

有一个称为“Master”的步骤，其执行分为一些“Slave”步骤。这些Slave可以代替Master，结果依然不变。Master和Slave都是Step的实例。Slave可以是远程服务也可以只是本地执行的线程。

如果需要，我们可以将数据从Master传递到Slave。元数据(即JobRepository)确保每个Slave在作业的单次执行中只执行一次。

以下是显示其工作原理的序列图：

<img src="../assets/img_1.png">

如图所示，PartitionStep正在驱动执行。PartitionHandler负责将“Master”的工作拆分到“Slaves”中。最右边的Step是Slave。

## 3. Maven POM

Maven依赖与我们[上一篇文章](https://www.baeldung.com/introduction-to-spring-batch)中提到的相同。也就是说，Spring Core、Spring Batch和数据库的依赖项(在我们的例子中是SQLite)。

## 4. 配置

在我们的[介绍性文章](https://www.baeldung.com/introduction-to-spring-batch)中，我们看到了一个将一些财务数据从CSV文件转换为XML文件的示例。让我们扩展相同的示例。

在这里，我们将使用多线程实现将财务信息从5个CSV文件转换为相应的XML文件。

我们可以使用单个Job和Step分区来实现这一点。我们将有五个线程，每个CSV文件一个线程。

首先，让我们创建一个作业：

```java
@Bean(name = "partitionerJob")
public Job partitionerJob() throws UnexpectedInputException, MalformedURLException, ParseException {
    return jobs.get("partitioningJob")
        .start(partitionStep())
        .build();
}
```

正如我们所看到的，这个作业从PartitioningStep开始。这是我们的主(Master)步骤，将分为多个从(Slave)步骤：

```java
@Bean
public Step partitionStep() throws UnexpectedInputException, MalformedURLException, ParseException {
    return steps.get("partitionStep")
        .partitioner("slaveStep", partitioner())
        .step(slaveStep())
        .taskExecutor(taskExecutor())
        .build();
}
```

在这里，我们将使用StepBuilderFactory创建PartitioningStep。为此，我们需要提供有关SlaveSteps和Partitioner的信息。

Partitioner是一个接口，它提供了为每个Slave定义一组输入值的工具。换句话说，将任务划分到各个线程的逻辑就在这里。

让我们创建一个它的实现，称为CustomMultiResourcePartitioner，我们将在其中将输入和输出文件名放在ExecutionContext中以传递给每个Slave步骤：

```java
public class CustomMultiResourcePartitioner implements Partitioner {

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        int i = 0, k = 1;
        for (Resource resource : resources) {
            ExecutionContext context = new ExecutionContext();
            Assert.state(resource.exists(), "Resource does not exist: " + resource);
            context.putString(keyName, resource.getFilename());
            context.putString("opFileName", "output"+k+++".xml");
            map.put(PARTITION_KEY + i, context);
            i++;
        }
        return map;
    }
}
```

我们还将为此类创建bean，我们将在其中提供输入文件的源目录：

```java
@Bean
public CustomMultiResourcePartitioner partitioner() {
    CustomMultiResourcePartitioner partitioner = new CustomMultiResourcePartitioner();
    Resource[] resources;
    try {
        resources = resoursePatternResolver
            .getResources("file:src/main/resources/input/*.csv");
    } catch (IOException e) {
        throw new RuntimeException("I/O problems when resolving" + " the input file pattern.", e);
    }
    partitioner.setResources(resources);
    return partitioner;
}
```

我们将定义Slave步骤，就像Reader和Writer的任何其他步骤一样。Reader和Writer将与我们在介绍性示例中看到的相同，只是它们将从StepExecutionContext接收文件名参数。

请注意，这些bean需要在步骤作用域内，以便它们能够在每个步骤中接收stepExecutionContext参数。如果它们不在步骤范围内，它们的bean将在最初创建，并且不会在步骤级别接收文件名：

```java
@StepScope
@Bean
public FlatFileItemReader<Transaction> itemReader(@Value("#{stepExecutionContext[fileName]}") String filename) throws UnexpectedInputException, ParseException {
    FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    String[] tokens = {"username", "userid", "transactiondate", "amount"};
    tokenizer.setNames(tokens);
    reader.setResource(new ClassPathResource("input/" + filename));
    DefaultLineMapper<Transaction> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(new RecordFieldSetMapper());
    reader.setLinesToSkip(1);
    reader.setLineMapper(lineMapper);
    return reader;
}
```

```java
@Bean
@StepScope
public ItemWriter<Transaction> itemWriter(Marshaller marshaller, @Value("#{stepExecutionContext[opFileName]}") String filename) throws MalformedURLException {
    StaxEventItemWriter<Transaction> itemWriter = new StaxEventItemWriter<Transaction>();
    itemWriter.setMarshaller(marshaller);
    itemWriter.setRootTagName("transactionRecord");
    itemWriter.setResource(new ClassPathResource("xml/" + filename));
    return itemWriter;
}
```

在Slave步骤中提及Reader和Writer时，我们可以将参数作为null传递，因为不会使用这些文件名，因为它们将从stepExecutionContext接收文件名：

```java
@Bean
public Step slaveStep() throws UnexpectedInputException, MalformedURLException, ParseException {
    return steps.get("slaveStep").<Transaction, Transaction>chunk(1)
        .reader(itemReader(null))
        .writer(itemWriter(marshaller(), null))
        .build();
}
```

## 5. 总结

在本教程中，我们讨论了如何使用Spring Batch实现具有并行处理的作业。