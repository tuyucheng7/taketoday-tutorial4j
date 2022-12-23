## 1. 概述

在本教程中，我们将讨论用于定义不同类型bean的最常见的Spring bean注解。

有几种方法可以在Spring容器中配置bean。首先，我们可以使用XML配置声明它们。我们也可以在配置类中使用@Bean注解来声明bean。

最后，我们可以使用org.springframework.stereotype包中的注解之一标记该类，并将其余部分留给组件扫描。

## 2. 组件扫描

如果启用了组件扫描，Spring可以自动扫描包中的bean。

@ComponentScan配置要扫描哪些包以查找具有注解配置的类。我们可以直接使用basePackages或value参数指定包名称(value是basePackages的别名)：

```java
@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.annotations")
public class VehicleFactoryConfig {

}
```

此外，我们可以使用basePackageClasses参数指向包中的类：

```java
@Configuration
@ComponentScan(basePackageClasses = VehicleFactoryConfig.class)
public class VehicleFactoryConfig {

}
```

这两个参数都是数组类型，因此我们可以为每个参数提供多个值。

如果未指定参数，则扫描从存在@ComponentScan注解类的同一包中进行。

@ComponentScan利用了Java 8的重复注解特性，这意味着我们可以用它多次标记一个类：

```java
@Configuration
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.annotations")
@ComponentScan(basePackageClasses = VehicleFactoryConfig.class)
public class VehicleFactoryConfig {

}
```

或者，我们可以使用@ComponentScans指定多个@ComponentScan配置：

```java
@Configuration
@ComponentScans({
        @ComponentScan(basePackages = "cn.tuyucheng.taketoday.annotations"),
        @ComponentScan(basePackageClasses = VehicleFactoryConfig.class)
})
public class VehicleFactoryConfig {

}
```

使用XML配置时，配置组件扫描同样简单：

```xml
<context:component-scan base-package="cn.tuyucheng.taketoday"/>
```

## 3. @Component

@Component是一个类级别的注解。**在组件扫描期间，Spring会自动检测带有@Component注解的类**：

```java
@Component
public class CarUtility {

}
```

默认情况下，此类的bean实例名称为类的全名将首字母转为小写。此外，我们可以使用此注解的value参数指定不同的名称。

由于@Repository、@Service、@Configuration和@Controller都是@Component的元注解，它们共享相同的bean命名行为。
Spring也会在组件扫描过程中自动检测它们。

## 4. @Repository

DAO或Repository类通常代表应用程序中的数据库访问层，应使用@Repository进行标注：

```java
@Repository
public class VehicleRepository {

}
```

使用此注解的一个优点是**它启用了自动持久性异常转换**。当使用持久层框架(例如Hibernate)时，在带有@Repository注解的类中抛出的原生异常将自动转换为Spring的DataAccessExeption的子类。

要启用异常转换，我们需要声明自己的PersistenceExceptionTranslationPostProcessor bean：

```java
@Bean
public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
}
```

请注意，在大多数情况下，Spring会自动执行上述步骤。

或通过XML配置：

```xml
<bean class="org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor"/>
```

## 5. @Service

应用程序的业务逻辑通常驻留在Service层中，因此我们可以使用@Service注解来指示一个类属于Service层：

```java
@Service
public class VehicleService {
    // ...    
}
```

## 6. @Controller

@Controller是一个类级别的注解，**它告诉Spring这个类作为Spring MVC中的一个控制器**：

```java
@Controller
public class VehicleController {
    // ...
}
```

## 7. @Configuration

配置类可以包含使用@Bean注解的bean定义方法：

```java
@Configuration
class VehicleFactoryConfig {

    @Bean
    Engine engine() {
        return new Engine();
    }
}
```

## 8. 原型注解和AOP

当我们使用Spring原型注解时，很容易创建一个指向所有具有特定原型的类的切入点。

例如，假设我们想从DAO层计算方法的执行时间，我们可以通过@Repository原型创建以下切面(使用AspectJ注解)：

```java
@Aspect
@Component
public class PerformanceAspect {

    private static final Logger logger = Logger.getLogger(PerformanceAspect.class.getName());

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryClassMethods() {
    }

    @Around("repositoryClassMethods()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        Object retval = pjp.proceed();
        long end = System.nanoTime();
        String methodName = pjp.getSignature().getName();
        logger.info("Execution of " + methodName + " took " + TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");
        return retval;
    }
}
```

在这个例子中，我们创建了一个切入点，该切入点匹配包含@Repository注解的类中的所有方法。然后我们使用@Around通知来定位那个切入点，并确定被拦截方法调用的执行时间。

此外，使用这种方法，我们可以将日志记录、性能管理、审计和其他行为添加到每个应用程序层。

## 9. 总结

在本文中，我们介绍了Spring原型注解并讨论了它们各自代表的语义类型。

我们还学习了如何使用组件扫描来告诉容器在哪里可以找到带注解的类。