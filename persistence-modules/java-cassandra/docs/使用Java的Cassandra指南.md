## 1. 概述

[本教程是使用Java的Apache Cassandra](https://cassandra.apache.org/)数据库的入门指南。

你将找到解释的关键概念，以及一个工作示例，该示例涵盖从Java连接到此 NoSQL 数据库并开始使用该数据库的基本步骤。

## 延伸阅读：

## [使用 Cassandra、Astra 和 Stargate 构建仪表板](https://www.baeldung.com/cassandra-astra-stargate-dashboard)

了解如何使用 DataStax Astra 构建仪表板，DataStax Astra 是一种由 Apache Cassandra 和 Stargate API 提供支持的数据库即服务。

[阅读更多](https://www.baeldung.com/cassandra-astra-stargate-dashboard)→

## [使用 Cassandra、Astra、REST 和 GraphQL 构建仪表板——记录状态更新](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)

使用 Cassandra 存储时间序列数据的示例。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)→

## [使用 Cassandra、Astra 和 CQL 构建仪表板——映射事件数据](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)

了解如何根据存储在 Astra 数据库中的数据在交互式地图上显示事件。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)→



## 2.卡桑德拉

Cassandra 是一种可扩展的 NoSQL 数据库，可提供无单点故障的持续可用性，并能够以卓越的性能处理大量数据。

这个数据库使用环状设计，而不是使用主从架构。在环形设计中，没有主节点——所有参与节点都是相同的，并且作为对等节点相互通信。

这使得 Cassandra 成为一个水平可扩展的系统，允许在不需要重新配置的情况下增量添加节点。

### 2.1. 关键概念

让我们从对 Cassandra 的一些关键概念的简短调查开始：

-   集群——以环形架构排列的节点或数据中心的集合。必须为每个集群分配一个名称，随后将由参与节点使用
-   键空间——如果你来自关系数据库，那么模式就是 Cassandra 中的相应键空间。键空间是 Cassandra 中最外层的数据容器。每个键空间设置的主要属性是因子、副本放置策略和列族
-   列族——Cassandra 中的列族就像关系数据库中的表。每个 Column Family 包含一组行，这些行由Map<RowKey, SortedMap<ColumnKey, ColumnValue>> 表示。密钥提供了一起访问相关数据的能力
-   列——Cassandra 中的列是一种数据结构，包含列名、值和时间戳。与数据结构良好的关系数据库相比，每行中的列和列数可能会有所不同

## 3. 使用Java客户端

### 3.1. Maven 依赖

