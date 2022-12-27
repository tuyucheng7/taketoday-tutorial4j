## 1. 概述

在本教程中，我们将了解**使用Spring Boot中的框架支持编写测试**，我们将介绍可以单独运行的单元测试以及在执行测试之前启动Spring上下文的集成测试。

## 2. 项目设置

我们将在本文中使用的应用程序是一个API，它提供对员工资源的一些基本操作。这是一个典型的分层架构，API调用从Controller到Service再到持久层进行处理。

## 3. maven依赖

首先我们添加测试依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
    <scope>test</scope>
</dependency>
```

spring-boot-starter-test是包含测试所需的大多数元素的主要依赖项。H2是我们的内存数据库，它消除了出于测试目的而独立配置和启动真实数据库的需要。

### 3.1 JUnit 4

从Spring Boot 2.4开始，JUnit 5的老式引擎已从spring-boot-starter-test中删除，如果我们仍然想使用JUnit 4编写测试，我们需要添加以下Maven依赖项：

```xml
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 4. @SpringBootTest集成测试

顾名思义，集成测试侧重于集成应用程序的不同层，这也意味着不涉及mock。

**理想情况下，我们应该将集成测试与单元测试分开，并且不应该与单元测试一起运行**。为此，我们可以通过使用不同的Profile只运行集成测试来做到这一点。这样做的几个原因可能是集成测试非常耗时，并且可能需要一个实际的数据库才能执行。

但是在本文中，我们不会使用一个真实的数据库实例，而是使用内存数据库H2。

集成测试需要启动一个Spring容器来执行测试用例，因此需要一些额外的设置-所有这些在Spring Boot中都很容易实现：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@AutoConfigureTestDatabase
class EmployeeRestControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmployeeRepository repository;

    // write test cases here
}
```

**当我们需要启动整个容器时，@SpringBootTest注解很有用**，该注解会创建将在我们的测试中使用的ApplicationContext。

我们可以使用@SpringBootTest的webEnvironment属性来配置我们的运行环境；在这里我们使用WebEnvironment.MOCK以便容器将在mock servlet环境中运行。

接下来，@TestPropertySource注解有助于配置特定于我们测试的属性文件的位置，请注意，使用@TestPropertySource加载的属性文件将覆盖现有的application.properties文件。

application-integrationtest.properties包含配置H2数据库的详细信息：

```properties
spring.datasource.url=jdbc:h2:mem:test
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.show-sql=true
```

如果我们想针对MySQL运行集成测试，我们可以在属性文件中更改上述值。

集成测试的测试用例可能类似于控制器层单元测试：

```java
class EmployeeRestControllerIntegrationTest {

    @Test
    void givenEmployees_whenGetEmployees_thenStatus200() throws Exception {
        createTestEmployee("bob");
        createTestEmployee("alex");

        mvc.perform(get("/api/employees").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].name", is("bob")))
                .andExpect(jsonPath("$[1].name", is("alex")));
    }

    private void createTestEmployee(String name) {
        Employee employee = new Employee(name);
        repository.saveAndFlush(employee);
    }
}
```

与控制器层单元测试的不同之处在于，这里没有任何对象被mock，并且将执行端到端的场景。

## 5. @TestConfiguration测试配置

正如我们在上一节中看到的，使用@SpringBootTest注解的测试将启动完整的应用程序上下文，这意味着我们可以使用@Autowire注入任何通过组件扫描获取的bean到我们的测试类中：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeeServiceImplIntegrationTest {
    
    @Autowired
    private EmployeeService employeeService;

    // class code ...
}
```

但是，我们可能希望避免启动真实的应用程序上下文，而是使用特殊的测试配置，我们可以通过@TestConfiguration注解来实现这一点。该注解有两种使用方法，第一种是在我们想要使用@Autowire注入bean的同一测试类中的静态内部类上：

```java
@ExtendWith(SpringExtension.class)
public class EmployeeServiceImplIntegrationTest {
    
    @Autowired
    private EmployeeService employeeService;

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
        @Bean
        public EmployeeService employeeService() {
            return new EmployeeService() {
                // implement methods
            };
        }
    }
}
```

