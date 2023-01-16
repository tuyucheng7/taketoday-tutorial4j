## 1. 概述

在本教程中，我们将学习如何使用 JPA 和 Hibernate 投影实体属性。

## 2.实体

首先，让我们看看我们将在整篇文章中使用的实体：

```java
@Entity
public class Product {
    @Id
    private long id;
    
    private String name;
    
    private String description;
    
    private String category;
    
    private BigDecimal unitPrice;

    // setters and getters
}
```

这是一个简单的实体类，表示具有各种属性的产品。

## 3. JPA 预测

尽管 JPA 规范没有明确提及投影，但在很多情况下我们都在概念中找到它们。

通常，JPQL 查询有一个候选实体类。查询在执行时创建候选类的对象——使用检索到的数据填充它们的所有属性。

但是，可以检索实体属性的子集，或者，即列数据的投影 。

除了列数据，我们还可以投影分组函数的结果。

### 3.1. 单列投影

假设我们要列出所有产品的名称。在 JPQL 中，我们可以通过在select子句中只包含名称来做到这一点：

```java
Query query = entityManager.createQuery("select name from Product");
List<Object> resultList = query.getResultList();
```

或者，我们可以对CriteriaBuilder做同样的事情：

```java
CriteriaBuilder builder = entityManager.getCriteriaBuilder();
CriteriaQuery<String> query = builder.createQuery(String.class);
Root<Product> product = query.from(Product.class);
query.select(product.get("name"));
List<String> resultList = entityManager.createQuery(query).getResultList();
```

因为我们正在投影恰好是String类型的单个列，所以我们希望在结果中获得String的列表。因此，我们在createQuery()方法中将候选类指定为String 。

因为我们想要投影到单个属性，所以我们使用了Query.select()方法。这里是我们想要的属性，所以在我们的例子中，我们将使用Product实体中的name属性。

现在，让我们看一下上述两个查询生成的示例输出：

```plaintext
Product Name 1
Product Name 2
Product Name 3
Product Name 4
```

请注意，如果我们在投影中使用id属性而不是name，则查询将返回一个Long对象列表。

### 3.2. 多列投影

要使用 JPQL 在多个列上进行投影，我们只需将所有必需的列添加到 select子句中：

```java
Query query = session.createQuery("select id, name, unitPrice from Product");
List resultList = query.getResultList();
```

但是，当使用CriteriaBuilder时，我们必须做一些不同的事情：

```java
CriteriaBuilder builder = session.getCriteriaBuilder();
CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
Root<Product> product = query.from(Product.class);
query.multiselect(product.get("id"), product.get("name"), product.get("unitPrice"));
List<Object[]> resultList = entityManager.createQuery(query).getResultList();
```

在这里，我们使用方法multiselect()而不是select()。使用此方法，我们可以指定要选择的多个项目。

另一个重大变化是使用Object[]。当我们选择多个项目时，查询返回一个对象数组，其中包含投影的每个项目的值。JPQL 也是如此。

让我们看看打印时数据的样子：

```plaintext
[1, Product Name 1, 1.40]
[2, Product Name 2, 4.30]
[3, Product Name 3, 14.00]
[4, Product Name 4, 3.90]
```

正如我们所见，返回的数据处理起来有点麻烦。但是，幸运的是，我们可以让 JPA 将[这些数据填充到自定义类](https://www.baeldung.com/hibernate-query-to-custom-class)中。

此外，我们可以使用CriteriaBuilder.tuple()或CriteriaBuilder.construct()分别将结果作为 Tuple对象或自定义类对象的列表。

### 3.3. 投影聚合函数

除了列数据，有时我们可能希望对数据进行分组并使用聚合函数，如计数和平均值。

假设我们要查找每个类别中的产品数量。我们可以使用JPQL中的count()聚合函数来做到这一点：

```java
Query query = entityManager.createQuery("select p.category, count(p) from Product p group by p.category");
```

或者我们可以使用CriteriaBuilder：

```java
CriteriaBuilder builder = entityManager.getCriteriaBuilder();
CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
Root<Product> product = query.from(Product.class);
query.multiselect(product.get("category"), builder.count(product));
query.groupBy(product.get("category"));
```

在这里，我们使用了CriteriaBuilder的 count()方法。

使用上面的任何一个都会产生一个对象数组列表：

```plaintext
[category1, 2]
[category2, 1]
[category3, 1]
```

除了count()之外，CriteriaBuilder还提供各种其他聚合函数：

-   avg – 计算组中列的平均值
-   max – 计算组中列的最大值
-   min – 计算组中列的最小值
-   least – 查找最少的列值(例如，按字母顺序或按日期)
-   sum – 计算组中列值的总和

## 4. 休眠投影

与 JPA 不同，Hibernate 提供org.hibernate.criterion.Projection用于使用[Criteria查询](https://www.baeldung.com/hibernate-criteria-queries)进行投影 。它还提供了一个名为org.hibernate.criterion.Projections 的类，一个Projection实例的工厂。

### 4.1. 单列投影

首先，让我们看看如何投影单个列。我们将使用之前看到的示例：

```java
Criteria criteria = session.createCriteria(Product.class);
criteria = criteria.setProjection(Projections.property("name"));

```

我们使用了Criteria.setProjection()方法来指定我们想要在查询结果中显示的属性。Projections.property()为我们做的工作与 Root.get()在指示要选择的列时 所做的一样。

### 4.2. 多列投影

要投影多列，我们必须首先创建一个ProjectionList。ProjectionList是一种特殊的 Projection，它包装了其他投影以允许选择多个值。

我们可以使用Projections.projectionList()方法创建一个ProjectionList ，例如显示Product的id和name：

```java
Criteria criteria = session.createCriteria(Product.class);
criteria = criteria.setProjection(
  Projections.projectionList()
    .add(Projections.id())
    .add(Projections.property("name")));
```

### 4.3. 投影聚合函数

就像 CriteriaBuilder一样，Projections类也提供聚合函数的方法。

让我们看看如何实现我们之前看到的计数示例：

```java
Criteria criteria = session.createCriteria(Product.class);
criteria = criteria.setProjection(
  Projections.projectionList()
    .add(Projections.groupProperty("category"))
    .add(Projections.rowCount()));
```

需要注意的是，我们没有直接在 Criteria对象中指定 GROUP BY 。调用groupProperty会为我们触发这个。

除了rowCount()函数之外，Projections还提供了我们之前看到的聚合函数。

### 4.4. 为投影使用别名

Hibernate Criteria API 的一个有趣特性是使用投影的别名。

这在使用聚合函数时特别有用，因为我们可以在Criterion和Order实例中引用别名：

```java
Criteria criteria = session.createCriteria(Product.class);
criteria = criteria.setProjection(Projections.projectionList()
             .add(Projections.groupProperty("category"))
             .add(Projections.alias(Projections.rowCount(), "count")));
criteria.addOrder(Order.asc("count"));
```

## 5.总结

在本文中，我们了解了如何使用 JPA 和 Hibernate 来投影实体属性。

重要的是要注意，Hibernate 从 5.2 版开始就弃用了它的 Criteria API，以支持 JPA CriteriaQuery API。但是，这只是因为 Hibernate 团队没有时间保持两个不同的 API，它们几乎同步地做同样的事情。