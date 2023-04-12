## 1. 概述

在本教程中，我们将讨论如何在Spring Boot的application.properties和application.yml中使用环境变量。然后，我们将学习如何在代码中引用这些属性。

## 延伸阅读

## [Spring和Spring Boot的属性](https://www.baeldung.com/properties-with-spring)

有关如何在Spring中使用属性文件和属性值的教程。

[阅读更多](https://www.baeldung.com/properties-with-spring)→

### [在Spring Boot中使用application.yml与application.properties](https://www.baeldung.com/spring-boot-yaml-vs-properties)

Spring Boot同时支持.properties和YAML。我们探讨了注入属性之间的差异，以及如何提供多种配置。

[阅读更多](https://www.baeldung.com/spring-boot-yaml-vs-properties)→

### [Spring Boot 2.5中的环境变量前缀](https://www.baeldung.com/spring-boot-env-variable-prefixes)

了解如何在Spring Boot中为环境变量使用前缀。

[阅读更多](https://www.baeldung.com/spring-boot-env-variable-prefixes)→

## 2. 在application.properties文件中使用环境变量

让我们定义一个名为JAVA_HOME的[全局环境变量](https://www.baeldung.com/linux/environment-variables)，其值为“C:\Program Files\Java\jdk-11.0.14”。

要在Spring Boot的application.properties中使用这个变量，我们需要用大括号将它括起来：

```properties
java.home=${JAVA_HOME}
```

我们也可以以相同的方式使用系统属性。例如，在Windows上，OS属性是默认定义的：

```properties
environment.name=${OS}
```

也可以组合多个变量值。让我们定义另一个环境变量HELLO_TUYUCHENG，其值为“Hello Tuyucheng”。现在我们可以拼接这两个变量：

```properties
tuyucheng.presentation=${HELLO_TUYUCHENG}. Java is installed in the folder: ${JAVA_HOME}
```

[属性](https://www.baeldung.com/properties-with-spring)tuyucheng.presentation现在包含以下文本：“Hello Tuyucheng. Java is installed in the folder: C:\Program Files\Java\jdk-11.0.14”。

这样，我们的属性根据环境具有不同的值。

## 3. 在代码中使用特定于环境的属性

假设我们启动了一个[Spring上下文](https://www.baeldung.com/spring-web-contexts)，我们现在将看到如何将属性值注入到我们的代码中。

### 3.1 使用@Value注入值

首先，我们可以使用[@Value](https://www.baeldung.com/spring-value-annotation)注解。@Value处理setter、[构造](https://www.baeldung.com/constructor-injection-in-spring)和字段[注入](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)：

```java
@Value("${tuyucheng.presentation}")
private String tuyuchengPresentation;
```

### 3.2 从Spring Environment中获取

我们还可以通过Spring的Environment获取属性的值，首先需要[自动注入](https://www.baeldung.com/spring-autowire)它：

```java
@Autowired
private Environment environment;
```

通过getProperty()方法，现在可以检索属性值：

```java
environment.getProperty("tuyucheng.presentation")
```

### 3.3 使用@ConfigurationProperties对属性进行分组

如果我们想将属性组合在一起，[@ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot)注解非常有用。我们将定义一个[组件](https://www.baeldung.com/spring-component-annotation)，它将收集具有给定前缀的所有属性，在我们的例子中是tuyucheng。然后，我们可以为每个属性定义一个[setter](https://www.baeldung.com/java-why-getters-setters)。setter的名称是属性名称的其余部分。在我们的例子中，我们只有一个，称为presentation：

```java
@Component
@ConfigurationProperties(prefix = "tuyucheng")
public class TuyuchengProperties {

    private String presentation;

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }
}
```

我们现在可以自动装配一个TuyuchengProperties对象：

```java
@Autowired
private TuyuchengProperties tuyuchengProperties;
```

最后，要获取特定属性的值，我们需要使用相应的getter：

```java
tuyuchengProperties.getPresentation()
```

## 4. 在application.yml文件中使用环境变量

就像application.properties一样，application.yml是一个配置文件，用于定义应用程序的各种属性和设置。要使用环境变量，**我们需要在属性占位符中声明它的名称**。

让我们看一个示例application.yml文件，其中包含一个属性占位符和变量名称：

```yaml
spring:
    datasource:
        url: ${DATABASE_URL}
```

上面的示例显示我们正在尝试在我们的Spring Boot应用程序中导入数据库URL。${DATABASE_URL}表达式告诉Spring Boot查找名称为DATABASE_URL的环境变量。

要在application.yml中定义环境变量，我们必须以美元符号开头，然后是左大括号、环境变量的名称和右大括号。所有这些组合起来构成了属性占位符和环境变量名称。

此外，我们可以在代码中使用特定于环境的属性，就像我们使用application.properties一样。我们可以使用@Value注解注入值。另外，我们可以使用Environment类。最后，我们可以使用@ConfigurationProperties注解。

## 5. 总结

在本文中，我们学习了如何根据环境定义具有不同值的属性并在代码中使用它们。此外，我们还了解了如何在application.properties和application.yml文件中定义环境变量。最后，我们查看了将定义的属性注入示例代码的示例。