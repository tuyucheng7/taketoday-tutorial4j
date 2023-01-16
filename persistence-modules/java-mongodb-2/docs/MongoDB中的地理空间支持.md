## 1. 概述

在本教程中，我们将探讨 MongoDB 中的地理空间支持。

我们将讨论如何存储地理空间数据、地理索引和地理空间搜索。我们还将使用多个地理空间搜索查询，如near、geoWithin和geoIntersects。

## 2. 存储地理空间数据

首先，让我们看看如何在 MongoDB 中存储地理空间数据。

MongoDB 支持多种GeoJSON类型来存储地理空间数据。在我们的示例中，我们将主要使用Point和Polygon类型。

### 2.1. 观点

这是最基本和常见的GeoJSON类型，用于表示网格上的一个特定点。

在这里，我们有一个简单的对象，在我们的places集合中，它的字段位置是一个Point：

```bash
{
  "name": "Big Ben",
  "location": {
    "coordinates": [-0.1268194, 51.5007292],
    "type": "Point"
  }
}
```

请注意，经度值在前，然后是纬度。

### 2.2. 多边形

多边形是有点复杂的GeoJSON类型。

我们可以使用Polygon来定义一个区域，其外部边界和内部孔洞(如果需要)。

让我们看看另一个将其位置定义为Polygon的对象：

```bash
{
  "name": "Hyde Park",
  "location": {
    "coordinates": [
      [
        [-0.159381, 51.513126],
        [-0.189615, 51.509928],
        [-0.187373, 51.502442],
        [-0.153019, 51.503464],
        [-0.159381, 51.513126]
      ]
    ],
    "type": "Polygon"
  }
}
```

在此示例中，我们定义了一个表示外部边界的点数组。我们还必须关闭边界，使最后一个点等于第一个点。

请注意，我们需要定义逆时针方向的外部边界点和顺时针方向的孔边界。

除了这些类型之外，还有许多其他类型，如LineString、MultiPoint、MultiPolygon、MultiLineString和GeometryCollection。

## 3. 地理空间索引

要对我们存储的地理空间数据执行搜索查询，我们需要在位置字段上创建地理空间索引。

我们基本上有两个选择：2d和2dsphere。

但首先，让我们定义我们的地点集合：

```java
MongoClient mongoClient = new MongoClient();
MongoDatabase db = mongoClient.getDatabase("myMongoDb");
collection = db.getCollection("places");
```

### 3.1. 二维地理空间索引

2d索引使我们能够执行基于 2d 平面计算的搜索查询。

我们可以在我们的Java应用程序中的位置字段上创建一个二维索引，如下所示：

```java
collection.createIndex(Indexes.geo2d("location"));
```

当然，我们也可以在mongo shell中做同样的事情：

```bash
db.places.createIndex({location:"2d"})
```

### 3.2. 2dsphere地理空间索引

2dsphere索引支持基于球体计算的查询。

同样，我们可以使用与上面相同的Indexes类在 Java中创建一个2dsphere索引：

```java
collection.createIndex(Indexes.geo2dsphere("location"));
```

或者在mongo shell 中：

```bash
db.places.createIndex({location:"2dsphere"})
```

## 4. 使用地理空间查询进行搜索

现在，对于令人兴奋的部分，让我们使用地理空间查询根据对象的位置搜索对象。

### 4.1. 近查询

让我们从附近开始。 我们可以使用near查询来搜索给定距离内的地点。

near查询适用于2d和2dsphere索引。

在下一个示例中，我们将搜索距给定位置小于 1 公里且大于 10 米的地点：

```java
@Test
public void givenNearbyLocation_whenSearchNearby_thenFound() {
    Point currentLoc = new Point(new Position(-0.126821, 51.495885));
 
    FindIterable<Document> result = collection.find(
      Filters.near("location", currentLoc, 1000.0, 10.0));

    assertNotNull(result.first());
    assertEquals("Big Ben", result.first().get("name"));
}
```

以及mongo shell中的相应查询：

```bash
db.places.find({
  location: {
    $near: {
      $geometry: {
        type: "Point",
        coordinates: [-0.126821, 51.495885]
      },
      $maxDistance: 1000,
      $minDistance: 10
    }
  }
})
```

请注意，结果是从最近到最远排序的。

同样，如果我们使用一个非常远的位置，我们将找不到任何附近的地方：

```java
@Test
public void givenFarLocation_whenSearchNearby_thenNotFound() {
    Point currentLoc = new Point(new Position(-0.5243333, 51.4700223));
 
    FindIterable<Document> result = collection.find(
      Filters.near("location", currentLoc, 5000.0, 10.0));

    assertNull(result.first());
}
```

我们还有nearSphere方法，它的作用与near 完全一样，只是它使用球面几何计算距离。

