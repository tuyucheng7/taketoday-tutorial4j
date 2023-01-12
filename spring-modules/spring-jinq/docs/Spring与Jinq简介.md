## 1. 简介

[Jinq](http://www.jinq.org/)为在Java中查询数据库提供了一种直观且方便的方法。在本教程中，我们将探讨如何配置 Spring 项目以使用 Jinq及其一些通过简单示例说明的功能。

## 2.Maven依赖

我们需要在pom.xml文件中添加[Jinq 依赖项：](https://search.maven.org/classic/#search|gav|1|g%3A"org.jinq" AND a%3A"jinq")

```xml
<dependency>
    <groupId>org.jinq</groupId>
    <artifactId>jinq-jpa</artifactId>
    <version>1.8.22</version>
</dependency>
```

对于 Spring，我们将在pom.xml文件中添加[Spring ORM 依赖项：](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework" AND a%3A"spring-orm")

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>5.3.3</version>
</dependency>
```

最后，为了测试，我们将使用 H2 内存数据库，因此我们也将此[依赖](https://search.maven.org/classic/#search|gav|1|g%3A"com.h2database" AND a%3A"h2")项与spring-boot-starter-data-jpa一起添加到 pom.xml 文件中：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.7.2</version>
</dependency>
```

## 3. 了解Jinq

Jinq 通过公开内部基于[Java Stream API 的流畅 API 帮助我们编写更简单、更易读的数据库查询。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html)

让我们看一个按型号过滤汽车的例子：

```java
jinqDataProvider.streamAll(entityManager, Car.class)
  .where(c -> c.getModel().equals(model))
  .toList();
```

Jinq 以高效的方式将上述代码片段转换为 SQL 查询，因此本例中的最终查询将是：

```sql
select c. from car c where c.model=?
```

由于我们不使用纯文本来编写查询，而是使用类型安全的 API，因此这种方法不太容易出错。

此外，Jinq 旨在通过使用通用、易于阅读的表达式来加快开发速度。

然而，它在我们可以使用的类型和操作的数量上有一些限制，我们将在接下来看到。

### 3.1. 限制

Jinq 仅支持 JPA 中的基本类型和 SQL 函数的具体列表。它通过将所有对象和方法映射到 JPA 数据类型和 SQL 函数，将 lambda 操作转换为原生 SQL 查询来工作。

因此，我们不能期望该工具可以翻译每个自定义类型或一个类型的所有方法。

### 3.2. 支持的数据类型

让我们看看支持的数据类型和方法：

-   String –仅equals() , compareTo()方法
-   原始数据类型——算术运算
-   枚举和自定义类——仅支持 == 和 != 操作
-   java.util.Collection –包含()
-   日期API –仅限equals()、before()、after()方法

注意：如果我们想要自定义从Java对象到数据库对象的转换，我们需要在 Jinq中注册我们的AttributeConverter的具体实现。

## 4. Jinq 与 Spring 集成

Jinq 需要一个[EntityManager](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html)实例来获取持久化上下文。在本教程中，我们将介绍一种使用 Spring 的简单方法，使 Jinq 与[Hibernate](http://hibernate.org/)提供的EntityManager一起工作。

### 4.1. 存储库接口

Spring 使用存储库的概念来管理实体。让我们看看我们的CarRepository接口，其中我们有一个方法来检索给定模型的Car ：

```java
public interface CarRepository {
    Optional<Car> findByModel(String model);
}
```

### 4.2. 抽象库

接下来，我们需要一个基础存储库来提供所有 Jinq 功能：

```java
public abstract class BaseJinqRepositoryImpl<T> {
    @Autowired
    private JinqJPAStreamProvider jinqDataProvider;

    @PersistenceContext
    private EntityManager entityManager;

    protected abstract Class<T> entityType();

    public JPAJinqStream<T> stream() {
        return streamOf(entityType());
    }

    protected <U> JPAJinqStream<U> streamOf(Class<U> clazz) {
        return jinqDataProvider.streamAll(entityManager, clazz);
    }
}
```

### 4.3. 实施存储库

现在，我们对 Jinq 只需要一个EntityManager实例和实体类型类。

让我们看看使用我们刚刚定义的 Jinq 基础存储库的Car存储库实现：

```java
@Repository
public class CarRepositoryImpl 
  extends BaseJinqRepositoryImpl<Car> implements CarRepository {

    @Override
    public Optional<Car> findByModel(String model) {
        return stream()
          .where(c -> c.getModel().equals(model))
          .findFirst();
    }

    @Override
    protected Class<Car> entityType() {
        return Car.class;
    }
}
```

### 4.4. 连接JinqJPAStreamProvider

为了连接JinqJPAStreamProvider实例，我们将添加 Jinq 提供者配置：

```java
@Configuration
public class JinqProviderConfiguration {

    @Bean
    @Autowired
    JinqJPAStreamProvider jinqProvider(EntityManagerFactory emf) {
        return new JinqJPAStreamProvider(emf);
    }
}
```

### 4.5. 配置 Spring 应用程序

最后一步是使用 Hibernate 和我们的 Jinq 配置来配置我们的 Spring 应用程序。作为参考，请参阅我们的application.properties文件，其中我们使用内存中的 H2 实例作为数据库：

```plaintext
spring.datasource.url=jdbc:h2:~/jinq
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
```

## 五、查询指南

Jinq 提供了许多直观的选项来使用select、where、 join 等自定义最终的 SQL 查询。请注意，这些具有与我们上面已经介绍的相同的限制。

### 5.1. 在哪里

where子句允许将多个过滤器应用于数据集合。

在下一个示例中，我们要按型号和描述过滤汽车：

```java
stream()
  .where(c -> c.getModel().equals(model)
    && c.getDescription().contains(desc))
  .toList();
```

这是 Jinq 翻译的 SQL：

```plaintext
select c.model, c.description from car c where c.model=? and locate(?, c.description)>0
```

### 5.2. 选择

如果我们只想从数据库中检索几个列/字段，我们需要使用select子句。

为了映射多个值，Jinq 提供了多个Tuple类，最多有八个值：

```java
stream()
  .select(c -> new Tuple3<>(c.getModel(), c.getYear(), c.getEngine()))
  .toList()
```

以及翻译后的 SQL：

```plaintext
select c.model, c.year, c.engine from car c
```

### 5.3. 加入

如果实体正确链接，Jinq 能够解决一对一和多对一的关系。

例如，如果我们在Car中添加制造商实体：

```java
@Entity(name = "CAR")
public class Car {
    //...
    @OneToOne
    @JoinColumn(name = "name")
    public Manufacturer getManufacturer() {
        return manufacturer;
    }
}
```

以及带有Car列表的Manufacturer实体：

```java
@Entity(name = "MANUFACTURER")
public class Manufacturer {
    // ...
    @OneToMany(mappedBy = "model")
    public List<Car> getCars() {
        return cars;
    }
}
```

我们现在能够获得给定模型的制造商：

```java
Optional<Manufacturer> manufacturer = stream()
  .where(c -> c.getModel().equals(model))
  .select(c -> c.getManufacturer())
  .findFirst();
```

正如预期的那样， Jinq 将在这种情况下使用内部连接 SQL 子句：

```plaintext
select m.name, m.city from car c inner join manufacturer m on c.name=m.name where c.model=?
```

如果我们需要对join子句有更多的控制，以便在实体上实现更复杂的关系，比如多对多关系，我们可以使用join方法：

```java
List<Pair<Manufacturer, Car>> list = streamOf(Manufacturer.class)
  .join(m -> JinqStream.from(m.getCars()))
  .toList()
```

最后，我们可以通过使用leftOuterJoin方法而不是join方法来使用左外连接 SQL 子句。

### 5.4. 聚合

到目前为止我们介绍的所有示例都使用toList或findFirst方法——返回我们在 Jinq 中查询的最终结果。

除了这些方法，我们还可以使用其他方法来聚合结果。

例如，让我们使用count方法获取数据库中具体模型的汽车总数：

```java
long total = stream()
  .where(c -> c.getModel().equals(model))
  .count()
```

最终的 SQL按预期使用了count SQL 方法：

```plaintext
select count(c.model) from car c where c.model=?
```

Jinq 还提供聚合方法，如sum、average、min、max以及组合不同聚合的可能性。

### 5.5. 分页

如果我们想批量读取数据，我们可以使用limit和skip方法。

让我们看一个例子，我们想跳过前 10 辆车，只得到 20 件物品：

```java
stream()
  .skip(10)
  .limit(20)
  .toList()
```

生成的 SQL 是：

```java
select c. from car c limit ? offset ?
```

## 六. 总结

我们开始了。在本文中，我们看到了一种使用 Hibernate(最低限度)设置带有 Jinq 的 Spring 应用程序的方法。

我们还简要探讨了 Jinq 的优势及其一些主要功能。