## 1. 概述

我们可以使用org.springframework.beans.factory.annotation和org.springframework.context.annotation包中的注解来利用Spring DI的功能。

## 2. DI相关注解

### 2.1 @Autowired

**我们可以使用@Autowired来标记Spring将要解析和注入的依赖bean**。我们可以将此注解与构造函数、setter或字段注入一起使用。

构造注入：

```java
class Car {
    Engine engine;

    @Autowired
    Car(Engine engine) {
        this.engine = engine;
    }
}
```

Setter注入：

```java
class Car {
    Engine engine;

    @Autowired
    void setEngine(Engine engine) {
        this.engine = engine;
    }
}
```

字段注入：

```java
class Car {
    @Autowired
    Engine engine;
}
```

@Autowired有一个名为required的布尔参数，默认值为true。当找不到合适的bean进行注入时，它会调整Spring的行为。当为true时，抛出异常，否则不会注入任何内容，程序不会抛出异常。

请注意，如果我们使用构数注入，则所有构造函数参数都是必需的。

从4.3版本开始，我们不需要用@Autowired显式标注构造函数，除非我们至少声明了两个构造函数。

### 2.2 @Bean

@Bean标记一个实例化Spring bean的工厂方法：

```java
@Bean
Engine engine() {
    return new Engine();
}
```

**当需要返回类型的新实例时，Spring会调用这些方法**。

生成的bean与工厂方法名具有相同的名称。如果我们想以不同的方式命名它，我们可以使用这个注解的name或value参数(value是name的别名)：

```java
@Bean("engine")
Engine getEngine() {
    return new Engine();
}
```

**注意，所有使用@Bean标注的方法都必须在@Configuration类中，否则不起作用**。

### 2.3 @Qualifier

**我们使用@Qualifier和@Autowired来提供在不明确的情况下要使用的bean id或bean名称**。

例如，以下两个bean实现了相同的接口：

```java
class Bike implements Vehicle {
}

class Car implements Vehicle {
}
```

如果Spring需要注入一个Vehicle bean，它最终会找到多个匹配的Vehicle bean定义。
在这种情况下，我们可以使用@Qualifier注解显式地提供bean的名称。

使用构造注入：

```java
@Autowired
Biker(@Qualifier("bike") Vehicle vehicle) {
    this.vehicle = vehicle;
}
```

使用setter注入：

```java
@Autowired
void setVehicle(@Qualifier("bike") Vehicle vehicle) {
    this.vehicle = vehicle;
}
```

或者：

```java
@Autowired
@Qualifier("bike")
void setVehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
}
```

使用字段注入：

```java
@Autowired
@Qualifier("bike")
Vehicle vehicle;
```

### 2.4 @Required

@Required在setter方法上标记我们希望通过XML填充的依赖项：

```java
@Required
void setColor(String color) {
    this.color = color;
}
```

```xml
<bean class="cn.tuyucheng.taketoday.annotations.Bike">
    <property name="color" value="green"/>
</bean>
```

否则，将抛出BeanInitializationException。

### 2.5 @Value

我们可以使用@Value将属性值注入到bean中，它与构造、setter和字段注入兼容。

构造注入：

```java
Engine(@Value("8") int cylinderCount) {
    this.cylinderCount = cylinderCount;
}
```

setter注入：

```java
@Autowired
void setCylinderCount(@Value("8") int cylinderCount) {
    this.cylinderCount = cylinderCount;
}
```

或者：

```java
@Value("8")
void setCylinderCount(int cylinderCount) {
    this.cylinderCount = cylinderCount;
}
```

字段注入：

```text
@Value("8")
int cylinderCount;
```

当然，注入静态值是没有用的。因此，**我们可以在@Value中使用占位符字符串来注入在外部源中定义的值**，例如.properties或.yaml文件。

假设有以下.properties文件：

```properties
engine.fuelType=petrol
```

我们可以使用以下内容注入engine.fuelType的值：

```java
@Value("${engine.fuelType}")
String fuelType;
```

我们甚至可以在SpEL中使用@Value。

### 2.6 @DependsOn

**我们可以使用这个注解让Spring在被标注的bean之前初始化其他bean**。通常，这种行为是自动的，基于bean之间的显式依赖关系。

**我们只有在依赖是隐式的时候才需要这个注解**，例如JDBC驱动加载或者静态变量初始化。

我们可以在依赖类上使用@DependsOn来指定依赖bean的名称，注解的value参数需要一个包含依赖bean名称的数组：

```java
@DependsOn("engine")
class Car implements Vehicle {
}
```

或者，如果我们用@Bean注解定义一个bean，工厂方法应该用@DependsOn注解：

