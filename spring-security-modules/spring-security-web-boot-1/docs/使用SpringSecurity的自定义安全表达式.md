## 1. 概述

在本教程中，我们将重点介绍如何**使用Spring Security创建自定义安全表达式**。

有时，[框架中可用的表达式](https://www.baeldung.com/spring-security-expressions)根本不够富有表现力。在这些情况下，构建一个语义比现有表达式更丰富的新表达式相对简单。

我们将首先讨论如何创建一个自定义的PermissionEvaluator，然后是一个完全自定义的表达式，最后讨论如何覆盖一个内置的安全表达式。

## 2. 用户实体

首先，让我们为创建新的安全表达式奠定基础。

让我们看一下我们的User实体-它有一个Privileges和一个Organization：

```java
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_privileges",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    )
    private Set<Privilege> privileges;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    private Organization organization;
    
    // standard getters and setters
}
```

这是Privilege：

```java
@Entity
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // standard getters and setters
}
```

这是Organization：

```java
@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // standard setters and getters
}
```

最后-我们将使用一个更简单的自定义Principal：

```java
public class MyUserPrincipal implements UserDetails {

    private User user;

    public MyUserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Privilege privilege : user.getPrivileges()) {
            authorities.add(new SimpleGrantedAuthority(privilege.getName()));
        }
        return authorities;
    }
    
    // ...
}
```

准备好所有这些类后，我们将在基本的UserDetailsService实现中使用我们的自定义Principal：

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new MyUserPrincipal(user);
    }
}
```

如你所见，这些类的关系并不复杂-用户拥有一个或多个权限，每个用户都属于一个组织。

## 3. 数据设置

接下来，让我们用简单的测试数据初始化我们的数据库：

```java
@Component
public class SetupData {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @PostConstruct
    public void init() {
        initPrivileges();
        initOrganizations();
        initUsers();
    }
}
```

这是我们的init方法：

```java
private void initUsers() {
    final Privilege privilege1 = privilegeRepository.findByName("FOO_READ_PRIVILEGE");
    final Privilege privilege2 = privilegeRepository.findByName("FOO_WRITE_PRIVILEGE");

    final User user1 = new User();
    user1.setUsername("john");
    user1.setPassword(encoder.encode("123"));
    user1.setPrivileges(new HashSet<>(List.of(privilege1)));
    user1.setOrganization(organizationRepository.findByName("FirstOrg"));
    userRepository.save(user1);

    final User user2 = new User();
    user2.setUsername("tom");
    user2.setPassword(encoder.encode("111"));
    user2.setPrivileges(new HashSet<>(Arrays.asList(privilege1, privilege2)));
    user2.setOrganization(organizationRepository.findByName("SecondOrg"));
    userRepository.save(user2);
}

private void initOrganizations() {
    final Organization org1 = new Organization("FirstOrg");
    organizationRepository.save(org1);

    final Organization org2 = new Organization("SecondOrg");
    organizationRepository.save(org2);
}

private void initPrivileges() {
    final Privilege privilege1 = new Privilege("FOO_READ_PRIVILEGE");
    privilegeRepository.save(privilege1);

    final Privilege privilege2 = new Privilege("FOO_WRITE_PRIVILEGE");
    privilegeRepository.save(privilege2);
}
```

注意：

+ 用户“john”只有FOO_READ_PRIVILEGE权限
+ 用户”tom“同时拥有FOO_READ_PRIVILEGE和FOO_WRITE_PRIVILEGE权限

## 4. 自定义权限评估器

此时，我们已准备好开始通过新的自定义权限评估器实现新表达式。

我们将使用用户的权限来保护我们的方法-但我们希望实现更开放、更灵活，而不是使用硬编码的权限名称。

### 4.1 PermissionEvaluator

为了创建我们自己的自定义权限评估器，我们需要实现PermissionEvaluator接口：

```java
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }
        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

        return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(auth, targetType.toUpperCase(), permission.toString().toUpperCase());
    }
}
```

这是我们的hasPrivilege()方法：

```java
private boolean hasPrivilege(Authentication authentication, String targetType, String permission) {
    for (GrantedAuthority grantedAuth : authentication.getAuthorities()) {
        if (grantedAuth.getAuthority().startsWith(targetType) && grantedAuth.getAuthority().contains(permission))
            return true;
    }
    return false;
}
```

**我们现在有一个新的安全表达式可以使用：hasPermission**。

因此，不要使用更硬编码的版本：

```java
@PostAuthorize("hasAuthority('FOO_READ_PRIVILEGE')")
```

我们可以使用：

```java
@PostAuthorize("hasPermission(returnObject, 'read')")
```

或者：

```java
@PreAuthorize("hasPermission(#id, 'Foo', 'read')")
```

> 注意：#id指方法参数，“Foo”指目标对象类型。

### 4.2 方法安全配置

仅仅定义CustomPermissionEvaluator是不够的-我们还需要在我们的方法安全配置中使用它：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return expressionHandler;
    }
}
```

### 4.3 实践中的例子

现在让我们开始在几个简单的控制器方法中使用新表达式：

```java
@Controller
public class MainController {

    @PostAuthorize("hasPermission(returnObject, 'read')")
    @GetMapping("/foos/{id}")
    @ResponseBody
    public Foo findById(@PathVariable long id) {
        return new Foo("Sample");
    }

    @PreAuthorize("hasPermission(#foo, 'write')")
    @PostMapping("/foos")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Foo create(@RequestBody Foo foo) {
        return foo;
    }
}
```

