## 1. 概述

在本教程中，**我们将探索遍历使用[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)检索的大型数据集的各种方法**。

首先，我们将使用分页查询，我们将看到切片和分页之间的区别。之后，我们将学习如何在不收集数据的情况下从数据库中流式传输和处理数据。

## 2. 分页查询

这种情况的一种常见方法是使用[分页查询](https://www.baeldung.com/spring-data-jpa-pagination-sorting)。为此，**我们需要定义批处理大小并执行多个查询**。因此，我们将能够以较小的批次处理所有实体，并避免在内存中加载大量数据。

### 2.1 使用切片分页

对于本文中的代码示例，我们将使用Student实体作为数据模型：

```java
@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;

    // constructor, getters and setters
}
```

让我们添加一个通过firstName查询所有学生的方法。使用Spring Data JPA，我们只需向JpaRepository添加一个接收Pageable作为参数并返回Slice的方法：

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Slice<Student> findAllByFirstName(String firstName, Pageable page);
}
```

**我们可以注意到返回的类型是Slice<Student\>。Slice对象允许我们处理第一批学生实体**。slice对象公开了一个hasNext()方法，该方法允许我们检查我们正在处理的批次是否是结果集中的最后一个。

此外，我们可以借助nextPageable()方法从一个切片移动到下一个切片。此方法返回请求下一个切片所需的Pageable对象。因此，我们可以在while循环中结合使用这两种方法，逐片检索所有数据：

```java
void processStudentsByFirstName(String firstName) {
    Slice<Student> slice = repository.findAllByFirstName(firstName, PageRequest.of(0, BATCH_SIZE));
    List<Student> studentsInBatch = slice.getContent();
    studentsInBatch.forEach(emailService::sendEmailToStudent);

    while(slice.hasNext()) {
        slice = repository.findAllByFirstName(firstName, slice.nextPageable());
        slice.get().forEach(emailService::sendEmailToStudent);
    }
}
```

让我们使用小批量运行一个简短的测试并打印SQL语句，我们预计会执行多个查询：

```shell
[main] DEBUG org.hibernate.SQL - select student0_.id as id1_0_, student0_.first_name as first_na2_0_, student0_.last_name as last_nam3_0_ from student student0_ where student0_.first_name=? limit ?
[main] DEBUG org.hibernate.SQL - select student0_.id as id1_0_, student0_.first_name as first_na2_0_, student0_.last_name as last_nam3_0_ from student student0_ where student0_.first_name=? limit ? offset ?
[main] DEBUG org.hibernate.SQL - select student0_.id as id1_0_, student0_.first_name as first_na2_0_, student0_.last_name as last_nam3_0_ from student student0_ where student0_.first_name=? limit ? offset ?
```

### 2.2 使用Page分页

作为Slice<>的替代方案，我们还可以使用Page<>作为查询的返回类型：

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Slice<Student> findAllByFirstName(String firstName, Pageable page);
    Page<Student> findAllByLastName(String lastName, Pageable page);
}
```

**Page接口扩展了Slice，向其添加了另外两个方法：getTotalPages()和getTotalElements()**。

当通过网络请求分页数据时，Page通常用作返回类型。这样，调用者就会确切地知道还剩下多少行以及需要多少额外的请求。

另一方面，使用Page会导致额外的查询，这些查询会计算满足条件的行数：

```shell
[main] DEBUG org.hibernate.SQL - select student0_.id as id1_0_, student0_.first_name as first_na2_0_, student0_.last_name as last_nam3_0_ from student student0_ where student0_.last_name=? limit ?
[main] DEBUG org.hibernate.SQL - select count(student0_.id) as col_0_0_ from student student0_ where student0_.last_name=?
[main] DEBUG org.hibernate.SQL - select student0_.id as id1_0_, student0_.first_name as first_na2_0_, student0_.last_name as last_nam3_0_ from student student0_ where student0_.last_name=? limit ? offset ?
[main] DEBUG org.hibernate.SQL - select count(student0_.id) as col_0_0_ from student student0_ where student0_.last_name=?
[main] DEBUG org.hibernate.SQL - select student0_.id as id1_0_, student0_.first_name as first_na2_0_, student0_.last_name as last_nam3_0_ from student student0_ where student0_.last_name=? limit ? offset ?
```

**因此，如果我们需要知道实体总数，我们应该只使用Page<>作为返回类型**。

## 3. 从数据库流式传输

Spring Data JPA还允许我们从结果集中流式传输数据：

```java
Stream<Student> findAllByFirstName(String firstName);
```

**因此，我们将逐个地处理实体，而不是将它们同时加载到内存中**。但是，我们需要使用[try-with-resource](https://www.baeldung.com/java-try-with-resources)块手动关闭由Spring Data JPA创建的流。此外，我们必须将查询包装在只读事务中。

最后，即使我们逐行处理，我们也必须确保持久性上下文不会保留对所有实体的引用。我们可以通过在消费流之前手动分离实体来实现这一点：

```java
private final EntityManager entityManager;

@Transactional(readOnly = true)
public void processStudentsByFirstNameUsingStreams(String firstName) {
    try (Stream<Student> students = repository.findAllByFirstName(firstName)) {
        students.peek(entityManager::detach).forEach(emailService::sendEmailToStudent);
    }
}
```

## 4. 总结

在本文中，我们探讨了处理大型数据集的各种方法。最初，我们通过多个分页查询来实现这一点。我们了解到，当调用者需要知道元素总数时，我们应该使用Page<>作为返回类型，否则使用Slice<>。之后，我们学习了如何从数据库中流式传输行并单独处理它们。