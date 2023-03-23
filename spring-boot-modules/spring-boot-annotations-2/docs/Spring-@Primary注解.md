## 1. 概述

在本文中，我们将介绍Spring的@Primary注解，它是在3.0版本中引入的。

简单地说，**当有多个相同类型的bean时，我们可以使用@Primary给一个bean更高的优先级**。

## 2. 为什么需要@Primary？

在某些情况下，**我们需要注册多个相同类型的bean**。

假设我们定义了两个Employee类型的bean：

```java

@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.primary")
public class Config {

    @Bean
    public Employee johnEmployee() {
        return new Employee("John");
    }

    @Bean
    public Employee tonyEmployee() {
        return new Employee("Tony");
    }
}

@Data
@AllArgsConstructor
public class Employee {

    private final String name;
}
```

**如果我们尝试获取Employee bean，Spring会抛出NoUniqueBeanDefinitionException**。

要访问具有相同类型的bean，我们通常使用@Qualifier("beanName")注解。

我们将它与@Autowired结合使用。在我们的例子中，我们在配置阶段选择了bean，所以@Qualifier不能在这里应用。

为了解决这个问题，Spring提供了@Primary注解。

## 3. 将@Primary与@Bean一起使用

让我们看一下配置类：

```java

@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.primary")
public class Config {

    @Bean
    public Employee johnEmployee() {
        return new Employee("John");
    }

    @Bean
    @Primary
    public Employee tonyEmployee() {
        return new Employee("Tony");
    }
}
```

**我们用@Primary标记tonyEmployee()方法。Spring将优先注入tonyEmployee bean，而不是johnEmployee**。

现在，让我们启动应用程序上下文并从中获取Employee bean：

```java
public class PrimaryApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        Employee employee = context.getBean(Employee.class);
        System.out.println(employee);
    }
}
```

运行应用程序后，控制台的输出如下：

```
Employee{name='Tony'}
```

## 4. 将@Primary与@Component一起使用

**我们可以直接在bean上使用@Primary**。让我们看一下以下场景：

```java
public interface Manager {
    String getManagerName();
}
```

我们有一个Manager接口和两个子类bean，DepartmentManager：

```java

@Component
public class DepartmentManager implements Manager {

    @Override
    public String getManagerName() {
        return "Department manager";
    }
}
```

和GeneralManager bean：

```java

@Primary
@Component
public class GeneralManager implements Manager {

    @Override
    public String getManagerName() {
        return "General manager";
    }
}
```

它们都重写了Manager接口的getManagerName()方法。另外，请注意我们用@Primary标记了GeneralManager bean。

这一次，**@Primary仅在我们启用组件扫描时才有意义**：

```java

@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.primary")
public class Config {

}
```

让我们创建一个Service类来注入Manager类型的bean：

```java

@Service
public class ManagerService {

    @Autowired
    private Manager manager;

    public Manager getManager() {
        return manager;
    }
}
```

在这里，bean DepartmentManager和GeneralManager都符合自动装配的条件。

**当我们用@Primary标记GeneralManager bean时，它将被选择用于依赖注入**：

```java
public class PrimaryApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        ManagerService service = context.getBean(ManagerService.class);
        Manager manager = service.getManager();
        System.out.println(manager.getManagerName());
    }
}
```

运行以上程序，控制台的输出是“General manager”。

## 5. 总结

在本文中，我们了解了Spring的@Primary注解。通过代码示例，我们演示了@Primary的需求和用例。