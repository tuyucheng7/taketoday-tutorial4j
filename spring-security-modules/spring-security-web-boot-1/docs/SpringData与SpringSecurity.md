## 1. 概述

Spring Security为与Spring Data的集成提供了良好的支持。前者处理我们应用程序的安全方面，而后者提供对包含应用程序数据的数据库的方便访问。

在本文中，我们将讨论如何**将Spring Security与Spring Data集成，以实现更多特定于用户的查询**。

## 2. Spring Security与Spring Data配置

在我们[对Spring Data JPA的介绍](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)中，我们了解了如何在Spring项目中设置Spring Data。像往常一样，要启用Spring Security和Spring Data，我们可以采用基于Java或XML的配置。

### 2.1 Java配置

回想一下[Spring Security登录表单](https://www.baeldung.com/spring-security-login)(第4和5节)，我们可以使用基于注解的配置将Spring Security添加到我们的项目中：

```java
@EnableWebSecurity
public class WebSecurityConfig {
    // Bean definitions
}
```

其他配置细节包括过滤器、bean和其他所需的安全规则的定义。

**要在Spring Security中启用Spring Data，我们只需将此bean添加到WebSecurityConfig**：

```java
@Bean
public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
    return new SecurityEvaluationContextExtension();
}
```

上面的定义支持自动解析类上注解的特定于Spring Data的表达式。

### 2.2 XML配置

基于XML的配置从包含Spring Security命名空间开始：

```xml
<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.springframework.org/schema/beans
  http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
  http://www.springframework.org/schema/security
  http://www.springframework.org/schema/security/spring-security.xsd">
    <!--...-->
</beans:beans>
```

就像在基于Java的配置中一样，对于基于XML的配置，我们也需要将**SecurityEvaluationContextExtension** bean添加到XML配置文件中：

```xml
<bean class="org.springframework.security.data.repository.query.SecurityEvaluationContextExtension"/>
```

定义SecurityEvaluationContextExtension使得Spring Security中的所有常用表达式都可以在Spring Data查询中使用。

这些常见的表达方式包括principal、authentication、isAnonymous()、hasRole(\[role\])、isAuthenticated等。

## 3. 使用示例

让我们考虑一些Spring Data和Spring Security的用例。

### 3.1 限制AppUser字段的更新

在此示例中，我们将AppUser的lastLogin字段更新限制为当前唯一经过身份验证的用户。

我们的意思是，无论何时触发updateLastLogin方法时，它只会更新当前经过身份验证的用户的lastLogin字段。

为此，我们将以下查询添加到我们的UserRepository接口中：

```java
@Query("UPDATE AppUser u SET u.lastLogin = :lastLogin WHERE u.username = ?#{principal?.username}")
@Modifying
@Transactional
void updateLastLogin(@Param("lastLogin") Date lastLogin);
```

如果没有Spring Data和Spring Security的集成，我们通常必须将username作为参数传递给updateLastLogin。

如果提供了错误的用户凭据，登录过程将失败，我们无需担心确保访问验证。

### 3.2 使用分页获取特定AppUser的内容

Spring Data和Spring Security完美配合的另一种情况是，我们需要从当前经过身份验证的用户拥有的数据库中检索内容。

例如，如果我们有一个推文应用程序，我们可能希望在他们的个性化提要页面上显示当前用户创建或喜欢的推文。

当然，这可能涉及编写查询以与我们数据库中的一个或多个表进行交互。使用Spring Data和Spring Security，这非常简单：

```java
public interface TweetRepository extends PagingAndSortingRepository<Tweet, Long> {

    @Query("SELECT twt FROM Tweet twt JOIN twt.likes AS lk WHERE lk = ?#{principal?.username} OR twt.owner = ?#{principal?.username}")
    Page<Tweet> getMyTweetsAndTheOnesILiked(Pageable pageable);
}
```

由于我们希望对结果进行分页，所以我们的TweetRepository在上面的接口定义中扩展了PagingAndSortingRepository。

## 4. 总结

Spring Data和Spring Security集成为管理Spring应用程序中的身份验证状态带来了很大的灵活性。

在本文中，我们了解了如何将Spring Security添加到Spring Data。