我们需要在pom.xml中定义以下 Cassandra 依赖项，最新版本可以在[这里](https://search.maven.org/classic/#search|ga|1|g%3A"com.datastax.cassandra")找到：

```xml
<dependency>
    <groupId>com.datastax.cassandra</groupId>
    <artifactId>cassandra-driver-core</artifactId>
    <version>3.1.0</version>
</dependency>
```

为了使用嵌入式数据库服务器测试代码，我们还应该添加cassandra-unit依赖项，其最新版本可在[此处](https://search.maven.org/classic/#search|ga|1|cassandra-unit)找到：

```xml
<dependency>
    <groupId>org.cassandraunit</groupId>
    <artifactId>cassandra-unit</artifactId>
    <version>3.0.0.1</version>
</dependency>
```

### 3.2. 连接到卡桑德拉

为了从Java连接到 Cassandra，我们需要构建一个Cluster对象。

需要提供节点的地址作为联系点。如果我们不提供端口号，将使用默认端口 (9042)。

这些设置允许驱动程序发现集群的当前拓扑。

```java
public class CassandraConnector {

    private Cluster cluster;

    private Session session;

    public void connect(String node, Integer port) {
        Builder b = Cluster.builder().addContactPoint(node);
        if (port != null) {
            b.withPort(port);
        }
        cluster = b.build();

        session = cluster.connect();
    }

    public Session getSession() {
        return this.session;
    }

    public void close() {
        session.close();
        cluster.close();
    }
}
```

### 3.3. 创建键空间

让我们创建我们的“库”键空间：

```java
public void createKeyspace(
  String keyspaceName, String replicationStrategy, int replicationFactor) {
  StringBuilder sb = 
    new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ")
      .append(keyspaceName).append(" WITH replication = {")
      .append("'class':'").append(replicationStrategy)
      .append("','replication_factor':").append(replicationFactor)
      .append("};");
        
    String query = sb.toString();
    session.execute(query);
}
```

除了keyspaceName我们还需要定义两个参数，replicationFactor和replicationStrategy。这些参数分别决定了副本的数量以及副本在环中的分布方式。

通过，Cassandra 通过在多个节点中存储数据副本来确保可靠性和容错性。

此时我们可以测试我们的键空间是否已成功创建：

```java
private KeyspaceRepository schemaRepository;
private Session session;

@Before
public void connect() {
    CassandraConnector client = new CassandraConnector();
    client.connect("127.0.0.1", 9142);
    this.session = client.getSession();
    schemaRepository = new KeyspaceRepository(session);
}
@Test
public void whenCreatingAKeyspace_thenCreated() {
    String keyspaceName = "library";
    schemaRepository.createKeyspace(keyspaceName, "SimpleStrategy", 1);

    ResultSet result = 
      session.execute("SELECT  FROM system_schema.keyspaces;");

    List<String> matchedKeyspaces = result.all()
      .stream()
      .filter(r -> r.getString(0).equals(keyspaceName.toLowerCase()))
      .map(r -> r.getString(0))
      .collect(Collectors.toList());

    assertEquals(matchedKeyspaces.size(), 1);
    assertTrue(matchedKeyspaces.get(0).equals(keyspaceName.toLowerCase()));
}
```

### 3.4. 创建列族

现在，我们可以将第一个列族“books”添加到现有的键空间中：

```java
private static final String TABLE_NAME = "books";
private Session session;

public void createTable() {
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
      .append(TABLE_NAME).append("(")
      .append("id uuid PRIMARY KEY, ")
      .append("title text,")
      .append("subject text);");

    String query = sb.toString();
    session.execute(query);
}
```

下面提供了用于测试列族是否已创建的代码：

```java
private BookRepository bookRepository;
private Session session;

@Before
public void connect() {
    CassandraConnector client = new CassandraConnector();
    client.connect("127.0.0.1", 9142);
    this.session = client.getSession();
    bookRepository = new BookRepository(session);
}
@Test
public void whenCreatingATable_thenCreatedCorrectly() {
    bookRepository.createTable();

    ResultSet result = session.execute(
      "SELECT  FROM " + KEYSPACE_NAME + ".books;");

    List<String> columnNames = 
      result.getColumnDefinitions().asList().stream()
      .map(cl -> cl.getName())
      .collect(Collectors.toList());
        
    assertEquals(columnNames.size(), 3);
    assertTrue(columnNames.contains("id"));
    assertTrue(columnNames.contains("title"));
    assertTrue(columnNames.contains("subject"));
}
```

### 3.5. 改变列族

一本书也有一个出版商，但是在创建的表中找不到这样的列。我们可以使用以下代码来更改表并添加新列：

```java
public void alterTablebooks(String columnName, String columnType) {
    StringBuilder sb = new StringBuilder("ALTER TABLE ")
      .append(TABLE_NAME).append(" ADD ")
      .append(columnName).append(" ")
      .append(columnType).append(";");

    String query = sb.toString();
    session.execute(query);
}
```

让我们确保已添加新的列发布者：

```java
@Test
public void whenAlteringTable_thenAddedColumnExists() {
    bookRepository.createTable();

    bookRepository.alterTablebooks("publisher", "text");

    ResultSet result = session.execute(
      "SELECT  FROM " + KEYSPACE_NAME + "." + "books" + ";");

    boolean columnExists = result.getColumnDefinitions().asList().stream()
      .anyMatch(cl -> cl.getName().equals("publisher"));
        
    assertTrue(columnExists);
}
```

### 3.6. 在列族中插入数据

现在已经创建了books表，我们准备开始向表中添加数据：

```java
public void insertbookByTitle(Book book) {
    StringBuilder sb = new StringBuilder("INSERT INTO ")
      .append(TABLE_NAME_BY_TITLE).append("(id, title) ")
      .append("VALUES (").append(book.getId())
      .append(", '").append(book.getTitle()).append("');");

    String query = sb.toString();
    session.execute(query);
}
```

“books”表中添加了一个新行，因此我们可以测试该行是否存在：

```java
@Test
public void whenAddingANewBook_thenBookExists() {
    bookRepository.createTableBooksByTitle();

    String title = "Effective Java";
    Book book = new Book(UUIDs.timeBased(), title, "Programming");
    bookRepository.insertbookByTitle(book);
        
    Book savedBook = bookRepository.selectByTitle(title);
    assertEquals(book.getTitle(), savedBook.getTitle());
}
```

在上面的测试代码中，我们使用了不同的方法来创建一个名为booksByTitle 的表：

```java
public void createTableBooksByTitle() {
    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
      .append("booksByTitle").append("(")
      .append("id uuid, ")
      .append("title text,")
      .append("PRIMARY KEY (title, id));");

    String query = sb.toString();
    session.execute(query);
}
```

在 Cassandra 中，最佳实践之一是使用一个查询一个表的模式。这意味着，对于不同的查询，需要不同的表。

在我们的示例中，我们选择按书名选择一本书。为了满足selectByTitle查询，我们使用列title和id创建了一个包含复合PRIMARY KEY的表。列标题是分区键，而id列是聚类键。

这样，数据模型中的许多表都包含重复数据。这不是该数据库的缺点。相反，这种做法优化了读取的性能。

让我们看看当前保存在我们表中的数据：

```java
public List<Book> selectAll() {
    StringBuilder sb = 
      new StringBuilder("SELECT  FROM ").append(TABLE_NAME);

    String query = sb.toString();
    ResultSet rs = session.execute(query);

    List<Book> books = new ArrayList<Book>();

    rs.forEach(r -> {
        books.add(new Book(
          r.getUUID("id"), 
          r.getString("title"),  
          r.getString("subject")));
    });
    return books;
}
```

查询返回预期结果的测试：

```java
@Test
public void whenSelectingAll_thenReturnAllRecords() {
    bookRepository.createTable();
        
    Book book = new Book(
      UUIDs.timeBased(), "Effective Java", "Programming");
    bookRepository.insertbook(book);
      
    book = new Book(
      UUIDs.timeBased(), "Clean Code", "Programming");
    bookRepository.insertbook(book);
        
    List<Book> books = bookRepository.selectAll(); 
        
    assertEquals(2, books.size());
    assertTrue(books.stream().anyMatch(b -> b.getTitle()
      .equals("Effective Java")));
    assertTrue(books.stream().anyMatch(b -> b.getTitle()
      .equals("Clean Code")));
}
```

到目前为止一切都很好，但必须意识到一件事。我们开始使用表books，但与此同时，为了满足按标题列的选择查询，我们不得不创建另一个名为booksByTitle 的表。

这两个表是相同的，包含重复的列，但我们只在booksByTitle表中插入了数据。因此，当前两个表中的数据不一致。

我们可以使用批处理查询来解决这个问题，它包含两个插入语句，每个表一个。批处理查询将多个 DML 语句作为单个操作执行。

提供了此类查询的示例：

```java
public void insertBookBatch(Book book) {
    StringBuilder sb = new StringBuilder("BEGIN BATCH ")
      .append("INSERT INTO ").append(TABLE_NAME)
      .append("(id, title, subject) ")
      .append("VALUES (").append(book.getId()).append(", '")
      .append(book.getTitle()).append("', '")
      .append(book.getSubject()).append("');")
      .append("INSERT INTO ")
      .append(TABLE_NAME_BY_TITLE).append("(id, title) ")
      .append("VALUES (").append(book.getId()).append(", '")
      .append(book.getTitle()).append("');")
      .append("APPLY BATCH;");

    String query = sb.toString();
    session.execute(query);
}
```

我们再次像这样测试批量查询结果：

```java
@Test
public void whenAddingANewBookBatch_ThenBookAddedInAllTables() {
    bookRepository.createTable();
        
    bookRepository.createTableBooksByTitle();
    
    String title = "Effective Java";
    Book book = new Book(UUIDs.timeBased(), title, "Programming");
    bookRepository.insertBookBatch(book);
    
    List<Book> books = bookRepository.selectAll();
    
    assertEquals(1, books.size());
    assertTrue(
      books.stream().anyMatch(
        b -> b.getTitle().equals("Effective Java")));
        
    List<Book> booksByTitle = bookRepository.selectAllBookByTitle();
    
    assertEquals(1, booksByTitle.size());
    assertTrue(
      booksByTitle.stream().anyMatch(
        b -> b.getTitle().equals("Effective Java")));
}
```

注意：从 3.0 版开始，一个名为“物化视图”的新功能可用，我们可以使用它来代替批量查询。[此处](http://www.datastax.com/dev/blog/new-in-cassandra-3-0-materialized-views)提供了一个详细记录的“物化视图”示例。

### 3.7. 删除列族

下面的代码显示了如何删除表：

```java
public void deleteTable() {
    StringBuilder sb = 
      new StringBuilder("DROP TABLE IF EXISTS ").append(TABLE_NAME);

    String query = sb.toString();
    session.execute(query);
}
```

选择键空间中不存在的表会导致InvalidQueryException: unconfigured table books：

```java
@Test(expected = InvalidQueryException.class)
public void whenDeletingATable_thenUnconfiguredTable() {
    bookRepository.createTable();
    bookRepository.deleteTable("books");
       
    session.execute("SELECT  FROM " + KEYSPACE_NAME + ".books;");
}
```

### 3.8. 删除键空间

最后，让我们删除键空间：

```java
public void deleteKeyspace(String keyspaceName) {
    StringBuilder sb = 
      new StringBuilder("DROP KEYSPACE ").append(keyspaceName);

    String query = sb.toString();
    session.execute(query);
}
```

并测试键空间是否已被删除：

```java
@Test
public void whenDeletingAKeyspace_thenDoesNotExist() {
    String keyspaceName = "library";
    schemaRepository.deleteKeyspace(keyspaceName);

    ResultSet result = 
      session.execute("SELECT  FROM system_schema.keyspaces;");
    boolean isKeyspaceCreated = result.all().stream()
      .anyMatch(r -> r.getString(0).equals(keyspaceName.toLowerCase()));
        
    assertFalse(isKeyspaceCreated);
}
```

## 4. 总结

本教程涵盖了使用Java连接和使用 Cassandra 数据库的基本步骤。为了帮助你快速入门，还讨论了该数据库的一些关键概念。