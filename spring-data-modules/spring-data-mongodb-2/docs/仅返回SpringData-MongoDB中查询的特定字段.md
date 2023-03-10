## 1. 概述

在使用[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)时，我们可能需要限制从数据库对象映射的属性。通常，出于安全原因，我们可能需要这样做-以避免暴露存储在服务器上的敏感信息。或者，例如我们可能需要过滤掉Web应用程序中显示的部分数据。

在这个简短的教程中，我们将了解MongoDB如何应用字段限制。

## 2. 使用投影的MongoDB字段限制

**MongoDB使用[投影](https://www.mongodb.com/docs/manual/tutorial/project-fields-from-query-results/)来指定或限制从查询返回的字段**。但是，如果我们使用Spring Data，我们希望将其与[MongoTemplate](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html)或[MongoRepository](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/repository/MongoRepository.html)一起应用。

因此，我们希望为MongoTemplate和MongoRepository创建测试用例，我们可以在其中应用字段限制。

## 3. 实现投影

### 3.1 设置实体

首先，让我们创建一个Inventory类：

```java
@Document(collection = "inventory")
public class Inventory {

    @Id
    private String id;
    private String status;
    private Size size;
    private InStock inStock;

    // standard getters and setters    
}
```

### 3.2 设置Repository

然后，为了测试MongoRepository，我们创建了一个InventoryRepository。我们还将使用带有@Query的where条件。例如，我们要过滤库存状态：

```java
public interface InventoryRepository extends MongoRepository<Inventory, String> {

    @Query(value = "{ 'status' : ?0 }", fields = "{ 'item' : 1, 'status' : 1 }")
    List<Inventory> findByStatusIncludeItemAndStatusFields(String status);

    @Query(value = "{ 'status' : ?0 }", fields = "{ 'item' : 1, 'status' : 1, '_id' : 0 }")
    List<Inventory> findByStatusIncludeItemAndStatusExcludeIdFields(String status);

    @Query(value = "{ 'status' : ?0 }", fields = "{ 'status' : 0, 'inStock' : 0 }")
    List<Inventory> findByStatusIncludeAllButStatusAndStockFields(String status);

    @Query(value = "{ 'status' : ?0 }", fields = "{ 'item' : 1, 'status' : 1, 'size.uom': 1 }")
    List<Inventory> findByStatusIncludeEmbeddedFields(String status);

    @Query(value = "{ 'status' : ?0 }", fields = "{ 'size.uom': 0 }")
    List<Inventory> findByStatusExcludeEmbeddedFields(String status);

    @Query(value = "{ 'status' : ?0 }", fields = "{ 'item' : 1, 'status' : 1, 'inStock.quantity': 1 }")
    List<Inventory> findByStatusIncludeEmbeddedFieldsInArray(String status);

    @Query(value = "{ 'status' : ?0 }", fields = "{ 'item' : 1, 'status' : 1, 'inStock': { $slice: -1 } }")
    List<Inventory> findByStatusIncludeEmbeddedFieldsLastElementInArray(String status);
}
```

### 3.3 添加Maven依赖项

**我们还将使用[嵌入式MongoDB](https://www.baeldung.com/spring-boot-embedded-mongodb)**。让我们将[spring-data-mongodb](https://search.maven.org/artifact/org.springframework.data/spring-data-mongodb)和[de.flapdoodle.embed.mongo](https://search.maven.org/artifact/de.flapdoodle.embed/de.flapdoodle.embed.mongo)依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>3.0.3.RELEASE</version>
</dependency>
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <version>3.2.6</version>
    <scope>test</scope>
</dependency>
```

## 4. 使用MongoRepository和MongoTemplate进行测试

**对于MongoRepository，我们将看到使用@Query和应用[字段限制](https://docs.spring.io/spring-data/mongodb/docs/3.3.1/reference/html/#mongodb.repositories.queries.json-based)的示例，而对于MongoTemplate，我们将使用[Query](https://docs.spring.io/spring-data/mongodb/docs/3.3.1/reference/html/#mongo.query)类**。

我们将尝试涵盖包含和排除的所有不同组合。**特别是，我们将看到如何使用slice属性限制嵌入字段，或者更有趣的是，限制数组**。

对于每个测试，我们将首先添加MongoRepository示例，然后是MongoTemplate示例。

### 4.1 仅包含字段

让我们从包含一些字段开始。所有排除的内容都将为null。投影默认添加_id：

```java
List<Inventory> inventoryList = inventoryRepository.findByStatusIncludeItemAndStatusFields("A");

inventoryList.forEach(i -> {
    assertNotNull(i.getId());
    assertNotNull(i.getItem());
    assertNotNull(i.getStatus());
    assertNull(i.getSize());
    assertNull(i.getInStock());
});
```

现在，让我们看看MongoTemplate版本：

```java
Query query = new Query();
query.fields()
    .include("item")
    .include("status");
```

### 4.2 包含和排除字段

这一次，我们将看到明确包含某些字段但排除其他字段的示例-在这种情况下，我们将排除_id字段：

```java
List<Inventory> inventoryList = inventoryRepository.findByStatusIncludeItemAndStatusExcludeIdFields("A");

inventoryList.forEach(i -> {
    assertNotNull(i.getItem());
    assertNotNull(i.getStatus());
    assertNull(i.getId());
    assertNull(i.getSize());
    assertNull(i.getInStock());
});
```

使用MongoTemplate的等效查询是：

```java
Query query = new Query();
query.fields()
    .include("item")
    .include("status")
    .exclude("_id");
```

### 4.3 仅排除字段

让我们继续排除一些字段。所有其他字段都将为非空：

```java
List<Inventory> inventoryList = inventoryRepository.findByStatusIncludeAllButStatusAndStockFields("A");

inventoryList.forEach(i -> {
    assertNotNull(i.getItem());
    assertNotNull(i.getId());
    assertNotNull(i.getSize());
    assertNull(i.getInStock());
    assertNull(i.getStatus());
});
```

并且，让我们看看MongoTemplate版本：

```java
Query query = new Query();
query.fields()
    .exclude("status")
    .exclude("inStock");
```

### 4.4 包括嵌入式字段

同样，包括嵌入字段会将它们添加到我们的结果中：

```java
List<Inventory> inventoryList = inventoryRepository.findByStatusIncludeEmbeddedFields("A");

inventoryList.forEach(i -> {
    assertNotNull(i.getItem());
    assertNotNull(i.getStatus());
    assertNotNull(i.getId());
    assertNotNull(i.getSize());
    assertNotNull(i.getSize().getUom());
    assertNull(i.getSize().getHeight());
    assertNull(i.getSize().getWidth());
    assertNull(i.getInStock());
});
```

让我们看看如何使用MongoTemplate做同样的事情：

```java
Query query = new Query();
query.fields()
    .include("item")
    .include("status")
    .include("size.uom");
```

### 4.5 排除嵌入字段

同样，**排除嵌入字段会将它们排除在我们的结果之外，但是，它会添加其余的嵌入字段**：

```java
List<Inventory> inventoryList = inventoryRepository.findByStatusExcludeEmbeddedFields("A");

inventoryList.forEach(i -> {
    assertNotNull(i.getItem());
    assertNotNull(i.getStatus());
    assertNotNull(i.getId());
    assertNotNull(i.getSize());
    assertNull(i.getSize().getUom());
    assertNotNull(i.getSize().getHeight());
    assertNotNull(i.getSize().getWidth());
    assertNotNull(i.getInStock());
});
```

让我们看一下MongoTemplate版本：

```java
Query query = new Query();
query.fields()
    .exclude("size.uom");
```

### 4.6 在数组中包含嵌入字段

与其他字段类似，我们也可以添加数组字段的投影：

```java
List<Inventory> inventoryList = inventoryRepository.findByStatusIncludeEmbeddedFieldsInArray("A");

inventoryList.forEach(i -> {
    assertNotNull(i.getItem());
    assertNotNull(i.getStatus());
    assertNotNull(i.getId());
    assertNotNull(i.getInStock());
    i.getInStock()
        .forEach(stock -> {
            assertNull(stock.getWareHouse());
            assertNotNull(stock.getQuantity());
        });
    assertNull(i.getSize());
});
```

让我们使用MongoTemplate实现相同的功能：

```java
Query query = new Query();
query.fields()
    .include("item")
    .include("status")
    .include("inStock.quantity");
```

### 4.7 使用切片在数组中包含嵌入字段

MongoDB可以使用JavaScript函数来限制数组的结果-例如，使用slice仅获取数组中的最后一个元素：

```java
List<Inventory> inventoryList = inventoryRepository.findByStatusIncludeEmbeddedFieldsLastElementInArray("A");

inventoryList.forEach(i -> {
    assertNotNull(i.getItem());
    assertNotNull(i.getStatus());
    assertNotNull(i.getId());
    assertNotNull(i.getInStock());
    assertEquals(1, i.getInStock().size());
    assertNull(i.getSize());
});
```

让我们使用MongoTemplate执行相同的查询：

```java
Query query = new Query();
query.fields()
    .include("item")
    .include("status")
    .slice("inStock", -1);
```

## 5. 总结

在本文中，我们研究了Spring Data MongoDB中的投影。

我们已经看到使用fields的示例，包括MongoRepository接口和@Query注解，以及MongoTemplate和Query类。