## 1. 概述

在本教程中，我们将看到如何解决一个常见的 [Hibernate](https://www.baeldung.com/tag/hibernate/)错误—— “org.hibernate.TransientObjectException：对象引用了一个未保存的瞬态实例”。 当我们尝试保留一个[托管实体时，我们从](https://www.baeldung.com/hibernate-entity-lifecycle#managed-entity)Hibernate [会话](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Session.html)中得到这个错误，并且该实体引用了一个未保存的 [瞬态](https://www.baeldung.com/hibernate-entity-lifecycle#transient) 实例。

## 2.描述问题

TransientObjectException是“当用户将瞬态实例传递给需要持久实例的会话方法时抛出” [。](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/TransientObjectException.html)

现在，避免此异常的最直接解决方案是通过持久化一个新实例或从数据库中获取一个实例并在持久化之前将其关联到依赖实例来获取所需实体的持久化实例。但是，这样做仅适用于此特定场景，并不适用于其他用例。

为了涵盖所有场景，我们需要一个解决方案来级联我们对依赖于另一个实体存在的实体关系的保存/更新/删除操作。我们可以通过在实体关联中使用适当的CascadeType来实现这一点。

在接下来的部分中，我们将创建一些 Hibernate 实体及其关联。然后，我们将尝试保留这些实体并查看会话抛出异常的原因。最后，我们将通过使用适当的CascadeType (s) 来解决这些异常。

## 3. @OneToOne协会

在本节中，我们将看到如何解决@OneToOne关联 中的TransientObjectException。

### 3.1. 实体

首先，让我们创建一个用户实体：

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    // standard getters and setters
}
```

让我们创建关联的地址实体：

```java
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @OneToOne(mappedBy = "address")
    private User user;

    // standard getters and setters
}
```

### 3.2. 产生错误

接下来，我们将添加一个单元测试以将 用户 保存在数据库中：

```java
@Test
public void whenSaveEntitiesWithOneToOneAssociation_thenSuccess() {
    User user = new User("Bob", "Smith");
    Address address = new Address("London", "221b Baker Street");
    user.setAddress(address);
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    session.save(user);
    session.getTransaction().commit();
    session.close();
}
```

现在，当我们运行上面的测试时，我们得到一个异常：

```java
java.lang.IllegalStateException: org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: com.baeldung.hibernate.exception.transientobject.entity.Address
```

在这里，在这个例子中，我们将一个新的/瞬态的Address实例与一个新的/瞬态的 User实例相关联。然后，当我们尝试持久化User 实例时，我们得到了TransientObjectException ，因为 Hibernate会话期望Address实体是一个持久化实例。换句话说，当持久化User时， Address应该已经在数据库中保存/可用。

### 3.3. 解决错误

最后，让我们更新User 实体并为User-Address关联使用适当的CascadeType ：

```java
@Entity
@Table(name = "user")
public class User {
    ...
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    ...
}
```

现在，每当我们保存/删除一个User时，Hibernate 会话也会保存/删除关联的地址，并且会话不会抛出 TransientObjectException 。

## 4. @OneToMany和@ManyToOne关联

在本节中，我们将看到如何解决@OneToMany和@ManyToOne关联 中的TransientObjectException。

### 4.1. 实体

首先，让我们创建一个Employee实体：

```java
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // standard getters and setters
}
```

以及关联的Department实体：

```java
@Entity
@Table(name = "department")
public class Department {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "department")
    private Set<Employee> employees = new HashSet<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    // standard getters and setters
}
```

### 4.2. 产生错误

接下来，我们将添加一个单元测试以将Employee 保存在数据库中：

```java
@Test
public void whenPersistEntitiesWithOneToManyAssociation_thenSuccess() {
    Department department = new Department();
    department.setName("IT Support");
    Employee employee = new Employee("John Doe");
    employee.setDepartment(department);
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    session.persist(employee);
    session.getTransaction().commit();
    session.close();
}
```

现在，当我们运行上面的测试时，我们得到一个异常：

```java
java.lang.IllegalStateException: org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: com.baeldung.hibernate.exception.transientobject.entity.Department
```

在这里，在这个例子中，我们将一个新的/瞬态的Employee实例与一个新的/瞬态的 Department实例相关联。然后，当我们尝试持久化Employee 实例时，我们得到了TransientObjectException ，因为 Hibernate会话期望Department实体是一个持久化实例。换句话说，在持久化Employee时，部门应该已经在数据库中保存/可用。

### 4.3. 解决错误

最后，让我们更新Employee实体并为Employee-Department关联使用适当的CascadeType ：

```java
@Entity
@Table(name = "employee")
public class Employee {
    ...
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "department_id")
    private Department department;
    ...
}
```

让我们更新Department实体，为Department-Employees关联使用适当的CascadeType ：

```java
@Entity
@Table(name = "department")
public class Department {
    ...
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Employee> employees = new HashSet<>();
    ...
}
```

现在，通过在Employee-Department关联上使用@Cascade(CascadeType.SAVE_UPDATE) ，每当我们将新的Department实例与新的Employee实例相关联并保存Employee时，Hibernate会话也将保存关联的Department实例。

类似地，通过在Department-Employees关联上使用cascade = CascadeType.ALL ，Hibernate会话会将所有操作从Department级联到关联的Employee (s)。例如，删除Department将删除与该Department关联的所有Employee。

## 5. @ManyToMany协会

在本节中，我们将看到如何解决@ManyToMany 关联 中的TransientObjectException 。

### 5.1. 实体

让我们创建一个Book实体：

```java
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @ManyToMany
    @JoinColumn(name = "author_id")
    private Set<Author> authors = new HashSet<>();

    public void addAuthor(Author author) {
        authors.add(author);
    }

    // standard getters and setters
}
```

让我们创建关联的Author实体：

```java
@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinColumn(name = "book_id")
    private Set<Book> books = new HashSet<>();

    public void addBook(Book book) {
        books.add(book);
    }

    // standard getters and setters
}
```

### 5.2. 产生问题

接下来，让我们添加一些单元测试，分别在数据库中保存一本有多位作者的书和一位有多本书的作者：

```java
@Test
public void whenSaveEntitiesWithManyToManyAssociation_thenSuccess_1() {
    Book book = new Book("Design Patterns: Elements of Reusable Object-Oriented Software");
    book.addAuthor(new Author("Erich Gamma"));
    book.addAuthor(new Author("John Vlissides"));
    book.addAuthor(new Author("Richard Helm"));
    book.addAuthor(new Author("Ralph Johnson"));
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    session.save(book);
    session.getTransaction().commit();
    session.close();
}

