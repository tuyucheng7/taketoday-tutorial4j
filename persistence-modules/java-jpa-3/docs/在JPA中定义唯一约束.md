## 1. 简介

在本教程中，我们将讨论使用JPA 和 Hibernate定义唯一约束 。

首先，我们将探索唯一约束以及它们与主键约束的区别。

然后我们再看看JPA的重要注解，@ Column(unique=true)和@UniqueConstraint。我们将实施它们以定义对单列和多列的唯一约束。

最后，我们将学习如何在引用的表列上定义唯一约束。

## 2.独特的约束

让我们从快速回顾开始。唯一键是表的一组单列或多列，用于唯一标识数据库表中的记录。

唯一键约束和主键约束都为一列或一组列的唯一性提供了保证。

### 2.1. 它与主键约束有何不同？

唯一约束确保一列或列组合中的数据对于每一行都是唯一的。例如，表的主键用作隐式唯一约束。因此，主键约束自动具有唯一约束。

此外，每个表只能有一个主键约束。但是，每个表可以有多个唯一约束。
简而言之，除了主键映射所带来的任何约束之外，唯一约束也适用。

我们定义的唯一约束在表创建期间用于生成适当的数据库约束，也可以在运行时用于对插入、更新或删除 语句进行排序。

### 2.2. 什么是单列和多列约束？

唯一约束可以是列约束或表约束。在表级别，我们可以定义跨多个列的唯一约束。

JPA 允许我们使用@Column(unique=true)和@UniqueConstraint在代码中定义唯一约束。这些注解由模式生成过程解释，自动创建约束。

首先，我们应该强调列级约束适用于单个列，表级约束适用于整个表。

我们将在下一节中更详细地讨论这一点。

## 3. 设置实体

