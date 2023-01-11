## 1. 概述

在本教程中，我们将学习如何对JPA实体执行INSERT语句。

## 2. JPA中持久化一个对象

在JPA中，每一个从瞬时态到托管状态的实体都由EntityManager自动处理。

EntityManager检查给定实体是否已经存在，然后决定是否应该插入或更新该实体。**由于这种自动管理，JPA只允许SELECT，UPDATE和DELETE语句**。

在下面的示例中，我们将研究管理和绕过此限制的不同方法。

## 3. 定义一个普通实体

我们从定义一个简单的实体开始：

```java
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
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

此外，我们使用@Transactional注解来自动处理Spring的事务。这样，我们就不必担心使用EntityManager创建事务，提交更改或在出现异常时手动执行回滚。

## 4. createNativeQuery

对于手动创建的查询，我们可以使用EntityManager的createNativeQuery()方法。
它允许我们创建任何类型的SQL查询，而不仅仅是JPA支持的查询，让我们向repository类添加一个新方法：

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

使用这种方法，我们需要定义一个包含列名的查询，并设置它们相应的值。

现在可以测试我们的Repository：

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

在我们测试方法中，每个insertWithQuery操作都试图向数据库中插入一条新记录。由于我们试图插入两个具有相同id的实体，第二个插入操作因抛出PersistenceException而失败。

如果我们使用Spring Data的@Query注解，这里的原理是相同的。

## 5. persist

在前面的示例中，我们创建了插入查询，但必须为每个实体编写文本查询，这种方法效率不高，会产生大量样板代码。

相反，我们可以使用EntityManager中的persist方法。

与前面的示例一样，我们向PersonInsertRepository类添加一个新的方法：

```java
@Repository
public class PersonInsertRepository {

    @Transactional
    public void insertWithEntityManager(Person person) {
        this.entityManager.persist(person);
    }
}
```

现在，我们可以测试该方法：

```java
class PersonInsertRepositoryIntegrationTest {

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
}
```

**与使用原生SQL语句相比，我们不必指定列名和相应的值**。相反，EntityManager为我们处理这些问题。

在上面的测试中，我们期望抛出的是EntityExistsException，而不是它的父类PersistenceException，后者更规范，由persist抛出。

另一方面，在本例中，**我们必须确保每次调用我们的插入方法时都使用一个新的Person实例**。否则，它将已经由EntityManager管理，从而执行的是update操作而不是insert。

## 6. 总结

在本文中，我们说明了对JPA对象执行插入操作的方法。我们演示了使用原生查询以及使用EntityManager#persist创建自定义INSERT语句的示例。