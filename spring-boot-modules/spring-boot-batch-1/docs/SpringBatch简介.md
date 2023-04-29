## 1. 概述

在本教程中，我们将查看一个实用的、以代码为中心的Spring Batch介绍。Spring Batch是一个处理框架，专为稳健地执行作业而设计。

它的当前版本4.3支持Spring 5和Java 8。它还适应JSR-352，这是用于批处理的新Java规范。

[以下](https://docs.spring.io/spring-batch/docs/current/reference/html/index-single.html#springBatchUsageScenarios)是该框架的一些有趣且实用的用例。

## 2. 工作流基础

Spring Batch遵循传统的批处理架构，其中作业存储库负责调度作业和与作业交互。

一个作业可以有多个步骤，每一步通常都遵循读取数据、处理数据和写入数据的顺序。

当然，该框架将在这里为我们完成大部分繁重的工作-尤其是当涉及到处理作业的低级持久性工作时，使用Sqlite作为作业存储库。

### 2.1 示例用例

我们要在这里处理的简单用例是将一些金融交易数据从CSV迁移到XML。

输入文件的结构非常简单。

它包含每行交易，由用户名、用户ID、交易日期和金额组成：

```csv
username, userid, transaction_date, transaction_amount
devendra, 1234, 31/10/2015, 10000
john, 2134, 3/12/2015, 12321
robin, 2134, 2/02/2015, 23411
```

## 3. Maven POM

该项目所需的依赖项是Spring Core、Spring Batch和Sqlite JDBC连接器：

```xml
<!-- SQLite database driver -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.15.1</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-oxm</artifactId>
    <version>5.3.0</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.3.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.batch</groupId>
    <artifactId>spring-batch-core</artifactId>
    <version>4.3.0</version>
</dependency>
```

## 4. Spring Batch配置

我们要做的第一件事是使用XML配置Spring Batch：

```xml
<!-- connect to SQLite database -->
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="org.sqlite.JDBC" />
    <property name="url" value="jdbc:sqlite:repository.sqlite" />
    <property name="username" value="" />
    <property name="password" value="" />
</bean>

<!-- create job-meta tables automatically -->
<jdbc:initialize-database data-source="dataSource">
    <jdbc:script location="org/springframework/batch/core/schema-drop-sqlite.sql" />
    <jdbc:script location="org/springframework/batch/core/schema-sqlite.sql" />
</jdbc:initialize-database>

<!-- stored job-meta in memory -->
<!--<bean id="jobRepository" class="org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean"> 
    <property name="transactionManager" ref="transactionManager" />
</bean> -->

<!-- stored job-meta in database -->
<bean id="jobRepository" class="org.springframework.batch.core.repository.support.JobRepositoryFactoryBean">
    <property name="dataSource" ref="dataSource" />
    <property name="transactionManager" ref="transactionManager" />
    <property name="databaseType" value="sqlite" />
</bean>

<bean id="transactionManager" class="org.springframework.batch.support.transaction.ResourcelessTransactionManager" />

<bean id="jobLauncher" class="org.springframework.batch.core.launch.support.SimpleJobLauncher">
    <property name="jobRepository" ref="jobRepository" />
</bean>
```

当然，也可以使用Java配置：

```java
@Configuration
@EnableBatchProcessing
@Profile("spring")
public class SpringConfig {

    @Value("org/springframework/batch/core/schema-drop-sqlite.sql")
    private Resource dropRepositoryTables;

    @Value("org/springframework/batch/core/schema-sqlite.sql")
    private Resource dataRepositorySchema;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:repository.sqlite");
        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) throws MalformedURLException {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();

        databasePopulator.addScript(dropReopsitoryTables);
        databasePopulator.addScript(dataReopsitorySchema);
        databasePopulator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }

    private JobRepository getJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource());
        factory.setTransactionManager(getTransactionManager());
        factory.afterPropertiesSet();
        return (JobRepository) factory.getObject();
    }

    private PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    public JobLauncher getJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
```

## 5. Spring Batch作业配置

现在让我们编写CSV到XML工作的作业描述：

```xml
<import resource="spring.xml" />

<bean id="record" class="cn.tuyucheng.taketoday.spring_batch_intro.model.Transaction"/>
<bean id="itemReader" class="org.springframework.batch.item.file.FlatFileItemReader">

    <property name="resource" value="input/record.csv" />

    <property name="lineMapper">
        <bean class="org.springframework.batch.item.file.mapping.DefaultLineMapper">
            <property name="lineTokenizer">
                <bean class="org.springframework.batch.item.file.transform.DelimitedLineTokenizer">
                    <property name="names" value="username,userid,transactiondate,amount" />
                </bean>
            </property>
            <property name="fieldSetMapper">
                <bean class="cn.tuyucheng.taketoday.spring_batch_intro.service.RecordFieldSetMapper" />
            </property>
        </bean>
    </property>
</bean>

<bean id="itemProcessor" class="cn.tuyucheng.taketoday.spring_batch_intro.service.CustomItemProcessor" />

<bean id="itemWriter" class="org.springframework.batch.item.xml.StaxEventItemWriter">
    <property name="resource" value="file:xml/output.xml" />
    <property name="marshaller" ref="recordMarshaller" />
    <property name="rootTagName" value="transactionRecord" />
</bean>

<bean id="recordMarshaller" class="org.springframework.oxm.jaxb.Jaxb2Marshaller">
    <property name="classesToBeBound">
        <list>
            <value>cn.tuyucheng.taketoday.spring_batch_intro.model.Transaction</value>
        </list>
    </property>
</bean>
<batch:job id="firstBatchJob">
    <batch:step id="step1">
        <batch:tasklet>
            <batch:chunk reader="itemReader" writer="itemWriter" processor="itemProcessor" commit-interval="10">
            </batch:chunk>
        </batch:tasklet>
    </batch:step>
</batch:job>
```

这是类似的基于Java的作业配置：

```java
@Profile("spring")
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Value("input/record.csv")
    private Resource inputCsv;

    @Value("file:xml/output.xml")
    private Resource outputXml;

    @Bean
    public ItemReader<Transaction> itemReader() throws UnexpectedInputException, ParseException {
        FlatFileItemReader<Transaction> reader = new FlatFileItemReader<Transaction>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        String[] tokens = { "username", "userid", "transactiondate", "amount" };
        tokenizer.setNames(tokens);
        reader.setResource(inputCsv);
        DefaultLineMapper<Transaction> lineMapper = new DefaultLineMapper<Transaction>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new RecordFieldSetMapper());
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<Transaction, Transaction> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<Transaction> itemWriter(Marshaller marshaller)
          throws MalformedURLException {
        StaxEventItemWriter<Transaction> itemWriter =
              new StaxEventItemWriter<Transaction>();
        itemWriter.setMarshaller(marshaller);
        itemWriter.setRootTagName("transactionRecord");
        itemWriter.setResource(outputXml);
        return itemWriter;
    }

    @Bean
    public Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(new Class[] { Transaction.class });
        return marshaller;
    }

    @Bean
    protected Step step1(ItemReader<Transaction> reader,
                         ItemProcessor<Transaction, Transaction> processor,
                         ItemWriter<Transaction> writer) {
        return steps.get("step1").<Transaction, Transaction> chunk(10)
              .reader(reader).processor(processor).writer(writer).build();
    }

    @Bean(name = "firstBatchJob")
    public Job job(@Qualifier("step1") Step step1) {
        return jobs.get("firstBatchJob").start(step1).build();
    }
}
```

现在我们有了整个配置，让我们分解它并开始讨论它。

### 5.1 使用ItemReader读取数据和创建对象

首先，我们配置了cvsFileItemReader，它将从record.csv中读取数据并将其转换为Transaction对象：

```java
@SuppressWarnings("restriction")
@XmlRootElement(name = "transactionRecord")
public class Transaction {
    private String username;
    private int userId;
    private LocalDateTime transactionDate;
    private double amount;

    /* getters and setters for the attributes */
    @Override
    public String toString() {
        return "Transaction [username=" + username + ", userId=" + userId
              + ", transactionDate=" + transactionDate + ", amount=" + amount
              + "]";
    }
}
```

为此，它使用自定义映射器：

```java
public class RecordFieldSetMapper implements FieldSetMapper<Transaction> {

    public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyy");
        Transaction transaction = new Transaction();

        transaction.setUsername(fieldSet.readString("username"));
        transaction.setUserId(fieldSet.readInt(1));
        transaction.setAmount(fieldSet.readDouble(3));
        String dateString = fieldSet.readString(2);
        transaction.setTransactionDate(LocalDate.parse(dateString, formatter).atStartOfDay());
        return transaction;
    }
}
```

### 5.2 使用ItemProcessor处理数据

我们已经创建了自己的项目处理器CustomItemProcessor，这不会处理与Transaction对象相关的任何内容。

它所做的只是将来自读取器的原始对象传递给写入器：

```java
public class CustomItemProcessor implements ItemProcessor<Transaction, Transaction> {

    public Transaction process(Transaction item) {
        return item;
    }
}
```

### 5.3 使用ItemWriter将对象写入FS

最后，我们要将此交易存储到位于xml/output.xml的XML文件中：

```xml
<bean id="itemWriter" class="org.springframework.batch.item.xml.StaxEventItemWriter">
    <property name="resource" value="file:xml/output.xml" />
    <property name="marshaller" ref="recordMarshaller" />
    <property name="rootTagName" value="transactionRecord" />
</bean>
```

### 5.4 配置批处理作业

因此，我们所要做的就是使用batch:job语法将点与作业联系起来。

注意commit-interval，这是在将批次提交给itemWriter之前要保存在内存中的交易数。

它将在内存中保存交易直到该点(或直到遇到输入数据的末尾)：

```xml
<batch:job id="firstBatchJob">
    <batch:step id="step1">
        <batch:tasklet>
            <batch:chunk reader="itemReader" writer="itemWriter"
                processor="itemProcessor" commit-interval="10">
            </batch:chunk>
        </batch:tasklet>
    </batch:step>
</batch:job>
```

### 5.5 运行批处理作业

现在让我们设置并运行一切：

```java
@Profile("spring")
public class App {
    public static void main(String[] args) {
        // Spring Java config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringConfig.class);
        context.register(SpringBatchConfig.class);
        context.refresh();

        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("firstBatchJob");
        System.out.println("Starting the batch job");
        try {
            JobExecution execution = jobLauncher.run(job, new JobParameters());
            System.out.println("Job Status : " + execution.getStatus());
            System.out.println("Job completed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Job failed");
        }
    }
}
```

我们使用-Dspring.profiles.active=spring Profile运行我们的Spring应用程序。

在下一节中，我们将在Spring Boot应用程序中配置我们的示例。

## 6. Spring Boot配置

在本节中，我们将创建一个Spring Boot应用程序，并将之前的Spring Batch Config转换为在Spring Boot环境中运行。事实上，这大致相当于前面的Spring Batch示例。

### 6.1 Maven依赖项

让我们首先在pom.xml的SpringBoot应用程序中声明[spring-boot-starter-batch](https://search.maven.org/search?q=g:org.springframework.boota:spring-boot-starter-batch)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>
```

我们需要一个数据库来存储Spring Batch作业信息，在本教程中，我们使用内存中的[HSQLDB](https://www.baeldung.com/spring-boot-hsqldb)数据库。因此，我们需要在Spring Boot中使用[hsqldb](https://search.maven.org/search?q=g:org.hsqldbANDa:hsqldb)：

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.7.0</version>
    <scope>runtime</scope>
</dependency>
```

### 6.2 Spring Boot配置

我们使用[@Profile](https://www.baeldung.com/spring-profiles)注解来区分Spring和Spring Boot配置。我们在我们的应用程序中设置spring-boot Profile：

```java
@SpringBootApplication
public class SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication springApp = new SpringApplication(SpringBatchApplication.class);
        springApp.setAdditionalProfiles("spring-boot");
        springApp.run(args);
    }
}
```

### 6.3 Spring Batch作业配置

我们使用与前面的Spring Batch Config类相同的批处理作业配置：

```java
@Configuration
@EnableBatchProcessing
@Profile("spring-boot")
public class SpringBootBatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value("input/record.csv")
    private Resource inputCsv;

    @Value("input/recordWithInvalidData.csv")
    private Resource invalidInputCsv;

    @Value("file:xml/output.xml")
    private Resource outputXml;

    // ...
}
```

我们从Spring @Configuration注解开始，然后，我们在类中添加@EnableBatchProcessing注解。**@EnableBatchProcessing注解自动创建数据源对象并将其提供给我们的作业**。

## 7. 总结

在本文中，我们学习了如何使用Spring Batch以及如何在简单用例中使用它。

我们看到了如何轻松开发批处理管道，以及如何自定义读取、处理和写入的不同阶段。