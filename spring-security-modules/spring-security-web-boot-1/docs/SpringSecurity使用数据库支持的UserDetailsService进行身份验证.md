## 1. 概述

在本文中，我们将展示如何创建一个自定义的数据库支持的UserDetailsService以使用Spring Security进行身份验证。

## 2. UserDetailsService

UserDetailsService接口用于检索与用户相关的数据。它有一个名为loadUserByUsername()的方法，可以重写该方法以自定义查找用户的过程。

DaoAuthenticationProvider使用它在身份验证期间加载有关用户的详细信息。

## 3. User模型

为了存储用户，我们将创建一个映射到数据库表的User实体，具有以下属性：

```java
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    // standard getters and setters
}
```

## 4. 检索用户

为了检索与username关联的User，我们将通过扩展JpaRepository接口使用Spring Data创建一个DAO类：

```java
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
```

## 5. UserDetailsService

为了提供我们自己的UserDetailsService，我们需要实现UserDetailsService接口。

我们将创建一个名为MyUserDetailsService的类，它会重写接口的loadUserByUsername()方法。

在此方法中，我们使用UserRepository检索User对象，如果存在，则将其包装到MyUserPrincipal对象中(该对象实现UserDetail)，并返回它：

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException(username);
        return new MyUserPrincipal(user);
    }
}
```

MyUserPrincipal类定义如下：

```java
public class MyUserPrincipal implements UserDetails {
    private User user;

    public MyUserPrincipal(User user) {
        this.user = user;
    }
    // ...
}
```

## 6. Spring配置

我们将演示两种类型的Spring配置：XML和基于注解的配置，这对于使用我们的自定义UserDetailsService实现是必需的。

### 6.1 注解配置

**要启用自定义的UserDetailsService，我们需要做的就是将其作为bean添加到我们的应用程序上下文中**。

由于我们使用@Service注解配置了我们的类，因此应用程序将在组件扫描期间自动检测到它，并从这个类创建一个bean。因此，我们在这里不需要做任何其他事情。

或者，我们可以：

+ 使用AuthenticationManagerBuilder#userDetailsService方法在authenticationManager中配置它
+ 将其设置为自定义authenticationProvider bean中的属性，然后使用AuthenticationManagerBuilder#authenticationProvider方法注入该属性

### 6.2 XML配置

另一方面，对于XML配置，我们需要定义一个类型为MyUserDetailsService的bean，并将其注入到Spring的authentication-provider bean中：

```xml
<bean id="myUserDetailsService" class="cn.tuyucheng.security.MyUserDetailsService"/>

<security:authentication-manager>
    <security:authentication-provider user-service-ref="myUserDetailsService" >
        <security:password-encoder ref="passwordEncoder"/>
    </security:authentication-provider>
</security:authentication-manager>
    
<bean id="passwordEncoder" class="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder">
    <constructor-arg value="11"/>
</bean>
```

## 7. 其他数据库支持的身份验证选项

AuthenticationManagerBuilder提供了另一种在我们的应用程序中配置基于JDBC的身份验证的方法。

我们必须使用DataSource实例配置AuthenticationManagerBuilder.jdbcAuthentication。如果我们的数据库遵循[Spring User模式](https://docs.spring.io/spring-security/reference/servlet/appendix/database-schema.html#_user_schema)，那么默认配置将非常适合我们。

我们在[之前的文章](https://www.baeldung.com/java-config-spring-security#Authentication)中演示了使用这种方法的基本配置。

由此配置生成的JdbcUserDetailsManager实体也实现了UserDetailsService。

因此，我们可以得出结论，这种配置更容易实现，特别是如果我们使用自动配置[DataSource](https://www.baeldung.com/spring-boot-configure-data-source-programmatic)的Spring Boot。

无论如何，如果我们需要更高级别的灵活性，准确自定义应用程序获取用户详细信息的方式，那么我们将选择本教程中采用的方法。

## 8. 总结

总而言之，在本文中，我们展示了如何创建由数据库支持的基于Spring的自定义UserDetailsService。