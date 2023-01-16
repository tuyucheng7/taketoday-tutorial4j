## 1. 概述

在本教程中，我们将介绍[Hibernate Object/Grid Mapper (OGM)](http://hibernate.org/ogm/)的基础知识。

Hibernate OGM 为 NoSQL 数据存储提供JavaPersistence API (JPA) 支持。NoSQL 是涵盖各种数据存储的总称。例如，这包括键值、文档、面向列和面向图形的数据存储。

## 2. Hibernate OGM的架构

[Hibernate 传统上为关系数据库提供对象关系映射 (ORM) 引擎](https://www.baeldung.com/hibernate-4-spring)。Hibernate OGM 引擎扩展了它的功能以支持 NoSQL 数据存储。 使用它的主要好处是 JPA 接口在关系数据存储和 NoSQL 数据存储之间的一致性。

由于两个关键接口DatastoreProvider和GridDialect，Hibernate OGM 能够提供对许多 NoSQL 数据存储的抽象。因此，它支持的每个新 NoSQL 数据存储都带有这些接口的实现。

截至目前，它并不支持所有的 NoSQL 数据存储，但它能够与其中的许多数据存储一起使用，例如 Infinispan 和 Ehcache(键值)、MongoDB 和 CouchDB(文档)以及 Neo4j(图形)。

它还 [完全支持事务](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)并且可以与标准 JTA 提供程序一起工作。首先，这可以通过 Jakarta EE 容器提供，无需任何显式配置。此外，我们可以在JavaSE 环境中使用像 Narayana 这样的独立 JTA 事务管理器。

## 3.设置

对于本教程，我们将使用 Maven 提取所需的依赖项以使用 Hibernate OGM。我们还将使用[MongoDB](https://www.baeldung.com/java-mongodb)。

为了澄清，让我们看看如何为教程设置它们。

### 3.1. Maven 依赖项

让我们看看使用 Hibernate OGM 和 MongoDB 所需的依赖项：

```xml
<dependency>
    <groupId>org.hibernate.ogm</groupId>
    <artifactId>hibernate-ogm-mongodb</artifactId>
    <version>5.4.0.Final</version>
</dependency>
<dependency>
    <groupId>org.jboss.narayana.jta</groupId>
    <artifactId>narayana-jta</artifactId>
    <version>5.9.2.Final</version>
</dependency>
```

在这里，我们通过 Maven 拉取所需的依赖项：

-   [用于 MongoDB 的 Hibernate OGM 方言](https://search.maven.org/search?q=a:hibernate-ogm-mongodb AND g:org.hibernate.ogm)
-   [Narayana 事务管理器](https://search.maven.org/search?q=g:org.jboss.narayana.jta AND a:narayana-jta)(JTA 的实际提供者)

### 3.2. 持久单元

我们还必须在 Hibernate persistance.xml中定义数据存储详细信息：

```xml
<persistence-unit name="ogm-mongodb" transaction-type="JTA">
    <provider>org.hibernate.ogm.jpa.HibernateOgmPersistence</provider>
    <properties>
        <property name="hibernate.ogm.datastore.provider" value="MONGODB" />
        <property name="hibernate.ogm.datastore.database" value="TestDB" />
        <property name="hibernate.ogm.datastore.create_database" value="true" />
    </properties>
</persistence-unit>
```

请注意我们在此处提供的定义：

-   属性事务类型的值为“JTA”(这意味着我们需要来自 EntityManagerFactory 的 JTA 实体管理器)
-   提供者，它是 Hibernate OGM 的HibernateOgmPersistence
-   一些与数据库相关的附加细节(这些通常因不同的数据源而异)

该配置假定 MongoDB 在默认情况下正在运行并且可以访问。如果不是这种情况，我们可以随时[提供必要的详细信息](https://docs.jboss.org/hibernate/stable/ogm/reference/en-US/html_single/#_configuring_mongodb)。我们之前的一篇文章还[详细介绍了如何设置 MongoDB](https://www.baeldung.com/java-mongodb)。

## 4.实体定义

现在我们已经了解了基础知识，让我们定义一些实体。如果我们以前使用过 Hibernate ORM 或 JPA，这就没有什么可补充的了。这是 Hibernate OGM 的基本前提。它承诺让我们只需了解 JPA 即可使用不同的 NoSQL 数据存储。

对于本教程，我们将定义一个简单的对象模型：

[![领域模型](https://www.baeldung.com/wp-content/uploads/2018/12/Domain-Model.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/Domain-Model.jpg)

它定义了 Article、Author和Editor 类及其关系。

让我们也用Java定义它们：

```java
@Entity
public class Article {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String articleId;
    
    private String articleTitle;
    
    @ManyToOne
    private Author author;

    // constructors, getters and setters...
}
@Entity
public class Author {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String authorId;
    
    private String authorName;
    
    @ManyToOne
    private Editor editor;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.PERSIST)
    private Set<Article> authoredArticles = new HashSet<>();

    // constructors, getters and setters...
}
@Entity
public class Editor {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String editorId;
    
    private String editorName;
    @OneToMany(mappedBy = "editor", cascade = CascadeType.PERSIST)
    private Set<Author> assignedAuthors = new HashSet<>();

    // constructors, getters and setters...
}
```

我们现在已经定义了实体类并使用 JPA 标准注解对它们进行了注解：

-   @Entity将它们建立为 JPA 实体
-   @Id为具有 UUID 的实体生成主键
-   @OneToMany和@ManyToOne在实体之间建立双向关系

## 5. 操作

现在我们已经创建了我们的实体，让我们看看是否可以对它们执行一些操作。作为第一步，我们必须生成一些测试数据。在这里，我们将创建一个Editor、一些Author和一些Article 。我们还将建立他们的关系。

此后，在执行任何操作之前，我们需要一个 EntityManagerFactory 实例。 我们可以使用它来创建EntityManager。与此同时，我们需要创建TransactionManager来处理事务边界。

让我们看看我们如何使用这些来持久化和检索我们之前创建的实体：

```java
private void persistTestData(EntityManagerFactory entityManagerFactory, Editor editor) 
  throws Exception {
    TransactionManager transactionManager = 
      com.arjuna.ats.jta.TransactionManager.transactionManager();
    transactionManager.begin();
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    
    entityManager.persist(editor);
    entityManager.close();
    transactionManager.commit();
}
```

在这里，我们使用EntityManager来持久化根实体，它级联到它的所有关系。我们还在定义的事务边界内执行此操作。

现在我们准备加载我们刚刚持久化的实体并验证其内容。我们可以运行一个测试来验证这一点：

```java
@Test
public void givenMongoDB_WhenEntitiesCreated_thenCanBeRetrieved() throws Exception {
    EntityManagerFactory entityManagerFactory = 
      Persistence.createEntityManagerFactory("ogm-mongodb");
    Editor editor = generateTestData();
    persistTestData(entityManagerFactory, editor);
    
    TransactionManager transactionManager = 
      com.arjuna.ats.jta.TransactionManager.transactionManager();  
    transactionManager.begin();
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    Editor loadedEditor = entityManager.find(Editor.class, editor.getEditorId());
    
    assertThat(loadedEditor).isNotNull();
    // Other assertions to verify the entities and relations
}
```

在这里，我们再次使用EntityManager来查找数据并对其执行标准断言。当我们运行这个测试时，它会实例化数据存储、持久化实体、检索它们并进行验证。

同样，我们刚刚使用 JPA 来持久化实体及其关系。同样，我们使用 JPA 来加载实体并且一切正常，即使我们选择的数据库是 MongoDB 而不是传统的关系数据库。

## 6.切换后端

我们也可以切换后端。现在让我们看看这样做有多难。

我们将后端更改为[Neo4j，它恰好是一种流行的面向图形的数据存储](https://www.baeldung.com/java-neo4j)。

首先，让我们[为 Neo4j 添加 Maven 依赖](https://search.maven.org/search?q=a:hibernate-ogm-neo4j)：

```xml
<dependency>
    <groupId>org.hibernate.ogm</groupId>
    <artifactId>hibernate-ogm-neo4j</artifactId>
    <version>5.4.0.Final</version>
</dependency>
```

接下来，我们必须在persistence.xml中添加相关的持久化单元：

```xml
<persistence-unit name="ogm-neo4j" transaction-type="JTA">
    <provider>org.hibernate.ogm.jpa.HibernateOgmPersistence</provider>
    <properties>
        <property name="hibernate.ogm.datastore.provider" value="NEO4J_EMBEDDED" />
        <property name="hibernate.ogm.datastore.database" value="TestDB" />
        <property name="hibernate.ogm.neo4j.database_path" value="target/test_data_dir" />
    </properties>
</persistence-unit>
```

简而言之，这些是 Neo4j 所需的非常基本的配置。这可以[根据需要进一步详细](https://docs.jboss.org/hibernate/stable/ogm/reference/en-US/html_single/#_configuring_neo4j)说明。

好吧，这几乎就是需要做的事情。当我们使用 Neo4j 作为后端数据存储运行相同的测试时，它可以无缝运行。

请注意，我们已将后端从恰好是面向文档的数据存储的 MongoDB 切换到面向图形的数据存储的 Neo4j。我们以最少的改动完成了所有这一切，并且不需要对我们的任何操作进行任何更改。

## 七. 总结

在本文中，我们了解了 Hibernate OGM 的基础知识，包括它的架构。随后，我们实现了一个基本的领域模型，并使用各种数据库执行了各种操作。