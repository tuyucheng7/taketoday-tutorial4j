## 1. 简介

与其他基于Spring的应用程序不同，测试批处理作业会带来一些特定的挑战，这主要是由于作业执行方式的异步性质。

在本教程中，我们将探索用于测试[Spring Batch](https://www.baeldung.com/introduction-to-spring-batch)作业的各种替代方案。

## 2. 所需依赖

**我们使用的是[spring-boot-starter-batch](https://search.maven.org/classic/#search|ga|1|spring-boot-starter-batch)**，所以首先让我们在pom.xml中设置所需的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
    <version>2.7.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.7.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.batch</groupId>
    <artifactId>spring-batch-test</artifactId>
    <version>4.3.0.RELEASE</version>
    <scope>test</scope>
</dependency>
```

**我们包括了[spring-boot -starter-test](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-test/3.0.3)和[spring-batch-test](https://central.sonatype.com/artifact/org.springframework.batch/spring-batch-test/5.0.1)**，它们引入了一些必要的辅助方法、监听器和Runner来测试Spring Batch应用程序。

## 3. 定义Spring Batch作业

让我们创建一个简单的应用程序来展示Spring Batch如何解决一些测试挑战。

**我们的应用程序使用两步作业，读取包含结构化书籍信息的CSV输入文件，并输出书籍和书籍详细信息**。

### 3.1 定义作业步骤

随后的两个Step从BookRecord中提取特定信息，然后将这些信息映射到Books(step1)和BookDetails(step2)：

```java
@Bean
public Step step1(ItemReader<BookRecord> csvItemReader, ItemWriter<Book> jsonItemWriter) throws IOException {
    return stepBuilderFactory
        .get("step1")
        .<BookRecord, Book> chunk(3)
        .reader(csvItemReader)
        .processor(bookItemProcessor())
        .writer(jsonItemWriter)
        .build();
}

@Bean
public Step step2(ItemReader<BookRecord> csvItemReader, ItemWriter<BookDetails> listItemWriter) {
    return stepBuilderFactory
        .get("step2")
        .<BookRecord, BookDetails> chunk(3)
        .reader(csvItemReader)
        .processor(bookDetailsItemProcessor())
        .writer(listItemWriter)
        .build();
}
```

### 3.2 定义InputReader和OutputWriter

现在让我们**使用FlatFileItemReader配置CSV文件输入读取器**，以将结构化书籍信息反序列化为BookRecord对象：

```java
private static final String[] TOKENS = { "bookname", "bookauthor", "bookformat", "isbn", "publishyear" };

@Bean
@StepScope
public FlatFileItemReader<BookRecord> csvItemReader(@Value("#{jobParameters['file.input']}") String input) {
    FlatFileItemReaderBuilder<BookRecord> builder = new FlatFileItemReaderBuilder<>();
    FieldSetMapper<BookRecord> bookRecordFieldSetMapper = new BookRecordFieldSetMapper();
    return builder
        .name("bookRecordItemReader")
        .resource(new FileSystemResource(input))
        .delimited()
        .names(TOKENS)
        .fieldSetMapper(bookRecordFieldSetMapper)
        .build();
}
```

这个定义中有几件重要的事情，它们将对我们的测试方式产生影响。

首先，**我们用@StepScope标注了FlatItemReader bean，因此，这个对象将与StepExecution共享它的生命周期**。

**这也允许我们在运行时注入动态值，以便我们可以从第4行的JobParameter传递我们的输入文件**。相反，用于BookRecordFieldSetMapper的标记是在编译时配置的。

然后我们类似地定义JsonFileItemWriter输出写入器：

```java
@Bean
@StepScope
public JsonFileItemWriter<Book> jsonItemWriter(@Value("#{jobParameters['file.output']}") String output) throws IOException {
    JsonFileItemWriterBuilder<Book> builder = new JsonFileItemWriterBuilder<>();
    JacksonJsonObjectMarshaller<Book> marshaller = new JacksonJsonObjectMarshaller<>();
    return builder
        .name("bookItemWriter")
        .jsonObjectMarshaller(marshaller)
        .resource(new FileSystemResource(output))
        .build();
}
```

对于第二步，我们使用Spring Batch提供的ListItemWriter，它只是将内容转储到内存列表中。

### 3.3 定义自定义JobLauncher

接下来，让我们通过在application.properties中设置spring.batch.job.enabled=false来禁用Spring Boot Batch的默认作业启动配置。

**我们配置自己的JobLauncher以在启动Job时传递自定义JobParameters实例**：

```java
@SpringBootApplication
public class SpringBatchApplication implements CommandLineRunner {

    // autowired jobLauncher and transformBooksRecordsJob

    @Value("${file.input}")
    private String input;

    @Value("${file.output}")
    private String output;

    @Override
    public void run(String... args) throws Exception {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.input", input);
        paramsBuilder.addString("file.output", output);
        jobLauncher.run(transformBooksRecordsJob, paramsBuilder.toJobParameters());
    }

    // other methods (main etc.)
}
```

## 4. 测试Spring Batch作业

spring-batch-test依赖项提供了一组有用的辅助方法和监听器，可用于在测试期间配置Spring Batch上下文。

让我们为我们的测试创建一个基本结构：

```java
@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = { SpringBatchConfiguration.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SpringBatchIntegrationTest {

    // other test constants

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @After
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.input", TEST_INPUT);
        paramsBuilder.addString("file.output", TEST_OUTPUT);
        return paramsBuilder.toJobParameters();
    }
}
```

**@SpringBatchTest注解提供了JobLauncherTestUtils和JobRepositoryTestUtils辅助类**。我们使用它们来触发测试中的Job和Step。

**我们的应用程序使用[Spring Boot自动配置](https://www.baeldung.com/spring-boot-annotations)，它启用默认的内存中JobRepository。因此，在同一类中运行多个测试需要在每次测试运行后执行清理步骤**。

最后，**如果我们想从多个测试类运行多个测试，我们需要将我们的[上下文标记为@DirtiesContext](https://www.baeldung.com/spring-dirtiescontext)**。这是为了避免使用相同数据源的多个JobRepository实例发生冲突。

### 4.1 测试端到端作业

我们要测试的第一件事是一个带有小数据集输入的完整的端到端作业。

然后我们可以将结果与预期的测试输出进行比较：

```java
@Test
public void givenReferenceOutput_whenJobExecuted_thenSuccess() throws Exception {
    // given
    FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
    FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
  
    // then
    assertThat(actualJobInstance.getJobName(), is("transformBooksRecords"));
    assertThat(actualJobExitStatus.getExitCode(), is("COMPLETED"));
    AssertFile.assertFileEquals(expectedResult, actualResult);
}
```

Spring Batch Test提供了一种有用的**文件比较方法，用于使用AssertFile类验证输出**。

### 4.2 测试单个步骤

有时端到端地测试完整的Job是非常昂贵的，因此测试单个Steps是有意义的：

```java
@Test
public void givenReferenceOutput_whenStep1Executed_thenSuccess() throws Exception {
    // given
    FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
    FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("step1", defaultJobParameters()); 
    Collection actualStepExecutions = jobExecution.getStepExecutions();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualStepExecutions.size(), is(1));
    assertThat(actualJobExitStatus.getExitCode(), is("COMPLETED"));
    AssertFile.assertFileEquals(expectedResult, actualResult);
}

@Test
public void whenStep2Executed_thenSuccess() {
    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("step2", defaultJobParameters());
    Collection actualStepExecutions = jobExecution.getStepExecutions();
    ExitStatus actualExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualStepExecutions.size(), is(1));
    assertThat(actualExitStatus.getExitCode(), is("COMPLETED"));
    actualStepExecutions.forEach(stepExecution -> {
        assertThat(stepExecution.getWriteCount(), is(8));
    });
}
```

请注意，**我们使用launchStep方法来触发特定步骤**。

请记住，**我们还将ItemReader和ItemWriter设计为在运行时使用动态值，这意味着我们可以将I/O参数传递给JobExecution(第9和23行)**。

对于第一步测试，我们将实际输出与预期输出进行比较。

另一方面，**在第二个测试中，我们验证了预期写入项目的StepExecution**。

### 4.3 测试步骤作用域的组件

现在让我们测试FlatFileItemReader。**回想一下，我们将它作为@StepScope bean公开，因此我们希望使用Spring Batch对此的专门支持**：

```java
// previously autowired itemReader

