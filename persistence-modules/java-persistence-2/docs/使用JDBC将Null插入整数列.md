## 1. 简介

在本文中，我们将研究如何使用普通 JDBC 在数据库中存储空值。我们将从描述使用空值的原因开始，然后是几个代码示例。

## 2.使用空值

null是一个超越所有编程语言的关键字。它代表着一种特殊的价值。人们普遍认为null没有任何价值或者它不代表任何东西。在数据库列中存储空值意味着在硬盘上保留了空间。如果有合适的值可用，我们可以将其存储在该空间中。

另一种看法是null等于零或空字符串。特定上下文中的零或空白字符串可能具有含义，例如，仓库中的零项。此外，我们可以对这两个值执行sum或concat等操作。但是这些操作在处理null时没有意义。

使用空值来表示我们数据中的特殊情况有很多优点。这些优势之一是大多数数据库引擎从内部函数(例如sum或avg )中排除空值。另一方面，当我们的代码中有null时，我们可以编写特殊操作来减少缺失值。

将null带到表中也会带来一些缺点。在编写处理包含空值的数据的代码时，我们必须以不同的方式处理该数据。这会导致难看的代码、混乱和错误。此外，空值在数据库中可以具有可变长度。存储在Integer和Byte列中的null将具有不同的长度。

## 3.实施

对于我们的示例，我们将使用带有[H2 内存数据库](https://www.baeldung.com/spring-boot-h2-database)的简单 Maven 模块。不需要其他依赖项。

首先，让我们创建名为Person的 POJO 类。这个类将有四个字段。Id用作我们数据库的主键，name和lastName是字符串，年龄表示为Integer。年龄不是必填字段，可以为null：

```java
public class Person {
    private Integer id;
    private String name;
    private String lastName;
    private Integer age;
    //getters and setters
}

```

要创建反映此Java类的数据库表，我们将使用以下 SQL 查询：

```sql
CREATE TABLE Person (id INTEGER not null, name VARCHAR(50), lastName VARCHAR(50), age INTEGER, PRIMARY KEY (id));
```

有了所有这些，现在我们可以专注于我们的主要目标。要将空值设置到Integer列中，在PreparedStatement接口中有两种定义的方法。

### 3.1. 使用setNull方法

使用setNull方法，我们始终确保在执行 SQL 查询之前我们的字段值为空。这使我们在代码中具有更大的灵活性。

对于列索引，我们还必须为PreparedStatement实例提供有关基础列类型的信息。在我们的例子中，这是java.sql.Types.INTEGER。

此方法仅保留用于空值。对于任何其他，我们必须使用PreparedStatement实例的适当方法：

```java
@Test
public void givenNewPerson_whenSetNullIsUsed_thenNewRecordIsCreated() throws SQLException {
    Person person = new Person(1, "John", "Doe", null);

    try (PreparedStatement preparedStatement = DBConfig.getConnection().prepareStatement(SQL)) {
        preparedStatement.setInt(1, person.getId());
        preparedStatement.setString(2, person.getName());
        preparedStatement.setString(3, person.getLastName());
        if (person.getAge() == null) {
            preparedStatement.setNull(4, Types.INTEGER);
        }
        else {
            preparedStatement.setInt(4, person.getAge());
        }
        int noOfRows = preparedStatement.executeUpdate();

        assertThat(noOfRows, equalTo(1));
    }
}

```

如果我们不检查getAge方法是否返回null并使用null值调用setInt方法，我们将得到NullPointerException。

### 3.2. 使用setObject方法

setObject方法使我们在处理代码中丢失的数据时灵活性较低。我们可以传递我们拥有的数据，底层结构会将JavaObject类型映射到 SQL 类型。

请注意，并非所有数据库都允许在不指定 SQL 类型的情况下传递null 。例如，JDBC 驱动程序无法从null 推断出 SQL 类型。

为了安全起见，最好将 SQL 类型传递给setObject方法：

```java
@Test
public void givenNewPerson_whenSetObjectIsUsed_thenNewRecordIsCreated() throws SQLException {
    Person person = new Person(2, "John", "Doe", null);

    try (PreparedStatement preparedStatement = DBConfig.getConnection().prepareStatement(SQL)) {
        preparedStatement.setInt(1, person.getId());
        preparedStatement.setString(2, person.getName());
        preparedStatement.setString(3, person.getLastName());
        preparedStatement.setObject(4, person.getAge(), Types.INTEGER);
        int noOfRows = preparedStatement.executeUpdate();

        assertThat(noOfRows, equalTo(1));
    }
}
```

## 4. 总结

在本教程中，我们解释了数据库中空值的一些基本用法。然后我们提供了如何使用普通 JDBC将空值存储在Integer列中的示例。