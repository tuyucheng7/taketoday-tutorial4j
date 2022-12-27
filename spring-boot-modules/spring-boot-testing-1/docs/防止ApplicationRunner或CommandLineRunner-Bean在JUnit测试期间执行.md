## 1. 概述

在本教程中，我们将演示如何防止ApplicationRunner或CommandLineRunner类型的bean在Spring Boot集成测试期间运行。

## 2. 应用示例

我们的示例应用程序由一个CommandLineRunner、一个ApplicationRunner和一个TaskService bean组成。

CommandLineRunner调用TaskService的execute()方法，以便在应用程序启动时执行任务：

```java
@Component
public class CommandLineTaskExecutor implements CommandLineRunner {
    private final TaskService taskService;

    public CommandLineTaskExecutor(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void run(String... args) {
        taskService.execute("command line runner task");
    }
}
```

以同样的方式，ApplicationRunner与TaskService交互以执行另一个任务：

```java
@Component
public class ApplicationRunnerTaskExecutor implements ApplicationRunner {
    private final TaskService taskService;

    public ApplicationRunnerTaskExecutor(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void run(ApplicationArguments args) {
        taskService.execute("application runner task");
    }
}
```

最后，TaskService负责执行其客户端的任务：

```java
@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public void execute(String task) {
        logger.info("do " + task);
    }
}
```

此外，我们还有一个Spring Boot应用程序主类，可以使其正常工作：

```java
@SpringBootApplication
public class ApplicationCommandLineRunnerApp {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationCommandLineRunnerApp.class, args);
    }
}
```

## 3. 预期的测试行为

**ApplicationRunnerTaskExecutor和CommandLineTaskExecutor在Spring Boot加载应用程序上下文之后运行**。

我们可以通过一个简单的测试来验证这一点：

```java
@SpringBootTest
class RunApplicationIntegrationTest {
    @SpyBean
    ApplicationRunnerTaskExecutor applicationRunnerTaskExecutor;
    @SpyBean
    CommandLineTaskExecutor commandLineTaskExecutor;

    @Test
    void whenContextLoads_thenRunnersRun() {
        verify(applicationRunnerTaskExecutor, times(1)).run(any());
        verify(commandLineTaskExecutor, times(1)).run(any());
    }
}
```

正如我们所看到的，我们使用@SpyBean注解将Mockito Spy应用到ApplicationRunnerTaskExecutor和CommandLineTaskExecutor bean，通过这样做，我们可以验证每个bean的run方法是否被调用了一次。

在接下来的部分中，我们将看到在Spring Boot集成测试期间防止这种默认行为的各种方法和技术。

## 4. Spring Profiles

我们可以通过@Profile对它们进行标注，来防止这两个类运行：

```java
@Profile("!test")
@Component
public class CommandLineTaskExecutor implements CommandLineRunner {
    // ...
}
```

```java
@Profile("!test")
@Component
public class ApplicationRunnerTaskExecutor implements ApplicationRunner {
    // ...
}
```

完成上述更改后，我们继续进行集成测试：

```java

@ActiveProfiles("test")
@SpringBootTest
class RunApplicationWithTestProfileIntegrationTest {
    @Autowired
    private ApplicationContext context;

    @Test
    void whenContextLoads_thenRunnersAreNotLoaded() {
        assertNotNull(context.getBean(TaskService.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(CommandLineTaskExecutor.class),
                "CommandLineRunner should not be loaded during this integration test");
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(ApplicationRunnerTaskExecutor.class),
                "ApplicationRunner should not be loaded during this integration test");
    }
}
```

如我们所见，**我们使用@ActiveProfiles("test")注解对上面的测试类进行了标注，这意味着它不会注入那些带有@Profile("!test")注解的类**，因此根本不会加载CommandLineTaskExecutor bean和ApplicationRunnerTaskExecutor bean。

## 5. @ConditionalOnProperty注解

或者，我们可以按属性配置它们的自动注入，然后使用@ConditionalOnProperty注解：

```java
@ConditionalOnProperty(
        prefix = "application.runner",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@Component
public class ApplicationRunnerTaskExecutor implements ApplicationRunner {
    // same as before
}
```

