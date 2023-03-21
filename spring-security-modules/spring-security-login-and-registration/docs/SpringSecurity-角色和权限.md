## 1. 概述

本教程继续使用Spring Security注册系列，了解如何正确实现**角色和权限**。

## 延伸阅读

### [Spring Security表达式介绍](https://www.baeldung.com/spring-security-expressions)

Spring Security表达式的简单实用指南。

[阅读更多](https://www.baeldung.com/spring-security-expressions)→

### [Spring方法安全简介](https://www.baeldung.com/spring-security-method-security)

使用Spring Security框架的方法级安全指南。

[阅读更多](https://www.baeldung.com/spring-security-method-security)→

### [Spring Security-登录后重定向到上一个URL](https://www.baeldung.com/spring-security-redirect-login)

Spring Security登录后重定向的一个简短例子。

[阅读更多](https://www.baeldung.com/spring-security-redirect-login)→

## 2. 用户、角色和权限

我们有三个主要实体：

-   User
-   Role表示用户在系统中的高级角色。每个角色都有一组低级权限。
-   Privilege表示系统中的低级别、细粒度的特权/权限。

这是User：

```java
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;
    private boolean tokenExpired;

    @ManyToMany
    @JoinTable(name = "users_roles",
          joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;
}
```

如我们所见，用户包含角色以及正确注册机制所必需的一些其他详细信息。

接下来，这是Role：

```java
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    @ManyToMany
    @JoinTable(name = "roles_privileges",
          joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Collection<Privilege> privileges;
}
```

最后，让我们看看Privilege：

```java
@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;
}
```

如我们所见，我们将用户<->角色和角色<->权限关系视为**多对多双向关系**。

## 3. 设置权限和角色

接下来，让我们专注于在系统中对权限和角色进行一些早期设置。

我们将它与应用程序的启动相关联，我们将在ContextRefreshedEvent上使用ApplicationListener在服务器启动时加载我们的初始数据：

```java
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail("test@test.com");
        user.setRoles(Arrays.asList(adminRole));
        user.setEnabled(true);
        userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
```

那么，在这段简单的设置代码中发生了什么？没什么复杂的：

-   我们创建了权限
-   然后我们创建角色并为他们分配权限
-   最后，我们创建一个用户并为其分配一个角色

请注意我们如何**使用alreadySetup标志来确定设置程序是否需要运行**。这仅仅是因为ContextRefreshedEvent可能会被多次触发，具体取决于我们在应用程序中配置的上下文数量。而我们只想运行一次设置程序。

这里有两个快速说明。我们先来看看术语。我们在这里使用Privilege – Role术语。但在Spring中，这些略有不同。在Spring中，我们的Privilege被称为Role，也被称为(授予的)authority，这有点令人困惑。

当然，这对于实现来说不是问题，但绝对值得注意。

**其次，这些Spring角色(我们的Privilege)需要一个前缀**。默认情况下，该前缀是“ROLE”，但可以更改。我们在这里没有使用该前缀，只是为了简单起见，但请记住，如果我们没有明确更改它，它将是必需的。

## 4. 自定义UserDetailsService

现在让我们看看身份验证过程。

我们将了解如何在我们的自定义UserDetailsService中检索用户，以及如何根据用户分配的角色和权限映射正确的权限集：

```java
@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new org.springframework.security.core.userdetails.User(
                  " ", " ", true, true, true, true,
                  getAuthorities(Arrays.asList(roleRepository.findByName("ROLE_USER"))));
        }

        return new org.springframework.security.core.userdetails.User(
              user.getEmail(), user.getPassword(), user.isEnabled(), true, true,
              true, getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles) {
        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
```

这里要遵循的有趣的事情是Privilege(和Role)如何映射到GrantedAuthority实体。

这种映射使得整个安全配置**非常灵活和强大**。我们可以根据需要混合和匹配角色和权限，最后，它们将正确映射到权限并返回到框架。

## 5. 角色层次

此外，让我们将我们的角色组织成层次结构。

我们已经了解了如何通过将权限映射到角色来实现基于角色的访问控制。这使我们可以将单个角色分配给用户，而不必分配所有单独的权限。

但是，随着角色数量的增加，**用户可能需要多个角色**，从而导致角色爆炸：

<img src="../assets/img_7.png">

为了克服这个问题，我们可以使用Spring Security的角色层次结构：

<img src="../assets/img_8.png">

**分配角色ADMIN会自动为用户提供STAFF和USER角色的权限**。

但是，具有STAFF角色的用户只能执行STAFF和USER角色操作。

让我们通过简单地公开一个RoleHierarchy类型的bean在Spring Security中创建这个层次结构：

```java
@Bean
public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    String hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER";
    roleHierarchy.setHierarchy(hierarchy);
    return roleHierarchy;
}
```

我们在表达式中使用>符号来定义角色层次结构。在这里，我们将角色ADMIN配置为包含角色STAFF，而后者又包含角色USER。

最后，为了在Spring Web Expressions中包含这个角色层次结构，我们将roleHierarchy实例添加到WebSecurityExpressionHandler：

```java
@Bean
public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
    DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
    expressionHandler.setRoleHierarchy(roleHierarchy());
    return expressionHandler;
}
```

最后，将expressionHandler添加到http.authorizeRequests()中：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .authorizeRequests()
            .expressionHandler(webSecurityExpressionHandler())
            .antMatchers(HttpMethod.GET, "/roleHierarchy")
            .hasRole("STAFF")
    // ...
}
```

端点/roleHierarchy受ROLE_STAFF保护，以证明webSecurityExpressionHandler正常工作。

正如我们所看到的，角色层次结构是减少我们需要添加到用户的角色和权限数量的好方法。

## 6. 用户注册

最后，让我们看一下新用户的注册。

我们已经了解了设置程序如何创建用户并为其分配角色(和权限)。

现在让我们来看看在新用户注册期间需要如何执行此操作：

```java
@Override
public User registerNewUserAccount(UserDto accountDto) throws EmailExistsException {
    if (emailExist(accountDto.getEmail())) {
        throw new EmailExistsException("There is an account with that email adress: " + accountDto.getEmail());
    }
    User user = new User();

    user.setFirstName(accountDto.getFirstName());
    user.setLastName(accountDto.getLastName());
    user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
    user.setEmail(accountDto.getEmail());

    user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
    return repository.save(user);
}
```

在这个简单的实现中，由于我们假设正在注册标准用户，因此我们为其分配ROLE_USER角色。

当然，更复杂的逻辑可以很容易地以相同的方式实现，或者通过多个硬编码的注册方法，或者通过允许客户端发送正在注册的用户类型。

## 7. 总结

在本文中，我们说明了如何使用JPA为Spring Security支持的系统实现角色和权限。

我们还配置了一个角色层次结构来简化我们的访问控制配置。