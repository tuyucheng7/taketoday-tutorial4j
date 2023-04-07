## 1. 概述

在本文中，我们将了解地理空间应用程序的核心架构和重要元素。我们将从了解什么是地理空间应用程序以及构建应用程序的典型挑战开始。

地理空间应用程序的一个重要方面是在直观的地图上表示有用的数据。尽管在本文中，我们将主要关注在后端处理地理空间数据以及当今行业中我们可用的选项。

## 2. 什么是地理空间应用？

让我们首先了解地理空间应用程序的含义。这些基本上是利用地理空间数据来提供其核心功能的应用程序。

简单来说，地理空间数据是表示地点、位置、地图、导航等的任何数据。即使没有任何奇特的定义，我们也被这些应用程序所包围。例如，我们最喜欢的拼车应用程序、送餐应用程序和电影预订应用程序都是地理空间应用程序。

地理空间数据基本上是描述位于地球表面或地球表面附近的物体、事件或其他特征的信息。例如，考虑一个应用程序，它可以向我们建议最近的剧院在今天晚上播放我们最喜欢的莎士比亚戏剧。它可以通过将剧院的位置信息与戏剧的属性信息和事件的时间信息相结合来实现。

地理空间数据还有其他几个有用的应用程序可以为我们提供日常价值——例如，当我们试图在一天中的任何时间在附近找到一辆愿意带我们去目的地的出租车时。或者当我们迫不及待地等待重要货物到达时，谢天谢地，我们可以准确地找到它在运输途中的位置。

事实上，这已经成为我们最近经常使用的几个应用程序的基本要求。

## 3.地理空间技术

在了解构建地理空间应用程序的细微差别之前，让我们先了解一些支持此类应用程序的核心技术。这些是帮助我们以有用的形式生成、处理和呈现地理空间数据的基础技术。

遥感 (RS) 是通过测量一定距离的反射和发射辐射来检测和监测区域物理特性的过程。通常这是使用遥感卫星完成的。它在测量、情报和商业应用领域都有重要的用途。

全球定位系统 (GPS) 是指基于卫星的导航系统，该系统基于在中地球轨道 (MEO) 中飞行的24 颗卫星组成的网络。它可以向地球上任何地方的合适接收器提供地理定位和时间信息，只要它与四颗或更多 GPS 卫星的视线畅通无阻。

地理信息系统 (GIS) 是一个创建、管理、分析和映射所有类型数据的系统。例如，它可以帮助我们将位置数据与更具描述性的信息(例如该位置中存在的信息)相结合。它有助于改善多个行业的沟通和决策。

## 4. 构建地理空间应用程序的挑战

要了解在构建地理空间应用程序时我们应该做出哪些设计选择，了解所涉及的挑战非常重要。通常，地理空间应用程序需要对大量地理空间数据进行实时分析。例如，找到通往最近发生自然灾害的灾区的最快替代路线对于急救人员来说至关重要。

因此，基本上，地理空间应用程序的基本要求之一是存储大量地理空间数据并以极低的延迟促进任意查询。现在，了解空间数据的性质及其需要特殊处理的原因也很重要。基本上，空间数据表示在几何空间中定义的对象。

假设我们在一个城市周围有几个感兴趣的地点。位置通常由其纬度、经度和(可能)海拔高度来描述：

