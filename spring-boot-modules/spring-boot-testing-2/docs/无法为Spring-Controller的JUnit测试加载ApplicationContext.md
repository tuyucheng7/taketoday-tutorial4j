## 1. 概述

**Spring Boot应用程序中bean的混合定义是一种同时包含基于注解和基于XML的配置的定义**，在这种环境下，**我们可能希望在测试类中使用基于XML的配置**。但是，有时在这种情况下，我们可能会遇到ApplicationContext加载错误“**Failed to load ApplicationContext**”。此错误出现在测试类中，因为应用程序上下文未加载到测试上下文中。

在本教程中，我们将讨论**如何将XML应用程序上下文集成到Spring Boot应用程序的测试中**。

## 2. ”Failed to load ApplicationContext“错误

让我们通过在Spring Boot应用程序中集成基于XML的应用程序上下文来重现该错误。

首先，假设我们有一个包含Service bean定义的application-context.xml文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
     http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="employeeServiceImpl" class="cn.tuyucheng.taketoday.xmlapplicationcontext.service.EmployeeServiceImpl"/>
</beans>
```

现在我们将该文件放置在webapp/WEB-INF/目录下：

<img src="../assets/img.png">

我们还将创建一个Service接口和类：

```java
public interface EmployeeService {
    Employee getEmployee();
}

public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public Employee getEmployee() {
        return new Employee("Tuyucheng", "Admin");
    }
}
```

最后，我们将创建一个测试用例，用于从应用程序上下文中获取EmployeeService bean：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:WEB-INF/application-context.xml")
class EmployeeServiceAppContextIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void whenContextLoads_thenServiceISNotNull() {
        assertThat(employeeService).isNotNull();
    }
}
```

现在如果我们尝试运行此测试，我们将看到该错误：

```shell
java.lang.IllegalStateException: Failed to load ApplicationContext
```

此错误出现在测试类中，因为应用程序上下文未加载到测试上下文中，**根本原因是WEB-INF未包含在类路径中**：

```java
@ContextConfiguration(locations={"classpath:WEB-INF/application-context.xml"})
```

## 3. 在测试中使用基于XML的ApplicationContext

让我们看看如何在测试类中使用基于XML的应用程序上下文，**我们有两个选项可以在测试中使用基于XML的应用程序上下文：@SpringBootTest和@ContextConfiguration注解**。

### 3.1 使用@SpringBootTest和@ImportResource进行测试

**Spring Boot提供了@SpringBootTest注解，我们可以使用它来创建一个应用程序上下文以在测试中使用。此外，我们必须在Spring Boot主类中使用@ImportResource来读取XML bean**，这个注解允许我们导入一个或多个包含bean定义的资源(XML文件或Java配置类)。

首先，让我们在主类中使用@ImportResource注解：

```java
@SpringBootApplication
@ImportResource({"classpath*:application-context.xml"})
public class XmlBeanApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmlBeanApplication.class, args);
    }
}
```

现在让我们创建一个测试用例，用于从应用程序上下文中获取EmployeeService bean：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = XmlBeanApplication.class)
class EmployeeServiceAppContextIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void whenContextLoads_thenServiceISNotNull() {
        assertThat(employeeService).isNotNull();
    }
}
```

@ImportResource注解加载位于resources目录中的XML bean，此外，@SpringBootTest注解会在测试类中加载整个应用程序的bean。因此，我们能够在测试类中访问EmployeeService bean。

### 3.2 使用@ContextConfiguration和resources进行测试

**我们可以通过将测试配置文件放在src/test/resources目录中来创建具有不同配置的bean的测试上下文**。

在这种情况下，**我们使用@ContextConfiguration注解从src/test/resources目录加载测试上下文**。

首先，让我们定义EmployeeService接口的另一个实现类，用于测试：

```java
public class EmployeeServiceTestImpl implements EmployeeService {

    @Override
    public Employee getEmployee() {
        return new Employee("tuyucheng-Test", "Admin");
    }
}
```

然后我们将在src/test/resources目录中创建test-context.xml文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
     http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="employeeServiceTestImpl"
          class="cn.tuyucheng.taketoday.xmlapplicationcontext.service.EmployeeServiceTestImpl"/>
</beans>
```

最后，创建我们的测试用例：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = XmlBeanApplication.class)
@ContextConfiguration(locations = "/test-context.xml")
class EmployeeServiceTestContextIntegrationTest {
    
    @Autowired
    @Qualifier("employeeServiceImpl")
    private EmployeeService employeeServiceTest;

    @Test
    void whenContextLoads_thenServiceTestIsNotNull() {
        assertThat(employeeServiceTest).isNotNull();
    }
}
```

这里，我们使用@ContextConfiguration注解从test-context.xml加载employeeServiceTestImpl bean。

### 3.3 使用带有WEB-INF的@ContextConfiguration进行测试

**我们也可以从WEB-INF目录中导入测试类中的应用程序上下文，为此，我们可以使用其文件URL对应用程序上下文进行寻址**：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/application-context.xml")
class EmployeeServiceAppContextIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void whenContextLoads_thenServiceISNotNull() {
        assertThat(employeeService).isNotNull();
    }
}
```

## 4. 总结

在本文中，我们学习了如何在Spring Boot应用程序的测试类中使用基于XML的配置文件。