### 4.2. 在查询中

接下来，我们将探索geoWithin查询。

geoWithin查询使我们能够搜索完全存在于给定Geometry中的位置，例如圆、框或多边形。这也适用于2d和2dsphere索引。

在此示例中，我们正在寻找距离给定中心位置 5 公里半径范围内的地点：

```java
@Test
public void givenNearbyLocation_whenSearchWithinCircleSphere_thenFound() {
    double distanceInRad = 5.0 / 6371;
 
    FindIterable<Document> result = collection.find(
      Filters.geoWithinCenterSphere("location", -0.1435083, 51.4990956, distanceInRad));

    assertNotNull(result.first());
    assertEquals("Big Ben", result.first().get("name"));
}
```

请注意，我们需要将距离从公里转换为弧度(只需除以地球半径)。

结果查询：

```bash
db.places.find({
  location: {
    $geoWithin: {
      $centerSphere: [
        [-0.1435083, 51.4990956],
        0.0007848061528802386
      ]
    }
  }
})
```

接下来，我们将搜索存在于矩形“框”内的所有位置。我们需要通过左下角位置和右上角位置来定义盒子：

```java
@Test
public void givenNearbyLocation_whenSearchWithinBox_thenFound() {
    double lowerLeftX = -0.1427638;
    double lowerLeftY = 51.4991288;
    double upperRightX = -0.1256209;
    double upperRightY = 51.5030272;

    FindIterable<Document> result = collection.find(
      Filters.geoWithinBox("location", lowerLeftX, lowerLeftY, upperRightX, upperRightY));

    assertNotNull(result.first());
    assertEquals("Big Ben", result.first().get("name"));
}
```

这是mongo shell中的相应查询：

```bash
db.places.find({
  location: {
    $geoWithin: {
      $box: [
        [-0.1427638, 51.4991288],
        [-0.1256209, 51.5030272]
      ]
    }
  }
})
```

最后，如果我们要搜索的区域不是矩形或圆形，我们可以使用多边形来定义更具体的区域：

```java
@Test
public void givenNearbyLocation_whenSearchWithinPolygon_thenFound() {
    ArrayList<List<Double>> points = new ArrayList<List<Double>>();
    points.add(Arrays.asList(-0.1439, 51.4952));
    points.add(Arrays.asList(-0.1121, 51.4989));
    points.add(Arrays.asList(-0.13, 51.5163));
    points.add(Arrays.asList(-0.1439, 51.4952));
 
    FindIterable<Document> result = collection.find(
      Filters.geoWithinPolygon("location", points));

    assertNotNull(result.first());
    assertEquals("Big Ben", result.first().get("name"));
}
```

这是相应的查询：

```bash
db.places.find({
  location: {
    $geoWithin: {
      $polygon: [
        [-0.1439, 51.4952],
        [-0.1121, 51.4989],
        [-0.13, 51.5163],
        [-0.1439, 51.4952]
      ]
    }
  }
})
```

我们只定义了一个带有外部边界的多边形，但我们也可以给它添加孔。每个洞都是一个Point列表：

```java
geoWithinPolygon("location", points, hole1, hole2, ...)
```

### 4.3. 相交查询

最后，让我们看看geoIntersects查询。

geoIntersects查询查找至少与给定Geometry相交的对象。 相比之下，geoWithin查找完全存在于给定Geometry中的对象。

此查询仅适用于 2dsphere索引。

让我们在实践中看看这一点，以寻找与Polygon相交的任何地方为例：

```java
@Test
public void givenNearbyLocation_whenSearchUsingIntersect_thenFound() {
    ArrayList<Position> positions = new ArrayList<Position>();
    positions.add(new Position(-0.1439, 51.4952));
    positions.add(new Position(-0.1346, 51.4978));
    positions.add(new Position(-0.2177, 51.5135));
    positions.add(new Position(-0.1439, 51.4952));
    Polygon geometry = new Polygon(positions);
 
    FindIterable<Document> result = collection.find(
      Filters.geoIntersects("location", geometry));

    assertNotNull(result.first());
    assertEquals("Hyde Park", result.first().get("name"));
}
```

结果查询：

```bash
db.places.find({
  location:{
    $geoIntersects:{
      $geometry:{
        type:"Polygon",
          coordinates:[
          [
            [-0.1439, 51.4952],
            [-0.1346, 51.4978],
            [-0.2177, 51.5135],
            [-0.1439, 51.4952]
          ]
        ]
      }
    }
  }
})
```

## 5.总结

在本文中，我们了解了如何在 MongoDB 中存储地理空间数据，并了解了2d和2dsphere地理空间索引之间的区别。我们还学习了如何使用地理空间查询在 MongoDB 中进行搜索。