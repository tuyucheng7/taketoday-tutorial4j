## 1. 概述

大多数时候，在保护Spring Web应用程序或REST API时，Spring Security提供的工具已经绰绰有余，但有时我们需要更具体的行为。

在本教程中，我们将编写一个自定义的AccessDecisionVoter，并展示如何使用它来抽象出Web应用程序的授权逻辑，并将其与应用程序的业务逻辑分离。

## 2. 场景

为了演示AccessDecisionVoter的工作原理，我们将实现一个具有两种用户类型(USER和ADMIN)的场景，其中USER只能在偶数分钟访问系统，而ADMIN将始终被授予访问权限。

## 3. AccessDecisionVoter实现

首先，我们将描述Spring提供的一些实现，这些实现将与我们的自定义投票器一起参与对授权做出最终决定。我们将了解如何实现自定义投票器。

### 3.1 默认的AccessDecisionVoter实现

Spring Security提供了几个AccessDecisionVoter实现。我们将在这里使用其中的一些作为我们的安全解决方案的一部分。

让我们来看看这些默认投票者实现如何以及何时投票。

AuthenticatedVoter将根据Authentication对象的身份验证级别进行投票-特别是寻找完全经过身份验证的主体、通过remember-me进行身份验证的主体或匿名的主体。

如果任何配置属性以字符串"ROLE_"开头，则RoleVoter将参与投票。如果是这样，它将在Authentication对象的GrantedAuthority列表中搜索角色。

WebExpressionVoter使我们能够使用SpEL(Spring表达式语言)通过@PreAuthorize注解来授权请求。

例如，如果我们使用Java配置：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // ...
    .antMatchers("/").hasAnyAuthority("ROLE_USER")
    // ...
}
```

或者使用XML配置-我们可以在http标签中的intercept-url标签内使用SpEL：

```xml
<http use-expressions="true">
    <intercept-url pattern="/" access="hasAuthority('ROLE_USER')"/>
    <!--...-->
</http>
```

### 3.2 自定义AccessDecisionVoter实现

现在让我们创建一个自定义投票器-通过实现AccessDecisionVoter接口：

```java
public class MinuteBasedVoter implements AccessDecisionVoter {
    // ...
}
```

我们必须实现的三个方法中的第一个是vote方法。vote方法是自定义投票器中最重要的部分，也是我们授权逻辑所在的地方。

vote方法可以返回三个可能的值：

+ ACCESS_GRANTED：投票者给出肯定答案
+ ACCESS_DENIED：投票者给出否定答案
+ ACCESS_ABSTAIN：投票者弃权

现在让我们实现vote方法：

```java
@Override
public int vote(Authentication authentication, Object object, Collection collection) {
    return authentication.getAuthorities()
          .stream()
          .map(GrantedAuthority::getAuthority)
          .filter(r -> "ROLE_USER".equals(r) && LocalDateTime.now().getMinute() % 2 != 0)
          .findAny()
          .map(s -> ACCESS_DENIED)
          .orElse(ACCESS_ABSTAIN);
}
```

在我们的vote方法中，我们检查请求是否来自USER。如果是，并且当前为偶数分钟，我们返回ACCESS_GRANTED，否则，我们返回ACCESS_DENIED。如果请求不是来自USER，我们将弃权并返回ACCESS_ABSTAIN。

第二个方法返回投票器是否支持特定的配置属性。在我们的示例中，投票器不需要任何自定义配置属性，因此我们返回true：

```java
@Override
public boolean supports(ConfigAttribute attribute) {
    return true;
}
```

第三个方法返回投票器是否可以投票给受保护的对象类型。由于我们的投票器不关心安全对象类型，因此我们返回true：

```java
@Override
public boolean supports(Class clazz) {
    return true;
}
```

## 4. AccessDecisionManager

最终授权决策由AccessDecisionManager处理。

AbstractAccessDecisionManager包含一个AccessDecisionVoters列表-它们负责彼此独立地投票。

有三种实现用于处理投票以覆盖最常见的用例：

+ AffirmativeBased(基于肯定)：如果任何AccessDecisionVoters给出赞成票，则授予访问权限
+ ConsensusBased(基于共识)：如果赞成票多于反对票(忽略弃权的投票器)，则授予访问权限
+ UnanimousBased(基于一致性)：如果每个投票器都弃权或投赞成票，则授予访问权限

当然，你可以使用自定义决策逻辑实现自己的AccessDecisionManager。

## 5. 配置

在本教程的这一部分中，我们将了解使用AccessDecisionManager配置自定义AccessDecisionVoter的基于Java和基于XML的方法。

### 5.1 Java配置

让我们为Spring Web Security创建一个配置类：

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    // ...
}
```

