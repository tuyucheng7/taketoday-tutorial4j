## 1. 简介

Spring Boot应用程序可以具有复杂的组件图、启动阶段和资源初始化步骤。

在本文中，我们将了解**如何通过[Spring Boot Actuator]()端点跟踪和监视此启动信息**。

## 2. 应用程序启动跟踪

跟踪应用程序启动期间的各个步骤可以提供有用的信息，可以帮助我们**了解应用程序启动的各个阶段所花费的时间**，这种检测还可以**提高我们对上下文生命周期和应用程序启动顺序的理解**。

[Spring框架]()提供了[记录应用程序启动](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-startup)和图形初始化的功能。此外，Spring Boot Actuator通过HTTP或JMX提供了多种生产级监控和管理功能。

从[Spring Boot 2.4](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.4-Release-Notes#startup-endpoint)开始，**应用程序启动跟踪指标现在可以通过/actuator/startup端点获得**。

## 3. 设置

要启用Spring Boot Actuator，让我们将[spring-boot-starter-actuator](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-actuator)依赖项添加到我们的POM中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.5.4</version>
</dependency>
```

我们还将添加[spring-boot-starter-web](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-web)依赖项，因为这是通过HTTP访问端点所必需的：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.5.4</version>
</dependency>
```

此外，我们还将通过在application.properties文件中设置配置属性来通过HTTP公开所需的端点：

```properties
management.endpoints.web.exposure.include=startup
```

最后，我们分别使用[curl](https://man7.org/linux/man-pages/man1/curl.1.html)和[jq]()查询Actuator HTTP端点并解析JSON响应。

## 4. Actuator端点

为了捕获启动事件，我们需要使用@ApplicationStartup接口的实现来配置我们的应用程序。默认情况下，用于管理应用程序生命周期的ApplicationContext使用空操作实现，这显然不执行任何启动检测和跟踪，从而将开销降至最低。

因此，**与其他Actuator端点不同，我们需要一些额外的设置**。

### 4.1 使用BufferingApplicationStartup

我们需要将应用程序的启动配置设置为BufferingApplicationStartup的一个实例，这是Spring Boot提供的ApplicationStartup接口的内存中实现，**它捕获Spring启动过程中的事件并将它们存储在内部缓冲区中**。

让我们从为我们的应用程序使用此实现创建一个简单的应用程序开始：

```java
@SpringBootApplication
public class StartupTrackingApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StartupTrackingApplication.class);
        app.setApplicationStartup(new BufferingApplicationStartup(2048));
        app.run(args);
    }
}
```

在这里，我们还为内部缓冲区指定了2048的容量。一旦缓冲区达到此事件容量，就不会再记录任何数据。因此，我们使用适当的值以允许根据应用程序复杂性和启动期间执行的各个步骤来存储事件是很重要的。

至关重要的是，**Actuator端点仅在配置此实现后可用**。

### 4.2 startup端点

现在，我们可以启动我们的应用程序并查询startup Actuator端点。

让我们使用curl调用此POST端点并使用jq格式化JSON输出：

```shell
> curl 'http://localhost:8080/actuator/startup' -X POST | jq
{
    "springBootVersion": "2.5.4",
    "timeline": {
        "startTime": "2022-12-19T21:08:00.931660Z",
        "events": [
            {
                "endTime": "2022-12-19T21:08:00.989076Z",
                "duration": "PT0.038859S",
                "startTime": "2022-12-19T21:08:00.950217Z",
                "startupStep": {
                    "name": "spring.boot.application.starting",
                    "id": 0,
                    "tags": [
                        {
                            "key": "mainApplicationClass",
                            "value": "cn.tuyucheng.taketoday.startup.StartupTrackingApplication"
                        }
                    ],
                    "parentId": null
                }
            },
            {
                "endTime": "2022-12-19T21:08:01.454239Z",
                "duration": "PT0.344867S",
                "startTime": "2022-12-19T21:08:01.109372Z",
                "startupStep": {
                    "name": "spring.boot.application.environment-prepared",
                    "id": 1,
                    "tags": [],
                    "parentId": null
                }
            },
#            ... other steps not shown
            {
                "endTime": "2022-12-19T21:08:12.199369Z",
                "duration": "PT0.00055S",
                "startTime": "2022-12-19T21:08:12.198819Z",
                "startupStep": {
                    "name": "spring.boot.application.running",
                    "id": 358,
                    "tags": [],
                    "parentId": null
                }
            }
        ]
    }
}
```

正如我们所见，详细的JSON响应包含一个检测启动事件列表。**它包含有关每个步骤的各种详细信息，例如步骤名称、开始时间、结束时间以及步骤计时详细信息**。有关响应结构的详细信息，请参阅[Spring Boot Actuator Web API](https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/#startup)文档。

此外，核心容器中定义的完整步骤列表以及有关每个步骤的更多详细信息可在[Spring参考文档](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#application-startup-steps)中找到。

这里需要注意的一个重要细节是，端点的后续调用不提供详细的JSON响应，这是因为startup端点调用会清除内部缓冲区。因此，我们需要重新启动应用程序以调用相同的端点并再次接收完整的响应。

**如果需要，我们应该保存有效载荷以供进一步分析**。

### 4.3 过滤启动事件

正如我们所见，缓冲实现具有固定的容量用于在内存中存储事件。因此，可能不希望在缓冲区中存储大量事件。

我们可以过滤检测到的事件，并只存储我们可能感兴趣的事件：

```java
BufferingApplicationStartup startup = new BufferingApplicationStartup(2048);
startup.addFilter(startupStep -> startupStep.getName().matches("spring.beans.instantiate");
```

在这里，我们使用addFilter方法仅检测具有指定名称的步骤。

### 4.4 自定义检测

我们还可以扩展BufferingApplicationStartup以提供自定义启动跟踪行为以满足我们特定的检测需求。

由于此检测在测试环境而非生产环境中特别有价值，因此使用系统属性并在无操作和缓冲或自定义实现之间切换是一个简单的练习。

## 5. 分析启动时间

作为一个实际示例，让我们尝试识别启动期间可能需要相对较长时间进行初始化的任何bean实例化。例如，这可能是缓存加载、数据库连接池或应用程序启动期间的一些其他昂贵的初始化。

我们可以像以前一样调用端点，但这一次，我们将使用jq处理输出。

由于响应非常冗长，我们过滤与名称spring.beans.instantiate匹配的步骤，并按持续时间对它们进行排序：

```shell
> curl 'http://localhost:8080/actuator/startup' -X POST 
| jq '[.timeline.events
 | sort_by(.duration) | reverse[]
 | select(.startupStep.name | match("spring.beans.instantiate"))
 | {beanName: .startupStep.tags[0].value, duration: .duration}]'
```

上面的表达式处理响应JSON以提取时间信息：

-   按降序对timeline.events数组进行排序。
-   从排序数组中选择与名称spring.beans.instantiate匹配的所有步骤。
-   创建一个新的JSON对象，其中包含beanName和每个匹配步骤的持续时间。

因此，输出显示了在应用程序启动期间实例化的各种bean的简洁、有序和过滤视图：

```json
[
    {
        "beanName": "resourceInitializer",
        "duration": "PT6.003171S"
    },
    {
        "beanName": "tomcatServletWebServerFactory",
        "duration": "PT0.143958S"
    },
    {
        "beanName": "requestMappingHandlerAdapter",
        "duration": "PT0.14302S"
    },
    ...
]
```

在这里，我们可以看到resourceInitializer bean在启动过程中花费了大约6秒，这可能被认为是对整个应用程序启动时间的重要持续时间。使用这种方法，**我们可以有效地识别这个问题并专注于进一步调查和可能的解决方案**。

请务必注意，**ApplicationStartup仅供在应用程序启动期间使用。换句话说，它不会取代用于应用程序检测的Java分析器和指标收集框架**。

## 6. 总结

在本文中，我们研究了如何在Spring Boot应用程序中获取和分析详细的启动指标。

首先，我们了解了如何启用和配置Spring Boot Actuator端点；然后，我们查看了从该端点获得的有用信息。最后，我们通过一个例子来分析这些信息，以便更好地理解应用程序启动过程中的各个步骤。