[![地理空间应用位置](https://www.baeldung.com/wp-content/uploads/2021/07/Geospatial-Application-Locations.jpg)](https://www.baeldung.com/wp-content/uploads/2021/07/Geospatial-Application-Locations.jpg)

现在，我们真正感兴趣的是找到给定位置的附近位置。因此，我们需要计算从该位置到所有可能位置的距离。此类查询与我们熟悉的常规数据库查询非常不同寻常。这些被称为空间查询。它们涉及几何数据类型，并考虑这些几何之间的空间关系。

我们已经知道，如果没有高效的索引，任何生产数据库都可能无法生存。空间数据也是如此。然而，由于其性质，常规索引对于空间数据和我们要执行的空间查询类型不是很有效。因此，我们需要称为空间索引的专门索引，可以帮助我们更有效地执行空间操作。

## 5.空间数据类型和查询

现在我们了解了处理空间数据的挑战，注意几种类型的空间数据很重要。此外，我们可以对它们执行几个有趣的查询以满足独特的需求。我们将介绍其中一些数据类型以及我们可以对它们执行的操作。

我们通常谈论关于空间参考系统的空间数据。它由坐标系和基准组成。有几种坐标系，如仿射坐标系、圆柱坐标系、笛卡尔坐标系、椭圆坐标系、线性坐标系、极坐标系、球坐标系和垂直坐标系。基准是一组参数，用于定义坐标系的原点位置、比例和方向。

从广义上讲，许多支持空间数据的数据库将它们分为两类，几何和地理：

[![地理空间应用坐标系](https://www.baeldung.com/wp-content/uploads/2021/07/Geospatial-Application-Coordinate-Systems-1024x471.jpg)](https://www.baeldung.com/wp-content/uploads/2021/07/Geospatial-Application-Coordinate-Systems.jpg)

几何将空间数据存储在平面坐标系上。这有助于我们用笛卡尔空间中的坐标表示点、线和区域等形状。地理存储基于圆地球坐标系的空间数据。这对于用纬度和经度坐标表示地球表面上的相同形状很有用。

我们可以对空间数据进行两种基本类型的查询。这些基本上是为了找到最近的邻居或发送不同类型的范围查询。我们已经看到了早期查找最近邻居的查询示例。一般的想法是识别一定数量的最接近查询点的项目。

另一种重要的查询类型是范围查询。在这里，我们有兴趣了解属于查询范围内的所有项目。查询范围可以是一个矩形，也可以是一个距离查询点一定半径的圆。例如，我们可以使用这种查询来识别我们所站位置两英里半径范围内的所有意大利餐馆。

## 6. 空间数据的数据结构

现在，我们将了解一些更适合构建空间索引的数据结构。这将帮助我们了解它们与常规索引的不同之处以及它们在处理空间操作方面效率更高的原因。几乎所有这些数据结构都是树数据结构的变体。

### 6.1. 常规数据库索引

数据库索引基本上是一种提高数据检索操作速度的数据结构。如果没有索引，我们将不得不遍历所有行来搜索我们感兴趣的行。但是，对于一个非常大的表，即使遍历索引也会花费大量时间。

但是，重要的是要减少获取密钥的步骤数并减少这样做的磁盘操作数。B 树或平衡树是一种自平衡树数据结构，它在每个节点中存储几个排序的键值对。这有助于在一次磁盘读取操作中从处理器缓存中提取更多键。

虽然 B-tree 工作得很好，但通常我们使用[B+tree 来构建数据库索引](https://www.baeldung.com/cs/b-trees-vs-btrees)。B+树与 B 树非常相似，除了它只在叶节点存储值或数据：

[![数据库索引树](https://www.baeldung.com/wp-content/uploads/2021/07/Database-Index-Btree-1024x421.jpg)](https://www.baeldung.com/wp-content/uploads/2021/07/Database-Index-Btree.jpg)

在这里，所有的叶节点也被链接起来，因此提供了对键值对的有序访问。这里的好处是叶节点提供第一级索引，而内部节点提供多级索引。

常规数据库索引侧重于在单个维度上对其键进行排序。例如，我们可以在数据库表中的邮政编码等属性之一上创建索引。这将帮助我们查询具有特定邮政编码或邮政编码范围内的所有位置。

### 6.2. 空间数据库索引

在地理空间应用程序中，我们通常对最近邻或范围查询感兴趣。例如，我们可能希望找到距特定点 10 英里以内的所有位置。常规的数据库索引在这里并不是很有用。事实上，还有其他更适合构建空间索引的数据结构。

最常用的数据结构之一是[R 树](http://www.mathcs.emory.edu/~cheung/Courses/554/Syllabus/3-index/R-tree.html)。R-tree 于 1984 年由 Antonin Guttman 首次提出，适用于存储位置等空间对象。R-tree 背后的基本思想是将附近的对象分组，并在树的下一个更高级别中用它们的最小边界矩形表示它们：

[![数据库索引 R 树](https://www.baeldung.com/wp-content/uploads/2021/07/Database-Index-R-tree-1024x449.jpg)](https://www.baeldung.com/wp-content/uploads/2021/07/Database-Index-R-tree.jpg)

对于大多数操作，R 树与 B 树没有太大区别。关键区别在于使用边界矩形来决定是否在子树内搜索。为了获得更好的性能，我们应该确保矩形不会覆盖太多空白区域并且它们不会重叠太多。最有趣的是，R 树可以扩展到覆盖三个甚至更多维度！

另一种用于构建空间索引的数据结构是[Kd-tree](https://www.cs.cmu.edu/~ckingsf/bioinfo-lectures/kdtrees.pdf)，它是 R-tree 的轻微变体。Kd -tree将数据空间一分为二，而不是分割成多个矩形。因此，Kd 树中的树节点表示分离平面而不是边界框。虽然 Kd-tree 被证明更容易实现并且速度更快，但它不适合总是变化的数据。

这些数据结构背后的关键思想基本上是将数据划分为轴对齐的区域并将它们存储在树节点中。事实上，我们可以使用很多其他这样的数据结构，比如 BSP-tree 和 R-tree。

## 7. 原生支持的数据库

我们已经了解了空间数据与常规数据的不同之处以及它们需要特殊处理的原因。因此，我们需要构建地理空间应用程序的是一个能够原生支持存储空间数据类型并能够高效执行空间查询的数据库。我们将这样的数据库管理系统称为空间数据库管理系统。

几乎所有的主流数据库都开始对空间数据提供一定程度的支持。这包括一些流行的数据库管理系统，如[MySQL](https://www.mysql.com/)、[Microsoft SQL Server](https://www.microsoft.com/en-in/sql-server/sql-server-downloads)、[PostgreSQL](https://www.postgresql.org/)、[Redis](https://redis.io/)、[MongoDB](https://www.mongodb.com/)、[Elasticsearch](https://www.elastic.co/)和[Neo4J](https://neo4j.com/)。但是，也有一些专门构建的空间数据库可用，例如[GeoMesa](https://www.geomesa.org/)、[PostGIS](http://postgis.net/)和[Oracle Spatial](https://www.oracle.com/database/spatial/)。

### 7.1. 雷迪斯

[Redis](https://redis.io/)是一种内存数据结构存储，我们可以将其用作数据库、缓存或消息代理。它可以最大限度地减少网络开销和延迟，因为它在内存中高效地执行操作。Redis 支持多种数据结构，如 Hash、Set、Sorted Set、List 和 String。我们特别感兴趣的是 Sorted Sets，它向成员添加有序视图，按分数排序。

地理空间索引是在 Redis 中使用 Sorted Sets 作为底层数据结构实现的。Redis实际上是使用[geohash算法](https://en.wikipedia.org/wiki/Geohash)将经纬度编码到Sorted Set的score中。Geo Set 是使用 Sorted Set 实现的关键数据结构，在更抽象的层面上支持 Redis 中的地理空间数据。

Redis 提供了简单的命令来处理地理空间索引并执行常见操作，例如创建新集合以及添加或更新集合中的成员。例如，要从命令行创建一个新集合并向其中添加成员，我们可以使用 GEOADD 命令：

```powershell
GEOADD locations 20.99 65.44 Vehicle-1 23.99 55.45 Vehicle-2复制
```

在这里，我们将一些车辆的位置添加到称为“位置”的地理集中。

Redis 还提供了多种读取索引的方法，如 ZRANGE、ZSCAN 和 GEOPOS。此外，我们可以使用命令 GEODIST 来计算集合中成员之间的距离。但最有趣的命令是那些允许我们按位置搜索索引的命令。例如，我们可以使用命令 GEORADIUSYMEMBER 搜索特定成员半径范围内的成员：

```powershell
GEORADIUSBYMEMBER locations Vehicle-3 1000 m复制
```

在这里，我们有兴趣找到第三辆车一公里半径内的所有其他车辆。

Redis 在为存储大量地理空间数据和执行低延迟地理空间查询提供支持方面非常强大且简单。

### 7.2. 数据库

[MongoDB](https://www.mongodb.com/)是一个面向文档的数据库管理系统，它使用类似 JSON 的文档和可选模式来存储数据。它提供了几种搜索文档的方法，如字段查询、范围查询和正则表达式。我们甚至可以将文档索引到主要和次要索引。此外，具有分片和复制功能的 MongoDB 提供了高可用性和水平可扩展性。

我们可以将空间数据作为 GeoJSON 对象或遗留坐标对存储在 MongoDB 中。GeoJSON 对象可用于存储类地表面上的位置数据，而传统坐标对可用于存储我们可以在欧几里得平面中表示的数据。

要指定 GeoJSON 数据，我们可以使用一个嵌入文档，其中包含一个名为 type 的字段来指示 GeoJSON 对象类型，另一个名为coordinates的字段来指示对象的坐标：

```powershell
db.vehicles.insert( {
    name: "Vehicle-1",
    location: { type: "Point", coordinates: [ 83.97, 70.77 ] }
} )复制
```

在这里，我们在名为vehicles 的集合中添加了一个文档。嵌入文档是一个点类型的 GeoJSON 对象，带有经度和纬度坐标。

此外，MongoDB还提供了2dsphere和2d等多种地理空间索引类型来支持地理空间查询。2dsphere支持在类地球体上计算几何形状的查询：

```powershell
db.vehicles.createIndex( { location : "2dsphere" } )复制
```

在这里，我们在集合的位置字段上创建一个2dsphere索引。

最后，MongoDB提供了几个地理空间查询运算符来帮助搜索地理空间数据。一些运算符是geoIntersects、geoWithin、near和nearSphere。这些运算符可以解释平面或球体上的几何图形。

例如，让我们看看如何使用near运算符：

```powershell
db.places.find(
   {
     location:
       { $near:
          {
            $geometry: { type: "Point", coordinates: [ 93.96, 30.78 ] },
            $minDistance: 500,
            $maxDistance: 1000
          }
       }
   }
)复制
```

在这里，我们正在搜索距离上述 GeoJSON Point至少 500 米和最多 1,000 米的文档。

以灵活的模式、扩展效率和对地理空间数据的固有支持来表示类 JSON 数据的能力使 MongoDB 非常适合地理空间应用程序。

### 7.3. 邮政地理信息系统

[PostgreSQL](https://www.postgresql.org/)是一种关系数据库管理系统，提供 SQL 合规性和 ACID 事务功能。它在支持各种工作负载方面非常通用。PostgreSQL 包括对同步复制的内置支持以及对常规 B 树和哈希表索引的内置支持。[PostGIS](https://postgis.net/)是 PostgreSQL 的空间数据库扩展程序。

基本上， PostGIS 添加了对在 PostgreSQL 中存储地理空间数据和在 SQL 中执行位置查询的支持。它为Point s、LineString s、Polygon s 等添加了几何类型。此外，它使用 R-tree-over-GiST(广义搜索树)提供空间索引。最后，它还为地理空间测量和集合操作添加了空间运算符。

我们可以像往常一样[在 PostgreSQL 中创建一个数据库](https://postgis.net/workshops/postgis-intro/creating_db.html)，并启用 PostGIS 扩展以开始使用它。从根本上说，数据存储在行和列中，但 PostGIS 引入了几何列，其中数据位于由空间参考标识符 (SRID) 定义的特定坐标系中。PostGIS 还添加了许多用于加载不同 GIS 数据格式的选项。

PostGIS支持几何和地理数据类型。我们可以使用常规 SQL 查询来创建表并插入地理数据类型：

```powershell
CREATE TABLE vehicles (name VARCHAR, geom GEOGRAPHY(Point));
INSERT INTO vehicles VALUES ('Vehicle-1', 'POINT(44.34 82.96)');复制
```

在这里，我们创建了一个新表“vehicles”，并使用点几何添加了特定车辆的位置。

PostGIS 添加了相当多的空间函数来对数据执行空间操作。例如，我们可以使用空间函数ST_AsText将几何数据读取为文本：

```powershell
SELECT name, ST_AsText(geom) FROM vehicles;复制
```

当然，对我们来说，更有用的查询是查找给定点附近的所有车辆：

```powershell
SELECT geom FROM vehicles 
  WHERE ST_Distance( geom, 'SRID=4326;POINT(43.32 68.35)' ) < 1000复制
```

在这里，我们正在搜索所提供点一公里半径范围内的所有车辆。

PostGIS 将空间功能添加到 PostgreSQL，使我们能够利用空间数据的众所周知的 SQL 语义。此外，我们可以受益于使用 PostgreSQL 的所有优势。

## 8. 行业标准和规范

虽然我们已经看到数据库层对空间数据的支持正在增长，但应用层呢？为了构建地理空间应用程序，我们需要编写能够有效处理空间数据的代码。

此外，我们需要标准和规范来表示和传输不同组件之间的空间数据。此外，语言绑定可以支持我们使用 Java 等语言构建地理空间应用程序。

在本节中，我们将介绍地理空间应用程序领域中出现的一些标准化、它们制定的标准以及可供我们使用的库。

### 8.1. 标准化工作

这个领域已经有了很大的发展，并且通过多个组织的协作努力，已经建立了一些标准和最佳实践。让我们首先了解一些为不同行业的地理空间应用程序的进步和标准化做出贡献的组织。

[环境系统研究所 (ESRI)](https://www.esri.com/en-us/home) 可能是历史最悠久、规模最大的地理信息系统 (GIS) 软件和地理数据库管理应用程序的国际供应商之一。他们开发了一套名为 ArcGIS 的 GIS 软件，适用于桌面、服务器和移动设备等多种平台。它还建立并推广了矢量和栅格数据类型的数据格式——例如，Shapefile、文件地理数据库、Esri Grid 和 Mosaic。

[Open Geospatial Consortium (OGC)](https://www.ogc.org/) 是一个国际行业联盟，由 300 多家公司、政府机构和大学组成，参与协商一致的过程以开发公开可用的接口规范。这些规范使复杂的空间信息和服务能够被各种应用程序访问和使用。目前，OGC 标准包括 30 多个标准，包括空间参考系统标识符 (SRID)、地理标记语言 (GML) 和简单要素 - SQL (SFS)。

[开源地理空间基金会 (OSGeo)](https://www.osgeo.org/)是一个非营利、非政府组织，支持和促进开放地理空间技术和数据的协作开发。它促进地理空间规范，如 Tile Map Service (TMS)。此外，它还有助于开发多个地理空间库，例如 GeoTools 和 PostGIS。它还适用于[QGIS](https://qgis.org/en/site/)等应用程序，这是一种用于数据查看、编辑和分析的桌面 GIS。这些只是 OSGeo 在其旗下推广的项目中的一小部分。

### 8.2. 地理空间标准：OGC GeoAPI

GeoAPI实现标准通过 GeoAPI 库定义了一个 Java 语言 API，包括一组我们可以用来操作地理信息的[类型和方法。](http://www.geoapi.org/)地理信息的底层结构应遵循国际标准化组织 (ISO) 技术委员会 211 和 OGC 采用的规范。

GeoAPI 提供实现中立的 Java 接口。在我们实际使用 GeoAPI 之前，我们必须从第三方实现列表中进行选择。我们可以使用 GeoAPI 执行多个地理空间操作。例如，我们可以从 EPSG 代码中获取坐标参考系统 (CRS)。然后，我们可以在一对 CRS 之间进行类似地图投影的坐标操作：

```java
CoordinateReferenceSystem sourceCRS = 
  crsFactory.createCoordinateReferenceSystem("EPSG:4326");  // WGS 84
CoordinateReferenceSystem targetCRS = 
  crsFactory.createCoordinateReferenceSystem("EPSG:3395");  // WGS 84 / World Mercator
CoordinateOperation operation = opFactory.createOperation(sourceCRS, targetCRS);
double[] sourcePt = new double[] {
  27 + (59 + 17.0 / 60) / 60,         // 27°59'17"N
  86 + (55 + 31.0 / 60) / 60          // 86°55'31"E
};
double[] targetPt = new double[2];
operation.getMathTransform().transform(sourcePt, 0, targetPt, 0, 1);复制
```

在这里，我们使用 GeoAPI 执行地图投影以转换单个 CRS 点。

有几个 GeoAPI 的第三方实现可用作现有库的包装器——例如，NetCDF Wrapper、Proj.6 Wrapper 和 GDAL Wrapper。

### 8.3. 地理空间库：OSGeo GeoTools

[GeoTools](https://geotools.org/)是一个 OSGeo 项目，它提供了一个用于处理地理空间数据的开源 Java 库。GeoTools 数据结构基本上基于 OGC 规范。它定义了关键空间概念和数据结构的接口。它还带有支持功能访问的数据访问 API、呈现的无状态低内存以及使用 XML 模式绑定到 GML 内容的解析技术。

要在 GeoTools 中创建地理空间数据，我们需要定义一个要素类型。最简单的方法是使用类SimpleFeatureType：

```java
SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
builder.setName("Location");
builder.setCRS(DefaultGeographicCRS.WGS84);
builder
  .add("Location", Point.class);
  .length(15)
  .add("Name", String.class);
SimpleFeatureType VEHICLE = builder.buildFeatureType();复制
```

准备好特征类型后，我们可以使用它通过SimpleFeatureBuilder创建特征：

```java
SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(VEHICLE);
DefaultFeatureCollection collection = new DefaultFeatureCollection();
GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);复制
```

我们还实例化了集合来存储我们的特征，并实例化了 GeoTools 工厂类来为位置创建一个点。现在，我们可以将特定位置作为特征添加到我们的集合中：

```java
Point point = geometryFactory.createPoint(new Coordinate(13.46, 42.97));
featureBuilder.add(point);
featureBuilder.add("Vehicle-1");
collection.add(featureBuilder.buildFeature(null))复制
```

这只是触及了我们可以使用 GeoTools 库做的事情的表面。GeoTools支持使用矢量和栅格数据类型。它还允许我们使用像shapefile这样的标准格式的数据。

## 9.总结

在本教程中，我们了解了构建地理空间应用程序的基础知识。我们讨论了构建此类应用程序的性质和挑战。这有助于我们了解可以使用的不同类型的空间数据和数据结构。

此外，我们浏览了一些原生支持存储空间数据、构建空间索引和执行空间操作的开源数据库。最后，我们还介绍了推动地理空间应用标准化工作的一些行业合作。