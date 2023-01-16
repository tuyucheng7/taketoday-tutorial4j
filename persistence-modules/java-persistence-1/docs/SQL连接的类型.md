## 1. 简介

在本教程中，我们将展示不同类型的 SQL 连接以及如何在Java中轻松实现它们。

## 2. 定义模型

让我们从创建两个简单的表开始：

```sql
CREATE TABLE AUTHOR
(
  ID int NOT NULL PRIMARY KEY,
  FIRST_NAME varchar(255),
  LAST_NAME varchar(255)
);

CREATE TABLE ARTICLE
(
  ID int NOT NULL PRIMARY KEY,
  TITLE varchar(255) NOT NULL,
  AUTHOR_ID int,
  FOREIGN KEY(AUTHOR_ID) REFERENCES AUTHOR(ID)
);

```

并用一些测试数据填充它们：

```sql
INSERT INTO AUTHOR VALUES 
(1, 'Siena', 'Kerr'),
(2, 'Daniele', 'Ferguson'),
(3, 'Luciano', 'Wise'),
(4, 'Jonas', 'Lugo');

INSERT INTO ARTICLE VALUES
(1, 'First steps in Java', 1),
(2, 'SpringBoot tutorial', 1),
(3, 'Java 12 insights', null),
(4, 'SQL JOINS', 2),
(5, 'Introduction to Spring Security', 3);
```

请注意，在我们的示例数据集中，并非所有作者都有文章，反之亦然。这将在我们的示例中发挥重要作用，稍后我们将看到。

我们还定义一个 POJO，我们将在整个教程中使用它来存储 JOIN 操作的结果：

```java
class ArticleWithAuthor {

    private String title;
    private String authorFirstName;
    private String authorLastName;

    // standard constructor, setters and getters
}
```

在我们的示例中，我们将从 ARTICLE 表中提取标题，并从 AUTHOR 表中提取作者数据。

## 三、配置

对于我们的示例，我们将使用在端口 5432 上运行的外部 PostgreSQL 数据库。除了 MySQL 或 H2 均不支持的 FULL JOIN 之外，所有提供的代码段都应适用于任何 SQL 提供程序。

