## 一、简介

在本教程中，我们将了解如何使用[Exposed](https://github.com/JetBrains/Exposed)查询关系数据库。

Exposed 是 JetBrains 开发的开源库(Apache 许可)，它为一些关系数据库实现提供了惯用的 Kotlin API，同时消除了数据库供应商之间的差异。

Exposed 既可以用作基于 SQL 的高级 DSL，也可以用作轻量级 ORM(对象关系映射)。因此，我们将在本教程中涵盖这两种用法。

## 2.暴露框架设置

让我们添加所需的 Maven 依赖项：

```xml
<dependency>
    <groupId>org.jetbrains.exposed</groupId>
    <artifactId>exposed-core</artifactId>
    <version>0.37.3</version>
</dependency>
<dependency>
    <groupId>org.jetbrains.exposed</groupId>
    <artifactId>exposed-dao</artifactId>
    <version>0.37.3</version>
</dependency>
<dependency>
    <groupId>org.jetbrains.exposed</groupId>
    <artifactId>exposed-jdbc</artifactId>
    <version>0.37.3</version>
</dependency>
```

此外，在以下部分中，我们将展示在内存中使用 H2 数据库的示例：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.1.210</version>
</dependency>
```

我们可以在 Maven Central 上找到最新版本的[Exposed](https://search.maven.org/search?q=g:org.jetbrains.exposed) dependencies 和最新版本的[H2 。](https://search.maven.org/search?q=g:com.h2database a:h2)

## 3. 连接数据库

我们使用Database类定义数据库连接：

```java
Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
```

我们还可以将用户和密码指定为命名参数：

```java
Database.connect(
  "jdbc:h2:mem:test", driver = "org.h2.Driver",
  user = "myself", password = "secret")
```

请注意，调用connect不会立即建立与数据库的连接。它只是保存连接参数供以后使用。

### 3.1. 附加参数

如果我们需要提供其他连接参数，我们将使用不同的connect方法重载，使我们能够完全控制数据库连接的获取：

```java
Database.connect({ DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL") })
```

这个版本的连接需要一个闭包参数。只要需要与数据库的新连接，Exposed 就会调用闭包。

### 3.2. 使用数据源

相反，如果我们使用DataSource连接到数据库，这在企业应用程序中通常是这种情况(例如，为了从连接池中受益)，我们可以使用适当的连接重载：

```java
Database.connect(datasource)
```

## 4. 开立交易

Exposed 中的每个数据库操作都需要一个活动事务。

事务方法采用闭包并使用活动事务调用它：

```java
transaction {
    //Do cool stuff
}
```

事务返回闭包返回的任何内容。然后，Exposed 在块执行终止时自动关闭事务。

### 4.1. 提交和回滚

当事务块成功返回时，Exposed 提交事务。相反，当闭包通过抛出异常退出时，框架会回滚事务。

我们也可以手动提交或回滚事务。由于 Kotlin 魔法，我们提供给事务的闭包实际上是Transaction类的一个实例。

因此，我们有可用的提交和回滚方法：

```java
transaction {
    //Do some stuff
    commit()
    //Do other stuff
}
```

### 4.2. 记录语句

在学习框架或调试时，我们可能会发现检查 Exposed 发送到数据库的 SQL 语句和查询很有用。

我们可以轻松地将这样的记录器添加到活动事务中：

```java
transaction {
    addLogger(StdOutSqlLogger)
    //Do stuff
}
```

## 5. 定义表

通常，在 Exposed 中我们不使用原始 SQL 字符串和名称。相反，我们使用高级 DSL 定义表、列、键、关系等。

我们用Table类的实例表示每个表：

```java
object StarWarsFilms : Table()
```

Exposed 自动根据类名计算表名，但我们也可以提供一个显式名称：

```java
object StarWarsFilms : Table("STAR_WARS_FILMS")
```

### 5.1. 列

没有列的表是没有意义的。我们将列定义为表类的属性：

为简洁起见，我们省略了类型，因为 Kotlin 可以为我们推断它们。不管怎样，每一列都是Column<T>类型，它有一个名称、一个类型和可能的类型参数。

```kotlin
object StarWarsFilms_Simple : Table() {
    val id = integer("id").autoIncrement()
    val sequelId = integer("sequel_id").uniqueIndex()
    val name = varchar("name", 50)
    val director = varchar("director", 50)
    override val primaryKey = PrimaryKey(id, name = "PK_StarWarsFilms_Id")
}
```

### 5.2. 主键

从上一节的示例中可以看出，我们可以使用流畅的 API 轻松定义索引和主键。

但是，对于具有整数主键的表的常见情况，Exposed 提供了类IntIdTable和LongIdTable来为我们定义键：

```java
object StarWarsFilms : IntIdTable() {
    val sequelId = integer("sequel_id").uniqueIndex()
    val name = varchar("name", 50)
    val director = varchar("director", 50)
}
```

还有一个UUIDTable；此外，我们可以通过子类化 IdTable 来定义我们自己的变体。

### 5.3. 外键

外键很容易引入。我们还受益于静态类型，因为我们总是引用编译时已知的属性。

假设我们要跟踪每部电影中扮演的演员的姓名：

```java
object Players : Table() {
    val sequelId = integer("sequel_id")
      .uniqueIndex()
      .references(StarWarsFilms.sequelId)
    val name = varchar("name", 50)
}
```

当可以从引用的列派生时，为了避免必须拼写列的类型(在本例中为integer )，我们可以使用引用方法作为简写：

```java
val sequelId = reference("sequel_id", StarWarsFilms.sequelId).uniqueIndex()
```

如果引用的是主键，我们可以省略列名：

```java
val filmId = reference("film_id", StarWarsFilms)
```

### 5.4. 创建表

我们可以通过编程方式创建上面定义的表：

```java
transaction {
    SchemaUtils.create(StarWarsFilms, Players)
    //Do stuff
}
```

这些表仅在它们尚不存在时才创建。但是，不支持数据库迁移。

## 6.查询

一旦我们如前几节所示定义了一些表类，我们就可以使用框架提供的扩展函数向数据库发出查询。

### 6.1. 全选

为了从数据库中提取数据，我们使用从表类构建的查询对象。最简单的查询是返回给定表的所有行的查询：

```java
val query = StarWarsFilms.selectAll()
```

查询是一个Iterable，因此它支持forEach：

```java
query.forEach {
    assertTrue { it[StarWarsFilms.sequelId] >= 7 }
}
```

在上面的示例中隐式调用的闭包参数是ResultRow类的一个实例。我们可以将其视为按列键控的地图。

### 6.2. 选择列的子集

我们还可以选择表列的子集，即执行投影，使用slice方法：

```java
StarWarsFilms.slice(StarWarsFilms.name, StarWarsFilms.director)
  .selectAll()
  .forEach {
      assertTrue { it[StarWarsFilms.name].startsWith("The") }
  }
```

我们也使用slice将函数应用于列：

```java
StarWarsFilms.slice(StarWarsFilms.name.countDistinct())
```

通常，在使用count和avg 等聚合函数时，我们需要在查询中使用 group by 子句。我们将在 6.5 节中讨论 group by。

### 6.3. 使用 Where 表达式过滤

Exposed 包含where表达式的专用 DSL ，用于过滤查询和其他类型的语句。这是一种基于我们之前遇到的列属性和一系列布尔运算符的迷你语言。

这是一个 where 表达式：

```java
{ (StarWarsFilms.director like "J.J.%") and (StarWarsFilms.sequelId eq 7) }
```

它的类型很复杂；它是SqlExpressionBuilder的子类，它定义了诸如like、eq 和之类的运算符。如我们所见，它是用and和or运算符组合在一起的一系列比较。

我们可以将这样的表达式传递给select方法，它再次返回一个查询：

```java
val select = StarWarsFilms.select { ... }
assertEquals(1, select.count())
```

多亏了类型推断，我们不需要像上面的例子一样直接传递给select方法时拼写出 where 表达式的复杂类型。

由于 where 表达式是 Kotlin 对象，因此对查询参数没有特殊规定。我们简单地使用变量：

```java
val sequelNo = 7
StarWarsFilms.select { StarWarsFilms.sequelId >= sequelNo }
```

### 6.4. 高级过滤

select及其变体返回的Query对象有许多方法，我们可以使用这些方法来优化查询。

例如，我们可能想要排除重复行：

```java
query.withDistinct(true).forEach { ... }
```

或者我们可能只想返回行的子集，例如在为 UI 的结果分页时：

```java
query.limit(20, offset = 40).forEach { ... }
```

这些方法返回一个新的Query，因此我们可以轻松地将它们链接起来。

### 6.5. 排序 依据和分组 依据

Query.orderBy方法接受映射到SortOrder值的列列表，指示排序是升序还是降序：

```java
query.orderBy(StarWarsFilms.name to SortOrder.ASC)
```

虽然按一列或多列分组，但在使用聚合函数时特别有用(请参阅第 6.2 节)，是使用groupBy方法实现的：

```java
StarWarsFilms
  .slice(StarWarsFilms.sequelId.count(), StarWarsFilms.director)
  .selectAll()
  .groupBy(StarWarsFilms.director)
```

### 6.6. 加入

联接可以说是关系数据库的卖点之一。在最简单的情况下，当我们有外键且没有连接条件时，我们可以使用内置连接运算符之一：

```java
(StarWarsFilms innerJoin Players).selectAll()
```

这里我们展示了 innerJoin，但我们也有左连接、右连接和交叉连接，原理相同。

然后，我们可以使用 where 表达式添加连接条件；例如，如果没有外键，我们必须显式执行连接：

```java
(StarWarsFilms innerJoin Players)
  .select { StarWarsFilms.sequelId eq Players.sequelId }
```

在一般情况下，连接的完整形式如下：

```java
val complexJoin = Join(
  StarWarsFilms, Players,
  onColumn = StarWarsFilms.sequelId, otherColumn = Players.sequelId,
  joinType = JoinType.INNER,
  additionalConstraint = { StarWarsFilms.sequelId eq 8 })
complexJoin.selectAll()
```

### 6.7. 混叠

由于列名到属性的映射，我们不需要在典型的连接中使用任何别名，即使列恰好具有相同的名称：

```java
(StarWarsFilms innerJoin Players)
  .selectAll()
  .forEach {
      assertEquals(it[StarWarsFilms.sequelId], it[Players.sequelId])
  }
```

事实上，在上面的例子中，StarWarsFilms.sequelId和Players.sequelId是不同的列。

然而，当同一个表在一个查询中出现不止一次时，我们可能想给它一个别名。为此，我们使用别名函数：

```java
val sequel = StarWarsFilms.alias("sequel")
```

然后我们可以像使用表格一样使用别名：

```java
Join(StarWarsFilms, sequel,
  additionalConstraint = {
      sequel[StarWarsFilms.sequelId] eq StarWarsFilms.sequelId + 1 
  }).selectAll().forEach {
      assertEquals(
        it[sequel[StarWarsFilms.sequelId]], it[StarWarsFilms.sequelId] + 1)
  }
```

在上面的例子中，我们可以看到sequel别名是一个参与连接的表。当我们想要访问它的列之一时，我们使用别名表的列作为键：

```java
sequel[StarWarsFilms.sequelId]
```

## 七、报表

现在我们已经了解了如何查询数据库，让我们看看如何执行 DML 语句。

### 7.1. 插入数据

要插入数据，我们调用插入函数的一种变体。所有变体都采用闭包：

```java
StarWarsFilms.insert {
    it[name] = "The Last Jedi"
    it[sequelId] = 8
    it[director] = "Rian Johnson"
}
```

上面的闭包中涉及两个值得注意的对象：

-   this(闭包本身)是StarWarsFilms类的一个实例；这就是为什么我们可以通过非限定名称访问作为属性的列
-   它(闭包参数)是一个InsertStatement；它是一个类似地图的结构，每列都有一个插槽供插入

### 7.2. 提取自动递增的列值

当我们有一个带有自动生成列(通常是自动递增或序列)的插入语句时，我们可能希望获得生成的值。

在典型情况下，我们只有一个生成值，我们调用insertAndGetId：

```java
val id = StarWarsFilms.insertAndGetId {
    it[name] = "The Last Jedi"
    it[sequelId] = 8
    it[director] = "Rian Johnson"
}
assertEquals(1, id.value)
```

如果我们有多个生成值，我们可以按名称读取它们：

```java
val insert = StarWarsFilms.insert {
    it[name] = "The Force Awakens"
    it[sequelId] = 7
    it[director] = "J.J. Abrams"
}
assertEquals(2, insert[StarWarsFilms.id]?.value)
```

### 7.3. 更新数据

我们现在可以使用我们了解的有关查询和插入的知识来更新数据库中的现有数据。实际上，一个简单的更新看起来像是选择与插入的组合：

```java
StarWarsFilms.update ({ StarWarsFilms.sequelId eq 8 }) {
    it[name] = "Episode VIII – The Last Jedi"
}
```

我们可以看到 where 表达式与UpdateStatement闭包的结合使用。事实上，UpdateStatement和InsertStatement通过一个公共超类UpdateBuilder共享大部分 API 和逻辑，它提供了使用惯用方括号设置列值的能力。

当我们需要通过从旧值计算新值来更新列时，我们利用SqlExpressionBuilder：

```java
StarWarsFilms.update ({ StarWarsFilms.sequelId eq 8 }) {
    with(SqlExpressionBuilder) {
        it.update(StarWarsFilms.sequelId, StarWarsFilms.sequelId + 1)
    }
}
```

这是一个提供中缀运算符(如加号、减号等)的对象，我们可以使用它们来构建更新指令。

### 7.4. 删除数据

最后，我们可以使用deleteWhere方法删除数据：

```java
StarWarsFilms.deleteWhere ({ StarWarsFilms.sequelId eq 8 })
```

## 8. DAO API，一个轻量级的 ORM

到目前为止，我们已经使用 Exposed 直接将 Kotlin 对象上的操作映射到 SQL 查询和语句。每个方法调用(如插入、更新、选择等)都会立即将 SQL 字符串发送到数据库。

然而，Exposed 也有一个更高级别的 DAO API，构成了一个简单的 ORM。现在让我们深入探讨。

### 8.1. 实体

在前面的部分中，我们使用类来表示数据库表并使用静态方法来表达对它们的操作。

更进一步，我们可以根据这些表类定义实体，其中实体的每个实例代表一个数据库行：

```java
class StarWarsFilm(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, StarWarsFilm>(StarWarsFilms)

    var sequelId by StarWarsFilms.sequelId
    var name     by StarWarsFilms.name
    var director by StarWarsFilms.director
}
```

现在让我们逐个分析上面的定义。

在第一行中，我们可以看到实体是扩展Entity的类。它有一个特定类型的 ID，在本例中为Int。

```java
class StarWarsFilm(id: EntityID<Int>) : Entity<Int>(id) {
```

然后，我们遇到了伴生对象定义。伴随对象代表实体类，即定义实体和我们可以对其执行的操作的静态元数据。

此外，在伴随对象的声明中，我们将实体StarWarsFilm -单数，因为它代表一行-连接到表StarWarsFilms - 复数，因为它代表所有行的集合。

```java
companion object : EntityClass<Int, StarWarsFilm>(StarWarsFilms)
```

最后，我们有属性，实现为相应表列的属性委托。

```java
var sequelId by StarWarsFilms.sequelId
var name     by StarWarsFilms.name
var director by StarWarsFilms.director
```

请注意，之前我们使用val声明了列，因为它们是不可变的元数据。现在，我们使用var声明实体属性，因为它们是数据库行中的可变槽。

### 8.2. 插入数据

要在表中插入一行，我们只需在事务中使用静态工厂方法new创建实体类的新实例：

```java
val theLastJedi = StarWarsFilm.new {
    name = "The Last Jedi"
    sequelId = 8
    director = "Rian Johnson"
}
```

请注意，对数据库的操作是延迟执行的；它们仅在刷新热缓存时发出。为了进行比较，Hibernate 将热缓存称为会话。

这在需要时会自动发生；例如，我们第一次读取生成的标识符时，Exposed 静默执行插入语句：

```java
assertEquals(1, theLastJedi.id.value) //Reading the ID causes a flush
```

将此行为与 7.1 节中的插入方法进行比较，后者会立即对数据库发出一条语句。在这里，我们在更高的抽象层次上工作。

### 8.3. 更新和删除对象

要更新一行，我们只需分配给它的属性：

```java
theLastJedi.name = "Episode VIII – The Last Jedi"
```

在删除一个对象时，我们对其调用delete：

```java
theLastJedi.delete()
```

与new一样，更新和操作是延迟执行的。

只能对先前加载的对象执行更新和删除。没有用于大量更新和删除的 API。相反，我们必须使用我们在第 7 节中看到的较低级别的 API。不过，这两个 API 可以在同一事务中一起使用。

### 8.4. 查询

使用 DAO API，我们可以执行三种类型的查询。

要无条件加载所有对象，我们使用静态方法all：

```java
val movies = StarWarsFilm.all()
```

要通过 ID 加载单个对象，我们调用findById：

```java
val theLastJedi = StarWarsFilm.findById(1)
```

如果不存在具有该 ID 的对象，则 findById返回null。

最后，在一般情况下，我们使用带有 where 表达式的find ：

```java
val movies = StarWarsFilm.find { StarWarsFilms.sequelId eq 8 }
```

### 8.5. 多对一关联

正如连接是关系数据库的一个重要特性一样，连接到引用的映射也是 ORM 的一个重要方面。那么，让我们看看 Exposed 能提供什么。

假设我们要跟踪用户对每部电影的评分。首先，我们定义两个额外的表：

```java
object Users: IntIdTable() {
    val name = varchar("name", 50)
}

object UserRatings: IntIdTable() {
    val value = long("value")
    val film = reference("film", StarWarsFilms)
    val user = reference("user", Users)
}
```

然后，我们将编写相应的实体。让我们省略简单的User实体，直接转到UserRating类：

```java
class UserRating(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserRating>(UserRatings)

    var value by UserRatings.value
    var film  by StarWarsFilm referencedOn UserRatings.film
    var user  by User         referencedOn UserRatings.user
}
```

特别要注意对表示关联的属性的referencedOn 中缀方法调用。模式如下：引用实体的var声明，在引用列上被引用。

以这种方式声明的属性的行为类似于常规属性，但它们的值是关联的对象：

```java
val someUser = User.new {
    name = "Some User"
}
val rating = UserRating.new {
    value = 9
    user = someUser
    film = theLastJedi
}
assertEquals(theLastJedi, rating.film)
```

### 8.6. 可选协会

我们在上一节中看到的关联是强制性的，也就是说，我们必须始终指定一个值。

如果我们想要一个可选的关联，我们必须首先在表中声明该列为可空：

```java
val user = reference("user", Users).nullable()
```

然后，我们将在实体中使用optionalReferencedOn而不是referencedOn ：

```java
var user by User optionalReferencedOn UserRatings.user
```

这样，用户属性将可以为空。

### 8.7. 一对多关联

我们可能还想映射关联的另一侧。评级是关于电影的，这就是我们在数据库中使用外键建模的内容；因此，一部电影有许多评级。

要映射电影的评级，我们只需将一个属性添加到关联的“一”端，即我们示例中的电影实体：

```java
class StarWarsFilm(id: EntityID<Int>) : Entity<Int>(id) {
    //Other properties omitted
    val ratings  by UserRating referrersOn UserRatings.film
}
```

该模式类似于多对一关系，但它使用referrersOn。这样定义的属性是一个Iterable，所以我们可以用forEach 遍历它：

```java
theLastJedi.ratings.forEach { ... }
```

请注意，与常规属性不同，我们使用val定义了评级。确实，属性是不可变的，我们只能读取它。

该属性的值也没有用于突变的 API。所以，要添加一个新的评级，我们必须参考电影来创建它：

```java
UserRating.new {
    value = 8
    user = someUser
    film = theLastJedi
}
```

然后，电影的评级列表将包含新添加的评级。

### 8.8. 多对多关联

在某些情况下，我们可能需要多对多关联。比方说我们想添加一个对StarWarsFilm类的Actors表的引用：

```java
object Actors: IntIdTable() {
    val firstname = varchar("firstname", 50)
    val lastname = varchar("lastname", 50)
}

class Actor(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Actor>(Actors)

    var firstname by Actors.firstname
    var lastname by Actors.lastname
}
```

定义表和实体后，我们需要另一个表来表示关联：

```kotlin
object StarWarsFilmActors : Table() {
    val starWarsFilm = reference("starWarsFilm", StarWarsFilms)
    val actor = reference("actor", Actors)
    override val primaryKey = PrimaryKey(
      starWarsFilm, actor, 
      name = "PK_StarWarsFilmActors_swf_act")
}
```

该表有两列，它们都是外键，也构成了复合主键。

最后，我们可以将关联表与StarWarsFilm实体连接起来：

```java
class StarWarsFilm(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StarWarsFilm>(StarWarsFilms)

    //Other properties omitted
    var actors by Actor via StarWarsFilmActors
}
```

在撰写本文时，无法使用生成的标识符创建实体并将其包含在同一事务的多对多关联中。

事实上，我们必须使用多个事务：

```java
//First, create the film
val film = transaction {
   StarWarsFilm.new {
    name = "The Last Jedi"
    sequelId = 8
    director = "Rian Johnson"r
  }
}
//Then, create the actor
val actor = transaction {
  Actor.new {
    firstname = "Daisy"
    lastname = "Ridley"
  }
}
//Finally, link the two together
transaction {
  film.actors = SizedCollection(listOf(actor))
}
```

在这里，为方便起见，我们使用了三种不同的交易。但是，两个就足够了。

## 9.总结

在本文中，我们全面概述了 Kotlin 的 Exposed 框架。有关其他信息和示例，请参阅[Exposed wiki](https://github.com/JetBrains/Exposed/wiki)。