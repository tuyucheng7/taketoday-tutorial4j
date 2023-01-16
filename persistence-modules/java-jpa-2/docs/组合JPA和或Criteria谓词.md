## 1. 概述

在查询数据库中的记录时，可以轻松使用 JPA Criteria API 添加多个 AND/OR 条件。在本教程中，我们将探索组合多个 AND/OR 谓词的 JPA 条件查询的快速示例。

如果你还不熟悉谓词，我们建议你先阅读[基本的 JPA 条件查询](https://www.baeldung.com/spring-data-criteria-queries)。

## 2. 示例应用

对于我们的示例，我们将考虑Item实体的清单，每个实体都有一个id、 name、color和grade：

```java
@Entity
public class Item {

    @Id
    private Long id;
    private String color;
    private String grade;
    private String name;
    
    // standard getters and setters
}
```

## 3.使用AND谓词组合两个OR谓词

让我们考虑一个场景，我们需要找到同时具有以下两者的项目：

1.  红色或蓝色
    AND
2.  A或B级

我们可以使用 JPA Criteria API 的and()和or()复合谓词轻松地做到这一点。

首先，我们将设置我们的查询：

```java
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
CriteriaQuery<Item> criteriaQuery = criteriaBuilder.createQuery(Item.class);
Root<Item> itemRoot = criteriaQuery.from(Item.class);
```

现在我们需要构建一个Predicate来查找具有蓝色或红色的项目：

```java
Predicate predicateForBlueColor
  = criteriaBuilder.equal(itemRoot.get("color"), "blue");
Predicate predicateForRedColor
  = criteriaBuilder.equal(itemRoot.get("color"), "red");
Predicate predicateForColor
  = criteriaBuilder.or(predicateForBlueColor, predicateForRedColor);
```

接下来，我们将构建一个Predicate来查找 A 级或 B 级的项目：

```java
Predicate predicateForGradeA
  = criteriaBuilder.equal(itemRoot.get("grade"), "A");
Predicate predicateForGradeB
  = criteriaBuilder.equal(itemRoot.get("grade"), "B");
Predicate predicateForGrade
  = criteriaBuilder.or(predicateForGradeA, predicateForGradeB);
```

最后，我们将定义这两者的 AND谓词，应用where()方法，并执行我们的查询：

```java
Predicate finalPredicate
  = criteriaBuilder.and(predicateForColor, predicateForGrade);
criteriaQuery.where(finalPredicate);
List<Item> items = entityManager.createQuery(criteriaQuery).getResultList();
```

## 4.使用OR谓词组合两个AND谓词

相反，让我们考虑一个场景，我们需要找到具有以下任一条件的项目：

1.  红色和 D 级
    或
2.  蓝色和B级

逻辑非常相似，但这里我们首先创建两个 AND Predicate，然后使用 OR Predicate将它们组合起来：

```java
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
CriteriaQuery<Item> criteriaQuery = criteriaBuilder.createQuery(Item.class);
Root<Item> itemRoot = criteriaQuery.from(Item.class);

Predicate predicateForBlueColor
  = criteriaBuilder.equal(itemRoot.get("color"), "red");
Predicate predicateForGradeA
  = criteriaBuilder.equal(itemRoot.get("grade"), "D");
Predicate predicateForBlueColorAndGradeA
  = criteriaBuilder.and(predicateForBlueColor, predicateForGradeA);

Predicate predicateForRedColor
  = criteriaBuilder.equal(itemRoot.get("color"), "blue");
Predicate predicateForGradeB
  = criteriaBuilder.equal(itemRoot.get("grade"), "B");
Predicate predicateForRedColorAndGradeB
  = criteriaBuilder.and(predicateForRedColor, predicateForGradeB);

Predicate finalPredicate
  = criteriaBuilder
  .or(predicateForBlueColorAndGradeA, predicateForRedColorAndGradeB);
criteriaQuery.where(finalPredicate);
List<Item> items = entityManager.createQuery(criteriaQuery).getResultList();
```

## 5.总结

在本文中，我们使用 JPA Criteria API 来实现需要组合 AND/OR 谓词的用例。