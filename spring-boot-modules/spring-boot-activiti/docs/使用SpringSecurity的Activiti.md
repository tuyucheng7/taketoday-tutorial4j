## 1. 概述

Activiti 是一个开源的 BPM(业务流程管理)系统。有关介绍，请查看我们[的JavaActiviti 指南](https://www.baeldung.com/java-activiti)。

Activiti 和 Spring 框架都提供了自己的身份管理。但是，在集成了这两个项目的应用程序中，我们可能希望将两者合并为一个用户管理过程。

在下文中，我们将探讨实现此目的的两种可能性：一种是为 Spring Security 提供 Activiti 支持的用户服务，另一种是将 Spring Security 用户源插入 Activiti 身份管理。

## 2.Maven依赖

要在Spring Boot项目中设置 Activiti，请查看[我们之前的文章](https://www.baeldung.com/spring-activiti)。除了activiti-spring-boot-starter-basic，我们还需要[activiti-spring-boot-starter-security](https://search.maven.org/classic/#search|ga|1|a%3A"activiti-spring-boot-starter-security")依赖：

```xml
<dependency>
    <groupId>org.activiti</groupId>
    <artifactId>activiti-spring-boot-starter-security</artifactId>
    <version>6.0.0</version>
</dependency>
```

## 3.使用Activiti进行身份管理

对于这种情况，Activiti 启动器提供了一个Spring Boot自动配置类，它使用HTTP Basic身份验证保护所有 REST 端点。

自动配置还会创建一个IdentityServiceUserDetailsS ervice类的UserDetailsService bean。

该类实现了 Spring 接口UserDetailsService并覆盖了loadUserByUsername()方法。此方法检索具有给定id的 Activiti User对象，并使用它创建 Spring UserDetails对象。

此外，Activiti Group对象对应一个 Spring 用户角色。

这意味着当我们登录到 Spring Security 应用程序时，我们将使用 Activiti 凭据。

### 3.1. 设置 Activiti 用户

首先，让我们使用IdentityService在主@SpringBootApplication类中定义的InitializingBean中创建一个用户：

```java
@Bean
InitializingBean usersAndGroupsInitializer(IdentityService identityService) {
    return new InitializingBean() {
        public void afterPropertiesSet() throws Exception {
            User user = identityService.newUser("activiti_user");
            user.setPassword("pass");
            identityService.saveUser(user);

            Group group = identityService.newGroup("user");
            group.setName("ROLE_USER");
            group.setType("USER");
            identityService.saveGroup(group);
            identityService.createMembership(user.getId(), group.getId());
        }
    };
}
```

你会注意到，由于 Spring Security 将使用它，因此组对象名称必须采用“ROLE_X”形式。

### 3.2. 弹簧安全配置

如果我们想使用不同的安全配置而不是 HTTP 基本身份验证，首先我们必须排除自动配置：

```java
@SpringBootApplication(
  exclude = org.activiti.spring.boot.SecurityAutoConfiguration.class)
public class ActivitiSpringSecurityApplication {
    // ...
}
```

然后，我们可以提供我们自己的 Spring Security 配置类，它使用IdentityServiceUserDetailsS ervice 从 Activiti 数据源检索用户：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
    @Autowired
    private IdentityService identityService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
      throws Exception {
 
        auth.userDetailsService(userDetailsService());
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return new IdentityServiceUserDetailsService(
          this.identityService);
    }

    // spring security configuration
}
```

## 4. 使用 Spring Security 进行身份管理

如果我们已经使用 Spring Security 设置了用户管理，并且我们想将 Activiti 添加到我们的应用程序中，那么我们需要自定义 Activiti 的身份管理。

为此，我们必须扩展两个主要类：UserEntityManagerImpl和GroupEntityManagerImpl，它们处理用户和组。

让我们更详细地了解其中的每一个。

### 4.1. 扩展UserEntityManagerImpl

让我们创建自己的类来扩展UserEntityManagerImpl类：

```java
public class SpringSecurityUserManager extends UserEntityManagerImpl {

    private JdbcUserDetailsManager userManager;

    public SpringSecurityUserManager(
      ProcessEngineConfigurationImpl processEngineConfiguration, 
      UserDataManager userDataManager, 
      JdbcUserDetailsManager userManager) {
 
        super(processEngineConfiguration, userDataManager);
        this.userManager = userManager;
    }
    
    // ...
}
```

此类需要上述形式的构造函数，以及 Spring Security 用户管理器。在我们的例子中，我们使用了数据库支持的UserDetailsManager。

我们要覆盖的主要方法是那些处理用户检索的方法：findById()、 findUserByQueryCriteria()和findGroupsByUser()。

findById ()方法使用JdbcUserDetailsManager查找UserDetails对象并将其转换为User对象：

```java
@Override
public UserEntity findById(String userId) {
    UserDetails userDetails = userManager.loadUserByUsername(userId);
    if (userDetails != null) {
        UserEntityImpl user = new UserEntityImpl();
        user.setId(userId);
        return user;
    }
    return null;
}
```

接下来，findGroupsByUser()方法查找用户的所有 Spring Security 权限并返回Group对象列表：

```java
public List<Group> findGroupsByUser(String userId) {
    UserDetails userDetails = userManager.loadUserByUsername(userId);
    if (userDetails != null) {
        return userDetails.getAuthorities().stream()
          .map(a -> {
            Group g = new GroupEntityImpl();
            g.setId(a.getAuthority());
            return g;
          })
          .collect(Collectors.toList());
    }
    return null;
}
```

findUserByQueryCriteria ()方法基于具有多个属性的UserQueryImpl对象，我们将从中提取组 ID 和用户 ID，因为它们在 Spring Security 中有对应关系：

```java
@Override
public List<User> findUserByQueryCriteria(
  UserQueryImpl query, Page page) {
    // ...
}
```

此方法遵循与上述类似的原则，即从UserDetails对象创建User对象。查看最后的 GitHub 链接以获得完整的实现。

同样，我们有findUserCountByQueryCriteria()方法：

```java
public long findUserCountByQueryCriteria(
  UserQueryImpl query) {
 
    return findUserByQueryCriteria(query, null).size();
}
```

checkPassword ()方法应该始终返回 true，因为密码验证不是由 Activiti 完成的：

```java
@Override
public Boolean checkPassword(String userId, String password) {
    return true;
}
```

对于其他方法，例如那些处理更新用户的方法，我们将抛出一个异常，因为这是由 Spring Security 处理的：

```java
public User createNewUser(String userId) {
    throw new UnsupportedOperationException("This operation is not supported!");
}
```

### 4.2. 扩展GroupEntityManagerImpl

SpringSecurityGroupManager类似于用户管理器类，不同之处在于它处理用户组：

```java
public class SpringSecurityGroupManager extends GroupEntityManagerImpl {

    private JdbcUserDetailsManager userManager;

    public SpringSecurityGroupManager(ProcessEngineConfigurationImpl 
      processEngineConfiguration, GroupDataManager groupDataManager) {
        super(processEngineConfiguration, groupDataManager);
    }

    // ...
}
```

这里要覆盖的主要方法是findGroupsByUser()方法：

```java
@Override
public List<Group> findGroupsByUser(String userId) {
    UserDetails userDetails = userManager.loadUserByUsername(userId);
    if (userDetails != null) {
        return userDetails.getAuthorities().stream()
          .map(a -> {
            Group g = new GroupEntityImpl();
            g.setId(a.getAuthority());
            return g;
          })
          .collect(Collectors.toList());
    }
    return null;
}
```

该方法检索 Spring Security 用户的权限并将它们转换为Group对象列表。

基于此，我们还可以重写findGroupByQueryCriteria()和findGroupByQueryCriteriaCount()方法：

```java
@Override
public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
    if (query.getUserId() != null) {
        return findGroupsByUser(query.getUserId());
    }
    return null;
}

