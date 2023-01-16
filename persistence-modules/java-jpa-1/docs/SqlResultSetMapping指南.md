## 1. 简介

在本指南中，我们将了解Java Persistence API (JPA) 中的SqlResultSetMapping 。

这里的核心功能涉及将结果集从数据库 SQL 语句映射到Java对象。

## 2.设置

在我们看它的用法之前，让我们做一些设置。

### 2.1. Maven 依赖

我们所需的 Maven 依赖项是 Hibernate 和 H2 数据库。 [Hibernate](http://hibernate.org/) 为我们提供了 JPA 规范的实现。我们使用 [H2 数据库](http://www.h2database.com/html/main.html) 作为内存数据库。

### 2.2. 数据库

接下来，我们将创建两个表，如下所示：

```sql
CREATE TABLE EMPLOYEE
(id BIGINT,
 name VARCHAR(10));
```

EMPLOYEE 表 存储一个结果实体对象。 SCHEDULE_DAYS 包含通过列 employeeId链接到EMPLOYEE表的记录：

```sql
CREATE TABLE SCHEDULE_DAYS
(id IDENTITY,
 employeeId BIGINT,
 dayOfWeek  VARCHAR(10));
```

可以在[本指南的代码中](https://github.com/eugenp/tutorials/tree/master/persistence-modules/java-jpa)找到用于创建数据的脚本。

### 2.3. 实体对象

我们的Entity 对象应该看起来很相似：

```java
@Entity
public class Employee {
    @Id
    private Long id;
    private String name;
}
```

实体 对象的命名可能与数据库表不同。我们可以用@Table 注解类来显式映射它们：

```java
@Entity
@Table(name = "SCHEDULE_DAYS")
public class ScheduledDay {

    @Id
    @GeneratedValue
    private Long id;
    private Long employeeId;
    private String dayOfWeek;
}
```

## 3. 标量映射

现在我们有了数据，我们可以开始映射查询结果了。

### 3.1. 列结果

虽然SqlResultSetMapping和Query注解也适用于 Repository类，但我们在此示例中使用实体 类上的注解。

每个SqlResultSetMapping 注解只需要一个属性，名称。 但是，如果没有其中一种成员类型，则不会映射任何内容。成员类型是ColumnResult、ConstructorResult和EntityResult。

在这种情况下， ColumnResult 将任何列映射到标量结果类型：

```java
@SqlResultSetMapping(
  name="FridayEmployeeResult",
  columns={@ColumnResult(name="employeeId")})
```

ColumnResult属性 名称 标识我们查询中的列： 

```java
@NamedNativeQuery(
  name = "FridayEmployees",
  query = "SELECT employeeId FROM schedule_days WHERE dayOfWeek = 'FRIDAY'",
  resultSetMapping = "FridayEmployeeResult")

```

请注意， 我们的NamedNativeQuery注解 中的resultSetMapping的值 很重要，因为它与我们的ResultSetMapping声明中的名称属性 相匹配。

因此，NamedNativeQuery 结果集按预期映射。同样，StoredProcedure API 需要这种关联。

### 3.2. 列结果测试

我们需要一些 Hibernate 特定对象来运行我们的代码：

```java
@BeforeAll
public static void setup() {
    emFactory = Persistence.createEntityManagerFactory("java-jpa-scheduled-day");
    em = emFactory.createEntityManager();
}
```

最后，我们调用命名查询来运行我们的测试：

```java
@Test
public void whenNamedQuery_thenColumnResult() {
    List<Long> employeeIds = em.createNamedQuery("FridayEmployees").getResultList();
    assertEquals(2, employeeIds.size());
}
```

## 4.构造函数映射

让我们看一下何时需要将结果集映射到整个对象。

### 4.1. 构造结果

与我们的ColumnResult示例类似，我们将在我们的实体类ScheduledDay上添加SqlResultMapping注解。但是，为了使用构造函数进行映射，我们需要创建一个：

```java
public ScheduledDay (
  Long id, Long employeeId, 
  Integer hourIn, Integer hourOut, 
  String dayofWeek) {
    this.id = id;
    this.employeeId = employeeId;
    this.dayOfWeek = dayofWeek;
}
```

此外，映射指定了目标类和列(两者都是必需的)：

```java
@SqlResultSetMapping(
    name="ScheduleResult",
    classes={
      @ConstructorResult(
        targetClass=com.baeldung.sqlresultsetmapping.ScheduledDay.class,
        columns={
          @ColumnResult(name="id", type=Long.class),
          @ColumnResult(name="employeeId", type=Long.class),
          @ColumnResult(name="dayOfWeek")})})
```

ColumnResults的顺序非常重要。 如果列乱序，则构造函数将无法识别。在我们的示例中，排序与表列匹配，因此实际上不需要。

```java
@NamedNativeQuery(name = "Schedules",
  query = "SELECT  FROM schedule_days WHERE employeeId = 8",
  resultSetMapping = "ScheduleResult")
```

ConstructorResult 的另一个独特区别 是生成的对象实例化为“新”或“分离”。当EntityManager中存在匹配的主键时，映射的实体将处于分离状态，否则它将是新的。

有时，由于 SQL 数据类型与Java数据类型不匹配，我们可能会遇到运行时错误。因此，我们可以用类型显式声明它。

### 4.2. 构造结果测试

让我们在单元测试中测试ConstructorResult ：

```java
@Test
public void whenNamedQuery_thenConstructorResult() {
  List<ScheduledDay> scheduleDays
    = Collections.checkedList(
      em.createNamedQuery("Schedules", ScheduledDay.class).getResultList(), ScheduledDay.class);
    assertEquals(3, scheduleDays.size());
    assertTrue(scheduleDays.stream().allMatch(c -> c.getEmployeeId().longValue() == 3));
}
```

## 5.实体映射

最后，对于代码较少的简单实体映射，让我们看一下 EntityResult。

### 5.1. 单一实体

EntityResult要求我们指定实体类Employee。我们使用可选的fields 属性进行更多控制。结合 FieldResult，我们可以映射别名和不匹配的字段：

```java
@SqlResultSetMapping(
  name="EmployeeResult",
  entities={
    @EntityResult(
      entityClass = com.baeldung.sqlresultsetmapping.Employee.class,
        fields={
          @FieldResult(name="id",column="employeeNumber"),
          @FieldResult(name="name", column="name")})})
```

现在我们的查询应该包括别名列：

```java
@NamedNativeQuery(
  name="Employees",
  query="SELECT id as employeeNumber, name FROM EMPLOYEE",
  resultSetMapping = "EmployeeResult")
```

与ConstructorResult类似，EntityResult需要一个构造函数。但是，这里使用默认的。

### 5.2. 多个实体

一旦我们映射了一个实体，映射多个实体就非常简单了：

```java
@SqlResultSetMapping(
  name = "EmployeeScheduleResults",
  entities = {
    @EntityResult(entityClass = com.baeldung.sqlresultsetmapping.Employee.class),
    @EntityResult(entityClass = com.baeldung.sqlresultsetmapping.ScheduledDay.class)
```

### 5.3. 实体结果测试

让我们看一下EntityResult的实际效果：

```java
@Test
public void whenNamedQuery_thenSingleEntityResult() {
    List<Employee> employees = Collections.checkedList(
      em.createNamedQuery("Employees").getResultList(), Employee.class);
    assertEquals(3, employees.size());
    assertTrue(employees.stream().allMatch(c -> c.getClass() == Employee.class));
}
```

由于多个实体结果连接了两个实体，因此仅对其中一个类的查询注解会造成混淆。

出于这个原因，我们在测试中定义查询：

```java
@Test
public void whenNamedQuery_thenMultipleEntityResult() {
    Query query = em.createNativeQuery(
      "SELECT e.id, e.name, d.id, d.employeeId, d.dayOfWeek "
        + " FROM employee e, schedule_days d "
        + " WHERE e.id = d.employeeId", "EmployeeScheduleResults");
    
    List<Object[]> results = query.getResultList();
    assertEquals(4, results.size());
    assertTrue(results.get(0).length == 2);

    Employee emp = (Employee) results.get(1)[0];
    ScheduledDay day = (ScheduledDay) results.get(1)[1];

    assertTrue(day.getEmployeeId() == emp.getId());
}
```

## 六. 总结

在本指南中，我们查看了使用 SqlResultSetMapping注解的不同选项。 SqlResultSetMapping 是JavaPersistence API 的关键部分。