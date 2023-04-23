## 1. 概述

多租户指的是一种架构，其中软件应用程序的单个实例为多个租户或客户提供服务。 

它在租户之间实现所需程度的隔离，以便租户使用的数据和资源与其他数据和资源分开。

在本教程中，我们将了解如何使用 Spring Data JPA 在Spring Boot应用程序中配置多租户。[此外，我们使用JWT](https://www.baeldung.com/java-json-web-tokens-jjwt#JWT)为租户增加安全性。

## 2. 多租户模型

多租户系统主要有以下三种方法：

-   独立的数据库
-   共享数据库和独立模式
-   共享数据库和共享模式

### 2.1. 独立的数据库

在这种方法中，每个租户的数据都保存在一个单独的数据库实例中，并与其他租户隔离。这也称为每个租户的数据库：

[![独立的数据库](https://www.baeldung.com/wp-content/uploads/2022/08/database_per_tenant.png)](https://www.baeldung.com/wp-content/uploads/2022/08/database_per_tenant.png)

### 2.2. 共享数据库和独立模式

在这种方法中，每个租户的数据都保存在共享数据库上的不同模式中。这有时称为每个租户的模式：

[![单独的模式](https://www.baeldung.com/wp-content/uploads/2022/08/separate_schema.png)](https://www.baeldung.com/wp-content/uploads/2022/08/separate_schema.png)

### 2.3. 共享数据库和共享模式

在这种方法中，所有租户共享一个数据库，每个表都有一个包含租户标识符的列：

[![共享数据库](https://www.baeldung.com/wp-content/uploads/2022/08/shareddatabase.png)](https://www.baeldung.com/wp-content/uploads/2022/08/shareddatabase.png)

## 3.Maven依赖

让我们首先在pom.xml的Spring Boot应用程序中声明[spring-boot-starter-data-jpa](https://search.maven.org/search?q=a:spring-boot-starter-data-jpa)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

此外，我们将使用PostgreSQL数据库，因此我们还要将[postgresql](https://search.maven.org/search?q=g:org.postgresql %26 a:postgresql)依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

分离数据库和共享数据库以及分离模式方法在Spring Boot应用程序中的配置相似。在本教程中，我们重点介绍分离数据库方法。

## 4.动态数据源路由

在本节中，我们将描述Database per Tenant模型背后的一般思想。

### 4.1. 抽象路由数据源

使用 Spring Data JPA 实现多租户的总体思路是在运行时根据当前租户标识符路由数据源。

为此，我们可以使用[AbstractRoutingDatasource](https://www.baeldung.com/spring-abstract-routing-data-source)作为一种根据当前租户动态确定实际数据源的方法。

让我们创建一个扩展AbstractRoutingDataSource类的MultitenantDataSource 类：

```java
public class MultitenantDataSource extends AbstractRoutingDataSource {

    @Override
    protected String determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }
}
```

AbstractRoutingDataSource将getConnection调用路由到基于查找键的各种目标数据源之一。

查找键通常是通过一些线程绑定的事务上下文来确定的。因此，我们创建了一个TenantContext 类来存储每个请求中的当前租户：

```java
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }
}
```

我们使用[ThreadLocal](https://www.baeldung.com/java-threadlocal)对象来保存当前请求的租户 ID。此外，我们使用set方法存储租户 ID，并使用get()方法检索它。 

### 4.2. 为每个请求设置租户 ID

在这个配置设置之后，当我们执行任何租户操作时，我们需要在创建任何交易之前知道租户 ID。因此，我们需要在访问控制器端点之前在[过滤器 ](https://www.baeldung.com/spring-boot-add-filter)或[拦截器中设置租户 ID。](https://www.baeldung.com/spring-mvc-handlerinterceptor)

让我们添加一个TenantFilter 来设置TenantContext中的当前租户：

```java
@Component
@Order(1)
class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String tenantName = req.getHeader("X-TenantID");
        TenantContext.setCurrentTenant(tenantName);

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.setCurrentTenant("");
        }

    }
}
```

在此过滤器中，我们从请求标头X-TenantID 中获取租户 ID，并将其设置在TenantContext中。我们将控制传递给过滤器链。我们的 finally块确保在下一个请求之前重置当前租户。这避免了任何跨租户请求污染的风险。

在下一节中，我们将在 Database per Tenant模型中实现租户和数据源声明。

## 5.数据库方法

在本节中，我们将基于每个租户模型的数据库实施多租户。

### 5.1. 租户声明

在这种方法中，我们有多个数据库，因此我们需要在Spring Boot应用程序中声明多个数据源。

我们可以在单独的租户文件中配置数据源。因此，我们在allTenants目录下创建tenant_1.properties 文件并声明租户的数据源：

```plaintext
name=tenant_1
datasource.url=jdbc:postgresql://localhost:5432/tenant1
datasource.username=postgres
datasource.password=123456
datasource.driver-class-name=org.postgresql.Driver
```

此外，我们为另一个租户创建一个tenant_2.properties文件：

```plaintext
name=tenant_2
datasource.url=jdbc:postgresql://localhost:5432/tenant2
datasource.username=postgres
datasource.password=123456
datasource.driver-class-name=org.postgresql.Driver
```

我们最终将为每个租户创建一个文件：

[![所有租户](https://www.baeldung.com/wp-content/uploads/2022/08/tenants.png)](https://www.baeldung.com/wp-content/uploads/2022/08/tenants.png)

### 5.2. 数据源声明

现在我们需要读取租户的数据并使用DataSourceBuilder类创建DataSource。此外，我们在AbstractRoutingDataSource类中设置DataSources 。

让我们为此添加一个MultitenantConfiguration类：

```java
@Configuration
public class MultitenantConfiguration {

    @Value("${defaultTenant}")
    private String defaultTenant;

    @Bean
    @ConfigurationProperties(prefix = "tenants")
    public DataSource dataSource() {
        File[] files = Paths.get("allTenants").toFile().listFiles();
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        for (File propertyFile : files) {
            Properties tenantProperties = new Properties();
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

            try {
                tenantProperties.load(new FileInputStream(propertyFile));
                String tenantId = tenantProperties.getProperty("name");

                dataSourceBuilder.driverClassName(tenantProperties.getProperty("datasource.driver-class-name"));
                dataSourceBuilder.username(tenantProperties.getProperty("datasource.username"));
                dataSourceBuilder.password(tenantProperties.getProperty("datasource.password"));
                dataSourceBuilder.url(tenantProperties.getProperty("datasource.url"));
                resolvedDataSources.put(tenantId, dataSourceBuilder.build());
            } catch (IOException exp) {
                throw new RuntimeException("Problem in tenant datasource:" + exp);
            }
        }

        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        return dataSource;
    }

}
```

首先，我们从allTenants目录中读取租户的定义，并使用DataSourceBuilder 类创建DataSource bean 。之后，我们需要为MultitenantDataSource类设置默认数据源和目标源，以分别使用setDefaultTargetDataSource和setTargetDataSources进行连接。我们使用defaultTenant属性将其中一个租户名称设置为application.properties文件中的默认数据源。为了完成数据源的初始化，我们调用afterPropertiesSet()方法。

现在我们的设置已经准备就绪。 

## 6.测试

### 6.1. 为租户创建数据库

首先，我们需要在PostgreSQL 中定义两个数据库：

[![租户数据库](https://www.baeldung.com/wp-content/uploads/2022/08/tenants-db.png)](https://www.baeldung.com/wp-content/uploads/2022/08/tenants-db.png)

之后，我们使用以下脚本在每个数据库中创建一个员工表：

```plaintext
create table employee (id int8 generated by default as identity, name varchar(255), primary key (id));
```

### 6.2. 样品控制器

让我们创建一个EmployeeController 类，用于在请求标头中的指定租户中创建和保存Employee实体：

```java
@RestController
@Transactional
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping(path = "/employee")
    public ResponseEntity<?> createEmployee() {
        Employee newEmployee = new Employee();
        newEmployee.setName("Baeldung");
        employeeRepository.save(newEmployee);
        return ResponseEntity.ok(newEmployee);
    }
}

```

### 6.3. 索取样品

让我们使用[Postman](https://www.baeldung.com/postman-testing-collections)创建一个在租户 ID tenant_1中插入员工实体的发布请求：

[![租户 1](https://www.baeldung.com/wp-content/uploads/2022/08/tenant_1.png)](https://www.baeldung.com/wp-content/uploads/2022/08/tenant_1.png)

此外，我们向tenant_2发送请求：

[![租户2](https://www.baeldung.com/wp-content/uploads/2022/08/tenant2.png)](https://www.baeldung.com/wp-content/uploads/2022/08/tenant2.png)

之后，当我们检查数据库时，我们看到每个请求都已保存在相关租户的数据库中。

## 7. 安全

多租户应在共享环境中保护客户的数据。这意味着每个租户只能访问他们的数据。因此，我们需要为租户增加安全性。让我们构建一个系统，用户必须登录到应用程序并获得一个 JWT，然后用它来证明访问租约的权利。

### 7.1. Maven 依赖项

让我们首先在pom.xml中添加[spring-boot-starter-security](https://search.maven.org/search?q=a:spring-boot-starter-security)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

此外，我们需要生成并验证 JWT。为此，我们将[jjwt](https://search.maven.org/search?q=a:jjwt)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

### 7.2. 安全配置

首先，我们需要为租户的用户提供认证能力。为简单起见，让我们在SecurityConfiguration类中使用内存中的用户声明：

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
      .passwordEncoder(passwordEncoder())
      .withUser("user")
      .password(passwordEncoder().encode("baeldung"))
      .roles("tenant_1");

    auth.inMemoryAuthentication()
      .passwordEncoder(passwordEncoder())
      .withUser("admin")
      .password(passwordEncoder().encode("baeldung"))
      .roles("tenant_2");
}
```

我们为两个租户添加两个用户。此外，我们将租户视为一个角色。根据以上代码，用户名user和admin分别可以访问tenant_1和tenant_2。

现在，我们为用户身份验证创建一个过滤器。让我们添加LoginFilter类： 

```java
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    public LoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException, IOException, ServletException {

        AccountCredentials creds = new ObjectMapper().
          readValue(req.getInputStream(), AccountCredentials.class);

        return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(creds.getUsername(),
            creds.getPassword(), Collections.emptyList())
        );
    }
```

LoginFilter类扩展了AbstractAuthenticationProcessingFilter。AbstractAuthenticationProcessingFilter拦截请求并尝试使用 attemptAuthentication() 方法执行身份验证。在此方法中，我们将用户凭据映射到AccountCredentials [DTO](https://www.baeldung.com/java-dto-pattern)类，并根据内存中的身份验证管理器对用户进行身份验证：

```typescript
public class AccountCredentials {

    private String username;
    private String password;

   // getter and setter methods
}
```

### 7.3. 智威汤逊

现在我们需要生成 JWT 并添加租户 ID。为此，我们覆盖了 successfulAuthentication()方法。此方法在身份验证成功后执行：

```java
@Override
protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
  FilterChain chain, Authentication auth) throws IOException, ServletException {

    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
    String tenant = "";
    for (GrantedAuthority gauth : authorities) {
        tenant = gauth.getAuthority();
    }

    AuthenticationService.addToken(res, auth.getName(), tenant.substring(5));
}
```

根据上面的代码，我们获取到用户的角色，并添加JWT。为此，我们创建AuthenticationService类和addToken() 方法： 

```java
public class AuthenticationService {

    private static final long EXPIRATIONTIME = 864_000_00; // 1 day in milliseconds
    private static final String SIGNINGKEY = "SecretKey";
    private static final String PREFIX = "Bearer";

    public static void addToken(HttpServletResponse res, String username, String tenant) {
        String JwtToken = Jwts.builder().setSubject(username)
          .setAudience(tenant)
          .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
          .signWith(SignatureAlgorithm.HS512, SIGNINGKEY)
          .compact();
        res.addHeader("Authorization", PREFIX + " " + JwtToken);
    }
```

addToken方法生成了包含租户 ID 作为受众声明的 JWT，并将其添加到响应中的Authorization标头。

最后，我们在SecurityConfiguration类中添加LoginFilter ：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
      .antMatchers("/login").permitAll()
      .anyRequest().authenticated()
      .and()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .addFilterBefore(new LoginFilter("/login", authenticationManager()),
        UsernamePasswordAuthenticationFilter.class)
      .addFilterBefore(new AuthenticationFilter(),
        UsernamePasswordAuthenticationFilter.class)
      .csrf().disable()
      .headers().frameOptions().disable()
      .and()
      .httpBasic();
}
```

此外，我们添加AuthenticationFilter 类用于在SecurityContextHolder类中设置身份验证：

```java
public class AuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

        Authentication authentication = AuthenticationService.getAuthentication((HttpServletRequest) req);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }
}
```

### 7.4. 从 JWT 获取租户 ID

让我们修改TenantFilter以在TenantContext中设置当前租户：

```java
String tenant = AuthenticationService.getTenant((HttpServletRequest) req);
TenantContext.setCurrentTenant(tenant);
```

在这种情况下，我们使用AuthenticationService类的getTenant()方法从JWT 获取租户 ID ：

```java
public static String getTenant(HttpServletRequest req) {
    String token = req.getHeader("Authorization");
    if (token == null) {
        return null;
    }
    String tenant = Jwts.parser()
      .setSigningKey(SIGNINGKEY)
      .parseClaimsJws(token.replace(PREFIX, ""))
      .getBody()
      .getAudience();
    return tenant;
}
```

## 8. 安全测试

### 8.1. JWT一代 

让我们为用户名user生成 JWT 。为此，我们将凭证发布到/login端点：

[![jwt](https://www.baeldung.com/wp-content/uploads/2022/08/jwt.png)](https://www.baeldung.com/wp-content/uploads/2022/08/jwt.png)

让我们检查令牌：

```plaintext
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiYXVkIjoidGVuYW50XzEiLCJleHAiOjE2NTk2MDk1Njd9.
```

当我们[解码 token](https://jwt.io/)时，我们发现租户 ID 集为观众声称的：

```json
{
    alg: "HS512"
}.
{
    sub: "user",
    aud: "tenant_1",
    exp: 1659609567
}.
```

### 8.2. 索取样品

让我们使用生成的令牌创建一个用于插入员工实体的发布请求：

[![示例令牌](https://www.baeldung.com/wp-content/uploads/2022/08/tenant_1_jwt.png)](https://www.baeldung.com/wp-content/uploads/2022/08/tenant_1_jwt.png)

我们在Authorization标头中设置生成的令牌。租户 ID 已从令牌中提取并设置在TenantContext中。

## 9.总结

在本文中，我们研究了不同的多租户模型。

我们描述了使用 Spring Data JPA 在Spring Boot应用程序中添加多租户所需的类，用于单独的数据库和共享数据库以及单独的模式模型。

然后，我们在 PostgreSQL 数据库中设置测试多租户所需的环境。

最后，我们使用 JWT 为租户增加了安全性。