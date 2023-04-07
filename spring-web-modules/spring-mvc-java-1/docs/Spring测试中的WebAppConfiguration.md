## **一、概述**

在本文中，我们将探讨Spring 中的*@WebAppConfiguration*注释，为什么我们在集成测试中需要它，以及我们如何配置它以便这些测试真正引导*WebApplicationContext*。

## **2. \*@WebAppConfiguration\***

简单地说，这是一个类级别的注解，用于在 Spring 框架中创建 Web 版本的应用程序上下文。

它用于表示为测试引导的*ApplicationContext应该是**WebApplicationContext*的一个实例。

关于用法的快速说明——我们通常会在集成测试中找到此注释，因为 WebApplicationContext*用于*构建*MockMvc*对象。[您可以在此处](https://www.baeldung.com/integration-testing-in-spring)找到有关使用 Spring 进行集成测试的更多信息。

## **3.加载\*WebApplicationContext\***

从 Spring 3.2 开始，现在支持在集成测试中加载*WebApplicationContext* ：

```java
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class EmployeeControllerTest {
    ...
}
复制
```

这指示*TestContext*框架应该为测试加载*WebApplicationContext 。*

并且，在后台创建一个*MockServletContext*并由*TestContext框架提供给我们测试的**WebApplicationContext*。

### **3.1. 配置选项**

*默认情况下， WebApplicationContext*的基本资源路径将设置为*“file:src/main/webapp”，*这是 Maven 项目中 WAR 根目录的默认位置。

*但是，我们可以通过简单地为@WebAppConfiguration*注释提供替代路径来覆盖它：

```java
@WebAppConfiguration("src/test/webapp")复制
```

我们还可以从类路径而不是文件系统中引用基本资源路径：

```java
@WebAppConfiguration("classpath:test-web-resources")复制
```

### **3.2. 缓存**

*加载WebApplicationContext*后，它将被缓存并重新用于所有在同一测试套件中声明相同唯一上下文配置的后续测试。

有关缓存的更多详细信息，您可以查阅参考手册的[上下文缓存部分。](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#testcontext-ctx-management-caching)

## **4.在测试中使用\*@WebAppConfiguration\***

现在我们明白了为什么我们需要在我们的测试类中添加*@WebAppConfiguration*注释，让我们看看如果我们在使用*WebApplicationContext时***错过添加它会发生什么**。

```java
@RunWith(SpringJUnit4ClassRunner.class)
// @WebAppConfiguration omitted on purpose
@ContextConfiguration(classes = WebConfig.class)
public class EmployeeTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }
    
    ...
}复制
```

请注意，我们注释掉了注释以模拟我们忘记添加它的场景。在这里很容易看出为什么当我们运行 JUnit 测试时测试会失败：**我们试图在一个我们没有设置的类中自动装配\*WebApplicationContext\* \*。\***

然而，一个更典型的例子是使用支持 web 的 Spring 配置的测试；这实际上足以使测试中断。

我们来看一下：

```java
@RunWith(SpringJUnit4ClassRunner.class)
// @WebAppConfiguration omitted on purpose
@ContextConfiguration(classes = WebConfig.class)
public class EmployeeTestWithoutMockMvc {

    @Autowired
    private EmployeeController employeeController;

    ...
}复制
```

即使上面的示例没有自动装配 WebApplicationContext*它*仍然会失败，因为它正在尝试使用启用 Web 的配置 – *WebConfig*：

```java
@Configuration
@EnableWebMvc
@ComponentScan("com.baeldung.web")
public class WebConfig implements WebMvcConfigurer {
    ...
}复制
```

注释*@EnableWebMvc*是这里的罪魁祸首——它基本上需要一个支持 web 的 Spring 上下文，如果没有它——我们将看到测试失败：

```java
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: 
  No qualifying bean of type [javax.servlet.ServletContext] found for dependency: 
    expected at least 1 bean which qualifies as autowire candidate for this dependency. 

Dependency annotations: 
  {@org.springframework.beans.factory.annotation.Autowired(required=true)}
    at o.s.b.f.s.DefaultListableBeanFactory
      .raiseNoSuchBeanDefinitionException(DefaultListableBeanFactory.java:1373)
    at o.s.b.f.s.DefaultListableBeanFactory
      .doResolveDependency(DefaultListableBeanFactory.java:1119)
    at o.s.b.f.s.DefaultListableBeanFactory
      .resolveDependency(DefaultListableBeanFactory.java:1014)
    at o.s.b.f.a.AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement
      .inject(AutowiredAnnotationBeanPostProcessor.java:545)
    ... 43 more复制
```

因此，我们可以通过在测试中添加*@WebAppConfiguration*注释轻松解决这个问题。

## **5.结论**

在本文中，我们展示了如何让*TestContext*框架通过添加注释将*WebApplicationContext*加载到我们的集成测试中。

最后，我们查看了示例，即使我们将 @ContextConfiguration 添加*到测试中，除非我们添加**@WebAppConfiguration*注释，否则这将无法工作。