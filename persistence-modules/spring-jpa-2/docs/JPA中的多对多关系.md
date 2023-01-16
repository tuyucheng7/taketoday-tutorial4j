## 1. 概述

在本教程中，我们将看到使用 JPA 处理多对多关系的多种方法。

我们将使用学生、课程以及它们之间的各种关系的模型。

为了简单起见，在代码示例中，我们将只显示与多对多关系相关的属性和 JPA 配置。

## 延伸阅读：

## [使用 JPA 将实体类名称映射到 SQL 表名称](https://www.baeldung.com/jpa-entity-table-names)

了解默认情况下如何生成表名以及如何覆盖该行为。

[阅读更多](https://www.baeldung.com/jpa-entity-table-names)→

## [JPA/Hibernate 级联类型概述](https://www.baeldung.com/jpa-cascade-types)

JPA/Hibernate 级联类型的快速实用概述。

[阅读更多](https://www.baeldung.com/jpa-cascade-types)→

## 2. 基本的多对多

### 2.1. 建模多对多关系

关系是两种类型的实体之间的连接。在多对多关系的情况下，双方都可以与另一方的多个实例相关。

请注意，实体类型可能与自身存在关系。想想家谱建模的例子：每个节点都是一个人，所以如果我们谈论父子关系，两个参与者都是一个人。

但是，无论我们谈论单个实体类型还是多个实体类型之间的关系，都没有什么区别。由于考虑两种不同实体类型之间的关系更容易，我们将使用它来说明我们的案例。

让我们以学生标记他们喜欢的课程为例。

一个学生可以喜欢多门课程，许多学生可以喜欢同一门课程：

[![更简单](https://www.baeldung.com/wp-content/uploads/2018/11/simple-er.png)](https://www.baeldung.com/wp-content/uploads/2018/11/simple-er.png)

众所周知，在 RDBMS 中，我们可以创建与外键的关系。由于双方都应该能够引用对方，我们需要创建一个单独的表来保存外键：

[![简单模型更新](https://www.baeldung.com/wp-content/uploads/2018/11/simple-model-updated.png)](https://www.baeldung.com/wp-content/uploads/2018/11/simple-model-updated.png)

这样的表称为连接表。在连接表中，外键的组合将成为其复合主键。

### 2.2. JPA 中的实现

使用 POJO 建模多对多关系很容易。我们应该在两个类中都包含一个Collection，其中包含其他类的元素。

之后，我们需要用@Entity标记类，用@Id标记 主键，使它们成为正确的JPA 实体。

此外，我们应该配置关系类型。所以，我们用@ManyToMany注解标记集合：

```java
@Entity
class Student {

    @Id
    Long id;

    @ManyToMany
    Set<Course> likedCourses;

    // additional properties
    // standard constructors, getters, and setters
}

@Entity
class Course {

    @Id
    Long id;

    @ManyToMany
    Set<Student> likes;

    // additional properties
    // standard constructors, getters, and setters
}
```

此外，我们必须配置如何在 RDBMS 中为关系建模。

所有者端是我们配置关系的地方。我们将使用Student类。

我们可以在Student类中使用@JoinTable注解来做到这一点。我们提供连接表的名称 ( course_like ) 以及带有 @JoinColumn 注解的外键。joinColumn属性将连接到关系的所有者端，而inverseJoinColumn将连接到另一端：

```java
@ManyToMany
@JoinTable(
  name = "course_like", 
  joinColumns = @JoinColumn(name = "student_id"), 
  inverseJoinColumns = @JoinColumn(name = "course_id"))
Set<Course> likedCourses;
```

请注意，不需要使用@JoinTable 甚至@JoinColumn。JPA 将为我们生成表名和列名。但是，JPA 使用的策略并不总是与我们使用的命名约定相匹配。因此，我们需要能够配置表名和列名。

在目标端，我们只需要提供映射关系的字段名称。

因此，我们在Course类中设置@ManyToMany注解的mappedBy属性：

```java
@ManyToMany(mappedBy = "likedCourses")
Set<Student> likes;
```

请记住，由于多对多关系在数据库中没有所有者端，我们可以在Course类中配置连接表并从Student类中引用它。

## 3. 多对多使用复合键

### 3.1. 建模关系属性

假设我们想让学生对课程进行评分。一个学生可以评价任意数量的课程，任意数量的学生也可以评价同一门课程。因此，它也是一个多对多的关系。

让这个例子有点复杂的是评级关系比它存在的事实更多。我们需要存储学生在课程中给出的评分。

我们可以在哪里存储这些信息？我们不能将它放在Student实体中，因为学生可以对不同的课程给出不同的评分。同样，将其存储在Course实体中也不是一个好的解决方案。

这是关系本身具有属性的情况。

使用此示例，将属性附加到关系在 ER 图中如下所示：

[![关系属性](https://www.baeldung.com/wp-content/uploads/2018/11/relation-attribute-er.png)](https://www.baeldung.com/wp-content/uploads/2018/11/relation-attribute-er.png)

我们可以用几乎与简单的多对多关系相同的方式对其建模。唯一的区别是我们将一个新属性附加到连接表：

[![关系属性模型已更新](https://www.baeldung.com/wp-content/uploads/2018/11/relation-attribute-model-updated.png)](https://www.baeldung.com/wp-content/uploads/2018/11/relation-attribute-model-updated.png)

### 3.2. 在 JPA 中创建复合键

简单的多对多关系的实现相当简单。唯一的问题是我们不能以这种方式将属性添加到关系中，因为我们直接连接了实体。因此，我们无法为关系本身添加属性。

由于我们将 DB 属性映射到 JPA 中的类字段，因此我们需要为关系创建一个新的实体类。

当然，每个 JPA 实体都需要一个主键。因为我们的主键是复合键，所以我们必须创建一个新类来保存键的不同部分：

```java
@Embeddable
class CourseRatingKey implements Serializable {

    @Column(name = "student_id")
    Long studentId;

    @Column(name = "course_id")
    Long courseId;

    // standard constructors, getters, and setters
    // hashcode and equals implementation
}
```

请注意，复合键类必须满足一些关键要求：

-   我们必须用@Embeddable标记它。
-   它必须实现java.io.Serializable。
-   我们需要提供hashcode()和equals()方法的实现。
-   这些字段本身都不能是实体。

### 3.3. 在 JPA 中使用复合键

使用这个复合键类，我们可以创建实体类，它模拟连接表：

```java
@Entity
class CourseRating {

    @EmbeddedId
    CourseRatingKey id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    Student student;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    Course course;

    int rating;
    
    // standard constructors, getters, and setters
}
```

此代码与常规实体实现非常相似。但是，我们有一些关键的区别：

-   我们使用@EmbeddedId 来标记主键，它是CourseRatingKey类的一个实例。
-   我们用@MapsId标记了学生和课程字段。

@MapsId意味着我们将这些字段绑定到键的一部分，它们是多对一关系的外键。我们需要它，因为正如我们提到的，我们不能在组合键中包含实体。

在此之后，我们可以像以前一样在Student和Course实体中配置反向引用：

```java
class Student {

    // ...

    @OneToMany(mappedBy = "student")
    Set<CourseRating> ratings;

    // ...
}

class Course {

    // ...

    @OneToMany(mappedBy = "course")
    Set<CourseRating> ratings;

    // ...
}
```

请注意，还有一种使用复合键的替代方法：[@IdClass](https://www.baeldung.com/hibernate-identifiers)注解。

### 3.4. 更多特征

我们将与Student和Course类的关系配置为@ManyToOne。我们可以这样做是因为使用新实体，我们在结构上将多对多关系分解为两个多对一关系。

为什么我们能够做到这一点？如果我们仔细检查前一个案例中的表，我们可以看到它包含两个多对一关系。换句话说，RDBMS 中没有任何多对多关系。我们将使用连接表创建的结构称为多对多关系，因为这就是我们建模的对象。

此外，如果我们谈论多对多关系会更清楚，因为这是我们的意图。同时，连接表只是一个实现细节；我们并不真正关心它。

此外，此解决方案还有一个我们尚未提及的附加功能。简单的多对多解决方案在两个实体之间创建关系。因此，我们无法将关系扩展到更多实体。但是我们在这个解决方案中没有这个限制：我们可以对任意数量的实体类型之间的关系进行建模。

例如，当多个教师可以教授一门课程时，学生可以评价特定教师教授特定课程的方式。这样一来，评级将成为三个实体之间的关系：学生、课程和教师。

## 4. 与新实体的多对多

### 4.1. 建模关系属性

假设我们想让学生注册课程。此外，我们需要存储学生注册特定课程的时间点。最重要的是，我们要存储她在课程中获得的成绩。

在理想情况下，我们可以使用之前的解决方案来解决这个问题，我们有一个带有复合键的实体。然而，世界远非理想，学生并不总是在第一次尝试时就完成一门课程。

在这种情况下，具有相同student_id-course_id对的相同 student-course pairs或多行之间存在多个连接。我们无法使用之前的任何解决方案对其进行建模，因为所有主键都必须是唯一的。所以，我们需要使用一个单独的主键。

因此，我们可以引入一个 entity，它将保存注册的属性：

[![关系实体更新](https://www.baeldung.com/wp-content/uploads/2018/11/relation-entity-er-updated.png)](https://www.baeldung.com/wp-content/uploads/2018/11/relation-entity-er-updated.png)

在这种情况下，Registration 实体表示其他两个实体之间的关系。

因为它是一个实体，所以它有自己的主键。

在前面的解决方案中，请记住我们有一个从两个外键创建的复合主键。

现在这两个外键将不再是主键的一部分：

[![关系实体模型已更新](https://www.baeldung.com/wp-content/uploads/2018/11/relation-entity-model-updated.png)](https://www.baeldung.com/wp-content/uploads/2018/11/relation-entity-model-updated.png)

### 4.2. JPA 中的实现

由于course_registration成为常规表，我们可以创建一个普通的旧 JPA 实体对其进行建模：

```java
@Entity
class CourseRegistration {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;

    LocalDateTime registeredAt;

    int grade;
    
    // additional properties
    // standard constructors, getters, and setters
}
```

我们还需要配置Student和Course类中的关系：

```java
class Student {

    // ...

    @OneToMany(mappedBy = "student")
    Set<CourseRegistration> registrations;

    // ...
}

class Course {

    // ...

    @OneToMany(mappedBy = "course")
    Set<CourseRegistration> registrations;

    // ...
}
```

同样，我们之前配置了关系，所以我们只需要告诉 JPA 它在哪里可以找到该配置。

我们还可以使用此解决方案来解决之前的学生评分课程问题。但是，除非必须，否则创建专用主键感觉很奇怪。

此外，从 RDBMS 的角度来看，这没有多大意义，因为将两个外键组合在一起构成了一个完美的复合键。此外，该复合键具有明确的含义：我们在关系中连接了哪些实体。

否则，这两种实现之间的选择通常只是个人偏好。

## 5.总结

在本文中，我们了解了什么是多对多关系以及我们如何使用 JPA 在 RDBMS 中对其建模。

我们看到了三种在 JPA 中对其建模的方法。在这些方面，这三者各有优缺点：

-   代码清晰度
-   数据库清晰度
-   为关系分配属性的能力
-   我们可以将多少实体与关系联系起来
-   支持相同实体之间的多个连接