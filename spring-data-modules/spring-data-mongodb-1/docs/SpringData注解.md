## 1. 简介

Spring Data提供了对数据存储技术的抽象。因此，我们的业务逻辑代码可以更加独立于底层持久化实现。此外，Spring简化了数据存储的依赖于实现的细节的处理。

在本教程中，我们将看到Spring Data、Spring Data JPA和Spring Data MongoDB项目最常见的注解。

## 2. 常见的Spring Data注解

### 2.1 @Transactional

当我们**想要配置方法的事务行为**时，我们可以这样做：

```java
@Transactional
void pay() {}
```

如果我们在类级别应用此注解，那么它适用于类内的所有方法。但是，我们可以通过将其应用于特定方法来覆盖其效果。

它有很多配置选项，可以在[这篇文章](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)中找到。

### 2.2 @NoRepositoryBean

**有时我们想要创建Repository接口，其唯一目标是为子Repository提供通用方法**。

当然，我们不希望Spring创建这些Repository的bean，因为我们不会将它们注入任何地方。@NoRepositoryBean正是这样做的：当我们标记org.springframework.data.repository.Repository的子接口时，Spring不会从中创建一个bean。

例如，如果我们想要在所有Repository中使用Optional<T\> findById(ID id)方法，我们可以创建一个基本Repository：

```java
@NoRepositoryBean
interface MyUtilityRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {
    Optional<T> findById(ID id);
}
```

这个注解不会影响子接口；因此Spring将为以下Repository接口创建一个bean：

```java
@Repository
interface PersonRepository extends MyUtilityRepository<Person, Long> {}
```

请注意，上面的示例不是必需的，因为Spring Data版本2包含此方法替换了旧的T findOne(ID id)。

### 2.3 @Param

我们可以使用@Param将命名参数传递给我们的查询：

```java
@Query("FROM Person p WHERE p.name = :name")
Person findByName(@Param("name") String name);
```

请注意，我们使用:name语法引用参数。

