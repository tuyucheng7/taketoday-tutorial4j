## 1. 概述

Spring Boot中的FailureAnalyzer提供了一种方法来**拦截应用程序启动过程中发生的导致应用程序启动失败的异常**。

FailureAnalyzer将异常的堆栈跟踪替换为更易读的消息，该消息由包含错误描述和建议的操作过程的FailureAnalysis对象表示。

Boot包含一系列常见启动异常的分析器，例如PortInUseException、NoUniqueBeanDefinitionException和UnsatisfiedDependencyException，这些可以在org.springframework.boot.diagnostics包中找到。

在这个快速教程中，我们将了解**如何将我们自己的自定义FailureAnalyzer添加到现有的FailureAnalyzer中**。

## 2. 创建自定义FailureAnalyzer

要创建自定义的FailureAnalyzer，我们将简单地扩展抽象类AbstractFailureAnalyzer-它拦截指定的异常类型并实现analyze() API。

该框架提供了一个BeanNotOfRequiredTypeFailureAnalyzer实现，仅当注入的bean属于动态代理类时才处理异常BeanNotOfRequiredTypeException。

让我们创建一个自定义的FailureAnalyzer来处理BeanNotOfRequiredTypeException类型的所有异常。我们的类拦截异常并创建一个包含有用描述和操作消息的FailureAnalysis对象：

```java
public class MyBeanNotOfRequiredTypeFailureAnalyzer extends AbstractFailureAnalyzer<BeanNotOfRequiredTypeException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, BeanNotOfRequiredTypeException cause) {
        return new FailureAnalysis(getDescription(cause), getAction(cause), cause);
    }

    private String getDescription(BeanNotOfRequiredTypeException ex) {
        return String.format("The bean %s could not be injected as %s because it is of type %s", ex.getBeanName(), ex.getRequiredType().getName(), ex.getActualType().getName());
    }

    private String getAction(BeanNotOfRequiredTypeException ex) {
        return String.format("Consider creating a bean with name %s of type %s", ex.getBeanName(), ex.getRequiredType().getName());
    }
}
```

## 3. 注册自定义FailureAnalyzer

对于Spring Boot考虑的自定义FailureAnalyzer，必须将其注册到标准resources/META-INF/spring.factories文件中，该文件包含org.springframework.boot.diagnostics.FailureAnalyzer属性，其值为我们自定义FailureAnalyzer的全限定类名：

```properties
org.springframework.boot.diagnostics.FailureAnalyzer=\
  cn.tuyucheng.taketoday.failureanalyzer.MyBeanNotOfRequiredTypeFailureAnalyzer
```

## 4. 自定义FailureAnalyzer的实际应用

让我们创建一个非常简单的示例，在该示例中我们尝试注入一个不正确类型的bean以查看我们自定义FailureAnalyzer的行为。

让我们创建两个类MyDAO和MySecondDAO，并将第二个类标注为名为myDAO的bean：

```java
public class MyDAO {
}
```

```java
@Repository("myDAO")
public class MySecondDAO {
}
```

接下来，在MyService类中，我们将尝试将类型为MySecondDAO的myDAO bean注入到类型为MyDAO的变量中：

```java
@Service
public class MyService {

    @Resource(name = "myDAO")
    private MyDAO myDAO;
}
```

运行Spring Boot应用程序后，启动将失败并显示以下控制台输出：

```shell
***************************
APPLICATION FAILED TO START
***************************

Description:

The bean myDAO could not be injected as cn.tuyucheng.taketoday.failureanalyzer.MyDAO because it is of type cn.tuyucheng.taketoday.failureanalyzer.MySecondDAO

Action:

Consider creating a bean with name myDAO of type cn.tuyucheng.taketoday.failureanalyzer.MyDAO
```

## 5. 总结

在这个快速教程中，我们重点介绍了如何实现自定义Spring Boot FailureAnalyzer。