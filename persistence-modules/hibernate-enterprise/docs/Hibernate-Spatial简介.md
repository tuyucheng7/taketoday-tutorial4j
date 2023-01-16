## 1. 简介

在本文中，我们将了解 Hibernate 的空间扩展[hibernate-spatial](http://www.hibernatespatial.org/)。

从版本 5 开始，Hibernate Spatial 提供了一个用于处理地理数据的标准接口。

## 2. Hibernate Spatial 的背景

地理数据包括点、线、多边形等实体的表示。此类数据类型不是 JDBC 规范的一部分，因此[JTS(JTS 拓扑套件)](http://www.tsusiatsoftware.net/jts/main.html)已成为表示空间数据类型的标准。

除了 JTS，Hibernate spatial 还支持[Geolatte-geom——](https://github.com/GeoLatte/geolatte-geom)一个最近的库，它有一些 JTS 中没有的特性。

这两个库都已包含在 hibernate-spatial 项目中。使用一个库而不是另一个库只是我们从哪个 jar 导入数据类型的问题。

尽管 Hibernate spatial 支持不同的数据库，如 Oracle、MySQL、PostgreSQLql/PostGIS 和其他一些数据库，但对数据库特定功能的支持并不统一。

最好参考最新的 Hibernate 文档来检查 hibernate 为给定数据库提供支持的函数列表。

在本文中，我们将使用内存中的[Mariadb4j——](https://github.com/vorburger/MariaDB4j)它保留了 MySQL 的全部功能。

Mariadb4j 和 MySql 的配置相似，甚至 mysql-connector 库也适用于这两个数据库。

## 3 . Maven 依赖项

让我们看一下设置一个简单的 hibernate-spatial 项目所需的 Maven 依赖项：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.2.12.Final</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-spatial</artifactId>
    <version>5.2.12.Final</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>6.0.6</version>
</dependency>
<dependency>
    <groupId>ch.vorburger.mariaDB4j</groupId>
    <artifactId>mariaDB4j</artifactId>
    <version>2.2.3</version>
</dependency>

```

hibernate -spatial依赖项将为空间数据类型提供支持。最新版本的[hibernate-core](https://search.maven.org/classic/#search|ga|1|g%3A"org.hibernate" AND a%3A"hibernate-core")、[hibernate-spatial](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-spatial")、[mysql-connector-java](https://search.maven.org/classic/#search|gav|1|g%3A"mysql" AND a%3A"mysql-connector-java")和[mariaDB4j](https://search.maven.org/classic/#search|gav|1|g%3A"ch.vorburger.mariaDB4j" AND a%3A"mariaDB4j")可以从 Maven Central 获得。

## 4. 配置 Hibernate Spatial

第一步是在资源目录中创建一个hibernate.properties ：

```plaintext
hibernate.dialect=org.hibernate.spatial.dialect.mysql.MySQL56SpatialDialect
// ...
```

唯一特定于 hibernate-spatial 的是MySQL56SpatialDialect dialect。此方言扩展了MySQL55Dialect方言并提供与空间数据类型相关的附加功能。

特定于加载属性文件、创建SessionFactory和实例化 Mariadb4j 实例的代码与标准休眠项目中的代码相同。

## 5 . 了解几何类型

几何是 JTS 中所有空间类型的基本类型。这意味着其他类型(如Point、Polygon和其他类型)从Geometry扩展而来。java中的Geometry类型也对应MySql中的GEOMETRY类型。

通过解析类型的String表示，我们得到了Geometry的一个实例。JTS 提供的实用程序类WKTReader可用于将任何[众所周知的文本](https://en.wikipedia.org/wiki/Well-known_text)表示形式转换为Geometry类型：

```java
public Geometry wktToGeometry(String wellKnownText) 
  throws ParseException {
 
    return new WKTReader().read(wellKnownText);
}
```

现在，让我们看看这个方法的实际效果：

```java
@Test
public void shouldConvertWktToGeometry() {
    Geometry geometry = wktToGeometry("POINT (2 5)");
 
    assertEquals("Point", geometry.getGeometryType());
    assertTrue(geometry instanceof Point);
}
```

正如我们所看到的，即使方法的返回类型是read()方法是Geometry，实际实例是Point的实例。

## 6. 在数据库中存储一个点

现在我们已经很好地了解什么是Geometry类型以及如何从String中获取Point，让我们看一下PointEntity：

```java
@Entity
public class PointEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Point point;

    // standard getters and setters
}
```

请注意，实体PointEntity包含空间类型Point。如前所述，一个点由两个坐标表示：

```java
public void insertPoint(String point) {
    PointEntity entity = new PointEntity();
    entity.setPoint((Point) wktToGeometry(point));
    session.persist(entity);
}
```

方法insertPoint()接受Point的众所周知的文本 (WKT) 表示，将其转换为Point实例，并保存在数据库中。

提醒一下，该会话并不特定于 hibernate-spatial，它的创建方式类似于另一个 hibernate 项目。

我们可以在这里注意到，一旦我们创建了Point的实例，存储PointEntity的过程类似于任何常规实体。

让我们看一些测试：

```java
@Test
public void shouldInsertAndSelectPoints() {
    PointEntity entity = new PointEntity();
    entity.setPoint((Point) wktToGeometry("POINT (1 1)"));

    session.persist(entity);
    PointEntity fromDb = session
      .find(PointEntity.class, entity.getId());
 
    assertEquals("POINT (1 1)", fromDb.getPoint().toString());
    assertTrue(geometry instanceof Point);
}
```

在Point上调用toString()会返回Point的 WKT 表示。这是因为Geometry类覆盖了toString()方法并在内部使用了WKTWriter，它是我们之前看到的WKTReader的补充类。

一旦我们运行这个测试，hibernate 就会为我们创建PointEntity表。

让我们看一下该表：

```sql
desc PointEntity;
Field    Type          Null    Key
id       bigint(20)    NO      PRI
point    geometry      YES
```

正如预期的那样，场点的类型是GEOMETRY。因此，在使用我们的 SQL 编辑器(如 MySql 工作台)获取数据时，我们需要将此 GEOMETRY 类型转换为人类可读的文本：

```sql
select id, astext(point) from PointEntity;

id      astext(point)
1       POINT(2 4)
```

然而，当我们在Geometry或其任何子类上调用toString()方法时，hibernate 已经返回 WKT 表示，因此我们不需要为这种转换操心。

## 7. 使用空间函数

### 7.1. ST_WITHIN()例子

我们现在来看看处理空间数据类型的数据库函数的用法。

MySQL 中的此类函数之一是ST_WITHIN()，它告诉你一个几何图形是否在另一个几何图形中。这里的一个很好的例子是找出给定半径内的所有点。

让我们先看看如何创建一个圆圈：

```java
public Geometry createCircle(double x, double y, double radius) {
    GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
    shapeFactory.setNumPoints(32);
    shapeFactory.setCentre(new Coordinate(x, y));
    shapeFactory.setSize(radius  2);
    return shapeFactory.createCircle();
}
```

圆由setNumPoints()方法指定的有限点集表示。半径在调用setSize()方法之前加倍，因为我们需要在两个方向上围绕中心绘制圆。

现在让我们继续前进，看看如何获取给定半径内的点：

```java
@Test
public void shouldSelectAllPointsWithinRadius() throws ParseException {
    insertPoint("POINT (1 1)");
    insertPoint("POINT (1 2)");
    insertPoint("POINT (3 4)");
    insertPoint("POINT (5 6)");

    Query query = session.createQuery("select p from PointEntity p where 
      within(p.point, :circle) = true", PointEntity.class);
    query.setParameter("circle", createCircle(0.0, 0.0, 5));

    assertThat(query.getResultList().stream()
      .map(p -> ((PointEntity) p).getPoint().toString()))
      .containsOnly("POINT (1 1)", "POINT (1 2)");
    }
```

Hibernate 将其within()函数映射到MySql 的ST_WITHIN()函数。

这里一个有趣的观察是点 (3, 4) 恰好落在圆上。不过，查询不会返回这一点。这是因为只有当给定的Geometry完全在另一个Geometry内时， within()函数才返回 true。

### 7.2. ST_TOUCHES()示例

在这里，我们将展示一个示例，在数据库中插入一组Polygon并选择与给定Polygon相邻的Polygon。让我们快速浏览一下PolygonEntity类：

```java
@Entity
public class PolygonEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Polygon polygon;

    // standard getters and setters
}
```

这里与之前的PointEntity唯一不同的是我们使用类型Polygon而不是Point。

现在让我们进行测试：

```java
@Test
public void shouldSelectAdjacentPolygons() throws ParseException {
    insertPolygon("POLYGON ((0 0, 0 5, 5 5, 5 0, 0 0))");
    insertPolygon("POLYGON ((3 0, 3 5, 8 5, 8 0, 3 0))");
    insertPolygon("POLYGON ((2 2, 3 1, 2 5, 4 3, 3 3, 2 2))");

    Query query = session.createQuery("select p from PolygonEntity p 
      where touches(p.polygon, :polygon) = true", PolygonEntity.class);
    query.setParameter("polygon", wktToGeometry("POLYGON ((5 5, 5 10, 10 10, 10 5, 5 5))"));
    assertThat(query.getResultList().stream()
      .map(p -> ((PolygonEntity) p).getPolygon().toString())).containsOnly(
      "POLYGON ((0 0, 0 5, 5 5, 5 0, 0 0))", "POLYGON ((3 0, 3 5, 8 5, 8 0, 3 0))");
}
```

insertPolygon()方法类似于我们之前看到的insertPoint()方法。源代码包含此方法的完整实现。

我们正在使用touches()函数来查找与给定Polygon相邻的Polygon。显然，结果中没有返回第三个Polygon，因为没有边接触给定的Polygon。

## 八. 总结

在本文中，我们已经看到 hibernate-spatial 使处理空间数据类型变得更加简单，因为它处理了低级细节。

尽管本文使用的是Mariadb4j，但我们可以在不修改任何配置的情况下将其替换为MySql。