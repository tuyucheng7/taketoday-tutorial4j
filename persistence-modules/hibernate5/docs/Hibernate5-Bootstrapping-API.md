## 1. 概述

在本教程中，我们将探索可以初始化和启动 Hibernate SessionFactory 的新机制。 我们将特别关注新的本机引导过程，因为它在 5.0 版中进行了重新设计。

在 5.0 版之前，应用程序必须使用 Configuration 类来引导 SessionFactory。这种方法现在已被弃用，因为 Hibernate 文档建议使用基于 ServiceRegistry 的新 API。

简而言之，构建 SessionFactory就是拥有一个ServiceRegistry 实现，该实现 在启动和运行时保存Hibernate 所需的服务。

## 2.Maven依赖

在我们开始探索新的引导过程之前，我们需要将hibernate-core jar 文件添加到项目类路径中。在基于 Maven 的项目中，我们只需要在pom.xml文件中声明这个依赖：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.0.Final</version>
</dependency>
```

由于 Hibernate 是一个 JPA 提供者，这也将传递地包括 JPA API 依赖项。

我们还需要我们正在使用的数据库的 JDBC 驱动程序。在此示例中，我们将使用嵌入式 H2 数据库：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.197</version>
</dependency>
```

请随时 在 Maven Central 上查看最新版本的[hibernate-core](https://search.maven.org/search?q=g:org.hibernate AND a:hibernate-core&core=gav)和[H2 驱动程序](https://search.maven.org/search?q=g:com.h2database AND a:h2&core=gav)。

## 3. 引导 API

Bootstrapping 是指构建和初始化SessionFactory 的过程。

为了达到这个目的，我们需要有一个ServiceRegistry ，用来存放Hibernate需要的Services。从这个注册表中，我们可以构建一个元数据 对象，它表示应用程序的域模型及其到数据库的映射。

让我们更详细地探讨这些主要对象。

### 3.1. 服务

在我们深入研究ServiceRegistry概念之前，我们首先需要了解什么是Service 。在 Hibernate 5.0 中，服务是一种由同名接口表示的功能：

```java
org.hibernate.service.Service
```

默认情况下，Hibernate 为最常见的 Services提供实现，在大多数情况下它们就足够了。否则，我们可以构建自己的 服务来修改原始 Hibernate 功能或添加新功能。

在下一小节中，我们将展示 Hibernate 如何通过一个名为ServiceRegistry的轻量级容器使这些服务可用。

### 3.2. 服务登记处

构建SessionFactory的第一步是创建 ServiceRegistry 。这允许持有提供 Hibernate 所需功能并基于[Java SPI](https://www.baeldung.com/java-spi)功能的各种 服务。

从技术上讲，我们可以将ServiceRegistry 视为一个轻量级的依赖注入工具，其中的 bean 只是服务类型。

ServiceRegistry有两种类型 ，它们是分层的。 第一个是BootstrapServiceRegistry，它没有父级并包含这三个必需的服务：

-   ClassLoaderService：允许Hibernate与各种运行时环境的ClassLoader 进行交互
-   IntegratorService： 控制Integrator服务的发现和管理，允许第三方应用程序与 Hibernate 集成
-   StrategySelector： 解析各种策略合约的实现

要构建BootstrapServiceRegistry实现，我们使用 BootstrapServiceRegistryBuilder 工厂类， 它允许以类型安全的方式自定义这三个服务：

```java
BootstrapServiceRegistry bootstrapServiceRegistry = new BootstrapServiceRegistryBuilder()
  .applyClassLoader()
  .applyIntegrator()
  .applyStrategySelector()
  .build();
```

第二个ServiceRegistry是StandardServiceRegistry，它建立在前面的BootstrapServiceRegistry之上，保存着上面提到的三个 Service。此外，它还包含Hibernate 所需的 各种其他服务，列在[StandardServiceInitiators](https://github.com/hibernate/hibernate-orm/blob/master/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java) 类中。

与前面的注册表一样，我们使用 StandardServiceRegistryBuilder 来创建 StandardServiceRegistry 的实例 ：

```java
StandardServiceRegistryBuilder standardServiceRegistry =
  new StandardServiceRegistryBuilder();
```

在底层， StandardServiceRegistryBuilder创建并使用 BootstrapServiceRegistry 的一个实例。我们还可以使用重载的构造函数来传递一个已经创建的实例：

```java
BootstrapServiceRegistry bootstrapServiceRegistry = 
  new BootstrapServiceRegistryBuilder().build();
StandardServiceRegistryBuilder standardServiceRegistryBuilder = 
  new StandardServiceRegistryBuilder(bootstrapServiceRegistry);
```

我们使用此构建器从资源文件加载配置，例如默认的 hibernate.cfg.xml，最后，我们调用build()方法来获取 StandardServiceRegistry 的实例 。

```java
StandardServiceRegistry standardServiceRegistry = standardServiceRegistryBuilder
  .configure()
  .build();
```

### 3.3.元数据

通过实例化BootstrapServiceRegistry 或 StandardServiceRegistry 类型的 ServiceRegistry来配置所需的所有服务后，我们现在需要提供应用程序域模型及其数据库映射的表示。

MetadataSources类负责： 

```java
MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
metadataSources.addAnnotatedClass();
metadataSources.addResource()
```

接下来，我们得到一个Metadata的实例，我们将在最后一步中使用它：

```java
Metadata metadata = metadataSources.buildMetadata();
```

### 3.4. 会话工厂

最后一步是根据之前创建的元数据创建SessionFactory ：

```java
SessionFactory sessionFactory = metadata.buildSessionFactory();
```

我们现在可以打开一个会话 并开始持久化和读取实体：

```java
Session session = sessionFactory.openSession();
Movie movie = new Movie(100L);
session.persist(movie);
session.createQuery("FROM Movie").list();
```

## 4. 总结

在本文中，我们探讨了构建SessionFactory 所需的步骤。 虽然这个过程看起来很复杂，但我们可以将其概括为三个主要步骤：我们首先创建了一个 StandardServiceRegistry实例，然后我们构建了一个Metadata对象，最后，我们构建了SessionFactory。