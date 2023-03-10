## 1. 概述

Spring Data提供了许多方法来定义我们可以执行的查询。其中之一是@Query注解。

在本教程中，我们将演示**如何在Spring Data JPA中使用@Query注解来执行JPQL和原生SQL查询**。

我们还将展示如何在@Query注解不够用时构建动态查询。

## 延伸阅读

### [Spring Data JPA Repository中的派生查询方法](https://www.baeldung.com/spring-data-derived-queries)

探索Spring Data JPA中的查询派生机制。

[阅读更多](https://www.baeldung.com/spring-data-derived-queries)→

### [Spring Data JPA @Modifying注解](https://www.baeldung.com/spring-data-jpa-modifying-annotation)

通过组合@Query和@Modifying注解在Spring Data JPA中创建DML和DDL查询

[阅读更多](https://www.baeldung.com/spring-data-jpa-modifying-annotation)→

## 2. 选择查询

为了定义要为Spring Data Repository方法执行的SQL，我们**可以使用@Query注解对方法进行标注-它的value属性包含要执行的JPQL或SQL**。

@Query注解优先于命名查询，命名查询使用@NamedQuery进行标注或在orm.xml文件中定义。

将查询定义放在Repository中的方法上而不是作为命名查询放在我们的域模型中是一种很好的方法。Repository负责持久层，因此它是存储这些定义的更好地方。

### 2.1 JPQL

**默认情况下，查询定义使用JPQL**。

让我们看一个从数据库返回有效User实体的简单Repository方法：

```java
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
    @Query("select u from User u where u.status = 1")
    Collection<User> findAllActiveUsers();
}
```

### 2.2 原生SQL

我们也可以使用原生SQL语句来定义我们的查询。我们所要做的就是**将@Query注解nativeQuery属性的值设置为true**，并在注解的value属性中定义原生SQL查询：

```java
@Query(value = "select * from Users u where u.status = 1", nativeQuery = true)
Collection<User> findAllActiveUsersNative();
```

## 3. 在查询中定义排序

我们可以将Sort类型的参数传递给具有@Query注解的Spring Data方法声明。它将被转换成传递给数据库的ORDER BY子句。

### 3.1 JPA提供和派生方法的排序

对于我们开箱即用的方法，例如findAll(Sort)或通过解析方法签名生成的方法，**我们只能使用对象属性来定义我们的排序**：

```java
userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
```

现在假设我们要按name属性的长度排序：

```java
userRepository.findAll(Sort.by("LENGTH(name)"));
```

当我们执行上面的代码时，我们会得到一个异常：

> org.springframework.data.mapping.PropertyReferenceException: No property LENGTH(name) found for type User!

### 3.2 JPQL

**当我们使用JPQL定义查询时，Spring Data可以毫无问题地处理排序**-我们所要做的就是添加一个Sort类型的方法参数：

```java
@Query(value = "SELECT u FROM User u")
List<User> findAllUsers(Sort sort);
```

我们可以调用此方法并传递一个Sort参数，该参数将按照User对象的name属性对结果进行排序：

```java
userRepository.findAllUsers(Sort.by("name"));
```

由于我们使用了@Query注解，因此我们可以使用相同的方法来获取按name长度排序的用户列表：

```java
userRepository.findAllUsers(JpaSort.unsafe("LENGTH(name)"));
```

**使用JpaSort.unsafe()创建一个Sort对象实例是至关重要的**。

当我们使用：

```java
Sort.by("LENGTH(name)");
```

然后我们将收到与上面看到的findAll()方法完全相同的异常。

当Spring Data发现使用@Query注解的方法的Sort顺序不安全时，它只是将排序子句附加到查询中-它会跳过检查要排序的属性是否属于域模型。

### 3.3 原生SQL

**当@Query注解使用原生SQL时，则无法定义Sort**。

如果我们这样做，我们将收到一个异常：

> org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException: Cannot use native queries with dynamic sorting and/or pagination

如异常所述，原生查询不支持排序。错误消息给了我们一个提示，即分页也会导致异常。

但是，有一种启用分页的解决方法，我们将在下一节中介绍它。

## 4. 分页

分页允许我们只返回页面中整个结果的子集。例如，在浏览网页上的多页数据时，这很有用。

分页的另一个优点是最小化了从服务器发送到客户端的数据量。通过发送较小的数据块，我们通常可以看到性能的提高。

### 4.1 JPQL

在JPQL查询定义中使用分页非常简单：

```java
@Query("select u from User u order by id")
Page<User> findAllUsersWithPagination(Pageable pageable);
```

**我们可以传递一个PageRequest参数来获取一页数据**。

原生查询也支持分页，但需要一些额外的配置。

### 4.2 原生SQL

我们可以通过**声明一个额外的属性countQuery来为原生查询启用分页**。

这定义了要执行的SQL，用来计算整个结果中的行数：

```java
@Query(
      value = "select * from Users order by id",
      countQuery = "select count(*) from Users",
      nativeQuery = true
)
Page<User> findAllUsersWithPaginationNative(Pageable pageable);
```

### 4.3 Spring Data JPA 2.0.4之前的版本

上述原生SQL查询解决方案适用于Spring Data JPA 2.0.4及更高版本。

在该版本之前，当我们尝试执行这样的查询时，我们将收到与上一节排序中描述的相同的异常。

我们可以通过在查询中添加一个额外的分页参数来解决这个问题：

```java
@Query(
      value = "select * from Users order by id \n-- #pageable\n",
      countQuery = "select count(*) from Users",
      nativeQuery = true
)
Page<User> findAllUsersWithPagination(Pageable pageable);
```

在上面的例子中，我们添加了：

```java
\n-- #pageable\n
```

作为分页参数的占位符。这告诉Spring Data JPA如何解析查询并注入pageable参数。此解决方案适用于H2数据库。

我们已经介绍了如何通过JPQL和原生SQL创建简单的选择查询。接下来，我们将展示如何定义附加参数。

## 5. 索引查询参数

有两种可能的方法可以将方法参数传递给查询：索引参数和命名参数。

在本节中，我们将介绍索引参数。

### 5.1 JPQL

对于JPQL中的索引参数，Spring Data将**按照它们在方法声明中出现的顺序将方法参数传递给查询**：

```java
@Query("SELECT u FROM User u WHERE u.status = ?1")
User findUserByStatus(Integer status);

@Query("SELECT u FROM User u WHERE u.status = ?1 and u.name = ?2")
User findUserByStatusAndName(Integer status, String name);
```

对于上述查询，方法参数status将被分配给索引为1的查询参数，name方法参数将分配给索引为2的查询参数。

### 5.2 原生SQL

原生查询的索引参数的工作方式与JPQL完全相同：

```java
@Query(value = "select * from Users u where u.status = ?1", nativeQuery = true)
User findUserByStatusNative(Integer status);
```

在下一节中，我们将演示一种不同的方法：通过名称传递参数。

## 6. 命名参数

我们还可以**使用命名参数将方法参数传递给查询**。我们使用Repository方法声明中的@Param注解来定义这些命名参数。

使用@Param标注的每个参数都必须具有与相应的JPQL或SQL查询参数名称匹配的字符串值。使用命名参数的查询更易于阅读，并且在需要重构查询时不易出错。

### 6.1 JPQL

如上所述，我们在方法声明中使用@Param注解将JPQL中按名称定义的参数与方法声明中的参数进行匹配：

```java
@Query("select u from User u where u.status = :status and u.name = :name")
User findUserByStatusAndNameNamedParams(
      @Param("status") Integer status,
      @Param("name") String name
);
```

请注意，在上面的示例中，我们将SQL查询和方法参数定义为具有相同的名称，但只要@Param注解的value属性值与查询中的参数名相同就不需要这样做：

```java
@Query("select u from User u where u.status = :status and u.name = :name")
User findUserByUserStatusAndUserName(
      @Param("status") Integer userStatus,
      @Param("name") String userName
);
```

### 6.2 原生SQL

对于原生查询定义，与JPQL相比，我们通过名称将参数传递给查询的方式没有区别-我们使用@Param注解：

```java
@Query(value = "select * from Users u where u.status = :status and u.name = :name", nativeQuery = true)
User findUserByStatusAndNameNamedParamsNative(
      @Param("status") Integer status,
      @Param("name") String name
);
```

## 7. 集合参数

让我们考虑一个JPQL或SQL查询的where子句包含IN(或NOT IN)关键字的情况：

```jpaql
SELECT u FROM User u WHERE u.name IN :names
```

在这种情况下，我们可以定义一个以Collection为参数的查询方法：

```java
@Query("select u from User u where u.name in :names")
List<User> findUserByNameList(@Param("names") Collection<String> names);
```

由于参数是一个Collection，因此它可以与List、HashSet等一起使用。

接下来，我们将展示如何使用@Modifying注解修改数据。

## 8. 使用@Modifying执行UPDATE语句

**我们可以使用@Query注解来修改数据库的状态，方法是将@Modifying注解添加到Repository方法上**。

### 8.1 JPQL

与select查询相比，修改数据的Repository方法有两个不同之处-它包含@Modifying注解，当然，JPQL查询使用update而不是select：

```java
@Modifying
@Query("update User u set u.status = :status where u.name = :name")
int updateUserSetStatusForName(
      @Param("status") Integer status,
      @Param("name") String name
);
```

返回值定义执行更新了多少行。索引参数和命名参数同样可以在update查询中使用。

### 8.2 原生SQL

我们也可以使用原生查询修改数据库的状态。我们只需要添加@Modifying注解：

```java
@Modifying
@Query(value = "update Users u set u.status = ? where u.name = ?", nativeQuery = true)
int updateUserSetStatusForNameNative(Integer status, String name);
```

### 8.3 Insert

要执行insert操作，我们必须同时应用@Modifying并使用原生查询，因为insert不是JPA接口的一部分：

```java
@Modifying
@Query(value = "insert into Users (name,age,email,status,active)" + "values(:name,:age,:email,:status,:active)", nativeQuery = true)
void insertUser(
      @Param("name") String name,
      @Param("age") Integer age,
      @Param("email") String email,
      @Param("status") Integer status,
      @Param("active") boolean active
);
```

## 9. 动态查询

通常，我们会遇到基于条件或数据集构建SQL语句的需求，这些条件或数据集的值仅在运行时已知。在这些情况下，我们不能只使用静态查询。

### 9.1 动态查询示例

例如，让我们想象这样一种情况，我们需要从运行时定义的集合中选择电子邮件为email1、email2、...、emailn的所有用户：

```jpaql
SELECT u FROM User u WHERE u.email LIKE '%email1%'
    or  u.email LIKE '%email2%'
    ...
    or  u.email LIKE '%emailn%'
```

由于集合是动态构造的，因此我们无法在编译时知道要添加多少LIKE子句。

在这种情况下，**我们不能只使用@Query注解，因为我们不能提供静态SQL语句**。

相反，通过实现自定义复合Repository，我们可以扩展基本的JpaRepository功能，并提供我们自己的逻辑来构建动态查询。让我们来看看如何做到这一点。

### 9.2 自定义Repository与JPA Criteria API

幸运的是，**Spring提供了一种通过使用自定义片段接口来扩展基础Repository的方法**。然后我们可以将它们链接在一起以创建一个[复合Repository](https://www.baeldung.com/spring-data-composable-repositories)。

我们将首先创建自定义片段接口：

```java
public interface UserRepositoryCustom {
    List<User> findUserByEmails(Set<String> emails);
}
```

然后我们实现该接口：

```java
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findUserByEmails(Set<String> emails) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);

        Path<String> emailPath = user.get("email");

        List<Predicate> predicates = new ArrayList<>();
        for (String email : emails) {

            predicates.add(cb.like(emailPath, email));

        }
        query.select(user)
              .where(cb.or(predicates.toArray(new Predicate[predicates.size()])));

        return entityManager.createQuery(query)
              .getResultList();
    }
}
```

**如上所示，我们利用[JPA Criteria API](https://www.baeldung.com/hibernate-criteria-queries)来构建我们的动态查询**。

**此外，我们需要确保在类名中包含Impl后缀**。Spring会搜索名为UserRepositoryCustomImpl的类作为UserRepositoryCustom的实现。由于片段接口本身不是Repository，因此Spring依靠这种机制来查找片段接口的实现。

### 9.3 扩展现有Repository

请注意，第2节到第7节的所有查询方法都在UserRepository中定义。

因此，现在我们将通过在UserRepository中扩展新接口来集成我们的片段接口：

```java
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
    // ...
}
```

### 9.4 使用

最后，我们可以调用我们的动态查询方法：

```java
Set<String> emails = new HashSet<>();
userRepository.findUserByEmails(emails);
```

至此，我们已经成功创建了一个复合Repository并调用了我们的自定义方法。

## 10. 总结

在本文中，我们介绍了几种使用@Query注解在Spring Data JPA Repository方法中定义查询的方法。

我们还学习了如何实现自定义Repository和创建动态查询。