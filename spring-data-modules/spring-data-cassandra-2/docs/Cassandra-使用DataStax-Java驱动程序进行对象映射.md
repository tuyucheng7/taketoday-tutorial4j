## 一、概述

在本教程中，我们将学习如何使用[DataStax Java 驱动程序](https://docs.datastax.com/en/developer/java-driver/4.14/manual/)将对象映射到 Cassandra 表。

我们将了解如何使用 Java 驱动程序定义实体、创建 DAO 以及对 Cassandra 表执行 CRUD 操作。

## 2.项目设置

我们将使用 Spring Boot 框架创建一个与 Cassandra 数据库交互的简单应用程序。我们将使用 Java 驱动程序创建表、实体和 DAO。然后，我们将使用 DAO 对表执行 CRUD 操作。

### 2.1. 依赖关系

让我们首先将所需的依赖项添加到我们的项目中。我们将使用[Cassandra 的 Spring Boot starter](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-data-cassandra)连接到数据库：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
</dependency>
复制
```

此外，我们将添加 [*java-driver-mapper-runtime*](https://search.maven.org/artifact/com.datastax.oss/java-driver-mapper-runtime) 依赖项以将对象映射到 Cassandra 表：![img]()

```xml
<dependency>
    <groupId>com.datastax.oss</groupId>
    <artifactId>java-driver-mapper-runtime</artifactId>
    <version>4.13.0</version>
</dependency>
复制
```

最后，让我们[配置注释处理器](https://docs.datastax.com/en/developer/java-driver/4.2/manual/mapper/config/)以在编译时生成 DAO 和映射器：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>com.datastax.oss</groupId>
                <artifactId>java-driver-mapper-processor</artifactId>
                <version>4.13.0</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
复制
```

## 3. Cassandra实体

让我们定义一个可用于映射到 Cassandra 表的实体。我们将创建一个 代表 *user_profile表的**User*类 ：

```java
@Entity
public class User {
    @PartitionKey
    private int id;
    private String userName;
    private int userAge;
    
    // getters and setters
}
复制
```

**@Entity 注释告诉映射器\*将\*此类映射到表**。@PartitionKey注释告诉映射器使用*id*字段作为*表* 的[分区键。](https://www.baeldung.com/cassandra-keys#1-partition-key)

映射器使用默认构造函数来创建实体的新实例。因此，**我们需要确保可以访问默认的无参数构造函数。**如果我们创建一个非默认构造函数，我们需要显式声明默认构造函数。

默认情况下，实体是可变的，因此我们必须声明 getter 和 setter。我们将在本教程后面看到如何更改此行为。

### 3.1. 命名策略

**@NamingStrategy 注释\*允许\* 我们 为表和列指定[命名约定。](https://docs.datastax.com/en/drivers/java/4.14/com/datastax/oss/driver/api/mapper/entity/naming/NamingConvention.html)** 默认的命名策略是 *NamingConvention.SNAKE_CASE_INSENSITIVE。*它在与数据库交互时将类名和字段名转换为蛇形命名法。

例如，默认情况下， *userName*字段映射到 数据库中的*user_name列。*如果我们将命名策略更改为 *NamingConvention.LOWER_CAMEL_CASE*， 则*userName* 字段将映射到 数据库中的*userName列。*

### 3.2. 物业策略

@PropertyStrategy 注释指定映射器将如何访问实体*的*属性。它具有三个属性*——mutable*、 *getterStyle*和 *setterStyle*。

**可变 属性告诉映射器实体是否可变 \*。\***默认情况下是*正确的*。如果我们将其设置为 *false*，映射器将使用 *“所有列”* 构造函数来创建实体的新实例。

*“所有列”*构造函数 是一个将表的所有列作为参数的构造函数，其顺序与它们在实体中定义的顺序相同。例如，如果我们有一个包含以下字段的实体： *id*、 *userName*和 *userAge*， *“所有列”* 构造函数将如下所示：

```java
public User(int id, String userName, int userAge) {
    this.id = id;
    this.userName = userName;
    this.userAge = userAge;
}
复制
```

除此之外，实体应该有 getter 但不需要有 setter。可选地，按照惯例，字段可以是*final*。

getterStyle 和 *setterStyle* 属性告诉映射器如何找到实体的 getter 和 setter *。*它们都有两个可能的值——FLUENT 和 JAVA_BEANS。

如果设置为 FLUENT，映射器将查找与字段同名的方法。例如，如果该字段是 *userName* ，则映射器将查找名为 *userName()*的方法。

如果设置为 JAVA_BEANS，映射器将查找带有 *get* 或 *set* 前缀的方法。例如，如果该字段是 *userName* ，则映射器将查找名为 *getUserName()*的方法。

对于普通的 Java 类， *getterStyle* 和 *setterStyle*属性默认 设置为*JAVA_BEANS 。*但是，对于 Records，它们默认设置为 FLUENT。同样， Records 的*可变*属性 默认 设置为 *false 。*

### 3.3. *@CqlName*

**@CqlName 注解指定 Cassandra 数据库中表或列的名称\*。\*** 由于我们要将 *User* 实体映射到 *user_profile*表，并将 *userName* 字段映射到 表中的 *用户名列，我们可以使用**@CqlName* 注解：

```java
@Entity
@CqlName("user_profile")
public class User {
    @PartitionKey
    private int id;
    @CqlName("username")
    private String userName;
    private int userAge;
    
    // getters and setters
}
复制
```

遵循默认或指定命名策略的字段不需要注释。

### 3.4. *@PartitionKey*和*@ClusteringColumn*

分区键和集群列分别使用 *@PartitionKey* 和 *@ClusteringColumn* 注释定义。在我们的例子中， *id* 字段是分区键。*如果我们想按userAge*字段对行进行排序 ，我们可以将 *@ClusteringColumn* 注释添加到 *userAge* 字段。

```java
@ClusteringColumn
private int userAge;
复制
```

**可以在实体中定义多个分区键和集群列。** 可以通过在注释中传递顺序来指定分区的顺序。例如，如果我们想按 *id* 然后按 *userName*对表进行分区，我们可以执行以下操作：

```java
@PartitionKey(1)
private int id;
@PartitionKey(2)
@CqlName("username")
复制
```

并且类似地用于聚类列。

### 3.5. *@短暂的*

*@Transient*注释 告诉映射器忽略该字段。*标记为@Transient*的字段 不会映射到数据库中的列。它只会是 Java 对象的一部分。映射器不会尝试从数据库读取或写入字段的值。

除了 在字段上使用*@Transient* 注解，我们还可以 在实体上使用*@TransientProperties注解，将多个字段标记为transient。*

### 3.6. *@计算*

标记为 *@Computed 的*字段 映射到数据库中的列，但它们不能由客户端设置。它们由存储在服务器上的数据库函数计算得出。

假设我们要向存储行的写入时间戳的实体添加一个新字段。我们可以像下面这样添加一个实体：

```java
@Computed("writetime(userName)")
private long writeTime;
复制
```

*创建用户*记录时 ，映射器将调用*writetime()方法并将字段**writeTime* 的值设置 为函数的结果。

## 4.层次实体

实体可以使用继承来定义。这可能是对具有大量公共字段的实体建模的好方法。

例如，我们可以有一个 *user_profile*表，其中包含所有用户的公共字段。然后我们可以有一个 *admin_profile*表，其中包含用于管理员的附加字段。

在这种情况下，我们可以为 *user_profile表定义一个实体，然后扩展它为**admin_profile*表 创建一个实体 ：

```java
@Entity
@CqlName("admin_profile")
public class Admin extends User {
    private String role;
    private String department;
    
    // getters and setters
}
复制
```

Admin 实体将具有*User*实体 的所有字段 以及*role* 和 *department**的*附加字段 。我们应该注意，*@Entity*注释仅在具体类上是必需的。抽象类或接口不需要它。

### 4.1. 层次实体中的不变性

如果子类不可变，则 子类的 *“所有列”*构造函数需要调用 父类的*“所有列”构造函数。*在这种情况下，参数顺序应该是**先传递子类的参数，再传递父类的参数**。

例如，我们可以为 Admin 实体创建一个*“所有列”构造函数：*

```java
public Admin(String role, String department, int id, String userName, int userAge) {
    super(id, userName, userAge);
    this.role = role;
    this.department = department;
}
复制
```

### 4.2. *@HierarchyScanStrategy*

@HierarchyScanStrategy 注释指定映射器应如何扫描实体的层次结构以获取注释*。*

它具有三个字段：

-   *scanAncestors——*默认情况下，它设置为*true* ，映射器将扫描实体的整个层次结构。当设置为 *false*时，映射器将只扫描实体。
-   *highestAncestor——*当设置为一个类时，映射器将扫描实体的层次结构，直到它到达指定的类。指定类以上的类将不会被扫描。
-   *includeHighestAncestor* – 当设置为*true*时，映射器还将扫描指定的 highestAncestor。默认情况下，映射器只会扫描指定类之上的类。

让我们看看如何使用这些注释：

```java
@Entity
@HierarchyScanStrategy(highestAncestor = User.class, includeHighestAncestor = true)
public class Admin extends User {
    private String role;
    private String department;
    
    // getters and setters
}
复制
```

通过将 *highestAncestor* 属性设置为 *User.class*，映射器将扫描 *Admin* 实体的层次结构，直到它到达 *User* 实体。

*我们已将includeHighestAncestor*设置 为 *true* ，因此映射器还将扫描 *User* 实体。默认情况下，该属性设置为 *false* ，因此映射器不会扫描 *用户* 实体。

扫描器不会扫描*用户*实体扩展的任何实体。

## 5.DAO接口

映射器提供了一个在 Cassandra 数据库上执行操作的 DAO 接口。我们可以使用*@Dao*注解来创建一个DAO 接口。接口的方法必须具有映射器提供的注解之一。

### 5.1. CRUD 注释

映射器提供以下注释来对数据库执行基本的 CRUD 操作：

-   *@Insert* – 在数据库中插入一行
-   *@Select* – 创建带有指定参数的选择查询并返回结果。结果可以是单个实体或实体列表。
-   *@Update* – 更新数据库中的一行
-   *@Delete* – 从数据库中删除一行

让我们看看如何使用这些注释：![img]()

```java
@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);
    
    @Select
    User getUserById(int id);
    
    @Select
    PagingIterable<User> getAllUsers();
    
    @Update
    void updateUser(User user);
    
    @Delete
    void deleteUser(User user);
}
复制
```

需要注意的重点是方法的参数应该与注释的允许参数相匹配。

插入、更新和删除方法应该有一个参数，即要插入、更新或删除的实体。

select 方法有两个选项：

-   实体的完整[主键](https://www.baeldung.com/cassandra-keys#primary-key)——参数以分区键开头，然后是按它们在实体中应用的顺序排列的聚簇列。在这种情况下，该方法将返回单个实体。
-   主键的子集——在这种情况下，该方法将返回一个实体列表。

### 5.2. 使用*@Query的自定义查询*

有两种方法可以对数据库执行自定义查询。我们可以使用 *@Query* 或*@QueryProvider*注释。

我们先看 *@Query* 注解：

```java
@Dao
public interface UserDao {

    @Query("select * from user_profile where user_age > :userAge ALLOW FILTERING")
    PagingIterable<User> getUsersOlderThan(int userAge);
}
复制
```

*ALLOW FILTERING*子句 是必需的，因为我们在未指定分区键的情况下对二级索引执行查询。此类查询可能需要更长的时间，应避免在大型数据集上进行。

当是简单的查询时，我们可以使用 *@Query* 注解。当查询比较复杂时，可能需要使用核心驱动来执行查询。我们可以使用 *@QueryProvider* 注解来做到这一点。

### 5.3. 使用*@QueryProvider 的自定义查询*

**@QueryProvider 注释采用一个负责查询执行并返回结果的类\*。\***

让我们为上述查询创建一个查询提供程序：``

```java
public class UserQueryProvider {

    private final CqlSession session;
    private final EntityHelper<User> userHelper;

    public UserQueryProvider(MapperContext context, EntityHelper<User> userHelper) {
        this.session = context.getSession();
        this.userHelper = userHelper;
    }

    public PagingIterable<User> getUsersOlderThanAge(String age) {
        SimpleStatement statement = QueryBuilder.selectFrom("user_profile")
          .all()
          .whereColumn("user_age")
          .isGreaterThan(QueryBuilder
            .bindMarker(age))
          .build();
        PreparedStatement preparedSelectUser = session.prepare(statement);
        return session
          .execute(preparedSelectUser.getQuery())
          .map(result -> userHelper.get(result, true));
    }
}复制
```

实体助手用于将查询结果转换为实体。映射器自动为实体创建实体帮助器 bean，因此该 bean 将存在用于自动装配。

现在，我们可以使用 *@QueryProvider* 注释来使用查询提供程序：![img]()

```java
@Dao
public interface UserDao {

    @QueryProvider(providerClass = UserQueryProvider.class, entityHelpers = User.class, providerMethod = "getUsersOlderThanAge")
    PagingIterable<User> getUsersOlderThan(int age);
}
复制
```

providerClass 字段指定查询提供程序类， *entityHelpers* 字段指定查询中使用的实体类*。*providerMethod 字段指定执行查询的查询提供程序类中*的*方法。

如果查询不使用任何实体，则不需要指定 *entityHelpers字段。* 如果方法名称与 DAO 接口中的方法名称相同，则也不必指定*providerMethod字段。*

### 5.4. *@GetEntity*和*@SetEntity*

有时，可能需要在 Cassandra 的核心驱动程序和映射器的操作之间切换。如果出现这样的需求，我们可以使用*@GetEntity* 和 *@SetEntity* 注解来定义在两者之间转换的方法。

让我们看看如何使用这些注释：

```java
@GetEntity
User getUser(Row row);

@SetEntity
UdtValue setUser(UdtValue udtValue, User user);
复制
```

**@GetEntity 注释告诉映射器该方法将\*Row\**转换\*为实体。**当我们想要使用核心驱动程序执行查询然后将结果转换为实体时，这会有所帮助。

**@SetEntity 注释告诉映射器该方法将实体转换为\*SettableByName\**对象\*。**第一个参数是将被更新和返回的对象。第二个参数是将提供要设置的值的实体。

如果 *SettableByName对象是**BoundStatement* 之类的语句 ，映射器会自动将参数绑定到语句并返回语句。这在使用核心驱动程序但使用实体进行其他操作的语句时很有用。

*当使用像UdtValue*这样的值对象时 ，该方法将*User*对象转换为通用*UdtValue*对象。这在使用实体进行数据库交互但使用核心驱动程序库进行结果集时很有用。

### 5.5. 柜台桌

Cassandra 中的计数器存储在单独的表中。映射器提供了一种增加计数器表中计数器值的方法。首先，让我们为计数器表创建一个实体：![img]()

```java
@Entity
public class Counter {

    @PartitionKey
    private String id;
    private long count;
  
    // getters, setters and constructors
}
复制
```

我们应该注意，一个计数器表应该只有一个计数器列和分区键。计数器列应为*long*类型。表中不能有其他数据列。

### 5.6. 递增计数器

现在，我们可以为计数器表创建一个 DAO：![img]()![img]()

```java
@Dao
public interface CounterDao {

    @Increment(entityClass = Counter.class)
    void incrementCounter(String id, long count);

    @Select
    Counter getCounterById(String id);
}
复制
```

我们先看看 *@Increment*方法。它用于创建和更新计数器。

首先，需要为实体类提供 *entityClass*属性。接下来，该方法将所有分区键列和集群键列作为参数。最后，最后一个参数将是我们要增加字段的值。

要找到要递增的列，我们可以用*@CqlName*注释最后一个参数并指定确切的列名。如果参数没有注释，映射器会查找与参数同名的字段。在这种情况下，参数名称是*count*并且映射器 在实体类中查找名称为*count 的字段。*

**计数器表的 DAO 只能有 \*@Increment\*、 \*@Select\* 和 \*@Delete\* 方法。**

映射器不允许我们使用 *@Update* 方法更新整个计数器行。我们也不能使用 *@Insert* 方法向计数器表中插入新行。如果我们尝试这样做，映射器将抛出异常。 **如果不存在，*****@Increment\*****方法 本身将创建一个新行。**

## 6.映射器接口

映射器接口是映射器的入口点。它提供了获取 DAO 实例的方法。我们可以使用 *@Mapper* 注解来创建映射器接口。对于返回 DAO 实例的方法，我们可以使用 *@DaoFactory* 注释。

让我们创建一个映射器接口：

```java
@Mapper
public interface DaoMapper {

    @DaoFactory
    UserDao getUserDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    CounterDao getUserCounterDao(@DaoKeyspace CqlIdentifier keyspace);
}
复制
```

@DaoFactory 注释创建*一个*DAO 实例。**@DaoKeyspace注释指定用于 DAO 实例的键空间\*。\***该接口还负责 DAO 实例的生命周期。DAO 实例与 Cassandra 会话具有相同的生命周期。

## 7. 测试

让我们看看如何测试映射器。我们将创建一个测试类，它将使用映射器提供的 DAO 对数据库执行操作。

让我们从创建一个测试类开始，在 Cassandra 中创建表，并设置 DAO。

要运行测试，应运行 Cassandra 数据库实例并[配置连接](https://www.baeldung.com/spring-data-cassandra-tutorial#configuration-for-cassandra)。或者，[我们可以使用 testcontainers 来设置一个临时实例。](https://www.baeldung.com/spring-data-cassandra-test-containers)

### 7.1 创建表和 DAO

在使用 DAO 之前，我们需要创建表。我们可以通过直接在 Cassandra 数据库上运行查询来创建表，也可以使用*CQLSession*以编程方式创建表。

*让我们通过在setup()*方法中执行 CQL 语句来创建表：

```java
class MapperLiveTest {

    static UserDao userDao;
    static CounterDao counterDao;

    @BeforeAll
    static void setup() {
        CqlSession session = CqlSession.builder().build();

        String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS baeldung " +
          "WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};";
        String useKeyspace = "USE baeldung;";
        String createUserTable = "CREATE TABLE IF NOT EXISTS user_profile " +
          "(id int, username text, user_age int, writetime bigint, PRIMARY KEY (id, user_age)) " +
          "WITH CLUSTERING ORDER BY (user_age DESC);";
        String createAdminTable = "CREATE TABLE IF NOT EXISTS admin_profile " +
          "(id int, username text, user_age int, role text, writetime bigint, department text, " +
          "PRIMARY KEY (id, user_age)) " +
          "WITH CLUSTERING ORDER BY (user_age DESC);";
        String createCounter = "CREATE TABLE IF NOT EXISTS counter " +
          "(id text, count counter, PRIMARY KEY (id));";

        session.execute(createKeyspace);
        session.execute(useKeyspace);
        session.execute(createUserTable);
        session.execute(createAdminTable);
        session.execute(createCounter);

        DaoMapper mapper = new DaoMapperBuilder(session).build();
        userDao = mapper.getUserDao(CqlIdentifier.fromCql("baeldung"));
        counterDao = mapper.getUserCounterDao(CqlIdentifier.fromCql("baeldung"));
    }

    // ...
}复制
```

我们已经创建了**查询来创建键空间、表和计数器表。**

我们还创建了一个 *DaoMapper*实例并从中获取了 DAO 实例。

注解处理器自动生成*DaoMapperBuilder*类。构建器将*CqlSession* 实例作为参数并返回 *DaoMapper* 实例。*上面定义的 DaoMapper*方法 提供了 DAO 实例。

### 7.2. 测试用户 DAO

让我们编写一些测试来查看调用 DAO 方法的语法。我们将从测试 *UserDao* 实例开始。

让我们创建一个用户并从数据库中检索它：

```java
@Test
void givenUser_whenInsert_thenRetrievedDuringGet() {
    User user = new User(1, "JohnDoe", 31);
    userDao.insertUser(user);
    User retrievedUser = userDao.getUserById(1);
    Assertions.assertEquals(retrievedUser.getUserName(), user.getUserName());
}
复制
```

我们创建了一个 *User对象，并使用**insertUser()*方法 将其插入到数据库中 。*然后我们使用getUserById()*方法从数据库中检索用户 并验证用户名是否相同。

让我们测试查询提供程序方法：![img]()

```java
@Test
void givenUser_whenGetUsersOlderThan_thenRetrieved() {
    User user = new User(2, "JaneDoe", 20);
    userDao.insertUser(user);
    List<User> retrievedUsers = userDao.getUsersOlderThanAge(30).all();
    Assertions.assertEquals(retrievedUsers.size(), 1);
}
复制
```

我们向数据库添加了一个新用户，然后使用 *getUsersOlderThanAge()* 方法检索了所有 30 岁以上的用户。

getUsersOlderThanAge *()* 方法返回一个 *PagingIterable* 实例。我们可以使用 *all()* 方法来检索所有结果。

该查询将只返回一个用户。

### 7.3. Testing Counter DAO

最后，让我们看看如何使用计数器。让我们创建一个递增计数器的测试：

```java
@Test
void givenCounter_whenIncrement_thenIncremented() {
    Counter users = counterDao.getCounterById("users");
    long initialCount = users != null ? users.getCount(): 0;

    counterDao.incrementCounter("users", 1);

    users = counterDao.getCounterById("users");
    long finalCount = users != null ? users.getCount(): 0;

    Assertions.assertEquals(finalCount - initialCount, 1);
}
复制
```

我们首先获取计数器的初始计数，然后将计数器加 1 并从数据库中获取最终计数。然后我们验证最终计数是否比初始计数多 1。

## 八、结论

在本文中，我们了解了 DataStax Java Driver Mapper。我们已经了解了如何使用映射器对表和计数器执行 CRUD 操作。

我们还看到了在预定义的 DAO 方法不够用时如何使用映射器来使用查询提供程序。