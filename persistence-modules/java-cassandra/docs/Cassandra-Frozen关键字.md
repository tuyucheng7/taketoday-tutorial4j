## 1. 概述

在本教程中，我们将讨论[Apache Cassandra 数据库中的](https://cassandra.apache.org/_/index.html)frozen关键字。首先，我们将展示如何声明冻结集合或用户定义类型 (UDT) 。接下来，我们将讨论使用示例以及它如何影响持久存储的基本操作。

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

```bash
CREATE KEYSPACE mykeyspace WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
```

对于本教程，我们创建了一个只有一个数据副本的键空间 。现在，让我们将客户端会话连接到一个键空间：

```bash
USE mykeyspace;
```

## 3. 冻结集合类型

类型为冻结集合(set、map或list)的列只能将其值作为一个整体替换。换句话说，我们不能像在非冻结集合类型中那样从集合中添加、更新或删除单个元素。因此，frozen关键字很有用，例如，当我们想要保护集合免受单值更新时。

此外，由于冻结，我们可以将冻结的集合用作表中的主键。我们可以使用set、list或map等集合类型来声明集合列。然后我们添加集合类型。

要声明一个冻结的集合，我们必须在集合定义之前添加关键字：

```sql
CREATE TABLE mykeyspace.users
(
    id         uuid PRIMARY KEY,
    ip_numbers frozen<set<inet>>,
    addresses  frozen<map<text, tuple<text>>>,
    emails     frozen<list<varchar>>,
);
```

让我们插入一些数据：

```sql
INSERT INTO mykeyspace.users (id, ip_numbers)
VALUES (6ab09bec-e68e-48d9-a5f8-97e6fb4c9b47, {'10.10.11.1', '10.10.10.1', '10.10.12.1'});
```

重要的是，正如我们上面提到的，冻结的集合只能作为一个整体进行替换。这意味着我们不能添加或删除元素。让我们尝试向ip_numbers集合添加一个新元素：

```sql
UPDATE mykeyspace.users
SET ip_numbers = ip_numbers + {'10.10.14.1'}
WHERE id = 6ab09bec-e68e-48d9-a5f8-97e6fb4c9b47;

```

执行更新后，我们会得到错误：

```bash
InvalidRequest: Error from server: code=2200 [Invalid query] message="Invalid operation (ip_numbers = ip_numbers + {'10.10.14.1'}) for frozen collection column ip_numbers"
```

如果我们想更新我们集合中的数据，我们需要更新整个集合：

```bash
UPDATE mykeyspace.users
SET ip_numbers = {'11.10.11.1', '11.10.10.1', '11.10.12.1'}
WHERE id = 6ab09bec-e68e-48d9-a5f8-97e6fb4c9b47;

```

### 3.1. 嵌套集合

有时我们不得不在 Cassandra 数据库中使用嵌套集合。嵌套集合只有在我们将它们标记为frozen时才有可能。这意味着这个集合将是不可变的。我们可以冻结冻结和非冻结集合中的嵌套集合。让我们看一个例子：

```sql
CREATE TABLE mykeyspace.users_score
(
    id    uuid PRIMARY KEY,
    score set<frozen<set<int>>>
);
```

## 4.冻结用户自定义类型

用户定义的类型 (UDT) 可以将多个数据字段(每个字段都命名和类型化)附加到单个列。用于创建用户定义类型的字段可以是任何有效的数据类型，包括集合或其他 UDT。让我们创建我们的 UDT：

```sql
CREATE TYPE mykeyspace.address (
    city text,
    street text,
    streetNo int,
    zipcode text
);

```

让我们看看冻结 的用户定义类型的声明：

```sql
CREATE TABLE mykeyspace.building
(
    id      uuid PRIMARY KEY,
    address frozen<address>
);
```

当我们在用户定义的类型上使用frozen时，Cassandra 将值视为一个 blob。这个 blob 是通过将我们的 UDT 序列化为单个值获得的。所以，我们不能更新用户定义类型值的一部分。我们必须覆盖整个值。

首先，让我们插入一些数据：

```sql
INSERT INTO mykeyspace.building (id, address)
VALUES (6ab09bec-e68e-48d9-a5f8-97e6fb4c9b48,
  {city: 'City', street: 'Street', streetNo: 2,zipcode: '02-212'});
```

让我们看看当我们尝试只更新一个字段时会发生什么：

```sql
UPDATE mykeyspace.building
SET address.city = 'City2'
WHERE id = 6ab09bec-e68e-48d9-a5f8-97e6fb4c9b48;

```

我们将再次收到错误：

```bash
InvalidRequest: Error from server: code=2200 [Invalid query] message="Invalid operation (address.city = 'City2') for frozen UDT column address"
```

因此，让我们更新整个值：

```sql
UPDATE mykeyspace.building
SET address = {city : 'City2', street : 'Street2'}
WHERE id = 6ab09bec-e68e-48d9-a5f8-97e6fb4c9b48;
```

这一次，地址将被更新。未包含在查询中的字段用空值完成。

## 5. 元组

与其他组合类型不同，元组始终是冻结的。因此，我们不必用frozen关键字来标记元组。因此，不可能只更新元组的某些元素。与冻结集合或 UDT 的情况一样，我们必须覆盖整个值。

## 六. 总结

在本快速教程中，我们探索了 Cassandra 数据库中冻结组件的基本概念。接下来，我们创建了冻结集合和用户定义类型。然后，我们检查了这些数据结构的行为。之后，我们谈到了元组数据类型。