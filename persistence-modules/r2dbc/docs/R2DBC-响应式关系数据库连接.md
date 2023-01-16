## 1. 概述

在本教程中，我们将展示如何使用 R2DBC 以反应方式执行数据库操作。

为了探索 R2DBC，我们将创建一个简单的 Spring WebFlux REST 应用程序，它为单个实体实现 CRUD 操作，仅使用异步操作来实现该目标。

## 2. 什么是R2DBC？

响应式开发正在兴起，每天都有新的框架出现，现有的框架也越来越多地被采用。然而，反应式开发的一个主要问题是Java/JVM 世界中的数据库访问基本上保持同步。这是 JDBC 设计方式的直接结果，并导致了一些丑陋的 hack 来适应这两种根本不同的方法。

为了解决Java领域对异步数据库访问的需求，出现了两种标准。第一个是 ADBC(异步数据库访问 API)，由 Oracle 提供支持，但在撰写本文时，似乎有些停滞不前，没有明确的时间表。

我们将在此处介绍的第二个是 R2DBC(反应式关系数据库连接)，这是一个由来自 Pivotal 和其他公司的团队领导的社区工作。这个目前还处于测试阶段的项目已经显示出更多的生命力，并且已经提供了 Postgres、H2 和 MSSQL 数据库的驱动程序。

## 3.项目设置

在项目中使用 R2DBC 需要我们添加对核心 API 的依赖和合适的驱动程序。在我们的示例中，我们将使用 H2，因此这意味着只有两个依赖项：

```xml
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-spi</artifactId>
    <version>0.8.0.M7</version>
</dependency>
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-h2</artifactId>
    <version>0.8.0.M7</version>
</dependency>
```

Maven Central 现在仍然没有 R2DBC 工件，所以我们还需要向我们的项目添加几个 Spring 的存储库：

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
   </repository>
   <repository>
       <id>spring-snapshots</id>
       <name>Spring Snapshots</name>
       <url>https://repo.spring.io/snapshot</url>
       <snapshots>
           <enabled>true</enabled>
       </snapshots>
    </repository>
</repositories>
```

## 4.连接工厂设置

要使用 R2DBC 访问数据库，我们需要做的第一件事是创建一个ConnectionFactory 对象，它起着与 JDBC 的DataSource 类似的作用。创建ConnectionFactory最直接的方法是通过ConnectionFactories类。

此类具有采用ConnectionFactoryOptions对象并返回ConnectionFactory 的静态方法。 由于我们只需要我们的 ConnectionFactory的一个实例，让我们创建一个@Bean，我们以后可以在任何需要的地方通过注入使用它：

```java
@Bean
public ConnectionFactory connectionFactory(R2DBCConfigurationProperties properties) {
    ConnectionFactoryOptions baseOptions = ConnectionFactoryOptions.parse(properties.getUrl());
    Builder ob = ConnectionFactoryOptions.builder().from(baseOptions);
    if (!StringUtil.isNullOrEmpty(properties.getUser())) {
        ob = ob.option(USER, properties.getUser());
    }
    if (!StringUtil.isNullOrEmpty(properties.getPassword())) {
        ob = ob.option(PASSWORD, properties.getPassword());
    }        
    return ConnectionFactories.get(ob.build());    
}

