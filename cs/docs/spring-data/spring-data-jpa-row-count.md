## 1. 概述

Spring Data JPA 实现为[Jakarta Persistence API](https://www.baeldung.com/jpa-hibernate-persistence-context)提供存储库支持，用于管理持久性和对象关系映射和函数。

在本文中，我们将探索使用 JPA 计算表中行数的不同方法。

## 2.实体类

对于我们的示例，我们将使用帐户实体，它与权限实体具有一对一的关系。

```java
@Entity
@Table(name="ACCOUNTS")
public class Account {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "accounts_seq")
    @SequenceGenerator(name = "accounts_seq", sequenceName = "accounts_seq", allocationSize = 1)
    @Column(name = "user_id")
    private int userId;
    private String username;
    private String password;
    private String email;
    private Timestamp createdOn;
    private Timestamp lastLogin;
    
    @OneToOne
    @JoinColumn(name = "permissions_id")
    private Permission permission;

   // getters , setters
}
```

权限属于账户实体。

```java
@Entity
@Table(name="PERMISSIONS")
public class Permission {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "permissions_id_sq")
    @SequenceGenerator(name = "permissions_id_sq", sequenceName = "permissions_id_sq", allocationSize = 1)
    private int id;

    private String type;

    // getters , setters
}复制
```

## 3.使用JPA存储库

Spring Data JPA 提供了一个可扩展的存储库接口，它提供了开箱即用的查询方法和派生查询方法，例如findAll() 、findBy() 、save() 、saveAndFlush() 、count() 、countBy () 、删除() 、删除全部() 。

我们将定义扩展JpaRepository接口的AccountRepository接口，以便可以访问count方法。

如果我们需要基于一个或多个条件进行计数，例如countByFirstname()、countByPermission()或countByPermissionAndCredtedOnGreaterThan() ，我们只需要AccountRepository接口中的方法名称，查询派生将负责为以下对象定义适当的 SQL：它。

```java
public interface AccountRepository extends JpaRepository<Account, Integer> { 
    long countByUsername(String username);
    long countByPermission(Permission permission); 
    long countByPermissionAndCreatedOnGreaterThan(Permission permission, Timestamp ts)
}复制
```

在下面的示例中，我们将使用逻辑类中的AccountRepository来执行计数操作。

### 3.1. 计算表中的所有行数

我们将定义一个逻辑类，在其中注入AccountRepository，对于简单的 row count() 操作，我们可以只使用accountRepository.count() ，我们就会得到结果。

```java
@Service
public class AccountStatsLogic {
    @Autowired
    private AccountRepository accountRepository;

    public long getAccountCount(){
        return accountRepository.count();
    }
}复制
```

### 3.2. 根据单一条件计算结果行数

正如我们上面所定义的，AccountRepository包含countByPermission和countByUsername等方法名称， Spring Data JPA[查询派生](https://www.baeldung.com/spring-data-derived-queries)将派生这些方法的查询。

所以我们可以在逻辑类中使用这些方法进行条件计数，我们就会得到结果。

```java
@Service
public class AccountStatsLogic {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PermissionRepository permissionRepository;
    
    public long getAccountCountByUsername(){
        String username = "user2";
        return accountRepository.countByUsername(username);
    }
    
    public long getAccountCountByPermission(){
        Permission permission = permissionRepository.findByType("reader");
        return accountRepository.countByPermission(permission);
    }
}复制
```

### 3.3. 根据多个条件统计结果行数

我们还可以在查询派生中包含多个条件，如下所示，其中包含Permission和CreatedOnGreaterThan 。

```java
@Service
public class AccountStatsLogic {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PermissionRepository permissionRepository;
    
    public long getAccountCountByPermissionAndCreatedOn() throws ParseException {
        Permission permission = permissionRepository.findByType("reader");
        Date parsedDate = getDate();
        return accountRepository.countByPermissionAndCreatedOnGreaterThan(permission, new Timestamp(parsedDate.getTime()));
    }
}复制
```

## 4. 使用 CriteriaQuery

在 JPA 中计算行数的下一个方法是使用[CriteriaQuery](https://www.baeldung.com/spring-data-criteria-queries)接口。这个接口允许我们以面向对象的方式编写查询，这样我们就可以跳过编写原始SQL查询的知识。

它要求我们构造一个CriteriaBuilder对象，然后它帮助我们构造CriteriaQuery 。CriteriaQuery准备就绪后，我们可以使用entityManager中的createQuery方法来执行查询并获取结果。

### 4.1. 计算所有行数

现在，当我们使用CriteriaQuery构造查询时，我们可以定义要计数的选择查询，如下所示。

```java
public long getAccountsUsingCQ() throws ParseException {
    // creating criteria builder and query
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
    Root<Account> accountRoot = criteriaQuery.from(Account.class);
     
    // select query
    criteriaQuery.select(builder.count(accountRoot));
     
     // execute and get the result
    return entityManager.createQuery(criteriaQuery).getSingleResult();
}复制
```

### 4.2. 根据单一条件统计结果行数

我们还可以扩展 select 查询以包含 where 条件，以根据某些条件过滤我们的查询。我们可以向构建器实例添加一个谓词并将其传递给where子句。

```java
public long getAccountsByPermissionUsingCQ() throws ParseException {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
    Root<Account> accountRoot = criteriaQuery.from(Account.class);
        
    List<Predicate> predicateList = new ArrayList<>(); // list of predicates that will go in where clause
    predicateList.add(builder.equal(accountRoot.get("permission"), permissionRepository.findByType("admin")));

    criteriaQuery
      .select(builder.count(accountRoot))
      .where(builder.and(predicateList.toArray(new Predicate[0])));

    return entityManager.createQuery(criteriaQuery).getSingleResult();
}复制
```

### 4.3. 根据多个条件统计结果行数

在我们的谓词中，我们可以添加多个条件来过滤我们的查询。构建器实例提供了equal()和 GreaterThan ()等条件方法来支持查询条件。

```java
public long getAccountsByPermissionAndCreateOnUsingCQ() throws ParseException {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
    Root<Account> accountRoot = criteriaQuery.from(Account.class);
    
    List<Predicate> predicateList = new ArrayList<>();
    predicateList.add(builder.equal(accountRoot.get("permission"), permissionRepository.findByType("reader")));
    predicateList.add(builder.greaterThan(accountRoot.get("createdOn"), new Timestamp(getDate().getTime())));

    criteriaQuery
      .select(builder.count(accountRoot))
      .where(builder.and(predicateList.toArray(new Predicate[0])));

    return entityManager.createQuery(criteriaQuery).getSingleResult();
}
复制
```

## 5.使用JPQL查询

执行计数的下一个方法是使用[JPQL](https://www.baeldung.com/spring-data-jpa-query)。JPQL 查询针对实体而不是直接针对数据库工作，或多或少看起来像SQL查询。我们总是可以编写一个JPQL查询来计算 JPA 中的行数。

### 5.1. 计算所有行数

entityManager提供了一个createQuery()方法，该方法将JPQL查询作为参数并在数据库上执行该查询。

```java
public long getAccountsByJPQL() throws ParseException {
    Query query = entityManager.createQuery("SELECT COUNT(*) FROM Account a");
    return (long) query.getSingleResult();
}复制
```

### 5.2. 根据单一条件统计结果行数

在JPQL查询中，我们可以像在原始SQL中一样包含WHERE条件来过滤查询并计算返回的行数。

```java
public long getAccountsByPermissionUsingJPQL() throws ParseException {
    Query query = entityManager.createQuery("SELECT COUNT(*) FROM Account a WHERE a.permission = ?1");
    query.setParameter(1, permissionRepository.findByType("admin"));
    return (long) query.getSingleResult();
}复制
```

### 5.3. 根据多个条件计算结果行数

在JPQL查询中，我们可以像在原始SQL中一样在WHERE子句中包含多个条件来过滤查询并计算返回的行数。

```java
public long getAccountsByPermissionAndCreatedOnUsingJPQL() throws ParseException {
    Query query = entityManager.createQuery("SELECT COUNT(*) FROM Account a WHERE a.permission = ?1 and a.createdOn > ?2");
    query.setParameter(1, permissionRepository.findByType("admin"));
    query.setParameter(2, new Timestamp(getDate().getTime()));
    return (long) query.getSingleResult();
}复制
```

## 六，结论

在本教程中，我们学习了一种不同的方法来计算 JPA 中的行数。CriteriaBuilder和Spring Data JPA查询派生等规范帮助我们轻松编写不同条件的计数查询。

虽然CriteriaQuery和Spring Data JPA查询派生可以帮助我们构建不需要原始 SQL 知识的查询，但在某些用例中，如果它不能达到目的，我们始终可以使用JPQL编写原始 SQL。