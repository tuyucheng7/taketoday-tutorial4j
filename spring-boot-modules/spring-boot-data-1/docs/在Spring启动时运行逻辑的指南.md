## 1. 概述

在本教程中，我们将重点介绍如何**在Spring应用程序启动时运行逻辑**。

## 2. 启动时运行逻辑

在Spring应用程序启动期间/之后运行逻辑是一个常见的场景。但它也会导致多个问题。

为了从控制反转中受益，我们需要放弃对应用程序流向容器的部分控制。这就是为什么实例化、启动时的设置逻辑等需要特别注意的原因。

我们不能简单地将我们的逻辑包含在bean的构造函数中，或者在实例化任何对象后调用方法，因为我们在这些过程中无法控制。

让我们看一个真实的例子：

```java
@Component
public class InvalidInitExampleBean {

    @Autowired
    private Environment environment;

    public InvalidInitExampleBean() {
        environment.getActiveProfiles();
    }
}
```

在这里，我们试图在构造函数中访问自动装配字段。调用构造函数时，Spring bean尚未完全初始化。这是一个问题，因为**调用尚未初始化的字段将导致NullPointerExceptions**。

让我们看看Spring为我们提供的几种管理这种情况的方法。

### 2.1 @PostConstruct注解

我们可以使用Javax的@PostConstruct注解来标注一个应该**在bean初始化后立即运行一次的方法**。请记住，即使没有要注入的内容，Spring也会运行带注解的方法。

下面是@PostConstruct的实际应用：

```java
@Component
public class PostConstructExampleBean {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        LOG.info("Env Default Profiles {}", Arrays.asList(environment.getDefaultProfiles()));
    }
}
```

我们可以看到Environment实例被安全地注入，然后在@PostConstruct注解方法中调用而不会抛出NullPointerException。

### 2.2 InitializingBean接口

InitializingBean方法以类似的方式工作。我们不需要标注方法，而是需要实现InitializingBean接口和afterPropertiesSet()方法。

这里我们使用InitializingBean接口实现前面的示例：

```java
@Component
public class InitializingBeanExampleBean implements InitializingBean {

    @Autowired
    private Environment environment;

    @Override
    public void afterPropertiesSet() {
        LOGGER.info("Eve Default Profiles: {}", Arrays.asList(environment.getDefaultProfiles()));
    }
}
```

### 2.3 ApplicationListener

我们可以**在Spring上下文初始化后使用这种方法来运行逻辑**。因此，我们不关注任何特定的bean。相反，我们正在等待它们全部初始化。

为了做到这一点，我们需要创建一个实现ApplicationListener<ContextRefreshedEvent\>接口的bean：

```java
@Component
public class StartupApplicationListenerExample implements ApplicationListener<ContextRefreshedEvent> {

    public static int counter;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOG.info("Increment counter");
        counter++;
    }
}
```

我们可以通过使用新引入的@EventListener注解实现相同的效果：

```java
@Component
public class EventListenerExampleBean {

    public static int counter;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Increment counter");
        counter++;
    }
}
```

我们要确保为我们的需求选择合适的事件。在此示例中，我们选择了ContextRefreshedEvent。

### 2.4 @Bean的initMethod属性

我们可以使用initMethod属性在bean初始化之后运行一个方法。

如下所示的bean：

```java
public class InitMethodExampleBean {

    @Autowired
    private Environment environment;

    public void init() {
        LOG.info("Env Default Profiles: {}", Arrays.asList(environment.getDefaultProfiles()));
    }
}
```

请注意，我们没有实现任何特殊接口或使用任何特殊注解。

然后我们可以使用@Bean注解定义bean：

```java
@Bean(initMethod = "init")
public InitMethodExampleBean initMethodExampleBean() {
    return new InitMethodExampleBean();
}
```

这是bean定义在XML配置中的样子：

```xml
<bean id="initMethodExampleBean"
      class="cn.tuyucheng.taketoday.startup.InitMethodExampleBean"
      init-method="init">
</bean>
```

### 2.5 构造注入

如果我们使用构造函数注入来注入字段，我们可以简单地将我们的逻辑包含在构造函数中：

```java
@Component
public class LogicInConstructorExampleBean {

    private static final Logger LOG = Logger.getLogger(LogicInConstructorExampleBean.class);

    private final Environment environment;

    @Autowired
    public LogicInConstructorExampleBean(Environment environment) {
        this.environment = environment;
        LOG.info(Arrays.asList(environment.getDefaultProfiles()));
    }
}
```

### 2.6 CommandLineRunner接口

Spring Boot提供了一个带有回调run()方法的CommandLineRunner接口。这可以在实例化Spring应用程序上下文之后在应用程序启动时调用。

让我们看一个例子：

```java
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineAppStartupRunner.class);

    public static int counter;

    @Override
    public void run(String...args) throws Exception {
        LOG.info("Increment counter");
        counter++;
    }
}
```

>   **注意**：如[文档](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/CommandLineRunner.html)中所述，可以在同一应用程序上下文中定义多个CommandLineRunner bean，并且可以使用Ordered接口或@Order注解对其进行排序。

### 2.7 ApplicationRunner

与CommandLineRunner类似，Spring Boot还提供了一个ApplicationRunner接口，其中包含一个在应用程序启动时调用的run()方法。但是，它接收[ApplicationArguments](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/ApplicationArguments.html)类的实例作为参数，而不是传递给回调方法的原始字符串参数。

ApplicationArguments接口具有获取参数值的方法，这些参数值是选项和普通参数值。以––为前缀的参数是选项参数。

让我们看一个例子：

```java
@Component
public class AppStartupRunner implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AppStartupRunner.class);

    public static int counter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Application started with option names : {}", args.getOptionNames());
        LOG.info("Increment counter");
        counter++;
    }
}
```

## 3. 组合机制

为了完全控制我们的bean，我们可以将上述机制组合在一起。

这是执行顺序：

1. constructor
2. @PostConstruct注解方法
3. InitializingBean的afterPropertiesSet()方法
4. 在XML或@Bean注解中指定为init-method的初始化方法

让我们创建一个结合了所有机制的Spring bean：

```java
@Component
@Scope(value = "prototype")
public class AllStrategiesExampleBean implements InitializingBean {

    private static final Logger LOG = Logger.getLogger(AllStrategiesExampleBean.class);

    public AllStrategiesExampleBean() {
        LOG.info("Constructor");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("InitializingBean");
    }

    @PostConstruct
    public void postConstruct() {
        LOG.info("PostConstruct");
    }

    public void init() {
        LOG.info("init-method");
    }
}
```

如果我们尝试实例化这个bean，我们可以看到与上面指定的顺序匹配的日志：

```shell
06:32:30.357 [main] INFO cn.tuyucheng.taketoday.startup.AllStrategiesExampleBean - Constructor
06:32:30.368 [main] INFO cn.tuyucheng.taketoday.startup.AllStrategiesExampleBean - PostConstruct
06:32:30.368 [main] INFO cn.tuyucheng.taketoday.startup.AllStrategiesExampleBean - InitializingBean
06:32:30.369 [main] INFO cn.tuyucheng.taketoday.startup.AllStrategiesExampleBean - init-method
```

## 4. 总结

在本文中，我们展示了在Spring应用程序启动时运行逻辑的多种方式。