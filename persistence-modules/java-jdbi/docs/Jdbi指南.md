## 1. 简介

在本文中，我们将了解如何使用[jdbi](http://jdbi.org/)查询关系数据库。

Jdbi 是一个开源的Java库(Apache 许可)，它使用[lambda 表达式](https://www.baeldung.com/java-8-lambda-expressions-tips)和[反射](https://www.baeldung.com/java-reflection)来提供比[JDBC](https://www.baeldung.com/java-jdbc)更友好、更高级别的接口来访问数据库。

然而，Jdbi 不是 ORM；尽管它有一个可选的 SQL 对象映射模块，但它没有与附加对象的会话、数据库独立层以及典型 ORM 的任何其他附加功能。

## 2. Jdbi设置

Jdbi 被组织成一个核心模块和几个可选模块。

要开始，我们只需要在我们的依赖项中包含核心模块：

```xml
<dependencies>
    <dependency>
        <groupId>org.jdbi</groupId>
        <artifactId>jdbi3-core</artifactId>
        <version>3.1.0</version>
    </dependency>
</dependencies>
```

在本文中，我们将展示使用 HSQL 数据库的示例：

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.4.0</version>
    <scope>test</scope>
</dependency>
```

我们可以在 Maven Central 上找到最新版本的[jdbi3-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.jdbi" AND a%3A"jdbi3-core")、[HSQLDB](https://search.maven.org/classic/#search|gav|1|g%3A"org.hsqldb" AND a%3A"hsqldb")和其他 Jdbi 模块。

## 3. 连接数据库

首先，我们需要连接到数据库。为此，我们必须指定连接参数。

起点是Jdbi类：

```java
Jdbi jdbi = Jdbi.create("jdbc:hsqldb:mem:testDB", "sa", "");
```

在这里，我们指定了连接 URL、用户名，当然还有密码。

### 3.1. 附加参数

如果我们需要提供其他参数，我们使用接受Properties对象的重载方法：

```java
Properties properties = new Properties();
properties.setProperty("username", "sa");
properties.setProperty("password", "");
Jdbi jdbi = Jdbi.create("jdbc:hsqldb:mem:testDB", properties);
```

在这些示例中，我们将Jdbi实例保存在局部变量中。那是因为我们将使用它向数据库发送语句和查询。

事实上，仅调用create不会与数据库建立任何连接。它只是保存连接参数供以后使用。

### 3.2. 使用数据源

如果我们使用DataSource连接到数据库，通常情况下，我们可以使用适当的创建重载：

```java
Jdbi jdbi = Jdbi.create(datasource);
```

### 3.3. 使用句柄

与数据库的实际连接由Handle类的实例表示。

使用句柄并让它们自动关闭的最简单方法是使用 lambda 表达式：

```java
jdbi.useHandle(handle -> {
    doStuffWith(handle);
});
```

当我们不必返回值时，我们调用useHandle 。

否则，我们使用withHandle：

```java
jdbi.withHandle(handle -> {
    return computeValue(handle);
});
```

虽然不推荐，但也可以手动打开连接句柄；在这种情况下，我们必须在完成后关闭它：

```java
Jdbi jdbi = Jdbi.create("jdbc:hsqldb:mem:testDB", "sa", "");
try (Handle handle = jdbi.open()) {
    doStuffWith(handle);
}
```

幸运的是，正如我们所见，Handle实现了 Closeable，因此它可以与[try-with-resources 一起](https://www.baeldung.com/java-try-with-resources)使用。

## 4. 简单语句

现在我们知道如何获取连接，让我们看看如何使用它。

在本节中，我们将创建一个将在整篇文章中使用的简单表格。

要向数据库发送诸如创建表之类的语句，我们使用execute方法：

```java
handle.execute(
  "create table project "
  + "(id integer identity, name varchar(50), url varchar(100))");
```

execute返回受语句影响的行数：

```java
int updateCount = handle.execute(
  "insert into project values "
  + "(1, 'tutorials', 'github.com/eugenp/tutorials')");

assertEquals(1, updateCount);
```

实际上，execute 只是一个方便的方法。

我们将在后面的部分中查看更复杂的用例，但在此之前，我们需要了解如何从数据库中提取结果。

## 5.查询数据库

从数据库中生成结果的最直接的表达式是 SQL 查询。

要使用 Jdbi Handle 发出查询，我们至少必须：

1.  创建查询
2.  选择如何表示每一行
3.  迭代结果

我们现在来看看上面的每一点。

### 5.1. 创建查询

不出所料，Jdbi 将查询表示为Query类的实例。

我们可以从句柄中获取一个：

```java
Query query = handle.createQuery("select  from project");
```

### 5.2. 映射结果

Jdbi 从 JDBC ResultSet中抽象出来，后者具有相当繁琐的 API。

因此，它提供了多种可能性来访问由查询或返回结果的某些其他语句产生的列。我们现在将看到最简单的。

我们可以将每一行表示为一张地图：

```java
query.mapToMap();
```

地图的键将是选定的列名称。

或者，当查询返回单个列时，我们可以将其映射到所需的Java类型：

```java
handle.createQuery("select name from project").mapTo(String.class);
```

Jdbi 为许多常用类内置了映射器。特定于某些库或数据库系统的那些在单独的模块中提供。

当然，我们也可以定义并注册我们的映射器。我们将在后面的部分中讨论它。

最后，我们可以将行映射到 bean 或其他一些自定义类。同样，我们将在专门的部分中看到更高级的选项。

### 5.3. 迭代结果

一旦我们决定了如何通过调用适当的方法来映射结果，我们就会收到一个ResultIterable对象。

然后我们可以使用它来迭代结果，一次一行。

在这里，我们将看看最常见的选项。

我们只能将结果累积在一个列表中：

```java
List<Map<String, Object>> results = query.mapToMap().list();
```

或者到另一种集合类型：

```java
List<String> results = query.mapTo(String.class).collect(Collectors.toSet());
```

或者我们可以将结果作为流进行迭代：

```java
query.mapTo(String.class).useStream((Stream<String> stream) -> {
    doStuffWith(stream)
});
```

在这里，为了清楚起见，我们显式地键入了流变量，但没有必要这样做。

### 5.4. 获得单一结果

作为一种特殊情况，当我们期望或只对一行感兴趣时，我们有几个专用方法可用。

如果我们最多想要一个结果，我们可以使用findFirst：

```java
Optional<Map<String, Object>> first = query.mapToMap().findFirst();
```

如我们所见，它返回一个Optional值，该值仅在查询返回至少一个结果时才存在。

如果查询返回多行，则只返回第一行。

相反，如果我们想要一个且只有一个结果，我们使用findOnly：

```java
Date onlyResult = query.mapTo(Date.class).findOnly();
```

最后，如果结果为零个或多个，findOnly将抛出IllegalStateException。

## 6.绑定参数

通常，查询具有固定部分和参数化部分。这有几个优点，包括：

-   安全性：通过避免字符串连接，我们可以防止 SQL 注入
-   轻松：我们不必记住时间戳等复杂数据类型的确切语法
-   性能：查询的静态部分可以被解析一次并缓存

Jdbi 支持位置参数和命名参数。

我们在查询或语句中插入位置参数作为问号：

```java
Query positionalParamsQuery =
  handle.createQuery("select  from project where name = ?");
```

相反，命名参数以冒号开头：

```java
Query namedParamsQuery =
  handle.createQuery("select  from project where url like :pattern");
```

在任何一种情况下，要设置参数的值，我们都使用bind方法的一种变体：

```java
positionalParamsQuery.bind(0, "tutorials");
namedParamsQuery.bind("pattern", "%github.com/eugenp/%");
```

请注意，与 JDBC 不同，索引从 0 开始。

### 6.1. 一次绑定多个命名参数

我们还可以使用一个对象将多个命名参数绑定在一起。

假设我们有这个简单的查询：

```java
Query query = handle.createQuery(
  "select id from project where name = :name and url = :url");
Map<String, String> params = new HashMap<>();
params.put("name", "REST with Spring");
params.put("url", "github.com/eugenp/REST-With-Spring");
```

然后，例如，我们可以使用地图：

```java
query.bindMap(params);
```

或者我们可以以各种方式使用一个对象。例如，这里我们绑定一个遵循 JavaBean 约定的对象：

```java
query.bindBean(paramsBean);
```

但我们也可以绑定对象的字段或方法；对于所有受支持的选项，请参阅[Jdbi 文档](http://jdbi.org/#_binding_arguments)。

## 7.发布更复杂的声明

现在我们已经看到了查询、值和参数，我们可以回到语句并应用相同的知识。

回想一下我们之前看到的execute方法只是一个方便的快捷方式。

事实上，与查询类似，DDL 和 DML 语句表示为Update 类的实例。

我们可以通过在句柄上调用方法createUpdate来获得一个：

```java
Update update = handle.createUpdate(
  "INSERT INTO PROJECT (NAME, URL) VALUES (:name, :url)");
```

然后，在Update上，我们拥有我们在Query中拥有的所有绑定方法，因此第 6 节也适用于更新。url

当我们调用 surprise 时执行语句：

```java
int rows = update.execute();
```

正如我们已经看到的，它返回受影响的行数。

### 7.1. 提取自动递增的列值

作为一种特殊情况，当我们有一个带有自动生成列(通常是自动递增或序列)的插入语句时，我们可能希望获得生成的值。

然后，我们不调用execute，而是调用executeAndReturnGeneratedKeys：

```java
Update update = handle.createUpdate(
  "INSERT INTO PROJECT (NAME, URL) "
  + "VALUES ('tutorials', 'github.com/eugenp/tutorials')");
ResultBearing generatedKeys = update.executeAndReturnGeneratedKeys();
```

ResultBearing与我们之前看到的Query类实现的接口相同，因此我们已经知道如何使用它：

```java
generatedKeys.mapToMap()
  .findOnly().get("id");
```

## 8. 交易

每当我们必须将多个语句作为单个原子操作执行时，我们就需要一个事务。

与连接句柄一样，我们通过调用带有闭包的方法来引入事务：

```java
handle.useTransaction((Handle h) -> {
    haveFunWith(h);
});
```

而且，与句柄一样，事务在闭包返回时自动关闭。

但是，我们必须在返回之前提交或回滚事务：

```java
handle.useTransaction((Handle h) -> {
    h.execute("...");
    h.commit();
});
```

但是，如果闭包抛出异常，Jdbi 会自动回滚事务。

与句柄一样，如果我们想从闭包中返回一些东西，我们有一个专用方法inTransaction ：

```java
handle.inTransaction((Handle h) -> {
    h.execute("...");
    h.commit();
    return true;
});
```

### 8.1. 手动交易管理

虽然在一般情况下不推荐这样做，但我们也可以手动开始和关闭事务：

```java
handle.begin();
// ...
handle.commit();
handle.close();
```

## 9. 总结和进一步阅读

在本教程中，我们介绍了 Jdbi 的核心：查询、语句和事务。

我们遗漏了一些高级功能，例如自定义行和列映射以及批处理。

我们也没有讨论任何可选模块，最值得注意的是 SQL 对象扩展。

[Jdbi 文档](http://jdbi.org/)中详细介绍了所有内容。