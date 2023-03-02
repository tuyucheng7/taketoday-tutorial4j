## 1. 简介

**[Spring Batch](https://projects.spring.io/spring-batch/)提供了两种不同的方式来实现作业：使用tasklets和chunks**。

在本文中，我们将学习如何使用一个简单的真实示例来配置和实现这两种方法。

## 2. 依赖关系

让我们从添加所需的依赖项开始：

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

要获取最新版本的[spring-batch-core](https://search.maven.org/search?q=a:spring-batch-core)和[spring-batch-test](https://search.maven.org/search?q=a:spring-batch-test)，请参考Maven Central。

## 3. 我们的用例

让我们考虑一个包含以下内容的CSV文件：

```csv
Mae Hodges,10/22/1972
Gary Potter,02/22/1953
Betty Wise,02/17/1968
Wayne Rose,04/06/1977
Adam Caldwell,09/27/1995
Lucille Phillips,05/14/1992
```

每行的**第一个位置代表一个人的名字，第二个位置代表他/她的出生日期**。

我们的用例是**生成另一个包含每个人的姓名和年龄的CSV文件**：

```csv
Mae Hodges,45
Gary Potter,64
Betty Wise,49
Wayne Rose,40
Adam Caldwell,22
Lucille Phillips,25
```

现在我们的领域已经很清楚了，让我们继续使用这两种方法构建解决方案。我们将从tasklet开始。

## 4. Tasklet方法

### 4.1 介绍与设计

Tasklet旨在在一个步骤中执行单个任务。我们的作业将由几个步骤组成，这些步骤一个接一个地执行。**每个步骤应该只执行一个定义的任务**。

我们的作业将包括三个步骤：

1.  从输入CSV文件中读取行
2.  计算输入CSV文件中每个人的年龄
3.  将每个人的姓名和年龄写入新的输出CSV文件

现在大致思路已经准备就绪，让我们为每个步骤创建一个类。

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

至此，**我们所有的步骤都实现了Tasklet接口**。这将迫使我们实现它的execute方法：

```java
@Override
public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    // ...
}
```

我们将在这个方法中为每个步骤添加逻辑。在开始使用该代码之前，让我们配置我们的作业。

### 4.2 配置

我们需要**在Spring的应用程序上下文中添加一些配置**。为上一节中创建的类添加标准bean声明后，我们就可以创建作业定义了：

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

这意味着我们的“taskletsJob”将包含三个步骤。第一个(readLines)将执行bean linesReader中定义的tasklet并进入到下一步：processLines。ProcessLines将执行bean linesProcessor中定义的tasklet并转到最后一步：writeLines。

我们的作业流程已定义，我们准备添加一些逻辑！

### 4.3 模型和实用程序

由于我们将在CSV文件中操作行，因此我们将创建一个Line类：

```java
public class Line implements Serializable {

    private String name;
    private LocalDate dob;
    private Long age;

    // standard constructor, getters, setters and toString implementation
}
```

请注意，Line实现了Serializable。这是因为Line将充当DTO在步骤之间传输数据。根据Spring Batch，**在步骤之间传输的对象必须是可序列化的**。

另一方面，我们可以开始考虑读写行。

为此，我们将使用OpenCSV：

```xml
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>4.1</version>
</dependency>
```

在Maven Central中查找最新的[OpenCSV](https://search.maven.org/search?q=a:opencsv)版本。

包含OpenCSV后，**我们还将创建一个FileUtils类**。它将提供读取和写入CSV行的方法：

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
              LocalDate.parse(line[1], DateTimeFormatter.ofPattern("MM/dd/yyyy")));
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

请注意，readLine充当OpenCSV的readNext方法的包装器并返回一个Line对象。

同样，writeLine包装OpenCSV的writeNext接收一个Line对象。可以在[GitHub项目](https://github.com/eugenp/tutorials/blob/master/spring-batch/src/main/java/com/baeldung/taskletsvschunks/utils/FileUtils.java)中找到此类的完整实现。

此时，我们已经准备好从每个步骤实现开始。

### 4.4 LinesReader

让我们继续完成我们的LinesReader类：

```java
public class LinesReader implements Tasklet, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(LinesReader.class);

    private List<Line> lines;
    private FileUtils fu;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        lines = new ArrayList<>();
        fu = new FileUtils("taskletsvschunks/input/tasklets-vs-chunks.csv");
        logger.debug("Lines Reader initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
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

LinesReader的execute方法在输入文件路径上创建一个FileUtils实例。然后，**将行添加到列表中，直到没有更多行可读**。

我们的类还**实现了StepExecutionListener**，它提供了两个额外的方法：beforeStep和afterStep。我们将使用这些方法在execute运行之前和之后初始化和关闭内容。

如果我们看一下afterStep代码，我们会注意到将结果列表(lines)放入作业上下文中以使其可用于下一步的行：

```java
stepExecution
    .getJobExecution()
    .getExecutionContext()
    .put("lines", this.lines);
```

此时，我们的第一步已经完成了它的职责：将CSV行加载到内存中的List中。让我们转到第二步并处理它们。

### 4.5 LinesProcessor

**LinesProcessor还将实现StepExecutionListener，当然还有Tasklet**。这意味着它还将实现beforeStep、execute和afterStep方法：

```java
public class LinesProcessor implements Tasklet, StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(LinesProcessor.class);

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
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
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

很容易理解它**从作业上下文中加载lines列表并计算每个人的年龄**。

无需在上下文中放置另一个结果列表，因为修改发生在来自上一步的同一对象上。

我们已经为最后一步做好了准备。

### 4.6 LinesWriter

**LinesWriter的任务是遍历lines列表并将姓名和年龄写入输出文件**：

```java
public class LinesWriter implements Tasklet, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(LinesWriter.class);

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
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
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

我们完成了作业的实现！让我们创建一个测试来运行它并查看结果。

### 4.7 运行作业

要运行该作业，我们将创建一个测试：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TaskletsConfig.class)
public class TaskletsTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenTaskletsJob_whenJobEnds_thenStatusCompleted() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
```

ContextConfiguration注解指向具有我们的作业定义的Spring上下文配置类。

在运行测试之前，我们需要添加几个额外的bean：

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

```shell
[main] DEBUG c.t.t.t.tasklets.LinesReader - Lines Reader initialized.
[main] DEBUG c.t.t.t.tasklets.LinesReader - Read line: [Mae Hodges,10/22/1972]
[main] DEBUG c.t.t.t.tasklets.LinesReader - Read line: [Gary Potter,02/22/1953]
[main] DEBUG c.t.t.t.tasklets.LinesReader - Read line: [Betty Wise,02/17/1968]
[main] DEBUG c.t.t.t.tasklets.LinesReader - Read line: [Wayne Rose,04/06/1977]
[main] DEBUG c.t.t.t.tasklets.LinesReader - Read line: [Adam Caldwell,09/27/1995]
[main] DEBUG c.t.t.t.tasklets.LinesReader - Read line: [Lucille Phillips,05/14/1992]
[main] DEBUG c.t.t.t.tasklets.LinesReader - Lines Reader ended.
[main] DEBUG c.t.t.t.tasklets.LinesProcessor - Lines Processor initialized.
[main] DEBUG c.t.t.t.tasklets.LinesProcessor - Calculated age 45 for line [Mae Hodges,10/22/1972]
[main] DEBUG c.t.t.t.tasklets.LinesProcessor - Calculated age 64 for line [Gary Potter,02/22/1953]
[main] DEBUG c.t.t.t.tasklets.LinesProcessor - Calculated age 49 for line [Betty Wise,02/17/1968]
[main] DEBUG c.t.t.t.tasklets.LinesProcessor - Calculated age 40 for line [Wayne Rose,04/06/1977]
[main] DEBUG c.t.t.t.tasklets.LinesProcessor - Calculated age 22 for line [Adam Caldwell,09/27/1995]
[main] DEBUG c.t.t.t.tasklets.LinesProcessor - Calculated age 25 for line [Lucille Phillips,05/14/1992]
[main] DEBUG c.t.t.t.tasklets.LinesProcessor - Lines Processor ended.
[main] DEBUG c.t.t.t.tasklets.LinesWriter - Lines Writer initialized.
[main] DEBUG c.t.t.t.tasklets.LinesWriter - Wrote line [Mae Hodges,10/22/1972,45]
[main] DEBUG c.t.t.t.tasklets.LinesWriter - Wrote line [Gary Potter,02/22/1953,64]
[main] DEBUG c.t.t.t.tasklets.LinesWriter - Wrote line [Betty Wise,02/17/1968,49]
[main] DEBUG c.t.t.t.tasklets.LinesWriter - Wrote line [Wayne Rose,04/06/1977,40]
[main] DEBUG c.t.t.t.tasklets.LinesWriter - Wrote line [Adam Caldwell,09/27/1995,22]
[main] DEBUG c.t.t.t.tasklets.LinesWriter - Wrote line [Lucille Phillips,05/14/1992,25]
[main] DEBUG c.t.t.t.tasklets.LinesWriter - Lines Writer ended.
```

这就是Tasklet的全部内容。现在我们可以继续使用Chunks方法。

## 5. Chunks方法

### 5.1 介绍与设计

顾名思义，这种方法**对数据块执行操作**。也就是说，它不会一次读取、处理和写入所有行，而是一次读取、处理和写入固定数量的记录(块)。

然后，它会重复这个循环，直到文件中没有更多数据。

因此，流程会略有不同：

1.  当文件有更多行可读时：
    -   对X行数执行操作：
        -   读取1行
        -   处理1行
    -   写入X行。

因此，我们还需要**为面向chunk(块)的方法创建三个bean**：

```java
public class LineReader {
     // ...
}
```

```java
public class LineProcessor {
    // ...
}
```

```java
public class LinesWriter {
    // ...
}
```

在开始实现之前，让我们配置我们的作业。

### 5.2 配置

作业定义看起来也会有所不同：

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
    protected Step processLines(ItemReader<Line> reader, ItemProcessor<Line, Line> processor, ItemWriter<Line> writer) {
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

在这种情况下，只有一个步骤只执行一个tasklet。

但是，**该tasklet定义了一个reader、一个writer和一个将对数据块进行操作的processor**。

请注意，**提交间隔表示一个块中要处理的数据量**。我们的作业将一次读取、处理和写入两行。

现在我们准备好添加块逻辑了！

### 5.3 LineReader

LineReader将负责读取一条记录并返回一个包含其内容的Line实例。

要成为reader，**我们的类必须实现ItemReader接口**：

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

代码很简单，它只读取一行并返回它。我们还将为此类的最终版本实现StepExecutionListener：

```java
public class LineReader implements ItemReader<Line>, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(LineReader.class);

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

### 5.4 LineProcessor

LineProcessor遵循与LineReader几乎相同的逻辑。

但是，在这种情况下，**我们将实现ItemProcessor及其方法process()**：

```java
public class LineProcessor implements ItemProcessor<Line, Line> {

    private Logger logger = LoggerFactory.getLogger(LineProcessor.class);

    @Override
    public Line process(Line line) throws Exception {
        long age = ChronoUnit.YEARS.between(line.getDob(), LocalDate.now());
        logger.debug("Calculated age " + age + " for line " + line.toString());
        line.setAge(age);
        return line;
    }
}
```

process()方法接收输入行，对其进行处理并返回输出行。同样，我们还将实现StepExecutionListener：

```java
public class LineProcessor implements ItemProcessor<Line, Line>, StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(LineProcessor.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Line Processor initialized.");
    }

    @Override
    public Line process(Line line) throws Exception {
        long age = ChronoUnit.YEARS.between(line.getDob(), LocalDate.now());
        logger.debug("Calculated age " + age + " for line " + line.toString());
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

### 5.5 LinesWriter

与reader和processor不同，**LinesWriter将写入整个块的所有行**，因此它接收一个lines列表：

```java
public class LinesWriter implements ItemWriter<Line>, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(LinesWriter.class);

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

LinesWriter代码不言自明。再一次，我们准备好测试我们的作业。

### 5.6 运行作业

我们将创建一个新测试，与我们为tasklet方法创建的测试相同：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ChunksConfig.class)
public class ChunksTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenChunksJob_whenJobEnds_thenStatusCompleted() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
```

按照上面为TaskletsConfig解释的配置ChunksConfig之后，我们就可以运行测试了！

作业完成后，我们可以看到output.csv再次包含预期的结果，日志描述了流程：

```shell
[main] DEBUG c.t.t.t.chunks.LineReader - Line Reader initialized.
[main] DEBUG c.t.t.t.chunks.LinesWriter - Line Writer initialized.
[main] DEBUG c.t.t.t.chunks.LineProcessor - Line Processor initialized.
[main] DEBUG c.t.t.t.chunks.LineReader - Read line: [Mae Hodges,10/22/1972]
[main] DEBUG c.t.t.t.chunks.LineReader - Read line: [Gary Potter,02/22/1953]
[main] DEBUG c.t.t.t.chunks.LineProcessor - Calculated age 45 for line [Mae Hodges,10/22/1972]
[main] DEBUG c.t.t.t.chunks.LineProcessor - Calculated age 64 for line [Gary Potter,02/22/1953]
[main] DEBUG c.t.t.t.chunks.LinesWriter - Wrote line [Mae Hodges,10/22/1972,45]
[main] DEBUG c.t.t.t.chunks.LinesWriter - Wrote line [Gary Potter,02/22/1953,64]
[main] DEBUG c.t.t.t.chunks.LineReader - Read line: [Betty Wise,02/17/1968]
[main] DEBUG c.t.t.t.chunks.LineReader - Read line: [Wayne Rose,04/06/1977]
[main] DEBUG c.t.t.t.chunks.LineProcessor - Calculated age 49 for line [Betty Wise,02/17/1968]
[main] DEBUG c.t.t.t.chunks.LineProcessor - Calculated age 40 for line [Wayne Rose,04/06/1977]
[main] DEBUG c.t.t.t.chunks.LinesWriter - Wrote line [Betty Wise,02/17/1968,49]
[main] DEBUG c.t.t.t.chunks.LinesWriter - Wrote line [Wayne Rose,04/06/1977,40]
[main] DEBUG c.t.t.t.chunks.LineReader - Read line: [Adam Caldwell,09/27/1995]
[main] DEBUG c.t.t.t.chunks.LineReader - Read line: [Lucille Phillips,05/14/1992]
[main] DEBUG c.t.t.t.chunks.LineProcessor - Calculated age 22 for line [Adam Caldwell,09/27/1995]
[main] DEBUG c.t.t.t.chunks.LineProcessor - Calculated age 25 for line [Lucille Phillips,05/14/1992]
[main] DEBUG c.t.t.t.chunks.LinesWriter - Wrote line [Adam Caldwell,09/27/1995,22]
[main] DEBUG c.t.t.t.chunks.LinesWriter - Wrote line [Lucille Phillips,05/14/1992,25]
[main] DEBUG c.t.t.t.chunks.LineProcessor - Line Processor ended.
[main] DEBUG c.t.t.t.chunks.LinesWriter - Line Writer ended.
[main] DEBUG c.t.t.t.chunks.LineReader - Line Reader ended.
```

**我们有相同的结果和不同的流程**。日志清楚地表明作业是如何按照这种方法执行的。

## 6. 总结

具体使用哪种方法取决于不同环境的需求。**Tasklet对于“一个接一个”的场景感觉更自然，而Chunks提供了一种简单的解决方案来处理分页读取或我们不想在内存中保留大量数据的情况**。