对于我们的Java实现，我们需要一个[PostgreSQL 驱动程序](https://search.maven.org/classic/#search|ga|1|g%3A"org.postgresql" AND a%3A"postgresql")：

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.5</version>
    <scope>test</scope>
</dependency>
```

让我们首先配置一个 java.sql.Connection来使用我们的数据库：

```java
Class.forName("org.postgresql.Driver");
Connection connection = DriverManager.
  getConnection("jdbc:postgresql://localhost:5432/myDb", "user", "pass");
```

接下来，让我们创建一个 DAO 类和一些实用方法：

```java
class ArticleWithAuthorDAO {

    private final Connection connection;

    // constructor

    private List<ArticleWithAuthor> executeQuery(String query) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            return mapToList(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
            return new ArrayList<>();
    }

    private List<ArticleWithAuthor> mapToList(ResultSet resultSet) throws SQLException {
        List<ArticleWithAuthor> list = new ArrayList<>();
        while (resultSet.next()) {
            ArticleWithAuthor articleWithAuthor = new ArticleWithAuthor(
              resultSet.getString("TITLE"),
              resultSet.getString("FIRST_NAME"),
              resultSet.getString("LAST_NAME")
            );
            list.add(articleWithAuthor);
        }
        return list;
    }
}
```

在本文中，我们不会深入探讨有关使用 [ResultSet](https://www.baeldung.com/jdbc-resultset)、Statement和 Connection 的详细信息。[我们的JDBC](https://www.baeldung.com/java-jdbc) 相关文章涵盖了这些主题 。

让我们在下面的部分开始探索 SQL 连接。

## 4.内连接

让我们从最简单的连接类型开始。INNER JOIN 是一种从两个表中选择与提供的条件匹配的行的操作。查询至少由三部分组成：选择列、连接表和连接条件。

牢记这一点，语法本身变得非常简单：

```sql
SELECT ARTICLE.TITLE, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME
  FROM ARTICLE INNER JOIN AUTHOR 
  ON AUTHOR.ID=ARTICLE.AUTHOR_ID
```

我们还可以将INNER JOIN 的结果说明为相交集的公共部分：

[![内部联接](https://www.baeldung.com/wp-content/uploads/2019/04/inner_join.png)](https://www.baeldung.com/wp-content/uploads/2019/04/inner_join.png)

现在让我们在ArticleWithAuthorDAO类中实现 INNER JOIN 的方法：

```java
List<ArticleWithAuthor> articleInnerJoinAuthor() {
    String query = "SELECT ARTICLE.TITLE, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME "
      + "FROM ARTICLE INNER JOIN AUTHOR ON AUTHOR.ID=ARTICLE.AUTHOR_ID";
    return executeQuery(query);
}
```

并测试它：

```java
@Test
public void whenQueryWithInnerJoin_thenShouldReturnProperRows() {
    List<ArticleWithAuthor> articleWithAuthorList = articleWithAuthorDAO.articleInnerJoinAuthor();

    assertThat(articleWithAuthorList).hasSize(4);
    assertThat(articleWithAuthorList)
      .noneMatch(row -> row.getAuthorFirstName() == null || row.getTitle() == null);
}
```

正如我们之前提到的，INNER JOIN 根据提供的条件仅选择公共行。查看我们的插页，我们看到我们有一篇没有作者的文章和一位没有文章的作者。这些行被跳过，因为它们不满足提供的条件。结果，我们检索了四个连接的结果，它们都没有空的作者数据或空的标题。

## 5.左连接

接下来，让我们关注 LEFT JOIN。这种连接从第一个表中选择所有行并匹配第二个表中的相应行。因为当没有匹配时，列被填充为空值。

在深入研究Java实现之前，让我们看一下 LEFT JOIN 的图形表示：

[![左连接](https://www.baeldung.com/wp-content/uploads/2019/04/left_join.png)](https://www.baeldung.com/wp-content/uploads/2019/04/left_join.png)

在这种情况下，LEFT JOIN 的结果包括代表第一个表的集合中的每条记录以及第二个表中的相交值。

现在，让我们转到Java实现：

```java
List<ArticleWithAuthor> articleLeftJoinAuthor() {
    String query = "SELECT ARTICLE.TITLE, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME "
      + "FROM ARTICLE LEFT JOIN AUTHOR ON AUTHOR.ID=ARTICLE.AUTHOR_ID";
    return executeQuery(query);
}
```

与前面示例的唯一区别是我们使用了 LEFT 关键字而不是 INNER 关键字。

在我们测试我们的 LEFT JOIN 方法之前，让我们再次看一下我们的插入。在这种情况下，我们将从 ARTICLE 表中接收所有记录，并从 AUTHOR 表中接收它们的匹配行。正如我们之前提到的，并非每篇文章都有作者，因此我们希望用 空值代替作者数据：

```java
@Test
public void whenQueryWithLeftJoin_thenShouldReturnProperRows() {
    List<ArticleWithAuthor> articleWithAuthorList = articleWithAuthorDAO.articleLeftJoinAuthor();

    assertThat(articleWithAuthorList).hasSize(5);
    assertThat(articleWithAuthorList).anyMatch(row -> row.getAuthorFirstName() == null);
}
```

## 6.右加入

RIGHT JOIN 与 LEFT JOIN 非常相似，但它返回第二个表中的所有行并匹配第一个表中的行。与 LEFT JOIN 的情况一样，空匹配被空值替换。

这种连接的图形表示是我们为 LEFT JOIN 说明的图形表示：

[![右连接](https://www.baeldung.com/wp-content/uploads/2019/04/right_join.png)](https://www.baeldung.com/wp-content/uploads/2019/04/right_join.png)

让我们用Java实现 RIGHT JOIN：

```java
List<ArticleWithAuthor> articleRightJoinAuthor() {
    String query = "SELECT ARTICLE.TITLE, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME "
      + "FROM ARTICLE RIGHT JOIN AUTHOR ON AUTHOR.ID=ARTICLE.AUTHOR_ID";
    return executeQuery(query);
}
```

再一次，让我们看看我们的测试数据。由于此连接操作从第二个表中检索所有记录，我们希望检索五行，并且因为并非每个作者都已经写过一篇文章，所以我们希望TITLE 列中有一些空值：

```java
@Test
public void whenQueryWithRightJoin_thenShouldReturnProperRows() {
    List<ArticleWithAuthor> articleWithAuthorList = articleWithAuthorDAO.articleRightJoinAuthor();

    assertThat(articleWithAuthorList).hasSize(5);
    assertThat(articleWithAuthorList).anyMatch(row -> row.getTitle() == null);
}
```

## 7. 全外连接

这个连接操作可能是最棘手的一个。FULL JOIN 从第一个和第二个表中选择所有行，无论是否满足条件。

我们还可以将相同的想法表示为来自每个相交集的所有值：

[![全加入](https://www.baeldung.com/wp-content/uploads/2019/04/full_join.png)](https://www.baeldung.com/wp-content/uploads/2019/04/full_join.png)

让我们看一下Java实现：

```java
List<ArticleWithAuthor> articleOuterJoinAuthor() {
    String query = "SELECT ARTICLE.TITLE, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME "
      + "FROM ARTICLE FULL JOIN AUTHOR ON AUTHOR.ID=ARTICLE.AUTHOR_ID";
    return executeQuery(query);
}
```

现在，我们可以测试我们的方法：

```java
@Test
public void whenQueryWithFullJoin_thenShouldReturnProperRows() {
    List<ArticleWithAuthor> articleWithAuthorList = articleWithAuthorDAO.articleOuterJoinAuthor();

    assertThat(articleWithAuthorList).hasSize(6);
    assertThat(articleWithAuthorList).anyMatch(row -> row.getTitle() == null);
    assertThat(articleWithAuthorList).anyMatch(row -> row.getAuthorFirstName() == null);
}
```

再一次，让我们看一下测试数据。我们有五篇不同的文章，其中一篇没有作者，还有四位作者，其中一篇没有指定的文章。作为 FULL JOIN 的结果，我们期望检索六行。其中四个是相互匹配的，其余两个不是。出于这个原因，我们还假设至少有一行在 AUTHOR 数据列中具有空值，并且在 TITLE 列中至少有一行具有空值。

## 八. 总结

在本文中，我们探索了 SQL 连接的基本类型。我们查看了四种类型的连接示例以及如何在Java中实现它们。