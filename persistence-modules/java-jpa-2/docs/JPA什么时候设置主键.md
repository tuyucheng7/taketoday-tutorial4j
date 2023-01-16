## 1. 概述

在本教程中，我们将说明JPA 为主键赋值的时刻。我们将阐明 JPA 规范的内容，然后展示使用各种 JPA 策略生成主键的示例。

## 2.问题陈述

正如我们所知，JPA(Java Persistence API)使用EntityManager来管理Entity的生命周期。在某些时候，JPA 提供者需要为主键分配一个值。所以，我们可能会问自己，这会在什么时候发生？说明这一点的文档在哪里？

JPA 规范说：

>   通过在其上调用持久化方法或通过级联持久化操作，新的实体实例变得既可管理又可持久化。

因此，我们将重点关注本文中的EntityManager.persist()方法。

## 3. 创造价值战略

当我们调用EntityManager.persist()方法时，实体的状态会根据 JPA 规范进行更改：

>   如果 X 是一个新实体，它就变成托管的。实体 X 将在事务提交时或之前或作为刷新操作的结果输入到数据库中。

这意味着有多种方法可以生成主键。一般来说，有两种解决方法：

-   预分配主键
-   持久化到数据库后分配主键

更具体地说，JPA 提供了四种生成主键的策略：

-   GenerationType.AUTO
-   世代类型.IDENTITY
-   GenerationType.SEQUENCE
-   GenerationType.TABLE

让我们一一看看。

### 3.1. GenerationType.AUTO

AUTO是@GeneratedValue的默认策略。如果我们只想有一个主键，我们可以使用AUTO策略。JPA 提供者将为底层数据库选择合适的策略：

```java
@Entity
@Table(name = "app_admin")
public class Admin {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "admin_name")
    private String name;

    // standard getters and setters
}
```

### 3.2. 世代类型.IDENTITY

IDENTITY策略依赖于数据库自增列。数据库在每次插入操作后生成主键。JPA 在执行插入操作后或事务提交时分配主键值：

```java
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String name;

    // standard getters and setters
}
```

在这里，我们验证事务提交前后的id值：

```java
@Test
public void givenIdentityStrategy_whenCommitTransction_thenReturnPrimaryKey() {
    User user = new User();
    user.setName("TestName");
        
    entityManager.getTransaction().begin();
    entityManager.persist(user);
    Assert.assertNull(user.getId());
    entityManager.getTransaction().commit();

    Long expectPrimaryKey = 1L;
    Assert.assertEquals(expectPrimaryKey, user.getId());
}
```

MySQL、SQL Server、PostgreSQL、DB2、Derby 和 Sybase 支持IDENTITY策略。

### 3.3. GenerationType.SEQUENCE

通过使用SEQUENCE策略，JPA 使用数据库序列生成主键。在应用此策略之前，我们首先需要在数据库端创建一个序列：

```sql
CREATE SEQUENCE article_seq
  MINVALUE 1
  START WITH 50
  INCREMENT BY 50
```

JPA在我们调用EntityManager.persist()方法之后和提交事务之前设置主键。

让我们使用SEQUENCE 策略定义一个Article实体：

```java
@Entity
@Table(name = "article")
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_gen")
    @SequenceGenerator(name="article_gen", sequenceName="article_seq")
    private Long id;

    @Column(name = "article_name")
    private String name

    // standard getters and setters
}
```

序列从 50 开始，所以第一个id将是下一个值 51。

现在，让我们测试一下SEQUENCE策略：

```java
@Test
public void givenSequenceStrategy_whenPersist_thenReturnPrimaryKey() {
    Article article = new Article();
    article.setName("Test Name");

    entityManager.getTransaction().begin();
    entityManager.persist(article);
    Long expectPrimaryKey = 51L;
    Assert.assertEquals(expectPrimaryKey, article.getId());

    entityManager.getTransaction().commit();
}
```

Oracle、PostgreSQL 和 DB2 支持SEQUENCE策略。

### 3.4. GenerationType.TABLE

TABLE策略从表中生成主键，无论底层数据库如何，它的工作原理都是一样的。

我们需要在数据库端创建一个生成表来生成主键。该表至少应该有两列：一列代表生成器的名称，另一列存储主键值。

首先，让我们创建一个生成器表：

```java
@Table(name = "id_gen")
@Entity
public class IdGenerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "gen_name")
    private String gen_name;

    @Column(name = "gen_value")
    private Long gen_value;

    // standard getters and setters
}
```

然后，我们需要向生成器表中插入两个初始值：

```sql
INSERT INTO id_gen (gen_name, gen_val) VALUES ('id_generator', 0);
INSERT INTO id_gen (gen_name, gen_val) VALUES ('task_gen', 10000);
```

JPA 在调用EntityManager.persist()方法之后和事务提交之前分配主键值。

现在让我们将生成器表与TABLE策略一起使用。我们可以使用allocationSize来预分配一些主键：

```java
@Entity
@Table(name = "task")
public class Task {
    
    @TableGenerator(name = "id_generator", table = "id_gen", pkColumnName = "gen_name", valueColumnName = "gen_value",
        pkColumnValue="task_gen", initialValue=10000, allocationSize=10)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "id_generator")
    private Long id;

    @Column(name = "name")
    private String name;

    // standard getters and setters
}
```

调用persist方法后id从10000开始：

```java
@Test
public void givenTableStrategy_whenPersist_thenReturnPrimaryKey() {
    Task task = new Task();
    task.setName("Test Task");

    entityManager.getTransaction().begin();
    entityManager.persist(task);
    Long expectPrimaryKey = 10000L;
    Assert.assertEquals(expectPrimaryKey, task.getId());

    entityManager.getTransaction().commit();
}
```

## 4. 总结

本文阐述了JPA在不同策略下设置主键的时刻。此外，我们还通过示例了解了每种策略的用法。