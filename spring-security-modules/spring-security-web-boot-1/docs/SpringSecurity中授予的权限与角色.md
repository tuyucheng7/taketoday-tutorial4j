## 1. 概述

在这篇文章中，我们将解释**Spring Security中Role和GrantedAuthority之间微妙但重要的区别**。

## 2. GrantedAuthority

在Spring Security中，我们可以将每个**GrantedAuthority视为一个单独的权限**。
例如READ_AUTHORITY、WRITE_PRIVILEGE甚至CAN_EXECUTE_AS_ROOT。重要的是要理解这个名字是任意的。

当直接使用GrantedAuthority时，例如通过使用hasAuthority('READ_AUTHORITY')之类的表达式，我们**以细粒度的方式限制访问**。

正如你可能知道的那样，我们也可以使用特权来引用权限的概念。

## 3. 角色作为权限

类似地，在Spring Security中，我们可以**将每个Role看作是一个粗粒度的GrantedAuthority**，它以String表示，并以“ROLE”为前缀。
当直接使用Role时，例如通过hasRole("ADMIN")之类的表达式，我们以粗粒度的方式限制访问。

值得注意的是，默认的“ROLE”前缀是可配置的，但本文不做详细说明。

**这两者之间的核心区别在于我们如何使用该功能所附加的语义**。对于框架开说，差异很小，并且它基本上以完全相同的方式处理这些问题。

## 4. 角色作为容器

现在我们已经了解了框架如何使用角色概念，让我们也快速讨论一种替代方案，即**使用角色作为权限/特权的容器**。

这是一种更高级别的角色方法，使它们成为一个更面向业务的概念，而不是一个以实现为中心的概念。

Spring Security框架在我们应该如何使用这个概念方面没有给出任何指导，所以选择完全是特定于实现的。

## 5. Spring Security配置

我们可以通过使用READ_AUTHORITY限制用户对/protectedbyauthority的访问来演示细粒度的授权要求。

我们可以通过将/protectedbyrole的访问权限限制为具有ROLE_USER的用户来演示粗粒度的授权要求。

让我们在Security配置中实现这样一个场景：

```text
@Override
protected void configure(HttpSecurity http) throws Exception {
    // ...
    .antMatchers("/protectedbyrole").hasRole("USER")
    .antMatchers("/protectedbyauthority").hasAuthority("READ_PRIVILEGE")
    // ...
}
```

## 6. 简单数据初始化

现在我们已经更好地理解了核心概念，接下来我们在应用程序启动时初始化一些数据。

当然，这是一种非常简单的方法，可以在开发过程中与一些初步测试用户一起运行，而不是在生产中处理数据的方式。

我们将监听容器上下文刷新事件：

```java

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        // == create initial privileges
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        // == create initial roles
        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        List<Privilege> rolePrivileges = new ArrayList<>();
        createRoleIfNotFound("ROLE_USER", rolePrivileges);

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        User user = new User();
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setEmail("admin@test.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRoles(List.of(adminRole));
        user.setEnabled(true);
        userRepository.save(user);

        Role basicRole = roleRepository.findByName("ROLE_USER");
        User basicUser = new User();
        basicUser.setFirstName("User");
        basicUser.setLastName("User");
        basicUser.setEmail("user@test.com");
        basicUser.setPassword(passwordEncoder.encode("user"));
        basicUser.setRoles(List.of(basicRole));
        basicUser.setEnabled(true);
        userRepository.save(basicUser);
        alreadySetup = true;
    }
}
```

这里的代码实现并不重要，通常取决于你使用的持久层框架。重点是我们坚持我们在代码中使用权限。

## 7. UserDetailsService

我们的UserDetailsService实现是进行权限映射的地方。一旦用户通过了身份验证，
我们的getAuthorities()方法就会填充并返回一个UserDetails对象：

```java

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UsernameNotFoundException("No user found with username: " + email);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            authorities.addAll(role.getPrivileges()
                    .stream()
                    .map(p -> new SimpleGrantedAuthority(p.getName()))
                    .toList());
        }
        return authorities;
    }
}
```

## 8. 运行和测试

**要验证基于角色的授权，我们需要**：

+ 访问http://localhost:8082/protectedbyrole
+ 以user@test.com进行身份验证(密码为"user")
+ 注意授权成功
+ 访问http://localhost:8082/protectedbyauthority
+ 注意授权失败

**要验证基于权限的授权，我们需要注销应用程序，然后**：

+ 访问http://localhost:8082/protectedbyauthority
+ 以admin@test.com进行身份验证(密码为"admin")
+ 注意授权成功
+ 访问http://localhost:8082/protectedbyrole
+ 注意授权失败

## 9. 总结

在这个教程中，我们介绍了Spring Security中Role和GrantedAuthority之间的区别。