```java
@ConditionalOnProperty(
        prefix = "command.line.runner",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@Component
public class CommandLineTaskExecutor implements CommandLineRunner {
    // same as before
}
```

如我们所见，**ApplicationRunnerTaskExecutor和CommandLineTaskExecutor在默认情况下处于启用状态**，如果我们将以下属性设置为false，则可以禁用它们：

```properties
command.line.runner.enabled=false
application.runner.enabled=false
```

因此，在我们的测试中，**我们将这些属性设置为false，并且ApplicationRunnerTaskExecutor和CommandLineTaskExecutor bean都不会加载到应用程序上下文中**：

```java
@SpringBootTest(properties = {
        "command.line.runner.enabled=false",
        "application.runner.enabled=false"
})
class RunApplicationWithTestPropertiesIntegrationTest {
    @Autowired
    private ApplicationContext context;

    @Test
    void whenContextLoads_thenRunnersAreNotLoaded() {
        assertNotNull(context.getBean(TaskService.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(CommandLineTaskExecutor.class),
                "CommandLineRunner should not be loaded during this integration test");
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(ApplicationRunnerTaskExecutor.class),
                "ApplicationRunner should not be loaded during this integration test");
    }
}
```

现在，尽管上述技术可以帮助我们实现目标，但在某些情况下，我们希望测试所有Spring bean是否已正确加载和注入。

例如，我们可能想要测试TaskService bean是否正确注入到CommandLineTaskExecutor，但我们仍然不希望在测试期间执行它的run方法。因此，我们会在最后一节解释如何实现这一点。

## 6. 不启动整个容器

在这里，我们将描述如何通过不启动整个应用程序容器来防止CommandLineTaskExecutor和ApplicationRunnerTaskExecutor bean执行。

在前面的部分中，我们使用了@SpringBootTest注解，这导致整个容器在我们的集成测试期间启动，@SpringBootTest包含两个与最后一个解决方案相关的元注解：

```java
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@ExtendWith(SpringExtension.class)
```

好吧，如果在我们的测试过程中不需要启动整个容器，那么就不要使用@BootstrapWith。

相反，**我们可以用@ContextConfiguration替换它**：

```java
@ContextConfiguration(classes = {ApplicationCommandLineRunnerApp.class}, initializers = ConfigDataApplicationContextInitializer.class)
```

通过@ContextConfiguration，我们确定如何为集成测试加载和配置应用程序上下文。**通过设置@ContextConfiguration注解的classes属性，我们告诉Spring Boot应该使用ApplicationCommandLineRunnerApp类来加载应用程序上下文，通过将initializers定义为ConfigDataApplicationContextInitializer，应用程序加载其属性**。

我们**仍然需要@ExtendWith(SpringExtension.class)注解，因为它将Spring TestContext Framework集成到JUnit 5的Jupiter编程模型中**。

由于上述原因，**Spring Boot应用程序上下文在不执行CommandLineTaskExecutor或ApplicationRunnerTaskExecutor bean的情况下加载应用程序的组件和属性**：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationCommandLineRunnerApp.class}, initializers = ConfigDataApplicationContextInitializer.class)
class LoadSpringContextIntegrationTest {
    @SpyBean
    TaskService taskService;

    @SpyBean
    CommandLineRunner commandLineRunner;

    @SpyBean
    ApplicationRunner applicationRunner;

    @Test
    void whenContextLoads_thenRunnersDoNotRun() throws Exception {
        assertNotNull(taskService);
        assertNotNull(commandLineRunner);
        assertNotNull(applicationRunner);

        verify(taskService, times(0)).execute(any());
        verify(commandLineRunner, times(0)).run(any());
        verify(applicationRunner, times(0)).run(any());
    }
}
```

另外，我们必须记住，**单独使用ConfigDataApplicationContextInitializer时，不提供对@Value("${...}")注入的支持**。如果我们想支持它，我们必须配置一个PropertySourcesPlaceholderConfigurer。

## 7. 总结

在本文中，我们演示了在Spring Boot集成测试期间阻止ApplicationRunner和CommandLineRunner bean执行的各种方法。