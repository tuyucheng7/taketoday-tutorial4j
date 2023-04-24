## 1. 概述

在本教程中，我们将介绍DBUnit，这是一个用于在Java中**测试关系型数据库交互**的单元测试工具。

我们将看到它如何帮助我们将数据库置于已知状态并针对预期状态进行断言。

## 2. 依赖

首先，让我们将dbunit依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.dbunit</groupId>
    <artifactId>dbunit</artifactId>
    <version>2.7.0</version>
    <scope>test</scope>
</dependency>
```

我们可以在[Maven Central](https://central.sonatype.com/artifact/org.dbunit/dbunit/2.7.3)上查找最新版本。

## 3. HelloWorld示例

接下来，让我们定义一个**数据库模式schema.sql**：

```sql
CREATE TABLE IF NOT EXISTS CLIENTS
(
    `id`         int AUTO_INCREMENT NOT NULL,
    `first_name` varchar(100)       NOT NULL,
    `last_name`  varchar(100)       NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS ITEMS
(
    `id`       int AUTO_INCREMENT NOT NULL,
    `title`    varchar(100)       NOT NULL,
    `produced` date,
    `price`    float,
    PRIMARY KEY (`id`)
);
```

### 3.1 定义初始数据库内容

**DBUnit允许我们以一种简单的声明方式定义和加载我们的测试数据集**。

我们使用一个XML元素定义每个表行，其中标签名是表名，属性名和值分别映射到列名和值。可以为多个表创建行数据。我们必须实现DataSourceBasedDBTestCase的getDataSet()方法来定义初始数据集，在这里我们可以使用FlatXmlDataSetBuilder来引用我们的XML文件：

data.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<dataset>
    <CLIENTS id='1' first_name='Charles' last_name='Xavier'/>
    <ITEMS id='1' title='Grey T-Shirt' price='17.99' produced='2019-03-20'/>
    <ITEMS id='2' title='Fitted Hat' price='29.99' produced='2019-03-21'/>
    <ITEMS id='3' title='Backpack' price='54.99' produced='2019-03-22'/>
    <ITEMS id='4' title='Earrings' price='14.99' produced='2019-03-23'/>
    <ITEMS id='5' title='Socks' price='9.99'/>
</dataset>
```

### 3.2 初始化数据库连接和模式

**现在我们已经有了模式，我们必须初始化我们的数据库**。

我们必须扩展DataSourceBasedDBTestCase类并在其getDataSource()方法中初始化数据库模式：

DataSourceDBUnitTest.java:

```java
public class DataSourceDBUnitTest extends DataSourceBasedDBTestCase {
    @Override
    protected DataSource getDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:default;DB_CLOSE_DELAY=-1;init=runscript from 'classpath:schema.sql'");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(getClass().getClassLoader()
              .getResourceAsStream("data.xml"));
    }
}
```

在这里，我们在其连接字符串中将SQL文件传递给H2内存数据库。如果我们想在其他数据库上进行测试，我们需要为其提供自定义实现。

请记住，在我们的示例中，**DBUnit将在每个测试方法执行之前使用给定的测试数据重新初始化数据库**。

有多种方法可以通过getSetUpOperation和getTearDownOperation进行配置：

```java
@Override
protected DatabaseOperation getSetUpOperation() {
    return DatabaseOperation.REFRESH;
}

@Override
protected DatabaseOperation getTearDownOperation() {
    return DatabaseOperation.DELETE_ALL;
}
```

REFRESH操作告诉DBUnit刷新其所有数据。这将确保所有缓存都被清除，并且我们的单元测试不会受到另一个单元测试的影响。DELETE_ALL操作确保在每个单元测试结束时删除所有数据。在我们的例子中，我们告诉DBUnit在设置期间，使用getSetUpOperation方法实现我们将刷新所有缓存。最后，我们使用getTearDownOperation方法实现告诉DBUnit在拆卸操作期间删除所有数据。

### 3.3 比较预期状态和实际状态

现在，让我们检查一下我们的实际测试用例。对于第一个测试，我们将保持简单-我们将加载我们预期的数据集并将其与从我们的数据库连接中检索到的数据集进行比较：

```java
@Test
public void givenDataSetEmptySchema_whenDataSetCreated_thenTablesAreEqual() throws Exception {
    IDataSet expectedDataSet = getDataSet();
    ITable expectedTable = expectedDataSet.getTable("CLIENTS");
    IDataSet databaseDataSet = getConnection().createDataSet();
    ITable actualTable = databaseDataSet.getTable("CLIENTS");
    assertEquals(expectedTable, actualTable);
}
```

## 4. 深入研究断言

在上一节中，我们看到了一个将表的实际内容与预期数据集进行比较的基本示例。现在我们将了解DBUnit对自定义数据断言的支持。

### 4.1 使用SQL查询断言

**检查实际状态的一种直接方法是使用SQL查询**。

在此示例中，我们将在CLIENTS表中插入一条新记录，然后验证新创建的行的内容。我们在一个**单独的XML文件**中定义了预期的输出，并通过SQL查询提取了实际的行值：

```java
@Test
public void givenDataSet_whenInsert_thenTableHasNewClient() throws Exception {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream("dbunit/expected-user.xml")) {
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
        ITable expectedTable = expectedDataSet.getTable("CLIENTS");
        Connection conn = getDataSource().getConnection();

        conn.createStatement()
            .executeUpdate(
            "INSERT INTO CLIENTS (first_name, last_name) VALUES ('John', 'Jansen')");
        ITable actualData = getConnection()
            .createQueryTable(
                "result_name",
                "SELECT * FROM CLIENTS WHERE last_name='Jansen'");

        assertEqualsIgnoreCols(expectedTable, actualData, new String[] { "id" });
    }
}
```

DBTestCase祖先类的getConnection()方法返回数据源连接(一个IDatabaseConnection实例)的特定于DBUnit的表示。**IDatabaseConnection的createQueryTable()方法可用于从数据库中获取实际数据**，以便使用Assertion.assertEquals()方法与预期的数据库状态进行比较。传递给createQueryTable()的SQL查询就是我们要测试的查询。它返回一个Table实例，我们用它来进行断言。

### 4.2 忽略列

**有时在数据库测试中，我们希望忽略实际表的某些列**。这些通常是我们无法严格控制的自动生成的值，例如**生成的主键或当前时间戳**。

我们可以通过**在SQL查询中省略SELECT子句中的列**来做到这一点，但是DBUnit提供了一个更方便的实用程序来实现这一点。**使用DefaultColumnFilter类的静态方法，我们可以通过排除某些列从现有的ITable实例创建一个新的实例**，如下所示：

```java
@Test
public void givenDataSet_whenInsert_thenGetResultsAreStillEqualIfIgnoringColumnsWithDifferentProduced() throws Exception {
    Connection connection = tester.getConnection().getConnection();
    String[] excludedColumns = { "id", "produced" };
    try (InputStream is = getClass().getClassLoader().getResourceAsStream("dbunit/expected-ignoring-registered_at.xml")) {
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
        ITable expectedTable = excludedColumnsTable(expectedDataSet.getTable("ITEMS"), excludedColumns);

        connection.createStatement()
            .executeUpdate("INSERT INTO ITEMS (title, price, produced)  VALUES('Necklace', 199.99, now())");

        IDataSet databaseDataSet = tester.getConnection().createDataSet();
        ITable actualTable = excludedColumnsTable(databaseDataSet.getTable("ITEMS"), excludedColumns);

        assertEquals(expectedTable, actualTable);
    }
}
```

### 4.3 多个故障

**如果DBUnit找到不正确的值，那么它会立即抛出一个AssertionError**。

在特定情况下，我们可以使用DiffCollectingFailureHandler类，我们可以将其作为第三个参数传递给Assertion.assertEquals()方法。

这个失败处理程序将收集所有失败而不是在第一个失败时停止，这意味着如果我们**使用DiffCollectingFailureHandler，Assertion.assertEquals()方法将始终成功**。因此，我们必须以编程方式检查处理程序是否发现任何错误：

```java
@Test
public void givenDataSet_whenInsertUnexpectedData_thenFailOnAllUnexpectedValues() throws Exception {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream("dbunit/expected-multiple-failures.xml")) {
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(is);
        ITable expectedTable = expectedDataSet.getTable("ITEMS");
        Connection conn = getDataSource().getConnection();
        DiffCollectingFailureHandler collectingHandler = new DiffCollectingFailureHandler();

        conn.createStatement()
            .executeUpdate("INSERT INTO ITEMS (title, price) VALUES ('Battery', '1000000')");
        ITable actualData = getConnection().createDataSet().getTable("ITEMS");

        assertEquals(expectedTable, actualData, collectingHandler);
        if (!collectingHandler.getDiffList().isEmpty()) {
            String message = (String) collectingHandler.getDiffList()
                .stream()
                .map(d -> formatDifference((Difference) d))
                .collect(joining("\n"));
            logger.error(() -> message);
        }
    }
}

private static String formatDifference(Difference diff) {
    return "expected value in " + diff.getExpectedTable()
        .getTableMetaData()
        .getTableName() + "." + 
        diff.getColumnName() + " row " + 
        diff.getRowIndex() + ":" + 
        diff.getExpectedValue() + ", but was: " + 
        diff.getActualValue();
}
```

此外，处理程序以Difference实例的形式提供失败，这使我们能够格式化错误。

运行测试后，我们得到一个格式化的报告：

```shell
java.lang.AssertionError: expected value in ITEMS.price row 5:199.99, but was: 1000000.0
expected value in ITEMS.produced row 5:2019-03-23, but was: null
expected value in ITEMS.title row 5:Necklace, but was: Battery

	at cn.tuyucheng.taketoday.dbunit.DataSourceDBUnitTest.givenDataSet_whenInsertUnexpectedData_thenFailOnAllUnexpectedValues(DataSourceDBUnitTest.java:91)
```

需要注意的是，此时我们预计新商品的价格为199.99，但实际价格为1000000.0。然后我们看到生产日期是2019-03-23，但最后是空的。最后，预期的物品是Necklace，而我们得到了Battery。

## 5. 总结

在本文中，我们看到了DBUnit如何提供一种定义测试数据的声明方式来测试Java应用程序的数据访问层。