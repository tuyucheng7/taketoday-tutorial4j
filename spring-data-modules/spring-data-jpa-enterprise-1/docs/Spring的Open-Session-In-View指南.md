## 1. 概述

每个请求对应一个会话是一种事务模式，用于将持久性会话和请求生命周期绑定在一起。毫不奇怪，Spring自带了这种模式的实现，名为[OpenSessionInViewInterceptor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/orm/hibernate5/support/OpenSessionInViewInterceptor.html)，以促进使用惰性关联，从而提高开发人员的生产力。

在本教程中，首先，我们将了解拦截器的内部工作原理，然后，我们将了解这种[有争议](https://github.com/spring-projects/spring-boot/issues/7107)的模式如何成为我们应用程序的双刃剑！

## 2. Open Session in View介绍

为了更好地理解Open Session in View(OSIV)的作用，假设我们有一个传入请求：

1.  Spring在请求开始时打开一个新的Hibernate Session，这些Session不一定连接到数据库。
2.  每当应用程序需要Session时，它都会重用已经存在的Session。
3.  在请求结束时，同一个拦截器关闭该Session。

乍一看，启用此功能可能很有意义。毕竟，框架会处理会话的创建和终止，因此开发人员不会关心这些看似低级的细节。这反过来又提高了开发人员的生产力。

然而，**有时OSIV会在生产中引起微妙的性能问题**。通常，这些类型的问题很难诊断。

### 2.1 Spring Boot

**默认情况下，OSIV在Spring Boot应用程序中处于激活状态**。尽管如此，从Spring Boot 2.0开始，它会警告我们，如果我们没有明确配置它，它会在应用程序启动时启用：

```shell
spring.jpa.open-in-view is enabled by default. Therefore, database 
queries may be performed during view rendering.Explicitly configure 
spring.jpa.open-in-view to disable this warning
```

无论如何，我们可以通过使用spring.jpa.open-in-view配置属性来禁用OSIV：

```properties
spring.jpa.open-in-view=false
```

### 2.2 模式还是反模式？

对OSIV的反应一直很复杂。支持OSIV阵营的主要论点是开发人员的生产力，尤其是在处理[惰性关联](https://www.baeldung.com/hibernate-lazy-eager-loading)时。

另一方面，数据库性能问题是反OSIV运动的主要论点。稍后，我们将详细评估这两个论点。

## 3. 惰性初始化

由于OSIV将Session生命周期绑定到每个请求，**因此即使在从显式@Transactional服务返回后Hibernate也可以解析惰性关联**。

为了更好地理解这一点，假设我们正在对用户及其安全权限进行建模：

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @ElementCollection
    private Set<String> permissions;

    // getters and setters
}
```

与其他一对多和多对多关系类似，permissions属性是一个惰性集合。

然后，在我们的服务层实现中，让我们使用@Transactional显式划分我们的事务边界：

```java
@Service
public class SimpleUserService implements UserService {

    private final UserRepository userRepository;

    public SimpleUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findOne(String username) {
        return userRepository.findByUsername(username);
    }
}
```

### 3.1 期望

当我们的代码调用findOne方法时，我们期望发生以下情况：

1.  首先，Spring代理拦截调用并获取当前事务，如果不存在则创建一个事务。
2.  然后，它将方法调用委托给我们的实现。
3.  **最后，代理提交事务并因此关闭底层Session，我们只需要在服务层中使用该Session**。

在findOne方法实现中，我们没有初始化permissions权限集合。**因此，在方法返回后，我们不应该能够使用permissions**。如果我们对这个属性进行迭代，我们应该得到一个LazyInitializationException。

### 3.2 真实的世界

让我们编写一个简单的REST控制器，看看我们是否可以使用permissions属性：

```java
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> findOne(@PathVariable String username) {
        return userService
              .findOne(username)
              .map(DetailedUserDto::fromEntity)
              .map(ResponseEntity::ok)
              .orElse(ResponseEntity.notFound().build());
    }
}
```

在这里，我们在实体到DTO转换期间迭代permissions。由于我们预计转换会因LazyInitializationException而失败，因此以下测试不应通过：

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("root");
        user.setPermissions(new HashSet<>(Arrays.asList("PERM_READ", "PERM_WRITE")));

        userRepository.save(user);
    }

    @Test
    void givenTheUserExists_WhenOsivIsEnabled_ThenLazyInitWorksEverywhere() throws Exception {
        mockMvc.perform(get("/users/root"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.username").value("root"))
              .andExpect(jsonPath("$.permissions", containsInAnyOrder("PERM_READ", "PERM_WRITE")));
    }
}
```

