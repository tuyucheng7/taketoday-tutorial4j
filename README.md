Taketoday Tutorial4j
================

![Language](https://img.shields.io/badge/language-java-brightgreen)
[![License MIT](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/tu-yucheng/java-development-practice/master/LICENSE.md)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=tu-yucheng_taketoday-tutorial4j&metric=ncloc)](https://sonarcloud.io/project/overview?id=tu-yucheng_taketoday-tutorial4j)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=tu-yucheng_taketoday-tutorial4j&metric=coverage)](https://sonarcloud.io/dashboard?id=tu-yucheng_taketoday-tutorial4j)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tu-yucheng_taketoday-tutorial4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tu-yucheng_taketoday-tutorial4j)
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors)

这个项目是**一个小型和重点教程的集合**，每个教程都涵盖了Java生态系统中一个明确定义的开发领域。
当然，其中一个重点在于Spring框架、Spring Data、Spring Boot和Spring Security。
除了Spring之外，这里的模块还涵盖了Java的许多方面。

基于Maven Profile的隔离
====================

我们使用Maven构建Profile来隔离我们仓库中庞大的单个项目列表。

就目前而言，绝大多数模块都需要JDK 8才能正确构建和运行。

这些项目大致分为3个列表：first、second和heavy。

接下来，根据我们要执行的测试进一步隔离它们。

此外，还有2个专用于JDK 9及更高版本的Profile。

因此，我们总共有8个配置Profile：

| Profile                    | 包含          | 启用的测试类型          |
|----------------------------|-------------|------------------|
| default-first              | 第一批项目       | *UnitTest        |
| integration-lite-first     | 第一批项目       | *IntegrationTest |
| default-second             | 第二批项目       | *UnitTest        |
| integration-lite-second    | 第二批项目       | *IntegrationTest |
| default-heavy              | 繁重/长时间运行的项目 | *UnitTest        |
| integration-heavy          | 繁重/长时间运行的项目 | *IntegrationTest |
| default-jdk9-and-above     | JDK9及以上项目   | *UnitTest        |
| integration-jdk9-and-above | JDK9及以上项目   | *IntegrationTest |

构建项目
====================
尽管不需要经常一次构建整个仓库，因为我们通常关注特定的模块。

但是，如果我们想在仅启用单元测试的情况下构建整个仓库，我们可以从仓库的根目录调用以下命令：

`mvn clean install -Pdefault-first,default-second,default-heavy`

或者，如果我们想在启用集成测试的情况下构建整个仓库，我们可以执行以下操作：

`mvn clean install -Pintegration-lite-first,integration-lite-second,integration-heavy`

类似地，对于JDK 9及以上项目，命令为：

`mvn clean install -Pdefault-jdk9-and-above`

和

`mvn clean install -Pintegration-jdk9-and-above`

构建单个模块
====================
要构建特定模块，请在模块目录中运行命令：`mvn clean install`


运行Spring Boot模块
====================
要运行Spring Boot模块，请在模块目录中运行命令：`mvn spring-boot：run`


使用IDE
====================
此仓库包含大量模块，当你使用单个模块时，无需导入所有模块(或构建所有模块) - 你可以只需在Eclipse或IntelliJ中导入该特定模块即可。


运行测试
=============

模块中的命令`mvn clean install`将运行该模块中的单元测试。对于Spring模块，如果存在，这也将运行`SpringContextTest`。

要运行集成测试，请使用以下命令：

`mvn clean install -Pintegration-lite-first` 或者

`mvn clean install -Pintegration-lite-second` 或者

`mvn clean install -Pintegration-heavy` 或者

`mvn clean install -Pintegration-jdk9-and-above`

取决于我们的模块所在的列表

内容清单
===================

**Java Core**

* [Java 8-17](java-core/java8-1/README.md)
* Java Core Concurrency
    + [Java Core Concurrency Basic](java-core/java-concurrency-simple/README.md)
    + [Java Core Concurrency Advanced](java-core/java-concurrency-advanced-1/README.md)
    + [Java Core Concurrency Collections](java-core/java-concurrency-collections-1/README.md)

**Jackson**

+ [Jackson Core](jackson-modules/jackson-core/README.md)

**Build Tools**

+ [Maven](maven.modules/maven-multi-source/README.md)
+ [Gradle](gradle.modules/gradle-7/README.md)

**Spring Framework**

