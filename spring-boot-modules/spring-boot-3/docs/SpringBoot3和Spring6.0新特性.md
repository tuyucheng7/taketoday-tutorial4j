## 1. 概述

距离[Spring Boot 3的发布](https://spring.io/blog/2022/05/24/preparing-for-spring-boot-3-0)只有很短的时间了，现在似乎是查看新功能的好时机。

## 2. Java 17

虽然之前已经支持Java 17，但这个LTS版本现在有了基线。

从LTS版本11迁移时，Java开发人员将受益于新的语言特性。由于Java本身不是本文的主题，因此我们将只列出对Spring Boot开发人员最重要的新功能。我们可以在Java [17](https://www.baeldung.com/java-17-new-features)、[16](https://www.baeldung.com/java-16-new-features)、[15](https://www.baeldung.com/java-15-new)、[14](https://www.baeldung.com/java-14-new-features)、[13](https://www.baeldung.com/java-13-new-features)和[12](https://www.baeldung.com/java-12-new-features)的单独文章中找到更多详细信息。

### 2.1 记录

Java记录([JEP 395](https://openjdk.java.net/jeps/395)，请参阅[Java 14 Record关键字](https://www.baeldung.com/java-record-keyword))旨在用作创建数据载体类的快速方法，即其目标是简单地包含数据并在模块之间传输数据的类，也称为POJO(Plain Old Java Object)和DTO(Data Transfer Objects)。

我们可以轻松创建不可变的DTO：

```java
public record Person(String name, String address) {
}
```

目前，我们在将它们与[Bean Validation](https://github.com/spring-projects/spring-framework/issues/27868)结合使用时需要小心，因为构造函数参数不支持验证约束，例如在JSON反序列化(Jackson)上创建实例并作为参数放入控制器的方法时。

### 2.2 文本块

使用[JEP 378](https://openjdk.java.net/jeps/378)，现在可以创建多行文本块，而无需在换行符处拼接字符串：

```java
String textBlock = """
Hello, this is a
multi-line
text block.
""";
```

### 2.3 Switch表达式

Java 12引入了switch表达式([JEP 361](https://openjdk.java.net/jeps/361))，它(像所有表达式一样)计算单个值，并且可以在语句中使用。我们现在可以使用switch–case构造，而不是组合嵌套的if–else运算符(?:)：

```java
DayOfWeek day = DayOfWeek.FRIDAY;
int numOfLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY                -> 7;
    case THURSDAY, SATURDAY     -> 8;
    case WEDNESDAY              -> 9;
};
```

### 2.4 模式匹配

模式匹配在[Project Amber](https://openjdk.org/projects/amber/)中得到了详细阐述，并找到了进入Java语言的方式。在Java语言中，它们可以帮助简化instanceof求值的代码。

我们可以直接将它们与instanceof一起使用：

```java
if (obj instanceof String s) {
    System.out.println(s.toLowerCase());
}
```

我们也可以在switch–case语句中使用它：

```java
static double getDoubleUsingSwitch(Object o) {
    return switch (o) {
        case Integer i -> i.doubleValue();
        case Float f -> f.doubleValue();
        case String s -> Double.parseDouble(s);
        default -> 0d;
    };
}
```

### 2.5 密封类和接口

密封类可以通过指定允许的子类来限制继承：

```java
public abstract sealed class Pet permits Dog, Cat {
}
```

我们可以在[Java中的密封类和接口](https://www.baeldung.com/java-sealed-classes-interfaces)中找到更多详细信息。

## 3. Jakarta EE 9

最重要的变化可能是从Java EE升级到Jakarta EE 9，其中包命名空间从javax.\*更改为jakarta.\*。因此，每当我们直接使用Java EE中的类时，我们都需要调整代码中的所有导入。

例如，当我们在Spring MVC Controller中访问HttpServletRequest对象时，我们需要替换：

```java
import javax.servlet.http.HttpServletRequest;
```

为：

```java
import jakarta.servlet.http.HttpServletRequest;
```

当然，我们不必经常使用Servlet API的类型，但是如果我们使用Bean Validation和JPA，那么这是不可避免的。

当我们使用依赖于Java/Jakarta EE的外部库时，我们也应该意识到这一点(例如，我们必须使用Hibernate Validator 7+、Tomcat 10+和Jetty 11+)。

## 4. 进一步的依赖

Spring Framework 6和Spring Boot 3需要以下最低版本：

-   Kotlin 1.7+
-   Lombok 1.18.22+([JDK 17支持](https://github.com/projectlombok/lombok/issues/2898))
-   Gradle 7.3+

## 5. 主要关注点

两个首要主题受到了特别关注：本机可执行文件和可观察性。总体意味着：

-   Spring框架引入了核心抽象
-   投资组合项目始终与他们整合
-   Spring Boot提供自动配置

### 5.1 本机可执行文件

构建本机可执行文件并将它们部署到GraalVM获得更高的优先级，所以[Spring Native](https://www.baeldung.com/spring-native-intro)倡议[正在进入Spring本身](https://spring.io/blog/2022/03/22/initial-aot-support-in-spring-framework-6-0-0-m3)。

对于AOT生成，不需要包含单独的插件，我们可以使用spring-boot-maven-plugin的[新目标](https://docs.spring.io/spring-boot/docs/3.0.0-M3/maven-plugin/reference/htmlsingle/#aot)：

```shell
mvn spring-boot:aot-generate
```

[Native Hints](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#native-hints)也将成为Spring核心的一部分。[Milestone 5(v6.0.0-M5)](https://github.com/spring-projects/spring-framework/issues/27981)将提供为此的测试基础设施。

### 5.2 可观察性

Spring 6引入了[Spring Observability](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3)-一项建立在Micrometer和Micrometer Tracing(以前称为Spring Cloud Sleuth)基础之上的新计划。目标是使用Micrometer有效地记录应用程序指标，并通过[OpenZipkin](https://zipkin.io/)或[OpenTelemetry](https://opentelemetry.io/)等提供程序实施跟踪。

Spring Boot 3中对所有这些进行了自动配置，并且Spring项目正在努力使用新的Observation API进行自我检测。

我们可以在[专门的文章](https://www.baeldung.com/spring-boot-3-observability)中找到有关它的更多详细信息。

## 6. Spring Web MVC中的小改动

最重要的新功能之一是对[RFC7807](https://github.com/spring-projects/spring-framework/issues/27052)(问题详细信息标准)的支持。现在我们不需要包含单独的库，例如[Zalando Problem](https://github.com/zalando/problem)。

另一个较小的变化是[HttpMethod](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpMethod.html)不再是一个枚举，而是一个允许我们为扩展的HTTP方法创建实例的类，例如那些由WebDAV定义的：

```java
HttpMethod lock = HttpMethod.valueOf("LOCK");
```

至少删除了一些过时的基于Servlet的集成，例如Commons FileUpload(我们应该使用[StandardServletMultipartResolver](https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/file-upload-servlet-resolver.html)进行Multipart文件上传)、Tiles和FreeMarker JSP支持(我们应该使用[FreeMarker模板视图](https://www.baeldung.com/freemarker-in-spring-mvc-tutorial))。

## 7. 迁移项目

我们应该了解一些项目迁移的提示。推荐的步骤是：

1.  迁移到[Spring Boot 2.7](https://spring.io/blog/2022/05/19/spring-boot-2-7-0-available-now)(等Spring Boot 3发布时，会有基于Spring Boot 2.7的迁移指南)
2.  检查不推荐使用的代码使用和[遗留配置文件处理](https://spring.io/blog/2020/08/14/config-file-processing-in-spring-boot-2-4)；它将随着新的主要版本被删除
3.  迁移到Java 17
4.  检查第三方项目是否有Jakarta EE 9兼容版本
5.  由于Spring Boot 3尚未发布，我们可以尝试使用[当前里程碑](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0.0-M4-Release-Notes)来测试迁移

## 8. 总结

正如我们所了解到的，迁移到Spring Boot 3和Spring 6也将是迁移到Java 17和Jakarta EE 9，如果我们非常重视可观察性和本机可执行文件，我们将从即将发布的主要版本中获益很多。