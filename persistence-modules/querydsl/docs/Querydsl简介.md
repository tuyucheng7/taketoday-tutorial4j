## 1. 简介

这是一篇介绍性文章，可帮助你启动并运行功能强大的[Querydsl](http://www.querydsl.com/) API 以实现数据持久性。

此处的目标是为你提供将 Querydsl 添加到项目中的实用工具，了解生成类的结构和用途，并基本了解如何为大多数常见场景编写类型安全的数据库查询。

## 2. Querydsl的用途

对象关系映射框架是 EnterpriseJava的核心。这些弥补了面向对象方法和关系数据库模型之间的不匹配。它们还允许开发人员编写更清晰、更简洁的持久性代码和领域逻辑。

但是，ORM 框架最困难的设计选择之一是用于构建正确且类型安全的查询的 API。

作为使用最广泛的JavaORM 框架之一，Hibernate(以及密切相关的 JPA 标准)提出了一种与 SQL 非常相似的基于字符串的查询语言 HQL (JPQL)。这种方法的明显缺点是缺乏类型安全和静态查询检查。此外，在更复杂的情况下(例如，当需要根据某些条件在运行时构造查询时)，构建 HQL 查询通常涉及字符串的连接，这通常非常不安全且容易出错。

[JPA 2.0 标准以Criteria Query API](https://docs.oracle.com/javaee/7/tutorial/persistence-criteria.htm#GJITV)的形式带来了改进——一种新的类型安全的构建查询的方法，它利用了在注解预处理期间生成的元模型类。不幸的是，Criteria Query API 本质上是开创性的，但最终变得非常冗长且几乎不可读。下面是 Jakarta EE 教程中的一个示例，用于生成像SELECT p FROM Pet p这样简单的查询：

```java
EntityManager em = ...;
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Pet> cq = cb.createQuery(Pet.class);
Root<Pet> pet = cq.from(Pet.class);
cq.select(pet);
TypedQuery<Pet> q = em.createQuery(cq);
List<Pet> allPets = q.getResultList();
```

难怪很快出现了一个更合适的[Querydsl](http://www.querydsl.com/)库，它基于生成元数据类的相同想法，但使用流畅且可读的 API 实现。

## 3. Querydsl类生成

让我们从生成和探索解释 Querydsl 流畅 API 的神奇元类开始。

### 3.1. 将 Querydsl 添加到 Maven 构建

在你的项目中包含 Querydsl 就像将几个依赖项添加到你的构建文件并配置一个插件来处理 JPA 注解一样简单。让我们从依赖关系开始。Querydsl 库的版本应提取到<project><properties>部分内的单独属性，如下所示(有关 Querydsl 库的最新版本，请查看[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"com.querydsl")存储库)：

```xml
<properties>
    <querydsl.version>4.1.3</querydsl.version>
</properties>
```

接下来，将以下依赖项添加到pom.xml文件的<project><dependencies>部分：

```xml
<dependencies>

    <dependency>
        <groupId>com.querydsl</groupId>
        <artifactId>querydsl-apt</artifactId>
        <version>${querydsl.version}</version>
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>com.querydsl</groupId>
        <artifactId>querydsl-jpa</artifactId>
        <version>${querydsl.version}</version>
    </dependency>

</dependencies>
```

querydsl -apt依赖项是一个注解处理工具 (APT)——相应JavaAPI 的实现，允许在源文件进入编译阶段之前处理源文件中的注解。此工具生成所谓的 Q 类型 — 与应用程序的实体类直接相关的类，但以字母 Q 为前缀。例如，如果你的应用程序中有一个标有@Entity注解的用户类，那么生成的 Q-type 将驻留在QUser.java源文件中。

提供的querydsl-apt依赖范围意味着这个 jar 应该只在构建时可用，而不是包含在应用程序工件中。

querydsl-jpa 库是 Querydsl 本身，旨在与 JPA 应用程序一起使用。

要配置利用querydsl-apt的注解处理插件，请将以下插件配置添加到你的 pom – 在<project><build><plugins>元素内：

```xml
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```

此插件确保在 Maven 构建的过程目标期间生成 Q 类型。outputDirectory配置属性指向将生成 Q 类型源文件的目录。该属性的值将在你稍后探索 Q 文件时有用。

如果你的 IDE 没有自动执行此操作，你还应该将此目录添加到项目的源文件夹中——请参阅你最喜欢的 IDE 的文档以了解如何执行此操作。

对于本文，我们将使用一个简单的博客服务 JPA 模型，它由用户和他们的BlogPosts组成，它们之间是一对多的关系：

```java
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String login;

    private Boolean disabled;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
    private Set<BlogPost> blogPosts = new HashSet<>(0);

    // getters and setters

}

@Entity
public class BlogPost {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String body;

    @ManyToOne
    private User user;

    // getters and setters

}
```

要为你的模型生成 Q 类型，只需运行：

```bash
mvn compile
```

### 3.2. 探索生成的类

现在转到apt-maven-plugin的outputDirectory属性中指定的目录(在我们的示例中为 target/generated-sources/java)。你将看到一个直接反映你的领域模型的包和类结构，除了所有以字母 Q 开头的类(在我们的例子中是QUser和QBlogPost)。

打开文件QUser.java。这是构建所有以用户为根实体的查询的入口点。你会注意到的第一件事是@Generated注解，这意味着该文件是自动生成的，不应手动编辑。如果你更改任何域模型类，则必须再次运行mvn compile以重新生成所有相应的 Q 类型。

除了这个文件中存在的几个QUser构造函数之外，你还应该注意QUser类的公共静态最终实例：

```java
public static final QUser user = new QUser("user");
```

这是你可以在对该实体的大多数 Querydsl 查询中使用的实例，除非你需要编写一些更复杂的查询，例如在单个查询中连接一个表的多个不同实例。

最后要注意的是，对于实体类的每个字段，Q-type 中都有一个对应的Path字段，例如QUser类中的NumberPath id、 StringPath login和SetPath blogPosts(注意字段的名称对应于Set的是复数形式)。这些字段用作我们稍后会遇到的流畅查询 API 的一部分。

## 4. 使用 Querydsl 查询

### 4.1. 简单的查询和过滤

要构建查询，首先我们需要一个[JPAQueryFactory](http://www.querydsl.com/static/querydsl/4.1.3/apidocs/com/querydsl/jpa/impl/JPAQueryFactory.html)实例，这是开始构建过程的首选方式。JPAQueryFactory唯一需要的是EntityManager，它应该已经通过EntityManagerFactory.createEntityManager()调用或@PersistenceContext注入在你的 JPA 应用程序中可用。

```java
EntityManagerFactory emf = 
  Persistence.createEntityManagerFactory("com.baeldung.querydsl.intro");
EntityManager em = entityManagerFactory.createEntityManager();
JPAQueryFactory queryFactory = new JPAQueryFactory(em);
```

现在让我们创建我们的第一个查询：

```java
QUser user = QUser.user;

User c = queryFactory.selectFrom(user)
  .where(user.login.eq("David"))
  .fetchOne();
```

请注意，我们已经定义了一个局部变量QUser user 并使用QUser.user静态实例对其进行了初始化。这样做纯粹是为了简洁，或者你可以导入静态QUser.user字段。

JPAQueryFactory的selectFrom方法开始构建查询。我们将QUser实例传递给它，并继续使用 .where ()方法构建查询的条件子句。user.login是对我们之前看到的QUser类的StringPath字段的引用。StringPath对象还有.eq ()方法，允许通过指定字段相等条件流畅地继续构建查询。

最后，为了从数据库中获取值到持久性上下文中，我们通过调用fetchOne()方法结束构建链。如果找不到对象，此方法返回null ，但如果有多个实体满足.where()条件，则抛出NonUniqueResultException 。

### 4.2. 排序和分组

现在让我们获取列表中的所有用户，按他们的登录名按升序排序。

```java
List<User> c = queryFactory.selectFrom(user)
  .orderBy(user.login.asc())
  .fetch();
```

这种语法是可能的，因为Path类有.asc()和.desc()方法。你还可以为.orderBy()方法指定多个参数以按多个字段排序。

现在让我们尝试一些更困难的事情。假设我们需要按标题对所有帖子进行分组并计算重复的标题。这是通过.groupBy()子句完成的。我们还希望根据结果出现次数对标题进行排序。

```java
NumberPath<Long> count = Expressions.numberPath(Long.class, "c");

List<Tuple> userTitleCounts = queryFactory.select(
  blogPost.title, blogPost.id.count().as(count))
  .from(blogPost)
  .groupBy(blogPost.title)
  .orderBy(count.desc())
  .fetch();
```

我们选择了博客文章标题和重复次数，按标题分组，然后按总计数排序。请注意，我们首先为. select()子句，因为我们需要在.orderBy()子句中引用它。

### 4.3. 具有连接和子查询的复杂查询

让我们找到所有写过标题为“Hello World!”的帖子的用户。对于这样的查询，我们可以使用内部连接。请注意，我们为联接表创建了一个别名blogPost以在.on()子句中引用它：

```java
QBlogPost blogPost = QBlogPost.blogPost;

List<User> users = queryFactory.selectFrom(user)
  .innerJoin(user.blogPosts, blogPost)
  .on(blogPost.title.eq("Hello World!"))
  .fetch();
```

现在让我们尝试使用子查询实现相同的目的：

```java
List<User> users = queryFactory.selectFrom(user)
  .where(user.id.in(
    JPAExpressions.select(blogPost.user.id)
      .from(blogPost)
      .where(blogPost.title.eq("Hello World!"))))
  .fetch();
```

正如我们所看到的，子查询与查询非常相似，它们也非常可读，但是它们以JPAExpressions工厂方法开始。为了将子查询与主查询连接起来，我们一如既往地引用了之前定义和使用的别名。

### 4.4. 修改数据

JPAQueryFactory不仅允许构建查询，还允许修改和删除记录。让我们更改用户的登录名并禁用该帐户：

```java
queryFactory.update(user)
  .where(user.login.eq("Ash"))
  .set(user.login, "Ash2")
  .set(user.disabled, true)
  .execute();
```

我们可以为不同的字段设置任意数量的.set()子句。.where()子句不是必需的，因此我们可以一次更新所有记录。

要删除符合特定条件的记录，我们可以使用类似的语法：

```java
queryFactory.delete(user)
  .where(user.login.eq("David"))
  .execute();
```

.where()子句也不是必需的，但要小心，因为省略.where ()子句会导致删除特定类型的所有实体。

你可能想知道，为什么JPAQueryFactory没有.insert()方法。这是 JPA 查询接口的限制。底层的[javax.persistence.Query.executeUpdate()](https://docs.oracle.com/javaee/7/api/javax/persistence/Query.html#executeUpdate--)方法能够执行更新和删除但不能执行插入语句。要插入数据，你应该简单地使用 EntityManager 持久化实体。

如果你仍然想利用类似的 Querydsl 语法来插入数据，你应该使用驻留在 querydsl-sql 库中的 SQLQueryFactory 类[。](http://www.querydsl.com/static/querydsl/4.1.3/apidocs/com/querydsl/sql/SQLQueryFactory.html)

## 5.总结

在本文中，我们发现了 Querydsl 提供的用于持久对象操作的功能强大且类型安全的 API。

我们已经学会了将 Querydsl 添加到项目中并探索了生成的 Q 类型。我们还介绍了一些典型的用例，并欣赏它们的简洁性和可读性。

示例的所有源代码都可以在[github 存储库](https://github.com/eugenp/tutorials/tree/master/persistence-modules/querydsl)中找到。

最后，当然还有 Querydsl 提供的更多功能，包括使用原始 SQL、非持久集合、NoSQL 数据库和全文搜索——我们将在以后的文章中探讨其中的一些功能。