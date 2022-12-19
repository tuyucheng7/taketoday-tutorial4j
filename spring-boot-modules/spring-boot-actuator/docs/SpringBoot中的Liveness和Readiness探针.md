## 1. 概述

在本教程中，我们将了解[Spring Boot 2.3](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes)如何与[Kubernetes探针]()集成，以创建更加友好的云原生体验。

首先，我们将从Kubernetes探针的一些背景知识开始，然后看看Spring Boot 2.3如何支持这些探针。

## 2. Kubernetes探针

当使用Kubernetes作为我们的编排平台时，每个节点中的[kubelet](https://kubernetes.io/docs/admin/kubelet/)负责保持该节点中的pod健康。

例如，有时我们的应用程序可能需要一点时间才能接受请求，kubelet可以确保应用程序仅在准备就绪时才收到请求。此外，如果pod的主进程因任何原因崩溃，kubelet将重新启动容器。

为了履行这些职责，Kubernetes有两个探针：liveness探针和readiness探针。

**kubelet将使用readiness探针来确定应用程序何时准备好接受请求**。更具体地说，当所有容器都准备就绪时，一个pod就准备好了。

同样，**kubelet可以通过liveness探针检查pod是否仍然存在**。基本上，liveness探针可以帮助kubelet知道什么时候应该重新启动容器。

现在我们已经熟悉了这些概念，让我们看看Spring Boot集成是如何工作的。

## 3. Actuator的就绪和活动状态

从Spring Boot 2.3开始，[LivenessStateHealthIndicator](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/availability/LivenessStateHealthIndicator.java)和[ReadinessStateHealthIndicator](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/availability/ReadinessStateHealthIndicator.java)类将公开应用程序的活跃性和就绪状态，当我们将应用程序部署到Kubernetes时，Spring Boot会自动注册这些健康指标。

**因此，我们可以分别使用/actuator/health/liveness和/actuator/health/readiness端点作为我们的liveness和readiness探针**。

例如，我们可以将这些添加到我们的pod定义中，以将liveness探针配置为HTTP GET请求：

```yaml
livenessProbe:
    httpGet:
        path: /actuator/health/liveness
        port: 8080
        initialDelaySeconds: 3
        periodSeconds: 3
```

我们通常会让Spring Boot决定何时为我们建立这些探针。但是，如果我们愿意，我们可以在我们的application.properties中手动启用它们。

如果我们使用Spring Boot 2.3.0或2.3.1，我们可以通过配置属性启用上述探针：

```properties
management.health.probes.enabled=true
```

但是，**自Spring Boot 2.3.2起，由于[配置混乱](https://github.com/spring-projects/spring-boot/issues/22107)，此属性已被弃用**。

如果我们使用Spring Boot 2.3.2，我们可以使用新属性来启用liveness和readiness探针：

```properties
management.endpoint.health.probes.enabled=true
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
```

### 3.1 就绪和活跃状态转换

Spring Boot使用两个枚举来封装不同的就绪状态和活动状态。对于就绪状态，有一个名为[ReadinessState](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/availability/ReadinessState.java)的枚举，其值如下：

-   ACCEPTING_TRAFFIC状态表示应用程序已准备好接受流量 
-   REFUSING_TRAFFIC状态意味着应用程序还不愿意接受任何请求

同样，[LivenessState](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/availability/LivenessState.java)枚举使用两个值表示应用程序的活动状态：

-   CORRECT值表示应用程序正在运行且其内部状态正确
-   另一方面，BROKEN值意味着应用程序正在运行，但出现了一些致命故障

以下是Spring中应用程序生命周期事件方面的就绪性和活动状态的变化方式：

1.  注册监听器和初始化器
2.  准备环境
3.  准备ApplicationContext
4.  加载bean定义
5.  **将活动状态更改为CORRECT**
6.  调用应用程序和命令行运行器
7.  **将就绪状态更改为ACCEPTING_TRAFFIC**

一旦应用程序启动并运行，我们(和Spring本身)就可以通过发布适当的[AvailabilityChangeEvents](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/availability/AvailabilityChangeEvent.java)来更改这些状态。

## 4. 管理应用程序可用性

应用程序组件可以通过注入[ApplicationAvailability](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/availability/ApplicationAvailability.java)接口来检索当前的就绪和活动状态：

```java
@Autowired private ApplicationAvailability applicationAvailability;
```

然后我们可以如下使用它：

```java
assertThat(applicationAvailability.getLivenessState())
      .isEqualTo(LivenessState.CORRECT);
assertThat(applicationAvailability.getReadinessState())
      .isEqualTo(ReadinessState.ACCEPTING_TRAFFIC);
assertThat(applicationAvailability.getState(ReadinessState.class))
      .isEqualTo(ReadinessState.ACCEPTING_TRAFFIC);
```

### 4.1 更新可用性状态

我们还可以通过发布AvailabilityChangeEvent事件来更新应用程序状态：

```java
assertThat(applicationAvailability.getLivenessState())
      .isEqualTo(LivenessState.CORRECT);
mockMvc.perform(get("/actuator/health/liveness"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("UP"));

AvailabilityChangeEvent.publish(context, LivenessState.BROKEN);

assertThat(applicationAvailability.getLivenessState())
      .isEqualTo(LivenessState.BROKEN);
mockMvc.perform(get("/actuator/health/liveness"))
      .andExpect(status().isServiceUnavailable())
      .andExpect(jsonPath("$.status").value("DOWN"));
```

如上所示，在发布任何事件之前，/actuator/health/liveness端点会返回具有以下JSON的200 OK响应：

```json
{
    "status": "OK"
}
```

然后在中断活动状态后，同一个端点将返回具有以下JSON的503服务不可用响应：

```json
{
    "status": "DOWN"
}
```

当我们更改为REFUSING_TRAFFIC就绪状态时，状态值将为 OUT_OF_SERVICE：

```java
assertThat(applicationAvailability.getReadinessState())
      .isEqualTo(ReadinessState.ACCEPTING_TRAFFIC);
mockMvc.perform(get("/actuator/health/readiness"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("UP"));

AvailabilityChangeEvent.publish(context, ReadinessState.REFUSING_TRAFFIC);

assertThat(applicationAvailability.getReadinessState())
      .isEqualTo(ReadinessState.REFUSING_TRAFFIC);
mockMvc.perform(get("/actuator/health/readiness"))
      .andExpect(status().isServiceUnavailable())
      .andExpect(jsonPath("$.status").value("OUT_OF_SERVICE"));
```

### 4.2 监听变化

我们可以注册事件监听器，以便在应用程序可用性状态发生变化时收到通知：

```java
@Component
public class LivenessEventListener {

    @EventListener
    public void onEvent(AvailabilityChangeEvent<LivenessState> event) {
        switch (event.getState()) {
            case BROKEN:
                // notify others
                break;
            case CORRECT:
                // we're back
        }
    }
}
```

在这里，我们正在监听应用程序活动状态的任何变化。

## 5. 自动配置

在结束之前，让我们看看Spring Boot如何在Kubernetes部署中自动配置这些探针。[AvailabilityProbesAutoConfiguration](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/availability/AvailabilityProbesAutoConfiguration.java)类负责有条件地注册liveness和readiness探针。

事实上，有一个特殊[条件](https://github.com/spring-projects/spring-boot/blob/ba7a42088e86e442c327c7a5143cd275a92ae844/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/availability/AvailabilityProbesAutoConfiguration.java#L74)可以在满足以下条件之一时注册探针：

-   [Kubernetes](https://github.com/spring-projects/spring-boot/blob/ba7a42088e86e442c327c7a5143cd275a92ae844/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/availability/AvailabilityProbesAutoConfiguration.java#L88)是部署环境
-   [management.health.probes.enabled](https://github.com/spring-projects/spring-boot/blob/ba7a42088e86e442c327c7a5143cd275a92ae844/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/availability/AvailabilityProbesAutoConfiguration.java#L82)属性设置为true

当应用程序满足上述任一条件时，自动配置会注册LivenessStateHealthIndicator和ReadinessStateHealthIndicator bean。

## 6. 总结

在本文中，我们介绍了如何使用Spring Boot为Kubernetes集成提供两个健康探针。