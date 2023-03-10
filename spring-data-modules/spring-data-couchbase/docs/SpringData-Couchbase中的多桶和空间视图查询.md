## 1. 概述

在关于Spring Data Couchbase的第三篇教程中，我们演示了支持跨多个存储桶的Couchbase数据模型所需的配置，并介绍了使用空间视图查询多维数据。

## 2. 数据模型

除了[第一个教程Person](https://www.baeldung.com/spring-data-couchbase)中的实体和[第二个教程](https://www.baeldung.com/entity-validation-locking-and-query-consistency-in-spring-data-couchbase)中的Student实体之外，我们还为本教程定义了一个Campus实体：

```java
@Document
public class Campus {
    @Id
    private String id;

    @Field
    @NotNull
    private String name;

    @Field
    @NotNull
    private Point location;

    // standard getters and setters
}
```

## 3. 多个Couchbase桶的Java配置

为了在你的项目中使用多个桶，你需要使用2.0.0或更高版本的Spring Data Couchbase模块，并且你需要使用基于Java的配置，因为基于XML的配置仅支持单存储桶桶场景。

这是我们在Maven pom.xml文件中包含的依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-couchbase</artifactId>
    <version>2.1.1.RELEASE</version>
</dependency>
```

### 3.1 定义桶Bucket Bean

在我们的[Spring Data Couchbase](https://www.baeldung.com/spring-data-couchbase)教程简介中，我们将“tuyucheng”指定为与Spring Data一起使用的默认Couchbase存储桶的名称。

我们将Campus实体存储在“tuyucheng2”桶中。

要使用第二个桶，我们首先必须在Couchbase配置类中为Bucket本身定义一个@Bean：

```java
@Bean
public Bucket campusBucket() throws Exception {
    return couchbaseCluster().openBucket("baeldung2", "");
}
```

### 3.2 配置模板Bean

接下来，我们为要与此存储桶一起使用的CouchbaseTemplate定义一个@Bean：

```java
@Bean
public CouchbaseTemplate campusTemplate() throws Exception {
    CouchbaseTemplate template = new CouchbaseTemplate(
        couchbaseClusterInfo(), campusBucket(),
        mappingCouchbaseConverter(), translationService());
    template.setDefaultConsistency(getDefaultConsistency());
    return template;
}
```

### 3.3 映射Repository

最后，我们定义一个Couchbase Repository操作的自定义映射，这样Campus实体类将使用新的模板和存储桶，而其他实体类将继续使用默认的模板和存储桶：

```java
@Override
public void configureRepositoryOperationsMapping(RepositoryOperationsMapping baseMapping) {
    try {
        baseMapping.mapEntity(Campus.class, campusTemplate());
    } catch (Exception e) {
        //custom Exception handling
    }
}
```

## 4. 查询空间或多维数据

Couchbase使用一种称为空间视图的特殊类型的视图，为针对二维数据(例如地理数据)运行边界框查询提供原生支持。

边界框查询是一种范围查询，它使用框的最西南[x,y]点作为其startRange参数，使用最西北[x,y]点作为其endRange参数。

Spring Data使用旨在消除误报匹配的算法将Couchbase的原生边界框查询功能扩展到涉及圆形和多边形的查询，并且它还为涉及多于两个维度的查询提供支持。

Spring Data通过一组关键字简化了多维查询的创建，这些关键字可用于定义Couchbase Repository中的派生查询。

### 4.1 支持的数据类型

Spring Data Couchbase Repository查询支持org.springframework.data.geo包中的数据类型，包括Point、Box、Circle、Polygon和Distance。

### 4.2 派生查询关键字

除了标准的Spring Data Repository关键字外，Couchbase Repository还支持涉及二维的派生查询中的以下关键字：

- Within,InWithin(采用两个Point参数定义边界框)
- Near,IsNear(以Point和Distance作为参数)

以下关键字可用于涉及两个以上维度的查询：

- Between(用于将单个数值添加到startRange和endRange)
- GreaterThan、GreaterThanEqual、After(用于将单个数值添加到startRange)
- LessThan、LessThanEqual、Before(用于将单个数值添加到endRange)

以下是使用这些关键字的派生查询方法的一些示例：

- findByLocationNear
- findByLocationWithin
- findByLocationNearAndPopulationGreaterThan
- findByLocationWithinAndAreaLessThan
- findByLocationNearAndTuitionBetween

## 5. 定义Repository

空间视图支持的Repository方法必须使用@Dimensional注解进行修饰，该注解指定设计文档名称、视图名称和用于定义视图键的维度数(如果未另外指定，则默认为2)。

### 5.1 CampusRepository接口

在我们的CampusRepository接口中，我们声明了两种方法-一种使用传统的Spring Data关键字，由MapReduce视图支持，另一种使用维度Spring Data关键字，由Spatial视图支持：

```java
public interface CampusRepository extends CrudRepository<Campus, String> {

    @View(designDocument="campus", viewName="byName")
    Set<Campus> findByName(String name);

    @Dimensional(dimensions=2, designDocument="campus_spatial",
          spatialViewName="byLocation")
    Set<Campus> findByLocationNear(Point point, Distance distance);
}
```

### 5.2 空间视图

空间视图是作为JavaScript函数编写的，很像MapReduce视图。与由map函数和reduce函数组成的MapReduce视图不同，Spatial视图只包含一个spatial函数，并且可能不会与MapReduce视图共存于同一个Couchbase设计文档中。

对于我们的校园实体，我们将创建一个名为“campus_spatial”的设计文档，其中包含一个名为“byLocation”的空间视图，其函数如下：

```javascript
function (doc) {
    if (doc.location && doc._class == "cn.tuyucheng.taketoday.spring.data.couchbase.model.Campus") {
        emit([doc.location.x, doc.location.y], null);
    }
}
```

如本示例所示，当你编写空间视图函数时，emit函数调用中使用的键必须是包含两个或更多值的数组。

### 5.3 MapReduce视图

为了为我们的Repository提供全面支持，我们必须创建一个名为“campus”的设计文档，其中包含两个MapReduce视图：“all”和“byName”。

这是“all”视图的map函数：

```javascript
function (doc, meta) {
    if(doc._class == "cn.tuyucheng.taketoday.spring.data.couchbase.model.Campus") {    
        emit(meta.id, null);
    }
}
```

这是“byName”视图的map函数：

```javascript
function (doc, meta) {
    if(doc._class == "cn.tuyucheng.taketoday.spring.data.couchbase.model.Campus" && doc.name) {    
        emit(doc.name, null);
    }
}
```

## 6. 总结

我们展示了如何配置你的Spring Data Couchbase项目以支持使用多个存储桶，还展示了如何使用Repository抽象来编写针对多维数据的空间视图查询。

要了解有关Spring Data Couchbase的更多信息，请访问官方[Spring Data Couchbase](http://projects.spring.io/spring-data-couchbase/)项目站点。