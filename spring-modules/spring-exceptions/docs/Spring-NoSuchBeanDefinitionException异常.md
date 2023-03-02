## 1. 概述

在本教程中，我们讨论Spring org.springframework.beans.factory.NoSuchBeanDefinitionException。

这是BeanFactory在尝试解析Spring上下文中未定义的bean时抛出的常见异常。

我们将说明此问题的可能原因和可用的解决方案。

当然，异常会在我们最意想不到的时候发生，因此请查看[Spring中异常和解决方案的完整列表](https://www.baeldung.com/spring-exceptions)。

### 延伸阅读

### [Spring异常教程](https://www.baeldung.com/spring-exceptions)

Spring中一些最常见的异常及其示例-为什么会发生以及如何快速解决它们。

[阅读更多](https://www.baeldung.com/spring-exceptions)→

### [Spring BeanCreationException](https://www.baeldung.com/spring-beancreationexception)

处理Spring BeanCreationException不同原因的快速实用指南

[阅读更多](https://www.baeldung.com/spring-beancreationexception)→

## 2. 原因：没有找到依赖类型[...]的合格Bean

此异常的最常见原因只是试图注入未定义的bean。

例如，BeanB正在连接一个协作者BeanA：

```java
@Component
public class BeanA {

    @Autowired
    private BeanB dependency;
    //...
}
```

现在，如果依赖BeanB没有在Spring上下文中定义，引导过程将失败并出现no such bean definition异常：

```bash
org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type [cn.tuyucheng.taketoday.packageB.BeanB]
  found for dependency: 
expected at least 1 bean which qualifies as
  autowire candidate for this dependency. 
Dependency annotations: 
  {@org.springframework.beans.factory.annotation.Autowired(required=true)}
```

Spring清楚地指出了原因：expected at least 1 bean which qualified as autowire candidate for this dependency。

BeanB可能不存在于上下文中的一个原因是——如果bean被类路径扫描自动拾取，并且如果BeanB被正确地注解为一个bean(@Component、@Repository、@Service、@Controller等)-是它可能在Spring未扫描的包中定义：

```java
package cn.tuyucheng.taketoday.packageB;
@Component
public class BeanB { ...}
```

并且类路径扫描可以配置如下：

```java
@Configuration
@ComponentScan("cn.tuyucheng.taketoday.packageA")
public class ContextWithJavaConfig {
    // ...
}
```

如果beans不是自动扫描的而是手动定义的，那么BeanB根本就没有在当前的Spring上下文中定义。

## 3. 原因：[...]中的字段[...]需要类型为[...]的Bean，但找不到

在上述场景的Spring Boot应用程序中，我们得到了不同的消息。

让我们以相同的示例为例，其中BeanB连接到BeanA中，但未定义：

```java
@Component
public class BeanA {
	
    @Autowired
    private BeanB dependency;
    //...
}
```

如果我们尝试运行这个简单的应用程序，它会尝试加载BeanA：

```java
@SpringBootApplication
public class NoSuchBeanDefinitionDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(NoSuchBeanDefinitionDemoApp.class, args);
    }
}
```

应用程序将无法启动并显示以下错误消息：

```shell
APPLICATION FAILED TO START


Description:

Field dependency in cn.tuyucheng.taketoday.springbootmvc.nosuchbeandefinitionexception.BeanA required a bean of type 'cn.tuyucheng.taketoday.springbootmvc.nosuchbeandefinitionexception.BeanB' that could not be found.


Action:

Consider defining a bean of type 'cn.tuyucheng.taketoday.springbootmvc.nosuchbeandefinitionexception.BeanB' in your configuration.
```

这里cn.tuyucheng.taketoday.springbootmvc.nosuchbeandefinitionexception是BeanA、BeanB和NoSuchBeanDefinitionDemoApp的包。

此示例的代码片段可在[此GitHub项目](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-mvc)中找到。

## 4. 原因：没有定义[...]类型的合格Bean

异常的另一个原因是上下文中存在两个bean定义，而不是一个。

假设一个接口IBeanB由两个bean实现，BeanB1和BeanB2：

```java
@Component
public class BeanB1 implements IBeanB {
    //
}
@Component
public class BeanB2 implements IBeanB {
    //
}
```

现在如果BeanA自动装配这个接口，Spring将不知道要注入两个实现中的哪一个：

```java
@Component
public class BeanA {

    @Autowired
    private IBeanB dependency;
    // ...
}
```

同样，这将导致BeanFactory抛出NoSuchBeanDefinitionException：

```bash
Caused by: org.springframework.beans.factory.NoUniqueBeanDefinitionException: 
No qualifying bean of type 
  [cn.tuyucheng.taketoday.packageB.IBeanB] is defined: 
expected single matching bean but found 2: beanB1,beanB2
```

同样，Spring明确指出了布线失败的原因：预期单个匹配的bean但找到了2个。

但是，请注意，在这种情况下，抛出的确切异常不是NoSuchBeanDefinitionException而是一个子类：NoUniqueBeanDefinitionException。[正是出于这个原因，在Spring 3.2.1中引入了](https://jira.springsource.org/browse/SPR-10194)这个新异常-区分没有找到bean定义的原因和在上下文中找到多个定义的原因。

在此更改之前，这是上述异常：

```bash
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type [cn.tuyucheng.taketoday.packageB.IBeanB] is defined: 
expected single matching bean but found 2: beanB1,beanB2
```

此问题的一种解决方案是使用@Qualifier注解来准确指定我们要连接的bean的名称：

```java
@Component
public class BeanA {

    @Autowired
    @Qualifier("beanB2")
    private IBeanB dependency;
    // ...
}
```

现在Spring有足够的信息来决定注入哪个bean-BeanB1或BeanB2(BeanB2的默认名称是beanB2)。

## 5. 原因：没有定义名为[...]的Bean

当从Spring上下文中按名称请求未定义的bean时，也可能抛出NoSuchBeanDefinitionException：

```java
@Component
public class BeanA implements InitializingBean {

    @Autowired
    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() {
        context.getBean("someBeanName");
    }
}
```

在这种情况下，“someBeanName”没有bean定义，导致以下异常：

```bash
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No bean named 'someBeanName' is defined
```

同样，Spring清楚而简洁地指出了失败的原因：No bean named X is defined。

## 6. 原因：被代理的Bean

当使用JDK动态代理机制代理上下文中的bean时，代理不会扩展目标bean(但它会实现相同的接口)。

因此，如果bean由接口注入，它将被正确连接。但是，如果bean由实际类注入，Spring将找不到与该类匹配的bean定义，因为代理实际上没有扩展班上。

bean可能被代理的一个非常常见的原因是Spring事务支持，即用@Transactional注解的bean。

例如，如果ServiceA注入ServiceB，并且两个服务都是事务性的，则通过类定义注入将不起作用：

```java
@Service
@Transactional
public class ServiceA implements IServiceA{

    @Autowired
    private ServiceB serviceB;
    // ...
}

@Service
@Transactional
public class ServiceB implements IServiceB{
    // ...
}
```

同样的两个服务，这次通过interface正确注入，就没问题了：

```java
@Service
@Transactional
public class ServiceA implements IServiceA{

    @Autowired
    private IServiceB serviceB;
    // ...
}

@Service
@Transactional
public class ServiceB implements IServiceB{
    // ...
}
```

## 7. 总结

本文讨论了常见NoSuchBeanDefinitionException的可能原因的示例-重点是如何在实践中解决这些异常。

最后，Spring中的[异常和解决方案的完整列表](https://www.baeldung.com/spring-exceptions)可能是一个很好的收藏资源。