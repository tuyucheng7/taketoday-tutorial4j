## 1. 概述

单例对象通常被需要单个实例的开发人员使用，该实例旨在被应用程序中的许多对象重用。在Spring中，我们可以**通过使用Spring的单例bean或自己实现单例设计模式来创建它们**。

在本教程中，我们将首先了解单例设计模式及其线程安全实现。然后，我们将查看Spring中的单例bean作用域，并将单例bean与使用单例设计模式创建的对象进行比较。

最后，我们将介绍一些可能的最佳实践。

## 2. 单例设计模式

Singleton[是四人帮](https://www.baeldung.com/creational-design-patterns)于1994年发布的最简单的设计模式之一。它被分组在创建型模式下，因为**单例提供了一种创建只有一个实例的对象的方法**。

### 2.1 模式定义

单例模式涉及负责创建对象并确保只创建一个类的单个实例。我们经常使用单例来共享状态或避免设置多个对象的成本。

单例模式实现通过执行以下操作确保仅创建一个实例：

-   通过实现单个[私有构造函数](https://www.baeldung.com/java-private-constructors#:~:text=Private%20constructors%20allow%20us%20to,is%20known%20as%20constructor%20delegation.)隐藏所有构造函数
-   仅在实例不存在时创建实例并将其存储在私有[静态变量](https://www.baeldung.com/java-static)中
-   使用公共静态[getter](https://www.baeldung.com/java-why-getters-setters)提供对该实例的简单访问

让我们看一个使用单例对象的几个类的例子：

<img src="../assets/img.png">

在上面的类图中，我们可以看到多个服务如何使用同一个只创建一次的单例实例。

### 2.2 惰性初始化

单例模式实现通常**使用惰性初始化来延迟实例创建，直到第一次实际需要它时**。为了确保延迟实例化，我们可以在首次调用静态getter时创建一个实例：

```java
public final class ThreadSafeSingleInstance {

    private static volatile ThreadSafeSingleInstance instance = null;

    private ThreadSafeSingleInstance() {
    }

    public static ThreadSafeSingleInstance getInstance() {
        if (instance == null) {
            synchronized (ThreadSafeSingleInstance.class) {
                if (instance == null) {
                    instance = new ThreadSafeSingleInstance();
                }
            }
        }
        return instance;
    }

    // standard getters
}
```

在多线程应用程序中，惰性实例化会导致[争用条件](https://www.baeldung.com/java-common-concurrency-pitfalls)。因此，我们还应用了[双重检查锁定](https://www.baeldung.com/java-singleton-double-checked-locking)来防止不同线程创建多个实例。

## 3. Spring中的单例Bean

**Spring框架中的[bean](https://www.baeldung.com/spring-bean)是在[Spring IoC容器](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)中创建、管理和销毁的对象**。

### 3.1 Bean作用域

使用Spring bean，我们可以使用[控制反转](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)(IoC)通过元数据将对象注入到Spring容器中。实际上，**一个对象可以在不创建它们的情况下定义它的依赖关系，并将该工作委托给IoC容器**。

最新版本的Spring框架定义了[六种作用域](https://www.baeldung.com/spring-bean-scopes)：

-   singleton
-   prototype
-   request
-   session
-   application
-   websocket

bean的作用域定义了它的生命周期和可见性。它还确定将如何创建bean的实际实例。例如，我们可能希望在每次请求bean时创建一个全局实例或不同的实例。

### 3.2 单例Bean

我们可以在配置类使用[@Bean注解](https://www.baeldung.com/spring-bean-annotations)在Spring中声明bean。Spring中的单例作用域在容器中**为每个bean标识符创建一个bean**：

```java
@Configuration
public class SingletonBeanConfig {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SingletonBean singletonBean() {
        return new SingletonBean();
    }
}
```

单例是Spring中定义的所有bean的默认作用域。因此，即使我们没有使用@Scope注解指定特定作用域，我们仍然会得到一个单例bean。此处包含的作用域仅用于说明目的。它通常用于表示其他可用作用域。

### 3.3 Bean标识符

与纯单例设计模式不同，我们**可以从同一个类创建多个单例bean**：

```java
@Bean
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public SingletonBean singletonBean() {
    return new SingletonBean();
}

@Bean
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public SingletonBean anotherSingletonBean() {
    return new SingletonBean();
}
```

对具有匹配标识符的bean的所有请求都将导致框架返回一个特定的bean实例。 当我们在方法上使用@Bean注解时，Spring使用方法名作为[bean标识符](https://www.baeldung.com/spring-bean-names)。

注入bean时，如果容器中存在多个相同类型的bean，则框架会抛出NoUniqueBeanDefinitionException：

```java
@Autowired
private SingletonBeanConfig.SingletonBean bean; //throws exception
```

在这种情况下，我们可以使用[@Qualifier注解](https://www.baeldung.com/spring-qualifier-annotation)来指定要注入的正确bean标识符：

```java
@Autowired
@Qualifier("singletonBean")
private SingletonBeanConfig.SingletonBean beanOne;

@Autowired
@Qualifier("anotherSingletonBean")
private SingletonBeanConfig.SingletonBean beanThree;
```

或者，当存在多个相同类型的bean时，可以使用另一个注解[@Primary](https://www.baeldung.com/spring-primary)来定义主bean。

## 4. 比较

现在让我们比较这两种方法并确定在Spring中遵循的最佳实践。

### 4.1 单例反模式

**有些人认为单例是一种反模式，因为它引入了应用程序级全局状态**。使用单例的任何其他对象都直接依赖于它。这会导致类和模块之间不必要的相互依赖。

单例模式也违反了单一职责原则。因为单例对象至少负责了两件事：

-   确保只创建一个实例
-   执行他们的正常操作

此外，单例需要在多线程环境中进行特殊处理，以确保单独的线程不会创建多个实例。它们还可能使单元测试和Mock变得更加困难。由于许多Mock框架依赖于继承，私有构造函数使得单例对象难以Mock。

### 4.2 推荐方法

使用Spring的单例bean而不是实现单例设计模式可以消除上述许多缺点。

**Spring框架在所有使用它的类中注入一个bean，但保留了替换或扩展它的灵活性**。该框架通过保持对bean生命周期的控制来实现这一点。因此，以后可以将其替换为另一种方法，而无需更改任何代码。

此外，Spring bean使单元测试更加简单。Spring bean很容易Mock，框架可以将它们注入到测试类。我们可以选择注入实际的bean实现或它们的Mock版本。

我们应该注意，单例bean不会只创建类的一个实例，而是在容器中为每个bean标识符创建一个bean。

## 5. 总结

在本文中，我们探讨了如何在Spring框架中创建单例实例。我们着眼于实现单例设计模式，以及使用Spring的单例bean。

我们探讨了如何实现具有延迟加载和线程安全的单例模式。然后我们研究了Spring中的单例bean作用域，并探讨了如何实现和注入单例bean。我们还看到了单例bean如何区别于使用单例设计模式创建的对象。

最后，我们了解了如何在Spring中使用单例bean来消除传统单例设计模式实现的一些缺点。