## 1. 概述

Spring Boot中的一个常见做法是使用[外部配置来定义我们的属性]()，这使我们能够在不同的环境中使用相同的应用程序代码。

我们可以使用properties文件、YAML文件、环境变量和命令行参数。

在这个简短的教程中，我们将探索properties和YAML文件之间的主要区别。

## 2. 属性配置

默认情况下，Spring Boot可以访问application.properties文件中设置的配置，该文件使用键值格式：

```properties
spring.datasource.url=jdbc:h2:dev
spring.datasource.username=SA
spring.datasource.password=password
```

这里每一行都是一个配置，所以我们需要通过为我们的键使用相同的前缀来表达分层数据。在这个例子中，每个键都属于spring.datasource。

### 2.1 属性中的占位符

在我们的值中，我们可以使用带有${}语法的占位符来引用其他键、系统属性或环境变量的内容：

```properties
app.name=MyApp
app.description=${app.name} is a Spring Boot application
```

### 2.2 集合结构

如果我们有具有不同值的相同类型的属性，我们可以用数组索引表示集合结构：

```properties
application.servers[0].ip=127.0.0.1
application.servers[0].path=/path1
application.servers[1].ip=127.0.0.2
application.servers[1].path=/path2
application.servers[2].ip=127.0.0.3
application.servers[2].path=/path3
```

### 2.3 多个Profile

从2.4.0版本开始，Spring Boot支持创建多文档属性文件。简单的说，我们可以将单个物理文件拆分成多个逻辑文件。

这允许我们为每个需要声明的Profile定义一个文档，所有这些都在同一个文件中：

```properties
logging.file.name=myapplication.log
tuyucheng.property=defaultValue
#---
spring.config.activate.on-profile=dev
spring.datasource.password=password
spring.datasource.url=jdbc:h2:dev
spring.datasource.username=SA
tuyucheng.property=devValue
#---
spring.config.activate.on-profile=prod
spring.datasource.password=password
spring.datasource.url=jdbc:h2:prod
spring.datasource.username=prodUser
tuyucheng.property=prodValue
```

请注意，我们使用“#---”符号来指示我们要拆分文档的位置。

在此示例中，我们有两个带有不同Profile标记的spring部分。此外，我们可以在根级别拥有一组通用属性，在这种情况下，logging.file.name属性在所有Profile中都是相同的。

### 2.4 跨多个文件的Profile

作为在同一文件中拥有不同Profile的替代方法，我们可以在不同文件中存储多个Profile。在2.4.0版本之前，这是可用于属性文件的唯一方法。

我们可以通过将Profile的名称放在文件名中来实现这一点，例如，application-dev.yml或application-dev.properties。

## 3. YAML配置

### 3.1 YAML格式

除了Java属性文件，我们还可以在Spring Boot应用程序中使用基于[YAML]()的配置文件，**YAML是一种用于指定分层配置数据的便捷格式**。

现在让我们从我们的属性文件中获取相同的示例并将其转换为YAML：

```yaml
spring:
    datasource:
        password: password
        url: jdbc:h2:dev
        username: SA
```

这可能比它的属性文件替代方案更具可读性，因为它不包含重复的前缀。

### 3.2 集合结构

YAML具有更简洁的格式来表达集合：

```yaml
application:
    servers:
        -   ip: '127.0.0.1'
            path: '/path1'
        -   ip: '127.0.0.2'
            path: '/path2'
        -   ip: '127.0.0.3'
            path: '/path3'
```

### 3.3 多个Profile

与属性文件不同，YAML在设计上支持多文档文件，这样，无论我们使用哪个版本的Spring Boot，我们都可以将多个Profile存储在同一个文件中。

但是，在这种情况下，规范表明我们必须**使用三个破折号来指示新文档的开始**：

```yaml
logging:
    file:
        name: myapplication.log
---
spring:
    config:
        activate:
            on-profile: staging
    datasource:
        password: 'password'
        url: jdbc:h2:staging
        username: SA
tuyucheng:
    property: stagingValue
```

注意：我们通常不希望在我们的项目中同时包含标准的application.properties和application.yml文件，因为这可能会导致意想不到的结果。

例如，如果我们将上面显示的属性(在application.yml文件中)与第2.3节中描述的属性组合在一起，那么tuyucheng.property将被分配defaultValue而不是特定于Profile的值，这仅仅是因为application.properties是后面加载的，覆盖了到那时已经分配的值。

## 4. Spring Boot的使用

现在我们已经定义了我们的配置，让我们看看如何访问它们。

### 4.1 @Value注解

我们可以使用@Value注解注入属性的值：

```java
@Value("${key.something}")
private String injectedProperty;
```

在这里，属性key.something通过字段注入注入到我们的一个对象中。

### 4.2 Environment抽象

我们还可以使用Environment API获取属性的值：

```java
@Autowired
private Environment env;

public String getSomeKey(){
    return env.getProperty("key.something");
}
```

### 4.3 @ConfigurationProperties注解

最后，我们还可以使用@ConfigurationProperties注解将我们的属性绑定到类型安全的结构化对象：

```java
@ConfigurationProperties(prefix = "mail")
public class ConfigProperties {
	String name;
	String description;
	// ...
}
```

## 5. 总结

在本文中，我们看到了properties和yml Spring Boot配置文件之间的一些差异，并演示了它们的值如何引用其他属性。最后，我们研究了如何将值注入到我们的运行时中。