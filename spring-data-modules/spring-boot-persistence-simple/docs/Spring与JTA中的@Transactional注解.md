## 1. 概述

在本教程中，我们将讨论**org.springframework.transaction.annotation.Transactional和javax.transaction.Transactional注解之间的区别**。

我们将首先概述它们的配置属性。然后，我们将讨论它们适用于哪些类型的组件，以及在哪些情况下我们可以使用其中一种组件。

## 2. 配置差异

Spring的@Transactional注解与JTA对应的注解相比具有额外的配置：

-   隔离：Spring通过isolation属性提供事务范围内的隔离；然而，在JTA中，此功能仅在连接级别可用
-   传播：通过Spring中的propagation属性和Java EE中的value属性，该配置在两个库中都可用；Spring提供Nested作为额外的传播类型
-   只读：仅在Spring中通过readOnly属性可用
-   超时：仅在Spring中通过timeout属性可用
-   回滚：两个注解都提供回滚管理；JTA提供了rollbackOn和dontRollbackOn属性，而Spring有rollbackFor和noRollbackFor，以及两个额外的属性：rollbackForClassName和noRollbackForClassName

### 2.1 Spring事务注解配置

例如，下面我们在一个简单的CarService上使用和配置Spring @Transactional注解：

```java
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
      isolation = Isolation.READ_COMMITTED,
      propagation = Propagation.SUPPORTS,
      readOnly = false,
      timeout = 30)
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Transactional(
          rollbackFor = IllegalArgumentException.class,
          noRollbackFor = EntityExistsException.class,
          rollbackForClassName = "IllegalArgumentException",
          noRollbackForClassName = "EntityExistsException")
    public Car save(Car car) {
        return carRepository.save(car);
    }
}
```

### 2.2 JTA事务注解配置

让我们使用JTA @Transactional注解为简单的RentalService执行相同的操作：

```java
import javax.transaction.Transactional;

@Service
@Transactional(Transactional.TxType.SUPPORTS)
public class RentalService {

    @Autowired
    private CarRepository carRepository;

    @Transactional(
          rollbackOn = IllegalArgumentException.class,
          dontRollbackOn = EntityExistsException.class)
    public Car rent(Car car) {
        return carRepository.save(car);
    }
}
```

## 3. 适用性和互换性

JTA @Transactional注解适用于CDI管理的bean和Java EE规范定义为托管bean的类，而Spring @Transactional注解仅适用于Spring bean。

还值得注意的是，Spring 4.0中引入了对JTA 1.2的支持。因此，**我们可以在Spring应用程序中使用JTA的@Transactional注解**。但是，反过来是不可能的，因为我们不能在Spring上下文之外使用Spring注解。

## 4. 总结

在本教程中，我们讨论了Spring和JTA的事务注解之间的区别，以及何时可以使用其中一个或另一个。