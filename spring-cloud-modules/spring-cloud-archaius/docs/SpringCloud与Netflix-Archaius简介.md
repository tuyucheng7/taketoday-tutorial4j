## 1. 概述

Netflix [Archaius](https://github.com/Netflix/archaius)是一个功能强大的配置管理库。

简而言之，它是一个框架，可用于从许多不同的来源收集配置属性，并提供对它们的快速、线程安全的访问。

最重要的是，该库允许属性在运行时动态更改，从而使系统无需重新启动应用程序即可获得这些变化。

在这个介绍性教程中，我们将设置一个简单的Spring Cloud Archaius配置，我们将解释幕后发生的事情，最后，我们将了解Spring如何允许扩展基本设置。

## 2. Netflix Archaius功能

正如我们所知，Spring Boot已经提供了管理[外部化配置](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)的工具，那么为什么还要设置不同的机制呢？

好吧，**Archaius提供了一些其他任何配置框架都没有想到的方便和有趣的功能**。它的一些关键点是：

-   动态和类型化属性
-   在属性更改时调用的回调机制
-   动态配置源(如URL、JDBC和Amazon DynamoDB)的即用型实施
-   可由Spring Boot Actuator或JConsole访问以检查和操作属性的JMX MBean
-   动态属性验证

在许多情况下，这些功能可能是有益的。

因此，Spring Cloud开发了一个库，可以轻松配置“Spring Environment Bridge”，以便Archaius可以从Spring Environment读取属性。

## 3. 依赖关系

让我们将spring-cloud-starter-netflix-archaius添加到我们的应用程序中，它会将所有必要的依赖项添加到我们的项目中。

或者，我们也可以将spring-cloud-netflix添加到我们的dependencyManagement部分，并依赖于它对工件版本的规范：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-archaius</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix</artifactId>
            <version>2.0.1.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

注意：我们可以检查Maven Central来验证我们使用的是最新版本的[starter库](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-starter-netflix-archaius)。

## 4. 用法

**添加所需的依赖项后，我们将能够访问框架管理的属性**：

```java
DynamicStringProperty dynamicProperty = DynamicPropertyFactory.getInstance()
    .getStringProperty("tuyucheng.archaius.property", "default value");

String propertyCurrentValue = dynamicProperty.get();
```

让我们通过一个简短的示例来了解它是如何开箱即用的。

### 4.1 快速示例

**默认情况下，它动态管理应用程序类路径中名为config.properties的文件中定义的所有属性**。

因此，让我们将它添加到具有一些任意属性的资源文件夹中：

```properties
#config.properties
tuyucheng.archaius.properties.one=one FROM:config.properties
```

现在我们需要一种在任何特定时刻检查属性值的方法。在这种情况下，我们将创建一个RestController来检索作为JSON响应的值：

```java
@RestController
public class ConfigPropertiesController {

    private DynamicStringProperty propertyOneWithDynamic = DynamicPropertyFactory.getInstance()
          .getStringProperty("tuyucheng.archaius.properties.one", "not found!");

    @GetMapping("/property-from-dynamic-management")
    public String getPropertyValue() {
        return propertyOneWithDynamic.getName() + ": " + propertyOneWithDynamic.get();
    }
}
```

让我们尝试一下。我们可以向此端点发送请求，服务将按预期检索存储在config.properties中的值。

到目前为止没什么大不了的，对吧？好的，让我们继续更改类路径文件中属性的值，而无需重新启动服务。因此，大约一分钟后，对端点的调用应该会检索到新值。很酷，不是吗？

接下来，我们将尝试了解幕后发生的事情。

## 5. 它是如何工作的？

首先，让我们尝试了解全局。

**Archaius是**[Apache的Commons Configuration库](https://commons.apache.org/proper/commons-configuration/)**的扩展，添加了一些不错的功能，例如动态源的轮询框架，具有高吞吐量和线程安全的实现。**

**然后spring-cloud-netflix-archaius库开始发挥作用，合并所有不同的属性源，并使用这些源自动配置Archaius工具**。

### 5.1 Netflix Archaius库

它定义了一个复合配置，即从不同来源获得的各种配置的集合。

此外，其中一些配置源可能支持在运行时轮询更改。Archaius提供接口和一些预定义的实现来配置这些类型的源。

源集合是分层的，因此如果一个属性存在于多个配置中，最终值将是最顶层中的那个。

最后，ConfigurationManager处理系统范围的配置和部署上下文。它可以安装最终的组合配置，或检索已安装的组合配置进行修改。

### 5.2 Spring Cloud支持

**Spring Cloud Archaius库的主要任务是将所有不同的配置源合并为一个ConcurrentCompositeConfiguration并使用ConfigurationManager安装它**。

库定义源的优先顺序是：

1.  在上下文中定义的任何Apache Common Configuration AbstractConfiguration bean
2.  自动注入的Spring ConfigurableEnvironment中定义的所有源
3.  我们在上面的示例中看到的默认Archaius源
4.  Apache的SystemConfiguration和EnvironmentConfiguration源

这个Spring Cloud库提供的另一个有用的特性是定义Actuator端点来监控属性并与之交互。它的用法超出了本教程的范围。

## 6. 调整和扩展Archaius配置

现在我们对Archaius的工作原理有了更好的了解，我们可以很好地分析如何使配置适应我们的应用程序，或者如何使用我们的配置源扩展功能。

### 6.1 Archaius支持的配置属性

如果我们希望Archaius考虑类似于config.properties的其他配置文件，我们可以定义archaius.configurationSource.additionalUrls系统属性。

该值被解析为以逗号分隔的URL列表，因此，例如，我们可以在启动应用程序时添加此系统属性：

```shell
-Darchaius.configurationSource.additionalUrls=
  "classpath:other-dir/extra.properties,
  file:///home/user/other-extra.properties"
```

Archaius将首先读取config.properties文件，然后按照指定的顺序读取其他文件。因此，后一个文件中定义的属性将优先于前面的文件。

我们还可以使用一些其他系统属性来配置Archaius默认配置的各个方面：

-   archaius.configurationSource.defaultFileName：类路径中的默认配置文件名
-   archaius.fixedDelayPollingScheduler.initialDelayMills：读取配置源之前的初始延迟
-   archaius.fixedDelayPollingScheduler.delayMills：两次读取源之间的延迟；默认值为1分钟

### 6.2 使用Spring添加额外的配置源

我们如何添加不同的配置源以由所描述的框架管理？我们如何管理比Spring环境中定义的动态属性具有更高优先级的动态属性？

回顾我们在4.2节中提到的内容，我们可以意识到Spring定义的复合配置中的最高配置是在上下文中定义的AbstractConfiguration beans。

因此，**我们需要做的就是使用Archaius提供的一些功能将这个Apache抽象类的实现添加到我们的Spring上下文中，并且Spring的自动配置将自发地将它添加到托管配置属性中**。

为了简单起见，我们将看到一个示例，其中我们配置了一个类似于默认config.properties的属性文件，但不同之处在于它的优先级高于其余的Spring环境和应用程序属性：

```java
@Bean
public AbstractConfiguration addApplicationPropertiesSource() {
    URL configPropertyURL = (new ClassPathResource("other-config.properties")).getURL();
    PolledConfigurationSource source = new URLConfigurationSource(configPropertyURL);
    return new DynamicConfiguration(source, new FixedDelayPollingScheduler());
}
```

对我们来说幸运的是，它考虑了几个我们可以毫不费力地设置的配置源。它们的配置超出了本介绍性教程的范围。

## 7. 总结

总而言之，我们了解了Archaius以及它为利用配置管理提供的一些很酷的功能。

此外，我们还看到了Spring Cloud自动配置库如何发挥作用，使我们能够方便地使用该库的API。