JPA 中的[实体](https://www.baeldung.com/jpa-entities)表示存储在数据库中的表。实体的每个实例代表表中的一行。

让我们从创建域实体并将其映射到数据库表开始。对于这个例子，我们将创建一个Person实体：

```java
@Entity
@Table
public class Person implements Serializable {
    @Id
    @GeneratedValue
    private Long id;  
    private String name;
    private String password;
    private String email;
    private Long personNumber;
    private Boolean isActive;
    private String securityNumber;
    private String departmentCode;
    @JoinColumn(name = "addressId", referencedColumnName = "id")
    private Address address;
   //getters and setters
 }

```

地址字段是地址实体中的引用字段：

```java
@Entity
@Table
public class Address implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String streetAddress;
    //getters and setters
}
```

在本教程中，我们将使用此Person实体来演示我们的示例。

## 4.列约束

当我们准备好模型时，我们可以实施我们的第一个唯一约束。

让我们考虑一下保存个人信息的Person实体。我们有一个id列的主键。该实体还包含不包含任何重复值的PersonNumber 。另外，我们不能定义主键，因为我们的表已经有了。

在这种情况下，我们可以使用列唯一约束来确保没有在PersonNumber字段中输入重复值。JPA 允许我们使用具有唯一属性的@Column注解来实现这一点。

在接下来的部分中，我们将了解@Column注解，然后学习如何实现它。

### 4.1. @Column(唯一=真)

注解类型[Column](https://docs.jboss.org/hibernate/jpa/2.1/api/javax/persistence/Column.html)用于指定持久属性或字段的映射列。

让我们看一下定义：

```java
@Target(value={METHOD,FIELD})
@Retention(value=RUNTIME)
public @interface Column {
    boolean unique;
   //other elements
 }

```

unique属性指定该列是否是唯一键。这是UniqueConstraint注解的快捷方式，在唯一键约束仅对应于单个列时很有用。

我们将在下一节中看到如何定义它。

### 4.2. 定义列约束

每当唯一约束仅基于一个字段时，我们可以在该列上使用@Column(unique=true) 。

让我们在personNumber字段上定义一个唯一约束：

```java
@Column(unique=true)
private Long personNumber;
```

当我们执行模式创建过程时，我们可以从日志中验证它：

```java
[main] DEBUG org.hibernate.SQL -
    alter table Person add constraint UK_d44q5lfa9xx370jv2k7tsgsqt unique (personNumber)
```

同样，如果我们想限制一个人注册一个唯一的邮箱，我们可以在email字段上添加一个唯一的约束：

```java
@Column(unique=true)
private String email;
```

让我们执行模式创建过程并检查约束：

```java
[main] DEBUG org.hibernate.SQL -
    alter table Person add constraint UK_585qcyc8qh7bg1fwgm1pj4fus unique (email)
```

虽然这在我们想要对单个列施加唯一约束时很有用，但有时我们可能希望对组合键(即列的组合)添加唯一约束。要定义复合唯一键，我们可以使用表约束。我们将在下一节中讨论。

## 5.表约束

复合唯一键是由列组合组成的唯一键。要定义复合唯一键，我们可以在表而不是列上添加约束。JPA 使用@UniqueConstraint注解帮助我们实现这一点。

### 5.1. @UniqueConstraint注解

注解类型[UniqueConstraint](https://docs.jboss.org/hibernate/jpa/2.1/api/javax/persistence/UniqueConstraint.html)指定唯一约束将包含在为表生成的 DDL(数据定义语言)中。

让我们看一下定义：

```java
@Target(value={})
@Retention(value=RUNTIME)
public @interface UniqueConstraint {
    String name() default "";
    String[] columnNames();
}
```

正如我们所见，类型String和String[]的name和columnNames分别是可以为UniqueConstraint注解指定的注解元素。

我们将通过示例更好地了解下一节中的每个参数。

### 5.2. 定义唯一约束

让我们考虑一下我们的Person实体。一个人不应该有任何重复的活动状态记录。换句话说，包含personNumber和isActive的键不会有任何重复值。在这里，我们需要添加跨越多个列的唯一约束。

JPA 使用@UniqueConstraint注解帮助我们实现这一点。我们在uniqueConstraints属性下的[@Table](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/Table.html) 注解中使用它。让我们记住指定列的名称：

```java
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "personNumber", "isActive" }) })
```

我们可以在模式生成后对其进行验证：

```java
[main] DEBUG org.hibernate.SQL -
    alter table Person add constraint UK5e0bv5arhh7jjhsls27bmqp4a unique (personNumber, isActive)
```

这里要注意的一点是，如果我们不指定名称，它就是提供者生成的值。 从 JPA 2.0 开始，我们可以为我们的唯一约束提供一个名称：

```java
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueNumberAndStatus", columnNames = { "personNumber", "isActive" }) })
```

我们可以验证相同的：

```java
[main] DEBUG org.hibernate.SQL -
    alter table Person add constraint UniqueNumberAndStatus unique (personNumber, isActive)
```

在这里，我们在一组列上添加了唯一约束。我们还可以添加多个唯一约束，即对多组列进行唯一约束。我们将在下一节中这样做。

### 5.3. 单个实体的多个唯一约束

一个表可以有多个唯一约束。在上一节中，我们定义了复合键的唯一约束：personNumber和isActive状态。在本节中，我们将对securityNumber和departmentCode的组合添加约束。

让我们收集我们的唯一索引并立即指定它们。我们通过在大括号中重复@UniqueConstraint注解并用逗号分隔来做到这一点：

```java
@Table(uniqueConstraints = {
   @UniqueConstraint(name = "UniqueNumberAndStatus", columnNames = {"personNumber", "isActive"}),
   @UniqueConstraint(name = "UniqueSecurityAndDepartment", columnNames = {"securityNumber", "departmentCode"})})
```

现在让我们看看日志，并检查约束：

```java
[main] DEBUG org.hibernate.SQL -
    alter table Person add constraint UniqueNumberAndStatus unique (personNumber, isActive)
[main] DEBUG org.hibernate.SQL -
   alter table Person add constraint UniqueSecurityAndDepartment unique (securityNumber, departmentCode)
```

到目前为止，我们对同一实体中的字段定义了唯一约束。但是在某些情况下，我们可能引用了其他实体的字段，需要保证这些字段的唯一性。我们将在下一节中讨论。

## 6. 引用表列的唯一约束

当我们创建两个或多个相互关联的表时，它们通常通过一个表中的列引用另一个表的主键来关联。该列称为“外键”。例如，Person和Address实体通过addressId字段连接。因此，addressId充当引用的表列。

我们可以在引用的列上定义唯一约束。我们将首先在单列上实现它，然后在多列上实现。

### 6.1. 单列约束

在我们的Person实体中，我们有一个引用Address实体的地址字段。一个人应该有一个唯一的地址。

因此，让我们在Person的地址字段上定义一个唯一约束：

```java
@Column(unique = true)
private Address address;
```

现在让我们快速检查一下这个约束：

```java
[main] DEBUG org.hibernate.SQL -
   alter table Person add constraint UK_7xo3hsusabfaw1373oox9uqoe unique (address)
```

我们还可以在引用的表列上定义多个列约束，我们将在下一节中看到。

### 6.2. 多列约束

我们可以对列的组合指定唯一约束。如前所述，我们可以使用表约束来做到这一点。

让我们在personNumber和address上定义唯一约束，并将其添加到uniqueConstraints数组：

```java
@Entity
@Table(uniqueConstraints = 
  { //other constraints
  @UniqueConstraint(name = "UniqueNumberAndAddress", columnNames = { "personNumber", "address" })})
```

最后，让我们看看独特的约束：

```java
[main] DEBUG org.hibernate.SQL -
    alter table Person add constraint UniqueNumberAndAddress unique (personNumber, address)
```

## 七. 总结

唯一约束防止两个记录在一列或一组列中具有相同的值。

在本文中，我们了解了如何在 JPA 中定义唯一约束。首先，我们对独特的限制进行了一些回顾。然后我们讨论了@Column(unique=true)和@UniqueConstraint注解分别定义单列和多列的唯一约束。