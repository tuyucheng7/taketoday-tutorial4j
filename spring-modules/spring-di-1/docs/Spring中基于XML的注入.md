## 1. 概述

在这个教程中，我们学习如何使用Spring框架进行简单的基于XML的bean配置。

## 2. 依赖注入

依赖注入是一种由外部容器提供对象依赖的技术。

假设我们有一个依赖于实际处理业务逻辑服务的应用程序类：

```java
public class IndexApp {

    private Service service;

    // standard constructors/getters/setters ...
}
```

Service是一个接口：

```java
public interface Service {
    String serve();
}
```

该接口可能有多个实现：

```java
public class IndexService implements Service {

    @Override
    public String serve() {
        return "Hello World";
    }
}
```

在这里，IndexApp是一个高级组件，它依赖于名为Service的低级组件。

从本质上讲，我们将IndexApp与Service的特定实现分离，Service实现可以根据各种因素而变化。

## 3. 实践

### 3.1 使用Setter注入

让我们看看如何使用基于XML的配置注入依赖：

```xml

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <bean id="indexService" class="cn.tuyucheng.taketoday.di.spring.IndexService"/>

    <bean id="indexApp" class="cn.tuyucheng.taketoday.di.spring.IndexApp">
        <property name="service" ref="indexService"/>
    </bean>
</beans>
```

可以看出，我们创建一个IndexService实例并为其分配一个id。
默认情况下，bean是单例的。此外，我们还创建一个IndexApp实例。在这个bean中，我们使用setter方法注入另一个bean。

### 3.2 使用构造注入

我们可以使用构造函数注入依赖：

```xml

<bean id="indexApp" class="cn.tuyucheng.taketoday.di.spring.IndexApp">
    <constructor-arg ref="indexService"/>
</bean>    
```

### 3.3 使用静态工厂方法

我们还可以注入工厂返回的bean。让我们创建一个简单的工厂，它根据提供的数字返回Service的实例：

```java
public class StaticServiceFactory {

    public static Service getService(int number) {
        return switch (number) {
            case 1 -> new MessageService("Foo");
            case 0 -> new IndexService();
            default -> throw new IllegalArgumentException("Unknown parameter " + number);
        };
    }
}
```

现在让我们看看如何使用上面的实现通过基于XML的配置将bean注入IndexApp：

```xml

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <bean id="messageService" class="cn.tuyucheng.taketoday.di.spring.StaticServiceFactory"
          factory-method="getService">
        <constructor-arg value="1"/>
    </bean>

    <bean id="indexApp" class="cn.tuyucheng.taketoday.di.spring.IndexApp">
        <property name="service" ref="messageService"/>
    </bean>
</beans>
```

在上面的示例中，我们使用工厂方法调用静态getService方法来创建一个id为messageService的bean，并将其注入IndexApp。

### 3.4 使用实例工厂方法

实例工厂方法根据提供的数字返回一个Service实例：

```java
public class InstanceServiceFactory {

    public Service getService(int number) {
        return switch (number) {
            case 1 -> new MessageService("Foo");
            case 0 -> new IndexService();
            default -> throw new IllegalArgumentException("Unknown parameter " + number);
        };
    }
}
```

现在让我们看看如何使用上述实现通过XML配置将bean注入IndexApp：

```xml

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <bean id="indexServiceFactory" class="cn.tuyucheng.taketoday.di.spring.InstanceServiceFactory"/>

    <bean id="messageServiceFromInstanceFactory" class="cn.tuyucheng.taketoday.di.spring.InstanceServiceFactory"
          factory-method="getService" factory-bean="indexServiceFactory">
        <constructor-arg value="1"/>
    </bean>

    <bean id="indexApp" class="cn.tuyucheng.taketoday.di.spring.IndexApp">
        <property name="service" ref="messageServiceFromInstanceFactory"/>
    </bean>
</beans>
```

在上面的示例中，我们使用工厂方法在InstanceServiceFactory实例上调用getService方法，以创建一个id为messageService的bean，并将其注入IndexApp。

## 4. 测试

```java
class BeanInjectionIntegrationTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        applicationContext = new ClassPathXmlApplicationContext("cn.tuyucheng.taketoday.di.spring.xml");
    }

    @Test
    void getBean_returnsInstance() {
        final IndexApp indexApp = applicationContext.getBean("indexApp", IndexApp.class);
        assertNotNull(indexApp);
    }
}
```

## 5. 总结

在这个教程中，我们举例说明了如何使用Spring通过基于XML的配置来注入依赖bean。