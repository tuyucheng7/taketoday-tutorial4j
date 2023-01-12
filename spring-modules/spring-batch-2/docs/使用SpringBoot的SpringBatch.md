## 1. 概述

[Spring Batch](https://spring.io/projects/spring-batch)是一个强大的框架，用于开发健壮的批处理应用程序。在我们之前的教程中，我们[介绍了 Spring Batch](https://www.baeldung.com/introduction-to-spring-batch)。

[在本教程中，我们将在上一个教程的基础上学习如何使用Spring Boot](https://www.baeldung.com/category/spring/spring-boot/)设置和创建一个基本的批处理驱动应用程序。

## 2.Maven依赖

首先，让我们将[spring-boot-starter-batch](https://search.maven.org/classic/#search|ga|1|g%3Aorg.springframework.boot a%3Aspring-boot-starter-batch)添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
    <version>2.7.2</version>
</dependency>
```

我们还将添加org.hsqldb依赖项，它也可以从[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.hsqldb" AND a%3A"hsqldb") 获得：

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.5.1</version>
    <scope>runtime</scope>
</dependency>
```

## 3. 定义一个简单的 Spring Batch 作业

我们将构建一个从 CSV 文件导入咖啡列表的作业，使用自定义处理器对其进行转换，并将最终结果存储在内存数据库中。

### 3.1. 入门

让我们从定义我们的应用程序入口点开始：

```java
@SpringBootApplication
public class SpringBootBatchProcessingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootBatchProcessingApplication.class, args);
    }
}
```

正如我们所见，这是一个标准的Spring Boot应用程序。由于我们希望尽可能使用默认配置值，因此我们将使用一组非常简单的应用程序配置属性。

我们将在src/main/resources/application.properties文件中定义这些属性：

```plaintext
file.input=coffee-list.csv
```

此属性包含我们输入的咖啡列表的位置。每行都包含我们咖啡的品牌、产地和一些特征：

```plaintext
Blue Mountain,Jamaica,Fruity
Lavazza,Colombia,Strong
Folgers,America,Smokey
```

正如我们将要看到的，这是一个平面 CSV 文件，这意味着 Spring 无需任何特殊定制即可处理它。

接下来，我们将添加一个 SQL 脚本schema-all.sql来创建我们的咖啡桌来存储数据：

```sql
DROP TABLE coffee IF EXISTS;

CREATE TABLE coffee  (
    coffee_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    brand VARCHAR(20),
    origin VARCHAR(20),
    characteristics VARCHAR(30)
);
```

方便的是，Spring Boot 会在启动时自动运行这个脚本。

### 3.2. 咖啡领域类

随后，我们需要一个简单的域类来保存我们的咖啡：

```java
public class Coffee {

    private String brand;
    private String origin;
    private String characteristics;

    public Coffee(String brand, String origin, String characteristics) {
        this.brand = brand;
        this.origin = origin;
        this.characteristics = characteristics;
    }

    // getters and setters
}
```

如前所述，我们的Coffee对象包含三个属性：

-   一个品牌
-   一个起源
-   一些额外的特性

## 4.作业配置

现在，进入关键组件，即我们的作业配置。我们将逐步进行，构建我们的配置并沿途解释每个部分：

```java
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Value("${file.input}")
    private String fileInput;
    
    // ...
}
```

首先，我们从一个标准的 Spring @Configuration类开始。接下来，我们向我们的类添加一个 @EnableBatchProcessing注解。值得注意的是，这使我们能够访问许多支持工作的有用 bean，并将节省我们大量的腿部工作。

此外，使用此注解还使我们能够访问两个有用的工厂，稍后我们将在构建作业配置和作业步骤时使用它们。

对于我们初始配置的最后一部分，我们包括对我们之前声明的file.input属性的引用。

### 4.1. 我们工作的读者和作家

现在，我们可以继续在我们的配置中定义一个读取器 bean：

```java
@Bean
public FlatFileItemReader reader() {
    return new FlatFileItemReaderBuilder().name("coffeeItemReader")
      .resource(new ClassPathResource(fileInput))
      .delimited()
      .names(new String[] { "brand", "origin", "characteristics" })
      .fieldSetMapper(new BeanWrapperFieldSetMapper() {{
          setTargetType(Coffee.class);
      }})
      .build();
}
```

简而言之，我们上面定义的阅读器 bean 查找一个名为coffee-list.csv的文件，并将每个行项目解析为一个Coffee对象。

同样，我们定义一个 writer bean：

```java
@Bean
public JdbcBatchItemWriter writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder()
      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
      .sql("INSERT INTO coffee (brand, origin, characteristics) VALUES (:brand, :origin, :characteristics)")
      .dataSource(dataSource)
      .build();
}
```

这一次，我们包含了将单个咖啡项目插入数据库所需的 SQL 语句，由Coffee对象的Javabean 属性驱动。方便地，数据源由 @EnableBatchProcessing注解自动创建。

### 4.2. 把我们的工作放在一起

最后，我们需要添加实际的作业步骤和配置：

```java
@Bean
public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
    return jobBuilderFactory.get("importUserJob")
      .incrementer(new RunIdIncrementer())
      .listener(listener)
      .flow(step1)
      .end()
      .build();
}

@Bean
public Step step1(JdbcBatchItemWriter writer) {
    return stepBuilderFactory.get("step1")
      .<Coffee, Coffee> chunk(10)
      .reader(reader())
      .processor(processor())
      .writer(writer)
      .build();
}

@Bean
public CoffeeItemProcessor processor() {
    return new CoffeeItemProcessor();
}
```

如我们所见，我们的工作相对简单，由在step1方法中定义的一个步骤组成。

我们来看看这一步做了什么：

-   首先，我们配置我们的步骤，使其使用chunk(10)声明一次最多写入十条记录
-   然后，我们使用 reader bean 读取咖啡数据，我们使用reader方法设置它
-   接下来，我们将每个咖啡项目传递给自定义处理器，我们在其中应用一些自定义业务逻辑
-   最后，我们使用之前看到的 writer 将每个咖啡项目写入数据库

另一方面，我们的importUserJob包含我们的作业定义，其中包含一个使用内置RunIdIncrementer类的 id。我们还设置了一个JobCompletionNotificationListener，我们用它来在作业完成时得到通知。

为了完成我们的作业配置，我们列出了每个步骤(尽管这个作业只有一个步骤)。我们现在有一个完美配置的工作！

## 5.定制咖啡处理器

让我们详细看一下我们之前在作业配置中定义的自定义处理器：

```java
public class CoffeeItemProcessor implements ItemProcessor<Coffee, Coffee> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeItemProcessor.class);

    @Override
    public Coffee process(final Coffee coffee) throws Exception {
        String brand = coffee.getBrand().toUpperCase();
        String origin = coffee.getOrigin().toUpperCase();
        String chracteristics = coffee.getCharacteristics().toUpperCase();

        Coffee transformedCoffee = new Coffee(brand, origin, chracteristics);
        LOGGER.info("Converting ( {} ) into ( {} )", coffee, transformedCoffee);

        return transformedCoffee;
    }
}
```

特别有趣的是，ItemProcessor接口为我们提供了一种在作业执行期间应用某些特定业务逻辑的机制。

为了简单起见，我们定义了CoffeeItemProcessor，它接受一个输入Coffee对象并将每个属性转换为大写。

## 6. 工作完成

此外，我们还将编写一个JobCompletionNotificationListener 以在我们的工作完成时提供一些反馈：

```java
@Override
public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
        LOGGER.info("!!! JOB FINISHED! Time to verify the results");

        String query = "SELECT brand, origin, characteristics FROM coffee";
        jdbcTemplate.query(query, (rs, row) -> new Coffee(rs.getString(1), rs.getString(2), rs.getString(3)))
          .forEach(coffee -> LOGGER.info("Found < {} > in the database.", coffee));
    }
}
```

在上面的示例中，我们重写了afterJob方法并检查作业是否成功完成。此外，我们运行一个简单的查询来检查每个咖啡项目是否已成功存储在数据库中。

## 7. 运行我们的工作

现在我们已经准备好运行我们的工作，接下来是有趣的部分。让我们继续运行我们的工作：

```plaintext
...
17:41:16.336 [main] INFO  c.b.b.JobCompletionNotificationListener -
  !!! JOB FINISHED! Time to verify the results
17:41:16.336 [main] INFO  c.b.b.JobCompletionNotificationListener -
  Found < Coffee [brand=BLUE MOUNTAIN, origin=JAMAICA, characteristics=FRUITY] > in the database.
17:41:16.337 [main] INFO  c.b.b.JobCompletionNotificationListener -
  Found < Coffee [brand=LAVAZZA, origin=COLOMBIA, characteristics=STRONG] > in the database.
17:41:16.337 [main] INFO  c.b.b.JobCompletionNotificationListener -
  Found < Coffee [brand=FOLGERS, origin=AMERICA, characteristics=SMOKEY] > in the database.
...

```

如我们所见，我们的工作成功运行，并且每个咖啡项目都按预期存储在数据库中。

## 八. 总结

在本文中，我们学习了如何使用Spring Boot创建一个简单的 Spring Batch 作业。首先，我们从定义一些基本配置开始。

然后，我们看到了如何添加文件读取器和数据库写入器。最后，我们了解了如何应用一些自定义处理并检查我们的作业是否已成功执行。