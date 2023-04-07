## **一、概述**

在本教程中，我们将介绍 IoC（控制反转）和 DI（依赖注入）的概念，并了解它们在 Spring 框架中是如何实现的。

## 延伸阅读：

## [Spring 中的连接：@Autowired、@Resource 和@Inject](https://www.baeldung.com/spring-annotations-resource-inject-autowire)

本文将比较和对比依赖注入相关注解的使用，即@Resource、@Inject、@Autowired注解。

[阅读更多](https://www.baeldung.com/spring-annotations-resource-inject-autowire)→

## [Spring 中的@Component 与@Repository 和@Service](https://www.baeldung.com/spring-component-repository-service)

了解 @Component、@Repository 和 @Service 注释之间的区别以及何时使用它们。

[阅读更多](https://www.baeldung.com/spring-component-repository-service)→

## **2.什么是控制反转？**

控制反转是软件工程中的一个原则，它将对象或程序部分的控制转移到容器或框架。我们最常在面向对象编程的上下文中使用它。

与我们的自定义代码调用库的传统编程相比，IoC 使框架能够控制程序流并调用我们的自定义代码。为了实现这一点，框架使用内置额外行为的抽象。**如果我们想添加我们自己的行为，我们需要扩展框架的类或插入我们自己的类。**

这种架构的优点是：

-   将任务的执行与其实现解耦
-   更容易在不同的实现之间切换
-   程序的更大模块化
-   通过隔离组件或模拟其依赖关系，并允许组件通过契约进行通信，更容易测试程序

我们可以通过多种机制实现控制反转，例如：策略设计模式、服务定位器模式、工厂模式和依赖注入（DI）。

接下来我们要看看 DI。

## **3. 什么是依赖注入？**

依赖注入是一种我们可以用来实现 IoC 的模式，其中被反转的控件是设置对象的依赖项。

将对象与其他对象连接起来，或将对象“注入”到其他对象中，是由汇编器完成的，而不是由对象本身完成的。

以下是我们如何在传统编程中创建对象依赖关系：

```java
public class Store {
    private Item item;
 
    public Store() {
        item = new ItemImpl1();    
    }
}复制
```

在上面的示例中，我们需要在*Store类本身中实例化**Item*接口的实现。

通过使用 DI，我们可以在不指定所需*Item*的实现的情况下重写示例：

```java
public class Store {
    private Item item;
    public Store(Item item) {
        this.item = item;
    }
}复制
```

在接下来的部分中，我们将了解如何通过元数据提供*Item*的实现。

IoC 和 DI 都是简单的概念，但它们对我们构建系统的方式有着深远的影响，因此它们非常值得充分理解。

## **4. Spring IoC 容器**

IoC 容器是实现 IoC 的框架的一个共同特征。

在 Spring 框架中，接口 *ApplicationContext*代表 IoC 容器。Spring 容器负责实例化、配置和组装称为*beans 的*对象，以及管理它们的生命周期。

Spring 框架提供了*ApplicationContext*接口的几种实现：用于独立应用程序的*ClassPathXmlApplicationContext*和*FileSystemXmlApplicationContext* ，以及用于 Web 应用程序的*WebApplicationContext 。*

为了组装 bean，容器使用配置元数据，可以是 XML 配置或注释的形式。

这是手动实例化容器的一种方法：

```java
ApplicationContext context
  = new ClassPathXmlApplicationContext("applicationContext.xml");复制
```

要在上面的示例中设置*项目属性，我们可以使用元数据。*然后容器将读取此元数据并在运行时使用它来组装 bean。

**Spring 中的依赖注入可以通过构造函数、setter 或字段来完成。**

## **5. 基于构造函数的依赖注入**

[在基于构造函数的依赖注入](https://www.baeldung.com/constructor-injection-in-spring)的情况下，容器将调用一个构造函数，每个参数代表我们要设置的依赖项。

Spring 主要按类型解析每个参数，然后是属性名称和用于消除歧义的索引。让我们使用注释查看 bean 及其依赖项的配置：

```java
@Configuration
public class AppConfig {

    @Bean
    public Item item1() {
        return new ItemImpl1();
    }

    @Bean
    public Store store() {
        return new Store(item1());
    }
}复制
```

*@Configuration*注释表示该类是 bean 定义的来源。我们也可以将它添加到多个配置类中。

我们在方法上使用*@Bean注释来定义一个bean。*如果我们不指定自定义名称，那么 bean 名称将默认为方法名称。

*对于具有默认单例*作用域的 bean ，Spring 首先检查该 bean 的缓存实例是否已经存在，如果不存在，则只创建一个新实例。如果我们使用*原型*作用域，容器会为每个方法调用返回一个新的 bean 实例。

另一种创建 bean 配置的方法是通过 XML 配置：

```xml
<bean id="item1" class="org.baeldung.store.ItemImpl1" /> 
<bean id="store" class="org.baeldung.store.Store"> 
    <constructor-arg type="ItemImpl1" index="0" name="item" ref="item1" /> 
</bean>复制
```

## **6. 基于 Setter 的依赖注入**

对于基于 setter 的 DI，容器会在调用无参数构造函数或无参数静态工厂方法实例化 bean 后调用我们类的 setter 方法。让我们使用注解创建这个配置：

```java
@Bean
public Store store() {
    Store store = new Store();
    store.setItem(item1());
    return store;
}复制
```

我们也可以使用 XML 对 bean 进行相同的配置：

```xml
<bean id="store" class="org.baeldung.store.Store">
    <property name="item" ref="item1" />
</bean>复制
```

我们可以为同一个 bean 组合基于构造函数和基于 setter 的注入类型。Spring 文档建议对强制依赖项使用基于构造函数的注入，对可选依赖项使用基于设置器的注入。

## **7.基于字段的****依赖注入**

在基于字段的 DI 的情况下，我们可以通过使用*@Autowired*注释标记它们来注入依赖项：

```java
public class Store {
    @Autowired
    private Item item; 
}复制
```

在构造*Store*对象时，如果没有构造函数或 setter 方法来注入*Item* bean，容器将使用反射将*Item*注入*Store*。

我们也可以使用[XML 配置](https://www.baeldung.com/spring-xml-injection)来实现这一点。

这种方法可能看起来更简单、更简洁，但我们不建议使用它，因为它有一些缺点，例如：

-   此方法使用反射来注入依赖项，这比基于构造函数或基于设置器的注入成本更高。
-   使用这种方法继续添加多个依赖项真的很容易。如果我们使用构造函数注入，那么拥有多个参数会让我们认为这个类做了不止一件事，这可能违反单一职责原则。

*有关@Autowired*注释的更多信息，请参阅[Wiring In Spring](https://www.baeldung.com/spring-annotations-resource-inject-autowire)一文。

## **8. 自动装配依赖**

[连接](https://www.baeldung.com/spring-annotations-resource-inject-autowire)允许 Spring 容器通过检查已定义的 bean 来自动解决协作 bean 之间的依赖关系。

使用 XML 配置自动装配 bean 有四种模式：

-   ***no\*：**默认值——这意味着没有自动装配用于 bean，我们必须显式命名依赖项。
-   ***byName\*：**自动装配是根据属性的名称完成的，因此 Spring 将查找与需要设置的属性同名的 bean。
-   ***byType\*：**类似于*byName*自动装配，仅基于属性的类型。这意味着 Spring 将寻找具有相同类型属性的 bean 来设置。如果该类型的 bean 不止一个，则框架会抛出异常。
-   ***constructor\*：**自动装配是基于构造函数参数完成的，这意味着 Spring 将查找与构造函数参数具有相同类型的 bean。

例如，让我们将上面按类型定义的*item1 bean 自动装配到**store* bean 中：

```java
@Bean(autowire = Autowire.BY_TYPE)
public class Store {
    
    private Item item;

    public setItem(Item item){
        this.item = item;    
    }
}复制
```

*我们还可以使用@Autowired*注解注入 bean以按类型自动装配：

```java
public class Store {
    
    @Autowired
    private Item item;
}复制
```

如果同一类型的 bean 不止一个，我们可以使用*@Qualifier*注解按名称引用一个 bean：

```java
public class Store {
    
    @Autowired
    @Qualifier("item1")
    private Item item;
}复制
```

现在让我们通过 XML 配置按类型自动装配 bean：

```xml
<bean id="store" class="org.baeldung.store.Store" autowire="byType"> </bean>复制
```

接下来，让我们通过 XML 按名称将一个名为 item 的 bean 注入到 store bean*的*item*属性*中*：*

```xml
<bean id="item" class="org.baeldung.store.ItemImpl1" />

<bean id="store" class="org.baeldung.store.Store" autowire="byName">
</bean>复制
```

我们还可以通过构造函数参数或设置器显式定义依赖项来覆盖自动装配。

## **9. 惰性初始化 Bean**

默认情况下，容器会在初始化期间创建并配置所有单例 bean。为避免这种情况，我们可以在 bean 配置中使用值为*true的**lazy-init属性：*

```xml
<bean id="item1" class="org.baeldung.store.ItemImpl1" lazy-init="true" />复制
```

因此，*item1* bean 只会在首次请求时被初始化，而不是在启动时。这样做的好处是初始化时间更快，但代价是在请求 bean 之前我们不会发现任何配置错误，这可能是应用程序已经运行后的几个小时甚至几天。

## **10.结论**

在本文中，我们介绍了控制反转和依赖注入的概念，并在 Spring 框架中进行了示例。

我们可以在 Martin Fowler 的文章中阅读更多关于这些概念的信息：

-   [控制容器的反转和依赖注入模式](http://martinfowler.com/articles/injection.html)。
-   [控制反转](http://martinfowler.com/bliki/InversionOfControl.html)

[此外，我们可以在Spring Framework Reference Documentation](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-dependencies)中了解 IoC 和 DI 的 Spring 实现。