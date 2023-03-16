## 1. 简介

在本教程中，我们将了解如何使用Parameterized JUnit测试运行器在JUnit 4中实现参数化[Spring集成测试](https://www.baeldung.com/spring-tests)。

## 2. SpringJUnit4ClassRunner

SpringJUnit4ClassRunner是JUnit 4的ClassRunner的实现，它**将Spring的TestContextManager嵌入到JUnit测试中**。

TestContextManager是Spring TestContext框架的入口点，因此在JUnit测试类中管理对Spring ApplicationContext和依赖注入的访问。因此，SpringJUnit4ClassRunner使开发人员能够为Spring组件(如Controller和Repository)实现集成测试。

例如，我们可以为我们的RestController实现一个集成测试：

```java
@Controller
public class EmployeeRoleController {

    private static final Map<String, Role> userRoleCache = new HashMap<>();

    static {
        userRoleCache.put("John", Role.ADMIN);
        userRoleCache.put("Doe", Role.EMPLOYEE);
    }

    @ResponseBody
    @GetMapping(value = "/role/{name}", produces = "application/text;charset=UTF-8")
    public String getEmployeeRole(@PathVariable("name") String employeeName) {
        return userRoleCache.get(employeeName).toString();
    }

    private enum Role {
        ADMIN, EMPLOYEE
    }
}
```

```java
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class RoleControllerIntegrationTest {

    private static final String CONTENT_TYPE = "application/text;charset=ISO-8859-1";
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void givenEmployeeNameJohnWhenInvokeRoleThenReturnAdmin() throws Exception {
        this.mockMvc.perform(get("/role/John"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().contentType(CONTENT_TYPE))
              .andExpect(content().string("ADMIN"));
    }
}
```

从测试中可以看出，我们的Controller接收name作为路径参数，并相应地返回用户角色。

现在，**为了使用不同的name/role组合测试这个REST服务，我们必须实现一个新的测试**：

```java
@Test
public void givenEmployeeNameDoeWhenInvokeRoleThenReturnEmployee() throws Exception {
    this.mockMvc.perform(get("/role/Doe"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(content().string("EMPLOYEE"));
}
```

**对于可能具有大量输入组合的服务，这显然是不可取的**。

为了避免在我们的测试类中出现这种重复，让我们看看如何使用Parameterized来实现接收多个输入的JUnit测试。

## 3. 使用Parameterized

### 3.1 定义参数

Parameterized是一个自定义的JUnit测试运行器，它允许我们编写单个测试用例并让它针对多个输入参数运行：

```java
@RunWith(Parameterized.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class RoleControllerParameterizedIntegrationTest {

    @Parameter(value = 0)
    public String name;

    @Parameter(value = 1)
    public String role;

    @Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList();
        params.add(new Object[]{"John", "ADMIN"});
        params.add(new Object[]{"Doe", "EMPLOYEE"});

        return params;
    }

    //...
}
```

如上所示，我们使用@Parameters注解来准备要注入到JUnit测试中的输入参数，并且在带有@Parameter注解的字段name和role中提供了这些值的映射。

但是现在，我们还有另一个问题需要解决-**JUnit不允许在一个JUnit测试类中指定多个运行器。这意味着我们不能再利用SpringJUnit4ClassRunner将TestContextManager嵌入到我们的测试类中**。我们必须找到另一种嵌入TestContextManager的方法。

幸运的是，Spring提供了几个选项来实现这一点。我们将在以下部分讨论这些内容。

### 3.2 手动初始化TestContextManager

第一个选项非常简单，Spring允许我们手动初始化TestContextManager：

```java
@RunWith(Parameterized.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class RoleControllerParameterizedIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private TestContextManager testContextManager;

    @Before
    public void setup() throws Exception {
        this.testContextManager = new TestContextManager(getClass());
        this.testContextManager.prepareTestInstance(this);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    //...
}
```

值得注意的是，在这个例子中，我们使用了Parameterized运行器而不是SpringJUnit4ClassRunner。接下来，我们在setup()方法中初始化了TestContextManager。

现在，我们可以实现我们的参数化JUnit测试：

```java
@Test
public void givenEmployeeNameWhenInvokeRoleThenReturnRole() throws Exception {
    this.mockMvc.perform(get("/role/" + name))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(content().string(role));
}
```

**JUnit将执行此测试用例两次**-针对我们使用@Parameters注解定义的每组输入执行一次。

### 3.3 SpringClassRule和SpringMethodRule

通常**不建议手动初始化TestContextManager**。相反，Spring推崇使用SpringClassRule和SpringMethodRule。

SpringClassRule实现了JUnit的TestRule，这是另一种编写测试用例的方法。TestRule可用于替换以前使用@Before、@BeforeClass、@After和@AfterClass方法完成的设置和清理操作。

SpringClassRule将TestContextManager的类级功能嵌入到JUnit测试类中。它初始化TestContextManager并调用Spring TestContext的setup和cleanup。因此，它提供了依赖注入和对ApplicationContext的访问。

除了SpringClassRule之外，我们还必须使用SpringMethodRule。它为TestContextManager提供实例级和方法级功能。

SpringMethodRule负责测试方法的准备。它还检查标记为跳过的测试用例，并阻止它们运行。

让我们看看如何在测试类中使用这种方法：

```java
@RunWith(Parameterized.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class RoleControllerParameterizedClassRuleIntegrationTest {
    private static final String CONTENT_TYPE = "application/text;charset=ISO-8859-1";

    @ClassRule
    public static final SpringClassRule scr = new SpringClassRule();
    @Rule
    public final SpringMethodRule smr = new SpringMethodRule();

    @Parameter(value = 0)
    public String name;

    @Parameter(value = 1)
    public String role;

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList<>();
        params.add(new Object[]{"John", "ADMIN"});
        params.add(new Object[]{"Doe", "EMPLOYEE"});
        return params;
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void givenEmployeeNameWhenInvokeRoleThenReturnRole() throws Exception {
        this.mockMvc.perform(get("/role/" + name))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().contentType(CONTENT_TYPE))
              .andExpect(content().string(role));
    }
}
```

## 4. 总结

在本文中，我们讨论了使用Parameterized测试运行器而不是SpringJUnit4ClassRunner来实现Spring集成测试的两种方法。我们看到了如何手动初始化TestContextManager，以及如何使用SpringClassRule和SpringMethodRule的示例，这是Spring推荐的方法。

虽然我们在本文中只讨论了Parameterized运行器，但实际上我们可以将这些方法中的任何一种与任何JUnit运行器一起使用来编写Spring集成测试。