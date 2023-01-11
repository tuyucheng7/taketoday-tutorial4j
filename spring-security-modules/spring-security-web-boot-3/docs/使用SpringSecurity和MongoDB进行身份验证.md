## 1. 概述

Spring Security提供不同的身份验证系统，例如通过数据库和UserDetailService。

除了使用JPA持久层，我们还可以使用MongoDB Repository。
在本教程中，我们将了解如何使用Spring Security和MongoDB对用户进行身份验证。

## 2. 使用MongoDB进行Spring Security认证

与使用JPA Repository类似，我们可以使用MongoDB Repository。但是，我们需要设置不同的配置才能使用它。

### 2.1 Maven依赖

对于本教程，我们使用嵌入式MongoDB。但是，MongoDB实例和Testcontainer可能是生产环境的最佳选择。
首先，让我们添加spring-boot-starter-data-mongodb和de.flapdoodle.embed.mongo依赖项：

```text
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
</dependency>
```

### 2.2 配置

添加了依赖后，我们就可以创建配置类：

```java

@Configuration
public class MongoConfig {
    private static final String CONNECTION_STRING = "mongodb://%s:%d";
    private static final String HOST = "localhost";

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {

        int randomPort = SocketUtils.findAvailableTcpPort();

        ImmutableMongodConfig mongoDbConfig = MongodConfig.builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(HOST, randomPort, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodExecutable mongodExecutable = starter.prepare(mongoDbConfig);
        mongodExecutable.start();
        return new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, HOST, randomPort)), "mongo_auth");
    }
}
```

我们还需要配置我们的AuthenticationManager：

```java

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(@Autowired AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .and()
                .httpBasic()
                .and()
                .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
```

### 2.3 User实体和Repository

首先，让我们定义一个简单的User，其中包含用于身份验证的Role。我们让它实现UserDetails接口以重用Principal对象的公共方法：

```java

@Document
public class User implements UserDetails {
    private @MongoId ObjectId id;
    private String username;
    private String password;
    private Set<UserRole> userRoles;
    // getters and setters
}
```

现在让我们定义一个简单的Repository：

```java
public interface UserRepository extends MongoRepository<User, String> {

    @Query("{username:'?0'}")
    User findUserByUsername(String username);
}
```

### 2.4 Authentication Service

最后，**让我们实现我们的UserDetailService以检索用户并检查它是否已通过身份验证**：

```java

@Service
public class MongoAuthUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public MongoAuthUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        cn.tuyucheng.taketoday.mongoauth.domain.User user = userRepository.findUserByUsername(userName);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        user.getAuthorities().forEach(role ->
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole().getName())));

        return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
```

### 2.5 测试

为了测试我们的应用程序，让我们定义一个简单的控制器。例如，我们定义了两个不同的角色来测试特定端点的身份验证和授权：

```java

@RestController
public class ResourceController {

    @RolesAllowed("ROLE_ADMIN")
    @GetMapping("/admin")
    public String admin() {
        return "Hello Admin!";
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/user")
    public String user() {
        return "Hello User!";
    }
}
```

这里使用Spring Boot Tests编写测试，以检查我们的身份验证是否有效。
正如我们所看到的，我们期望为提供无效凭据或系统中不存在的用户返回401状态码：

```java

@SpringBootTest(classes = {MongoAuthApplication.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MongoAuthApplicationIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private MockMvc mvc;

    private static final String USER_NAME = "user@gmail.com";
    private static final String ADMIN_NAME = "admin@gmail.com";
    private static final String PASSWORD = "password";

    @BeforeEach
    void setup() {
        setUp();
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private void setUp() {
        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");
        mongoTemplate.save(roleUser);

        User user = new User();
        user.setUsername(USER_NAME);
        user.setPassword(bCryptPasswordEncoder.encode(PASSWORD));

        UserRole userRole = new UserRole();
        userRole.setRole(roleUser);
        user.setUserRoles(new HashSet<>(Collections.singletonList(userRole)));
        mongoTemplate.save(user);

        User admin = new User();
        admin.setUsername(ADMIN_NAME);
        admin.setPassword(bCryptPasswordEncoder.encode(PASSWORD));

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        mongoTemplate.save(roleAdmin);

        UserRole adminRole = new UserRole();
        adminRole.setRole(roleAdmin);
        admin.setUserRoles(new HashSet<>(Collections.singletonList(adminRole)));
        mongoTemplate.save(admin);
    }

    @Test
    void givenUserCredentials_whenInvokeUserAuthorizedEndPoint_thenReturn200() throws Exception {
        mvc.perform(get("/user").with(httpBasic(USER_NAME, PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void givenUserNotExists_whenInvokeEndPoint_thenReturn401() throws Exception {
        mvc.perform(get("/user").with(httpBasic("not_existing_user", "password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenUserExistsAndWrongPassword_whenInvokeEndPoint_thenReturn401() throws Exception {
        mvc.perform(get("/user").with(httpBasic(USER_NAME, "wrong_password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenUserCredentials_whenInvokeAdminAuthorizedEndPoint_thenReturn403() throws Exception {
        mvc.perform(get("/admin").with(httpBasic(USER_NAME, PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAdminCredentials_whenInvokeAdminAuthorizedEndPoint_thenReturn200() throws Exception {
        mvc.perform(get("/admin").with(httpBasic(ADMIN_NAME, PASSWORD)))
                .andExpect(status().isOk());

        mvc.perform(get("/user").with(httpBasic(ADMIN_NAME, PASSWORD)))
                .andExpect(status().isOk());
    }
}
```

## 3. 总结

在本文中，我们介绍了如何使用MongoDB与Spring Security进行身份验证。

我们了解了如何配置并实现我们的自定义UserDetailService。并了解了如何mock MVC上下文并测试身份验证和授权。