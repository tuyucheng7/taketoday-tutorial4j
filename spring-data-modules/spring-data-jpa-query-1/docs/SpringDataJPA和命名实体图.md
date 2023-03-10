## 1. 概述

**简而言之，[实体图](https://www.baeldung.com/jpa-entity-graph)是在JPA 2.1中描述查询的另一种方式**。我们可以使用它们来制定性能更好的查询。

在本教程中，我们将通过一个简单的示例来学习如何使用[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)实现实体图。

## 2. 实体

首先，让我们创建一个名为Item的模型，它具有多个Characteristic：

```java
@Entity
public class Item {

    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "item")
    private List<Characteristic> characteristics = new ArrayList<>();

    // getters and setters
}
```

现在让我们定义Characteristic实体：

```java
@Entity
public class Characteristic {

    @Id
    private Long id;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Item item;

    //Getters and Setters
}
```

正如我们在代码中看到的，Item实体中的characteristics字段和Characteristic实体中的item字段都是使用fetch参数延迟加载的。因此，**我们的目标是在运行时急切地加载它们**。

## 3. 实体图

在Spring Data JPA中，我们可以使用**@NamedEntityGraph和@EntityGraph注解的组合**来定义实体图。或者，我们也可以仅使用**@EntityGraph注解的attributePaths参数**来定义临时实体图。

让我们看看如何做到这一点。

### 3.1 @NamedEntityGraph注解

首先，我们可以直接在Item实体上使用JPA的@NamedEntityGraph注解：

```java
@Entity
@NamedEntityGraph(
      name = "Item.characteristics",
      attributeNodes = @NamedAttributeNode("characteristics")
)
public class Item {
    // ...
}
```

然后，我们可以将@EntityGraph注解添加到我们的Repository方法上：

```java
public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(value = "Item.characteristics", type = EntityGraphType.FETCH)
    Item findByName(String name);
}
```

如代码所示，我们已将之前在Item实体上创建的实体图的名称"Item.characteristics"传递给@EntityGraph注解。当我们调用该方法时，这就是Spring Data将使用的查询。

**@EntityGraph注解的type参数的默认值为EntityGraphType.FETCH**。当我们使用它时，Spring Data模块将在指定的属性节点上应用FetchType.EAGER策略。对于其他属性，将应用FetchType.LAZY策略。

因此在我们的例子中，即使@OneToMany注解的默认获取策略是惰性的，characteristics属性也会被急切地加载。

这里的一个问题是，**如果定义的获取策略是EAGER，那么我们不能将其行为更改为LAZY**。这是经过设计的，因为后续操作可能需要在执行过程中稍后时间点急切获取的数据。

### 3.2 不使用@NamedEntityGraph

或者，**我们也可以使用attributePaths定义一个ad-hoc(临时)实体图**。

让我们将一个临时实体图添加到我们的CharacteristicRepository中，它急切地加载其Item父级：

```java
public interface CharacteristicsRepository extends JpaRepository<Characteristic, Long> {

    @EntityGraph(attributePaths = {"item"})
    Characteristic findByType(String type);
}
```

这将急切地加载Characteristic实体的item属性，**即使我们的实体为此属性声明了延迟加载策略**。

这很方便，因为我们可以内联定义实体图，而不是引用现有的命名实体图。

## 4. 测试用例

现在我们已经定义了实体图，让我们创建一个测试用例来验证它：

```java
@DataJpaTest(showSql = false)
@ExtendWith(SpringExtension.class)
@Sql(scripts = "/entitygraph-data.sql")
class EntityGraphIntegrationTest {

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private CharacteristicsRepository characteristicsRepo;

    @Test
    void givenEntityGraph_whenCalled_shouldReturnDefinedFields() {
        Item item = itemRepo.findByName("Table");
        assertThat(item.getId()).isEqualTo(1L);
    }

    @Test
    void givenAdhocEntityGraph_whenCalled_shouldReturnDefinedFields() {
        Characteristic characteristic = characteristicsRepo.findByType("Rigid");
        assertThat(characteristic.getId()).isEqualTo(1L);
    }
}
```

上述测试类使用到的entitygraph-data.sql脚本文件为：

```sql
INSERT INTO Item(id, name)
VALUES (1, 'Table');
INSERT INTO Item(id, name)
VALUES (2, 'Bottle');

INSERT INTO Characteristic(id, item_id, type)
VALUES (1, 1, 'Rigid');
INSERT INTO Characteristic(id, item_id, type)
VALUES (2, 1, 'Big');
INSERT INTO Characteristic(id, item_id, type)
VALUES (3, 2, 'Fragile');
INSERT INTO Characteristic(id, item_id, type)
VALUES (4, 2, 'Small');
```

第一个测试将使用@NamedEntityGraph注解定义的实体图。

让我们看看Hibernate生成的SQL语：

```sql
select item0_.id            as id1_4_0_,
       characteri1_.id      as id1_1_1_,
       item0_.name          as name2_4_0_,
       characteri1_.item_id as item_id3_1_1_,
       characteri1_.type    as type2_1_1_,
       characteri1_.item_id as item_id3_1_0__,
       characteri1_.id      as id1_1_0__
from item item0_
         left outer join characteristic characteri1_ on item0_.id = characteri1_.item_id
where item0_.name = ?
```

为了进行比较，让我们从Repository中删除@EntityGraph注解并检查SQL：

```sql
select item0_.id as id1_4_, item0_.name as name2_4_
from item item0_
where item0_.name = ?
```

从这些查询中，我们可以清楚地观察到不带@EntityGraph注解生成的查询**没有加载Characteristic实体的任何属性**。因此，它只加载Item实体。

最后，让我们将第二个测试的Hibernate查询与@EntityGraph注解进行比较：

```sql
select characteri0_.id      as id1_1_0_,
       item1_.id            as id1_4_1_,
       characteri0_.item_id as item_id3_1_0_,
       characteri0_.type    as type2_1_0_,
       item1_.name          as name2_4_1_
from characteristic characteri0_
         left outer join item item1_ on characteri0_.item_id = item1_.id
where characteri0_.type = ?
```

以及没有@EntityGraph注解的查询：

```sql
select characteri0_.id      as id1_1_,
       characteri0_.item_id as item_id3_1_,
       characteri0_.type    as type2_1_
from characteristic characteri0_
where characteri0_.type = ?
```

## 5. 总结

在本教程中，我们学习了如何在Spring Data中使用JPA实体图。**使用Spring Data，我们可以创建多个链接到不同实体图的Repository方法**。