## 1. 概述

在本教程中，我们将学习如何将 Spring Boot 的[@ConfigurationProperties](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)注解与 Kotlin 的数据类结合使用，作为 Spring Boot 配置的一部分。

## 2. Kotlin 数据类中的@ConfigurationProperties

Spring Boot 提供了一种使用@Value(“${property}”)注解配置单个外部属性的方法。然而，这种方法变得很麻烦，尤其是当我们使用许多属性或者属性本质上是分层的时候。

因此，Spring Boot 提供了一个替代的@ConfigurationProperties注解，方便我们将外部应用属性映射到Kotlin 或Java bean 对象。

### 2.1. 启用@ConfigurationProperties

Spring Boot 提供了几种方法来将配置类绑定和注册为 bean。一种选择是使用@ConfigurationProperties 注解一个类，然后将它与@Configuration类中的@Bean方法一起使用。另一种选择是启用对@ConfigurationProperties作为 bean 的支持，并使用[@EnableAutoConfiguration](https://www.baeldung.com/spring-enable-config-properties)注解注册它。

让我们看一个配置第三方 API 端点的示例。作为其中的一部分，我们可以外部化客户端 ID、API URL 和 API 密钥等属性。application.yml文件将如下所示：

```yaml
api:
    clientId: "client123"
    url: "https://api.url.com"
    key: "api-access-key"
```

让我们看一下上述属性的@ConfigurationProperties数据类的定义：

```kotlin
@ConfigurationProperties(prefix = "api")
data class ApiConfiguration(
    var clientId: String = "",
    var url: String = "",
    var key: String = ""
)
```

在上面的代码中，请注意属性定义为var。在后续部分中，我们将看到如何重构此代码以使用val进行属性定义。

现在，让我们将@ConfigurationProperties注解类注册为@Bean：

```kotlin
@Configuration
class AppConfiguration {
    @Bean
    fun apiConfiguration(): ApiConfiguration {
        return ApiConfiguration()
    }
}
```

最后，让我们启用配置类扫描以 使用@EnableAutoConfiguration加载我们的@ConfigurationProperties 类：

```kotlin
@Configuration
@EnableConfigurationProperties(ApiConfiguration::class)
class AppConfiguration
```

### 2.2. 使用@ConfigurationProperties

由于@ConfigurationProperties被注册为一个 bean，我们可以像注入任何其他 Spring bean 一样注入它。我们可以将ApiConfiguration bean 注入到@Service类中：

```kotlin
@Service
class ApiService(val apiConfiguration: ApiConfiguration)
```

作为注入的结果，我们可以通过ApiService类中的ApiConfiguration类访问外部属性。

## 3.在Kotlin数据类中绑定@ConfigurationProperties

让我们看看如何以可变和不可变的方式使用 Kotlin 数据类来定义@ConfigurationProperties。

### 3.1. JavaBean 绑定选项

让我们再看看ApiConfiguration：

```kotlin
@ConfigurationProperties(prefix = "api")
data class ApiConfiguration(
    var clientId: String = "",
    var url: String = "",
    var key: String = ""
)
```

如果我们查看ApiConfiguration类成员属性，我们会发现这些被定义为var而不是Kotlin 建议的val作为最终值。因此，var 属性是可变的。

此外，我们必须用初始值初始化所有属性。否则，我们会看到以下错误：

```bash

APPLICATION FAILED TO START


Description:
Parameter 0 of constructor in com.example.kotlin.kotlinspring.config.ApiConfiguration required a bean of type 'java.lang.String' that could not be found.

Action:
Consider defining a bean of type 'java.lang.String' in your configuration.

Disconnected from the target VM, address: '127.0.0.1:49351', transport: 'socket'

Process finished with exit code 1
```

对于具有var类型属性的@ConfigurationProperties类，Spring Boot 使用默认的无参数构造函数实例化该类。 因此，它使用访问器(getter 和 setter)来设置值。此外，我们知道 Kotlin 数据类本质上为定义的属性提供 getter 和 setter。

### 3.2. 构造函数绑定选项

自 Spring Boot v2.2.0 以来，有一个 JavaBean 绑定选项的替代方案：使用@ConstructorBinding注解实例化和设置@ConfigurationProperties 。

在有多个构造函数的情况下，我们可以在类级别或构造函数上进行注解，以指示应使用构造函数绑定。我们必须确保构造函数接收所有需要绑定的参数。

通过使用@ConstructorBinding，所有属性都在实例化期间绑定，并且值是最终的。因此，我们可以使用val来定义属性。这使我们的数据类定义更加简洁。

请注意，为了使用@ConstructorBinding，我们必须使用@EnableAutoConfiguration或配置属性扫描注册@ConfigurationProperties bean 。因此，如果我们使用常规的 Spring bean 注册方法(如@Bean或@Component )来创建@ConfigurationProperties ，我们就不能使用@ConstructorBinding。此外，我们已经在前面的部分中了解了如何注册@ConfigurationProperties 。

让我们使用@ConstructorBinding注解重写我们的示例：

```kotlin
@ConfigurationProperties(prefix = "api")
@ConstructorBinding
data class ApiConfiguration(
    val clientId: String,
    val url: String,
    val key: String
)
```

注意上例中val的使用。

## 4。总结

在本文中，我们学习了如何使用 Kotlin 数据类注册和使用 Spring Boot @ConfigurationProperties bean。此外，我们还研究了如何以可变和不可变的方式使用 Kotlin 数据类将外部属性绑定到@ConfigurationProperties bean。