```

在这里，我们采用从装饰有@ConfigurationProperties注解的辅助类接收的选项，并填充我们的ConnectionFactoryOptions实例。为了填充它，R2DBC 实现了一个构建器模式，其中包含一个带有选项和值的选项 方法。

R2DBC 定义了许多众所周知的选项，例如 我们在上面使用的USERNAME 和 PASSWORD 。设置这些选项的另一种方法是将连接字符串传递给ConnectionFactoryOptions类的parse()方法 。

以下是典型 R2DBC 连接 URL 的示例：

```bash
r2dbc:h2:mem://./testdb
```

让我们把这个字符串分解成它的组件：

-   r2dbc：R2DBC URL 的固定方案标识符——另一个有效方案是rd2bcs，用于 SSL 安全连接
-   h2：用于定位适当连接工厂的驱动程序标识符
-   mem：特定于驱动程序的协议——在我们的例子中，这对应于一个内存数据库
-   //./testdb：特定于驱动程序的字符串，通常包含主机、数据库和任何其他选项。

准备好选项后，我们将其传递给get()静态工厂方法以创建我们的ConnectionFactory bean。

## 5. 执行语句

与 JDBC 类似，使用 R2DBC 主要是将 SQL 语句发送到数据库并处理结果集。但是，由于 R2DBC 是一种反应式 API，因此它在很大程度上依赖于反应式流类型，例如Publisher 和 Subscriber。

直接使用这些类型有点麻烦，所以我们将使用项目反应器的类型，如Mono 和 Flux来帮助我们编写更清晰、更简洁的代码。

在接下来的部分中，我们将了解如何通过为简单的Account类创建反应式 DAO 类来实现与数据库相关的任务。这个类只包含三个属性，并且在我们的数据库中有一个对应的表：

```java
public class Account {
    private Long id;
    private String iban;
    private BigDecimal balance;
    // ... getters and setters omitted
}
```

### 5.1. 建立联系

在我们可以向数据库发送任何语句之前，我们需要一个Connection实例。我们已经了解了如何创建ConnectionFactory，因此我们将使用它来获取Connection也就不足为奇了。我们必须记住的是，现在我们得到的 不是常规的Connection ，而是单个 Connection的Publisher 。

我们的ReactiveAccountDao是一个常规的 Spring @Component，它通过构造函数注入获取它的ConnectionFactory，因此它很容易在处理程序方法中使用。

让我们看一下findById()方法的前几行，看看如何检索和开始使用 Connection：

```java
public Mono<Account>> findById(Long id) {         
    return Mono.from(connectionFactory.create())
      .flatMap(c ->
          // use the connection
      )
      // ... downstream processing omitted
}
```

在这里，我们将从ConnectionFactory返回的Publisher调整为 Mono，它是我们事件流的初始源。

### 5.1. 准备和提交报表

现在我们有了一个 Connection，让我们用它来创建一个Statement并将一个参数绑定到它：

```java
.flatMap( c -> 
    Mono.from(c.createStatement("select id,iban,balance from Account where id = $1")
      .bind("$1", id)
      .execute())
      .doFinally((st) -> close(c))
 )

```

Connection的方法createStatement接受一个 SQL 查询字符串，它可以有选择地具有绑定占位符——[在规范](https://r2dbc.io/spec/0.8.0.M8/spec/html/#statements.parameterized)中称为“标记” 。

这里有几点值得注意：首先，createStatement是一个同步操作，它允许我们使用流畅的风格将值绑定到返回的 Statement 上； 其次，也是非常重要的一点，占位符/标记语法是特定于供应商的！

在这个例子中，我们使用了 H2 的特定语法，它使用$n来标记参数。其他供应商可能使用不同的语法，例如:param、 @Pn或其他一些约定。这是我们在将遗留代码迁移到这个新 API 时必须注意的一个重要方面。

由于流畅的 API 模式和简化的输入，绑定过程本身非常简单：只有一个重载的bind()方法负责所有类型转换——当然要遵守数据库规则。

传递给bind() 的第一个参数可以是一个从零开始的序号，对应于标记在语句中的位置，也可以是带有实际标记的字符串。

一旦我们为所有参数设置了值，我们就调用execute()，它返回一个Result 对象的发布 者 ，我们再次将其包装到 Mono 中以进行进一步处理。我们将一个doFinally()处理程序附加到此 Mono ，以确保无论流处理是否正常完成，我们都会关闭连接。

### 5.2. 处理结果

我们管道中的下一步负责处理Result对象并生成ResponseEntity< Account>实例流。

因为我们知道只能有一个具有给定id的实例，所以我们实际上会返回一个 Mono流。实际的转换发生在传递给 接收到的Result的map()方法的 函数内部：

```java
.map(result -> result.map((row, meta) -> 
    new Account(row.get("id", Long.class),
      row.get("iban", String.class),
      row.get("balance", BigDecimal.class))))
.flatMap(p -> Mono.from(p));

