## 1. 概述

Spring允许我们将自定义操作附加到bean创建和销毁。例如，我们可以通过实现InitializingBean和DisposableBean接口来做到这一点。

在本教程中，我们将研究第二种可能性，@PostConstruct和@PreDestroy注解。

## 2. @PostConstruct

Spring仅在初始化bean属性之后调用使用@PostConstruct注释的方法一次。请记住，即使没有要初始化的内容，这些方法也会运行。

使用@PostConstruct 注解的方法可以具有任何访问级别，但不能是静态的。

@PostConstruct的一种可能用途是填充数据库。例如，在开发过程中，我们可能想要创建一些默认用户：