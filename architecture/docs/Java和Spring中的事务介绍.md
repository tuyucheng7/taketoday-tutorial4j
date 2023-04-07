## 1. 概述

在本教程中，我们将了解 Java 中事务的含义。从而我们将了解如何执行资源本地事务和全局事务。这也将使我们能够探索在 Java 和 Spring 中管理事务的不同方式。

## 2. 什么是交易？

Java 中的事务，通常指的是[必须全部成功完成的一系列操作](https://www.baeldung.com/transactions-intro)。因此，如果一个或多个操作失败，则所有其他操作都必须退出，同时保持应用程序的状态不变。这是确保应用程序状态的完整性永远不会受到损害所必需的。

此外，这些事务可能涉及一个或多个资源，如数据库、消息队列，从而产生不同的方式来执行事务下的操作。这些包括使用单个资源执行资源本地事务。或者，多个资源可以参与全局事务。

## 3.资源本地交易

我们将首先探讨如何在处理单个资源时使用 Java 中的事务。在这里，我们可能有多个单独的操作，我们使用数据库等资源执行这些操作。但是，我们可能希望它们作为一个统一的整体发生，就像在一个不可分割的工作单元中一样。换句话说，我们希望这些操作在单个事务下发生。

在 Java 中，我们有多种方法来访问和操作数据库等资源。因此，我们处理交易的方式也不尽相同。在本节中，我们将了解如何在 Java 中经常使用的这些库中使用事务。

### 3.1. JDBC

[Java Database Connectivity (JDBC)](https://docs.oracle.com/javase/tutorial/jdbc/overview/index.html)是Java 中的 API，它定义了如何访问 Java 中的数据库。不同的数据库供应商提供 JDBC 驱动程序以与供应商无关的方式连接到数据库。因此，我们从驱动程序中检索一个Connection以对数据库执行不同的操作：

[![JDBC 体系结构](https://www.baeldung.com/wp-content/uploads/2020/08/JDBC-Architecture.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/JDBC-Architecture.jpg)

JDBC 为我们提供了在事务下执行语句的选项。Connection的默认行为是auto-commit。澄清一下，这意味着每条语句都被视为一个事务，并在执行后立即自动提交。

但是，如果我们希望在单个事务中捆绑多个语句，这也是可以实现的：

```java
Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
try {
    connection.setAutoCommit(false);
    PreparedStatement firstStatement = connection .prepareStatement("firstQuery");
    firstStatement.executeUpdate();
    PreparedStatement secondStatement = connection .prepareStatement("secondQuery");
    secondStatement.executeUpdate();
    connection.commit();
} catch (Exception e) {
    connection.rollback();
}复制
```

在这里，我们禁用了Connection的自动提交模式。因此，我们可以手动定义事务边界并执行提交或回滚。JDBC 还允许我们设置一个保存点，使我们能够更好地控制回滚量。

### 3.2. JPA

[Java Persistence API (JPA)](https://docs.oracle.com/javaee/6/tutorial/doc/bnbpz.html)是 Java 中的一个规范，可用于弥合面向对象领域模型和关系数据库系统之间的差距。因此，Hibernate、EclipseLink 和 iBatis 等第三方提供了多种 JPA 实现。

在 JPA 中，我们可以将常规类定义为为它们提供持久标识的实体。EntityManager类提供了在持久性上下文中处理多个实体所必需的接口。持久化上下文可以被认为是管理实体的一级缓存：

[![JPA架构](https://www.baeldung.com/wp-content/uploads/2020/08/JPA-Architecture.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/JPA-Architecture.jpg)

JPA架构

这里的持久性上下文可以有两种类型，事务范围或扩展范围。事务范围的持久性上下文绑定到单个事务。而扩展范围的持久性上下文可以跨越多个事务。持久化上下文的默认范围是事务范围。

让我们看看如何创建 EntityManager并手动定义事务边界：

```java
EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-example");
EntityManager entityManager = entityManagerFactory.createEntityManager();
try {
    entityManager.getTransaction().begin();
    entityManager.persist(firstEntity);
    entityManager.persist(secondEntity);
    entityManager.getTransaction().commit();
} catch (Exception e) {
    entityManager.getTransaction().rollback();
}复制
```

在这里，我们在事务范围持久性上下文的上下文中从EntityManagerFactory创建一个EntityManager 。然后我们使用begin、commit和rollback方法定义事务边界。

### 3.3. ETC

[Java 消息服务 (JMS)](https://docs.oracle.com/javaee/6/tutorial/doc/bncdq.html)是 Java 中的一种规范，它允许应用程序使用消息进行异步通信。API 允许我们创建、发送、接收和读取来自队列或主题的消息。有多种消息服务符合 JMS 规范，包括 OpenMQ 和 ActiveMQ。

JMS API 支持在单个事务中捆绑多个发送或接收操作。但是，根据基于消息的集成架构的性质，消息的生产和消费不能属于同一事务。事务的范围仍然在客户端和 JMS 提供者之间：

[![JMS 体系结构](https://www.baeldung.com/wp-content/uploads/2020/08/JMS-Architecture.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/JMS-Architecture.jpg)
JMS 允许我们从从供应商特定的ConnectionFactory获得的Connection 创建Session。我们可以选择创建是否进行事务处理的会话。对于非事务Session，我们也可以进一步定义合适的确认方式。

让我们看看我们如何创建一个事务会话来在事务下发送多条消息：

```java
ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTION_URL);
Connection connection = = connectionFactory.createConnection();
connection.start();
try {
    Session session = connection.createSession(true, 0);
    Destination = destination = session.createTopic("TEST.FOO");
    MessageProducer producer = session.createProducer(destination);
    producer.send(firstMessage);
    producer.send(secondMessage);
    session.commit();
} catch (Exception e) {
    session.rollback();
}复制
```

在这里，我们正在为主题类型的Destination创建一个MessageProducer 。我们从之前创建的Session中获取Destination 。我们进一步使用Session使用方法commit和rollback来定义事务边界。

## 4. 全球交易

正如我们所见，资源本地事务允许我们在单个资源内作为一个统一的整体执行多个操作。但是，我们经常处理跨越多个资源的操作。例如，在两个不同的数据库或一个数据库和一个消息队列中进行操作。在这里，资源内的本地事务支持对我们来说是不够的。

在这些场景中，我们需要的是一种全局机制来划分跨多个参与资源的事务。这通常被称为分布式事务，并且已经提出了一些规范来有效地处理它们。

XA规范就是这样一种规范，它定义了一个事务管理器来控制跨多个资源的事务。Java通过组件JTA和JTS对符合XA规范的分布式事务的支持已经相当成熟。

### 4.1. JTA

[Java Transaction API (JTA)](https://www.oracle.com/technetwork/java/javaee/jta/index.html) 是在 Java Community Process 下开发的 Java Enterprise Edition API。它 使 Java 应用程序和应用程序服务器能够跨 XA 资源执行分布式事务。JTA 是围绕 XA 架构建模的，利用了两阶段提交。

JTA 指定了事务管理器和分布式事务中其他各方之间的标准 Java 接口：

[![截图-2020-04-25-at-06.53.12](https://www.baeldung.com/wp-content/uploads/2020/08/Screenshot-2020-04-25-at-06.53.12.png)](https://www.baeldung.com/wp-content/uploads/2020/08/Screenshot-2020-04-25-at-06.53.12.png)

让我们了解上面突出显示的一些关键接口：

-   TransactionManager ： 一个允许应用程序服务器划分和控制事务的接口
-   UserTransaction ： 此接口允许应用程序显式划分和控制事务
-   XAResource ： 此接口的目的是允许事务管理器与资源管理器一起工作以获取符合 XA 的资源

### 4.2. JTS

[Java Transaction Service (JTS)](https://download.oracle.com/otndocs/jcp/7309-jts-1.0-spec-oth-JSpec/) 是 一种用于构建映射到 OMG OTS 规范的事务管理器的规范。JTS 使用标准的 CORBA ORB/TS 接口和因特网 ORB 间协议 (IIOP) 在 JTS 事务管理器之间传播事务上下文。

在较高级别，它支持 Java Transaction API (JTA)。JTS 事务管理器为分布式事务中涉及的各方提供事务服务：

[![截图-2020-04-25-at-06.53.53](https://www.baeldung.com/wp-content/uploads/2020/08/Screenshot-2020-04-25-at-06.53.53.png)](https://www.baeldung.com/wp-content/uploads/2020/08/Screenshot-2020-04-25-at-06.53.53.png)

JTS 为应用程序提供的服务在很大程度上是透明的，因此我们甚至可能不会在应用程序体系结构中注意到它们。JTS 是围绕应用程序服务器构建的，它从应用程序中抽象出所有事务语义。

## 5. JTA事务管理

现在是了解如何使用 JTA 管理分布式事务的时候了。分布式事务不是微不足道的解决方案，因此也具有成本影响。此外，我们可以从多个选项中选择将 JTA 包含在我们的应用程序中。因此，我们的选择必须考虑到整体应用程序架构和愿望。

### 5.1. 应用服务器中的 JTA

正如我们之前看到的，JTA 体系结构依赖于应用服务器来促进许多与事务相关的操作。它依赖于服务器提供的关键服务之一是通过 JNDI 的命名服务。这是像数据源这样的 XA 资源被绑定和检索的地方。

除此之外，我们还可以选择如何管理应用程序中的事务边界。这在 Java 应用程序服务器中产生了两种类型的事务：

-   Container-managed Transaction：顾名思义，这里的事务边界是由应用服务器设置的。这简化了 Enterprise Java Beans (EJB) 的开发，因为它不包含与事务划分相关的语句，而仅依赖于容器来执行此操作。然而，这并没有为应用程序提供足够的灵活性。
-   Bean 管理的事务：与容器管理的事务相反，在 Bean 管理的事务中，EJB 包含定义事务划分的显式语句。这在标记事务边界时为应用程序提供了精确控制，尽管代价是更复杂。

在应用程序服务器上下文中执行事务的主要缺点之一是应用程序与服务器紧密耦合。这对应用程序的可测试性、可管理性和可移植性有影响。这在微服务架构中更为深刻，在微服务架构中，重点更多地放在开发服务器中立的应用程序上。

### 5.2. JTA独立

我们在上一节中讨论的问题为创建不依赖于应用程序服务器的分布式事务解决方案提供了巨大的动力。在这方面，我们有多种选择，例如使用 Spring 的事务支持或使用像 Atomikos 这样的事务管理器。

让我们看看我们如何[使用像 Atomikos 这样的事务管理器](https://www.baeldung.com/java-atomikos)来促进具有数据库和消息队列的分布式事务。分布式事务的关键方面之一是使用事务监视器登记和取消列出参与的资源。Atomikos 为我们处理了这件事。我们所要做的就是使用 Atomikos 提供的抽象：

```java
AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
atomikosDataSourceBean.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
DataSource dataSource = atomikosDataSourceBean;复制
```

在这里，我们正在创建AtomikosDataSourceBean的实例并注册特定于供应商的XADataSource。从这里开始，我们可以像使用任何其他数据源一样继续使用它，并获得分布式事务的好处。

类似地，我们有一个消息队列抽象，它负责自动向事务监视器注册特定于供应商的 XA 资源：

```java
AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
atomikosConnectionFactoryBean.setXaConnectionFactory(new ActiveMQXAConnectionFactory());
ConnectionFactory connectionFactory = atomikosConnectionFactoryBean;复制
```

在这里，我们正在创建AtomikosConnectionFactoryBean的实例并从支持 XA 的 JMS 供应商注册XAConnectionFactory 。在此之后，我们可以继续将其用作常规ConnectionFactory。

现在，Atomikos 为我们提供了将所有内容组合在一起的最后一块拼图，即UserTransaction的一个实例：

```java
UserTransaction userTransaction = new UserTransactionImp();复制
```

现在，我们准备创建一个跨数据库和消息队列的分布式事务应用程序：

```java
try {
    userTransaction.begin();

    java.sql.Connection dbConnection = dataSource.getConnection();
    PreparedStatement preparedStatement = dbConnection.prepareStatement(SQL_INSERT);
    preparedStatement.executeUpdate();

    javax.jms.Connection mbConnection = connectionFactory.createConnection();
    Session session = mbConnection.createSession(true, 0);
    Destination destination = session.createTopic("TEST.FOO");
    MessageProducer producer = session.createProducer(destination);
    producer.send(MESSAGE);

    userTransaction.commit();
} catch (Exception e) {
    userTransaction.rollback();
}复制
```

在这里，我们使用类UserTransaction中的方法begin和commit来划分事务边界。这包括在数据库中保存记录以及将消息发布到消息队列。

## 6. Spring 的事务支持

我们已经看到处理事务是一项相当复杂的任务，其中包括大量样板代码和配置。此外，每个资源都有自己处理本地事务的方式。在 Java 中，JTA 将我们从这些变体中抽象出来，但进一步带来了特定于提供者的细节和应用程序服务器的复杂性。

Spring 平台为我们提供了一种更简洁的处理事务的方式，包括Java 中的资源本地事务和全局事务。这与 Spring 的其他好处一起为使用 Spring 处理事务创建了一个令人信服的案例。此外，使用 Spring 配置和切换事务管理器非常容易，它可以是服务器提供的，也可以是独立的。

Spring通过为具有事务代码的方法创建代理，为我们提供了这种无缝抽象。代理在TransactionManager的帮助下代表代码管理事务状态：
![春季交易](https://www.baeldung.com/wp-content/uploads/2020/08/Spring-Transactions.jpg)
这里的中央接口是PlatformTransactionManager，它有许多不同的可用实现。它提供了对 JDBC (DataSource)、JMS、JPA、JTA 和许多其他资源的抽象。

### 6.1. 配置

让我们看看我们如何配置Spring 以使用 Atomikos 作为事务管理器并为 JPA 和 JMS 提供事务支持。我们将从定义JTA 类型的PlatformTransactionManager开始：

```java
@Bean
public PlatformTransactionManager platformTransactionManager() throws Throwable {
    return new JtaTransactionManager(
                userTransaction(), transactionManager());
}复制
```

在这里，我们向JTATransactionManager提供UserTransaction和TransactionManager的实例。这些实例由 Atomikos 等事务管理器库提供：

```java
@Bean
public UserTransaction userTransaction() {
    return new UserTransactionImp();
}

@Bean(initMethod = "init", destroyMethod = "close")
public TransactionManager transactionManager() {
    return new UserTransactionManager();
}复制
```

UserTransactionImp和UserTransactionManager类由 Atomikos 在这里提供。

进一步，我们需要定义JmsTemplete，它是Spring允许同步JMS访问的核心类：

```java
@Bean
public JmsTemplate jmsTemplate() throws Throwable {
    return new JmsTemplate(connectionFactory());
}复制
```

这里，ConnectionFactory由 Atomikos 提供，它为它提供的Connection启用分布式事务：

```java
@Bean(initMethod = "init", destroyMethod = "close")
public ConnectionFactory connectionFactory() {
    ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new 
ActiveMQXAConnectionFactory();
    activeMQXAConnectionFactory.setBrokerURL("tcp://localhost:61616");
    AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
    atomikosConnectionFactoryBean.setUniqueResourceName("xamq");
    atomikosConnectionFactoryBean.setLocalTransactionMode(false);
atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory);
    return atomikosConnectionFactoryBean;
}复制
```

因此，正如我们所见，我们在这里用AtomikosConnectionFactoryBean包装了特定于 JMS 提供者的XAConnectionFactory。

接下来，我们需要定义一个AbstractEntityManagerFactoryBean，它负责在 Spring 中创建 JPA EntityManagerFactory bean：

```java
@Bean
public LocalContainerEntityManagerFactoryBean entityManager() throws SQLException {
    LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
    entityManager.setDataSource(dataSource());
    Properties properties = new Properties();
    properties.setProperty( "javax.persistence.transactionType", "jta");
    entityManager.setJpaProperties(properties);
    return entityManager;
}复制
```

和之前一样，我们在LocalContainerEntityManagerFactoryBean中设置的DataSource是由 Atomikos 提供的，启用了分布式事务：

```java
@Bean(initMethod = "init", destroyMethod = "close")
public DataSource dataSource() throws SQLException {
    MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
    mysqlXaDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test");
    AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
    xaDataSource.setXaDataSource(mysqlXaDataSource);
    xaDataSource.setUniqueResourceName("xads");
    return xaDataSource;
}复制
```

同样，我们将特定于提供者的XADataSource包装在AtomikosDataSourceBean中。

### 6.2. 交易管理

看完了上一节的所有配置，我们一定感到不知所措！毕竟，我们甚至可能会质疑使用 Spring 的好处。但请记住，所有这些配置使我们能够从大多数特定于提供者的样板中抽象出来，我们的实际应用程序代码根本不需要知道这一点。

所以，现在我们准备探索如何在我们打算更新数据库和发布消息的 Spring 中使用事务。Spring 为我们提供了两种实现方式，各有好处，可供选择。让我们了解如何使用它们：

-   声明性支持

在 Spring 中使用事务的最简单方法是使用声明式支持。在这里，我们有一个方便的注释可用于方法甚至类。这只是为我们的代码启用了全局事务：

```java
@PersistenceContext
EntityManager entityManager;

@Autowired
JmsTemplate jmsTemplate;

@Transactional(propagation = Propagation.REQUIRED)
public void process(ENTITY, MESSAGE) {
   entityManager.persist(ENTITY);
   jmsTemplate.convertAndSend(DESTINATION, MESSAGE);
}复制
```

上面的简单代码足以允许在 JTA 事务中的数据库中进行保存操作，并在消息队列中进行发布操作。

-   程序化支持

虽然声明式支持非常优雅和简单，但它并没有为我们提供更精确地控制事务边界的好处。因此，如果我们确实需要实现这一点，Spring 会提供编程支持来划定事务边界：

```java
@Autowired
private PlatformTransactionManager transactionManager;

public void process(ENTITY, MESSAGE) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.executeWithoutResult(status -> {
        entityManager.persist(ENTITY);
        jmsTemplate.convertAndSend(DESTINATION, MESSAGE);
    });
}复制
```

因此，正如我们所见，我们必须使用可用的PlatformTransactionManager创建一个TransactionTemplate。然后我们可以使用TransactionTemplete来处理全局事务中的一堆语句。

## 7.事后思考

正如我们所见，处理事务，尤其是那些跨越多个资源的事务是复杂的。此外，事务本质上是阻塞的，这对应用程序的延迟和吞吐量是有害的。此外，使用分布式事务测试和维护代码并不容易，尤其是在事务依赖于底层应用程序服务器的情况下。所以，总而言之，如果可以的话，最好完全避免交易！

但这与现实相去甚远。简而言之，在现实世界的应用程序中，我们确实经常有交易的合理需求。尽管可以重新考虑没有事务的应用程序架构，但这并不总是可能的。因此，在使用 Java 处理事务时，我们必须采用某些最佳实践来改进我们的应用程序：

-   我们应该采用的基本转变之一是使用独立的事务管理器，而不是应用程序服务器提供的事务管理器。仅此一项就可以大大简化我们的应用程序。此外，它非常适合云原生微服务架构。
-   此外，像 Spring 这样的抽象层可以帮助我们包含 JPA 或 JTA 提供程序等提供程序的直接影响。因此，这可以使我们能够在不对我们的业务逻辑产生太大影响的情况下在提供者之间切换。此外，它消除了我们管理事务状态的低级责任。
-   最后，我们在选择代码中的事务边界时应该小心。由于事务是阻塞的，因此最好尽可能限制事务边界。如果有必要，我们应该更喜欢程序化而不是声明式的事务控制。

## 八、总结

总而言之，在本教程中，我们讨论了 Java 上下文中的事务。我们通过 Java 支持不同资源的单个资源本地事务。我们还介绍了在 Java 中实现全局事务的方法。

此外，我们通过不同的方式在 Java 中管理全局事务。此外，我们了解了 Spring 如何让我们更轻松地使用 Java 中的事务。

最后，我们介绍了使用 Java 处理事务时的一些最佳实践。