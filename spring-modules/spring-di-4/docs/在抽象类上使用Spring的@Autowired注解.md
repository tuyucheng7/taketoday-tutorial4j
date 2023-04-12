## 1. 概述

在本教程中，我们将介绍如何在抽象类中使用@Autowired注解。

我们将@Autowired应用于抽象类，并关注我们应该考虑的重点。

## 2. Setter注入

我们可以在setter方法上使用@Autowired：

```java
public abstract class BallService {
    private LogRepository logRepository;

    @Autowired
    public final void setLogRepository(LogRepository logRepository) {
        this.logRepository = logRepository;
    }
}
```

当我们在setter方法上使用@Autowired时，我们应该使用final关键字，**这样子类就不能重写setter方法**。否则，注解将无法按我们的预期工作。

## 3. 构造注入

**我们不能在抽象类的构造函数上使用@Autowired**。

Spring不会扫描抽象类构造函数上的@Autowired注解。子类应该为父类构造函数提供必要的参数。

相反，我们应该在子类的构造函数上使用@Autowired：

```java
public abstract class BallService {
    private RuleRepository ruleRepository;

    public BallService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }
}

@Component
public class BasketballService extends BallService {

    @Autowired
    public BasketballService(RuleRepository ruleRepository) {
        super(ruleRepository);
    }
}
```

## 4. 要点

让我们总结一下要记住的一些规则。

首先，**抽象类不会被当作组件扫描**，因为没有具体的子类就无法实例化它。

其次，**setter注入在抽象类中是可能的**，但如果我们不对setter方法使用final关键字，就会有风险。如果子类重写setter方法，应用程序可能不稳定。

第三，由于Spring不支持在抽象类中使用构造注入，**我们一般应该让具体的子类提供父类构造函数参数**。
这意味着**我们需要依赖于具体子类中的构造注入**。

最后，对必需依赖项使用构造注入，对可选依赖项使用setter注入是一个很好的经验总结。
然而，正如我们所看到的抽象类的一些细微差别，**构造注入在这里通常更有利**。

所以，实际上我们可以说，**一个具体的子类控制着它的抽象父类如何获得它的依赖项**。只要Spring注入子类，Spring就会执行注入。

## 5. 总结

在本文中，我们介绍了在抽象类中使用@Autowired并解释了一些重要的关键点。