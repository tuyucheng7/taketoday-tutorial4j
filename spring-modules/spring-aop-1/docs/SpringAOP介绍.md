## 1. 概述

在本教程中，我们介绍Spring的AOP(Aspect Oriented Programming)，并学习如何在实际场景中使用这个强大的工具。

在使用Spring AOP进行开发时，也可以使用AspectJ的注解，但在本文中，我们将重点关注基于XML的核心Spring AOP配置。

## 2. AOP

**AOP是一种编程范式，旨在通过允许分离横切关注点来提高模块化**，它通过在不修改代码本身的情况下向现有代码添加额外的行为来实现这一点。

相反，我们可以分别声明新代码和新行为。

Spring的AOP框架帮助我们实现这些横切关注点。

## 3. Maven依赖

首先在pom.xml中添加Spring的AOP库依赖：

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
        <version>2.6.1</version>
    </dependency>
</dependencies>
```

## 4. AOP概念和术语

让我们简要回顾一下AOP特有的概念和术语：

<img src="../assets/img-1.png">

### 4.1 业务对象

业务对象是具有正常业务逻辑的普通类，比如下面的SampleAdder类可以看作是一个业务对象，它只是简单的做加法运算：

```java
public class SampleAdder {

    public int add(int a, int b) {
        return a + b;
    }
}
```

注意这个类是一个普通的带有业务逻辑的类，没有任何与Spring相关的注解。

### 4.2 切面

切面是跨多个类的关注点的模块化，统一日志记录可以是这种横切关注点的一个例子。

让我们看看如何定义一个简单的切面：

```java
public class AdderAfterReturnAspect {

    public static final Logger LOGGER = LoggerFactory.getLogger(AdderAfterReturnAspect.class);

    public void afterReturn(final Object returnValue) throws Throwable {
        LOGGER.info("value return was {}", returnValue);
    }
}
```

在上面的例子中，我们定义了一个简单的Java类，该类具有一个名为afterReturn的方法，该方法接收一个Object类型的参数并通过日志记录该值。
请注意，我们的AdderAfterReturnAspect也是一个标准类，没有任何Spring注解。

在下一节中，我们说明如何将该切面织入到我们的业务对象。

### 4.3 连接点

**连接点是程序执行过程中的一个点，例如方法的执行或异常的处理**。

在Spring AOP中，连接点总是代表一个方法的执行。

### 4.4 切入点

切入点是一个谓词，有助于匹配切面在特定连接点应用的通知。

我们经常将通知与切入点表达式相关联，它在任何与切入点匹配的连接点处运行。

### 4.5 通知

通知是一个切面在特定连接点处采取的行动，不同类型的通知包括“Around”、“Before”和“After”。

在Spring中，通知被建模为一个拦截器，在连接点前后维护一个拦截器链。

### 4.6 连接业务对象和切面

现在让我们看看如何使用AfterReturning通知将业务对象连接到通知。

下面是我们放置在“<beans\>”标签中的标准Spring配置：

```xml

<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/aop 
       http://www.springframework.org/schema/aop/spring-aop-3.2.xsd 
       http://www.springframework.org/schema/beans 
       http://www.springframework.org/schema/beans/spring-beans-3.2.xsd">

    <bean id="sampleAdder" class="cn.tuyucheng.taketoday.logger.SampleAdder"/>
    <bean id="doAfterReturningAspect" class="cn.tuyucheng.taketoday.logger.AdderAfterReturnAspect"/>

    <aop:config>
        <aop:aspect id="aspects" ref="doAfterReturningAspect">
            <aop:pointcut id="pointCutAfterReturning"
                          expression="execution(* cn.tuyucheng.taketoday.logger.SampleAdder+.*(..))"/>
            <aop:after-returning method="afterReturn" returning="returnValue" pointcut-ref="pointCutAfterReturning"/>
        </aop:aspect>
    </aop:config>
</beans>
```

如我们所见，我们定义了一个名为simpleAdder的简单bean，它表示一个业务对象的实例。
此外，我们创建了一个名为AdderAfterReturnAspect的切面实例。

当然，XML不是我们的唯一选择。如前所述，也完全支持AspectJ注解。

### 4.7 配置一览

我们可以使用标签<aop:config\>来定义AOP相关的配置，在config标签中，我们定义了代表一个切面的类。
然后我们给它一个“doAfterReturningAspect”的引用，这是我们之前创建的一个切面bean。

接下来我们使用<aop:pointcut\>标签定义一个切入点。
上面示例中使用的切入点为execution(* cn.tuyucheng.taketoday.logger.SampleAdder+.*(..))，
这意味着对SampleAdder类中接收任意数量的参数并返回任意值类型的任何方法上应用通知。

然后我们定义想要应用的通知，在上面的示例中，我们应用after-returning类型的通知。
我们在切面AdderAfterReturnAspect中通过执行我们使用method属性定义的afterReturn方法来定义它。

切面中的这个通知接收Object类型的一个参数，该参数为我们提供了在目标方法调用之前和/或之后执行操作的机会。
在本例中，我们只是简单地记录方法的返回值。

## 5. 总结

在本文中，我们说明了AOP中涉及到的概念，并且演示了一个使用Spring AOP的案例。
如果想进一步了解AOP，可以查看以下文章：

- [Intro to AspectJ](./AspectJ简介.md)
- [Implementing a Custom Spring AOP Annotation](./实现自定义SpringAOP注解.md)
- [Introduction to Pointcut Expressions in Spring](./Spring切入点表达式介绍.md)
- [Comparing Spring AOP and AspectJ]()
- [Introduction to Advice Types in Spring](./Spring中的通知类型介绍.md)