## 1. 概述

在本教程中，我们将讨论Spring org.springframework.beans.factory.BeanCreationException。当BeanFactory创建bean 定义的bean 并遇到问题时抛出一个非常常见的异常。本文将探讨此异常的最常见原因以及解决方案。

## 延伸阅读：

## [Spring 的控制反转和依赖注入简介](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)

快速介绍控制反转和依赖注入的概念，然后使用 Spring 框架进行简单演示

[阅读更多](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)→

## [Spring 中的 BeanNameAware 和 BeanFactoryAware 接口](https://www.baeldung.com/spring-bean-name-factory-aware)

查看在 Spring 中使用 BeanNameAware 和 BeanFactoryAware 接口。

[阅读更多](https://www.baeldung.com/spring-bean-name-factory-aware)→

## [Spring 5 功能 Bean 注册](https://www.baeldung.com/spring-5-functional-beans)

查看如何使用 Spring 5 中的函数式方法注册 bean。

[阅读更多](https://www.baeldung.com/spring-5-functional-beans)→

## 2. 原因：org.springframework.beans.factory.NoSuchBeanDefinitionException

到目前为止，BeanCreationException最常见的原因是 Spring 试图注入上下文中不存在的 bean 。

例如，BeanA试图注入BeanB：

```java
@Component
public class BeanA {

    @Autowired
    private BeanB dependency;
    ...
}
```

如果在上下文中找不到BeanB，则会抛出以下异常(创建 Bean 时出错)：

```bash
Error creating bean with name 'beanA': Injection of autowired dependencies failed; 
nested exception is org.springframework.beans.factory.BeanCreationException: 
Could not autowire field: private com.baeldung.web.BeanB cpm.baeldung.web.BeanA.dependency; 
nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type [com.baeldung.web.BeanB] found for dependency: 
expected at least 1 bean which qualifies as autowire candidate for this dependency. 
Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
```

要诊断此类问题，我们首先要确保已声明 bean：

-   在 XML 配置文件中使用<bean />元素
-   或通过@Bean注解在Java@Configuration类中
-   或者用@Component、@Repository、@Service、@Controller 注解，并且类路径扫描对该包是活动的

我们还将检查 Spring 是否实际获取配置文件或类，并将它们加载到主上下文中。

## 延伸阅读：

## [Spring 的控制反转和依赖注入简介](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)

快速介绍控制反转和依赖注入的概念，然后使用 Spring 框架进行简单演示

[阅读更多](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)→

## [Spring 中的 BeanNameAware 和 BeanFactoryAware 接口](https://www.baeldung.com/spring-bean-name-factory-aware)

查看在 Spring 中使用 BeanNameAware 和 BeanFactoryAware 接口。

[阅读更多](https://www.baeldung.com/spring-bean-name-factory-aware)→

## [Spring 5 功能 Bean 注册](https://www.baeldung.com/spring-5-functional-beans)

查看如何使用 Spring 5 中的函数式方法注册 bean。

[阅读更多](https://www.baeldung.com/spring-5-functional-beans)→

## 3. 原因：org.springframework.beans.factory.NoUniqueBeanDefinitionException

bean 创建异常的另一个类似原因是 Spring 尝试按类型(即按其接口)注入 bean，并在上下文中找到两个或多个实现该接口的 bean 。

例如，BeanB1和BeanB2都实现了相同的接口：

```java
@Component
public class BeanB1 implements IBeanB { ... }
@Component
public class BeanB2 implements IBeanB { ... }

@Component
public class BeanA {

    @Autowired
    private IBeanB dependency;
    ...
}
```

这将导致 Spring bean 工厂抛出以下异常：

```bash
Error creating bean with name 'beanA': Injection of autowired dependencies failed; 
nested exception is org.springframework.beans.factory.BeanCreationException: 
Could not autowire field: private com.baeldung.web.IBeanB com.baeldung.web.BeanA.b; 
nested exception is org.springframework.beans.factory.NoUniqueBeanDefinitionException: 
No qualifying bean of type [com.baeldung.web.IBeanB] is defined: 
expected single matching bean but found 2: beanB1,beanB2
```

## 4. 原因：org.springframework.beans.BeanInstantiationException

### 4.1. 自定义异常

接下来是一个在其创建过程中抛出异常的 bean。一个易于理解问题的简化示例是在 bean 的构造函数中抛出异常：

```java
@Component
public class BeanA {

    public BeanA() {
        super();
        throw new NullPointerException();
    }
    ...
}
```

正如预期的那样，这将导致 Spring 快速失败并出现以下异常：

```bash
Error creating bean with name 'beanA' defined in file [...BeanA.class]: 
Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: 
Could not instantiate bean class [com.baeldung.web.BeanA]: 
Constructor threw exception; 
nested exception is java.lang.NullPointerException
```

### 4.2. java.lang.InstantiationException异常

BeanInstantiationException的另一种可能出现是将抽象类定义为 XML 中的 bean；这必须在 XML 中，因为在Java@Configuration文件中无法做到这一点，类路径扫描将忽略抽象类：

```java
@Component
public abstract class BeanA implements IBeanA { ... }
```

这是 bean 的 XML 定义：

```xml
<bean id="beanA" class="com.baeldung.web.BeanA" />
```

此设置将导致类似的异常：

```bash
org.springframework.beans.factory.BeanCreationException: 
Error creating bean with name 'beanA' defined in class path resource [beansInXml.xml]: 
Instantiation of bean failed; 
nested exception is org.springframework.beans.BeanInstantiationException: 
Could not instantiate bean class [com.baeldung.web.BeanA]: 
Is it an abstract class?; 
nested exception is java.lang.InstantiationException
```

### 4.3. java.lang.NoSuchMethodException异常

如果 bean 没有默认构造函数，而 Spring 试图通过查找该构造函数来实例化它，这将导致运行时异常：

```java
@Component
public class BeanA implements IBeanA {

    public BeanA(final String name) {
        super();
        System.out.println(name);
    }
}
```

当类路径扫描机制拾取这个 bean 时，失败将是：

```bash
Error creating bean with name 'beanA' defined in file [...BeanA.class]: Instantiation of bean failed; 
nested exception is org.springframework.beans.BeanInstantiationException: 
Could not instantiate bean class [com.baeldung.web.BeanA]: 
No default constructor found; 
nested exception is java.lang.NoSuchMethodException: com.baeldung.web.BeanA.<init>()
```

当类路径上的 Spring 依赖项没有相同的版本时，可能会发生类似的异常，但更难诊断。由于 API 更改，这种版本不兼容可能会导致NoSuchMethodException 。解决此类问题的方法是确保所有 Spring 库在项目中具有完全相同的版本。

## 5. 原因：org.springframework.beans.NotWritablePropertyException

另一种可能性是定义一个 bean BeanA，并引用另一个 bean BeanB ，而BeanA 中没有相应的 setter 方法：

```java
@Component
public class BeanA {
    private IBeanB dependency;
    ...
}
@Component
public class BeanB implements IBeanB { ... }
```

这是 Spring XML 配置：

```xml
<bean id="beanA" class="com.baeldung.web.BeanA">
    <property name="beanB" ref="beanB" />
</bean>
```

同样，这只会发生在 XML 配置中 ，因为在使用Java@Configuration时，编译器将无法重现此问题。

当然，为了解决这个问题，我们需要为IBeanB添加setter ：

```java
@Component
public class BeanA {
    private IBeanB dependency;

    public void setDependency(final IBeanB dependency) {
        this.dependency = dependency;
    }
}
```

## 6. 原因：org.springframework.beans.factory.CannotLoadBeanClassException

Spring 无法加载定义的 bean 的类时抛出此异常。如果 Spring XML 配置包含一个根本没有对应类的 bean，则可能会发生这种情况。例如，如果类BeanZ不存在，则以下定义将导致异常：

```xml
<bean id="beanZ" class="com.baeldung.web.BeanZ" />
```

ClassNotFoundException的根本原因和本例中的完整异常是：

```bash
nested exception is org.springframework.beans.factory.BeanCreationException: 
...
nested exception is org.springframework.beans.factory.CannotLoadBeanClassException: 
Cannot find class [com.baeldung.web.BeanZ] for bean with name 'beanZ' 
defined in class path resource [beansInXml.xml]; 
nested exception is java.lang.ClassNotFoundException: com.baeldung.web.BeanZ
```

## 7. BeanCreationException的孩子

### 7.1. org.springframework.beans.factory.BeanCurrentlyInCreationException _

BeanCreationException的子类之一是BeanCurrentlyInCreationException。这通常发生在使用构造函数注入时，例如，在循环依赖的情况下：

```java
@Component
public class BeanA implements IBeanA {
    private IBeanB beanB;

    @Autowired
    public BeanA(final IBeanB beanB) {
        super();
        this.beanB = beanB;
    }
}
@Component
public class BeanB implements IBeanB {
    final IBeanA beanA;

    @Autowired
    public BeanB(final IBeanA beanA) {
        super();
        this.beanA = beanA;
    }
}
```

Spring 无法解决这种布线场景，最终结果将是：

```bash
org.springframework.beans.factory.BeanCurrentlyInCreationException: 
Error creating bean with name 'beanA': 
Requested bean is currently in creation: Is there an unresolvable circular reference?
```

完整的异常非常冗长：

```bash
org.springframework.beans.factory.UnsatisfiedDependencyException: 
Error creating bean with name 'beanA' defined in file [...BeanA.class]: 
Unsatisfied dependency expressed through constructor argument with index 0 
of type [com.baeldung.web.IBeanB]: : 
Error creating bean with name 'beanB' defined in file [...BeanB.class]: 
Unsatisfied dependency expressed through constructor argument with index 0 
of type [com.baeldung.web.IBeanA]: : 
Error creating bean with name 'beanA': Requested bean is currently in creation: 
Is there an unresolvable circular reference?; 
nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: 
Error creating bean with name 'beanA': 
Requested bean is currently in creation: 
Is there an unresolvable circular reference?; 
nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: 
Error creating bean with name 'beanB' defined in file [...BeanB.class]: 
Unsatisfied dependency expressed through constructor argument with index 0 
of type [com.baeldung.web.IBeanA]: : 
Error creating bean with name 'beanA': 
Requested bean is currently in creation: 
Is there an unresolvable circular reference?; 
nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: 
Error creating bean with name 'beanA': 
Requested bean is currently in creation: Is there an unresolvable circular reference?
```

### 7.2. org.springframework.beans.factory.BeanIsAbstractException _

当 Bean Factory 试图检索和实例化一个声明为抽象的 bean 时，可能会发生此实例化异常：

```java
public abstract class BeanA implements IBeanA {
   ...
}
```

我们在 XML 配置中将其声明为：

```xml
<bean id="beanA" abstract="true" class="com.baeldung.web.BeanA" />
```

如果我们尝试通过名称从 Spring 上下文中检索BeanA ，就像在实例化另一个 bean 时：

```java
@Configuration
public class Config {
    @Autowired
    BeanFactory beanFactory;

    @Bean
    public BeanB beanB() {
        beanFactory.getBean("beanA");
        return new BeanB();
    }
}
```

这将导致以下异常：

```bash
org.springframework.beans.factory.BeanIsAbstractException: 
Error creating bean with name 'beanA': Bean definition is abstract
```

以及完整的异常堆栈跟踪：

```bash
org.springframework.beans.factory.BeanCreationException: 
Error creating bean with name 'beanB' defined in class path resource 
[org/baeldung/spring/config/WebConfig.class]: Instantiation of bean failed; 
nested exception is org.springframework.beans.factory.BeanDefinitionStoreException: 
Factory method 
[public com.baeldung.web.BeanB com.baeldung.spring.config.WebConfig.beanB()] threw exception; 
nested exception is org.springframework.beans.factory.BeanIsAbstractException: 
Error creating bean with name 'beanA': Bean definition is abstract
```

## 八. 总结

在本文中，我们了解了如何解决可能导致 Spring 中的BeanCreationException的各种原因和问题，并很好地掌握了如何解决所有这些问题。

所有异常示例的实现都可以在[github项目](https://github.com/eugenp/tutorials/tree/master/spring-exceptions)中找到。这是一个基于 Eclipse 的项目，因此它应该很容易导入和运行。