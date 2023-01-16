## 1. 简介

EntityManager类的getReference ()方法自第一个版本以来一直是 JPA 规范的一部分。然而，这种方法让一些开发人员感到困惑，因为它的行为因底层持久性提供程序而异。

在本教程中，我们将解释如何在[Hibernate EntityManager](https://www.baeldung.com/hibernate-entitymanager)中使用getReference()方法。

## 2. EntityManager Fetch操作

首先，我们将看看如何通过主键获取实体。在不编写任何查询的情况下，EntityManager为我们提供了两种基本方法来实现这一点。

### 2.1. 寻找()

find()是最常用的获取实体的方法：

```java
Game game = entityManager.find(Game.class, 1L);

```

当我们请求时，此方法会初始化实体。

### 2.2. 获取引用()

与find()方法类似，getReference()也是另一种检索实体的方法：

```java
Game game = entityManager.getReference(Game.class, 1L);

```

但是，返回的对象是一个只有主键字段初始化的实体代理。除非我们懒惰地请求它们，否则其他字段保持未设置状态。

接下来，让我们看看这两种方法在各种场景下的表现。

## 3. 示例用例

为了演示EntityManager 的获取操作，我们将创建两个模型，Game和Player，作为我们的领域，许多玩家可以参与同一个游戏。

### 3.1. 领域模型

首先，让我们定义一个名为Game 的实体：

```java
@Entity
public class Game {

    @Id
    private Long id;

    private String name;

    // standard constructors, getters, setters

}

```

接下来，我们定义我们的Player实体：

```java
@Entity
public class Player {

    @Id
    private Long id;

    private String name;

    // standard constructors, getters, setters

}

```

### 3.2. 配置关系

我们需要配置从Player到Game的@ManyToOne关系。因此，让我们向Player实体添加一个游戏属性：

```java
@ManyToOne
private Game game;

```

## 4. 测试用例

在开始编写测试方法之前，最好单独定义测试数据：

```java
entityManager.getTransaction().begin();

entityManager.persist(new Game(1L, "Game 1"));
entityManager.persist(new Game(2L, "Game 2"));
entityManager.persist(new Player(1L,"Player 1"));
entityManager.persist(new Player(2L, "Player 2"));
entityManager.persist(new Player(3L, "Player 3"));

entityManager.getTransaction().commit();

```

此外，要检查底层 SQL 查询，我们应该在persistence.xml中配置 Hibernate 的hibernate.show_sql属性：

```xml
<property name="hibernate.show_sql" value="true"/>

```

### 4.1. 更新实体字段

首先，我们将检查使用find()方法更新实体的最常见方式。

因此，让我们先编写一个测试方法来获取Game实体，然后简单地更新其名称字段：

```java
Game game1 = entityManager.find(Game.class, 1L);
game1.setName("Game Updated 1");

entityManager.persist(game1);

```

运行测试方法向我们展示了执行的 SQL 查询：

```java
Hibernate: select game0_.id as id1_0_0_, game0_.name as name2_0_0_ from Game game0_ where game0_.id=?
Hibernate: update Game set name=? where id=?

```

正如我们所注意到的， SELECT查询在这种情况下看起来是不必要的。由于我们不需要在更新操作之前读取Game实体的任何字段，我们想知道是否有某种方法可以只执行UPDATE查询。

那么，让我们看看getReference()方法在同一场景中的行为方式：

```java
Game game1 = entityManager.getReference(Game.class, 1L);
game1.setName("Game Updated 2");

entityManager.persist(game1);

```

令人惊讶的是，运行测试方法的结果仍然相同，我们看到SELECT查询仍然存在。

正如我们所见，当我们使用getReference()更新实体字段时，Hibernate 确实执行了一个SELECT查询。

因此，如果我们执行实体代理字段的任何设置器，使用getReference()方法并不能避免额外的SELECT查询。

### 4.2. 删除实体

当我们执行删除操作时，可能会发生类似的情况。

让我们定义另外两个测试方法来删除Player实体：

```java
Player player2 = entityManager.find(Player.class, 2L);
entityManager.remove(player2);

Player player3 = entityManager.getReference(Player.class, 3L);
entityManager.remove(player3);

```

运行这些测试方法向我们展示了相同的查询：

```java
Hibernate: select
    player0_.id as id1_1_0_,
    player0_.game_id as game_id3_1_0_,
    player0_.name as name2_1_0_,
    game1_.id as id1_0_1_,
    game1_.name as name2_0_1_ from Player player0_
    left outer join Game game1_ on player0_.game_id=game1_.id
    where player0_.id=?
Hibernate: delete from Player where id=?

```

同样，对于删除操作，结果也是类似的。即使我们不读取Player实体的任何字段，Hibernate 也会执行一个额外的SELECT查询。

因此，删除现有实体时选择getReference()还是find()方法没有区别。

在这一点上，我们想知道，getReference()有什么不同吗？让我们继续实体关系并找出答案。

### 4.3. 更新实体关系

当我们需要保存实体之间的关系时，会出现另一个常见用例。

让我们添加另一种方法来通过简单地更新Player的游戏属性来演示Player参与游戏：

```java
Game game1 = entityManager.find(Game.class, 1L);

Player player1 = entityManager.find(Player.class, 1L);
player1.setGame(game1);

entityManager.persist(player1);

```

运行测试再次给我们类似的结果，我们仍然可以在使用find()方法时看到SELECT查询：

```java
Hibernate: select game0_.id as id1_0_0_, game0_.name as name2_0_0_ from Game game0_ where game0_.id=?
Hibernate: select
    player0_.id as id1_1_0_,
    player0_.game_id as game_id3_1_0_,
    player0_.name as name2_1_0_,
    game1_.id as id1_0_1_,
    game1_.name as name2_0_1_ from Player player0_
    left outer join Game game1_ on player0_.game_id=game1_.id
    where player0_.id=?
Hibernate: update Player set game_id=?, name=? where id=?

```

现在，让我们再定义一个测试，看看getReference()方法在这种情况下是如何工作的：

```java
Game game2 = entityManager.getReference(Game.class, 2L);

Player player1 = entityManager.find(Player.class, 1L);
player1.setGame(game2);

entityManager.persist(player1);

```

希望运行测试能给我们带来预期的行为：

```java
Hibernate: select
    player0_.id as id1_1_0_,
    player0_.game_id as game_id3_1_0_,
    player0_.name as name2_1_0_,
    game1_.id as id1_0_1_,
    game1_.name as name2_0_1_ from Player player0_
    left outer join Game game1_ on player0_.game_id=game1_.id
    where player0_.id=?
Hibernate: update Player set game_id=?, name=? where id=?

```

我们看到，这次我们使用getReference()时，Hibernate 没有为Game实体执行SELECT查询。

因此，在这种情况下选择getReference()似乎是一个好习惯。这是因为代理Game实体足以从Player实体创建关系 - Game实体不必初始化。

因此，当我们更新实体关系时，使用getReference()可以消除不必要的数据库往返。

## 5. Hibernate一级缓存

在某些情况下， find()和getReference()方法可能都不执行任何SELECT查询，这有时会让人感到困惑。

让我们想象一种情况，我们的实体在我们操作之前已经加载到持久化上下文中：

```java
entityManager.getTransaction().begin();
entityManager.persist(new Game(1L, "Game 1"));
entityManager.persist(new Player(1L, "Player 1"));
entityManager.getTransaction().commit();

entityManager.getTransaction().begin();
Game game1 = entityManager.getReference(Game.class, 1L);

Player player1 = entityManager.find(Player.class, 1L);
player1.setGame(game1);

entityManager.persist(player1);
entityManager.getTransaction().commit();

```

运行测试表明只执行了更新查询：

```java
Hibernate: update Player set game_id=?, name=? where id=?

```

在这种情况下，我们应该注意到我们没有看到任何SELECT查询，无论我们使用find()还是getReference()。这是因为我们的实体缓存在Hibernate的一级缓存中。

因此，当我们的实体存储在 Hibernate 的一级缓存中时，find()和getReference()方法的行为相同，不会访问我们的数据库。

## 6. 不同的 JPA 实现

最后提醒一下，我们应该知道getReference()方法的行为取决于底层的持久性提供程序。

根据[JPA 2 规范](https://github.com/eclipse-ee4j/jpa-api/blob/ffe76306a2846df4849612af20ef03ecc6ff4843/api/src/main/java/javax/persistence/EntityManager.java#L258)，在调用getReference()方法时允许持久性提供程序抛出 EntityNotFoundException 。因此，它可能与其他持久性提供程序不同，我们在使用getReference()时可能会遇到EntityNotFoundException。

然而，Hibernate 在默认情况下不遵循getReference()的规范以在可能的情况下保存数据库往返。因此，当我们检索实体代理时它不会抛出异常，即使它们不存在于数据库中。

或者，Hibernate 提供了一个配置属性，为那些想要遵循 JPA 规范的人提供了一种自以为是的方式。

在这种情况下，我们可以考虑将hibernate.jpa.compliance.proxy属性设置为true：

```xml
<property name="hibernate.jpa.compliance.proxy" value="true"/>

```

使用此设置，Hibernate 在任何情况下都会初始化实体代理，这意味着即使我们使用getReference()它也会执行SELECT查询。

## 七. 总结

在本教程中，我们探讨了一些可以从引用代理对象中受益的用例，并学习了如何在 Hibernate 中使用EntityManager的getReference()方法。