## 一、概述

数据访问对象 (DAO) 模式是一种结构模式，它允许我们使用抽象 API 将应用程序/业务层与持久层(通常是关系数据库，但也可以是任何其他持久性机制)隔离开来。

API 向应用程序隐藏了在底层存储机制中执行 CRUD 操作的所有复杂性。这允许两个层在彼此一无所知的情况下单独进化。

在本教程中，我们将深入研究该模式的实现，并学习如何使用它来抽象调用 [JPA 实体管理器](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html)。

## 延伸阅读：

## [Spring Data JPA 简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)

Spring Data JPA 与 Spring 4 简介 - Spring 配置、DAO、手动和生成的查询以及事务管理。

[阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)→

## [JPA/Hibernate 级联类型概述](https://www.baeldung.com/jpa-cascade-types)

JPA/Hibernate 级联类型的快速实用概述。

[阅读更多](https://www.baeldung.com/jpa-cascade-types)→

## 2. 一个简单的实现

要了解 DAO 模式的工作原理，让我们创建一个基本示例。

假设我们要开发一个管理用户的应用程序。我们希望应用程序的领域模型完全不受数据库的影响。因此，我们将创建一个简单的 DAO 类，它将负责保持这些组件彼此之间的分离。

### 2.1. 域类

由于我们的应用程序将与用户一起工作，因此我们只需要定义一个类来实现其领域模型：

```java
public class User {
    
    private String name;
    private String email;
    
    // constructors / standard setters / getters
}
```

User类只是用户数据的普通容器，因此它没有实现任何其他值得强调的行为。

当然，这里重要的设计选择是如何使使用此类的应用程序与任何可能实现的持久性机制隔离开来。

而这正是 DAO 模式试图解决的问题。

### 2.2. The DAO API

让我们定义一个基本的 DAO 层，这样我们就可以看到它如何使领域模型与持久层完全分离。

这是 DAO API：

```java
public interface Dao<T> {
    
    Optional<T> get(long id);
    
    List<T> getAll();
    
    void save(T t);
    
    void update(T t, String[] params);
    
    void delete(T t);
}
```

从鸟瞰图来看，很明显Dao接口定义了一个抽象 API，该 API 对类型T的对象执行 CRUD 操作。

由于接口提供的高度抽象，很容易创建与用户对象一起使用的具体、细粒度的实现。

### 2.3. UserDao类_

让我们定义一个特定于用户的Dao接口实现：

```java
public class UserDao implements Dao<User> {
    
    private List<User> users = new ArrayList<>();
    
    public UserDao() {
        users.add(new User("John", "john@domain.com"));
        users.add(new User("Susan", "susan@domain.com"));
    }
    
    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get((int) id));
    }
    
    @Override
    public List<User> getAll() {
        return users;
    }
    
    @Override
    public void save(User user) {
        users.add(user);
    }
    
    @Override
    public void update(User user, String[] params) {
        user.setName(Objects.requireNonNull(
          params[0], "Name cannot be null"));
        user.setEmail(Objects.requireNonNull(
          params[1], "Email cannot be null"));
        
        users.add(user);
    }
    
    @Override
    public void delete(User user) {
        users.remove(user);
    }
}
```

UserDao类实现了获取、更新和删除User对象所需的所有功能。

为了简单起见， 用户列表就像一个内存数据库，在构造函数中填充了几个用户对象。

当然，重构其他方法很容易，例如，它们可以用于关系数据库。

虽然User和UserDao类在同一个应用程序中独立共存，但我们仍然需要了解后者如何用于保持持久层对应用程序逻辑隐藏：

```java
public class UserApplication {

    private static Dao<User> userDao;

    public static void main(String[] args) {
        userDao = new UserDao();
        
        User user1 = getUser(0);
        System.out.println(user1);
        userDao.update(user1, new String[]{"Jake", "jake@domain.com"});
        
        User user2 = getUser(1);
        userDao.delete(user2);
        userDao.save(new User("Julie", "julie@domain.com"));
        
        userDao.getAll().forEach(user -> System.out.println(user.getName()));
    }

    private static User getUser(long id) {
        Optional<User> user = userDao.get(id);
        
        return user.orElseGet(
          () -> new User("non-existing user", "no-email"));
    }
}
```

该示例是人为设计的，但它简要说明了 DAO 模式背后的动机。在这种情况下，main方法只是使用一个UserDao实例对几个User对象执行 CRUD 操作。

这个过程最相关的方面是UserDao如何向应用程序隐藏有关对象如何持久化、更新和删除的所有低级细节。

## 3. 在 JPA 中使用模式

开发人员倾向于认为 JPA 的发布将 DAO 模式的功能降级为零。该模式只是在 JPA 的实体管理器提供的层之上的另一层抽象和复杂性。

在某些情况下确实如此。即便如此，有时我们只想向我们的应用程序公开实体管理器 API 的一些特定于域的方法。DAO 模式在这种情况下占有一席之地。

### 3.1. JpaUserDao类_

让我们创建一个Dao接口的新实现，看看它如何封装 JPA 的实体管理器提供的开箱即用的功能：

```java
public class JpaUserDao implements Dao<User> {
    
    private EntityManager entityManager;
    
    // standard constructors
    
    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }
    
    @Override
    public List<User> getAll() {
        Query query = entityManager.createQuery("SELECT e FROM User e");
        return query.getResultList();
    }
    
    @Override
    public void save(User user) {
        executeInsideTransaction(entityManager -> entityManager.persist(user));
    }
    
    @Override
    public void update(User user, String[] params) {
        user.setName(Objects.requireNonNull(params[0], "Name cannot be null"));
        user.setEmail(Objects.requireNonNull(params[1], "Email cannot be null"));
        executeInsideTransaction(entityManager -> entityManager.merge(user));
    }
    
    @Override 
    public void delete(User user) {
        executeInsideTransaction(entityManager -> entityManager.remove(user));
    }
    
    private void executeInsideTransaction(Consumer<EntityManager> action) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            action.accept(entityManager);
            tx.commit(); 
        }
        catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }
}
```

JpaUserDao类可以与 JPA 实现支持的任何关系数据库一起使用。

此外，如果我们仔细查看该类，我们将意识到[组合](https://en.wikipedia.org/wiki/Composition_over_inheritance) 和 [依赖注入](https://en.wikipedia.org/wiki/Dependency_injection)的使用如何允许我们仅调用应用程序所需的实体管理器方法。

简单地说，我们有一个特定于领域的定制 API，而不是整个实体管理器的 API。

### 3.2. 重构用户类

在这种情况下，我们将使用 Hibernate 作为 JPA 默认实现，因此我们将相应地重构User类：

```java
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private String name;
    private String email;
    
    // standard constructors / setters / getters
}
```

### 3.3. 以编程方式引导 JPA 实体管理器 

假设我们已经有一个本地或远程运行的 MySQL 工作实例和一个填充了一些用户记录的数据库表“users”，我们需要一个 JPA 实体管理器，以便我们可以使用JpaUserDao类在数据库中执行 CRUD 操作.

在大多数情况下，我们通过典型的persistence.xml 文件完成此操作，这是标准方法。

在这种情况下，我们将采用无 XML 的方法，并通过 Hibernate 方便的 [EntityManagerFactoryBuilderImpl](https://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.html)类使用纯 Java 获取实体管理器。

有关如何使用 Java 引导 JPA 实现的详细说明，请查看[这篇文章](https://www.baeldung.com/java-bootstrap-jpa)。

### 3.4. UserApplication类_

最后，让我们重构初始的UserApplication类，以便它可以使用JpaUserDao实例并在User实体上运行 CRUD 操作：

```java
public class UserApplication {

    private static Dao<User> jpaUserDao;

    // standard constructors
    
    public static void main(String[] args) {
        User user1 = getUser(1);
        System.out.println(user1);
        updateUser(user1, new String[]{"Jake", "jake@domain.com"});
        saveUser(new User("Monica", "monica@domain.com"));
        deleteUser(getUser(2));
        getAllUsers().forEach(user -> System.out.println(user.getName()));
    }
    
    public static User getUser(long id) {
        Optional<User> user = jpaUserDao.get(id);
        
        return user.orElseGet(
          () -> new User("non-existing user", "no-email"));
    }
    
    public static List<User> getAllUsers() {
        return jpaUserDao.getAll();
    }
    
    public static void updateUser(User user, String[] params) {
        jpaUserDao.update(user, params);
    }
    
    public static void saveUser(User user) {
        jpaUserDao.save(user);
    }
    
    public static void deleteUser(User user) {
        jpaUserDao.delete(user);
    }
}
```

这里的例子非常有限。但它对于展示如何将 DAO 模式的功能与实体管理器提供的功能相集成很有用。

在大多数应用程序中，都有一个 DI 框架，它负责将JpaUserDao实例注入到UserApplication类中。为了简单起见，我们省略了这个过程的细节。

这里需要强调的最相关的一点是JpaUserDao类如何帮助使UserApplication类完全不知道持久层如何执行 CRUD 操作。

此外，我们可以将 MySQL 换成任何其他 RDBMS(甚至换成平面数据库)，我们的应用程序将继续按预期工作，这要归功于Dao接口和实体管理器提供的抽象级别。

## 4。总结

在本文中，我们深入了解了 DAO 模式的关键概念。我们看到了如何在 Java 中实现它以及如何在 JPA 的实体管理器之上使用它。