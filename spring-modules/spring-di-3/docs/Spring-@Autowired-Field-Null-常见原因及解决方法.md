## 1. 概述

在本文中，我们将了解导致使用@Autowired标注的字段出现NullPointerException的常见错误。我们还将解释如何解决该问题。

## 2. 问题陈述

首先，让我们定义一个带有空doWork方法的Spring组件：

```java

@Component
public class MyComponent {

  public void doWork() {

  }
}
```

然后，让我们定义我们的Service类。我们将使用Spring在我们的Service中注入一个MyComponent bean，
以便我们可以在Service方法中调用doWork方法：

```java
public class MyService {
  @Autowired
  MyComponent myComponent;

  public String serve() {
    myComponent.doWork();
    return "success";
  }
}
```

现在，让我们添加一个控制器，它将实例化一个Service并调用serve方法：

```java

@Controller
public class FlawedController {

  public String control() {
    MyService userService = new MyService();
    return userService.serve();
  }
}
```

乍一看，我们的代码看起来没有任何问题。但是，运行应用程序后，调用我们控制器的control方法会导致如下异常：

```
java.lang.NullPointerException: null
  at cn.tuyucheng.taketoday.autowiring.service.MyService.serve(MyService.java:14)
  at cn.tuyucheng.taketoday.autowiring.controller.MyController.control(MyController.java:14)
```

这里发生了什么？当我们在控制器中调用MyService构造函数时，我们创建的是一个不受Spring管理的对象。
由于不知道这个MyService对象的存在，Spring无法在其中注入MyComponent bean。
因此，我们创建的MyService对象中的MyComponent实例将保持为空，从而导致我们在尝试调用该对象的方法时得到NullPointerException。

## 3. 解决方案

为了解决这个问题，我们必须将控制器中使用的MyService实例设为Spring管理的Bean。

首先，我们告诉Spring为我们的MyService类生成一个Bean。我们有多种方法来实现这一目标。
最简单的方法是使用@Component注解或任何衍生注解来修饰MyService类。例如，我们可以执行以下操作：

```java

@Service
public class MyService {
  @Autowired
  MyComponent myComponent;

  public String serve() {
    myComponent.doWork();
    return "success";
  }
}
```

另一种方法是在@Configuration类中添加@Bean方法：

```java

@Configuration
public class MyServiceConfiguration {

  @Bean
  MyService myService() {
    return new MyService();
  }
}
```

但是，将MyService类转换为Spring管理的bean是不够的。现在，我们必须在控制器内部自动注入它，而不是在它上面调用new。

```java

@Controller
public class CorrectController {
  @Autowired
  MyService myService;

  public String control() {
    return myService.serve();
  }
}
```

现在，调用control方法将按预期返回serve方法的结果。