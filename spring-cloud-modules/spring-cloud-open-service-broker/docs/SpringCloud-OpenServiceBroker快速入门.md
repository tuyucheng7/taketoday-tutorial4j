## 1. 概述

在本教程中，**我们将介绍[Spring Cloud Open Service Broker](https://spring.io/projects/spring-cloud-open-service-broker)项目并学习如何实现[Open Service Broker API](https://www.openservicebrokerapi.org/)**。

首先，我们将深入了解Open Service Broker API的规范。然后，我们将学习如何使用Spring Cloud Open Service Broker来构建实现API规范的应用程序。

最后，我们将探讨可以使用哪些安全机制来保护我们的服务代理端点。

## 2. Open Service Broker API

**Open Service Broker API项目使我们能够快速为在Cloud Foundry和Kubernetes等云原生平台上运行的应用程序提供支持服务**。本质上，API规范描述了一组REST端点，通过这些端点我们可以配置和连接到这些服务。

特别是，我们可以在云原生平台中使用服务代理来：

-   播发后备服务目录
-   预配服务实例
-   创建和删除后背服务和客户端应用程序之间的绑定
-   取消预配服务实例

**Spring Cloud Open Service Broker通过提供所需的Web控制器、域对象和配置，为符合Open Service Broker API的实现奠定了基础**。此外，我们需要通过实施适当的[服务代理接口](https://docs.spring.io/spring-cloud-open-service-broker/docs/current/apidocs/org/springframework/cloud/servicebroker/service/package-summary.html)来提出我们的业务逻辑。

## 3. 自动配置

**为了在我们的应用程序中使用Spring Cloud Open Service Broker，我们需要添加相关的Starter工件**。我们可以使用Maven Central搜索最新版本的[open-service-broker starter](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-open-service-broker/3.6.0)。

除了Cloud Starter，我们还需要包括一个Spring Boot Web Starter，以及Spring WebFlux或Spring MVC，以激活自动配置：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-open-service-broker</artifactId>
    <version>3.1.1.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

自动配置机制为服务代理所需的大多数组件配置默认实现。如果需要，我们可以通过提供我们自己的open-service-broker Spring相关bean的实现来覆盖默认行为。

### 3.1 服务代理端点路径配置

默认情况下，注册服务代理端点的上下文路径是“/”。

如果这不理想并且我们想要更改它，最直接的方法是在我们的应用程序properties或yaml文件中设置属性spring.cloud.openservicebroker.base-path：

```yaml
spring:
    cloud:
        openservicebroker:
            base-path: /broker
```

在这种情况下，要查询服务代理端点，我们首先需要在我们的请求前加上/broker/基本路径。

## 4. 服务代理示例

让我们使用Spring Cloud Open Service Broker库创建一个服务代理应用程序并探索API的工作原理。

通过我们的示例，我们将使用服务代理来预配和连接到后背邮件系统。为简单起见，我们将使用代码示例中提供的虚拟邮件API。

### 4.1 服务目录

首先，**为了控制我们的服务代理提供哪些服务，我们需要定义一个服务目录**。为了快速初始化服务目录，在我们的示例中，我们将提供一个[Catalog](https://docs.spring.io/spring-cloud-open-service-broker/docs/current/apidocs//org/springframework/cloud/servicebroker/model/catalog/Catalog.html)类型的Spring bean：

```java
@Bean
public Catalog catalog() {
    Plan mailFreePlan = Plan.builder()
        .id("fd81196c-a414-43e5-bd81-1dbb082a3c55")
        .name("mail-free-plan")
        .description("Mail Service Free Plan")
        .free(true)
        .build();

    ServiceDefinition serviceDefinition = ServiceDefinition.builder()
        .id("b92c0ca7-c162-4029-b567-0d92978c0a97")
        .name("mail-service")
        .description("Mail Service")
        .bindable(true)
        .tags("mail", "service")
        .plans(mailFreePlan)
        .build();

    return Catalog.builder()
        .serviceDefinitions(serviceDefinition)
        .build();
}
```

如上所示，服务目录包含描述我们的服务代理可以提供的所有可用服务的元数据。此外，服务的定义故意很宽泛，**因为它可以指数据库、消息队列，或者在我们的例子中，可以引用邮件服务**。

另一个关键点是每项服务都是根据计划构建的，这是另一个通用术语。从本质上讲，**每个计划都可以提供不同的功能并花费不同的金额**。

最后，服务目录通过服务代理/v2/catalog端点提供给云原生平台：

```shell
curl http://localhost:8080/broker/v2/catalog

{
    "services": [
        {
            "bindable": true,
            "description": "Mail Service",
            "id": "b92c0ca7-c162-4029-b567-0d92978c0a97",
            "name": "mail-service",
            "plans": [
                {
                    "description": "Mail Service Free Plan",
                    "free": true,
                    "id": "fd81196c-a414-43e5-bd81-1dbb082a3c55",
                    "name": "mail-free-plan"
                }
            ],
            "tags": [
                "mail",
                "service"
            ]
        }
    ]
}
```

因此，云原生平台将从所有服务代理查询服务代理目录端点，以呈现服务目录的聚合视图。

### 4.2 服务提供

一旦我们开始广告服务，我们还需要在我们的代理中提供机制，以在云平台内提供和管理它们的生命周期。

此外，预配所代表的内容因代理而异。在某些情况下，**预配可能涉及启动空数据库、创建消息代理或简单地提供一个帐户来访问外部API**。

就术语而言，服务代理创建的服务将被称为服务实例。

使用Spring Cloud Open Service Broker，我们可以通过实现[ServiceInstanceService](https://docs.spring.io/spring-cloud-open-service-broker/docs/current/apidocs/org/springframework/cloud/servicebroker/service/ServiceInstanceService.html)接口来管理服务生命周期。例如，要在我们的服务代理中管理服务供应请求，我们必须为createServiceInstance方法提供一个实现：

```java
@Override
public Mono<CreateServiceInstanceResponse> createServiceInstance(
    CreateServiceInstanceRequest request) {
    return Mono.just(request.getServiceInstanceId())
        .flatMap(instanceId -> Mono.just(CreateServiceInstanceResponse.builder())
            .flatMap(responseBuilder -> mailService.serviceInstanceExists(instanceId)
                .flatMap(exists -> {
                    if (exists) {
                        return mailService.getServiceInstance(instanceId)
                            .flatMap(mailServiceInstance -> Mono.just(responseBuilder
                                .instanceExisted(true)
                                .dashboardUrl(mailServiceInstance.getDashboardUrl())
                                .build()));
                    } else {
                        return mailService.createServiceInstance(
                            instanceId, request.getServiceDefinitionId(), request.getPlanId())
                            .flatMap(mailServiceInstance -> Mono.just(responseBuilder
                                .instanceExisted(false)
                                .dashboardUrl(mailServiceInstance.getDashboardUrl())
                                .build()));
                    }
                })));
}
```

在这里，如果不存在具有相同服务实例ID的邮件服务，我们会在内部映射分配一个新邮件服务，并提供仪表板URL。我们可以将仪表板视为我们服务实例的Web管理界面。

服务预配通过/v2/service_instances/{instance_id}端点提供给云原生平台：

```shell
curl -X PUT http://localhost:8080/broker/v2/service_instances/newsletter@baeldung.com 
    -H 'Content-Type: application/json' 
    -d '{
        "service_id": "b92c0ca7-c162-4029-b567-0d92978c0a97", 
        "plan_id": "fd81196c-a414-43e5-bd81-1dbb082a3c55"
    }' 

{"dashboard_url":"http://localhost:8080/mail-dashboard/newsletter@baeldung.com"}
```

简而言之，**当我们提供新服务时，我们需要传递服务目录中发布的service_id和plan_id**。此外，我们需要提供一个唯一的instance_id，我们的服务代理将在未来的绑定和取消预配请求中使用它。

### 4.3 服务绑定

预配服务后，我们希望我们的客户端应用程序开始与它通信。从服务代理的角度来看，这称为服务绑定。

与服务实例和计划类似，我们应该将绑定视为我们可以在服务代理中使用的另一种灵活抽象。通常，**我们将提供服务绑定来公开用于访问服务实例的凭据**。

在我们的示例中，如果广告服务的bindable字段设置为true，则我们的服务代理必须提供[ServiceInstanceBindingService](https://docs.spring.io/spring-cloud-open-service-broker/docs/current/apidocs/org/springframework/cloud/servicebroker/service/ServiceInstanceBindingService.html)接口的实现。否则，云平台将不会从我们的服务代理调用服务绑定方法。

让我们通过提供createServiceInstanceBinding方法的实现来处理服务绑定创建请求：

```java
@Override
public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(
    CreateServiceInstanceBindingRequest request) {
    return Mono.just(CreateServiceInstanceAppBindingResponse.builder())
        .flatMap(responseBuilder -> mailService.serviceBindingExists(
            request.getServiceInstanceId(), request.getBindingId())
            .flatMap(exists -> {
                if (exists) {
                    return mailService.getServiceBinding(
                        request.getServiceInstanceId(), request.getBindingId())
                        .flatMap(serviceBinding -> Mono.just(responseBuilder
                            .bindingExisted(true)
                            .credentials(serviceBinding.getCredentials())
                            .build()));
                } else {
                    return mailService.createServiceBinding(
                        request.getServiceInstanceId(), request.getBindingId())
                        .switchIfEmpty(Mono.error(
                            new ServiceInstanceDoesNotExistException(
                                request.getServiceInstanceId())))
                        .flatMap(mailServiceBinding -> Mono.just(responseBuilder
                            .bindingExisted(false)
                            .credentials(mailServiceBinding.getCredentials())
                            .build()));
                }
            }));
}
```

上面的代码生成一组唯一的凭据-用户名、密码和URI，我们可以通过它们连接并验证我们的新邮件服务实例。

Spring Cloud Open Service Broker框架通过/v2/service_instances/{instance_id}/service_bindings/{binding_id}端点公开服务绑定操作：

```shell
curl -X PUT 
    http://localhost:8080/broker/v2/service_instances/newsletter@baeldung.com/service_bindings/admin 
    -H 'Content-Type: application/json' 
    -d '{ 
        "service_id": "b92c0ca7-c162-4029-b567-0d92978c0a97", 
        "plan_id": "fd81196c-a414-43e5-bd81-1dbb082a3c55" 
    }'

{
    "credentials": {
        "password": "bea65996-3871-4319-a6bb-a75df06c2a4d",
        "uri": "http://localhost:8080/mail-system/newsletter@baeldung.com",
        "username": "admin"
    }
}
```

就像服务实例预配一样，我们在绑定请求中使用服务目录中公布的service_id和plan_id。此外，我们还传递了一个唯一的binding_id，代理将其用作我们的凭据集的用户名。

## 5. Service Broker API Security

通常，当Service Broker与云原生平台之间进行通信时，需要身份验证机制。

遗憾的是，Open Service Broker API规范目前并未涵盖服务代理端点的身份验证部分。因此，Spring Cloud Open Service Broker库也没有实现任何安全配置。

幸运的是，如果我们需要保护我们的服务代理端点，我们可以快速使用Spring Security来实施[基本身份验证](https://www.baeldung.com/spring-security-basic-authentication)或[OAuth 2.0](https://www.baeldung.com/rest-api-spring-oauth2-angular)机制。在这种情况下，我们应该使用我们选择的身份验证机制对所有服务代理请求进行身份验证，并在身份验证失败时返回401 Unauthorized响应。

## 6. 总结

在本文中，我们探讨了Spring Cloud Open Service Broker项目。

首先，我们了解了Open Service Broker API是什么，以及它如何允许我们提供和连接到后备服务。随后，我们看到了如何使用Spring Cloud Open Service Broker库快速构建一个Service Broker API兼容项目。