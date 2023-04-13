## 1. 概述

在我们之前的[Spring Boot日志记录](https://www.baeldung.com/spring-boot-logging)教程中，我们展示了如何在Spring Boot中使用Log4j2。

在这个简短的教程中，我们将学习**如何更改Log4j2配置文件的默认位置**。

## 2. 使用属性文件

默认情况下，我们会将Log4j2配置文件(log4j2.xml/log4j2-spring.xml)保留在项目类路径或resources文件夹中。

我们可以通过在application.properties文件中添加/修改以下行来更改此文件的位置：

```properties
logging.config=/path/to/log4j2.xml
```

## 3. 使用VM选项

我们还可以在运行程序时添加以下VM选项来达到同样的目的：

```shell
-Dlogging.config=/path/to/log4j2.xml
```

## 4. 程序化配置

最后，我们可以通过更改我们的@SpringBootApplication类来以编程方式配置此文件的位置，如下所示：

```java
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... param) {
        Configurator.initialize(null, "/path/to/log4j2.xml");
    }
}
```

该解决方案有一个缺点：不会使用Log4j2记录应用程序启动过程。

## 5. 总结

总之，我们学习了在Spring Boot中更改Log4j2配置文件的默认位置的不同方法。