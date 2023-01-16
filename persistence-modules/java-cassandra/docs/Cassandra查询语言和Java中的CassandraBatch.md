## 1. 概述

在本教程中，我们将了解[Cassandra](https://www.baeldung.com/cassandra-with-java)批处理查询及其不同用例。我们将分析单个分区和多个分区表的批处理查询。

我们将探索Cqlsh和Java应用程序中的批处理。

## 2. Cassandra 批处理基础

与关系数据库不同，像 Cassandra 这样的分布式数据库不支持[ACID](https://www.baeldung.com/cs/transactions-intro#1-acid-properties)(原子性、一致性、隔离性和持久性)属性。不过，在某些情况下，我们需要多次数据修改才能成为原子操作或/和隔离操作。

批处理语句组合了多种数据修改语言语句(如INSERT、UPDATE、DELETE)，在针对单个分区时实现原子性和隔离性，在针对多个分区时仅实现原子性。

下面是批量查询的语法：

```sql
BEGIN [ ( UNLOGGED | COUNTER ) ] BATCH
[ USING TIMESTAMP [ epoch_microseconds ] ]
dml_statement [ USING TIMESTAMP [ epoch_microseconds ] ] ;
[ dml_statement [ USING TIMESTAMP [ epoch_microseconds ] ] [ ; ... ] ]
APPLY BATCH;
```

让我们通过一个例子来看一下上面的语法：

```apache
BEGIN BATCH 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f3,'banana'); 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f5,'banana'); 

APPLY BATCH;
```

首先，我们使用不带任何可选参数(如 UNLOGGED 或USING TIMESTAMP )的BEGIN BATCH语句来启动批量查询，然后包括所有DML 操作，即product表的insert 语句。

最后，我们使用APPLY BATCH语句来执行批处理。

我们应该注意，我们将无法撤消任何批查询，因为批查询不支持回滚功能。

### 2.1. 单一分区

批处理语句在单个分区内应用所有 DML 语句，确保原子性和隔离性。

针对单个分区的精心设计的批处理可以减少客户端-服务器流量，并更有效地更新具有单行突变的表。这是因为仅当批处理操作写入单个分区时才会发生批处理隔离。

单个分区批处理还可以涉及具有相同分区键并存在于相同键空间中的两个不同表。

默认情况下，单分区批处理操作不记录日志，因此不会因日志记录而受到性能损失。

下图描述了从协调节点H到分区节点B及其节点C和D的单分区批处理请求流程：

[![单分区](https://www.baeldung.com/wp-content/uploads/2022/01/single-partition.png)](https://www.baeldung.com/wp-content/uploads/2022/01/single-partition.png)

礼貌：Datastax

### 2.2. 多个分区

涉及多个分区的批处理需要精心设计，因为它涉及多个节点之间的协调。多分区批处理的最佳用例是将相同的数据写入两个相关的表，即两个表具有相同的列但分区键不同。

多分区批量操作使用batchlog机制保证原子性。协调节点向批处理日志节点发送批处理日志请求，一旦收到确认，它就会执行批处理语句。然后，它从节点中删除batchlog 并向客户端发送确认。

建议避免使用多分区批量查询。这是因为此类查询给协调节点带来巨大压力并严重影响其性能。

当没有其他可行的选择时，我们应该只使用多分区批处理。

下图描述了从协调节点H到分区节点B、E及其各自的节点C、D和F、G的多分区批处理请求流：

[![多分区](https://www.baeldung.com/wp-content/uploads/2022/01/multi-partition.png)](https://www.baeldung.com/wp-content/uploads/2022/01/multi-partition.png)

礼貌：Datastax

## 3. Cqlsh中的批量执行

首先，让我们创建一个产品 表来运行一些批处理查询：

```sql
CREATE TABLE product (
  product_id UUID,
  variant_id UUID,
  product_name text,
  description text,
  price float,
  PRIMARY KEY (product_id, variant_id)
  );
```

###  3.1. 没有时间戳的单个分区批处理

我们将针对产品表的单个分区执行以下批量查询，并且不会提供时间戳：

```sql
BEGIN BATCH 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f3,'banana') IF NOT EXISTS; 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f5,'banana') IF NOT EXISTS; 

UPDATE product SET price = 7.12, description = 'banana v1' 
WHERE product_id = 2c11bbcd-4587-4d15-bb57-4b23a546bd7f AND variant_id=0e9ef8f7-d32b-4926-9d37-27225933a5f3; 

UPDATE product SET price = 11.90, description = 'banana v2' 
WHERE product_id = 2c11bbcd-4587-4d15-bb57-4b23a546bd7f AND variant_id=0e9ef8f7-d32b-4926-9d37-27225933a5f5; 

APPLY BATCH;
```

上述查询使用了比较并设置(CAS)逻辑，即IF NOT EXISTS 子句，所有此类条件语句都必须返回true才能执行批处理。如果任何此类语句返回false，则整个批次未处理。

执行上述查询后，我们将获得以下成功确认：

[![使用 BatchExamplesAck](https://www.baeldung.com/wp-content/uploads/2022/01/useBatchExamplesAck.png)](https://www.baeldung.com/wp-content/uploads/2022/01/useBatchExamplesAck.png)

现在我们来验证批量执行后数据的写入时间是否相同：

```sql
cqlsh:testkeyspace> select product_id, variant_id, product_name, description, price, writetime(product_name) from product;

@ Row 1
-------------------------+--------------------------------------
product_id | 3a043b68-20ee-4ece-8f4b-a07e704bc9f5
variant_id | b84b9366-9998-4b2d-9a96-7e9a59a94ae5
product_name | Banana
description | banana v1
price | 12
writetime(product_name) | 1639275574653000

@ Row 2
-------------------------+--------------------------------------
product_id | 3a043b68-20ee-4ece-8f4b-a07e704bc9f5
variant_id | facc3997-299d-419b-b133-a54b5d4dfc3b
product_name | Banana
description | banana v2
price | 12
writetime(product_name) | 1639275574653000
```

###  3.2. 带时间戳的单分区批处理

我们现在将看到使用 USING TIMESTAMP选项以纪元时间格式(即微秒)提供时间戳的批查询示例。

下面是对所有 DML 语句应用相同时间戳的批量查询：

```sql
BEGIN BATCH USING TIMESTAMP 1638810270 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f3,'banana'); 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f5,'banana'); 

UPDATE product SET price = 7.12, description = 'banana v1' 
WHERE product_id = 2c11bbcd-4587-4d15-bb57-4b23a546bd7f AND variant_id=0e9ef8f7-d32b-4926-9d37-27225933a5f3; 

UPDATE product SET price = 11.90, description = 'banana v2' 
WHERE product_id = 2c11bbcd-4587-4d15-bb57-4b23a546bd7f AND variant_id=0e9ef8f7-d32b-4926-9d37-27225933a5f5; 

APPLY BATCH;
```

现在让我们在任何单独的 DML 语句上指定自定义时间戳：

```sql
BEGIN BATCH 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f3,'banana');

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f5,'banana') USING TIMESTAMP 1638810270; 

UPDATE product SET price = 7.12, description = 'banana v1' 
WHERE product_id = 2c11bbcd-4587-4d15-bb57-4b23a546bd7f AND variant_id=0e9ef8f7-d32b-4926-9d37-27225933a5f3 USING TIMESTAMP 1638810270; 

UPDATE product SET price = 11.90, description = 'banana v2' 
WHERE product_id = 2c11bbcd-4587-4d15-bb57-4b23a546bd7f AND variant_id=0e9ef8f7-d32b-4926-9d37-27225933a5f5; 

APPLY BATCH;
```

我们现在将看到一个无效的批处理查询，它同时具有自定义时间戳和比较并设置 (CAS)逻辑，即IF NOT EXISTS子句：

```sql
BEGIN BATCH USING TIMESTAMP 1638810270 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f3,'banana') IF NOT EXISTS; 

INSERT INTO product (product_id, variant_id, product_name) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,0e9ef8f7-d32b-4926-9d37-27225933a5f5,'banana') IF NOT EXISTS; 

UPDATE product SET price = 7.12, description = 'banana v1' 
WHERE product_id = 2c11bbcd-4587-4d15-bb57-4b23a546bd7f AND variant_id=0e9ef8f7-d32b-4926-9d37-27225933a5f3; 

UPDATE product SET price = 11.90, description = 'banana v2' 
WHERE product_id = 2c11bbcd-4587-4d15-bb57-4b23a546bd7f AND variant_id=0e9ef8f7-d32b-4926-9d37-27225933a5f5; 

APPLY BATCH;
```

我们将在执行上述查询时得到以下错误：

```bash
InvalidRequest: Error from server: code=2200 [Invalid query]
message="Cannot provide custom timestamp for conditional BATCH"
```

上述错误是因为客户端时间戳被禁止用于任何条件插入或更新。

### 3.3. 多分区批量查询

在多个分区上进行批处理的最佳用例 是将准确的数据插入到两个相关表中。

让我们将相同的数据插入到具有不同分区键的product_by_name和product_by_id表中：

```sql
BEGIN BATCH 

INSERT INTO product_by_name (product_name, product_id, description, price) 
VALUES ('banana',2c11bbcd-4587-4d15-bb57-4b23a546bd7f,'banana',12.00); 

INSERT INTO product_by_id (product_id, product_name, description, price) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,'banana','banana',12.00); 

APPLY BATCH;
```

现在让我们为上面的查询启用UNLOGGED选项：

```sql
BEGIN UNLOGGED BATCH 

INSERT INTO product_by_name (product_name, product_id, description, price) 
VALUES ('banana',2c11bbcd-4587-4d15-bb57-4b23a546bd7f,'banana',12.00); 

INSERT INTO product_by_id (product_id, product_name, description, price) 
VALUES (2c11bbcd-4587-4d15-bb57-4b23a546bd7f,'banana','banana',12.00); 

APPLY BATCH;
```

上面的UNLOGGED批量查询不会保证原子性或隔离性，也不会使用批量日志 写入数据。

### 3.4. 对计数器更新进行批处理

我们需要对任何[计数器](https://www.baeldung.com/cassandra-data-types)列使用COUNTER选项，因为计数器更新操作不是幂等的。

让我们创建一个表product_by_sales ，它将sales_vol存储为Counter数据类型：

```sas
CREATE TABLE product_by_sales (
  product_id UUID,
  sales_vol counter,
  PRIMARY KEY (product_id)
);
```

下面的计数器批量查询将sales_vol增加两次 100：

```sql
BEGIN COUNTER BATCH

UPDATE product_by_sales
SET sales_vol = sales_vol + 100
WHERE product_id = 6ab09bec-e68e-48d9-a5f8-97e6fb4c9b47;

UPDATE product_by_sales
SET sales_vol = sales_vol + 100
WHERE product_id = 6ab09bec-e68e-48d9-a5f8-97e6fb4c9b47;

APPLY BATCH
```

## 4. Java中的批量操作

让我们看几个在Java应用程序中构建和执行批处理查询的示例。

### 4.1. Maven 依赖

首先，我们需要包含与[DataStax](https://www.baeldung.com/cassandra-datastax-java-driver)相关的 Maven 依赖项：

```xml
<dependency>
    <groupId>com.datastax.oss</groupId>
    <artifactId>java-driver-core</artifactId>
    <version>4.1.0</version>
</dependency>
<dependency>
   <groupId>com.datastax.oss</groupId>
   <artifactId>java-driver-query-builder</artifactId>
   <version>4.1.0</version>
</dependency>
```

### 4.2. 单个分区批处理

让我们看一个例子，看看如何对单分区数据执行批处理。

我们将使用BatchStatement实例构建批处理查询。BatchStatement 使用DefaultBatchType枚举和BoundStatement实例进行实例化。

首先，我们将创建一个方法来通过将Product属性绑定到PreparedStatement插入查询来获取BoundStatement实例：

```java
BoundStatement getProductVariantInsertStatement(Product product, UUID productId) {
    String insertQuery = new StringBuilder("") 
      .append("INSERT INTO ")
      .append(PRODUCT_TABLE_NAME)
      .append("(product_id, variant_id, product_name, description, price) ")
      .append("VALUES (")
      .append(":product_id")
      .append(", ")
      .append(":variant_id")
      .append(", ")
      .append(":product_name")
      .append(", ")
      .append(":description")
      .append(", ")
      .append(":price")
      .append(");")
      .toString();

    PreparedStatement preparedStatement = session.prepare(insertQuery);
        
    return preparedStatement.bind(
      productId, 
      UUID.randomUUID(),
      product.getProductName(), 
      product.getDescription(),
      product.getPrice());
}
```

现在，我们将使用相同的Product UUID为上面创建的BoundStatement执行BatchStatement：

```java
UUID productId = UUID.randomUUID();
BoundStatement productBoundStatement1 = this.getProductVariantInsertStatement(productVariant1, productId);
BoundStatement productBoundStatement2 = this.getProductVariantInsertStatement(productVariant2, productId);
        
BatchStatement batch = BatchStatement.newInstance(DefaultBatchType.UNLOGGED,
            productBoundStatement1, productBoundStatement2);

session.execute(batch);
```

上面的代码使用UNLOGGED批处理在同一个分区键上插入两个产品变体。

### 4.3. 多分区批处理

现在，让我们看看如何将相同的数据插入到两个相关的表中——product_by_id和product_by_name 。

首先，我们将创建一个可重用的方法来为PreparedStatement插入查询获取BoundStatement实例：

```java
BoundStatement getProductInsertStatement(Product product, UUID productId, String productTableName) {
    String cqlQuery1 = new StringBuilder("")
      .append("INSERT INTO ")
      .append(productTableName)
      .append("(product_id, product_name, description, price) ")
      .append("VALUES (")
      .append(":product_id")
      .append(", ")
      .append(":product_name")
      .append(", ")
      .append(":description")
      .append(", ")
      .append(":price")
      .append(");")
      .toString();

    PreparedStatement preparedStatement = session.prepare(cqlQuery1);
        
    return preparedStatement.bind(
      productId,
      product.getProductName(),
      product.getDescription(),
      product.getPrice());
}
```

现在，我们将使用相同的Product UUID执行BatchStatement ：

```java
UUID productId = UUID.randomUUID();
        
BoundStatement productBoundStatement1 = this.getProductInsertStatement(product, productId, PRODUCT_BY_ID_TABLE_NAME);
BoundStatement productBoundStatement2 = this.getProductInsertStatement(product, productId, PRODUCT_BY_NAME_TABLE_NAME);
        
BatchStatement batch = BatchStatement.newInstance(DefaultBatchType.LOGGED,
            productBoundStatement1,productBoundStatement2);

session.execute(batch);
```

这将使用LOGGED批处理将相同的产品数据插入到product_by_id和product_by_name表中。

## 5.总结

在本文中，我们了解了 Cassandra 批处理查询以及如何使用BatchStatement在Cqlsh 和Java中应用它。