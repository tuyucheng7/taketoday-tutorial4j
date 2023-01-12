## 1. 概述

Bean是Spring框架的一个关键概念。因此，理解这个概念对于掌握框架并以有效的方式使用它至关重要。

**不幸的是，对于什么是Spring bean这个简单的问题，没有明确的答案**。有些解释太低级，以至于忽略了关键，而另一些解释太过模糊。

本文旨在从官方文档中的描述开始，阐明这个主题。

## 2. Bean Definition

以下是Spring官方文档中对bean的定义：

> 在Spring中，构成应用程序主干并由Spring IoC容器管理的对象称为bean。bean是由Spring IoC容器实例化、组装和管理的对象。

这个定义简洁明了，**但没有详细阐述一个重要元素：Spring IoC容器**。让我们仔细看看它是什么以及它带来的好处。

## 3. 控制反转(Inversion of Control)

简单地说，**控制反转(IoC)是一个对象定义其依赖关系而不创建它们的过程**。该对象将构造此类依赖项的任务委托给IoC容器。

在深入介绍IoC之前，让我们先声明几个域类。

### 3.1 域类

假设我们有以下类声明：

```java

@Data
public class Company {
    private Address address;

    public Company(Address address) {
        this.address = address;
    }
}
```

该类包含一个Address类型的字段：

```java

@Data
public class Address {
    private String street;
    private int number;

    public Address(String street, int number) {
        this.street = street;
        this.number = number;
    }
}
```

### 3.2 传统方式

通常，我们使用类的构造函数创建对象：

```text
Address address = new Address("High Street", 1000);
Company company = new Company(address);
```

这种方法并没有什么错，但是以更好的方式管理对象依赖关系不是更好吗？

假设一个应用程序有几十个甚至数百个类。有时我们希望在整个应用程序中共享一个类的单个实例，有时我们需要为每个用例使用一个单独的对象，等等。

管理如此多的对象简直是一场噩梦。**这就是控制反转出现的原因所在**。

对象可以从IoC容器中获取其依赖项，而不是自己构建依赖项。**我们需要做的就是为容器提供适当的配置元数据**。

### 3.3 Bean配置

首先，让我们用@Component注解标注Company类：

```java

@Component
public class Company {
    // this body is the same as before
}
```

下面是一个向IoC容器提供bean元数据的配置类：

```java

@Configuration
@ComponentScan(basePackageClasses = Company.class)
public class Config {

    @Bean
    public Address getAddress() {
        return new Address("High Street", 1000);
    }
}
```

配置类产生一个Address类型的bean。它还带有@ComponentScan注解，该注解指示容器在包含Company类的包中查找bean。

**当Spring IoC容器构造这些类型的对象时，所有对象都称为Spring beans，因为它们是由IoC容器管理的**。

### 3.4 IoC实践

由于我们在配置类中定义了bean，**因此需要AnnotationConfigApplicationContext类的实例来构建容器**：

```text
ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
```

然后我们可以从容器中获取Company类型的bean，并验证它的成员变量address已成功注入：

```text
Company company = context.getBean("company", Company.class);
assertEquals("High Street", company.getAddress().getStreet());
assertEquals(1000, company.getAddress().getNumber());
```

## 4. 总结

本文简要描述了Spring Bean及其与IoC容器的关系。