### 4.4 测试

现在让我们编写一个简单的测试，调用我们的API并确保一切正常：

```java
// In order to execute these tests, cn.tuyucheng.taketoday.roles.custom.CustomSecurityExpressionApplication needs to be running.
class CustomExpressionApplicationLiveTest {

    @Test
    void givenUserWithReadPrivilegeAndHasPermission_whenGetFooById_thenOK() {
        final Response response = givenAuth("john", "123").get("http://localhost:8080/foos/1");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    @Test
    void givenUserWithNoWritePrivilegeAndHasPermission_whenPostFoo_thenForbidden() {
        final Response response = givenAuth("john", "123").contentType(MediaType.APPLICATION_JSON_VALUE).body(new Foo("sample")).post("http://localhost:8080/foos");
        assertEquals(403, response.getStatusCode());
    }

    @Test
    void givenUserWithWritePrivilegeAndHasPermission_whenPostFoo_thenOk() {
        final Response response = givenAuth("tom", "111").and().body(new Foo("sample")).and().contentType(MediaType.APPLICATION_JSON_VALUE).post("http://localhost:8080/foos");
        assertEquals(201, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }
}
```

这是我们的givenAuth()方法：

```java
private RequestSpecification givenAuth(String username, String password) {
    return RestAssured.given().log().uri().auth().form(username, password, new FormAuthConfig("/login", "username", "password"));
}
```

## 5. 新的安全表达式

使用之前的解决方案，我们能够定义和使用hasPermission表达式-这可能非常有用。

但是，我们在这里仍然受到表达式本身的名称和语义的限制。

因此，在本节中我们将进行完全自定义-我们将实现一个名为isMember()的安全表达式，检查主体是否是组织的成员。

### 5.1 自定义方法安全表达式

```java
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean isMember(Long OrganizationId) {
        final User user = ((MyUserPrincipal) this.getPrincipal()).getUser();
        return user.getOrganization().getId().longValue() == OrganizationId.longValue();
    }
    // ...
}
```

isMember()用于检查当前用户是否是给定组织的成员。

另请注意我们如何扩展SecurityExpressionRoot以包含内置表达式。

### 5.2 自定义表达式处理程序

接下来，我们需要在表达式处理程序中注入CustomMethodSecurityExpressionRoot：

```java
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
```

### 5.3 方法安全配置

现在，我们需要在方法安全配置中使用我们的CustomMethodSecurityExpressionHandler：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return expressionHandler;
    }
}
```

### 5.4 使用新表达式

下面是一个使用isMember()保护控制器方法的简单示例：

```java
@Autowired
private OrganizationRepository organizationRepository;

@PreAuthorize("isMember(#id)")
@GetMapping("/organizations/{id}")
@ResponseBody
public Organization findOrgById(@PathVariable final long id) {
    return organizationRepository.findById(id).orElse(null);
}
```

### 5.5 测试

最后，这是对用户“john”的简单实时测试：

```java
@Test
void givenUserMemberInOrganization_whenGetOrganization_thenOK() {
    final Response response = givenAuth("john", "123").get("http://localhost:8080/organizations/1");
    assertEquals(200, response.getStatusCode());
    assertTrue(response.asString().contains("id"));
}

@Test
void givenUserMemberNotInOrganization_whenGetOrganization_thenForbidden() {
    final Response response = givenAuth("john", "123").get("http://localhost:8080/organizations/2");
    assertEquals(403, response.getStatusCode());
}
```

## 6. 禁用内置安全表达式

最后，让我们看看如何覆盖内置的安全表达式-这里我们以hasAuthority()为例。

### 6.1 自定义SecurityExpressionRoot

同样，我们从实现SecurityExpressionRoot开始-主要是因为内置方法是final的，所以我们不能重写它们：

```java
public class MySecurityExpressionRoot implements MethodSecurityExpressionOperations {

    public MySecurityExpressionRoot(Authentication authentication) {
        if (authentication == null)
            throw new IllegalArgumentException("Authentication object cannot be null");
        this.authentication = authentication;
    }

    @Override
    public final boolean hasAuthority(String authority) {
        throw new RuntimeException("method hasAuthority() not allowed");
    }
}
```

之后，我们必须将它注入到表达式处理程序中，然后将该处理程序注入到我们的配置中，就像我们在上面第5节中所做的那样。

### 6.2 使用表达式

现在，如果我们想使用hasAuthority()来保护方法-如下所示，当我们尝试访问方法时它会抛出RuntimeException：

```java
@PreAuthorize("hasAuthority('FOO_READ_PRIVILEGE')")
@GetMapping("/foos")
@ResponseBody
public Foo findFooByName(@RequestParam final String name) {
    return new Foo(name);
}
```

### 6.3 测试

最后，这是我们的简单测试：

```java
@Test
void givenDisabledSecurityExpression_whenGetFooByName_thenError() {
    final Response response = givenAuth("john", "123").get("http://localhost:8080/foos?name=sample");
    assertEquals(500, response.getStatusCode());
    assertTrue(response.asString().contains("method hasAuthority() not allowed"));
}
```

## 7. 总结

在本教程中，我们深入探讨了在Spring Security中实现自定义安全表达式的各种方法。