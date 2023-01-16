## 1. 简介

在本教程中，我们将讨论使用 JPA 的 @Index注解定义索引。通过示例，我们将学习如何使用 JPA 和 Hibernate 定义我们的第一个索引。之后，我们将修改定义以显示自定义索引的其他方法。

## 2. @Index注解

让我们首先快速回顾一下。数据库索引是一种数据结构，它以额外的写入和存储空间为代价来提高对表的数据检索操作的速度。大多数情况下，它是从单个表中选择的数据列的副本。我们应该创建索引来提高持久层的性能。

JPA 允许我们通过使用@Index从我们的代码中定义索引来实现这一点。此注解由模式生成过程解释，自动创建工件。请注意，没有必要为我们的实体指定任何索引。

现在，让我们看一下定义。

### 2.1. javax.persistence.Index

索引支持最终由[javax.persistence.Index](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/Index.html)添加到 JPA 2.1 规范中。这个注解让我们为我们的表定义一个索引并相应地定制它：

```java
@Target({})
@Retention(RUNTIME)
public @interface Index {
    String name() default "";
    String columnList();
    boolean unique() default false;
}
```

正如我们所见，只有columnList属性是必需的，我们必须对其进行定义。稍后我们将通过示例更好地了解每个参数。

这里要注意的一个方面是注解不支持更改默认索引算法 — btree。

### 2.2. JPA 与 Hibernate

