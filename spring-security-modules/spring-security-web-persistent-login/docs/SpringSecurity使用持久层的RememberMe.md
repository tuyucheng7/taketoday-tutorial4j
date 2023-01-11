## 1. 概述

本文演示如何在Spring Security中设置“Remember Me”功能，使用的不是标准的只使用cookie的方法，而是使用更安全的解决方案，使用持久存储。

Spring可以配置为记住浏览器会话之间的登录详细信息。
这允许你登录到一个网站，然后在你下次访问该网站时自动重新登录(即使你在此期间关闭了浏览器）。

## 2. 两种“Remember Me”解决方案

Spring提供了两种稍微不同的实现来解决这个问题。两者都使用UsernamePasswordAuthenticationFilter，使用钩子调用RememberMeServices实现。

在之前的文章中，我们已经介绍了仅使用cookie的标准Remember Me解决方案。
该解决方案使用了一个名为“remember-me”的cookie，其中包含用户名、过期时间和包含密码的MD5哈希。
由于它包含密码哈希，所以如果cookie被捕获，此解决方案可能会受到攻击。

考虑到这一点，让我们看看第二种方法 - 使用PersistentTokenBasedRememberMeServices在会话之间将持久登录信息存储在数据库表中。

## 3. 创建数据库表

首先，我们需要在数据库中创建一个表来保存用户登录数据：

```h2
create table if not exists persistent_logins
(
    username  varchar_ignorecase(100) not null,
    series    varchar(64) primary key,
    token     varchar(64)             not null,
    last_used timestamp               not null
);
```

这是通过以下XML配置在启动时自动创建的(使用内存中的H2数据库)：

```xml

<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xmlns:jdbc="http://www.springframework.org/schema/jdbc"
             xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.2.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd 
		http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-4.3.xsd">

    <!-- create H2 embedded database table on startup -->
    <jdbc:embedded-database id="dataSource" type="H2">
        <jdbc:script location="classpath:/persisted_logins_create_table.sql"/>
    </jdbc:embedded-database>
</beans:beans>
```

下面是持久层的配置：

```java

@Configuration
@EnableTransactionManagement
@PropertySource({"classpath:persistence-h2.properties"})
public class PersistenceConfig {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Preconditions.checkNotNull(environment.getProperty("jdbc.driverClassName")));
        dataSource.setUrl(Preconditions.checkNotNull(environment.getProperty("jdbc.url")));
        dataSource.setUsername(Preconditions.checkNotNull(environment.getProperty("jdbc.user")));
        dataSource.setPassword(Preconditions.checkNotNull(environment.getProperty("jdbc.pass")));
        return dataSource;
    }
}
```

以及对应的persistence-h2.properties属性文件：

```properties
jdbc.driverClassName=org.h2.Driver
jdbc.url=jdbc:h2:mem:test;MVCC=TRUE
jdbc.user=sa
jdbc.pass=
```

## 4. Spring Security配置

第一个关键配置是Remember-Me Http配置(注意dataSource属性)：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.2.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd">

    <http use-expressions="true">
        <remember-me data-source-ref="dataSource" token-validity-seconds="86400"/>
    </http>
</beans:beans>
```

接下来，我们需要配置实际的RememberMeService和JdbcTokenRepository(它也使用数据源)：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.2.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd">

    <!-- Persistent Remember Me Service -->
    <beans:bean id="rememberMeAuthenticationProvider"
                class="org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices">
        <beans:constructor-arg value="myAppKey"/>
        <beans:constructor-arg ref="jdbcTokenRepository"/>
        <beans:constructor-arg ref="myUserDetailsService"/>
    </beans:bean>

    <!-- Uses a database table to maintain a set of persistent login data -->
    <beans:bean id="jdbcTokenRepository"
                class="org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl">
        <beans:property name="createTableOnStartup" value="false"/>
        <beans:property name="dataSource" ref="dataSource"/>
    </beans:bean>

    <!-- Authentication Manager (uses same UserDetailsService as RememberMeService) -->
    <authentication-manager alias="authenticationManager">
        <authentication-provider user-service-ref="myUserDetailsService">
        </authentication-provider>
    </authentication-manager>
</beans:beans>
```

## 5. Cookie

正如我们所提到的，标准的TokenBasedRememberMeServices将用户的哈希密码存储在cookie中。

此解决方案 – PersistentTokenBasedRememberMeServices为用户使用唯一的序列标识符。
这标识了用户的初始登录，并在该持久会话期间每次用户自动登录时保持不变。
它还包含一个随机令牌，用户每次通过持久化的Remember Me功能登录时都会重新生成该令牌。

这种随机生成的序列和令牌的组合是持久的，因此不太可能发生暴力攻击。

## 6. 实践

为了看到“Remember Me”的实际生效，你可以：

1. 在“Remember Me“启用时登录
2. 关闭浏览器
3. 重新打开浏览器并返回到同一页面，刷新浏览器。
4. 你仍然处于登录状态

如果没有激活”Remember Me“，则在cookie过期后，用户应该被重定向回登录页面。
通过启用”Remember Me“，用户现在在新令牌/cookie的帮助下保持登录状态。

你还可以在浏览器中查看cookie，以及数据库中的持久数据(注意，你可能需要为此从嵌入式H2实现切换)。

## 7. 总结

本教程演示了如何设置和配置数据库持久化的”Remember Me“令牌功能。
数据库方法更安全，因为密码详细信息不会持久保存在cookie中，但它涉及更多的配置。