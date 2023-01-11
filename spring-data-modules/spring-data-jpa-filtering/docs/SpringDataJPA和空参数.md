## 1. 概述

在本教程中，我们演示如何在[Spring Data JPA]()中处理空参数。

在某些情况下，当我们通过参数执行查询时，我们希望得到字段值为null的记录；而有些时候，我们希望忽略空值并跳过查询中的该字段，本文我们介绍如何实现其中的每一个。

## 2. 快速示例

假设我们有一个Customer实体：

```java
@Entity
public class Customer {

	@Id
	@GeneratedValue
	private long id;
	private String name;
	private String email;

	public Customer(String name, String email) {
		this.name = name;
		this.email = email;
	}

	// getters/setters
}
```

此外，还有一个JPA Repository：

```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	// method1
	// method2
}
```

我们希望通过姓名和电子邮件查找客户。为此，我们将编写两种以不同方式处理空参数的方法。

## 3. 处理空参数的方法

首先，我们添加一个将参数的空值解释为IS NULL子句的方法；然后我们创建一个忽略空参数并将它们从WHERE子句中排除的方法。

### 3.1 IS NULL查询

第一种方法非常简单，因为默认情况下查询方法中的null参数已经是被解释为IS NULL：

```java
List<Customer> findByNameAndEmail(String name, String email);
```

现在，如果我们传递一个空电子邮件，生成的JPQL将包含IS NULL条件：

```sql
customer0_.email is null
```

为了证明这一点，我们添加一个测试。首先，我们保存一些用于测试的Customer数据：

```java
@BeforeEach
void before() {
	entityManager.persist(new Customer("A", "A@example.com"));
	entityManager.persist(new Customer("D", null));
	entityManager.persist(new Customer("D", "D@example.com"));
}
```

现在，我们将“D”作为name参数的值和null作为email参数的值传递给我们的findByNameAndEmail方法，我们可以看到返回的结果中只包含一个客户：

```java
List<Customer> customers = repository.findByNameAndEmail("D", null);

assertEquals(1, customers.size());

Customer actual = customers.get(0);

assertEquals(null, actual.getEmail());
assertEquals("D", actual.getName());
```

### 3.2 使用替代方法避免空参数

有时我们希望忽略一些参数，而不在WHERE子句中包含它们对应的字段。例如，要忽略email，我们可以添加一个只接收name的方法：

```java
 List<Customer> findByName(String name);
```

但是这种忽略一个列的方式会随着数量的增加而很难扩展，因为我们必须添加许多方法来实现所有的组合。

### 3.3 使用@Query注解忽略空参数

我们可以通过使用@Query注解并向JPQL语句引入一个小的变化来避免创建额外的方法：

```java
@Query("SELECT c FROM Customer c WHERE (:name is null or c.name = :name) and (:email is null or c.email = :email)")
List<Customer> findCustomerByNameAndEmail(@Param("name") String name, @Param("email") String email);
```

请注意，如果email参数为null，则该子句始终为真，因此不会影响整个WHERE子句： 

```sql
:email is null or s.email = :email
```

下面是一个简单的测试：

```java
@Test
void givenQueryAnnotation_whenEmailIsNull_thenIgnoreEmail() {
	List<Customer> customers = repository.findCustomerByNameAndEmail("D", null);
    
	assertEquals(2, customers.size());
}
```

我们得到两个名字为“D”的客户忽略了他们的电子邮件，生成的JPQL的WHERE子句如下所示：

```sql
where (? is null or customer0_.name=?) and (? is null or customer0_.email=?)
```

使用这种方法，我们相信数据库服务器能够识别关于我们的查询参数为null的子句，并优化查询的执行计划，这样它就不会产生显著的性能开销。对于某些查询或数据库服务器，尤其是涉及巨大的表扫描时，可能会产生性能开销。

## 4. 总结

在本文中，我们演示了Spring Data JPA如何解释查询方法中的空参数以及如何更改默认行为。

也许在未来，我们能够使用[@NullMeans](https://jira.spring.io/browse/DATAJPA-209)注解指定如何解释空参数。请注意，这是目前提出的一项功能，仍在考虑中。

总而言之，有两种主要的方法来解释null参数，它们都将由提议的@NullMeans注解提供：

-   IS(is null)：3.1节中演示的默认方法。
-   IGNORED(从WHERE子句中排除空参数)：通过额外的查询方法(第3.2节)或使用变通方法(第3.3节)实现