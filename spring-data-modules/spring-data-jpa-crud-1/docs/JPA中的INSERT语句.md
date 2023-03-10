## 1. 概述

在本快速教程中，我们将学习如何**对JPA实体执行INSERT语句**。

有关Hibernate的更多信息，请查看我们的[JPA与Spring综合指南](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)和[Spring Data JPA简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)，以深入了解该主题。

## 2. 在JPA中持久化对象

在JPA中，从瞬态到托管状态的每个实体都由[EntityManager](https://www.baeldung.com/hibernate-entitymanager)自动处理。

EntityManager检查给定实体是否已经存在，然后决定是否应该插入或更新该实体。**由于这种自动管理，JPA只允许使用SELECT，UPDATE和DELETE语句**。

在下面的示例中，我们将研究管理和绕过此限制的不同方法。

## 3. 定义普通实体

现在，让我们首先定义一个将在本教程中使用的简单实体：

```java
@Entity
public class Person {

    @Id
    private Long id;
    private String firstName;
    private String lastName;

    // standard getters and setters, default and all-args constructors
}
```

另外，让我们定义一个将用于实现的Repository类：

```java
@Repository
public class PersonInsertRepository {

    @PersistenceContext
    private EntityManager entityManager;
}
```

此外，我们将应用[@Transactional](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)注解来由Spring自动处理事务。这样，我们就不必担心使用EntityManager创建事务，提交更改或在出现异常时手动执行回滚。

## 4. createNativeQuery

对于手动创建的查询，我们可以使用EntityManager#createNativeQuery()方法。它允许我们创建任何类型的SQL查询，而不仅仅是JPA支持的查询。让我们向repository类添加一个新方法：

```java
public class PersonInsertRepository {

    @Transactional
    public void insertWithQuery(Person person) {
        entityManager.createNativeQuery("insert into person (id,first_name,last_name) values(?,?,?)")
              .setParameter(1, person.getId())
              .setParameter(2, person.getFirstName())
              .setParameter(3, person.getLastName())
              .executeUpdate();
    }
}
```

使用这种方法，我们需要定义一个包含列名的文本查询并设置它们相应的值。

我们现在可以测试我们的Repository：

```java
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(PersonInsertRepository.class)
class PersonInsertRepositoryIntegrationTest {

    private static final Long ID = 1L;
    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private static final Person PERSON = new Person(ID, FIRST_NAME, LAST_NAME);

    @Autowired
    private PersonInsertRepository personInsertRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void givenPersonEntity_whenInsertedTwiceWithNativeQuery_thenPersistenceExceptionExceptionIsThrown() {
        assertThatExceptionOfType(PersistenceException.class).isThrownBy(() -> {
            insertWithQuery();
            insertWithQuery();
        });
    }

    private void insertWithQuery() {
        personInsertRepository.insertWithQuery(PERSON);
    }
}
```

在我们的测试中，每个insertWithQuery操作都试图向数据库中插入一条新记录。由于我们试图插入两个具有相同id的实体，因此第二个插入操作因抛出PersistenceException而失败。

如果我们使用Spring Data的[@Query](https://www.baeldung.com/spring-data-jpa-query)注解，这里的原理是相同的。

## 5. persist

在我们之前的示例中，我们创建了插入查询，但必须为每个实体编写文本查询.这种方法效率不高，并且会导致大量样板代码。

相反，我们可以使用EntityManager中的persist方法。

与前面的示例一样，让我们使用自定义方法扩展我们的Repository类：

```java
@Transactional
public void insertWithEntityManager(Person person) {
    this.entityManager.persist(person);
}
```

现在，我们可以再次测试我们的方法：

```java
@Test
void givenPersonEntity_whenInsertedTwiceWithEntityManager_thenEntityExistsExceptionIsThrown() {
    assertThatExceptionOfType(EntityExistsException.class).isThrownBy(() -> {
        insertPersonWithEntityManager();
        insertPersonWithEntityManager();
    });
}

private void insertPersonWithEntityManager() {
    personInsertRepository.insertWithEntityManager(new Person(ID, FIRST_NAME, LAST_NAME));
}
```

**与使用原生SQL语句相比，我们不必指定列名和相应的值**。相反，EntityManager为我们处理这个问题。

在上面的测试中，我们还期望抛出EntityExistsException而不是它的超类PersistenceException，后者更规范，由persist抛出。

另一方面，在此示例中，**我们必须确保每次调用我们的插入方法时都使用一个新的Person实例**。否则，它将已经由EntityManager管理，从而导致的是update操作而不是insert。

## 6. 总结

在本文中，我们说明了对JPA对象执行插入操作的方法。我们查看了使用原生查询以及使用EntityManager#persist创建自定义INSERT语句的示例。