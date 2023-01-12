## 1. 简介

本文重点介绍 Spring 的[状态机项目](https://spring.io/projects/spring-statemachine)——它可用于表示工作流或任何其他类型的有限状态自动机表示问题。

## 2.Maven依赖

首先，我们需要添加主要的 Maven 依赖项：

```xml
<dependency>
    <groupId>org.springframework.statemachine</groupId>
    <artifactId>spring-statemachine-core</artifactId>
    <version>3.2.0.RELEASE</version>
</dependency>
```

此依赖项的最新版本可在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.statemachine" AND a%3A"spring-statemachine-core")找到。

## 3.状态机配置

现在，让我们开始定义一个简单的状态机：

```java
@Configuration
@EnableStateMachine
public class SimpleStateMachineConfiguration 
  extends StateMachineConfigurerAdapter<String, String> {

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) 
      throws Exception {
 
        states
          .withStates()
          .initial("SI")
          .end("SF")
          .states(
            new HashSet<String>(Arrays.asList("S1", "S2", "S3")));

    }

    @Override
    public void configure(
      StateMachineTransitionConfigurer<String, String> transitions) 
      throws Exception {
 
        transitions.withExternal()
          .source("SI").target("S1").event("E1").and()
          .withExternal()
          .source("S1").target("S2").event("E2").and()
          .withExternal()
          .source("S2").target("SF").event("end");
    }
}
```

请注意，此类被注解为传统的 Spring 配置以及状态机。它还需要扩展StateMachineConfigurerAdapter，以便调用各种初始化方法。在其中一种配置方法中，我们定义了状态机的所有可能状态，在另一种配置方法中，我们定义了事件如何改变当前状态。

上面的配置设置了一个非常简单的直线转换状态机，应该很容易理解。

[![SI-SF](https://www.baeldung.com/wp-content/uploads/2017/04/simple-300x69.png)](https://www.baeldung.com/wp-content/uploads/2017/04/simple.png)

现在我们需要启动一个 Spring 上下文并获取对我们配置定义的状态机的引用：

```java
@Autowired
private StateMachine<String, String> stateMachine;
```

一旦我们有了状态机，就需要启动它：

```java
stateMachine.start();
```

现在我们的机器处于初始状态，我们可以发送事件并触发转换：

```java
stateMachine.sendEvent("E1");
```

我们总是可以检查状态机的当前状态：

```java
stateMachine.getState();
```

## 4. 动作

让我们添加一些要围绕状态转换执行的操作。首先，我们在同一个配置文件中将我们的操作定义为一个 Spring bean：

```java
@Bean
public Action<String, String> initAction() {
    return ctx -> System.out.println(ctx.getTarget().getId());
}
```

然后我们可以在我们的配置类中注册上面创建的转换动作：

```java
@Override
public void configure(
  StateMachineTransitionConfigurer<String, String> transitions)
  throws Exception {
 
    transitions.withExternal()
      transitions.withExternal()
      .source("SI").target("S1")
      .event("E1").action(initAction())
```

当通过事件E1从SI到S1的转换发生时，将执行此操作。动作可以附加到状态本身：

```java
@Bean
public Action<String, String> executeAction() {
    return ctx -> System.out.println("Do" + ctx.getTarget().getId());
}

states
  .withStates()
  .state("S3", executeAction(), errorAction());
```

此状态定义函数接受当机器处于目标状态时要执行的操作，以及可选的错误操作处理程序。

错误操作处理程序与任何其他操作没有太大区别，但如果在评估状态操作期间的任何时间抛出异常，它将被调用：

```java
@Bean
public Action<String, String> errorAction() {
    return ctx -> System.out.println(
      "Error " + ctx.getSource().getId() + ctx.getException());
}
```

也可以为entry、do和exit状态转换注册单独的动作：

```java
@Bean
public Action<String, String> entryAction() {
    return ctx -> System.out.println(
      "Entry " + ctx.getTarget().getId());
}

@Bean
public Action<String, String> executeAction() {
    return ctx -> 
      System.out.println("Do " + ctx.getTarget().getId());
}

@Bean
public Action<String, String> exitAction() {
    return ctx -> System.out.println(
      "Exit " + ctx.getSource().getId() + " -> " + ctx.getTarget().getId());
}
states
  .withStates()
  .stateEntry("S3", entryAction())
  .state("S3", executeAction())
  .stateExit("S3", exitAction());
```

将在相应的状态转换上执行相应的操作。例如，我们可能希望在进入时验证一些前置条件或在退出时触发一些报告。

## 5.全球听众

可以为状态机定义全局事件侦听器。这些侦听器将在任何时候发生状态转换时被调用，并且可以用于日志记录或安全性等事情。

首先，我们需要添加另一种配置方法——一种不处理状态或转换但处理状态机本身的配置方法。

我们需要通过扩展StateMachineListenerAdapter来定义一个监听器：

```java
public class StateMachineListener extends StateMachineListenerAdapter {
 
    @Override
    public void stateChanged(State from, State to) {
        System.out.printf("Transitioned from %s to %s%n", from == null ? 
          "none" : from.getId(), to.getId());
    }
}
```

这里我们只覆盖stateChanged，尽管还有许多其他偶数钩子可用。

## 6.扩展状态

Spring State Machine 跟踪其状态，但要跟踪我们的应用程序状态，无论是一些计算值、来自管理员的条目还是来自调用外部系统的响应，我们需要使用所谓的扩展状态。

假设我们要确保一个帐户申请经过两个级别的批准。我们可以使用存储在扩展状态中的整数来跟踪批准计数：

```java
@Bean
public Action<String, String> executeAction() {
    return ctx -> {
        int approvals = (int) ctx.getExtendedState().getVariables()
          .getOrDefault("approvalCount", 0);
        approvals++;
        ctx.getExtendedState().getVariables()
          .put("approvalCount", approvals);
    };
}
```

## 7. 卫兵

守卫可用于在执行状态转换之前验证某些数据。守卫看起来与动作非常相似：

```java
@Bean
public Guard<String, String> simpleGuard() {
    return ctx -> (int) ctx.getExtendedState()
      .getVariables()
      .getOrDefault("approvalCount", 0) > 0;
}
```

这里明显的区别是守卫返回true或false，这将通知状态机是否应该允许转换发生。

还支持 SPeL 表达式作为守卫。上面的例子也可以写成：

```java
.guardExpression("extendedState.variables.approvalCount > 0")
```

## 8. 来自建造者的状态机

StateMachineBuilder可用于在不使用 Spring 注解或创建 Spring 上下文的情况下创建状态机：

```java
StateMachineBuilder.Builder<String, String> builder 
  = StateMachineBuilder.builder();
builder.configureStates().withStates()
  .initial("SI")
  .state("S1")
  .end("SF");

builder.configureTransitions()
  .withExternal()
  .source("SI").target("S1").event("E1")
  .and().withExternal()
  .source("S1").target("SF").event("E2");

StateMachine<String, String> machine = builder.build();
```

## 9.等级状态

可以通过结合使用多个withStates()和parent()来配置分层状态：

```java
states
  .withStates()
    .initial("SI")
    .state("SI")
    .end("SF")
    .and()
  .withStates()
    .parent("SI")
    .initial("SUB1")
    .state("SUB2")
    .end("SUBEND");
```

这种设置允许状态机具有多个状态，因此对getState()的调用将产生多个 ID。例如，在启动后立即出现以下表达式：

```java
stateMachine.getState().getIds()
["SI", "SUB1"]
```

## 10. 路口(选择)

到目前为止，我们已经创建了本质上是线性的状态转换。这不仅相当无趣，而且也没有反映开发人员将被要求实现的真实用例。很有可能需要实现条件路径，而 Spring 状态机的连接(或选择)允许我们做到这一点。

首先，我们需要在状态定义中将状态标记为连接点(选择)：

```java
states
  .withStates()
  .junction("SJ")
```

然后在转换中，我们定义了对应于 if-then-else 结构的 first/then/last 选项：

```java
.withJunction()
  .source("SJ")
  .first("high", highGuard())
  .then("medium", mediumGuard())
  .last("low")
```

首先，然后采用第二个参数，它是一个常规守卫，将被调用以找出采用哪条路径：

```java
@Bean
public Guard<String, String> mediumGuard() {
    return ctx -> false;
}

@Bean
public Guard<String, String> highGuard() {
    return ctx -> false;
}
```

请注意，转换不会在连接节点处停止，而是会立即执行定义的守卫并转到指定路线之一。

在上面的示例中，指示状态机转换为 SJ 将导致实际状态变低，因为两个守卫都返回 false。

最后要注意的是，API 提供了连接点和选择。但是，从功能上讲，它们在各个方面都是相同的。

## 11.叉子

有时需要将执行拆分为多个独立的执行路径。这可以使用fork功能来实现。

首先，我们需要指定一个节点作为分叉节点并创建层次区域，状态机将在其中执行拆分：

```java
states
  .withStates()
  .initial("SI")
  .fork("SFork")
  .and()
  .withStates()
    .parent("SFork")
    .initial("Sub1-1")
    .end("Sub1-2")
  .and()
  .withStates()
    .parent("SFork")
    .initial("Sub2-1")
    .end("Sub2-2");
```

然后定义分叉过渡：

```java
.withFork()
  .source("SFork")
  .target("Sub1-1")
  .target("Sub2-1");
```

## 12.加入

fork操作的补充是join。它允许我们设置一个状态转换到该状态取决于完成其他一些状态：

[![分叉连接](https://www.baeldung.com/wp-content/uploads/2017/04/forkjoin-300x160.png)](https://www.baeldung.com/wp-content/uploads/2017/04/forkjoin.png)

与分叉一样，我们需要在状态定义中指定一个连接节点：

```java
states
  .withStates()
  .join("SJoin")
```

然后在转换中，我们定义需要完成哪些状态才能启用我们的加入状态：

```java
transitions
  .withJoin()
    .source("Sub1-2")
    .source("Sub2-2")
    .target("SJoin");
```

而已！使用此配置，当Sub1-2和Sub2-2都实现时，状态机将转换为SJoin

## 13.用枚举代替字符串

在上面的示例中，为了清晰和简单起见，我们使用字符串常量来定义状态和事件。在真实世界的生产系统上，人们可能希望使用Java的枚举来避免拼写错误并获得更多的类型安全性。

首先，我们需要定义系统中所有可能的状态和事件：

```java
public enum ApplicationReviewStates {
    PEER_REVIEW, PRINCIPAL_REVIEW, APPROVED, REJECTED
}

public enum ApplicationReviewEvents {
    APPROVE, REJECT
}
```

当我们扩展配置时，我们还需要将我们的枚举作为通用参数传递：

```java
public class SimpleEnumStateMachineConfiguration 
  extends StateMachineConfigurerAdapter
  <ApplicationReviewStates, ApplicationReviewEvents>
```

一旦定义，我们就可以使用我们的枚举常量而不是字符串。例如定义一个转换：

```java
transitions.withExternal()
  .source(ApplicationReviewStates.PEER_REVIEW)
  .target(ApplicationReviewStates.PRINCIPAL_REVIEW)
  .event(ApplicationReviewEvents.APPROVE)
```

## 14.总结

本文探讨了 Spring 状态机的一些特性。