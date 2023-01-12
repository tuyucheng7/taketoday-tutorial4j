## 1. 概述

本文将重点讲解Spring **BeanFactory API**。

BeanFactory接口提供了一种简单而灵活的配置机制，可以通过Spring IoC容器管理任何性质的对象。
在深入介绍这个核心Spring API之前，让我们先了解一些基础知识。

## 2. Bean和容器

简而言之，bean是构成Spring应用程序主干的Java对象，由Spring IoC容器管理。
除了由容器管理之外，bean没有什么特殊之处(在所有其他方面，它是应用程序中的众多对象之一)。

Spring容器负责实例化、配置和组装bean。容器通过读取我们为应用程序定义的配置元数据来获取有关要实例化、配置和管理哪些对象的信息。

## 3. BeanFactory接口

首先我们看看org.springframework.beans.factory包中的接口定义并在此讨论它的一些重要API。

### 3.1 getBean()

各种重载的getBean()方法返回指定bean的实例，该实例可以在应用程序中共享或独立。

### 3.2 containsBean()

此方法确认此bean工厂是否包含具有给定名称的bean。更具体地说，它确认getBean(java.lang.String)是否能够获得具有给定名称的bean实例。

### 3.3 isSingleton()

isSingleton() API可用于查询此bean是否为共享单例。也就是说，getBean(java.lang.String)将始终返回相同的实例。

### 3.4 isPrototype()

此API将确认getBean(java.lang.String)是否返回独立实例，这意味着配置了原型作用域的bean。

需要注意的重要一点是，即使这个方法返回false也不代表bean为单例。它仅表示非原型实例，也可能对应于其他作用域。

我们需要使用isSingleton(java.lang.String)方法来显式检查bean是否为共享单例。

### 3.5 其他API

虽然isTypeMatch(String name, Class targetType)方法可以检查具有给定名称的bean是否与指定类型匹配，
但getType(String name)在识别具有给定名称的bean的类型方面更有用。

最后，getAliases(String name)返回给定bean名称的别名(如果有)。

## 4. BeanFactory API

BeanFactory保存bean定义信息并在客户端应用程序请求时实例化它们，这意味着：

+ 它通过实例化bean并调用适当的销毁方法来处理bean的生命周期。
+ 它能够在实例化包含依赖的对象时创建它们之间的关联。
+ 重要的是，BeanFactory不支持基于注解的依赖注入，而ApplicationContext是BeanFactory的强力扩展。

## 5. 定义Bean

让我们定义一个简单的bean：

```java
public class Employee {
    private String name;
    private int age;
    // constructors, getters and setters
}
```

## 6. 使用XML配置BeanFactory

我们可以使用XML配置BeanFactory。让我们创建一个beanfactory-example.xml文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <bean id="employee" class="cn.tuyucheng.taketoday.beanfactory.Employee">
        <constructor-arg name="name" value="Hello! My name is Java"/>
        <constructor-arg name="age" value="18"/>
    </bean>

    <alias name="employee" alias="empalias"/>
</beans>
```

请注意，我们还为Employee bean创建了一个别名。

## 7. BeanFactory和ClassPathResource

ClassPathResource属于org.springframework.core.io包。
让我们编写一个测试并使用ClassPathResource初始化XmlBeanFactory，如下所示：

```java
class BeanFactoryWithClassPathResourceIntegrationTest {

    @Test
    void createBeanFactoryAndCheckEmployeeBean() {
        Resource res = new ClassPathResource("beanfactory-example.xml");
        BeanFactory factory = new XmlBeanFactory(res);
        Employee emp = (Employee) factory.getBean("employee");

        assertTrue(factory.isSingleton("employee"));
        assertTrue(factory.getBean("employee") instanceof Employee);
        assertTrue(factory.isTypeMatch("employee", Employee.class));
        assertTrue(factory.getAliases("employee").length > 0);
    }
}
```

## 8. 总结

在这篇文章中，我们了解了Spring BeanFactory API提供的主要方法，并通过一个示例来说明配置及其用法。