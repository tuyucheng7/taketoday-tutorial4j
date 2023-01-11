## 1. 概述

Spring Data JPA允许我们定义从数据库读取，更新或删除记录的派生方法。这非常有用，因为它减少了数据访问层的样板代码。

在本教程中，我们将重点介绍如何**通过实际代码示例定义和使用Spring Data派生的删除方法**。

## 2. deleteBy方法

首先，我们定义一个Fruit实体来保存水果的名称和颜色：

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

接下来，我们通过继承JpaRepository接口并将派生方法添加到这个接口中，以便对Fruit实体进行操作。

**派生方法可以定义为实体中定义的动词+属性**，允许使用的动词有findBy，deleteBy和removeBy。

下面是根据水果名称删除Fruit实体的方法：

```java
@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    Long deleteByName(String name);
}
```

在本例中，deleteByName()方法返回已删除的总记录条数。

类似地，我们还可以派生以下形式的删除方法：

```java
@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    List<Fruit> deleteByColor(String color);
}
```

这里，deleteByColor()方法删除具有给定颜色的所有水果，并返回已删除记录的集合。

让我们测试一下这些删除方法。首先，通过在test-fruit-data.sql中定义一些初始数据，我们可以在Fruit实体表中插入一些记录。

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

然后，我们编写一个测试删除所有颜色为绿色的水果：

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

**另外，请注意，对于删除方法，我们需要使用@Transactional注解**。

接下来，让我们为第二个delete方法添加一个类似的测试用例：

```java
class FruitRepositoryIntegrationTest {

    @Test
    @Transactional
    @Sql(scripts = "/test-fruit-data.sql")
    void givenFruits_whenDeletedByName_thenDeletedFruitCountShouldReturn() {
        Long deletedFruitCount = fruitRepository.deleteByName("apple");

        assertEquals(1, deletedFruitCount.intValue(), "deleted fruit count is not matching");
    }
}
```

## 3. removeBy方法

**我们还可以使用removeBy动词派生删除方法**：

```java
@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    Long removeByName(String name);

    List<Fruit> removeByColor(String color);
}
```

**注意，这两种方法的行为没有区别。**

此时我们FruitRepository接口包含如下几个方法:

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
class FruitRepositoryIntegrationTest {

    @Test
    @Transactional
    @Sql(scripts = "/test-fruit-data.sql")
    void givenFruits_whenRemovedByColor_thenDeletedFruitsShouldReturn() {
        List<Fruit> fruits = fruitRepository.removeByColor("green");

        assertEquals(2, fruits.size(), "number of fruits are not matching");
        fruits.forEach(fruit -> assertEquals("green", fruit.getColor(), "It's not a green fruit"));
    }

    @Test
    @Transactional
    @Sql(scripts = "/test-fruit-data.sql")
    void givenFruits_whenRemovedByName_thenDeletedFruitCountsShouldReturn() {
        Long deletedFruitCount = fruitRepository.removeByName("apple");

        assertEquals(1, deletedFruitCount.intValue(), "deleted fruit count is not matching");
    }
}
```

## 4. 派生的删除方法与@Query注解

我们可能会遇到这样一种情况，即派生方法的名称太长，或者涉及到不相关实体之间的多表连接SQL。

在这种情况下，我们还可以使用@Query和@Modifying注解来实现删除操作。

让我们使用自定义查询定义派生的delete方法的等效代码：

```java
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    @Transactional
    @Modifying
    @Query("delete from Fruit f where f.name = :name or f.color = :color")
    int deleteFruits(@Param("name") String name, @Param("color") String color);
}
```

虽然这两种解决方案看起来很相似，并且确实达到了相同的效果，但它们采取的方法略有不同。**@Query注解方法针对数据库创建单个JPQL查询。相比之下，deleteBy方法执行一个读取查询，然后逐个删除每一项**。

此外，deleteBy方法可以返回已删除记录的集合，而自定义查询返回的是已删除记录的总数量。

## 5. 总结

在本文中，我们重点介绍了Spring Data派生的删除方法。