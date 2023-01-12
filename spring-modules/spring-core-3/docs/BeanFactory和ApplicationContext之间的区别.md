## 1. 概述

Spring框架带有两个IOC容器 - BeanFactory和ApplicationContext。
BeanFactory是IOC容器的最基本版本，ApplicationContext扩展了BeanFactory的特性。

在本文中，我们将通过实际示例了解这两个IOC容器之间的差异。

## 2. 延迟加载与快速加载

**BeanFactory按需加载bean，而ApplicationContext在启动时加载所有bean**。
因此，与ApplicationContext相比，BeanFactory是轻量级的。让我们通过一个例子来理解它。

### 2.1 使用BeanFactory延迟加载

假设我们有一个名为Student的单例bean类，它有一个方法：

```java
public class Student {
    private static boolean isBeanInstantiated = false;

    public void postConstruct() {
        setBeanInstantiated(true);
    }
    // setters and getters ...
}
```

我们将在BeanFactory配置文件ioc-container-difference-example.xml中将postConstruct()方法定义为init-method：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <bean id="student" class="cn.tuyucheng.taketoday.ioccontainer.bean.Student" init-method="postConstruct"/>
</beans>
```

现在，让我们编写一个测试用例，创建一个BeanFactory来检查它是否加载了Student bean：

```java
class IOCContainerAppUnitTest {

    @BeforeEach
    @AfterEach
    void resetInstantiationFlag() {
        Student.setBeanInstantiated(false);
    }

    @Test
    void whenBFInitialized_thenStudentNotInitialized() {
        Resource res = new ClassPathResource("ioc-container-difference-example.xml");
        BeanFactory factory = new XmlBeanFactory(res);
        assertFalse(Student.isBeanInstantiated());
    }
}
```

在这里，**Student对象没有被初始化**。换句话说，**只有BeanFactory被初始化**。
只有当我们显式调用getBean()方法时，我们的BeanFactory中定义的bean才会被加载：

```java
class IOCContainerAppUnitTest {

    @Test
    void whenBFInitialized_thenStudentInitialized() {
        Resource res = new ClassPathResource("ioc-container-difference-example.xml");
        BeanFactory factory = new XmlBeanFactory(res);
        Student student = (Student) factory.getBean("student");
        assertTrue(Student.isBeanInstantiated());
    }
}
```

在这里，Student bean加载成功。因此，BeanFactory仅在需要时才加载bean。

### 2.2 使用ApplicationContext快速加载

现在，让我们使用ApplicationContext代替BeanFactory。

我们将只定义ApplicationContext，它会通过使用急切加载策略立即加载所有bean：

```java
class IOCContainerAppUnitTest {

    @Test
    void whenAppContInitialized_thenStudentInitialized() {
        ApplicationContext context = new ClassPathXmlApplicationContext("ioc-container-difference-example.xml");
        assertTrue(Student.isBeanInstantiated());
    }
}
```

**在这里，即使我们没有调用getBean()方法，也会创建Student对象**。

ApplicationContext被认为是一个重量级IOC容器，因为它的预先加载策略会在启动时加载所有bean。
相比之下，BeanFactory是轻量级的，可以在内存受限的系统中使用。
**尽管如此，我们将在下一节中看到为什么ApplicationContext是大多数用例的首选**。

## 3. 企业应用功能

ApplicationContext以更加面向框架的风格增强了BeanFactory，并提供了一些适用于企业应用程序的特性。

例如，它提供**消息传递(i18n或国际化)**功能、**事件发布**功能、**基于注解的依赖注入**以及与**Spring AOP功能的轻松集成**。

除此之外，ApplicationContext支持几乎所有类型的bean作用域，但BeanFactory只支持两种作用域 - Singleton和Prototype。
因此，在构建复杂的企业应用程序时，最好使用ApplicationContext。

## 4. BeanFactoryPostProcessor和BeanPostProcessor的自动注册

**ApplicationContext在启动时会自动注册BeanFactoryPostProcessor和BeanPostProcessor**。而BeanFactory不会自动注册这些接口。

### 4.1 BeanFactory注册

为了理解，让我们编写两个类。

首先，CustomBeanFactoryPostProcessor类，它实现了BeanFactoryPostProcessor：

```java
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private static boolean isBeanFactoryPostProcessorRegistered = false;

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) {
        setBeanFactoryPostProcessorRegistered(true);
    }
    // setters and getters ...
}
```

在这里，我们重写了postProcessBeanFactory()方法来检查它的注册。

其次，CustomBeanPostProcessor类，它实现了BeanPostProcessor：

```java
public class CustomBeanPostProcessor implements BeanPostProcessor {
    private static boolean isBeanPostProcessorRegistered = false;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        setBeanPostProcessorRegistered(true);
        return bean;
    }
    // setters and getters ...
}
```

在这里，我们重写了postProcessBeforeInitialization()方法来检查它的注册。

此外，我们在ioc-container-difference-example.xml配置文件中配置这两个类：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <bean id="customBeanPostProcessor" class="cn.tuyucheng.taketoday.ioccontainer.bean.CustomBeanPostProcessor"/>
    <bean id="customBeanFactoryPostProcessor"
          class="cn.tuyucheng.taketoday.ioccontainer.bean.CustomBeanFactoryPostProcessor"/>
</beans>
```