* [Spring Core](spring-framework/spring-core-1/README.md)
* [Spring DI](spring-framework/spring-di-1/README.md)
* [Spring Aop](spring-framework/spring-aop-1/README.md)
* [Spring Caching](spring-framework/spring-caching-1/README.md)

**Spring MVC**

* [Spring MVC Basic](spring-web-modules/spring-mvc-basics-1/README.md)

**Spring Boot**

* [Spring Boot Admin](spring-boot-modules/spring-boot-admin/README.md)
* [Spring Boot Data](spring-boot-modules/spring-boot-data-1/README.md)
* [Spring Boot Annotations](spring-boot-modules/spring-boot-annotations-1/README.md)
* [Spring Boot Customization](spring-boot-modules/spring-boot-basic-customization-1/README.md)
* [Spring Boot Mvc](spring-boot-modules/spring-boot-mvc-1/README.md)

**Spring Data**

* [Spring Data JPA CRUD](spring-data-modules/spring-data-jpa-crud-1/README.md)
* [Spring Data JPA Query](spring-data-modules/spring-data-jpa-query-1/README.md)
* [Spring Data JPA Repository](spring-data-modules/spring-data-jpa-repo-1/README.md)
* [Spring Data JPA Enterprise](spring-data-modules/spring-data-jpa-enterprise-1/README.md)
* [Spring Data JPA Annotations](spring-data-modules/spring-data-jpa-annotations/README.md)
* [Spring Data JPA Filtering](spring-data-modules/spring-data-jpa-filtering/README.md)
* [Spring Data JDBC](spring-data-modules/spring-data-jdbc/README.md)
* [Spring Data Rest](spring-data-modules/spring-data-rest-1/README.md)
* [Spring Boot Persistence](spring-data-modules/spring-boot-persistence-1/README.md)
* [Spring Boot Persistence Mongodb](spring-data-modules/spring-boot-persistence-mongodb-1/README.md)

**Spring Security**

* [Spring Security Core](spring-security-modules/spring-security-core-1/README.md)
* [Spring Security Web Boot](spring-security-modules/spring-security-web-boot-1/README.md)
* [Spring Security Web Login](spring-security-modules/spring-security-web-login-1/README.md)
* [Spring Security Auth0](spring-security-modules/spring-security-auth0/README.md)
* [Spring Security Acl](spring-security-modules/spring-security-acl/README.md)
* [Spring Security Angular](spring-security-modules/spring-security-web-angular/README.md)

**Spring Cloud**

**Reactive Stack**

* [Reactor Core](reactive-stack/reactor-core/README.md)

**Software Testing**

* [Junit 4](software-test/junit-4/README.md)
* [Junit 5](software-test/junit-5/README.md)
* [TestNG](software-test/testng-selenium/README.md)
* [Mockito](software-test/mockito-1/README.md)
* [PowerMock](software-test/powermock/README.md)
* [EasyMock](software-test/easymock/README.md)
* [Mocks](software-test/mocks-1/README.md)
* [Cucumber](software-test/cucumber-1/README.md)
* [Rest Assured](software-test/rest-assured/README.md)
* [Spring Test](software-test/spring-1/README.md)
* [MockServer](software-test/mockserver/README.md)
* [Rest API](software-test/rest-testing/README.md)
* [Assertion Libraries](software-test/assertion-libraries/README.md)
* [Hamcrest](software-test/hamcrest/README.md)
* [Test Libraries](software-test/libraries-1/README.md)
* [Groovy Spock](software-test/groovy-spock/README.md)
* [TestContainers](software-test/containers/README.md)
* [EasyRandom](software-test/easy-random/README.md)

**Message Queue**

* [RabbitMQ](messaging.queue/rabbitmq/README.md)
* [Spring JMS](messaging.queue/spring-jms/README.md)
* [JGroups](messaging.queue/jgroups/README.md)

贡献人员
==============
<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/tu-yucheng"><img src="https://avatars.githubusercontent.com/u/88582540?v=4s=100" width="100px;" alt=""/><br /><sub><b>tuyucheng</b></sub></a><br /><a href="#projectManagement-tuyucheng" title="Project Management">📆</a> <a href="#maintenance-tuyucheng" title="Maintenance">🚧</a> <a href="#content-tuyucheng" title="Content">🖋</a></td>
    <td align="center"><a href="https://github.com/take-today"><img src="https://avatars.githubusercontent.com/u/116951809?v=4s=100" width="100px;" alt=""/><br /><sub><b>taketoday</b></sub></a><br /><a href="#content-taketoday" title="Content">🖋</a></td>
  </tr>
</table>