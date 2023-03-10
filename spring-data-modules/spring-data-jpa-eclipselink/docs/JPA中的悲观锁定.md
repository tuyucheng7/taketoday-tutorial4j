## 1. 概述

当我们想要从数据库中检索数据时，有很多情况。有时我们想为自己锁定它以进行进一步处理，这样其他人就无法打断我们的操作。

**我们可以想到两种允许我们这样做的并发控制机制：设置适当的事务隔离级别或对我们当前需要的数据设置锁定**。

事务隔离是为数据库连接定义的。我们可以配置它来保留不同程度的锁定数据。

但是，**一旦连接被创建，隔离级别就会被设置**，它会影响该连接中的每条语句。幸运的是，我们可以使用悲观锁定，它使用数据库机制来保留对数据的更细粒度的独占访问。

我们可以使用悲观锁来确保没有其他事务可以修改或删除保留数据。

我们可以保留两种类型的锁：独占锁和共享锁。当其他人持有共享锁时，我们可以读取但不能写入数据。为了修改或删除保留数据，我们需要有排它锁。

我们可以使用“SELECT ... FOR UPDATE”语句获取独占锁。

## 2. 锁定模式

JPA规范定义了我们将要讨论的三种悲观锁模式：

-   PESSIMISTIC_READ允许我们获取共享锁并防止数据被更新或删除
-   PESSIMISTIC_WRITE允许我们获取独占锁并防止数据被读取、更新或删除
-   PESSIMISTIC_FORCE_INCREMENT的工作方式与PESSIMISTIC_WRITE类似，它还会递增版本化实体的版本属性

它们都是LockModeType类的静态成员，允许事务获取数据库锁。它们都被保留，直到事务提交或回滚。

**值得注意的是，我们一次只能获得一把锁。如果不可能，则抛出PersistenceException**。

### 2.1 PESSIMISTIC_READ

每当我们只想读取数据而不遇到脏读时，我们可以使用PESSIMISTIC_READ(共享锁)。**不过，我们将无法进行任何更新或删除**。

有时我们使用的数据库不支持PESSIMISTIC_READ锁，因此我们改为可以获取PESSIMISTIC_WRITE锁。

### 2.2 PESSIMISTIC_WRITE

任何需要获取数据锁并对其进行更改的事务都应该获取PESSIMISTIC_WRITE锁。根据JPA规范，持有PESSIMISTIC_WRITE锁将阻止其他事务读取、更新或删除数据。

