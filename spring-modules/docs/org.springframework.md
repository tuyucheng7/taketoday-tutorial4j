## **一、简介**

Spring Framework 为可以在任何部署平台上运行的现代基于 Java 的企业应用程序提供了一个干净且富有表现力的编程和配置模型。

本文涵盖了 Spring 框架的高级概述，主要是 org.springframework[包](http://docs.spring.io/spring/docs/current/javadoc-api/overview-summary.html)，它为依赖注入、事务管理、Web 应用程序、数据访问、消息传递、测试等提供支持。

## **2.特点**

Spring 框架提供了一个完整的功能列表：

-   Spring MVC Web 应用程序和 RESTful Web 服务框架
-   面向方面的编程，包括 Spring 的声明式事务管理
-   依赖注入
-   控制反转

以及更多。

## **3.Maven依赖**

如果您想将 Spring 添加到您的 Maven 项目中，您可以[在此处](https://www.baeldung.com/spring-with-maven)找到有关它的更多信息。

## **4. 春季项目**

该框架包括许多不同的模块和项目。从配置到安全，从 Web 应用程序到大数据——无论您的应用程序的基础设施需求是什么，都有一个 Spring 项目可以帮助您构建它。

从小处着手，只用你需要的——Spring 在设计上是模块化的。让我们在这里看看其中的一些项目。

### **4.1. 春季网络MVC**

[Web MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html)框架提供模型-视图-控制器架构，围绕处理所有 HTTP 请求和响应并使应用程序松散耦合的*DispatcherServlet设计。*

它最好的一点是它允许您将任何对象用作命令或表单对象——无需实现特定于框架的接口或基类。它的数据绑定非常灵活：例如，它将类型不匹配视为可以由应用程序评估的验证错误，而不是系统错误。

在这里您可以找到完整的[指南](https://spring.io/guides/gs/serving-web-content/)。

### **4.2. Spring IO 平台**

[IO Platform](http://platform.spring.io/platform/)定义了一组依赖项（Spring Framework 依赖项都作为第三方库），可以包含在 Java 项目中，以允许您选择必要的依赖项，而不必担心它们版本之间的兼容性（因为 Spring IO 保证了这一点）。

IO 平台经认证可与 Java 7 和 8 一起使用。

查看[GitHub 项目](https://github.com/spring-io/platform)。

### **4.3. 弹簧靴**

[Spring Boot](http://projects.spring.io/spring-boot/)可以轻松创建独立的、生产级的基于 Spring 的应用程序，您可以“直接运行”。它使得用最少的工作创建一个 Spring 支持的应用程序变得非常容易。

使用它创建的应用程序可以在很大程度上自动配置一些合理的默认值，然后可以通过指标（多少请求、请求花费了多长时间等）进行改进。

它由几个（可选）模块组成：

1.  [CLI](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-cli) – 基于 Groovy 的命令行界面，用于启动/停止 spring boot 创建的应用程序。
2.  [Boot Core——](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot)其他模块的基础。
3.  [自动配置](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-autoconfigure)——自动配置各种 Spring 项目的模块。它将检测某些框架（Spring Batch、Spring Data JPA、Hibernate、JDBC）的可用性。
4.  [Actuator——](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-actuator)添加该项目后，将为您的应用程序启用某些企业功能（安全、指标、默认错误页面）。
5.  [Starters](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-starters) – 不同的快速启动项目作为依赖项包含在您的 Maven 或 Gradle 构建文件中。它将具有该类型应用程序所需的依赖项。当前，存在用于 Web 项目（基于 tomcat 和 jetty）的启动项目、Spring Batch、Spring Data JPA、Spring Integration、Spring Security。
6.  [工具](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-tools)——Maven 和 Gradle 构建工具以及自定义 Spring Boot Loader（用于单个可执行 jar/war）包含在该项目中。

[我们可以在这里](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter)找到 Maven 工件并查看[GitHub 项目](https://github.com/spring-projects/spring-boot)。

### **4.4. 弹簧数据**

[Spring Data](https://spring.io/projects/spring-data)的使命是为数据访问提供一个熟悉且一致的、基于 Spring 的编程模型，同时仍保留底层数据存储的特殊特性。

该项目的主要目标是更容易构建使用新数据访问技术（如非关系数据库、map-reduce 框架和基于云的数据服务）的 Spring 驱动的应用程序，并为关系数据库技术提供改进的支持.

这是一个综合项目，其中包含许多特定于给定数据库的子项目（如[JPA](http://projects.spring.io/spring-data-jpa/)、[MongoDB](https://spring.io/projects/spring-data-mongodb)、[Redis](https://spring.io/projects/spring-data-redis)、[Apache Solr](https://spring.io/projects/spring-data-solr)、[Gemfire](https://spring.io/projects/spring-data-gemfire)、[Apache Cassandra](https://spring.io/projects/spring-data-cassandra)）。这些项目是通过与这些激动人心的技术背后的许多公司和开发商合作开发的。

### **4.5. 弹簧安全**

[Spring Security](https://spring.io/projects/spring-security)是一个专注于为 Java 应用程序提供身份验证和授权的框架。与所有 Spring 项目一样，Spring Security 的真正强大之处在于它可以轻松扩展以满足自定义需求。它是在[Apache 2.0 许可](https://github.com/spring-projects/spring-security/blob/master/LICENSE.txt)下发布的，因此您可以放心地在您的项目中使用它。

它还易于学习、部署和管理。它有专门的安全命名空间，为最常见的操作提供指令，只需几行 XML 即可实现完整的应用程序安全性，并可以保护您的应用程序免受会话固定、点击劫持、跨站点请求伪造等攻击。

Spring Security 还集成了许多其他 Spring 技术，包括 Spring Web Flow、Spring Web Services 和 Pivotal tc Server。

请查看Spring 安全性的[常见问题解答](https://docs.spring.io/spring-security/site/faq/faq.html)以获得更深入的了解和[Maven](https://mvnrepository.com/artifact/org.springframework.security/spring-security-core)依赖项页面。另外，请查看有关[Authentication](https://www.baeldung.com/spring-security-authentication-and-registration)、[Registration](https://www.baeldung.com/spring-security-registration)和[setup](https://www.baeldung.com/spring-security-with-maven) Spring Security with Maven 的Spring security 教程**。**

### **4.6. 春季社会**

[Spring Social](https://spring.io/projects/spring-social)是框架的扩展，它使应用程序能够与软件即服务提供商（例如 Twitter、Facebook 和其他基于[OAuth](http://oauth.net/)身份验证的 API）连接。它为基于 Web 的应用程序提供了一个随时可用的 OAuth 身份验证框架。

#### 特征：

-   一个可扩展的服务提供商框架，大大简化了将本地用户帐户连接到托管提供商帐户的过程。
-   一个连接控制器，用于处理您的 Java/Spring Web 应用程序、服务提供商和您的用户之间的授权流程。
-   Java 绑定到流行的服务提供商 API，例如 Facebook、Twitter、LinkedIn、TripIt 和 GitHub。
-   一个登录控制器，使用户能够通过服务提供商登录来对您的应用程序进行身份验证。

#### 入门指南：

-   [访问脸书数据](https://docs.spring.io/spring-social-facebook/docs/current/reference/htmlsingle/index.html)
-   [Spring Social Twitter 设置](https://www.baeldung.com/spring_social_twitter_setup)
-   [二级 Facebook 登录](https://www.baeldung.com/facebook-authentication-with-spring-security-and-social)

Spring 提供了相当多的[GitHub](https://github.com/spring-projects/spring-social-samples)项目示例，可以让您快速入门，[Spring Social 参考资料](http://docs.spring.io/spring-social/docs/current/reference/htmlsingle/)也很方便，还有一个[快速启动](https://github.com/spring-projects/spring-social/wiki/Quick-Start)页面。

### **4.7. 弹簧壳**

[Spring Shell](https://spring.io/projects/spring-shell)是一个交互式 shell，可以使用基于 Spring 的编程模型通过命令轻松扩展。

shell 项目的用户可以通过依赖 Spring Shell jar 并添加他们自己的命令（作为 spring beans 的方法）轻松构建一个功能齐全的 shell（也称为命令行）应用程序*。*创建命令行应用程序可能很有用*，例如*与项目的 REST API 交互或处理本地文件内容。

[GitHub 项目](https://github.com/spring-projects/spring-shell)可以在这里找到。

### **4.8. 春季手机**

[Spring Mobile](https://spring.io/projects/spring-mobile)是框架和[Spring Web MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html)的扩展，旨在简化移动 Web 应用程序的开发。

Spring Mobile 是一个框架，它提供检测向您的 Spring 网站发出请求的设备类型并提供基于该设备的替代视图的功能。与所有 Spring 项目一样，Spring Mobile 的真正强大之处在于它可以轻松扩展。
特征：

-   用于移动和平板设备的服务器端检测的设备解析器抽象
-   网站偏好管理，允许用户表明他或她更喜欢“正常”、“移动”还是“平板电脑”体验
-   站点切换器能够根据用户的设备将用户切换到最合适的站点，无论是手机、平板电脑还是普通站点，并且可以选择指示站点首选项
-   设备感知视图管理，用于组织和管理特定设备的不同视图。

这个[示例应用程序](https://github.com/spring-projects/spring-mobile-samples)将帮助您快速入门。

您还可以使用 Spring MVC[检测设备](https://docs.spring.io/spring-mobile/docs/current/reference/html/device.html)、处理[网站偏好](https://docs.spring.io/spring-mobile/docs/current/reference/html/device.html#site-preference)或提供移动 web 内容。

### **4.9. 春批**

[Spring Batch](https://spring.io/projects/spring-batch)是一个轻量级的综合框架，旨在支持开发对企业系统日常运营至关重要的批处理应用程序。

在这种情况下，批处理应用程序是指针对批量数据处理的自动化离线系统。Spring Batch 自动化了这个基本的批处理迭代，提供了将类似事务作为一组进行处理的能力，通常是在没有任何用户交互的离线环境中。

Spring Batch 的工作方式是从数据源读取具有可配置块大小的数据，对其进行处理，最后将其写入资源。

阅读器的数据源可以是平面文件（文本文件、XML 文件、CSV 文件……）、关系数据库（MySQL……）、MongoDB。
同样，写入器可以将数据写入平面文件、关系数据库、MongoDB、邮件程序等。

[通过创建批处理服务](https://spring.io/projects/spring-batch)和其他 Spring 批处理[资源](https://spring.io/projects/spring-batch)快速入门。

## **5.核心Spring包**

在这里，让我们看一下核心 Spring 包。

-   [org.springframework.cache – 该包支持用于声明式缓存管理的子包和类，在](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/package-summary.html)[Caffeine](https://github.com/ben-manes/caffeine/)库中设置开源缓存，支持开源缓存[EhCache 2.x](http://ehcache.sourceforge.net/)的类。
-   [org.springframework.context——](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/package-summary.html)这个包建立在 beans 包之上，添加了对消息源和观察者设计模式的支持，以及应用程序对象使用一致的 API 获取资源的能力。
-   [org.springframework.core](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/package-summary.html) – 提供用于异常处理和版本检测的基本类以及不特定于框架的任何部分的其他核心助手。
-   [org.springframework.expression – 这个包提供了](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/expression/package-summary.html)*Spring Expression Language*背后的核心抽象。
-   [org.springframework.http](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/package-summary.html) – 这个包包含对客户端/服务器端 HTTP 的基本抽象。
-   [org.springframework.jdbc](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/package-summary.html) – 此包中的类使 JDBC 更易于使用并减少常见错误的可能性。
-   [org.springframework.jms](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jms/package-summary.html) – 这个包包含 JMS 的集成类，允许 Spring 风格的 JMS 访问。
-   [org.springframework.jndi——](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jndi/package-summary.html)这个包中的类使 JNDI 更易于使用，便于访问存储在 JNDI 中的配置，并为 JNDI 访问类提供有用的超类。
-   [org.springframework.orm.hibernate5](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/orm/hibernate5/) – 提供[Hibernate 5.x](http://www.hibernate.org/)与 Spring 概念集成的包。
-   [org.springframework.test.util](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/util/package-summary.html) – 用于单元和集成测试的通用实用程序类。

这个列表是有限的，只描述了 Spring 框架的核心包。[您可以在此处](http://docs.spring.io/spring/docs/current/javadoc-api/overview-summary.html)找到完整列表。

## **六，结论**

在这篇快速概述文章中，我们了解了 Spring 生态系统中存在的各种项目，并收集了丰富的 Maven 依赖项、GitHub 项目以及每个项目提供的综合功能，使我们的 Web 应用程序安全、可扩展且易于使用生活。

我们还查看了核心包，这些包使我们能够专注于应用程序的逻辑方面。