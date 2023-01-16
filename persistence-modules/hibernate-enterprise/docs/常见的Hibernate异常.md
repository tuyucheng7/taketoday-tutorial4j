## 1. 简介

在本教程中，我们将讨论在使用 Hibernate 时可能遇到的一些常见异常。

我们将回顾它们的目的和一些常见原因。此外，我们将研究他们的解决方案。

## 2. Hibernate 异常概述

在使用 Hibernate 时，许多情况会导致抛出异常。这些可能是映射错误、基础结构问题、SQL 错误、数据完整性违规、会话问题和事务错误。

这些异常主要从[HibernateException](https://docs.jboss.org/hibernate/orm/6.0/javadocs/org/hibernate/HibernateException.html)扩展而来。但是，如果我们使用 Hibernate 作为 JPA 持久性提供者，这些异常可能会被包装到 [PersistenceException](https://docs.oracle.com/javaee/7/api/javax/persistence/PersistenceException.html)中。

这两个基类都从RuntimeException扩展而来。因此，它们都是未经检查的。因此，我们不需要在每个使用它们的地方捕获或声明它们。

此外，其中大部分是不可恢复的。因此，重试该操作无济于事。这意味着我们必须在遇到它们时放弃当前会话。

现在让我们逐一研究，一次一个。

## 3.映射错误

对象关系映射是 Hibernate 的一个主要优点。具体来说，就是将我们从手工编写SQL语句中解放出来。

同时需要我们指定Java对象与数据库表的映射关系。因此，我们使用注解或映射文档来指定它们。这些映射可以手动编码。或者，我们可以使用工具来生成它们。

在指定这些映射时，我们可能会出错。这些可以在映射规范中。或者，Java 对象和相应的数据库表之间可能存在不匹配。

此类映射错误会产生异常。我们在最初的开发过程中经常遇到它们。此外，我们可能会在跨环境迁移更改时遇到它们。

让我们通过一些例子来研究这些错误。

### 3.1. 映射异常

对象关系映射的问题导致 抛出MappingException：

```java
public void whenQueryExecutedWithUnmappedEntity_thenMappingException() {
    thrown.expectCause(isA(MappingException.class));
    thrown.expectMessage("Unknown entity: java.lang.String");

    Session session = sessionFactory.getCurrentSession();
    NativeQuery<String> query = session
      .createNativeQuery("select name from PRODUCT", String.class);
    query.getResultList();
}
```

在上面的代码中， createNativeQuery方法尝试将查询结果映射到指定的Java类型 String。[它使用元模型](https://docs.oracle.com/javaee/6/api/javax/persistence/metamodel/Metamodel.html)中String类的隐式映射 来进行映射。

但是，String类没有指定任何映射。因此，Hibernate 不知道如何将name 列映射到String并抛出异常。

详细分析可能的原因和解决办法，查看 [Hibernate Mapping Exception – Unknown Entity](https://www.baeldung.com/hibernate-mappingexception-unknown-entity)。

同样，其他错误也可能导致此异常：

-   在字段和方法上混合注解
-   未能为@ManyToMany关联指定 @JoinTable
-   映射类的默认构造函数在映射处理过程中抛出异常

此外，MappingException有几个子类可以指示特定的映射问题：

-   AnnotationException – 注解问题
-   DuplicateMappingException –类、表或属性名称的重复映射
-   InvalidMappingException – 映射无效
-   MappingNotFoundException – 找不到映射资源
-   PropertyNotFoundException – 在类中找不到预期的 getter 或 setter 方法

因此， 如果我们遇到这个异常，我们应该首先验证我们的映射。

### 3.2. 注解异常

为了理解 AnnotationException，让我们创建一个在任何字段或属性上都没有标识符注解的实体：

```java
@Entity
public class EntityWithNoId {
    private int id;
    public int getId() {
        return id;
    }

    // standard setter
}
```

因为Hibernate 期望每个实体都有一个[identifier](https://www.baeldung.com/hibernate-identifiers) ，所以当我们使用实体时我们会得到一个 AnnotationException ：

```java
public void givenEntityWithoutId_whenSessionFactoryCreated_thenAnnotationException() {
    thrown.expect(AnnotationException.class);
    thrown.expectMessage("No identifier specified for entity");

    Configuration cfg = getConfiguration();
    cfg.addAnnotatedClass(EntityWithNoId.class);
    cfg.buildSessionFactory();
}
```

此外，其他一些可能的原因是：

-   @GeneratedValue注解中使用的未知序列生成器
-   @Temporal注解与Java8日期/时间类一起使用
-   @ManyToOne或@OneToMany的目标实体丢失或不存在
-   与关系注解@OneToMany或@ManyToMany一起使用的[原始集合类](https://www.baeldung.com/java-diamond-operator)
-   与集合注解一起使用的具体类@OneToMany，@ManyToMany或@ElementCollection因为 Hibernate 期望集合接口

要解决此异常，我们应该首先检查错误消息中提到的特定注解。

### 3.3. 查询语法异常

在深入细节之前，让我们试着理解异常的含义。

QuerySyntaxException，顾名思义，告诉我们指定的查询有无效的语法。

此异常的最典型原因是在 HQL 查询中使用表名而不是类名。

例如，让我们考虑Product实体：

```java
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PRODUCT")
public class Product {

    private int id;
    private String name;
    private String description;

    // Getters and setters
}

```

@Entity表示被注解的类是一个[实体](https://www.baeldung.com/jpa-entities)。它告诉我们这个类代表一个存储在数据库中的表。

通常，表名可能与实体名不同。所以，这就是@Table来拯救的地方。它允许我们指定数据库中表的确切名称。

现在，让我们使用测试用例来举例说明异常：

```java
@Test
public void whenQueryExecutedWithInvalidClassName_thenQuerySyntaxException() {
    thrown.expectCause(isA(QuerySyntaxException.class));
    thrown.expectMessage("PRODUCT is not mapped [from PRODUCT]");

    Session session = sessionFactory.openSession();
    List<Product> result = session.createQuery("from PRODUCT", Product.class)
        .getResultList();
}
```

正如我们所见，Hibernate 因QuerySyntaxException而失败，因为我们在查询中使用了PRODUCT而不是Product 。换句话说，我们必须在查询中使用实体名称而不是表名称。

## 4.模式管理错误

自动数据库模式管理是 Hibernate 的另一个好处。例如，它可以生成 DDL 语句来创建或验证数据库对象。

要使用此功能，我们需要适当地设置hibernate.hbm2ddl.auto 属性。

如果在执行模式管理时出现问题，我们会得到一个异常。让我们检查一下这些错误。

### 4.1. 模式管理异常

执行模式管理时任何与基础架构相关的问题都会导致SchemaManagementException。

为了演示，让我们指示 Hibernate 验证数据库模式：

```java
public void givenMissingTable_whenSchemaValidated_thenSchemaManagementException() {
    thrown.expect(SchemaManagementException.class);
    thrown.expectMessage("Schema-validation: missing table");

    Configuration cfg = getConfiguration();
    cfg.setProperty(AvailableSettings.HBM2DDL_AUTO, "validate");
    cfg.addAnnotatedClass(Product.class);
    cfg.buildSessionFactory();
}
```

由于与Product对应的表 不存在于数据库中，因此我们在构建SessionFactory时遇到模式验证异常。

此外，此异常还有其他可能的情况：

-   无法连接到数据库以执行架构管理任务
-   该架构不存在于数据库中

### 4.2. 命令接受异常

执行与特定模式管理命令对应的 DDL 的任何问题都可能导致CommandAcceptanceException。

例如，让我们在设置 SessionFactory 时指定错误的 方言：

```java
public void whenWrongDialectSpecified_thenCommandAcceptanceException() {
    thrown.expect(SchemaManagementException.class);
        
    thrown.expectCause(isA(CommandAcceptanceException.class));
    thrown.expectMessage("Halting on error : Error executing DDL");

    Configuration cfg = getConfiguration();
    cfg.setProperty(AvailableSettings.DIALECT,
      "org.hibernate.dialect.MySQLDialect");
    cfg.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
    cfg.setProperty(AvailableSettings.HBM2DDL_HALT_ON_ERROR,"true");
    cfg.getProperties()
      .put(AvailableSettings.HBM2DDL_HALT_ON_ERROR, true);

    cfg.addAnnotatedClass(Product.class);
    cfg.buildSessionFactory();
}
```

在这里，我们指定了错误的方言： MySQLDialect。 此外，我们正在指示 Hibernate 更新模式对象。因此，由 Hibernate 执行的更新 H2 数据库的 DDL 语句将失败，我们将得到一个异常。

默认情况下，Hibernate 静默记录此异常并继续。 当我们稍后使用SessionFactory 时， 我们得到异常。

为确保针对此错误抛出异常，我们已将属性 HBM2DDL_HALT_ON_ERROR设置为true。

同样，这些是导致此错误的其他一些常见原因：

-   映射和数据库之间的列名不匹配
-   两个类映射到同一个表
-   用于类或表的名称是数据库中的保留字，例如USER
-   用于连接数据库的用户没有所需的权限

## 5. SQL 执行错误

当我们使用 Hibernate 插入、更新、删除或查询数据时，它会使用 JDBC 对数据库执行 DML 语句。如果操作导致错误或警告，此 API 会引发SQLException 。

Hibernate 将此异常转换为JDBCException或其合适的子类之一：

-   ConstraintViolationException异常
-   数据异常
-   JDBC连接异常
-   锁定获取异常
-   悲观锁异常
-   查询超时异常
-   SQL语法异常
-   通用 JDBCException

让我们讨论常见的错误。

### 5.1. JDBC异常

JDBCException总是由特定的 SQL 语句引起的。我们可以调用getSQL方法来获取有问题的 SQL 语句。

此外，我们可以使用getSQLException方法检索底层的SQLException。

### 5.2. SQL语法异常

SQLGrammarException表示发送到数据库的 SQL 无效。这可能是由于语法错误或无效的对象引用。

例如，缺少表可能会在查询数据时导致此错误：

```java
public void givenMissingTable_whenQueryExecuted_thenSQLGrammarException() {
    thrown.expect(isA(PersistenceException.class));
    thrown.expectCause(isA(SQLGrammarException.class));
    thrown.expectMessage("SQLGrammarException: could not prepare statement");

    Session session = sessionFactory.getCurrentSession();
    NativeQuery<Product> query = session.createNativeQuery(
      "select  from NON_EXISTING_TABLE", Product.class);
    query.getResultList();
}
```

此外，如果表丢失，我们在保存数据时会遇到此错误：

```java
public void givenMissingTable_whenEntitySaved_thenSQLGrammarException() {
    thrown.expect(isA(PersistenceException.class));
    thrown.expectCause(isA(SQLGrammarException.class));
    thrown
      .expectMessage("SQLGrammarException: could not prepare statement");

    Configuration cfg = getConfiguration();
    cfg.addAnnotatedClass(Product.class);

    SessionFactory sessionFactory = cfg.buildSessionFactory();
    Session session = null;
    Transaction transaction = null;
    try {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        Product product = new Product();
        product.setId(1);
        product.setName("Product 1");
        session.save(product);
        transaction.commit();
    } catch (Exception e) {
        rollbackTransactionQuietly(transaction);
        throw (e);
    } finally {
        closeSessionQuietly(session);
        closeSessionFactoryQuietly(sessionFactory);
    }
}
```

其他一些可能的原因是：

-   使用的命名策略没有将类映射到正确的表
-   @JoinColumn中指定的列不存在

### 5.3. ConstraintViolationException异常

ConstraintViolationException表示请求的 DML 操作导致违反完整性约束。我们可以通过调用getConstraintName方法来获取这个约束的名称。

此异常的一个常见原因是试图保存重复记录：

```java
public void whenDuplicateIdSaved_thenConstraintViolationException() {
    thrown.expect(isA(PersistenceException.class));
    thrown.expectCause(isA(ConstraintViolationException.class));
    thrown.expectMessage(
      "ConstraintViolationException: could not execute statement");

    Session session = null;
    Transaction transaction = null;

    for (int i = 1; i <= 2; i++) {
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Product product = new Product();
            product.setId(1);
            product.setName("Product " + i);
            session.save(product);
            transaction.commit();
        } catch (Exception e) {
            rollbackTransactionQuietly(transaction);
            throw (e);
        } finally {
            closeSessionQuietly(session);
        }
    }
}
```

此外，将空值保存到数据库中的NOT NULL列也会引发此错误。

为了解决这个错误，我们应该在业务层执行所有验证。此外，不应使用数据库约束来进行应用程序验证。

### 5.4. 数据异常

DataException表示对 SQL 语句的评估导致某些非法操作、类型不匹配或不正确的基数。

例如，对数字列使用字符数据会导致此错误：

```java
public void givenQueryWithDataTypeMismatch_WhenQueryExecuted_thenDataException() {
    thrown.expectCause(isA(DataException.class));
    thrown.expectMessage(
      "org.hibernate.exception.DataException: could not prepare statement");

    Session session = sessionFactory.getCurrentSession();
    NativeQuery<Product> query = session.createNativeQuery(
      "select  from PRODUCT where id='wrongTypeId'", Product.class);
    query.getResultList();
}
```

要修复此错误，我们应该确保应用程序代码和数据库之间的数据类型和长度匹配。

### 5.5. JDBC连接异常

JDBCConectionException表示与数据库通信时出现问题。

例如，数据库或网络出现故障会导致抛出此异常。

此外，不正确的数据库设置可能会导致此异常。一种这样的情况是数据库连接被服务器关闭，因为它长时间处于空闲状态。如果我们使用[连接池](https://www.baeldung.com/java-connection-pooling)并且池上的空闲超时设置大于数据库中的连接超时值，就会发生这种情况。

要解决这个问题，首先要确保数据库主机存在并且是up。然后，我们应该验证是否为数据库连接使用了正确的身份验证。最后，我们应该检查连接池的超时值是否设置正确。

### 5.6. 查询超时异常

当数据库查询超时时，我们会得到这个异常。我们也可以看到它是由于其他错误导致的，例如表空间已满。

这是为数不多的可恢复错误之一，这意味着我们可以在同一事务中重试该语句。

要解决此问题，我们可以通过多种方式增加长时间运行的查询的查询超时 ：

-   在@NamedQuery或@NamedNativeQuery注解中设置[超时](https://docs.jboss.org/hibernate/orm/6.0/javadocs/org/hibernate/annotations/NamedQuery.html#timeout--)元素
-   调用Query接口的[setHint方法](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/Query.html#setHint-java.lang.String-java.lang.Object-) 
-   调用Transaction接口的 [setTimeout方法](http://docs.jboss.org/hibernate/orm/6.0/javadocs/org/hibernate/Transaction.html#setTimeout-int-)
-   调用Query接口的[setTimeout方法](http://docs.jboss.org/hibernate/orm/6.0/javadocs/org/hibernate/query/Query.html#setTimeout-int-)

## 6. 会话状态相关的错误

现在让我们看看由于 Hibernate 会话使用错误引起的错误。

### 6.1. 非唯一对象异常

Hibernate 不允许在一个会话中有两个具有相同标识符的对象。

如果我们尝试在单个会话中将同一Java类的两个实例与相同的标识符相关联，我们将得到NonUniqueObjectException。我们可以通过调用 getEntityName()和getIdentifier()方法来获取实体的名称和标识符。

要重现此错误，让我们尝试使用会话保存两个具有相同 ID的Product实例：

```java
public void 
givenSessionContainingAnId_whenIdAssociatedAgain_thenNonUniqueObjectException() {
    thrown.expect(isA(NonUniqueObjectException.class));
    thrown.expectMessage(
      "A different object with the same identifier value was already associated with the session");

    Session session = null;
    Transaction transaction = null;

    try {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        Product product = new Product();
        product.setId(1);
        product.setName("Product 1");
        session.save(product);

        product = new Product();
        product.setId(1);
        product.setName("Product 2");
        session.save(product);

        transaction.commit();
    } catch (Exception e) {
        rollbackTransactionQuietly(transaction);
        throw (e);
    } finally {
        closeSessionQuietly(session);
    }
}
```

正如预期的那样，我们将得到 NonUniqueObjectException 。

通过调用update方法将已分离的对象重新附加到会话中时，经常会出现此异常。如果会话加载了另一个具有相同标识符的实例，则会出现此错误。为了解决这个问题，我们可以使用merge方法重新附加分离的对象。

### 6.2. StaleStateException异常

当版本号或时间戳检查失败时，Hibernate 会抛出StaleStateException 。它表示会话包含过时的数据。

有时这会被包装到 OptimisticLockException中。

此错误通常在使用带版本控制的长时间运行的事务时发生。

此外，如果相应的数据库行不存在，尝试更新或删除实体时也会发生这种情况：

```java
public void whenUpdatingNonExistingObject_thenStaleStateException() {
    thrown.expect(isA(OptimisticLockException.class));
    thrown.expectMessage(
      "Batch update returned unexpected row count from update");
    thrown.expectCause(isA(StaleStateException.class));

    Session session = null;
    Transaction transaction = null;

    try {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        Product product = new Product();
        product.setId(15);
        product.setName("Product1");
        session.update(product);
        transaction.commit();
    } catch (Exception e) {
        rollbackTransactionQuietly(transaction);
        throw (e);
    } finally {
        closeSessionQuietly(session);
    }
}
```

其他一些可能的情况是：

-   我们没有为实体指定适当的未保存值策略
-   两个用户几乎同时尝试删除同一行
-   我们在自动生成的 ID 或版本字段中手动设置一个值

## 7.延迟初始化错误

我们通常将关联配置为延迟加载，以提高应用程序性能。关联仅在首次使用时获取。

但是，Hibernate 需要一个活动会话来获取数据。如果在我们尝试访问未初始化的关联时会话已经关闭，则会出现异常。

让我们研究一下这个异常以及修复它的各种方法。

### 7.1. 延迟初始化异常

LazyInitializationException表示尝试在活动会话之外加载未初始化的数据。在许多情况下，我们都会遇到此错误。

首先，我们可以在访问表示层中的惰性关系时得到这个异常。原因是实体在业务层被部分加载，会话被关闭。

其次，如果我们使用getOne方法，我们可以通过[Spring Data](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)获取此错误。此方法延迟获取实例。

有很多方法可以解决这个异常。

首先，我们可以让所有的关系都热切加载。但是，这会影响应用程序性能，因为我们将加载不会使用的数据。

其次，我们可以保持会话打开直到视图被渲染。这被称为“[在视图中打开会话](https://vladmihalcea.com/the-open-session-in-view-anti-pattern/)”，它是一种反模式。我们应该避免这种情况，因为它有几个缺点。

第三，我们可以打开另一个会话并重新附加实体以获取关系。我们可以通过在会话中使用merge方法来做到这一点。

最后，我们可以在业务层初始化所需的关联。我们将在下一节讨论这个问题。

### 7.2. 在业务层初始化相关惰性关系

有很多方法可以初始化惰性关系。

一种选择是通过调用实体上的相应方法来初始化它们。在这种情况下，Hibernate 将发出多个数据库查询，从而导致性能下降。我们将其称为“N+1 SELECT”问题。

其次，我们可以使用Fetch Join在单个查询中获取数据。但是，我们需要编写自定义代码来实现这一点。

最后，我们可以使用[实体图](https://www.baeldung.com/jpa-entity-graph)来定义所有要获取的属性。我们可以使用注解@NamedEntityGraph、@NamedAttributeNode和@NamedEntitySubgraph来声明性地定义实体图。我们还可以使用 JPA API 以编程方式定义它们。然后，我们通过在获取操作中指定它来在一次调用中检索整个图。

## 八、交易问题

[事务](http://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#transactions)定义工作单元和并发活动之间的隔离。我们可以用两种不同的方式来划分它们。首先，我们可以使用注解以声明方式定义它们。[其次，我们可以使用Hibernate Transaction API](http://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#transactions-api)以编程方式管理它们。

此外，Hibernate 将事务管理委托给事务管理器。如果由于任何原因无法启动、提交或回滚事务，Hibernate 将抛出异常。

根据事务管理器，我们通常会得到 TransactionException 或 IllegalArgumentException 。

作为说明，让我们尝试提交一个已标记为回滚的事务：

```java
public void 
givenTxnMarkedRollbackOnly_whenCommitted_thenTransactionException() {
    thrown.expect(isA(TransactionException.class));
    thrown.expectMessage(
        "Transaction was marked for rollback only; cannot commit");

    Session session = null;
    Transaction transaction = null;
    try {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        Product product = new Product();
        product.setId(15);
        product.setName("Product1");
        session.save(product);
        transaction.setRollbackOnly();

        transaction.commit();
    } catch (Exception e) {
        rollbackTransactionQuietly(transaction);
        throw (e);
    } finally {
        closeSessionQuietly(session);
    }
}
```

同样，其他错误也可能导致异常：

-   混合声明式和编程式事务
-   当另一个事务已在会话中处于活动状态时尝试启动事务
-   尝试在不启动事务的情况下提交或回滚
-   多次尝试提交或回滚事务

## 9. 并发问题

Hibernate 支持两种[锁定](http://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#locking)策略来防止由于并发事务导致的数据库不一致——[乐观](https://www.baeldung.com/jpa-optimistic-locking)和[悲观](https://www.baeldung.com/jpa-pessimistic-locking)。如果发生锁定冲突，它们都会引发异常。

为了支持高并发和高可扩展性，我们通常使用带版本检查的乐观并发控制。这使用版本号或时间戳来检测冲突的更新。

抛出OptimisticLockingException以指示乐观锁定冲突。例如，如果我们对同一个实体执行两次更新或删除而没有在第一次操作后刷新它，就会出现此错误：

```java
public void whenDeletingADeletedObject_thenOptimisticLockException() {
    thrown.expect(isA(OptimisticLockException.class));
    thrown.expectMessage(
        "Batch update returned unexpected row count from update");
    thrown.expectCause(isA(StaleStateException.class));

    Session session = null;
    Transaction transaction = null;

    try {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        Product product = new Product();
        product.setId(12);
        product.setName("Product 12");
        session.save(product1);
        transaction.commit();
        session.close();

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        product = session.get(Product.class, 12);
        session.createNativeQuery("delete from Product where id=12")
          .executeUpdate();
        // We need to refresh to fix the error.
        // session.refresh(product);
        session.delete(product);
        transaction.commit();
    } catch (Exception e) {
        rollbackTransactionQuietly(transaction);
        throw (e);
    } finally {
        closeSessionQuietly(session);
    }
}
```

同样，如果两个用户几乎同时尝试更新同一个实体，我们也会遇到此错误。在这种情况下，第一个可能会成功，而第二个会引发此错误。

因此，不引入悲观锁我们就不能完全避免这个错误。但是，我们可以通过执行以下操作来最大程度地降低其发生的可能性：

-   保持更新操作尽可能短
-   尽可能频繁地更新客户端中的实体表示
-   不要缓存实体或代表它的任何值对象
-   更新后始终刷新客户端上的实体表示

## 10.总结

在本文中，我们研究了使用 Hibernate 时遇到的一些常见异常。此外，我们调查了它们的可能原因和解决方案。