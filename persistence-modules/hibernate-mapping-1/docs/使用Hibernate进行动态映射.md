## 1. 简介

在本文中，我们将使用@Formula、@Where、@Filter和@Any注解探索 Hibernate 的一些动态映射功能。

请注意，虽然 Hibernate 实现了 JPA 规范，但此处描述的注解仅在 Hibernate 中可用，不能直接移植到其他 JPA 实现。

## 2.项目设置

为了演示这些功能，我们只需要 hibernate-core 库和一个支持的 H2 数据库：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.12.Final</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.194</version>
</dependency>
```

对于当前版本的hibernate-core库，请前往[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-core")。

## 3. 使用@Formula计算列

假设我们要根据其他一些属性计算实体字段值。一种方法是在我们的Java实体中定义一个计算的只读字段：

```java
@Entity
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long grossIncome;

    private int taxInPercents;

    public long getTaxJavaWay() {
        return grossIncome  taxInPercents / 100;
    }

}
```

明显的缺点是每次我们通过 getter 访问这个虚拟字段时都必须重新计算。

从数据库中获取已经计算出的值会容易得多。这可以通过@Formula注解来完成：

```java
@Entity
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long grossIncome;

    private int taxInPercents;

    @Formula("grossIncome  taxInPercents / 100")
    private long tax;

}
```

使用@Formula，我们可以使用子查询，调用本地数据库函数和存储过程，基本上可以做任何不破坏该字段的SQL select 子句语法的事情。

Hibernate 足够聪明，可以解析我们提供的 SQL 并插入正确的表和字段别名。需要注意的是，由于注解的值是原始 SQL，它可能使我们的映射依赖于数据库。

另外，请记住，该值是在从数据库中获取实体时计算的。因此，当我们持久化或更新实体时，该值不会被重新计算，直到实体从上下文中被逐出并再次加载：

```java
Employee employee = new Employee(10_000L, 25);
session.save(employee);

session.flush();
session.clear();

employee = session.get(Employee.class, employee.getId());
assertThat(employee.getTax()).isEqualTo(2_500L);
```

## 4. 使用@Where过滤实体

假设我们希望在请求某个实体时为查询提供附加条件。

例如，我们需要实现“软删除”。这意味着该实体永远不会从数据库中删除，而只是用布尔字段标记为已删除。

我们必须非常小心地处理应用程序中所有现有和未来的查询。我们必须为每个查询提供这个附加条件。幸运的是，Hibernate 提供了一种在一个地方执行此操作的方法：

```java
@Entity
@Where(clause = "deleted = false")
public class Employee implements Serializable {

    // ...
}
```

方法上的@Where注解包含一个 SQL 子句，该子句将添加到该实体的任何查询或子查询中：

```java
employee.setDeleted(true);

session.flush();
session.clear();

employee = session.find(Employee.class, employee.getId());
assertThat(employee).isNull();
```

与@Formula注解的情况一样，由于我们正在处理原始 SQL，因此在我们将实体刷新到数据库并将其从上下文中逐出之前，不会重新评估@Where条件。

在那之前，实体将保留在上下文中，并且可以通过id查询和查找访问。

@Where注解也可以用于集合字段。假设我们有一个可删除电话列表：

```java
@Entity
public class Phone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean deleted;

    private String number;

}
```

然后，从Employee端，我们可以映射一组可删除电话，如下所示：

```java
public class Employee implements Serializable {
    
    // ...

    @OneToMany
    @JoinColumn(name = "employee_id")
    @Where(clause = "deleted = false")
    private Set<Phone> phones = new HashSet<>(0);

}
```

不同之处在于Employee.phones集合将始终被过滤，但我们仍然可以通过直接查询获取所有电话，包括已删除的电话：

```java
employee.getPhones().iterator().next().setDeleted(true);
session.flush();
session.clear();

employee = session.find(Employee.class, employee.getId());
assertThat(employee.getPhones()).hasSize(1);

List<Phone> fullPhoneList 
  = session.createQuery("from Phone").getResultList();