```java
@Bean
@DependsOn("fuel")
Engine engine() {
    return new Engine();
}
```

### 2.7 @Lazy

**当我们想要惰性地初始化bean时，我们使用@Lazy**。默认情况下，Spring在应用程序上下文的启动/引导时急切地创建所有单例bean。

**但是，在某些情况下，我们只需要在请求时才创建bean，而不是在应用程序启动时**。

这个注解的行为会根据我们准确放置的位置而有所不同。我们可以将它放置在：

+ 一个@Bean注解的bean工厂方法，用于延迟方法调用(因此创建bean)。
+ 一个@Configuration类，其中所有包含的@Bean方法都会受到影响。
+ 一个@Component类，它不是一个@Configuration类，这个bean将被延迟初始化。
+ @Autowired构造函数、setter或字段，用于延迟加载依赖项本身(通过代理)。

此注解有一个名为value的参数，默认值为true。覆盖默认行为很有用。

例如，在全局设置为惰性时将bean标记为急切加载，或者在标有@Lazy的@Configuration类中配置特定的@Bean方法以急切加载：

```java
@Configuration
@Lazy
class VehicleFactoryConfig {

    @Bean
    @Lazy(false)
    Engine engine() {
        return new Engine();
    }
}
```

### 2.8 @Lookup

**使用@Lookup注解的方法告诉Spring在我们调用它时返回该方法的返回类型的实例**。

### 2.9 @Primary

有时我们需要定义同一类型的多个bean。在这些情况下，注入将不成功，因为Spring不知道我们需要哪个bean。

我们已经介绍了处理这种情况的一个方法：用@Qualifier标记所有注入点并指定所需bean的名称。

然而，大多数时候我们需要一个特定的bean，而很少需要其他bean。我们可以使用@Primary来简化这种情况：如果我们用@Primary标记最常用的bean，它将在没有使用@Qualifier的注入点上被选中：

```java
@Component
@Primary
class Car implements Vehicle {
}

@Component
class Bike implements Vehicle {
}

@Component
class Driver {
    @Autowired
    Vehicle vehicle;
}

@Component
class Biker {
    @Autowired
    @Qualifier("bike")
    Vehicle vehicle;
}
```

在上面的示例中，Car是主要的。因此在Driver类中，Spring注入了一个Car bean。当然，在Biker bean中，字段vehicle的值将是Bike对象，因为它是明确通过@Qualifier指定的。

### 2.10 @Scope

我们使用@Scope来定义@Component类或@Bean定义的作用域。它可以是单例、原型、request、session、globalSession或一些自定义作用域。

例如：

```java
@Component
@Scope("prototype")
class Engine {
}
```

## 3. 上下文配置注解

我们可以使用本节中描述的注解来配置应用程序上下文。

### 3.1 @Profile

如果我们希望Spring仅在特定的Profile处于激活状态时使用@Component类或@Bean方法，我们可以用@Profile标记它。
我们可以使用注解的value参数配置Profile的名称：

```java
@Component
@Profile("sportDay")
class Bike implements Vehicle {
}
```

### 3.2 @Import

**我们可以使用特定的@Configuration类而无需使用此注解进行组件扫描**。我们可以为这些类提供@Import的value参数：

```java
@Import(VehiclePartSupplier.class)
class VehicleFactoryConfig {
}
```

### 3.3 @ImportResource

**我们可以使用此注解导入XML配置**，通过使用locations参数指定XML文件的位置，或者使用它的别名value参数：

```java
@Configuration
@ImportResource("classpath:/annotations.xml")
class VehicleFactoryConfig {
}
```

### 3.4 @PropertySource

**使用这个注解，我们可以定义包含应用程序配置的属性文件**：

```java
@Configuration
@PropertySource("classpath:/annotations.properties")
class VehicleFactoryConfig {
}
```

@PropertySource利用Java 8重复注解功能，这意味着我们可以多次使用它标记一个类：

```java
@Configuration
@PropertySource("classpath:/annotations.properties")
@PropertySource("classpath:/vehicle-factory.properties")
class VehicleFactoryConfig {
}
```

### 3.5 @PropertySources

我们可以使用这个注解来指定多个@PropertySource配置：

```java
@Configuration
@PropertySources({
        @PropertySource("classpath:/annotations.properties"),
        @PropertySource("classpath:/vehicle-factory.properties")
})
class VehicleFactoryConfig {
}
```

请注意，从Java 8开始，我们可以通过上述重复注解功能实现相同的功能。

## 4. 总结

在本文中，我们介绍了最常见的Spring核心注解。如何配置bean注入和应用程序上下文，以及如何标记类以进行组件扫描。