## 1. 概述

**CrudRepository是一个Spring Data接口，用于对特定类型的Repository进行通用CRUD操作**。它提供了几种开箱即用的方法来与数据库交互。

在本教程中，我们将解释如何以及何时使用CrudRepository save()方法。

要了解有关Spring Data Repository的更多信息，请查看我们将CrudRepository与框架的其他Repository接口进行比较的[文章](https://www.baeldung.com/spring-data-repositories)。

## 延伸阅读

### [Spring Data JPA @Query](https://www.baeldung.com/spring-data-jpa-query)

了解如何使用Spring Data JPA中的@Query注解来使用JPQL和原生SQL定义自定义查询。

[阅读更多](https://www.baeldung.com/spring-data-jpa-query)→

### [Spring Data JPA-派生的删除方法](https://www.baeldung.com/spring-data-jpa-deleteby)

了解如何定义Spring Data deleteBy和removeBy方法

[阅读更多](https://www.baeldung.com/spring-data-jpa-deleteby)→

## 2. Maven依赖

我们必须将[Spring Data](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)和[H2](https://mvnrepository.com/artifact/com.h2database/h2)数据库依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 3. 应用示例

首先，让我们创建名为MerchandiseEntity的Spring Data实体。此类将**定义在我们调用save()方法时将持久保存到数据库的数据类型**：

```java
@Entity
public class MerchandiseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double price;

    private String brand;

    public MerchandiseEntity() {
    }

    public MerchandiseEntity(String brand, double price) {
        this.brand = brand;
        this.price = price;
    }
}
```

接下来，让我们创建一个扩展CrudRepository的接口来处理MerchandiseEntity：

```java
public interface InventoryRepository extends CrudRepository<MerchandiseEntity, Long> {
}
```

**这里我们的泛型指定实体类为MerchandiseEntity，以及实体类的id类型为Long**。当这个Repository的一个实例被实例化时，底层逻辑将自动应用以使用我们的MerchandiseEntity类。

因此，只需很少的代码，我们已经能够开始使用save()方法。

## 4. CrudRepository的save()方法保存实例

让我们创建MerchandiseEntity的一个新实例，并使用InventoryRepository将其保存到数据库中：

```java
InventoryRepository repo = context.getBean(InventoryRepository.class);
MerchandiseEntity pants = new MerchandiseEntity("Pair of Pants", BigDecimal.ONE);
pants = repo.save(pants);
```

运行此命令将在MerchandiseEntity的数据库表中添加一条新记录。请注意，我们从未指定id。该实例最初创建时其id为null值，当我们调用save()方法时，会自动生成一个id。

save()方法返回保存的实体，包括生成的id字段。

## 5. CrudRepository save()方法更新实例

我们可以使用相同的save()方法来**更新数据库中的现有记录**。假设我们保存了一个具有特定标题的Merchandising实例：

```java
MerchandiseEntity pants = new MerchandiseEntity("Pair of Pants", 34.99);
pants = repo.save(pants);
```

后来假设我们要更新商品的价格。然后我们可以简单地从数据库中获取实体，进行更改，然后像以前一样使用save()方法。

假设我们知道元素的id(pantsId)，我们可以使用CrudRepository的findById()方法从数据库中获取我们的实体：

```java
MerchandiseEntity pantsInDB = repo.findById(pantsId).get();
pantsInDB.setPrice(44.99);
repo.save(pantsInDB);
```

在这里，我们用新价格更新了原始实体并将更改保存回数据库。

需要记住的是，**调用save()来更新[事务方法](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)中的对象不是强制性的**。

**当我们使用findById()在事务方法中检索实体时，返回的实体由持久性提供程序[管理](https://www.baeldung.com/hibernate-entity-lifecycle#managed-entity)**。因此，无论我们是否调用save()方法，对该实体的任何更改都将自动保存在数据库中。现在，让我们创建一个简单的测试用例来确认这一点：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class InventoryRepositoryIntegrationTest {
    private static final String ORIGINAL_TITLE = "Pair of Pants";
    private static final String UPDATED_TITLE = "Branded Luxury Pants";
    private static final String UPDATED_BRAND = "Armani";
    private static final String ORIGINAL_SHORTS_TITLE = "Pair of Shorts";

    @Autowired
    private InventoryRepository repository;

    @Test
    @Transactional
    void shouldUpdateExistingEntryInDBWithoutSave() {
        MerchandiseEntity pants = new MerchandiseEntity(ORIGINAL_TITLE, BigDecimal.ONE);
        pants = repository.save(pants);

        Long originalId = pants.getId();

        pants.setTitle(UPDATED_TITLE);
        pants.setPrice(BigDecimal.TEN);
        pants.setBrand(UPDATED_BRAND);

        Optional<MerchandiseEntity> resultOp = repository.findById(originalId);

        assertTrue(resultOp.isPresent());
        MerchandiseEntity result = resultOp.get();

        assertEquals(originalId, result.getId());
        assertEquals(UPDATED_TITLE, result.getTitle());
        assertEquals(BigDecimal.TEN, result.getPrice());
        assertEquals(UPDATED_BRAND, result.getBrand());
    }
}
```

## 6. 总结

在这篇简短的文章中，我们介绍了CrudRepository的save()方法的使用。我们可以使用此方法将新记录添加到我们的数据库中，或者可以更新现有记录。