@Override
public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
    return findGroupByQueryCriteria(query, null).size();
}
```

可以覆盖更新组的其他方法以抛出异常：

```java
public Group createNewGroup(String groupId) {
    throw new UnsupportedOperationException("This operation is not supported!");
}
```

### 4.3. 流程引擎配置

在定义了两个身份管理器类之后，我们需要将它们连接到配置中。

spring 启动器为我们自动配置SpringProcessEngineConfiguration。要修改它，我们可以使用InitializingBean：

```java
@Autowired
private SpringProcessEngineConfiguration processEngineConfiguration;

@Autowired
private JdbcUserDetailsManager userManager;

@Bean
InitializingBean processEngineInitializer() {
    return new InitializingBean() {
        public void afterPropertiesSet() throws Exception {
            processEngineConfiguration.setUserEntityManager(
              new SpringSecurityUserManager(processEngineConfiguration, 
              new MybatisUserDataManager(processEngineConfiguration), userManager));
            processEngineConfiguration.setGroupEntityManager(
              new SpringSecurityGroupManager(processEngineConfiguration, 
              new MybatisGroupDataManager(processEngineConfiguration)));
            }
        };
    }
```

在这里，现有的processEngineConfiguration被修改为使用我们的自定义身份管理器。

如果我们想在Activiti中设置当前用户，我们可以使用方法：

```java
identityService.setAuthenticatedUserId(userId);
```

请记住，这会设置一个ThreadLocal属性，因此每个线程的值都不同。

## 5.总结

在本文中，我们了解了将 Activiti 与 Spring Security 集成的两种方式。