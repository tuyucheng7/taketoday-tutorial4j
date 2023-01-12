## 1. 概述

当我们有多个相同类型的实现时，为Spring bean起一个名字是非常有必要的。
这是因为如果我们的bean没有唯一的名称，那么注入bean对Spring来说是不明确的。

通过控制bean的命名，我们可以告诉Spring要将哪个bean注入目标对象。

在本文中，我们将讨论Spring bean的命名策略，并探讨如何为单一类型的bean赋予多个名称。

## 2. 默认Bean命名策略

Spring为创建bean提供了多个注解。我们可以在不同级别使用这些注解。

首先，让我们看看Spring的默认命名策略。当我们只指定注解而没有任何值时，Spring如何命名我们的bean？

### 2.1 类级别注解

为了命名一个bean，**Spring使用类名并将第一个字母转换为小写**。

让我们看一个例子：

```java

@Service
public class LoggingService {

}
```

在这里，Spring为类LoggingService创建一个bean，并使用名称“loggingService”注册它。

同样的默认命名策略适用于所有用于创建Spring bean的类级别注解，例如@Component、@Service和@Controller。

### 2.2 方法级注解

Spring提供了@Bean和@Qualifier之类的注解，用于标注创建bean的方法。

让我们看一个例子来理解@Bean注解的默认命名策略：

```java

@Configuration
public class AuditConfiguration {

    @Bean
    public AuditService audit() {
        return new AuditService();
    }
}
```

在这个配置类中，Spring注册了一个名为“audit”的AuditService类型的bean。
**因为当我们在方法上使用@Bean注解时，Spring使用方法名称作为bean名称**。

我们还可以在方法上使用@Qualifier注解。

## 3. bean的自定义命名

当我们需要在同一个Spring上下文中创建多个相同类型的bean时，我们可以为这些bean提供自定义名称并使用这些名称来引用它们。

那么，让我们看看如何给我们的Spring bean一个自定义名称：

```java

@Component("myBean")
public class CustomComponent {

}
```

这一次，Spring将创建名为“myBean”的CustomComponent类型的bean。

当我们显示地为bean命名时，Spring将使用这个名称，然后可以使用它来引用或访问bean。

与@Component("myBean")类似，我们可以使用@Service("myService")、@Controller("myController")、
@Bean("myCustomBean")等其他注解来指定名称，然后Spring会注册具有给定名称的bean。

## 4. 使用@Bean和@Qualifier命名Bean

### 4.1 @Bean的value属性

正如我们之前看到的，@Bean注解是在方法级别使用的，默认情况下，Spring使用方法名作为bean名称。

这个默认的bean名称可以被覆盖，我们可以使用@Bean注解指定value属性：

```java

@Configuration("configuration")
public class MyConfiguration {

    @Bean("beanComponent")
    public CustomComponent myComponent() {
        return new CustomComponent();
    }
}
```

在这种情况下，当我们想要获取CustomComponent类型的bean时，我们可以使用名称“beanComponent”来引用这个bean。

Spring @Bean注解通常在配置类方法中声明。它可以通过直接调用同一类中的其他@Bean方法来引用它们。

### 4.2 @Qualifier的value属性

我们也可以使用@Qualifier注解来命名bean。

首先，让我们创建一个接口Animal，它将由多个类实现：

```java
public interface Animal {
    String name();
}
```

现在，我们定义一个实现类Cat并为其添加@Qualifier注解，其value属性为“cat”：

```java

@Component
@Qualifier("cat")
public class Cat implements Animal {

    @Override
    public String name() {
        return "Cat";
    }
}
```

然后添加另一个Animal的实现类Dog，并用@Qualifier和value属性“dog”标注它：

```java

@Component
@Qualifier("dog")
public class Dog implements Animal {

    @Override
    public String name() {
        return "Dog";
    }
}
```

现在，让我们编写一个PetShow类，我们可以在其中注入两个不同的Animal实例：

```java

@Service
public class PetShow {
    private final Animal dog;
    private final Animal cat;

    public PetShow(@Qualifier("dog") Animal dog, @Qualifier("cat") Animal cat) {
        this.dog = dog;
        this.cat = cat;
    }

    public Animal getDog() {
        return dog;
    }

    public Animal getCat() {
        return cat;
    }
}
```

在PetShow类中，我们通过在构造函数参数上使用@Qualifier注解注入了Animal类型的两种实现，每个注解的value属性中都有限定的bean名称。
每当我们使用这个限定名时，Spring都会将具有该限定名的bean注入目标bean中。

## 5. 验证Bean名称

到目前为止，我们已经介绍了不同的例子来演示为Spring bean命名。现在的问题是，我们如何验证或测试这一点？

让我们看一个单元测试来验证上述的行为：

```java

@ExtendWith(SpringExtension.class)
class SpringBeanNamingUnitTest {
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.scan("cn.tuyucheng.taketoday.springbean.naming");
        context.refresh();
    }

    @Test
    void givenMultipleImplementationsOfAnimal_whenFieldIsInjectedWithQualifiedName_thenTheSpecificBeanShouldGetInjected() {
        PetShow petShow = (PetShow) context.getBean("petShow");
        assertNotNull(context.getBean("cat"));
        assertNotNull(context.getBean("dog"));

        assertThat(petShow.getCat().getClass()).isEqualTo(Cat.class);
        assertThat(petShow.getDog().getClass()).isEqualTo(Dog.class);
    }
}
```

在这个JUnit测试中，我们在setUp方法中初始化AnnotationConfigApplicationContext，context用于获取bean。

然后我们简单地使用标准断言验证我们的Spring bean的类型匹配。

## 6. 总结

在这篇文章中，我们介绍了默认和自定义Spring bean命名策略。

在我们需要管理多个相同类型的bean的用例中，自定义bean的名字是非常有用的。