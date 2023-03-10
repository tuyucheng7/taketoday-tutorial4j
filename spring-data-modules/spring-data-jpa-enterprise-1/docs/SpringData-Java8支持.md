## 1. 概述

Spring Data现在支持Java 8的核心功能，例如Optional、Stream API和CompletableFuture。

在这篇简短的文章中，我们将通过一些示例来说明如何将它们与框架一起使用。

## 2. Optional

让我们从CRUD Repository方法开始-现在将结果包装在Optional中：

```java
public interface CrudRepository<T, ID> extends Repository<T, ID> {

    Optional<T> findById(ID id);
}
```

当返回Optional实例时，这是一个有用的提示，表明该值可能不存在。有关Optional的更多信息，请参阅[此处](https://www.baeldung.com/java-optional)。

我们现在要做的就是将返回类型指定为Optional：

```java
public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findOneByName(String name);
}
```

## 3. Stream接口

Spring Data还提供了对Java 8最重要的特性之一-Stream API的支持。

过去，每当我们需要返回多个结果时，我们需要返回一个集合：

```java
public interface UserRepository extends JpaRepository<User, Integer> {
    // ...
    List<User> findAll();
    // ...
}
```

这种实现的问题之一是内存消耗。

我们不得不急切地加载并保存所有检索到的对象。

我们可以通过利用分页来改进：

```java
public interface UserRepository extends JpaRepository<User, Integer> {
    // ...
    Page<User> findAll(Pageable pageable);
    // ...
}
```

在某些情况下，这就足够了，但在其他情况下分页确实不是可行的方法，因为检索数据所需的请求数量很多。

多亏了Java 8 Stream API和JPA提供程序-我们现在可以**定义我们的Repository方法只返回一个Stream对象**：

```java
public interface UserRepository extends JpaRepository<User, Integer> {
    // ...
    Stream<User> findAllByName(String name);
    // ...
}
```

Spring Data使用特定于提供程序的实现来流式传输结果(Hibernate使用ScrollableResultSet，EclipseLink使用ScrollableCursor)。它减少了内存消耗量和对数据库的查询调用。因此，它也比前面提到的两种解决方案快得多。

**使用Stream处理数据需要我们在完成时关闭Stream**。

这可以通过调用Stream上的close()方法或使用try-with-resources来完成：

```java
try (Stream<User> foundUsersStream= userRepository.findAllByName(USER_NAME_ADAM)) {
 
assertThat(foundUsersStream.count(), equalTo(3l));
```

**我们还必须记住在事务中调用Repository方法**，否则我们会得到一个异常：

>   org.springframework.dao.InvalidDataAccessApiUsageException：你正在尝试在没有保持连接打开的周围事务的情况下执行流式查询方法，以便实际使用流。确保使用流的代码使用@Transactional或任何其他声明(只读)事务的方式。

## 4. CompletableFuture

**Spring Data Repository可以在Java 8的CompletableFuture和Spring异步方法执行机制的支持下异步运行**：

```java
@Async
CompletableFuture<User> findOneByStatus(Integer status);
```

调用此方法的客户端将立即返回Future，但方法将在不同的线程中继续执行。

有关CompletableFuture处理的更多信息，请参见[此处](https://www.baeldung.com/java-completablefuture)。

## 5. 总结

在本教程中，我们展示了如何在Spring Data中使用Java 8功能。