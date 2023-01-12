## 1. 概述

在本教程中，我们介绍Spring AOP切入点表达式语言。

首先，让我们回顾一些在面向切面编程中使用的术语。
连接点是程序执行的一个步骤，例如方法的执行或异常的处理，在Spring AOP中，一个连接点总是代表一个方法的执行。
切入点是匹配连接点的谓词，切入点表达式语言是一种以编程方式描述切入点的方式。

## 2. 用法

切入点表达式可以配置为@Pointcut注解的value属性值使用：

```java

@Aspect
@Component
public class PerformanceAspect {

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryClassMethods() {
    }
}
```

**方法声明称为切入点表达式签名**。它提供了一个名称，通知注解可以使用该名称来引用该切入点。

```java

@Aspect
@Component
public class PerformanceAspect {

    @Around("repositoryClassMethods()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
    }
}
```

切入点表达式也可以作为<aop:pointcut\>标签的expression属性的值指定：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop-4.2.xsd">

    <bean id="performanceMeter" class="cn.tuyucheng.taketoday.pointcutadvice.PerformanceAspect"/>
    <bean id="fooDao" class="cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao"/>

    <aop:config>
        <aop:pointcut id="anyDaoMethod" expression="@target(org.springframework.stereotype.Repository)"/>
        <aop:aspect ref="performanceMeter">
            <aop:around method="measureMethodExecutionTime" pointcut-ref="anyDaoMethod"/>
        </aop:aspect>
    </aop:config>
</beans>
```

## 3. 切入点指示符

**切入点表达式以切入点指示符(PCD)开头**，这是一个关键字，用于告诉Spring AOP要匹配什么。
有几个切入点指示符，例如方法的execution、type、args或annotations。

### 3.1 execution

主要的Spring PCD是execution，它匹配方法执行连接点。

```java

@Component
@Aspect
public class PublishingAspect {

    @Pointcut("execution(public String cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.findById(Long))")
    public void repositoryMethods() {
    }
}
```

以上切入点将与FooDao类的findById方法执行完全匹配。这是可行的，但不是很灵活。
假设我们想要匹配FooDao类的所有方法，这些方法可能具有不同的签名、返回类型和参数。为此，我们可以使用通配符：

```java

public class PublishingAspect {

    @Pointcut("execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.*(..))")
    public void repositoryMethods() {
    }
}
```

这里，第一个通配符“*”匹配任何返回值，第二个匹配任何方法名称，并且(..)模式匹配任意数量的参数(零个或多个)。

### 3.2 within

实现与上一节相同结果的另一种方法是within PCD，它将匹配限制为某些类型的连接点。

```java

public class PublishingAspect {

    @Pointcut("within(cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao)")
    public void repositoryMethods() {
    }
}
```

我们还可以匹配cn.tuyucheng.taketoday包或子包中的任何类型。

```java

public class PublishingAspect {

    @Pointcut("within(cn.tuyucheng.taketoday..*)")
    public void repositoryMethods() {
    }
}
```

### 3.3 this和target

this将匹配限制在bean引用是给定类型实例的连接点，而target将限制匹配到目标对象是给定类型实例的连接点。
前者在Spring AOP创建基于CGLIB的代理时起作用，后者在创建基于JDK的代理时使用。假设目标类实现了一个接口：

```java
public class FooDao implements BarDao {
    // ...
}
```

在这种情况下，Spring AOP将使用基于JDK的动态代理，此时我们应该使用target PCD，因为代理对象将是Proxy类的实例并实现BarDao接口：

```text
@Pointcut("target(cn.tuyucheng.taketoday.pointcutadvice.dao.BarDao)")
```

另一方面，如果FooDao没有实现任何接口，或者proxyTargetClass属性设置为true，
那么代理对象将是FooDao的子类，我们可以使用this PCD：

```text
@Pointcut("this(cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao)")
```

### 3.4 args

这个PCD用于匹配特定的方法参数：

```text
@Pointcut("execution(* *..find*(Long))")
```

以上切入点匹配任何以find开头并且只有一个Long类型参数的方法。
如果我们想匹配具有任意数量的参数，但第一个参数为Long类型的方法，我们可以使用以下表达式：

```text
@Pointcut("execution(* *..find*(Long,..))")
```

### 3.5 @target

@target PCD(不要与之前讲的target PCD混淆)将匹配限制为执行对象的类具有给定类型注解的连接点：

```text
@Pointcut("@target(org.springframework.stereotype.Repository)")
```

### 3.6 @args

此PCD将匹配限制为传递的实际参数的运行时类型具有给定类型注解的连接点。
假设我们要跟踪所有接收带有@Entity注解的bean的方法：

```java
public class LoggingAspect {

    @Pointcut("within(cn.tuyucheng.taketoday..*) && @args(cn.tuyucheng.taketoday.pointcutadvice.annotations.Entity)")
    public void methodsAcceptingEntities() {
    }
}
```

要访问参数，我们应该为通知提供一个JoinPoint类型的参数：

```java
public class LoggingAspect {

    @Before("methodsAcceptingEntities()")
    public void logMethodAcceptingEntityAnnotatedBean(JoinPoint joinPoint) {
        LOGGER.info("Accepting beans with @Entity annotation: " + joinPoint.getArgs()[0]);
    }
}
```

### 3.7 @within

此PCD将匹配限制为具有给定注解的类型内的连接点：

```text
@Pointcut("@within(org.springframework.stereotype.Repository)")
```

这相当于：

```text
@Pointcut("within(@org.springframework.stereotype.Repository *)")
```

### 3.8 @annotation

此PCD将匹配限制为连接点的主题具有给定注解的连接点，例如我们可以创建一个@Loggable注解：

```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Loggable {

}
```

```java
public class LoggingAspect {

    @Pointcut("@annotation(cn.tuyucheng.taketoday.pointcutadvice.annotations.Loggable)")
    public void loggableMethods() {
    }
}
```

然后我们可以记录由该注解标记的方法的执行：

```java

@Repository
public class FooDao {

    @Loggable
    public Foo create(Long id, String name) {
        return new Foo(id, name);
    }
}
```

## 4. 组合切入点表达式

切入点表达式也可以使用&&、||和!运算符组合：

```java

@Component
@Aspect
public class PublishingAspect {

    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Pointcut("within(cn.tuyucheng.taketoday..*)")
    public void repositoryMethods() {
    }

    @Pointcut("execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.create*(Long,..))")
    public void firstLongParamMethods() {
    }

    @Pointcut("repositoryMethods() && firstLongParamMethods()")
    public void entityCreationMethods() {
    }
}
```

## 5. 总结

在本文中，我们演示了切入点表达式使用的一些代码示例。