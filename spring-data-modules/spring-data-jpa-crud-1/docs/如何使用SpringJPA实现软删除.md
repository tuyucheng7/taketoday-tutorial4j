## 1. 概述

在与数据库交互时，从表中物理删除数据是一项常见要求。但有时会有业务需求，不能永久删除数据库中的数据。例如，需要进行数据历史跟踪或审计，并且还涉及引用完整性。

我们可以隐藏这些数据，使其无法从应用程序前端访问，而不是物理删除数据。

**在本教程中，我们将了解软删除以及如何使用[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)实现此技术**。

## 2. 什么是软删除

**软删除执行update操作以将某些数据标记为已删除，而不是从数据库中的表中物理删除**。实现软删除的一种常见方法是添加一个字段，该字段用于指示数据是否已被删除。

例如，假设我们有一个具有以下结构的table_product表：

| Column  |     Type      | Default Value |  Nullable  |
|:-------:|:-------------:|:-------------:|:----------:|
|   id    |  bigint(20)   |               |     NO     |
|  name   | varchar(255)  |               |    YES     |
|  price  |    double     |               |     NO     |

现在让我们看一下从表中物理删除记录时将运行的SQL命令：

```sql
delete
from table_product
where id = 1
```

此SQL语句将从数据库的表中永久删除id=1的产品。

现在让我们实现上面描述的软删除机制：

| Column  |     Type     | Default Value | Nullable |
|:-------:|:------------:|:-------------:|:--------:|
|   id    |  bigint(20)  |               |    NO    |
|  name   | varchar(255) |               |   YES    |
|  price  |    double    |               |    NO    |
| deleted |    bit(1)    |               |    NO    |

注意，我们添加了一个名为deleted的新字段，该字段的值为0或1。

1表示数据已被删除，0表示数据未被删除。我们应该将0设置为默认值，对于每个数据删除过程，我们不执行SQL delete语句，而是执行以下SQL update语句：

```sql
update from table_product
set deleted=1
where id = 1
```

使用这个SQL语句，我们实际上并没有删除该行，而只是将其标记为已删除。因此，当我们要执行读取查询时，我们读取的是那些没有被标记为已删除的行，我们只需在SQL查询中添加一个where条件：

```sql
select *
from table_product
where deleted = 0
```

## 3. Spring Data JPA如何实现软删除

使用Spring Data JPA，软删除的实现变得更加容易。为此，我们只需要几个JPA注解。

正如我们所知，我们通常只在JPA中使用一些SQL命令。它将在幕后创建和执行大部分SQL查询。

现在让我们使用与上面相同的表结构在Spring Data JPA中实现软删除。

### 3.1 实体类

最重要的部分是创建实体类。

让我们创建一个Product实体类：

```java
@Entity
@Table(name = "table_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    private boolean deleted = Boolean.FALSE;

    // setter getter methods
}
```

如我们所见，我们添加了一个deleted属性，默认值设置为FALSE。

下一步需要覆盖JPA Repository中的delete命令：

默认情况下，JPA Repository中的delete命令将运行delete语句，因此让我们首先向我们的实体类添加一些注解：

```java
@Entity
@Table(name = "table_product")
@SQLDelete(sql = "UPDATE table_product SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    private boolean deleted = Boolean.FALSE;

    // setter getter method
}
```

**我们使用@SQLDelete注解来重写delete命令**。每次我们执行delete语句时，**我们实际上都将其转换为update语句，将deleted的字段值更改为true**，而不是永久删除数据。

**另一方面，@Where注解会在我们读取Product数据时添加一个where子句**。因此，根据上面的代码示例，deleted=true的Product数据不会包含在查询结果集中。

### 3.2 Repository

Repository接口没有什么特别的变化：

```java
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
}
```

### 3.3 Service

对于服务类也同样没有什么特别的。我们可以从所需的Repository中调用函数。

在此示例中，让我们调用三个Repository函数来创建一条记录，然后执行软删除：

```java
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public void remove(Long id) {
        productRepository.deleteById(id);
    }

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }
}
```

## 4. 如何获取已删除的数据

通过使用@Where注解，我们无法获取已删除的产品数据。为了防止我们仍然希望可以访问已删除的数据，例如管理员级别的用户具有完全访问权限，可以查看已删除的数据。

**为了实现这一点，我们不应该使用@Where注解，而应该使用两个不同的注解@FilterDef和@Filter**。通过这些注解，我们可以根据需要动态添加条件：

```java
@Entity
@Table(name = "tbl_products")
@SQLDelete(sql = "UPDATE tbl_products SET deleted = true WHERE id=?")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedProductFilter", condition = "deleted = :isDeleted")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    private boolean deleted = Boolean.FALSE;
}
```

这里@FilterDef注解定义了@Filter注解将使用的基本要求。此外，我们还需要更改ProductService类中的findAll()方法来处理动态参数或过滤器：

```java
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public void remove(Long id){
        productRepository.deleteById(id);
    }

    public Iterable<Product> findAll(boolean isDeleted){
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", isDeleted);
        Iterable<Product> products =  productRepository.findAll();
        session.disableFilter("deletedProductFilter");
        return products;
    }
}
```

在这里，我们添加了isDeleted参数，该参数将添加到影响读取Product实体过程的对象Filter中。

## 5. 总结

使用Spring Data JPA很容易实现软删除技术。我们需要做的是定义一个字段，用来标识记录是否被删除。然后，我们必须在特定实体类上使用@SQLDelete注解重写delete命令。

如果我们想要更多的控制，我们可以使用@FilterDef和@Filter注解，这样我们就可以指定查询结果集是否应该包含已删除的数据。