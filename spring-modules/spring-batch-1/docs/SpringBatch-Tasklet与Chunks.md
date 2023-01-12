## 1. 简介

[Spring Batch](https://projects.spring.io/spring-batch/)提供了两种不同的方式来实现作业：使用 tasklets 和 chunks。

在本文中，我们将学习如何使用一个简单的真实示例来配置和实施这两种方法。

## 2.依赖关系

让我们开始添加所需的依赖项：

```xml
<dependency>
    <groupId>org.springframework.batch</groupId>
    <artifactId>spring-batch-core</artifactId>
    <version>4.3.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.batch</groupId>
    <artifactId>spring-batch-test</artifactId>
    <version>4.3.0</version>
    <scope>test</scope>
</dependency>
```

要获取最新版本的[spring-batch-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.batch" AND a%3A"spring-batch-core")和[spring-batch-test](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.batch" AND a%3A"spring-batch-test")，请参考 Maven Central。

## 3. 我们的用例

让我们考虑一个包含以下内容的 CSV 文件：

```plaintext
Mae Hodges,10/22/1972
Gary Potter,02/22/1953
Betty Wise,02/17/1968
Wayne Rose,04/06/1977
Adam Caldwell,09/27/1995
Lucille Phillips,05/14/1992
```

每行的第一个位置代表一个人的名字，第二个位置代表他/她的出生日期。

我们的用例是生成另一个包含每个人的姓名和年龄的 CSV 文件：

```plaintext
Mae Hodges,45
Gary Potter,64
Betty Wise,49
Wayne Rose,40
Adam Caldwell,22
Lucille Phillips,25
```

现在我们的领域已经很清楚了，让我们继续使用这两种方法构建解决方案。我们将从 tasklet 开始。

## 4. Tasklet 方法

### 4.1. 介绍与设计

Tasklet 旨在在一个步骤中执行单个任务。我们的工作将由几个步骤组成，这些步骤一个接一个地执行。每个步骤应该只执行一个定义的任务。

我们的工作将包括三个步骤：

1.  从输入 CSV 文件中读取行。
2.  计算输入 CSV 文件中每个人的年龄。
3.  将每个人的姓名和年龄写入新的输出 CSV 文件。

现在大图已经准备就绪，让我们每一步创建一个类。

LinesReader将负责从输入文件中读取数据：

```java
public class LinesReader implements Tasklet {
    // ...
}
```

LinesProcessor将计算文件中每个人的年龄：

```java
public class LinesProcessor implements Tasklet {
    // ...
}
```

最后，LinesWriter将负责将姓名和年龄写入输出文件：

```java
public class LinesWriter implements Tasklet {
    // ...
}
```

至此，我们所有的步骤都实现了Tasklet接口。这将迫使我们实现它的执行方法：

```java
@Override
public RepeatStatus execute(StepContribution stepContribution, 
  ChunkContext chunkContext) throws Exception {
    // ...
}
```

我们将在这个方法中为每个步骤添加逻辑。在开始使用该代码之前，让我们配置我们的工作。

### 4.2. 配置

我们需要在Spring 的应用程序上下文中添加一些配置。为上一节中创建的类添加标准 bean 声明后，我们就可以创建作业定义了：

```java
@Configuration
@EnableBatchProcessing
public class TaskletsConfig {

    @Autowired 
    private JobBuilderFactory jobs;

    @Autowired 
    private StepBuilderFactory steps;

    @Bean
    protected Step readLines() {
        return steps
          .get("readLines")
          .tasklet(linesReader())
          .build();
    }

    @Bean
    protected Step processLines() {
        return steps
          .get("processLines")
          .tasklet(linesProcessor())
          .build();
    }

    @Bean
    protected Step writeLines() {
        return steps
          .get("writeLines")
          .tasklet(linesWriter())
          .build();
    }

    @Bean
    public Job job() {
        return jobs
          .get("taskletsJob")
          .start(readLines())
          .next(processLines())
          .next(writeLines())
          .build();
    }

    // ...

}
```

这意味着我们的“taskletsJob”将包含三个步骤。第一个 ( readLines ) 将执行 bean linesReader中定义的 tasklet并移动到下一步：processLines。ProcessLines将执行 bean linesProcessor中定义的 tasklet并转到最后一步：writeLines。

我们的工作流程已定义，我们准备添加一些逻辑！

### 4.3. 模型和实用程序

由于我们将在 CSV 文件中操作线条，因此我们将创建一个Line 类：

```java
public class Line implements Serializable {

    private String name;
    private LocalDate dob;
    private Long age;

    // standard constructor, getters, setters and toString implementation

}
```

请注意，Line实现了Serializable。这是因为Line将充当 DTO 在步骤之间传输数据。根据 Spring Batch，在步骤之间传输的对象必须是可序列化的。

另一方面，我们可以开始考虑读写行。

为此，我们将使用 OpenCSV：

```xml
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>4.1</version>
</dependency>
```

在 Maven Central 中查找最新的[OpenCSV版本。](https://search.maven.org/classic/#search|gav|1|g%3A"com.opencsv" AND a%3A"opencsv")

包含 OpenCSV 后，我们还将创建一个FileUtils类。它将提供读取和写入 CSV 行的方法：

```java
public class FileUtils {

    public Line readLine() throws Exception {
        if (CSVReader == null) 
          initReader();
        String[] line = CSVReader.readNext();
        if (line == null) 
          return null;
        return new Line(
          line[0], 
          LocalDate.parse(
            line[1], 
            DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    }

    public void writeLine(Line line) throws Exception {
        if (CSVWriter == null) 
          initWriter();
        String[] lineStr = new String[2];
        lineStr[0] = line.getName();
        lineStr[1] = line
          .getAge()
          .toString();
        CSVWriter.writeNext(lineStr);
    }

    // ...
}
```

请注意，readLine充当 OpenCSV 的readNext方法的包装器并返回一个Line对象。

同样，writeLine包装 OpenCSV 的writeNext接收一个Line对象。可以在[GitHub 项目](https://github.com/eugenp/tutorials/blob/master/spring-batch/src/main/java/com/baeldung/taskletsvschunks/utils/FileUtils.java)中找到此类的完整实现。

在这一点上，我们都准备好开始每个步骤的实施。

### 4.4. 线阅读器

让我们继续完成我们的LinesReader类：

```java
public class LinesReader implements Tasklet, StepExecutionListener {

    private final Logger logger = LoggerFactory
      .getLogger(LinesReader.class);

    private List<Line> lines;
    private FileUtils fu;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        lines = new ArrayList<>();
        fu = new FileUtils(
          "taskletsvschunks/input/tasklets-vs-chunks.csv");
        logger.debug("Lines Reader initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, 
      ChunkContext chunkContext) throws Exception {
        Line line = fu.readLine();
        while (line != null) {
            lines.add(line);
            logger.debug("Read line: " + line.toString());
            line = fu.readLine();
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        fu.closeReader();
        stepExecution
          .getJobExecution()
          .getExecutionContext()
          .put("lines", this.lines);
        logger.debug("Lines Reader ended.");
        return ExitStatus.COMPLETED;
    }
}
```

LinesReader 的 execute方法在输入文件路径上创建一个 FileUtils 实例。然后，向列表中添加行，直到没有更多行可读。

我们的类还实现了StepExecutionListener，它提供了两个额外的方法：beforeStep和afterStep。我们将使用这些方法在执行运行之前和之后初始化和关闭事物。

如果我们看一下afterStep代码，我们会注意到将结果列表 (行 )放入作业上下文中以使其可用于下一步的行：

```java
stepExecution
  .getJobExecution()
  .getExecutionContext()
  .put("lines", this.lines);
```

此时，我们的第一步已经完成了它的职责：将 CSV 行加载到内存中的List中。让我们进入第二步并处理它们。

### 4.5. 线处理器

LinesProcessor还将实现StepExecutionListener，当然还有Tasklet。这意味着它还将实现beforeStep、 execute和afterStep方法：

```java
public class LinesProcessor implements Tasklet, StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(
      LinesProcessor.class);

    private List<Line> lines;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
          .getJobExecution()
          .getExecutionContext();
        this.lines = (List<Line>) executionContext.get("lines");
        logger.debug("Lines Processor initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, 
      ChunkContext chunkContext) throws Exception {
        for (Line line : lines) {
            long age = ChronoUnit.YEARS.between(
              line.getDob(), 
              LocalDate.now());
            logger.debug("Calculated age " + age + " for line " + line.toString());
            line.setAge(age);
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Lines Processor ended.");
        return ExitStatus.COMPLETED;
    }
}
```

很容易理解它从工作上下文中加载行列表并计算每个人的年龄。

无需在上下文中放置另一个结果列表，因为修改发生在来自上一步的同一对象上。

我们已经为最后一步做好了准备。

### 4.6. 台词作家

LinesWriter的任务是遍历行列表并将姓名和年龄写入输出文件：

```java
public class LinesWriter implements Tasklet, StepExecutionListener {

    private final Logger logger = LoggerFactory
      .getLogger(LinesWriter.class);

    private List<Line> lines;
    private FileUtils fu;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
          .getJobExecution()
          .getExecutionContext();
        this.lines = (List<Line>) executionContext.get("lines");
        fu = new FileUtils("output.csv");
        logger.debug("Lines Writer initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, 
      ChunkContext chunkContext) throws Exception {
        for (Line line : lines) {
            fu.writeLine(line);
            logger.debug("Wrote line " + line.toString());
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        fu.closeWriter();
        logger.debug("Lines Writer ended.");
        return ExitStatus.COMPLETED;
    }
}
```

我们完成了工作的实施！让我们创建一个测试来运行它并查看结果。

### 4.7. 运行作业

要运行该作业，我们将创建一个测试：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TaskletsConfig.class)
public class TaskletsTest {

    @Autowired 
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenTaskletsJob_whenJobEnds_thenStatusCompleted()
      throws Exception {
 
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
```

ContextConfiguration注解指向具有我们的作业定义的 Spring 上下文配置类。

在运行测试之前，我们需要添加几个额外的 bean：

```java
@Bean
public JobLauncherTestUtils jobLauncherTestUtils() {
    return new JobLauncherTestUtils();
}

@Bean
public JobRepository jobRepository() throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(dataSource());
    factory.setTransactionManager(transactionManager());
    return factory.getObject();
}

@Bean
public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.sqlite.JDBC");
    dataSource.setUrl("jdbc:sqlite:repository.sqlite");
    return dataSource;
}

@Bean
public PlatformTransactionManager transactionManager() {
    return new ResourcelessTransactionManager();
}

@Bean
public JobLauncher jobLauncher() throws Exception {
    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(jobRepository());
    return jobLauncher;
}
```

一切都准备好了！继续并运行测试！

作业完成后，output.csv具有预期的内容，日志显示执行流程：

```plaintext
[main] DEBUG o.b.t.tasklets.LinesReader - Lines Reader initialized.
[main] DEBUG o.b.t.tasklets.LinesReader - Read line: [Mae Hodges,10/22/1972]
[main] DEBUG o.b.t.tasklets.LinesReader - Read line: [Gary Potter,02/22/1953]
[main] DEBUG o.b.t.tasklets.LinesReader - Read line: [Betty Wise,02/17/1968]
[main] DEBUG o.b.t.tasklets.LinesReader - Read line: [Wayne Rose,04/06/1977]
[main] DEBUG o.b.t.tasklets.LinesReader - Read line: [Adam Caldwell,09/27/1995]
[main] DEBUG o.b.t.tasklets.LinesReader - Read line: [Lucille Phillips,05/14/1992]
[main] DEBUG o.b.t.tasklets.LinesReader - Lines Reader ended.
[main] DEBUG o.b.t.tasklets.LinesProcessor - Lines Processor initialized.
[main] DEBUG o.b.t.tasklets.LinesProcessor - Calculated age 45 for line [Mae Hodges,10/22/1972]
[main] DEBUG o.b.t.tasklets.LinesProcessor - Calculated age 64 for line [Gary Potter,02/22/1953]
[main] DEBUG o.b.t.tasklets.LinesProcessor - Calculated age 49 for line [Betty Wise,02/17/1968]
[main] DEBUG o.b.t.tasklets.LinesProcessor - Calculated age 40 for line [Wayne Rose,04/06/1977]
[main] DEBUG o.b.t.tasklets.LinesProcessor - Calculated age 22 for line [Adam Caldwell,09/27/1995]
[main] DEBUG o.b.t.tasklets.LinesProcessor - Calculated age 25 for line [Lucille Phillips,05/14/1992]
[main] DEBUG o.b.t.tasklets.LinesProcessor - Lines Processor ended.
[main] DEBUG o.b.t.tasklets.LinesWriter - Lines Writer initialized.
[main] DEBUG o.b.t.tasklets.LinesWriter - Wrote line [Mae Hodges,10/22/1972,45]
[main] DEBUG o.b.t.tasklets.LinesWriter - Wrote line [Gary Potter,02/22/1953,64]
[main] DEBUG o.b.t.tasklets.LinesWriter - Wrote line [Betty Wise,02/17/1968,49]
[main] DEBUG o.b.t.tasklets.LinesWriter - Wrote line [Wayne Rose,04/06/1977,40]
[main] DEBUG o.b.t.tasklets.LinesWriter - Wrote line [Adam Caldwell,09/27/1995,22]
[main] DEBUG o.b.t.tasklets.LinesWriter - Wrote line [Lucille Phillips,05/14/1992,25]
[main] DEBUG o.b.t.tasklets.LinesWriter - Lines Writer ended.
```

这就是 Tasklet 的全部内容。现在我们可以继续使用 Chunks 方法。

## 5 . 块方法

### 5.1. 介绍与设计

顾名思义，这种方法对数据块执行操作。也就是说，它不会一次读取、处理和写入所有行，而是一次读取、处理和写入固定数量的记录(块)。

然后，它会重复这个循环，直到文件中没有更多数据。

因此，流程会略有不同：

1.  虽然有线：
    -   为 X 行数做：
        -   读一行
        -   处理一行
    -   写 X 行。

因此，我们还需要为面向块的方法创建三个 bean：

```java
public class LineReader {
     // ...
}
public class LineProcessor {
    // ...
}
public class LinesWriter {
    // ...
}
```

在开始实施之前，让我们配置我们的工作。

### 5.2. 配置

作业定义也会有所不同：

```java
@Configuration
@EnableBatchProcessing
public class ChunksConfig {

    @Autowired 
    private JobBuilderFactory jobs;

    @Autowired 
    private StepBuilderFactory steps;

    @Bean
    public ItemReader<Line> itemReader() {
        return new LineReader();
    }

    @Bean
    public ItemProcessor<Line, Line> itemProcessor() {
        return new LineProcessor();
    }

    @Bean
    public ItemWriter<Line> itemWriter() {
        return new LinesWriter();
    }

    @Bean
    protected Step processLines(ItemReader<Line> reader,
      ItemProcessor<Line, Line> processor, ItemWriter<Line> writer) {
        return steps.get("processLines").<Line, Line> chunk(2)
          .reader(reader)
          .processor(processor)
          .writer(writer)
          .build();
    }

    @Bean
    public Job job() {
        return jobs
          .get("chunksJob")
          .start(processLines(itemReader(), itemProcessor(), itemWriter()))
          .build();
    }

}
```

在这种情况下，只有一个步骤只执行一个 tasklet。

但是，该 tasklet定义了一个读取器、一个写入器和一个将对数据块进行操作的处理器。

请注意，提交间隔表示一个块中要处理的数据量。我们的工作将一次读取、处理和写入两行。

现在我们准备好添加块逻辑了！

### 5.3. 线阅读器

LineReader将负责读取一条记录并返回一个包含其内容的Line实例。

要成为阅读器，我们的类必须实现ItemReader接口：

```java
public class LineReader implements ItemReader<Line> {
     @Override
     public Line read() throws Exception {
         Line line = fu.readLine();
         if (line != null) 
           logger.debug("Read line: " + line.toString());
         return line;
     }
}
```

代码很简单，它只读取一行并返回它。我们还将为此类的最终版本实现StepExecutionListener ：

```java
public class LineReader implements 
  ItemReader<Line>, StepExecutionListener {

    private final Logger logger = LoggerFactory
      .getLogger(LineReader.class);
 
    private FileUtils fu;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        fu = new FileUtils("taskletsvschunks/input/tasklets-vs-chunks.csv");
        logger.debug("Line Reader initialized.");
    }

    @Override
    public Line read() throws Exception {
        Line line = fu.readLine();
        if (line != null) logger.debug("Read line: " + line.toString());
        return line;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        fu.closeReader();
        logger.debug("Line Reader ended.");
        return ExitStatus.COMPLETED;
    }
}
```

需要注意的是beforeStep和afterStep分别在整个步骤之前和之后执行。

### 5.4. 线路处理器

LineProcessor遵循与LineReader几乎相同的逻辑。

但是，在这种情况下，我们将实现ItemProcessor及其方法process()：

```java
public class LineProcessor implements ItemProcessor<Line, Line> {

    private Logger logger = LoggerFactory.getLogger(LineProcessor.class);

    @Override
    public Line process(Line line) throws Exception {
        long age = ChronoUnit.YEARS
          .between(line.getDob(), LocalDate.now());
        logger.debug("Calculated age " + age + " for line " + line.toString());
        line.setAge(age);
        return line;
    }

}
```

process()方法接受输入行，对其进行处理并返回输出行。同样，我们还将实现StepExecutionListener：

```java
public class LineProcessor implements 
  ItemProcessor<Line, Line>, StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(LineProcessor.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Line Processor initialized.");
    }
    
    @Override
    public Line process(Line line) throws Exception {
        long age = ChronoUnit.YEARS
          .between(line.getDob(), LocalDate.now());
        logger.debug(
          "Calculated age " + age + " for line " + line.toString());
        line.setAge(age);
        return line;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Line Processor ended.");
        return ExitStatus.COMPLETED;
    }
}
```

### 5.5. 台词作家

与读取器和处理器不同，LinesWriter将写入一整行，以便它接收一个行列表：

```java
public class LinesWriter implements 
  ItemWriter<Line>, StepExecutionListener {

    private final Logger logger = LoggerFactory
      .getLogger(LinesWriter.class);
 
    private FileUtils fu;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        fu = new FileUtils("output.csv");
        logger.debug("Line Writer initialized.");
    }

    @Override
    public void write(List<? extends Line> lines) throws Exception {
        for (Line line : lines) {
            fu.writeLine(line);
            logger.debug("Wrote line " + line.toString());
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        fu.closeWriter();
        logger.debug("Line Writer ended.");
        return ExitStatus.COMPLETED;
    }
}
```

LinesWriter代码不言自明。再一次，我们准备好测试我们的工作。

### 5.6. 运行作业

我们将创建一个新测试，与我们为 tasklet 方法创建的测试相同：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ChunksConfig.class)
public class ChunksTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenChunksJob_whenJobEnds_thenStatusCompleted() 
      throws Exception {
 
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
 
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus()); 
    }
}
```

