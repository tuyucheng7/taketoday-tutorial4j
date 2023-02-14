## 1. 概述

有时我们需要在同一个应用程序中连接到多个不同的数据库。

**在本教程中，我们将探讨在同一应用程序中使用多个Spring Data模块时的各种配置选项**。

## 2. 所需依赖

首先，我们需要在pom.xml文件中添加我们的依赖项，以便我们可以使用[spring-boot-starter-data-mongodb](https://spring.io/projects/spring-data-mongodb)和[spring-boot-starter-data-cassandra](https://spring.io/projects/spring-data-cassandra) Spring Boot Data绑定：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

## 3. 数据库设置

接下来，我们需要使用[Cassandra](https://hub.docker.com/_/cassandra)和[Mongo](https://hub.docker.com/_/mongo)的预构建[Docker](https://docs.docker.com/install/)镜像来**设置实际的数据库**：

```shell
docker run --name mongo-db -d -p 27017:27017 mongo:latest
docker run --name cassandra-db -d -p 9042:9042 cassandra:latest
```

这两个命令将自动下载最新的Cassandra和MongoDB Docker镜像并运行实际的容器。

我们还需要在实际操作系统环境中从容器内部转发端口(使用-p选项)，以便我们的应用程序可以访问数据库。

**我们必须使用cqlsh实用程序为Cassandra创建数据库结构。CassandraDataAutoConfiguration无法自动创建Keyspace**，因此我们需要使用[CQL](https://cassandra.apache.org/doc/latest/cql/)语法声明它。

所以首先，我们连接到Cassandra容器的bash shell：

```shell
$ docker exec -it cassandra-db /bin/bash
root@419acd18891e:/# cqlsh
Connected to Test Cluster at 127.0.0.1:9042.
[cqlsh 5.0.1 | Cassandra 3.11.4 | CQL spec 3.4.4 | Native protocol v4]
Use HELP for help.
cqlsh> CREATE KEYSPACE IF NOT exists tuyucheng WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
cqlsh> USE tuyucheng;
cqlsh> CREATE TABLE bookaudit(
   bookid VARCHAR,
   rentalrecno VARCHAR,
   loandate VARCHAR,
   loaner VARCHAR,
   primary key(bookid, rentalrecno)
);
```

在第6行和第9行，我们创建了相关的keyspace和table。

我们可以跳过创建表并简单地依赖spring-boot-starter-data-cassandra来为我们初始化schema，但是，由于我们想单独探索框架配置，所以这是必要的步骤。

默认情况下，**Mongo不强加任何类型的模式验证，因此无需为其配置任何其他内容**。

最后，我们在application.properties中配置数据库的相关信息：

```properties
spring.data.cassandra.username=cassandra
spring.data.cassandra.password=cassandra
spring.data.cassandra.keyspaceName=tuyucheng
spring.data.cassandra.contactPoints=localhost
spring.data.cassandra.port=9042
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=tuyucheng
```

## 4. 数据存储检测机制

**当在类路径上检测到多个Spring Data模块时，Spring框架进入严格的Repository配置模式**。这意味着它在底层使用不同的检测机制来识别哪个Repository属于哪个持久性技术。

### 4.1 扩展模块特定的Repository接口

第一种机制尝试确定Repository是否扩展了特定于Spring Data模块的Repository类型：

```java
public interface BookAuditRepository extends CassandraRepository<BookAudit, String> {
}
```

出于我们示例的目的，BookAudit.java包含一些基本的存储结构，用于跟踪借出图书的用户：

```java
public class BookAudit {
    private String bookId;
    private String rentalRecNo;
    private String loaner;
    private String loanDate;

    // getters and setters ...
}
```

这同样适用于MongoDB相关的Repository定义：

```java
public interface BookDocumentRepository extends MongoRepository<BookDocument, String> {
}
```

这个存储了本书的内容和一些相关的元数据：

```java
public class BookDocument {
    private String bookId;
    private String bookName;
    private String bookAuthor;
    private String content;

    // getters and setters ...
}
```

加载应用程序上下文时，**框架将使用派生自的基类匹配每个Repository类型**：

```java
@Test
void givenBookAudit_whenPersistWithBookAuditRepository_thenSuccess() {
    // given
    BookAudit bookAudit = new BookAudit("lorem", "ipsum", "Tuyucheng", "19:30/17.09.2022");
    
    // when
    bookAuditRepository.save(bookAudit);
    List<BookAudit> result = bookAuditRepository.findAll();
    
    // then
    assertThat(result.isEmpty(), is(false));
    assertThat(result.contains(bookAudit), is(true));
}
```

你会注意到我们的域类是简单的Java对象。在这种特殊情况下，必须使用CQL在外部创建Cassandra数据库模式，就像我们在第3节中所做的那样。

### 4.2 在域对象上使用特定于模块的注解

**第二种策略基于域对象上特定于模块的注解来确定持久层技术**。

让我们扩展一个通用的CrudRepository，现在依靠托管对象注解进行检测：

```java
public interface BookAuditCrudRepository extends CrudRepository<BookAudit, String> {
}
```

```java
public interface BookDocumentCrudRepository extends CrudRepository<BookDocument, String> {
}
```

BookAudit.java现在使用特定于Cassandra的@Table进行标注，并且需要一个复合主键：

```java
@Table
public class BookAudit {

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private String bookId;
    @PrimaryKeyColumn
    private String rentalRecNo;
    private String loaner;
    private String loanDate;

    // standard getters and setters ...
}
```

我们选择bookId和rentRecNo的组合作为我们的复合主键，因为我们的应用程序只是在每次有人借出一本书时记录一条新的租赁记录。

对于BookDocument.java，我们使用MongoDB特定的@Document注解：

```java
@Document
public class BookDocument {

    private String bookId;
    private String bookName;
    private String bookAuthor;
    private String content;

    // standard getters and setters ...
}
```

使用CrudRepository触发BookDocument保存仍然成功，但第11行返回的类型现在是Iterable而不是List：

```java
@Test
void givenBookAudit_whenPersistWithBookDocumentCrudRepository_thenSuccess() {
    // given  
    BookDocument bookDocument = new BookDocument("lorem", "Foundation", "Isaac Asimov", "Once upon a time ...");
    bookDocumentCrudRepository.save(bookDocument);
    
    // when
    Iterable<BookDocument> resultIterable = bookDocumentCrudRepository.findAll();
    List<BookDocument> result = StreamSupport.stream(resultIterable.spliterator(), false)
        .collect(Collectors.toList());
           
    // then
    assertThat(result.isEmpty(), is(false));
    assertThat(result.contains(bookDocument), is(true));
}
```

### 4.3 使用基于包的作用域

最后，我们可以通过使用@EnableCassandraRepositories和@EnableMongoRepositories注解来**指定定义Repository的基础包**：

```java
@EnableCassandraRepositories(basePackages = "cn.tuyucheng.taketoday.multipledatamodules.cassandra")
@EnableMongoRepositories(basePackages = "cn.tuyucheng.taketoday.multipledatamodules.mongo")
public class SpringDataMultipleModules {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataMultipleModules.class, args);
    }
}
```

正如我们在第1行和第2行中看到的，**这种配置模式假设我们为Cassandra和MongoDB Repository使用不同的包**。

## 5. 总结

在本教程中，我们配置了一个简单的Spring Boot应用程序，以三种方式使用两个不同的Spring Data模块。

作为第一个示例，我们扩展了CassandraRepository和MongoRepository并为域对象使用了简单的类。

在我们的第二种方法中，我们扩展了通用的CrudRepository接口，并依赖于特定于模块的注解，例如托管对象上的@Table和@Document。

最后，我们在@EnableCassandraRepositories和@EnableMongoRepositories的帮助下配置应用程序时使用了基于包的检测。