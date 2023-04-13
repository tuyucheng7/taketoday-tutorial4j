## 1. 概述

在这个快速教程中，我们将学习如何将命令行参数传递给Spring Boot应用程序。

我们可以使用命令行参数来配置我们的应用程序、覆盖应用程序属性并传递自定义参数。

## 2. Maven命令行参数

首先，让我们看看如何在使用Maven插件运行应用程序时传递参数。

然后我们将学习如何访问代码中的参数。

### 2.1 Spring Boot 1.x

对于Spring Boot 1.x，我们可以使用-Drun.arguments将参数传递给我们的应用程序：

```shell
mvn spring-boot:run -Drun.arguments=--customArgument=custom
```

我们还可以将多个参数传递给我们的应用程序：

```shell
mvn spring-boot:run -Drun.arguments=--spring.main.banner-mode=off,--customArgument=custom
```

注意：

-   参数应以逗号分隔
-   每个参数都应以-为前缀
-   我们还可以传递配置属性，如spring.main.banner-mode，如上例所示

### 2.2 Spring Boot 2.x

对于Spring Boot 2.x，我们可以使用-Dspring-boot.run.arguments传递参数：

```shell
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.main.banner-mode=off,--customArgument=custom
```

## 3. Gradle命令行参数

接下来，让我们探索如何在使用Gradle插件运行我们的应用程序时传递参数。

我们需要在build.gradle文件中配置我们的bootRun任务：

```groovy
bootRun {
    if (project.hasProperty('args')) {
        args project.args.split(',')
    }
}
```

现在我们可以传递命令行参数：

```shell
./gradlew bootRun --args=--spring.main.banner-mode=off,--customArgument=custom
```

## 4. 覆盖系统属性

除了传递自定义参数，我们还可以覆盖系统属性。

例如，这是我们的application.properties文件：

```properties
server.port=8081
spring.application.name=SampleApp
```

要覆盖server.port值，我们需要按以下方式传递新值(对于Spring Boot 1.x)：

```shell
mvn spring-boot:run -Drun.arguments=--server.port=8085
```

同样，对于Spring Boot 2.x：

```shell
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8085
```

注意：

-   Spring Boot将命令行参数转换为属性，并将它们添加为环境变量。

-   我们可以使用简短的命令行参数–port=8085而不是–server.port=8085，方法是在我们的application.properties中使用占位符：

    ```properties
    server.port=${port:8080}
    ```

-   命令行参数优先于application.properties值。

如有必要，我们可以停止应用程序将命令行参数转换为属性：

```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setAddCommandLineProperties(false);
        application.run(args);
    }
}
```

## 5. 访问命令行参数

让我们看看如何从应用程序的main()方法访问命令行参数：

```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        for(String arg:args) {
            System.out.println(arg);
        }
        SpringApplication.run(Application.class, args);
    }
}
```

这将打印我们从命令行传递给应用程序的参数，但我们也可以稍后在应用程序中使用它们。

## 6. 将命令行参数传递给@SpringBootTest 

随着Spring Boot 2.2的发布，我们获得了在测试期间使用@SpringBootTest及其args属性注入命令行参数的能力：

```java
@SpringBootTest(args = "--spring.main.banner-mode=off")
public class ApplicationTest {

    @Test
    public void whenUsingSpringBootTestArgs_thenCommandLineArgSet(@Autowired Environment env) {
        Assertions.assertThat(env.getProperty("spring.main.banner-mode")).isEqualTo("off");
    }
}
```

## 7. 总结

在这篇简短的文章中，我们学习了如何使用Maven和Gradle从命令行向我们的Spring Boot应用程序传递参数。

我们还演示了如何从我们的代码中访问这些参数以配置我们的应用程序。