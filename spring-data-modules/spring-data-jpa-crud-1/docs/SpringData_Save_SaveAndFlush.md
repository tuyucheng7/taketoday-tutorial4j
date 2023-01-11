## 1. 概述

在这个快速教程中，我们介绍Spring Data JPA中save()和saveAndFlush()方法之间的区别。

尽管这两种方法都可以将实体保存到数据库中，但存在一些本质区别。

## 2. Entity

首先，让我们创建一个实体类：

```java

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    private Long id;
    private String name;
}
```

接下来，我们为Employee实体类上的CRUD操作创建一个Jpa Repository：

```java

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
```

## 3. save()方法

顾名思义，save()方法允许我们将实体保存到数据库中。它属于Spring Data定义的CrudRepository接口，让我们看看如何使用它：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JpaInsertApplication.class)
class EmployeeRepositoryIntegrationTest {
    private static final Employee EMPLOYEE1 = new Employee(1L, "John");
    private static final Employee EMPLOYEE2 = new Employee(2L, "Alice");

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void givenEmployeeEntity_whenInsertWithSave_ThenEmployeeIsPersisted() {
        employeeRepository.save(EMPLOYEE1);
        assertEmployeePersisted(EMPLOYEE1);
    }

    private void assertEmployeePersisted(Employee input) {
        Employee employee = employeeRepository.getById(input.getId());
        assertThat(employee).isNotNull();
    }
}
```

通常，Hibernate在内存中保存持久态。将此状态同步到底层数据库的过程称为刷新。

**当我们使用save()方法时，与save操作相关的数据不会被刷新到DB中，除非并且直到显式调用flush()或commit()方法**。

如果我们使用像Hibernate这样的JPA实现，那么该特定实现将管理flush和commit操作。

这里我们必须记住的一点是，如果我们自己刷新数据而不是提交数据，那么外部事务将看不到更改，
除非在该事务中进行提交调用，或者外部事务的隔离级别为READ_UNCOMMITTED。

### 4. saveAndFlush方法

与save()不同，saveAndFlush()方法在执行过程中会立即刷新数据。
该方法属于Spring Data JPA的JpaRepository接口。

```java
class EmployeeRepositoryIntegrationTest {

    @Test
    void givenEmployeeEntity_whenInsertWithSaveAndFlush_thenEmployeeIsPersisted() {
        employeeRepository.saveAndFlush(EMPLOYEE2);
        assertEmployeePersisted(EMPLOYEE2);
    }
}
```

通常，当我们的业务逻辑需要在同一事务的稍后时间但在提交之前，读取保存的更改时，我们使用这种方法。

例如，想象一个场景，我们必须执行一个存储过程，该存储过程需要我们要保存的实体的属性。
在这种情况下，save()方法将不起作用，因为更改与DB不同步，并且存储过程并不知道这些更改。而saveAndFlush()方法非常适合这种情况。

## 5. 总结

在这篇简短的文章中，我们重点介绍了Spring Data JPA中save()和saveAndFlush()方法之间的区别。

在大多数情况下，我们都是使用save()方法。但有时，我们可能还需要针对特定用例使用saveAndFlush()方法。