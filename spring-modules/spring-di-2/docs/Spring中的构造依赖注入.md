## 1. 概述

可以说，现代软件设计最重要的开发原则之一是依赖注入(DI)，它很自然地源于另一个至关重要的原则：模块化。

本文将探讨Spring中一种特定类型的DI技术，称为基于构造函数的依赖注入，简单地说，就是我们在实例化时将所需的组件传递给一个类。

## 2. 基于注解的配置

```java

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.constructordi")
public class Config {

    @Bean
    public Engine engine() {
        return new Engine("v8", 5);
    }

    @Bean
    public Transmission transmission() {
        return new Transmission("sliding");
    }
}
```

这里我们使用@Configuration注解来通知Spring，在运行时这个类提供了bean definition(@Bean注解)，
并且需要对包cn.tuyucheng.taketoday.constructordi中额外的bean执行上下文扫描。

接下来，我们定义一个Car类：

```java

@Component
public class Car {

    private final Engine engine;

    private final Transmission transmission;

    @Autowired
    public Car(Engine engine, Transmission transmission) {
        this.engine = engine;
        this.transmission = transmission;
    }
}
```

Spring会在进行包扫描时扫描到我们的Car类，并将通过调用带有@Autowired注解的构造函数来初始化其实例。

通过调用Config类的@Bean注解方法，我们可以获得Engine和Transmission的实例。
最后，我们需要使用我们的POJO配置来启动ApplicationContext：

```java
public class SpringRunner {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        Car car = context.getBean(Car.class);
    }
}
```

## 3. 隐式构造注入

从Spring 4.3开始，只包含单个构造函数的类可以省略@Autowired注解。

最重要的是，同样从4.3开始，我们可以在@Configuration注解类中利用基于构造函数的注入。
另外，如果这样的类只有一个构造函数，我们也可以省略@Autowired注解。

## 4. 基于XML的配置

使用基于构造注入配置Spring运行时的另一种方法是使用XML配置文件：

```xml

<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="toyota" class="cn.tuyucheng.taketoday.constructordi.domain.Car">
        <constructor-arg index="0" ref="engine"/>
        <constructor-arg index="1" ref="transmission"/>
    </bean>

    <bean id="engine" class="cn.tuyucheng.taketoday.constructordi.domain.Engine">
        <constructor-arg index="0" value="v4"/>
        <constructor-arg index="1" value="2"/>
    </bean>

    <bean id="transmission" class="cn.tuyucheng.taketoday.constructordi.domain.Transmission">
        <constructor-arg value="sliding"/>
    </bean>
</beans>
```

请注意，constructor-arg可以接收字符串文本或对另一个bean的引用，并且可以提供可选的显式index和type。
我们可以使用type和index属性来解决歧义(例如，如果构造函数接收同一类型的多个参数)。

> name属性也可以用于xml到java变量的匹配，但是你的代码必须在编译时使用debug标志。

在这种情况下，我们需要使用ClassPathXmlApplicationContext启动我们的Spring应用程序上下文：

```java
import cn.tuyucheng.taketoday.constructordi.domain.Car;

public class SpringRunner {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("constructordi.xml");
        Car car = context.getBean(Car.class);
    }
}
```

## 5. 利弊

与字段注入相比，构造函数注入有一些优点。

**第一个好处是可测试性**，假设我们要对一个使用字段注入的Spring bean进行单元测试：

```java
public class UserService {

    @Autowired
    private UserRepository userRepository;
}
```

在UserService实例的构建过程中，我们无法初始化userRepository状态。
实现这一点的唯一方法是通过反射API，这完全打破了封装。此外，与简单的构造函数调用相比，生成的代码更不安全。

此外，**使用字段注入，我们无法强制执行类级别的不变量**，因此可能会在没有正确初始化userRepository的情况下拥有UserService实例，
从而导致随机的NullPointerExceptions。相反，使用构造函数注入，更容易构建不可变组件。

此外，**从OOP的角度来看，使用构造函数创建对象实例更为自然**。

另一方面，构造注入的主要缺点是冗长，尤其是bean具有大量依赖项时。
但有时这可能是因祸得福，因为如果出现这种情况往往意味着我我们的代码能够进一步的重构。

## 6. 总结

这篇简短的文章演示了通过Spring使用基于构造函数依赖注入的两种不同方法实现。