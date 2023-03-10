## 1. 简介

本文是对[Neo4j](https://neo4j.com/)(当今市场上最成熟、功能最全的图数据库之一)的介绍。图数据库处理数据建模任务的观点是，生活中的许多事物都可以表示为节点(V)的集合以及它们之间称为边(E)的连接。

## 2. 嵌入式Neo4j

开始使用Neo4j的最简单方法是使用嵌入式版本，其中Neo4j与你的应用程序在同一JVM中运行。

首先，我们需要添加Maven依赖项：

```xml
<dependency>
    <groupId>org.neo4j</groupId>
    <artifactId>neo4j</artifactId>
    <version>3.4.6</version>
</dependency>
```

你可以查看[此链接](https://central.sonatype.com/artifact/org.neo4j/neo4j/5.5.0)以下载最新版本。

接下来，让我们创建一个工厂：

```java
GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
```

最后，我们创建一个嵌入式数据库：

```java
GraphDatabaseService graphDb = graphDbFactory.newEmbeddedDatabase(new File("data/cars"));
```

现在可以开始真正的数据库操作了！首先，我们需要在图中创建一些节点，为此，我们需要启动一个事务，因为Neo4j将拒绝任何破坏性操作，除非事务已经启动：

```java
graphDb.beginTx();
```

一旦我们有一个正在进行的事务，我们就可以开始添加节点：

```java
Node car = graphDb.createNode(Label.label("Car"));
car.setProperty("make", "tesla");
car.setProperty("model", "model3");

Node owner = graphDb.createNode(Label.label("Person"));
owner.setProperty("firstName", "tuyucheng");
owner.setProperty("lastName", "tuyucheng");
```

在这里，我们添加了一个具有属性make和model的节点Car以及具有属性firstName和lastName的节点Person。

现在我们可以添加一个关系：

```java
owner.createRelationshipTo(car, RelationshipType.withName("owner"));
```

上面的语句添加了一条边，将两个节点连接起来并带有owner标签。我们可以通过运行用Neo4j强大的Cypher语言编写的查询来验证这种关系：

```java
Result result = graphDb.execute(
    "MATCH (c:Car) <-[owner]- (p:Person) " +
    "WHERE c.make = 'tesla'" +
    "RETURN p.firstName, p.lastName");
```

在这里，我们要求找到任何一辆特斯拉汽车的车主，并返回他/她的firstName和lastName。毫不奇怪，这将返回：{p.firstName=tuyucheng, p.lastName=tuyucheng}

## 3. Cypher查询语言

Neo4j提供了一种非常强大且非常直观的查询语言，它支持人们期望从数据库获得的全部功能。让我们来看看如何完成标准的创建、检索、更新和删除任务。

### 3.1 创建节点

Create关键字可用于创建节点和关系。

```
CREATE (self:Company {name:"Tuyucheng"})
RETURN self
```

在这里，我们创建了一个具有单一属性name的公司。节点定义用圆括号标记，其属性用花括号括起来。在这种情况下，self是节点的别名，Company是节点标签。

### 3.2 创建关系

可以在单个查询中创建一个节点和与该节点的关系：

```text
Result result = graphDb.execute(
    "CREATE (tuyucheng:Company {name:\"Tuyucheng\"}) " +
    "-[:owns]-> (tesla:Car {make: 'tesla', model: 'modelX'})" +
    "RETURN tuyucheng, tesla");
```

这里我们创建了节点tuyucheng和tesla，并在它们之间建立了所有者关系。当然，也可以创建与预先存在的节点的关系。

### 3.3 检索数据

MATCH关键字用于查找数据，结合RETURN来控制返回哪些数据点。WHERE子句可用于仅过滤掉那些具有我们所需属性的节点。

让我们找出拥有tesla modelX的公司名称：

```text
Result result = graphDb.execute(
    "MATCH (company:Company)-[:owns]-> (car:Car)" +
    "WHERE car.make='tesla' and car.model='modelX'" +
    "RETURN company.name");
```

### 3.4 更新节点

SET关键字可用于更新节点属性或标签。让我们为我们的tesla增加milage：

```text
Result result = graphDb.execute("MATCH (car:Car)" +
    "WHERE car.make='tesla'" +
    " SET car.milage=120" +
    " SET car :Car:Electro" +
    " SET car.model=NULL" +
    " RETURN car");
```

在这里，我们添加了一个名为milage的新属性，将标签修改为Car和Electro，最后，我们完全删除了模型属性。

### 3.5 删除节点

DELETE关键字可用于从图中永久删除节点或关系：

```text
graphDb.execute("MATCH (company:Company)" +
    " WHERE company.name='Tuyucheng'" +
    " DELETE company");
```

这里我们删除了一个名为Tuyucheng的公司。

### 3.6 参数绑定

在上面的例子中，我们有硬编码的参数值，这不是最佳实践。幸运的是，Neo4j提供了一种将变量绑定到查询的工具：

```java
Map<String, Object> params = new HashMap<>();
params.put("name", "tuyucheng");
params.put("make", "tesla");
params.put("model", "modelS");

Result result = graphDb.execute("CREATE (tuyucheng:Company {name:$name}) " +
    "-[:owns]-> (tesla:Car {make: $make, model: $model})" +
    "RETURN tuyucheng, tesla", params);
```

## 4. Java驱动程序

到目前为止，我们一直在考虑与嵌入式Neo4j实例进行交互，但是，在生产环境中，我们很可能希望运行一个独立的服务器并通过提供的驱动程序连接到它。首先，我们需要在我们的Maven pom.xml中添加另一个依赖项：

```xml
<dependency>
    <groupId>org.neo4j.driver</groupId>
    <artifactId>neo4j-java-driver</artifactId>
    <version>1.6.2</version>
</dependency>
```

你可以点击[此链接](https://central.sonatype.com/artifact/org.neo4j.driver/neo4j-java-driver/5.6.0)来检查此驱动程序的最新版本。

现在我们可以创建连接：

```java
Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "12345"));
```

然后，创建一个会话：

```java
Session session = driver.session();
```

最后，我们可以运行一些查询：

```java
session.run("CREATE (tuyucheng:Company {name:\"Tuyucheng\"}) " +
    "-[:owns]-> (tesla:Car {make: 'tesla', model: 'modelX'})" +
    "RETURN tuyucheng, tesla");
```

完成所有工作后，我们需要关闭会话和驱动程序：

```java
session.close();
driver.close();
```

## 5. JDBC驱动程序

也可以通过JDBC驱动程序与Neo4j交互。pom.xml的另一个依赖项：

```xml
<dependency>
    <groupId>org.neo4j</groupId>
    <artifactId>neo4j-jdbc-driver</artifactId>
    <version>3.4.0</version>
</dependency>
```

你可以点击[此链接](https://central.sonatype.com/artifact/org.neo4j/neo4j-jdbc-driver/4.0.9)下载此驱动程序的最新版本。

接下来，让我们建立一个JDBC连接：

```xml
Connection con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost/?user=neo4j,password=12345,scheme=basic");
```

这里的con是一个常规的JDBC连接，可用于创建和执行语句或预准备语句：

```java
try (Statement stmt = con.
    stmt.execute("CREATE (tuyucheng:Company {name:\"Tuyucheng\"}) "
    + "-[:owns]-> (tesla:Car {make: 'tesla', model: 'modelX'})"
    + "RETURN tuyucheng, tesla")
    
        ResultSet rs = stmt.executeQuery(
            "MATCH (company:Company)-[:owns]-> (car:Car)" +
            "WHERE car.make='tesla' and car.model='modelX'" +
            "RETURN company.name");
        
        while (rs.next()) {
            rs.getString("company.name");
        }
}
```

## 6. 对象图映射

对象图映射或OGM是一种技术，它使我们能够将我们的域POJO用作Neo4j数据库中的实体。让我们来看看这是如何工作的。第一步，像往常一样，我们向pom.xml添加新的依赖项：

```xml
<dependency>
    <groupId>org.neo4j</groupId>
    <artifactId>neo4j-ogm-core</artifactId>
    <version>3.1.2</version>
</dependency>

<dependency> 
    <groupId>org.neo4j</groupId>
    <artifactId>neo4j-ogm-embedded-driver</artifactId>
    <version>3.1.2</version>
</dependency>
```

你可以查看[OGM Core Link](https://central.sonatype.com/artifact/org.neo4j/neo4j-ogm-core/4.0.4)和[OGM Embedded Driver Link](https://central.sonatype.com/artifact/org.neo4j/neo4j-ogm-embedded-driver/3.2.39)来检查这些库的最新版本。

其次，我们使用OGM注解来标注我们的POJO：

```java
@NodeEntity
public class Company {
    private Long id;

    private String name;

    @Relationship(type="owns")
    private Car car;
}

@NodeEntity
public class Car {
    private Long id;

    private String make;

    @Relationship(direction = "INCOMING")
    private Company company;
}
```

@NodeEntity通知Neo4j该对象将需要由结果图中的节点表示。@Relationship传达了与表示相关类型的节点创建关系的需要。在这种情况下，一家公司拥有一辆汽车。

请注意，Neo4j要求每个实体都有一个主键，默认情况下会选择一个名为id的字段。可以通过使用@Id、@GeneratedValue对其进行标注来使用替代命名的字段。

然后，我们需要创建一个用于引导Neo4j的OGM的配置。为简单起见，让我们使用一个嵌入式内存数据库：

```java
Configuration conf = new Configuration.Builder().build();
```

之后，我们使用我们创建的配置和我们带注解的POJO所在的包名称初始化SessionFactory：

```java
SessionFactory factory = new SessionFactory(conf, "cn.tuyucheng.taketoday.graph");
```

最后，我们可以创建一个Session并开始使用它：

```java
Session session = factory.openSession();
Car tesla = new Car("tesla", "modelS");
Company baeldung = new Company("baeldung");

baeldung.setCar(tesla);
session.save(baeldung);
```

在这里，我们启动了一个会话，创建了我们的POJO，并要求OGM会话持久化它们。Neo4j OGM运行时透明地将对象转换为一组Cypher查询，这些查询在数据库中创建了适当的节点和边。

如果这个过程看起来很熟悉，那是因为这正是JPA的工作方式，唯一的区别在于对象是被转换为持久保存到RDBMS的行，还是持久保存到图数据库的一系列节点和边。

## 7. 总结

本文介绍了面向图的数据库Neo4j的一些基础知识。