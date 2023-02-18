## 一、概述

单例对象经常被需要单个实例的开发人员使用，该实例旨在被应用程序中的许多对象重用。**在 Spring 中，我们可以通过使用 Spring 的单例 bean 或自己实现单例设计模式来**创建它们。

在本教程中，我们将首先了解单例设计模式及其线程安全实现。然后，我们将查看 Spring 中的单例 bean 范围，并将单例 bean 与使用单例设计模式创建的对象进行比较。

最后，我们将看看一些可能的最佳实践。

## 2.单例设计模式

[Singleton 是四人帮](https://www.baeldung.com/creational-design-patterns)于 1994 年发布的最简单的设计模式之一。它被分组在创建模式下，因为**单例提供了一种创建只有一个实例的对象的方法**。

### 2.1. 模式定义

单例模式涉及**负责创建对象并确保只创建一个实例的单个类**。我们经常使用单例来共享状态或避免设置多个对象的成本。

单例模式实现通过执行以下操作确保仅创建一个实例：

-   通过实现单个[私有构造函数隐藏所有构造函数](https://www.baeldung.com/java-private-constructors#:~:text=Private constructors allow us to,is known as constructor delegation.)
-   仅在实例不存在时创建实例并将其存储在私有 [静态变量中](https://www.baeldung.com/java-static)
-   使用公共静态[getter提供对该实例的简单访问](https://www.baeldung.com/java-why-getters-setters)

让我们看一个使用单例对象的几个类的例子：

[![单例设计模式类图](https://www.baeldung.com/wp-content/uploads/2023/02/singleton_design_pattern3.png)](https://www.baeldung.com/wp-content/uploads/2023/02/singleton_design_pattern3.png)

在上面的类图中，我们可以看到多个服务如何使用同一个只创建一次的单例实例。

### 2.2. 惰性初始化

单例模式实现通常使用**惰性初始化来延迟实例创建，直到第一次实际需要它时**。为了确保延迟实例化，我们可以在首次调用静态 getter 时创建一个实例：

```java
public final class ThreadSafeSingleInstance {

    private static volatile ThreadSafeSingleInstance instance = null;

    private ThreadSafeSingleInstance() {}

    public static ThreadSafeSingleInstance getInstance() {
        if (instance == null) {
            synchronized(ThreadSafeSingleInstance.class) {
                if (instance == null) {
                    instance = new ThreadSafeSingleInstance();
                }
            }
        }
        return instance;
    }

    //standard getters

}复制
```

在多线程应用程序中，惰性实例化会导致[竞争条件](https://www.baeldung.com/java-common-concurrency-pitfalls)。因此，我们还应用了[双重检查锁定](https://www.baeldung.com/java-singleton-double-checked-locking)来防止不同线程创建多个实例。

## 3. Spring中的单例Bean

Spring 框架中的 bean 是在**[Spring IoC Container](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)****中创建、管理和销毁的**[对象](https://www.baeldung.com/spring-bean)。

### 3.1. Bean 作用域

[使用 Spring bean，我们可以使用控制反转](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)(IoC)通过元数据将对象注入到 Spring 容器中。实际上，一个**对象可以在不创建它们的情况下定义它的依赖关系，并将该工作委托给 IoC Container**。

最新版本的 Spring 框架定义了[六种作用域：](https://www.baeldung.com/spring-bean-scopes)

-   单例
-   原型
-   要求
-   会议
-   应用
-   网络套接字

bean 的范围定义了它的生命周期和可见性。它还确定将如何创建 bean 的实际实例。例如，我们可能希望在每次请求 bean 时创建一个全局实例或不同的实例。

### 3.2. 单例 Bean

我们可以使用位于配置类中的[*@Bean*注释](https://www.baeldung.com/spring-bean-annotations)在 Spring 中声明 bean 。Spring 中的单例范围**在容器中为每个 bean 标识符创建一个 bean**：

```java
@Configuration
public class SingletonBeanConfig {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SingletonBean singletonBean() {
        return new SingletonBean();
    }

}复制
```

单例是Spring中定义的所有bean的默认作用域。因此，即使我们没有使用*@Scope*注释指定特定范围，我们仍然会得到一个单例 bean。此处包含的范围仅用于说明目的。它通常用于表示其他可用范围。

### 3.3. Bean 标识符

与纯单例设计模式不同，我们可以**从同一个类创建多个单例 bean**：

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
}复制
```

对具有匹配标识符的 bean 的所有请求都将导致框架返回一个特定的 bean 实例。 当我们在方法上使用*@Bean注解时，Spring 使用方法名作为*[bean 标识符](https://www.baeldung.com/spring-bean-names)。

注入 bean 时，如果容器中存在多个相同类型的 bean，则框架会抛出 *NoUniqueBeanDefinitionException*：

```java
@Autowired
private SingletonBeanConfig.SingletonBean bean; //throws exception复制
```

在这种情况下，我们可以使用[*@Qualifier*注释](https://www.baeldung.com/spring-qualifier-annotation)来指定要注入的正确 bean 标识符：

```java
@Autowired
@Qualifier("singletonBean")
private SingletonBeanConfig.SingletonBean beanOne;

@Autowired
@Qualifier("anotherSingletonBean")
private SingletonBeanConfig.SingletonBean beanThree;复制
```

或者，当存在多个相同类型的 bean 时，可以使用另一个注解 – [*@Primary – 来定义主 bean。*](https://www.baeldung.com/spring-primary)

## 4.比较

现在让我们比较这两种方法并确定在 Spring 中遵循的最佳实践。

### 4.1. 单例**反模式**

有些人认为单例是一种**反模式，因为它引入了应用程序级全局状态。**使用单例的任何其他对象都直接依赖于它。这会导致类和模块之间不必要的相互依赖。

单例模式也违反了单一职责原则。由于单例对象至少负责两件事：

-   确保只创建一个实例
-   执行他们的正常操作

此外，单例需要在多线程环境中进行特殊处理，以确保单独的线程不会创建多个实例。它们还可能使单元测试和模拟变得更加困难。由于许多模拟框架依赖于继承，私有构造函数使得单例对象难以模拟。

### 4.2. 推荐方法

使用 Spring 的单例 bean 而不是实现单例设计模式可以消除上述许多缺点。

**Spring 框架** **在所有使用它的类中注入一个 bean，但保留替换或扩展它的灵活性**。该框架通过保持对 bean 生命周期的控制来实现这一点。因此，以后可以将其替换为另一种方法，而无需更改任何代码。

此外，Spring bean 使单元测试更加简单。Spring bean 很容易模拟，框架可以将它们注入测试类。我们可以选择注入实际的 bean 实现或它们的模拟。

我们应该注意，单例 bean 不会只创建一个类的一个实例，而是在容器中为每个 bean 标识符创建一个 bean。

## 5.结论

在本文中，我们探讨了如何在 Spring 框架中创建单例实例。我们着眼于实现单例设计模式，以及使用 Spring 的单例 bean。

我们探讨了如何实现具有延迟加载和线程安全的单例模式。然后我们研究了 Spring 中的单例 bean 范围，并探索了如何实现和注入单例 bean。我们还看到了单例 bean 如何区别于使用单例设计模式创建的对象。

最后，我们了解了如何在 Spring 中使用单例 bean 来消除传统单例设计模式实现的一些缺点。