## 1. 概述

本文是**对流行图数据库Spring Data Neo4j的介绍**。

Spring Data Neo4j为Neo4j图数据库启用基于POJO的开发，并使用熟悉的Spring概念，例如用于核心API使用的模板类，并提供基于注解的编程模型。

此外，许多开发人员并不真正了解Neo4j是否真的能很好地满足他们的特定需求；这是关于Stackoverflow的[可靠概述](https://stackoverflow.com/questions/1000162/has-anyone-used-graph-based-databases-http-neo4j-org)，讨论了为什么使用Neo4j以及优缺点。

## 2. Maven依赖

让我们首先在pom.xml中声明Spring Data Neo4j依赖项。Spring Data Neo4j还需要下面提到的Spring模块：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-neo4j</artifactId>
    <version>5.0.1.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.neo4j</groupId>
    <artifactId>neo4j-ogm-test</artifactId>
    <version>3.1.2</version>
    <scope>test</scope>
</dependency>
```

这些依赖项还包括测试所需的模块。

请注意，最后一个依赖项的作用域为“test”。但也请注意，在现实世界的应用程序开发中，你更有可能运行完整的Neo4J服务器。

如果我们想使用嵌入式服务器，我们还必须添加依赖项：

```xml
<dependency>
    <groupId>org.neo4j</groupId>
    <artifactId>neo4j-ogm-embedded-driver</artifactId>
    <version>3.1.2</version>
</dependency>
```

Maven Central上提供了[spring-data-neo4j](https://central.sonatype.com/artifact/org.springframework.data/spring-data-neo4j/7.0.3)、[neo4j-ogm-test](https://central.sonatype.com/artifact/org.neo4j/neo4j-ogm-test/3.2.0-alpha02)和[neo4j-ogm-embedded-driver](https://central.sonatype.com/artifact/org.neo4j/neo4j-ogm-embedded-driver/3.2.39)依赖项。

## 3. Neo4Jj配置

Neo4j配置非常简单，它定义了应用程序连接到服务器的连接设置。与大多数其他Spring Data模块类似，这是一个Spring配置，可以定义为XML或Java配置。

在本教程中，我们将仅使用基于Java的配置：

```java
public static final String URL = 
    System.getenv("NEO4J_URL") != null ? 
    System.getenv("NEO4J_URL") : "http://neo4j:movies@localhost:7474";

@Bean
public org.neo4j.ogm.config.Configuration getConfiguration() {
    return new Builder().uri(URL).build();
}

@Bean
public SessionFactory getSessionFactory() {
    return new SessionFactory(getConfiguration(), "cn.tuyucheng.taketoday.spring.data.neo4j.domain");
}

@Bean
public Neo4jTransactionManager transactionManager() {
    return new Neo4jTransactionManager(getSessionFactory());
}
```

如上所述，配置很简单，只包含两个设置。首先-SessionFactory引用我们创建的模型来表示数据对象。然后，连接属性与服务器端点和访问凭证。

Neo4j将根据URI的协议推断驱动程序类，在我们的例子中是“http”。

请注意，在本示例中，与连接相关的属性直接配置到服务器；但是在生产应用程序中，这些应该适当地外部化并成为项目标准配置的一部分。

## 4. Neo4j Repository

Neo4j与Spring Data框架保持一致，支持Spring Data Repository抽象行为。这意味着访问底层持久机制是在内置的Neo4jRepository中抽象的，项目可以在其中直接扩展它并使用提供的开箱即用的操作。

存储库可通过注解、命名或派生的查询方法进行扩展。对Spring Data Neo4j Repository的支持也基于Neo4jTemplate，因此底层功能是相同的。

### 4.1 创建MovieRepository和PersonRepository

在本教程中，我们使用两个Repository来实现数据持久性：

```java
@Repository
public interface MovieRepository extends Neo4jRepository<Movie, Long> {

    Movie findByTitle(@Param("title") String title);

    @Query("MATCH (m:Movie) WHERE m.title =~ ('(?i).*'+{title}+'.*') RETURN m")
    Collection<Movie>
    findByTitleContaining(@Param("title") String title);

    @Query("MATCH (m:Movie)<-[:ACTED_IN]-(a:Person) RETURN m.title as movie, collect(a.name) as cast LIMIT {limit}")
    List<Map<String,Object>> graph(@Param("limit") int limit);
}
```

如你所见，Repository包含一些自定义操作以及从基类继承的标准操作。

接下来我们有更简单的PersonRepository，它只有标准操作：

```java
@Repository
public interface PersonRepository extends Neo4jRepository <Person, Long> {
    //
}
```

你可能已经注意到PersonRepository只是标准的Spring Data接口。这是因为，在这个简单的示例中，基本上使用内置操作几乎就足够了，因为我们的操作集与Movie实体相关。但是，你始终可以在此处添加自定义操作，这些操作可能会包装单个/多个内置操作。

### 4.2 配置Neo4jRepository

下一步，我们必须让Spring知道在第3节中创建的Neo4jConfiguration类中指示它的相关Repository：

```java
@Configuration
@ComponentScan("cn.tuyucheng.taketoday.spring.data.neo4j")
@EnableNeo4jRepositories(basePackages = "cn.tuyucheng.taketoday.spring.data.neo4j.repository")
public class MovieDatabaseNeo4jConfiguration {
    //
}
```

## 5. 完整数据模型

我们已经开始查看数据模型，所以现在让我们把它全部列出来-完整的Movie、Role和Person。Person实体通过Role关系引用Movie实体。

```java
@NodeEntity
public class Movie {

    @Id @GeneratedValue
    Long id;

    private String title;

    private int released;

    private String tagline;

    @Relationship(type="ACTED_IN", direction = Relationship.INCOMING)

    private List<Role> roles;

    // standard constructor, getters and setters 
}
```

请注意我们如何使用@NodeEntity注解Movie以指示此类直接映射到Neo4j中的节点。

```java
@JsonIdentityInfo(generator=JSOGGenerator.class)
@NodeEntity
public class Person {

    @Id @GeneratedValue
    Long id;

    private String name;

    private int born;

    @Relationship(type = "ACTED_IN")
    private List<Movie> movies;

    // standard constructor, getters and setters 
}

@JsonIdentityInfo(generator=JSOGGenerator.class)
@RelationshipEntity(type = "ACTED_IN")
public class Role {

    @Id @GeneratedValue
    Long id;

    private Collection<String> roles;

    @StartNode
    private Person person;

    @EndNode
    private Movie movie;

    // standard constructor, getters and setters 
}
```

当然，最后两个类也有类似的注解，-movies–引用通过“ACTED_IN”关系将Person链接到Movie类。

## 6. 使用MovieRepository访问数据

### 6.1 保存新电影对象

让我们保存一些数据，首先是一部新电影，然后是一个人，当然还有一个角色-包括我们拥有的所有关系数据：

```java
Movie italianJob = new Movie();
italianJob.setTitle("The Italian Job");
italianJob.setReleased(1999);
movieRepository.save(italianJob);

Person mark = new Person();
mark.setName("Mark Wahlberg");
personRepository.save(mark);

Role charlie = new Role();
charlie.setMovie(italianJob);
charlie.setPerson(mark);
Collection<String> roleNames = new HashSet();
roleNames.add("Charlie Croker");
charlie.setRoles(roleNames);
List<Role> roles = new ArrayList();
roles.add(charlie);
italianJob.setRoles(roles);
movieRepository.save(italianJob);
```

### 6.2 按标题检索现有电影对象

现在让我们通过使用自定义操作定义的标题检索它来验证插入的电影：

```java
Movie result = movieRepository.findByTitle(title);
```

### 6.3 通过标题的一部分检索现有的电影对象

可以使用标题的一部分搜索现有电影：

```java
Collection<Movie> result = movieRepository.findByTitleContaining("Italian");
```

### 6.4 检索所有电影

可以一次检索所有电影，并可以检查正确的计数：

```java
Collection<Movie> result = (Collection<Movie>) movieRepository.findAll();
```

然而，有许多查找方法提供了默认行为，这对于海关要求很有用，但此处并未介绍所有方法。

### 6.5 计算现有电影对象

插入多个电影对象后，我们可以得到存在的电影数：

```java
long movieCount = movieRepository.count();
```

### 6.6 删除现有电影

```java
movieRepository.delete(movieRepository.findByTitle("The Italian Job"));
```

删除插入的电影后，我们可以搜索电影对象并验证结果是否为null：

```java
assertNull(movieRepository.findByTitle("The Italian Job"));
```

### 6.7 删除所有插入的数据

可以删除数据库中的所有元素，使数据库为空：

```java
movieRepository.deleteAll();
```

此操作的结果是快速从表中删除所有数据。

## 7. 总结

在本教程中，我们使用一个非常简单的示例了解了Spring Data Neo4j的基础知识。

然而，Neo4j能够满足具有大量关系和网络的非常高级和复杂的应用程序。并且Spring Data Neo4j还提供高级功能来将带注解的实体类映射到Neo4j图形数据库。