## 1. 简介

**Spring Boot CLI是一个命令行抽象，它允许我们轻松地运行以Groovy脚本表示的Spring微服务**。它还为这些服务提供简化和增强的依赖项管理。

这篇简短的文章简要介绍了**如何配置Spring Boot CLI并执行简单的终端命令来运行预配置的微服务**。

在本文中，我们将使用Spring Boot CLI 2.6.1，可以在[Maven Central](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-cli/3.0.5)找到最新版本的Spring Boot CLI(请注意，由于维护成本超过了现有收益，Spring团队已在[3.0.x版本](https://github.com/spring-projects/spring-boot/issues/32263)中删除了grab、jar、run、war命令)。

## 2. 设置Spring Boot CLI

设置Spring Boot CLI的最简单方法之一是使用SDKMAN。可以在[此处](https://sdkman.io/install)找到SDKMAN的设置和安装说明。

安装SDKMAN后，运行以下命令自动安装和配置Spring Boot CLI：

```bash
$ sdk install springboot
```

要验证安装，请运行命令：

```bash
$ spring --version
```

我们还可以通过从源代码编译来安装Spring Boot CLI，Mac用户可以使用[Homebrew](https://brew.sh/)或[MacPorts](https://www.macports.org/)中的预构建包。有关所有安装选项，请参阅官方[文档](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-installing-spring-boot.html#getting-started-installing-the-cli)。

## 3. 常用终端命令

Spring Boot CLI提供了几个开箱即用的有用命令和功能。最有用的功能之一是Spring Shell，它使用必要的spring前缀包装命令。

要**启动嵌入式shell**，我们运行：

```bash
spring shell
```

从这里，我们可以直接输入所需的命令，而无需预先添加spring关键字(因为我们现在在spring shell中)。

例如，我们可以通过键入以下命令来显示正在运行的CLI的当前版本：

```bash
version
```

**最重要的命令之一是告诉Spring Boot CLI运行Groovy脚本**：

```bash
run [SCRIPT_NAME].groovy
```

Spring Boot CLI将自动推断依赖关系，或者根据正确提供的注解执行此操作。在此之后，它将启动一个嵌入式Web容器和应用程序。

让我们仔细看看如何在Spring Boot CLI中使用Groovy脚本！

## 4. 基本的Groovy脚本

Groovy和Spring与Spring Boot CLI结合在一起，**允许在单文件Groovy部署中快速编写强大、高性能的微服务脚本**。

对多脚本应用程序的支持通常需要额外的构建工具，如[Maven](https://www.baeldung.com/maven)或[Gradle](https://www.baeldung.com/gradle)。

下面我们将介绍Spring Boot CLI的一些最常见用例，为其他文章保留更复杂的设置。

有关所有Spring支持的Groovy注解的列表，请查看[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/cli-using-the-cli.html)。

### 4.1 @Grab

@Grab注解和Groovy的Java式import子句允许**轻松进行依赖管理和注入**。

事实上，大多数注解抽象、简化并自动包含必要的导入语句。这让我们可以花更多时间思考架构和我们要部署的服务的底层逻辑。

让我们来看看@Grab注解的使用方法：

```groovy
package org.test

@Grab("spring-boot-starter-actuator")

@RestController
class ExampleRestController{
    // ...
}
```

正如我们所看到的，**spring-boot-starter-actuator是预配置的，允许简洁的脚本部署，而无需自定义应用程序或环境属性、XML或其他编程配置**，尽管这些东西中的每一个都可以在必要时指定。

[此处](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-dependency-versions.html)提供了@Grab参数的完整列表(每个参数都指定了一个要下载和导入的库)。

### 4.2 @Controller、@RestController和@EnableWebMvc

为了进一步加快部署速度，我们可以选择**使用Spring Boot CLI提供的“grab hints”来自动推断要导入的正确依赖项**。

**我们将在下面讨论一些最常见的用例**。

例如，我们可以使用熟悉的@Controller和@Service注解来快速构建一个标准的MVC控制器和服务：

```groovy
@RestController
class Example {

    @Autowired
    private MyService myService;

    @GetMapping("/")
    public String helloWorld() {
        return myService.sayWorld();
    }
}

@Service
class MyService {
    public String sayWorld() {
        return "World!";
    }
}
```

Spring Boot CLI支持Spring Boot的所有默认配置。因此，我们可以让Groovy应用程序自动从它们通常的默认位置访问静态资源。

### 4.3 @EnableWebSecurity

要将Spring Boot Security选项添加到我们的应用程序中，我们可以使用@EnableWebSecurity注解，然后由Spring Boot CLI自动下载。

下面，我们将使用spring-boot-starter-security依赖项抽象出这个过程的一部分，它在底层利用了@EnableWebSecurity注解：

```groovy
package tuyucheng.security

@Grab("spring-boot-starter-security")

@RestController
class SampleController {

    @RequestMapping("/")
    public def example() {
        [message: "Hello World!"]
    }
}
```

有关如何保护资源和处理安全性的更多详细信息，请查看[官方文档](https://spring.io/projects/spring-cloud-security)。

### 4.4 @Test

要设置一个简单的JUnit测试，我们可以添加@Grab('junit')或@Test注解：

```groovy
package tuyucheng.test

@Grab('junit')
class Test {
    //...
}
```

这将使我们能够轻松地执行JUnit测试。

### 4.5 DataSource和JdbcTemplate

可以指定持久数据选项，包括DataSource或JdbcTemplate，而无需显式使用@Grab注解：

```groovy
package tuyucheng.data

@Grab('h2')
@Configuration
@EnableWebMvc
@ComponentScan('tuyucheng.data')
class DataConfig {

    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }
}
```

**通过简单地使用熟悉的Spring Bean配置约定，我们获取了H2嵌入式数据库并将其设置为DataSource**。

## 5. 自定义配置

使用Spring Boot CLI配置Spring Boot微服务有两种主要方法：

1.  我们可以在终端命令中添加argument参数
2.  我们可以使用自定义的YAML文件来提供应用程序配置

Spring Boot会自动在/config目录中搜索application.yml或application.properties：

```powershell
├── app
    ├── app.groovy
    ├── config
        ├── application.yml
    ...
```

我们还可以设置：

```powershell
├── app
    ├── example.groovy
    ├── example.yml
    ...
```

应用程序属性的完整列表可以在[Spring](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)上找到。

## 6. 总结

有关Spring Boot CLI的介绍到此结束！更多详细信息，请查看[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/cli-using-the-cli.html)。