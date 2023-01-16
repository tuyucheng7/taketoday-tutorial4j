## 1. 简介

在本教程中，我们将了解实体的基础知识，以及在 JPA 中定义和自定义实体的各种注解。

## 2.实体

JPA 中的实体只不过是表示可以持久保存到数据库的数据的 POJO。实体表示存储在数据库中的表。实体的每个实例代表表中的一行。

### 2.1. 实体注解_

假设我们有一个名为Student 的 POJO，它代表一个学生的数据，我们想将其存储在数据库中：

```java
public class Student {
    
    // fields, getters and setters
    
}
```

为此，我们应该定义一个实体，以便 JPA 能够识别它。

因此，让我们使用@Entity注解来定义它。我们必须在类级别指定此注解。我们还必须确保实体具有无参数构造函数和主键： 

```java
@Entity
public class Student {
    
    // fields, getters and setters
    
}
```

实体名称默认为类的名称。我们可以使用name元素更改它的名称：

```java
@Entity(name="student")
public class Student {
    
    // fields, getters and setters
    
}
```

因为各种 JPA 实现将尝试子类化我们的实体以提供它们的功能，所以实体类不能声明为final。

### 2.2. Id注解_

每个 JPA 实体都必须有一个唯一标识它的主键。[@Id](https://www.baeldung.com/hibernate-identifiers)[注解](https://www.baeldung.com/hibernate-identifiers)定义了主键[。](https://www.baeldung.com/hibernate-identifiers)我们可以用不同的方式生成标识符，这些方式由@GeneratedValue注解指定。

我们可以使用strategy元素从四种 id 生成策略中进行选择。该值可以是AUTO、TABLE、SEQUENCE或IDENTITY：

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private String name;
    
    // getters and setters
}
```

如果我们指定GenerationType。AUTO，JPA 提供者将使用它想要生成标识符的任何策略。

如果我们注解实体的字段，JPA 提供程序将使用这些字段来获取和设置实体的状态。除了Field Access，我们还可以做Property Access或者Mixed Access，这样我们就可以在同一个实体中同时使用Field和Property访问。

### 2.3. 表注解_

在大多数情况下，数据库中表的名称和实体的名称不会相同。

在这些情况下，我们可以使用@Table注解指定表名：

```java
@Entity
@Table(name="STUDENT")
public class Student {
    
    // fields, getters and setters
    
}
```

我们还可以使用schema元素提及模式：

```java
@Entity
@Table(name="STUDENT", schema="SCHOOL")
public class Student {
    
    // fields, getters and setters
    
}
```

架构名称有助于将一组表与另一组表区分开来。

如果我们不使用@Table注解，表的名称将是实体的名称。

### 2.4. 列注解_

就像@Table注解一样，我们可以使用@Column注解来提及表中某一列的详细信息。

@Column注解有许多元素，例如名称、长度、可为空和唯一：

```java
@Entity
@Table(name="STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="STUDENT_NAME", length=50, nullable=false, unique=false)
    private String name;
    
    // other fields, getters and setters
}
```

name元素指定表中列的名称。长度元素指定其长度。nullable元素指定该列是否可以为 null，unique元素指定该列是否唯一。

如果我们不指定这个注解，表中列的名称将是字段的名称。

### 2.5. 瞬态注解_

有时，我们可能想让一个字段成为非持久化的。我们可以使用@Transient注解来做到这一点。它指定不会保留该字段。

例如，我们可以根据出生日期计算学生的年龄。

那么让我们用@Transient注解来注解age这个字段：

```java
@Entity
@Table(name="STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="STUDENT_NAME", length=50, nullable=false)
    private String name;
    
    @Transient
    private Integer age;
    
    // other fields, getters and setters
}
```

因此，字段年龄不会持久保存到表中。

### 2.6. 时间注解_

在某些情况下，我们可能必须在表中保存时间值。

为此，我们[有@Temporal注解](https://www.baeldung.com/hibernate-date-time)：

```java
@Entity
@Table(name="STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="STUDENT_NAME", length=50, nullable=false, unique=false)
    private String name;
    
    @Transient
    private Integer age;
    
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    // other fields, getters and setters
}
```

但是，对于 JPA 2.2，我们还支持java.time.LocalDate、java.time.LocalTime、java.time.LocalDateTime、java.time.OffsetTime和java.time.OffsetDateTime。

### 2.7. 枚举注解_

有时，我们可能希望持久化一个 Java枚举类型。

我们可以使用@Enumerated注解来指定枚举是按名称还是按序号(默认)持久化：

```java
public enum Gender {
    MALE, 
    FEMALE
}

@Entity
@Table(name="STUDENT")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="STUDENT_NAME", length=50, nullable=false, unique=false)
    private String name;
    
    @Transient
    private Integer age;
    
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    // other fields, getters and setters
}
```

实际上，如果我们要通过enum的序号持久化Gender ，我们根本不必指定@Enumerated注解。

但是，为了通过枚举名称保留性别，我们已经使用EnumType.STRING 配置了注解。

## 3.总结

在本文中，我们了解了什么是 JPA 实体以及如何创建它们。我们还了解了可用于进一步自定义实体的不同注解。