然而，这个测试没有抛出任何异常，它通过了。

**因为OSIV在请求开始时创建一个Session，事务代理使用当前可用的Session而不是创建一个全新的Session**。

因此，尽管我们可能有所期望，但实际上我们甚至可以在显式@Transactional之外使用permissions属性。此外，这些惰性关联可以在当前请求范围内的任何地方获取。

### 3.3 关于开发人员生产力

**如果没有启用OSIV，我们将不得不在事务上下文中手动初始化所有必要的惰性关联**。最基本的(通常也是错误的)方法是使用Hibernate.initialize()方法：

```java
@Override
@Transactional(readOnly = true)
public Optional<User> findOne(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    user.ifPresent(u -> Hibernate.initialize(u.getPermissions()));

    return user;
}
```

到目前为止，OSIV对开发人员生产力的影响是显而易见的。然而，这并不总是与开发人员的生产力有关。

## 4. 表演反派

假设我们必须扩展简单的用户服务以**在从数据库中获取用户后调用另一个远程服务**：

```java
@Override
public Optional<User> findOne(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isPresent()) {
        // remote call
    }

    return user;
}
```

在这里，我们删除了@Transactional注解，因为我们显然不希望在等待远程服务时保持连接的Session。

### 4.1 避免混合IO

让我们澄清一下如果我们不删除@Transactional注解会发生什么。**假设新的远程服务响应速度比平常慢一点**：

1.  首先，Spring代理获取当前Session或创建一个新Session。无论哪种方式，这个会话都还没有连接。也就是说，它没有使用池中的任何连接。
2.  一旦我们执行查找用户的查询，Session就会连接起来并从池中借用一个Connection。
3.  如果整个方法是事务性的，则该方法继续调用慢速远程服务，同时保留借用的Connection。

**想象一下，在此期间，我们收到了对findOne方法的大量调用**。然后，过了一会儿，所有连接都可能等待来自该API调用的响应。因此，**我们可能很快就会耗尽数据库连接**。

在事务上下文中将数据库IO与其他类型的IO混合是一种代码坏味道，我们应该不惜一切代价避免这种情况。

无论如何，**由于我们从我们的服务中删除了@Transactional注解，所以我们期望是安全的**。

### 4.2 耗尽连接池

**当OSIV处于活动状态时，即使我们删除@Transactional，当前请求范围内也始终存在一个Session**。尽管此会话最初未连接，但在我们的第一个数据库IO之后，它会连接并保持连接状态直到请求结束。

因此，在OSIV存在的情况下，我们看似无辜且最近优化的服务实现是灾难的根源：

```java
@Override
public Optional<User> findOne(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isPresent()) {
        // remote call
    }

    return user;
}
```

以下是启用OSIV时发生的情况：

1.  在请求开始时，相应的过滤器创建一个新的Session。
2.  当我们调用findByUsername方法时，该Session从池中借用一个Connection。
3.  会话将保持连接，直到请求结束。

尽管我们期望我们的服务代码不会耗尽连接池，但OSIV的存在可能会使整个应用程序无响应。

更糟糕的是，**问题的根本原因(远程服务慢)和症状(数据库连接池)是不相关的**。由于这种相关性很小，因此在生产环境中很难诊断此类性能问题。

### 4.3 不必要的查询

