## 1. 简介

[Jinq](http://www.jinq.org/)为在Java中查询数据库提供了一种直观且方便的方法。在本教程中，我们将探讨**如何配置Spring项目以使用Jinq**及其一些通过简单示例说明的功能。

## 2. Maven依赖

我们需要在pom.xml文件中添加[Jinq依赖项](https://central.sonatype.com/artifact/org.jinq/jinq-jpa/2.0.0)：

```xml
<dependency>
    <groupId>org.jinq</groupId>
    <artifactId>jinq-jpa</artifactId>
    <version>1.8.22</version>
</dependency>
```

对于Spring，我们将在pom.xml文件中添加[Spring ORM依赖项](https://central.sonatype.com/artifact/org.springframework/spring-orm/6.0.5)：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>5.3.3</version>
</dependency>
```

最后，为了测试，我们将使用H2内存数据库，因此我们也将此[依赖项](https://central.sonatype.com/artifact/com.h2database/h2/2.1.212)与spring-boot-starter-data-jpa一起添加到pom.xml文件中：

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

Jinq通过公开内部基于[Java Stream API](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html)的流式API帮助我们编写更简单、更易读的数据库查询。

让我们看一个按型号过滤汽车的例子：

```java
jinqDataProvider.streamAll(entityManager, Car.class)
    .where(c -> c.getModel().equals(model))
    .toList();
```

**Jinq以高效的方式将上述代码片段转换为SQL查询**，因此本例中的最终查询将是：

```sql
select c.* from car c where c.model=?
```

**由于我们不使用纯文本来编写查询，而是使用类型安全的API，因此这种方法不太容易出错**。

此外，Jinq旨在通过使用通用、易于阅读的表达式来加快开发速度。

然而，它在我们可以使用的类型和操作的数量上有一些限制，我们将在接下来看到。

### 3.1 限制

**Jinq仅支持JPA中的基本类型和SQL函数的具体列表**。它的工作原理是将所有对象和方法映射到JPA数据类型和SQL函数，将lambda操作转换为原生SQL查询。

因此，我们不能期望该工具可以转换每个自定义类型或某个类型的所有方法。

### 3.2 支持的数据类型

让我们看看支持的数据类型和方法：

-   String：仅equals()、compareTo()方法
-   原始数据类型：算术运算
-   枚举和自定义类：仅支持==和!=操作
-   java.util.Collection：contains()
-   Date API：仅限equals()、before()、after()方法

**注意：如果我们想要自定义从Java对象到数据库对象的转换，我们需要在Jinq中注册我们的AttributeConverter的具体实现**。

## 4. Jinq与Spring集成

**Jinq需要一个[EntityManager](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html)实例来获取持久化上下文**。在本教程中，我们将介绍一种使用Spring的简单方法，使Jinq与[Hibernate](http://hibernate.org/)提供的EntityManager一起工作。

### 4.1 Repository接口

**Spring使用Repository的概念来管理实体**。让我们看看我们的CarRepository接口，其中我们有一个方法来检索给定模型的Car：

```java
public interface CarRepository {
    Optional<Car> findByModel(String model);
}
```

### 4.2 抽象Repository

接下来，**我们需要一个基础Repository**来提供所有Jinq功能：

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

### 4.3 实现Repository

**现在，我们对Jinq只需要一个EntityManager实例和实体类型类**。

让我们看看使用我们刚刚定义的Jinq基础Repository的CarRepository实现：

```java
@Repository
public class CarRepositoryImpl extends BaseJinqRepositoryImpl<Car> implements CarRepository {

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

### 4.4 注入Jinq JPAStreamProvider

为了注入Jinq JPAStreamProvider实例，我们将**添加Jinq提供者配置**：

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

### 4.5 配置Spring应用程序

最后一步是**使用Hibernate和我们的Jinq配置来配置我们的Spring应用程序**。作为参考，请参阅我们的application.properties文件，其中我们使用内存中的H2实例作为数据库：

```properties
spring.datasource.url=jdbc:h2:~/jinq
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
```

## 5. 查询指南

**Jinq提供了许多直观的选项来使用select、where、join等自定义最终的SQL查询**。请注意，这些具有与我们上面已经介绍的相同的限制。

### 5.1 where

where子句允许将多个过滤器应用于数据集合。

在下一个示例中，我们要按型号和描述过滤汽车：

```java
stream()
    .where(c -> c.getModel().equals(model) && c.getDescription().contains(desc))
    .toList();
```

这是Jinq翻译的SQL：

```sql
select c.model, c.description from car c where c.model=? and locate(?, c.description)>0
```

### 5.2 select

如果我们只想从数据库中检索几个列/字段，我们需要使用select子句。

为了映射多个值，Jinq提供了多个Tuple类，最多有八个值：

```java
stream()
    .select(c -> new Tuple3<>(c.getModel(), c.getYear(), c.getEngine()))
    .toList()
```

以及翻译后的SQL：

```sql
select c.model, c.year, c.engine from car c
```

### 5.3 join

**如果实体正确链接，Jinq能够解决一对一和多对一的关系**。

例如，如果我们在Car中添加Manufacturer实体：

```java
@Entity(name = "CAR")
public class Car {
    // ...
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

我们现在能够获取给定模型的制造商：

```java
Optional<Manufacturer> manufacturer = stream()
    .where(c -> c.getModel().equals(model))
    .select(c -> c.getManufacturer())
    .findFirst();
```

正如预期的那样，**Jinq将在这种情况下使用内连接SQL子句**：

```sql
select m.name, m.city from car c inner join manufacturer m on c.name=m.name where c.model=?
```

**如果我们需要对join子句有更多的控制，以便在实体上实现更复杂的关系，比如多对多关系，我们可以使用join方法**：

```java
List<Pair<Manufacturer, Car>> list = streamOf(Manufacturer.class)
    .join(m -> JinqStream.from(m.getCars()))
    .toList()
```

最后，我们可以通过使用leftOuterJoin方法而不是join方法来使用左外连接SQL子句。

### 5.4 聚合

到目前为止我们介绍的所有示例都使用toList或findFirst方法-返回我们在Jinq中查询的最终结果。

除了这些方法，**我们还可以使用其他方法来聚合结果**。

例如，让我们使用count方法获取数据库中具体模型的汽车总数：

```java
long total = stream()
    .where(c -> c.getModel().equals(model))
    .count()
```

最终的SQL按预期使用了count SQL方法：

```sql
select count(c.model) from car c where c.model=?
```

Jinq还提供聚合方法，如sum、average、min、max以及**组合不同聚合的可能性**。

### 5.5 分页

如果我们想批量读取数据，我们可以使用limit和skip方法。

让我们看一个例子，我们想跳过前10辆车，只得到20个元素：

```java
stream()
    .skip(10)
    .limit(20)
    .toList()
```

生成的SQL是：

```sql
select c.* from car c limit ? offset ?
```

## 6. 总结

在本文中，我们看到了一种使用Hibernate(最低限度)设置带有Jinq的Spring应用程序的方法。

我们还简要探讨了Jinq的优势及其一些主要功能。