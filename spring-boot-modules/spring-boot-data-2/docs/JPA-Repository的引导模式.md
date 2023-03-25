## 1. 概述

在这个简短的教程中，**我们将重点介绍Spring为JPA Repository提供的不同类型的BootstrapMode，用于更改其实例化的编排**。

在启动时，Spring Data扫描Repository并将它们的bean定义注册为单例作用域bean。在初始化期间，Repository会立即获得一个EntityManager。具体来说，他们获取JPA元模型并验证声明的查询。

**默认情况下，JPA是同步启动的。因此，在引导过程完成之前，Repository的实例化将被阻塞**。随着Repository数量的增加，应用程序可能需要很长时间才能开始接受请求。

## 2. 引导Repository的不同选项

让我们从添加spring-data-jpa依赖项开始。当我们使用Spring Boot时，我们将使用相应的[spring-boot-starter-data-jpa](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

我们可以通过配置属性告诉Spring使用默认的Repository启动行为：

```properties
spring.data.jpa.repositories.bootstrap-mode=default
```

我们可以通过使用基于注解的配置来实现相同的目的：

```java
@SpringBootApplication
@EnableJpaRepositories(bootstrapMode = BootstrapMode.DEFAULT)
public class BootstrapModeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapModeApplication.class, args);
    }
}
```

第三种方法(仅限于单个测试类)是使用@DataJpaTest注解：

```java
@DataJpaTest(bootstrapMode = BootstrapMode.LAZY)
class BootstrapModeLazyIntegrationTest {
    // ...
}
```

对于以下示例，**我们将使用@DataJpaTest注解并说明不同的Repository启动选项**。

### 2.1 Default

启动模式的默认值将急切地实例化Repository。**因此，与任何其他Spring bean一样，它们的初始化将在注入时发生**。

让我们创建一个Todo实体：

```java
@Entity
public class Todo {
    @Id
    private Long id;
    private String label;

    // standard setters and getters
}
```

接下来，我们需要创建其关联的Repository：

```java
public interface TodoRepository extends CrudRepository<Todo, Long> {
}
```

最后，让我们添加一个使用Repository的测试：

```java
@DataJpaTest
class BootstrapModeDefaultIntegrationTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void givenBootstrapModeValueIsDefault_whenCreatingTodo_shouldSuccess() {
        Todo todo = new Todo("Something to be done");

        assertThat(todoRepository.save(todo)).hasNoNullFieldsOrProperties();
    }
}
```

运行我们的测试后，让我们观察一下日志，我们可以从中知道Spring如何启动TodoRepository：

```shell
20:59:54.705 [main] INFO  [o.s.d.r.c.RepositoryConfigurationDelegate] >>> Bootstrapping Spring Data JPA repositories in DEFAULT mode. 
20:59:54.739 [main] INFO  [o.s.d.r.c.RepositoryConfigurationDelegate] >>> Finished Spring Data repository scanning in 27 ms. Found 1 JPA repository interfaces. 
20:59:54.965 [main] INFO  [o.s.j.d.e.EmbeddedDatabaseFactory] >>> Starting embedded database: url='jdbc:h2:mem:e9b57c1c-d852-428d-b6ca-ba86bbc7517e;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa' 
21:00:00.930 [delayedTaskExecutor-1] INFO  [o.h.e.t.j.p.i.JtaPlatformInitiator] >>> HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform] 
21:00:00.936 [delayedTaskExecutor-1] INFO  [o.s.o.j.LocalContainerEntityManagerFactoryBean] >>> Initialized JPA EntityManagerFactory for persistence unit 'default' 
21:00:01.130 [main] INFO  [c.t.t.b.b.BootstrapModeDefaultIntegrationTest] >>> Started BootstrapModeDefaultIntegrationTest in 7.167 seconds (JVM running for 7.951) 
```

在我们的示例中，**我们尽早地初始化Repository并在应用程序启动后使其可用**。

### 2.2 Lazy

通过对JPA Repository使用惰性BootstrapMode，Spring会注册Repository的bean定义，但不会立即实例化它。**因此，使用LAZY选项，只会在第一次使用时触发其初始化**。

让我们修改我们的测试并将LAZY选项应用于bootstrapMode：

```java
@DataJpaTest(bootstrapMode = BootstrapMode.LAZY)
```

然后，让我们使用新的配置启动测试，并检查相应的日志：

```shell
21:04:01.892 [main] INFO  [o.s.d.r.c.RepositoryConfigurationDelegate] >>> Bootstrapping Spring Data JPA repositories in LAZY mode. 
21:04:01.928 [main] INFO  [o.s.d.r.c.RepositoryConfigurationDelegate] >>> Finished Spring Data repository scanning in 29 ms. Found 1 JPA repository interfaces. 
21:04:01.972 [main] INFO  [o.s.b.t.a.j.TestDatabaseAutoConfiguration$EmbeddedDataSourceBeanFactoryPostProcessor] >>> Replacing 'dataSource' DataSource bean with embedded version 
21:04:02.158 [main] INFO  [o.s.j.d.e.EmbeddedDatabaseFactory] >>> Starting embedded database: url='jdbc:h2:mem:8e4088ba-71de-49fc-a7ad-238f07485be9;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa' 
21:04:02.504 [main] INFO  [c.t.t.b.b.BootstrapModeLazyIntegrationTest] >>> Started BootstrapModeLazyIntegrationTest in 1.332 seconds (JVM running for 2.119) 
```

我们应该注意这里的几个缺点：

+ Spring可能会在没有初始化Repository的情况下开始接收请求，从而在处理第一个Repository时增加延迟。
+ 将BootstrapMode全局设置为LAZY很容易出错，Spring不会验证我们测试中未包含的Repository中包含的查询和元数据。

**我们应该只在开发期间使用LAZY模式，以避免在生产中部署应用程序时出现潜在的初始化错误**。为此，我们可以优雅地使用[Spring Profile](https://www.baeldung.com/spring-profiles)。

### 2.3 Deferred

Deferred是异步启动JPA时使用的正确方式。因此，**Repository不会等待EntityManagerFactory的初始化**。

让我们使用ThreadPoolTaskExecutor(它的Spring实现之一)在配置类中声明一个AsyncTaskExecutor，并覆盖返回[Future](https://www.baeldung.com/java-future)的submit方法：

```java
@Bean
AsyncTaskExecutor delayedTaskExecutor() {
    return new ThreadPoolTaskExecutor() {
        @Override
        public <T> @NotNull Future<T> submit(@NotNull Callable<T> task) {
            return super.submit(() -> {
                Thread.sleep(5000);
                return task.call();
            });
        }
    };
}
```

接下来，让我们将EntityManagerFactory bean添加到我们的配置中，并指示我们想要使用异步执行器进行后台启动：

```java
@Bean
LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, AsyncTaskExecutor delayedTaskExecutor) {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setPackagesToScan("cn.tuyucheng.boot.bootstrapmode");
    factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    factory.setDataSource(dataSource);
    factory.setBootstrapExecutor(delayedTaskExecutor);
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "create-drop");
    factory.setJpaPropertyMap(properties);
    return factory;
}
```

最后，让我们修改测试以使用DEFERRED启动模式：

```java
@DataJpaTest(bootstrapMode = BootstrapMode.DEFERRED)
```

再次运行测试并观察日志：

```shell
21:10:46.442 [main] INFO  [o.s.d.r.c.RepositoryConfigurationDelegate] >>> Bootstrapping Spring Data JPA repositories in DEFERRED mode. 
21:10:46.475 [main] INFO  [o.s.d.r.c.RepositoryConfigurationDelegate] >>> Finished Spring Data repository scanning in 28 ms. Found 1 JPA repository interfaces. 
21:10:46.519 [main] INFO  [o.s.b.t.a.j.TestDatabaseAutoConfiguration$EmbeddedDataSourceBeanFactoryPostProcessor] >>> Replacing 'dataSource' DataSource bean with embedded version 
21:10:46.709 [main] INFO  [o.s.j.d.e.EmbeddedDatabaseFactory] >>> Starting embedded database: url='jdbc:h2:mem:8156978a-0778-4c1f-b617-957358609b82;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa' 
21:10:47.032 [main] INFO  [o.s.d.r.c.DeferredRepositoryInitializationListener] >>> Triggering deferred initialization of Spring Data repositories… 
```

在此示例中，应用程序上下文启动完成会触发Repository的初始化。简而言之，Spring将Repository标记为惰性，并注册一个DeferredRepositoryInitializationListener bean。当ApplicationContext触发ContextRefreshedEvent事件时，它会初始化所有Repository。

**因此，Spring Data在应用程序启动之前初始化Repository并验证其包含的查询和元数据**。

## 3. 总结

在本文中，我们研究了初始化JPA Repository的各种方法以及在哪些情况下使用它们。