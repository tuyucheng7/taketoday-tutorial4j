## 1. 简介

[Thymeleaf](https://www.thymeleaf.org/)是一个模板引擎，我们可以将其用于我们的[Spring Boot应用程序](https://www.baeldung.com/spring-boot-crud-thymeleaf)。与许多事情一样，Spring Boot提供了一个默认位置，它希望在其中找到我们的模板。

在这个简短的教程中，我们将了解如何更改模板位置。在我们这样做之后，我们将学习如何拥有多个位置。

## 2. 设置

要使用 Thymeleaf，我们需要将适当的[Spring Boot Starter](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <versionId>2.2.2.RELEASE</versionId>
</dependency>
```

## 3. 更改默认位置

默认情况下，Spring Boot在src/main/resources/templates中查找我们的模板。我们可以将我们的模板放在那里并将它们组织在子目录中并且没有问题。

现在，假设我们要求所有模板都位于名为templates-2的目录中。

让我们创建一个返回问候语的模板并将其放在src/main/resources/templates-2中：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Enums in Thymeleaf</title>
</head>
<body>
<h2>Hello from 'templates/templates-2'</h2>
</body>
</html>
```

我们还需要一个控制器：

```java
@GetMapping("/hello")
public String sayHello() {
    return "hello";
}
```

完成该基本设置后，让我们通过覆盖application.properties中的属性来配置Spring Boot以使用我们的templates-2目录：

```properties
spring.thymeleaf.prefix=classpath:/templates-2/
```

现在，当我们调用HelloController时，我们会看到来自hello.html的问候语。

## 4. 使用多个位置

现在我们已经了解了如何更改默认位置，让我们看看如何使用多个模板位置。

为此，让我们创建一个ClassLoaderTemplateResolver bean：

```java
@Bean
public ClassLoaderTemplateResolver secondaryTemplateResolver() {
    ClassLoaderTemplateResolver secondaryTemplateResolver = new ClassLoaderTemplateResolver();
    secondaryTemplateResolver.setPrefix("templates-2/");
    secondaryTemplateResolver.setSuffix(".html");
    secondaryTemplateResolver.setTemplateMode(TemplateMode.HTML);
    secondaryTemplateResolver.setCharacterEncoding("UTF-8");
    secondaryTemplateResolver.setOrder(1);
    secondaryTemplateResolver.setCheckExistence(true);
        
    return secondaryTemplateResolver;
}
```

在我们的自定义bean中，我们将前缀设置为我们正在使用的辅助模板目录：templates-2。我们还将CheckExistence标志设置为true，这是允许解析器在链中运行的关键。

通过此配置，我们的应用程序可以使用默认main/resources/templates目录和main/resources/templates-2中的模板。

## 5. 错误

当我们使用Thymeleaf时，我们可能会看到这个错误：

```bash
Error resolving template [hello], template might not exist or might not be accessible
  by any of the configured Template Resolvers
```

当Thymeleaf出于某种原因无法找到模板时，我们会看到此消息。让我们看看造成这种情况的一些可能原因以及如何解决这些问题。

### 5.1 控制器中的错字

由于简单的拼写错误，我们经常会看到此错误。首先要检查的是我们的文件名减去扩展名和我们在控制器中要求的模板是否完全匹配。如果我们使用子目录，我们需要确保它们也是正确的。

此外，该问题可能是某些操作系统的问题。Windows不区分大小写，但其他操作系统是。如果一切正常，比如在我们的本地Windows机器上，我们应该调查一下，但一旦我们部署了就不会调查了。

### 5.2 在控制器中包含文件扩展名

由于我们的文件通常有一个扩展名，当我们在控制器中返回我们的模板路径时，很自然地包含它们。Thymeleaf会自动附加后缀，所以我们应该避免提供它。

### 5.3 不使用默认位置

如果我们将模板放在src/main/resources/templates以外的地方，我们也会看到这个错误。如果我们想使用不同的位置，我们需要设置spring.thymeleaf.prefix属性或创建我们自己的ClassLoaderTemplateResolver bean来处理多个位置。

## 6. 总结

在本快速教程中，我们了解了Thymeleaf模板位置。首先，我们了解了如何通过设置属性来更改默认位置。然后我们通过创建我们自己的ClassLoaderTemplateResolver来使用多个位置。

我们最后讨论了当Thymeleaf找不到我们的模板时我们将看到的错误以及如何解决它。