## 1. 简介

在为真实世界的系统或流程建模时，领域驱动设计(DDD)风格的Repository是一个不错的选择。为此，我们可以使用Spring Data JPA作为我们的数据访问抽象层。

如果你不熟悉此概念，请查看此[介绍性教程](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)以帮助你快速上手。

在本教程中，我们将重点介绍创建自定义和可组合Repository的概念，这些Repository是使用称为片段的较小Repository创建的。

## 2. Maven依赖

创建可组合Repository的方法从Spring 5开始可用。

让我们为Spring Data JPA添加所需的依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jpa</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

我们还需要设置一个数据源，以便我们能够通过数据访问层与DB交互。像[H2](https://www.baeldung.com/spring-testing-separate-data-source)这样的内存数据库用于开发和快速测试是个好方法。

## 3. 背景

### 3.1 Hibernate作为JPA实现

默认情况下，Spring Data JPA使用Hibernate作为JPA实现。我们很容易将一个与另一个混淆或将它们进行比较，但它们有不同的用途。

Spring Data JPA是数据访问抽象层，我们可以在其下使用任何实现。例如，我们可以[将Hibernate切换为EclipseLink](https://www.baeldung.com/spring-eclipselink)。

### 3.2 默认Repository

在很多情况下，我们不需要自己编写任何查询。

相反，我们只需要创建接口，这些接口扩展了通用的Spring Data Repository接口：

```java
public interface LocationRepository extends JpaRepository<Location, Long> {
}
```

这本身就允许我们在具有Long类型主键的Location对象上执行一些常见操作，比如CRUD、分页和排序。

此外，Spring Data JPA配备了一个查询构建器机制，它提供了使用方法名称约定代表我们生成查询的能力：

```java
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findStoreByLocationId(Long locationId);
}
```

### 3.3 自定义Repository

如果需要，我们可以通过编写片段接口和实现所需的功能来**加强我们的模型Repository**。然后可以将其注入到我们自己的JPA Repository中。

例如，这里我们通过扩展片段Repository来加强我们的ItemTypeRepository：

```java
public interface ItemTypeRepository extends JpaRepository<ItemType, Long>, CustomItemTypeRepository {
}
```

这里CustomItemTypeRepository是另一个接口：

```java
public interface CustomItemTypeRepository {
    void deleteCustomById(ItemType entity);
}
```

它的实现可以是任何类型的Repository，而不仅仅是JPA：

```java
public class CustomItemTypeRepositoryImpl implements CustomItemTypeRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void deleteCustomById(ItemType itemType) {
        entityManager.remove(itemType);
    }
}
```

我们只需要确保实现类以后缀Impl结尾。但是，我们可以使用以下XML配置设置自定义后缀：

```xml
<repositories base-package="cn.tuyucheng.taketoday.repository" repository-impl-postfix="CustomImpl" />
```

或者通过使用这个注解：

```java
@EnableJpaRepositories(
    basePackages = "cn.tuyucheng.taketoday.repository", 
    repositoryImplementationPostfix = "CustomImpl"
)
```

## 4. 使用多个片段组成Repository

直到几个版本之前，我们只能使用单个自定义实现来扩展我们的Repository接口。这是一个限制，因为我们必须将所有相关的功能整合到一个对象中。

不用说，对于具有复杂域模型的大型项目，这会导致类膨胀。

现在使用Spring 5，**我们可以选择使用多个片段Repository来加强我们的JPA Repository**。同样，要求仍然是我们将这些片段作为接口实现对。

为了证明这一点，让我们创建两个片段接口：

```java
public interface CustomItemTypeRepository {
    void deleteCustom(ItemType entity);

    void findThenDelete(Long id);
}

public interface CustomItemRepository {
    Item findItemById(Long id);

    void deleteCustom(Item entity);

    void findThenDelete(Long id);
}
```

当然，我们需要编写它们的实现。但是，我们可以扩展单个JPA Repository的功能，而不是将这些具有相关功能的自定义Repository插入到它们自己的JPA Repository中：

```java
public interface ItemTypeRepository extends JpaRepository<ItemType, Long>, CustomItemTypeRepository, CustomItemRepository {
}
```

现在，我们会将所有链接的功能都放在一个Repository中。

## 5. 处理歧义

由于我们是从多个Repository继承的，因此我们可能无法确定在发生冲突时将使用哪个实现。例如，在我们的示例中，两个片段Repository接口都有一个findThenDelete()方法，具有相同的签名。

在这种情况下，**接口声明的顺序用于解决歧义**。因此，在我们的例子中将使用CustomItemTypeRepository中的方法，因为它是首先声明的。

我们可以使用此测试用例对此进行测试：

```java
@ExtendWith(SpringExtension.class)
@DataJpaTest(properties = "spring.sql.init.data-locations=classpath:import_entities.sql", showSql = false)
class JpaRepositoriesIntegrationTest {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ItemTypeRepository compositeRepository;
    @Autowired
    private ReadOnlyLocationRepository readOnlyRepository;

    @Test
    void givenItemAndItemType_WhenAmbiguousDeleteCalled_ThenItemTypeDeletedAndNotItem() {
        Optional<ItemType> itemType = compositeRepository.findById(1L);
        assertTrue(itemType.isPresent());

        Item item = compositeRepository.findItemById(2L);
        assertNotNull(item);

        compositeRepository.findThenDelete(1L);
        Optional<ItemType> sameItemType = compositeRepository.findById(1L);
        assertFalse(sameItemType.isPresent());

        Item sameItem = compositeRepository.findItemById(2L);
        assertNotNull(sameItem);
    }
}
```

## 6. 总结

在本文中，我们介绍了使用Spring Data JPA Repository的不同方式。Spring使得在我们的域对象上执行数据库操作变得简单，而无需编写太多代码甚至SQL查询。

通过使用可组合的Repository，这种支持在很大程度上是可定制的。