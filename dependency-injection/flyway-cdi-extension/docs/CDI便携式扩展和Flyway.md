## 1. 概述

在本教程中，我们将介绍 CDI(上下文和依赖注入)的一个有趣功能，称为 CDI 可移植扩展。

首先，我们将从了解它的工作原理开始，然后我们将了解如何编写扩展。我们将完成为 Flyway 实现 CDI 集成模块的步骤，因此我们可以在 CDI 容器启动时运行数据库迁移。

本教程假设你对 CDI 有基本的了解。查看 [这篇文章](https://www.baeldung.com/java-ee-cdi) ，了解 CDI 的介绍。

## 2. 什么是 CDI 便携式扩展？

CDI 可移植扩展是一种机制，通过它我们可以在 CDI 容器之上实现额外的功能。在引导时，CDI 容器扫描类路径并创建有关已发现类的元数据。

在此扫描过程中，CDI 容器会触发许多只能由扩展程序观察到的初始化事件。这就是 CDI 便携式扩展发挥作用的地方。

CDI Portable 扩展会观察这些事件，然后修改信息或将信息添加到容器创建的元数据中。

## 3.Maven依赖

让我们首先在pom.xml中添加 CDI API 所需的依赖项。这足以实现一个空的扩展。

```xml
<dependency>
    <groupId>javax.enterprise</groupId>
    <artifactId>cdi-api</artifactId>
    <version>2.0.SP1</version>
</dependency>
```

为了运行应用程序，我们可以使用任何兼容的 CDI 实现。在本文中，我们将使用 Weld 实现。

```xml
<dependency>
    <groupId>org.jboss.weld.se</groupId>
    <artifactId>weld-se-core</artifactId>
    <version>3.0.5.Final</version>
    <scope>runtime</scope>
</dependency>
```

你可以检查是否有任何新版本 [的 API](https://search.maven.org/classic/#search|gav|1|g%3A"javax.enterprise" AND a%3A"cdi-api")和[实现](https://search.maven.org/classic/#search|gav|1|g%3A"org.jboss.weld.se" AND a%3A"weld-se-core")已在 Maven Central 上发布。

## 4. 在非CDI环境下运行Flyway

在我们开始集成 Flyway和 CDI 之前，我们应该先看看如何在非 CDI 上下文中运行它。

那么让我们看一下以下摘自[Flyway](https://flywaydb.org/)[官网的示例](https://flywaydb.org/)：

```java
DataSource dataSource = //...
Flyway flyway = new Flyway();
flyway.setDataSource(dataSource);
flyway.migrate();
```

正如我们所见，我们只使用了一个 需要DataSource实例的Flyway实例。

我们的 CDI 可移植扩展稍后将生成Flyway和 数据源bean。出于此示例的目的，我们将使用嵌入式 H2 数据库，我们将通过DataSourceDefinition注解提供DataSource属性。

## 5. CDI 容器初始化事件

在应用程序引导程序中，CDI 容器首先加载并实例化所有 CDI 可移植扩展。然后，在每个扩展中，如果有的话，它会搜索并注册初始化事件的观察者方法。之后，它执行以下步骤：

1.  在扫描过程开始之前触发BeforeBeanDiscovery事件
2.  执行扫描存档 bean 的类型发现，并为每个发现的类型触发ProcessAnnotatedType事件
3.  触发 AfterTypeDiscovery事件
4.  执行 bean 发现
5.  触发 AfterBeanDiscovery 事件
6.  执行 bean 验证并检测定义错误
7.  触发 AfterDeploymentValidation事件

CDI 可移植扩展的目的是观察这些事件、检查有关发现的 bean 的元数据、修改此元数据或添加到其中。

在 CDI 可移植扩展中，我们只能观察这些事件。

## 6. 编写 CDI 便携式扩展

让我们看看如何通过构建我们自己的 CDI 可移植扩展来挂钩其中一些事件。

### 6.1. 实施 SPI 提供程序

CDI 可移植扩展是接口javax.enterprise.inject.spi.Extension 的JavaSPI 提供程序。查看[这篇文章](https://www.baeldung.com/java-spi)，了解JavaSPI 的介绍。

首先，我们首先提供扩展实现。稍后，我们将向 CDI 容器引导事件添加观察者方法：

```java
public class FlywayExtension implements Extension {
}
```

然后，我们添加一个文件名META-INF/services/javax.enterprise.inject.spi.Extension，内容如下：

```plaintext
com.baeldung.cdi.extension.FlywayExtension
```

作为 SPI，此扩展在容器引导程序之前加载。因此可以注册 CDI 引导事件上的观察者方法。

### 6.2. 定义初始化事件的观察者方法

在此示例中，我们在扫描过程开始之前让 CDI 容器知道Flyway类。这是在 registerFlywayType()观察者方法中完成的：

```java
public void registerFlywayType(
  @Observes BeforeBeanDiscovery bbdEvent) {
    bbdEvent.addAnnotatedType(
      Flyway.class, Flyway.class.getName());
}
```

在这里，我们添加了有关Flyway类的元数据。从现在开始，它的行为就像被容器扫描过一样。为此，我们使用了addAnnotatedType()方法。

接下来，我们将观察 ProcessAnnotatedType 事件，使Flyway类成为 CDI 托管 bean：

```java
public void processAnnotatedType(@Observes ProcessAnnotatedType<Flyway> patEvent) {
    patEvent.configureAnnotatedType()
      .add(ApplicationScoped.Literal.INSTANCE)
      .add(new AnnotationLiteral<FlywayType>() {})
      .filterMethods(annotatedMethod -> {
          return annotatedMethod.getParameters().size() == 1
            && annotatedMethod.getParameters().get(0).getBaseType()
              .equals(javax.sql.DataSource.class);
      }).findFirst().get().add(InjectLiteral.INSTANCE);
}
```

首先，我们使用@ApplicationScoped和@FlywayType注解对Flyway 类进行 注解，然后我们搜索Flyway.setDataSource(DataSource dataSource)方法并使用@Inject 对它进行注解。

上述操作的最终结果与容器扫描以下Flyway bean 具有相同的效果：

```java
@ApplicationScoped
@FlywayType
public class Flyway {
 
    //...
    @Inject
    public void setDataSource(DataSource dataSource) {
      //...
    }
}
```

下一步是使DataSource bean 可用于注入，因为我们的Flyway bean 依赖于DataSource bean。

为此，我们将处理将DataSource Bean 注册到容器中，我们将使用AfterBeanDiscovery事件：

```java
void afterBeanDiscovery(@Observes AfterBeanDiscovery abdEvent, BeanManager bm) {
    abdEvent.addBean()
      .types(javax.sql.DataSource.class, DataSource.class)
      .qualifiers(new AnnotationLiteral<Default>() {}, new AnnotationLiteral<Any>() {})
      .scope(ApplicationScoped.class)
      .name(DataSource.class.getName())
      .beanClass(DataSource.class)
      .createWith(creationalContext -> {
          DataSource instance = new DataSource();
          instance.setUrl(dataSourceDefinition.url());
          instance.setDriverClassName(dataSourceDefinition.className());
              return instance;
      });
}
```

如我们所见，我们需要一个提供 DataSource 属性的DataSourceDefinition 。

我们可以使用以下注解来注解任何托管 bean：

```java
@DataSourceDefinition(
  name = "ds", 
  className = "org.h2.Driver", 
  url = "jdbc:h2:mem:testdb")
```

为了提取这些属性，我们观察ProcessAnnotatedType事件以及 @WithAnnotations 注解：

```java
public void detectDataSourceDefinition(
  @Observes @WithAnnotations(DataSourceDefinition.class) ProcessAnnotatedType<?> patEvent) {
    AnnotatedType at = patEvent.getAnnotatedType();
    dataSourceDefinition = at.getAnnotation(DataSourceDefinition.class);
}
```

最后，我们监听AfterDeployementValidation事件以从 CDI 容器中获取所需的Flyway bean，然后调用migrate()方法：

```java
void runFlywayMigration(
  @Observes AfterDeploymentValidation adv, 
  BeanManager manager) {
    Flyway flyway = manager.createInstance()
      .select(Flyway.class, new AnnotationLiteral<FlywayType>() {}).get();
    flyway.migrate();
}
```

## 七. 总结

第一次构建 CDI 可移植扩展似乎很困难，但是一旦我们了解容器初始化生命周期和专用于扩展的 SPI，它就会成为一个非常强大的工具，我们可以使用它在 Jakarta EE 之上构建框架。