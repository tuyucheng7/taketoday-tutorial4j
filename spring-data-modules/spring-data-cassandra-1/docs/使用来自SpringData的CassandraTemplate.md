## 1. 概述

这是Spring Data Cassandra文章系列的第二篇文章。在本文中，我们将主要关注数据访问层中的CassandraTemplate和CQL查询。你可以在本系列的[第一篇文章](https://www.baeldung.com/spring-data-cassandra-tutorial)中阅读有关Spring Data Cassandra的更多信息。

Cassandra查询语言(CQL)是Cassandra数据库的查询语言，CqlTemplate是Spring Data Cassandra中的低级数据访问模板-它方便地公开数据操作相关操作以执行CQL语句。

CassandraTemplate建立在低级CqlTemplate之上，提供了一种简单的方法来查询域对象并将对象映射到Cassandra中的持久数据结构。

让我们从配置开始，然后深入研究使用这两个模板的示例。

### 延伸阅读

### [使用Cassandra、Astra和Stargate构建仪表板](https://www.baeldung.com/cassandra-astra-stargate-dashboard)

了解如何使用DataStax Astra构建仪表板，DataStax Astra是一种由Apache Cassandra和Stargate API提供支持的数据库即服务。

[阅读更多](https://www.baeldung.com/cassandra-astra-stargate-dashboard)→

### [使用Cassandra、Astra、REST和GraphQL构建仪表板-记录状态更新](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)

使用Cassandra存储时间序列数据的示例。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)→

### [使用Cassandra、Astra和CQL构建仪表板-映射事件数据](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)

了解如何根据存储在Astra数据库中的数据在交互式地图上显示事件。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)→

## 2. CassandraTemplate配置

CassandraTemplate在Spring上下文中可用，因为我们的主要CassandraSpring配置扩展了AbstractCassandraConfiguration：

```java
@Configuration
@EnableCassandraRepositories(basePackages = "cn.tuyucheng.taketoday.spring.data.cassandra.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {
    // ...
}
```

然后我们可以简单地连接模板——通过它的确切类型，CassandraTemplate，或者作为更通用的接口CassandraOperations：

```java
@Autowired
private CassandraOperations cassandraTemplate;
```

## 3. 使用CassandraTemplate访问数据

让我们使用上面在我们的数据访问层模块中定义的CassandraTemplate来处理数据持久化。

### 3.1 保存一本新书

我们可以将一本新书保存到我们的书店：

```java
Book javaBook = new Book(
    UUIDs.timeBased(), "Head First Java", "O'Reilly Media",
    ImmutableSet.of("Computer", "Software"));
cassandraTemplate.insert(javaBook);
```

然后我们可以检查数据库中插入的书的可用性：

```java
Select select = QueryBuilder.select().from("book")
    .where(QueryBuilder.eq("title", "Head First Java"))
    .and(QueryBuilder.eq("publisher", "O'Reilly Media"));
Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
```

我们在这里使用Select QueryBuilder，映射到cassandraTemplate中的selectOne()。我们将在CQL查询部分更深入地讨论QueryBuilder。

### 3.2 保存多本书

我们可以使用列表一次将多本书保存到我们的书店：

```java
Book javaBook = new Book(
    UUIDs.timeBased(), "Head First Java", "O'Reilly Media",
    ImmutableSet.of("Computer", "Software"));
Book dPatternBook = new Book(
    UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media",
    ImmutableSet.of("Computer", "Software"));
List<Book> bookList = new ArrayList<Book>();
bookList.add(javaBook);
bookList.add(dPatternBook);
cassandraTemplate.insert(bookList);
```

### 3.3 更新现有图书

让我们首先插入一本新书：

```java
Book javaBook = new Book(
    UUIDs.timeBased(), "Head First Java", "O'Reilly Media",
    ImmutableSet.of("Computer", "Software"));
cassandraTemplate.insert(javaBook);
```

让我们取书：

```java
Select select = QueryBuilder.select().from("book");
Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
```

然后让我们为检索到的书添加一些额外的标签：

```java
retrievedBook.setTags(ImmutableSet.of("Java", "Programming"));
cassandraTemplate.update(retrievedBook);
```

### 3.4 删除插入的书

让我们插入一本新书：

```java
Book javaBook = new Book(
    UUIDs.timeBased(), "Head First Java", "O'Reilly Media",
    ImmutableSet.of("Computer", "Software"));
cassandraTemplate.insert(javaBook);
```

然后删除这本书：

```java
cassandraTemplate.delete(javaBook);
```

### 3.5 删除所有书籍

现在让我们插入一些新书：

```java
Book javaBook = new Book(
    UUIDs.timeBased(), "Head First Java", "O'Reilly Media",
    ImmutableSet.of("Computer", "Software"));
Book dPatternBook = new Book(
    UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", 
    ImmutableSet.of("Computer", "Software"));
cassandraTemplate.insert(javaBook);
cassandraTemplate.insert(dPatternBook);
```

然后删除所有的书：

```java
cassandraTemplate.deleteAll(Book.class);
```

## 4. 使用CQL查询进行数据访问

始终可以使用CQL查询在数据访问层中进行数据操作。CQL查询处理由CqlTemplate类执行，允许我们根据需要执行自定义查询。

然而，由于CassandraTemplate类是CqlTemplate的扩展，我们可以直接使用它来执行这些查询。

让我们看一下我们可以使用CQL查询来操作数据的不同方法。

### 4.1 使用查询生成器

QueryBuilder可用于为数据库中的数据操作构建查询。几乎所有的标准操作都可以使用开箱即用的构建块来构建：

```java
Insert insertQueryBuider = QueryBuilder.insertInto("book")
    .value("isbn", UUIDs.timeBased())
    .value("title", "Head First Java")
    .value("publisher", "OReilly Media")
    .value("tags", ImmutableSet.of("Software"));
cassandraTemplate.execute(insertQueryBuider);
```

如果仔细查看代码片段，你可能会注意到使用了execute()方法，而不是相关的操作类型(插入、删除等)。这是因为查询类型由QueryBuilder的输出定义。

### 4.2 使用PreparedStatements

尽管PreparedStatements可用于任何情况，但通常建议将此机制用于多个插入以实现高速摄取。

PreparedStatement只准备一次，有助于确保高性能：

```java
UUID uuid = UUIDs.timeBased();
String insertPreparedCql ="insert into book (isbn, title, publisher, tags) values (?, ?, ?, ?)";
List<Object> singleBookArgsList = new ArrayList<>();
List<List<?>> bookList = new ArrayList<>();
singleBookArgsList.add(uuid);
singleBookArgsList.add("Head First Java");
singleBookArgsList.add("OReilly Media");
singleBookArgsList.add(ImmutableSet.of("Software"));
bookList.add(singleBookArgsList);
cassandraTemplate.ingest(insertPreparedCql, bookList);
```

### 4.3 使用CQL语句

我们可以直接使用CQL语句查询数据，如下：

```java
UUID uuid = UUIDs.timeBased();
String insertCql = "insert into book (isbn, title, publisher, tags) 
    values (" + uuid + ", 'Head First Java', 'OReilly Media', {'Software'})";
cassandraTemplate.execute(insertCql);
```

## 5. 总结

在本文中，我们研究了使用Spring Data Cassandra的各种数据操作策略，包括CassandraTemplate和CQL查询。