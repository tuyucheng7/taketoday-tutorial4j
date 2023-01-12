## 1. 概述

在本文中，我们将全面介绍Spring的@Component注解。
我们介绍使用它来与一些核心Spring功能集成的不同方式，以及如何利用它的许多好处。

## 2. Spring ApplicationContext

在我们讲解@Component的之前，
我们首先需要对[Spring ApplicationContext](../../spring-core-4/docs/Spring_ApplicationContext.md)有一定的了解。

ApplicationContext是Spring保存已识别为自动管理和分发的对象实例的地方，这些被称为Bean。

Bean管理和依赖注入是Spring的一些主要特性。

使用控制反转原理，**Spring从我们的应用程序中收集bean实例，并在适当的时候使用它们**。
我们可以向Spring提供bean依赖关系，而无需处理这些对象的设置和实例化。

使用@Autowired等注解将Spring管理的bean注入我们的应用程序的能力是在Spring中创建强大且可扩展的代码的驱动力。

那么，我们如何告诉Spring我们希望它为我们管理的bean呢？
**我们应该利用Spring的自动bean检测，在类上使用原型注解**。

## 3. @Component

@Component是一个注解，它允许Spring自动检测我们的自定义bean。

换句话说，无需编写任何显式代码，Spring将：

+ 扫描我们的应用程序，寻找带有@Component注解的类。
+ 实例化它们并将任何指定的依赖项注入其中。
+ 在需要的地方注入它们。

但是，大多数开发人员更喜欢使用更专用的构造型注解来提供此功能。

### 3.1 Spring原型注解

Spring提供了一些专门的原型注解：@Controller、@Service和@Repository。它们都提供与@Component相同的功能。

**它们的行为都是一样的，因为它们都是以@Component作为每个注解的元注解的组合注解**。
它们类似与@Component的别名，在Spring自动检测或依赖注入之外具有特殊用途和含义。

如果我们想，理论上我们可以选择专门使用@Component来满足我们的bean自动检测需求。
另一方面，我们也可以使用@Component编写自己的专用注解。

但是，Spring的其他功能专门寻找Spring的专用注解以提供额外的自动化优势。
**因此，我们可能应该在大多数情况下坚持使用特定注解**。

假设我们在Spring Boot项目中有上述注解的示例：

```java

@Controller
public class ControllerExample {

}

@Service
public class ServiceExample {

}

@Repository
public class RepositoryExample {

}

@Component
public class ComponentExample {

}

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CustomComponent {

}

@CustomComponent
public class CustomComponentExample {

}
```

我们可以编写一个测试，证明每一个都被Spring自动检测并添加到ApplicationContext中：

```java

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ComponentUnitTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void givenInScopeComponents_whenSearchingInApplicationContext_thenFindThem() {
        assertNotNull(applicationContext.getBean(ControllerExample.class));
        assertNotNull(applicationContext.getBean(ServiceExample.class));
        assertNotNull(applicationContext.getBean(RepositoryExample.class));
        assertNotNull(applicationContext.getBean(ComponentExample.class));
        assertNotNull(applicationContext.getBean(CustomComponentExample.class));
    }
}
```

### 3.2 @ComponentScan

在我们完全依赖@Component之前，我们必须明白它只是一个普通的注解。注解用于将bean与其他对象(例如域对象)区分开来。

然而，**Spring使用@ComponentScan注解将它们全部收集到它的ApplicationContext中**。

如果我们正在编写一个Spring Boot应用程序，我们要知道@SpringBootApplication是一个包含@ComponentScan的组合注解。
只要我们的@SpringBootApplication类在我们项目的根包下，它就会默认扫描我们定义的每一个@Component。

但是，如果我们的@SpringBootApplication类不是位于项目的根包下，或者我们想要扫描外部源，
我们可以显式配置@ComponentScan以扫描我们指定的任何包，只要它存在于类路径中。

让我们定义一个超出范围的@Component bean：

```java
package cn.tuyucheng.taketoday.component.scannedscope;

@Component
public class ScannedScopeExample {

}
```

接下来，我们可以显式将其包含到我们的@ComponentScan注解中：

```java
package cn.tuyucheng.taketoday.component.inscope;

@SpringBootApplication
@ComponentScan({"cn.tuyucheng.taketoday.component.inscope", "cn.tuyucheng.taketoday.component.scannedscope"})
public class ComponentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComponentApplication.class, args);
    }
}
```

最后，我们可以测试它是否存在：

```java

class ComponentUnitTest {

    @Test
    void givenScannedScopeComponent_whenSearchingInApplicationContext_thenFindIt() {
        assertNotNull(applicationContext.getBean(ScannedScopeExample.class));
    }
}
```

实际上，当我们想要扫描项目中包含的外部依赖项时，更有可能发生这种情况。

### 3.3 @Component的局限性

在某些情况下，当我们无法使用@Component时，我们希望某个对象成为Spring管理的bean。

让我们在项目外部的包中定义一个用@Component标注的对象：

```java
package cn.tuyucheng.taketoday.component.outsidescope;

@Component
public class OutsideScopeExample {

}
```

下面是一个证明ApplicationContext不包含OutsideScopeExample bean的测试：

```java

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ComponentUnitTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void givenOutsideScopeComponent_whenSearchingInApplicationContext_thenFail() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(OutsideScopeExample.class));
    }
}
```

此外，我们可能无法访问第三方库的源代码，并且我们无法添加@Component注解。
或者，也许我们希望根据我们运行的环境有条件地使用一个bean实现而不是另一个实现。
**大多数时候自动检测就足够了，但如果不满足，我们可以使用@Bean**。

## 4. @Component与@Bean

@Bean也是Spring在运行时用来收集bean的注解，但它不在类级别使用。
**我们使用@Bean标注方法，以便Spring可以将方法的返回结果配置为Spring bean**。

我们首先创建一个不带任何注解的POJO：

```java
public class BeanExample {

}
```

在用@Configuration标注的类中，我们可以创建一个生成bean的方法：

```java

@Configuration
public class ComponentApplication {

    @Bean
    public BeanExample beanExample() {
        return new BeanExample();
    }
}
```

BeanExample可能是一个项目的本地类，也可能是一个外部类。没关系，因为我们只需要返回它的一个实例。

然后我们可以编写一个测试来验证Spring确实配置了bean：

```java

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ComponentUnitTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void givenBeanComponents_whenSearchingInApplicationContext_thenFindThem() {
        assertNotNull(applicationContext.getBean(BeanExample.class));
    }
}
```

由于@Component和@Bean之间的差异，我们应该注意一些重要的含义。

+ @Component是类级别的注解，而@Bean是方法级别的注解，所以@Component只有在源代码可以编辑的情况下可用。
  而@Bean总是可以使用，但它更冗长。
+ @Component兼容Spring的自动检测，但@Bean需要手动实例化类。
+ 使用@Bean将bean的实例化与其类定义分离。这就是为什么我们可以使用它甚至将第三方类配置成Spring bean。
  这也意味着我们可以引入逻辑来决定bean使用几个可能的实例选项中的哪一个。

## 5. 总结

我们介绍了Spring @Component注解以及其他相关方面。首先，我们讨论了各种Spring原型注解，它们只是@Component的特殊版本。

@Component本身不会做任何事情，除非它可以被@ComponentScan扫描到。

最后，由于不可能在不能修改源代码的类上使用@Component，我们只能使用@Bean注解。