```

结果的map()方法需要一个带有两个参数的函数。第一个是一个 Row对象，我们用它来收集每一列的值并填充一个 Account 实例。第二个meta是一个 RowMetadata 对象，它包含有关当前行的信息，例如列名称和类型。

我们管道中之前的map()调用解析为Mono<Producer<Account>> ，但我们需要从此方法返回Mono<Account> 。为了解决这个问题，我们添加了最后一个flatMap()步骤，它将Producer调整为 Mono。

### 5.3. 批量报表

R2DBC 还支持语句批处理的创建和执行，这允许在单个execute() 调用中执行多个 SQL 语句。与常规语句相比，批处理语句不支持绑定，主要用于ETL作业等场景的性能考虑。

我们的示例项目使用一批语句创建Account表并向其中插入一些测试数据：

```java
@Bean
public CommandLineRunner initDatabase(ConnectionFactory cf) {
    return (args) ->
      Flux.from(cf.create())
        .flatMap(c -> 
            Flux.from(c.createBatch()
              .add("drop table if exists Account")
              .add("create table Account(" +
                "id IDENTITY(1,1)," +
                "iban varchar(80) not null," +
                "balance DECIMAL(18,2) not null)")
              .add("insert into Account(iban,balance)" +
                "values('BR430120980198201982',100.00)")
              .add("insert into Account(iban,balance)" +
                "values('BR430120998729871000',250.00)")
              .execute())
            .doFinally((st) -> c.close())
          )
        .log()
        .blockLast();
}
```

在这里，我们使用 从 createBatch()返回的Batch 并添加一些 SQL 语句。然后，我们使用Statement接口中可用的相同execute()方法 发送这些语句以供执行 。

在这种特殊情况下，我们对任何结果都不感兴趣——只是语句都执行得很好。如果我们需要任何生成的结果，我们所要做的就是在此流中添加一个下游步骤来处理发出的Result对象。

## 6. 交易

我们将在本教程中介绍的最后一个主题是交易。正如我们现在所期望的那样，我们像在 JDBC 中一样管理事务，也就是说，通过使用Connection 对象中可用的方法。

和以前一样，主要区别在于现在所有与事务相关的方法都是异步的，返回一个Publisher，我们必须在适当的时候将其添加到我们的流中。

我们的示例项目在其 createAccount() 方法的实现中使用了一个事务：

```java
public Mono<Account> createAccount(Account account) {    
    return Mono.from(connectionFactory.create())
      .flatMap(c -> Mono.from(c.beginTransaction())
        .then(Mono.from(c.createStatement("insert into Account(iban,balance) values($1,$2)")
          .bind("$1", account.getIban())
          .bind("$2", account.getBalance())
          .returnGeneratedValues("id")
          .execute()))
        .map(result -> result.map((row, meta) -> 
            new Account(row.get("id", Long.class),
              account.getIban(),
              account.getBalance())))
        .flatMap(pub -> Mono.from(pub))
        .delayUntil(r -> c.commitTransaction())
        .doFinally((st) -> c.close()));   
}
```

这里，我们在两点添加了交易相关的调用。首先，在从数据库获得新连接后，我们立即调用beginTransactionMethod()。一旦我们知道事务已成功启动，我们就准备并执行插入语句。

这次我们还使用了 returnGeneratedValues()方法来指示数据库返回为这个新 帐户生成的标识值。R2DBC 在Result 中返回这些值，其中包含一行所有生成的值，我们用它来创建Account实例。

同样，我们需要将传入的Mono<Publisher<Account>>适配为Mono<Account>，因此我们添加一个flatMap()来解决这个问题。 接下来，我们在delayUntil() 步骤中提交事务。我们需要这个，因为我们想确保返回的 帐户 已经提交给数据库。

最后，我们将一个 doFinally步骤附加到此管道， 当使用完返回的 Mono的所有事件时关闭连接。

## 7. DAO 用法示例

现在我们有了一个响应式 DAO，让我们用它来创建一个简单的[Spring WebFlux](https://www.baeldung.com/spring-webflux)应用程序来展示如何在典型应用程序中使用它。由于该框架已经支持反应式构造，因此这成为一项微不足道的任务。例如，让我们看一下GET方法的实现：

```java
@RestController
public class AccountResource {
    private final ReactiveAccountDao accountDao;

    public AccountResource(ReactiveAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @GetMapping("/accounts/{id}")
    public Mono<ResponseEntity<Account>> getAccount(@PathVariable("id") Long id) {
        return accountDao.findById(id)
          .map(acc -> new ResponseEntity<>(acc, HttpStatus.OK))
          .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)));
    }
    // ... other methods omitted
}
```

在这里，我们使用我们的 DAO 返回的 Mono来构造一个具有适当状态代码的ResponseEntity 。我们这样做只是因为 当没有具有给定 ID 的帐户时我们想要一个NOT_FOUND (404)状态代码。 

## 八. 总结

在本文中，我们介绍了使用 R2DBC 进行反应式数据库访问的基础知识。尽管处于起步阶段，该项目正在迅速发展，目标是在 2020 年初的某个时间发布。

与肯定不会成为Java12 一部分的 ADBA 相比，R2DBC 似乎更有前途，并且已经为一些流行的数据库提供了驱动程序——Oracle 是一个值得注意的缺席者。