## 一、概述

在我们之前的教程[Spring Profiles](https://www.baeldung.com/spring-profiles)和[Logging in Spring Boot](https://www.baeldung.com/spring-boot-logging)中，我们展示了如何在 Spring 中激活不同的配置文件和使用 Log4j2。

在这个简短的教程中，我们将学习**如何在每个 Spring 配置文件中使用不同的 Log4j2 配置**。

## 2.使用不同的属性文件

例如，假设我们有两个文件，*log4j2.xml*和*log4j2-dev.xml*，一个用于默认配置文件，另一个用于“dev”配置文件。

让我们创建我们的*application.properties*文件并告诉它在哪里可以找到日志记录配置文件：

```properties
logging.config=/path/to/log4j2.xml复制
```

接下来，让我们为名为*application-dev.properties*的“dev”配置文件创建一个新的属性文件，并添加类似的行：

```properties
logging.config=/path/to/log4j2-dev.xml复制
```

如果我们有其他配置文件——例如，“prod”——我们只需要为我们的“prod”配置文件创建一个名称相似的属性文件*——application-prod.properties*。**配置文件特定的属性总是覆盖默认的**。

## 3. 程序化配置

*我们可以通过更改 Spring Boot Application*类以编程方式选择要使用的 Log4j2 配置文件：

```java
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... param) {
        if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            Configurator.initialize(null, "/path/to/log4j2-dev.xml");
        } else {
            Configurator.initialize(null, "/path/to/log4j2.xml");
        }
    }
}复制
```

*Configurator*是 Log4j2 库的一个类。它提供了几种使用配置文件的位置和各种可选参数构造*LoggerContext的方法。*

**该解决方案有一个缺点：不会使用 Log4j2 记录应用程序启动过程**。

## 4。结论

总之，我们已经看到了两种针对每个 Spring 配置文件使用不同 Log4j2 配置的方法。首先，我们看到我们可以为每个配置文件提供不同的属性文件。然后，我们看到了一种在应用程序启动时基于活动配置文件以编程方式配置 Log4j2 的方法。