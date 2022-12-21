## 1. 概述

在本文中，我们将探讨在容器中显示所有Spring管理的bean的不同方式。

## 2. IoC容器

bean是Spring管理的应用程序的基础；所有bean都驻留在负责管理其生命周期的IOC容器中。

我们可以通过两种方式获取此容器中所有bean的列表：

1.  使用ListableBeanFactory接口
2.  使用Spring Boot Actuator

## 3. 使用ListableBeanFactory接口

**ListableBeanFactory接口提供了getBeanDefinitionNames()方法，该方法返回此工厂中定义的所有bean的名称**。该接口由所有预加载其bean定义以枚举其所有bean实例的bean工厂实现。

你可以在[官方文档](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/beans/factory/ListableBeanFactory.html)中找到所有已知子接口及其实现类的列表。

对于此示例，我们将使用Spring Boot应用程序。

首先，我们将创建一些Spring bean，让我们创建一个简单的Spring控制器FooController：

```java
@Controller
public class FooController {

    @Autowired
    private FooService fooService;

    @RequestMapping(value="/displayallbeans")
    public String getHeaderAndBody(Map model){
        model.put("header", fooService.getHeader());
        model.put("message", fooService.getBody());
        return "displayallbeans";
    }
}
```

这个Controller依赖于另一个Spring bean FooService：

```java
@Service
public class FooService {

    public String getHeader() {
        return "Display All Beans";
    }

    public String getBody() {
        return "This is a sample application that displays all beans "
              + "in Spring IoC container using ListableBeanFactory interface "
              + "and Spring Boot Actuators.";
    }
}
```

请注意，我们在这里创建了两个不同的bean：

1.  fooController
2.  fooService

在执行此应用程序时，我们将使用applicationContext对象并调用其getBeanDefinitionNames()方法，该方法将返回applicationContext容器中的所有bean：

```java
@SpringBootApplication
public class Application {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(Application.class, args);
        displayAllBeans();
    }

    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
}
```

这将打印来自applicationContext容器的所有bean：

```shell
fooController
fooService
// other beans
```

请注意，**除了我们定义的bean之外，它还会记录此容器中的所有其他bean**。为了清楚起见，我们在这里省略了它们，因为它们太多了。

## 4. 使用Spring Boot Actuator

Spring Boot Actuator功能提供了用于监控应用程序统计信息的端点。

它包括许多内置的端点，包括/bean，这将显示我们应用程序中所有Spring管理的bean的完整列表。你可以在[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints)上找到现有端点的完整列表。


现在，我们只需访问URL http://<address>:<management-port>/beans。如果我们没有指定任何单独的管理端口，我们可以使用我们的默认服务器端口。这将返回一个JSON响应，显示Spring IoC容器中的所有bean：

```json
[
    {
        "context": "application:8080",
        "parent": null,
        "beans": [
            {
                "bean": "fooController",
                "aliases": [],
                "scope": "singleton",
                "type": "cn.tuyucheng.taketoday.displayallbeans.controller.FooController",
                "resource": "file [D:/workspace/intellij/spring-boot/target/classes/cn/tuyucheng/taketoday/displayallbeans/controller/FooController.class]",
                "dependencies": [
                    "fooService"
                ]
            },
            {
                "bean": "fooService",
                "aliases": [],
                "scope": "singleton",
                "type": "cn.tuyucheng.taketoday.displayallbeans.service.FooService",
                "resource": "file [E:/workspace/intellij/spring-boot/target/classes/cn/tuyucheng/taketoday/displayallbeans/service/FooService.class]",
                "dependencies": []
            }
            // ...other beans
        ]
    }
]
```

当然，这也包含许多驻留在同一个Spring容器中的其他bean，但为了清楚起见，我们在这里省略了它们。

如果你想了解有关Spring Boot Actuator的更多信息，可以阅读[Spring Boot Actuator]()指南。

## 5. 总结

在本文中，我们学习了如何使用ListableBeanFactory接口和Spring Boot Actuators在Spring IoC容器中显示所有bean。