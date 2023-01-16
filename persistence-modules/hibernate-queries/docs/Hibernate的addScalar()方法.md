## 1. 概述

在本快速教程中，我们将借助示例讨论 Hibernate 中使用的addScalar()方法。我们将学习如何使用该方法以及使用它的好处。

## 2. addScalar()解决了什么问题？

通常，当使用原生 SQL 查询在 Hibernate 中获取结果时，我们使用createNativeQuery()方法，然后是list()方法：

```java
session.createNativeQuery("SELECT  FROM Student student")
  .list();
```

在这种情况下，Hibernate 使用ResultSetMetadata来查找列的详细信息并返回Object arrays的列表。

但是，过度使用ResultSetMetadata可能会导致性能不佳，这就是addScalar()方法的用武之地。

通过使用addScalar()方法，我们可以防止 Hibernate 使用ResultSetMetadata。

## 3. 如何使用addScalar()？

让我们创建一个新方法来使用addScalar()方法获取学生列表：

```java
public List<Object[]> fetchColumnWithScalar() {
    return session.createNativeQuery("SELECT  FROM Student student")
      .addScalar("studentId", StandardBasicTypes.LONG)
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("age", StandardBasicTypes.INTEGER)
      .list();
}
```

在这里，我们需要指定列名及其数据类型作为addScalar()方法的参数。

现在，Hibernate 不会使用ResultSetMetadata来获取列详细信息，因为我们在addScalar() 中预先定义了它。因此，与以前的方法相比，我们将获得更好的性能。

## 4.其他优势

让我们再看几个可以使用addScalar()方法的用例。

### 4.1. 限制列数

我们还可以使用addScalar()方法来限制查询返回的列数。

让我们编写另一个方法fetchLimitedColumnWithScalar()来只获取学生姓名列：

```java
public List<String> fetchLimitedColumnWithScalar() {
    return session.createNativeQuery("SELECT  FROM Student student")
      .addScalar("name", StandardBasicTypes.STRING)
      .list();
}
```

在这里，我们在查询中使用星号来获取学生列表：

```sql
SELECT  FROM Student student
```

但是，它不会获取所有列并且只返回列表中的一个列名，因为我们在addScalar()方法中只指定了一个列。

让我们创建一个 JUnit 方法来验证fetchLimitedColumnWithScalar()方法返回的列：

```java
List<String> list = scalarExample.fetchLimitedColumnWithScalar();
for (String colValue : list) {
    assertTrue(colValue.startsWith("John"));
}
```

如我们所见，这将返回字符串列表而不是对象数组。此外，在我们的示例数据中，我们保留了所有以“John”开头的学生姓名，这就是我们在上面的单元测试中针对它断言列值的原因。

这使我们的代码在返回内容方面更加明确。

### 4.2. 返回单个标量值

我们还可以使用addScalar()方法直接返回单个标量值而不是列表。

让我们创建一个返回所有学生平均年龄的方法：

```java
public Integer fetchAvgAgeWithScalar() {
    return (Integer) session.createNativeQuery("SELECT AVG(age) as avgAge FROM Student student")
      .addScalar("avgAge")
      .uniqueResult();
}
```

现在，让我们用单元测试方法验证一下：


```java
Integer avgAge = scalarExample.fetchAvgAgeWithScalar();
assertEquals(true, (avgAge >= 5 && avgAge <= 24));
```

正如我们所见，fetchAvgAgeScalar()方法直接返回Integer值，我们正在断言它。

在我们的样本数据中，我们提供了 5 到 24 岁之间的随机学生年龄。因此，在断言期间，我们期望平均值在 5 到 24 之间。

同样，我们可以使用 SQL 中的任何其他聚合函数直接使用addScalar()方法直接获取count、max、min、sum或任何其他单个标量值。

## 5.重载addScalar()方法 

我们还有一个重载的addScalar()方法，它只接受一个列名作为它的单个参数。

让我们创建一个新方法并使用重载的addScalar()方法，它获取“ age ”列而不指定其类型：

```java
public List<Object[]> fetchWithOverloadedScalar() {
    return session.createNativeQuery("SELECT  FROM Student student")
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("age")
      .list();
}
```

现在，让我们编写另一个 JUnit 方法来验证我们的方法是否返回两列或更多列：

```java
List<Object[]> list = scalarExample.fetchColumnWithOverloadedScalar();
for (Object[] colArray : list) {
    assertEquals(2, colArray.length);
}
```

正如我们所见，这返回了一个Object数组列表，数组的大小为 2，代表列表中的姓名和年龄列。

## 六. 总结

在本文中，我们了解了 Hibernate 中addScalar()方法的用法、如何使用它以及何时使用它，以及一个示例。