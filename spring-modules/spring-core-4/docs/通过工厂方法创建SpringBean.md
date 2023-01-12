## 1. 概述

工厂方法是一种非常有用的技术，可以将复杂的创建逻辑隐藏在单个方法调用中。

虽然我们通常使用构造函数或字段注入在Spring中创建bean，但我们**也可以使用工厂方法创建Spring bean**。

在本文中，我们将深入研究使用实例和静态工厂方法创建Spring bean。

## 2. 实例工厂方法

工厂方法模式的标准实现是创建返回所需bean的实例方法。

此外，**我们可以配置Spring以创建带或不带参数的所需bean**。

### 2.1 不带参数

我们可以创建一个Foo类来代表我们需要创建的bean：

```java
public class Foo {

}
```

然后，我们创建一个InstanceFooFactory类，该类包含一个工厂方法createInstance，用于创建我们的Foo bean：

```java
public class InstanceFooFactory {

    public Foo createInstance() {
        return new Foo();
    }
}
```

之后，我们配置Spring：

1. 为我们的工厂类创建一个bean(InstanceFooFactory)
2. 使用factory-bean属性来引用我们的工厂bean
3. 使用factory-method属性来引用我们的工厂方法(createInstance)

将其应用于Spring XML配置，我们最终得到：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="instanceFooFactory" class="cn.tuyucheng.taketoday.factorymethod.InstanceFooFactory"/>
    <bean id="foo" factory-bean="instanceFooFactory" factory-method="createInstance"/>
</beans>
```

最后，我们自动注入我们想要的Foo bean。然后Spring将使用我们的createInstance工厂方法创建我们的bean：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/factorymethod/instance-foo-config.xml")
class InstanceFooFactoryIntegrationTest {

    @Autowired
    private Foo foo;

    @Test
    void givenValidInstanceFactoryConfig_whenCreateFooInstance_thenInstanceIsNotNull() {
        assertNotNull(foo);
    }
}
```

### 2.2 带参数

**我们还可以使用Spring配置中的constructor-arg标签为我们的实例工厂方法提供参数**。

首先，我们创建一个带有参数的Bar类：

```java

@AllArgsConstructor
@Data
public class Bar {
    private String name;
}
```

接下来，我们创建一个实例工厂类InstanceBarFactory，其工厂方法接收一个参数并返回一个Bar bean：

```java
public class InstanceBarFactory {

    public Bar createInstance(String name) {
        return new Bar(name);
    }
}
```

最后，我们在Bar bean定义中添加一个constructor-arg标签：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="instanceBarFactory" class="cn.tuyucheng.taketoday.factorymethod.InstanceBarFactory"/>

    <bean id="bar" factory-bean="instanceBarFactory" factory-method="createInstance">
        <constructor-arg value="someName"/>
    </bean>
</beans>
```

然后我们可以像我们为我们的Foo bean所做的那样自动注入我们的Bar bean：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/factorymethod/instance-bar-config.xml")
class InstanceBarFactoryIntegrationTest {

    @Autowired
    private Bar instance;

    @Test
    void givenValidInstanceFactoryConfig_whenCreateInstance_thenNameIsCorrect() {
        assertNotNull(instance);
        assertEquals("someName", instance.getName());
    }
}
```

## 3. 静态工厂方法

我们还可以将Spring配置为使用静态方法作为工厂方法。

**虽然应该首选实例工厂方法，但如果我们有现有的遗留静态方法来生成所需的bean，则此方法可能很有用**。
例如，如果一个工厂方法返回一个单例，我们可以配置Spring使用这个单例工厂方法。

与实例工厂方法类似，我们可以配置带参数和不带参数的静态方法。

### 3.1 不带参数

使用Foo类作为我们想要创建的bean，我们可以创建一个SingletonFooFactory类，
它包括一个createInstance工厂方法，该方法返回一个Foo的单例实例：

```java
public class SingletonFooFactory {
    private static final Foo INSTANCE = new Foo();

    public static Foo createInstance() {
        return INSTANCE;
    }
}
```

这次，**我们只需要创建一个bean**。这个bean只需要两个属性：

1. class - 声明我们的工厂类(SingletonFooFactory)
2. factory-method - 声明静态工厂方法(createInstance)

将其应用于我们的Spring XML配置，我们得到：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="foo" class="cn.tuyucheng.taketoday.factorymethod.SingletonFooFactory" factory-method="createInstance"/>
</beans>
```

最后，我们使用与之前相同的结构自动注入我们的Foo bean：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/factorymethod/static-foo-config.xml")
class SingletonFooFactoryIntegrationTest {

    @Autowired
    private Foo singleton;

    @Test
    void givenValidStaticFactoryConfig_whenCreateInstance_thenInstanceIsNotNull() {
        assertNotNull(singleton);
    }
}
```

### 3.2 带参数

虽然**我们应该尽可能避免改变静态对象的状态，比如我们的单例**，但我们仍然可以将参数传递给我们的静态工厂方法。

为此，我们创建一个接收所需参数的新工厂方法：

```java
public class SingletonBarFactory {
    private static final Bar INSTANCE = new Bar("unnamed");

    public static Bar createInstance(String name) {
        INSTANCE.setName(name);
        return INSTANCE;
    }
}
```

之后，我们将Spring配置为使用constructor-arg标签传入所需的参数：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="bar" class="cn.tuyucheng.taketoday.factorymethod.SingletonBarFactory" factory-method="createInstance">
        <constructor-arg value="someName"/>
    </bean>
</beans>
```

最后，我们使用与之前相同的结构自动注入我们的Bar bean：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/factorymethod/static-bar-config.xml")
class SingletonBarFactoryIntegrationTest {

    @Autowired
    private Bar instance;

    @Test
    void givenValidStaticFactoryConfig_whenCreateInstance_thenNameIsCorrect() {
        assertNotNull(instance);
        assertEquals("someName", instance.getName());
    }
}
```

## 4. 总结

在本文中，我们研究了如何配置Spring以使用实例和静态工厂方法 - 无论是带参数还是不带参数。

虽然通过构造函数和字段注入创建bean更为常见，但工厂方法对于复杂的创建步骤和遗留代码非常方便。