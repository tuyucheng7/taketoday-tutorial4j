## 1. 简介

在这个快速教程中，我们将学习如何在MongoDB中使用Java [HashMap](https://www.baeldung.com/java-hashmap)。**MongoDB有一个Map友好的API，而[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)使得使用Map或Map集合更加简单**。

## 2. 设置我们的场景

Spring Data MongoDB带有MongoTemplate，它有许多insert()的重载版本，允许我们将Map插入到我们的集合中。**MongoDB表示[JSON](https://www.baeldung.com/java-json)格式的文档。因此，我们可以用Java中的Map<String, Object\>表示它**。

**我们将使用MongoTemplate和一个简单的可重用Map来实现我们的用例**。让我们从创建Map引用和注入MongoTemplate开始：

```java
class MongoDbHashMapIntegrationTest {
    private static final Map<String, Object> MAP = new HashMap<>();

    @Autowired
    private MongoTemplate mongo;
}
```

然后，我们将使用一些不同类型的条目来初始化我们的Map：

```java
@BeforeAll
static void init() {
    MAP.put("name", "Document A");
    MAP.put("number", 2);
    MAP.put("dynamic", true);
}
```

我们用[@BeforeAll](https://www.baeldung.com/junit-before-beforeclass-beforeeach-beforeall)标记它，这样我们所有的测试都可以使用它。

## 3. 直接插入单个Map

首先，我们调用mongo.insert()并选择一个集合插入：

```java
@Test
void whenUsingMap_thenInsertSucceeds() {
    Map<String, Object> saved = mongo.insert(MAP, "map-collection");

    assertNotNull(saved.get("_id"));
}
```

**不需要特定的包装器**。插入后，我们的Map成为我们MongoDB集合中的单个JSON文档。**最重要的是，我们可以检查MongoDB生成的_id属性是否存在，确保它被正确处理**。

## 4. 直接批量插入Map

我们还可以插入一个Map[集合](https://www.baeldung.com/java-collections)。每次插入都会成为不同的文档。此外，为确保我们不插入重复项，我们将使用[Set](https://www.baeldung.com/java-set-vs-list)。

**让我们将之前创建的Map与新Map一起添加到我们的集合中**：

```java
@Test
void whenMapSet_thenInsertSucceeds() {
    Set<Map<String, Object>> set = new HashSet<>();

    Map<String, Object> otherMap = new HashMap<>();
    otherMap.put("name", "Other Document");
    otherMap.put("number", 22);

    set.add(MAP);
    set.add(otherMap);

    Collection<Map<String, Object>> insert = mongo.insert(set, "map-set");

    assertEquals(2, insert.size());
}
```

结果，我们得到了两个插入。**此方法有助于减少一次添加大量文档的开销**。

## 5. 从Map构建文档并插入

**Document类是在Java中处理MongoDB文档的[推荐方式](https://www.mongodb.com/docs/drivers/java/sync/v4.3/fundamentals/data-formats/documents/#overview)**。它实现了Map和Bson，使其易于工作。让我们使用接收Map的构造函数：

```java
@Test
void givenMap_whenDocumentConstructed_thenInsertSucceeds() {
    Document document = new Document(MAP);

    Document saved = mongo.insert(document, "doc-collection");

    assertNotNull(saved.get("_id"));
}
```

在内部，Document使用LinkedHashMap来保证插入顺序。

## 6. 从Map构造BasicDBObject并插入

虽然 Document类是首选，但我们也可以从Map构造BasicDBObject：

```java
@Test
void givenMap_whenBasicDbObjectConstructed_thenInsertSucceeds() {
    BasicDBObject dbObject = new BasicDBObject(MAP);

    BasicDBObject saved = mongo.insert(dbObject, "db-collection");

    assertNotNull(saved.get("_id"));
}
```

**如果我们处理遗留代码，BasicDBObject仍然很有用，因为Document类仅从MongoDB驱动程序版本3开始可用**。

## 7. 从对象值流构建文档并插入

在最后一个示例中，我们将从Map构建一个Document对象，其中每个键的值都是一个Object值列表。由于我们知道值的格式，因此我们可以通过为每个值放置一个属性名称来构建我们的Document。

让我们从构建我们的input Map开始：

```java
Map<String, List<Object>> input = new HashMap<>();
List<Object> listOne = new ArrayList<>();
listOne.add("Doc A");
listOne.add(1);

List<Object> listTwo = new ArrayList<>();
listTwo.add("Doc B");
listTwo.add(2);

input.put("a", listOne);
input.put("b", listTwo);
```

**如我们所见，没有属性名称，只有值**。因此，让我们[流式传输](https://www.baeldung.com/java-streams)我们的input的[entrySet()](https://www.baeldung.com/java-map-entries-methods)并从中构建我们的result。为此，我们会将输入中的每个条目[收集](https://www.baeldung.com/java-8-collectors)到HashSet中。**然后，我们将在accumulator函数中构建一个Document，将entry键作为_id属性。之后，我们将遍历条目值，将它们放在适当的属性名称下**。最后，我们将每个Document添加到我们的result中：

```java
Set<Document> docs = input.entrySet()
    .stream()
    .collect(HashSet::new, (set, entry) -> {
        Document document = new Document();
        
        document.put("_id", entry.getKey());
        Iterator<Object> iterator = entry.getValue()
            .iterator();
        document.put("name", iterator.next());
        document.put("number", iterator.next());
        set.add(document);
    }, Set::addAll);

Collection<Document> saved = mongo.insert(docs, "custom-set");

saved.forEach(this::assertHasMongoId);
```

请注意，在此示例中我们不需要来自collect()的第三个参数。但这是因为只有[并行流](https://www.baeldung.com/java-when-to-use-parallel-stream)使用combiner函数。

**最后，我们可以将result插入MongoDB**：

```java
mongo.insert(result, "custom-set");
```

这种策略很有用，例如，如果我们想将CSV值转换为JSON。

## 8. 总结

在本文中，我们看到了使用HashMap和HashMap列表将文档插入MongoDB集合的不同方法。我们使用MongoTemplate来简化任务并使用最常见的文档抽象：BasicDBObject和Document。