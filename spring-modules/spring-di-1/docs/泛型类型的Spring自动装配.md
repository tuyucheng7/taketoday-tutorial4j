## 1. 概述

在本教程中，我们介绍如何通过泛型参数注入Spring bean。

## 2. Spring 3.2中的自动注入与泛型

**Spring从3.2开始支持泛型类型的注入**。

假设我们有一个名为Vehicle的抽象类和一个名为Car的具体子类：

```java
public abstract class Vehicle {
    private String name;
    private String manufacturer;

    // getters and setters ...
}
```

```java
public class Car extends Vehicle {
    private String engineType;

    // getters and setters ...
}
```

假设我们想将Vehicle类型的对象集合注入到某个类中：

```text
@Autowired
private List<Vehicle> vehicles;
```

**Spring会将所有Vehicle实例bean自动装配到此集合中**，我们如何通过Java或XML配置实例化这些bean并不重要。

我们也可以使用限定符来仅获取Vehicle类型的特定bean，然后我们创建@CarQualifier并使用@Qualifier对其进行标注：

```java

@Target({
        ElementType.FIELD, ElementType.METHOD,
        ElementType.TYPE, ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface CarQualifier {

}
```

现在，我们可以在字段上使用这个注解来注入一些特定的Vehicle对象：

```text
@Autowired
@CarQualifier
private List<Vehicle> vehicles;
```

**在这种情况下，我们可能会创建几个Vehicle bean，但Spring只会将带有@CarQualifier注解的那些注入到上面的集合中**：

```java

@SpringBootApplication
@ComponentScan("cn.tuyucheng.taketoday.dependencyinjectiontypes.model")
public class CustomConfiguration {

    @Bean
    @CarQualifier
    public Car getMercedes() {
        return new Car("E280", "Mercedes", "Diesel");
    }
}
```

## 3. Spring 4中的自动注入与泛型

假设我们有另一个名为Motorcycle的Vehicle子类：

```java
public class Motorcycle extends Vehicle {
    private boolean twoWheeler;

    // getters and setters ...
}
```

现在，如果我们只想将Car类型的bean注入到我们的集合中，而不注入Motorcycle类型的bean，我们可以通过使用特定的子类作为类型参数来做到这一点：

```text
@Autowired
private List<Car> vehicles;
```

自4.0版本以来，Spring允许我们使用泛型类型作为限定符，而无需显式使用注解。

在Spring 4.0之前，上面的代码不适用于Vehicle的多个子类的bean。
如果没有显式限定符，我们会得到NonUniqueBeanDefinitionException。

## 4. ResolvableType

**泛型自动装配功能是通过ResolvableType类的帮助下在幕后工作**。

它是在Spring 4.0中引入的，用于封装Java Type并处理对父类、接口、泛型参数的访问，并最终解析为一个类：

```java

@Component
public class CarHandler {

    @Autowired
    @CarQualifier
    private List<Vehicle> vehicles;

    public List<Vehicle> getVehicles() throws NoSuchFieldException {
        ResolvableType vehiclesType = ResolvableType.forField(getClass().getDeclaredField("vehicles"));
        System.out.println(vehiclesType);
        ResolvableType type = vehiclesType.getGeneric();
        System.out.println(type);
        Class<?> aClass = type.resolve();
        System.out.println(aClass);
        return this.vehicles;
    }
}
```

上述代码的输出将显示相应的简单类型和泛型类型：

```text
java.util.List<cn.tuyucheng.taketoday.dependencyinjectiontypes.model.Vehicle>
cn.tuyucheng.taketoday.dependencyinjectiontypes.model.Vehicle
class cn.tuyucheng.taketoday.dependencyinjectiontypes.model.Vehicle
```

## 5. 总结

泛型类型的注入是一个强大的功能，它可以让开发人员省去分配显式限定符的工作，使代码更简洁、更易于理解。