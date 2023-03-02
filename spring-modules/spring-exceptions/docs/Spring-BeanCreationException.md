## 1. 概述

在本教程中，我们将讨论Spring org.springframework.beans.factory.BeanCreationException。当BeanFactory创建bean定义的bean并遇到问题时抛出一个非常常见的异常。本文将探讨此异常的最常见原因以及解决方案。

### 延伸阅读

### [Spring的控制反转和依赖注入简介](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)

快速介绍控制反转和依赖注入的概念，然后使用Spring框架进行简单演示

[阅读更多](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)→

### [Spring中的BeanNameAware和BeanFactoryAware接口](https://www.baeldung.com/spring-bean-name-factory-aware)

查看在Spring中使用BeanNameAware和BeanFactoryAware接口。

[阅读更多](https://www.baeldung.com/spring-bean-name-factory-aware)→

### [Spring 5函数Bean注册](https://www.baeldung.com/spring-5-functional-beans)

查看如何使用Spring 5中的函数式方法注册bean。

[阅读更多](https://www.baeldung.com/spring-5-functional-beans)→

## 2. 原因：org.springframework.beans.factory.NoSuchBeanDefinitionException

到目前为止，BeanCreationException最常见的原因是Spring试图注入上下文中不存在的bean。

例如，BeanA试图注入BeanB：

```java
@Component
public class BeanA {

    @Autowired
    private BeanB dependency;
    // ...
}
```

如果在上下文中找不到BeanB，则会抛出以下异常(创建Bean时出错)：

```bash
Error creating bean with name 'beanA': Injection of autowired dependencies failed; 
nested exception is org.springframework.beans.factory.BeanCreationException: 
Could not autowire field: private cn.tuyucheng.taketoday.web.BeanB cpm.baeldung.web.BeanA.dependency; 
nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type [cn.tuyucheng.taketoday.web.BeanB] found for dependency: 
expected at least 1 bean which qualifies as autowire candidate for this dependency. 
Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
```

要诊断此类问题，我们首先要确保已声明bean：

- 在XML配置文件中使用<bean/>元素
- 或通过@Bean注解在Java@Configuration类中
- 或者用@Component、@Repository、@Service、@Controller注解，并且类路径扫描对该包是活动的

我们还将检查Spring是否实际获取配置文件或类，并将它们加载到主上下文中。

## 3. 原因：org.springframework.beans.factory.NoUniqueBeanDefinitionException

bean创建异常的另一个类似原因是Spring尝试按类型(即按其接口)注入bean，并在上下文中找到两个或多个实现该接口的bean。

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

这将导致Spring bean工厂抛出以下异常：

```bash
Error creating bean with name 'beanA': Injection of autowired dependencies failed; 
nested exception is org.springframework.beans.factory.BeanCreationException: 
Could not autowire field: private cn.tuyucheng.taketoday.web.IBeanB cn.tuyucheng.taketoday.web.BeanA.b; 
nested exception is org.springframework.beans.factory.NoUniqueBeanDefinitionException: 
No qualifying bean of type [cn.tuyucheng.taketoday.web.IBeanB] is defined: 
expected single matching bean but found 2: beanB1,beanB2
```

## 4. 原因：org.springframework.beans.BeanInstantiationException

### 4.1 自定义异常

接下来是一个在其创建过程中抛出异常的bean。一个易于理解问题的简化示例是在bean的构造函数中抛出异常：

```java
@Component
public class BeanA {

    public BeanA() {
        super();
        throw new NullPointerException();
    }
    // ...
}
```

正如预期的那样，这将导致Spring快速失败并出现以下异常：

```bash
Error creating bean with name 'beanA' defined in file [...BeanA.class]: 
Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: 
Could not instantiate bean class [cn.tuyucheng.taketoday.web.BeanA]: 
Constructor threw exception; 
nested exception is java.lang.NullPointerException
```

### 4.2 java.lang.InstantiationException异常

BeanInstantiationException的另一种可能出现是将抽象类定义为XML中的bean；这必须在XML中，因为在Java@Configuration文件中无法做到这一点，类路径扫描将忽略抽象类：

```java
@Component
public abstract class BeanA implements IBeanA { ... }
```

这是bean的XML定义：

```xml
<bean id="beanA" class="cn.tuyucheng.taketoday.web.BeanA" />
```

此设置将导致类似的异常：

```bash
org.springframework.beans.factory.BeanCreationException: 
Error creating bean with name 'beanA' defined in class path resource [beansInXml.xml]: 
Instantiation of bean failed; 
nested exception is org.springframework.beans.BeanInstantiationException: 
Could not instantiate bean class [cn.tuyucheng.taketoday.web.BeanA]: 
Is it an abstract class?; 
nested exception is java.lang.InstantiationException
```

### 4.3 java.lang.NoSuchMethodException异常

如果bean没有默认构造函数，而Spring试图通过查找该构造函数来实例化它，这将导致运行时异常：

```java
@Component
public class BeanA implements IBeanA {

    public BeanA(final String name) {
        super();
        System.out.println(name);
    }
}
```

当类路径扫描机制拾取这个bean时，失败将是：

```bash
Error creating bean with name 'beanA' defined in file [...BeanA.class]: Instantiation of bean failed; 
nested exception is org.springframework.beans.BeanInstantiationException: 
Could not instantiate bean class [cn.tuyucheng.taketoday.web.BeanA]: 
No default constructor found; 
nested exception is java.lang.NoSuchMethodException: cn.tuyucheng.taketoday.web.BeanA.<init>()
```

当类路径上的Spring依赖项没有相同的版本时，可能会发生类似的异常，但更难诊断。由于API更改，这种版本不兼容可能会导致NoSuchMethodException。解决此类问题的方法是确保所有Spring库在项目中具有完全相同的版本。

## 5. 原因：org.springframework.beans.NotWritablePropertyException

另一种可能性是定义一个beanBeanA，并引用另一个beanBeanB，而BeanA中没有相应的setter方法：

```java
@Component
public class BeanA {
    private IBeanB dependency;
    ...
}
@Component
public class BeanB implements IBeanB { ... }
```

这是SpringXML配置：

```xml
<bean id="beanA" class="cn.tuyucheng.taketoday.web.BeanA">
    <property name="beanB" ref="beanB" />
</bean>
```

同样，这只会发生在XML配置中，因为在使用Java@Configuration时，编译器将无法重现此问题。

当然，为了解决这个问题，我们需要为IBeanB添加setter：

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

Spring无法加载定义的bean的类时抛出此异常。如果SpringXML配置包含一个根本没有对应类的bean，则可能会发生这种情况。例如，如果类BeanZ不存在，则以下定义将导致异常：

```xml
<bean id="beanZ" class="cn.tuyucheng.taketoday.web.BeanZ" />
```

ClassNotFoundException的根本原因和本例中的完整异常是：

```bash
nested exception is org.springframework.beans.factory.BeanCreationException: 
...
nested exception is org.springframework.beans.factory.CannotLoadBeanClassException: 
Cannot find class [cn.tuyucheng.taketoday.web.BeanZ] for bean with name 'beanZ' 
defined in class path resource [beansInXml.xml]; 
nested exception is java.lang.ClassNotFoundException: cn.tuyucheng.taketoday.web.BeanZ
```

## 7. BeanCreationException的孩子

### 7.1 org.springframework.beans.factory.BeanCurrentlyInCreationException_

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

Spring无法解决这种布线场景，最终结果将是：

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
of type [cn.tuyucheng.taketoday.web.IBeanB]: : 
Error creating bean with name 'beanB' defined in file [...BeanB.class]: 
Unsatisfied dependency expressed through constructor argument with index 0 
of type [cn.tuyucheng.taketoday.web.IBeanA]: : 
Error creating bean with name 'beanA': Requested bean is currently in creation: 
Is there an unresolvable circular reference?; 
nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: 
Error creating bean with name 'beanA': 
Requested bean is currently in creation: 
Is there an unresolvable circular reference?; 
nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: 
Error creating bean with name 'beanB' defined in file [...BeanB.class]: 
Unsatisfied dependency expressed through constructor argument with index 0 
of type [cn.tuyucheng.taketoday.web.IBeanA]: : 
Error creating bean with name 'beanA': 
Requested bean is currently in creation: 
Is there an unresolvable circular reference?; 
nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: 
Error creating bean with name 'beanA': 
Requested bean is currently in creation: Is there an unresolvable circular reference?
```

### 7.2 org.springframework.beans.factory.BeanIsAbstractException

当BeanFactory试图检索和实例化一个声明为抽象的bean时，可能会发生此实例化异常：

```java
public abstract class BeanA implements IBeanA {
   // ...
}
```

我们在XML配置中将其声明为：

```xml
<bean id="beanA" abstract="true" class="cn.tuyucheng.taketoday.web.BeanA" />
```

如果我们尝试通过名称从Spring上下文中检索BeanA，就像在实例化另一个bean时：

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
[public cn.tuyucheng.taketoday.web.BeanB cn.tuyucheng.taketoday.spring.config.WebConfig.beanB()] threw exception; 
nested exception is org.springframework.beans.factory.BeanIsAbstractException: 
Error creating bean with name 'beanA': Bean definition is abstract
```

## 8. 总结

在本文中，我们了解了如何解决可能导致Spring中的BeanCreationException的各种原因和问题，并很好地掌握了如何解决所有这些问题。