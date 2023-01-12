##  1. 概述

在本教程中，我们将解释如何在 Spring Boot 的application.properties中使用环境变量。然后我们将演示如何在代码中引用这些属性。

## 延伸阅读：

## [Spring 和 Spring Boot 的属性](https://www.baeldung.com/properties-with-spring)

有关如何在 Spring 中使用属性文件和属性值的教程。

[阅读更多](https://www.baeldung.com/properties-with-spring)→

## [在 Spring Boot 中使用 application.yml 与 application.properties](https://www.baeldung.com/spring-boot-yaml-vs-properties)

Spring Boot 同时支持 .properties 和 YAML。我们探讨了注入属性之间的差异，以及如何提供多种配置。

[阅读更多](https://www.baeldung.com/spring-boot-yaml-vs-properties)→

## [Spring Boot 2.5 中的环境变量前缀](https://www.baeldung.com/spring-boot-env-variable-prefixes)

了解如何在 Spring Boot 中为环境变量使用前缀。

[阅读更多](https://www.baeldung.com/spring-boot-env-variable-prefixes)→

## 2. Use Environment Variables in the application.properties File

Let's define a [global environment variable](https://www.baeldung.com/linux/environment-variables) called JAVA_HOME with the value “C:Program FilesJavajdk-11.0.14”.

To use this variable in Spring Boot's application.properties, we need to surround it with braces:

```plaintext
java.home=${JAVA_HOME}Copy
```

We can also use the System properties in the same way. For instance, on Windows, an OS property is defined by default:

```plaintext
environment.name=${OS}Copy
```

It's also possible to combine several variable values. Let's define another environment variable, HELLO_BAELDUNG, with the value “Hello Baeldung”. We can now concatenate our two variables:

```plaintext
baeldung.presentation=${HELLO_BAELDUNG}. Java is installed in the folder: ${JAVA_HOME}Copy
```

The [property](https://www.baeldung.com/properties-with-spring) baeldung.presentation now contains the following text: “Hello Baeldung. Java is installed in the folder: C:Program FilesJavajdk-11.0.14”.

这样，我们的属性根据环境具有不同的值。

## 3. 在代码中使用我们环境的特定属性

鉴于我们启动了一个[Spring 上下文](https://www.baeldung.com/spring-web-contexts)，我们现在将解释如何将属性值注入到我们的代码中。

### 3.1. 使用@Value注入值

首先，我们可以使用[@Value](https://www.baeldung.com/spring-value-annotation)注解。@Value处理 setter、[constructor](https://www.baeldung.com/constructor-injection-in-spring)和 field[注入](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)：

```java
@Value("${baeldung.presentation}")
private String baeldungPresentation;
```

### 3.2. 从 Spring 环境中获取

我们还可以通过 Spring 的Environment获取属性的值。我们需要[自动装配](https://www.baeldung.com/spring-autowire)它：

```java
@Autowired
private Environment environment;
```

由于getProperty方法，现在可以检索属性值：

```java
environment.getProperty("baeldung.presentation")
```

### 3.3. 使用@ConfigurationProperties 对属性进行分组

如果我们想将属性组合在一起，[@ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot)注释非常有用。我们将定义一个[组件](https://www.baeldung.com/spring-component-annotation)，它将收集具有给定前缀的所有属性，在我们的例子中是 baeldung。然后我们可以为每个属性定义一个[setter](https://www.baeldung.com/java-why-getters-setters)。setter 的名称是属性名称的其余部分。在我们的例子中，我们只有一个叫做presentation：

```java
@Component
@ConfigurationProperties(prefix = "baeldung")
public class BaeldungProperties {

    private String presentation;

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }
}
```

我们现在可以自动装配一个BaeldungProperties对象：

```java
@Autowired
private BaeldungProperties baeldungProperties;
```

最后，要获取特定属性的值，我们需要使用相应的getter：

```java
baeldungProperties.getPresentation()
```

## 4。总结

在本文中，我们学习了如何根据环境定义具有不同值的属性，并在代码中使用它们。