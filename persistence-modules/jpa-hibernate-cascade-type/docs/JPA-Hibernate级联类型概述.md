## 1. 概述

在本教程中，我们将讨论什么是 JPA/Hibernate 中的级联。然后我们将介绍可用的各种级联类型及其语义。

## 延伸阅读：

## [Spring Data JPA 简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)

Spring Data JPA 与 Spring 4 简介 - Spring 配置、DAO、手动和生成的查询以及事务管理。

[阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)→

## [使用 JPA 将实体类名称映射到 SQL 表名称](https://www.baeldung.com/jpa-entity-table-names)

了解默认情况下如何生成表名以及如何覆盖该行为。

[阅读更多](https://www.baeldung.com/jpa-entity-table-names)→

## 2.什么是级联？

实体关系通常取决于另一个实体的存在，例如人-地址关系。没有Person，Address实体本身就没有任何意义。当我们删除Person实体时，我们的Address实体也应该被删除。

级联是实现这一目标的方法。当我们对目标实体执行某些操作时，相同的操作将应用于关联的实体。

### 2.1. JPA 级联类型

所有特定于 JPA 的级联操作都由包含条目的javax.persistence.CascadeType枚举表示：

-   全部
-   坚持
-   合并
-   消除
-   刷新
-   分离

### 2.2. 休眠级联类型

Hibernate 支持三种额外的级联类型以及 JPA 指定的级联类型。这些特定于 Hibernate 的级联类型在org.hibernate.annotations.CascadeType中可用：

-   
-   SAVE_UPDATE
-   锁

## 3.级联类型的区别

### 3.1. 级联类型。全部

CascadeType.ALL 将所有操作——包括特定于 Hibernate 的操作——从父实体传播到子实体。

让我们看一个例子：

```java
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Address> addresses;
}
```

请注意，在OneToMany关联中，我们在注解中提到了级联类型。

现在让我们看看关联的实体Address：

```java
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String street;
    private int houseNumber;
    private String city;
    private int zipCode;
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;
}
```

### 3.2. 级联类型。坚持

持久化操作使瞬态实例持久化。级联类型PERSIST将持久化操作从父实体传播到子实体。当我们保存person实体时，address实体也会被保存。

让我们看一下持久化操作的测试用例：

```java
@Test
public void whenParentSavedThenChildSaved() {
    Person person = new Person();
    Address address = new Address();
    address.setPerson(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();
    session.clear();
}
```

当我们运行上面的测试用例时，我们将看到以下 SQL：

```java
Hibernate: insert into Person (name, id) values (?, ?)
Hibernate: insert into Address (
    city, houseNumber, person_id, street, zipCode, id) values (?, ?, ?, ?, ?, ?)
```

### 3.3. 级联类型。合并

合并操作将给定对象的状态到具有相同标识符的持久对象上。CascadeType.MERGE将合并操作从父实体传播到子实体。

让我们测试合并操作：

```java
@Test
public void whenParentSavedThenMerged() {
    int addressId;
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();
    addressId = address.getId();
    session.clear();

    Address savedAddressEntity = session.find(Address.class, addressId);
    Person savedPersonEntity = savedAddressEntity.getPerson();
    savedPersonEntity.setName("devender kumar");
    savedAddressEntity.setHouseNumber(24);
    session.merge(savedPersonEntity);
    session.flush();
}
```

当我们运行测试用例时，合并操作会生成如下 SQL：

```sql
Hibernate: select address0_.id as id1_0_0_, address0_.city as city2_0_0_, address0_.houseNumber as houseNum3_0_0_, address0_.person_id as person_i6_0_0_, address0_.street as street4_0_0_, address0_.zipCode as zipCode5_0_0_ from Address address0_ where address0_.id=?
Hibernate: select person0_.id as id1_1_0_, person0_.name as name2_1_0_ from Person person0_ where person0_.id=?
Hibernate: update Address set city=?, houseNumber=?, person_id=?, street=?, zipCode=? where id=?
Hibernate: update Person set name=? where id=?
```

在这里，我们可以看到合并操作首先加载地址和人员实体，然后作为CascadeType.MERGE的结果更新两者。

### 3.4. 级联类型.REMOVE

顾名思义，删除操作从数据库中以及持久上下文中删除与实体对应的行。

CascadeType.REMOVE将删除操作从父实体传播到子实体。 类似于 JPA 的 CascadeType.REMOVE，我们有CascadeType.DELETE，它特定于 Hibernate。两者没有区别。

现在是测试CascadeType.Remove的时候了：

```java
@Test
public void whenParentRemovedThenChildRemoved() {
    int personId;
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();
    personId = person.getId();
    session.clear();

    Person savedPersonEntity = session.find(Person.class, personId);
    session.remove(savedPersonEntity);
    session.flush();
}
```

当我们运行测试用例时，我们将看到以下 SQL：

```sql
Hibernate: delete from Address where id=?
Hibernate: delete from Person where id=?
```

由于CascadeType.REMOVE ，与此人关联的地址也被删除。

### 3.5. 级联类型.DETACH

分离操作将实体从持久上下文中移除。当我们使用CascadeType.DETACH时，子实体也将从持久上下文中删除。

让我们看看它的实际效果：

```java
@Test
public void whenParentDetachedThenChildDetached() {
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();
    
    assertThat(session.contains(person)).isTrue();
    assertThat(session.contains(address)).isTrue();

    session.detach(person);
    assertThat(session.contains(person)).isFalse();
    assertThat(session.contains(address)).isFalse();
}
```

在这里，我们可以看到在分离person之后，person和address都不存在于持久上下文中。

### 3.6. 级联类型。锁

不直观的是，CascadeType.LOCK再次将实体及其关联的子实体重新附加到持久性上下文。

让我们看一下测试用例来理解CascadeType.LOCK：

```java
@Test
public void whenDetachedAndLockedThenBothReattached() {
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();
    
    assertThat(session.contains(person)).isTrue();
    assertThat(session.contains(address)).isTrue();

    session.detach(person);
    assertThat(session.contains(person)).isFalse();
    assertThat(session.contains(address)).isFalse();
    session.unwrap(Session.class)
      .buildLockRequest(new LockOptions(LockMode.NONE))
      .lock(person);

    assertThat(session.contains(person)).isTrue();
    assertThat(session.contains(address)).isTrue();
}
```

如我们所见，在使用CascadeType.LOCK时，我们将实体person及其关联地址附加回持久上下文。

### 3.7. 级联类型。刷新

刷新操作从数据库中重新读取给定实例的值。在某些情况下，我们可能会在持久化到数据库后更改实例，但稍后我们需要撤消这些更改。

在那种情况下，这可能会有用。当我们将此操作与级联类型REFRESH一起使用时，每当刷新父实体时，子实体也会从数据库中重新加载。

为了更好地理解，让我们看一个CascadeType.REFRESH的测试用例：

```java
@Test
public void whenParentRefreshedThenChildRefreshed() {
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.persist(person);
    session.flush();
    person.setName("Devender Kumar");
    address.setHouseNumber(24);
    session.refresh(person);
    
    assertThat(person.getName()).isEqualTo("devender");
    assertThat(address.getHouseNumber()).isEqualTo(23);
}
```

在这里，我们对保存的实体person和address进行了一些更改。当我们刷新person实体时，地址也会被刷新。

### 3.8. 级联类型.REPLICATE

当我们有多个数据源并且我们希望数据同步时，使用操作。使用CascadeType.REPLICATE时，只要在父实体上执行同步操作，它也会传播到子实体。

现在让我们测试CascadeType。：

```java
@Test
public void whenParentReplicatedThenChildReplicated() {
    Person person = buildPerson("devender");
    person.setId(2);
    Address address = buildAddress(person);
    address.setId(2);
    person.setAddresses(Arrays.asList(address));
    session.unwrap(Session.class).replicate(person, ReplicationMode.OVERWRITE);
    session.flush();
    
    assertThat(person.getId()).isEqualTo(2);
    assertThat(address.getId()).isEqualTo(2);
}
```

因为级联类型。REPLICATE，当我们个人实体时，其关联地址也会使用我们设置的标识符进行。

### 3.9. CascadeType.SAVE_UPDATE

CascadeType.SAVE_UPDATE将相同的操作传播到关联的子实体。当我们使用特定于 Hibernate 的操作(如save、update 和saveOrUpdate )时，它很有用。 

让我们看看CascadeType。SAVE_UPDATE在行动：

```java
@Test
public void whenParentSavedThenChildSaved() {
    Person person = buildPerson("devender");
    Address address = buildAddress(person);
    person.setAddresses(Arrays.asList(address));
    session.saveOrUpdate(person);
    session.flush();
}
```

因为CascadeType.SAVE_UPDATE ，当我们运行上面的测试用例时，我们可以看到人和地址都被保存了。

下面是生成的 SQL：

```sql
Hibernate: insert into Person (name, id) values (?, ?)
Hibernate: insert into Address (
    city, houseNumber, person_id, street, zipCode, id) values (?, ?, ?, ?, ?, ?)
```

## 4. 总结

在本文中，我们讨论了级联以及 JPA 和 Hibernate 中可用的不同级联类型选项。