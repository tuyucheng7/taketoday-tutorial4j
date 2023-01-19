## 1. 简介

Apache Ignite 是一个开源的以内存为中心的分布式平台。我们可以将其用作数据库、缓存系统或用于内存中数据处理。

该平台使用内存作为存储层，因此具有令人印象深刻的性能。简而言之，这是目前生产使用中速度最快的原子数据处理平台之一。

## 2. 安装与设置

首先，请查看 [入门页面](https://apacheignite.readme.io/docs/getting-started) 以获取初始设置和安装说明。

我们要构建的应用程序的 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.ignite</groupId>
    <artifactId>ignite-core</artifactId>
    <version>${ignite.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.ignite</groupId>
    <artifactId>ignite-indexing</artifactId>
    <version>${ignite.version}</version>
</dependency>
```

[ignite-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.ignite" AND a%3A"ignite-core")是该项目唯一的强制依赖。由于我们还想与 SQL 交互，因此ignite-indexing也在这里。[${ignite.version}](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.ignite" AND a%3A"ignite-core")是 Apache Ignite 的最新版本。

作为最后一步，我们启动 Ignite 节点：

```plaintext
Ignite node started OK (id=53c77dea)
Topology snapshot [ver=1, servers=1, clients=0, CPUs=4, offheap=1.2GB, heap=1.0GB]
Data Regions Configured:
^-- default [initSize=256.0 MiB, maxSize=1.2 GiB, persistenceEnabled=false]
```

上面的控制台输出显示我们已准备就绪。

## 3.内存架构

该平台基于持久内存架构。这使得能够在磁盘和内存中存储和处理数据。它通过有效地使用集群的 RAM 资源来提高性能。

内存中和磁盘上的数据具有相同的二进制表示。这意味着在从一层移动到另一层时不会对数据进行额外的转换。

持久内存架构分为固定大小的块，称为页面。页面存储在Java堆之外，并组织在 RAM 中。它有一个唯一标识符：FullPageId。

页面使用PageMemory抽象与内存交互 。

它有助于读取、写入页面，也有助于分配页面 ID。在内存内部，Ignite 将页面与内存缓冲区相关联。

## 4.内存页

页面可以具有以下状态：

-   Unloaded——内存中没有加载页面缓冲区
-   清除——页面缓冲区被加载并与磁盘上的数据同步
-   Durty——页面缓冲区保存的数据与磁盘中的数据不同
-   检查点脏——在第一个修改保存到磁盘之前，另一个修改开始了。此处开始检查点，PageMemory为每个 Page 保留两个内存缓冲区。

持久内存在本地分配一个称为数据区域的内存段。默认情况下，它的容量为集群内存的 20%。多区域配置允许将可用数据保存在内存中。

该区域的最大容量是一个Memory Segment。它是物理内存或连续字节数组。

为了避免内存碎片，单个页面包含多个键值条目。每个新条目都将添加到最佳页面。如果键值对大小超过页面的最大容量，Ignite 会将数据存储在多个页面中。相同的逻辑适用于更新数据。

SQL 和缓存索引存储在称为 B+ 树的结构中。缓存键按其键值排序。

## 5. 生命周期

每个 Ignite 节点都在单个 JVM 实例上运行。但是，可以配置为在单个 JVM 进程中运行多个 Ignite 节点。

让我们来看看生命周期事件类型：

-   BEFORE_NODE_START——在 Ignite 节点启动之前
-   AFTER_NODE_START – 在 Ignite 节点启动后触发
-   BEFORE_NODE_STOP – 在启动节点停止之前
-   AFTER_NODE_STOP——在 Ignite 节点停止之后

要启动默认的 Ignite 节点：

```java
Ignite ignite = Ignition.start();
```

或者从配置文件：

```java
Ignite ignite = Ignition.start("config/example-cache.xml");
```

如果我们需要对初始化过程进行更多控制，可以借助LifecycleBean接口使用另一种方法：

```java
public class CustomLifecycleBean implements LifecycleBean {
 
    @Override
    public void onLifecycleEvent(LifecycleEventType lifecycleEventType) 
      throws IgniteException {
 
        if(lifecycleEventType == LifecycleEventType.AFTER_NODE_START) {
            // ...
        }
    }
}
```

在这里，我们可以使用生命周期事件类型在节点启动/停止之前或之后执行操作。

为此，我们将带有CustomLifecycleBean的配置实例传递给启动方法：

```java
IgniteConfiguration configuration = new IgniteConfiguration();
configuration.setLifecycleBeans(new CustomLifecycleBean());
Ignite ignite = Ignition.start(configuration);
```

## 6. 内存数据网格

Ignite data grid 是一个分布式的 key-value 存储，很熟悉分区的HashMap。它是水平缩放的。这意味着我们添加了更多的集群节点，更多的数据被缓存或存储在内存中。

它可以为第 3 方软件(如 NoSql、RDMS 数据库)提供显着的性能改进，作为缓存的附加层。

### 6.1. 缓存支持

数据访问 API 基于 JCache JSR 107 规范。

例如，让我们使用模板配置创建一个缓存：

```java
IgniteCache<Employee, Integer> cache = ignite.getOrCreateCache(
  "baeldingCache");
```

让我们看看这里发生了什么以获取更多详细信息。首先，Ignite 找到缓存所在的内存区域。

然后根据key hash code定位B+树索引Page。如果索引存在，则定位到对应key的数据页。

当索引为 NULL 时，平台使用给定的键创建新的数据条目。

接下来，让我们添加一些Employee对象：

```java
cache.put(1, new Employee(1, "John", true));
cache.put(2, new Employee(2, "Anna", false));
cache.put(3, new Employee(3, "George", true));
```

同样，持久内存将查找缓存所属的内存区域。基于缓存键，索引页将位于 B+ 树结构中。

当索引页不存在时，将请求一个新页并将其添加到树中。

接下来，将数据页分配给索引页。

要从缓存中读取员工，我们只需使用键值：

```java
Employee employee = cache.get(1);
```

### 6.2. 流媒体支持

内存数据流为基于磁盘和文件系统的数据处理应用程序提供了另一种方法。Streaming API 将高负载数据流拆分为多个阶段并路由它们进行处理。

我们可以修改示例并从文件中流式传输数据。首先，我们定义一个数据流送器：

```java
IgniteDataStreamer<Integer, Employee> streamer = ignite
  .dataStreamer(cache.getName());
```

接下来，我们可以注册一个流转换器来将接收到的员工标记为已雇用：

```java
streamer.receiver(StreamTransformer.from((e, arg) -> {
    Employee employee = e.getValue();
    employee.setEmployed(true);
    e.setValue(employee);
    return employee;
}));
```

作为最后一步，我们遍历employees.txt文件行并将它们转换为Java对象：

```java
Path path = Paths.get(IgniteStream.class.getResource("employees.txt")
  .toURI());
Gson gson = new Gson();
Files.lines(path)
  .forEach(l -> streamer.addData(
    employee.getId(), 
    gson.fromJson(l, Employee.class)));
```

使用streamer.addData()将员工对象放入流中。

## 7. SQL 支持

该平台提供以内存为中心的容错SQL数据库。

我们可以使用纯 SQL API 或 JDBC 进行连接。这里的SQL语法是ANSI-99，所以支持查询中所有的标准聚合函数，DML，DDL语言操作。

### 7.1. JDBC

为了更加实用，让我们创建一个员工表并向其中添加一些数据。

为此，我们注册一个 JDBC 驱动程序并打开一个连接作为下一步：

```java
Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1/");
```

在标准 DDL 命令的帮助下，我们填充Employee表：

```java
sql.executeUpdate("CREATE TABLE Employee (" +
  " id LONG PRIMARY KEY, name VARCHAR, isEmployed tinyint(1)) " +
  " WITH "template=replicated"");
```

在WITH关键字之后，我们可以设置缓存配置模板。这里我们使用REPLICATED。默认情况下，模板模式是PARTITIONED。要指定数据的副本数，我们还可以在此处指定BACKUPS参数，默认为 0。

然后，让我们使用 INSERT DML 语句添加一些数据：

```java
PreparedStatement sql = conn.prepareStatement(
  "INSERT INTO Employee (id, name, isEmployed) VALUES (?, ?, ?)");

sql.setLong(1, 1);
sql.setString(2, "James");
sql.setBoolean(3, true);
sql.executeUpdate();

// add the rest

```

之后，我们选择记录：

```java
ResultSet rs 
  = sql.executeQuery("SELECT e.name, e.isEmployed " 
    + " FROM Employee e " 
    + " WHERE e.isEmployed = TRUE ")
```

### 7.2. 查询对象

也可以对存储在缓存中的Java对象执行查询。Ignite 将Java对象视为单独的 SQL 记录：

```java
IgniteCache<Integer, Employee> cache = ignite.cache("baeldungCache");

SqlFieldsQuery sql = new SqlFieldsQuery(
  "select name from Employee where isEmployed = 'true'");

QueryCursor<List<?>> cursor = cache.query(sql);

for (List<?> row : cursor) {
    // do something with the row
}
```

## 8.总结

在本教程中，我们快速浏览了 Apache Ignite 项目。本指南重点介绍了该平台相对于其他类似产品的优势，例如性能提升、耐用性和轻量级 API。

因此，我们学习了如何使用 SQL 语言和JavaAPI 在持久性或内存网格中存储、检索和流式传输数据。