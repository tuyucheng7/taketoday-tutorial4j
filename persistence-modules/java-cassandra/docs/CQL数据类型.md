## 1. 概述

在本教程中，我们将展示 Apache Cassandra 数据库的一些不同数据类型。[Apache Cassandra](https://www.baeldung.com/cassandra-with-java)支持一组丰富的数据类型，包括集合类型、本机类型、元组类型和用户定义类型。

Cassandra 查询语言 (CQL) 是结构化查询语言 (SQL) 的简单替代方案。它是一种声明性语言，旨在提供与其数据库的通信。与 SQL 类似，CQL 也将数据存储在表中，并将数据组织成行和列。

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



## 2. Cassandra数据库配置

[让我们使用 docker镜像](https://github.com/bitnami/bitnami-docker-cassandra)创建一个数据库，并使用cqlsh将其连接到数据库。接下来，我们应该创建一个键空间：

```shell
CREATE KEYSPACE baeldung WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
```

出于本教程的目的，我们创建了一个只有一个数据副本的键空间 。现在，让我们将客户端会话连接到一个键空间：

```powershell
USE <code class="language-shell">baeldung;
```

## 3.内置数据类型

CQL 支持一组丰富的本机数据类型。这些数据类型是预定义的，我们可以直接引用它们中的任何一个。

### 3.1. 数值类型

数字类型类似于Java和其他语言中的标准类型，例如具有不同范围的整数或浮点数：

[![数字](https://www.baeldung.com/wp-content/uploads/2021/07/numeric.png)](https://www.baeldung.com/wp-content/uploads/2021/07/numeric.png)

让我们创建一个包含所有这些数据类型的表：

```sql
CREATE TABLE numeric_types
(
    type1 int PRIMARY KEY,
    type2 bigint,
    type3 smallint,
    type4 tinyint,
    type5 varint,
    type6 float,
    type7 double,
    type8 decimal
);
```

### 3.2. 文本类型

CQL 提供了两种数据类型来表示文本。我们可以使用 text 或 varchar 创建一个 UTF-8 字符串。UTF-8 是更新和广泛使用的文本标准，支持国际化。

还有 ascii 类型创建一个 ASCII 字符串。如果我们处理的是 ASCII 格式的遗留数据，则 ascii 类型最有用。文本值的大小受列的最大大小限制。单列值大小为 2 GB，但建议仅为 1 MB。

让我们创建一个包含所有这些数据类型的表：

```sql
CREATE TABLE text_types
(
    primaryKey int PRIMARY KEY,
    type2      text,
    type3      varchar,
    type4      ascii
);
```

### 3.3. 日期类型

现在，让我们谈谈日期类型。Cassandra 提供了几种类型，这些类型在定义唯一分区键或定义普通列时非常有用：

[![日期2](https://www.baeldung.com/wp-content/uploads/2021/07/date2.png)](https://www.baeldung.com/wp-content/uploads/2021/07/date2.png)

 

time did 由[UUID 版本 1](https://en.wikipedia.org/wiki/Universally_unique_identifier)表示。我们可以将整数或字符串输入到 CQL 时间戳、时间和日期。持续时间类型的值被编码为 3 个有符号整数。

第一个整数表示月数，第二个整数表示天数，第三个整数表示纳秒数。

让我们看一个创建表命令的例子：

```sql
CREATE TABLE date_types
(
    primaryKey int PRIMARY KEY,
    type1      timestamp,
    type2      time,
    type3      date,
    type4      timeuuid,
    type5      duration
);
```

### 3.4. 计数器类型

计数器类型用于定义计数器列。计数器列是其值为 64 位带符号整数的列。我们只能对计数器列执行两个操作——递增和递减。

因此，我们不能给计数器设置值。我们可以使用计数器来跟踪统计信息，例如页面浏览量、推文、日志消息等。我们不能将计数器类型与其他类型混合使用。

让我们看一个例子：

```sql
CREATE TABLE counter_type
(
    primaryKey uuid PRIMARY KEY,
    type1      counter
);
```

### 3.5. 其他数据类型

-   布尔值是一个简单的真/假值
-   uuid 是 Type 4 UUID，它完全基于随机数。我们可以使用破折号分隔的十六进制数字序列输入 UUID
-   二进制大对象 (blob) 是任意字节数组的通俗计算术语。CQL blob 类型存储媒体或其他二进制文件类型。最大 blob 大小为 2 GB，但建议小于 1 MB。
-   inet 是表示IPv4或IPv6 Internet 地址的类型

同样，让我们创建一个具有这些类型的表：

```sql
CREATE TABLE other_types
(
    primaryKey int PRIMARY KEY,
    type1      boolean,
    type2      uuid,
    type3      blob,
    type4      inet
);
```

## 4. 集合数据类型

有时我们希望存储相同类型的数据而不生成新的列。集合可以存储多个值。CQL 提供了三种集合类型来帮助我们，例如列表、集合和映射。

例如，我们可以创建一个包含文本元素列表、整数列表或其他一些元素类型列表的表。

### 4.1. 放

我们可以使用 set 数据类型存储多个唯一值。同样，在 Java中，元素不是按顺序存储的。

让我们创建一个集合：

```sql
CREATE TABLE collection_types
(
    primaryKey int PRIMARY KEY,
    email      set<text>
);
```

### 4.2. 列表

在这种数据类型中，值以列表的形式存储。我们不能改变元素的顺序。将值存储在列表中后，元素将获得特定的索引。我们可以使用这些索引来检索数据。

与集合不同，列表可以存储重复值。让我们在表中添加一个列表：

```sql
ALTER TABLE collection_types
    ADD scores list<text>;
```

### 4.3. 地图

使用 Cassandra ，我们可以使用 map 数据类型将数据存储在键值对集中。密钥是唯一的。因此，我们可以按键对地图进行排序。

让我们在表中添加另一列：

```sql
ALTER TABLE collection_types
    ADD address map<uuid, text>;
```

## 5. 元组

元组是一组不同类型的元素。这些集合具有固定长度：

```sql
CREATE TABLE tuple_type
(
    primaryKey int PRIMARY KEY,
    type1 tuple<int, text, float>
);
```

## 6. 用户自定义数据类型

Cassandra 提供了创建我们自己的数据类型的可能性。我们可以创建、修改和删除这些数据类型。首先，让我们创建自己的类型：

```sql
CREATE TYPE user_defined_type (
    type1 timestamp,
    type2 text,
    type3 text,
    type4 text);
```

所以，现在我们可以用我们的类型创建一个表：

```sql
CREATE TABLE user_type
(
    primaryKey int PRIMARY KEY,
    our_type   user_defined_type
);
```

## 七. 总结

在本快速教程中，我们探索了基本的 CQL 数据类型。此外，我们还创建了具有这些数据类型的表。之后，我们谈到了它们可以存储什么样的数据。