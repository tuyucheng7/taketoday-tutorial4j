## 1. 概述

Spring Boot中的自动配置机制尝试根据应用程序的依赖关系自动配置应用程序。

在这个快速教程中，我们将**了解Spring Boot如何在启动时记录其自动配置报告**。

## 2. 示例应用程序

让我们编写一个简单的Spring Boot应用程序，我们将在示例中使用它：

```java
@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
```

## 3. 应用程序属性方法

在启动这个应用程序时，我们没有得到很多关于Spring Boot如何或为什么决定编写应用程序配置的信息。

但是，**我们可以让Spring Boot创建一个报告，只需在我们的application.properties文件中启用调试模式**：

```properties
debug=true
```

或者我们的application.yml文件：

```yaml
debug: true
```

## 4. 命令行方法

或者，如果我们不想使用属性文件方法，我们可以通过**使用--debug选项启动应用程序来触发自动配置报告**：

```shell
java -jar myproject-1.0.0.jar --debug
```

## 5. 报告输出

自动配置报告包含有关Spring Boot在类路径中找到并自动配置的类的信息。它还显示有关Spring Boot已知但在类路径中找不到的类的信息。

而且，因为我们设置了debug=true，所以我们会在输出中看到它：

```shell
============================
CONDITIONS EVALUATION REPORT
============================


Positive matches:
-----------------

   AopAutoConfiguration matched:
      - @ConditionalOnClass found required classes 'org.springframework.context.annotation.EnableAspectJAutoProxy', 
        'org.aspectj.lang.annotation.Aspect', 'org.aspectj.lang.reflect.Advice', 'org.aspectj.weaver.AnnotatedElement'; 
        @ConditionalOnMissingClass did not find unwanted class (OnClassCondition)
      - @ConditionalOnProperty (spring.aop.auto=true) matched (OnPropertyCondition)

   AopAutoConfiguration.CglibAutoProxyConfiguration matched:
      - @ConditionalOnProperty (spring.aop.proxy-target-class=true) matched (OnPropertyCondition)

   AuditAutoConfiguration#auditListener matched:
      - @ConditionalOnMissingBean (types: org.springframework.boot.actuate.audit.listener.AbstractAuditListener; 
        SearchStrategy: all) did not find any beans (OnBeanCondition)

   AuditAutoConfiguration.AuditEventRepositoryConfiguration matched:
      - @ConditionalOnMissingBean (types: org.springframework.boot.actuate.audit.AuditEventRepository; 
        SearchStrategy: all) did not find any beans (OnBeanCondition)

   AuditEventsEndpointAutoConfiguration#auditEventsEndpoint matched:
      - @ConditionalOnBean (types: org.springframework.boot.actuate.audit.AuditEventRepository; 
        SearchStrategy: all) found bean 'auditEventRepository'; 
        @ConditionalOnMissingBean (types: org.springframework.boot.actuate.audit.AuditEventsEndpoint; 
        SearchStrategy: all) did not find any beans (OnBeanCondition)
      - @ConditionalOnEnabledEndpoint no property management.endpoint.auditevents.enabled found 
        so using endpoint default (OnEnabledEndpointCondition)


Negative matches:
-----------------

   ActiveMQAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required classes 'javax.jms.ConnectionFactory', 
           'org.apache.activemq.ActiveMQConnectionFactory' (OnClassCondition)

   AopAutoConfiguration.JdkDynamicAutoProxyConfiguration:
      Did not match:
         - @ConditionalOnProperty (spring.aop.proxy-target-class=false) did not find property 
           'proxy-target-class' (OnPropertyCondition)

   ArtemisAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required classes 'javax.jms.ConnectionFactory', 
           'org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory' (OnClassCondition)

   AtlasMetricsExportAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'io.micrometer.atlas.AtlasMeterRegistry' 
           (OnClassCondition)

   AtomikosJtaConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'com.atomikos.icatch.jta.UserTransactionManager' 
           (OnClassCondition)
```

## 6. 总结

在这个快速教程中，我们了解了如何显示和读取Spring Boot自动配置报告。