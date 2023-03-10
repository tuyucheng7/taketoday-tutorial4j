## 1. 简介

Elasticsearch以其全文搜索功能而闻名，但它还具有完整的地理空间支持。

我们可以在[上一篇文章](https://www.baeldung.com/elasticsearch-java)中找到有关设置Elasticsearch和入门的更多信息。

让我们来看看如何在Elasticsearch中保存地理数据，以及如何使用地理查询来搜索这些数据。

## 2. 地理数据类型

要启用地理查询，我们需要手动创建索引的映射并显式设置字段映射。

**为地理类型设置映射时，动态映射将不起作用**。

Elasticsearch提供了两种表示地理数据的方式：

1. 使用geo-point字段类型的经纬度对
2. 使用geo-shape字段类型在GeoJSON中定义的复杂形状

让我们更深入地了解以上每个类别。

### 2.1 Geo Point数据类型

geo-point字段类型接收经纬度对，可用于：

- 查找中心点一定距离内的点
- 查找框内或多边形内的点
- 按地理位置或距中心点的距离聚合文档
- 按距离排序文档

以下是用于保存geo-point数据的字段的示例映射：

```json
PUT /index_name
{
    "mappings": {
        "TYPE_NAME": {
            "properties": {
                "location": { 
                    "type": "geo_point" 
                } 
            } 
        } 
    } 
}
```

正如我们从上面的示例中看到的，location字段的类型是geo_point。因此，我们现在可以在location字段的位置中提供经纬度对。

### 2.2 Geo Shape数据类型

与geo-point不同，geo-shape提供了保存和搜索多边形和矩形等复杂形状的功能。当我们要搜索包含地理点以外的形状的文档时，必须使用geo-shape数据类型。

让我们看一下geo-shape数据类型的映射：

```json
PUT /index_name
{
    "mappings": {
        "TYPE_NAME": {
            "properties": {
                "location": {
                    "type": "geo_shape"
                }
            }
        }
    }
}
```

Elasticsearch的最新版本将提供的geo-shape分解为三角形网格。根据[官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-shape.html#geo-shape-mapping-options)，这提供了近乎完美的空间分辨率。

## 3. 保存Geo Point数据的不同方式

### 3.1 纬度经度对象

```json
PUT index_name/index_type/1
{
    "location": { 
        "lat": 23.02,
        "lon": 72.57
    }
}
```

在这里，地理点location被保存为一个以纬度和经度为键的对象。

### 3.2 纬度经度对

```json
{
    "location": "23.02,72.57"
}
```

在这里，location表示为纯字符串格式的经纬度对。请注意，字符串格式的经纬度顺序。

### 3.3 地理哈希

```json
{
    "location": "tsj4bys"
}
```

我们还可以以地理哈希的形式提供地理点数据，如上例所示。我们可以使用[在线工具](http://www.movable-type.co.uk/scripts/geohash.html)将经纬度转换为geo-hash。

### 3.4 经纬度数组

```json
{
    "location": [72.57, 23.02]
}
```

**当纬度和经度作为数组提供时，纬度-经度的顺序是相反的**。最初，经纬度对同时用于字符串和数组中，但后来为了匹配[GeoJSON](https://en.wikipedia.org/wiki/GeoJSON)使用的格式，它被颠倒了。

## 4. 保存Geo Shape数据的不同方式

### 4.1 Point

```json
POST /index/type
{
    "location" : {
        "type" : "point",
        "coordinates" : [72.57, 23.02]
    }
}
```

在这里，我们尝试插入的地理形状类型是一个点。请看一下location字段，我们有一个由字段type和coordinates组成的嵌套对象。这些元字段帮助Elasticsearch识别地理形状及其实际数据。

### 4.2 LineString

```json
POST /index/type
{
    "location" : {
        "type" : "linestring",
        "coordinates" : [[77.57, 23.02], [77.59, 23.05]]
    }
}
```

在这里，我们插入linestring地理形状。linestring的坐标由两点组成，即起点和终点。LineString地理形状对于导航用例非常有帮助。

### 4.3 Polygon

```json
POST /index/type
{
    "location" : {
        "type" : "polygon",
        "coordinates" : [
            [ [10.0, 0.0], [11.0, 0.0], [11.0, 1.0], [10.0, 1.0], [10.0, 0.0] ]
        ]
    }
}
```

在这里，我们插入polygon地理形状。请查看上面示例中的coordinates，多边形中的第一个和最后一个坐标应始终匹配，即闭合多边形。

**Elasticsearch还支持其他GeoJSON结构。其他支持格式的完整列表如下**：

- **MultiPoint**
- **MultiLineString**
- **MultiPolygon**
- **GeometryCollection**
- **Envelope**
- **Circle**

我们可以在[ElasticSearch官方网站](https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-shape.html#input-structure)上找到上述支持格式的示例。

对于所有结构，内部type和coordinates都是必填字段。此外，由于结构复杂，目前无法在Elasticsearch中对geo-shape字段进行排序和检索。因此，检索地理字段的唯一方法是从源字段。

## 5. ElasticSearch地理查询

现在，我们知道如何插入包含地理形状的文档，让我们深入研究使用地理形状查询来获取这些记录。但在我们开始使用地理查询之前，我们需要以下Maven依赖项来支持地理查询的Java API：

```xml
<dependency>
    <groupId>org.locationtech.spatial4j</groupId>
    <artifactId>spatial4j</artifactId>
    <version>0.7</version> 
</dependency>
<dependency>
    <groupId>com.vividsolutions</groupId>
    <artifactId>jts</artifactId>
    <version>1.13</version>
    <exclusions>
        <exclusion>
            <groupId>xerces</groupId>
            <artifactId>xercesImpl</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

我们也可以在[Maven中央仓库](https://central.sonatype.com/artifact/org.locationtech.spatial4j/spatial4j/0.8)中搜索上述依赖项。

Elasticsearch支持不同类型的地理查询，它们如下：

### 5.1 地理形状查询

这需要geo_shape映射。

与geo_shape类型类似，geo_shape使用GeoJSON结构来查询文档。

下面是一个示例查询，用于获取给定左上角和右下角坐标范围内的所有文档：

```json
{
    "query": {
        "bool": {
            "must": {
                "match_all": {}
            },
            "filter": {
                "geo_shape": {
                    "region": {
                        "shape": {
                            "type": "envelope",
                            "coordinates": [
                                [
                                    75.00,
                                    25.0
                                ],
                                [
                                    80.1,
                                    30.2
                                ]
                            ]
                        },
                        "relation": "within"
                    }
                }
            }
        }
    }
}
```

这里，relation决定了搜索时使用的**空间关系运算符**。

以下是支持的运算符列表：

- **INTERSECTS**(默认)：返回其geo_shape字段与查询几何图形相交的所有文档
- **DISJOINT**：检索其geo_shape字段与查询几何没有共同点的所有文档
- **WITHIN**：获取其geo_shape字段在查询几何内的所有文档
- **CONTAINS**：返回其geo_shape字段包含查询几何的所有文档

同样，我们可以使用不同的GeoJSON形状进行查询。

上述查询的Java代码如下：

```java
Coordinate topLeft = new Coordinate(74, 31.2);
Coordinate bottomRight = new Coordinate(81.1, 24);

GeoShapeQueryBuilder qb = QueryBuilders.geoShapeQuery("region",
    new EnvelopeBuilder(topLeft, bottomRight).buildGeometry());
qb.relation(ShapeRelation.INTERSECTS);
```

### 5.2 地理边界框查询

地理边界框查询用于根据点位置获取所有文档。下面是一个示例边界框查询：

```json
{
    "query": {
        "bool": {
            "must": {
                "match_all": {}
            },
            "filter": {
                "geo_bounding_box": {
                    "location": {
                        "bottom_left": [
                            28.3,
                            30.5
                        ],
                        "top_right": [
                            31.8,
                            32.12
                        ]
                    }
                }
            }
        }
    }
}
```

上述边界框查询的Java代码如下：

```java
QueryBuilders
    .geoBoundingBoxQuery("location").setCorners(31.8, 30.5, 28.3, 32.12);
```

地理边界框查询支持与我们在geo_point数据类型中类似的格式。有关支持的格式的示例查询可以在[官方网站](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-bounding-box-query.html#_accepted_formats)上找到。

### 5.3 地理距离查询

地理距离查询用于过滤指定点范围内的所有文档。

这是一个示例geo_distance查询：

```json
{
    "query": {
        "bool": {
            "must": {
                "match_all": {}
            },
            "filter": {
                "geo_distance": {
                    "distance": "10miles",
                    "location": [
                        31.131,
                        29.976
                    ]
                }
            }
        }
    }
}
```

这是上述查询的Java代码：

```java
QueryBuilders
    .geoDistanceQuery("location")
    .point(29.976, 31.131)
    .distance(10, DistanceUnit.MILES);
```

与geo_point类似，地理距离查询也支持多种格式传递位置坐标。有关支持格式的更多详细信息，请访问[官方网站](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-query.html#_accepted_formats_2)。

### 5.4 地理多边形查询

用于过滤具有落在给定多边形点内的点的所有记录的查询。

让我们快速看一下示例查询：

```json
{
    "query": {
        "bool": {
            "must": {
                "match_all": {}
            },
            "filter": {
                "geo_polygon": {
                    "location": {
                        "points": [
                            {
                                "lat": 22.733,
                                "lon": 68.859
                            },
                            {
                                "lat": 24.733,
                                "lon": 68.859
                            },
                            {
                                "lat": 23,
                                "lon": 70.859
                            }
                        ]
                    }
                }
            }
        }
    }
}
```

此查询的Java代码：

```java
List<GeoPoint> allPoints = new ArrayList<GeoPoint>(); 
allPoints.add(new GeoPoint(22.733, 68.859)); 
allPoints.add(new GeoPoint(24.733, 68.859)); 
allPoints.add(new GeoPoint(23, 70.859));

QueryBuilders.geoPolygonQuery("location", allPoints);
```

地理多边形查询还支持以下格式：

-   lat-long作为数组：[lon,lat]
-   lat-long作为一个字符串：“lat,lon”
-   geo-hash

要使用此查询，geo_point数据类型是必需的。

## 6. 总结

在本文中，我们讨论了用于索引地理数据的不同映射选项，即geo_point和geo_shape。

我们还尝试了不同的方式来存储地理数据，最后，我们观察了地理查询和Java API，以使用地理查询来过滤结果。