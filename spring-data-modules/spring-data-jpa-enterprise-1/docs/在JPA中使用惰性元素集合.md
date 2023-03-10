## 1. 概述

JPA规范提供了两种不同的获取策略：急切和惰性。虽然惰性方法有助于避免不必要地加载我们不需要的数据，**但有时我们需要读取最初未在关闭的[Persistence Context](https://www.baeldung.com/jpa-hibernate-persistence-context)中加载的数据**。此外，在关闭的Persistence Context中访问惰性元素集合是一个常见问题。

在本教程中，我们将重点介绍如何从惰性元素集合加载数据。我们将探讨三种不同的解决方案：一种涉及JPA查询语言，另一种使用实体图，最后一种涉及事务传播。

## 2. 元素集合问题

**默认情况下，JPA在@ElementCollection类型的关联中使用延迟获取策略**。因此，在关闭的持久性上下文中对集合的任何访问都将导致异常。

为了理解这个问题，让我们根据员工与其电话列表之间的关系定义一个域模型：

```java
@Entity
public class Employee {
    @Id
    private int id;
    private String name;
    @ElementCollection
    @CollectionTable(name = "employee_phone", joinColumns = @JoinColumn(name = "employee_id"))
    private List phones;

    // standard constructors, getters, and setters
}

@Embeddable
public class Phone {
    private String type;
    private String areaCode;
    private String number;

    // standard constructors, getters, and setters
}
```

我们的模型指定一名员工可以拥有多部电话，电话集合是[可嵌入类型的集合](https://www.baeldung.com/jpa-tagging-advanced)。让我们在这个模型中使用Spring Repository：

```java
@Repository
public class EmployeeRepository {

    public Employee findById(int id) {
        return em.find(Employee.class, id);
    }

    // additional properties and auxiliary methods
}
```

现在，让我们用一个简单的JUnit测试用例重现问题：

```java
class ElementCollectionIntegrationTest {

    @Before
    void init() {
        Employee employee = new Employee(1, "Fred");
        employee.setPhones(Arrays.asList(new Phone("work", "+55", "99999-9999"), new Phone("home", "+55", "98888-8888")));
        employeeRepository.save(employee);
    }

    @After
    void clean() {
        employeeRepository.remove(1);
    }

    @Test
    void whenAccessLazyCollection_thenThrowLazyInitializationException() {
        Employee employee = employeeRepository.findById(1);

        assertThrows(LazyInitializationException.class, () -> assertThat(employee.getPhones().size(), is(2)));
    }
}
```

**当我们尝试访问电话列表时，此测试会引发异常，因为PersistenceContext已关闭**。

**我们可以通过将@ElementCollection的获取策略更改为使用eager来解决这个问题**。但是，急切地获取数据不一定是最好的解决方案，因为无论我们是否需要，phones数据总是会被加载。

## 3. 使用JPA查询语言加载数据

**JPA查询语言允许我们自定义投影信息**。因此，我们可以在EmployeeRepository中定义一个新方法来选择员工及其电话：

```java
public Employee findByJPQL(int id) {
    return em.createQuery("SELECT u FROM Employee AS u JOIN FETCH u.phones WHERE u.id=:id", Employee.class)
        .setParameter("id", id).getSingleResult();
}
```

上面的查询**使用内连接操作**来获取返回的每个员工的电话列表。

## 4. 使用实体图加载数据

另一种可能的解决方案是使用JPA中的[实体图功能](https://www.baeldung.com/jpa-entity-graph)。**实体图使我们可以选择JPA查询将投影哪些字段**。

让我们在Repository中再定义一个方法：

```java
public Employee findByEntityGraph(int id) {
    EntityGraph entityGraph = em.createEntityGraph(Employee.class);
    entityGraph.addAttributeNodes("name", "phones");
    Map<String, Object> properties = new HashMap<>();
    properties.put("javax.persistence.fetchgraph", entityGraph);
    return em.find(Employee.class, id, properties);
}
```

可以看到，**我们的实体图包括两个属性：name和phones**。因此，当JPA将其转换为SQL时，它会投射相关列。

## 5. 在事务范围内加载数据

最后，我们将探讨最后一个解决方案。到目前为止，我们已经知道问题的原因是与Persistence Context生命周期有关。

**发生的情况是我们的PersistenceContext是[事务范围](https://www.baeldung.com/jpa-hibernate-persistence-context#transaction_persistence_context)的，并且在事务完成之前将保持打开状态**。事务生命周期从Repository方法的执行开始到结束。

因此，让我们创建另一个测试用例并配置我们的持久性上下文以绑定到由我们的测试方法启动的事务。我们将保持Persistence Context打开直到测试结束：

```java
@Test
@Transactional
void whenUseTransaction_thenFetchResult() {
    Employee employee = employeeRepository.findById(1);
    assertThat(employee.getPhones().size(), is(2));
}
```

**@Transactional注解围绕相关测试类的实例配置事务代理**。此外，事务与执行它的线程相关联。考虑到默认的事务传播设置，从该方法创建的每个持久性上下文都加入到同一个事务中。因此，事务持久性上下文绑定到测试方法的事务范围。

## 6. 总结

在本教程中，**我们介绍了三种不同的解决方案，以解决在关闭的持久性上下文中从惰性关联读取数据的问题**。

首先，我们使用JPA查询语言来获取元素集合。

接下来，我们定义了一个实体图来检索必要的数据。

在最终的解决方案中，我们使用Spring @Transaction来保持Persistence Context打开并读取所需的数据。