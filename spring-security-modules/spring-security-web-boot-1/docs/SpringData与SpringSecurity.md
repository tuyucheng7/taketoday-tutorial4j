## 1. 概述

Spring Security为与Spring Data的集成提供了良好的支持。
前者处理我们应用程序的安全方面，后者提供对数据库的方便访问。

在本文中，我们将讨论**如何将Spring Security与Spring Data集成，以支持更多特定于用户的查询**。

## 2. Spring Security + Spring Data配置

要启用Spring Security和Spring Data，像往常一样，我们可以采用基于Java或基于XML的配置。

### 2.1 Java配置

我们可以使用基于注解的配置将Spring Security添加到我们的项目中：

```java

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    // Bean definitions ...
}
```

其他配置细节包括过滤器、bean和其他安全规则的定义。

**要在Spring Security中启用Spring Data，我们只需将下面的bean添加到WebSecurityConfig**：

```java

@Configuration
@EnableWebSecurity
@ComponentScan("cn.tuyucheng.taketoday.relationships.security")
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
```

上面的定义支持自动解析类上注解的特定于Spring Data的表达式。

### 2.2 XML配置

基于XML的配置需要包含Spring Security的namespace：

```xml

<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.springframework.org/schema/beans
  http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
  http://www.springframework.org/schema/security
  http://www.springframework.org/schema/security/spring-security.xsd">
    ...
</beans:beans>
```

就像基于Java的配置中一样，对于基于XML的配置，我们也需要将SecurityEvaluationContextExtension bean添加到XML配置文件中：

```text
<bean class="org.springframework.security.data.repository.query.SecurityEvaluationContextExtension"/>
```

定义SecurityEvaluationContextExtension使Spring Security中的所有常用表达式都可以在Spring Data查询中使用。

这些常见的表达方式包括principal、authentication、isAnonymous()、hasRole([role])、isAuthenticated等。

## 3. 使用案例

### 3.1 限制AppUser字段的更新

在此示例中，我们将AppUser的lastLogin字段更新限制为当前唯一经过身份验证的用户。

我们的意思是，每当触发updateLastLogin方法时，它只会更新当前经过身份验证的用户的lastLogin字段。

为此，我们将以下查询方法添加到我们的UserRepository接口中：

```java
public interface UserRepository extends CrudRepository<AppUser, Long> {

    @Query("UPDATE AppUser u SET u.lastLogin = :lastLogin WHERE u.username = ?#{principal?.username}")
    @Modifying
    @Transactional
    void updateLastLogin(@Param("lastLogin") Date lastLogin);
}
```

如果没有Spring Data和Spring Security的集成，我们通常必须将username作为参数传递给updateLastLogin。

如果提供了错误的用户凭据，登录过程将失败，我们无需担心访问验证。

### 3.2 使用分页获取特定AppUser的内容

Spring Data和Spring Security完美协同工作的另一种情况是，我们需要从当前经过身份验证的用户拥有的数据库中检索内容。

例如，如果我们有一个推文应用程序，我们可能希望在他们的个性化提要页面上显示当前用户创建或喜欢的推文。

当然，这可能需要编写查询来与我们数据库中的一个或多个表进行交互。使用Spring Data和Spring Security，这非常简单：

```java
public interface TweetRepository extends PagingAndSortingRepository<Tweet, Long> {

    @Query("SELECT twt FROM Tweet twt JOIN twt.likes AS lk WHERE lk = ?#{principal?.username} OR twt.owner = ?#{principal?.username}")
    Page<Tweet> getMyTweetsAndTheOnesILiked(Pageable pageable);
}
```

由于我们需要用到分页的对象Pageable，所以我们的TweetRepository在上面的接口定义中继承了PagingAndSortingRepository。

## 4. 总结

Spring Data和Spring Security集成为管理Spring应用程序中的身份验证状态带来了很大的灵活性。

在本文中，我们了解了如何将Spring Security添加到Spring Data。