## 1. 概述

在本文中，我们将介绍[Kodein——](https://github.com/SalomonBrys/Kodein)一个纯 Kotlin 依赖注入 (DI) 框架——并将其与其他流行的 DI 框架进行比较。

## 2. 依赖

首先，让我们将 Kodein 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.salomonbrys.kodein</groupId>
    <artifactId>kodein</artifactId>
    <version>4.1.0</version>
</dependency>
```

请注意，最新的可用版本可在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.github.salomonbrys.kodein" AND a%3A"kodein")或[jCenter](https://bintray.com/bintray/jcenter/com.github.salomonbrys.kodein%3Akodein)上获得。

## 三、配置

我们将使用下面的模型来说明基于 Kodein 的配置：

```java
class Controller(private val service : Service)

class Service(private val dao: Dao, private val tag: String)

interface Dao

class JdbcDao : Dao

class MongoDao : Dao
```

## 4.绑定类型

Kodein 框架提供了多种绑定类型。让我们仔细看看它们是如何工作的以及如何使用它们。

### 4.1. 单例

使用Singleton绑定，目标 bean 在第一次访问时延迟实例化，并在所有进一步的请求中重新使用：

```java
var created = false;
val kodein = Kodein {
    bind<Dao>() with singleton { MongoDao() }
}

assertThat(created).isFalse()

val dao1: Dao = kodein.instance()

assertThat(created).isFalse()

val dao2: Dao = kodein.instance()

assertThat(dao1).isSameAs(dao2)
```

注意：我们可以使用Kodein.instance()来检索基于静态变量类型的目标管理 bean。

### 4.2. 渴望单例

这类似于单例绑定。唯一的区别是初始化块被急切地调用：

```java
var created = false;
val kodein = Kodein {
    bind<Dao>() with singleton { MongoDao() }
}

assertThat(created).isTrue()
val dao1: Dao = kodein.instance()
val dao2: Dao = kodein.instance()

assertThat(dao1).isSameAs(dao2)
```

### 4.3. 工厂

使用Factory绑定，初始化块接收一个参数，并且每次都从中返回一个新对象：

```java
val kodein = Kodein {
    bind<Dao>() with singleton { MongoDao() }
    bind<Service>() with factory { tag: String -> Service(instance(), tag) }
}
val service1: Service = kodein.with("myTag").instance()
val service2: Service = kodein.with("myTag").instance()

assertThat(service1).isNotSameAs(service2)
```

注意：我们可以使用Kodein.instance()来配置传递依赖。

### 4.4. 多顿

Multiton绑定与Factory绑定非常相似。唯一的区别是在后续调用中为相同的参数返回相同的对象：

```java
val kodein = Kodein {
    bind<Dao>() with singleton { MongoDao() }
    bind<Service>() with multiton { tag: String -> Service(instance(), tag) }
}
val service1: Service = kodein.with("myTag").instance()
val service2: Service = kodein.with("myTag").instance()

assertThat(service1).isSameAs(service2)
```

### 4.5. 供应商

这是一个无参数工厂绑定：

```java
val kodein = Kodein {
    bind<Dao>() with provider { MongoDao() }
}
val dao1: Dao = kodein.instance()
val dao2: Dao = kodein.instance()

assertThat(dao1).isNotSameAs(dao2)
```

### 4.6. 实例

我们可以在容器中注册一个预配置的 bean 实例：

```java
val dao = MongoDao()
val kodein = Kodein {
    bind<Dao>() with instance(dao)
}
val fromContainer: Dao = kodein.instance()

assertThat(dao).isSameAs(fromContainer)
```

### 4.7. 标记

我们也可以在不同的标签下注册多个相同类型的bean ：

```java
val kodein = Kodein {
    bind<Dao>("dao1") with singleton { MongoDao() }
    bind<Dao>("dao2") with singleton { MongoDao() }
}
val dao1: Dao = kodein.instance("dao1")
val dao2: Dao = kodein.instance("dao2")

assertThat(dao1).isNotSameAs(dao2)
```

### 4.8. 持续的

这是标记绑定的语法糖，假定用于配置常量——没有继承的简单类型：

```java
val kodein = Kodein {
    constant("magic") with 42
}
val fromContainer: Int = kodein.instance("magic")

assertThat(fromContainer).isEqualTo(42)
```

## 5.绑定分离

Kodein 允许我们在单独的块中配置 bean 并将它们组合起来。

### 5.1. 模块

我们可以按特定标准对组件进行分组——例如，所有与数据持久性相关的类——并将这些块组合起来构建一个结果容器：

```java
val jdbcModule = Kodein.Module {
    bind<Dao>() with singleton { JdbcDao() }
}
val kodein = Kodein {
    import(jdbcModule)
    bind<Controller>() with singleton { Controller(instance()) }
    bind<Service>() with singleton { Service(instance(), "myService") }
}

val dao: Dao = kodein.instance()
assertThat(dao).isInstanceOf(JdbcDao::class.java)
```

注意：由于模块包含绑定规则，当在多个 Kodein 实例中使用相同的模块时，目标 bean 将被重新创建。

### 5.2. 作品

我们可以从一个 Kodein 实例扩展另一个——这允许我们重用 bean：

```java
val persistenceContainer = Kodein {
    bind<Dao>() with singleton { MongoDao() }
}
val serviceContainer = Kodein {
    extend(persistenceContainer)
    bind<Service>() with singleton { Service(instance(), "myService") }
}
val fromPersistence: Dao = persistenceContainer.instance()
val fromService: Dao = serviceContainer.instance()

assertThat(fromPersistence).isSameAs(fromService)
```

### 5.3. 覆盖

我们可以覆盖绑定——这对测试很有用：

```java
class InMemoryDao : Dao

val commonModule = Kodein.Module {
    bind<Dao>() with singleton { MongoDao() }
    bind<Service>() with singleton { Service(instance(), "myService") }
}
val testContainer = Kodein {
    import(commonModule)
    bind<Dao>(overrides = true) with singleton { InMemoryDao() }
}
val dao: Dao = testContainer.instance()

assertThat(dao).isInstanceOf(InMemoryDao::class.java)
```

## 6. 多重绑定

我们可以在容器中配置多个具有相同公共(超级)类型的 bean：

```java
val kodein = Kodein {
    bind() from setBinding<Dao>()
    bind<Dao>().inSet() with singleton { MongoDao() }
    bind<Dao>().inSet() with singleton { JdbcDao() }
}
val daos: Set<Dao> = kodein.instance()

assertThat(daos.map {it.javaClass as Class<>})
  .containsOnly(MongoDao::class.java, JdbcDao::class.java)
```

## 7.喷油器

在我们之前使用的所有示例中，我们的应用程序代码都不知道 Kodein——它使用在容器初始化期间提供的常规构造函数参数。

但是，该框架允许通过[委托属性](https://kotlinlang.org/docs/reference/delegated-properties.html)和Injectors来配置依赖项的替代方法：

```java
class Controller2 {
    private val injector = KodeinInjector()
    val service: Service by injector.instance()
    fun injectDependencies(kodein: Kodein) = injector.inject(kodein)
}
val kodein = Kodein {
    bind<Dao>() with singleton { MongoDao() }
    bind<Service>() with singleton { Service(instance(), "myService") }
}
val controller = Controller2()
controller.injectDependencies(kodein)

assertThat(controller.service).isNotNull
```

换句话说，域类通过注入器定义依赖项并从给定容器中检索它们。这种方法在 Android 等特定环境中很有用。

## 8. 在 Android 上使用 Kodein

在 Android 中，Kodein 容器是在自定义的[Application](https://developer.android.com/reference/android/app/Application)类中配置的，稍后，它会绑定到[Context](https://developer.android.com/reference/android/content/Context)实例。假定所有组件(活动、片段、服务、广播接收器)都是从实用程序类(如KodeinActivity和KodeinFragment )扩展的：

```java
class MyActivity : Activity(), KodeinInjected {
    override val injector = KodeinInjector()

    val random: Random by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        inject(appKodein())
    }
}
```

## 九、分析

在本节中，我们将了解 Kodein 与流行的 DI 框架的比较。

### 9.1. 弹簧框架

Spring Framework 的功能比 Kodein 丰富得多。例如，Spring 有一个非常方便的组件扫描工具。当我们用@Component 、@ Service和@Named等特定注解标记我们的类时，组件扫描会在容器初始化期间自动选取这些类。

Spring 还具有强大的元编程扩展点[BeanPostProcessor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html)和[BeanFactoryPostProcessor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanFactoryPostProcessor.html)，这在使已配置的应用程序适应特定环境时可能至关重要。

最后，Spring 提供了一些构建在它之上的便捷技术，包括 AOP、事务、测试框架等等。如果我们想使用这些，那么坚持使用 Spring IoC 容器是值得的。

### 9.2. 匕首2

Dagger 2 框架不像 Spring Framework 那样功能丰富，但由于其速度(它生成执行注入的 Java 代码并仅在运行时执行)和大小，它在 Android 开发中很受欢迎。

[让我们使用MethodsCount](https://github.com/dextorer/MethodsCount)比较库的方法数量和大小：

Kodein：[![可待因](https://www.baeldung.com/wp-content/uploads/2018/03/kodein-1024x417-1024x417.png)](https://www.baeldung.com/wp-content/uploads/2018/03/kodein-1024x417.png)请注意，kotlin-stdlib依赖项占这些数字的大部分。当我们排除它时，我们得到 1282 个方法和 244 KB DEX 大小。

 

匕首 2：

[![匕首2](https://www.baeldung.com/wp-content/uploads/2018/03/dagger2-1024x413-1024x413.png)](https://www.baeldung.com/wp-content/uploads/2018/03/dagger2-1024x413.png)我们可以看到 Dagger 2 框架添加的方法少得多，它的 JAR 文件也更小。

关于用法——它非常相似，因为用户代码配置依赖项(通过Kodein 中的 Injector 和Dagger 2 中[的 JSR-330](https://jcp.org/en/jsr/detail?id=330)注解)，然后通过单个方法调用注入它们。

然而，Dagger 2 的一个关键特性是它在编译时验证依赖图，因此如果存在配置错误，它不会允许应用程序编译。

## 10.总结

我们现在知道如何使用 Kodein 进行依赖注入，它提供了哪些配置选项，以及它与其他几个流行的 DI 框架的比较。但是，是否在实际项目中使用它由你决定。