## 1. 概述

**本教程将讨论Spring Boot 2.5中添加的一项功能，该功能可以为系统环境变量指定前缀**。这样，我们可以在同一环境中运行多个不同的Spring Boot应用程序，因为所有属性都需要一个前缀版本。

## 2. 环境变量前缀

我们可能需要在同一环境中运行多个[Spring Boot]()应用程序，并且经常**面临环境变量名称分配给不同属性的问题**。

我们可以使用Spring Boot[属性]()，这些属性在某种程度上可以是相似的，但我们也可能希望在应用程序级别设置前缀以在环境方面发挥作用。

让我们设置一个简单的Spring Boot应用程序作为示例，并**通过设置此前缀来修改应用程序属性，例如tomcat服务器端口**。

### 2.1 我们的Spring Boot应用程序

让我们创建一个Spring Boot应用程序来演示此功能。首先，**我们为应用程序添加一个前缀，为了简单起见，我们称之为“prefix”**：

```java
@SpringBootApplication
public class PrefixApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PrefixApplication.class);
        application.setEnvironmentPrefix("prefix");
        application.run(args);
    }
}
```

我们不能使用已经包含下划线字符(_)的单词作为前缀，否则应用程序将抛出错误。

我们还创建一个端点来检查我们的应用程序正在监听的端口：

```java
@Controller
public class PrefixController {

    @Autowired
    private Environment environment;

    @GetMapping("/prefix")
    public String getServerPortInfo(final Model model) {
        model.addAttribute("serverPort", environment.getProperty("server.port"));
        return "prefix";
    }
}
```

在这种情况下，我们使用[Thymeleaf]()来解析我们的模板，同时设置我们的服务器端口，一个简单的主体如下：

```xml
<html>
    // ...
    <body>
        It is working as we expected. Your server is running at port :
        <b th:text="${serverPort}"></b>
    </body>
</html>
```

### 2.2 设置环境变量

**现在，我们可以将环境变量(例如prefix_server_port)设置为8085**。例如，我们可以了解如何在[Linux]()中设置系统环境变量。

设置环境变量后，我们希望应用程序基于该前缀创建属性。

在从IDE运行的情况下，我们需要编辑启动配置并添加环境变量或从已加载的环境变量中选择它：

<img src="../assets/img.png">

### 2.3 运行应用程序

我们现在可以从命令行或我们最喜欢的IDE启动应用程序。

如果我们用浏览器访问URL”http://localhost:8085/prefix“ ，我们可以看到服务器正在运行，并且在端口上，我们之前添加了前缀：

```text
It is working as we expected. Your server is running at port : 8085
```

如果没有前缀，应用程序将开始使用默认环境变量。

## 3. 总结

在本教程中，我们了解了如何在Spring Boot中为环境变量使用前缀。例如，如果我们想在同一环境中运行多个Spring Boot应用程序并为具有相同名称的属性(例如服务器端口)分配不同的值，它会有所帮助。