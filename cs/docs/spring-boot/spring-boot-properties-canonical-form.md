## 1. 概述

在这个快速教程中，我们将仔细研究 [Spring Boot](https://www.baeldung.com/spring-boot)错误“原因：规范名称应该是短横线大小写(‘-’分隔)、小写字母数字字符，并且必须以字母开头”。

首先，我们将阐明Spring Boot中出现此错误的主要原因。然后，我们将通过一个实际示例深入探讨如何重现和解决该问题。

## 2. 问题陈述

首先，让我们了解一下错误消息的含义。“规范名称应该是短横线大小写”只是告诉我们规范名称(规范名称是指唯一标识属性的属性名称)应该是短横线大小写。

为了确保一致性， @ConfigurationProperties注释的前缀参数中使用的命名约定应遵循短横线大小写。

例如：

```java
@ConfigurationProperties(prefix = "my-example")
```

在上面的代码片段中，前缀my-example 应遵守 kebab 大小写约定。

## 3.Maven依赖

由于这是一个基于 Maven 的项目，因此我们将必要的依赖项添加到 pom.xml中：

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter</artifactId> 
    <version>3.0.5</version>
</dependency>

```

为了重现该问题，[spring-boot-starter](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-maven-plugin)是唯一需要的依赖项。

## 4. 重现错误

### 4.1. 应用程序配置

如果你不熟悉配置属性，可以通过探索[Spring Boot 中的配置属性指南](https://www.baeldung.com/configuration-properties-in-spring-boot)来获得更好的理解。

让我们注册所需的组件：

```java
@Configuration
@ConfigurationProperties(prefix = "customProperties")
public class MainConfiguration {
    String name;
 
    // getters and setters
}

```

然后，我们需要向application.properties文件添加自定义属性：

```java
custom-properties.name="Baeldung"
```

application.properties 位于src/main/resources下：

```applescript
|   pom.xml
+---src
|   +---main
|   |   +---java
|   |   |   \---com
|   |   |       \---baeldung
|   |   |           ...
|   |   |           ...
|   |   \---resources
|   |           application.properties
```

现在，让我们通过在项目根文件夹中执行 mvn spring-boot:run命令来运行示例Spring Boot应用程序，看看会发生什么：

```java
$ mvn spring-boot:run
...
...
***************************
APPLICATION FAILED TO START
***************************

Description:

Configuration property name 'customProperties' is not valid:

    Invalid characters: 'P'
    Bean: mainConfiguration
    Reason: Canonical names should be kebab-case ('-' separated), lowercase alpha-numeric characters and must start with a letter

Action:

Modify 'customProperties' so that it conforms to the canonical names requirements.
```

如上所示，我们收到一条错误消息修改“customProperties”，使其符合规范名称要求。此错误消息表明当前用于customProperties的命名约定不遵循 Spring 设置的命名约定。换句话说，需要更改名称customProperties以符合 Spring 中命名属性的要求。

## 5. 修复错误

我们需要更改属性前缀：

```java
@ConfigurationProperties(prefix = "customProperties")
```

对于 kebab 大小写前缀：

```java
@ConfigurationProperties(prefix = "custom-properties")
```

在属性中，我们可以保留任何样式，并且可以很好地访问它们。

## 6. 烤肉串肠衣的优点

在访问这些属性时使用 kebab 外壳的主要优点是我们可以使用以下任何外壳：

- 骆驼案例类似这样
- 像这样的帕斯卡案例
- 类似蛇的情况
- 像这样的烤肉串盒

在属性文件中并使用 kebab 外壳访问它们。

```swift
@ConfigurationProperties(prefix = "custom-properties")
```

将能够访问以下任何属性

```ini
customProperties.name="Baeldung"
CustomProperties.name="Baeldung"
custom_properties.name="Baeldung"
custom-properties.name="Baeldung"
```

## 七、总结

在本教程中，我们了解到Spring Boot支持多种格式，包括属性名称中的驼峰命名法、蛇形命名法和短横线命名法，但鼓励我们以短横线命名法规范地访问它们，从而减少因命名约定不一致而导致错误或混乱的可能性。