我们定义一个AccessDecisionManager bean，它使用UnanimousBased管理器和我们自定义的投票器列表：

```java
@Bean
public AccessDecisionManager accessDecisionManager() {
    List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(
          new WebExpressionVoter(),
          new RoleVoter(),
          new AuthenticatedVoter(),
          new MinuteBasedVoter()
    );
    return new UnanimousBased(decisionVoters);
}
```

最后，让我们配置Spring Security以使用上面定义的bean作为默认的AccessDecisionManager：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf()
          .disable()
          .authorizeRequests()
          .anyRequest()
          .authenticated()
          .accessDecisionManager(accessDecisionManager())
          .and()
          .formLogin()
          .permitAll()
          .and()
          .logout()
          .permitAll()
          .deleteCookies("JSESSIONID")
          .logoutSuccessUrl("/login");
    return http.build();
}
```

### 5.2 XML配置

如果使用XML配置，则需要修改spring-security.xml文件(或任何包含安全设置的文件)。

首先，你需要修改<http\>标签：

```xml
<http access-decision-manager-ref="accessDecisionManager">
    <intercept-url pattern="/**" access="hasAnyRole('ROLE_ADMIN', 'ROLE_USER')"/>
    // ...
</http>
```

接下来，为自定义投票器创建一个bean：

```xml
<beans:bean id="minuteBasedVoter" class="cn.tuyucheng.taketoday.roles.voter.MinuteBasedVoter"/>
```

然后为AccessDecisionManager创建一个bean：

```xml
<beans:bean id="accessDecisionManager" class="org.springframework.security.access.vote.UnanimousBased">
    <beans:constructor-arg>
        <beans:list>
            <beans:bean class="org.springframework.security.web.access.expression.WebExpressionVoter"/>
            <beans:bean class="org.springframework.security.access.vote.AuthenticatedVoter"/>
            <beans:bean class="org.springframework.security.access.vote.RoleVoter"/>
            <beans:bean class="cn.tuyucheng.taketoday.roles.voter.MinuteBasedVoter"/>
        </beans:list>
    </beans:constructor-arg>
</beans:bean>
```

下面是一个支持我们场景的<authentication-manager\>配置：

```xml
<authentication-manager>
    <authentication-provider>
        <user-service>
            <user name="user" password="pass" authorities="ROLE_USER"/>
            <user name="admin" password="pass" authorities="ROLE_ADMIN"/>
        </user-service>
    </authentication-provider>
</authentication-manager>
```

如果你结合使用Java和XML配置，则可以将XML文件导入配置类：

```java
@Configuration
@ImportResource({"classpath:spring-security.xml"})
public class XmlSecurityConfig {
    public XmlSecurityConfig() {
        super();
    }
}
```

## 6. 总结

在本教程中，我们研究了一种使用AccessDecisionVoters自定义Spring Web应用程序安全性的方法。我们看到了Spring Security提供的一些投票器，然后我们讨论了如何实现自定义的AccessDecisionVoter。

然后我们讨论了AccessDecisionManager如何做出最终的授权决策，并展示了如何在所有投票器投票后使用Spring提供的实现来做出这个决定。

然后我们通过Java和XML使用AccessDecisionManager配置了一个AccessDecisionVoters列表。

当项目在本地运行时，可以在以下位置访问登录页面：

```text
http://localhost:8082/login
```

USER的凭据是"user"和"pass"，ADMIN的凭据是"admin"和"pass"。