我们知道JPA只是一个规范。为了正常工作，我们还需要指定持久性提供程序。默认情况下，Hibernate Framework 是由 Spring 提供的 JPA 实现。更多相关信息，你可以[在此处](https://www.baeldung.com/spring-boot-hibernate)阅读。

我们应该记住，索引支持很晚才添加到 JPA 中。在此之前，许多 ORM 框架通过引入自己的自定义实现来支持索引，这可能会有所不同。Hibernate Framework 也做到了，并引入了[org.hibernate.annotations.Index](https://docs.jboss.org/hibernate/orm/5.4/javadocs/org/hibernate/annotations/Index.html)注解。在使用该框架时，我们必须注意它已被弃用，因为 JPA 2.1 规范支持，我们应该使用 JPA 的框架。

现在，当我们有了一些技术背景后，我们可以通过示例并在 JPA 中定义我们的第一个索引。

## 3.定义@Index

在本节中，我们将实现我们的索引。稍后，我们将尝试修改它，呈现不同的定制可能性。

在我们开始之前，我们需要[正确地初始化我们的项目并定义一个模型](https://www.baeldung.com/jpa-entities)。

让我们实现一个 Student实体：

```java
@Entity
@Table
public class Student implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;

    // getters, setters
}
```

有了模型后，让我们实施第一个索引。我们所要做的就是添加一个@Index注解。我们在indexes属性下的[@Table](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/Table.html)注解中执行此操作。让我们记住指定列的名称：

```java
@Table(indexes = @Index(columnList = "firstName"))
```

我们已经使用firstName列声明了第一个索引。当我们执行模式创建过程时，我们可以验证它：

```sas
[main] DEBUG org.hibernate.SQL -
  create index IDX2gdkcjo83j0c2svhvceabnnoh on Student (firstName)
```

现在，是时候修改我们的声明以显示其他功能了。

### 3.1. @索引名称

如我们所见，我们的索引必须有一个名称。默认情况下，如果我们没有指定，它就是提供者生成的值。当我们想要自定义标签时，我们应该简单地添加name属性：

```java
@Index(name = "fn_index", columnList = "firstName")
```

此变体创建一个具有用户定义名称的索引：

```sas
[main] DEBUG org.hibernate.SQL -
  create index fn_index on Student (firstName)
```

此外，我们可以通过在名称中指定架构名称来在不同架构中创建索引：

```
@Index(name = "schema2.fn_index", columnList = "firstName")
```

### 3.2. 多列@Index

现在，让我们仔细看看columnList语法：

```sql
column ::= index_column [,index_column]
index_column ::= column_name [ASC | DESC]
```

正如我们已经知道的，我们可以指定要包含在索引中的列名。当然，我们可以为单个索引指定多个列。我们通过用逗号分隔名称来做到这一点：

```java
@Index(name = "mulitIndex1", columnList = "firstName, lastName")

@Index(name = "mulitIndex2", columnList = "lastName, firstName")
[main] DEBUG org.hibernate.SQL -
  create index mulitIndex1 on Student (firstName, lastName)
   
[main] DEBUG org.hibernate.SQL -
  create index mulitIndex2 on Student (lastName, firstName)
```

请注意，持久性提供程序必须遵守指定的列顺序。在我们的示例中，索引略有不同，即使它们指定了同一组列。

### 3.3. @索引顺序

正如我们回顾上一节中的语法一样，我们还可以 在column_name之后指定ASC(升序)和DESC(降序)值。我们用它来设置索引列中值的排序顺序：

```java
@Index(name = "mulitSortIndex", columnList = "firstName, lastName DESC")
[main] DEBUG org.hibernate.SQL -
  create index mulitSortIndex on Student (firstName, lastName desc)
```

我们可以指定每一列的顺序。如果我们不这样做，则假定为升序。

### 3.4. @Index唯一性

最后一个可选参数是unique属性，定义索引是否唯一。唯一索引确保索引字段不存储重复值。默认情况下，它是false。如果我们想改变它，我们可以声明：

```java
@Index(name = "uniqueIndex", columnList = "firstName", unique = true)
[main] DEBUG org.hibernate.SQL -
  alter table Student add constraint uniqueIndex unique (firstName)
```

当我们以这种方式创建索引时，我们在我们的列上添加了一个唯一性约束，类似地，[@Column](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/Column.html)注解上的唯一属性是如何做的。由于可以声明多列唯一约束，@ Index优于@Column ：

```java
@Index(name = "uniqueMulitIndex", columnList = "firstName, lastName", unique = true)
```

### 3.5. 单个实体上的多个@Index

到目前为止，我们已经实现了索引的不同变体。当然，我们不限于在实体上声明单个索引。让我们收集我们的声明并立即指定每个索引。我们通过在大括号中重复@Index注解并用逗号分隔来做到这一点：

```less
@Entity
@Table(indexes = {
  @Index(columnList = "firstName"),
  @Index(name = "fn_index", columnList = "firstName"),
  @Index(name = "mulitIndex1", columnList = "firstName, lastName"),
  @Index(name = "mulitIndex2", columnList = "lastName, firstName"),
  @Index(name = "mulitSortIndex", columnList = "firstName, lastName DESC"),
  @Index(name = "uniqueIndex", columnList = "firstName", unique = true),
  @Index(name = "uniqueMulitIndex", columnList = "firstName, lastName", unique = true)
})
public class Student implements Serializable
```

更重要的是，我们还可以为同一组列创建多个索引。

### 3.6. 首要的关键

当我们谈论索引时，我们必须在主键上停下来。正如我们所知，由[EntityManager](https://www.baeldung.com/hibernate-entitymanager)管理的每个实体都必须指定一个映射到主键的标识符。

通常，主键是一种特定类型的唯一索引。值得补充的是，我们不必以前面介绍的方式声明此键的定义。一切都由[@Id](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/Id.html)注解自动完成。

### 3.7. 非实体@Index

在我们学习了实现索引的不同方法之后，我们应该提到@Table并不是唯一指定它们的地方。同样的，我们可以在@SecondaryTable、 @CollectionTable、@JoinTable、@ TableGenerator注解中声明索引。本文不涉及这些示例。有关详细信息，请查看[javax.persistence ](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/package-summary.html)[JavaDoc](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/package-summary.html)。

## 4. 总结

在本文中，我们讨论了使用 JPA 声明索引。我们首先回顾了关于它们的一般知识。后来我们实现了我们的第一个索引，并通过示例学习了如何通过更改名称、包含的列、顺序和唯一性来自定义它。最后，我们讨论了主键以及我们可以声明它们的其他方式和位置。