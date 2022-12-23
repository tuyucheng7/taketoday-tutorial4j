## 1. 概述

在这个教程中，我们介绍Spring中@ComponentScan和@EnableAutoConfiguration注解之间的区别。

## 2. Spring注解

注解使在Spring中配置依赖注入变得更加容易。我们可以在类和方法上使用Spring Bean注解来定义bean，而不是使用XML配置文件。然后，Spring IoC容器配置和管理bean。

@ComponentScan和@EnableAutoConfiguration注解的主要作用为：

+ @ComponentScan扫描带注解的Spring组件。
+ @EnableAutoConfiguration用于启用自动配置。

## 3. 它们的差异

这些注解的主要区别在于@ComponentScan扫描Spring组件，而@EnableAutoConfiguration用于自动配置Spring Boot应用程序中classpath存在的bean。

### 3.1 @ComponentScan

在开发应用程序时，我们需要告诉Spring框架扫描Spring管理的组件。**@ComponentScan使Spring能够扫描@Configuration、@Controller、@Service和我们定义的其他组件**。

特别地，@ComponentScan注解与@Configuration注解一起使用来指定Spring扫描组件的包：

```java
@ComponentScan
@Configuration
public class EmployeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }
}
```

**或者，Spring也可以从指定的包开始扫描，我们可以使用basePackageClasses或basePackages属性来定义。如果未指定包，则将包含@ComponentScan注解的类所在的包视为起始包**：

```java
@Configuration
@ComponentScan(
        basePackages = {
                "cn.tuyucheng.taketoday.annotations.componentscanautoconfigure.healthcare",
                "cn.tuyucheng.taketoday.annotations.componentscanautoconfigure.employee"
        },
        basePackageClasses = Teacher.class
)
public class EmployeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }
}
```

在上面的例子中，Spring会扫描healthcare和employee包，以及Teacher类所在的包中的组件。

Spring在指定的包及其所有子包中搜索带有@Configuration注解的类。**此外，配置类可以包含@Bean注解，这些注解将方法注册为Spring应用程序上下文中的bean**。之后，@ComponentScan注解可以自动检测此类bean：

```java
@Configuration
public class Hospital {

    @Bean("doctor")
    public Doctor getDoctor() {
        return new Doctor();
    }
}
```

**此外，@ComponentScan注解还可以扫描、检测和注册带有@Component、@Controller、@Service和@Repository注解的类的bean**。

例如，我们可以创建一个Employee类作为一个组件，它可以通过@ComponentScan注解进行扫描：

```java
@Component("employee")
public class Employee {
    // ...
}
```

### 3.2 @EnableAutoConfiguration

**@EnableAutoConfiguration注解使Spring Boot能够自动配置应用程序上下文。因此，它会根据类路径中包含的jar文件和我们定义的bean自动创建和注册bean**。

例如，当我们在类路径中定义spring-boot-starter-web依赖项时，Spring boot会自动配置Tomcat和Spring MVC。但是，如果我们定义自己的配置，则此自动配置的优先级较低。

**声明@EnableAutoConfiguration注解的类的包被认为是默认包**。因此，我们应该始终在根包中应用@EnableAutoConfiguration注解，以便可以扫描每个子包和类：

```java
@Configuration
@EnableAutoConfiguration
public class EmployeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }
}
```

此外，@EnableAutoConfiguration注解提供了两个参数来手动排除任何参数：

我们可以使用exclude属性来禁用我们不想自动配置的类列表：

```java
@Configuration
@EnableAutoConfiguration(exclude = {JdbcTemplateAutoConfiguration.class})
public class EmployeeApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EmployeeApplication.class, args);
    }
}
```

我们可以使用excludeName属性定义想要从自动配置中排除的类的全限定名列表：

```java
@Configuration
@EnableAutoConfiguration(excludeName = {"org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration"})
public class EmployeeApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EmployeeApplication.class, args);
    }
}
```

**从Spring Boot 1.2.0开始，我们可以使用@SpringBootApplication注解，它是@Configuration、@EnableAutoConfiguration和@ComponentScan三个注解及其默认属性的组合**：

```java
@SpringBootApplication
public class EmployeeApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EmployeeApplication.class, args);
    }
}
```

## 4. 总结

在本文中，我们了解了Spring Boot中@ComponentScan和@EnableAutoConfiguration注解的区别。