不幸的是，耗尽连接池并不是唯一与OSIV相关的性能问题。

由于Session在整个请求生命周期内都是打开的，**因此某些属性导航可能会在事务上下文之外触发更多不需要的查询**。甚至有可能最终出现[n+1选择问题](https://www.baeldung.com/hibernate-common-performance-problems-in-logs)，最糟糕的消息是我们可能直到生产才注意到这一点。

雪上加霜的是，**Session在[自动提交模式](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html#setAutoCommit(boolean))下执行所有这些额外的查询**。在自动提交模式下，每条SQL语句都被视为一个事务，并在执行后立即自动提交。这反过来又给数据库带来了很大的压力。

## 5. 明智地选择

OSIV是模式还是反模式无关紧要。这里最重要的是我们生活的现实。

**如果我们正在开发一个简单的CRUD服务，那么使用OSIV可能是有意义的**，因为我们可能永远不会遇到这些性能问题。

另一方面，**如果我们发现自己调用了很多远程服务，或者在我们的事务上下文之外发生了太多事情，强烈建议完全禁用OSIV**。 

如有疑问，请从不使用OSIV开始，因为我们之后可以轻松启用它。另一方面，禁用已启用的OSIV可能很麻烦，因为我们可能需要处理大量的LazyInitializationExceptions。

底线是，我们应该意识到使用或忽略OSIV时的权衡。

## 6. 备选方案

如果我们禁用OSIV，那么我们应该在处理懒惰关联时以某种方式防止潜在的LazyInitializationExceptions。在处理惰性关联的几种方法中，我们将在这里列举其中的两种。

### 6.1 实体图

在Spring Data JPA中定义查询方法时，我们可以使用@EntityGraph标注查询方法以[急切地获取实体的某些部分](https://www.baeldung.com/spring-data-jpa-named-entity-graphs)：

```java
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "permissions")
    Optional<User> findByUsername(String username);
}
```

在这里，我们定义了一个临时实体图来急切地加载permissions属性，即使默认情况下它是一个惰性集合。

如果我们需要从同一个查询返回多个投影，那么我们应该定义具有不同实体图配置的多个查询：

```java
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "permissions")
    Optional<User> findDetailedByUsername(String username);

    Optional<User> findSummaryByUsername(String username);
}
```

### 6.2 使用Hibernate.initialize()时的注意事项

有人可能会争辩说，我们可以使用臭名昭著的Hibernate.initialize()来在需要的地方获取惰性关联，而不是使用实体图：

```java
@Override
@Transactional(readOnly = true)
public Optional<User> findOne(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    user.ifPresent(u -> Hibernate.initialize(u.getPermissions()));
        
    return user;
}
```

他们可能很聪明，还建议调用getPermissions()方法来触发获取过程：

```java
Optional<User> user = userRepository.findByUsername(username);
user.ifPresent(u -> {
    Set<String> permissions = u.getPermissions();
    System.out.println("Permissions loaded: " + permissions.size());
});
```

不建议使用这两种方法，因为除了原始查询之外，**它们会产生一个(至少)额外的查询来获取惰性关联**。也就是说，Hibernate生成以下查询来获取用户及其权限：

```sql
> select u.id, u.username from users u where u.username=?
> select p.user_id, p.permissions from user_permissions p where p.user_id=?
```

虽然大多数数据库都非常擅长执行第二个查询，但我们应该避免额外的网络往返。

另一方面，如果我们使用实体图甚至[Fetch Joins](https://www.baeldung.com/jpa-join-types)，Hibernate只需一个查询即可获取所有必要的数据：

```sql
> select u.id, u.username, p.user_id, p.permissions from users u 
  left outer join user_permissions p on u.id=p.user_id where u.username=?
```

## 7. 总结

在本文中，我们将注意力转向了Spring和其他一些企业框架中一个颇具争议的特性：Open Session In View。首先，我们从概念上和实现上都熟悉了这种模式。然后我们从生产力和性能的角度对其进行了分析。