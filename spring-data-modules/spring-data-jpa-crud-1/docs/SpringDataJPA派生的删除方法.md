## 1. 概述

Spring Data JPA允许我们定义从数据库读取、更新或删除记录的[派生方法](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)。这非常有用，因为它减少了数据访问层的样板代码。

在本教程中，我们将重点介绍如何**通过实际代码示例定义和使用Spring Data派生的删除方法**。

## 2. 派生deleteBy方法

首先，我们将定义一个Fruit实体来保存水果店中可用商品的名称和颜色：

```java
@Entity
@Setter
@Getter
public class Fruit {

    @Id
    private long id;
    private String name;
    private String color;
}
```

接下来，我们将通过扩展JpaRepository接口并将我们的派生方法添加到这个接口中，以便对Fruit实体进行操作。

**派生方法可以定义为实体中定义的动词+属性**。一些允许的动词是findBy、deleteBy和removeBy。

让我们编写一个按名称删除水果的方法：

```java
@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    Long deleteByName(String name);
}
```

在此示例中，deleteByName()方法返回已删除记录的计数。

同样，我们也可以派生出如下形式的delete方法：

```java
@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    List<Fruit> deleteByColor(String color);
}
```

在这里，deleteByColor()方法删除具有给定颜色的所有水果，并返回已删除记录的列表。

**让我们测试派生的删除方法**。首先，我们将通过在test-fruit-data.sql中定义一些初始数据，在Fruit实体表中插入一些记录。

```h2
truncate table fruit;

insert into fruit(id, name, color)
values (1, 'apple', 'red');
insert into fruit(id, name, color)
values (2, 'custard apple', 'green');
insert into fruit(id, name, color)
values (3, 'mango', 'yellow');
insert into fruit(id, name, color)
values (4, 'guava', 'green');
```

然后，我们将删除所有“绿色”水果：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
class FruitRepositoryIntegrationTest {

    @Autowired
    private FruitRepository fruitRepository;

    @Test
    @Transactional
    @Sql(scripts = "/test-fruit-data.sql")
    void givenFruits_whenDeletedByColor_thenDeletedFruitsShouldReturn() {
        List<Fruit> fruits = fruitRepository.deleteByColor("green");

        assertEquals(2, fruits.size(), "number of fruits are not matching");
        fruits.forEach(fruit -> assertEquals("green", fruit.getColor(), "It's not a green fruit"));
    }
}
```

**另外请注意，我们需要为删除方法使用@Transactional注解**。

接下来，让我们为第二个deleteBy方法添加一个类似的测试用例：

```java
@Test
@Transactional
@Sql(scripts = "/test-fruit-data.sql")
void givenFruits_whenDeletedByName_thenDeletedFruitCountShouldReturn() {
    Long deletedFruitCount = fruitRepository.deleteByName("apple");

    assertEquals(1, deletedFruitCount.intValue(), "deleted fruit count is not matching");
}
```

## 3. 派生removeBy方法

**我们还可以使用removeBy动词来派生删除方法**：

```java
@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    Long removeByName(String name);

    List<Fruit> removeByColor(String color);
}
```

**请注意，这两种方法的行为没有区别**。

最终接口将如下所示:

```java
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    Long deleteByName(String name);

    List<Fruit> deleteByColor(String color);

    Long removeByName(String name);

    List<Fruit> removeByColor(String color);
}
```

让我们为removeBy方法添加类似的单元测试：

```java
@Test
@Transactional
@Sql(scripts = "/test-fruit-data.sql")
void givenFruits_whenRemovedByColor_thenDeletedFruitsShouldReturn() {
    List<Fruit> fruits = fruitRepository.removeByColor("green");

    assertEquals(2, fruits.size(), "number of fruits are not matching");
    fruits.forEach(fruit -> assertEquals("green", fruit.getColor(), "It's not a green fruit"));
}
```

```java
@Test
@Transactional
@Sql(scripts = "/test-fruit-data.sql")
void givenFruits_whenRemovedByName_thenDeletedFruitCountsShouldReturn() {
    Long deletedFruitCount = fruitRepository.removeByName("apple");

    assertEquals(1, deletedFruitCount.intValue(), "deleted fruit count is not matching");
}
```

## 4. 派生删除方法与@Query注解

我们可能会遇到这样一种情况-即派生方法的名称太长，或涉及不相关实体之间的多表连接SQL。

在这种情况下，我们还可以使用@Query和[@Modifying](https://www.baeldung.com/spring-data-jpa-modifying-annotation)注解来实现删除操作。

让我们使用自定义查询定义派生delete方法的等效代码：

```java
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    @Transactional
    @Modifying
    @Query("delete from Fruit f where f.name = :name or f.color = :color")
    int deleteFruits(@Param("name") String name, @Param("color") String color);
}
```

虽然这两种解决方案看起来很相似，并且它们确实实现了相同的结果，但它们采用的方法略有不同。**@Query注解方法针对数据库创建单个JPQL查询。相比之下，deleteBy方法执行读取查询，然后逐个删除每一项**。

此外，deleteBy方法可以返回已删除记录的列表，而自定义查询返回的是已删除记录的数量。

## 5. 总结

在本文中，我们重点介绍了Spring Data派生的删除方法。