让我们看一个测试用例来检查这两个类是否在启动时自动注册：

```java
class IOCContainerAppUnitTest {

    @Test
    void whenBFInitialized_thenBFPProcessorAndBPProcessorNotRegAutomatically() {
        Resource res = new ClassPathResource("ioc-container-difference-example.xml");
        ConfigurableListableBeanFactory factory = new XmlBeanFactory(res);
        assertFalse(CustomBeanFactoryPostProcessor.isBeanFactoryPostProcessorRegistered());
        assertFalse(CustomBeanPostProcessor.isBeanPostProcessorRegistered());
    }
}
```

从我们的测试中可以看出，**没有发生自动注册**。

**现在，让我们看一个在BeanFactory中手动注册它们的测试用例**：

```java
class IOCContainerAppUnitTest {

    @Test
    void whenBFPostProcessorAndBPProcessorRegisteredManually_thenReturnTrue() {
        Resource res = new ClassPathResource("ioc-container-difference-example.xml");
        ConfigurableListableBeanFactory factory = new XmlBeanFactory(res);

        CustomBeanFactoryPostProcessor beanFactoryPostProcessor = new CustomBeanFactoryPostProcessor();
        beanFactoryPostProcessor.postProcessBeanFactory(factory);
        assertTrue(CustomBeanFactoryPostProcessor.isBeanFactoryPostProcessorRegistered());

        CustomBeanPostProcessor beanPostProcessor = new CustomBeanPostProcessor();
        factory.addBeanPostProcessor(beanPostProcessor);
        Student student = (Student) factory.getBean("student");
        assertTrue(CustomBeanPostProcessor.isBeanPostProcessorRegistered());
    }
}
```

在这里，我们使用postProcessBeanFactory()方法注册CustomBeanFactoryPostProcessor和addBeanPostProcessor()
方法注册CustomBeanPostProcessor。在这种情况下，他们都成功注册。

### 4.2 ApplicationContext注册

正如我们前面提到的，ApplicationContext会自动注册这两个类，而无需编写额外的代码。

让我们在单元测试中验证此行为：

```java
class IOCContainerAppUnitTest {

    @Test
    void whenAppContInitialized_thenBFPostProcessorAndBPostProcessorRegisteredAutomatically() {
        ApplicationContext context = new ClassPathXmlApplicationContext("ioc-container-difference-example.xml");
        assertTrue(CustomBeanFactoryPostProcessor.isBeanFactoryPostProcessorRegistered());
        assertTrue(CustomBeanPostProcessor.isBeanPostProcessorRegistered());
    }
}
```

在这种情况下，**两个类都是成功的自动注册**。

因此，**始终建议使用ApplicationContext**，因为Spring 2.0(及更高版本)大量使用BeanPostProcessor。

还值得注意的是，如果你使用的是普通的BeanFactory，那么事务和AOP等功能将不会生效(至少在不编写额外代码的情况下不会生效)。
这可能会导致混淆，因为配置看起来没有任何问题。

## 5. 总结

在本文中，我们通过实际测试了解了ApplicationContext和BeanFactory之间的主要区别。

ApplicationContext带有高级功能，包括一些面向企业应用程序的功能，而BeanFactory仅带有基本功能。
因此，一般推荐使用ApplicationContext，并且**只有在内存消耗非常关键的情况下才应该使用BeanFactory**。