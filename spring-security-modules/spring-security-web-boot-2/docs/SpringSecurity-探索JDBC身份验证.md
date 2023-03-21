## 1. 概述

在这个简短的教程中，我们将探讨Spring提供的使用现有[DataSource](https://www.baeldung.com/spring-boot-configure-data-source-programmatic)配置执行JDBC身份验证的功能。

在我们的[使用数据库支持的UserDetailsService进行身份验证](https://www.baeldung.com/spring-security-authentication-with-a-database)一文中，我们分析了一种实现此目的的方法，即我们自己实现UserDetailService接口。

这一次，我们将使用AuthenticationManagerBuilder#jdbcAuthentication方法来分析这种更简单方法的优缺点。

## 2. 使用嵌入式H2

首先，我们将分析如何使用嵌入式H2数据库实现身份验证。

这很容易实现，因为大多数Spring Boot的自动配置都是为这种情况准备的开箱即用的。

### 2.1 依赖和数据库配置

1. 包括相应的spring-boot-starter-data-jpa和h2依赖项
2. 使用application.properties配置数据库连接
3. 启用H2控制台

### 2.2 配置JDBC身份验证

**我们将使用[Spring Security的AuthenticationManagerBuilder](https://spring.io/guides/topicals/spring-security-architecture#_customizing_authentication_managers)来配置JDBC身份验证**：

```java
@Configuration
public class SecurityConfiguration {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, DataSource dataSource, PasswordEncoder passwordEncoder) throws Exception {
        auth.jdbcAuthentication()
              .dataSource(dataSource)
              .withDefaultSchema()
              .withUser(User.withUsername("user")
                    .password(passwordEncoder.encode("pass"))
                    .roles("USER"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

如我们所见，我们使用的是自动配置的DataSource。withDefaultSchema方法添加了一个数据库脚本，该脚本将填充默认数据库，允许存储用户和权限。

这个基本的user表记录在[Spring Security附录](https://docs.spring.io/spring-security/site/docs/current/reference/html/appendix.html#user-schema)中。

最后，我们以编程方式在数据库中创建一个角色为USER的用户。

### 2.3 验证配置

让我们创建一个非常简单的端点来检索经过身份验证的Principal信息：

```java
@RestController
@RequestMapping("/principal")
public class UserController {

    @GetMapping
    public Principal retrievePrincipal(Principal principal) {
        return principal;
    }
}
```

此外，我们将保护此端点，同时允许访问H2控制台：

```java
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
              .antMatchers("/h2-console/**")
              .permitAll()
              .anyRequest()
              .authenticated()
              .and()
              .formLogin()
              .permitAll();
        httpSecurity.csrf()
              .ignoringAntMatchers("/h2-console/**");
        httpSecurity.headers()
              .frameOptions()
              .sameOrigin();
        return httpSecurity.build();
    }
}
```

注意：这里我们重现了以前由Spring Boot实现的[安全配置](https://github.com/spring-projects/spring-boot/issues/10435)，但**在实际场景中，我们可能根本不会启用H2控制台**。

现在我们将运行应用程序并访问H2控制台。我们可以**验证Spring是否在我们的嵌入式数据库中创建了两个表：users和authorities**。

它们的结构对应于我们之前提到的Spring Security附录中定义的结构。

最后，让我们验证并请求/principal端点以查看相关信息，包括用户详细信息。

### 2.4 背后原理

在这篇文章的开头，我们提供了一个教程的链接，该教程解释了**我们如何自定义实现UserDetailsService接口的数据库支持的身份验证**；如果我们想了解幕后工作原理，强烈建议你查看该文章。

**在这种情况下，我们依赖于Spring Security提供的相同接口的实现-JdbcDaoImpl**。

如果我们观察这个类，我们可以看到它使用的UserDetails实现，以及从数据库中检索用户信息的机制。

这对于这个简单的场景非常有效，但是如果我们想要自定义数据库模式，或者即使我们想要使用不同的数据库供应商，它也有一些缺点。

让我们看看如果我们更改配置以使用不同的JDBC服务会发生什么。

## 3. 为不同的数据库调整模式

在本节中，我们将使用MySQL数据库为我们的项目配置身份验证。

正如我们接下来将看到的，为了实现这一点，我们需要避免使用默认模式(表)并提供我们自己的模式。

### 3.1 依赖和数据库配置

首先，让我们删除h2依赖项并将其替换为相应的MySQL库：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.27</version>
</dependency>
```

一如既往，我们可以在[Maven Central](https://central.sonatype.com/artifact/mysql/mysql-connector-java/8.0.32)中查找最新版本的库。

现在让我们相应地重新设置应用程序属性：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jdbc_authentication
spring.datasource.username=root
spring.datasource.password=pass
```

### 3.2 运行默认配置

当然，这些应该进行自定义以连接到正在运行的MySQL服务器。出于测试目的，这里我们将使用Docker启动一个新实例：

```shell
docker run -p 3306:3306
  --name mysql
  -e MYSQL_ROOT_PASSWORD=pass
  -e MYSQL_DATABASE=jdbc_authentication
  mysql:latest
```

现在让我们运行项目，看看默认配置是否适用于MySQL数据库。

实际上，由于SQLSyntaxErrorException，应用程序将无法启动。这是有道理的；正如我们所说，大多数默认自动配置都适用于HSQLDB。

在这种情况下，**withDefaultSchema方法提供的DDL脚本使用了不适合MySQL的方言**。

因此，我们需要避免使用此模式并提供我们自己的模式。

### 3.3 调整身份验证配置

由于我们不想使用默认模式，因此我们必须从AuthenticationManagerBuilder配置中删除相关的方法调用。

此外，由于我们将提供自己的SQL脚本，因此我们可以避免尝试以编程方式创建用户：

```java
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().dataSource(dataSource);
}
```

现在让我们看一下数据库初始化脚本。

首先，我们的schema.sql：

```sql
CREATE TABLE users (
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled  TINYINT      NOT NULL DEFAULT 1,
    PRIMARY KEY (username)
);

CREATE TABLE authorities (
    username  VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES users (username)
);

CREATE UNIQUE INDEX ix_auth_username on authorities (username, authority);
```

然后，我们的data.sql：

```sql
INSERT INTO users (username, password, enabled)
values ('user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 1);

INSERT INTO authorities (username, authority)
values ('user', 'ROLE_USER');
```

最后，我们应该修改一些其他的应用程序属性：

+ 由于现在我们不希望Hibernate创建模式，因此我们应该禁用ddl-auto属性
+ 默认情况下，Spring Boot只为嵌入式数据库初始化数据源，但此处的情况并非如此：

```properties
spring.sql.init.mode=always
spring.jpa.hibernate.ddl-auto=none
```

因此，我们现在应该能够正确启动应用程序，从端点验证和检索Principal数据。

另外，请注意spring.sql.init.mode属性是在Spring Boot 2.5.0中引入的；对于早期版本，我们需要使用spring.datasource.initialization-mode。

## 4. 为不同的模式调整查询

让我们更进一步。想象一下默认模式不适合我们的需求。

### 4.1 更改默认模式

例如，假设我们已经有一个结构与默认结构略有不同的数据库：

```sql
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS bael_users;

CREATE TABLE tuyucheng_users (
    name     VARCHAR(50)  NOT NULL,
    email    VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled  TINYINT      NOT NULL DEFAULT 1,
    PRIMARY KEY (email)
);

CREATE TABLE authorities (
    email     VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (email) REFERENCES tuyucheng_users (email)
);

CREATE UNIQUE INDEX ix_auth_email on authorities (email, authority);
```

最后，我们的data.sql脚本也需要适应这种变化：

```sql
INSERT INTO tuyucheng_users (name, email, password, enabled)
values ('user', 'user@email.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 1);

INSERT INTO authorities (email, authority)
values ('user@email.com', 'ROLE_USER');
```

### 4.2 使用新模式运行应用程序

让我们启动我们的应用程序。它正确初始化，这是有道理的，因为我们的模式是正确的。

现在，如果我们尝试登录，我们会发现在出示凭据时提示错误。

Spring Security仍在数据库中查找username字段。幸运的是，JDBC身份验证配置提供了**自定义用于在身份验证过程中检索用户详细信息的查询的可能性**。

### 4.3 自定义搜索查询

调整查询非常容易。我们只需要在配置AuthenticationManagerBuilder时提供我们自己的 SQL语句：

```java
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().dataSource(dataSource)
          .usersByUsernameQuery("select email,password,enabled "
                + "from tuyucheng_users "
                + "where email = ?")
          .authoritiesByUsernameQuery("select email,authority "
                + "from authorities "
                + "where email = ?");
}
```

我们可以再次启动应用程序，并使用新凭据访问/principal端点。

## 5. 总结

正如我们所看到的，这种方法比创建我们自己的UserDetailService实现要简单得多，这意味着一个艰巨的过程；创建实现UserDetail接口的实体和类，并将Repository添加到我们的项目中。

当然，**缺点是当我们的数据库或逻辑与Spring Security解决方案提供的默认策略不同时，它提供的灵活性很小**。