assertThat(fullPhoneList).hasSize(2);
```

## 5. 使用@Filter 进行参数化过滤

@Where注解的问题在于它允许我们只指定一个不带参数的静态查询，并且不能按需禁用或启用。

@Filter注解的工作方式与@Where相同，但它也可以在会话级别启用或禁用，也可以参数化。

### 5.1. 定义@Filter

为了演示@Filter的工作原理，让我们首先将以下过滤器定义添加到Employee实体：

```java
@FilterDef(
    name = "incomeLevelFilter", 
    parameters = @ParamDef(name = "incomeLimit", type = "int")
)
@Filter(
    name = "incomeLevelFilter", 
    condition = "grossIncome > :incomeLimit"
)
public class Employee implements Serializable {
```

@FilterDef注解定义过滤器名称和一组将参与查询的参数。参数的类型是其中一种 Hibernate 类型([Type](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/type/Type.html)、[UserType](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/usertype/UserType.html)或[CompositeUserType](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/usertype/CompositeUserType.html))的名称，在我们的例子中是一个int。

@FilterDef注解可以放在类型或包级别。请注意，它不指定过滤条件本身(尽管我们可以指定defaultCondition参数)。

这意味着我们可以在一个地方定义过滤器(它的名称和参数集)，然后在多个其他地方以不同的方式定义过滤器的条件。

这可以通过@Filter注解来完成。在我们的例子中，为了简单起见，我们将它放在同一个类中。条件的语法是原始 SQL，参数名称前面有冒号。

### 5.2. 访问过滤的实体

@Filter与@Where的另一个区别是默认情况下不启用@Filter 。我们必须在会话级别手动启用它，并为其提供参数值：

```java
session.enableFilter("incomeLevelFilter")
  .setParameter("incomeLimit", 11_000);
```

现在假设我们在数据库中有以下三个员工：

```java
session.save(new Employee(10_000, 25));
session.save(new Employee(12_000, 25));
session.save(new Employee(15_000, 25));
```

然后启用过滤器，如上所示，通过查询只会看到其中两个：

```java
List<Employee> employees = session.createQuery("from Employee")
  .getResultList();
assertThat(employees).hasSize(2);
```

请注意，已启用的过滤器及其参数值仅在当前会话中应用。在未启用过滤器的新会话中，我们将看到所有三名员工：

```java
session = HibernateUtil.getSessionFactory().openSession();
employees = session.createQuery("from Employee").getResultList();
assertThat(employees).hasSize(3);
```

此外，当直接通过 id 获取实体时，不应用过滤器：

```java
Employee employee = session.get(Employee.class, 1);
assertThat(employee.getGrossIncome()).isEqualTo(10_000);
```

### 5.3. @Filter和二级缓存

如果我们有一个高负载应用程序，那么我们肯定希望启用 Hibernate 二级缓存，这可以带来巨大的性能优势。我们应该记住，@Filter注解不能很好地处理缓存。

二级缓存只保留完整的未过滤集合。如果不是这种情况，那么我们可以在一个启用过滤器的会话中读取一个集合，然后在另一个会话中获取相同的缓存过滤集合，即使过滤器被禁用。

这就是@Filter注解基本上禁用实体缓存的原因。

## 6. 使用@Any映射任何实体引用

有时我们希望将引用映射到多个实体类型中的任何一个，即使它们不基于单个@MappedSuperclass。它们甚至可以映射到不同的不相关表。我们可以使用@Any注解来实现这一点。

在我们的示例中，我们需要为持久性单元中的每个实体附加一些描述，即Employee和Phone。仅仅为了做到这一点而从单个抽象超类继承所有实体是不合理的。

### 6.1. 与@Any 的映射关系

以下是我们如何定义对实现Serializable 的任何实体的引用(即，对任何实体的引用)：

```java
@Entity
public class EntityDescription implements Serializable {

    private String description;

    @Any(
        metaDef = "EntityDescriptionMetaDef",
        metaColumn = @Column(name = "entity_type"))
    @JoinColumn(name = "entity_id")
    private Serializable entity;

}
```

metaDef属性是定义的名称，metaColumn是将用于区分实体类型的列的名称(与单表层次结构映射中的鉴别器列不同)。

我们还指定将引用实体ID的列。值得注意的是，该列不会是外键，因为它可以引用我们想要的任何表。

entity_id列通常也不能是唯一的，因为不同的表可能有重复的标识符。

然而， entity_type / entity_id对应该是唯一的，因为它唯一地描述了我们所指的实体。

### 6.2. 使用@AnyMetaDef定义@Any映射

现在，Hibernate 不知道如何区分不同的实体类型，因为我们没有指定entity_type列可以包含什么。

为了使这项工作有效，我们需要使用@AnyMetaDef注解添加映射的元定义。放置它的最佳位置是包级别，这样我们就可以在其他映射中重用它。

下面是带有@AnyMetaDef注解的package-info.java文件的样子：

```java
@AnyMetaDef(
    name = "EntityDescriptionMetaDef", 
    metaType = "string", 
    idType = "int",
    metaValues = {
        @MetaValue(value = "Employee", targetEntity = Employee.class),
        @MetaValue(value = "Phone", targetEntity = Phone.class)
    }
)
package com.baeldung.hibernate.pojo;
```

此处我们指定了entity_type列的类型( string )、 entity_id列的类型( int )、 entity_type列中可接受的值(“Employee”和“Phone”)以及相应的实体类型。

现在，假设我们有一个员工，他有两部电话，描述如下：

```java
Employee employee = new Employee();
Phone phone1 = new Phone("555-45-67");
Phone phone2 = new Phone("555-89-01");
employee.getPhones().add(phone1);
employee.getPhones().add(phone2);
```

现在我们可以向所有三个实体添加描述性元数据，即使它们具有不同的不相关类型：

```java
EntityDescription employeeDescription = new EntityDescription(
  "Send to conference next year", employee);
EntityDescription phone1Description = new EntityDescription(
  "Home phone (do not call after 10PM)", phone1);
EntityDescription phone2Description = new EntityDescription(
  "Work phone", phone1);
```

## 七. 总结

在本文中，我们探索了一些允许使用原始 SQL 微调实体映射的 Hibernate 注解。