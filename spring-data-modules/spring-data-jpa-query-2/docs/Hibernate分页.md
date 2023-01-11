## 1. 概述

**本文简要介绍Hibernate中的分页**，我们重点关注HQL以及ScrollableResults API。最后，使用Hibernate Criteria进行分页。

## 2. 使用HQL和setFirstResult、setMaxResults API进行分页

在Hibernate中进行分页的最简单和最常见的方法是**使用HQL**：

```java
Session session = sessionFactory.openSession();
Query query = sess.createQuery("From Foo");
query.setFirstResult(0);
query.setMaxResults(10);
List<Foo> fooList = fooList = query.list();
```

这个例子使用了一个基本的Foo实体，与JPA的JQL实现非常相似，唯一的区别是查询语言。如果我们观察Hibernate的日志记录，可以看到以下SQL语句：

```sql
Hibernate:
select foo0_.id   as id1_1_,
       foo0_.name as name2_1_
from Foo foo0_ limit ?
```

### 2.1 总数和最后一页

在不知道实体总数的情况下，分页解决方案是不完整的：

```java
String countQ = "select count (f.id) from Foo f";
Query countQuery = session.createQuery(countQ);
Long countResults = (Long) countQuery.uniqueResult();
```

最后，根据总数和给定的页面大小，我们可以计算出最后一页：

```java
int pageSize = 10;
int lastPageNumber = (int) (Math.ceil(countResults / pageSize));
```

在这一点上，我们可以看一个完整的分页示例，我们计算最后一页，然后检索它：

```java
@Test
public final void whenRetrievingLastPage_thenCorrectSize() {
    final int pageSize = 10;
    final String countQ = "Select count (f.id) from Foo f";
    final Query countQuery = session.createQuery(countQ);
    final Long countResults = (Long) countQuery.uniqueResult();
    final int lastPageNumber = (int) (Math.ceil(countResults / pageSize));
    
    final Query selectQuery = session.createQuery("From Foo");
    selectQuery.setFirstResult((lastPageNumber - 1) * pageSize);
    selectQuery.setMaxResults(pageSize);
    final List<Foo> lastPage = selectQuery.list();
    
    assertThat(lastPage, hasSize(lessThan(pageSize + 1)));
}
```

## 3. 使用HQL和ScrollableResults API进行分页

使用ScrollableResults实现分页有可能减少数据库调用，这种方法在程序运行时对结果集进行流式处理,因此无需重复查询来填充每页：

```java
@Test
public final void givenUsingTheScrollableApi_whenRetrievingPaginatedData_thenCorrect() {
    final int pageSize = 10;
    final String hql = "FROM Foo f order by f.name";
    final Query query = session.createQuery(hql);
    
    final ScrollableResults resultScroll = query.scroll(ScrollMode.FORWARD_ONLY);
    resultScroll.first();
    resultScroll.scroll(0);
    final List<Foo> fooPage = Lists.newArrayList();
    int i = 0;
    
    while (pageSize > i++) {
        fooPage.add((Foo) resultScroll.get(0));
        if (!resultScroll.next()) {
            break;
        }
    }
    
    assertThat(fooPage, hasSize(lessThan(10 + 1)));
}
```

此方法不仅具有时间效率(仅一次数据库调用)，而且允许用户**无需额外查询即可访问结果集的总数**：

```java
resultScroll.last();
int totalResults = resultScroll.getRowNumber() + 1;
```

另一方面，请记住，尽管ScrollableResults非常有效，但一个大窗口可能会占用相当多的内存。

## 4. 使用Criteria API进行分页

最后，让我们来看**一个更灵活的解决方案**-Criteria API：

```java
Criteria criteria = session.createCriteria(Foo.class);
criteria.setFirstResult(0);
criteria.setMaxResults(pageSize);
List<Foo> firstPage = criteria.list();
```

通过使用Projection对象，Hibernate Criteria API获得结果集总数也非常简单：

```java
Criteria criteriaCount = session.createCriteria(Foo.class);
criteriaCount.setProjection(Projections.rowCount());
Long count = (Long) criteriaCount.uniqueResult();
```

如你所见，使用此API将产生比普通HQL更冗长的代码，但**该API是完全安全的，并且更加灵活**。

## 5. 总结

本文简要介绍了在Hibernate中进行分页的各种方法。