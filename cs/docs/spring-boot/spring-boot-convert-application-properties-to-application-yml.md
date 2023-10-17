## 1. 概述

在本教程中，我们将学习如何将从[Spring Initializer下载新的](https://start.spring.io/)[Spring Boot](https://www.baeldung.com/spring-boot)项目时收到的默认application.properties文件转换为更具可读性的application.yml文件。

## 2. 属性和YML文件的区别

在直接进入主题之前，让我们以代码的形式看看这两种文件格式之间的区别。

在application.properties文件中，属性存储为单行配置。Spring Boot生成属性文件作为默认文件：

```properties
spring.datasource.url=jdbc:h2:mem:testDB
spring.datasource.username=user
spring.datasource.password=testpwd
```

另一方面，我们可以创建一个application.yml。这是一个基于 YML 的文件，与属性文件相比，在具有分层数据时更易于读取：

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testDB
    username: user
    password: testpwd

```

[正如我们所看到的，在基于 YML 的配置](https://www.baeldung.com/spring-boot-yaml-vs-properties)的帮助下，我们不再需要添加重复的前缀(spring.datasource)。

## 3. 将属性转换为 YML，反之亦然

### 3.1. IntelliJ 插件

如果我们使用 IntelliJ 作为 IDE 来运行Spring Boot应用程序，我们可以通过安装以下插件来进行转换：

[![属性至 YML-Intellij](https://www.baeldung.com/wp-content/uploads/2023/06/Plugin-properties-to-yml-1024x735.png)](https://www.baeldung.com/wp-content/uploads/2023/06/Plugin-properties-to-yml.png)

我们需要转到“文件”>“设置”>“插件”>“安装”转换 YAML 和属性文件”。

安装插件后，我们：

- 右键单击application.properties文件
- 选择“转换 YAML 和属性文件”选项，自动将文件转换为application.yml

我们也能够将其转换回来。

### 3.2. 在线建站工具

我们还可以直接将代码库中的配置复制粘贴到[Mageddo](https://mageddo.com/tools/yaml-converter)转换器网站，而不是安装插件。

出于安全目的，请确保我们不会在第三方网站上输入转换密码：[![Mageddo-属性-至-YML](https://www.baeldung.com/wp-content/uploads/2023/06/properties-to-yaml-2-1024x373.png)](https://www.baeldung.com/wp-content/uploads/2023/06/properties-to-yaml-2.png)

我们将代码放在相应的properties/YML部分中，并将其转换为另一种格式。

## 4。总结

在本教程中，我们了解了.properties和.yml文件之间的区别，并了解了如何使用突出显示的各种工具和插件将application.properties文件转换为application.yml，反之亦然。