或者，我们可以创建一个单独的测试配置类：

```java
@TestConfiguration
public class EmployeeServiceImplTestContextConfiguration {

    @Bean
    public EmployeeService employeeService() {
        return new EmployeeService() {
            // implement methods
        };
    }
}
```

使用@TestConfiguration注解的配置类被排除在组件扫描之外，因此我们需要在每个我们想要使用@Autowire的测试中显式地导入它，我们可以使用@Import注解来做到这一点：

```java
@ExtendWith(SpringExtension.class)
@Import(EmployeeServiceImplTestContextConfiguration.class)
public class EmployeeServiceImplIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    // remaining class code
}
```

## 6. 使用@MockBean进行mock

我们的Service层代码依赖于我们的Repository：

```java
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee getEmployeeByName(String name) {
        return employeeRepository.findByName(name);
    }
}
```

但是，要测试Service层，我们不需要知道或关心持久层是如何实现的，理想情况下，我们应该能够编写和测试我们的Service层代码，而无需在完整的持久层中进行连接。

为了实现这一点，**我们可以使用Spring Boot Test提供的mock支持**。

我们先看一下测试类的基本结构：

```java
@ExtendWith(SpringExtension.class)
public class EmployeeServiceImplIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {

        @Bean
        public EmployeeService employeeService() {
            return new EmployeeServiceImpl();
        }
    }

    // write test cases here
}
```

要测试Service类，我们需要创建一个Service类的实例并将其作为@Bean使用，以便我们可以在测试类中使用@Autowire注入它，我们可以在使用@TestConfiguration注解的配置类来实现这个配置。

这里另一个有趣的地方是@MockBean的使用，它为EmployeeRepository创建了一个mock，可用于绕过对实际EmployeeRepository的调用：

```java
@Before
public void setUp() {
    Employee alex = new Employee("alex");
    Mockito.when(employeeRepository.findByName(alex.getName())).thenReturn(alex);
}
```

配置到这里已经完成，测试用例会更简单：

```java
@Test
public void whenValidName_thenEmployeeShouldBeFound() {
    String name = "alex";
    Employee found = employeeService.getEmployeeByName(name);
    
    assertThat(found.getName()).isEqualTo(name);
}
```

## 7. 使用@DataJpaTest进行集成测试

我们使用一个名为Employee的实体，该实体具有一个id和一个name作为它的属性：

```java
@Entity
@Table(name = "person")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(min = 3, max = 20)
    private String name;
    private Date birthday;
    // standard getters and setters, constructors
}
```

下面是相应的Repository接口：

```java
@Repository
@Transactional
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByName(String name);
}
```

这就是持久层的代码，现在让我们开始编写我们的测试类。首先，我们编写测试类的基本骨架：

```java
@ExtendWith(SpringExtension.class)
@DataJpaTest
class EmployeeRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    // write test cases here
}
```

@ExtendWith(SpringExtension.class)提供了Spring Boot测试功能和JUnit之间的桥梁，每当我们在JUnit测试中使用任何Spring Boot测试功能时，都需要此注解。

@DataJpaTest提供了测试持久层所需的一些标准设置：

+ 配置内存数据库H2
+ 设置Hibernate、SpringData和DataSource
+ 执行@EntityScan
+ 启用SQL日志记录

要执行数据库操作，我们需要数据库中已有的一些数据，要设置这些数据，我们可以使用TestEntityManager。

**Spring Boot TestEntityManager是标准JPA EntityManager的替代方案，它提供了编写测试时常用的方法**。

EmployeeRepository是我们将要测试的组件，现在让我们编写我们的第一个测试用例：

```java
@Test
void whenFindByName_thenReturnEmployee() {
    // given
    Employee alex = new Employee("alex");
    entityManager.persist(alex);
    entityManager.flush();

    // when
    Employee found = employeeRepository.findByName(alex.getName());

    // then
    assertThat(found.getName()).isEqualTo(alex.getName());
}
```

在上面的测试中，我们使用TestEntityManager在数据库中插入一个Employee并通过findByName()获取它。

