## 1. 简介

我们经常发现自己的应用程序执行某种网络请求。在测试此行为时，我们有几个Spring应用程序[选项](https://medium.com/@davidh_23/testing-http-applications-with-a-few-ok-libraries-3092790f426d)。

在这个快速教程中，我们将只看几种Mock仅通过RestTemplate执行的此类调用的方法。

我们将从使用流行的Mock库Mockito进行测试开始。然后我们将使用Spring Test，它为我们提供了一种机制来创建Mock服务器来定义服务器交互。

## 2. 使用Mockito

我们可以使用Mockito来MockRestTemplate。使用这种方法，测试我们的服务将与[涉及Mock的任何其他测试](https://www.baeldung.com/mockito-annotations)一样简单。

假设我们有一个简单的EmployeeService类，它通过HTTP获取员工详细信息：

```java
@Service
public class EmployeeService {

    @Autowired
    private RestTemplate restTemplate;

    public Employee getEmployee(String id) {
        ResponseEntity resp = restTemplate.getForEntity("http://localhost:8080/employee/" + id, Employee.class);

        return resp.getStatusCode() == HttpStatus.OK ? resp.getBody() : null;
    }
}
```

现在让我们对前面的代码执行我们的测试：

```java
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService empService = new EmployeeService();

    @Test
    public void givenMockingIsDoneByMockito_whenGetIsCalled_shouldReturnMockedObject() {
        Employee emp = new Employee("E001", "Eric Simmons");
        Mockito.when(restTemplate.getForEntity("http://localhost:8080/employee/E001”, Employee.class"))
          .thenReturn(new ResponseEntity(emp, HttpStatus.OK));

        Employee employee = empService.getEmployee(id);
        Assertions.assertEquals(emp, employee);
    }
}
```

在上面的JUnit测试类中，我们首先要求Mockito使用@Mock注解创建一个虚拟RestTemplate实例。

然后我们用@InjectMocks注解EmployeeService实例以将虚拟实例注入其中。

最后，在测试方法中，我们使用[Mockito的when/then支持](https://www.baeldung.com/mockito-behavior)定义了mock的行为。

## 3. 使用Spring测试

Spring测试模块包含一个名为MockRestServiceServer的Mock服务器。通过这种方法，我们将服务器配置为在通过我们的RestTemplate实例分派特定请求时返回特定对象。此外，我们可以在该服务器实例上验证()是否满足所有期望。

MockRestServiceServer实际上是通过使用MockClientHttpRequestFactory拦截HTTP API调用来工作的。根据我们的配置，它创建了一个预期请求和相应响应的列表。当RestTemplate实例调用API时，它会在其期望列表中查找请求，并返回相应的响应。

因此，它消除了在任何其他端口运行HTTP服务器以发送Mock响应的需要。

让我们使用MockRestServiceServer为同一个getEmployee()示例创建一个简单的测试：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringTestConfig.class)
public class EmployeeServiceMockRestServiceServerUnitTest {

    @Autowired
    private EmployeeService empService;
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_thenReturnsMockedObject()() {
        Employee emp = new Employee("E001", "Eric Simmons");
        mockServer.expect(ExpectedCount.once(),
                    requestTo(new URI("http://localhost:8080/employee/E001")))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(emp))
              );

        Employee employee = empService.getEmployee(id);
        mockServer.verify();
        Assertions.assertEquals(emp, employee);
    }
}
```

在前面的代码片段中，我们使用MockRestRequestMatchers和MockRestResponseCreators中的静态方法以清晰易读的方式定义REST调用的期望和响应：

```java
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;      
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
```

我们应该记住，测试类中的RestTemplate应该与EmployeeService类中使用的实例相同。为了确保这一点，我们在Spring配置中定义了一个RestTemplate bean，并在测试和实现中自动连接实例：

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

当我们编写集成测试并且只需要Mock外部HTTP调用时，使用MockRestServiceServer非常有用。

## 4. 总结

在这篇简短的文章中，我们讨论了一些在编写单元测试时通过HTTP Mock外部REST API调用的有效选项。