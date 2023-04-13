## 1. 概述

在构建新的Spring Boot项目时，我们可以高度灵活地组织类。

尽管如此，我们还是需要牢记一些建议。

## 2. 无默认包

鉴于@ComponentScan、@EntityScan、@ConfigurationPropertiesScan和@SpringBootApplication等Spring Boot注解使用包来定义扫描位置，建议我们避免使用默认包-也就是说，**我们应该始终在我们的类中声明包**。

## 3. 主类

@SpringBootApplication注解触发当前包及其子包的组件扫描。因此，**一个可靠的方法是将项目的主类驻留在基包中**。

这是可配置的，我们仍然可以通过手动指定基础包将其定位到其他地方。但是，在大多数情况下，此选项肯定更简单。

更重要的是，一个基于JPA的项目需要在主类上有一些额外的注解：

```java
@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday")
@EnableJpaRepositories("cn.tuyucheng.taketoday")
@EntityScan("cn.tuyucheng.taketoday")
```

**另外，请注意可能需要额外的配置**。

## 4. 设计

包结构的设计独立于Spring Boot。因此，它应该由我们项目的要求强加。

**一种流行的策略是每个功能一个包**，它增强了模块化并在子包内实现包私有可见性。

让我们以[PetClinic](https://github.com/spring-projects/spring-petclinic)项目为例。这个项目是由Spring开发人员构建的，以说明他们对应该如何构建一个常见的Spring Boot项目的看法。

它以逐个功能的方式进行包组织。因此，我们有主包org.springframework.samples.petclinic和5个子包：

-   org.springframework.samples.petclinic.**model**
-   org.springframework.samples.petclinic.**owner**
-   org.springframework.samples.petclinic.**system**
-   org.springframework.samples.petclinic.**vet**
-   org.springframework.samples.petclinic.**visit**

它们中的每一个都代表应用程序的一个领域或一个功能，**将高度耦合的类分组在内部并实现高内聚**。

## 5. 总结

在这篇简短的文章中，我们了解了在构建Spring Boot项目时需要牢记的一些建议，并了解了如何设计包结构。