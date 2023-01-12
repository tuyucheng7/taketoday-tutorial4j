## 1. 概述

在这个教程中，我们介绍Spring中的UnsatisfiedDependencyException异常、导致它的原因以及如何避免它。

## 2. UnsatisfiedDependencyException的原因

**顾名思义，当某些bean或属性依赖关系不满足时，就会抛出UnsatisfiedDependencyException**。

当Spring应用程序尝试注入bean并且无法解析其中一个强制依赖项时，可能会发生这种情况。

## 3. 案例

假设我们有一个Service类PurchaseDeptService，它依赖于InventoryRepository：

```java

@Service
public class PurchaseDeptService {

    private InventoryRepository repository;

    public PurchaseDeptService(@Qualifier("dresses") InventoryRepository repository) {
        this.repository = repository;
    }
}

@Repository
public interface InventoryRepository {

}

@Repository
public class ShoeRepository implements InventoryRepository {

}
```

```java

@SpringBootApplication
public class SpringDependenciesExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDependenciesExampleApplication.class, args);
    }
}
```

现在，我们假设所有这些类都位于同一个名为cn.tuyucheng.taketoday.dependency.exception.app的包中。

当我们运行这个Spring Boot应用程序时，一切正常。

让我们看看如果我们跳过一个配置步骤，会遇到什么样的问题。

## 4. 缺少组件注解

现在我们从ShoeRepository类中删除@Repository注解：

```java
public class ShoeRepository implements InventoryRepository {
}
```

当我们再次启动应用程序时，会看到以下错误消息：

```text
UnsatisfiedDependencyException: Error created bean with name ‘purchaseDeptService’: Unsatisfied dependency express through constructor parameter 0
```

我们没有指定Spring将ShoeRepository作为bean并将其添加到应用程序上下文中，因此Spring无法注入它并引发异常。

## 5. 未扫描包

现在我们将ShoeRepository(连同InventoryRepository)放入一个名为cn.tuyucheng.taketoday.dependency.exception.repository的单独包中。

当我们再次运行应用程序时，它会抛出UnsatisfiedDependencyException。

为了解决这个问题，我们可以在父包上配置包扫描，并确保包含所有相关类：

```java

@SpringBootApplication
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.dependency.exception")
public class SpringDependenciesExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDependenciesExampleApplication.class, args);
    }
}
```

## 6. 非唯一依赖解析

假设我们添加另一个InventoryRepository的实现类DressRepository：

```java

@Repository
public class DressRepository implements InventoryRepository {
}
```

现在，当我们运行应用程序时，它会再次抛出UnsatisfiedDependencyException。

然而，这一次的情况有所不同，**当有多个bean满足依赖关系时，Spring无法正确解析依赖关系**。

为了解决这个问题，我们需要添加@Qualifier注解来区分InventoryRepository接口的实现类：

```java

@Qualifier("dresses")
@Repository
public class DressRepository implements InventoryRepository {
}

@Qualifier("shoes")
@Repository
public class ShoeRepository implements InventoryRepository {
}
```

此外，我们必须向PurchaseDeptService构造函数的参数添加一个@Qualifier：

```java
public class PurchaseDeptService {

    public PurchaseDeptService(@Qualifier("dresses") InventoryRepository repository) {
        this.repository = repository;
    }
}
```

这使DressRepository成为唯一可行的选择，Spring会将其注入到PurchaseDeptService。

## 7. 总结

在本文中，我们演示了抛出UnsatisfiedDependencyException异常的几个最常见的案例，然后我们说明了如何解决这些问题。