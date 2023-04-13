Taketoday Tutorial4j
==============

![Language](https://img.shields.io/badge/language-java-brightgreen)
[![License MIT](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/tu-yucheng/java-development-practice/master/LICENSE.md)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=tu-yucheng_taketoday-tutorial4j&metric=ncloc)](https://sonarcloud.io/project/overview?id=tu-yucheng_taketoday-tutorial4j)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=tu-yucheng_taketoday-tutorial4j&metric=coverage)](https://sonarcloud.io/dashboard?id=tu-yucheng_taketoday-tutorial4j)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tu-yucheng_taketoday-tutorial4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tu-yucheng_taketoday-tutorial4j)
[![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors)

这个项目是**一个小型和重点教程的集合**，每个教程都涵盖了Java生态系统中一个明确定义的开发领域。当然，其中一个重点在于Spring框架、Spring Data、Spring Boot和Spring Security。除了Spring之外，这里的模块还涵盖了Java的许多方面。

## 多版本JDK构建

就目前而言，大多数模块都是基于JDK 17才能正确构建和运行。此外，还有一些项目是基于JDK 8/19构建的。我们通过Maven ToolChains工具来保证这些模块能够使用单独的JDK构建。

首先，你需要同时下载这些版本的JDK。然后配置Maven ToolChains，在你的用户目录下的.m2文件夹中添加一个toolchains.xml文件：

<img src="assets/img.png" align="left">

然后指定以下内容(务必将每个版本的<jdkHome\>指向你本地该JDK版本的位置，例如D:\\\xxx\\\jdk-17)：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains xmlns="http://maven.apache.org/TOOLCHAINS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/TOOLCHAINS/1.1.0 http://maven.apache.org/xsd/toolchains-1.1.0.xsd">
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>17</version>
            <vendor>adopt</vendor>
        </provides>
        <configuration>
            <jdkHome>your jdk 17 path</jdkHome>
        </configuration>
    </toolchain>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>8</version>
            <vendor>adopt</vendor>
        </provides>
        <configuration>
            <jdkHome>your jdk 8 path</jdkHome>
        </configuration>
    </toolchain>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>19</version>
            <vendor>adopt</vendor>
        </provides>
        <configuration>
            <jdkHome>your jdk 19 path</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

## Maven Profiles

我们使用Maven Profile来隔离各种测试(单元测试、集成测试、实时测试...)的执行：

|   Profile   |           启用的测试类型           |
|:-----------:|:---------------------------:|
|    unit     |          *UnitTest          |
| integration |      *IntegrationTest       |
|     all     | *IntegrationTest、\*UnitTest |
|    live     |          *LiveTest          |

## 构建项目

尽管不需要经常一次构建整个仓库，因为我们通常关注特定的模块。

但是，如果我们想在仅启用单元测试的情况下构建整个仓库，我们可以从仓库的根目录调用以下命令：

```shell
mvn clean install -Punit
```

或者，如果我们想在启用集成测试的情况下构建整个仓库，我们可以执行以下操作：

```shell
mvn clean install -Pintegration
```

## 构建单个模块

要构建特定模块，请在模块目录中运行命令：

```shell
mvn clean install
```

## 运行Spring Boot模块

要运行Spring Boot模块，请在模块目录中运行命令：

```shell
mvn spring-boot:run
```

## 导入到IDE

此仓库包含大量模块，当你使用单个模块时，无需导入所有模块(或构建所有模块)-你可以只需在Eclipse或IntelliJ中导入该特定模块即可。

当你将项目导入到Intellij IDEA中时，默认不会加载任何子模块。你需要在IDE中转到Maven -> Profiles，然后选择你想要构建的子模块所属的Profile，最后刷新等待IDE索引构建完成：

<img src="assets/img_1.png">

## 运行测试

模块中的命令`mvn clean install`将运行该模块中的单元测试。对于Spring模块，这也将运行`SpringContextTest`(如果存在)。

要同时运行单元和集成测试，请使用以下命令：

```shell
mvn clean install -Pall
```

## 模块列表

+ [Java Core](java-core-modules/README.md)
+ [Kotlin Core](kotlin-modules/kotlin-core/README.md)
+ [Design Patterns](design-patterns-modules/README.md)
+ Spring Framework
  + [Spring Core](spring-modules/README.md)
  + [Spring Web](spring-web-modules/README.md)
  + [Spring Data](spring-data-modules/README.md)
  + [Spring Boot](spring-boot-modules/README.md)
  + [Spring Security](spring-security-modules/README.md)
  + [Spring Reactive](spring-reactive-modules/README.md)
  + [Spring Cloud](spring-cloud-modules/README.md)
+ Reactive
  + [Akka](akka-modules/README.md)
  + [Reactor](reactor-core/README.md)
  + [RxJava](rxjava-modules/README.md)
  + [Vert.x](vertx-modules/README.md)
  + [RSocket](rsocket/README.md)
+ [Message Queue](messaging-modules/README.md)
+ [Software Testing](software.test/README.md)
+ [GraphQL](graphql.modules/README.md)
+ [gRPC](grpc/README.md)

## 贡献人员

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/tu-yucheng"><img src="https://avatars.githubusercontent.com/u/88582540?v=4s=100" width="100px;" alt=""/><br /><sub><b>tuyucheng</b></sub></a><br /><a href="#projectManagement-tuyucheng" title="Project Management">📆</a> <a href="#maintenance-tuyucheng" title="Maintenance">🚧</a> <a href="#content-tuyucheng" title="Content">🖋</a></td>
    <td align="center"><a href="https://github.com/take-today"><img src="https://avatars.githubusercontent.com/u/116951809?v=4s=100" width="100px;" alt=""/><br /><sub><b>taketoday</b></sub></a><br /><a href="#content-taketoday" title="Content">🖋</a></td>
  </tr>
</table>