**请注意，某些数据库系统实现了[多版本并发控制](https://en.wikipedia.org/wiki/Multiversion_concurrency_control)，允许读访问获取已经被阻塞的数据**。

### 2.3 PESSIMISTIC_FORCE_INCREMENT

此锁的工作方式与PESSIMISTIC_WRITE类似，但引入它是为了与版本化实体协作-具有用@Version标注的属性的实体。

版本化实体的任何更新都可以在获得PESSIMISTIC_FORCE_INCREMENT锁之前进行。**获取该锁会导致更新版本列**。

由持久层提供者决定它是否支持未版本化实体的PESSIMISTIC_FORCE_INCREMENT。如果没有，它会抛出PersistenceException。

### 2.4 异常

最好知道在使用悲观锁定时可能会发生哪种异常。JPA规范提供了不同类型的异常：

-   PessimisticLockException表示获取锁或将共享锁转换为独占锁失败并导致语句级回滚。
-   LockTimeoutException表示获取锁或将共享锁转换为独占锁超时并导致语句级回滚。
-   PersistenceException表示发生持久性问题。PersistenceException及其子类型(除了NoResultException、NonUniqueResultException、LockTimeoutException和QueryTimeoutException)，**标记要回滚的活动事务**。

## 3. 使用悲观锁

**有几种可能的方法可以在单个记录或一组记录上配置悲观锁**。让我们看看如何在JPA中执行此操作。

### 3.1 查找

查找可能是最直接的方法。

将LockModeType对象作为参数传递给find方法就足够了：

```java
entityManager.find(Student.class, studentId, LockModeType.PESSIMISTIC_READ);
```

### 3.2 查询

此外，我们还可以使用Query对象，并以锁定模式作为参数调用setLockMode setter：

```java
Query query = entityManager.createQuery("from Student where studentId = :studentId");
query.setParameter("studentId", studentId);
query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
query.getResultList()
```

### 3.3 显式锁定

也可以手动锁定find方法检索到的结果：

```java
Student resultStudent = entityManager.find(Student.class, studentId);
entityManager.lock(resultStudent, LockModeType.PESSIMISTIC_WRITE);
```

### 3.4 刷新

如果我们想通过refresh方法覆盖实体的状态，我们也可以设置一个锁：

```java
Student resultStudent = entityManager.find(Student.class, studentId);
entityManager.refresh(resultStudent, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
```

### 3.5 命名查询

@NamedQuery注解也允许我们设置锁定模式：

```java
@NamedQuery(name="lockStudent",
    query="SELECT s FROM Student s WHERE s.id LIKE :studentId",
    lockMode = PESSIMISTIC_READ)
```

## 4. 锁定范围

**锁定范围参数定义了如何处理被锁定实体的锁定关系**。可以仅在查询中定义的单个实体上获得锁定，或者另外阻止其关系。

要配置范围，我们可以使用PessimisticLockScope枚举。它包含两个值：NORMAL和EXTENDED。

我们可以通过将带有PessimisticLockScope值的参数'javax.persistence.lock.scope'传递给EntityManager、Query、TypedQuery或NamedQuery的适当方法来设置范围：

```java
Map<String, Object> properties = new HashMap<>();
map.put("javax.persistence.lock.scope", PessimisticLockScope.EXTENDED);
    
entityManager.find(Student.class, 1L, LockModeType.PESSIMISTIC_WRITE, properties);
```

### 4.1 PessimisticLockScope.NORMAL

**PessimisticLockScope.NORMAL是默认范围**。使用这个锁定范围，我们就锁定了实体本身。当与join继承一起使用时，它还会锁定祖先。

让我们看一下包含两个实体的示例代码：

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {

    @Id
    private Long id;
    private String name;
    private String lastName;

    // getters and setters
}

@Entity
public class Employee extends Person {

    private BigDecimal salary;

    // getters and setters
}
```

当我们想要获得对Employee的锁定时，我们可以观察跨越这两个实体的SQL查询：

```sql
SELECT t0.ID, t0.DTYPE, t0.LASTNAME, t0.NAME, t1.ID, t1.SALARY
FROM PERSON t0, EMPLOYEE t1
WHERE ((t0.ID = ?) AND ((t1.ID = t0.ID) AND (t0.DTYPE = ?))) FOR UPDATE
```

### 4.2 PessimisticLockScope.EXTENDED

EXTENDED范围涵盖与NORMAL相同的功能。此外，**它还能够阻止连接表中的相关实体**。

简而言之，它适用于使用@ElementCollection或@OneToOne、@OneToMany等注解的实体以及@JoinTable。

让我们看一下带有@ElementCollection注解的示例代码：

```java
@Entity
public class Customer {

    @Id
    private Long customerId;
    private String name;
    private String lastName;
    @ElementCollection
    @CollectionTable(name = "customer_address")
    private List<Address> addressList;

    // getters and setters
}

@Embeddable
public class Address {

    private String country;
    private String city;

    // getters and setters
}
```

让我们分析一下搜索客户实体时的一些查询：

```sql
SELECT CUSTOMERID, LASTNAME, NAME 
FROM CUSTOMER WHERE (CUSTOMERID = ?) FOR UPDATE

SELECT CITY, COUNTRY, Customer_CUSTOMERID 
FROM customer_address 
WHERE (Customer_CUSTOMERID = ?) FOR UPDATE
```

我们可以看到有两个FOR UPDATE查询锁定了客户表中的一行以及连接表中的一行。

**另一个需要注意的有趣事实是，并非所有持久性提供程序都支持锁定范围**。

## 5. 设置锁定超时

除了设置锁范围，我们还可以调整另一个锁参数-超时。**超时值是我们希望在发生LockTimeoutException之前等待获取锁的毫秒数**。

我们可以通过使用具有适当毫秒数的属性“javax.persistence.lock.timeout”来更改超时值，类似于锁定范围。

也可以通过将超时值更改为0来指定“无等待”锁定。

但是，我们应该记住，**有些数据库驱动程序不支持以这种方式设置超时值**：

```java
Map<String, Object> properties = new HashMap<>(); 
map.put("javax.persistence.lock.timeout", 1000L); 

entityManager.find(Student.class, 1L, LockModeType.PESSIMISTIC_READ, properties);
```

## 6. 总结

当设置适当的隔离级别不足以应对并发事务时，JPA为我们提供了悲观锁定。它使我们能够隔离和编排不同的事务，这样它们就不会同时访问相同的资源。

为此，我们可以在所讨论的锁类型之间进行选择，然后修改诸如范围或超时之类的参数。

另一方面，我们应该记住，了解数据库锁与了解底层数据库系统的机制同样重要。

同样重要的是要记住，悲观锁的行为取决于我们使用的持久性提供程序(JPA实现者)。