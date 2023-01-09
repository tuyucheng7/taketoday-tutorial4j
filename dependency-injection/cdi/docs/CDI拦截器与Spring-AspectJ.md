## 1. 简介

拦截器模式通常用于在应用程序中添加新的、横切的功能或逻辑，并且在大量库中有可靠的支持。

在本文中，我们将介绍并对比其中的两个主要库：CDI 拦截器和 Spring AspectJ。

## 2. CDI 拦截器项目设置

Jakarta EE 正式支持 CDI，但一些实现支持在JavaSE 环境中使用 CDI。[Weld](http://weld.cdi-spec.org/)可以被认为是JavaSE 支持的 CDI 实现的一个例子。

为了使用 CDI，我们需要在 POM 中导入 Weld 库：

```xml
<dependency>
    <groupId>org.jboss.weld.se</groupId>
    <artifactId>weld-se-core</artifactId>
    <version>3.0.5.Final</version>
</dependency>
```

可以在[Maven](https://mvnrepository.com/artifact/org.jboss.weld.se/weld-se-core)存储库中找到最新的 Weld 库。

现在让我们创建一个简单的拦截器。

## 3. CDI拦截器介绍

为了指定我们需要拦截的类，让我们创建拦截器绑定：

```java
@InterceptorBinding
@Target( { METHOD, TYPE } )
@Retention( RUNTIME )
public @interface Audited {
}
```

在我们定义了拦截器绑定之后，我们需要定义实际的拦截器实现：

```java
@Audited
@Interceptor
public class AuditedInterceptor {
    public static boolean calledBefore = false;
    public static boolean calledAfter = false;

    @AroundInvoke
    public Object auditMethod(InvocationContext ctx) throws Exception {
        calledBefore = true;
        Object result = ctx.proceed();
        calledAfter = true;
        return result;
    }
}
```

每个@AroundInvoke方法都接受一个javax.interceptor.InvocationContext参数，返回一个java.lang.Object，并且可以抛出一个Exception。

因此，当我们使用新的@Audit接口注解一个方法时，auditMethod将首先被调用，然后目标方法才会继续。

## 4.应用CDI拦截器

让我们在一些业务逻辑上应用创建的拦截器：

```java
public class SuperService {
    @Audited
    public String deliverService(String uid) {
        return uid;
    }
}
```

我们已经创建了这个简单的服务，并使用@Audited注解对我们想要拦截的方法进行了注解。

要启用 CDI 拦截器，需要在位于META-INF目录中的beans.xml文件中指定完整的类名：

```xml
<beans xmlns="http://java.sun.com/xml/ns/javaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
      http://java.sun.com/xml/ns/javaee/beans_1_2.xsd">
    <interceptors>
        <class>com.baeldung.interceptor.AuditedInterceptor</class>
    </interceptors>
</beans>
```

为了验证拦截器确实有效，让我们现在运行以下测试：

```java
public class TestInterceptor {
    Weld weld;
    WeldContainer container;

    @Before
    public void init() {
        weld = new Weld();
        container = weld.initialize();
    }

    @After
    public void shutdown() {
        weld.shutdown();
    }

    @Test
    public void givenTheService_whenMethodAndInterceptorExecuted_thenOK() {
        SuperService superService = container.select(SuperService.class).get();
        String code = "123456";
        superService.deliverService(code);
        
        Assert.assertTrue(AuditedInterceptor.calledBefore);
        Assert.assertTrue(AuditedInterceptor.calledAfter);
    }
}
```

在这个快速测试中，我们首先从容器中获取 bean SuperService ，然后在其上调用业务方法deliverService并通过验证其状态变量来检查拦截器AuditedInterceptor是否实际被调用。

我们还有@Before和@After注解方法，我们分别在其中初始化和关闭 Weld 容器。

## 5.CDI注意事项

我们可以指出 CDI 拦截器的以下优点：

-   它是 Jakarta EE 规范的标准功能
-   一些 CDI 实现库可以在JavaSE 中使用
-   当我们的项目对第三方库有严格限制时可以使用

CDI拦截器的缺点如下：

-   类与业务逻辑和拦截器之间的紧耦合
-   很难看出项目中拦截了哪些类
-   缺乏将拦截器应用于一组方法的灵活机制

## 6. 春季看点

Spring 也支持使用 AspectJ 语法实现类似的拦截器功能。

首先，我们需要在 POM 中添加以下 Spring 和 AspectJ 依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.2.8.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.2</version>
</dependency>
```

可以在 Maven 存储库中找到最新版本的[Spring 上下文](https://mvnrepository.com/artifact/org.springframework/spring-context)、[aspectjweaver 。](https://mvnrepository.com/artifact/org.aspectj/aspectjweaver)

我们现在可以使用 AspectJ 注解语法创建一个简单的方面：

```java
@Aspect
public class SpringTestAspect {
    @Autowired
    private List accumulator;

    @Around("execution( com.baeldung.spring.service.SpringSuperService.(..))")
    public Object auditMethod(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getName();
        accumulator.add("Call to " + methodName);
        Object obj = jp.proceed();
        accumulator.add("Method called successfully: " + methodName);
        return obj;
    }
}
```

我们创建了一个适用于SpringSuperService类的所有方法的方面——为简单起见，它看起来像这样：

```java
public class SpringSuperService {
    public String getInfoFromService(String code) {
        return code;
    }
}
```

## 7. Spring AspectJ 切面应用

为了验证该方面是否真正适用于服务，让我们编写以下单元测试：

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
public class TestSpringInterceptor {
    @Autowired
    SpringSuperService springSuperService;

    @Autowired
    private List accumulator;

    @Test
    public void givenService_whenServiceAndAspectExecuted_thenOk() {
        String code = "123456";
        String result = springSuperService.getInfoFromService(code);
        
        Assert.assertThat(accumulator.size(), is(2));
        Assert.assertThat(accumulator.get(0), is("Call to getInfoFromService"));
        Assert.assertThat(accumulator.get(1), is("Method called successfully: getInfoFromService"));
    }
}
```

在这个测试中，我们注入我们的服务，调用方法并检查结果。

下面是配置的样子：

```java
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {
    @Bean
    public SpringSuperService springSuperService() {
        return new SpringSuperService();
    }

    @Bean
    public SpringTestAspect springTestAspect() {
        return new SpringTestAspect();
    }

    @Bean
    public List getAccumulator() {
        return new ArrayList();
    }
}
```

@EnableAspectJAutoProxy注解中的一个重要方面- 它支持处理标有 AspectJ 的@Aspect注解的组件，类似于 Spring 的 XML 元素中的功能。

## 8. Spring AspectJ注意事项

让我们指出一些使用 Spring AspectJ 的优点：

-   拦截器与业务逻辑解耦
-   拦截器可以从依赖注入中获益
-   拦截器本身就有所有的配置信息
-   添加新的拦截器不需要扩充现有代码
-   拦截器有灵活的机制来选择拦截哪些方法
-   可以在没有 Jakarta EE 的情况下使用

当然还有一些缺点：

-   你需要了解 AspectJ 语法来开发拦截器
-   AspectJ 拦截器的学习曲线高于 CDI 拦截器

## 9. CDI 拦截器与 Spring AspectJ

如果你当前的项目使用 Spring，那么考虑 Spring AspectJ 是一个不错的选择。

如果你使用的是成熟的应用程序服务器，或者你的项目不使用 Spring(或其他框架，例如 Google Guice)并且严格来说是 Jakarta EE，那么除了选择 CDI 拦截器之外别无选择。

## 10.总结

在本文中，我们介绍了拦截器模式的两种实现：CDI 拦截器和 Spring AspectJ。我们已经介绍了它们各自的优点和缺点。