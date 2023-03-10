## 1. 简介

在本教程中，我们将研究在Spring Data JPA中使用派生查询API时findBy和findAllBy方法命名约定之间的差异。

## 2. JPA中的派生查询是什么

Spring Data JPA支持基于方法名称的[派生查询](https://www.baeldung.com/spring-data-derived-queries)。这意味着如果我们在方法名称中使用特定关键字，则无需手动指定查询。

find和By关键字一起工作以生成一个查询，该查询使用规则搜索结果集合。请注意，**这两个关键字以集合的形式返回所有结果**，这可能会在findAllBy的使用中造成混淆。[Spring Data文档](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords)中没有定义All关键字。

在以下部分中，我们将验证Spring Data JPA中的findBy和findAllBy关键字之间没有区别，并提供一种仅搜索一个结果而不是集合的替代方法。

### 2.1 示例应用程序

让我们首先定义一个[示例Spring Data应用程序](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)。然后，让我们创建Player实体类：

```java
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Integer score;

    // all-arg and no-arg constructors overriden equals method getters and setters
}
```

让我们也创建扩展JpaRepository接口的PlayerRepository接口：

```java
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
```

### 2.2 findBy查询示例

如前所述，findBy关键字使用规则返回结果集合。该规则位于By关键字之后。让我们在PlayerRepository类中创建一个方法来派生一个查询，以查找得分高于给定输入的所有玩家：

```java
List<Player> findByScoreGreaterThan(Integer target);
```

Spring Data JPA将方法名语法解析成SQL语句来派生查询。让我们看看每个关键字的作用：

- find被翻译成select语句。
- By被解析为where子句。
- Score是表列名称，应该与Player类中定义的名称相同。
- GreaterThan在查询中添加>运算符以将分数字段与目标方法参数进行比较。

### 2.3 findAllBy查询示例

与findBy类似，让我们在PlayerRepository类中创建一个带有All关键字的方法：

```java
List<Player> findAllByScoreGreaterThan(Integer target);
```

该方法的工作方式类似于findByScoreGreaterThan()方法-唯一的区别是All关键字。该关键字只是一种命名约定，不会向派生查询添加任何功能，正如我们将在下一节中看到的那样。

## 3. findBy与findAllBy

现在，让我们验证findBy和findAllBy关键字之间仅存在命名约定差异，但证明它们在功能上是相同的。

### 3.1 功能差异

为了分析这两种方法之间是否存在功能差异，让我们编写一个集成测试：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FindByVsFindAllByApplication.class)
public class FindByVsFindAllByIntegrationTest {
    @Autowired
    private PlayerRepository playerRepository;

    @Before
    public void setup() {
        Player player1 = new Player(600);
        Player player2 = new Player(500);
        Player player3 = new Player(300);
        playerRepository.saveAll(Arrays.asList(player1, player2, player3));
    }

    @Test
    public void givenSavedPlayer_whenUseFindByOrFindAllBy_thenReturnSameResult() {
        List<Player> findByPlayers = playerRepository.findByScoreGreaterThan(400);
        List<Player> findAllByPlayers = playerRepository.findAllByScoreGreaterThan(400);
        assertEquals(findByPlayers, findAllByPlayers);
    }
}
```

注意：要使该测试通过，Player实体必须有一个重写的[equals()方法](https://www.baeldung.com/java-equals-hashcode-contracts)来比较id和score字段。

这两种方法返回的结果与assertEquals()中所示的结果相同。因此，从功能的角度来看，它们没有区别。

### 3.2 查询语法差异

为了完整起见，让我们比较一下这两种方法生成的查询的语法。为此，我们需要首先将以下行添加到我们的application.properties文件中：

```properties
spring.jpa.show-sql=true
```

如果我们重新运行集成测试，两个查询都应该出现在控制台中。这是findByScoreGreaterThan()的派生查询：


```sql
select
    player0_.id as id1_0_, player0_.score as score2_0_ 
from
    player player0_ 
where
    player0_.score>?
```

以及findAllByScoreGreaterThan()的派生查询：


```sql
select
    player0_.id as id1_0_, player0_.score as score2_0_
from
    player player0_
where
    player0_.score>?
```

如我们所见，生成的查询的语法没有差异。因此，**我们想要采用的代码风格是使用findBy和findAllBy关键字的唯一区别**。我们可以使用它们中的任何一个并期望得到相同的结果。

## 4. 返回单个结果

我们已经阐明了findBy和findAllBy之间没有区别，并且两者都返回结果集合。如果我们更改接口以从这些可能返回多个结果的查询返回单个结果，我们就有可能得到一个[NonUniqueResultException](https://www.baeldung.com/spring-jpa-non-unique-result-exception)。

在本节中，我们将查看[findFirst和findTop](https://www.baeldung.com/spring-data-jpa-findfirst-vs-findtop)关键字来派生返回单个结果的查询。

**应该在find和By关键字之间插入First和Top关键字，以查找存储的第一个元素**。它们也可以与条件关键字一起使用，例如IsGreaterThan。让我们看一个示例，查找存储的第一个分数大于400的玩家。首先，让我们在PlayerRepository类中创建我们的查询方法：

```java
Optional<Player> findFirstByScoreGreaterThan(Integer target);
```

Top关键字在功能上等同于First。它们之间的唯一区别是命名约定。因此，我们可以使用名为findTopByScoreGreaterThan()的方法获得相同的结果。

然后，我们验证此测试是否只得到一个结果：

```java
@Test
public void givenSavedPlayer_whenUsefindFirst_thenReturnSingleResult() {
    Optional<Player> player = playerRepository.findFirstByScoreGreaterThan(400);
    assertTrue(player.isPresent());
    assertEquals(600, player.get().getScore());
}
```

**findFirstBy查询使用limit SQL运算符返回存储的第一个与我们的条件匹配的元素**，在这种情况下，返回id=1且score=600的玩家。

最后，让我们看一下我们的方法生成的查询：

```sql
select
    player0_.id as id1_0_, player0_.score as score2_0_
from
    player player0_
where
    player0_.score>?
limit ?
```

查询与findBy和findAllBy几乎相同，除了末尾的limit运算符。

## 5. 总结

在本文中，我们探讨了Spring Data JPA中findBy和findAllBy关键字之间的相似之处。我们还了解了如何使用findFirstBy关键字返回单个结果。