按照上面为TaskletsConfig解释的配置ChunksConfig之后，我们就可以运行测试了！

作业完成后，我们可以看到output.csv再次包含预期结果，日志描述了流程：

```plaintext
[main] DEBUG o.b.t.chunks.LineReader - Line Reader initialized.
[main] DEBUG o.b.t.chunks.LinesWriter - Line Writer initialized.
[main] DEBUG o.b.t.chunks.LineProcessor - Line Processor initialized.
[main] DEBUG o.b.t.chunks.LineReader - Read line: [Mae Hodges,10/22/1972]
[main] DEBUG o.b.t.chunks.LineReader - Read line: [Gary Potter,02/22/1953]
[main] DEBUG o.b.t.chunks.LineProcessor - Calculated age 45 for line [Mae Hodges,10/22/1972]
[main] DEBUG o.b.t.chunks.LineProcessor - Calculated age 64 for line [Gary Potter,02/22/1953]
[main] DEBUG o.b.t.chunks.LinesWriter - Wrote line [Mae Hodges,10/22/1972,45]
[main] DEBUG o.b.t.chunks.LinesWriter - Wrote line [Gary Potter,02/22/1953,64]
[main] DEBUG o.b.t.chunks.LineReader - Read line: [Betty Wise,02/17/1968]
[main] DEBUG o.b.t.chunks.LineReader - Read line: [Wayne Rose,04/06/1977]
[main] DEBUG o.b.t.chunks.LineProcessor - Calculated age 49 for line [Betty Wise,02/17/1968]
[main] DEBUG o.b.t.chunks.LineProcessor - Calculated age 40 for line [Wayne Rose,04/06/1977]
[main] DEBUG o.b.t.chunks.LinesWriter - Wrote line [Betty Wise,02/17/1968,49]
[main] DEBUG o.b.t.chunks.LinesWriter - Wrote line [Wayne Rose,04/06/1977,40]
[main] DEBUG o.b.t.chunks.LineReader - Read line: [Adam Caldwell,09/27/1995]
[main] DEBUG o.b.t.chunks.LineReader - Read line: [Lucille Phillips,05/14/1992]
[main] DEBUG o.b.t.chunks.LineProcessor - Calculated age 22 for line [Adam Caldwell,09/27/1995]
[main] DEBUG o.b.t.chunks.LineProcessor - Calculated age 25 for line [Lucille Phillips,05/14/1992]
[main] DEBUG o.b.t.chunks.LinesWriter - Wrote line [Adam Caldwell,09/27/1995,22]
[main] DEBUG o.b.t.chunks.LinesWriter - Wrote line [Lucille Phillips,05/14/1992,25]
[main] DEBUG o.b.t.chunks.LineProcessor - Line Processor ended.
[main] DEBUG o.b.t.chunks.LinesWriter - Line Writer ended.
[main] DEBUG o.b.t.chunks.LineReader - Line Reader ended.
```

我们有相同的结果和不同的流程。日志清楚地表明作业是如何按照这种方法执行的。

## 六. 总结

不同的环境将表明需要一种方法或另一种方法。虽然 Tasklet 对于“一个接一个”的场景感觉更自然，但块提供了一种简单的解决方案来处理分页读取或我们不想在内存中保留大量数据的情况。