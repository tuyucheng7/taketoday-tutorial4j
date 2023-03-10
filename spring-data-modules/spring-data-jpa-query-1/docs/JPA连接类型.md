## 1. 概述

在本文中，我们将介绍[JPA](https://www.baeldung.com/jpa-hibernate-difference)支持的不同连接类型。

为此，我们将使用JPQL-一种[用于JPA的查询语言](https://www.baeldung.com/jpa-queries)。

## 2. 简单数据模型

让我们看看我们将在示例中使用的示例数据模型。

首先，我们将创建一个Employee实体：

```java
@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private int age;

    @ManyToOne
    private Department department;

    @OneToMany(mappedBy = "employee")
    private List<Phone> phones;

    // getters and setters...
}
```

每个员工将被分配到一个部门：

```java
@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    // getters and setters...
}
```

最后，每个员工将拥有多个电话：

```java
@Entity
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String number;

    @ManyToOne
    private Employee employee;

    // getters and setters...
}
```

## 3. 内连接

我们将从内连接开始。当两个或多个实体内连接时，结果中只收集与连接条件匹配的记录。

### 3.1 单值关联导航的隐式内连接

内连接可以是隐式的。顾名思义，**开发人员不用指定隐式内连接**。每当我们导航一个单值关联时，JPA都会自动创建一个隐式连接：

```java
@ExtendWith(SpringExtension.class)
@DataJpaTest(showSql = false)
@ActiveProfiles("joins")
class JpaJoinsIntegrationTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void whenPathExpressionIsUsedForSingleValuedAssociation_thenCreatesImplicitInnerJoin() {
        TypedQuery<Department> query = entityManager.createQuery("select e.department from Employee e", Department.class);
        List<Department> resultList = query.getResultList();

        assertThat(resultList).hasSize(3);
        assertThat(resultList).extracting("name").containsOnly("Infra", "Accounting", "Accounting");
    }
}
```

上面使用到的application-joins.properties文件以及import.joins.sql脚本为：

```properties
## application-joins.properties
spring.sql.init.data-locations=classpath:db/import_joins.sql
```

```sql
-- import_joins.sql
INSERT INTO department (id, name)
VALUES (1, 'Infra');
INSERT INTO department (id, name)
VALUES (2, 'Accounting');
INSERT INTO department (id, name)
VALUES (3, 'Management');

INSERT INTO joins_employee (id, name, age, department_id)
VALUES (1, 'Tuyucheng', '35', 1);
INSERT INTO joins_employee (id, name, age, department_id)
VALUES (2, 'John', '35', 2);
INSERT INTO joins_employee (id, name, age, department_id)
VALUES (3, 'Jane', '35', 2);

INSERT INTO phone (id, number, employee_id)
VALUES (1, '111', 1);
INSERT INTO phone (id, number, employee_id)
VALUES (2, '222', 1);
INSERT INTO phone (id, number, employee_id)
VALUES (3, '333', 1);

COMMIT;
```

在这里，Employee实体与Department实体具有多对一的关系。**如果我们从Employee实体导航到他的Department，并指定e.department，我们将导航到单值关联**。因此，JPA将创建一个内连接。此外，连接条件将从映射元数据中派生。

### 3.2 具有单值关联的显式内连接

接下来，我们将查看在[JPQL查询](https://www.baeldung.com/jpa-queries)中**使用JOIN关键字的显式内连接**：

```java
@Test
void whenJoinKeywordIsUsed_thenCreatesExplicitInnerJoin() {
    TypedQuery<Department> query = entityManager.createQuery("select d from Employee e join e.department d", Department.class);
    List<Department> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(3);
    assertThat(resultList).extracting("name").containsOnly("Infra", "Accounting", "Accounting");
}
```

在此查询中，**我们在from子句中指定了一个join关键字和关联的Department实体**，而在上一个查询中根本没有指定它们。但是，除了这种语法差异之外，生成的SQL查询将非常相似。

我们还可以指定一个可选的inner关键字：

```java
@Test
void whenInnerJoinKeywordIsUsed_thenCreatesExplicitInnerJoin() {
    TypedQuery<Department> query = entityManager.createQuery("select d from Employee e inner join e.department d", Department.class);
    List<Department> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(3);
    assertThat(resultList).extracting("name").containsOnly("Infra", "Accounting", "Accounting");
}
```

那么既然JPA自动创建了一个隐式的内连接，我们什么时候需要显式的呢？

首先，**JPA仅在我们指定路径表达式时创建隐式内连接**。例如，当我们只想选择具有部门的员工，并且不使用像e.department这样的路径表达式时，我们应该在查询中使用join关键字。

其次，如果我们明确指定的话，可以更容易知道发生了什么。

### 3.3 具有集合值关联的显式内连接

**我们需要明确的另一个地方是集合值关联**。

如果我们观察我们的数据模型，Employee与Phone具有一对多的关系。与前面的示例一样，我们可以尝试编写类似的查询：

```sql
SELECT e.phones
FROM Employee e
```

但是，这不会像我们预期的那样有效。由于选定的关联e.phones是集合值，因此我们将获得Collection的列表而不是Phone实体：

```java
@Test
void whenCollectionValuedAssociationIsSpecifiedInSelect_ThenReturnsCollections() {
    TypedQuery<Collection> query = entityManager.createQuery("SELECT e.phones FROM Employee e", Collection.class);
    List<Collection> resultList = query.getResultList();
    
    assertThat(resultList).extracting("number").containsOnly("111", "222", "333");
}
```

**此外，如果我们想在where子句中过滤Phone实体，JPA将不允许这样做。这是因为路径表达式不能从集合值关联继续。例如e.phones.number无效**。

相反，我们应该创建一个显式内连接，并为Phone实体创建一个别名。然后我们可以在select或where子句中指定Phone实体：

```java
@Test
void whenCollectionValuedAssociationIsJoined_ThenCanSelect() {
    TypedQuery<Phone> query = entityManager.createQuery("select ph from Employee e join e.phones ph where ph like '1%'", Phone.class);
    List<Phone> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(1);
}
```

## 4. 外连接

当两个或多个实体进行外连接时，**满足连接条件的记录以及左侧实体中的记录将收集在结果中**：

```java
@Test
void whenLeftKeywordIsSpecified_thenCreatesOuterJoinAndIncludesNonMatched() {
    TypedQuery<Department> query = entityManager.createQuery("select distinct d from Department d left join d.employees e", Department.class);
    List<Department> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(3);
    assertThat(resultList).extracting("name").containsOnly("Infra", "Accounting", "Management");
}
```

在这里，结果将包含具有关联员工的部门以及没有关联的部门。

这也称为左外连接。**JPA不提供右连接**，我们还从右实体收集不匹配的记录。尽管如此，我们可以通过在from子句中交换实体来模拟右连接。

## 5. where子句中的join

### 5.1 带有条件

**我们可以在from子句中列出两个实体，然后在where子句中指定连接条件**。

这可能很方便，尤其是当数据库级外键不存在时：

```java
@Test
void whenEntitiesAreListedInFromAndMatchedInWhere_ThenCreatesJoin() {
    TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Employee e, Department d WHERE e.department = d", Department.class);
    List<Department> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(3);
    assertThat(resultList).extracting("name").containsOnly("Infra", "Accounting", "Accounting");
}
```

在这里，我们连接Employee和Department实体，但这次在where子句中指定了一个条件。

### 5.2 无条件(笛卡尔积)

同样，**我们可以在from子句中列出两个实体，而无需指定任何连接条件**。**在这种情况下，我们将得到一个笛卡尔积**。这意味着第一个实体中的每条记录都与第二个实体中的所有其他记录配对：

```java
@Test
void whenEntitiesAreListedInFrom_ThenCreatesCartesianProduct() {
    TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Employee e, Department d", Department.class);
    List<Department> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(9);
    assertThat(resultList).extracting("name").containsOnly("Infra", "Accounting", "Management", "Infra", "Accounting", "Management", "Infra", "Accounting", "Management");
}
```

正如我们所猜测的，这些类型的查询不会很好地执行。

## 6. 多重连接

到目前为止，我们已经使用了两个实体来执行连接，但这不是一个死规则。**我们还可以在单个JPQL查询中连接多个实体**：

```java
@Test
void whenMultipleEntitiesAreListedWithJoin_ThenCreatesMultipleJoins() {
    TypedQuery<Phone> query = entityManager.createQuery("select ph from Employee e join e.department d join e.phones ph where d.name is not null ", Phone.class);
    List<Phone> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(3);
    assertThat(resultList).extracting("number").containsOnly("111", "222", "333");
}
```

在这里，我们选择具有部门的所有员工的所有手机。与其他内连接类似，我们没有指定条件，因为JPA从映射元数据中提取此信息。

## 7. Fetch连接

现在让我们谈谈Fetch连接。**它们的主要用途是为当前查询[急切地获取延迟加载的关联](https://www.baeldung.com/hibernate-lazy-eager-loading)**。

在这里，我们将急切地加载员工关联：

```java
@Test
public void whenFetchKeywordIsSpecified_ThenCreatesFetchJoin() {
    TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Department d JOIN FETCH d.employees", Department.class);
    List<Department> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(3);
    assertThat(resultList).extracting("name").containsOnly("Infra", "Accounting", "Accounting");
}
```

尽管此查询看起来与其他查询非常相似，但有一个区别；**员工被急切地加载**。这意味着一旦我们在上面的测试中调用getResultList()，Department实体将加载其employees字段，从而为我们节省了另一次访问数据库的时间。

**然而，我们必须意识到内存的权衡**。这样做可能会更有效率，因为我们只执行了一个查询，但我们也同时将所有部门及其员工加载到内存中。

我们还可以以与外连接类似的方式执行外fetch连接，我们从左侧实体中收集与连接条件不匹配的记录。此外，它会急切地加载指定的关联：

```java
@Test
public void whenLeftAndFetchKeywordsAreSpecified_ThenCreatesOuterFetchJoin() {
    TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Department d LEFT JOIN FETCH d.employees", Department.class);
    List<Department> resultList = query.getResultList();
    
    assertThat(resultList).hasSize(4);
    assertThat(resultList).extracting("name").containsOnly("Infra", "Accounting", "Accounting", "Management");
}
```

## 8. 总结

在本文中，我们介绍了JPA连接类型。
