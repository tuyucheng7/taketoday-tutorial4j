## 1. 概述

在这篇文章中，我们介绍将原型bean注入单例实例的不同方法。

默认情况下，Spring bean是单例的，当我们尝试注入不同作用域的bean时，就会出现问题。例如，将原型bean注入单例bean。这被称为作用域bean注入问题。

## 2. 原型Bean注入问题

为了描述问题，我们首先配置以下bean：

```java

@Configuration
public class AppConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PrototypeBean prototypeBean() {
        return new PrototypeBean();
    }

    @Bean
    public SingletonBean singletonBean() {
        return new SingletonBean();
    }
}
```

请注意，第一个bean是原型作用域bean，另一个是单例。

现在，我们将原型作用域的bean注入到单例中，然后通过getPrototypeBean()方法公开：

```java

@Slf4j
public class SingletonBean {

    @Autowired
    private PrototypeBean prototypeBean;

    public SingletonBean() {
        log.info("Singleton instance created");
    }

    public PrototypeBean getPrototypeBean() {
        log.info(String.valueOf(LocalDate.now()));
        return prototypeBean;
    }
}
```

接下来，我们加载ApplicationContext并获取两次单例bean：

```java

public class BeanInjectionStarter {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        SingletonBean firstSingleton = context.getBean(SingletonBean.class);
        PrototypeBean firstPrototype = firstSingleton.getPrototypeBean();

        SingletonBean secondSingleton = context.getBean(SingletonBean.class);
        PrototypeBean secondPrototype = secondSingleton.getPrototypeBean();

        isTrue(firstPrototype.equals(secondPrototype), "The same instance is returned");
    }
}
```

下面是控制台的输出：

```text
23:11:16.810 [main] INFO  [c.t.t.i.singletone.SingletonBean] >>> Singleton instance created 
23:11:16.823 [main] INFO  [c.t.t.i.prototype.PrototypeBean] >>> Prototype instance created 
23:11:16.864 [main] INFO  [c.t.t.i.singletone.SingletonBean] >>> 23:11:16.864035400 
23:11:16.865 [main] INFO  [c.t.t.i.singletone.SingletonBean] >>> 23:11:16.865034500 
```

**两个bean只在应用程序上下文启动时初始化一次**。

## 3. 注入ApplicationContext

我们还可以将ApplicationContext直接注入到一个bean中。

**为此，我们可以使用@Autowire注解或实现ApplicationContextAware接口**：

```java

@Component
public class SingletonAppContextBean implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public PrototypeBean getPrototypeBean() {
        return applicationContext.getBean(PrototypeBean.class);
    }
}
```

每次调用getPrototypeBean()方法时，都会从ApplicationContext返回一个新的PrototypeBean实例。

**然而，这种方法有严重的缺点，它与控制反转的原则相矛盾，因为我们直接从容器中请求依赖bean**。

此外，我们从SingletonAppContextBean类中的applicationContext获取原型bean，这意味着将代码耦合到Spring框架。

## 4. 方法注入

解决问题的另一种方法是使用@Lookup注解的方法注入：

```java

@Component
public class SingletonLookupBean {

    @Lookup
    public PrototypeBean getPrototypeBean() {
        return null;
    }
}
```

Spring将重写带有@Lookup注解的getPrototypeBean()方法。
然后它将bean注册到应用程序上下文中，每当我们请求getPrototypeBean()方法时，它都会返回一个新的PrototypeBean实例。

**它使用CGLIB生成负责从应用程序上下文中获取PrototypeBean的字节码**。

## 5. javax.inject API

```java
public class SingletonProviderBean {

    @Autowired
    private Provider<PrototypeBean> myPrototypeBeanProvider;

    public PrototypeBean getPrototypeInstance() {
        return myPrototypeBeanProvider.get();
    }
}
```

我们使用Provider接口来注入原型bean。
对于每个getPrototypeInstance()方法调用，myPrototypeBeanProvider.get()方法返回一个新的PrototypeBean实例。

## 6. 作用域代理

默认情况下，Spring持有对真实对象的引用以执行注入。
**在这里，我们创建一个代理对象来注入真实对象和依赖对象**。

每次调用代理对象上的方法时，代理都会自行决定是创建真实对象的新实例还是重用现有实例。

为此，我们需要修改AppConfig类：

```text
@Scope(
  value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, 
  proxyMode = ScopedProxyMode.TARGET_CLASS)
```

默认情况下，Spring使用CGLIB库直接子类化对象。
为了避免使用CGLIB，我们可以使用ScopedProxyMode.INTERFACES配置代理模式，改为使用JDK动态代理。

## 7. ObjectFactory接口

Spring提供ObjectFactory<T\>接口来按需生成给定类型的对象：

```text
public class SingletonObjectFactoryBean {
    
    @Autowired
    private ObjectFactory<PrototypeBean> prototypeBeanObjectFactory;

    public PrototypeBean getPrototypeInstance() {
        return prototypeBeanObjectFactory.getObject();
    }
}
```

让我们看看getPrototypeInstance()方法；getObject()为每个请求返回一个全新的PrototypeBean实例。在这里，我们可以更好地控制原型的初始化。

此外，ObjectFactory是框架的一部分；这意味着避免为了使用此方法而进行其他设置。

## 8. 使用java.util.Function在运行时创建Bean

另一种选择是在运行时创建原型bean实例，这也允许我们向实例添加参数。

我们在PrototypeBean类中添加一个name字段：

```java
public class PrototypeBean {
    private String name;

    public PrototypeBean(String name) {
        this.name = name;
        log.info("Prototype instance " + name + " created");
    }

    // ...
}
```

接下来，我们通过使用java.util.Function接口将bean工厂注入到我们的单例bean中：

```java
public class SingletonFunctionBean {

    @Autowired
    private Function<String, PrototypeBean> beanFactory;

    public PrototypeBean getPrototypeInstance(String name) {
        return beanFactory.apply(name);
    }
}
```

最后，我们必须在配置中定义工厂bean、原型和单例bean：

```java

@Configuration
public class AppConfigFunctionBean {

    @Bean
    public Function<String, PrototypeBean> beanFactory() {
        return this::prototypeBeanWithParam;
    }

    @Bean
    @Scope(value = "prototype")
    public PrototypeBean prototypeBeanWithParam(String name) {
        return new PrototypeBean(name);
    }

    @Bean
    public SingletonFunctionBean singletonFunctionBean() {
        return new SingletonFunctionBean();
    }
}
```

## 9. 测试

现在我们编写一个简单的JUnit测试来测试ObjectFactory接口的使用：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = AppConfig.class)
class PrototypeBeanInjectionIntegrationTest {

    @Test
    void givenPrototypeInjection_whenObjectFactory_thenNewInstanceReturn() {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        SingletonObjectFactoryBean firstContext = context.getBean(SingletonObjectFactoryBean.class);
        SingletonObjectFactoryBean secondContext = context.getBean(SingletonObjectFactoryBean.class);

        PrototypeBean firstInstance = firstContext.getPrototypeInstance();
        PrototypeBean secondInstance = secondContext.getPrototypeInstance();

        assertTrue("New instance expected", firstInstance != secondInstance);
    }
}
```

成功启动测试后，我们可以看到每次调用getPrototypeInstance()方法时，都会创建一个新的原型bean实例。

## 10. 总结

在这个教程中，我们学习了几种将原型bean注入单例实例的方法。