有关更多示例，请访问[本文](https://www.baeldung.com/spring-data-jpa-query)。

### 2.4 @ID

@Id将模型类中的字段标记为主键：

```java
class Person {

    @Id
    Long id;

    // ...
}
```

由于它是独立于实现的，因此它使模型类易于与多个数据存储引擎一起使用。

### 2.5 @Transient

我们可以使用此注解将模型类中的字段标记为瞬态。因此数据存储引擎不会读取或写入该字段的值：

```java
class Person {

    // ...

    @Transient
    int age;

    // ...
}
```

与@Id一样，@Transient也是独立于实现的，这使得它可以方便地与多个数据存储实现一起使用。

### 2.6 @CreatedBy、@LastModifiedBy、@CreatedDate、@LastModifiedDate

使用这些注解，我们可以审计我们的模型类：Spring自动使用创建对象的主体、最后修改对象、创建日期和最后修改日期来填充带注解的字段：

```java
public class Person {

    // ...

    @CreatedBy
    User creator;

    @LastModifiedBy
    User modifier;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date modifiedAt;

    // ...
}
```

请注意，如果我们希望Spring填充主体，我们还需要使用Spring Security。

有关更全面的描述，请访问[这篇文章](https://www.baeldung.com/database-auditing-jpa)。

## 3. Spring Data JPA注解

### 3.1 @Query

使用@Query，我们可以为Repository方法提供JPQL实现：

```java
@Query("SELECT COUNT(*) FROM Person p")
long getPersonCount();
```

另外，我们可以使用命名参数：

```java
@Query("FROM Person p WHERE p.name = :name")
Person findByName(@Param("name") String name);
```

此外，如果我们将nativeQuery参数设置为true，我们可以使用原生SQL查询：

```java
@Query(value = "SELECT AVG(p.age) FROM person p", nativeQuery = true)
int getAverageAge();
```

有关更多信息，请访问[这篇文章](https://www.baeldung.com/spring-data-jpa-query)。

### 3.2 @Procedure

**使用Spring Data JPA，我们可以轻松地从Repository调用存储过程**。

首先，我们需要使用标准JPA注解在实体类上声明Repository：

```java
@NamedStoredProcedureQueries({
      @NamedStoredProcedureQuery(
            name = "count_by_name",
            procedureName = "person.count_by_name",
            parameters = {
                  @StoredProcedureParameter(
                        mode = ParameterMode.IN,
                        name = "name",
                        type = String.class),
                  @StoredProcedureParameter(
                        mode = ParameterMode.OUT,
                        name = "count",
                        type = Long.class)
            }
      )
})
class Person {}
```

在此之后，我们可以使用我们在name参数中声明的名称在Repository中引用它：

```java
@Procedure(name = "count_by_name")
long getCountByName(@Param("name") String name);
```

### 3.3 @Lock

我们可以在执行Repository查询方法时配置锁定模式：

```java
@Lock(LockModeType.NONE)
@Query("SELECT COUNT(*) FROM Person p")
long getPersonCount();
```

可用的锁定模式：

-   READ
-   WRITE
-   OPTIMISTIC
-   OPTIMISTIC_FORCE_INCREMENT
-   PESSIMISTIC_READ
-   PESSIMISTIC_WRITE
-   PESSIMISTIC_FORCE_INCREMENT
-   NONE

### 3.4 @Modifying

如果我们使用@Modifying标注它，我们可以使用Repository方法修改数据：

```java
@Modifying
@Query("UPDATE Person p SET p.name = :name WHERE p.id = :id")
void changeName(@Param("id") long id, @Param("name") String name);
```

有关更多信息，请访问[这篇文章](https://www.baeldung.com/spring-data-jpa-query)。

### 3.5 @EnableJpaRepositories

要使用JPA Repository，我们必须向Spring指出它。我们可以使用@EnableJpaRepositories来做到这一点。

请注意，我们必须将此注解与@Configuration一起使用：

```java
@Configuration
@EnableJpaRepositories
class PersistenceJPAConfig {}
```

Spring将在这个@Configuration类的子包中查找Repository。

我们可以使用basePackages参数改变这种行为：

```java
@Configuration
@EnableJpaRepositories(basePackages = "cn.tuyucheng.taketoday.persistence.dao")
class PersistenceJPAConfig {}
```

另请注意，如果Spring Boot在类路径上找到Spring Data JPA，它会自动执行此操作。

## 4. Spring Data Mongo注解

Spring Data使使用MongoDB变得更加容易。在接下来的部分中，我们将探讨Spring Data MongoDB的最基本功能。

有关更多信息，请访问我们[关于Spring Data MongoDB的文章](https://www.baeldung.com/spring-data-mongodb-tutorial)。

### 4.1 @Document

此注解将类标记为我们想要持久保存到数据库的域对象：

```java
@Document
class User {}
```

它还允许我们选择要使用的集合的名称：

```java
@Document(collection = "user")
class User {}
```

请注意，此注解是JPA中@Entity的Mongo等价物。

### 4.2 @Field

使用@Field，我们可以配置我们想要在MongoDB持久化文档时使用的字段名称：

```java
@Document
class User {

    // ...

    @Field("email")
    String emailAddress;

    // ...
}
```

请注意，此注解是JPA中@Column的Mongo等价物。

### 4.3 @Query

使用@Query，我们可以在MongoDB Repository方法上提供查找器查询：

```java
@Query("{ 'name' : ?0 }")
List<User> findUsersByName(String name);
```

### 4.4 @EnableMongoRepositories

要使用MongoDBRepository，我们必须向Spring指出它。我们可以使用@EnableMongoRepositories来做到这一点。

请注意，我们必须将此注解与@Configuration一起使用：

```java
@Configuration
@EnableMongoRepositories
class MongoConfig {}
```

Spring将在这个@Configuration类的子包中查找Repository。我们可以使用basePackages参数改变这种行为：

```java
@Configuration
@EnableMongoRepositories(basePackages = "cn.tuyucheng.taketoday.repository")
class MongoConfig {}
```

另请注意，如果Spring Boot在类路径上找到Spring Data MongoDB，它会自动执行此操作。

## 5. 总结

在本文中，我们了解了使用Spring处理一般数据所需的最重要的注解。此外，我们研究了最常见的JPA和MongoDB注解。