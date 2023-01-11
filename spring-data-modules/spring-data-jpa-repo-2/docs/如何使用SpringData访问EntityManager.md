## 1. 概述

在处理Spring Data应用程序时，我们通常不需要直接访问[EntityManager]()。但是，有时我们可能想要访问它，例如，创建自定义查询或分离实体。

在这个简短的教程中，我们了解如何通过扩展Spring Data [Repository]()来访问EntityManager。

## 2.使用Spring Data访问EntityManager

**我们可以通过创建一个扩展的自定义Repository来获取EntityManager，例如，一个内置的JpaRepository**。

首先，我们定义一个[Entity]()，例如我们要存储在数据库中的用户：

```java
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    // ...
}
```

**我们无法直接访问JpaRepository中的EntityManager**，因此我们需要创建自己的。

下面使用自定义查找方法创建一个自定义的Repository接口：

```java
public interface CustomUserRepository {
    User customFindMethod(Long id);
}
```

**使用@PeristenceContext注解，我们可以在实现类中注入EntityManager**：

```java
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User customFindMethod(Long id) {
        return (User) entityManager.createQuery("FROM User u WHERE u.id = :id")
            .setParameter("id", id)
            .getSingleResult();
    }
}
```

**同样，我们可以使用@PersistenceUnit注解**，在这种情况下，我们访问的是EntityManagerFactory，并从中访问EntityManager。

最后，我们创建一个同时扩展JpaRepository和CustomRepository的Repository：

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
}
```

最后，我们可以创建一个Spring Boot应用程序并进行测试，以检查一切是否正常并按预期工作：

```java
@SpringBootTest(classes = CustomRepositoryApplication.class)
class CustomRepositoryUnitTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void givenCustomRepository_whenInvokeCustomFindMethod_thenEntityIsFound() {
        User user = new User();
        user.setEmail("foo@gmail.com");
        user.setName("userName");

        User persistedUser = userRepository.save(user);

        assertEquals(persistedUser, userRepository.customFindMethod(user.getId()));
    }
}
```

## 3. 总结

在本文中，我们介绍了在Spring Data应用程序中访问EntityManager的快速示例。

我们可以访问自定义Repository中的EntityManager，并且仍然可以通过扩展其功能来使用我们的Spring Data Repository。