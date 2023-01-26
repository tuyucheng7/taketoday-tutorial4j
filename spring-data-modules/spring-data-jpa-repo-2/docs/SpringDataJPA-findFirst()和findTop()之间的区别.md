## 一、概述

在本教程中，我们将了解 Spring Data JPA 中的*findFirst()*和*findTop()*方法。这些方法提供数据检索功能。它们映射到 SQL 中相应的选择查询。

## 2. Spring Data JPA API

[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)是Spring项目下的框架之一。它提供了与持久层一起工作的 API，即，我们将它用于 RDBMS 的数据访问层。

JpaRepository接口提供[*了*](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)一种实现数据访问层的方法。

*JpaRepository*是一个通用接口。我们定义了一个扩展*JpaRepository 的接口。*该接口是用我们的*实体*和*实体*的主键键入的。接下来，我们将方法声明添加到我们的存储库接口。

然后 Spring 框架生成接口实现。接口方法的代码是自动生成的。结果，我们在持久性存储上获得了数据访问层。

**Spring 框架将我们的\*Repository\* bean 加载到容器中。**使用[*@Autowired*](https://www.baeldung.com/spring-autowire)注释，我们可以将这个*Repository* bean 注入到我们的组件**中。**这消除了编写 SQL 查询的复杂性。我们得到增强调试能力的类型化数据。Spring Data JPA 是一个伟大的生产力助推器。

## 3. 使用 Spring Data JPA *findFirst()*

数据检索是数据访问层的核心操作。*findFirst()*顾名思义，是一种数据检索方法。名称中的“First”表示它从一组记录的开头检索数据。大多数情况下，我们需要基于某些标准的数据记录子集。

让我们举一个实体*Student*持有学生数据的例子*。*它的字段是*studentId*、*name*和*score*：

```java
@Entity
class Student{
    private Long studentId;
    private String name;
    private Double score;
}复制
```

接下来，我们定义对应的*Repository，*命名为*StudentReposity。*这是一个从*JpaRepository*扩展的接口。我们将*Student*和*Long*类型传递给了*JpaRepository*。*这为我们的学生*实体创建了一个数据访问层：

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findFirstByOrderByScoreDesc();
    List<Student> findFirst3ByOrderByScoreDesc();
}复制
```

### 3.1. 方法名称意义

[方法名称](https://www.baeldung.com/spring-data-derived-queries) *findFirstByOrderByScoreDesc()*不是随机的**。\*方法名称findFirstByOrderByScoreDesc()\*的每一部分都有其意义\*。\***

“查找”意味着它映射到一个选择查询。“第一”表示它从记录列表中检索第一条记录。“OrderByScore”表示我们希望记录按*分数*属性排序。“Desc”表示我们希望排序是倒序的。此方法的返回类型是*Student*对象**。**

Spring 框架会智能地评估方法名称。然后它生成并执行查询以构建我们想要的输出。在我们的特定情况下，它检索第一个*学生*记录。它首先按*分数*降序对*学生*记录进行排序。*因此，我们在所有Student*中获得了最高分。

### 3.2. 归还记录集

下一个方法是*findFirst3ByOrderByScoreDesc()*。此方法的声明中存在一些新功能。

首先，返回类型是*Student*的集合，而不是单个*Student*对象。其次，我们在“findFirst”之后有数字 3。这意味着我们期望多条记录作为此方法的输出。

**方法名称中的 3 定义了我们期望的准确记录数**。如果总记录少于 3，那么结果中的记录少于 3。

此方法还按相反顺序按*score*属性排序。接下来，它获取前 3 条记录并将它们作为记录列表返回。因此，我们按分数获得前三名*Student 。*我们可以将限制数量更改为 2、4 等。

### 3.3. 与过滤条件混淆

我们可以用不同的方式定义相同的限制方法，同时也可以与其他过滤条件混合使用：

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{

  Student findFirstBy(Sort sort);
  Student findFirstByNameLike(String name, Sort sort);

  List<Student> findFirst2ByScoreBetween(int startScore, int endScore, Sort sort);
}复制
```

在这里，*findFirstBy()*将[*Sort*](https://www.baeldung.com/spring-data-sorting)定义为参数。这使得*findFirstBy()*成为一种通用方法。我们将在进行方法调用之前定义排序逻辑。

除了*findFirstBy()*功能之外， *findFirstByNameLike()*还创建了一个针对*学生姓名的过滤器。*

*f* *indFirst2ByScoreBetween()*定义分数范围。它*按排序标准对**Student*进行排序。然后它找到*开始*和*结束*分数范围之间的前两个*Student 。*

让我们看看如何创建一个[*Sort*](https://www.baeldung.com/spring-data-sorting)对象：

```java
Sort sort = Sort.by("score").descending();复制
```

*by()*方法将属性名称作为我们要排序的字符串。另一方面，*descending()*方法调用以相反的顺序进行排序。

## 4. 使用 Spring Data JPA *findTop()*

接下来是*findTop()*方法。***findTop()\* 只是同一\*findFirst()\*方法**的另一个名称。**我们可以互换使用\*firstFirst()\*或\*findTop()\*，没有任何问题。**

它们只是别名和开发人员喜欢的问题，没有任何实际影响。这是我们之前看到的相同方法的*findTop()风格：*

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findTopByOrderByScoreDesc();
    List<Student> findTop3ByOrderByScoreDesc();
    Student findTopBy(Sort sort);
    Student findTopByNameLike(String name, Sort sort);
    List<Student> findTop2ByScoreBetween(int startScore, int endScore, Sort sort);
}
复制
```

## 5.结论

在本文中，我们了解了 Spring Data JPA API 提供的两个方法*findFirst()*和*findTop() 。*这两种方法可以作为更复杂的检索方法的基础。

我们已经看到了一些将*findFirst()*和*findTop()*与其他过滤条件混合使用的示例。当使用没有数字的*findFirst()*或*findTop()*时，返回单个记录。如果*findFirst()*或*findTop()*附加了一个数字，则检索到该数字的记录。

**一个重要的学习点是我们可以使用 \*findFirst()\*或\*findTop()\*。**这是个人选择的问题。*findFirst()*和*findTop()*之间没有区别。