@Test
public void whenSaveEntitiesWithManyToManyAssociation_thenSuccess_2() {
    Author author = new Author("Erich Gamma");
    author.addBook(new Book("Design Patterns: Elements of Reusable Object-Oriented Software"));
    author.addBook(new Book("Introduction to Object Orient Design in C"));
    
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    session.save(author);
    session.getTransaction().commit();
    session.close();
}
```

现在，当我们运行上述测试时，我们分别得到以下异常：

```java
java.lang.IllegalStateException: org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: com.baeldung.hibernate.exception.transientobject.entity.Author

java.lang.IllegalStateException: org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: com.baeldung.hibernate.exception.transientobject.entity.Book
```

同样，在这些示例中，当我们将新的/瞬态实例与一个实例相关联并尝试保留该实例时，我们得到了TransientObjectException 。

### 5.3. 解决问题

最后，让我们更新Author实体并为Author s -Book s 关联使用适当的CascadeType s：

```java
@Entity
@Table(name = "author")
public class Author {
    ...
    @ManyToMany
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "book_id")
    private Set<Book> books = new HashSet<>();
    ...
}
```

同样，让我们更新Book实体并为Book s- Author s 关联使用适当的CascadeType s：

```java
@Entity
@Table(name = "book")
public class Book {
    ...
    @ManyToMany
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "author_id")
    private Set<Author> authors = new HashSet<>();
    ...
}
```

请注意，我们不能在@ManyToMany关联中使用CascadeType.ALL ，因为我们不想在删除Author时删除Book，反之亦然。

## 六. 总结

总而言之，本文展示了如何定义一个合适的CascadeType来解决“o rg.hibernate.TransientObjectException: object references an unsaved transient instance”错误。