@Test
public void givenMockedStep_whenReaderCalled_thenSuccess() throws Exception {
    // given
    StepExecution stepExecution = MetaDataInstanceFactory
        .createStepExecution(defaultJobParameters());

    // when
    StepScopeTestUtils.doInStepScope(stepExecution, () -> {
        BookRecord bookRecord;
        itemReader.open(stepExecution.getExecutionContext());
        while ((bookRecord = itemReader.read()) != null) {

            // then
            assertThat(bookRecord.getBookName(), is("Foundation"));
            assertThat(bookRecord.getBookAuthor(), is("Asimov I."));
            assertThat(bookRecord.getBookISBN(), is("ISBN 12839"));
            assertThat(bookRecord.getBookFormat(), is("hardcover"));
            assertThat(bookRecord.getPublishingYear(), is("2018"));
        }
        itemReader.close();
        return null;
    });
}
```

**MetadataInstanceFactory创建了一个自定义的StepExecution，该StepExecution是注入Step-scoped ItemReader所必需的**。

因此，**我们可以借助doInTestScope方法检查读取器的行为**。

接下来，让我们测试JsonFileItemWriter并验证其输出：

```java
@Test
public void givenMockedStep_whenWriterCalled_thenSuccess() throws Exception {
    // given
    FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT_ONE);
    FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);
    Book demoBook = new Book();
    demoBook.setAuthor("Grisham J.");
    demoBook.setName("The Firm");
    StepExecution stepExecution = MetaDataInstanceFactory
        .createStepExecution(defaultJobParameters());

    // when
    StepScopeTestUtils.doInStepScope(stepExecution, () -> {
        jsonItemWriter.open(stepExecution.getExecutionContext());
        jsonItemWriter.write(Arrays.asList(demoBook));
        jsonItemWriter.close();
        return null;
    });

    // then
    AssertFile.assertFileEquals(expectedResult, actualResult);
}
```

与之前的测试不同，**我们现在可以完全控制我们的测试对象**。因此，**我们负责打开和关闭I/O流**。

## 5. 总结

在本教程中，我们探讨了测试Spring Batch作业的各种方法。

端到端测试验证作业的完整执行。测试单个步骤可能有助于复杂的场景。

最后，当涉及到Step-scoped组件时，我们可以使用spring-batch-test提供的一堆辅助方法。他们将协助我们stubbing和mocking Spring Batch域对象。