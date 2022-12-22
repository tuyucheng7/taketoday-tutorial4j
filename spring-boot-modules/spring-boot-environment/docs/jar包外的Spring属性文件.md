## 1. 概述

属性文件是我们可以用来存储项目特定信息的常用方法，理想情况下，我们应该将其保存在包外部，以便能够根据需要更改配置。

在本快速教程中，我们将探讨**在[Spring Boot应用程序]()中从jar外部位置加载属性文件的各种方法**。

## 2. 使用默认位置

按照惯例，Spring Boot会在四个预定位置按以下优先级顺序查找外部化的配置文件application.properties或application.yml：

-   当前目录的/config子目录
-   当前目录
-   类路径/config包
-   类路径根

因此，**将加载在application.properties中定义并放置在当前目录的/config子目录中的属性**，这也将在发生冲突时覆盖其他位置的属性。

## 3. 使用命令行

如果上述约定对我们不起作用，我们可以**直接在命令行中配置位置**：

```bash
java -jar app.jar --spring.config.location=file:///Users/home/config/jdbc.properties
```

我们还可以传递应用程序将在其中搜索文件的文件夹位置：

```bash
java -jar app.jar --spring.config.name=application,jdbc --spring.config.location=file:///Users/home/config
```

最后，另一种方法是通过[Maven插件](https://www.baeldung.com/spring-boot-command-line-arguments)运行Spring Boot应用程序。

这样的话，我们可以使用-D参数：

```bash
mvn spring-boot:run -Dspring.config.location="file:///Users/home/jdbc.properties"
```

## 4. 使用环境变量

现在假设我们无法更改启动命令。

很棒的是**Spring Boot还将读取环境变量SPRING_CONFIG_NAME和SPRING_CONFIG_LOCATION**：

```bash
export SPRING_CONFIG_NAME=application,jdbc
export SPRING_CONFIG_LOCATION=file:///Users/home/config
java -jar app.jar
```

请注意，仍会加载默认文件。**但是在属性冲突的情况下，特定于环境的属性文件优先**。

## 5. 使用应用程序属性

如我们所见，我们必须在应用程序启动之前定义spring.config.name和 spring.config.location属性，因此在application.properties文件(或YAML对应文件)中使用它们将没有任何效果。

Spring Boot修改了2.4.0版本中处理属性的方式。

与此更改一起，该团队引入了一个新属性，允许直接从应用程序属性导入其他配置文件：

```properties
spring.config.import=file:./additional.properties,optional:file:/Users/home/config/jdbc.properties
```

## 6. 程序化

如果我们想要编程访问，我们可以注册一个PropertySourcesPlaceholderConfigurer bean：

```java
public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
    properties.setLocation(new FileSystemResource("/Users/home/conf.properties"));
    properties.setIgnoreResourceNotFound(false);
    return properties;
}
```

在这里，我们使用PropertySourcesPlaceholderConfigurer从自定义位置加载属性。

## 7. 从Fat Jar中排除一个文件

Maven Boot插件会自动将src/main/resources目录下的所有文件包含到jar包中。

如果我们不希望某个文件成为jar的一部分，我们可以使用一个简单的配置来排除它：

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
            <excludes>
                <exclude>/conf.properties</exclude>
            </excludes>
        </resource>
    </resources>
</build>
```

在此示例中，我们过滤掉了conf.properties文件，使其不包含在生成的jar中。

## 8. 总结

本文演示了Spring Boot框架本身如何为我们处理[外部化配置]()。

通常，我们只需要将属性值放在正确的文件和位置即可，但我们也可以使用Spring的Java API进行更多的控制。