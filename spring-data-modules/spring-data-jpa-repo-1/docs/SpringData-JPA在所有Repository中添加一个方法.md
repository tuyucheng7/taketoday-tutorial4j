## 1. 概述

Spring Data通过仅定义Repository接口，使处理实体的过程变得更加容易。它们带有一组预定义的方法，并允许在每个接口中添加自定义方法。

但是，如果我们想添加一个在所有Repository中都可用的自定义方法，过程会稍微复杂一些。因此，这就是我们将在本文中探讨的内容。

有关配置和使用Spring Data JPA的更多信息，请查看我们之前的文章：[Hibernate与Spring 4指南](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)和[Spring Data JPA简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)。

## 2. 定义基本Repository接口

首先，我们必须创建一个新接口来声明我们的自定义方法：

```java
@NoRepositoryBean
public interface ExtendedRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    List<T> findByAttributeContainsText(String attributeName, String text);
}
```

我们的接口扩展了JpaRepository接口，因此我们能够受益所有标准的CRUD操作方法。

**你还会注意到我们添加了@NoRepositoryBean注解。这是必要的，否则默认的Spring行为是为Repository的所有子接口创建一个实现**。

在这里，我们希望提供我们应该使用的实现，因为这只是一个接口，旨在由实际的特定于实体的DAO接口扩展。

## 3. 实现基类

接下来，我们将提供ExtendedRepository接口的实现：

```java
public class ExtendedRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ExtendedRepository<T, ID> {

    private EntityManager entityManager;

    public ExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    // ...
}
```

**该类扩展了SimpleJpaRepository类，这是Spring用于提供Repository接口实现的默认类**。

这需要我们创建包含JpaEntityInformation和EntityManager参数的构造函数，并调用父类构造函数。

我们还需要在自定义方法中使用EntityManager属性。

此外，我们必须实现从ExtendedRepository接口继承的自定义方法：

```java
@Transactional
public List<T> findByAttributeContainsText(String attributeName, String text) {
	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
	CriteriaQuery<T> query = builder.createQuery(getDomainClass());
	Root<T> root = query.from(getDomainClass());
	query.select(root).where(builder.like(root.get(attributeName), "%" + text + "%"));
	TypedQuery<T> q = entityManager.createQuery(query);
	return q.getResultList();
}
```

在这里，findByAttributeContainsText()方法搜索具有特定属性的所有T类型对象，该属性包含作为参数给出的字符串值。

## 4. JPA配置

为了告诉Spring使用我们的自定义类而不是默认类来构建Repository实现，**我们可以使用repositoryBaseClass属性**：

```java
@Configuration
@EnableJpaRepositories(
      basePackages = "cn.tuyucheng.taketoday.persistence.dao",
      repositoryBaseClass = ExtendedRepositoryImpl.class
)
public class StudentJPAH2Config {
    // additional JPA Configuration
}
```

## 5. 创建实体Repository

接下来，让我们看看如何使用我们的新接口。

首先，让我们添加一个简单的Student实体：

```java
@Entity
public class Student {

    @Id
    private long id;
    private String name;

    // standard constructor, getters, setters
}
```

然后，我们可以为Student实体创建一个扩展ExtendedRepository接口的Repository：

```java
public interface ExtendedStudentRepository extends ExtendedRepository<Student, Long> {
}
```

就是这样！现在我们的实现将具有自定义的findByAttributeContainsText()方法。

同样，我们通过扩展ExtendedRepository接口定义的任何接口都将具有相同的方法。

## 6. 测试Repository

让我们创建一个JUnit测试来观察自定义方法的运行情况：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataRepositoryApplication.class})
@DirtiesContext
class ExtendedStudentRepositoryIntegrationTest {
    @Resource
    private ExtendedStudentRepository extendedStudentRepository;

    @BeforeEach
    void setup() {
        Student student = new Student(1, "john");
        extendedStudentRepository.save(student);
        Student student2 = new Student(2, "johnson");
        extendedStudentRepository.save(student2);
        Student student3 = new Student(3, "tom");
        extendedStudentRepository.save(student3);
    }

    @Test
    void givenStudents_whenFindByName_thenGetOk() {
        List<Student> students = extendedStudentRepository.findByAttributeContainsText("name", "john");
        assertThat(students.size()).isEqualTo(2);
    }
}
```

该测试首先使用extendedStudentRepository bean创建3个Student记录。然后，调用findByAttributeContains()方法来查找名字中包含字符串“john”的所有学生。

ExtendedStudentRepository类可以使用标准的Repository方法(如save())和我们添加的自定义方法。

## 7. 总结

在这篇快速文章中，我们展示了如何将自定义方法添加到Spring Data JPA中的所有Repository。