assertThat(...)部分来自Assertj库，它与Spring Boot捆绑在一起。

## 8. 使用@WebMvcTest进行单元测试

我们的Controller依赖于Service层；为简单起见，我们只包含一个方法：

```java
@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}
```

由于我们只关注Controller代码，因此很自然地为我们的单元测试mock Service层对象：

```java
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = EmployeeRestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class EmployeeControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService service;

    // write test cases here
}
```

**要测试Controller，我们可以使用@WebMvcTest，它将为我们的单元测试自动配置Spring MVC基础环境**。

在大多数情况下，@WebMvcTest被限制为启动单个控制器，我们还可以将它与@MockBean一起使用，为任何所需的依赖项提供mock实现。

@WebMvcTest还自动配置MockMvc，它提供了一种强大的方法来轻松测试MVC控制器，而无需启动完整的HTTP服务器。

话虽如此，让我们编写我们的测试用例：

```java
@Test 
void givenEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {
    Employee alex = new Employee("alex");
    Employee john = new Employee("john");
    Employee bob = new Employee("bob");

    List<Employee> allEmployees = Arrays.asList(alex, john, bob);

    given(service.getAllEmployees()).willReturn(allEmployees);

    mvc.perform(get("/api/employees").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].name", is(alex.getName())))
            .andExpect(jsonPath("$[1].name", is(john.getName())))
            .andExpect(jsonPath("$[2].name", is(bob.getName())));
    verify(service, VerificationModeFactory.times(1)).getAllEmployees();
    reset(service);
}
```

get(...)方法调用可以替换为其他HTTP请求对应的方法，如put()、post()等。请注意，我们在请求中设置了contentType。

MockMvc非常灵活，有完整的请求调用以及响应断言API，我们可以使用它创建任何请求并验证响应。

## 9. 自动配置测试

Spring Boot自动配置注解的一个惊人功能是，它有助于加载代码库的完整应用程序和特定于测试的层的部分。

除了上面提到的注解之外，这里列出了一些广泛使用的注解：

+ @WebFluxTest：我们可以使用@WebFluxTest注解来测试Spring WebFlux控制器，它通常与@MockBean一起使用，为所需的依赖bean提供mock实现。
+ @JdbcTest：我们可以使用@JdbcTest注解来测试JPA应用程序，但它只适用于只需要DataSource的测试，该注解配置了一个内存中嵌入式数据库和一个JdbcTemplate。
+ @JooqTest：要测试jOOQ相关的测试，我们可以使用@JooqTest注解，它配置了一个DSLContext。
+ @DataMongoTest：为了测试MongoDB应用程序，我们可以使用@DataMongoTest。默认情况下，如果驱动程序通过依赖项可用，它会配置内存中嵌入式MongoDB，配置MongoTemplate，扫描@Document类，并配置Spring Data MongoDB Repository。
+ @DataRedisTest：使测试Redis应用程序变得更加容易，默认情况下，它会扫描@RedisHash类并配置Spring Data Redis Repository。
+ @DataLdapTest：配置内存中的嵌入式LDAP(如果可用)，配置LdapTemplate，扫描@Entry类，并默认配置Spring Data LDAP Repository。
+ @RestClientTest：我们通常使用@RestClientTest注解来测试REST客户端，它自动配置不同的依赖项，例如Jackson、GSON和Jsonb支持；配置一个RestTemplateBuilder；并默认添加对MockRestServiceServer的支持。
+ @JsonTest：仅使用测试JSON序列化所需的那些bean初始化Spring应用程序上下文。

你可以在我们的文章[优化Spring集成测试](https://www.baeldung.com/spring-tests)中阅读有关这些注解以及如何进一步优化集成测试的更多信息。

## 10. 总结

在本文中，我们深入探讨了Spring Boot中的测试支持，并演示了如何高效地编写单元测试。

如果你想继续学习测试，我们有与[集成测试](https://www.baeldung.com/integration-testing-in-spring)、[优化Spring集成测试](https://www.baeldung.com/spring-tests)和[JUnit 5中的单元测试](https://www.baeldung.com/junit-5)相关的单独文章。