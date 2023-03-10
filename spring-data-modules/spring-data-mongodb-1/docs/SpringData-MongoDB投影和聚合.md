## 1. 概述

Spring Data MongoDB为MongoDB原生查询语言提供了简单的高级抽象。在本文中，**我们将探讨对投影和聚合框架的支持**。

如果你不熟悉此主题，请参阅我们的介绍性文章[Spring Data MongoDB简介](https://www.baeldung.com/spring-data-mongodb-tutorial)。

## 2. 投影

在MongoDB中，投影是一种从数据库中仅获取文档所需字段的方法。这减少了必须从数据库服务器传输到客户端的数据量，从而提高了性能。

使用Spring Data MongoDB，投影可以与MongoTemplate和MongoRepository一起使用。

在我们进一步讨论之前，让我们看看我们将使用的数据模型：

```java
@Document
public class User {
    @Id
    private String id;
    private String name;
    private Integer age;

    // standard getters and setters
}
```

### 2.1 使用MongoTemplate的投影

Field类的include()和exclude()方法分别用于包含和排除字段：

```java
Query query = new Query();
query.fields().include("name").exclude("id");
List<User> john = mongoTemplate.find(query, User.class);
```

这些方法可以链接在一起以包含或排除多个字段。标记为@Id(数据库中的_id)的字段总是被获取，除非明确排除。

当使用投影获取记录时，模型类实例中排除的字段为空。在字段是原始类型或其包装类的情况下，排除字段的值是原始类型的默认值。

例如，String将为null，int/Integer将为0而boolean/Boolean将为false。

因此，在上面的示例中，name字段将为John，id将为null，age将为0。

### 2.2 使用MongoRepository的投影

在使用MongoRepository时，@Query注解的字段可以定义为JSON格式：

```java
@Query(value="{}", fields="{name : 1, _id : 0}")
List<User> findNameAndExcludeId();
```

结果与使用MongoTemplate相同。value="{}"表示没有过滤器，因此将获取所有文档。

## 3. 聚合

MongoDB中的聚合旨在处理数据并返回计算结果。数据分阶段处理，一个阶段的输出作为输入提供给下一阶段。这种分阶段应用转换和对数据进行计算的能力使聚合成为非常强大的分析工具。

Spring Data MongoDB使用三个类：Aggregation包装聚合查询，AggregationOperation包装各个管道阶段和AggregationResults是聚合产生的结果的容器，为原生聚合查询提供抽象。

要执行聚合，首先，使用Aggregation类上的静态构建器方法创建聚合管道，然后使用Aggregation类上的newAggregation()方法创建聚合实例，最后使用MongoTemplate运行聚合：

```java
MatchOperation matchStage = Aggregation.match(new Criteria("foo").is("bar"));
ProjectionOperation projectStage = Aggregation.project("foo", "bar.baz");
        
Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage);

AggregationResults<OutType> output = mongoTemplate.aggregate(aggregation, "foobar", OutType.class);
```

请注意MatchOperation和ProjectionOperation都实现了AggregationOperation。其他聚合管道也有类似的实现。OutType是预期输出的数据模型。

现在，我们将查看几个示例及其解释，以涵盖主要的聚合管道和运算符。

我们将在本文中使用的数据集列出了美国所有邮政编码的详细信息，这些信息可以从[MongoDB Repository](http://media.mongodb.org/zips.json)下载。

让我们看一下将示例文档导入测试数据库中名为zips的集合后的示例文档。

```json
{
    "_id": "01001",
    "city": "AGAWAM",
    "loc": [
        -72.622739,
        42.070206
    ],
    "pop": 15338,
    "state": "MA"
}
```

为了简单和代码简洁，在接下来的代码片段中，我们将假设Aggregation类的所有静态方法都是静态导入的。

### 3.1 按人口降序排列所有人口大于1000万的州

在这里，我们将有三个管道：

1.  $group阶段汇总所有邮政编码的人口
2.  $match阶段过滤掉人口超过1000万的州
3.  $sort阶段按人口降序对所有文档进行排序

预期的输出将有一个字段_id作为state和一个字段statePop作为州总人口。让我们为此创建一个数据模型并运行聚合：

```java
public class StatePoulation {

    @Id
    private String state;
    private Integer statePop;

    // standard getters and setters
}
```

@Id注解会将_id字段从输出映射到模型中的state：

```java
GroupOperation groupByStateAndSumPop = group("state")
    .sum("pop").as("statePop");
MatchOperation filterStates = match(new Criteria("statePop").gt(10000000));
SortOperation sortByPopDesc = sort(Sort.by(Direction.DESC, "statePop"));

Aggregation aggregation = newAggregation(groupByStateAndSumPop, filterStates, sortByPopDesc);
AggregationResults<StatePopulation> result = mongoTemplate.aggregate(aggregation, "zips", StatePopulation.class);
```

AggregationResults类实现了Iterable，因此我们可以迭代它并打印结果。

如果不知道输出数据模型，可以使用标准的MongoDB Document类。

### 3.2 按平均城市人口计算最小的州

对于这个问题，我们需要四个阶段：

1. $group求和每个城市的总人口
2. $group计算每个州的平均人口
3. $sort阶段按平均城市人口升序对各州进行排序
4. $limit获得平均城市人口最少的第一个州

尽管不一定需要，但我们将使用额外的$project阶段根据StatePopulation数据模型重新格式化文档。

```java
GroupOperation sumTotalCityPop = group("state", "city")
    .sum("pop").as("cityPop");
GroupOperation averageStatePop = group("_id.state")
    .avg("cityPop").as("avgCityPop");
SortOperation sortByAvgPopAsc = sort(Sort.by(Direction.ASC, "avgCityPop"));
LimitOperation limitToOnlyFirstDoc = limit(1);
ProjectionOperation projectToMatchModel = project()
    .andExpression("_id").as("state")
    .andExpression("avgCityPop").as("statePop");

Aggregation aggregation = newAggregation(
    sumTotalCityPop, averageStatePop, sortByAvgPopAsc,
    limitToOnlyFirstDoc, projectToMatchModel);

AggregationResults<StatePopulation> result = mongoTemplate
    .aggregate(aggregation, "zips", StatePopulation.class);
StatePopulation smallestState = result.getUniqueMappedResult();
```

在这个例子中，我们已经知道结果中只有一个文档，因为我们在最后阶段将输出文档的数量限制为1。因此，我们可以调用getUniqueMappedResult()来获取所需的StatePopulation实例。

另一件需要注意的事情是，我们没有依赖@Id注解将_id映射到state，而是在投影阶段明确地完成了它。

### 3.3 获取具有最大和最小邮政编码的州

对于这个例子，我们需要三个阶段：

1.  $group计算每个州的邮政编码数量
2.  $sort按邮政编码数量对各州进行排序
3.  $group使用$first和$last运算符查找具有最大和最小邮政编码的州

```java
GroupOperation sumZips = group("state").count().as("zipCount");
SortOperation sortByCount = sort(Direction.ASC, "zipCount");
GroupOperation groupFirstAndLast = group().first("_id").as("minZipState")
    .first("zipCount").as("minZipCount").last("_id").as("maxZipState")
    .last("zipCount").as("maxZipCount");

Aggregation aggregation = newAggregation(sumZips, sortByCount, groupFirstAndLast);

AggregationResults<Document> result = mongoTemplate
    .aggregate(aggregation, "zips", Document.class);
Document document= result.getUniqueMappedResult();
```

在这里，我们没有使用任何模型，而是使用了MongoDB驱动程序已经提供的Document。

## 4. 总结

在本文中，我们学习了如何使用Spring Data MongoDB中的投影在MongoDB中获取文档的指定字段。

我们还了解了Spring Data中对MongoDB聚合框架的支持。我们涵盖了主要的聚合阶段-分组、排序、限制和匹配，并查看了其实际应用的一些示例。