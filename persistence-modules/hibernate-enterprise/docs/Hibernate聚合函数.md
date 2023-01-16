## 1. 概述

Hibernate 聚合函数 使用满足给定查询条件的所有对象的属性值来计算最终结果。

Hibernate 查询语言 (HQL) 支持各种聚合函数 ——SELECT语句中的min()、max()、sum()、avg() 和 count()。就像任何其他 SQL 关键字一样，这些函数的用法不区分大小写。

在本快速教程中，我们将探讨如何使用它们。请注意，在下面的示例中，我们使用原始类型或包装类型来存储聚合函数的结果。HQL 两者都支持，所以只需要选择使用哪一个即可。

## 2.初始设置

让我们从定义一个Student实体开始：

```java
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long studentId;

    private String name;

    private int age;

    // constructor, getters and setters
}
```

并用一些学生填充我们的数据库：

```java
public class AggregateFunctionsIntegrationTest {

    private static Session session;
    private static Transaction transaction;

    @BeforeClass
    public static final void setup() throws HibernateException, IOException {
        session = HibernateUtil.getSessionFactory()
          .openSession();
        transaction = session.beginTransaction();

        session.save(new Student("Jonas", 22, 12f));
        session.save(new Student("Sally", 20, 34f));
        session.save(new Student("Simon", 25, 45f));
        session.save(new Student("Raven", 21, 43f));
        session.save(new Student("Sam", 23, 33f));

    }

}
```

请注意，我们的studentId字段已使用SEQUENCE生成策略填充。

[我们可以在Hibernate Identifier Generation Strategies](https://www.baeldung.com/hibernate-identifiers)教程中了解更多相关信息 。

## 3.分钟()

现在，假设我们想找出Student表中存储的所有学生中的最小年龄。我们可以使用min()函数轻松地做到这一点：

```java
@Test
public void whenMinAge_ThenReturnValue() {
    int minAge = (int) session.createQuery("SELECT min(age) from Student")
      .getSingleResult();
    assertThat(minAge).isEqualTo(20);
}
```

getSingleResult ()方法返回一个对象类型。因此，我们已将输出向下转换为int。

## 4.最大()

与min()函数类似，我们有一个max()函数：

```java
@Test
public void whenMaxAge_ThenReturnValue() {
    int maxAge = (int) session.createQuery("SELECT max(age) from Student")
      .getSingleResult();
    assertThat(maxAge).isEqualTo(25);
}
```

同样，结果被向下转换为int类型。

min()和max()函数的返回类型取决于上下文中的字段。对我们来说，它返回一个整数，因为学生的年龄是一个int类型的属性。

## 5.总和()

我们可以使用sum()函数来计算所有年龄的总和：

```java
@Test
public void whenSumOfAllAges_ThenReturnValue() {
    Long sumOfAllAges = (Long) session.createQuery("SELECT sum(age) from Student")
      .getSingleResult();
    assertThat(sumOfAllAges).isEqualTo(111);
}
```

根据字段的数据类型，sum ()函数返回Long或Double。

## 6.平均()

同样，我们可以使用avg()函数来计算平均年龄：

```java
@Test
public void whenAverageAge_ThenReturnValue() {
    Double avgAge = (Double) session.createQuery("SELECT avg(age) from Student")
      .getSingleResult();
    assertThat(avgAge).isEqualTo(22.2);
}
```

avg ()函数始终返回Double值。

## 7.计数()

与原生 SQL 一样，HQL 也提供了一个count()函数。让我们找出Student表中的记录数：

```java
@Test
public void whenCountAll_ThenReturnValue() {
    Long totalStudents = (Long) session.createQuery("SELECT count() from Student")
      .getSingleResult();
    assertThat(totalStudents).isEqualTo(5);
}
```

count()函数返回Long类型。

我们可以使用count()函数的任何可用变体——count()、count(…)、count(distinct …)或count(all …)。它们中的每一个在语义上都等同于其原生 SQL 对应物。

## 八. 总结

在本教程中，我们简要介绍了 Hibernate 中可用的聚合函数类型。Hibernate 聚合函数类似于普通 SQL 中可用的函数。