## 1. 概述

[之前](https://www.baeldung.com/apache-cayenne-orm)，我们一直专注于如何开始使用 Apache Cayenne。

在本文中，我们将介绍如何使用 ORM 编写简单和高级查询。

## 2.设置

该设置类似于上一篇文章中使用的设置。

此外，在每次测试之前，我们都会保存三位作者，并在最后删除他们：

-   保罗泽维尔
-   保罗·史密斯
-   薇琪萨拉

## 3.对象选择

让我们从简单开始，看看我们如何获得名字中包含“Paul”的所有作者：

```java
@Test
public void whenContainsObjS_thenWeGetOneRecord() {
    List<Author> authors = ObjectSelect.query(Author.class)
      .where(Author.NAME.contains("Paul"))
      .select(context);

    assertEquals(authors.size(), 1);
}
```

接下来，让我们看看如何在作者姓名列上应用不区分大小写的 LIKE 查询类型：

```java
@Test
void whenLikeObjS_thenWeGetTwoAuthors() {
    List<Author> authors = ObjectSelect.query(Author.class)
      .where(Author.NAME.likeIgnoreCase("Paul%"))
      .select(context);

    assertEquals(authors.size(), 2);
}
```

接下来，endsWith()表达式将只返回一条记录，因为只有一位作者具有匹配的姓名：

```java
@Test
void whenEndsWithObjS_thenWeGetOrderedAuthors() {
    List<Author> authors = ObjectSelect.query(Author.class)
      .where(Author.NAME.endsWith("Sarra"))
      .select(context);
    Author firstAuthor = authors.get(0);

    assertEquals(authors.size(), 1);
    assertEquals(firstAuthor.getName(), "Vicky Sarra");
}
```

一个更复杂的方法是查询名字在列表中的作者：

```java
@Test
void whenInObjS_thenWeGetAuthors() {
    List names = Arrays.asList(
      "Paul Xavier", "pAuL Smith", "Vicky Sarra");
 
    List<Author> authors = ObjectSelect.query(Author.class)
      .where(Author.NAME.in(names))
      .select(context);

    assertEquals(authors.size(), 3);
}
```

nin一个是相反的，这里只有“Vicky”会出现在结果中：

```java
@Test
void whenNinObjS_thenWeGetAuthors() {
    List names = Arrays.asList(
      "Paul Xavier", "pAuL Smith");
    List<Author> authors = ObjectSelect.query(Author.class)
      .where(Author.NAME.nin(names))
      .select(context);
    Author author = authors.get(0);

    assertEquals(authors.size(), 1);
    assertEquals(author.getName(), "Vicky Sarra");
}
```

请注意，以下这两个代码是相同的，因为它们都将创建具有相同参数的相同类型的表达式：

```java
Expression qualifier = ExpressionFactory
  .containsIgnoreCaseExp(Author.NAME.getName(), "Paul");
Author.NAME.containsIgnoreCase("Paul");
```

以下是[Expression](https://cayenne.apache.org/docs/4.0/api/org/apache/cayenne/exp/Expression.html) 和[ExpressionFactory](https://cayenne.apache.org/docs/4.0/api/org/apache/cayenne/exp/ExpressionFactory.html)类中一些可用表达式的列表：

-   likeExp：用于构建 LIKE 表达式
-   likeIgnoreCaseExp：用于构建 LIKE_IGNORE_CASE 表达式
-   containsExp：LIKE 查询的表达式，其模式匹配字符串中的任意位置
-   containsIgnoreCaseExp ：与containsExp相同，但使用不区分大小写的方法
-   startsWithExp：模式应该匹配字符串的开头
-   startsWithIgnoreCaseExp：类似于startsWithExp但使用不区分大小写的方法
-   endsWithExp：匹配字符串结尾的表达式
-   endsWithIgnoreCaseExp：使用不区分大小写的方法匹配字符串结尾的表达式
-   expTrue：用于布尔真表达式
-   expFalse：布尔假表达式
-   andExp：用于使用and运算符链接两个表达式
-   orExp：使用or运算符链接两个表达式

文章代码源中有更多笔试，请查看[Github](https://github.com/eugenp/tutorials/tree/master/persistence-modules/apache-cayenne)仓库。

## 4.选择查询

它是用户应用程序中使用最广泛的查询类型。SelectQuery描述了一个简单而强大的 API，它的行为类似于 SQL 语法，但仍然使用Java对象和方法，后跟构建器模式以构建更复杂的表达式。

在这里，我们讨论的是一种表达式语言，我们使用表达式(构建表达式)也就是限定符和排序(对结果进行排序)类构建查询，这些类接下来由 ORM 转换为本机 SQL。

为了在行动中看到这一点，我们已经将一些测试放在一起，这些测试在实践中展示了如何构建一些表达式和排序数据。

让我们应用一个 LIKE 查询来获取名字如“Paul”的作者：

```java
@Test
void whenLikeSltQry_thenWeGetOneAuthor() {
    Expression qualifier 
      = ExpressionFactory.likeExp(Author.NAME.getName(), "Paul%");
    SelectQuery query 
      = new SelectQuery(Author.class, qualifier);
    
    List<Author> authorsTwo = context.performQuery(query);

    assertEquals(authorsTwo.size(), 1);
}
```

这意味着如果你不向查询提供任何表达式 ( SelectQuery )，结果将是 Author 表的所有记录。

可以使用containsIgnoreCaseExp表达式执行类似的查询，以获取名称中包含 Paul 的所有作者，而不考虑字母的大小写：

```java
@Test
void whenCtnsIgnorCaseSltQry_thenWeGetTwoAuthors() {
    Expression qualifier = ExpressionFactory
      .containsIgnoreCaseExp(Author.NAME.getName(), "Paul");
    SelectQuery query 
      = new SelectQuery(Author.class, qualifier);
    
    List<Author> authors = context.performQuery(query);

    assertEquals(authors.size(), 2);
}
```

同样，让我们以不区分大小写的方式 ( containsIgnoreCaseExp ) 获取名称中包含“Paul”的作者，名称以字母 h结尾 ( endsWithExp )：

```java
@Test
void whenCtnsIgnorCaseEndsWSltQry_thenWeGetTwoAuthors() {
    Expression qualifier = ExpressionFactory
      .containsIgnoreCaseExp(Author.NAME.getName(), "Paul")
      .andExp(ExpressionFactory
        .endsWithExp(Author.NAME.getName(), "h"));
    SelectQuery query = new SelectQuery(
      Author.class, qualifier);
    List<Author> authors = context.performQuery(query);

    Author author = authors.get(0);

    assertEquals(authors.size(), 1);
    assertEquals(author.getName(), "pAuL Smith");
}
```

可以使用Ordering类执行升序：

```java
@Test
void whenAscOrdering_thenWeGetOrderedAuthors() {
    SelectQuery query = new SelectQuery(Author.class);
    query.addOrdering(Author.NAME.asc());
 
    List<Author> authors = query.select(context);
    Author firstAuthor = authors.get(0);

    assertEquals(authors.size(), 3);
    assertEquals(firstAuthor.getName(), "Paul Xavier");
}
```

这里不使用query.addOrdering(Author.NAME.asc())，我们也可以只使用SortOrder类来获取升序：

```java
query.addOrdering(Author.NAME.getName(), SortOrder.ASCENDING);
```

相对有降序排列：

```java
@Test
void whenDescOrderingSltQry_thenWeGetOrderedAuthors() {
    SelectQuery query = new SelectQuery(Author.class);
    query.addOrdering(Author.NAME.desc());

    List<Author> authors = query.select(context);
    Author firstAuthor = authors.get(0);

    assertEquals(authors.size(), 3);
    assertEquals(firstAuthor.getName(), "pAuL Smith");
}
```

正如我们在前面的例子中看到的——另一种设置这种顺序的方法是：

```java
query.addOrdering(Author.NAME.getName(), SortOrder.DESCENDING);
```

## 5.SQL模板

SQLTemplate也是我们可以与 Cayenne 一起使用而不使用对象样式查询的一种替代方法。

使用SQLTemplate构建查询与使用某些参数编写本机 SQL 语句直接相关。让我们实现一些简单的例子。

以下是我们如何在每次测试后删除所有作者：

```java
@After
void deleteAllAuthors() {
    SQLTemplate deleteAuthors = new SQLTemplate(
      Author.class, "delete from author");
    context.performGenericQuery(deleteAuthors);
}
```

要找到所有记录的作者，我们只需要应用 SQL 查询select  from Author我们将直接看到结果是正确的，因为我们正好有三个保存的作者：

```java
@Test
void givenAuthors_whenFindAllSQLTmplt_thenWeGetThreeAuthors() {
    SQLTemplate select = new SQLTemplate(
      Author.class, "select  from Author");
    List<Author> authors = context.performQuery(select);

    assertEquals(authors.size(), 3);
}
```

接下来，让我们获取名为“Vicky Sarra”的作者：

```java
@Test
void givenAuthors_whenFindByNameSQLTmplt_thenWeGetOneAuthor() {
    SQLTemplate select = new SQLTemplate(
      Author.class, "select  from Author where name = 'Vicky Sarra'");
    List<Author> authors = context.performQuery(select);
    Author author = authors.get(0);

    assertEquals(authors.size(), 1);
    assertEquals(author.getName(), "Vicky Sarra");
}
```

## 6.EJBQL查询

接下来，让我们通过EJBQLQuery 查询数据，它是作为在 Cayenne 中采用JavaPersistence API 的实验的一部分而创建的。

在这里，查询应用了参数化对象样式；让我们看一些实际的例子。

首先，搜索所有已保存的作者将如下所示：

```java
@Test
void givenAuthors_whenFindAllEJBQL_thenWeGetThreeAuthors() {
    EJBQLQuery query = new EJBQLQuery("select a FROM Author a");
    List<Author> authors = context.performQuery(query);

    assertEquals(authors.size(), 3);
}
```

让我们再次使用名称“Vicky Sarra”搜索作者，但现在使用EJBQLQuery：

```java
@Test
void givenAuthors_whenFindByNameEJBQL_thenWeGetOneAuthor() {
    EJBQLQuery query = new EJBQLQuery(
      "select a FROM Author a WHERE a.name = 'Vicky Sarra'");
    List<Author> authors = context.performQuery(query);
    Author author = authors.get(0);

    assertEquals(authors.size(), 1);
    assertEquals(author.getName(), "Vicky Sarra");
}
```

一个更好的例子是更新作者：

```java
@Test
void whenUpdadingByNameEJBQL_thenWeGetTheUpdatedAuthor() {
    EJBQLQuery query = new EJBQLQuery(
      "UPDATE Author AS a SET a.name "
      + "= 'Vicky Edison' WHERE a.name = 'Vicky Sarra'");
    QueryResponse queryResponse = context.performGenericQuery(query);

    EJBQLQuery queryUpdatedAuthor = new EJBQLQuery(
      "select a FROM Author a WHERE a.name = 'Vicky Edison'");
    List<Author> authors = context.performQuery(queryUpdatedAuthor);
    Author author = authors.get(0);

    assertNotNull(author);
}
```

如果我们只想选择一列，我们应该使用这个查询“select a.name FROM Author a”。[Github](https://github.com/eugenp/tutorials/tree/master/persistence-modules/apache-cayenne)上文章的源代码中提供了更多示例。

## 7.SQL执行

SQLExec也是从 Cayenne M4 版本引入的一个新的流畅查询 API。

一个简单的插入看起来像这样：

```java
@Test
void whenInsertingSQLExec_thenWeGetNewAuthor() {
    int inserted = SQLExec
      .query("INSERT INTO Author (name) VALUES ('Baeldung')")
      .update(context);

    assertEquals(inserted, 1);
}
```

接下来，我们可以根据他的名字更新作者：

```java
@Test
void whenUpdatingSQLExec_thenItsUpdated() {
    int updated = SQLExec.query(
      "UPDATE Author SET name = 'Baeldung' "
      + "WHERE name = 'Vicky Sarra'")
      .update(context);

    assertEquals(updated, 1);
}
```

我们可以从[文档](https://cayenne.apache.org/docs/4.0/index.html)中获得更多细节。

## 八. 总结

在本文中，我们研究了多种使用 Cayenne 编写简单和更高级查询的方法。