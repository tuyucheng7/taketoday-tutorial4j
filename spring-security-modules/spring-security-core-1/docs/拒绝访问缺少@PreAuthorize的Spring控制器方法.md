## 1. 概述

在我们的[Spring方法安全](Spring方法安全介绍.md)教程中，我们介绍了如何使用@PreAuthorize和@PostAuthorize注解。

在本教程中，我们将了解**如何拒绝对缺少授权注解的方法的访问**。

## 2. 默认安全

不管怎么样，我们可能会忘记保护我们的某个端点。不幸的是，没有简单的方法可以拒绝对非注解端点的访问。

幸运的是，默认情况下，Spring Security需要对所有端点进行身份验证。但是，它不需要特定的角色。此外，**当我们没有添加安全注解时，它不会拒绝访问**。

## 3. 项目构建

首先，让我们看一下该示例的应用程序。我们使用一个简单的Spring Boot应用程序：

```java
@SpringBootApplication
public class DenyAccessApplication {

    public static void main(String[] args) {
        SpringApplication.run(DenyAccessApplication.class, args);
    }
}
```

其次，我们有一个安全配置。我们设置两个用户并启用pre/post注解：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DenyMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
              User.withUsername("user").password("{noop}password").roles("USER").build(),
              User.withUsername("guest").password("{noop}password").roles().build()
        );
    }
}
```

最后，我们有一个带有两个方法的RestController。但是，我们假装忘记了保护/bye端点：

```java
@RestController
public class DenyOnMissingController {
    
    @GetMapping(path = "hello")
    @PreAuthorize("hasRole('USER')")
    public String hello() {
        return "Hello world!";
    }

    @GetMapping(path = "bye")
    // whoops!
    public String bye() {
        return "Bye bye world!";
    }
}
```

运行示例时，我们可以使用user/password登录。然后，我们访问/hello端点。我们也可以使用guest/password登录。在这种情况下，我们无法访问/hello端点，因为guest不具有"USER"角色。

但是，**任何经过身份验证的用户都可以访问/bye**。在下一节中，我们将编写一个测试来证明这一点。

## 4. 测试解决方案

使用[MockMvc](https://www.baeldung.com/integration-testing-in-spring#3-mocking-web-context-beans)我们可以编写一个测试检查不带安全注解的方法是否仍然可以访问：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DenyApplication.class)
class DenyOnMissingControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
              .build();
    }

    @Test
    @WithMockUser(username = "user")
    void givenANormalUser_whenCallingHello_thenAccessDenied() throws Exception {
        mockMvc.perform(get("/hello"))
              .andExpect(status().isOk())
              .andExpect(content().string("Hello world!"));
    }

    @Test
    @WithMockUser(username = "user")
    void givenANormalUser_whenCallingBye_thenAccessDenied() throws Exception {
        Assertions.assertThatThrownBy(() -> mockMvc.perform(get("/bye")))
              .hasCauseExactlyInstanceOf(AccessDeniedException.class);
    }
}
```

第二个测试失败，因为/bye端点是可访问的。在下一节中，**我们将更新配置以拒绝访问未带注解的方法API**。

## 5. 解决方案：默认拒绝

让我们扩展MethodSecurityConfig类并设置MethodSecurityMetadataSource：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DenyMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new CustomPermissionAllowedMethodSecurityMetadataSource();
    }
}
```

现在让我们实现MethodSecurityMetadataSource接口：

```java
public class CustomPermissionAllowedMethodSecurityMetadataSource extends AbstractFallbackMethodSecurityMetadataSource {

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return null;
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        Annotation[] annotations = AnnotationUtils.getAnnotations(method);
        List<ConfigAttribute> attributes = new ArrayList<>();

        // if the class is annotated as @Controller we should by default deny access to every method
        if (AnnotationUtils.findAnnotation(targetClass, Controller.class) != null) {
            attributes.add(DENY_ALL_ATTRIBUTE);
        }

        if (annotations != null) {
            for (Annotation a : annotations) {
                // but not if the method has at least a PreAuthorize or PostAuthorize annotation
                if (a instanceof PreAuthorize || a instanceof PostAuthorize) {
                    return null;
                }
            }
        }
        return attributes;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }
}
```

**我们将DENY_ALL_ATTRIBUTE添加到@Controller类的所有方法中**。

但是，如果方法上存在@PreAuthorize/@PostAuthorize注解，我们不会添加它们。我们通过返回null，这表示[不应用元数据](https://docs.spring.io/spring-security/site/docs/5.1.x/api/org/springframework/security/access/method/AbstractFallbackMethodSecurityMetadataSource.html#findAttributes-java.lang.reflect.Method-java.lang.Class-)。

使用更新的代码，我们的/bye端点受到保护并且测试应该成功。

## 6. 总结

在这个简短的教程中，我们展示了**如何保护缺少@PreAuthorize/@PostAuthorize注解的端点**。