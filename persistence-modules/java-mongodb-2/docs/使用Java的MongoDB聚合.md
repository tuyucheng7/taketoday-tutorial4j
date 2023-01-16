## 1. 概述

在本教程中，我们将深入了解使用[MongoDB Java](https://www.baeldung.com/java-mongodb#maven-dependencies)驱动程序的MongoDB 聚合框架。

我们将首先了解聚合在概念上的含义，然后设置数据集。最后，我们将看到使用[Aggregates](https://mongodb.github.io/mongo-java-driver/3.12/builders/aggregation/) builder的各种聚合技术。

## 2.什么是聚合？

聚合在 MongoDB 中用于分析数据并从中获取有意义的信息。

这些通常在不同的阶段执行，阶段形成一个管道——这样一个阶段的输出作为输入传递到下一个阶段。

最常用的阶段可以概括为：

| 阶段     | 等效的 SQL         | 描述                                                         |
| -------- | ------------------ | ------------------------------------------------------------ |
| 项目 | 选择               | 仅选择必需的字段，也可用于计算派生字段并将其添加到集合中     |
| 匹配 | 在哪里             | 根据指定条件过滤集合                                         |
| 团体 | 通过...分组        | 根据指定的标准(例如计数、总和)将输入收集在一起，为每个不同的分组返回一个文档 |
| 种类 | 订购方式           | 按给定字段的升序或降序对结果进行排序                         |
| 数数 | 数数               | 计算集合包含的文档                                           |
| 限制 | 限制               | 将结果限制为指定数量的文档，而不是返回整个集合               |
| 出去 | 选择进入 NEW_TABLE | 将结果写入命名集合；这个阶段只能作为管道中的最后一个阶段     |


上面包含了每个聚合阶段的[SQL 等效](https://docs.mongodb.com/manual/reference/sql-aggregation-comparison/)项，让我们了解上述操作在 SQL 世界中的含义。

我们很快就会看到所有这些阶段的Java代码示例。但在此之前，我们需要一个数据库。

## 3.数据库设置

### 3.1. 数据集

学习任何与数据库相关的东西的首要要求是数据集本身！

出于本教程的目的，我们将使用一个[公开可用的 restful API 端点](https://restcountries.com/#api-endpoints-v3)，它提供有关世界所有国家/地区的综合信息。这个 API 以方便的 JSON 格式为我们提供了一个国家的大量数据点。我们将在分析中使用的一些字段是：

-   name – 国家名称；例如，美利坚合众国
-   alpha3Code——国家名称的简码；例如，IND(印度)
    
-   region——国家所属的地区；例如， 欧洲
-   area——国家的地理区域
-   languages——数组格式的国家官方语言；例如，英语
-   borders – 一系列邻国的alpha3Code s

现在让我们看看如何将这些数据转换成 MongoDB 数据库中的集合。

### 3.2. 导入到 MongoDB

首先，我们需要访问[API 端点以获取所有国家](https://api.countrylayer.com/rest/v2/all)/地区并将响应保存在本地的 JSON 文件中。下一步是使用mongoimport命令将其导入 MongoDB：

```shell
mongoimport.exe --db <db_name> --collection <collection_name> --file <path_to_file> --jsonArray
```

成功的导入应该给我们一个包含 250 个文档的集合。

## 4.Java中的聚合示例

现在我们已经涵盖了基础，让我们开始从我们拥有的所有国家/地区的数据中得出一些有意义的见解。为此，我们将使用多个 JUnit 测试。

但在我们这样做之前，我们需要连接到数据库：

```java
@BeforeClass
public static void setUpDB() throws IOException {
    mongoClient = MongoClients.create();
    database = mongoClient.getDatabase(DATABASE);
    collection = database.getCollection(COLLECTION);
}

```

在接下来的所有示例中，我们将使用[MongoDBJava驱动程序提供的](https://mongodb.github.io/mongo-java-driver/)[聚合](https://mongodb.github.io/mongo-java-driver/3.10/javadoc/?com/mongodb/client/model/Aggregates.html)助手类。

为了更好地阅读我们的代码片段，我们可以添加一个静态导入：

```java
import static com.mongodb.client.model.Aggregates.;
```

### 4.1. 匹配和计数

首先，让我们从一些简单的事情开始。早些时候我们注意到数据集包含有关语言的信息。

现在，假设我们要检查世界上以英语为官方语言的国家/地区的数量：

```java
@Test
public void givenCountryCollection_whenEnglishSpeakingCountriesCounted_thenNinetyOne() {
    Document englishSpeakingCountries = collection.aggregate(Arrays.asList(
      match(Filters.eq("languages.name", "English")),
      count())).first();
    
    assertEquals(91, englishSpeakingCountries.get("count"));
}
```

在这里，我们在聚合管道中使用了两个阶段：[匹配](https://docs.mongodb.com/manual/reference/operator/aggregation/match/)和[计数](https://docs.mongodb.com/manual/reference/operator/aggregation/count/)。

首先，我们过滤掉集合以仅匹配那些在其语言字段中包含英语的文档。这些文档可以想象成一个临时或中间集合，成为我们下一阶段计数的输入。这将计算上一阶段的文档数量。

此示例中还有一点需要注意的是方法first的使用。由于我们知道最后一个阶段的输出count将是单个记录，因此这是提取单独结果文档的可靠方法。

### 4.2. 分组(用sum)和排序

在此示例中，我们的目标是找出包含最多国家/地区的地理区域：

```java
@Test
public void givenCountryCollection_whenCountedRegionWise_thenMaxInAfrica() {
    Document maxCountriedRegion = collection.aggregate(Arrays.asList(
      group("$region", Accumulators.sum("tally", 1)),
      sort(Sorts.descending("tally")))).first();
    
    assertTrue(maxCountriedRegion.containsValue("Africa"));
}
```

很明显，我们正在使用[group](https://docs.mongodb.com/manual/reference/operator/aggregation/group/)和[sort](https://docs.mongodb.com/manual/reference/operator/aggregation/sort/)来实现我们的目标。

首先，我们通过在可变计数中累积它们出现的总和来收集每个地区的国家数量。这为我们提供了一个中间文档集合，每个文档包含两个字段：地区和其中的国家总数。然后我们按降序对它进行排序，并提取第一个文档来为我们提供国家最多的地区。

### 4.3. 排序、 限制和输出

现在让我们使用sort、[limit](http://docs.mongodb.org/manual/reference/operator/aggregation/limit/)和[out](http://docs.mongodb.org/manual/reference/operator/aggregation/out/)按面积提取七个最大的国家并将它们写入一个新集合：

```java
@Test
public void givenCountryCollection_whenAreaSortedDescending_thenSuccess() {
    collection.aggregate(Arrays.asList(
      sort(Sorts.descending("area")), 
      limit(7),
      out("largest_seven"))).toCollection();

    MongoCollection<Document> largestSeven = database.getCollection("largest_seven");

    assertEquals(7, largestSeven.countDocuments());

    Document usa = largestSeven.find(Filters.eq("alpha3Code", "USA")).first();

    assertNotNull(usa);
}
```

在这里，我们首先按照面积的降序对给定的集合进行排序。然后，我们使用Aggregates#limit方法将结果限制为仅七个文档。最后，我们使用out阶段将这些数据反序列化为一个名为largest_seven的新集合。现在可以像使用任何其他集合一样使用此集合——例如，查找它是否包含USA。

### 4.4. 项目，组(最大)，匹配

在我们的最后一个示例中，让我们尝试一些更棘手的事情。假设我们需要找出每个国家与其他国家共有多少边界，以及最大边界数是多少。

现在在我们的数据集中，我们有一个borders字段，它是一个数组，列出了该国所有接壤国家的alpha3Code，但没有任何字段直接为我们提供计数。因此，我们需要使用[project](https://docs.mongodb.com/manual/reference/operator/aggregation/project/)导出borderingCountries的数量：

```java
@Test
public void givenCountryCollection_whenNeighborsCalculated_thenMaxIsFifteenInChina() {
    Bson borderingCountriesCollection = project(Projections.fields(Projections.excludeId(), 
      Projections.include("name"), Projections.computed("borderingCountries", 
        Projections.computed("$size", "$borders"))));
    
    int maxValue = collection.aggregate(Arrays.asList(borderingCountriesCollection, 
      group(null, Accumulators.max("max", "$borderingCountries"))))
      .first().getInteger("max");

    assertEquals(15, maxValue);

    Document maxNeighboredCountry = collection.aggregate(Arrays.asList(borderingCountriesCollection,
      match(Filters.eq("borderingCountries", maxValue)))).first();
       
    assertTrue(maxNeighboredCountry.containsValue("China"));
}
```

之后，如前所述，我们将对投影集合进行分组以找到borderingCountries的最大值。这里要指出的一件事是max累加器为我们提供了最大值作为数字，而不是包含最大值的整个文档。如果要执行任何进一步的操作，我们需要执行匹配以筛选出所需的文档。

## 5.总结

在本文中，我们了解了什么是 MongoDB 聚合，以及如何使用示例数据集在Java中应用它们。

我们使用四个示例来说明各个聚合阶段，以形成对该概念的基本理解。该框架提供的数据分析有无数种可能性，可以进一步探索。

为了进一步阅读，[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-guide)提供了一种替代方法来处理Java 中的[投影和聚合](https://www.baeldung.com/spring-data-mongodb-projections-aggregations)。