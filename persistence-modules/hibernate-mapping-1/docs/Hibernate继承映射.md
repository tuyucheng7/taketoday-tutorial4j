## 1. 概述

关系数据库没有将类层次结构映射到数据库表的直接方法。

为了解决这个问题，JPA 规范提供了几种策略：

-   MappedSuperclass – 父类，不能是实体
-   单表——来自具有共同祖先的不同类的实体被放置在一个表中。
-   连接表——每个类都有它的表，查询子类实体需要连接表。
-   每个类的表——一个类的所有属性都在它的表中，因此不需要连接。

每种策略都会产生不同的数据库结构。

实体继承意味着我们可以在查询超类时使用多态查询来检索所有子类实体。

由于 Hibernate 是一个 JPA 实现，它包含上述所有内容以及一些与继承相关的特定于 Hibernate 的功能。

在接下来的部分中，我们将更详细地介绍可用的策略。

## 2.映射超类

使用MappedSuperclass策略，继承仅在类中很明显，而在实体模型中不明显。

让我们从创建一个代表父类的Person类开始：

```java
@MappedSuperclass
public class Person {

    @Id
    private long personId;
    private String name;

    // constructor, getters, setters
}
```

请注意，此类不再具有@Entity注解，因为它不会自行保存在数据库中。

接下来，让我们添加一个Employee子类：

```java
@Entity
public class MyEmployee extends Person {
    private String company;
    // constructor, getters, setters 
}
```

在数据库中，这将对应一个MyEmployee 表，其中三列用于子类的声明和继承字段。

如果我们使用此策略，则祖先不能包含与其他实体的关联。

## 3、单表

单表策略为每个类层次结构创建一个表。如果我们没有明确指定，JPA 也会默认选择此策略。

我们可以通过将@Inheritance注解添加到超类来定义我们想要使用的策略：

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class MyProduct {
    @Id
    private long productId;
    private String name;

    // constructor, getters, setters
}
```

实体的标识符也在超类中定义。

然后我们可以添加子类实体：

```java
@Entity
public class Book extends MyProduct {
    private String author;
}
@Entity
public class Pen extends MyProduct {
    private String color;
}
```

### 3.1. 鉴别值

由于所有实体的记录都在同一个表中，Hibernate 需要一种方法来区分它们。

默认情况下，这是通过名为DTYPE的鉴别器列完成的，该列将实体名称作为值。

要自定义鉴别器列，我们可以使用@DiscriminatorColumn注解：

```java
@Entity(name="products")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="product_type", 
  discriminatorType = DiscriminatorType.INTEGER)
public class MyProduct {
    // ...
}
```

在这里，我们选择通过名为product_type的整数列来区分MyProduct子类实体。

接下来，我们需要告诉 Hibernate 每个子类记录对product_type列有什么值：

```java
@Entity
@DiscriminatorValue("1")
public class Book extends MyProduct {
    // ...
}
@Entity
@DiscriminatorValue("2")
public class Pen extends MyProduct {
    // ...
}
```

Hibernate 添加了注解可以采用的另外两个预定义值—— null和not null：

-   @DiscriminatorValue(“null”)表示任何没有鉴别器值的行都会被映射到有这个注解的实体类；这可以应用于层次结构的根类。
-   @DiscriminatorValue(“not null”) – 任何具有不匹配任何与实体定义关联的鉴别器值的行都将映射到具有此注解的类。

除了列，我们还可以使用 Hibernate 特定的@DiscriminatorFormula注解来确定区分值：

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("case when author is not null then 1 else 2 end")
public class MyProduct { ... }
```

这种策略具有多态查询性能的优势，因为在查询父实体时只需要访问一个表。

另一方面，这也意味着我们不能再对子类实体属性使用NOT NULL约束。

## 4.连接表

使用此策略，层次结构中的每个类都映射到其表。在所有表中重复出现的唯一一列是标识符，它将在需要时用于连接它们。

让我们创建一个使用此策略的超类：

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Animal {
    @Id
    private long animalId;
    private String species;

    // constructor, getters, setters 
}
```

那么我们可以简单的定义一个子类：

```java
@Entity
public class Pet extends Animal {
    private String name;

    // constructor, getters, setters
}
```

两个表都有一个animalId标识符列。

Pet实体的主键对其父实体的主键也有一个外键约束。

要自定义此列，我们可以添加@PrimaryKeyJoinColumn注解：

```java
@Entity
@PrimaryKeyJoinColumn(name = "petId")
public class Pet extends Animal {
    // ...
}
```

这种继承映射方法的缺点是检索实体需要表之间的连接，这会导致大量记录的性能降低。

查询父类时连接的数量更高，因为它会与每个相关的子类连接——所以性能更有可能受到影响，我们想要检索记录的层次结构越高。

## 5. 每班一桌

Table per Class 策略将每个实体映射到它的表，该表包含实体的所有属性，包括继承的属性。

生成的模式类似于使用 @MappedSuperclass 的模式。但是 Table per Class 确实会为父类定义实体，因此允许关联和多态查询。

要使用这个策略，我们只需要在基类上加上@Inheritance注解即可：

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Vehicle {
    @Id
    private long vehicleId;

    private String manufacturer;

    // standard constructor, getters, setters
}
```

然后我们可以用标准的方式创建子类。

这与仅映射没有继承的每个实体没有什么不同。查询基类时区别很明显，它会在后台使用UNION语句返回所有子类记录。

选择此策略时，使用UNION也会导致性能下降。另一个问题是我们不能再使用身份密钥生成。

## 6. 多态查询

如前所述，查询基类也将检索所有子类实体。

让我们通过 JUnit 测试看看这种行为：

```java
@Test
public void givenSubclasses_whenQuerySingleTableSuperclass_thenOk() {
    Book book = new Book(1, "1984", "George Orwell");
    session.save(book);
    Pen pen = new Pen(2, "my pen", "blue");
    session.save(pen);

    assertThat(session.createQuery("from MyProduct")
      .getResultList()).size()).isEqualTo(2);
}
```

在此示例中，我们创建了两个Book和Pen对象，然后查询它们的超类MyProduct以验证我们将检索两个对象。

Hibernate 还可以查询不是实体但由实体类扩展或实现的接口或基类。

让我们看看使用我们的@MappedSuperclass示例的 JUnit 测试：

```java
@Test
public void givenSubclasses_whenQueryMappedSuperclass_thenOk() {
    MyEmployee emp = new MyEmployee(1, "john", "baeldung");
    session.save(emp);

    assertThat(session.createQuery(
      "from com.baeldung.hibernate.pojo.inheritance.Person")
      .getResultList())
      .hasSize(1);
}
```

请注意，这也适用于任何超类或接口，无论它是否是@MappedSuperclass 。与通常的 HQL 查询不同的是，我们必须使用完全限定名称，因为它们不是 Hibernate 管理的实体。

如果我们不希望此类查询返回子类，我们只需要在其定义中添加 Hibernate @Polymorphism注解，类型为EXPLICIT：

```java
@Entity
@Polymorphism(type = PolymorphismType.EXPLICIT)
public class Bag implements Item { ...}
```

在这种情况下，查询Items时，不会返回Bag记录。

## 七. 总结

在本文中，我们展示了在 Hibernate 中映射继承的不同策略。